-- V012: Core HR Employee tables
-- Tables: employees, employee_documents, employment_events, custom_fields, custom_field_values

-- ============================================================
-- EMPLOYEES
-- ============================================================
CREATE TABLE employees (
    id BIGSERIAL PRIMARY KEY,
    employee_number VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(20),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    preferred_name VARCHAR(100),
    email VARCHAR(255) NOT NULL,
    personal_email VARCHAR(255),
    phone VARCHAR(20),
    mobile_phone VARCHAR(20),
    date_of_birth DATE,
    gender VARCHAR(20),
    marital_status VARCHAR(20),
    nationality VARCHAR(100),

    -- Encrypted PII fields (AES-256-GCM via DataEncryptionService)
    id_number VARCHAR(500),
    tax_number VARCHAR(500),
    passport_number VARCHAR(500),
    bank_name VARCHAR(255),
    bank_branch_code VARCHAR(255),
    bank_account_number VARCHAR(500),
    bank_account_type VARCHAR(50),

    -- Address
    physical_address TEXT,
    postal_address TEXT,
    city VARCHAR(100),
    province VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100) DEFAULT 'South Africa',

    -- Employment details
    department VARCHAR(100),
    division VARCHAR(100),
    job_title VARCHAR(255),
    job_grade VARCHAR(50),
    cost_centre VARCHAR(100),
    location VARCHAR(255),
    employment_type VARCHAR(30) NOT NULL DEFAULT 'PERMANENT',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    hire_date DATE NOT NULL,
    probation_end_date DATE,
    termination_date DATE,
    termination_reason TEXT,

    -- Compensation
    salary NUMERIC(15, 2),
    salary_currency VARCHAR(3) DEFAULT 'ZAR',
    pay_frequency VARCHAR(20) DEFAULT 'MONTHLY',

    -- Org hierarchy
    reporting_manager_id BIGINT,
    applicant_id BIGINT,
    user_id BIGINT,

    -- Employment equity
    race VARCHAR(50),
    disability_status VARCHAR(50),
    citizenship_status VARCHAR(50),

    -- Emergency contact
    emergency_contact_name VARCHAR(200),
    emergency_contact_phone VARCHAR(20),
    emergency_contact_relationship VARCHAR(50),

    -- Metadata
    notes TEXT,
    profile_photo_url VARCHAR(500),
    tenant_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_employees_reporting_manager FOREIGN KEY (reporting_manager_id) REFERENCES employees(id) ON DELETE SET NULL,
    CONSTRAINT fk_employees_applicant FOREIGN KEY (applicant_id) REFERENCES applicants(id) ON DELETE SET NULL,
    CONSTRAINT chk_employees_status CHECK (status IN (
        'ACTIVE', 'PROBATION', 'SUSPENDED', 'TERMINATED', 'RESIGNED', 'RETIRED'
    )),
    CONSTRAINT chk_employees_employment_type CHECK (employment_type IN (
        'PERMANENT', 'CONTRACT', 'TEMPORARY', 'INTERN', 'PART_TIME', 'FIXED_TERM'
    )),
    CONSTRAINT chk_employees_pay_frequency CHECK (pay_frequency IN (
        'WEEKLY', 'BI_WEEKLY', 'MONTHLY', 'ANNUAL'
    ))
);

CREATE INDEX idx_employees_employee_number ON employees(employee_number);
CREATE INDEX idx_employees_email ON employees(email);
CREATE INDEX idx_employees_department ON employees(department);
CREATE INDEX idx_employees_status ON employees(status);
CREATE INDEX idx_employees_job_title ON employees(job_title);
CREATE INDEX idx_employees_hire_date ON employees(hire_date);
CREATE INDEX idx_employees_reporting_manager_id ON employees(reporting_manager_id);
CREATE INDEX idx_employees_applicant_id ON employees(applicant_id);
CREATE INDEX idx_employees_tenant_id ON employees(tenant_id);
CREATE INDEX idx_employees_name ON employees(first_name, last_name);
CREATE INDEX idx_employees_location ON employees(location);
CREATE INDEX idx_employees_division ON employees(division);

CREATE TRIGGER update_employees_updated_at
    BEFORE UPDATE ON employees
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE employees IS 'Core employee records with encrypted PII fields for POPIA compliance';
COMMENT ON COLUMN employees.id_number IS 'SA ID number — encrypted with AES-256-GCM';
COMMENT ON COLUMN employees.tax_number IS 'SARS tax number — encrypted with AES-256-GCM';
COMMENT ON COLUMN employees.bank_account_number IS 'Bank account number — encrypted with AES-256-GCM';
COMMENT ON COLUMN employees.employee_number IS 'Unique employee number with configurable format (e.g. UTW-2026-0001)';
COMMENT ON COLUMN employees.reporting_manager_id IS 'Self-referencing FK for org hierarchy';

-- ============================================================
-- EMPLOYEE DOCUMENTS
-- ============================================================
CREATE TABLE employee_documents (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    filename VARCHAR(255) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    file_size BIGINT,
    content_type VARCHAR(100),
    version INTEGER NOT NULL DEFAULT 1,
    is_current BOOLEAN NOT NULL DEFAULT TRUE,
    expiry_date DATE,
    issued_date DATE,
    issuing_authority VARCHAR(255),
    notes TEXT,
    uploaded_by BIGINT,
    tenant_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_employee_documents_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    CONSTRAINT chk_employee_documents_type CHECK (document_type IN (
        'CONTRACT', 'ID_COPY', 'PASSPORT', 'QUALIFICATION', 'CERTIFICATION',
        'TAX_DOCUMENT', 'MEDICAL', 'DISCIPLINARY', 'PERFORMANCE_REVIEW',
        'POLICY_ACKNOWLEDGEMENT', 'VISA', 'WORK_PERMIT', 'DRIVERS_LICENSE',
        'BANK_CONFIRMATION', 'PROOF_OF_ADDRESS', 'OTHER'
    ))
);

