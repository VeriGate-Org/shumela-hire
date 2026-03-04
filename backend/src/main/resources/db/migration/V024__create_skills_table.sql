-- V024: Create skills table for searchable skill selection
CREATE TABLE IF NOT EXISTS skills (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(100) NOT NULL,
    category VARCHAR(100),
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE skills ADD CONSTRAINT uk_skills_tenant_name UNIQUE (tenant_id, name);
ALTER TABLE skills ADD CONSTRAINT uk_skills_tenant_code UNIQUE (tenant_id, code);

CREATE INDEX idx_skills_tenant ON skills(tenant_id);
CREATE INDEX idx_skills_tenant_active ON skills(tenant_id, is_active);
CREATE INDEX idx_skills_category ON skills(category);

CREATE TRIGGER update_skills_updated_at
    BEFORE UPDATE ON skills
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE skills IS 'Predefined skills for talent pool criteria and job requirements';

-- Seed common skills for idc-demo tenant
INSERT INTO skills (tenant_id, name, code, category) VALUES
    ('idc-demo', 'Java', 'java', 'TECHNICAL'),
    ('idc-demo', 'Python', 'python', 'TECHNICAL'),
    ('idc-demo', 'JavaScript', 'javascript', 'TECHNICAL'),
    ('idc-demo', 'TypeScript', 'typescript', 'TECHNICAL'),
    ('idc-demo', 'React', 'react', 'TECHNICAL'),
    ('idc-demo', 'Angular', 'angular', 'TECHNICAL'),
    ('idc-demo', 'Node.js', 'nodejs', 'TECHNICAL'),
    ('idc-demo', 'Spring Boot', 'spring-boot', 'TECHNICAL'),
    ('idc-demo', 'SQL', 'sql', 'TECHNICAL'),
    ('idc-demo', 'PostgreSQL', 'postgresql', 'TECHNICAL'),
    ('idc-demo', 'AWS', 'aws', 'TECHNICAL'),
    ('idc-demo', 'Docker', 'docker', 'TECHNICAL'),
    ('idc-demo', 'Kubernetes', 'kubernetes', 'TECHNICAL'),
    ('idc-demo', 'Git', 'git', 'TECHNICAL'),
    ('idc-demo', 'REST APIs', 'rest-apis', 'TECHNICAL'),
    ('idc-demo', 'GraphQL', 'graphql', 'TECHNICAL'),
    ('idc-demo', 'CI/CD', 'ci-cd', 'TECHNICAL'),
    ('idc-demo', 'Agile/Scrum', 'agile-scrum', 'DOMAIN'),
    ('idc-demo', 'Project Management', 'project-management', 'DOMAIN'),
    ('idc-demo', 'Data Analysis', 'data-analysis', 'DOMAIN'),
    ('idc-demo', 'Machine Learning', 'machine-learning', 'TECHNICAL'),
    ('idc-demo', 'Communication', 'communication', 'SOFT_SKILL'),
    ('idc-demo', 'Leadership', 'leadership', 'SOFT_SKILL'),
    ('idc-demo', 'Problem Solving', 'problem-solving', 'SOFT_SKILL'),
    ('idc-demo', 'Teamwork', 'teamwork', 'SOFT_SKILL'),
    ('idc-demo', 'Financial Analysis', 'financial-analysis', 'DOMAIN'),
    ('idc-demo', 'Sales', 'sales', 'DOMAIN'),
    ('idc-demo', 'Marketing', 'marketing', 'DOMAIN'),
    ('idc-demo', 'HR Management', 'hr-management', 'DOMAIN'),
    ('idc-demo', 'Customer Service', 'customer-service', 'SOFT_SKILL')
ON CONFLICT DO NOTHING;
