package com.arthmatic.shumelahire.dto.attendance;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ClockOutRequest {

    @NotNull
    private Long attendanceRecordId;

    private BigDecimal latitude;
    private BigDecimal longitude;
    private String clockMethod;
    private String ipAddress;
    private String deviceInfo;
    private String notes;

    public Long getAttendanceRecordId() { return attendanceRecordId; }
    public void setAttendanceRecordId(Long attendanceRecordId) { this.attendanceRecordId = attendanceRecordId; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public String getClockMethod() { return clockMethod; }
    public void setClockMethod(String clockMethod) { this.clockMethod = clockMethod; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
