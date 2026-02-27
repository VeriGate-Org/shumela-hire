-- V006: Add multi-tenancy support across all tables (SQL Server)
-- Creates tenants table, adds tenant_id to all tables, re-scopes unique constraints, adds RLS policies

-- ============================================================
-- 1. TENANTS TABLE
-- ============================================================
CREATE TABLE tenants (
    id              NVARCHAR(50) PRIMARY KEY,
    name            NVARCHAR(255) NOT NULL,
    subdomain       NVARCHAR(63) NOT NULL UNIQUE,
    status          NVARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    plan            NVARCHAR(30) NOT NULL DEFAULT 'STANDARD',
    contact_email   NVARCHAR(255) NOT NULL,
    contact_name    NVARCHAR(255),
    max_users       INTEGER DEFAULT 50,
    settings        NVARCHAR(MAX) DEFAULT '{}',
    created_at      DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at      DATETIME2 NOT NULL DEFAULT GETDATE(),
    CONSTRAINT chk_tenants_status CHECK (status IN ('ACTIVE', 'SUSPENDED', 'TRIAL', 'CANCELLED')),
    CONSTRAINT chk_tenants_plan CHECK (plan IN ('TRIAL', 'STARTER', 'STANDARD', 'ENTERPRISE'))
);

CREATE INDEX idx_tenants_subdomain ON tenants(subdomain);
CREATE INDEX idx_tenants_status ON tenants(status);
GO

CREATE TRIGGER trg_tenants_updated_at ON tenants
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE tenants SET updated_at = GETDATE()
    FROM tenants INNER JOIN inserted ON tenants.id = inserted.id;
END;
GO

-- Insert default tenant for data backfill
INSERT INTO tenants (id, name, subdomain, status, plan, contact_email, contact_name)
VALUES ('default', 'Default Organization', 'default', 'ACTIVE', 'STANDARD', 'admin@shumelahire.co.za', 'System Admin');

-- ============================================================
-- 2. ADD tenant_id TO V001 TABLES (6 tables)
-- ============================================================

-- users
ALTER TABLE users ADD tenant_id NVARCHAR(50);
GO
UPDATE users SET tenant_id = 'default';
ALTER TABLE users ALTER COLUMN tenant_id NVARCHAR(50) NOT NULL;
ALTER TABLE users ADD CONSTRAINT fk_users_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_users_tenant_id ON users(tenant_id);

-- applicants
ALTER TABLE applicants ADD tenant_id NVARCHAR(50);
GO
UPDATE applicants SET tenant_id = 'default';
ALTER TABLE applicants ALTER COLUMN tenant_id NVARCHAR(50) NOT NULL;
ALTER TABLE applicants ADD CONSTRAINT fk_applicants_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_applicants_tenant_id ON applicants(tenant_id);

-- job_postings
ALTER TABLE job_postings ADD tenant_id NVARCHAR(50);
GO
UPDATE job_postings SET tenant_id = 'default';
ALTER TABLE job_postings ALTER COLUMN tenant_id NVARCHAR(50) NOT NULL;
ALTER TABLE job_postings ADD CONSTRAINT fk_job_postings_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_job_postings_tenant_id ON job_postings(tenant_id);

-- applications
ALTER TABLE applications ADD tenant_id NVARCHAR(50);
GO
UPDATE applications SET tenant_id = 'default';
ALTER TABLE applications ALTER COLUMN tenant_id NVARCHAR(50) NOT NULL;
ALTER TABLE applications ADD CONSTRAINT fk_applications_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_applications_tenant_id ON applications(tenant_id);

-- documents
ALTER TABLE documents ADD tenant_id NVARCHAR(50);
GO
UPDATE documents SET tenant_id = 'default';
ALTER TABLE documents ALTER COLUMN tenant_id NVARCHAR(50) NOT NULL;
ALTER TABLE documents ADD CONSTRAINT fk_documents_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_documents_tenant_id ON documents(tenant_id);

