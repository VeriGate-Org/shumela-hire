package com.arthmatic.shumelahire.config.tenant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SqlServerTenantConnectionPreparerTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    private final SqlServerTenantConnectionPreparer preparer = new SqlServerTenantConnectionPreparer();

    @Test
    void setTenantOnConnection_executesSessionContext() throws Exception {
        when(connection.prepareStatement(
                "EXEC sp_set_session_context @key = N'TenantId', @value = ?"))
                .thenReturn(preparedStatement);

        preparer.setTenantOnConnection(connection, "tenant-xyz");

        verify(preparedStatement).setString(1, "tenant-xyz");
        verify(preparedStatement).execute();
        verify(preparedStatement).close();
    }

    @Test
    void setTenantOnConnection_handlesSpecialCharacters() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        preparer.setTenantOnConnection(connection, "tenant'special");

        verify(preparedStatement).setString(1, "tenant'special");
        verify(preparedStatement).execute();
    }
}
