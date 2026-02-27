package com.arthmatic.shumelahire.dto.attendance;

import com.arthmatic.shumelahire.entity.ShiftSchedule;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ShiftScheduleResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private Long shiftId;
    private String shiftName;
    private String shiftCode;
    private LocalDate scheduleDate;
    private String status;
    private Long shiftPatternId;
    private String shiftPatternName;
    private String notes;
    private Boolean isPublished;
    private LocalDateTime createdAt;

    public static ShiftScheduleResponse fromEntity(ShiftSchedule s) {
        ShiftScheduleResponse r = new ShiftScheduleResponse();
        r.id = s.getId();
        r.employeeId = s.getEmployee().getId();
        r.employeeName = s.getEmployee().getFullName();
        r.shiftId = s.getShift().getId();
        r.shiftName = s.getShift().getName();
        r.shiftCode = s.getShift().getCode();
        r.scheduleDate = s.getScheduleDate();
        r.status = s.getStatus().name();
        r.shiftPatternId = s.getShiftPattern() != null ? s.getShiftPattern().getId() : null;
        r.shiftPatternName = s.getShiftPattern() != null ? s.getShiftPattern().getName() : null;
        r.notes = s.getNotes();
        r.isPublished = s.getIsPublished();
        r.createdAt = s.getCreatedAt();
        return r;
    }

    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public Long getShiftId() { return shiftId; }
    public String getShiftName() { return shiftName; }
    public String getShiftCode() { return shiftCode; }
    public LocalDate getScheduleDate() { return scheduleDate; }
    public String getStatus() { return status; }
    public Long getShiftPatternId() { return shiftPatternId; }
    public String getShiftPatternName() { return shiftPatternName; }
    public String getNotes() { return notes; }
    public Boolean getIsPublished() { return isPublished; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
