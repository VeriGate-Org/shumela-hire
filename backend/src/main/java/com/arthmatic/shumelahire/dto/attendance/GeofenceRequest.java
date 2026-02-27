package com.arthmatic.shumelahire.dto.attendance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class GeofenceRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private BigDecimal centerLatitude;

    @NotNull
    private BigDecimal centerLongitude;

    @NotNull
    private Integer radiusMeters;

    private String address;
    private String city;
    private String province;
    private String siteCode;
    private String geofenceType;
    private String polygonCoordinates;
    private Boolean enforceOnClockIn;
    private Boolean enforceOnClockOut;
    private Boolean allowOverrideWithReason;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getCenterLatitude() { return centerLatitude; }
    public void setCenterLatitude(BigDecimal centerLatitude) { this.centerLatitude = centerLatitude; }

    public BigDecimal getCenterLongitude() { return centerLongitude; }
    public void setCenterLongitude(BigDecimal centerLongitude) { this.centerLongitude = centerLongitude; }

    public Integer getRadiusMeters() { return radiusMeters; }
    public void setRadiusMeters(Integer radiusMeters) { this.radiusMeters = radiusMeters; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getSiteCode() { return siteCode; }
    public void setSiteCode(String siteCode) { this.siteCode = siteCode; }

    public String getGeofenceType() { return geofenceType; }
    public void setGeofenceType(String geofenceType) { this.geofenceType = geofenceType; }

    public String getPolygonCoordinates() { return polygonCoordinates; }
    public void setPolygonCoordinates(String polygonCoordinates) { this.polygonCoordinates = polygonCoordinates; }

    public Boolean getEnforceOnClockIn() { return enforceOnClockIn; }
    public void setEnforceOnClockIn(Boolean enforceOnClockIn) { this.enforceOnClockIn = enforceOnClockIn; }

    public Boolean getEnforceOnClockOut() { return enforceOnClockOut; }
    public void setEnforceOnClockOut(Boolean enforceOnClockOut) { this.enforceOnClockOut = enforceOnClockOut; }

    public Boolean getAllowOverrideWithReason() { return allowOverrideWithReason; }
    public void setAllowOverrideWithReason(Boolean allowOverrideWithReason) { this.allowOverrideWithReason = allowOverrideWithReason; }
}
