-- Seed AI feature flags for the AI subsystem
INSERT INTO platform_features (code, name, description, category, included_plans, is_active) VALUES
('AI_ENABLED', 'AI Features', 'Parent flag that gates the entire AI subsystem', 'ai', 'STANDARD,ENTERPRISE', true),
('AI_SCREENING_CV', 'AI CV Screening', 'AI-powered CV screening and match scoring against job requirements', 'ai', 'STANDARD,ENTERPRISE', true),
('AI_SCREENING_SUMMARY', 'AI Candidate Summary', 'AI-generated executive summary of candidate profiles', 'ai', 'STANDARD,ENTERPRISE', true),
('AI_SCREENING_RANKING', 'AI Candidate Ranking', 'AI-powered candidate ranking with adjustable weights', 'ai', 'STANDARD,ENTERPRISE', true),
('AI_SCREENING_NOTES', 'AI Screening Notes', 'AI-assisted drafting of screening notes from observations', 'ai', 'STANDARD,ENTERPRISE', true),
('AI_SEARCH', 'AI Smart Search', 'Natural language search interpretation for candidate discovery', 'ai', 'STANDARD,ENTERPRISE', true),
('AI_SALARY_BENCHMARK', 'AI Salary Benchmark', 'AI-powered salary benchmarking with market data analysis', 'ai', 'ENTERPRISE', true),
('AI_JOB_DESCRIPTION', 'AI Job Description Writer', 'AI-generated job descriptions with bias checking', 'ai', 'STANDARD,ENTERPRISE', true),
('AI_EMAIL_DRAFTER', 'AI Email Drafter', 'AI-assisted email drafting for candidate communications', 'ai', 'STANDARD,ENTERPRISE', true),
('AI_INTERVIEW_QUESTIONS', 'AI Interview Questions', 'AI-generated interview question bank with difficulty levels', 'ai', 'STANDARD,ENTERPRISE', true),
('AI_OFFER_PREDICTION', 'AI Offer Prediction', 'AI-powered offer acceptance probability prediction', 'ai', 'ENTERPRISE', true),
('AI_DUPLICATE_DETECTION', 'AI Duplicate Detection', 'AI-powered duplicate candidate detection with confidence scoring', 'ai', 'STANDARD,ENTERPRISE', true),
('AI_REPORT_NARRATIVE', 'AI Report Narrative', 'AI-generated report narratives and executive summaries', 'ai', 'ENTERPRISE', true);
