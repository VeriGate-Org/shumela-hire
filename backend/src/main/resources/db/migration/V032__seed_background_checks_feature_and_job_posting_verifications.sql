-- 1. Add BACKGROUND_CHECKS feature to platform_features (available on all plans)
INSERT INTO platform_features (code, name, description, category, included_plans, is_active)
VALUES ('BACKGROUND_CHECKS', 'Background Checks', 'Background verification checks via integrated providers (criminal, credit, qualification, etc.)', 'compliance', 'TRIAL,STANDARD,ENTERPRISE', true);

-- 2. Seed required verification checks on all 6 existing job postings
-- Risk Manager (80) — financial role: credit, criminal, fraud, qualifications
UPDATE job_postings
SET required_check_types = '["CRIMINAL_CHECK","CREDIT_CHECK","FRAUD_CHECK","QUALIFICATION_VERIFICATION"]',
    enforce_check_completion = true
WHERE id = 80;

-- Investment Analyst (79) — financial role: credit, criminal, qualification, professional body
UPDATE job_postings
SET required_check_types = '["CRIMINAL_CHECK","CREDIT_CHECK","QUALIFICATION_VERIFICATION","PROFESSIONAL_BODY"]',
    enforce_check_completion = true
WHERE id = 79;

-- Senior Investment Analyst (75) — senior financial role: credit, criminal, fraud, qualification, professional body
UPDATE job_postings
SET required_check_types = '["CRIMINAL_CHECK","CREDIT_CHECK","FRAUD_CHECK","QUALIFICATION_VERIFICATION","PROFESSIONAL_BODY"]',
    enforce_check_completion = true
WHERE id = 75;

-- Regional Business Advisor (76) — advisory role: criminal, credit, qualification
UPDATE job_postings
SET required_check_types = '["CRIMINAL_CHECK","CREDIT_CHECK","QUALIFICATION_VERIFICATION"]',
    enforce_check_completion = true
WHERE id = 76;

-- Film & Media Fund Manager (77) — fund management: criminal, credit, fraud, qualification
UPDATE job_postings
SET required_check_types = '["CRIMINAL_CHECK","CREDIT_CHECK","FRAUD_CHECK","QUALIFICATION_VERIFICATION"]',
    enforce_check_completion = true
WHERE id = 77;

-- Agro-Processing Development Specialist (78) — specialist role: criminal, qualification, employment history
UPDATE job_postings
SET required_check_types = '["CRIMINAL_CHECK","QUALIFICATION_VERIFICATION","EMPLOYMENT_HISTORY"]',
    enforce_check_completion = true
WHERE id = 78;