-- audit_logs
ALTER TABLE audit_logs ADD tenant_id NVARCHAR(50);
GO
UPDATE audit_logs SET tenant_id = 'default';
ALTER TABLE audit_logs ALTER COLUMN tenant_id NVARCHAR(50) NOT NULL;
ALTER TABLE audit_logs ADD CONSTRAINT fk_audit_logs_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_audit_logs_tenant_id ON audit_logs(tenant_id);

-- ============================================================
-- 3. ADD tenant_id TO V002 TABLES (15 tables)
-- ============================================================

-- interviews
ALTER TABLE interviews ADD tenant_id NVARCHAR(50);
GO
UPDATE interviews SET tenant_id = 'default';
ALTER TABLE interviews ALTER COLUMN tenant_id NVARCHAR(50) NOT NULL;
ALTER TABLE interviews ADD CONSTRAINT fk_interviews_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_interviews_tenant_id ON interviews(tenant_id);

-- offers
ALTER TABLE offers ADD tenant_id NVARCHAR(50);
GO
UPDATE offers SET tenant_id = 'default';
ALTER TABLE offers ALTER COLUMN tenant_id NVARCHAR(50) NOT NULL;
ALTER TABLE offers ADD CONSTRAINT fk_offers_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_offers_tenant_id ON offers(tenant_id);

-- pipeline_transitions
ALTER TABLE pipeline_transitions ADD tenant_id NVARCHAR(50);
GO
UPDATE pipeline_transitions SET tenant_id = 'default';
ALTER TABLE pipeline_transitions ALTER COLUMN tenant_id NVARCHAR(50) NOT NULL;
ALTER TABLE pipeline_transitions ADD CONSTRAINT fk_pipeline_transitions_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_pipeline_transitions_tenant_id ON pipeline_transitions(tenant_id);

-- talent_pools
ALTER TABLE talent_pools ADD tenant_id NVARCHAR(50);
GO
UPDATE talent_pools SET tenant_id = 'default';
ALTER TABLE talent_pools ALTER COLUMN tenant_id NVARCHAR(50) NOT NULL;
ALTER TABLE talent_pools ADD CONSTRAINT fk_talent_pools_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_talent_pools_tenant_id ON talent_pools(tenant_id);

-- talent_pool_entries
ALTER TABLE talent_pool_entries ADD tenant_id NVARCHAR(50);
GO
UPDATE talent_pool_entries SET tenant_id = 'default';
ALTER TABLE talent_pool_entries ALTER COLUMN tenant_id NVARCHAR(50) NOT NULL;
ALTER TABLE talent_pool_entries ADD CONSTRAINT fk_talent_pool_entries_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_talent_pool_entries_tenant_id ON talent_pool_entries(tenant_id);

-- screening_questions
ALTER TABLE screening_questions ADD tenant_id NVARCHAR(50);
GO
UPDATE screening_questions SET tenant_id = 'default';
ALTER TABLE screening_questions ALTER COLUMN tenant_id NVARCHAR(50) NOT NULL;
ALTER TABLE screening_questions ADD CONSTRAINT fk_screening_questions_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_screening_questions_tenant_id ON screening_questions(tenant_id);

-- screening_answers
ALTER TABLE screening_answers ADD tenant_id NVARCHAR(50);
GO
UPDATE screening_answers SET tenant_id = 'default';
ALTER TABLE screening_answers ALTER COLUMN tenant_id NVARCHAR(50) NOT NULL;
ALTER TABLE screening_answers ADD CONSTRAINT fk_screening_answers_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_screening_answers_tenant_id ON screening_answers(tenant_id);

-- shortlist_scores
ALTER TABLE shortlist_scores ADD tenant_id NVARCHAR(50);
GO
UPDATE shortlist_scores SET tenant_id = 'default';
ALTER TABLE shortlist_scores ALTER COLUMN tenant_id NVARCHAR(50) NOT NULL;
ALTER TABLE shortlist_scores ADD CONSTRAINT fk_shortlist_scores_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_shortlist_scores_tenant_id ON shortlist_scores(tenant_id);

