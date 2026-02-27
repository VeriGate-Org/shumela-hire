-- V014: Leave Management tables for ShumelaHire
-- Tables: leave_types, leave_policies, leave_requests, leave_balances,
--         leave_encashments, leave_accruals, public_holidays, leave_delegations

-- ============================================================
-- LEAVE TYPES
-- ============================================================
CREATE TABLE leave_types (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(30) NOT NULL,
    description TEXT,
    default_days_per_year NUMERIC(5,1) NOT NULL DEFAULT 0,
    max_carry_over_days NUMERIC(5,1) DEFAULT 0,
    carry_over_allowed BOOLEAN NOT NULL DEFAULT FALSE,
    carry_over_expiry_months INTEGER,
    requires_approval BOOLEAN NOT NULL DEFAULT TRUE,
    requires_documentation BOOLEAN NOT NULL DEFAULT FALSE,
    min_days_notice INTEGER DEFAULT 0,
    max_consecutive_days INTEGER,
    is_paid BOOLEAN NOT NULL DEFAULT TRUE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    gender_restriction VARCHAR(20),
    applies_to_employment_types TEXT,
    color_code VARCHAR(7),
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_leave_types_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT uq_leave_types_tenant_code UNIQUE (tenant_id, code),
    CONSTRAINT chk_leave_types_gender CHECK (gender_restriction IS NULL OR gender_restriction IN ('MALE', 'FEMALE', 'ALL'))
);

CREATE INDEX idx_leave_types_tenant ON leave_types(tenant_id);
CREATE INDEX idx_leave_types_active ON leave_types(tenant_id, is_active);

CREATE TRIGGER update_leave_types_updated_at
    BEFORE UPDATE ON leave_types
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE leave_types IS 'Configurable leave types per tenant (Annual, Sick, Maternity, etc.)';
COMMENT ON COLUMN leave_types.carry_over_expiry_months IS 'Months after year-end before carried-over days expire';

-- ============================================================
-- LEAVE POLICIES
-- ============================================================
CREATE TABLE leave_policies (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    leave_type_id BIGINT NOT NULL,
    employment_type VARCHAR(50),
    department VARCHAR(200),
    job_grade VARCHAR(50),
    min_service_months INTEGER DEFAULT 0,
    annual_entitlement NUMERIC(5,1) NOT NULL,
    accrual_frequency VARCHAR(30) NOT NULL DEFAULT 'MONTHLY',
    pro_rata_on_join BOOLEAN NOT NULL DEFAULT TRUE,
    pro_rata_on_leave BOOLEAN NOT NULL DEFAULT TRUE,
    max_negative_balance NUMERIC(5,1) DEFAULT 0,
    require_manager_approval BOOLEAN NOT NULL DEFAULT TRUE,
    require_hr_approval BOOLEAN NOT NULL DEFAULT FALSE,
    auto_approve_days_threshold INTEGER,
    effective_from DATE NOT NULL,
    effective_to DATE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_leave_policies_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_leave_policies_leave_type FOREIGN KEY (leave_type_id) REFERENCES leave_types(id) ON DELETE CASCADE,
    CONSTRAINT chk_leave_policies_accrual CHECK (accrual_frequency IN (
        'MONTHLY', 'QUARTERLY', 'SEMI_ANNUALLY', 'ANNUALLY', 'ON_HIRE_DATE'
    ))
);

CREATE INDEX idx_leave_policies_tenant ON leave_policies(tenant_id);
CREATE INDEX idx_leave_policies_leave_type ON leave_policies(leave_type_id);
CREATE INDEX idx_leave_policies_active ON leave_policies(tenant_id, is_active);

CREATE TRIGGER update_leave_policies_updated_at
    BEFORE UPDATE ON leave_policies
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE leave_policies IS 'Leave entitlement policies per leave type, department, and employment type';

-- ============================================================
-- LEAVE REQUESTS
-- ============================================================
CREATE TABLE leave_requests (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    employee_id BIGINT NOT NULL,
    leave_type_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    number_of_days NUMERIC(5,1) NOT NULL,
    is_half_day BOOLEAN NOT NULL DEFAULT FALSE,
    half_day_period VARCHAR(10),
    reason TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_by BIGINT,
    approved_at TIMESTAMP,
    approval_notes TEXT,
    rejected_by BIGINT,
    rejected_at TIMESTAMP,
    rejection_reason TEXT,
    cancelled_at TIMESTAMP,
    cancellation_reason TEXT,
    hr_approved_by BIGINT,
    hr_approved_at TIMESTAMP,
    delegate_id BIGINT,
    attachment_url VARCHAR(500),
    attachment_filename VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_leave_requests_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_leave_requests_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    CONSTRAINT fk_leave_requests_leave_type FOREIGN KEY (leave_type_id) REFERENCES leave_types(id),
    CONSTRAINT fk_leave_requests_approved_by FOREIGN KEY (approved_by) REFERENCES employees(id),
    CONSTRAINT fk_leave_requests_delegate FOREIGN KEY (delegate_id) REFERENCES employees(id),
    CONSTRAINT chk_leave_requests_status CHECK (status IN (
        'PENDING', 'MANAGER_APPROVED', 'HR_APPROVED', 'APPROVED',
        'REJECTED', 'CANCELLED', 'RECALLED'
    )),
    CONSTRAINT chk_leave_requests_half_day CHECK (half_day_period IS NULL OR half_day_period IN ('MORNING', 'AFTERNOON')),
    CONSTRAINT chk_leave_requests_dates CHECK (end_date >= start_date),
    CONSTRAINT chk_leave_requests_days CHECK (number_of_days > 0)
);

