package com.arthmatic.shumelahire.dto.attendance;

import com.arthmatic.shumelahire.entity.Geofence;

import java.time.LocalDateTime;

public class GeofenceResponse {

    private Long id;
    private String name;
    private String site;
    private String type;
    private Double latitude;
    private Double longitude;
    private Double radiusMeters;
    private String polygonCoords;
    private Boolean active;
    private LocalDateTime createdAt;

    public GeofenceResponse() {}

    public static GeofenceResponse fromEntity(Geofence g) {
        GeofenceResponse r = new GeofenceResponse();
        r.setId(g.getId());
        r.setName(g.getName());
        r.setSite(g.getSite());
        r.setType(g.getType().name());
        r.setLatitude(g.getLatitude());
        r.setLongitude(g.getLongitude());
        r.setRadiusMeters(g.getRadiusMeters());
        r.setPolygonCoords(g.getPolygonCoords());
        r.setActive(g.getActive());
        r.setCreatedAt(g.getCreatedAt());
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
