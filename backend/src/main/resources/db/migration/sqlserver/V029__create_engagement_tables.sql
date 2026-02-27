-- V013: Employee Engagement Tables — Surveys, Recognition, Wellness & Social
-- STORY-013

-- =============================================
-- Surveys (Pulse & Engagement)
-- =============================================

CREATE TABLE surveys (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    title NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX),
    survey_type NVARCHAR(30) NOT NULL DEFAULT 'PULSE',
    status NVARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    created_by NVARCHAR(255) NOT NULL,
    start_date DATETIME2,
    end_date DATETIME2,
    is_anonymous BIT NOT NULL DEFAULT 1,
    tenant_id NVARCHAR(50) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    CONSTRAINT fk_survey_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT chk_survey_type CHECK (survey_type IN ('PULSE', 'ENGAGEMENT', 'SATISFACTION', 'CUSTOM')),
    CONSTRAINT chk_survey_status CHECK (status IN ('DRAFT', 'ACTIVE', 'CLOSED', 'ARCHIVED'))
);

CREATE INDEX idx_surveys_tenant_id ON surveys(tenant_id);
CREATE INDEX idx_surveys_status ON surveys(status);
CREATE INDEX idx_surveys_type ON surveys(survey_type);

CREATE TABLE survey_questions (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    survey_id BIGINT NOT NULL,
    question_text NVARCHAR(MAX) NOT NULL,
    question_type NVARCHAR(30) NOT NULL DEFAULT 'RATING',
    display_order INT NOT NULL DEFAULT 0,
    is_required BIT NOT NULL DEFAULT 1,
    options NVARCHAR(MAX),
    tenant_id NVARCHAR(50) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    CONSTRAINT fk_survey_question_survey FOREIGN KEY (survey_id) REFERENCES surveys(id) ON DELETE CASCADE,
    CONSTRAINT fk_survey_question_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT chk_question_type CHECK (question_type IN ('RATING', 'TEXT', 'MULTIPLE_CHOICE', 'YES_NO', 'SCALE'))
);

CREATE INDEX idx_survey_questions_survey_id ON survey_questions(survey_id);

CREATE TABLE survey_responses (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    survey_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    respondent_id BIGINT,
    anonymous_token NVARCHAR(255),
    rating_value INT,
    text_value NVARCHAR(MAX),
    selected_option NVARCHAR(255),
    tenant_id NVARCHAR(50) NOT NULL,
    submitted_at DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    CONSTRAINT fk_survey_response_survey FOREIGN KEY (survey_id) REFERENCES surveys(id),
    CONSTRAINT fk_survey_response_question FOREIGN KEY (question_id) REFERENCES survey_questions(id),
    CONSTRAINT fk_survey_response_respondent FOREIGN KEY (respondent_id) REFERENCES employees(id),
    CONSTRAINT fk_survey_response_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT chk_rating_range CHECK (rating_value IS NULL OR (rating_value >= 1 AND rating_value <= 10))
);

CREATE INDEX idx_survey_responses_survey_id ON survey_responses(survey_id);
CREATE INDEX idx_survey_responses_question_id ON survey_responses(question_id);
CREATE INDEX idx_survey_responses_anonymous_token ON survey_responses(anonymous_token);

-- =============================================
-- Recognition / Kudos System
-- =============================================

CREATE TABLE recognitions (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    giver_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    badge NVARCHAR(50) NOT NULL,
    message NVARCHAR(MAX) NOT NULL,
    points INT NOT NULL DEFAULT 10,
    is_public BIT NOT NULL DEFAULT 1,
    tenant_id NVARCHAR(50) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    CONSTRAINT fk_recognition_giver FOREIGN KEY (giver_id) REFERENCES employees(id),
    CONSTRAINT fk_recognition_receiver FOREIGN KEY (receiver_id) REFERENCES employees(id),
    CONSTRAINT fk_recognition_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT chk_recognition_badge CHECK (badge IN (
        'TEAM_PLAYER', 'INNOVATOR', 'LEADER', 'HELPER', 'STAR_PERFORMER',
        'PROBLEM_SOLVER', 'MENTOR', 'CULTURE_CHAMPION', 'CUSTOMER_HERO', 'EXTRA_MILE'
    )),
    CONSTRAINT chk_recognition_points CHECK (points > 0 AND points <= 100)
);

