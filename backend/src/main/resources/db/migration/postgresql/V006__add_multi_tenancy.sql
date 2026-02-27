-- V006: Add multi-tenancy support across all tables
-- Creates tenants table, adds tenant_id to all 31 tables, re-scopes unique constraints, adds RLS policies

-- ============================================================
-- 1. TENANTS TABLE
-- ============================================================
CREATE TABLE tenants (
    id              VARCHAR(50) PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    subdomain       VARCHAR(63) NOT NULL UNIQUE,
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    plan            VARCHAR(30) NOT NULL DEFAULT 'STANDARD',
    contact_email   VARCHAR(255) NOT NULL,
    contact_name    VARCHAR(255),
    max_users       INTEGER DEFAULT 50,
    settings        JSONB DEFAULT '{}',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_tenants_status CHECK (status IN ('ACTIVE', 'SUSPENDED', 'TRIAL', 'CANCELLED')),
    CONSTRAINT chk_tenants_plan CHECK (plan IN ('TRIAL', 'STARTER', 'STANDARD', 'ENTERPRISE'))
);

CREATE INDEX idx_tenants_subdomain ON tenants(subdomain);
CREATE INDEX idx_tenants_status ON tenants(status);

CREATE TRIGGER update_tenants_updated_at
    BEFORE UPDATE ON tenants
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE tenants IS 'Organizations using the ShumelaHire platform';

-- Insert default tenant for data backfill
INSERT INTO tenants (id, name, subdomain, status, plan, contact_email, contact_name)
VALUES ('default', 'Default Organization', 'default', 'ACTIVE', 'STANDARD', 'admin@shumelahire.co.za', 'System Admin');

-- ============================================================
-- 2. ADD tenant_id TO V001 TABLES (6 tables)
-- ============================================================

-- users
ALTER TABLE users ADD COLUMN tenant_id VARCHAR(50);
UPDATE users SET tenant_id = 'default';
ALTER TABLE users ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE users ADD CONSTRAINT fk_users_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_users_tenant_id ON users(tenant_id);

-- applicants
ALTER TABLE applicants ADD COLUMN tenant_id VARCHAR(50);
UPDATE applicants SET tenant_id = 'default';
ALTER TABLE applicants ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE applicants ADD CONSTRAINT fk_applicants_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_applicants_tenant_id ON applicants(tenant_id);

-- job_postings
ALTER TABLE job_postings ADD COLUMN tenant_id VARCHAR(50);
UPDATE job_postings SET tenant_id = 'default';
ALTER TABLE job_postings ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE job_postings ADD CONSTRAINT fk_job_postings_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_job_postings_tenant_id ON job_postings(tenant_id);

-- applications
ALTER TABLE applications ADD COLUMN tenant_id VARCHAR(50);
UPDATE applications SET tenant_id = 'default';
ALTER TABLE applications ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE applications ADD CONSTRAINT fk_applications_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_applications_tenant_id ON applications(tenant_id);

-- documents
ALTER TABLE documents ADD COLUMN tenant_id VARCHAR(50);
UPDATE documents SET tenant_id = 'default';
ALTER TABLE documents ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE documents ADD CONSTRAINT fk_documents_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_documents_tenant_id ON documents(tenant_id);

-- audit_logs
ALTER TABLE audit_logs ADD COLUMN tenant_id VARCHAR(50);
UPDATE audit_logs SET tenant_id = 'default';
ALTER TABLE audit_logs ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE audit_logs ADD CONSTRAINT fk_audit_logs_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_audit_logs_tenant_id ON audit_logs(tenant_id);

-- ============================================================
-- 3. ADD tenant_id TO V002 TABLES (15 tables)
-- ============================================================

-- interviews
ALTER TABLE interviews ADD COLUMN tenant_id VARCHAR(50);
UPDATE interviews SET tenant_id = 'default';
ALTER TABLE interviews ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE interviews ADD CONSTRAINT fk_interviews_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_interviews_tenant_id ON interviews(tenant_id);

