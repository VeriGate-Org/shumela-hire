-- ============================================================================
-- V016: Platform Feature Management System
-- Creates platform tenant, feature registry, and tenant entitlements
-- ============================================================================

-- 1. Insert reserved "platform" tenant
IF NOT EXISTS (SELECT 1 FROM tenants WHERE id = 'platform')
BEGIN
    INSERT INTO tenants (id, name, subdomain, status, plan, contact_email, max_users, settings, created_at, updated_at)
    VALUES ('platform', 'ShumelaHire Platform', 'platform', 'ACTIVE', 'ENTERPRISE', 'platform@shumelahire.co.za', 100, '{}', GETDATE(), GETDATE());
END;

-- 2. Create platform_features table
CREATE TABLE platform_features (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    category VARCHAR(50) NOT NULL,
    included_plans VARCHAR(200) NOT NULL,
    is_active BIT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),

    CONSTRAINT uq_platform_features_code UNIQUE (code)
);

CREATE INDEX idx_platform_features_category ON platform_features(category);
CREATE INDEX idx_platform_features_is_active ON platform_features(is_active);

-- 3. Create tenant_feature_entitlements table
CREATE TABLE tenant_feature_entitlements (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    feature_id BIGINT NOT NULL,
    is_enabled BIT NOT NULL,
    reason VARCHAR(500),
    granted_by VARCHAR(100),
    expires_at DATETIME2,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),

    CONSTRAINT uq_tenant_feature UNIQUE (tenant_id, feature_id),
    CONSTRAINT fk_entitlement_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_entitlement_feature FOREIGN KEY (feature_id) REFERENCES platform_features(id) ON DELETE CASCADE
);

CREATE INDEX idx_tenant_feature_entitlements_tenant ON tenant_feature_entitlements(tenant_id);
CREATE INDEX idx_tenant_feature_entitlements_feature ON tenant_feature_entitlements(feature_id);

-- 4. Seed initial features
INSERT INTO platform_features (code, name, description, category, included_plans) VALUES
('WORKFLOW_MANAGEMENT', 'Workflow Management', 'Custom recruitment workflow builder with configurable stages', 'recruitment', 'STANDARD,ENTERPRISE'),
('AI_SCREENING', 'AI Candidate Screening', 'AI-powered resume screening and candidate matching', 'ai', 'STANDARD,ENTERPRISE'),
('AI_JOB_DESCRIPTION', 'AI Job Description Generator', 'AI-assisted job description creation and optimization', 'ai', 'STARTER,STANDARD,ENTERPRISE'),
('AI_INSIGHTS', 'AI Recruitment Insights', 'AI-powered analytics and recruitment trend analysis', 'ai', 'ENTERPRISE'),
('ADVANCED_REPORTING', 'Advanced Reporting', 'Custom report builder with export capabilities', 'analytics', 'STANDARD,ENTERPRISE'),
('BACKGROUND_CHECKS', 'Background Checks', 'Integrated background verification and screening', 'compliance', 'STANDARD,ENTERPRISE'),
('SAP_PAYROLL', 'SAP Payroll Integration', 'SAP payroll system data transmission', 'integrations', 'ENTERPRISE'),
('E_SIGNATURE', 'Electronic Signatures', 'DocuSign integration for offer letter signing', 'integrations', 'STANDARD,ENTERPRISE'),
('MS_TEAMS', 'Microsoft Teams Integration', 'Teams notifications and interview scheduling', 'integrations', 'STANDARD,ENTERPRISE'),
('LINKEDIN_SOCIAL', 'LinkedIn Social Posting', 'Post job ads directly to LinkedIn company pages', 'integrations', 'STANDARD,ENTERPRISE'),
('TALENT_POOLS', 'Talent Pools', 'Build and manage candidate talent pools', 'recruitment', 'STARTER,STANDARD,ENTERPRISE'),
('CUSTOM_FIELDS', 'Custom Fields', 'Add custom data fields to jobs and applications', 'customization', 'STANDARD,ENTERPRISE'),
('DATA_RESIDENCY', 'Data Residency Controls', 'Configure regional data storage and compliance', 'compliance', 'ENTERPRISE'),
('AGENCY_PORTAL', 'Agency Portal', 'External recruitment agency collaboration portal', 'recruitment', 'STANDARD,ENTERPRISE'),
('MULTI_LANGUAGE', 'Multi-Language Support', 'Job postings and communications in multiple languages', 'customization', 'ENTERPRISE');