CREATE INDEX idx_leave_requests_tenant ON leave_requests(tenant_id);
CREATE INDEX idx_leave_requests_employee ON leave_requests(employee_id);
CREATE INDEX idx_leave_requests_leave_type ON leave_requests(leave_type_id);
CREATE INDEX idx_leave_requests_status ON leave_requests(status);
CREATE INDEX idx_leave_requests_start_date ON leave_requests(start_date);
CREATE INDEX idx_leave_requests_end_date ON leave_requests(end_date);
CREATE INDEX idx_leave_requests_approved_by ON leave_requests(approved_by);
CREATE INDEX idx_leave_requests_date_range ON leave_requests(employee_id, start_date, end_date);

CREATE TRIGGER update_leave_requests_updated_at
    BEFORE UPDATE ON leave_requests
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE leave_requests IS 'Employee leave requests with multi-level approval workflow';

-- ============================================================
-- LEAVE BALANCES
-- ============================================================
CREATE TABLE leave_balances (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    employee_id BIGINT NOT NULL,
    leave_type_id BIGINT NOT NULL,
    leave_year INTEGER NOT NULL,
    opening_balance NUMERIC(6,1) NOT NULL DEFAULT 0,
    accrued NUMERIC(6,1) NOT NULL DEFAULT 0,
    used NUMERIC(6,1) NOT NULL DEFAULT 0,
    pending NUMERIC(6,1) NOT NULL DEFAULT 0,
    carried_over NUMERIC(6,1) NOT NULL DEFAULT 0,
    adjustment NUMERIC(6,1) NOT NULL DEFAULT 0,
    adjustment_reason TEXT,
    encashed NUMERIC(6,1) NOT NULL DEFAULT 0,
    forfeited NUMERIC(6,1) NOT NULL DEFAULT 0,
    closing_balance NUMERIC(6,1) GENERATED ALWAYS AS (opening_balance + accrued + carried_over + adjustment - used - encashed - forfeited) STORED,
    available_balance NUMERIC(6,1) GENERATED ALWAYS AS (opening_balance + accrued + carried_over + adjustment - used - pending - encashed - forfeited) STORED,
    last_accrual_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_leave_balances_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_leave_balances_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    CONSTRAINT fk_leave_balances_leave_type FOREIGN KEY (leave_type_id) REFERENCES leave_types(id),
    CONSTRAINT uq_leave_balances_employee_type_year UNIQUE (tenant_id, employee_id, leave_type_id, leave_year)
);

CREATE INDEX idx_leave_balances_tenant ON leave_balances(tenant_id);
CREATE INDEX idx_leave_balances_employee ON leave_balances(employee_id);
CREATE INDEX idx_leave_balances_leave_type ON leave_balances(leave_type_id);
CREATE INDEX idx_leave_balances_year ON leave_balances(leave_year);
CREATE INDEX idx_leave_balances_employee_year ON leave_balances(employee_id, leave_year);

CREATE TRIGGER update_leave_balances_updated_at
    BEFORE UPDATE ON leave_balances
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE leave_balances IS 'Employee leave balances per type per year with computed closing/available balances';

