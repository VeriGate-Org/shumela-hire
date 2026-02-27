package com.arthmatic.shumelahire.controller.compensation;

import com.arthmatic.shumelahire.dto.compensation.*;
import com.arthmatic.shumelahire.service.compensation.CompensationService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/compensation")
@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER', 'EMPLOYEE')")
public class CompensationController {

    private static final Logger logger = LoggerFactory.getLogger(CompensationController.class);

    @Autowired
    private CompensationService compensationService;

    // ==================== Pay Grades ====================

    @PostMapping("/pay-grades")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> createPayGrade(@Valid @RequestBody PayGradeRequest request) {
        try {
            PayGradeResponse response = compensationService.createPayGrade(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating pay grade", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    @PutMapping("/pay-grades/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> updatePayGrade(@PathVariable Long id, @Valid @RequestBody PayGradeRequest request) {
        try {
            PayGradeResponse response = compensationService.updatePayGrade(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating pay grade {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/pay-grades/{id}")
    public ResponseEntity<?> getPayGrade(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(compensationService.getPayGrade(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting pay grade {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/pay-grades")
    public ResponseEntity<?> getAllPayGrades(@RequestParam(defaultValue = "false") boolean activeOnly) {
        try {
            List<PayGradeResponse> grades = activeOnly
                    ? compensationService.getActivePayGrades()
                    : compensationService.getAllPayGrades();
            return ResponseEntity.ok(grades);
        } catch (Exception e) {
            logger.error("Error getting pay grades", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    @DeleteMapping("/pay-grades/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> deletePayGrade(@PathVariable Long id) {
        try {
            compensationService.deletePayGrade(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting pay grade {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    // ==================== Salary Bands ====================

    @PostMapping("/salary-bands")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> createSalaryBand(@Valid @RequestBody SalaryBandRequest request) {
        try {
            SalaryBandResponse response = compensationService.createSalaryBand(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating salary band", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    @PutMapping("/salary-bands/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> updateSalaryBand(@PathVariable Long id, @Valid @RequestBody SalaryBandRequest request) {
        try {
            SalaryBandResponse response = compensationService.updateSalaryBand(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating salary band {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/salary-bands/{id}")
    public ResponseEntity<?> getSalaryBand(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(compensationService.getSalaryBand(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting salary band {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/pay-grades/{payGradeId}/salary-bands")
    public ResponseEntity<?> getSalaryBandsByPayGrade(@PathVariable Long payGradeId) {
        try {
            return ResponseEntity.ok(compensationService.getSalaryBandsByPayGrade(payGradeId));
        } catch (Exception e) {
            logger.error("Error getting salary bands for pay grade {}", payGradeId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    @DeleteMapping("/salary-bands/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> deleteSalaryBand(@PathVariable Long id) {
        try {
            compensationService.deleteSalaryBand(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting salary band {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    // ==================== Compensation Reviews ====================

    @PostMapping("/reviews")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> createReview(@Valid @RequestBody CompensationReviewRequest request) {
        try {
            CompensationReviewResponse response = compensationService.createReview(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating compensation review", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    @PutMapping("/reviews/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> updateReview(@PathVariable Long id, @Valid @RequestBody CompensationReviewRequest request) {
        try {
            CompensationReviewResponse response = compensationService.updateReview(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating review {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/reviews/{id}")
    public ResponseEntity<?> getReview(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(compensationService.getReview(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting review {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/reviews")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> getAllReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        try {
            Sort.Direction sortDirection = Sort.Direction.fromString(direction);
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            Page<CompensationReviewResponse> results = compensationService.getAllReviews(pageable);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Error getting all reviews", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/employees/{employeeId}/reviews")
    public ResponseEntity<?> getReviewsByEmployee(@PathVariable Long employeeId) {
        try {
            return ResponseEntity.ok(compensationService.getReviewsByEmployee(employeeId));
        } catch (Exception e) {
            logger.error("Error getting reviews for employee {}", employeeId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    @PostMapping("/reviews/{id}/submit")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> submitForApproval(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(compensationService.submitForApproval(id));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error submitting review {} for approval", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    @PostMapping("/reviews/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> approveReview(
            @PathVariable Long id,
            @RequestParam String approver,
            @RequestParam(required = false) BigDecimal approvedSalary,
            @RequestParam(required = false) String notes) {
        try {
            return ResponseEntity.ok(compensationService.approveReview(id, approver, approvedSalary, notes));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error approving review {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    @PostMapping("/reviews/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> rejectReview(
            @PathVariable Long id,
            @RequestParam String approver,
            @RequestParam(required = false) String notes) {
        try {
            return ResponseEntity.ok(compensationService.rejectReview(id, approver, notes));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error rejecting review {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    @PostMapping("/reviews/{id}/implement")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> implementReview(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(compensationService.implementReview(id));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error implementing review {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    // ==================== Total Rewards Statements ====================

    @PostMapping("/total-rewards")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> createTotalRewardsStatement(@Valid @RequestBody TotalRewardsRequest request) {
        try {
            TotalRewardsResponse response = compensationService.createTotalRewardsStatement(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating total rewards statement", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/total-rewards/{id}")
    public ResponseEntity<?> getTotalRewardsStatement(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(compensationService.getTotalRewardsStatement(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting total rewards statement {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/employees/{employeeId}/total-rewards")
    public ResponseEntity<?> getStatementsByEmployee(@PathVariable Long employeeId) {
        try {
            return ResponseEntity.ok(compensationService.getStatementsByEmployee(employeeId));
        } catch (Exception e) {
            logger.error("Error getting statements for employee {}", employeeId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/employees/{employeeId}/total-rewards/latest")
    public ResponseEntity<?> getLatestStatement(@PathVariable Long employeeId) {
        try {
            return ResponseEntity.ok(compensationService.getLatestStatementForEmployee(employeeId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting latest statement for employee {}", employeeId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    // ==================== Benefits ====================

    @PostMapping("/benefits")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> createBenefit(@Valid @RequestBody BenefitRequest request) {
        try {
            BenefitResponse response = compensationService.createBenefit(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating benefit", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    @PutMapping("/benefits/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> updateBenefit(@PathVariable Long id, @Valid @RequestBody BenefitRequest request) {
        try {
            BenefitResponse response = compensationService.updateBenefit(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating benefit {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/benefits/{id}")
    public ResponseEntity<?> getBenefit(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(compensationService.getBenefit(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting benefit {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/employees/{employeeId}/benefits")
    public ResponseEntity<?> getBenefitsByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "false") boolean activeOnly) {
        try {
            List<BenefitResponse> benefits = activeOnly
                    ? compensationService.getActiveBenefitsByEmployee(employeeId)
                    : compensationService.getBenefitsByEmployee(employeeId);
            return ResponseEntity.ok(benefits);
        } catch (Exception e) {
            logger.error("Error getting benefits for employee {}", employeeId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    @DeleteMapping("/benefits/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> deleteBenefit(@PathVariable Long id) {
        try {
            compensationService.deleteBenefit(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting benefit {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error"));
        }
    }

    // ==================== Error Response ====================

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
