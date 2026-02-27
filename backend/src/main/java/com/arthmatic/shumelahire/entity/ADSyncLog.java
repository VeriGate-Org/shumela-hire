package com.arthmatic.shumelahire.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ad_sync_logs")
public class ADSyncLog extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "sync_type", nullable = false, length = 20)
    private SyncType syncType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SyncStatus status = SyncStatus.IN_PROGRESS;

    @Column(name = "users_created")
    private Integer usersCreated = 0;

    @Column(name = "users_updated")
    private Integer usersUpdated = 0;

    @Column(name = "users_disabled")
    private Integer usersDisabled = 0;

    @Column(name = "users_skipped")
    private Integer usersSkipped = 0;

    @Column(name = "total_ad_users_processed")
    private Integer totalAdUsersProcessed = 0;

    @Column(name = "errors", length = 10000)
    private String errors;

    @Column(name = "triggered_by", length = 100)
    private String triggeredBy;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "duration_ms")
    private Long durationMs;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public enum SyncType {
        FULL, DELTA
    }

    public enum SyncStatus {
        IN_PROGRESS, COMPLETED, FAILED, PARTIAL
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public SyncType getSyncType() { return syncType; }
    public void setSyncType(SyncType syncType) { this.syncType = syncType; }

    public SyncStatus getStatus() { return status; }
    public void setStatus(SyncStatus status) { this.status = status; }

    public Integer getUsersCreated() { return usersCreated; }
    public void setUsersCreated(Integer usersCreated) { this.usersCreated = usersCreated; }

    public Integer getUsersUpdated() { return usersUpdated; }
    public void setUsersUpdated(Integer usersUpdated) { this.usersUpdated = usersUpdated; }

    public Integer getUsersDisabled() { return usersDisabled; }
    public void setUsersDisabled(Integer usersDisabled) { this.usersDisabled = usersDisabled; }

    public Integer getUsersSkipped() { return usersSkipped; }
    public void setUsersSkipped(Integer usersSkipped) { this.usersSkipped = usersSkipped; }

    public Integer getTotalAdUsersProcessed() { return totalAdUsersProcessed; }
    public void setTotalAdUsersProcessed(Integer totalAdUsersProcessed) { this.totalAdUsersProcessed = totalAdUsersProcessed; }

    public String getErrors() { return errors; }
    public void setErrors(String errors) { this.errors = errors; }

    public String getTriggeredBy() { return triggeredBy; }
    public void setTriggeredBy(String triggeredBy) { this.triggeredBy = triggeredBy; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
