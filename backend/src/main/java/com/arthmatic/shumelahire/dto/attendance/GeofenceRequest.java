package com.arthmatic.shumelahire.dto.attendance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class GeofenceRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String site;

    @NotNull(message = "Geofence type is required")
    private String geofenceType;

    private Double latitude;
    private Double longitude;
    private Double radiusMeters;
    private String polygonCoordinates;
    private Boolean isActive = true;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSite() { return site; }
    public void setSite(String site) { this.site = site; }

    public String getGeofenceType() { return geofenceType; }
    public void setGeofenceType(String geofenceType) { this.geofenceType = geofenceType; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getRadiusMeters() { return radiusMeters; }
    public void setRadiusMeters(Double radiusMeters) { this.radiusMeters = radiusMeters; }

    public String getPolygonCoordinates() { return polygonCoordinates; }
    public void setPolygonCoordinates(String polygonCoordinates) { this.polygonCoordinates = polygonCoordinates; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
