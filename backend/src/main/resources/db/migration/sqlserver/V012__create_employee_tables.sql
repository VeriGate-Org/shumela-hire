-- V012: Core HR Employee Module Tables (SQL Server)
-- PostgreSQL ENUM types replaced with NVARCHAR + CHECK constraints

-- =====================================================
-- Employees table
-- =====================================================
CREATE TABLE employees (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    employee_number NVARCHAR(50) NOT NULL,
    title NVARCHAR(20),
    first_name NVARCHAR(100) NOT NULL,
    last_name NVARCHAR(100) NOT NULL,
    preferred_name NVARCHAR(100),
    email NVARCHAR(255) NOT NULL,
    personal_email NVARCHAR(255),
    phone NVARCHAR(20),
    mobile_phone NVARCHAR(20),
    date_of_birth DATE,
    gender NVARCHAR(20),
    race NVARCHAR(50),
    disability_status NVARCHAR(50),
    citizenship_status NVARCHAR(50),
    nationality NVARCHAR(100),
    marital_status NVARCHAR(30),

    -- Encrypted PII fields (AES-256-GCM via @Convert)
    id_number NVARCHAR(MAX),
    tax_number NVARCHAR(MAX),
    bank_account_number NVARCHAR(MAX),
    bank_name NVARCHAR(100),
    bank_branch_code NVARCHAR(20),

    -- Address
    physical_address NVARCHAR(MAX),
    postal_address NVARCHAR(MAX),
    city NVARCHAR(100),
    province NVARCHAR(100),
    postal_code NVARCHAR(20),
    country NVARCHAR(100) DEFAULT 'South Africa',

    -- Employment details
    status NVARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    department NVARCHAR(200),
    division NVARCHAR(200),
    job_title NVARCHAR(200),
    job_grade NVARCHAR(50),
    employment_type NVARCHAR(50),
    hire_date DATE NOT NULL,
    probation_end_date DATE,
    termination_date DATE,
    termination_reason NVARCHAR(MAX),
    contract_end_date DATE,

    -- Org structure
    reporting_manager_id BIGINT,
    cost_centre NVARCHAR(100),
    location NVARCHAR(200),
    site NVARCHAR(200),

    -- Source tracking
    applicant_id BIGINT,

    -- Photo
    profile_photo_url NVARCHAR(500),

    -- Emergency contact
    emergency_contact_name NVARCHAR(200),
    emergency_contact_phone NVARCHAR(20),
    emergency_contact_relationship NVARCHAR(100),

    -- POPIA consent
    demographics_consent BIT DEFAULT 0,
    demographics_consent_date DATETIME2,

    -- Multi-tenancy
    tenant_id NVARCHAR(50) NOT NULL,

    -- Timestamps
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),

    -- Constraints
    CONSTRAINT fk_employee_reporting_manager FOREIGN KEY (reporting_manager_id) REFERENCES employees(id),
    CONSTRAINT fk_employee_applicant FOREIGN KEY (applicant_id) REFERENCES applicants(id),
    CONSTRAINT fk_employee_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT uk_employee_number_tenant UNIQUE (employee_number, tenant_id),
    CONSTRAINT uk_employee_email_tenant UNIQUE (email, tenant_id),
    CONSTRAINT chk_employee_status CHECK (status IN (
        'ACTIVE', 'PROBATION', 'SUSPENDED', 'TERMINATED', 'RESIGNED', 'RETIRED'
    ))
);

-- Indexes for employees
CREATE INDEX idx_employees_tenant_id ON employees(tenant_id);
CREATE INDEX idx_employees_status ON employees(status);
CREATE INDEX idx_employees_department ON employees(department);
CREATE INDEX idx_employees_job_title ON employees(job_title);
CREATE INDEX idx_employees_reporting_manager ON employees(reporting_manager_id);
CREATE INDEX idx_employees_hire_date ON employees(hire_date);
CREATE INDEX idx_employees_name ON employees(first_name, last_name);
CREATE INDEX idx_employees_applicant ON employees(applicant_id);

-- =====================================================
-- Employee documents table
-- =====================================================
CREATE TABLE employee_documents (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    document_type NVARCHAR(50) NOT NULL,
    title NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX),
    filename NVARCHAR(255) NOT NULL,
    file_url NVARCHAR(500) NOT NULL,
    file_size BIGINT,
    content_type NVARCHAR(100),
    version INT NOT NULL DEFAULT 1,
    expiry_date DATE,
    is_active BIT NOT NULL DEFAULT 1,
    uploaded_by NVARCHAR(255),

    -- Multi-tenancy
    tenant_id NVARCHAR(50) NOT NULL,

    -- Timestamps
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),

    -- Constraints
    CONSTRAINT fk_employee_doc_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    CONSTRAINT fk_employee_doc_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT chk_employee_doc_type CHECK (document_type IN (
        'ID_DOCUMENT', 'PASSPORT', 'WORK_PERMIT', 'TAX_CERTIFICATE', 'QUALIFICATION',
        'CONTRACT', 'OFFER_LETTER', 'DISCIPLINARY', 'MEDICAL', 'TRAINING_CERTIFICATE',
        'PERFORMANCE_REVIEW', 'OTHER'
    ))
);

