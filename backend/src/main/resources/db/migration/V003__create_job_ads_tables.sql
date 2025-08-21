-- Create job_ads table
CREATE TABLE job_ads (
    id BIGSERIAL PRIMARY KEY,
    requisition_id BIGINT,
    title VARCHAR(500) NOT NULL,
    html_body TEXT NOT NULL,
    channel_internal BOOLEAN NOT NULL DEFAULT FALSE,
    channel_external BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    closing_date DATE,
    slug VARCHAR(200) UNIQUE,
    created_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_job_ads_status CHECK (status IN ('DRAFT', 'PUBLISHED', 'UNPUBLISHED', 'EXPIRED')),
    CONSTRAINT chk_job_ads_channels CHECK (channel_internal = TRUE OR channel_external = TRUE)
);

-- Create job_ad_history table
CREATE TABLE job_ad_history (
    id BIGSERIAL PRIMARY KEY,
    job_ad_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    actor_user_id VARCHAR(100) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    details TEXT,
    
    CONSTRAINT fk_job_ad_history_job_ad FOREIGN KEY (job_ad_id) REFERENCES job_ads(id) ON DELETE CASCADE
);

-- Create indexes
CREATE INDEX idx_job_ads_status ON job_ads(status);
CREATE INDEX idx_job_ads_channel_internal ON job_ads(channel_internal);
CREATE INDEX idx_job_ads_channel_external ON job_ads(channel_external);
CREATE INDEX idx_job_ads_closing_date ON job_ads(closing_date);
CREATE INDEX idx_job_ads_created_by ON job_ads(created_by);
CREATE INDEX idx_job_ads_created_at ON job_ads(created_at);
CREATE INDEX idx_job_ads_requisition_id ON job_ads(requisition_id);

CREATE INDEX idx_job_ad_history_job_ad_id ON job_ad_history(job_ad_id);
CREATE INDEX idx_job_ad_history_action ON job_ad_history(action);
CREATE INDEX idx_job_ad_history_actor_user_id ON job_ad_history(actor_user_id);
CREATE INDEX idx_job_ad_history_timestamp ON job_ad_history(timestamp);

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger for job_ads table
CREATE TRIGGER update_job_ads_updated_at 
    BEFORE UPDATE ON job_ads 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Add comments
COMMENT ON TABLE job_ads IS 'Job advertisements that can be published to internal and/or external channels';
COMMENT ON TABLE job_ad_history IS 'Audit trail for job advertisement changes';

COMMENT ON COLUMN job_ads.requisition_id IS 'Reference to requisition that triggered this job ad';
COMMENT ON COLUMN job_ads.channel_internal IS 'Whether this ad is published to internal job portal';
COMMENT ON COLUMN job_ads.channel_external IS 'Whether this ad is published to external job portal';
COMMENT ON COLUMN job_ads.status IS 'Current status of the job ad';
COMMENT ON COLUMN job_ads.slug IS 'URL-friendly identifier for external access';
COMMENT ON COLUMN job_ads.closing_date IS 'Date when applications close, null means no expiry';

COMMENT ON COLUMN job_ad_history.action IS 'Type of action performed (CREATED, UPDATED, PUBLISHED, UNPUBLISHED, EXPIRED)';
COMMENT ON COLUMN job_ad_history.actor_user_id IS 'User who performed the action';
COMMENT ON COLUMN job_ad_history.details IS 'Additional details about the action';