-- Report templates table for the report library
CREATE TABLE report_templates (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    created_by VARCHAR(100),
    is_shared BOOLEAN DEFAULT FALSE,
    is_system BOOLEAN DEFAULT FALSE,
    run_count INTEGER DEFAULT 0,
    last_run TIMESTAMP,
    fields_json TEXT,
    filters_json TEXT,
    visualization_json TEXT,
    schedule_json TEXT,
    date_range_json TEXT,
    tags_json TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_report_templates_tenant ON report_templates(tenant_id);
CREATE INDEX idx_report_templates_created_by ON report_templates(created_by);
CREATE INDEX idx_report_templates_system ON report_templates(is_system);

-- Seed system report templates (available to all tenants)
INSERT INTO report_templates (tenant_id, name, description, created_by, is_shared, is_system, fields_json, filters_json, visualization_json, date_range_json, tags_json) VALUES

-- 1. Recruitment Pipeline Report
('default', 'Recruitment Pipeline Overview',
 'End-to-end view of the recruitment pipeline showing applications at each stage, conversion rates, and bottlenecks.',
 'system', true, true,
 '["candidate_name","candidate_email","position_title","position_department","application_date","applications_count","interviews_count","offers_count","hires_count","conversion_rate"]',
 '[]',
 '{"type":"funnel","xAxis":"position_department","yAxis":"applications_count","groupBy":"position_department","aggregation":"count"}',
 '{"start":"","end":""}',
 '["recruitment","pipeline","overview"]'),

-- 2. Time-to-Hire Analysis
('default', 'Time-to-Hire Analysis',
 'Measures average time from application to hire across departments and positions. Identifies delays in the hiring process.',
 'system', true, true,
 '["position_title","position_department","application_date","interview_date","offer_date","hire_date","time_to_hire"]',
 '[]',
 '{"type":"bar","xAxis":"position_department","yAxis":"time_to_hire","aggregation":"avg"}',
 '{"start":"","end":""}',
 '["performance","time-to-hire","analytics"]'),

-- 3. Applicant Source Effectiveness
('default', 'Applicant Source Effectiveness',
 'Compares recruitment channels by volume, quality, and conversion rate. Helps optimise recruitment spend allocation.',
 'system', true, true,
 '["candidate_source","candidate_name","candidate_score","applications_count","interviews_count","hires_count","conversion_rate","cost_per_hire"]',
 '[]',
 '{"type":"bar","xAxis":"candidate_source","yAxis":"applications_count","groupBy":"candidate_source","aggregation":"count"}',
 '{"start":"","end":""}',
 '["recruitment","source","effectiveness","ROI"]'),

-- 4. Interview Performance Summary
('default', 'Interview Performance Summary',
 'Tracks interview completion rates, interviewer activity, average scores, and pass/fail ratios across the organisation.',
 'system', true, true,
 '["candidate_name","position_title","interview_date","interviews_count","candidate_score","conversion_rate"]',
 '[]',
 '{"type":"line","xAxis":"interview_date","yAxis":"candidate_score","aggregation":"avg"}',
 '{"start":"","end":""}',
 '["performance","interviews","metrics"]'),

-- 5. Executive Hiring Dashboard
('default', 'Executive Hiring Dashboard',
 'High-level summary for leadership showing headcount progress, open positions, cost metrics, and hiring velocity.',
 'system', true, true,
 '["position_department","applications_count","interviews_count","offers_count","hires_count","conversion_rate","cost_per_hire","time_to_hire"]',
 '[]',
 '{"type":"bar","xAxis":"position_department","yAxis":"hires_count","groupBy":"position_department","aggregation":"sum"}',
 '{"start":"","end":""}',
 '["executive","dashboard","KPI"]'),

-- 6. Employment Equity Report
('default', 'Employment Equity Demographics',
 'Demographic breakdown of applicants and hires by gender, race, disability status, and citizenship for EE compliance reporting.',
 'system', true, true,
 '["candidate_name","position_title","position_department","candidate_source","applications_count","hires_count"]',
 '[]',
 '{"type":"pie","xAxis":"position_department","yAxis":"applications_count","aggregation":"count"}',
 '{"start":"","end":""}',
 '["compliance","EE","demographics","BBBEE"]'),

-- 7. Offer Acceptance Rate
('default', 'Offer Acceptance Rate',
 'Tracks offer-to-acceptance ratios by department, level, and salary band. Highlights where candidates decline and why.',
 'system', true, true,
 '["position_title","position_department","position_level","position_salary_min","position_salary_max","offers_count","hires_count","conversion_rate"]',
 '[]',
 '{"type":"bar","xAxis":"position_department","yAxis":"conversion_rate","aggregation":"avg"}',
 '{"start":"","end":""}',
 '["offers","acceptance","retention"]'),

-- 8. Candidate Experience Summary
('default', 'Candidate Experience Summary',
 'Application-to-decision timeline per candidate, showing touchpoints, wait times, and overall experience metrics.',
 'system', true, true,
 '["candidate_name","candidate_email","candidate_score","candidate_experience_years","position_title","application_date","interview_date","offer_date","time_to_hire"]',
 '[]',
 '{"type":"table"}',
 '{"start":"","end":""}',
 '["candidate","experience","timeline"]');
