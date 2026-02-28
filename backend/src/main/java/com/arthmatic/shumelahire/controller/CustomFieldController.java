package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.dto.CustomFieldRequest;
import com.arthmatic.shumelahire.dto.CustomFieldResponse;
import com.arthmatic.shumelahire.dto.CustomFieldValueRequest;
import com.arthmatic.shumelahire.entity.CustomFieldEntityType;
import com.arthmatic.shumelahire.service.CustomFieldService;
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
@RequestMapping("/api/custom-fields")
@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
public class CustomFieldController {

    private static final Logger logger = LoggerFactory.getLogger(CustomFieldController.class);

    @Autowired
    private CustomFieldService customFieldService;

    // ==================== Field Definitions ====================

    @PostMapping
    public ResponseEntity<?> createField(@Valid @RequestBody CustomFieldRequest request) {
        try {
            logger.info("Creating custom field: {} for {}", request.getFieldName(), request.getEntityType());
            CustomFieldResponse response = customFieldService.createField(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to create custom field: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating custom field", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateField(@PathVariable Long id,
                                        @Valid @RequestBody CustomFieldRequest request) {
        try {
            logger.info("Updating custom field: {}", id);
            CustomFieldResponse response = customFieldService.updateField(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to update custom field {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating custom field {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getField(@PathVariable Long id) {
        try {
            CustomFieldResponse response = customFieldService.getField(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting custom field {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/entity-type/{entityType}")
    public ResponseEntity<?> getFieldsByEntityType(@PathVariable CustomFieldEntityType entityType,
                                                   @RequestParam(defaultValue = "true") boolean activeOnly) {
        try {
            List<CustomFieldResponse> fields;
            if (activeOnly) {
                fields = customFieldService.getFieldsByEntityType(entityType);
            } else {
                fields = customFieldService.getAllFieldsByEntityType(entityType);
            }
            return ResponseEntity.ok(fields);
        } catch (Exception e) {
            logger.error("Error getting custom fields for {}", entityType, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteField(@PathVariable Long id) {
        try {
            customFieldService.deleteField(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting custom field {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    // ==================== Field Values ====================

    @PostMapping("/values")
    public ResponseEntity<?> setFieldValue(@Valid @RequestBody CustomFieldValueRequest request) {
        try {
            customFieldService.setFieldValue(request);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to set custom field value: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error setting custom field value", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @PostMapping("/values/bulk/{entityType}/{entityId}")
    public ResponseEntity<?> setFieldValues(
            @PathVariable CustomFieldEntityType entityType,
            @PathVariable Long entityId,
            @RequestBody Map<Long, String> fieldValues) {
        try {
            customFieldService.setFieldValues(entityType, entityId, fieldValues);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error setting custom field values for {} {}", entityType, entityId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/values/{entityType}/{entityId}")
    public ResponseEntity<?> getFieldValues(@PathVariable CustomFieldEntityType entityType,
                                           @PathVariable Long entityId) {
        try {
            Map<String, String> values = customFieldService.getFieldValues(entityType, entityId);
            return ResponseEntity.ok(values);
        } catch (Exception e) {
            logger.error("Error getting custom field values for {} {}", entityType, entityId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    // Error response DTO
    public static class ErrorResponse {
        private String message;
        private long timestamp;

        public ErrorResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}
