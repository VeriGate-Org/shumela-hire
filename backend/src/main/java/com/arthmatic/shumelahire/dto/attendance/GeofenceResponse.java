package com.arthmatic.shumelahire.dto.attendance;

import com.arthmatic.shumelahire.entity.Geofence;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GeofenceResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal centerLatitude;
    private BigDecimal centerLongitude;
    private Integer radiusMeters;
    private String address;
    private String city;
    private String province;
    private String siteCode;
    private String geofenceType;
    private String polygonCoordinates;
    private Boolean isActive;
    private Boolean enforceOnClockIn;
    private Boolean enforceOnClockOut;
    private Boolean allowOverrideWithReason;
    private LocalDateTime createdAt;

    public static GeofenceResponse fromEntity(Geofence g) {
        GeofenceResponse r = new GeofenceResponse();
        r.id = g.getId();
        r.name = g.getName();
        r.description = g.getDescription();
        r.centerLatitude = g.getCenterLatitude();
        r.centerLongitude = g.getCenterLongitude();
        r.radiusMeters = g.getRadiusMeters();
        r.address = g.getAddress();
        r.city = g.getCity();
        r.province = g.getProvince();
        r.siteCode = g.getSiteCode();
        r.geofenceType = g.getGeofenceType().name();
        r.polygonCoordinates = g.getPolygonCoordinates();
        r.isActive = g.getIsActive();
        r.enforceOnClockIn = g.getEnforceOnClockIn();
        r.enforceOnClockOut = g.getEnforceOnClockOut();
        r.allowOverrideWithReason = g.getAllowOverrideWithReason();
        r.createdAt = g.getCreatedAt();
        return r;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getCenterLatitude() { return centerLatitude; }
    public BigDecimal getCenterLongitude() { return centerLongitude; }
    public Integer getRadiusMeters() { return radiusMeters; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getProvince() { return province; }
    public String getSiteCode() { return siteCode; }
    public String getGeofenceType() { return geofenceType; }
    public String getPolygonCoordinates() { return polygonCoordinates; }
    public Boolean getIsActive() { return isActive; }
    public Boolean getEnforceOnClockIn() { return enforceOnClockIn; }
    public Boolean getEnforceOnClockOut() { return enforceOnClockOut; }
    public Boolean getAllowOverrideWithReason() { return allowOverrideWithReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
