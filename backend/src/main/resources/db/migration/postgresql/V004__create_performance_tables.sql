-- V004: Performance management tables for ShumelaHire
-- Tables: performance_templates, performance_cycles, performance_contracts,
--         performance_goals, goal_kpis, performance_reviews,
--         review_goal_scores, review_evidence

-- ============================================================
-- PERFORMANCE TEMPLATES
-- ============================================================
CREATE TABLE performance_templates (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    department VARCHAR(100),
    job_level VARCHAR(50),
    job_family VARCHAR(100),
    goal_template TEXT,
    kpi_template TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    is_default BOOLEAN DEFAULT FALSE,
    tenant_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(50) NOT NULL
);

CREATE INDEX idx_performance_templates_tenant ON performance_templates(tenant_id);
CREATE INDEX idx_performance_templates_active ON performance_templates(tenant_id, is_active);
CREATE INDEX idx_performance_templates_department ON performance_templates(department);

CREATE TRIGGER update_performance_templates_updated_at
    BEFORE UPDATE ON performance_templates
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE performance_templates IS 'Reusable templates for performance contracts with pre-defined goals and KPIs';

-- ============================================================
-- PERFORMANCE CYCLES
-- ============================================================
CREATE TABLE performance_cycles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    mid_year_deadline DATE NOT NULL,
    final_review_deadline DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PLANNING',
    tenant_id VARCHAR(50) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,

    CONSTRAINT chk_performance_cycles_status CHECK (status IN (
        'PLANNING', 'ACTIVE', 'MID_YEAR', 'FINAL_REVIEW', 'CLOSED'
    )),
    CONSTRAINT chk_performance_cycles_dates CHECK (start_date < end_date),
    CONSTRAINT chk_performance_cycles_mid_year CHECK (mid_year_deadline >= start_date AND mid_year_deadline <= end_date),
    CONSTRAINT chk_performance_cycles_final CHECK (final_review_deadline >= mid_year_deadline AND final_review_deadline <= end_date)
);

CREATE INDEX idx_performance_cycles_tenant ON performance_cycles(tenant_id);
CREATE INDEX idx_performance_cycles_status ON performance_cycles(status);
CREATE INDEX idx_performance_cycles_dates ON performance_cycles(start_date, end_date);

CREATE TRIGGER update_performance_cycles_updated_at
    BEFORE UPDATE ON performance_cycles
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE performance_cycles IS 'Annual or semi-annual performance review cycles';
COMMENT ON COLUMN performance_cycles.mid_year_deadline IS 'Deadline for mid-year reviews within this cycle';
COMMENT ON COLUMN performance_cycles.final_review_deadline IS 'Deadline for final reviews within this cycle';

-- ============================================================
-- PERFORMANCE CONTRACTS
-- ============================================================
CREATE TABLE performance_contracts (
    id BIGSERIAL PRIMARY KEY,
    cycle_id BIGINT NOT NULL,
    employee_id VARCHAR(50) NOT NULL,
    employee_name VARCHAR(100) NOT NULL,
    employee_number VARCHAR(20),
    manager_id VARCHAR(50) NOT NULL,
    manager_name VARCHAR(100) NOT NULL,
    department VARCHAR(100),
    job_title VARCHAR(100),
    job_level VARCHAR(50),
    template_id BIGINT,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    submitted_at TIMESTAMP,
    approved_at TIMESTAMP,
    approved_by VARCHAR(50),
    approval_comments TEXT,
    rejection_reason TEXT,
    version INTEGER DEFAULT 1,
    amendment_reason TEXT,
    amended_at TIMESTAMP,
    amended_by VARCHAR(50),
    tenant_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_performance_contracts_cycle FOREIGN KEY (cycle_id) REFERENCES performance_cycles(id) ON DELETE CASCADE,
    CONSTRAINT fk_performance_contracts_template FOREIGN KEY (template_id) REFERENCES performance_templates(id) ON DELETE SET NULL,
    CONSTRAINT chk_performance_contracts_status CHECK (status IN (
        'DRAFT', 'SUBMITTED', 'APPROVED', 'REJECTED', 'ACTIVE'
    ))
);

