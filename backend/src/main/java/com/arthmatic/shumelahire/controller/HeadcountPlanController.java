package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.dto.org.HeadcountPlanRequest;
import com.arthmatic.shumelahire.dto.org.HeadcountPlanResponse;
import com.arthmatic.shumelahire.service.HeadcountPlanService;
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
@RequestMapping("/api/org/headcount")
@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER')")
public class HeadcountPlanController {

    private static final Logger logger = LoggerFactory.getLogger(HeadcountPlanController.class);

    @Autowired
    private HeadcountPlanService headcountPlanService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> createHeadcountPlan(@Valid @RequestBody HeadcountPlanRequest request) {
        try {
            HeadcountPlanResponse response = headcountPlanService.createHeadcountPlan(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating headcount plan", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> updateHeadcountPlan(@PathVariable Long id,
                                                  @Valid @RequestBody HeadcountPlanRequest request) {
        try {
            HeadcountPlanResponse response = headcountPlanService.updateHeadcountPlan(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating headcount plan {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getHeadcountPlan(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(headcountPlanService.getHeadcountPlan(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting headcount plan {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping
    public ResponseEntity<List<HeadcountPlanResponse>> getHeadcountPlans(
            @RequestParam(required = false) Integer fiscalYear,
            @RequestParam(required = false) String department) {
        if (fiscalYear != null) {
            return ResponseEntity.ok(headcountPlanService.getHeadcountPlansByYear(fiscalYear));
        } else if (department != null) {
            return ResponseEntity.ok(headcountPlanService.getHeadcountPlansByDepartment(department));
        } else {
            return ResponseEntity.ok(headcountPlanService.getHeadcountPlansByYear(
                    java.time.Year.now().getValue()));
        }
    }

    @GetMapping("/fiscal-years")
    public ResponseEntity<List<Integer>> getFiscalYears() {
        return ResponseEntity.ok(headcountPlanService.getAvailableFiscalYears());
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getYearSummary(
            @RequestParam(defaultValue = "0") int year) {
        int fiscalYear = year > 0 ? year : java.time.Year.now().getValue();
        return ResponseEntity.ok(headcountPlanService.getYearSummary(fiscalYear));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> deleteHeadcountPlan(@PathVariable Long id) {
        try {
            headcountPlanService.deleteHeadcountPlan(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting headcount plan {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }
}
