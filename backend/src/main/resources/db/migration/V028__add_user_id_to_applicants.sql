-- Link applicants to their user accounts for self-registration
ALTER TABLE applicants ADD COLUMN user_id BIGINT;

ALTER TABLE applicants ADD CONSTRAINT fk_applicant_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL;

CREATE UNIQUE INDEX idx_applicants_user_id ON applicants(user_id) WHERE user_id IS NOT NULL;
