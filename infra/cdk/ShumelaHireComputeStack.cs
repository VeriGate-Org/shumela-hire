using Amazon.CDK;
using Amazon.CDK.AWS.EC2;
using Amazon.CDK.AWS.ECR;
using Amazon.CDK.AWS.ECS;
using Amazon.CDK.AWS.ElasticLoadBalancingV2;
using Amazon.CDK.AWS.IAM;
using Amazon.CDK.AWS.Logs;
using Constructs;
using System.Collections.Generic;

namespace ShumelaHire.Infra;

public class ShumelaHireComputeStack : Stack
{
    public ShumelaHireComputeStack(Construct scope, string id, EnvironmentConfig config,
        ShumelaHireFoundationStack foundation, IStackProps? props = null) : base(scope, id, props)
    {
        var prefix = config.Prefix;

        // ── ECR Repository ───────────────────────────────────────────────────
        var ecrRepository = new Repository(this, "BackendRepo", new RepositoryProps
        {
            RepositoryName = "shumelahire-backend",
            RemovalPolicy = RemovalPolicy.RETAIN,
            LifecycleRules = new[]
            {
                new Amazon.CDK.AWS.ECR.LifecycleRule
                {
                    MaxImageCount = 10,
                    Description = "Keep last 10 images"
                }
            }
        });

        // ── ECS Cluster ──────────────────────────────────────────────────────
        var cluster = new Cluster(this, "Cluster", new ClusterProps
        {
            ClusterName = prefix,
            Vpc = foundation.Vpc,
            ContainerInsights = config.IsProduction
        });

        // ── Task Role (IAM) ─────────────────────────────────────────────────
        var taskRole = new Role(this, "TaskRole", new RoleProps
        {
            RoleName = $"{prefix}-task-role",
            AssumedBy = new ServicePrincipal("ecs-tasks.amazonaws.com"),
            Description = "ECS task role with S3, SQS, SES, Secrets Manager, CloudWatch permissions"
        });

        taskRole.AddToPolicy(new PolicyStatement(new PolicyStatementProps
        {
            Effect = Effect.ALLOW,
            Actions = new[] { "s3:GetObject", "s3:PutObject", "s3:DeleteObject", "s3:ListBucket", "s3:GetBucketLocation" },
            Resources = new[]
            {
                foundation.DocumentsBucket.BucketArn,
                $"{foundation.DocumentsBucket.BucketArn}/*",
                foundation.UploadsBucket.BucketArn,
                $"{foundation.UploadsBucket.BucketArn}/*"
            }
        }));

        taskRole.AddToPolicy(new PolicyStatement(new PolicyStatementProps
        {
            Effect = Effect.ALLOW,
            Actions = new[] { "sqs:SendMessage", "sqs:ReceiveMessage", "sqs:DeleteMessage", "sqs:GetQueueAttributes" },
            Resources = new[] { foundation.NotificationQueue.QueueArn }
        }));

        taskRole.AddToPolicy(new PolicyStatement(new PolicyStatementProps
        {
            Effect = Effect.ALLOW,
            Actions = new[] { "ses:SendEmail", "ses:SendRawEmail" },
            Resources = new[] { $"arn:aws:ses:{this.Region}:{this.Account}:identity/*" }
        }));

        taskRole.AddToPolicy(new PolicyStatement(new PolicyStatementProps
        {
            Effect = Effect.ALLOW,
            Actions = new[] { "secretsmanager:GetSecretValue" },
            Resources = new[]
            {
                $"arn:aws:secretsmanager:{this.Region}:{this.Account}:secret:shumelahire/{config.EnvironmentName}/*",
                $"arn:aws:secretsmanager:{this.Region}:{this.Account}:secret:{prefix}/db-credentials*"
            }
        }));

        taskRole.AddToPolicy(new PolicyStatement(new PolicyStatementProps
        {
            Effect = Effect.ALLOW,
            Actions = new[] { "logs:CreateLogGroup", "logs:CreateLogStream", "logs:PutLogEvents" },
            Resources = new[] { $"arn:aws:logs:{this.Region}:{this.Account}:log-group:/ecs/{prefix}*" }
        }));

        taskRole.AddToPolicy(new PolicyStatement(new PolicyStatementProps
        {
            Effect = Effect.ALLOW,
            Actions = new[] { "cognito-idp:GetUser", "cognito-idp:AdminGetUser", "cognito-idp:ListUsers" },
            Resources = new[] { foundation.UserPool.UserPoolArn }
        }));

        // ── Task Execution Role ──────────────────────────────────────────────
        var executionRole = new Role(this, "ExecutionRole", new RoleProps
        {
            RoleName = $"{prefix}-execution-role",
            AssumedBy = new ServicePrincipal("ecs-tasks.amazonaws.com"),
            ManagedPolicies = new[]
            {
                ManagedPolicy.FromAwsManagedPolicyName("service-role/AmazonECSTaskExecutionRolePolicy")
            }
        });

        executionRole.AddToPolicy(new PolicyStatement(new PolicyStatementProps
        {
            Effect = Effect.ALLOW,
            Actions = new[] { "secretsmanager:GetSecretValue" },
            Resources = new[]
            {
                $"arn:aws:secretsmanager:{this.Region}:{this.Account}:secret:shumelahire/{config.EnvironmentName}/*",
                $"arn:aws:secretsmanager:{this.Region}:{this.Account}:secret:{prefix}/db-credentials*"
            }
        }));

        // ── Log Group ────────────────────────────────────────────────────────
        var logGroup = new LogGroup(this, "LogGroup", new LogGroupProps
        {
            LogGroupName = $"/ecs/{prefix}",
            Retention = config.IsProduction ? RetentionDays.THREE_MONTHS : RetentionDays.ONE_WEEK,
            RemovalPolicy = RemovalPolicy.DESTROY
        });

        // ── Task Definition ──────────────────────────────────────────────────
        var taskDef = new FargateTaskDefinition(this, "TaskDef", new FargateTaskDefinitionProps
        {
            Family = prefix,
            Cpu = config.IsProduction ? 1024 : 512,
            MemoryLimitMiB = config.IsProduction ? 2048 : 1024,
            TaskRole = taskRole,
            ExecutionRole = executionRole
        });

        taskDef.AddContainer("backend", new ContainerDefinitionOptions
        {
            ContainerName = "backend",
            Image = ContainerImage.FromEcrRepository(ecrRepository, "latest"),
            Logging = LogDrivers.AwsLogs(new AwsLogDriverProps
            {
                LogGroup = logGroup,
                StreamPrefix = "backend"
            }),
            PortMappings = new[]
            {
                new PortMapping { ContainerPort = 8080, Protocol = Amazon.CDK.AWS.ECS.Protocol.TCP }
            },
            HealthCheck = new Amazon.CDK.AWS.ECS.HealthCheck
            {
                Command = new[] { "CMD-SHELL", "wget -qO- http://localhost:8080/actuator/health || exit 1" },
                Interval = Duration.Seconds(30),
                Timeout = Duration.Seconds(5),
                Retries = 3,
                StartPeriod = Duration.Seconds(60)
            },
            Environment = new Dictionary<string, string>
            {
                ["SPRING_PROFILES_ACTIVE"] = config.EnvironmentName,
                ["DATABASE_URL"] = $"jdbc:postgresql://{foundation.Database.ClusterEndpoint.Hostname}:5432/shumelahire",
                ["REDIS_HOST"] = foundation.RedisEndpointAddress,
                ["REDIS_PORT"] = "6379",
                ["S3_BUCKET"] = foundation.DocumentsBucket.BucketName,
                ["S3_REGION"] = config.Region,
                ["STORAGE_PROVIDER"] = "s3",
                ["SQS_NOTIFICATION_QUEUE_URL"] = foundation.NotificationQueue.QueueUrl,
                ["NOTIFICATION_SQS_ENABLED"] = "true",
                ["NOTIFICATION_EMAIL_ENABLED"] = "true",
                ["DATA_RESIDENCY_REGION"] = "ZA",
                ["COGNITO_USER_POOL_ID"] = foundation.UserPool.UserPoolId,
                ["COGNITO_CLIENT_ID"] = foundation.AppClient.UserPoolClientId
            }
        });

        // ── ALB ──────────────────────────────────────────────────────────────
        var alb = new ApplicationLoadBalancer(this, "Alb",
            new Amazon.CDK.AWS.ElasticLoadBalancingV2.ApplicationLoadBalancerProps
        {
            Vpc = foundation.Vpc,
            LoadBalancerName = prefix,
            InternetFacing = true,
            SecurityGroup = foundation.AlbSecurityGroup,
            VpcSubnets = new SubnetSelection { SubnetType = SubnetType.PUBLIC }
        });

        // ── Fargate Service ──────────────────────────────────────────────────
        var service = new FargateService(this, "Service", new FargateServiceProps
        {
            ServiceName = prefix,
            Cluster = cluster,
            TaskDefinition = taskDef,
            DesiredCount = config.IsProduction ? 2 : 1,
            SecurityGroups = new[] { foundation.EcsSecurityGroup },
            VpcSubnets = new SubnetSelection { SubnetType = SubnetType.PRIVATE_WITH_EGRESS },
            AssignPublicIp = false,
            CircuitBreaker = new DeploymentCircuitBreaker { Rollback = true },
            MinHealthyPercent = 100,
            MaxHealthyPercent = 200
        });

        IApplicationListener primaryListener;

        if (!string.IsNullOrEmpty(config.ApiCertificateArn))
        {
            // HTTPS with certificate (sbx/ppe/prod)
            alb.AddListener("HttpRedirect", new BaseApplicationListenerProps
            {
                Port = 80,
                DefaultAction = ListenerAction.Redirect(new RedirectOptions
                {
                    Protocol = "HTTPS",
                    Port = "443",
                    Permanent = true
                })
            });

            primaryListener = alb.AddListener("Https", new BaseApplicationListenerProps
            {
                Port = 443,
                Protocol = ApplicationProtocol.HTTPS,
                Certificates = new[] { ListenerCertificate.FromArn(config.ApiCertificateArn) },
                DefaultAction = ListenerAction.FixedResponse(503, new FixedResponseOptions
                {
                    ContentType = "application/json",
                    MessageBody = "{\"error\":\"Service unavailable\"}"
                })
            });
        }
        else
        {
            // HTTP only (dev/local)
            primaryListener = alb.AddListener("Http", new BaseApplicationListenerProps
            {
                Port = 80,
                DefaultAction = ListenerAction.FixedResponse(503, new FixedResponseOptions
                {
                    ContentType = "application/json",
                    MessageBody = "{\"error\":\"Service unavailable\"}"
                })
            });
        }

        primaryListener.AddTargets("BackendTarget", new AddApplicationTargetsProps
        {
            Port = 8080,
            Protocol = ApplicationProtocol.HTTP,
            Targets = new[] { service },
            HealthCheck = new Amazon.CDK.AWS.ElasticLoadBalancingV2.HealthCheck
            {
                Path = "/actuator/health",
                HealthyThresholdCount = 2,
                UnhealthyThresholdCount = 3,
                Interval = Duration.Seconds(30),
                Timeout = Duration.Seconds(10)
            },
            DeregistrationDelay = Duration.Seconds(30)
        });

        // ── Auto Scaling ─────────────────────────────────────────────────────
        var scaling = service.AutoScaleTaskCount(new Amazon.CDK.AWS.ApplicationAutoScaling.EnableScalingProps
        {
            MinCapacity = config.IsProduction ? 2 : 1,
            MaxCapacity = config.IsProduction ? 10 : 4
        });

        scaling.ScaleOnCpuUtilization("CpuScaling", new CpuUtilizationScalingProps
        {
            TargetUtilizationPercent = 70,
            ScaleInCooldown = Duration.Seconds(300),
            ScaleOutCooldown = Duration.Seconds(60)
        });

        scaling.ScaleOnMemoryUtilization("MemoryScaling", new MemoryUtilizationScalingProps
        {
            TargetUtilizationPercent = 80,
            ScaleInCooldown = Duration.Seconds(300),
            ScaleOutCooldown = Duration.Seconds(60)
        });

        // ── CfnOutputs ──────────────────────────────────────────────────────
        new CfnOutput(this, "AlbDnsName", new CfnOutputProps
        {
            Value = alb.LoadBalancerDnsName,
            ExportName = $"{prefix}-AlbDnsName"
        });
        new CfnOutput(this, "AlbArn", new CfnOutputProps
        {
            Value = alb.LoadBalancerArn,
            ExportName = $"{prefix}-AlbArn"
        });
        new CfnOutput(this, "ClusterName", new CfnOutputProps
        {
            Value = cluster.ClusterName,
            ExportName = $"{prefix}-ClusterName"
        });
        new CfnOutput(this, "ServiceName", new CfnOutputProps
        {
            Value = service.ServiceName,
            ExportName = $"{prefix}-ServiceName"
        });
        new CfnOutput(this, "EcrRepositoryUri", new CfnOutputProps
        {
            Value = ecrRepository.RepositoryUri,
            ExportName = $"{prefix}-EcrRepositoryUri"
        });
    }
}
