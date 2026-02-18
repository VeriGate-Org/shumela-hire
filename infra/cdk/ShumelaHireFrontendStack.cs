using Amazon.CDK;
using Amazon.CDK.AWS.S3;
using Amazon.CDK.AWS.CloudFront;
using Amazon.CDK.AWS.CloudFront.Origins;
using Amazon.CDK.AWS.CertificateManager;
using Amazon.CDK.AWS.Route53;
using Amazon.CDK.AWS.Route53.Targets;
using Constructs;

namespace ShumelaHire.Infra;

public class ShumelaHireFrontendStack : Stack
{
    public ShumelaHireFrontendStack(Construct scope, string id, EnvironmentConfig config,
        ShumelaHireFoundationStack foundation, IStackProps? props = null) : base(scope, id, props)
    {
        var prefix = config.Prefix;

        // ── S3 Bucket for Next.js static assets ──────────────────────────────
        var uiBucket = new Bucket(this, "UiBucket", new BucketProps
        {
            Encryption = BucketEncryption.S3_MANAGED,
            BlockPublicAccess = BlockPublicAccess.BLOCK_ALL,
            RemovalPolicy = config.IsProduction ? RemovalPolicy.RETAIN : RemovalPolicy.DESTROY,
            AutoDeleteObjects = !config.IsProduction
        });

        // ── CloudFront Distribution ──────────────────────────────────────────
        var originAccessIdentity = new OriginAccessIdentity(this, "OAI");
        uiBucket.GrantRead(originAccessIdentity);

        var frontendDomain = config.EnvironmentName == "prod"
            ? config.DomainName
            : $"{config.EnvironmentName}.{config.DomainName}";

        var distributionProps = new DistributionProps
        {
            DefaultBehavior = new BehaviorOptions
            {
                Origin = S3BucketOrigin.WithOriginAccessIdentity(uiBucket, new S3BucketOriginWithOAIProps
                {
                    OriginAccessIdentity = originAccessIdentity
                }),
                ViewerProtocolPolicy = ViewerProtocolPolicy.REDIRECT_TO_HTTPS,
                Compress = true,
                CachePolicy = CachePolicy.CACHING_OPTIMIZED,
                AllowedMethods = AllowedMethods.ALLOW_GET_HEAD_OPTIONS
            },
            DefaultRootObject = "index.html",
            MinimumProtocolVersion = SecurityPolicyProtocol.TLS_V1_2_2021,
            HttpVersion = HttpVersion.HTTP2_AND_3,
            ErrorResponses = new[]
            {
                new ErrorResponse
                {
                    HttpStatus = 404,
                    ResponseHttpStatus = 200,
                    ResponsePagePath = "/index.html",
                    Ttl = Duration.Seconds(0)
                },
                new ErrorResponse
                {
                    HttpStatus = 403,
                    ResponseHttpStatus = 200,
                    ResponsePagePath = "/index.html",
                    Ttl = Duration.Seconds(0)
                }
            }
        };

        // Add custom domain and certificate if available
        if (!string.IsNullOrEmpty(config.CertificateArn))
        {
            distributionProps.DomainNames = new[] { frontendDomain };
            distributionProps.Certificate = Certificate.FromCertificateArn(
                this, "Certificate", config.CertificateArn);
        }

        var distribution = new Distribution(this, "Distribution", distributionProps);

        // ── Route 53 (only if certificate is provided) ──────────────────────
        if (!string.IsNullOrEmpty(config.CertificateArn))
        {
            var hostedZone = HostedZone.FromLookup(this, "HostedZone", new HostedZoneProviderProps
            {
                DomainName = config.DomainName
            });

            new ARecord(this, "FrontendDnsRecord", new ARecordProps
            {
                Zone = hostedZone,
                RecordName = frontendDomain,
                Target = RecordTarget.FromAlias(new CloudFrontTarget(distribution))
            });
        }

        // ── CfnOutputs ──────────────────────────────────────────────────────
        new CfnOutput(this, "UiBucketName", new CfnOutputProps
        {
            Value = uiBucket.BucketName,
            ExportName = $"{prefix}-UiBucketName"
        });
        new CfnOutput(this, "DistributionId", new CfnOutputProps
        {
            Value = distribution.DistributionId,
            ExportName = $"{prefix}-DistributionId"
        });
        new CfnOutput(this, "DistributionDomainName", new CfnOutputProps
        {
            Value = distribution.DistributionDomainName,
            ExportName = $"{prefix}-DistributionDomainName"
        });
        new CfnOutput(this, "FrontendUrl", new CfnOutputProps
        {
            Value = !string.IsNullOrEmpty(config.CertificateArn)
                ? $"https://{frontendDomain}"
                : $"https://{distribution.DistributionDomainName}"
        });
    }
}