-- messages
ALTER TABLE messages ADD tenant_id NVARCHAR(50);
GO
UPDATE messages SET tenant_id = 'default';
ALTER TABLE messages ALTER COLUMN tenant_id NVARCHAR(50) NOT NULL;
ALTER TABLE messages ADD CONSTRAINT fk_messages_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_messages_tenant_id ON messages(tenant_id);

-- notifications
ALTER TABLE notifications ADD tenant_id NVARCHAR(50);
GO
UPDATE notifications SET tenant_id = 'default';
ALTER TABLE notifications ALTER COLUMN tenant_id NVARCHAR(50) NOT NULL;
ALTER TABLE notifications ADD CONSTRAINT fk_notifications_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_notifications_tenant_id ON notifications(tenant_id);

-- agency_profiles
ALTER TABLE agency_profiles ADD tenant_id NVARCHAR(50);
GO
UPDATE agency_profiles SET tenant_id = 'default';
ALTER TABLE agency_profiles ALTER COLUMN tenant_id NVARCHAR(50) NOT NULL;
ALTER TABLE agency_profiles ADD CONSTRAINT fk_agency_profiles_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_agency_profiles_tenant_id ON agency_profiles(tenant_id);

-- agency_submissions
ALTER TABLE agency_submissions ADD tenant_id NVARCHAR(50);
GO
UPDATE agency_submissions SET tenant_id = 'default';
ALTER TABLE agency_submissions ALTER COLUMN tenant_id NVARCHAR(50) NOT NULL;
ALTER TABLE agency_submissions ADD CONSTRAINT fk_agency_submissions_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_agency_submissions_tenant_id ON agency_submissions(tenant_id);

-- recruitment_metrics
ALTER TABLE recruitment_metrics ADD tenant_id NVARCHAR(50);
GO
UPDATE recruitment_metrics SET tenant_id = 'default';
ALTER TABLE recruitment_metrics ALTER COLUMN tenant_id NVARCHAR(50) NOT NULL;
ALTER TABLE recruitment_metrics ADD CONSTRAINT fk_recruitment_metrics_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_recruitment_metrics_tenant_id ON recruitment_metrics(tenant_id);

-- tg_salary_recommendations
ALTER TABLE tg_salary_recommendations ADD tenant_id NVARCHAR(50);
GO
UPDATE tg_salary_recommendations SET tenant_id = 'default';
ALTER TABLE tg_salary_recommendations ALTER COLUMN tenant_id NVARCHAR(50) NOT NULL;
ALTER TABLE tg_salary_recommendations ADD CONSTRAINT fk_tg_salary_recommendations_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_tg_salary_recommendations_tenant_id ON tg_salary_recommendations(tenant_id);

-- tg_job_board_postings
ALTER TABLE tg_job_board_postings ADD tenant_id NVARCHAR(50);
GO
UPDATE tg_job_board_postings SET tenant_id = 'default';
ALTER TABLE tg_job_board_postings ALTER COLUMN tenant_id NVARCHAR(50) NOT NULL;
ALTER TABLE tg_job_board_postings ADD CONSTRAINT fk_tg_job_board_postings_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_tg_job_board_postings_tenant_id ON tg_job_board_postings(tenant_id);

-- ============================================================
-- 4. ADD tenant_id TO V003 TABLES (2 tables)
-- ============================================================

-- job_ads
ALTER TABLE job_ads ADD tenant_id NVARCHAR(50);
GO
UPDATE job_ads SET tenant_id = 'default';
ALTER TABLE job_ads ALTER COLUMN tenant_id NVARCHAR(50) NOT NULL;
ALTER TABLE job_ads ADD CONSTRAINT fk_job_ads_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_job_ads_tenant_id ON job_ads(tenant_id);

-- job_ad_history
ALTER TABLE job_ad_history ADD tenant_id NVARCHAR(50);
GO
UPDATE job_ad_history SET tenant_id = 'default';
ALTER TABLE job_ad_history ALTER COLUMN tenant_id NVARCHAR(50) NOT NULL;
ALTER TABLE job_ad_history ADD CONSTRAINT fk_job_ad_history_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_job_ad_history_tenant_id ON job_ad_history(tenant_id);

