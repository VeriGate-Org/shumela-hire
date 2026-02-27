package com.arthmatic.shumelahire.dto.attendance;

import com.arthmatic.shumelahire.entity.ShiftSwapRequest;

import java.time.LocalDateTime;

public class ShiftSwapResponse {

    private Long id;
    private Long requesterEmployeeId;
    private String requesterEmployeeName;
    private Long targetEmployeeId;
    private String targetEmployeeName;
    private Long requesterScheduleId;
    private Long targetScheduleId;
    private String status;
    private String reason;
    private String rejectionReason;
    private Long approvedById;
    private String approvedByName;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;

    public ShiftSwapResponse() {}

    public static ShiftSwapResponse fromEntity(ShiftSwapRequest s) {
        ShiftSwapResponse r = new ShiftSwapResponse();
        r.setId(s.getId());
        r.setRequesterEmployeeId(s.getRequesterEmployee().getId());
        r.setRequesterEmployeeName(s.getRequesterEmployee().getFullName());
        r.setTargetEmployeeId(s.getTargetEmployee().getId());
        r.setTargetEmployeeName(s.getTargetEmployee().getFullName());
        r.setRequesterScheduleId(s.getRequesterSchedule().getId());
        r.setTargetScheduleId(s.getTargetSchedule().getId());
        r.setStatus(s.getStatus().name());
        r.setReason(s.getReason());
        r.setRejectionReason(s.getRejectionReason());
        r.setApprovedAt(s.getApprovedAt());
        r.setCreatedAt(s.getCreatedAt());

        if (s.getApprovedBy() != null) {
            r.setApprovedById(s.getApprovedBy().getId());
            r.setApprovedByName(s.getApprovedBy().getFullName());
        }
        return r;
    }

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

    public Long getTargetScheduleId() { return targetScheduleId; }
    public void setTargetScheduleId(Long targetScheduleId) { this.targetScheduleId = targetScheduleId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public Long getApprovedById() { return approvedById; }
    public void setApprovedById(Long approvedById) { this.approvedById = approvedById; }

    public String getApprovedByName() { return approvedByName; }
    public void setApprovedByName(String approvedByName) { this.approvedByName = approvedByName; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
