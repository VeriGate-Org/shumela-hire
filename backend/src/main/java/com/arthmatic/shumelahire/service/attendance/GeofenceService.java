package com.arthmatic.shumelahire.service.attendance;

import com.arthmatic.shumelahire.dto.attendance.GeofenceRequest;
import com.arthmatic.shumelahire.dto.attendance.GeofenceResponse;
import com.arthmatic.shumelahire.entity.Geofence;
import com.arthmatic.shumelahire.repository.GeofenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GeofenceService {

    private static final Logger logger = LoggerFactory.getLogger(GeofenceService.class);
    private static final double EARTH_RADIUS_METERS = 6_371_000.0;

    private final GeofenceRepository geofenceRepository;

    public GeofenceService(GeofenceRepository geofenceRepository) {
        this.geofenceRepository = geofenceRepository;
    }

    public GeofenceResponse create(GeofenceRequest request) {
        Geofence geofence = new Geofence();
        mapRequestToEntity(request, geofence);
        geofence = geofenceRepository.save(geofence);
        logger.info("Geofence created: {} (radius: {}m)", geofence.getName(), geofence.getRadiusMeters());
        return GeofenceResponse.fromEntity(geofence);
    }

    public GeofenceResponse update(Long id, GeofenceRequest request) {
        Geofence geofence = findEntityById(id);
        mapRequestToEntity(request, geofence);
        geofence = geofenceRepository.save(geofence);
        logger.info("Geofence updated: {}", geofence.getName());
        return GeofenceResponse.fromEntity(geofence);
    }

    @Transactional(readOnly = true)
    public GeofenceResponse getById(Long id) {
        return GeofenceResponse.fromEntity(findEntityById(id));
    }

    @Transactional(readOnly = true)
    public List<GeofenceResponse> getAll() {
        return geofenceRepository.findAll().stream()
                .map(GeofenceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GeofenceResponse> getActive() {
        return geofenceRepository.findByIsActiveTrue().stream()
                .map(GeofenceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public GeofenceResponse toggleActive(Long id, boolean active) {
        Geofence geofence = findEntityById(id);
        geofence.setIsActive(active);
        geofence = geofenceRepository.save(geofence);
        logger.info("Geofence {} {}", geofence.getName(), active ? "activated" : "deactivated");
        return GeofenceResponse.fromEntity(geofence);
    }

    public void delete(Long id) {
        Geofence geofence = findEntityById(id);
        geofenceRepository.delete(geofence);
        logger.info("Geofence deleted: {}", geofence.getName());
    }

    /**
     * Check if a coordinate is within a geofence using the Haversine formula.
     */
    public boolean isWithinGeofence(Long geofenceId, BigDecimal latitude, BigDecimal longitude) {
        Geofence geofence = findEntityById(geofenceId);
        return isWithinGeofence(geofence, latitude, longitude);
    }

    public boolean isWithinGeofence(Geofence geofence, BigDecimal latitude, BigDecimal longitude) {
        if (latitude == null || longitude == null) {
            return false;
        }

        if (geofence.getGeofenceType() == Geofence.GeofenceType.POLYGON) {
            return isWithinPolygon(geofence, latitude.doubleValue(), longitude.doubleValue());
        }

        double distance = haversineDistance(
                geofence.getCenterLatitude().doubleValue(),
                geofence.getCenterLongitude().doubleValue(),
                latitude.doubleValue(),
                longitude.doubleValue()
        );
        return distance <= geofence.getRadiusMeters();
    }

    /**
     * Haversine formula to calculate great-circle distance between two points.
     * Returns distance in meters.
     */
    public static double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_METERS * c;
    }

    /**
     * Ray-casting algorithm for point-in-polygon test.
     * Expects polygonCoordinates as JSON array: [[lat1,lon1],[lat2,lon2],...]
     */
    private boolean isWithinPolygon(Geofence geofence, double lat, double lon) {
        String coords = geofence.getPolygonCoordinates();
        if (coords == null || coords.isBlank()) {
            return false;
        }

        try {
            // Simple parsing of [[lat,lon],[lat,lon],...] format
            String cleaned = coords.replaceAll("[\\[\\]\\s]", "");
            String[] parts = cleaned.split(",");
            if (parts.length < 6) return false; // need at least 3 points

            int n = parts.length / 2;
            double[] lats = new double[n];
            double[] lons = new double[n];
            for (int i = 0; i < n; i++) {
                lats[i] = Double.parseDouble(parts[i * 2]);
                lons[i] = Double.parseDouble(parts[i * 2 + 1]);
            }

            boolean inside = false;
            for (int i = 0, j = n - 1; i < n; j = i++) {
                if ((lons[i] > lon) != (lons[j] > lon)
                        && lat < (lats[j] - lats[i]) * (lon - lons[i]) / (lons[j] - lons[i]) + lats[i]) {
                    inside = !inside;
                }
            }
            return inside;
        } catch (Exception e) {
            logger.warn("Failed to parse polygon coordinates for geofence {}: {}", geofence.getId(), e.getMessage());
            return false;
        }
    }

    private Geofence findEntityById(Long id) {
        return geofenceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Geofence not found with id: " + id));
    }

    private void mapRequestToEntity(GeofenceRequest request, Geofence geofence) {
        geofence.setName(request.getName());
        geofence.setDescription(request.getDescription());
        geofence.setCenterLatitude(request.getCenterLatitude());
        geofence.setCenterLongitude(request.getCenterLongitude());
        geofence.setRadiusMeters(request.getRadiusMeters());
        geofence.setAddress(request.getAddress());
        geofence.setCity(request.getCity());
        geofence.setProvince(request.getProvince());
        geofence.setSiteCode(request.getSiteCode());
        if (request.getGeofenceType() != null) {
            geofence.setGeofenceType(Geofence.GeofenceType.valueOf(request.getGeofenceType()));
        }
        geofence.setPolygonCoordinates(request.getPolygonCoordinates());
        if (request.getEnforceOnClockIn() != null) geofence.setEnforceOnClockIn(request.getEnforceOnClockIn());
        if (request.getEnforceOnClockOut() != null) geofence.setEnforceOnClockOut(request.getEnforceOnClockOut());
        if (request.getAllowOverrideWithReason() != null) geofence.setAllowOverrideWithReason(request.getAllowOverrideWithReason());
    }
}
