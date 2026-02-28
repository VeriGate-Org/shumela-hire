package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.dto.attendance.GeofenceRequest;
import com.arthmatic.shumelahire.dto.attendance.GeofenceResponse;
import com.arthmatic.shumelahire.entity.Geofence;
import com.arthmatic.shumelahire.repository.GeofenceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeofenceServiceTest {

    @Mock
    private GeofenceRepository geofenceRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private GeofenceService geofenceService;

    private Geofence radiusGeofence;
    private Geofence polygonGeofence;

    @BeforeEach
    void setUp() {
        // uThukela Water office: Ladysmith, KZN (approx coords)
        radiusGeofence = new Geofence();
        radiusGeofence.setId(1L);
        radiusGeofence.setName("Ladysmith Office");
        radiusGeofence.setType(Geofence.GeofenceType.RADIUS);
        radiusGeofence.setLatitude(-28.5597);
        radiusGeofence.setLongitude(29.7789);
        radiusGeofence.setRadiusMeters(200.0);
        radiusGeofence.setActive(true);

        // Polygon geofence (simple square around Ladysmith)
        polygonGeofence = new Geofence();
        polygonGeofence.setId(2L);
        polygonGeofence.setName("Water Treatment Plant");
        polygonGeofence.setType(Geofence.GeofenceType.POLYGON);
        polygonGeofence.setPolygonCoords("[[-28.558,29.777],[-28.558,29.781],[-28.562,29.781],[-28.562,29.777]]");
        polygonGeofence.setActive(true);
    }

    // ==================== Haversine Distance Tests ====================

    @Test
    void haversineDistance_SamePoint_ReturnsZero() {
        double distance = GeofenceService.haversineDistance(-28.5597, 29.7789, -28.5597, 29.7789);
        assertThat(distance).isCloseTo(0.0, within(0.01));
    }

    @Test
    void haversineDistance_KnownDistance_ReturnsCorrectValue() {
        // Ladysmith to Pietermaritzburg (~130km)
        double distance = GeofenceService.haversineDistance(-28.5597, 29.7789, -29.6006, 30.3794);
        assertThat(distance).isCloseTo(130_000.0, within(10_000.0));
    }

    @Test
    void haversineDistance_ShortDistance_ReturnsCorrectValue() {
        // ~100m apart
        double distance = GeofenceService.haversineDistance(-28.5597, 29.7789, -28.5606, 29.7789);
        assertThat(distance).isCloseTo(100.0, within(10.0));
    }

    // ==================== Radius Geofence Tests ====================

    @Test
    void isWithinRadius_InsideRadius_ReturnsTrue() {
        boolean result = GeofenceService.isWithinRadius(-28.5597, 29.7789, 200.0, -28.5600, 29.7790);
        assertThat(result).isTrue();
    }

    @Test
    void isWithinRadius_OutsideRadius_ReturnsFalse() {
        // ~1km away
        boolean result = GeofenceService.isWithinRadius(-28.5597, 29.7789, 200.0, -28.5700, 29.7789);
        assertThat(result).isFalse();
    }

    @Test
    void isWithinRadius_OnBoundary_ReturnsTrue() {
        // Exactly at the center
        boolean result = GeofenceService.isWithinRadius(-28.5597, 29.7789, 200.0, -28.5597, 29.7789);
        assertThat(result).isTrue();
    }

    // ==================== Polygon Geofence Tests ====================

    @Test
    void pointInPolygon_InsideSquare_ReturnsTrue() {
        List<List<Double>> polygon = List.of(
                List.of(-28.558, 29.777),
                List.of(-28.558, 29.781),
                List.of(-28.562, 29.781),
                List.of(-28.562, 29.777)
        );
        boolean result = GeofenceService.pointInPolygon(polygon, -28.560, 29.779);
        assertThat(result).isTrue();
    }

    @Test
    void pointInPolygon_OutsideSquare_ReturnsFalse() {
        List<List<Double>> polygon = List.of(
                List.of(-28.558, 29.777),
                List.of(-28.558, 29.781),
                List.of(-28.562, 29.781),
                List.of(-28.562, 29.777)
        );
        boolean result = GeofenceService.pointInPolygon(polygon, -28.570, 29.790);
        assertThat(result).isFalse();
    }

    @Test
    void pointInPolygon_Triangle_InsideReturnsTrue() {
        List<List<Double>> triangle = List.of(
                List.of(0.0, 0.0),
                List.of(0.0, 10.0),
                List.of(10.0, 5.0)
        );
        boolean result = GeofenceService.pointInPolygon(triangle, 3.0, 5.0);
        assertThat(result).isTrue();
    }

    @Test
    void pointInPolygon_TooFewVertices_ReturnsFalse() {
        List<List<Double>> line = List.of(List.of(0.0, 0.0), List.of(10.0, 10.0));
        boolean result = GeofenceService.pointInPolygon(line, 5.0, 5.0);
        assertThat(result).isFalse();
    }

    // ==================== Integration Tests ====================

    @Test
    void isWithinGeofence_RadiusType_ValidatesCorrectly() {
        boolean inside = geofenceService.isWithinGeofence(radiusGeofence, -28.5600, 29.7790);
        assertThat(inside).isTrue();

        boolean outside = geofenceService.isWithinGeofence(radiusGeofence, -28.5700, 29.7789);
        assertThat(outside).isFalse();
    }

    @Test
    void isWithinGeofence_PolygonType_ValidatesCorrectly() {
        boolean inside = geofenceService.isWithinGeofence(polygonGeofence, -28.560, 29.779);
        assertThat(inside).isTrue();

        boolean outside = geofenceService.isWithinGeofence(polygonGeofence, -28.570, 29.790);
        assertThat(outside).isFalse();
    }

    @Test
    void isWithinGeofence_InactiveGeofence_ReturnsFalse() {
        radiusGeofence.setActive(false);
        boolean result = geofenceService.isWithinGeofence(radiusGeofence, -28.5600, 29.7790);
        assertThat(result).isFalse();
    }

    @Test
    void createGeofence_ValidRadius_ReturnsResponse() {
        GeofenceRequest request = new GeofenceRequest();
        request.setName("Test Geofence");
        request.setType("RADIUS");
        request.setLatitude(-28.5597);
        request.setLongitude(29.7789);
        request.setRadiusMeters(100.0);

        Geofence saved = new Geofence();
        saved.setId(1L);
        saved.setName("Test Geofence");
        saved.setType(Geofence.GeofenceType.RADIUS);
        saved.setLatitude(-28.5597);
        saved.setLongitude(29.7789);
        saved.setRadiusMeters(100.0);
        saved.setActive(true);

        when(geofenceRepository.save(any(Geofence.class))).thenReturn(saved);

        GeofenceResponse response = geofenceService.createGeofence(request);

        assertThat(response.getName()).isEqualTo("Test Geofence");
        assertThat(response.getType()).isEqualTo("RADIUS");
        assertThat(response.getActive()).isTrue();
    }
}
