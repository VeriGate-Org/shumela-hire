-- V010: Create background_checks table for verification provider integration (Dots Africa)
-- Tracks verification checks initiated against candidates during recruitment.

CREATE TABLE IF NOT EXISTS background_checks (
    id                      BIGSERIAL PRIMARY KEY,
    tenant_id               VARCHAR(50) NOT NULL,

    -- Application link
    application_id          BIGINT NOT NULL REFERENCES applications(id) ON DELETE CASCADE,

    -- Internal reference
    reference_id            VARCHAR(100) UNIQUE,

    -- Candidate info (denormalised for provider submission)
    candidate_id_number     VARCHAR(20) NOT NULL,
    candidate_name          VARCHAR(200),
    candidate_email         VARCHAR(200),

    -- Check configuration
    check_types             TEXT,         -- JSON array of check type codes

    -- Status & result
    status                  VARCHAR(30) NOT NULL DEFAULT 'INITIATED'
                            CHECK (status IN ('INITIATED','PENDING_CONSENT','IN_PROGRESS',
                                              'PARTIAL_RESULTS','COMPLETED','FAILED','CANCELLED')),
    overall_result          VARCHAR(30)
                            CHECK (overall_result IS NULL OR overall_result IN ('CLEAR','ADVERSE','PENDING_REVIEW','INCONCLUSIVE')),
    results_json            TEXT,         -- JSON object with per-check-type results

    -- Consent tracking (POPIA compliance)
    consent_obtained        BOOLEAN NOT NULL DEFAULT FALSE,
    consent_obtained_at     TIMESTAMP,

    -- User tracking
    initiated_by            BIGINT NOT NULL,

    -- Provider details
    provider                VARCHAR(50),
    external_screening_id   VARCHAR(200),
    report_url              VARCHAR(500),

    -- Error handling
    error_message           TEXT,
    notes                   TEXT,

    -- Timestamps
    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP,
    submitted_at            TIMESTAMP,
    completed_at            TIMESTAMP,
    cancelled_at            TIMESTAMP
);

-- Indexes
CREATE INDEX idx_bgcheck_tenant ON background_checks(tenant_id);
CREATE INDEX idx_bgcheck_application ON background_checks(application_id);
CREATE INDEX idx_bgcheck_reference ON background_checks(reference_id);
CREATE INDEX idx_bgcheck_status ON background_checks(status);
CREATE INDEX idx_bgcheck_initiated_by ON background_checks(initiated_by);
CREATE INDEX idx_bgcheck_external_id ON background_checks(external_screening_id);

-- Auto-update trigger for updated_at
CREATE OR REPLACE FUNCTION update_background_checks_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_background_checks_updated_at
    BEFORE UPDATE ON background_checks
    FOR EACH ROW
    EXECUTE FUNCTION update_background_checks_updated_at();

-- Documentation
COMMENT ON TABLE background_checks IS 'Verification/background checks initiated through external providers (Dots Africa)';
COMMENT ON COLUMN background_checks.check_types IS 'JSON array of check type codes: ID_VERIFICATION, CREDIT_CHECK, CRIMINAL_CHECK, etc.';
COMMENT ON COLUMN background_checks.results_json IS 'JSON object with per-check-type results from the verification provider';
COMMENT ON COLUMN background_checks.consent_obtained IS 'POPIA: whether candidate provided informed consent for the check';
COMMENT ON COLUMN background_checks.provider IS 'Verification provider identifier, e.g. dots-africa';
