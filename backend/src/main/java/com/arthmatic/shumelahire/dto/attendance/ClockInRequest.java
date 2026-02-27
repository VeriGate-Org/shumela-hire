package com.arthmatic.shumelahire.dto.attendance;

import jakarta.validation.constraints.NotNull;

public class ClockInRequest {

    @NotNull
    private Long employeeId;

    private Double latitude;
    private Double longitude;
    private Long geofenceId;
    private String notes;

    public ClockInRequest() {}

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Long getGeofenceId() { return geofenceId; }
    public void setGeofenceId(Long geofenceId) { this.geofenceId = geofenceId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
