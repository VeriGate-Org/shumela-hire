package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.dto.attendance.GeofenceRequest;
import com.arthmatic.shumelahire.dto.attendance.GeofenceResponse;
import com.arthmatic.shumelahire.entity.Geofence;
import com.arthmatic.shumelahire.repository.GeofenceRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GeofenceService {

    private static final Logger logger = LoggerFactory.getLogger(GeofenceService.class);
    private static final double EARTH_RADIUS_METERS = 6_371_000.0;

    @Autowired
    private GeofenceRepository geofenceRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public GeofenceResponse createGeofence(GeofenceRequest request) {
        Geofence geofence = new Geofence();
        geofence.setName(request.getName());
        geofence.setSite(request.getSite());
        geofence.setType(Geofence.GeofenceType.valueOf(request.getType()));
        geofence.setLatitude(request.getLatitude());
        geofence.setLongitude(request.getLongitude());
        geofence.setRadiusMeters(request.getRadiusMeters());
        geofence.setPolygonCoords(request.getPolygonCoords());
        geofence.setActive(true);

        Geofence saved = geofenceRepository.save(geofence);
        logger.info("Created geofence: {} ({})", saved.getName(), saved.getType());
        return GeofenceResponse.fromEntity(saved);
    }

    public GeofenceResponse updateGeofence(Long id, GeofenceRequest request) {
        Geofence geofence = geofenceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Geofence not found: " + id));
        geofence.setName(request.getName());
        geofence.setSite(request.getSite());
        geofence.setType(Geofence.GeofenceType.valueOf(request.getType()));
        geofence.setLatitude(request.getLatitude());
        geofence.setLongitude(request.getLongitude());
        geofence.setRadiusMeters(request.getRadiusMeters());
        geofence.setPolygonCoords(request.getPolygonCoords());

        return GeofenceResponse.fromEntity(geofenceRepository.save(geofence));
    }

    @Transactional(readOnly = true)
    public GeofenceResponse getGeofence(Long id) {
        return geofenceRepository.findById(id)
                .map(GeofenceResponse::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Geofence not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<GeofenceResponse> getAllActiveGeofences() {
        return geofenceRepository.findByActiveTrue().stream()
                .map(GeofenceResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GeofenceResponse> getGeofencesBySite(String site) {
        return geofenceRepository.findBySite(site).stream()
                .map(GeofenceResponse::fromEntity)
                .toList();
    }

    public void deactivateGeofence(Long id) {
        Geofence geofence = geofenceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Geofence not found: " + id));
        geofence.setActive(false);
        geofenceRepository.save(geofence);
    }

    /**
     * Validates whether a GPS point is within the given geofence.
     */
    public boolean isWithinGeofence(Long geofenceId, double lat, double lng) {
        Geofence geofence = geofenceRepository.findById(geofenceId)
                .orElseThrow(() -> new IllegalArgumentException("Geofence not found: " + geofenceId));
        return isWithinGeofence(geofence, lat, lng);
    }

    public boolean isWithinGeofence(Geofence geofence, double lat, double lng) {
        if (!geofence.getActive()) return false;

        return switch (geofence.getType()) {
            case RADIUS -> isWithinRadius(geofence.getLatitude(), geofence.getLongitude(),
                    geofence.getRadiusMeters(), lat, lng);
            case POLYGON -> isWithinPolygon(geofence.getPolygonCoords(), lat, lng);
        };
    }

    /**
     * Haversine distance check for RADIUS geofences.
     */
    static boolean isWithinRadius(double centerLat, double centerLng, double radiusMeters,
                                  double pointLat, double pointLng) {
        double distance = haversineDistance(centerLat, centerLng, pointLat, pointLng);
        return distance <= radiusMeters;
    }

    /**
     * Haversine formula — great-circle distance between two GPS points.
     */
    static double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_METERS * c;
    }

    /**
     * Ray-casting point-in-polygon algorithm for POLYGON geofences.
     * polygonCoords is a JSON array of [lat, lng] pairs:
     * [[lat1,lng1],[lat2,lng2],...]
     */
    boolean isWithinPolygon(String polygonCoordsJson, double lat, double lng) {
        try {
            List<List<Double>> coords = objectMapper.readValue(polygonCoordsJson,
                    new TypeReference<List<List<Double>>>() {});
            return pointInPolygon(coords, lat, lng);
        } catch (Exception e) {
            logger.error("Failed to parse polygon coordinates", e);
            return false;
        }
    }

    /**
     * Ray-casting algorithm to determine if point is inside polygon.
     */
    static boolean pointInPolygon(List<List<Double>> polygon, double lat, double lng) {
        int n = polygon.size();
        if (n < 3) return false;

        boolean inside = false;
        for (int i = 0, j = n - 1; i < n; j = i++) {
            double yi = polygon.get(i).get(0);
            double xi = polygon.get(i).get(1);
            double yj = polygon.get(j).get(0);
            double xj = polygon.get(j).get(1);

            if (((yi > lat) != (yj > lat)) &&
                (lng < (xj - xi) * (lat - yi) / (yj - yi) + xi)) {
                inside = !inside;
            }
        }
        return inside;
    }
}
