-- V027: Org Management Module Tables
-- Creates org_units, positions, headcount_plans tables with hierarchy support

-- =====================================================
-- Org unit type enum
-- =====================================================
CREATE TYPE org_unit_type AS ENUM (
    'COMPANY', 'DIVISION', 'DEPARTMENT', 'TEAM', 'SITE'
);

-- =====================================================
-- Position status enum
-- =====================================================
CREATE TYPE position_status AS ENUM (
    'ACTIVE', 'INACTIVE', 'FROZEN'
);

-- =====================================================
-- Org Units table (hierarchical)
-- =====================================================
CREATE TABLE org_units (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    code VARCHAR(50),
    unit_type VARCHAR(30) NOT NULL,
    parent_id BIGINT,
    manager_id BIGINT,
    cost_centre VARCHAR(100),
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    -- Multi-tenancy
    tenant_id VARCHAR(50) NOT NULL,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT fk_org_unit_parent FOREIGN KEY (parent_id) REFERENCES org_units(id),
    CONSTRAINT fk_org_unit_manager FOREIGN KEY (manager_id) REFERENCES employees(id),
    CONSTRAINT fk_org_unit_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT uk_org_unit_code_tenant UNIQUE (code, tenant_id)
);

-- Indexes for org_units
CREATE INDEX idx_org_units_tenant_id ON org_units(tenant_id);
CREATE INDEX idx_org_units_parent_id ON org_units(parent_id);
CREATE INDEX idx_org_units_unit_type ON org_units(unit_type);
CREATE INDEX idx_org_units_manager_id ON org_units(manager_id);
CREATE INDEX idx_org_units_active ON org_units(is_active);

-- =====================================================
-- Positions table (independent of employees)
-- =====================================================
CREATE TABLE positions (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    code VARCHAR(50),
    department VARCHAR(200),
    grade VARCHAR(50),
    reporting_position_id BIGINT,
    fte DECIMAL(5,2) NOT NULL DEFAULT 1.00,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    current_employee_id BIGINT,
    is_vacant BOOLEAN NOT NULL DEFAULT TRUE,
    job_sharing_allowed BOOLEAN NOT NULL DEFAULT FALSE,
    description TEXT,
    org_unit_id BIGINT,
    location VARCHAR(200),

    -- Multi-tenancy
    tenant_id VARCHAR(50) NOT NULL,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT fk_position_reporting FOREIGN KEY (reporting_position_id) REFERENCES positions(id),
    CONSTRAINT fk_position_employee FOREIGN KEY (current_employee_id) REFERENCES employees(id),
    CONSTRAINT fk_position_org_unit FOREIGN KEY (org_unit_id) REFERENCES org_units(id),
    CONSTRAINT fk_position_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT uk_position_code_tenant UNIQUE (code, tenant_id)
);

-- Indexes for positions
CREATE INDEX idx_positions_tenant_id ON positions(tenant_id);
CREATE INDEX idx_positions_department ON positions(department);
CREATE INDEX idx_positions_status ON positions(status);
CREATE INDEX idx_positions_is_vacant ON positions(is_vacant);
CREATE INDEX idx_positions_org_unit ON positions(org_unit_id);
CREATE INDEX idx_positions_reporting ON positions(reporting_position_id);
CREATE INDEX idx_positions_employee ON positions(current_employee_id);

-- =====================================================
-- Headcount Plans table
-- =====================================================
CREATE TABLE headcount_plans (
    id BIGSERIAL PRIMARY KEY,
    department VARCHAR(200) NOT NULL,
    fiscal_year INT NOT NULL,
    planned_headcount INT NOT NULL DEFAULT 0,
    current_headcount INT NOT NULL DEFAULT 0,
    budget DECIMAL(15,2),
    notes TEXT,
    forecast_vacancies INT NOT NULL DEFAULT 0,
    new_position_requests INT NOT NULL DEFAULT 0,
    org_unit_id BIGINT,

    -- Multi-tenancy
    tenant_id VARCHAR(50) NOT NULL,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT fk_headcount_org_unit FOREIGN KEY (org_unit_id) REFERENCES org_units(id),
    CONSTRAINT fk_headcount_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT uk_headcount_dept_year_tenant UNIQUE (department, fiscal_year, tenant_id)
);

-- Indexes for headcount_plans
CREATE INDEX idx_headcount_plans_tenant_id ON headcount_plans(tenant_id);
CREATE INDEX idx_headcount_plans_department ON headcount_plans(department);
CREATE INDEX idx_headcount_plans_fiscal_year ON headcount_plans(fiscal_year);
CREATE INDEX idx_headcount_plans_org_unit ON headcount_plans(org_unit_id);
