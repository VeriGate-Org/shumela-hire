package com.arthmatic.shumelahire.config.tenant;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Strategy interface for setting tenant context on a database connection.
 * PostgreSQL uses SET app.current_tenant; SQL Server uses sp_set_session_context.
 */
public interface TenantConnectionPreparer {

    void setTenantOnConnection(Connection connection, String tenantId) throws SQLException;
}