CREATE INDEX idx_employee_documents_employee_id ON employee_documents(employee_id);
CREATE INDEX idx_employee_documents_type ON employee_documents(document_type);
CREATE INDEX idx_employee_documents_expiry_date ON employee_documents(expiry_date);
CREATE INDEX idx_employee_documents_is_current ON employee_documents(is_current);
CREATE INDEX idx_employee_documents_tenant_id ON employee_documents(tenant_id);

CREATE TRIGGER update_employee_documents_updated_at
    BEFORE UPDATE ON employee_documents
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE employee_documents IS 'Employee documents stored in S3 with versioning and expiry tracking';
COMMENT ON COLUMN employee_documents.is_current IS 'TRUE for current version, FALSE for superseded versions';

-- ============================================================
-- EMPLOYMENT EVENTS (immutable lifecycle events)
-- ============================================================
CREATE TABLE employment_events (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    event_type VARCHAR(30) NOT NULL,
    event_date DATE NOT NULL,
    effective_date DATE NOT NULL,
    description TEXT,
    previous_value TEXT,
    new_value TEXT,
    reason TEXT,
    approved_by BIGINT,
    approved_at TIMESTAMP,
    reference_number VARCHAR(100),
    attachments TEXT,
    tenant_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_employment_events_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    CONSTRAINT chk_employment_events_type CHECK (event_type IN (
        'HIRE', 'PROMOTION', 'TRANSFER', 'DEMOTION', 'SUSPENSION',
        'REINSTATEMENT', 'RESIGNATION', 'DISMISSAL', 'RETIREMENT',
        'CONTRACT_END', 'PROBATION_COMPLETION', 'SALARY_CHANGE',
        'TITLE_CHANGE', 'DEPARTMENT_CHANGE', 'MANAGER_CHANGE'
    ))
);

CREATE INDEX idx_employment_events_employee_id ON employment_events(employee_id);
CREATE INDEX idx_employment_events_event_type ON employment_events(event_type);
CREATE INDEX idx_employment_events_event_date ON employment_events(event_date);
CREATE INDEX idx_employment_events_effective_date ON employment_events(effective_date);
CREATE INDEX idx_employment_events_tenant_id ON employment_events(tenant_id);

COMMENT ON TABLE employment_events IS 'Immutable employment lifecycle events for audit and history';
COMMENT ON COLUMN employment_events.previous_value IS 'JSON — previous state (e.g. old title, old department)';
COMMENT ON COLUMN employment_events.new_value IS 'JSON — new state (e.g. new title, new department)';

-- ============================================================
-- CUSTOM FIELDS (configurable per entity type)
-- ============================================================
CREATE TABLE custom_fields (
    id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    field_name VARCHAR(100) NOT NULL,
    field_label VARCHAR(255) NOT NULL,
    field_type VARCHAR(30) NOT NULL,
    is_required BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INTEGER NOT NULL DEFAULT 0,
    options TEXT,
    default_value VARCHAR(500),
    validation_regex VARCHAR(500),
    help_text VARCHAR(500),
    section VARCHAR(100),
    tenant_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_custom_fields_entity_type CHECK (entity_type IN (
        'EMPLOYEE', 'APPLICANT', 'JOB_POSTING', 'LEAVE_REQUEST'
    )),
    CONSTRAINT chk_custom_fields_field_type CHECK (field_type IN (
        'TEXT', 'NUMBER', 'DATE', 'BOOLEAN', 'SELECT', 'MULTI_SELECT',
        'TEXT_AREA', 'EMAIL', 'PHONE', 'URL', 'FILE'
    )),
    CONSTRAINT uq_custom_fields_name_entity_tenant UNIQUE (field_name, entity_type, tenant_id)
);

CREATE INDEX idx_custom_fields_entity_type ON custom_fields(entity_type);
CREATE INDEX idx_custom_fields_is_active ON custom_fields(is_active);
CREATE INDEX idx_custom_fields_tenant_id ON custom_fields(tenant_id);

CREATE TRIGGER update_custom_fields_updated_at
    BEFORE UPDATE ON custom_fields
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE custom_fields IS 'Configurable custom field definitions per entity type';
COMMENT ON COLUMN custom_fields.options IS 'JSON array of options for SELECT/MULTI_SELECT field types';

-- ============================================================
-- CUSTOM FIELD VALUES
-- ============================================================
CREATE TABLE custom_field_values (
    id BIGSERIAL PRIMARY KEY,
    custom_field_id BIGINT NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    field_value TEXT,
    tenant_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_custom_field_values_field FOREIGN KEY (custom_field_id) REFERENCES custom_fields(id) ON DELETE CASCADE,
    CONSTRAINT uq_custom_field_values_unique UNIQUE (custom_field_id, entity_type, entity_id)
);

CREATE INDEX idx_custom_field_values_field_id ON custom_field_values(custom_field_id);
CREATE INDEX idx_custom_field_values_entity ON custom_field_values(entity_type, entity_id);
CREATE INDEX idx_custom_field_values_tenant_id ON custom_field_values(tenant_id);

CREATE TRIGGER update_custom_field_values_updated_at
    BEFORE UPDATE ON custom_field_values
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE custom_field_values IS 'Values for custom fields associated with specific entity instances';
