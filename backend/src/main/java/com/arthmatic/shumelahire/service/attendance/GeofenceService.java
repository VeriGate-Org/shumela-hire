package com.arthmatic.shumelahire.service.attendance;

import com.arthmatic.shumelahire.dto.attendance.GeofenceRequest;
import com.arthmatic.shumelahire.dto.attendance.GeofenceResponse;
import com.arthmatic.shumelahire.entity.attendance.Geofence;
import com.arthmatic.shumelahire.entity.attendance.GeofenceType;
import com.arthmatic.shumelahire.repository.attendance.GeofenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GeofenceService {

    private static final Logger logger = LoggerFactory.getLogger(GeofenceService.class);
    private static final double EARTH_RADIUS_METERS = 6_371_000.0;

    @Autowired
    private GeofenceRepository geofenceRepository;

    public GeofenceResponse createGeofence(GeofenceRequest request) {
        logger.info("Creating geofence: {}", request.getName());

        Geofence geofence = new Geofence();
        geofence.setName(request.getName());
        geofence.setSite(request.getSite());
        geofence.setGeofenceType(GeofenceType.valueOf(request.getGeofenceType()));
        geofence.setLatitude(request.getLatitude());
        geofence.setLongitude(request.getLongitude());
        geofence.setRadiusMeters(request.getRadiusMeters());
        geofence.setPolygonCoordinates(request.getPolygonCoordinates());
        geofence.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        Geofence saved = geofenceRepository.save(geofence);
        logger.info("Geofence created: {} (id={})", saved.getName(), saved.getId());
        return GeofenceResponse.fromEntity(saved);
    }

    public GeofenceResponse updateGeofence(Long id, GeofenceRequest request) {
        logger.info("Updating geofence: {}", id);

        Geofence geofence = findById(id);
        geofence.setName(request.getName());
        geofence.setSite(request.getSite());
        geofence.setGeofenceType(GeofenceType.valueOf(request.getGeofenceType()));
        geofence.setLatitude(request.getLatitude());
        geofence.setLongitude(request.getLongitude());
        geofence.setRadiusMeters(request.getRadiusMeters());
        geofence.setPolygonCoordinates(request.getPolygonCoordinates());
        if (request.getIsActive() != null) geofence.setIsActive(request.getIsActive());

        Geofence saved = geofenceRepository.save(geofence);
        return GeofenceResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public GeofenceResponse getGeofence(Long id) {
        return GeofenceResponse.fromEntity(findById(id));
    }

    @Transactional(readOnly = true)
    public List<GeofenceResponse> getAllGeofences() {
        return geofenceRepository.findAll().stream()
                .map(GeofenceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GeofenceResponse> getActiveGeofences() {
        return geofenceRepository.findAll().stream()
                .filter(g -> Boolean.TRUE.equals(g.getIsActive()))
                .map(GeofenceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public void deleteGeofence(Long id) {
        Geofence geofence = findById(id);
        geofence.setIsActive(false);
        geofenceRepository.save(geofence);
        logger.info("Geofence deactivated: {}", id);
    }

    /**
     * Check if a GPS coordinate is within any active geofence.
     * Returns the matching geofence or null if not within any.
     */
    @Transactional(readOnly = true)
    public Geofence findContainingGeofence(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return null;
        }

        List<Geofence> activeGeofences = geofenceRepository.findAll().stream()
                .filter(g -> Boolean.TRUE.equals(g.getIsActive()))
                .collect(Collectors.toList());

        for (Geofence geofence : activeGeofences) {
            if (isWithinGeofence(geofence, latitude, longitude)) {
                return geofence;
            }
        }
        return null;
    }

    /**
     * Check if coordinates are within a specific geofence.
     */
    public boolean isWithinGeofence(Geofence geofence, double latitude, double longitude) {
        if (geofence.getGeofenceType() == GeofenceType.RADIUS) {
            return isWithinRadius(geofence, latitude, longitude);
        } else if (geofence.getGeofenceType() == GeofenceType.POLYGON) {
            return isWithinPolygon(geofence, latitude, longitude);
        }
        return false;
    }

    /**
     * Haversine formula to calculate distance between two GPS coordinates.
     * Returns distance in meters.
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_METERS * c;
    }

    private boolean isWithinRadius(Geofence geofence, double latitude, double longitude) {
        if (geofence.getLatitude() == null || geofence.getLongitude() == null || geofence.getRadiusMeters() == null) {
            return false;
        }
        double distance = calculateDistance(
                geofence.getLatitude(), geofence.getLongitude(),
                latitude, longitude
        );
        return distance <= geofence.getRadiusMeters();
    }

    /**
     * Ray-casting algorithm for point-in-polygon test.
     * Polygon coordinates stored as "lat1,lon1;lat2,lon2;lat3,lon3;..."
     */
    private boolean isWithinPolygon(Geofence geofence, double latitude, double longitude) {
        String coords = geofence.getPolygonCoordinates();
        if (coords == null || coords.isBlank()) {
            return false;
        }

        String[] points = coords.split(";");
        if (points.length < 3) {
            return false;
        }

        double[][] polygon = new double[points.length][2];
        for (int i = 0; i < points.length; i++) {
            String[] latLon = points[i].trim().split(",");
            if (latLon.length != 2) return false;
            polygon[i][0] = Double.parseDouble(latLon[0].trim());
            polygon[i][1] = Double.parseDouble(latLon[1].trim());
        }

        return raycastPointInPolygon(latitude, longitude, polygon);
    }

    /**
     * Ray-casting point-in-polygon algorithm.
     * Counts how many times a ray from the point crosses polygon edges.
     * Odd count = inside, even count = outside.
     */
    private boolean raycastPointInPolygon(double testLat, double testLon, double[][] polygon) {
        boolean inside = false;
        int n = polygon.length;

        for (int i = 0, j = n - 1; i < n; j = i++) {
            double yi = polygon[i][0], xi = polygon[i][1];
            double yj = polygon[j][0], xj = polygon[j][1];

            if ((yi > testLat) != (yj > testLat)
                    && testLon < (xj - xi) * (testLat - yi) / (yj - yi) + xi) {
                inside = !inside;
            }
        }
        return inside;
    }

    private Geofence findById(Long id) {
        return geofenceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Geofence not found: " + id));
    }
}
