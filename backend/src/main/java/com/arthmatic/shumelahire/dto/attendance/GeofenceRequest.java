package com.arthmatic.shumelahire.dto.attendance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class GeofenceRequest {

    @NotBlank
    private String name;
    private String site;
    @NotNull
    private String type;
    private Double latitude;
    private Double longitude;
    private Double radiusMeters;
    private String polygonCoords;

    public GeofenceRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSite() { return site; }
    public void setSite(String site) { this.site = site; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getRadiusMeters() { return radiusMeters; }
    public void setRadiusMeters(Double radiusMeters) { this.radiusMeters = radiusMeters; }

    public String getPolygonCoords() { return polygonCoords; }
    public void setPolygonCoords(String polygonCoords) { this.polygonCoords = polygonCoords; }
}
