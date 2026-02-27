package com.arthmatic.shumelahire.config.tenant;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
@Profile({"sbx", "ppe", "prod"})
public class PostgresTenantConnectionPreparer implements TenantConnectionPreparer {

    @Override
    public void setTenantOnConnection(Connection connection, String tenantId) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SET app.current_tenant = '" + tenantId.replace("'", "''") + "'");
        }
    }
}
