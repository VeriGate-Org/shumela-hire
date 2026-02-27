-- V011: SAP Payroll Transmissions (SQL Server)

IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'sap_payroll_transmissions') AND type = 'U')
CREATE TABLE sap_payroll_transmissions (
    id                  BIGINT IDENTITY(1,1) PRIMARY KEY,
    tenant_id           NVARCHAR(50) NOT NULL,
    offer_id            BIGINT NOT NULL,
    transmission_id     NVARCHAR(50) NOT NULL UNIQUE,
    sap_employee_number NVARCHAR(20),
    status              NVARCHAR(30) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'VALIDATING', 'TRANSMITTED', 'CONFIRMED', 'FAILED', 'RETRY_PENDING', 'CANCELLED')),
    payload_json        NVARCHAR(MAX),
    response_json       NVARCHAR(MAX),
    error_message       NVARCHAR(MAX),
    retry_count         INT NOT NULL DEFAULT 0,
    max_retries         INT NOT NULL DEFAULT 3,
    next_retry_at       DATETIME2,
    initiated_by        BIGINT,
    sap_company_code    NVARCHAR(10),
    sap_payroll_area    NVARCHAR(10),
    validation_errors   NVARCHAR(MAX),
    created_at          DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at          DATETIME2,
    transmitted_at      DATETIME2,
    confirmed_at        DATETIME2,
    cancelled_at        DATETIME2,
    cancelled_by        BIGINT,
    cancellation_reason NVARCHAR(MAX),

    CONSTRAINT fk_sap_transmission_offer FOREIGN KEY (offer_id) REFERENCES offers(id) ON DELETE CASCADE
);

CREATE INDEX idx_sap_transmissions_tenant ON sap_payroll_transmissions(tenant_id);
CREATE INDEX idx_sap_transmissions_offer ON sap_payroll_transmissions(offer_id);
CREATE INDEX idx_sap_transmissions_status ON sap_payroll_transmissions(status);
CREATE INDEX idx_sap_transmissions_transmission_id ON sap_payroll_transmissions(transmission_id);
CREATE INDEX idx_sap_transmissions_sap_employee ON sap_payroll_transmissions(sap_employee_number)
    WHERE sap_employee_number IS NOT NULL;
CREATE INDEX idx_sap_transmissions_retryable ON sap_payroll_transmissions(status, next_retry_at)
    WHERE status IN ('FAILED', 'RETRY_PENDING');

CREATE TRIGGER trg_sap_transmission_updated_at ON sap_payroll_transmissions
    AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE sap_payroll_transmissions SET updated_at = GETDATE()
    FROM sap_payroll_transmissions INNER JOIN inserted ON sap_payroll_transmissions.id = inserted.id;
END;
GO
