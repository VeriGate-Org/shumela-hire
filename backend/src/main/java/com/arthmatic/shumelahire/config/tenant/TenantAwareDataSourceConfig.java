package com.arthmatic.shumelahire.config.tenant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DelegatingDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
@Profile({"sbx", "ppe", "prod", "sqlserver", "onprem"})
public class TenantAwareDataSourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(TenantAwareDataSourceConfig.class);

    @Bean
    @Primary
    public DataSource tenantAwareDataSource(DataSourceProperties properties,
                                            TenantConnectionPreparer connectionPreparer) {
        DataSource baseDataSource = properties.initializeDataSourceBuilder().build();
        return new TenantSettingDataSource(baseDataSource, connectionPreparer);
    }

    private static class TenantSettingDataSource extends DelegatingDataSource {

        private final TenantConnectionPreparer connectionPreparer;

        TenantSettingDataSource(DataSource targetDataSource, TenantConnectionPreparer connectionPreparer) {
            super(targetDataSource);
            this.connectionPreparer = connectionPreparer;
        }

        @Override
        public Connection getConnection() throws SQLException {
            Connection connection = super.getConnection();
            setTenantOnConnection(connection);
            return connection;
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            Connection connection = super.getConnection(username, password);
            setTenantOnConnection(connection);
            return connection;
        }

        private void setTenantOnConnection(Connection connection) throws SQLException {
            String tenantId = TenantContext.getCurrentTenant();
            if (tenantId != null) {
                connectionPreparer.setTenantOnConnection(connection, tenantId);
            }
        }
    }
}
