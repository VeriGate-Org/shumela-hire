-- V004: Performance management tables for ShumelaHire (SQL Server)

CREATE TABLE performance_templates (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    description NVARCHAR(MAX),
    department NVARCHAR(100),
    job_level NVARCHAR(50),
    job_family NVARCHAR(100),
    goal_template NVARCHAR(MAX),
    kpi_template NVARCHAR(MAX),
    is_active BIT DEFAULT 1,
    is_default BIT DEFAULT 0,
    tenant_id NVARCHAR(50) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2,
    created_by NVARCHAR(50) NOT NULL
);

CREATE INDEX idx_performance_templates_tenant ON performance_templates(tenant_id);
CREATE INDEX idx_performance_templates_active ON performance_templates(tenant_id, is_active);
CREATE INDEX idx_performance_templates_department ON performance_templates(department);
GO

CREATE TRIGGER trg_performance_templates_updated_at ON performance_templates
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE performance_templates SET updated_at = GETDATE()
    FROM performance_templates INNER JOIN inserted ON performance_templates.id = inserted.id;
END;
GO

CREATE TABLE performance_cycles (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    description NVARCHAR(MAX),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    mid_year_deadline DATE NOT NULL,
    final_review_deadline DATE NOT NULL,
    status NVARCHAR(20) NOT NULL DEFAULT 'PLANNING',
    tenant_id NVARCHAR(50) NOT NULL,
    is_default BIT DEFAULT 0,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2,
    created_by NVARCHAR(255) NOT NULL,

    CONSTRAINT chk_performance_cycles_status CHECK (status IN ('PLANNING', 'ACTIVE', 'MID_YEAR', 'FINAL_REVIEW', 'CLOSED')),
    CONSTRAINT chk_performance_cycles_dates CHECK (start_date < end_date),
    CONSTRAINT chk_performance_cycles_mid_year CHECK (mid_year_deadline >= start_date AND mid_year_deadline <= end_date),
    CONSTRAINT chk_performance_cycles_final CHECK (final_review_deadline >= mid_year_deadline AND final_review_deadline <= end_date)
);

CREATE INDEX idx_performance_cycles_tenant ON performance_cycles(tenant_id);
CREATE INDEX idx_performance_cycles_status ON performance_cycles(status);
CREATE INDEX idx_performance_cycles_dates ON performance_cycles(start_date, end_date);
GO

CREATE TRIGGER trg_performance_cycles_updated_at ON performance_cycles
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE performance_cycles SET updated_at = GETDATE()
    FROM performance_cycles INNER JOIN inserted ON performance_cycles.id = inserted.id;
END;
GO

CREATE TABLE performance_contracts (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    cycle_id BIGINT NOT NULL,
    employee_id NVARCHAR(50) NOT NULL,
    employee_name NVARCHAR(100) NOT NULL,
    employee_number NVARCHAR(20),
    manager_id NVARCHAR(50) NOT NULL,
    manager_name NVARCHAR(100) NOT NULL,
    department NVARCHAR(100),
    job_title NVARCHAR(100),
    job_level NVARCHAR(50),
    template_id BIGINT,
    status NVARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    submitted_at DATETIME2,
    approved_at DATETIME2,
    approved_by NVARCHAR(50),
    approval_comments NVARCHAR(MAX),
    rejection_reason NVARCHAR(MAX),
    version INTEGER DEFAULT 1,
    amendment_reason NVARCHAR(MAX),
    amended_at DATETIME2,
    amended_by NVARCHAR(50),
    tenant_id NVARCHAR(50) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2,

    CONSTRAINT fk_performance_contracts_cycle FOREIGN KEY (cycle_id) REFERENCES performance_cycles(id) ON DELETE CASCADE,
    CONSTRAINT fk_performance_contracts_template FOREIGN KEY (template_id) REFERENCES performance_templates(id) ON DELETE SET NULL,
    CONSTRAINT chk_performance_contracts_status CHECK (status IN ('DRAFT', 'SUBMITTED', 'APPROVED', 'REJECTED', 'ACTIVE'))
);

