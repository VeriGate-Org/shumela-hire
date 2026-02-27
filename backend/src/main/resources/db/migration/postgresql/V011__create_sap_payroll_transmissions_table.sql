-- V011: SAP Payroll Transmissions — tracks employee data sent to SAP for payroll onboarding
-- Part of ShumelaHire's SAP SuccessFactors / SAP HCM integration

CREATE TABLE IF NOT EXISTS sap_payroll_transmissions (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           VARCHAR(50)  NOT NULL,
    offer_id            BIGINT       NOT NULL REFERENCES offers(id) ON DELETE CASCADE,
    transmission_id     VARCHAR(50)  NOT NULL UNIQUE,
    sap_employee_number VARCHAR(20),
    status              VARCHAR(30)  NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'VALIDATING', 'TRANSMITTED', 'CONFIRMED', 'FAILED', 'RETRY_PENDING', 'CANCELLED')),
    payload_json        TEXT,
    response_json       TEXT,
    error_message       TEXT,
    retry_count         INTEGER      NOT NULL DEFAULT 0,
    max_retries         INTEGER      NOT NULL DEFAULT 3,
    next_retry_at       TIMESTAMP,
    initiated_by        BIGINT,
    sap_company_code    VARCHAR(10),
    sap_payroll_area    VARCHAR(10),
    validation_errors   TEXT,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP,
    transmitted_at      TIMESTAMP,
    confirmed_at        TIMESTAMP,
    cancelled_at        TIMESTAMP,
    cancelled_by        BIGINT,
    cancellation_reason TEXT
);

-- Indexes for common query patterns
CREATE INDEX idx_sap_transmissions_tenant ON sap_payroll_transmissions(tenant_id);
CREATE INDEX idx_sap_transmissions_offer ON sap_payroll_transmissions(offer_id);
CREATE INDEX idx_sap_transmissions_status ON sap_payroll_transmissions(status);
CREATE INDEX idx_sap_transmissions_transmission_id ON sap_payroll_transmissions(transmission_id);
CREATE INDEX idx_sap_transmissions_sap_employee ON sap_payroll_transmissions(sap_employee_number) WHERE sap_employee_number IS NOT NULL;
CREATE INDEX idx_sap_transmissions_retryable ON sap_payroll_transmissions(status, next_retry_at)
    WHERE status IN ('FAILED', 'RETRY_PENDING');

-- Auto-update trigger for updated_at
CREATE OR REPLACE FUNCTION update_sap_transmission_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_sap_transmission_updated_at
    BEFORE UPDATE ON sap_payroll_transmissions
    FOR EACH ROW
    EXECUTE FUNCTION update_sap_transmission_updated_at();

-- Table comments
COMMENT ON TABLE sap_payroll_transmissions IS 'Tracks employee data transmissions from ShumelaHire to SAP Payroll';
COMMENT ON COLUMN sap_payroll_transmissions.transmission_id IS 'Unique reference for this transmission (SAP-XXXXXXXX format)';
COMMENT ON COLUMN sap_payroll_transmissions.sap_employee_number IS 'SAP-assigned employee number after confirmation';
COMMENT ON COLUMN sap_payroll_transmissions.payload_json IS 'Full JSON payload sent to SAP OData API';
COMMENT ON COLUMN sap_payroll_transmissions.response_json IS 'SAP API response JSON';
COMMENT ON COLUMN sap_payroll_transmissions.validation_errors IS 'JSON map of field-level validation errors';
