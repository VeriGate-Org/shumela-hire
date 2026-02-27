-- V013: Compensation Management Module Tables
-- Creates pay_grades, salary_bands, compensation_reviews, total_rewards_statements, benefits

-- =====================================================
-- Pay Grades table
-- =====================================================
CREATE TABLE pay_grades (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    min_salary NUMERIC(15, 2) NOT NULL,
    mid_salary NUMERIC(15, 2) NOT NULL,
    max_salary NUMERIC(15, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'ZAR',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    -- Multi-tenancy
    tenant_id VARCHAR(50) NOT NULL,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT fk_pay_grade_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT uk_pay_grade_code_tenant UNIQUE (code, tenant_id),
    CONSTRAINT chk_pay_grade_salary_range CHECK (min_salary <= mid_salary AND mid_salary <= max_salary)
);

-- Indexes for pay_grades
CREATE INDEX idx_pay_grades_tenant_id ON pay_grades(tenant_id);
CREATE INDEX idx_pay_grades_code ON pay_grades(code);
CREATE INDEX idx_pay_grades_active ON pay_grades(is_active);

-- =====================================================
-- Salary Bands table
-- =====================================================
CREATE TABLE salary_bands (
    id BIGSERIAL PRIMARY KEY,
    pay_grade_id BIGINT NOT NULL,
    band_name VARCHAR(100) NOT NULL,
    job_family VARCHAR(100),
    job_level VARCHAR(50),
    min_salary NUMERIC(15, 2) NOT NULL,
    max_salary NUMERIC(15, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'ZAR',
    effective_date TIMESTAMP,
    expiry_date TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    -- Multi-tenancy
    tenant_id VARCHAR(50) NOT NULL,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT fk_salary_band_pay_grade FOREIGN KEY (pay_grade_id) REFERENCES pay_grades(id) ON DELETE CASCADE,
    CONSTRAINT fk_salary_band_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT chk_salary_band_range CHECK (min_salary <= max_salary)
);

-- Indexes for salary_bands
CREATE INDEX idx_salary_bands_tenant_id ON salary_bands(tenant_id);
CREATE INDEX idx_salary_bands_pay_grade ON salary_bands(pay_grade_id);
CREATE INDEX idx_salary_bands_job_family ON salary_bands(job_family);
CREATE INDEX idx_salary_bands_active ON salary_bands(is_active);

-- =====================================================
-- Compensation Reviews table
-- =====================================================
CREATE TABLE compensation_reviews (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    pay_grade_id BIGINT,
    review_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    current_salary NUMERIC(15, 2),
    proposed_salary NUMERIC(15, 2),
    approved_salary NUMERIC(15, 2),
    increase_percentage NUMERIC(6, 2),
    effective_date DATE,
    review_date DATE,
    justification TEXT,
    approver_notes TEXT,
    approved_by VARCHAR(255),
    approved_at TIMESTAMP,
    implemented_at TIMESTAMP,

    -- Multi-tenancy
    tenant_id VARCHAR(50) NOT NULL,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT fk_comp_review_employee FOREIGN KEY (employee_id) REFERENCES employees(id),
    CONSTRAINT fk_comp_review_pay_grade FOREIGN KEY (pay_grade_id) REFERENCES pay_grades(id),
    CONSTRAINT fk_comp_review_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id)
);

-- Indexes for compensation_reviews
CREATE INDEX idx_comp_reviews_tenant_id ON compensation_reviews(tenant_id);
CREATE INDEX idx_comp_reviews_employee ON compensation_reviews(employee_id);
CREATE INDEX idx_comp_reviews_status ON compensation_reviews(status);
CREATE INDEX idx_comp_reviews_review_type ON compensation_reviews(review_type);
CREATE INDEX idx_comp_reviews_effective_date ON compensation_reviews(effective_date);

-- =====================================================
-- Total Rewards Statements table
-- =====================================================
CREATE TABLE total_rewards_statements (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    statement_date DATE NOT NULL,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,

    -- Base compensation
    base_salary NUMERIC(15, 2),

    -- Variable pay
    bonus NUMERIC(15, 2),
    commission NUMERIC(15, 2),
    incentives NUMERIC(15, 2),

    -- Benefits (employer cost)
    medical_aid_contribution NUMERIC(15, 2),
    retirement_fund_contribution NUMERIC(15, 2),
    life_insurance_contribution NUMERIC(15, 2),
    other_benefits NUMERIC(15, 2),

    -- Allowances
    travel_allowance NUMERIC(15, 2),
    housing_allowance NUMERIC(15, 2),
    other_allowances NUMERIC(15, 2),

    -- Total
    total_remuneration NUMERIC(15, 2),
    currency VARCHAR(10) NOT NULL DEFAULT 'ZAR',
    notes TEXT,
    generated_by VARCHAR(255),

    -- Multi-tenancy
    tenant_id VARCHAR(50) NOT NULL,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT fk_total_rewards_employee FOREIGN KEY (employee_id) REFERENCES employees(id),
    CONSTRAINT fk_total_rewards_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id)
);

-- Indexes for total_rewards_statements
CREATE INDEX idx_total_rewards_tenant_id ON total_rewards_statements(tenant_id);
CREATE INDEX idx_total_rewards_employee ON total_rewards_statements(employee_id);
CREATE INDEX idx_total_rewards_statement_date ON total_rewards_statements(statement_date);
CREATE INDEX idx_total_rewards_period ON total_rewards_statements(period_start, period_end);

-- =====================================================
-- Benefits table
-- =====================================================
CREATE TABLE benefits (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    benefit_type VARCHAR(50) NOT NULL,
    benefit_name VARCHAR(200) NOT NULL,
    provider VARCHAR(200),
    policy_number VARCHAR(100),
    employee_contribution NUMERIC(15, 2),
    employer_contribution NUMERIC(15, 2),
    currency VARCHAR(10) NOT NULL DEFAULT 'ZAR',
    start_date DATE,
    end_date DATE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    notes TEXT,

    -- Multi-tenancy
    tenant_id VARCHAR(50) NOT NULL,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT fk_benefit_employee FOREIGN KEY (employee_id) REFERENCES employees(id),
    CONSTRAINT fk_benefit_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id)
);

-- Indexes for benefits
CREATE INDEX idx_benefits_tenant_id ON benefits(tenant_id);
CREATE INDEX idx_benefits_employee ON benefits(employee_id);
CREATE INDEX idx_benefits_type ON benefits(benefit_type);
CREATE INDEX idx_benefits_active ON benefits(is_active);
