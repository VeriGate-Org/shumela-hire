package com.arthmatic.shumelahire.controller.engagement;

import com.arthmatic.shumelahire.config.tenant.TenantContext;
import com.arthmatic.shumelahire.dto.engagement.*;
import com.arthmatic.shumelahire.entity.engagement.SurveyStatus;
import com.arthmatic.shumelahire.service.engagement.EngagementService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/engagement")
@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER', 'EMPLOYEE')")
public class EngagementController {

    private static final Logger logger = LoggerFactory.getLogger(EngagementController.class);

    @Autowired
    private EngagementService engagementService;

    // ==================== Surveys ====================

    @PostMapping("/surveys")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> createSurvey(@Valid @RequestBody SurveyRequest request,
                                          @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        try {
            SurveyResponseDTO response = engagementService.createSurvey(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating survey", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/surveys")
    public ResponseEntity<?> getAllSurveys(@RequestParam(required = false) SurveyStatus status) {
        try {
            List<SurveyResponseDTO> surveys = (status != null)
                    ? engagementService.getSurveysByStatus(status)
                    : engagementService.getAllSurveys();
            return ResponseEntity.ok(surveys);
        } catch (Exception e) {
            logger.error("Error getting surveys", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/surveys/{id}")
    public ResponseEntity<?> getSurvey(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(engagementService.getSurvey(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting survey {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @PostMapping("/surveys/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> activateSurvey(@PathVariable Long id) {
        try {
            SurveyResponseDTO response = engagementService.activateSurvey(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error activating survey {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @PostMapping("/surveys/{id}/close")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> closeSurvey(@PathVariable Long id) {
        try {
            SurveyResponseDTO response = engagementService.closeSurvey(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error closing survey {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @PostMapping("/surveys/respond")
    public ResponseEntity<?> submitSurveyResponse(@Valid @RequestBody SurveyAnswerRequest request) {
        try {
            engagementService.submitSurveyResponses(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error submitting survey response", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    // ==================== Recognition ====================

    @PostMapping("/recognitions")
    public ResponseEntity<?> createRecognition(@Valid @RequestBody RecognitionRequest request) {
        try {
            RecognitionResponse response = engagementService.createRecognition(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating recognition", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/recognitions/feed")
    public ResponseEntity<?> getPublicRecognitions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            String tenantId = TenantContext.requireCurrentTenant();
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<RecognitionResponse> recognitions = engagementService.getPublicRecognitions(tenantId, pageable);
            return ResponseEntity.ok(recognitions);
        } catch (Exception e) {
            logger.error("Error getting recognition feed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/recognitions/received/{employeeId}")
    public ResponseEntity<?> getRecognitionsForEmployee(@PathVariable Long employeeId) {
        try {
            List<RecognitionResponse> recognitions = engagementService.getRecognitionsForEmployee(employeeId);
            return ResponseEntity.ok(recognitions);
        } catch (Exception e) {
            logger.error("Error getting recognitions for employee {}", employeeId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/recognitions/given/{employeeId}")
    public ResponseEntity<?> getRecognitionsGivenByEmployee(@PathVariable Long employeeId) {
        try {
            List<RecognitionResponse> recognitions = engagementService.getRecognitionsGivenByEmployee(employeeId);
            return ResponseEntity.ok(recognitions);
        } catch (Exception e) {
            logger.error("Error getting recognitions given by employee {}", employeeId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/recognitions/points/{employeeId}")
    public ResponseEntity<?> getTotalPoints(@PathVariable Long employeeId) {
        try {
            Long points = engagementService.getTotalPointsForEmployee(employeeId);
            return ResponseEntity.ok(java.util.Map.of("employeeId", employeeId, "totalPoints", points));
        } catch (Exception e) {
            logger.error("Error getting points for employee {}", employeeId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    // ==================== Wellness Programs ====================

    @PostMapping("/wellness/programs")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> createWellnessProgram(@Valid @RequestBody WellnessProgramRequest request) {
        try {
            WellnessProgramResponse response = engagementService.createWellnessProgram(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating wellness program", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/wellness/programs")
    public ResponseEntity<?> getWellnessPrograms(@RequestParam(defaultValue = "false") boolean activeOnly) {
        try {
            List<WellnessProgramResponse> programs = activeOnly
                    ? engagementService.getActiveWellnessPrograms(TenantContext.requireCurrentTenant())
                    : engagementService.getAllWellnessPrograms();
            return ResponseEntity.ok(programs);
        } catch (Exception e) {
            logger.error("Error getting wellness programs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/wellness/programs/{id}")
    public ResponseEntity<?> getWellnessProgram(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(engagementService.getWellnessProgram(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting wellness program {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @DeleteMapping("/wellness/programs/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> deleteWellnessProgram(@PathVariable Long id) {
        try {
            engagementService.deleteWellnessProgram(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting wellness program {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    // ==================== Wellness Check-Ins ====================

    @PostMapping("/wellness/check-ins")
    public ResponseEntity<?> createWellnessCheckIn(@Valid @RequestBody WellnessCheckInRequest request) {
        try {
            WellnessCheckInResponse response = engagementService.createWellnessCheckIn(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating wellness check-in", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/wellness/check-ins/{employeeId}")
    public ResponseEntity<?> getCheckInsForEmployee(@PathVariable Long employeeId) {
        try {
            List<WellnessCheckInResponse> checkIns = engagementService.getCheckInsForEmployee(employeeId);
            return ResponseEntity.ok(checkIns);
        } catch (Exception e) {
            logger.error("Error getting check-ins for employee {}", employeeId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    // ==================== Social Feed ====================

    @PostMapping("/social/posts")
    public ResponseEntity<?> createSocialPost(@Valid @RequestBody SocialPostRequest request) {
        try {
            SocialPostResponse response = engagementService.createSocialPost(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating social post", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/social/feed")
    public ResponseEntity<?> getSocialFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            String tenantId = TenantContext.requireCurrentTenant();
            Pageable pageable = PageRequest.of(page, size);
            Page<SocialPostResponse> feed = engagementService.getSocialFeed(tenantId, pageable);
            return ResponseEntity.ok(feed);
        } catch (Exception e) {
            logger.error("Error getting social feed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/social/posts/{id}")
    public ResponseEntity<?> getSocialPost(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(engagementService.getSocialPost(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting social post {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @PostMapping("/social/posts/{id}/like")
    public ResponseEntity<?> likeSocialPost(@PathVariable Long id) {
        try {
            SocialPostResponse response = engagementService.likeSocialPost(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error liking social post {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @DeleteMapping("/social/posts/{id}")
    public ResponseEntity<?> deleteSocialPost(@PathVariable Long id) {
        try {
            engagementService.deleteSocialPost(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting social post {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    // ==================== Analytics ====================

    @GetMapping("/analytics")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'EXECUTIVE')")
    public ResponseEntity<?> getEngagementAnalytics() {
        try {
            String tenantId = TenantContext.requireCurrentTenant();
            EngagementAnalyticsResponse analytics = engagementService.getEngagementAnalytics(tenantId);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            logger.error("Error getting engagement analytics", e);
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
        public long getTimestamp() { return timestamp; }
    }
}
