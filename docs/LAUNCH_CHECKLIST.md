# ShumelaHire Launch Checklist

## Infrastructure

- [ ] All 4 CDK stacks deployed to prod (`Foundation`, `Compute`, `Api`, `Frontend`)
- [ ] VPC with 2 AZs, public + private subnets verified
- [ ] NAT gateways operational (2 for prod)
- [ ] Aurora Serverless v2 PostgreSQL running (2-16 ACU for prod)
- [ ] ElastiCache Redis endpoint reachable from ECS
- [ ] S3 buckets created (`shumelahire-documents-prod`, `shumelahire-uploads-prod`)
- [ ] SQS notification queue and DLQ created
- [ ] Secrets Manager secrets populated (`ai-keys`, `docusign`, `encryption-key`)

## DNS and SSL

- [ ] `shumelahire.co.za` A record points to Amplify/CloudFront
- [ ] `api.shumelahire.co.za` A record points to ALB
- [ ] ACM certificate for `api.shumelahire.co.za` issued and validated (af-south-1)
- [ ] ACM certificate for `shumelahire.co.za` issued and validated
- [ ] HTTPS enforced (HTTP redirects to HTTPS)
- [ ] `curl -I https://api.shumelahire.co.za/actuator/health` returns 200

## Authentication

- [ ] Cognito User Pool created (`shumelahire-users-prod`)
- [ ] 8 groups created (ADMIN, EXECUTIVE, HR_MANAGER, HIRING_MANAGER, RECRUITER, INTERVIEWER, EMPLOYEE, APPLICANT)
- [ ] App client configured (no client secret for SPA)
- [ ] Admin user created and assigned to ADMIN group
- [ ] Frontend login flow tested end-to-end
- [ ] Backend JWT validation tested with Cognito token
- [ ] MFA configuration verified (optional TOTP)

## Database

- [ ] Flyway migrations applied (`V001` through `V004`)
- [ ] `spring.jpa.hibernate.ddl-auto=validate` passes
- [ ] Database credentials in Secrets Manager (auto-rotated)
- [ ] Automated backups enabled (7-day retention)
- [ ] Manual snapshot taken before go-live

## Application

- [ ] ECS running 2+ tasks (prod)
- [ ] ALB health check passing (`/actuator/health`)
- [ ] Deployment circuit breaker enabled
- [ ] Auto scaling configured (CPU 70%, Memory 80%)
- [ ] Container health checks passing
- [ ] Spring profile set to `prod`
- [ ] No test data seeders running (verified via logs)

## Security

- [ ] WAF WebACL attached to ALB
- [ ] `AWSManagedRulesCommonRuleSet` enabled
- [ ] `AWSManagedRulesSQLiRuleSet` enabled
- [ ] Rate limiting: 2000 requests/5min per IP
- [ ] SQL injection test blocked by WAF
- [ ] CORS restricted to `shumelahire.co.za`
- [ ] CSP headers present on frontend responses
- [ ] HSTS header present (`max-age=63072000; includeSubDomains; preload`)
- [ ] X-Frame-Options: DENY
- [ ] No hardcoded secrets in codebase (`grep -r "password" --include="*.yml"`)
- [ ] All S3 buckets encrypted at rest (SSE-S3)
- [ ] Aurora encryption at rest enabled
- [ ] TLS in transit for all connections

## Monitoring and Observability

- [ ] CloudWatch alarms configured:
  - [ ] ECS CPU > 80%
  - [ ] ECS Memory > 85%
  - [ ] ALB 5xx errors > 10/5min
  - [ ] Unhealthy targets > 0
- [ ] SNS alarm topic created with email subscription
- [ ] CloudWatch dashboard shows real-time metrics
- [ ] Structured JSON logging in CloudWatch Logs
- [ ] Correlation IDs propagated through requests
- [ ] Container Insights enabled for ECS

## CI/CD

- [ ] GitHub Actions CI pipeline passes (backend + frontend + CDK)
- [ ] OWASP dependency check runs in CI
- [ ] `npm audit` runs in CI
- [ ] Deployment to sbx auto-triggers on push to main
- [ ] Manual deployment to ppe works
- [ ] Manual deployment to prod works
- [ ] AWS OIDC provider configured for GitHub Actions
- [ ] IAM role for GitHub Actions created

## Compliance (POPIA)

- [ ] Privacy policy page accessible
- [ ] Terms of service page accessible
- [ ] Consent tracking implemented for applicant data
- [ ] Data erasure mechanism functional
- [ ] Data residency enforcement verified (ZA region)
- [ ] Audit logging captures all data access
- [ ] Data retention policies documented

## Performance

- [ ] Frontend build optimized (standalone output)
- [ ] Image optimization configured
- [ ] Cache headers set for static assets (1 year immutable)
- [ ] API cache headers set (5 min)
- [ ] SWR client-side caching configured
- [ ] Bundle size acceptable (check Amplify/CloudFront metrics)

## Go-Live

- [ ] Smoke test all critical user flows:
  - [ ] Login / logout
  - [ ] View job postings
  - [ ] Submit application
  - [ ] Schedule interview
  - [ ] Admin dashboard access
- [ ] DNS TTL lowered before cutover
- [ ] Rollback plan documented
- [ ] On-call contacts identified
- [ ] Status page or maintenance page ready