-- ============================================================
-- 5. ADD tenant_id TO V004 CHILD TABLES (4 tables)
-- ============================================================

-- performance_goals
ALTER TABLE performance_goals ADD tenant_id NVARCHAR(50);
GO
UPDATE performance_goals SET tenant_id = 'default';
ALTER TABLE performance_goals ALTER COLUMN tenant_id NVARCHAR(50) NOT NULL;
ALTER TABLE performance_goals ADD CONSTRAINT fk_performance_goals_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_performance_goals_tenant_id ON performance_goals(tenant_id);

-- goal_kpis
ALTER TABLE goal_kpis ADD tenant_id NVARCHAR(50);
GO
UPDATE goal_kpis SET tenant_id = 'default';
ALTER TABLE goal_kpis ALTER COLUMN tenant_id NVARCHAR(50) NOT NULL;
ALTER TABLE goal_kpis ADD CONSTRAINT fk_goal_kpis_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_goal_kpis_tenant_id ON goal_kpis(tenant_id);

-- review_goal_scores
ALTER TABLE review_goal_scores ADD tenant_id NVARCHAR(50);
GO
UPDATE review_goal_scores SET tenant_id = 'default';
ALTER TABLE review_goal_scores ALTER COLUMN tenant_id NVARCHAR(50) NOT NULL;
ALTER TABLE review_goal_scores ADD CONSTRAINT fk_review_goal_scores_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_review_goal_scores_tenant_id ON review_goal_scores(tenant_id);

-- review_evidence
ALTER TABLE review_evidence ADD tenant_id NVARCHAR(50);
GO
UPDATE review_evidence SET tenant_id = 'default';
ALTER TABLE review_evidence ALTER COLUMN tenant_id NVARCHAR(50) NOT NULL;
ALTER TABLE review_evidence ADD CONSTRAINT fk_review_evidence_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_review_evidence_tenant_id ON review_evidence(tenant_id);

-- ============================================================
-- 6. ADD FK + INDEX TO V004 PARENT TABLES (already have tenant_id)
-- ============================================================

ALTER TABLE performance_templates ADD CONSTRAINT fk_performance_templates_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
ALTER TABLE performance_cycles ADD CONSTRAINT fk_performance_cycles_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
ALTER TABLE performance_contracts ADD CONSTRAINT fk_performance_contracts_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
ALTER TABLE performance_reviews ADD CONSTRAINT fk_performance_reviews_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);

-- ============================================================
-- 7. RE-SCOPE UNIQUE CONSTRAINTS TO BE TENANT-AWARE
-- ============================================================

-- users: drop global unique, add tenant-scoped
IF EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_users_username' AND object_id = OBJECT_ID('users'))
    DROP INDEX idx_users_username ON users;
IF EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_users_email' AND object_id = OBJECT_ID('users'))
    DROP INDEX idx_users_email ON users;
GO

-- Drop existing unique constraints on users
DECLARE @sql NVARCHAR(MAX) = '';
SELECT @sql = @sql + 'ALTER TABLE users DROP CONSTRAINT ' + name + '; '
FROM sys.key_constraints
WHERE parent_object_id = OBJECT_ID('users') AND type = 'UQ';
EXEC sp_executesql @sql;
GO

ALTER TABLE users ADD CONSTRAINT uq_users_tenant_username UNIQUE (tenant_id, username);
ALTER TABLE users ADD CONSTRAINT uq_users_tenant_email UNIQUE (tenant_id, email);

-- applicants: drop global unique, add tenant-scoped
DECLARE @sql2 NVARCHAR(MAX) = '';
SELECT @sql2 = @sql2 + 'ALTER TABLE applicants DROP CONSTRAINT ' + name + '; '
FROM sys.key_constraints
WHERE parent_object_id = OBJECT_ID('applicants') AND type = 'UQ';
EXEC sp_executesql @sql2;
GO