-- offers
ALTER TABLE offers ADD COLUMN tenant_id VARCHAR(50);
UPDATE offers SET tenant_id = 'default';
ALTER TABLE offers ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE offers ADD CONSTRAINT fk_offers_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_offers_tenant_id ON offers(tenant_id);

-- pipeline_transitions
ALTER TABLE pipeline_transitions ADD COLUMN tenant_id VARCHAR(50);
UPDATE pipeline_transitions SET tenant_id = 'default';
ALTER TABLE pipeline_transitions ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE pipeline_transitions ADD CONSTRAINT fk_pipeline_transitions_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_pipeline_transitions_tenant_id ON pipeline_transitions(tenant_id);

-- talent_pools
ALTER TABLE talent_pools ADD COLUMN tenant_id VARCHAR(50);
UPDATE talent_pools SET tenant_id = 'default';
ALTER TABLE talent_pools ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE talent_pools ADD CONSTRAINT fk_talent_pools_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_talent_pools_tenant_id ON talent_pools(tenant_id);

-- talent_pool_entries
ALTER TABLE talent_pool_entries ADD COLUMN tenant_id VARCHAR(50);
UPDATE talent_pool_entries SET tenant_id = 'default';
ALTER TABLE talent_pool_entries ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE talent_pool_entries ADD CONSTRAINT fk_talent_pool_entries_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_talent_pool_entries_tenant_id ON talent_pool_entries(tenant_id);

-- screening_questions
ALTER TABLE screening_questions ADD COLUMN tenant_id VARCHAR(50);
UPDATE screening_questions SET tenant_id = 'default';
ALTER TABLE screening_questions ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE screening_questions ADD CONSTRAINT fk_screening_questions_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_screening_questions_tenant_id ON screening_questions(tenant_id);

-- screening_answers
ALTER TABLE screening_answers ADD COLUMN tenant_id VARCHAR(50);
UPDATE screening_answers SET tenant_id = 'default';
ALTER TABLE screening_answers ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE screening_answers ADD CONSTRAINT fk_screening_answers_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_screening_answers_tenant_id ON screening_answers(tenant_id);

-- shortlist_scores
ALTER TABLE shortlist_scores ADD COLUMN tenant_id VARCHAR(50);
UPDATE shortlist_scores SET tenant_id = 'default';
ALTER TABLE shortlist_scores ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE shortlist_scores ADD CONSTRAINT fk_shortlist_scores_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_shortlist_scores_tenant_id ON shortlist_scores(tenant_id);

-- messages
ALTER TABLE messages ADD COLUMN tenant_id VARCHAR(50);
UPDATE messages SET tenant_id = 'default';
ALTER TABLE messages ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE messages ADD CONSTRAINT fk_messages_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_messages_tenant_id ON messages(tenant_id);

-- notifications
ALTER TABLE notifications ADD COLUMN tenant_id VARCHAR(50);
UPDATE notifications SET tenant_id = 'default';
ALTER TABLE notifications ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE notifications ADD CONSTRAINT fk_notifications_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_notifications_tenant_id ON notifications(tenant_id);

-- agency_profiles
ALTER TABLE agency_profiles ADD COLUMN tenant_id VARCHAR(50);
UPDATE agency_profiles SET tenant_id = 'default';
ALTER TABLE agency_profiles ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE agency_profiles ADD CONSTRAINT fk_agency_profiles_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_agency_profiles_tenant_id ON agency_profiles(tenant_id);

-- agency_submissions
ALTER TABLE agency_submissions ADD COLUMN tenant_id VARCHAR(50);
UPDATE agency_submissions SET tenant_id = 'default';
ALTER TABLE agency_submissions ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE agency_submissions ADD CONSTRAINT fk_agency_submissions_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_agency_submissions_tenant_id ON agency_submissions(tenant_id);

-- recruitment_metrics
ALTER TABLE recruitment_metrics ADD COLUMN tenant_id VARCHAR(50);
UPDATE recruitment_metrics SET tenant_id = 'default';
ALTER TABLE recruitment_metrics ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE recruitment_metrics ADD CONSTRAINT fk_recruitment_metrics_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_recruitment_metrics_tenant_id ON recruitment_metrics(tenant_id);

