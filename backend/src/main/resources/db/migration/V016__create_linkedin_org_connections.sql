CREATE TABLE IF NOT EXISTS linkedin_org_connections (
    id                   BIGSERIAL PRIMARY KEY,
    tenant_id            VARCHAR(50) NOT NULL,
    access_token         TEXT NOT NULL,
    refresh_token        TEXT,
    token_expires_at     TIMESTAMP NOT NULL,
    organization_id      VARCHAR(50) NOT NULL,
    organization_name    VARCHAR(255),
    connected_by_user_id VARCHAR(255) NOT NULL,
    connected_at         TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at           TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP
);

CREATE UNIQUE INDEX idx_linkedin_org_conn_tenant ON linkedin_org_connections(tenant_id);
