package com.arthmatic.shumelahire.config.tenant;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TenantConnectionPreparerStrategyTest {

    @Test
    void postgresPreparer_implementsInterface() {
        TenantConnectionPreparer preparer = new PostgresTenantConnectionPreparer();
        assertThat(preparer).isInstanceOf(TenantConnectionPreparer.class);
    }

    @Test
    void sqlServerPreparer_implementsInterface() {
        TenantConnectionPreparer preparer = new SqlServerTenantConnectionPreparer();
        assertThat(preparer).isInstanceOf(TenantConnectionPreparer.class);
    }

    @Test
    void bothPreparers_areDistinctImplementations() {
        TenantConnectionPreparer pgPreparer = new PostgresTenantConnectionPreparer();
        TenantConnectionPreparer sqlPreparer = new SqlServerTenantConnectionPreparer();
        assertThat(pgPreparer.getClass()).isNotEqualTo(sqlPreparer.getClass());
    }
}
