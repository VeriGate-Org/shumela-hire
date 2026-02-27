package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.dto.attendance.GeofenceRequest;
import com.arthmatic.shumelahire.dto.attendance.GeofenceResponse;
import com.arthmatic.shumelahire.service.GeofenceService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/geofences")
@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
public class GeofenceController {

    private static final Logger logger = LoggerFactory.getLogger(GeofenceController.class);

    @Autowired
    private GeofenceService geofenceService;

    @PostMapping
    public ResponseEntity<?> createGeofence(@Valid @RequestBody GeofenceRequest request) {
        try {
            GeofenceResponse response = geofenceService.createGeofence(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating geofence", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal server error"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGeofence(@PathVariable Long id, @Valid @RequestBody GeofenceRequest request) {
        try {
            return ResponseEntity.ok(geofenceService.updateGeofence(id, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating geofence", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal server error"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGeofence(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(geofenceService.getGeofence(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllActiveGeofences() {
        try {
            List<GeofenceResponse> geofences = geofenceService.getAllActiveGeofences();
            return ResponseEntity.ok(geofences);
        } catch (Exception e) {
            logger.error("Error listing geofences", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal server error"));
        }
    }

    @GetMapping("/site/{site}")
    public ResponseEntity<?> getGeofencesBySite(@PathVariable String site) {
        return ResponseEntity.ok(geofenceService.getGeofencesBySite(site));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deactivateGeofence(@PathVariable Long id) {
        try {
            geofenceService.deactivateGeofence(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateLocation(
            @RequestParam Long geofenceId,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        try {
            boolean within = geofenceService.isWithinGeofence(geofenceId, latitude, longitude);
            return ResponseEntity.ok(Map.of("withinGeofence", within));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