CREATE INDEX idx_performance_contracts_cycle ON performance_contracts(cycle_id);
CREATE INDEX idx_performance_contracts_employee ON performance_contracts(cycle_id, employee_id);
CREATE INDEX idx_performance_contracts_manager ON performance_contracts(manager_id);
CREATE INDEX idx_performance_contracts_tenant ON performance_contracts(tenant_id);
CREATE INDEX idx_performance_contracts_status ON performance_contracts(status);
GO

CREATE TRIGGER trg_performance_contracts_updated_at ON performance_contracts
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE performance_contracts SET updated_at = GETDATE()
    FROM performance_contracts INNER JOIN inserted ON performance_contracts.id = inserted.id;
END;
GO

CREATE TABLE performance_goals (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    contract_id BIGINT NOT NULL,
    title NVARCHAR(200) NOT NULL,
    description NVARCHAR(MAX),
    smart_criteria NVARCHAR(MAX),
    goal_type NVARCHAR(20) NOT NULL,
    weighting DECIMAL(5,2),
    target_value NVARCHAR(MAX),
    measurement_criteria NVARCHAR(MAX),
    is_active BIT DEFAULT 1,
    sort_order INTEGER,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2,

    CONSTRAINT fk_performance_goals_contract FOREIGN KEY (contract_id) REFERENCES performance_contracts(id) ON DELETE CASCADE,
    CONSTRAINT chk_performance_goals_type CHECK (goal_type IN ('STRATEGIC', 'OPERATIONAL', 'DEVELOPMENT', 'BEHAVIORAL')),
    CONSTRAINT chk_performance_goals_weighting CHECK (weighting IS NULL OR (weighting >= 0.0 AND weighting <= 100.0))
);

CREATE INDEX idx_performance_goals_contract ON performance_goals(contract_id);
CREATE INDEX idx_performance_goals_type ON performance_goals(goal_type);
GO

CREATE TRIGGER trg_performance_goals_updated_at ON performance_goals
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE performance_goals SET updated_at = GETDATE()
    FROM performance_goals INNER JOIN inserted ON performance_goals.id = inserted.id;
END;
GO

CREATE TABLE goal_kpis (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    goal_id BIGINT NOT NULL,
    name NVARCHAR(200) NOT NULL,
    description NVARCHAR(MAX),
    target_value NVARCHAR(MAX),
    measurement_unit NVARCHAR(50),
    weighting DECIMAL(5,2),
    kpi_type NVARCHAR(20),
    sort_order INTEGER,
    is_active BIT DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2,

    CONSTRAINT fk_goal_kpis_goal FOREIGN KEY (goal_id) REFERENCES performance_goals(id) ON DELETE CASCADE,
    CONSTRAINT chk_goal_kpis_type CHECK (kpi_type IS NULL OR kpi_type IN ('QUANTITATIVE', 'QUALITATIVE', 'BEHAVIORAL')),
    CONSTRAINT chk_goal_kpis_weighting CHECK (weighting IS NULL OR (weighting >= 0.0 AND weighting <= 100.0))
);

CREATE INDEX idx_goal_kpis_goal ON goal_kpis(goal_id);
GO

CREATE TRIGGER trg_goal_kpis_updated_at ON goal_kpis
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE goal_kpis SET updated_at = GETDATE()
    FROM goal_kpis INNER JOIN inserted ON goal_kpis.id = inserted.id;
END;
GO