CREATE INDEX idx_performance_contracts_cycle ON performance_contracts(cycle_id);
CREATE INDEX idx_performance_contracts_employee ON performance_contracts(cycle_id, employee_id);
CREATE INDEX idx_performance_contracts_manager ON performance_contracts(manager_id);
CREATE INDEX idx_performance_contracts_tenant ON performance_contracts(tenant_id);
CREATE INDEX idx_performance_contracts_status ON performance_contracts(status);

CREATE TRIGGER update_performance_contracts_updated_at
    BEFORE UPDATE ON performance_contracts
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE performance_contracts IS 'Employee performance contracts within a review cycle';

-- ============================================================
-- PERFORMANCE GOALS
-- ============================================================
CREATE TABLE performance_goals (
    id BIGSERIAL PRIMARY KEY,
    contract_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    smart_criteria TEXT,
    goal_type VARCHAR(20) NOT NULL,
    weighting NUMERIC(5,2),
    target_value TEXT,
    measurement_criteria TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    sort_order INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_performance_goals_contract FOREIGN KEY (contract_id) REFERENCES performance_contracts(id) ON DELETE CASCADE,
    CONSTRAINT chk_performance_goals_type CHECK (goal_type IN (
        'STRATEGIC', 'OPERATIONAL', 'DEVELOPMENT', 'BEHAVIORAL'
    )),
    CONSTRAINT chk_performance_goals_weighting CHECK (weighting IS NULL OR (weighting >= 0.0 AND weighting <= 100.0))
);

CREATE INDEX idx_performance_goals_contract ON performance_goals(contract_id);
CREATE INDEX idx_performance_goals_type ON performance_goals(goal_type);

CREATE TRIGGER update_performance_goals_updated_at
    BEFORE UPDATE ON performance_goals
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE performance_goals IS 'Individual goals within a performance contract';
COMMENT ON COLUMN performance_goals.smart_criteria IS 'SMART criteria (Specific, Measurable, Achievable, Relevant, Time-bound)';
COMMENT ON COLUMN performance_goals.weighting IS 'Percentage weight of this goal in the overall performance score';

-- ============================================================
-- GOAL KPIs
-- ============================================================
CREATE TABLE goal_kpis (
    id BIGSERIAL PRIMARY KEY,
    goal_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    target_value TEXT,
    measurement_unit VARCHAR(50),
    weighting NUMERIC(5,2),
    kpi_type VARCHAR(20),
    sort_order INTEGER,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_goal_kpis_goal FOREIGN KEY (goal_id) REFERENCES performance_goals(id) ON DELETE CASCADE,
    CONSTRAINT chk_goal_kpis_type CHECK (kpi_type IS NULL OR kpi_type IN (
        'QUANTITATIVE', 'QUALITATIVE', 'BEHAVIORAL'
    )),
    CONSTRAINT chk_goal_kpis_weighting CHECK (weighting IS NULL OR (weighting >= 0.0 AND weighting <= 100.0))
);

CREATE INDEX idx_goal_kpis_goal ON goal_kpis(goal_id);

CREATE TRIGGER update_goal_kpis_updated_at
    BEFORE UPDATE ON goal_kpis
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE goal_kpis IS 'Key performance indicators measuring progress on individual goals';

