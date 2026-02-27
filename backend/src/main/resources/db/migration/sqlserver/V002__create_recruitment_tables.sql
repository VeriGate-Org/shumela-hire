-- V002: Recruitment workflow tables for ShumelaHire (SQL Server)

CREATE TABLE interviews (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    application_id BIGINT NOT NULL,
    title NVARCHAR(255) NOT NULL,
    type NVARCHAR(30) NOT NULL DEFAULT 'PHONE',
    round NVARCHAR(30) NOT NULL DEFAULT 'SCREENING',
    status NVARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    scheduled_at DATETIME2 NOT NULL,
    duration_minutes INTEGER NOT NULL DEFAULT 60,
    location NVARCHAR(255),
    meeting_link NVARCHAR(255),
    meeting_url NVARCHAR(255),
    phone_number NVARCHAR(255),
    meeting_room NVARCHAR(255),
    instructions NVARCHAR(MAX),
    agenda NVARCHAR(MAX),
    interviewer_id BIGINT,
    interviewer_name NVARCHAR(255),
    interviewer_email NVARCHAR(255),
    additional_interviewers NVARCHAR(255),
    feedback NVARCHAR(MAX),
    rating INTEGER,
    technical_assessment NVARCHAR(MAX),
    communication_skills INTEGER,
    technical_skills INTEGER,
    cultural_fit INTEGER,
    technical_score INTEGER,
    communication_score INTEGER,
    cultural_fit_score INTEGER,
    overall_impression NVARCHAR(MAX),
    recommendation NVARCHAR(30),
    next_steps NVARCHAR(MAX),
    candidate_questions NVARCHAR(MAX),
    interviewer_notes NVARCHAR(MAX),
    questions NVARCHAR(MAX),
    answers NVARCHAR(MAX),
    notes NVARCHAR(MAX),
    preparation_notes NVARCHAR(MAX),
    rescheduled_from DATETIME2,
    reschedule_reason NVARCHAR(255),
    reschedule_count INTEGER DEFAULT 0,
    reminder_sent BIT DEFAULT 0,
    confirmation_received BIT DEFAULT 0,
    reminder_sent_at DATETIME2,
    feedback_requested_at DATETIME2,
    feedback_submitted_at DATETIME2,
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2,
    started_at DATETIME2,
    completed_at DATETIME2,
    cancelled_at DATETIME2,
    cancellation_reason NVARCHAR(255),
    CONSTRAINT fk_interviews_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    CONSTRAINT chk_interviews_type CHECK (type IN ('PHONE', 'VIDEO', 'IN_PERSON', 'PANEL', 'TECHNICAL', 'BEHAVIOURAL', 'COMPETENCY', 'GROUP', 'PRESENTATION', 'CASE_STUDY')),
    CONSTRAINT chk_interviews_round CHECK (round IN ('SCREENING', 'FIRST', 'SECOND', 'THIRD', 'FINAL', 'TECHNICAL', 'PANEL', 'EXECUTIVE')),
    CONSTRAINT chk_interviews_status CHECK (status IN ('SCHEDULED', 'RESCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'NO_SHOW', 'POSTPONED')),
    CONSTRAINT chk_interviews_recommendation CHECK (recommendation IS NULL OR recommendation IN ('STRONG_HIRE', 'HIRE', 'MAYBE', 'NO_HIRE', 'STRONG_NO_HIRE')),
    CONSTRAINT chk_interviews_rating CHECK (rating IS NULL OR (rating >= 1 AND rating <= 5))
);
CREATE INDEX idx_interviews_application_id ON interviews(application_id);
CREATE INDEX idx_interviews_status ON interviews(status);
CREATE INDEX idx_interviews_type ON interviews(type);
CREATE INDEX idx_interviews_scheduled_at ON interviews(scheduled_at);
CREATE INDEX idx_interviews_interviewer_id ON interviews(interviewer_id);
GO
CREATE TRIGGER trg_interviews_updated_at ON interviews
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE interviews SET updated_at = GETDATE()
    FROM interviews INNER JOIN inserted ON interviews.id = inserted.id;
END;
GO

CREATE TABLE offers (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    application_id BIGINT NOT NULL,
    offer_number NVARCHAR(255) NOT NULL UNIQUE,
    version INTEGER NOT NULL DEFAULT 1,
    status NVARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    offer_type NVARCHAR(30) NOT NULL DEFAULT 'FULL_TIME_PERMANENT',
    negotiation_status NVARCHAR(30) NOT NULL DEFAULT 'NOT_STARTED',
    job_title NVARCHAR(255) NOT NULL,
    department NVARCHAR(255) NOT NULL,
    reporting_manager NVARCHAR(255),
    work_location NVARCHAR(255),
    remote_work_allowed BIT DEFAULT 0,
    base_salary DECIMAL(15,2) NOT NULL,
    currency NVARCHAR(10) NOT NULL DEFAULT 'ZAR',
    salary_frequency NVARCHAR(20) DEFAULT 'ANNUALLY',
    bonus_eligible BIT DEFAULT 0,
    bonus_target_percentage DECIMAL(15,2),
    bonus_maximum_percentage DECIMAL(15,2),
    commission_eligible BIT DEFAULT 0,
    commission_structure NVARCHAR(MAX),
    equity_eligible BIT DEFAULT 0,
    equity_details NVARCHAR(MAX),
    signing_bonus DECIMAL(15,2),
    relocation_allowance DECIMAL(15,2),
    benefits_package NVARCHAR(MAX),
    vacation_days_annual INTEGER,
    sick_days_annual INTEGER,
    health_insurance BIT DEFAULT 0,
    retirement_plan BIT DEFAULT 0,
    retirement_contribution_percentage DECIMAL(15,2),
    other_benefits NVARCHAR(MAX),
    employment_type NVARCHAR(50),
    contract_duration_months INTEGER,
    contract_end_date DATE,
    probationary_period_days INTEGER,
    notice_period_days INTEGER DEFAULT 30,
    start_date DATE,
    start_date_flexible BIT DEFAULT 0,
    earliest_start_date DATE,
    latest_start_date DATE,
    offer_expiry_date DATETIME2,
    offer_sent_at DATETIME2,
    candidate_viewed_at DATETIME2,
    candidate_response_at DATETIME2,
    accepted_at DATETIME2,
    declined_at DATETIME2,
    withdrawn_at DATETIME2,
    requires_approval BIT DEFAULT 1,
    approval_level_required INTEGER DEFAULT 1,
    approved_by BIGINT,
    approved_at DATETIME2,
    approval_notes NVARCHAR(MAX),
    rejected_by BIGINT,
    rejected_at DATETIME2,
    rejection_reason NVARCHAR(MAX),
    negotiation_rounds INTEGER DEFAULT 0,
    last_negotiation_at DATETIME2,
    negotiation_notes NVARCHAR(MAX),
    candidate_counter_offer NVARCHAR(MAX),
    company_response NVARCHAR(MAX),
    special_conditions NVARCHAR(MAX),
    confidentiality_agreement BIT DEFAULT 0,
    non_compete_agreement BIT DEFAULT 0,
    non_compete_duration_months INTEGER,
    intellectual_property_agreement BIT DEFAULT 0,
    offer_letter_template_id BIGINT,
    contract_template_id BIGINT,
    offer_document_path NVARCHAR(255),
    signed_document_path NVARCHAR(255),
    e_signature_envelope_id NVARCHAR(255),
    e_signature_status NVARCHAR(255),
    e_signature_sent_at DATETIME2,
    e_signature_completed_at DATETIME2,
    e_signature_provider NVARCHAR(255),
    e_signature_signer_email NVARCHAR(255),
    created_by BIGINT NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_by BIGINT,
    updated_at DATETIME2,
    superseded_by_offer_id BIGINT,
    supersedes_offer_id BIGINT,
    CONSTRAINT fk_offers_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    CONSTRAINT chk_offers_status CHECK (status IN ('DRAFT', 'PENDING_APPROVAL', 'APPROVED', 'SENT', 'VIEWED', 'AWAITING_SIGNATURE', 'SIGNED', 'ACCEPTED', 'DECLINED', 'WITHDRAWN', 'EXPIRED', 'NEGOTIATING', 'REJECTED', 'SUPERSEDED')),
    CONSTRAINT chk_offers_base_salary CHECK (base_salary >= 0)
);
CREATE INDEX idx_offers_application_id ON offers(application_id);
CREATE INDEX idx_offers_status ON offers(status);
CREATE INDEX idx_offers_offer_number ON offers(offer_number);
CREATE INDEX idx_offers_created_by ON offers(created_by);
GO
CREATE TRIGGER trg_offers_updated_at ON offers
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE offers SET updated_at = GETDATE()
    FROM offers INNER JOIN inserted ON offers.id = inserted.id;
END;
GO

CREATE TABLE pipeline_transitions (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    application_id BIGINT NOT NULL,
    from_stage NVARCHAR(50),
    to_stage NVARCHAR(50) NOT NULL,
    transition_type NVARCHAR(30) NOT NULL DEFAULT 'PROGRESSION',
    reason NVARCHAR(MAX),
    notes NVARCHAR(MAX),
    automated BIT NOT NULL DEFAULT 0,
    triggered_by_interview_id BIGINT,
    triggered_by_assessment_id BIGINT,
    metadata NVARCHAR(MAX),
    created_by BIGINT NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    effective_at DATETIME2,
    duration_in_previous_stage_hours BIGINT,
    CONSTRAINT fk_pipeline_transitions_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    CONSTRAINT chk_pipeline_transitions_type CHECK (transition_type IN ('PROGRESSION', 'REGRESSION', 'REJECTION', 'WITHDRAWAL', 'REACTIVATION'))
);
CREATE INDEX idx_pipeline_transitions_application_id ON pipeline_transitions(application_id);
CREATE INDEX idx_pipeline_transitions_to_stage ON pipeline_transitions(to_stage);
CREATE INDEX idx_pipeline_transitions_created_at ON pipeline_transitions(created_at);
GO

CREATE TABLE talent_pools (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    pool_name NVARCHAR(255) NOT NULL UNIQUE,
    description NVARCHAR(MAX),
    department NVARCHAR(255),
    skills_criteria NVARCHAR(MAX),
    experience_level NVARCHAR(255),
    is_active BIT NOT NULL DEFAULT 1,
    auto_add_enabled BIT NOT NULL DEFAULT 0,
    created_by BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2
);
CREATE INDEX idx_talent_pools_is_active ON talent_pools(is_active);
CREATE INDEX idx_talent_pools_department ON talent_pools(department);
GO
CREATE TRIGGER trg_talent_pools_updated_at ON talent_pools
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE talent_pools SET updated_at = GETDATE()
    FROM talent_pools INNER JOIN inserted ON talent_pools.id = inserted.id;
END;
GO

CREATE TABLE talent_pool_entries (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    talent_pool_id BIGINT NOT NULL,
    applicant_id BIGINT NOT NULL,
    source_application_id BIGINT,
    source_type NVARCHAR(255),
    notes NVARCHAR(MAX),
    rating INTEGER,
    is_available BIT NOT NULL DEFAULT 1,
    last_contacted_at DATETIME2,
    added_by BIGINT,
    added_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    removed_at DATETIME2,
    removal_reason NVARCHAR(255),
    CONSTRAINT fk_talent_pool_entries_pool FOREIGN KEY (talent_pool_id) REFERENCES talent_pools(id) ON DELETE CASCADE,
    CONSTRAINT fk_talent_pool_entries_applicant FOREIGN KEY (applicant_id) REFERENCES applicants(id) ON DELETE CASCADE,
    CONSTRAINT fk_talent_pool_entries_application FOREIGN KEY (source_application_id) REFERENCES applications(id) ON DELETE SET NULL,
    CONSTRAINT uq_talent_pool_entries UNIQUE (talent_pool_id, applicant_id),
    CONSTRAINT chk_talent_pool_entries_rating CHECK (rating IS NULL OR (rating >= 1 AND rating <= 5))
);
CREATE INDEX idx_talent_pool_entries_pool_id ON talent_pool_entries(talent_pool_id);
CREATE INDEX idx_talent_pool_entries_applicant_id ON talent_pool_entries(applicant_id);
CREATE INDEX idx_talent_pool_entries_is_available ON talent_pool_entries(is_available);
GO

CREATE TABLE screening_questions (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    job_posting_id BIGINT NOT NULL,
    question_text NVARCHAR(1000) NOT NULL,
    question_type NVARCHAR(30) NOT NULL,
    is_required BIT NOT NULL DEFAULT 0,
    display_order INTEGER NOT NULL DEFAULT 0,
    question_options NVARCHAR(MAX),
    validation_rules NVARCHAR(MAX),
    help_text NVARCHAR(500),
    is_active BIT NOT NULL DEFAULT 1,
    created_by NVARCHAR(100) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    CONSTRAINT chk_screening_questions_type CHECK (question_type IN ('TEXT', 'TEXTAREA', 'NUMBER', 'DATE', 'BOOLEAN', 'DROPDOWN', 'MULTIPLE_CHOICE', 'CHECKBOX', 'FILE_UPLOAD', 'YES_NO', 'RATING', 'SALARY_RANGE'))
);
CREATE INDEX idx_screening_questions_job_posting_id ON screening_questions(job_posting_id);
CREATE INDEX idx_screening_questions_is_active ON screening_questions(is_active);
GO
CREATE TRIGGER trg_screening_questions_updated_at ON screening_questions
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE screening_questions SET updated_at = GETDATE()
    FROM screening_questions INNER JOIN inserted ON screening_questions.id = inserted.id;
END;
GO

CREATE TABLE screening_answers (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    application_id BIGINT NOT NULL,
    screening_question_id BIGINT NOT NULL,
    answer_value NVARCHAR(MAX),
    answer_file_url NVARCHAR(255),
    answer_file_name NVARCHAR(255),
    is_valid BIT DEFAULT 1,
    validation_message NVARCHAR(500),
    answered_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    CONSTRAINT fk_screening_answers_question FOREIGN KEY (screening_question_id) REFERENCES screening_questions(id) ON DELETE CASCADE,
    CONSTRAINT uq_screening_answers UNIQUE (application_id, screening_question_id)
);
CREATE INDEX idx_screening_answers_application_id ON screening_answers(application_id);
CREATE INDEX idx_screening_answers_question_id ON screening_answers(screening_question_id);
GO

CREATE TABLE shortlist_scores (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    application_id BIGINT NOT NULL,
    total_score FLOAT NOT NULL,
    skills_match_score FLOAT,
    experience_score FLOAT,
    education_score FLOAT,
    screening_score FLOAT,
    keyword_match_score FLOAT,
    score_breakdown NVARCHAR(MAX),
    is_shortlisted BIT DEFAULT 0,
    manually_overridden BIT DEFAULT 0,
    override_reason NVARCHAR(255),
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2,
    CONSTRAINT fk_shortlist_scores_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE
);
CREATE INDEX idx_shortlist_scores_application_id ON shortlist_scores(application_id);
CREATE INDEX idx_shortlist_scores_total_score ON shortlist_scores(total_score);
CREATE INDEX idx_shortlist_scores_is_shortlisted ON shortlist_scores(is_shortlisted);
GO
CREATE TRIGGER trg_shortlist_scores_updated_at ON shortlist_scores
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE shortlist_scores SET updated_at = GETDATE()
    FROM shortlist_scores INNER JOIN inserted ON shortlist_scores.id = inserted.id;
END;
GO

CREATE TABLE messages (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    sender_name NVARCHAR(255),
    sender_role NVARCHAR(255),
    recipient_ids NVARCHAR(MAX),
    recipient_type NVARCHAR(30) DEFAULT 'DIRECT',
    message_type NVARCHAR(30) NOT NULL DEFAULT 'DIRECT_MESSAGE',
    subject NVARCHAR(255),
    content NVARCHAR(MAX) NOT NULL,
    message_format NVARCHAR(20) DEFAULT 'TEXT',
    priority NVARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    thread_id NVARCHAR(255),
    parent_message_id BIGINT,
    conversation_id NVARCHAR(255),
    is_thread_starter BIT DEFAULT 0,
    application_id BIGINT,
    interview_id BIGINT,
    job_posting_id BIGINT,
    offer_id BIGINT,
    is_read BIT DEFAULT 0,
    read_by NVARCHAR(MAX),
    is_delivered BIT DEFAULT 0,
    delivered_at DATETIME2,
    is_archived BIT DEFAULT 0,
    archived_at DATETIME2,
    is_deleted BIT DEFAULT 0,
    deleted_at DATETIME2,
    deleted_by BIGINT,
    has_attachments BIT DEFAULT 0,
    attachment_urls NVARCHAR(MAX),
    is_urgent BIT DEFAULT 0,
    requires_response BIT DEFAULT 0,
    response_deadline DATETIME2,
    is_confidential BIT DEFAULT 0,
    auto_delete_at DATETIME2,
    scheduled_for DATETIME2,
    is_scheduled BIT DEFAULT 0,
    tags NVARCHAR(MAX),
    category NVARCHAR(255),
    metadata NVARCHAR(MAX),
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2,
    sent_at DATETIME2
);
CREATE INDEX idx_messages_sender_id ON messages(sender_id);
CREATE INDEX idx_messages_message_type ON messages(message_type);
CREATE INDEX idx_messages_thread_id ON messages(thread_id);
CREATE INDEX idx_messages_conversation_id ON messages(conversation_id);
CREATE INDEX idx_messages_application_id ON messages(application_id);
CREATE INDEX idx_messages_is_read ON messages(is_read);
CREATE INDEX idx_messages_created_at ON messages(created_at);
GO
CREATE TRIGGER trg_messages_updated_at ON messages
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE messages SET updated_at = GETDATE()
    FROM messages INNER JOIN inserted ON messages.id = inserted.id;
END;
GO

CREATE TABLE notifications (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    recipient_id BIGINT NOT NULL,
    sender_id BIGINT,
    type NVARCHAR(50) NOT NULL,
    channel NVARCHAR(20) NOT NULL DEFAULT 'IN_APP',
    priority NVARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    title NVARCHAR(255) NOT NULL,
    message NVARCHAR(MAX) NOT NULL,
    action_url NVARCHAR(255),
    action_label NVARCHAR(255),
    icon NVARCHAR(255),
    metadata NVARCHAR(MAX),
    application_id BIGINT,
    interview_id BIGINT,
    job_posting_id BIGINT,
    offer_id BIGINT,
    is_read BIT DEFAULT 0,
    read_at DATETIME2,
    is_delivered BIT DEFAULT 0,
    delivered_at DATETIME2,
    delivery_attempts INTEGER DEFAULT 0,
    last_delivery_attempt DATETIME2,
    delivery_error NVARCHAR(255),
    email_to NVARCHAR(255),
    email_subject NVARCHAR(255),
    email_template NVARCHAR(255),
    phone_number NVARCHAR(255),
    sms_template NVARCHAR(255),
    push_device_token NVARCHAR(255),
    push_payload NVARCHAR(MAX),
    scheduled_for DATETIME2,
    is_scheduled BIT DEFAULT 0,
    expires_at DATETIME2,
    notification_group NVARCHAR(255),
    batch_id NVARCHAR(255),
    is_batch_digest BIT DEFAULT 0,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2,
    created_by BIGINT
);
CREATE INDEX idx_notifications_recipient_id ON notifications(recipient_id);
CREATE INDEX idx_notifications_type ON notifications(type);
CREATE INDEX idx_notifications_channel ON notifications(channel);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);
CREATE INDEX idx_notifications_application_id ON notifications(application_id);
GO
CREATE TRIGGER trg_notifications_updated_at ON notifications
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE notifications SET updated_at = GETDATE()
    FROM notifications INNER JOIN inserted ON notifications.id = inserted.id;
END;
GO

CREATE TABLE agency_profiles (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    agency_name NVARCHAR(255) NOT NULL UNIQUE,
    registration_number NVARCHAR(255) UNIQUE,
    contact_person NVARCHAR(255) NOT NULL,
    contact_email NVARCHAR(255) NOT NULL UNIQUE,
    contact_phone NVARCHAR(255),
    specializations NVARCHAR(MAX),
    status NVARCHAR(30) NOT NULL DEFAULT 'PENDING_APPROVAL',
    fee_percentage DECIMAL(5,2),
    contract_start_date DATE,
    contract_end_date DATE,
    bee_level INTEGER,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2,
    CONSTRAINT chk_agency_profiles_status CHECK (status IN ('PENDING_APPROVAL', 'APPROVED', 'SUSPENDED', 'TERMINATED'))
);
CREATE INDEX idx_agency_profiles_status ON agency_profiles(status);
GO
CREATE TRIGGER trg_agency_profiles_updated_at ON agency_profiles
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE agency_profiles SET updated_at = GETDATE()
    FROM agency_profiles INNER JOIN inserted ON agency_profiles.id = inserted.id;
END;
GO

CREATE TABLE agency_submissions (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    agency_id BIGINT NOT NULL,
    job_posting_id BIGINT NOT NULL,
    candidate_name NVARCHAR(255) NOT NULL,
    candidate_email NVARCHAR(255) NOT NULL,
    candidate_phone NVARCHAR(255),
    cv_file_key NVARCHAR(255),
    cover_note NVARCHAR(MAX),
    status NVARCHAR(30) NOT NULL DEFAULT 'SUBMITTED',
    linked_application_id BIGINT,
    submitted_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    reviewed_at DATETIME2,
    reviewed_by BIGINT,
    CONSTRAINT fk_agency_submissions_agency FOREIGN KEY (agency_id) REFERENCES agency_profiles(id) ON DELETE CASCADE,
    CONSTRAINT fk_agency_submissions_job FOREIGN KEY (job_posting_id) REFERENCES job_postings(id) ON DELETE CASCADE,
    CONSTRAINT fk_agency_submissions_application FOREIGN KEY (linked_application_id) REFERENCES applications(id) ON DELETE SET NULL,
    CONSTRAINT chk_agency_submissions_status CHECK (status IN ('SUBMITTED', 'UNDER_REVIEW', 'ACCEPTED', 'REJECTED', 'DUPLICATE'))
);
CREATE INDEX idx_agency_submissions_agency_id ON agency_submissions(agency_id);
CREATE INDEX idx_agency_submissions_job_posting_id ON agency_submissions(job_posting_id);
CREATE INDEX idx_agency_submissions_status ON agency_submissions(status);
GO

CREATE TABLE recruitment_metrics (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    metric_date DATE NOT NULL,
    metric_type NVARCHAR(30) NOT NULL,
    metric_category NVARCHAR(255) NOT NULL,
    metric_name NVARCHAR(255) NOT NULL,
    metric_value DECIMAL(15,4) NOT NULL,
    department NVARCHAR(255),
    job_posting_id BIGINT,
    recruiter_id BIGINT,
    hiring_manager_id BIGINT,
    period_start_date DATE,
    period_end_date DATE,
    target_value DECIMAL(15,4),
    previous_period_value DECIMAL(15,4),
    variance_percentage DECIMAL(10,4),
    trend_direction NVARCHAR(20),
    benchmark_value DECIMAL(15,4),
    notes NVARCHAR(MAX),
    data_source NVARCHAR(255),
    calculation_method NVARCHAR(255),
    is_active BIT DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2,
    created_by BIGINT,
    CONSTRAINT chk_recruitment_metrics_trend CHECK (trend_direction IS NULL OR trend_direction IN ('UP', 'DOWN', 'STABLE'))
);
CREATE INDEX idx_recruitment_metrics_date ON recruitment_metrics(metric_date);
CREATE INDEX idx_recruitment_metrics_type ON recruitment_metrics(metric_type);
CREATE INDEX idx_recruitment_metrics_category ON recruitment_metrics(metric_category);
CREATE INDEX idx_recruitment_metrics_department ON recruitment_metrics(department);
GO
CREATE TRIGGER trg_recruitment_metrics_updated_at ON recruitment_metrics
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE recruitment_metrics SET updated_at = GETDATE()
    FROM recruitment_metrics INNER JOIN inserted ON recruitment_metrics.id = inserted.id;
END;
GO

CREATE TABLE tg_salary_recommendations (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    recommendation_number NVARCHAR(255) NOT NULL UNIQUE,
    status NVARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    position_title NVARCHAR(255) NOT NULL,
    department NVARCHAR(255),
    job_grade NVARCHAR(255),
    position_level NVARCHAR(255),
    requested_by NVARCHAR(255),
    candidate_name NVARCHAR(255),
    candidate_current_salary DECIMAL(15,2),
    candidate_expected_salary DECIMAL(15,2),
    market_data_reference NVARCHAR(MAX),
    proposed_min_salary DECIMAL(15,2),
    proposed_max_salary DECIMAL(15,2),
    proposed_target_salary DECIMAL(15,2),
    recommended_salary DECIMAL(15,2),
    recommended_by NVARCHAR(255),
    recommended_at DATETIME2,
    recommendation_justification NVARCHAR(MAX),
    bonus_recommendation NVARCHAR(MAX),
    equity_recommendation NVARCHAR(MAX),
    benefits_notes NVARCHAR(MAX),
    requires_approval BIT DEFAULT 1,
    approval_level_required INTEGER,
    approved_by NVARCHAR(255),
    approved_at DATETIME2,
    approval_notes NVARCHAR(MAX),
    rejected_by NVARCHAR(255),
    rejection_reason NVARCHAR(MAX),
    currency NVARCHAR(10) DEFAULT 'ZAR',
    application_id BIGINT,
    offer_id BIGINT,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2,
    CONSTRAINT fk_salary_recommendations_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE SET NULL,
    CONSTRAINT chk_salary_recommendations_status CHECK (status IN ('DRAFT', 'SUBMITTED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED', 'IMPLEMENTED'))
);
CREATE INDEX idx_salary_recommendations_status ON tg_salary_recommendations(status);
CREATE INDEX idx_salary_recommendations_application_id ON tg_salary_recommendations(application_id);
GO
CREATE TRIGGER trg_tg_salary_recommendations_updated_at ON tg_salary_recommendations
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE tg_salary_recommendations SET updated_at = GETDATE()
    FROM tg_salary_recommendations INNER JOIN inserted ON tg_salary_recommendations.id = inserted.id;
END;
GO

CREATE TABLE tg_job_board_postings (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    job_posting_id NVARCHAR(255) NOT NULL,
    board_type NVARCHAR(30) NOT NULL,
    status NVARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    external_post_id NVARCHAR(255),
    external_url NVARCHAR(255),
    posted_at DATETIME2,
    expires_at DATETIME2,
    view_count INTEGER DEFAULT 0,
    click_count INTEGER DEFAULT 0,
    application_count INTEGER DEFAULT 0,
    error_message NVARCHAR(MAX),
    board_config NVARCHAR(MAX),
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2,
    CONSTRAINT chk_job_board_postings_board_type CHECK (board_type IN ('LINKEDIN', 'INDEED', 'PNET', 'CAREERS24', 'GLASSDOOR', 'COMPANY_WEBSITE', 'INTERNAL_PORTAL', 'OTHER')),
    CONSTRAINT chk_job_board_postings_status CHECK (status IN ('DRAFT', 'PENDING', 'POSTED', 'EXPIRED', 'REMOVED', 'ERROR'))
);
CREATE INDEX idx_job_board_postings_job_posting_id ON tg_job_board_postings(job_posting_id);
CREATE INDEX idx_job_board_postings_board_type ON tg_job_board_postings(board_type);
CREATE INDEX idx_job_board_postings_status ON tg_job_board_postings(status);
GO
CREATE TRIGGER trg_tg_job_board_postings_updated_at ON tg_job_board_postings
AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE tg_job_board_postings SET updated_at = GETDATE()
    FROM tg_job_board_postings INNER JOIN inserted ON tg_job_board_postings.id = inserted.id;
END;
GO
