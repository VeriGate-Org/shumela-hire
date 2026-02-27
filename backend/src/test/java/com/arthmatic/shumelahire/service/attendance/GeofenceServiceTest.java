package com.arthmatic.shumelahire.service.attendance;

import com.arthmatic.shumelahire.dto.attendance.GeofenceRequest;
import com.arthmatic.shumelahire.dto.attendance.GeofenceResponse;
import com.arthmatic.shumelahire.entity.Geofence;
import com.arthmatic.shumelahire.repository.GeofenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeofenceServiceTest {

    @Mock
    private GeofenceRepository geofenceRepository;

    @InjectMocks
    private GeofenceService geofenceService;

    @Test
    void create_validRequest_createsGeofence() {
        GeofenceRequest request = new GeofenceRequest();
        request.setName("Head Office");
        request.setCenterLatitude(new BigDecimal("-26.2041"));
        request.setCenterLongitude(new BigDecimal("28.0473"));
        request.setRadiusMeters(200);
        request.setAddress("123 Main St, Johannesburg");

        Geofence saved = new Geofence();
        saved.setId(1L);
        saved.setName("Head Office");
        saved.setCenterLatitude(new BigDecimal("-26.2041"));
        saved.setCenterLongitude(new BigDecimal("28.0473"));
        saved.setRadiusMeters(200);
        saved.setGeofenceType(Geofence.GeofenceType.CIRCLE);

        when(geofenceRepository.save(any(Geofence.class))).thenReturn(saved);

        GeofenceResponse response = geofenceService.create(request);

        assertThat(response.getName()).isEqualTo("Head Office");
        assertThat(response.getRadiusMeters()).isEqualTo(200);
        verify(geofenceRepository).save(any(Geofence.class));
    }

    @Test
    void getAll_returnsList() {
        Geofence g1 = new Geofence();
        g1.setId(1L);
        g1.setName("Office A");
        g1.setCenterLatitude(BigDecimal.ZERO);
        g1.setCenterLongitude(BigDecimal.ZERO);
        g1.setRadiusMeters(100);
        g1.setGeofenceType(Geofence.GeofenceType.CIRCLE);

        Geofence g2 = new Geofence();
        g2.setId(2L);
        g2.setName("Office B");
        g2.setCenterLatitude(BigDecimal.ONE);
        g2.setCenterLongitude(BigDecimal.ONE);
        g2.setRadiusMeters(150);
        g2.setGeofenceType(Geofence.GeofenceType.CIRCLE);

        when(geofenceRepository.findAll()).thenReturn(Arrays.asList(g1, g2));

        List<GeofenceResponse> result = geofenceService.getAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void getById_notFound_throwsException() {
        when(geofenceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> geofenceService.getById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Geofence not found");
    }

    @Test
    void haversineDistance_samePoint_returnsZero() {
        double distance = GeofenceService.haversineDistance(-26.2041, 28.0473, -26.2041, 28.0473);
        assertThat(distance).isEqualTo(0.0);
    }

    @Test
    void haversineDistance_knownPoints_returnsCorrectDistance() {
        // Johannesburg to Pretoria: ~56km
        double distance = GeofenceService.haversineDistance(-26.2041, 28.0473, -25.7479, 28.2293);
        assertThat(distance).isBetween(50_000.0, 60_000.0);
    }

    @Test
    void isWithinGeofence_insideRadius_returnsTrue() {
        Geofence geofence = new Geofence();
        geofence.setId(1L);
        geofence.setCenterLatitude(new BigDecimal("-26.2041"));
        geofence.setCenterLongitude(new BigDecimal("28.0473"));
        geofence.setRadiusMeters(500);
        geofence.setGeofenceType(Geofence.GeofenceType.CIRCLE);

        // Point very close to center (within 500m)
        boolean result = geofenceService.isWithinGeofence(
                geofence, new BigDecimal("-26.2040"), new BigDecimal("28.0474"));

        assertThat(result).isTrue();
    }

    @Test
    void isWithinGeofence_outsideRadius_returnsFalse() {
        Geofence geofence = new Geofence();
        geofence.setId(1L);
        geofence.setCenterLatitude(new BigDecimal("-26.2041"));
        geofence.setCenterLongitude(new BigDecimal("28.0473"));
        geofence.setRadiusMeters(100);
        geofence.setGeofenceType(Geofence.GeofenceType.CIRCLE);

        // Point far away (~56km)
        boolean result = geofenceService.isWithinGeofence(
                geofence, new BigDecimal("-25.7479"), new BigDecimal("28.2293"));

        assertThat(result).isFalse();
    }

    @Test
    void isWithinGeofence_nullCoordinates_returnsFalse() {
        Geofence geofence = new Geofence();
        geofence.setId(1L);
        geofence.setCenterLatitude(new BigDecimal("-26.2041"));
        geofence.setCenterLongitude(new BigDecimal("28.0473"));
        geofence.setRadiusMeters(100);
        geofence.setGeofenceType(Geofence.GeofenceType.CIRCLE);

        boolean result = geofenceService.isWithinGeofence(geofence, null, null);
        assertThat(result).isFalse();
    }

    @Test
    void toggleActive_deactivatesGeofence() {
        Geofence geofence = new Geofence();
        geofence.setId(1L);
        geofence.setName("Test");
        geofence.setIsActive(true);
        geofence.setCenterLatitude(BigDecimal.ZERO);
        geofence.setCenterLongitude(BigDecimal.ZERO);
        geofence.setRadiusMeters(100);
        geofence.setGeofenceType(Geofence.GeofenceType.CIRCLE);

        when(geofenceRepository.findById(1L)).thenReturn(Optional.of(geofence));
        when(geofenceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        GeofenceResponse response = geofenceService.toggleActive(1L, false);

        assertThat(response.getIsActive()).isFalse();
    }
}
