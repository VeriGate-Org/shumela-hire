package com.arthmatic.shumelahire.dto.attendance;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class ShiftScheduleRequest {

    @NotNull
    private Long shiftId;

    @NotNull
    private List<Long> employeeIds;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private Long shiftPatternId;
    private String notes;
    private Boolean publish;

    public Long getShiftId() { return shiftId; }
    public void setShiftId(Long shiftId) { this.shiftId = shiftId; }

    public List<Long> getEmployeeIds() { return employeeIds; }
    public void setEmployeeIds(List<Long> employeeIds) { this.employeeIds = employeeIds; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Long getShiftPatternId() { return shiftPatternId; }
    public void setShiftPatternId(Long shiftPatternId) { this.shiftPatternId = shiftPatternId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Boolean getPublish() { return publish; }
    public void setPublish(Boolean publish) { this.publish = publish; }
}
