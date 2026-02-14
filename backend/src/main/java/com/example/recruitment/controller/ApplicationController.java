package com.example.recruitment.controller;

import com.example.recruitment.dto.ApplicationCreateRequest;
import com.example.recruitment.dto.ApplicationResponse;
import com.example.recruitment.dto.ApplicationWithdrawRequest;
import com.example.recruitment.entity.ApplicationStatus;
import com.example.recruitment.service.ApplicationService;
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
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'RECRUITER')")
public class ApplicationController {
    
    private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);
    
    @Autowired
    private ApplicationService applicationService;
    
    /**
     * Submit new application
     * POST /api/applications
     */
    @PostMapping
    public ResponseEntity<?> submitApplication(@Valid @RequestBody ApplicationCreateRequest request) {
        try {
            logger.info("Submitting application for applicant {} to job {}", 
                       request.getApplicantId(), request.getJobAdId());
            ApplicationResponse response = applicationService.submitApplication(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to submit application: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error submitting application", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Get application by ID
     * GET /api/applications/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getApplication(@PathVariable Long id) {
        try {
            ApplicationResponse response = applicationService.getApplication(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Application not found: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting application {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Search applications with pagination
     * GET /api/applications?search={term}&page={page}&size={size}&sort={field}
     */
    @GetMapping
    public ResponseEntity<?> searchApplications(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "submittedAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        try {
            Sort.Direction sortDirection = Sort.Direction.fromString(direction);
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            
            Page<ApplicationResponse> results = applicationService.searchApplications(search, pageable);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Error searching applications", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Get applications by applicant
     * GET /api/applications/applicant/{applicantId}
     */
    @GetMapping("/applicant/{applicantId}")
    public ResponseEntity<?> getApplicationsByApplicant(@PathVariable Long applicantId) {
        try {
            List<ApplicationResponse> applications = applicationService.getApplicationsByApplicant(applicantId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            logger.error("Error getting applications for applicant {}", applicantId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Get applications by job ad
     * GET /api/applications/job/{jobAdId}
     */
    @GetMapping("/job/{jobAdId}")
    public ResponseEntity<?> getApplicationsByJobAd(@PathVariable Long jobAdId) {
        try {
            List<ApplicationResponse> applications = applicationService.getApplicationsByJobAd(jobAdId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            logger.error("Error getting applications for job {}", jobAdId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Get applications by status
     * GET /api/applications/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getApplicationsByStatus(@PathVariable ApplicationStatus status) {
        try {
            List<ApplicationResponse> applications = applicationService.getApplicationsByStatus(status);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            logger.error("Error getting applications with status {}", status, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Update application status
     * PUT /api/applications/{id}/status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status,
            @RequestParam(required = false) String notes) {
        try {
            logger.info("Updating application {} to status {}", id, status);
            ApplicationResponse response = applicationService.updateApplicationStatus(id, status, notes);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to update application {} status: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating application {} status", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Withdraw application
     * POST /api/applications/{id}/withdraw
     */
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<?> withdrawApplication(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationWithdrawRequest request) {
        try {
            logger.info("Withdrawing application {}", id);
            ApplicationResponse response = applicationService.withdrawApplication(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to withdraw application {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error withdrawing application {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Rate application
     * POST /api/applications/{id}/rate
     */
    @PostMapping("/{id}/rate")
    public ResponseEntity<?> rateApplication(
            @PathVariable Long id,
            @RequestParam Integer rating,
            @RequestParam(required = false) String feedback) {
        try {
            logger.info("Rating application {} with {} stars", id, rating);
            ApplicationResponse response = applicationService.rateApplication(id, rating, feedback);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to rate application {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error rating application {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Check if applicant can apply for job
     * GET /api/applications/can-apply?applicantId={id}&jobAdId={id}
     */
    @GetMapping("/can-apply")
    public ResponseEntity<?> canApplicantApplyForJob(
            @RequestParam Long applicantId,
            @RequestParam Long jobAdId) {
        try {
            boolean canApply = applicationService.canApplicantApplyForJob(applicantId, jobAdId);
            return ResponseEntity.ok(new CanApplyResponse(canApply));
        } catch (Exception e) {
            logger.error("Error checking if applicant {} can apply for job {}", applicantId, jobAdId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Get applications requiring action
     * GET /api/applications/requiring-action
     */
    @GetMapping("/requiring-action")
    public ResponseEntity<?> getApplicationsRequiringAction() {
        try {
            List<ApplicationResponse> applications = applicationService.getApplicationsRequiringAction();
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            logger.error("Error getting applications requiring action", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Get recent applications
     * GET /api/applications/recent?days={days}
     */
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentApplications(@RequestParam(defaultValue = "7") int days) {
        try {
            List<ApplicationResponse> applications = applicationService.getRecentApplications(days);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            logger.error("Error getting recent applications", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Delete application
     * DELETE /api/applications/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteApplication(@PathVariable Long id) {
        try {
            logger.info("Deleting application {}", id);
            applicationService.deleteApplication(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to delete application {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting application {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    /**
     * Get application statistics
     * GET /api/applications/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getApplicationStatistics() {
        try {
            List<Object[]> statistics = applicationService.getApplicationStatusStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("Error getting application statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    // Response DTOs
    public static class ErrorResponse {
        private String message;
        private long timestamp;
        
        public ErrorResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
    
    public static class CanApplyResponse {
        private boolean canApply;
        
        public CanApplyResponse(boolean canApply) {
            this.canApply = canApply;
        }
        
        public boolean isCanApply() {
            return canApply;
        }
        
        public void setCanApply(boolean canApply) {
            this.canApply = canApply;
        }
    }
}