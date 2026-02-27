-- V010: Create background_checks table (SQL Server)

IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'background_checks') AND type = 'U')
CREATE TABLE background_checks (
    id                      BIGINT IDENTITY(1,1) PRIMARY KEY,
    tenant_id               NVARCHAR(50) NOT NULL,
    application_id          BIGINT NOT NULL,
    reference_id            NVARCHAR(100) UNIQUE,
    candidate_id_number     NVARCHAR(20) NOT NULL,
    candidate_name          NVARCHAR(200),
    candidate_email         NVARCHAR(200),
    check_types             NVARCHAR(MAX),
    status                  NVARCHAR(30) NOT NULL DEFAULT 'INITIATED'
                            CHECK (status IN ('INITIATED','PENDING_CONSENT','IN_PROGRESS',
                                              'PARTIAL_RESULTS','COMPLETED','FAILED','CANCELLED')),
    overall_result          NVARCHAR(30)
                            CHECK (overall_result IS NULL OR overall_result IN ('CLEAR','ADVERSE','PENDING_REVIEW','INCONCLUSIVE')),
    results_json            NVARCHAR(MAX),
    consent_obtained        BIT NOT NULL DEFAULT 0,
    consent_obtained_at     DATETIME2,
    initiated_by            BIGINT NOT NULL,
    provider                NVARCHAR(50),
    external_screening_id   NVARCHAR(200),
    report_url              NVARCHAR(500),
    error_message           NVARCHAR(MAX),
    notes                   NVARCHAR(MAX),
    created_at              DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at              DATETIME2,
    submitted_at            DATETIME2,
    completed_at            DATETIME2,
    cancelled_at            DATETIME2,

    CONSTRAINT fk_bgcheck_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE
);

CREATE INDEX idx_bgcheck_tenant ON background_checks(tenant_id);
CREATE INDEX idx_bgcheck_application ON background_checks(application_id);
CREATE INDEX idx_bgcheck_reference ON background_checks(reference_id);
CREATE INDEX idx_bgcheck_status ON background_checks(status);
CREATE INDEX idx_bgcheck_initiated_by ON background_checks(initiated_by);
CREATE INDEX idx_bgcheck_external_id ON background_checks(external_screening_id);

CREATE TRIGGER trg_background_checks_updated_at ON background_checks
    AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE background_checks SET updated_at = GETDATE()
    FROM background_checks INNER JOIN inserted ON background_checks.id = inserted.id;
END;
GO
