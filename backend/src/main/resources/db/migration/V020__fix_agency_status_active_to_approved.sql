-- Fix agency_profiles status mismatch: Java enum used 'ACTIVE' but
-- the check constraint only allows 'APPROVED'. Migrate any existing
-- rows, then update the constraint to match the corrected enum values.

UPDATE agency_profiles SET status = 'APPROVED' WHERE status = 'ACTIVE';

-- Recreate check constraint with the correct allowed values
ALTER TABLE agency_profiles DROP CONSTRAINT IF EXISTS chk_agency_profiles_status;
ALTER TABLE agency_profiles ADD CONSTRAINT chk_agency_profiles_status
    CHECK (status IN ('PENDING_APPROVAL', 'APPROVED', 'SUSPENDED', 'TERMINATED'));
