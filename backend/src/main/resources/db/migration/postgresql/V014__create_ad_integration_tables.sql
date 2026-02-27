-- V014: Active Directory integration tables
-- ADGroupRoleMapping: maps AD groups to ShumelaHire roles
-- ADSyncLog: audit trail for AD user sync operations

CREATE TABLE ad_group_role_mappings (
    id BIGSERIAL PRIMARY KEY,
    ad_group_name VARCHAR(255) NOT NULL,
    ad_group_dn VARCHAR(500) NOT NULL,
    shumela_role VARCHAR(30) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    description VARCHAR(500),
    priority INTEGER DEFAULT 0,
    tenant_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_ad_mapping_role CHECK (shumela_role IN (
        'ADMIN', 'EXECUTIVE', 'HR_MANAGER', 'HIRING_MANAGER',
        'RECRUITER', 'INTERVIEWER', 'EMPLOYEE', 'APPLICANT'
    )),
    CONSTRAINT uq_ad_group_dn_tenant UNIQUE (ad_group_dn, tenant_id)
);

CREATE INDEX idx_ad_group_mappings_tenant ON ad_group_role_mappings(tenant_id);
CREATE INDEX idx_ad_group_mappings_active ON ad_group_role_mappings(is_active) WHERE is_active = TRUE;
CREATE INDEX idx_ad_group_mappings_role ON ad_group_role_mappings(shumela_role);

CREATE TABLE ad_sync_logs (
    id BIGSERIAL PRIMARY KEY,
    sync_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    users_created INTEGER DEFAULT 0,
    users_updated INTEGER DEFAULT 0,
    users_disabled INTEGER DEFAULT 0,
    users_skipped INTEGER DEFAULT 0,
    total_ad_users_processed INTEGER DEFAULT 0,
    errors TEXT,
    triggered_by VARCHAR(100),
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    duration_ms BIGINT,
    tenant_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_ad_sync_type CHECK (sync_type IN ('FULL', 'DELTA')),
    CONSTRAINT chk_ad_sync_status CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'FAILED', 'PARTIAL'))
);

CREATE INDEX idx_ad_sync_logs_tenant ON ad_sync_logs(tenant_id);
CREATE INDEX idx_ad_sync_logs_type ON ad_sync_logs(sync_type);
CREATE INDEX idx_ad_sync_logs_status ON ad_sync_logs(status);
CREATE INDEX idx_ad_sync_logs_started ON ad_sync_logs(started_at DESC);

-- Add AD-related columns to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS ad_object_guid VARCHAR(100);
ALTER TABLE users ADD COLUMN IF NOT EXISTS ad_distinguished_name VARCHAR(500);
ALTER TABLE users ADD COLUMN IF NOT EXISTS ad_synced_at TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS ad_source BOOLEAN DEFAULT FALSE;

CREATE INDEX idx_users_ad_object_guid ON users(ad_object_guid) WHERE ad_object_guid IS NOT NULL;
CREATE INDEX idx_users_ad_source ON users(ad_source) WHERE ad_source = TRUE;

-- RLS policies for AD tables (tenanted)
ALTER TABLE ad_group_role_mappings ENABLE ROW LEVEL SECURITY;
CREATE POLICY ad_group_role_mappings_tenant_policy ON ad_group_role_mappings
    USING (tenant_id = current_setting('app.current_tenant', TRUE));

ALTER TABLE ad_sync_logs ENABLE ROW LEVEL SECURITY;
CREATE POLICY ad_sync_logs_tenant_policy ON ad_sync_logs
    USING (tenant_id = current_setting('app.current_tenant', TRUE));
