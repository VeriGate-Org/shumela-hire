-- V014: Active Directory integration tables
-- ADGroupRoleMapping: maps AD groups to ShumelaHire roles
-- ADSyncLog: audit trail for AD user sync operations

CREATE TABLE ad_group_role_mappings (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ad_group_name NVARCHAR(255) NOT NULL,
    ad_group_dn NVARCHAR(500) NOT NULL,
    shumela_role NVARCHAR(30) NOT NULL,
    is_active BIT NOT NULL DEFAULT 1,
    description NVARCHAR(500),
    priority INT DEFAULT 0,
    tenant_id NVARCHAR(50) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),

    CONSTRAINT chk_ad_mapping_role CHECK (shumela_role IN (
        'ADMIN', 'EXECUTIVE', 'HR_MANAGER', 'HIRING_MANAGER',
        'RECRUITER', 'INTERVIEWER', 'EMPLOYEE', 'APPLICANT'
    )),
    CONSTRAINT uq_ad_group_dn_tenant UNIQUE (ad_group_dn, tenant_id)
);

CREATE INDEX idx_ad_group_mappings_tenant ON ad_group_role_mappings(tenant_id);
CREATE INDEX idx_ad_group_mappings_active ON ad_group_role_mappings(is_active) WHERE is_active = 1;
CREATE INDEX idx_ad_group_mappings_role ON ad_group_role_mappings(shumela_role);

CREATE TABLE ad_sync_logs (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    sync_type NVARCHAR(20) NOT NULL,
    status NVARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    users_created INT DEFAULT 0,
    users_updated INT DEFAULT 0,
    users_disabled INT DEFAULT 0,
    users_skipped INT DEFAULT 0,
    total_ad_users_processed INT DEFAULT 0,
    errors NVARCHAR(MAX),
    triggered_by NVARCHAR(100),
    started_at DATETIME2 NOT NULL,
    completed_at DATETIME2,
    duration_ms BIGINT,
    tenant_id NVARCHAR(50) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),

    CONSTRAINT chk_ad_sync_type CHECK (sync_type IN ('FULL', 'DELTA')),
    CONSTRAINT chk_ad_sync_status CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'FAILED', 'PARTIAL'))
);

CREATE INDEX idx_ad_sync_logs_tenant ON ad_sync_logs(tenant_id);
CREATE INDEX idx_ad_sync_logs_type ON ad_sync_logs(sync_type);
CREATE INDEX idx_ad_sync_logs_status ON ad_sync_logs(status);
CREATE INDEX idx_ad_sync_logs_started ON ad_sync_logs(started_at DESC);

-- Add AD-related columns to users table
ALTER TABLE users ADD ad_object_guid NVARCHAR(100);
ALTER TABLE users ADD ad_distinguished_name NVARCHAR(500);
ALTER TABLE users ADD ad_synced_at DATETIME2;
ALTER TABLE users ADD ad_source BIT DEFAULT 0;
GO

CREATE INDEX idx_users_ad_object_guid ON users(ad_object_guid) WHERE ad_object_guid IS NOT NULL;
CREATE INDEX idx_users_ad_source ON users(ad_source) WHERE ad_source = 1;
GO

-- RLS for AD tables using SESSION_CONTEXT
CREATE FUNCTION dbo.fn_ad_group_role_mappings_filter(@tenant_id NVARCHAR(50))
RETURNS TABLE WITH SCHEMABINDING AS
RETURN SELECT 1 AS result WHERE @tenant_id = CAST(SESSION_CONTEXT(N'TenantId') AS NVARCHAR(50));
GO

CREATE SECURITY POLICY dbo.ad_group_role_mappings_policy
    ADD FILTER PREDICATE dbo.fn_ad_group_role_mappings_filter(tenant_id) ON dbo.ad_group_role_mappings,
    ADD BLOCK PREDICATE dbo.fn_ad_group_role_mappings_filter(tenant_id) ON dbo.ad_group_role_mappings
    WITH (STATE = ON);
GO

CREATE FUNCTION dbo.fn_ad_sync_logs_filter(@tenant_id NVARCHAR(50))
RETURNS TABLE WITH SCHEMABINDING AS
RETURN SELECT 1 AS result WHERE @tenant_id = CAST(SESSION_CONTEXT(N'TenantId') AS NVARCHAR(50));
GO

CREATE SECURITY POLICY dbo.ad_sync_logs_policy
    ADD FILTER PREDICATE dbo.fn_ad_sync_logs_filter(tenant_id) ON dbo.ad_sync_logs,
    ADD BLOCK PREDICATE dbo.fn_ad_sync_logs_filter(tenant_id) ON dbo.ad_sync_logs
    WITH (STATE = ON);
GO