-- Indexes for employee_documents
CREATE INDEX idx_employee_docs_tenant_id ON employee_documents(tenant_id);
CREATE INDEX idx_employee_docs_employee ON employee_documents(employee_id);
CREATE INDEX idx_employee_docs_type ON employee_documents(document_type);
CREATE INDEX idx_employee_docs_expiry ON employee_documents(expiry_date);
CREATE INDEX idx_employee_docs_active ON employee_documents(is_active);

-- =====================================================
-- Employment events table (immutable lifecycle log)
-- =====================================================
CREATE TABLE employment_events (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    event_type NVARCHAR(50) NOT NULL,
    event_date DATE NOT NULL,
    effective_date DATE NOT NULL,
    description NVARCHAR(MAX),
    notes NVARCHAR(MAX),

    -- Before/after snapshots for change tracking
    previous_department NVARCHAR(200),
    new_department NVARCHAR(200),
    previous_job_title NVARCHAR(200),
    new_job_title NVARCHAR(200),
    previous_job_grade NVARCHAR(50),
    new_job_grade NVARCHAR(50),
    previous_reporting_manager_id BIGINT,
    new_reporting_manager_id BIGINT,
    previous_location NVARCHAR(200),
    new_location NVARCHAR(200),

    -- Who recorded this event
    recorded_by NVARCHAR(255),

    -- Multi-tenancy
    tenant_id NVARCHAR(50) NOT NULL,

    -- Timestamp
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),

    -- Constraints
    CONSTRAINT fk_event_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    CONSTRAINT fk_event_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT chk_event_type CHECK (event_type IN (
        'HIRE', 'PROMOTION', 'TRANSFER', 'DEMOTION', 'SUSPENSION',
        'REINSTATEMENT', 'RESIGNATION', 'DISMISSAL', 'RETIREMENT', 'CONTRACT_END'
    ))
);

-- Indexes for employment_events
CREATE INDEX idx_employment_events_tenant_id ON employment_events(tenant_id);
CREATE INDEX idx_employment_events_employee ON employment_events(employee_id);
CREATE INDEX idx_employment_events_type ON employment_events(event_type);
CREATE INDEX idx_employment_events_date ON employment_events(event_date);

-- =====================================================
-- Custom fields table (configurable field definitions)
-- =====================================================
CREATE TABLE custom_fields (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    field_name NVARCHAR(100) NOT NULL,
    field_label NVARCHAR(200) NOT NULL,
    entity_type NVARCHAR(50) NOT NULL,
    data_type NVARCHAR(30) NOT NULL,
    is_required BIT NOT NULL DEFAULT 0,
    is_active BIT NOT NULL DEFAULT 1,
    display_order INT NOT NULL DEFAULT 0,
    options NVARCHAR(MAX),
    default_value NVARCHAR(500),
    validation_regex NVARCHAR(500),
    help_text NVARCHAR(MAX),

    -- Multi-tenancy
    tenant_id NVARCHAR(50) NOT NULL,

    -- Timestamps
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),

    -- Constraints
    CONSTRAINT fk_custom_field_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT uk_custom_field_name_entity_tenant UNIQUE (field_name, entity_type, tenant_id),
    CONSTRAINT chk_custom_field_entity_type CHECK (entity_type IN (
        'EMPLOYEE', 'EMPLOYEE_DOCUMENT', 'EMPLOYMENT_EVENT'
    )),
    CONSTRAINT chk_custom_field_data_type CHECK (data_type IN (
        'TEXT', 'NUMBER', 'DATE', 'BOOLEAN', 'SELECT', 'MULTI_SELECT'
    ))
);

-- Indexes for custom_fields
CREATE INDEX idx_custom_fields_tenant_id ON custom_fields(tenant_id);
CREATE INDEX idx_custom_fields_entity_type ON custom_fields(entity_type);
CREATE INDEX idx_custom_fields_active ON custom_fields(is_active);

-- =====================================================
-- Custom field values table
-- =====================================================
CREATE TABLE custom_field_values (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    custom_field_id BIGINT NOT NULL,
    entity_id BIGINT NOT NULL,
    entity_type NVARCHAR(50) NOT NULL,
    field_value NVARCHAR(MAX),

    -- Multi-tenancy
    tenant_id NVARCHAR(50) NOT NULL,

    -- Timestamps
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),

    -- Constraints
    CONSTRAINT fk_field_value_field FOREIGN KEY (custom_field_id) REFERENCES custom_fields(id) ON DELETE CASCADE,
    CONSTRAINT fk_field_value_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT uk_field_value_entity UNIQUE (custom_field_id, entity_id, entity_type, tenant_id)
);

-- Indexes for custom_field_values
CREATE INDEX idx_custom_field_values_tenant_id ON custom_field_values(tenant_id);
CREATE INDEX idx_custom_field_values_field ON custom_field_values(custom_field_id);
CREATE INDEX idx_custom_field_values_entity ON custom_field_values(entity_id, entity_type);
