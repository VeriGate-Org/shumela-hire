package com.arthmatic.shumelahire.tenant;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * PostgreSQL Row Level Security (RLS) isolation tests.
 *
 * These tests require Testcontainers with a PostgreSQL image and verify that
 * RLS policies correctly isolate tenant data at the database level, independent
 * of Hibernate filters.
 *
 * Prerequisites:
 *   - Docker running locally
 *   - org.testcontainers:postgresql dependency in pom.xml
 *
 * To enable: remove @Disabled, add Testcontainers dependency, and configure
 * the PostgreSQL container to run V001-V006 migrations before tests.
 *
 * Test plan:
 *   1. Start PostgreSQL Testcontainer
 *   2. Run Flyway migrations (including V006__add_multi_tenancy.sql with RLS)
 *   3. Insert data as tenant-a via SET app.current_tenant = 'tenant-a'
 *   4. Insert data as tenant-b via SET app.current_tenant = 'tenant-b'
 *   5. Query as tenant-a → should only see tenant-a data
 *   6. Query as tenant-b → should only see tenant-b data
 *   7. Query without setting app.current_tenant → should see nothing (RLS default deny)
 */
@Disabled("Requires Testcontainers + Docker — enable when ready for integration testing")
class RlsIsolationTest {

    @Test
    void tenantA_canOnlySeeOwnRows() {
        // Placeholder — implement with Testcontainers PostgreSQL
    }

    @Test
    void tenantB_canOnlySeeOwnRows() {
        // Placeholder — implement with Testcontainers PostgreSQL
    }

    @Test
    void unsetTenant_shouldSeeNoRows() {
        // Placeholder — verify RLS default-deny when app.current_tenant is not set
    }
}
