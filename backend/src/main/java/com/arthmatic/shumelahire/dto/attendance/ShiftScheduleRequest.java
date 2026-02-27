package com.arthmatic.shumelahire.dto.attendance;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class ShiftScheduleRequest {

    @NotNull
    private Long employeeId;
    @NotNull
    private Long shiftId;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    private String notes;

    // For bulk assignment
    private List<Long> employeeIds;

    public ShiftScheduleRequest() {}

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public Long getShiftId() { return shiftId; }
    public void setShiftId(Long shiftId) { this.shiftId = shiftId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<Long> getEmployeeIds() { return employeeIds; }
    public void setEmployeeIds(List<Long> employeeIds) { this.employeeIds = employeeIds; }
}
