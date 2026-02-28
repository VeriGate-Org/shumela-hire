package com.arthmatic.shumelahire.service.attendance;

import com.arthmatic.shumelahire.dto.attendance.GeofenceRequest;
import com.arthmatic.shumelahire.dto.attendance.GeofenceResponse;
import com.arthmatic.shumelahire.entity.attendance.Geofence;
import com.arthmatic.shumelahire.entity.attendance.GeofenceType;
import com.arthmatic.shumelahire.repository.attendance.GeofenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeofenceServiceTest {

    @Mock
    private GeofenceRepository geofenceRepository;

    @InjectMocks
    private GeofenceService geofenceService;

    private Geofence radiusGeofence;
    private Geofence polygonGeofence;

    @BeforeEach
    void setUp() {
        // Radius geofence centered at Sandton City, Johannesburg
        radiusGeofence = new Geofence();
        radiusGeofence.setId(1L);
        radiusGeofence.setName("Sandton Office");
        radiusGeofence.setSite("Sandton");
        radiusGeofence.setGeofenceType(GeofenceType.RADIUS);
        radiusGeofence.setLatitude(-26.1076);
        radiusGeofence.setLongitude(28.0567);
        radiusGeofence.setRadiusMeters(200.0);
        radiusGeofence.setIsActive(true);
        radiusGeofence.setCreatedAt(LocalDateTime.now());
        radiusGeofence.setUpdatedAt(LocalDateTime.now());

        // Polygon geofence (roughly a triangle around a campus)
        polygonGeofence = new Geofence();
        polygonGeofence.setId(2L);
        polygonGeofence.setName("Campus Area");
        polygonGeofence.setSite("Richards Bay");
        polygonGeofence.setGeofenceType(GeofenceType.POLYGON);
        polygonGeofence.setPolygonCoordinates("-28.7800,32.0400;-28.7850,32.0500;-28.7900,32.0400");
        polygonGeofence.setIsActive(true);
        polygonGeofence.setCreatedAt(LocalDateTime.now());
        polygonGeofence.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createGeofence_ValidRequest_ReturnsGeofenceResponse() {
        GeofenceRequest request = new GeofenceRequest();
        request.setName("Test Geofence");
        request.setGeofenceType("RADIUS");
        request.setLatitude(-26.1076);
        request.setLongitude(28.0567);
        request.setRadiusMeters(100.0);

        when(geofenceRepository.save(any(Geofence.class))).thenReturn(radiusGeofence);

        GeofenceResponse result = geofenceService.createGeofence(request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Sandton Office");
        assertThat(result.getGeofenceType()).isEqualTo("RADIUS");
        verify(geofenceRepository).save(any(Geofence.class));
    }

    @Test
    void getGeofence_ExistingId_ReturnsGeofence() {
        when(geofenceRepository.findById(1L)).thenReturn(Optional.of(radiusGeofence));

        GeofenceResponse result = geofenceService.getGeofence(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Sandton Office");
    }

    @Test
    void getGeofence_NonExistingId_ThrowsException() {
        when(geofenceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> geofenceService.getGeofence(999L));
    }

    @Test
    void calculateDistance_SandtonToRosebank_ReturnsCorrectDistance() {
        // Sandton City to Rosebank Mall (~4.5 km)
        double distance = geofenceService.calculateDistance(
                -26.1076, 28.0567,  // Sandton City
                -26.1455, 28.0436   // Rosebank
        );

        // Should be roughly 4.3-4.7 km
        assertThat(distance).isBetween(4200.0, 4700.0);
    }

    @Test
    void calculateDistance_SamePoint_ReturnsZero() {
        double distance = geofenceService.calculateDistance(
                -26.1076, 28.0567,
                -26.1076, 28.0567
        );

        assertThat(distance).isCloseTo(0.0, within(0.01));
    }

    @Test
    void isWithinGeofence_InsideRadius_ReturnsTrue() {
        // Point very close to the center (within 200m)
        boolean result = geofenceService.isWithinGeofence(
                radiusGeofence, -26.1077, 28.0568);

        assertThat(result).isTrue();
    }

    @Test
    void isWithinGeofence_OutsideRadius_ReturnsFalse() {
        // Point far from center (Rosebank is ~4.5km away)
        boolean result = geofenceService.isWithinGeofence(
                radiusGeofence, -26.1455, 28.0436);

        assertThat(result).isFalse();
    }

    @Test
    void isWithinGeofence_InsidePolygon_ReturnsTrue() {
        // Point inside the triangle
        boolean result = geofenceService.isWithinGeofence(
                polygonGeofence, -28.7840, 32.0440);

        assertThat(result).isTrue();
    }

    @Test
    void isWithinGeofence_OutsidePolygon_ReturnsFalse() {
        // Point outside the triangle
        boolean result = geofenceService.isWithinGeofence(
                polygonGeofence, -28.7700, 32.0300);

        assertThat(result).isFalse();
    }

    @Test
    void findContainingGeofence_WithinActiveGeofence_ReturnsGeofence() {
        when(geofenceRepository.findAll()).thenReturn(Arrays.asList(radiusGeofence, polygonGeofence));

        // Point within radius geofence
        Geofence result = geofenceService.findContainingGeofence(-26.1077, 28.0568);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findContainingGeofence_OutsideAllGeofences_ReturnsNull() {
        when(geofenceRepository.findAll()).thenReturn(Arrays.asList(radiusGeofence, polygonGeofence));

        // Point far from any geofence
        Geofence result = geofenceService.findContainingGeofence(-30.0000, 30.0000);

        assertThat(result).isNull();
    }

    @Test
    void findContainingGeofence_NullCoordinates_ReturnsNull() {
        Geofence result = geofenceService.findContainingGeofence(null, null);
        assertThat(result).isNull();
    }

    @Test
    void isWithinGeofence_NullRadiusFields_ReturnsFalse() {
        Geofence incomplete = new Geofence();
        incomplete.setGeofenceType(GeofenceType.RADIUS);
        incomplete.setLatitude(null);
        incomplete.setLongitude(null);
        incomplete.setRadiusMeters(null);

        boolean result = geofenceService.isWithinGeofence(incomplete, -26.1076, 28.0567);

        assertThat(result).isFalse();
    }

    @Test
    void isWithinGeofence_EmptyPolygonCoordinates_ReturnsFalse() {
        Geofence incomplete = new Geofence();
        incomplete.setGeofenceType(GeofenceType.POLYGON);
        incomplete.setPolygonCoordinates("");

        boolean result = geofenceService.isWithinGeofence(incomplete, -26.1076, 28.0567);

        assertThat(result).isFalse();
    }

    @Test
    void deleteGeofence_ExistingId_DeactivatesGeofence() {
        when(geofenceRepository.findById(1L)).thenReturn(Optional.of(radiusGeofence));
        when(geofenceRepository.save(any(Geofence.class))).thenReturn(radiusGeofence);

        geofenceService.deleteGeofence(1L);

        assertThat(radiusGeofence.getIsActive()).isFalse();
        verify(geofenceRepository).save(radiusGeofence);
    }
}
