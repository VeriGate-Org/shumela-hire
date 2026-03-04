-- V022: Fix idc-demo seed data tenant_id
-- V021 inserted data with literal tenant_id='idc-demo', but the actual tenant
-- was created via TenantOnboardingService with a UUID-prefixed ID.
-- This migration moves all data to the correct tenant and removes the duplicate.

DO $$
DECLARE
    v_real_tenant_id    VARCHAR(50);
    v_bad_tenant_id     VARCHAR(50) := 'idc-demo';
    v_real_exists       BOOLEAN;
    v_bad_exists        BOOLEAN;
BEGIN
    -- Check if the duplicate tenant 'idc-demo' exists
    SELECT EXISTS(SELECT 1 FROM tenants WHERE id = v_bad_tenant_id) INTO v_bad_exists;

    IF NOT v_bad_exists THEN
        RAISE NOTICE 'No duplicate tenant with id=idc-demo found. Nothing to fix.';
        RETURN;
    END IF;

    -- Find the REAL tenant (UUID-prefixed, same subdomain)
    SELECT id INTO v_real_tenant_id
    FROM tenants
    WHERE subdomain = 'idc-demo' AND id != v_bad_tenant_id;

    IF v_real_tenant_id IS NULL THEN
        -- The only tenant with subdomain 'idc-demo' IS the 'idc-demo' one.
        -- This means V021 created the tenant fresh. Data is fine, just rename nothing.
        RAISE NOTICE 'Tenant idc-demo is the only one with that subdomain. Data is correct.';
        RETURN;
    END IF;

    RAISE NOTICE 'Migrating data from tenant_id=% to real tenant_id=%', v_bad_tenant_id, v_real_tenant_id;

    -- Move all seeded data from bad tenant to real tenant
    -- Order doesn't matter for UPDATEs (no FK constraints on tenant_id values, only on tenants.id)

    UPDATE departments SET tenant_id = v_real_tenant_id WHERE tenant_id = v_bad_tenant_id;
    UPDATE requisitions SET tenant_id = v_real_tenant_id WHERE tenant_id = v_bad_tenant_id;
    UPDATE job_postings SET tenant_id = v_real_tenant_id WHERE tenant_id = v_bad_tenant_id;
    UPDATE applicants SET tenant_id = v_real_tenant_id WHERE tenant_id = v_bad_tenant_id;
    UPDATE applications SET tenant_id = v_real_tenant_id WHERE tenant_id = v_bad_tenant_id;
    UPDATE interviews SET tenant_id = v_real_tenant_id WHERE tenant_id = v_bad_tenant_id;
    UPDATE offers SET tenant_id = v_real_tenant_id WHERE tenant_id = v_bad_tenant_id;
    UPDATE talent_pools SET tenant_id = v_real_tenant_id WHERE tenant_id = v_bad_tenant_id;
    UPDATE talent_pool_entries SET tenant_id = v_real_tenant_id WHERE tenant_id = v_bad_tenant_id;
    UPDATE agency_profiles SET tenant_id = v_real_tenant_id WHERE tenant_id = v_bad_tenant_id;

    -- Remove the duplicate tenant
    DELETE FROM tenants WHERE id = v_bad_tenant_id;

    RAISE NOTICE 'Successfully migrated all data to tenant % and removed duplicate.', v_real_tenant_id;

END $$;
