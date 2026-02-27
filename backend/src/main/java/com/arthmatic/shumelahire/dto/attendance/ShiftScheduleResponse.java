package com.arthmatic.shumelahire.dto.attendance;

import com.arthmatic.shumelahire.entity.ShiftSchedule;

import java.time.LocalDate;

public class ShiftScheduleResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private Long shiftId;
    private String shiftName;
    private LocalDate scheduleDate;
    private String status;
    private String notes;

    public ShiftScheduleResponse() {}

    public static ShiftScheduleResponse fromEntity(ShiftSchedule s) {
        ShiftScheduleResponse r = new ShiftScheduleResponse();
        r.setId(s.getId());
        r.setEmployeeId(s.getEmployee().getId());
        r.setEmployeeName(s.getEmployee().getFullName());
        r.setShiftId(s.getShift().getId());
        r.setShiftName(s.getShift().getName());
        r.setScheduleDate(s.getScheduleDate());
        r.setStatus(s.getStatus().name());
        r.setNotes(s.getNotes());
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public Long getShiftId() { return shiftId; }
    public void setShiftId(Long shiftId) { this.shiftId = shiftId; }

    public String getShiftName() { return shiftName; }
    public void setShiftName(String shiftName) { this.shiftName = shiftName; }

    public LocalDate getScheduleDate() { return scheduleDate; }
    public void setScheduleDate(LocalDate scheduleDate) { this.scheduleDate = scheduleDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