-- tg_salary_recommendations
ALTER TABLE tg_salary_recommendations ADD COLUMN tenant_id VARCHAR(50);
UPDATE tg_salary_recommendations SET tenant_id = 'default';
ALTER TABLE tg_salary_recommendations ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE tg_salary_recommendations ADD CONSTRAINT fk_tg_salary_recommendations_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_tg_salary_recommendations_tenant_id ON tg_salary_recommendations(tenant_id);

-- tg_job_board_postings
ALTER TABLE tg_job_board_postings ADD COLUMN tenant_id VARCHAR(50);
UPDATE tg_job_board_postings SET tenant_id = 'default';
ALTER TABLE tg_job_board_postings ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE tg_job_board_postings ADD CONSTRAINT fk_tg_job_board_postings_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_tg_job_board_postings_tenant_id ON tg_job_board_postings(tenant_id);

-- ============================================================
-- 4. ADD tenant_id TO V003 TABLES (2 tables)
-- ============================================================

-- job_ads
ALTER TABLE job_ads ADD COLUMN tenant_id VARCHAR(50);
UPDATE job_ads SET tenant_id = 'default';
ALTER TABLE job_ads ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE job_ads ADD CONSTRAINT fk_job_ads_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_job_ads_tenant_id ON job_ads(tenant_id);

-- job_ad_history
ALTER TABLE job_ad_history ADD COLUMN tenant_id VARCHAR(50);
UPDATE job_ad_history SET tenant_id = 'default';
ALTER TABLE job_ad_history ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE job_ad_history ADD CONSTRAINT fk_job_ad_history_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_job_ad_history_tenant_id ON job_ad_history(tenant_id);

-- ============================================================
-- 5. ADD tenant_id TO V004 CHILD TABLES (4 tables)
-- ============================================================

-- performance_goals
ALTER TABLE performance_goals ADD COLUMN tenant_id VARCHAR(50);
UPDATE performance_goals SET tenant_id = 'default';
ALTER TABLE performance_goals ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE performance_goals ADD CONSTRAINT fk_performance_goals_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_performance_goals_tenant_id ON performance_goals(tenant_id);

-- goal_kpis
ALTER TABLE goal_kpis ADD COLUMN tenant_id VARCHAR(50);
UPDATE goal_kpis SET tenant_id = 'default';
ALTER TABLE goal_kpis ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE goal_kpis ADD CONSTRAINT fk_goal_kpis_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_goal_kpis_tenant_id ON goal_kpis(tenant_id);

-- review_goal_scores
ALTER TABLE review_goal_scores ADD COLUMN tenant_id VARCHAR(50);
UPDATE review_goal_scores SET tenant_id = 'default';
ALTER TABLE review_goal_scores ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE review_goal_scores ADD CONSTRAINT fk_review_goal_scores_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
CREATE INDEX idx_review_goal_scores_tenant_id ON review_goal_scores(tenant_id);

-- review_evidence
ALTER TABLE review_evidence ADD COLUMN tenant_id VARCHAR(50);
UPDATE review_evidence SET tenant_id = 'default';
ALTER TABLE review_evidence ALTER COLUMN tenant_id SET NOT NULL;
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
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_username_key;
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_email_key;
DROP INDEX IF EXISTS idx_users_username;
DROP INDEX IF EXISTS idx_users_email;
ALTER TABLE users ADD CONSTRAINT uq_users_tenant_username UNIQUE (tenant_id, username);
ALTER TABLE users ADD CONSTRAINT uq_users_tenant_email UNIQUE (tenant_id, email);

-- applicants: drop global unique, add tenant-scoped
ALTER TABLE applicants DROP CONSTRAINT IF EXISTS applicants_email_key;
DROP INDEX IF EXISTS idx_applicants_email;
ALTER TABLE applicants ADD CONSTRAINT uq_applicants_tenant_email UNIQUE (tenant_id, email);

-- job_postings: drop global unique, add tenant-scoped
ALTER TABLE job_postings DROP CONSTRAINT IF EXISTS job_postings_slug_key;
DROP INDEX IF EXISTS idx_job_postings_slug;
ALTER TABLE job_postings ADD CONSTRAINT uq_job_postings_tenant_slug UNIQUE (tenant_id, slug);

