package com.arthmatic.shumelahire.controller.performance;

import com.arthmatic.shumelahire.config.tenant.TenantContext;
import com.arthmatic.shumelahire.dto.performance.*;
import com.arthmatic.shumelahire.service.performance.PerformanceEnhancementService;
import com.arthmatic.shumelahire.entity.performance.ReviewType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/performance/enhanced")
@Validated
public class PerformanceEnhancementController {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceEnhancementController.class);

    @Autowired
    private PerformanceEnhancementService enhancementService;

    // ========== KRA ENDPOINTS ==========

    @PostMapping("/kras")
    public ResponseEntity<?> createKRA(
            @Valid @RequestBody KRARequest request,
            @RequestHeader("X-User-Id") String userId) {
        try {
            String tenantId = TenantContext.requireCurrentTenant();
            KRAResponse response = enhancementService.createKRA(request, tenantId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating KRA", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/kras/contract/{contractId}")
    public ResponseEntity<?> getKRAsByContract(@PathVariable Long contractId) {
        try {
            String tenantId = TenantContext.requireCurrentTenant();
            List<KRAResponse> kras = enhancementService.getKRAsByContract(contractId, tenantId);
            return ResponseEntity.ok(kras);
        } catch (Exception e) {
            logger.error("Error fetching KRAs for contract {}", contractId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/kras/{id}")
    public ResponseEntity<?> getKRA(@PathVariable Long id) {
        try {
            String tenantId = TenantContext.requireCurrentTenant();
            return ResponseEntity.ok(enhancementService.getKRA(id, tenantId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/kras/{id}")
    public ResponseEntity<?> updateKRA(
            @PathVariable Long id,
            @Valid @RequestBody KRARequest request) {
        try {
            String tenantId = TenantContext.requireCurrentTenant();
            return ResponseEntity.ok(enhancementService.updateKRA(id, request, tenantId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/kras/{id}")
    public ResponseEntity<Void> deleteKRA(@PathVariable Long id) {
        try {
            String tenantId = TenantContext.requireCurrentTenant();
            enhancementService.deleteKRA(id, tenantId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== PIP ENDPOINTS ==========

    @PostMapping("/pips")
    public ResponseEntity<?> createPIP(
            @Valid @RequestBody PIPRequest request,
            @RequestHeader("X-User-Id") String userId) {
        try {
            String tenantId = TenantContext.requireCurrentTenant();
            PIPResponse response = enhancementService.createPIP(request, tenantId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating PIP", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/pips")
    public ResponseEntity<Page<PIPResponse>> getPIPs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String tenantId = TenantContext.requireCurrentTenant();
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(enhancementService.getPIPs(tenantId, pageable));
    }

    @GetMapping("/pips/{id}")
    public ResponseEntity<?> getPIP(@PathVariable Long id) {
        try {
            String tenantId = TenantContext.requireCurrentTenant();
            return ResponseEntity.ok(enhancementService.getPIP(id, tenantId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/pips/{id}/activate")
    public ResponseEntity<?> activatePIP(@PathVariable Long id) {
        try {
            String tenantId = TenantContext.requireCurrentTenant();
            return ResponseEntity.ok(enhancementService.activatePIP(id, tenantId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/pips/{id}/extend")
    public ResponseEntity<?> extendPIP(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        try {
            String tenantId = TenantContext.requireCurrentTenant();
            LocalDate newEndDate = LocalDate.parse((String) body.get("newEndDate"));
            String reason = (String) body.get("reason");
            return ResponseEntity.ok(enhancementService.extendPIP(id, newEndDate, reason, tenantId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/pips/{id}/complete")
    public ResponseEntity<?> completePIP(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            @RequestHeader("X-User-Id") String userId) {
        try {
            String tenantId = TenantContext.requireCurrentTenant();
            boolean successful = Boolean.parseBoolean(body.get("successful").toString());
            String notes = (String) body.get("notes");
            return ResponseEntity.ok(enhancementService.completePIP(id, successful, notes, userId, tenantId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/pips/{id}/terminate")
    public ResponseEntity<?> terminatePIP(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @RequestHeader("X-User-Id") String userId) {
        try {
            String tenantId = TenantContext.requireCurrentTenant();
            return ResponseEntity.ok(enhancementService.terminatePIP(id, body.get("notes"), userId, tenantId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/pips/milestones/{milestoneId}/complete")
    public ResponseEntity<?> completeMilestone(
            @PathVariable Long milestoneId,
            @RequestBody Map<String, String> body) {
        try {
            String tenantId = TenantContext.requireCurrentTenant();
            enhancementService.completeMilestone(milestoneId, body.get("notes"), tenantId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== CALIBRATION SESSION ENDPOINTS ==========

    @PostMapping("/calibrations")
    public ResponseEntity<?> createCalibrationSession(
            @Valid @RequestBody CalibrationSessionRequest request,
            @RequestHeader("X-User-Id") String userId) {
        try {
            String tenantId = TenantContext.requireCurrentTenant();
            CalibrationSessionResponse response = enhancementService.createCalibrationSession(request, tenantId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating calibration session", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/calibrations")
    public ResponseEntity<Page<CalibrationSessionResponse>> getCalibrationSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String tenantId = TenantContext.requireCurrentTenant();
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(enhancementService.getCalibrationSessions(tenantId, pageable));
    }

    @GetMapping("/calibrations/{id}")
    public ResponseEntity<?> getCalibrationSession(@PathVariable Long id) {
        try {
            String tenantId = TenantContext.requireCurrentTenant();
            return ResponseEntity.ok(enhancementService.getCalibrationSession(id, tenantId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/calibrations/{id}/start")
    public ResponseEntity<?> startCalibrationSession(@PathVariable Long id) {
        try {
            String tenantId = TenantContext.requireCurrentTenant();
            return ResponseEntity.ok(enhancementService.startCalibrationSession(id, tenantId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/calibrations/{id}/complete")
    public ResponseEntity<?> completeCalibrationSession(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            String tenantId = TenantContext.requireCurrentTenant();
            return ResponseEntity.ok(enhancementService.completeCalibrationSession(id, body.get("notes"), tenantId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/calibrations/{id}/cancel")
    public ResponseEntity<?> cancelCalibrationSession(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            String tenantId = TenantContext.requireCurrentTenant();
            return ResponseEntity.ok(enhancementService.cancelCalibrationSession(id, body.get("reason"), tenantId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/calibrations/{sessionId}/ratings")
    public ResponseEntity<?> addCalibrationRating(
            @PathVariable Long sessionId,
            @Valid @RequestBody CalibrationRatingRequest request,
            @RequestHeader("X-User-Id") String userId) {
        try {
            String tenantId = TenantContext.requireCurrentTenant();
            enhancementService.addCalibrationRating(sessionId, request, userId, tenantId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ========== SELF-ASSESSMENT ENDPOINT ==========

    @PostMapping("/self-assessment")
    public ResponseEntity<?> submitSelfAssessment(@Valid @RequestBody SelfAssessmentRequest request) {
        try {
            String tenantId = TenantContext.requireCurrentTenant();
            enhancementService.submitSelfAssessment(request, tenantId);
            return ResponseEntity.ok(Map.of("message", "Self-assessment submitted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ========== REVIEW CYCLE AUTOMATION ENDPOINT ==========

    @PostMapping("/cycles/{cycleId}/create-reviews")
    public ResponseEntity<?> createReviewsForCycle(
            @PathVariable Long cycleId,
            @RequestParam String reviewType) {
        try {
            String tenantId = TenantContext.requireCurrentTenant();
            ReviewType type = ReviewType.valueOf(reviewType.toUpperCase());
            int count = enhancementService.createReviewsForCycle(cycleId, type, tenantId);
            return ResponseEntity.ok(Map.of("reviewsCreated", count));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ========== MANAGER DASHBOARD ENDPOINT ==========

    @GetMapping("/dashboard/manager")
    public ResponseEntity<?> getManagerDashboard(
            @RequestHeader("X-User-Id") String managerId,
            @RequestParam(required = false) Long cycleId) {
        try {
            String tenantId = TenantContext.requireCurrentTenant();
            return ResponseEntity.ok(enhancementService.getManagerDashboard(managerId, cycleId, tenantId));
        } catch (Exception e) {
            logger.error("Error building manager dashboard", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    // ========== PERFORMANCE ANALYTICS ENDPOINT ==========

    @GetMapping("/analytics")
    public ResponseEntity<?> getPerformanceAnalytics(
            @RequestParam(required = false) Long cycleId) {
        try {
            String tenantId = TenantContext.requireCurrentTenant();
            return ResponseEntity.ok(enhancementService.getPerformanceAnalytics(cycleId, tenantId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error generating analytics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }
}
