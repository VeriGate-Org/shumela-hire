package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.dto.attendance.GeofenceRequest;
import com.arthmatic.shumelahire.dto.attendance.GeofenceResponse;
import com.arthmatic.shumelahire.service.attendance.GeofenceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/geofences")
@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
public class GeofenceController {

    private final GeofenceService geofenceService;

    public GeofenceController(GeofenceService geofenceService) {
        this.geofenceService = geofenceService;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody GeofenceRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(geofenceService.create(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody GeofenceRequest request) {
        try {
            return ResponseEntity.ok(geofenceService.update(id, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(geofenceService.getById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<GeofenceResponse>> getAll() {
        return ResponseEntity.ok(geofenceService.getAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<GeofenceResponse>> getActive() {
        return ResponseEntity.ok(geofenceService.getActive());
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggleActive(@PathVariable Long id, @RequestParam boolean active) {
        try {
            return ResponseEntity.ok(geofenceService.toggleActive(id, active));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            geofenceService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkWithinGeofence(
            @RequestParam Long geofenceId,
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude) {
        boolean within = geofenceService.isWithinGeofence(geofenceId, latitude, longitude);
        return ResponseEntity.ok(Map.of("withinGeofence", within));
    }
}