-- offers: drop global unique, add tenant-scoped
ALTER TABLE offers DROP CONSTRAINT IF EXISTS offers_offer_number_key;
DROP INDEX IF EXISTS idx_offers_offer_number;
ALTER TABLE offers ADD CONSTRAINT uq_offers_tenant_offer_number UNIQUE (tenant_id, offer_number);

-- talent_pools: drop global unique, add tenant-scoped
ALTER TABLE talent_pools DROP CONSTRAINT IF EXISTS talent_pools_pool_name_key;
ALTER TABLE talent_pools ADD CONSTRAINT uq_talent_pools_tenant_pool_name UNIQUE (tenant_id, pool_name);

-- talent_pool_entries: drop global unique, add tenant-scoped
ALTER TABLE talent_pool_entries DROP CONSTRAINT IF EXISTS uq_talent_pool_entries;
ALTER TABLE talent_pool_entries ADD CONSTRAINT uq_talent_pool_entries_tenant UNIQUE (tenant_id, talent_pool_id, applicant_id);

-- agency_profiles: drop global uniques, add tenant-scoped
ALTER TABLE agency_profiles DROP CONSTRAINT IF EXISTS agency_profiles_agency_name_key;
ALTER TABLE agency_profiles DROP CONSTRAINT IF EXISTS agency_profiles_registration_number_key;
ALTER TABLE agency_profiles DROP CONSTRAINT IF EXISTS agency_profiles_contact_email_key;
ALTER TABLE agency_profiles ADD CONSTRAINT uq_agency_profiles_tenant_name UNIQUE (tenant_id, agency_name);
ALTER TABLE agency_profiles ADD CONSTRAINT uq_agency_profiles_tenant_reg_number UNIQUE (tenant_id, registration_number);
ALTER TABLE agency_profiles ADD CONSTRAINT uq_agency_profiles_tenant_email UNIQUE (tenant_id, contact_email);

-- tg_salary_recommendations: drop global unique, add tenant-scoped
ALTER TABLE tg_salary_recommendations DROP CONSTRAINT IF EXISTS tg_salary_recommendations_recommendation_number_key;
ALTER TABLE tg_salary_recommendations ADD CONSTRAINT uq_tg_salary_recommendations_tenant_number UNIQUE (tenant_id, recommendation_number);

-- job_ads: drop global unique, add tenant-scoped
ALTER TABLE job_ads DROP CONSTRAINT IF EXISTS job_ads_slug_key;
ALTER TABLE job_ads ADD CONSTRAINT uq_job_ads_tenant_slug UNIQUE (tenant_id, slug);

-- ============================================================
-- 8. ROW LEVEL SECURITY POLICIES (PostgreSQL only)
-- ============================================================

-- RLS on tenants table (filter by id instead of tenant_id)
ALTER TABLE tenants ENABLE ROW LEVEL SECURITY;
ALTER TABLE tenants FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_tenants ON tenants
    USING (id = current_setting('app.current_tenant', true))
    WITH CHECK (id = current_setting('app.current_tenant', true));

-- V001 tables
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE users FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_users ON users
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

ALTER TABLE applicants ENABLE ROW LEVEL SECURITY;
ALTER TABLE applicants FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_applicants ON applicants
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

ALTER TABLE job_postings ENABLE ROW LEVEL SECURITY;
ALTER TABLE job_postings FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_job_postings ON job_postings
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

ALTER TABLE applications ENABLE ROW LEVEL SECURITY;
ALTER TABLE applications FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_applications ON applications
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

ALTER TABLE documents ENABLE ROW LEVEL SECURITY;
ALTER TABLE documents FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_documents ON documents
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

ALTER TABLE audit_logs ENABLE ROW LEVEL SECURITY;
ALTER TABLE audit_logs FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_audit_logs ON audit_logs
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

-- V002 tables
ALTER TABLE interviews ENABLE ROW LEVEL SECURITY;
ALTER TABLE interviews FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_interviews ON interviews
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

ALTER TABLE offers ENABLE ROW LEVEL SECURITY;
ALTER TABLE offers FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_offers ON offers
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