CREATE INDEX idx_recognitions_giver_id ON recognitions(giver_id);
CREATE INDEX idx_recognitions_receiver_id ON recognitions(receiver_id);
CREATE INDEX idx_recognitions_tenant_id ON recognitions(tenant_id);
CREATE INDEX idx_recognitions_badge ON recognitions(badge);
CREATE INDEX idx_recognitions_created_at ON recognitions(created_at);

-- =============================================
-- Wellness Check-Ins & Programs
-- =============================================

CREATE TABLE wellness_programs (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX),
    category NVARCHAR(30) NOT NULL,
    start_date DATE,
    end_date DATE,
    is_active BIT NOT NULL DEFAULT 1,
    max_participants INT,
    tenant_id NVARCHAR(50) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    CONSTRAINT fk_wellness_program_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT chk_wellness_category CHECK (category IN ('PHYSICAL', 'MENTAL', 'FINANCIAL', 'SOCIAL', 'NUTRITIONAL'))
);

CREATE INDEX idx_wellness_programs_tenant_id ON wellness_programs(tenant_id);
CREATE INDEX idx_wellness_programs_category ON wellness_programs(category);
CREATE INDEX idx_wellness_programs_active ON wellness_programs(is_active);

CREATE TABLE wellness_check_ins (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    mood_rating NVARCHAR(20) NOT NULL,
    energy_level INT,
    stress_level INT,
    notes NVARCHAR(MAX),
    wellness_program_id BIGINT,
    tenant_id NVARCHAR(50) NOT NULL,
    check_in_date DATE NOT NULL DEFAULT CAST(GETUTCDATE() AS DATE),
    created_at DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    CONSTRAINT fk_wellness_checkin_employee FOREIGN KEY (employee_id) REFERENCES employees(id),
    CONSTRAINT fk_wellness_checkin_program FOREIGN KEY (wellness_program_id) REFERENCES wellness_programs(id),
    CONSTRAINT fk_wellness_checkin_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT chk_mood_rating CHECK (mood_rating IN ('GREAT', 'GOOD', 'OKAY', 'LOW', 'STRUGGLING')),
    CONSTRAINT chk_energy_level CHECK (energy_level IS NULL OR (energy_level >= 1 AND energy_level <= 10)),
    CONSTRAINT chk_stress_level CHECK (stress_level IS NULL OR (stress_level >= 1 AND stress_level <= 10))
);

CREATE INDEX idx_wellness_checkins_employee_id ON wellness_check_ins(employee_id);
CREATE INDEX idx_wellness_checkins_tenant_id ON wellness_check_ins(tenant_id);
CREATE INDEX idx_wellness_checkins_date ON wellness_check_ins(check_in_date);
CREATE INDEX idx_wellness_checkins_mood ON wellness_check_ins(mood_rating);

-- =============================================
-- Social Feed / Wall
-- =============================================

CREATE TABLE social_posts (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    author_id BIGINT NOT NULL,
    post_type NVARCHAR(30) NOT NULL DEFAULT 'UPDATE',
    title NVARCHAR(255),
    content NVARCHAR(MAX) NOT NULL,
    is_pinned BIT NOT NULL DEFAULT 0,
    like_count INT NOT NULL DEFAULT 0,
    comment_count INT NOT NULL DEFAULT 0,
    tenant_id NVARCHAR(50) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
    CONSTRAINT fk_social_post_author FOREIGN KEY (author_id) REFERENCES employees(id),
    CONSTRAINT fk_social_post_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT chk_post_type CHECK (post_type IN ('UPDATE', 'ANNOUNCEMENT', 'ACHIEVEMENT', 'EVENT', 'POLL'))
);

CREATE INDEX idx_social_posts_author_id ON social_posts(author_id);
CREATE INDEX idx_social_posts_tenant_id ON social_posts(tenant_id);
CREATE INDEX idx_social_posts_type ON social_posts(post_type);
CREATE INDEX idx_social_posts_created_at ON social_posts(created_at);
CREATE INDEX idx_social_posts_pinned ON social_posts(is_pinned);
