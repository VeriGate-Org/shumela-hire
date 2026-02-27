package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.service.WorkforceAnalyticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/org/workforce")
@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER')")
public class WorkforceAnalyticsController {

    private static final Logger logger = LoggerFactory.getLogger(WorkforceAnalyticsController.class);

    @Autowired
    private WorkforceAnalyticsService workforceAnalyticsService;

    @GetMapping("/demographics")
    public ResponseEntity<?> getDemographics() {
        try {
            return ResponseEntity.ok(workforceAnalyticsService.getDemographics());
        } catch (Exception e) {
            logger.error("Error computing demographics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/turnover")
    public ResponseEntity<?> getTurnoverAnalysis() {
        try {
            return ResponseEntity.ok(workforceAnalyticsService.getTurnoverAnalysis());
        } catch (Exception e) {
            logger.error("Error computing turnover analysis", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/span-of-control")
    public ResponseEntity<?> getSpanOfControl() {
        try {
            return ResponseEntity.ok(workforceAnalyticsService.getSpanOfControl());
        } catch (Exception e) {
            logger.error("Error computing span of control", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/cost")
    public ResponseEntity<?> getWorkforceCost() {
        try {
            return ResponseEntity.ok(workforceAnalyticsService.getWorkforceCost());
        } catch (Exception e) {
            logger.error("Error computing workforce cost", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }
}