-- ============================================================
-- LEAVE ENCASHMENTS
-- ============================================================
CREATE TABLE leave_encashments (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    employee_id BIGINT NOT NULL,
    leave_type_id BIGINT NOT NULL,
    leave_balance_id BIGINT NOT NULL,
    days_encashed NUMERIC(5,1) NOT NULL,
    daily_rate NUMERIC(12,2) NOT NULL,
    total_amount NUMERIC(12,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'ZAR',
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_by BIGINT,
    approved_at TIMESTAMP,
    rejection_reason TEXT,
    paid_at TIMESTAMP,
    payroll_reference VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_leave_encashments_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_leave_encashments_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    CONSTRAINT fk_leave_encashments_leave_type FOREIGN KEY (leave_type_id) REFERENCES leave_types(id),
    CONSTRAINT fk_leave_encashments_balance FOREIGN KEY (leave_balance_id) REFERENCES leave_balances(id),
    CONSTRAINT chk_leave_encashments_status CHECK (status IN (
        'PENDING', 'APPROVED', 'REJECTED', 'PAID', 'CANCELLED'
    )),
    CONSTRAINT chk_leave_encashments_days CHECK (days_encashed > 0),
    CONSTRAINT chk_leave_encashments_amount CHECK (total_amount >= 0)
);

CREATE INDEX idx_leave_encashments_tenant ON leave_encashments(tenant_id);
CREATE INDEX idx_leave_encashments_employee ON leave_encashments(employee_id);
CREATE INDEX idx_leave_encashments_status ON leave_encashments(status);

CREATE TRIGGER update_leave_encashments_updated_at
    BEFORE UPDATE ON leave_encashments
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE leave_encashments IS 'Leave encashment requests converting unused leave to monetary compensation';

-- ============================================================
-- LEAVE ACCRUALS
-- ============================================================
CREATE TABLE leave_accruals (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    employee_id BIGINT NOT NULL,
    leave_type_id BIGINT NOT NULL,
    leave_balance_id BIGINT NOT NULL,
    accrual_date DATE NOT NULL,
    days_accrued NUMERIC(5,2) NOT NULL,
    accrual_period_start DATE NOT NULL,
    accrual_period_end DATE NOT NULL,
    is_pro_rated BOOLEAN NOT NULL DEFAULT FALSE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_leave_accruals_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_leave_accruals_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    CONSTRAINT fk_leave_accruals_leave_type FOREIGN KEY (leave_type_id) REFERENCES leave_types(id),
    CONSTRAINT fk_leave_accruals_balance FOREIGN KEY (leave_balance_id) REFERENCES leave_balances(id)
);

CREATE INDEX idx_leave_accruals_tenant ON leave_accruals(tenant_id);
CREATE INDEX idx_leave_accruals_employee ON leave_accruals(employee_id);
CREATE INDEX idx_leave_accruals_date ON leave_accruals(accrual_date);
CREATE INDEX idx_leave_accruals_balance ON leave_accruals(leave_balance_id);

COMMENT ON TABLE leave_accruals IS 'Immutable accrual transaction log for leave balances';

-- ============================================================
-- PUBLIC HOLIDAYS
-- ============================================================
CREATE TABLE public_holidays (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    name VARCHAR(200) NOT NULL,
    holiday_date DATE NOT NULL,
    description TEXT,
    country VARCHAR(3) NOT NULL DEFAULT 'ZA',
    region VARCHAR(100),
    is_recurring BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_public_holidays_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT uq_public_holidays_tenant_date UNIQUE (tenant_id, holiday_date, country)
);

CREATE INDEX idx_public_holidays_tenant ON public_holidays(tenant_id);
CREATE INDEX idx_public_holidays_date ON public_holidays(holiday_date);
CREATE INDEX idx_public_holidays_country ON public_holidays(country);
CREATE INDEX idx_public_holidays_active ON public_holidays(tenant_id, is_active);

CREATE TRIGGER update_public_holidays_updated_at
    BEFORE UPDATE ON public_holidays
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE public_holidays IS 'Public holidays per country/region for leave day calculations';

-- ============================================================
-- LEAVE DELEGATIONS
-- ============================================================
CREATE TABLE leave_delegations (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    delegator_id BIGINT NOT NULL,
    delegate_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason TEXT,
    can_approve_leave BOOLEAN NOT NULL DEFAULT TRUE,
    can_approve_encashment BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_leave_delegations_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_leave_delegations_delegator FOREIGN KEY (delegator_id) REFERENCES employees(id) ON DELETE CASCADE,
    CONSTRAINT fk_leave_delegations_delegate FOREIGN KEY (delegate_id) REFERENCES employees(id) ON DELETE CASCADE,
    CONSTRAINT chk_leave_delegations_dates CHECK (end_date >= start_date),
    CONSTRAINT chk_leave_delegations_different CHECK (delegator_id <> delegate_id)
);

CREATE INDEX idx_leave_delegations_tenant ON leave_delegations(tenant_id);
CREATE INDEX idx_leave_delegations_delegator ON leave_delegations(delegator_id);
CREATE INDEX idx_leave_delegations_delegate ON leave_delegations(delegate_id);
CREATE INDEX idx_leave_delegations_active ON leave_delegations(tenant_id, is_active);
CREATE INDEX idx_leave_delegations_dates ON leave_delegations(start_date, end_date);

CREATE TRIGGER update_leave_delegations_updated_at
    BEFORE UPDATE ON leave_delegations
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE leave_delegations IS 'Approval delegation for managers on leave — temporary authority transfer';