ALTER TABLE applicants ADD CONSTRAINT uq_applicants_tenant_email UNIQUE (tenant_id, email);

-- job_postings: drop global unique on slug, add tenant-scoped
DECLARE @sql3 NVARCHAR(MAX) = '';
SELECT @sql3 = @sql3 + 'ALTER TABLE job_postings DROP CONSTRAINT ' + name + '; '
FROM sys.key_constraints
WHERE parent_object_id = OBJECT_ID('job_postings') AND type = 'UQ';
EXEC sp_executesql @sql3;
GO

IF EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_job_postings_slug' AND object_id = OBJECT_ID('job_postings'))
    DROP INDEX idx_job_postings_slug ON job_postings;
ALTER TABLE job_postings ADD CONSTRAINT uq_job_postings_tenant_slug UNIQUE (tenant_id, slug);

-- offers: drop global unique, add tenant-scoped
DECLARE @sql4 NVARCHAR(MAX) = '';
SELECT @sql4 = @sql4 + 'ALTER TABLE offers DROP CONSTRAINT ' + name + '; '
FROM sys.key_constraints
WHERE parent_object_id = OBJECT_ID('offers') AND type = 'UQ';
EXEC sp_executesql @sql4;
GO

IF EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_offers_offer_number' AND object_id = OBJECT_ID('offers'))
    DROP INDEX idx_offers_offer_number ON offers;
ALTER TABLE offers ADD CONSTRAINT uq_offers_tenant_offer_number UNIQUE (tenant_id, offer_number);

-- talent_pools: drop global unique, add tenant-scoped
DECLARE @sql5 NVARCHAR(MAX) = '';
SELECT @sql5 = @sql5 + 'ALTER TABLE talent_pools DROP CONSTRAINT ' + name + '; '
FROM sys.key_constraints
WHERE parent_object_id = OBJECT_ID('talent_pools') AND type = 'UQ';
EXEC sp_executesql @sql5;
GO

ALTER TABLE talent_pools ADD CONSTRAINT uq_talent_pools_tenant_pool_name UNIQUE (tenant_id, pool_name);

-- talent_pool_entries: drop global unique, add tenant-scoped
DECLARE @sql6 NVARCHAR(MAX) = '';
SELECT @sql6 = @sql6 + 'ALTER TABLE talent_pool_entries DROP CONSTRAINT ' + name + '; '
FROM sys.key_constraints
WHERE parent_object_id = OBJECT_ID('talent_pool_entries') AND type = 'UQ';
EXEC sp_executesql @sql6;
GO

ALTER TABLE talent_pool_entries ADD CONSTRAINT uq_talent_pool_entries_tenant UNIQUE (tenant_id, talent_pool_id, applicant_id);

-- agency_profiles: drop global uniques, add tenant-scoped
DECLARE @sql7 NVARCHAR(MAX) = '';
SELECT @sql7 = @sql7 + 'ALTER TABLE agency_profiles DROP CONSTRAINT ' + name + '; '
FROM sys.key_constraints
WHERE parent_object_id = OBJECT_ID('agency_profiles') AND type = 'UQ';
EXEC sp_executesql @sql7;
GO

ALTER TABLE agency_profiles ADD CONSTRAINT uq_agency_profiles_tenant_name UNIQUE (tenant_id, agency_name);
ALTER TABLE agency_profiles ADD CONSTRAINT uq_agency_profiles_tenant_reg_number UNIQUE (tenant_id, registration_number);
ALTER TABLE agency_profiles ADD CONSTRAINT uq_agency_profiles_tenant_email UNIQUE (tenant_id, contact_email);

-- tg_salary_recommendations: drop global unique, add tenant-scoped
DECLARE @sql8 NVARCHAR(MAX) = '';
SELECT @sql8 = @sql8 + 'ALTER TABLE tg_salary_recommendations DROP CONSTRAINT ' + name + '; '
FROM sys.key_constraints
WHERE parent_object_id = OBJECT_ID('tg_salary_recommendations') AND type = 'UQ';
EXEC sp_executesql @sql8;
GO

