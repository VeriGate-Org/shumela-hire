package com.arthmatic.shumelahire.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_ad_history")
public class JobAdHistory extends TenantAwareEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_ad_id", nullable = false)
    @NotNull(message = "Job ad is required")
    private JobAd jobAd;
    
    @NotBlank(message = "Action is required")
    @Column(nullable = false, length = 50)
    private String action;
    
    @NotBlank(message = "Actor user ID is required")
    @Column(name = "actor_user_id", nullable = false, length = 100)
    private String actorUserId;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;
    
    @Column(columnDefinition = "TEXT")
    private String details;
    
    // Constructors
    public JobAdHistory() {}
    
    public JobAdHistory(JobAd jobAd, String action, String actorUserId) {
        this.jobAd = jobAd;
        this.action = action;
        this.actorUserId = actorUserId;
    }
    
    public JobAdHistory(JobAd jobAd, String action, String actorUserId, String details) {
        this.jobAd = jobAd;
        this.action = action;
        this.actorUserId = actorUserId;
        this.details = details;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public JobAd getJobAd() {
        return jobAd;
    }
    
    public void setJobAd(JobAd jobAd) {
        this.jobAd = jobAd;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getActorUserId() {
        return actorUserId;
    }
    
    public void setActorUserId(String actorUserId) {
        this.actorUserId = actorUserId;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getDetails() {
        return details;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
    
    @Override
    public String toString() {
        return "JobAdHistory{" +
                "id=" + id +
                ", action='" + action + '\'' +
                ", actorUserId='" + actorUserId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
    
    // Common action constants
    public static final String ACTION_CREATED = "CREATED";
    public static final String ACTION_UPDATED = "UPDATED";
    public static final String ACTION_PUBLISHED = "PUBLISHED";
    public static final String ACTION_UNPUBLISHED = "UNPUBLISHED";
    public static final String ACTION_EXPIRED = "EXPIRED";
}