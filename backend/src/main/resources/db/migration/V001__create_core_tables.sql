-- V001: Core tables for ShumelaHire
-- Tables: users, applicants, job_postings, applications, documents, audit_logs

-- Reusable function to auto-update updated_at on row modification
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- ============================================================
-- USERS
-- ============================================================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    role VARCHAR(20) NOT NULL DEFAULT 'APPLICANT',
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    failed_login_attempts INTEGER NOT NULL DEFAULT 0,
    locked_until TIMESTAMP,
    password_reset_token VARCHAR(255),
    password_reset_expires TIMESTAMP,
    email_verification_token VARCHAR(255),
    two_factor_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    two_factor_secret VARCHAR(255),
    sso_provider VARCHAR(255),
    sso_user_id VARCHAR(255),

    CONSTRAINT chk_users_role CHECK (role IN (
        'ADMIN', 'EXECUTIVE', 'HR_MANAGER', 'HIRING_MANAGER',
        'RECRUITER', 'INTERVIEWER', 'EMPLOYEE', 'APPLICANT'
    ))
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_sso_provider ON users(sso_provider);
CREATE INDEX idx_users_sso_user_id ON users(sso_user_id);

CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE users IS 'Platform users with Spring Security integration';
COMMENT ON COLUMN users.role IS 'User role determining access permissions';
COMMENT ON COLUMN users.sso_provider IS 'SSO provider name (AZURE_AD, SAML2) or null for local auth';
COMMENT ON COLUMN users.locked_until IS 'Account locked until this time after too many failed login attempts';