CREATE TABLE performance_reviews (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    contract_id BIGINT NOT NULL,
    review_type NVARCHAR(20) NOT NULL,
    status NVARCHAR(30) NOT NULL DEFAULT 'PENDING',
    self_assessment_notes NVARCHAR(MAX),
    self_rating DECIMAL(3,2),
    self_submitted_at DATETIME2,
    manager_assessment_notes NVARCHAR(MAX),
    manager_rating DECIMAL(3,2),
    manager_submitted_at DATETIME2,
    final_rating DECIMAL(3,2),
    moderated_at DATETIME2,
    moderated_by NVARCHAR(50),
    completed_at DATETIME2,
    review_period_start DATETIME2,
    review_period_end DATETIME2,
    due_date DATETIME2,
    tenant_id NVARCHAR(50) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2,

    CONSTRAINT fk_performance_reviews_contract FOREIGN KEY (contract_id) REFERENCES performance_contracts(id) ON DELETE CASCADE,
    CONSTRAINT chk_performance_reviews_type CHECK (review_type IN ('MID_YEAR', 'FINAL')),
    CONSTRAINT chk_performance_reviews_status CHECK (status IN ('PENDING', 'EMPLOYEE_SUBMITTED', 'MANAGER_SUBMITTED', 'COMPLETED')),
    CONSTRAINT chk_performance_reviews_self_rating CHECK (self_rating IS NULL OR (self_rating >= 0.0 AND self_rating <= 5.0)),
    CONSTRAINT chk_performance_reviews_manager_rating CHECK (manager_rating IS NULL OR (manager_rating >= 0.0 AND manager_rating <= 5.0)),
    CONSTRAINT chk_performance_reviews_final_rating CHECK (final_rating IS NULL OR (final_rating >= 0.0 AND final_rating <= 5.0))
);

CREATE INDEX idx_performance_reviews_contract ON performance_reviews(contract_id);
CREATE INDEX idx_performance_reviews_type ON performance_reviews(contract_id, review_type);
CREATE INDEX idx_performance_reviews_tenant ON performance_reviews(tenant_id);
CREATE INDEX idx_performance_reviews_status ON performance_reviews(status);
GO

CREATE TRIGGER trg_performance_reviews_updated_at ON performance_reviews
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE performance_reviews SET updated_at = GETDATE()
    FROM performance_reviews INNER JOIN inserted ON performance_reviews.id = inserted.id;
END;
GO

CREATE TABLE review_goal_scores (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    review_id BIGINT NOT NULL,
    goal_id BIGINT NOT NULL,
    score DECIMAL(3,2),
    comment NVARCHAR(MAX),

    CONSTRAINT fk_review_goal_scores_review FOREIGN KEY (review_id) REFERENCES performance_reviews(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_goal_scores_goal FOREIGN KEY (goal_id) REFERENCES performance_goals(id),
    CONSTRAINT chk_review_goal_scores_score CHECK (score IS NULL OR (score >= 0.0 AND score <= 5.0))
);

CREATE INDEX idx_review_goal_scores_review ON review_goal_scores(review_id);
CREATE INDEX idx_review_goal_scores_goal ON review_goal_scores(goal_id);
CREATE INDEX idx_review_goal_scores_review_goal ON review_goal_scores(review_id, goal_id);

CREATE TABLE review_evidence (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    review_id BIGINT NOT NULL,
    file_name NVARCHAR(255) NOT NULL,
    file_path NVARCHAR(500) NOT NULL,
    file_size BIGINT,
    content_type NVARCHAR(100),
    description NVARCHAR(MAX),
    evidence_type NVARCHAR(20),
    uploaded_by NVARCHAR(50) NOT NULL,
    uploaded_at DATETIME2 NOT NULL DEFAULT GETDATE(),

    CONSTRAINT fk_review_evidence_review FOREIGN KEY (review_id) REFERENCES performance_reviews(id) ON DELETE CASCADE,
    CONSTRAINT chk_review_evidence_type CHECK (evidence_type IS NULL OR evidence_type IN ('DOCUMENT', 'PRESENTATION', 'REPORT', 'CERTIFICATE', 'FEEDBACK', 'OTHER'))
);

CREATE INDEX idx_review_evidence_review ON review_evidence(review_id);
