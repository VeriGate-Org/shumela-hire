package com.arthmatic.shumelahire.controller.attendance;

import com.arthmatic.shumelahire.dto.attendance.GeofenceRequest;
import com.arthmatic.shumelahire.dto.attendance.GeofenceResponse;
import com.arthmatic.shumelahire.service.attendance.GeofenceService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating geofence", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGeofence(@PathVariable Long id, @Valid @RequestBody GeofenceRequest request) {
        try {
            GeofenceResponse response = geofenceService.updateGeofence(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating geofence {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGeofence(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(geofenceService.getGeofence(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<GeofenceResponse>> getAllGeofences() {
        return ResponseEntity.ok(geofenceService.getAllGeofences());
    }

    @GetMapping("/active")
    public ResponseEntity<List<GeofenceResponse>> getActiveGeofences() {
        return ResponseEntity.ok(geofenceService.getActiveGeofences());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGeofence(@PathVariable Long id) {
        try {
            geofenceService.deleteGeofence(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/check")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'EMPLOYEE')")
    public ResponseEntity<?> checkGeofence(@RequestParam Double latitude, @RequestParam Double longitude) {
        var geofence = geofenceService.findContainingGeofence(latitude, longitude);
        if (geofence != null) {
            return ResponseEntity.ok(GeofenceResponse.fromEntity(geofence));
        }
        return ResponseEntity.ok().body(java.util.Map.of("withinGeofence", false));
    }

    static class ErrorResponse {
        private final String message;
        public ErrorResponse(String message) { this.message = message; }
        public String getMessage() { return message; }
    }
}