-- ============================================================
-- APPLICANTS
-- ============================================================
CREATE TABLE applicants (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    id_passport_number VARCHAR(50),
    address TEXT,
    location VARCHAR(255),
    education TEXT,
    experience TEXT,
    skills TEXT,
    linkedin_url VARCHAR(255),
    portfolio_url VARCHAR(255),
    resume_url VARCHAR(255),
    cover_letter TEXT,
    source VARCHAR(255),
    gender VARCHAR(255),
    race VARCHAR(255),
    disability_status VARCHAR(255),
    citizenship_status VARCHAR(255),
    demographics_consent BOOLEAN,
    demographics_consent_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_applicants_email ON applicants(email);
CREATE INDEX idx_applicants_name_surname ON applicants(name, surname);
CREATE INDEX idx_applicants_source ON applicants(source);
CREATE INDEX idx_applicants_created_at ON applicants(created_at);

CREATE TRIGGER update_applicants_updated_at
    BEFORE UPDATE ON applicants
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE applicants IS 'Job applicant profiles with demographic data for Employment Equity compliance';
COMMENT ON COLUMN applicants.demographics_consent IS 'POPIA consent for collecting demographic information';

-- ============================================================
-- JOB POSTINGS
-- ============================================================
CREATE TABLE job_postings (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    department VARCHAR(100) NOT NULL,
    location VARCHAR(100),
    employment_type VARCHAR(50) NOT NULL DEFAULT 'FULL_TIME',
    experience_level VARCHAR(50) NOT NULL DEFAULT 'MID_LEVEL',
    description TEXT NOT NULL,
    requirements TEXT,
    responsibilities TEXT,
    qualifications TEXT,
    benefits TEXT,
    salary_min NUMERIC(10,2),
    salary_max NUMERIC(10,2),
    salary_currency VARCHAR(3) DEFAULT 'ZAR',
    remote_work_allowed BOOLEAN DEFAULT FALSE,
    travel_required BOOLEAN DEFAULT FALSE,
    application_deadline TIMESTAMP,
    positions_available INTEGER DEFAULT 1,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    created_by BIGINT NOT NULL,
    approved_by BIGINT,
    published_by BIGINT,
    approval_notes TEXT,
    rejection_reason TEXT,
    internal_notes TEXT,
    external_job_boards VARCHAR(255),
    seo_title VARCHAR(60),
    seo_description VARCHAR(160),
    seo_keywords VARCHAR(255),
    slug VARCHAR(255) UNIQUE,
    featured BOOLEAN DEFAULT FALSE,
    urgent BOOLEAN DEFAULT FALSE,
    views_count BIGINT DEFAULT 0,
    applications_count BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    submitted_for_approval_at TIMESTAMP,
    approved_at TIMESTAMP,
    published_at TIMESTAMP,
    unpublished_at TIMESTAMP,
    closed_at TIMESTAMP,

    CONSTRAINT chk_job_postings_status CHECK (status IN (
        'DRAFT', 'PENDING_APPROVAL', 'APPROVED', 'PUBLISHED',
        'UNPUBLISHED', 'REJECTED', 'CLOSED', 'CANCELLED'
    )),
    CONSTRAINT chk_job_postings_employment_type CHECK (employment_type IN (
        'FULL_TIME', 'PART_TIME', 'CONTRACT', 'INTERNSHIP', 'TEMPORARY', 'FREELANCE'
    )),
    CONSTRAINT chk_job_postings_experience_level CHECK (experience_level IN (
        'ENTRY_LEVEL', 'JUNIOR', 'MID_LEVEL', 'SENIOR', 'LEAD', 'MANAGER', 'DIRECTOR', 'EXECUTIVE'
    )),
    CONSTRAINT chk_job_postings_positions CHECK (positions_available >= 1),
    CONSTRAINT chk_job_postings_salary CHECK (salary_min IS NULL OR salary_max IS NULL OR salary_min <= salary_max)
);

CREATE INDEX idx_job_postings_status ON job_postings(status);
CREATE INDEX idx_job_postings_department ON job_postings(department);
CREATE INDEX idx_job_postings_created_by ON job_postings(created_by);
CREATE INDEX idx_job_postings_slug ON job_postings(slug);
CREATE INDEX idx_job_postings_published_at ON job_postings(published_at);
CREATE INDEX idx_job_postings_application_deadline ON job_postings(application_deadline);

CREATE TRIGGER update_job_postings_updated_at
    BEFORE UPDATE ON job_postings
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE job_postings IS 'Job postings with full approval workflow and SEO metadata';
COMMENT ON COLUMN job_postings.slug IS 'URL-friendly identifier for public job pages';

-- ============================================================
-- APPLICATIONS
-- ============================================================
CREATE TABLE applications (
    id BIGSERIAL PRIMARY KEY,
    applicant_id BIGINT NOT NULL,
    job_posting_id BIGINT,
    job_title VARCHAR(255),
    job_id VARCHAR(255),
    department VARCHAR(255),
    status VARCHAR(30) NOT NULL DEFAULT 'SUBMITTED',
    pipeline_stage VARCHAR(50) NOT NULL DEFAULT 'APPLICATION_RECEIVED',
    pipeline_stage_entered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cover_letter TEXT,
    application_source VARCHAR(255),
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    withdrawn_at TIMESTAMP,
    withdrawal_reason TEXT,
    screening_notes TEXT,
    interview_feedback TEXT,
    rating INTEGER,
    rejection_reason TEXT,
    offer_details TEXT,
    start_date TIMESTAMP,
    salary_expectation DOUBLE PRECISION,
    availability_date TIMESTAMP,
    interviewed_at TIMESTAMP,
    offer_extended_at TIMESTAMP,
    response_deadline TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_applications_applicant FOREIGN KEY (applicant_id) REFERENCES applicants(id) ON DELETE CASCADE,
    CONSTRAINT fk_applications_job_posting FOREIGN KEY (job_posting_id) REFERENCES job_postings(id) ON DELETE SET NULL,
    CONSTRAINT chk_applications_status CHECK (status IN (
        'SUBMITTED', 'SCREENING', 'INTERVIEW_SCHEDULED', 'INTERVIEW_COMPLETED',
        'REFERENCE_CHECK', 'OFFER_PENDING', 'OFFERED', 'OFFER_ACCEPTED',
        'OFFER_DECLINED', 'REJECTED', 'WITHDRAWN', 'HIRED'
    )),
    CONSTRAINT chk_applications_pipeline_stage CHECK (pipeline_stage IN (
        'APPLICATION_RECEIVED', 'INITIAL_SCREENING', 'PHONE_SCREENING',
        'FIRST_INTERVIEW', 'TECHNICAL_ASSESSMENT', 'SECOND_INTERVIEW',
        'PANEL_INTERVIEW', 'MANAGER_INTERVIEW', 'FINAL_INTERVIEW',
        'REFERENCE_CHECK', 'BACKGROUND_CHECK', 'OFFER_PREPARATION',
        'OFFER_EXTENDED', 'OFFER_NEGOTIATION', 'OFFER_ACCEPTED', 'HIRED',
        'WITHDRAWN', 'REJECTED', 'OFFER_DECLINED', 'NO_SHOW', 'DUPLICATE'
    )),
    CONSTRAINT chk_applications_rating CHECK (rating IS NULL OR (rating >= 1 AND rating <= 5))
);

CREATE INDEX idx_applications_applicant_id ON applications(applicant_id);
CREATE INDEX idx_applications_job_posting_id ON applications(job_posting_id);
CREATE INDEX idx_applications_status ON applications(status);
CREATE INDEX idx_applications_pipeline_stage ON applications(pipeline_stage);
CREATE INDEX idx_applications_submitted_at ON applications(submitted_at);
CREATE INDEX idx_applications_department ON applications(department);

CREATE TRIGGER update_applications_updated_at
    BEFORE UPDATE ON applications
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE applications IS 'Job applications linking applicants to job postings with pipeline tracking';

-- ============================================================
-- DOCUMENTS
-- ============================================================
CREATE TABLE documents (
    id BIGSERIAL PRIMARY KEY,
    applicant_id BIGINT NOT NULL,
    application_id BIGINT,
    type VARCHAR(20) NOT NULL,
    filename VARCHAR(255) NOT NULL,
    url VARCHAR(500) NOT NULL,
    file_size BIGINT,
    content_type VARCHAR(100),
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_documents_applicant FOREIGN KEY (applicant_id) REFERENCES applicants(id) ON DELETE CASCADE,
    CONSTRAINT fk_documents_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE SET NULL,
    CONSTRAINT chk_documents_type CHECK (type IN ('CV', 'SUPPORT'))
);

CREATE INDEX idx_documents_applicant_id ON documents(applicant_id);
CREATE INDEX idx_documents_application_id ON documents(application_id);
CREATE INDEX idx_documents_type ON documents(type);

COMMENT ON TABLE documents IS 'Applicant documents (CVs and supporting files) stored in S3';

-- ============================================================
-- AUDIT LOGS
-- ============================================================
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id VARCHAR(255) NOT NULL,
    action VARCHAR(255) NOT NULL,
    entity_type VARCHAR(255) NOT NULL,
    entity_id VARCHAR(255),
    details TEXT
);

CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_entity_type ON audit_logs(entity_type);
CREATE INDEX idx_audit_logs_entity_id ON audit_logs(entity_id);

COMMENT ON TABLE audit_logs IS 'Immutable audit trail for all significant system actions';
