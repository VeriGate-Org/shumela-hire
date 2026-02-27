package com.arthmatic.shumelahire.config.tenant;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Component
@Profile({"sqlserver", "onprem"})
public class SqlServerTenantConnectionPreparer implements TenantConnectionPreparer {

    @Override
    public void setTenantOnConnection(Connection connection, String tenantId) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "EXEC sp_set_session_context @key = N'TenantId', @value = ?")) {
            ps.setString(1, tenantId);
            ps.execute();
        }
    }
}
