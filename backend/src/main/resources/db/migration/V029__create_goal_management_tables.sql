-- V029: Goal Management Tables (STORY-014)
-- Tables: goals, key_results, goal_links
-- Supports OKR/KPI hierarchy, progress tracking, and performance cycle linkage

-- ============================================================
-- GOALS (OKR/KPI hierarchy with self-referencing parent)
-- ============================================================
CREATE TABLE goals (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    type VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    owner_type VARCHAR(20) NOT NULL,
    owner_id VARCHAR(50) NOT NULL,
    period VARCHAR(30) NOT NULL,
    start_date DATE,
    end_date DATE,
    parent_goal_id BIGINT,
    sort_order INTEGER,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    tenant_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(50),

    CONSTRAINT fk_goals_parent FOREIGN KEY (parent_goal_id) REFERENCES goals(id) ON DELETE SET NULL,
    CONSTRAINT chk_goals_type CHECK (type IN ('OKR', 'KPI')),
    CONSTRAINT chk_goals_status CHECK (status IN ('DRAFT', 'ACTIVE', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT chk_goals_owner_type CHECK (owner_type IN ('ORGANIZATION', 'DEPARTMENT', 'EMPLOYEE')),
    CONSTRAINT chk_goals_period CHECK (period IN ('QUARTERLY', 'SEMI_ANNUAL', 'ANNUAL', 'CUSTOM')),
    CONSTRAINT chk_goals_dates CHECK (start_date IS NULL OR end_date IS NULL OR start_date <= end_date)
);

CREATE INDEX idx_goals_tenant ON goals(tenant_id);
CREATE INDEX idx_goals_tenant_active ON goals(tenant_id, is_active);
CREATE INDEX idx_goals_owner ON goals(owner_type, owner_id);
CREATE INDEX idx_goals_parent ON goals(parent_goal_id);
CREATE INDEX idx_goals_status ON goals(status);
CREATE INDEX idx_goals_type ON goals(type);

CREATE TRIGGER update_goals_updated_at
    BEFORE UPDATE ON goals
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE goals IS 'Goal Management: OKR and KPI goals with hierarchical parent-child relationships';
COMMENT ON COLUMN goals.type IS 'Goal type: OKR (Objective and Key Result) or KPI (Key Performance Indicator)';
COMMENT ON COLUMN goals.owner_type IS 'Owner scope: ORGANIZATION, DEPARTMENT, or EMPLOYEE';
COMMENT ON COLUMN goals.period IS 'Goal period: QUARTERLY, SEMI_ANNUAL, ANNUAL, or CUSTOM';
COMMENT ON COLUMN goals.parent_goal_id IS 'Self-referencing FK for OKR hierarchy; NULL for top-level goals';

-- ============================================================
-- KEY RESULTS (metrics tied to OKR/KPI goals)
-- ============================================================
CREATE TABLE key_results (
    id BIGSERIAL PRIMARY KEY,
    goal_id BIGINT NOT NULL,
    metric VARCHAR(200) NOT NULL,
    description TEXT,
    target_value NUMERIC(15,2) NOT NULL,
    current_value NUMERIC(15,2) NOT NULL DEFAULT 0,
    unit_of_measure VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'ON_TRACK',
    last_updated DATE,
    sort_order INTEGER,
    tenant_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_key_results_goal FOREIGN KEY (goal_id) REFERENCES goals(id) ON DELETE CASCADE,
    CONSTRAINT chk_kr_target_value CHECK (target_value > 0),
    CONSTRAINT chk_kr_current_value CHECK (current_value >= 0),
    CONSTRAINT chk_kr_status CHECK (status IN ('ON_TRACK', 'AT_RISK', 'OFF_TRACK', 'COMPLETED'))
);

CREATE INDEX idx_key_results_goal ON key_results(goal_id);
CREATE INDEX idx_key_results_tenant ON key_results(tenant_id);
CREATE INDEX idx_key_results_status ON key_results(status);

CREATE TRIGGER update_key_results_updated_at
    BEFORE UPDATE ON key_results
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE key_results IS 'Key Results measuring progress toward a goal; progress_pct calculated application-side';
COMMENT ON COLUMN key_results.metric IS 'Name or description of the metric being measured';
COMMENT ON COLUMN key_results.target_value IS 'The target numeric value to achieve';
COMMENT ON COLUMN key_results.current_value IS 'Current progress value; progress% = current/target*100';

-- ============================================================
-- GOAL LINKS (goal <-> performance cycle association)
-- ============================================================
CREATE TABLE goal_links (
    id BIGSERIAL PRIMARY KEY,
    goal_id BIGINT NOT NULL,
    review_cycle_id BIGINT NOT NULL,
    weight NUMERIC(5,2) NOT NULL DEFAULT 0,
    tenant_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(50),

    CONSTRAINT fk_goal_links_goal FOREIGN KEY (goal_id) REFERENCES goals(id) ON DELETE CASCADE,
    CONSTRAINT fk_goal_links_cycle FOREIGN KEY (review_cycle_id) REFERENCES performance_cycles(id) ON DELETE CASCADE,
    CONSTRAINT uk_goal_links_goal_cycle UNIQUE (goal_id, review_cycle_id),
    CONSTRAINT chk_goal_links_weight CHECK (weight >= 0.0 AND weight <= 100.0)
);

CREATE INDEX idx_goal_links_goal ON goal_links(goal_id);
CREATE INDEX idx_goal_links_cycle ON goal_links(review_cycle_id);
CREATE INDEX idx_goal_links_tenant ON goal_links(tenant_id);

CREATE TRIGGER update_goal_links_updated_at
    BEFORE UPDATE ON goal_links
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE goal_links IS 'Links goals to performance review cycles with a weighted contribution';
COMMENT ON COLUMN goal_links.weight IS 'Percentage weight of this goal in the linked performance cycle (0-100)';
