package com.arthmatic.shumelahire.dto.attendance;

import com.arthmatic.shumelahire.entity.attendance.ShiftSwapRequest;

import java.time.LocalDateTime;

public class ShiftSwapResponse {

    private Long id;
    private Long requesterEmployeeId;
    private String requesterEmployeeName;
    private Long targetEmployeeId;
    private String targetEmployeeName;
    private Long requesterScheduleId;
    private String requesterScheduleDate;
    private Long targetScheduleId;
    private String targetScheduleDate;
    private String status;
    private String reason;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ShiftSwapResponse fromEntity(ShiftSwapRequest swap) {
        ShiftSwapResponse response = new ShiftSwapResponse();
        response.setId(swap.getId());
        response.setRequesterEmployeeId(swap.getRequesterEmployee().getId());
        response.setRequesterEmployeeName(swap.getRequesterEmployee().getFullName());
        response.setTargetEmployeeId(swap.getTargetEmployee().getId());
        response.setTargetEmployeeName(swap.getTargetEmployee().getFullName());
        response.setRequesterScheduleId(swap.getRequesterSchedule().getId());
        response.setRequesterScheduleDate(swap.getRequesterSchedule().getScheduleDate().toString());
        response.setTargetScheduleId(swap.getTargetSchedule().getId());
        response.setTargetScheduleDate(swap.getTargetSchedule().getScheduleDate().toString());
        response.setStatus(swap.getStatus().name());
        response.setReason(swap.getReason());
        response.setApprovedBy(swap.getApprovedBy());
        response.setApprovedAt(swap.getApprovedAt());
        response.setRejectionReason(swap.getRejectionReason());
        response.setCreatedAt(swap.getCreatedAt());
        response.setUpdatedAt(swap.getUpdatedAt());
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRequesterEmployeeId() { return requesterEmployeeId; }
    public void setRequesterEmployeeId(Long requesterEmployeeId) { this.requesterEmployeeId = requesterEmployeeId; }

    public String getRequesterEmployeeName() { return requesterEmployeeName; }
    public void setRequesterEmployeeName(String requesterEmployeeName) { this.requesterEmployeeName = requesterEmployeeName; }

    public Long getTargetEmployeeId() { return targetEmployeeId; }
    public void setTargetEmployeeId(Long targetEmployeeId) { this.targetEmployeeId = targetEmployeeId; }

    public String getTargetEmployeeName() { return targetEmployeeName; }
    public void setTargetEmployeeName(String targetEmployeeName) { this.targetEmployeeName = targetEmployeeName; }

    public Long getRequesterScheduleId() { return requesterScheduleId; }
    public void setRequesterScheduleId(Long requesterScheduleId) { this.requesterScheduleId = requesterScheduleId; }

    public String getRequesterScheduleDate() { return requesterScheduleDate; }
    public void setRequesterScheduleDate(String requesterScheduleDate) { this.requesterScheduleDate = requesterScheduleDate; }

    public Long getTargetScheduleId() { return targetScheduleId; }
    public void setTargetScheduleId(Long targetScheduleId) { this.targetScheduleId = targetScheduleId; }

    public String getTargetScheduleDate() { return targetScheduleDate; }
    public void setTargetScheduleDate(String targetScheduleDate) { this.targetScheduleDate = targetScheduleDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
