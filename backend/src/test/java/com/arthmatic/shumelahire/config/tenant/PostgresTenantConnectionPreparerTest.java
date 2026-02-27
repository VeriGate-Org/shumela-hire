package com.arthmatic.shumelahire.config.tenant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostgresTenantConnectionPreparerTest {

    @Mock
    private Connection connection;

    @Mock
    private Statement statement;

    private final PostgresTenantConnectionPreparer preparer = new PostgresTenantConnectionPreparer();

    @Test
    void setTenantOnConnection_executesSetCommand() throws Exception {
        when(connection.createStatement()).thenReturn(statement);

        preparer.setTenantOnConnection(connection, "tenant-abc");

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(statement).execute(sqlCaptor.capture());
        assertThat(sqlCaptor.getValue()).isEqualTo("SET app.current_tenant = 'tenant-abc'");
        verify(statement).close();
    }

    @Test
    void setTenantOnConnection_escapesSingleQuotes() throws Exception {
        when(connection.createStatement()).thenReturn(statement);

        preparer.setTenantOnConnection(connection, "tenant'inject");

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(statement).execute(sqlCaptor.capture());
        assertThat(sqlCaptor.getValue()).isEqualTo("SET app.current_tenant = 'tenant''inject'");
    }
}