-- ============================================================
-- PERFORMANCE REVIEWS
-- ============================================================
CREATE TABLE performance_reviews (
    id BIGSERIAL PRIMARY KEY,
    contract_id BIGINT NOT NULL,
    review_type VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    self_assessment_notes TEXT,
    self_rating NUMERIC(3,2),
    self_submitted_at TIMESTAMP,
    manager_assessment_notes TEXT,
    manager_rating NUMERIC(3,2),
    manager_submitted_at TIMESTAMP,
    final_rating NUMERIC(3,2),
    moderated_at TIMESTAMP,
    moderated_by VARCHAR(50),
    completed_at TIMESTAMP,
    review_period_start TIMESTAMP,
    review_period_end TIMESTAMP,
    due_date TIMESTAMP,
    tenant_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_performance_reviews_contract FOREIGN KEY (contract_id) REFERENCES performance_contracts(id) ON DELETE CASCADE,
    CONSTRAINT chk_performance_reviews_type CHECK (review_type IN ('MID_YEAR', 'FINAL')),
    CONSTRAINT chk_performance_reviews_status CHECK (status IN (
        'PENDING', 'EMPLOYEE_SUBMITTED', 'MANAGER_SUBMITTED', 'COMPLETED'
    )),
    CONSTRAINT chk_performance_reviews_self_rating CHECK (self_rating IS NULL OR (self_rating >= 0.0 AND self_rating <= 5.0)),
    CONSTRAINT chk_performance_reviews_manager_rating CHECK (manager_rating IS NULL OR (manager_rating >= 0.0 AND manager_rating <= 5.0)),
    CONSTRAINT chk_performance_reviews_final_rating CHECK (final_rating IS NULL OR (final_rating >= 0.0 AND final_rating <= 5.0))
);

CREATE INDEX idx_performance_reviews_contract ON performance_reviews(contract_id);
CREATE INDEX idx_performance_reviews_type ON performance_reviews(contract_id, review_type);
CREATE INDEX idx_performance_reviews_tenant ON performance_reviews(tenant_id);
CREATE INDEX idx_performance_reviews_status ON performance_reviews(status);

CREATE TRIGGER update_performance_reviews_updated_at
    BEFORE UPDATE ON performance_reviews
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE performance_reviews IS 'Mid-year and final performance reviews with self and manager assessments';

-- ============================================================
-- REVIEW GOAL SCORES
-- ============================================================
CREATE TABLE review_goal_scores (
    id BIGSERIAL PRIMARY KEY,
    review_id BIGINT NOT NULL,
    goal_id BIGINT NOT NULL,
    score NUMERIC(3,2),
    comment TEXT,

    CONSTRAINT fk_review_goal_scores_review FOREIGN KEY (review_id) REFERENCES performance_reviews(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_goal_scores_goal FOREIGN KEY (goal_id) REFERENCES performance_goals(id) ON DELETE CASCADE,
    CONSTRAINT chk_review_goal_scores_score CHECK (score IS NULL OR (score >= 0.0 AND score <= 5.0))
);

CREATE INDEX idx_review_goal_scores_review ON review_goal_scores(review_id);
CREATE INDEX idx_review_goal_scores_goal ON review_goal_scores(goal_id);
CREATE INDEX idx_review_goal_scores_review_goal ON review_goal_scores(review_id, goal_id);

COMMENT ON TABLE review_goal_scores IS 'Individual goal scores within a performance review';

-- ============================================================
-- REVIEW EVIDENCE
-- ============================================================
CREATE TABLE review_evidence (
    id BIGSERIAL PRIMARY KEY,
    review_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    content_type VARCHAR(100),
    description TEXT,
    evidence_type VARCHAR(20),
    uploaded_by VARCHAR(50) NOT NULL,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_review_evidence_review FOREIGN KEY (review_id) REFERENCES performance_reviews(id) ON DELETE CASCADE,
    CONSTRAINT chk_review_evidence_type CHECK (evidence_type IS NULL OR evidence_type IN (
        'DOCUMENT', 'PRESENTATION', 'REPORT', 'CERTIFICATE', 'FEEDBACK', 'OTHER'
    ))
);

CREATE INDEX idx_review_evidence_review ON review_evidence(review_id);

COMMENT ON TABLE review_evidence IS 'Supporting documents and evidence uploaded for performance reviews';
