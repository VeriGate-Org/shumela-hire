CREATE TABLE workflow_definitions (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT false,
    trigger_type VARCHAR(50),
    trigger_config TEXT,
    steps_json TEXT,
    created_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE workflow_executions (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    workflow_definition_id BIGINT REFERENCES workflow_definitions(id),
    status VARCHAR(20) NOT NULL DEFAULT 'running',
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    triggered_by VARCHAR(255),
    current_step INT DEFAULT 0,
    total_steps INT DEFAULT 0,
    execution_log_json TEXT,
    context_json TEXT
);

CREATE INDEX idx_workflow_definitions_tenant ON workflow_definitions(tenant_id);
CREATE INDEX idx_workflow_definitions_active ON workflow_definitions(is_active);
CREATE INDEX idx_workflow_definitions_category ON workflow_definitions(category);
CREATE INDEX idx_workflow_executions_tenant ON workflow_executions(tenant_id);
CREATE INDEX idx_workflow_executions_definition ON workflow_executions(workflow_definition_id);
CREATE INDEX idx_workflow_executions_status ON workflow_executions(status);