ALTER TABLE pipeline_transitions ENABLE ROW LEVEL SECURITY;
ALTER TABLE pipeline_transitions FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_pipeline_transitions ON pipeline_transitions
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

ALTER TABLE talent_pools ENABLE ROW LEVEL SECURITY;
ALTER TABLE talent_pools FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_talent_pools ON talent_pools
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

ALTER TABLE talent_pool_entries ENABLE ROW LEVEL SECURITY;
ALTER TABLE talent_pool_entries FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_talent_pool_entries ON talent_pool_entries
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

ALTER TABLE screening_questions ENABLE ROW LEVEL SECURITY;
ALTER TABLE screening_questions FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_screening_questions ON screening_questions
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

ALTER TABLE screening_answers ENABLE ROW LEVEL SECURITY;
ALTER TABLE screening_answers FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_screening_answers ON screening_answers
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

ALTER TABLE shortlist_scores ENABLE ROW LEVEL SECURITY;
ALTER TABLE shortlist_scores FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_shortlist_scores ON shortlist_scores
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

ALTER TABLE messages ENABLE ROW LEVEL SECURITY;
ALTER TABLE messages FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_messages ON messages
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

ALTER TABLE notifications ENABLE ROW LEVEL SECURITY;
ALTER TABLE notifications FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_notifications ON notifications
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

ALTER TABLE agency_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE agency_profiles FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_agency_profiles ON agency_profiles
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

ALTER TABLE agency_submissions ENABLE ROW LEVEL SECURITY;
ALTER TABLE agency_submissions FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_agency_submissions ON agency_submissions
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

ALTER TABLE recruitment_metrics ENABLE ROW LEVEL SECURITY;
ALTER TABLE recruitment_metrics FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_recruitment_metrics ON recruitment_metrics
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

ALTER TABLE tg_salary_recommendations ENABLE ROW LEVEL SECURITY;
ALTER TABLE tg_salary_recommendations FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_tg_salary_recommendations ON tg_salary_recommendations
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

ALTER TABLE tg_job_board_postings ENABLE ROW LEVEL SECURITY;
ALTER TABLE tg_job_board_postings FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_tg_job_board_postings ON tg_job_board_postings
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

-- V003 tables
ALTER TABLE job_ads ENABLE ROW LEVEL SECURITY;
ALTER TABLE job_ads FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_job_ads ON job_ads
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

ALTER TABLE job_ad_history ENABLE ROW LEVEL SECURITY;
ALTER TABLE job_ad_history FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_job_ad_history ON job_ad_history
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

-- V004 tables (all 8)
ALTER TABLE performance_templates ENABLE ROW LEVEL SECURITY;
ALTER TABLE performance_templates FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_performance_templates ON performance_templates
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

ALTER TABLE performance_cycles ENABLE ROW LEVEL SECURITY;
ALTER TABLE performance_cycles FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_performance_cycles ON performance_cycles
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

ALTER TABLE performance_contracts ENABLE ROW LEVEL SECURITY;
ALTER TABLE performance_contracts FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_performance_contracts ON performance_contracts
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

ALTER TABLE performance_reviews ENABLE ROW LEVEL SECURITY;
ALTER TABLE performance_reviews FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_performance_reviews ON performance_reviews
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

ALTER TABLE performance_goals ENABLE ROW LEVEL SECURITY;
ALTER TABLE performance_goals FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_performance_goals ON performance_goals
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

ALTER TABLE goal_kpis ENABLE ROW LEVEL SECURITY;
ALTER TABLE goal_kpis FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_goal_kpis ON goal_kpis
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

ALTER TABLE review_goal_scores ENABLE ROW LEVEL SECURITY;
ALTER TABLE review_goal_scores FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_review_goal_scores ON review_goal_scores
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));

ALTER TABLE review_evidence ENABLE ROW LEVEL SECURITY;
ALTER TABLE review_evidence FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation_review_evidence ON review_evidence
    USING (tenant_id = current_setting('app.current_tenant', true))
    WITH CHECK (tenant_id = current_setting('app.current_tenant', true));
