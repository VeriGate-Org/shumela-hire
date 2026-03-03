-- V019: Create departments table for centralized department management

CREATE TABLE departments (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    name VARCHAR(200) NOT NULL,
    code VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Unique constraints
ALTER TABLE departments ADD CONSTRAINT uk_departments_tenant_name UNIQUE (tenant_id, name);
ALTER TABLE departments ADD CONSTRAINT uk_departments_tenant_code UNIQUE (tenant_id, code);

-- Indexes
CREATE INDEX idx_departments_tenant ON departments(tenant_id);
CREATE INDEX idx_departments_tenant_active ON departments(tenant_id, is_active);

-- Seed from existing data: collect distinct department values across tables
INSERT INTO departments (tenant_id, name, code, is_active)
SELECT DISTINCT sub.tenant_id, sub.department,
       LOWER(REGEXP_REPLACE(REGEXP_REPLACE(TRIM(sub.department), '[^a-zA-Z0-9\s-]', '', 'g'), '\s+', '-', 'g')),
       TRUE
FROM (
    SELECT tenant_id, department FROM job_postings WHERE department IS NOT NULL AND TRIM(department) <> ''
    UNION
    SELECT tenant_id, department FROM applications WHERE department IS NOT NULL AND TRIM(department) <> ''
    UNION
    SELECT tenant_id, department FROM requisitions WHERE department IS NOT NULL AND TRIM(department) <> ''
    UNION
    SELECT tenant_id, department FROM employees WHERE department IS NOT NULL AND TRIM(department) <> ''
    UNION
    SELECT tenant_id, department FROM offers WHERE department IS NOT NULL AND TRIM(department) <> ''
) sub
ON CONFLICT DO NOTHING;
