-- V028: Performance Management Enhancement Tables (STORY-010)
-- Tables: key_result_areas, performance_improvement_plans, pip_milestones,
--         calibration_sessions, calibration_ratings
-- Also: adds kra_id column to performance_goals

-- ============================================================
-- KEY RESULT AREAS (KRA Framework)
-- ============================================================
CREATE TABLE key_result_areas (
    id BIGSERIAL PRIMARY KEY,
    contract_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    weighting NUMERIC(5,2),
    sort_order INTEGER,
    is_active BOOLEAN DEFAULT TRUE,
    tenant_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(50),

    CONSTRAINT fk_kra_contract FOREIGN KEY (contract_id) REFERENCES performance_contracts(id) ON DELETE CASCADE,
    CONSTRAINT chk_kra_weighting CHECK (weighting IS NULL OR (weighting >= 0.0 AND weighting <= 100.0))
);

CREATE INDEX idx_kra_contract ON key_result_areas(contract_id);
CREATE INDEX idx_kra_tenant ON key_result_areas(tenant_id);
CREATE INDEX idx_kra_active ON key_result_areas(contract_id, is_active);

CREATE TRIGGER update_kra_updated_at
    BEFORE UPDATE ON key_result_areas
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE key_result_areas IS 'Key Result Areas grouping related performance goals within a contract';
COMMENT ON COLUMN key_result_areas.weighting IS 'Percentage weight of this KRA in overall performance (0-100)';

-- Add KRA reference to performance_goals
ALTER TABLE performance_goals ADD COLUMN kra_id BIGINT;
ALTER TABLE performance_goals ADD CONSTRAINT fk_goals_kra FOREIGN KEY (kra_id) REFERENCES key_result_areas(id) ON DELETE SET NULL;
CREATE INDEX idx_performance_goals_kra ON performance_goals(kra_id);

-- ============================================================
-- PERFORMANCE IMPROVEMENT PLANS (PIP)
-- ============================================================
CREATE TABLE performance_improvement_plans (
    id BIGSERIAL PRIMARY KEY,
    contract_id BIGINT NOT NULL,
    employee_id VARCHAR(50) NOT NULL,
    employee_name VARCHAR(100) NOT NULL,
    manager_id VARCHAR(50) NOT NULL,
    manager_name VARCHAR(100) NOT NULL,
    reason TEXT NOT NULL,
    performance_gaps TEXT,
    expected_improvements TEXT,
    support_provided TEXT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    original_end_date DATE,
    extension_reason TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    outcome_notes TEXT,
    completed_at TIMESTAMP,
    completed_by VARCHAR(50),
    tenant_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(50) NOT NULL,

    CONSTRAINT fk_pip_contract FOREIGN KEY (contract_id) REFERENCES performance_contracts(id) ON DELETE CASCADE,
    CONSTRAINT chk_pip_status CHECK (status IN (
        'DRAFT', 'ACTIVE', 'EXTENDED', 'COMPLETED_SUCCESSFULLY', 'COMPLETED_UNSUCCESSFULLY', 'TERMINATED'
    )),
    CONSTRAINT chk_pip_dates CHECK (start_date <= end_date)
);

CREATE INDEX idx_pip_contract ON performance_improvement_plans(contract_id);
CREATE INDEX idx_pip_employee ON performance_improvement_plans(employee_id);
CREATE INDEX idx_pip_manager ON performance_improvement_plans(manager_id);
CREATE INDEX idx_pip_tenant ON performance_improvement_plans(tenant_id);
CREATE INDEX idx_pip_status ON performance_improvement_plans(status);
CREATE INDEX idx_pip_dates ON performance_improvement_plans(start_date, end_date);

CREATE TRIGGER update_pip_updated_at
    BEFORE UPDATE ON performance_improvement_plans
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE performance_improvement_plans IS 'Performance Improvement Plans for underperforming employees';
COMMENT ON COLUMN performance_improvement_plans.original_end_date IS 'Original end date before any extensions';

