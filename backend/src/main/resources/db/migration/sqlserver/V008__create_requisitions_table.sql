-- V008: Create requisitions table (SQL Server)

CREATE TABLE requisitions (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    job_title NVARCHAR(200) NOT NULL,
    department NVARCHAR(100),
    location NVARCHAR(100),
    employment_type NVARCHAR(30),
    salary_min DECIMAL(12,2),
    salary_max DECIMAL(12,2),
    description NVARCHAR(MAX),
    justification NVARCHAR(MAX),
    status NVARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    created_by BIGINT,
    tenant_id NVARCHAR(50) NOT NULL,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    CONSTRAINT fk_requisition_creator FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE INDEX idx_requisitions_status ON requisitions(status);
CREATE INDEX idx_requisitions_tenant ON requisitions(tenant_id);
