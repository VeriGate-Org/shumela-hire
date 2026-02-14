package com.example.recruitment.controller;

import com.example.recruitment.dto.JobPostingCreateRequest;
import com.example.recruitment.dto.JobPostingResponse;
import com.example.recruitment.entity.EmploymentType;
import com.example.recruitment.entity.ExperienceLevel;
import com.example.recruitment.entity.JobPostingStatus;
import com.example.recruitment.service.JobPostingService;
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
@RequestMapping("/api/job-postings")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
public class JobPostingController {
    
    private static final Logger logger = LoggerFactory.getLogger(JobPostingController.class);
    
    @Autowired
    private JobPostingService jobPostingService;
    
    /**
     * Create new job posting
     * POST /api/job-postings
     */
    @PostMapping
    public ResponseEntity<?> createJobPosting(@Valid @RequestBody JobPostingCreateRequest request,
                                              @RequestParam Long createdBy) {
        try {
            logger.info("Creating job posting: {} by user {}", request.getTitle(), createdBy);
            JobPostingResponse response = jobPostingService.createJobPosting(request, createdBy);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to create job posting: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating job posting", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Update job posting
     * PUT /api/job-postings/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateJobPosting(@PathVariable Long id,
                                              @Valid @RequestBody JobPostingCreateRequest request,
                                              @RequestParam Long updatedBy) {
        try {
            logger.info("Updating job posting: {} by user {}", id, updatedBy);
            JobPostingResponse response = jobPostingService.updateJobPosting(id, request, updatedBy);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.warn("Failed to update job posting {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating job posting {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Get job posting by ID
     * GET /api/job-postings/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getJobPosting(@PathVariable Long id) {
        try {
            JobPostingResponse response = jobPostingService.getJobPosting(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Job posting not found: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting job posting {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Get job posting by slug
     * GET /api/job-postings/slug/{slug}
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getJobPostingBySlug(@PathVariable String slug) {
        try {
            JobPostingResponse response = jobPostingService.getJobPostingBySlug(slug);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Job posting not found with slug: {}", slug);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting job posting by slug {}", slug, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Search job postings with pagination
     * GET /api/job-postings?search={term}&page={page}&size={size}&sort={field}
     */
    @GetMapping
    public ResponseEntity<?> searchJobPostings(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        try {
            Sort.Direction sortDirection = Sort.Direction.fromString(direction);
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            
            Page<JobPostingResponse> results = jobPostingService.searchJobPostings(search, pageable);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Error searching job postings", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Advanced search with filters
     * GET /api/job-postings/search
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchJobPostingsWithFilters(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) EmploymentType employmentType,
            @RequestParam(required = false) ExperienceLevel experienceLevel,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Boolean remoteWork,
            @RequestParam(required = false) JobPostingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "publishedAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        try {
            Sort.Direction sortDirection = Sort.Direction.fromString(direction);
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            
            Page<JobPostingResponse> results = jobPostingService.searchJobPostingsWithFilters(
                    search, department, employmentType, experienceLevel, 
                    location, remoteWork, status, pageable);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Error searching job postings with filters", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Get published jobs for public viewing
     * GET /api/job-postings/published
     */
    @GetMapping("/published")
    public ResponseEntity<?> getPublishedJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "publishedAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        try {
            Sort.Direction sortDirection = Sort.Direction.fromString(direction);
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            
            Page<JobPostingResponse> results = jobPostingService.getPublishedJobs(pageable);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Error getting published jobs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Get job postings by status
     * GET /api/job-postings/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getJobPostingsByStatus(@PathVariable JobPostingStatus status) {
        try {
            List<JobPostingResponse> results = jobPostingService.getJobPostingsByStatus(status);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Error getting job postings by status {}", status, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Get job postings by creator
     * GET /api/job-postings/creator/{createdBy}
     */
    @GetMapping("/creator/{createdBy}")
    public ResponseEntity<?> getJobPostingsByCreator(
            @PathVariable Long createdBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        try {
            Sort.Direction sortDirection = Sort.Direction.fromString(direction);
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            
            Page<JobPostingResponse> results = jobPostingService.getJobPostingsByCreator(createdBy, pageable);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Error getting job postings by creator {}", createdBy, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Submit job posting for approval
     * POST /api/job-postings/{id}/submit-for-approval
     */
    @PostMapping("/{id}/submit-for-approval")
    public ResponseEntity<?> submitForApproval(@PathVariable Long id,
                                               @RequestParam Long submittedBy) {
        try {
            JobPostingResponse response = jobPostingService.submitForApproval(id, submittedBy);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.warn("Failed to submit job posting {} for approval: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error submitting job posting {} for approval", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Approve job posting
     * POST /api/job-postings/{id}/approve
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveJobPosting(@PathVariable Long id,
                                               @RequestParam Long approvedBy,
                                               @RequestParam(required = false) String approvalNotes) {
        try {
            JobPostingResponse response = jobPostingService.approveJobPosting(id, approvedBy, approvalNotes);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.warn("Failed to approve job posting {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error approving job posting {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Reject job posting
     * POST /api/job-postings/{id}/reject
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectJobPosting(@PathVariable Long id,
                                              @RequestParam Long rejectedBy,
                                              @RequestParam String rejectionReason) {
        try {
            JobPostingResponse response = jobPostingService.rejectJobPosting(id, rejectedBy, rejectionReason);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.warn("Failed to reject job posting {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error rejecting job posting {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Publish job posting
     * POST /api/job-postings/{id}/publish
     */
    @PostMapping("/{id}/publish")
    public ResponseEntity<?> publishJobPosting(@PathVariable Long id,
                                               @RequestParam Long publishedBy) {
        try {
            JobPostingResponse response = jobPostingService.publishJobPosting(id, publishedBy);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.warn("Failed to publish job posting {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error publishing job posting {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Unpublish job posting
     * POST /api/job-postings/{id}/unpublish
     */
    @PostMapping("/{id}/unpublish")
    public ResponseEntity<?> unpublishJobPosting(@PathVariable Long id,
                                                 @RequestParam Long unpublishedBy) {
        try {
            JobPostingResponse response = jobPostingService.unpublishJobPosting(id, unpublishedBy);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.warn("Failed to unpublish job posting {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error unpublishing job posting {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Close job posting
     * POST /api/job-postings/{id}/close
     */
    @PostMapping("/{id}/close")
    public ResponseEntity<?> closeJobPosting(@PathVariable Long id,
                                             @RequestParam Long closedBy) {
        try {
            JobPostingResponse response = jobPostingService.closeJobPosting(id, closedBy);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.warn("Failed to close job posting {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error closing job posting {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Delete job posting
     * DELETE /api/job-postings/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJobPosting(@PathVariable Long id,
                                              @RequestParam Long deletedBy) {
        try {
            jobPostingService.deleteJobPosting(id, deletedBy);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.warn("Failed to delete job posting {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting job posting {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Get jobs requiring approval
     * GET /api/job-postings/requiring-approval
     */
    @GetMapping("/requiring-approval")
    public ResponseEntity<?> getJobsRequiringApproval() {
        try {
            List<JobPostingResponse> results = jobPostingService.getJobsRequiringApproval();
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Error getting jobs requiring approval", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Get featured jobs
     * GET /api/job-postings/featured
     */
    @GetMapping("/featured")
    public ResponseEntity<?> getFeaturedJobs() {
        try {
            List<JobPostingResponse> results = jobPostingService.getFeaturedJobs();
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Error getting featured jobs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Get urgent jobs
     * GET /api/job-postings/urgent
     */
    @GetMapping("/urgent")
    public ResponseEntity<?> getUrgentJobs() {
        try {
            List<JobPostingResponse> results = jobPostingService.getUrgentJobs();
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Error getting urgent jobs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }
    
    /**
     * Get job posting statistics
     * GET /api/job-postings/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getJobPostingStatistics() {
        try {
            List<Object[]> statistics = jobPostingService.getJobPostingStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("Error getting job posting statistics", e);
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
}