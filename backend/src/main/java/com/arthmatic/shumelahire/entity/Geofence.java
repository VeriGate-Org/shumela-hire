package com.arthmatic.shumelahire.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "geofences")
public class Geofence extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(name = "center_latitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal centerLatitude;

    @NotNull
    @Column(name = "center_longitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal centerLongitude;

    @NotNull
    @Column(name = "radius_meters", nullable = false)
    private Integer radiusMeters;

    @NotBlank
    @Column(name = "address", length = 500)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String province;

    @Column(name = "site_code", length = 50)
    private String siteCode;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "geofence_type", nullable = false, length = 30)
    private GeofenceType geofenceType = GeofenceType.CIRCLE;

    @Column(name = "polygon_coordinates", columnDefinition = "TEXT")
    private String polygonCoordinates;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "enforce_on_clock_in", nullable = false)
    private Boolean enforceOnClockIn = true;

    @Column(name = "enforce_on_clock_out", nullable = false)
    private Boolean enforceOnClockOut = false;

    @Column(name = "allow_override_with_reason", nullable = false)
    private Boolean allowOverrideWithReason = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum GeofenceType {
        CIRCLE, POLYGON
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public GeofenceType getGeofenceType() { return geofenceType; }
    public void setGeofenceType(GeofenceType geofenceType) { this.geofenceType = geofenceType; }

    public String getPolygonCoordinates() { return polygonCoordinates; }
    public void setPolygonCoordinates(String polygonCoordinates) { this.polygonCoordinates = polygonCoordinates; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Boolean getEnforceOnClockIn() { return enforceOnClockIn; }
    public void setEnforceOnClockIn(Boolean enforceOnClockIn) { this.enforceOnClockIn = enforceOnClockIn; }

    public Boolean getEnforceOnClockOut() { return enforceOnClockOut; }
    public void setEnforceOnClockOut(Boolean enforceOnClockOut) { this.enforceOnClockOut = enforceOnClockOut; }

    public Boolean getAllowOverrideWithReason() { return allowOverrideWithReason; }
    public void setAllowOverrideWithReason(Boolean allowOverrideWithReason) { this.allowOverrideWithReason = allowOverrideWithReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
