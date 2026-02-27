package com.arthmatic.shumelahire.dto.ad;

import com.arthmatic.shumelahire.entity.ADSyncLog;

import java.time.LocalDateTime;

public class ADSyncLogResponse {

    private Long id;
    private String syncType;
    private String status;
    private Integer usersCreated;
    private Integer usersUpdated;
    private Integer usersDisabled;
    private Integer usersSkipped;
    private Integer totalAdUsersProcessed;
    private String errors;
    private String triggeredBy;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Long durationMs;

    public static ADSyncLogResponse from(ADSyncLog log) {
        ADSyncLogResponse response = new ADSyncLogResponse();
        response.setId(log.getId());
        response.setSyncType(log.getSyncType().name());
        response.setStatus(log.getStatus().name());
        response.setUsersCreated(log.getUsersCreated());
        response.setUsersUpdated(log.getUsersUpdated());
        response.setUsersDisabled(log.getUsersDisabled());
        response.setUsersSkipped(log.getUsersSkipped());
        response.setTotalAdUsersProcessed(log.getTotalAdUsersProcessed());
        response.setErrors(log.getErrors());
        response.setTriggeredBy(log.getTriggeredBy());
        response.setStartedAt(log.getStartedAt());
        response.setCompletedAt(log.getCompletedAt());
        response.setDurationMs(log.getDurationMs());
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSyncType() { return syncType; }
    public void setSyncType(String syncType) { this.syncType = syncType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

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
}