ALTER TABLE tg_salary_recommendations ADD CONSTRAINT uq_tg_salary_recommendations_tenant_number UNIQUE (tenant_id, recommendation_number);

-- job_ads: drop global unique on slug, add tenant-scoped
DECLARE @sql9 NVARCHAR(MAX) = '';
SELECT @sql9 = @sql9 + 'ALTER TABLE job_ads DROP CONSTRAINT ' + name + '; '
FROM sys.key_constraints
WHERE parent_object_id = OBJECT_ID('job_ads') AND type = 'UQ';
EXEC sp_executesql @sql9;
GO

ALTER TABLE job_ads ADD CONSTRAINT uq_job_ads_tenant_slug UNIQUE (tenant_id, slug);

-- ============================================================
-- 8. ROW LEVEL SECURITY POLICIES (SQL Server)
-- Uses SESSION_CONTEXT instead of PostgreSQL current_setting
-- ============================================================

-- Create tenant filter predicate function
CREATE FUNCTION dbo.fn_tenant_filter(@tenant_id NVARCHAR(50))
RETURNS TABLE
WITH SCHEMABINDING
AS
    RETURN SELECT 1 AS result
    WHERE @tenant_id = CAST(SESSION_CONTEXT(N'TenantId') AS NVARCHAR(50));
GO

-- Tenants table (filter by id)
CREATE FUNCTION dbo.fn_tenant_filter_by_id(@id NVARCHAR(50))
RETURNS TABLE
WITH SCHEMABINDING
AS
    RETURN SELECT 1 AS result
    WHERE @id = CAST(SESSION_CONTEXT(N'TenantId') AS NVARCHAR(50));
GO

-- Apply security policies to all tables

CREATE SECURITY POLICY dbo.tenants_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter_by_id(id) ON dbo.tenants,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter_by_id(id) ON dbo.tenants
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.users_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.users,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.users
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.applicants_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.applicants,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.applicants
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.job_postings_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.job_postings,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.job_postings
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.applications_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.applications,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.applications
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.documents_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.documents,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.documents
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.audit_logs_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.audit_logs,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.audit_logs
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.interviews_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.interviews,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.interviews
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.offers_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.offers,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.offers
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.pipeline_transitions_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.pipeline_transitions,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.pipeline_transitions
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.talent_pools_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.talent_pools,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.talent_pools
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.talent_pool_entries_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.talent_pool_entries,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.talent_pool_entries
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.screening_questions_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.screening_questions,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.screening_questions
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.screening_answers_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.screening_answers,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.screening_answers
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.shortlist_scores_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.shortlist_scores,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.shortlist_scores
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.messages_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.messages,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.messages
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.notifications_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.notifications,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.notifications
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.agency_profiles_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.agency_profiles,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.agency_profiles
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.agency_submissions_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.agency_submissions,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.agency_submissions
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.recruitment_metrics_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.recruitment_metrics,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.recruitment_metrics
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.tg_salary_recommendations_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.tg_salary_recommendations,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.tg_salary_recommendations
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.tg_job_board_postings_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.tg_job_board_postings,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.tg_job_board_postings
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.job_ads_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.job_ads,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.job_ads
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.job_ad_history_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.job_ad_history,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.job_ad_history
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.performance_templates_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.performance_templates,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.performance_templates
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.performance_cycles_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.performance_cycles,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.performance_cycles
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.performance_contracts_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.performance_contracts,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.performance_contracts
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.performance_reviews_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.performance_reviews,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.performance_reviews
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.performance_goals_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.performance_goals,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.performance_goals
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.goal_kpis_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.goal_kpis,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.goal_kpis
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.review_goal_scores_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.review_goal_scores,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.review_goal_scores
    WITH (STATE = ON);
GO

CREATE SECURITY POLICY dbo.review_evidence_policy
    ADD FILTER PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.review_evidence,
    ADD BLOCK PREDICATE dbo.fn_tenant_filter(tenant_id) ON dbo.review_evidence
    WITH (STATE = ON);
GO
