package com.arthmatic.shumelahire.dto.attendance;

import com.arthmatic.shumelahire.entity.ShiftSwapRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ShiftSwapResponse {

    private Long id;
    private Long requesterId;
    private String requesterName;
    private Long requesterScheduleId;
    private Long targetEmployeeId;
    private String targetEmployeeName;
    private Long targetScheduleId;
    private LocalDate swapDate;
    private LocalDate targetDate;
    private String status;
    private String reason;
    private LocalDateTime targetResponseAt;
    private String targetResponseNotes;
    private String managerApprovedByName;
    private LocalDateTime managerApprovedAt;
    private String managerNotes;
    private LocalDateTime createdAt;

    public static ShiftSwapResponse fromEntity(ShiftSwapRequest s) {
        ShiftSwapResponse r = new ShiftSwapResponse();
        r.id = s.getId();
        r.requesterId = s.getRequester().getId();
        r.requesterName = s.getRequester().getFullName();
        r.requesterScheduleId = s.getRequesterSchedule().getId();
        r.targetEmployeeId = s.getTargetEmployee().getId();
        r.targetEmployeeName = s.getTargetEmployee().getFullName();
        r.targetScheduleId = s.getTargetSchedule() != null ? s.getTargetSchedule().getId() : null;
        r.swapDate = s.getSwapDate();
        r.targetDate = s.getTargetDate();
        r.status = s.getStatus().name();
        r.reason = s.getReason();
        r.targetResponseAt = s.getTargetResponseAt();
        r.targetResponseNotes = s.getTargetResponseNotes();
        r.managerApprovedByName = s.getManagerApprovedBy() != null ? s.getManagerApprovedBy().getFullName() : null;
        r.managerApprovedAt = s.getManagerApprovedAt();
        r.managerNotes = s.getManagerNotes();
        r.createdAt = s.getCreatedAt();
        return r;
    }

    public Long getId() { return id; }
    public Long getRequesterId() { return requesterId; }
    public String getRequesterName() { return requesterName; }
    public Long getRequesterScheduleId() { return requesterScheduleId; }
    public Long getTargetEmployeeId() { return targetEmployeeId; }
    public String getTargetEmployeeName() { return targetEmployeeName; }
    public Long getTargetScheduleId() { return targetScheduleId; }
    public LocalDate getSwapDate() { return swapDate; }
    public LocalDate getTargetDate() { return targetDate; }
    public String getStatus() { return status; }
    public String getReason() { return reason; }
    public LocalDateTime getTargetResponseAt() { return targetResponseAt; }
    public String getTargetResponseNotes() { return targetResponseNotes; }
    public String getManagerApprovedByName() { return managerApprovedByName; }
    public LocalDateTime getManagerApprovedAt() { return managerApprovedAt; }
    public String getManagerNotes() { return managerNotes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