-- ============================================================
-- PIP MILESTONES
-- ============================================================
CREATE TABLE pip_milestones (
    id BIGSERIAL PRIMARY KEY,
    pip_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    success_criteria TEXT,
    target_date DATE NOT NULL,
    completed_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    manager_notes TEXT,
    employee_notes TEXT,
    sort_order INTEGER,
    tenant_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_pip_milestones_pip FOREIGN KEY (pip_id) REFERENCES performance_improvement_plans(id) ON DELETE CASCADE,
    CONSTRAINT chk_pip_milestone_status CHECK (status IN (
        'PENDING', 'IN_PROGRESS', 'COMPLETED', 'MISSED'
    ))
);

CREATE INDEX idx_pip_milestones_pip ON pip_milestones(pip_id);
CREATE INDEX idx_pip_milestones_status ON pip_milestones(status);
CREATE INDEX idx_pip_milestones_target ON pip_milestones(target_date);

CREATE TRIGGER update_pip_milestones_updated_at
    BEFORE UPDATE ON pip_milestones
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE pip_milestones IS 'Milestones and checkpoints within a Performance Improvement Plan';

-- ============================================================
-- CALIBRATION SESSIONS
-- ============================================================
CREATE TABLE calibration_sessions (
    id BIGSERIAL PRIMARY KEY,
    cycle_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    department VARCHAR(100),
    job_level VARCHAR(50),
    facilitator_id VARCHAR(50) NOT NULL,
    facilitator_name VARCHAR(100) NOT NULL,
    scheduled_date TIMESTAMP,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    notes TEXT,
    distribution_target TEXT,
    tenant_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(50) NOT NULL,

    CONSTRAINT fk_calibration_cycle FOREIGN KEY (cycle_id) REFERENCES performance_cycles(id) ON DELETE CASCADE,
    CONSTRAINT chk_calibration_status CHECK (status IN (
        'SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'
    ))
);

CREATE INDEX idx_calibration_sessions_cycle ON calibration_sessions(cycle_id);
CREATE INDEX idx_calibration_sessions_department ON calibration_sessions(department);
CREATE INDEX idx_calibration_sessions_status ON calibration_sessions(status);
CREATE INDEX idx_calibration_sessions_tenant ON calibration_sessions(tenant_id);
CREATE INDEX idx_calibration_sessions_date ON calibration_sessions(scheduled_date);

CREATE TRIGGER update_calibration_sessions_updated_at
    BEFORE UPDATE ON calibration_sessions
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE calibration_sessions IS 'Calibration/moderation sessions for normalizing performance ratings across departments';
COMMENT ON COLUMN calibration_sessions.distribution_target IS 'JSON target distribution (e.g., bell curve percentages)';

-- ============================================================
-- CALIBRATION RATINGS
-- ============================================================
CREATE TABLE calibration_ratings (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL,
    review_id BIGINT NOT NULL,
    employee_id VARCHAR(50) NOT NULL,
    employee_name VARCHAR(100),
    original_rating NUMERIC(3,2),
    calibrated_rating NUMERIC(3,2),
    adjustment_reason TEXT,
    calibrated_by VARCHAR(50),
    calibrated_at TIMESTAMP,
    tenant_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_calibration_ratings_session FOREIGN KEY (session_id) REFERENCES calibration_sessions(id) ON DELETE CASCADE,
    CONSTRAINT fk_calibration_ratings_review FOREIGN KEY (review_id) REFERENCES performance_reviews(id) ON DELETE CASCADE,
    CONSTRAINT chk_calibration_original_rating CHECK (original_rating IS NULL OR (original_rating >= 0.0 AND original_rating <= 5.0)),
    CONSTRAINT chk_calibration_calibrated_rating CHECK (calibrated_rating IS NULL OR (calibrated_rating >= 0.0 AND calibrated_rating <= 5.0)),
    CONSTRAINT uk_calibration_session_review UNIQUE (session_id, review_id)
);

CREATE INDEX idx_calibration_ratings_session ON calibration_ratings(session_id);
CREATE INDEX idx_calibration_ratings_review ON calibration_ratings(review_id);
CREATE INDEX idx_calibration_ratings_employee ON calibration_ratings(employee_id);

COMMENT ON TABLE calibration_ratings IS 'Individual rating adjustments within a calibration session';
