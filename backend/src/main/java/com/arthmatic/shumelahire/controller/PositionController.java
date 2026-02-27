package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.dto.org.PositionRequest;
import com.arthmatic.shumelahire.dto.org.PositionResponse;
import com.arthmatic.shumelahire.service.PositionService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/org/positions")
@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER', 'EMPLOYEE')")
public class PositionController {

    private static final Logger logger = LoggerFactory.getLogger(PositionController.class);

    @Autowired
    private PositionService positionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> createPosition(@Valid @RequestBody PositionRequest request) {
        try {
            PositionResponse response = positionService.createPosition(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating position", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> updatePosition(@PathVariable Long id,
                                             @Valid @RequestBody PositionRequest request) {
        try {
            PositionResponse response = positionService.updatePosition(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating position {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPosition(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(positionService.getPosition(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting position {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping
    public ResponseEntity<Page<PositionResponse>> getPositions(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isVacant,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "title") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<PositionResponse> positions = positionService.getPositions(department, status, isVacant, pageable);
        return ResponseEntity.ok(positions);
    }

    @GetMapping("/vacant")
    public ResponseEntity<List<PositionResponse>> getVacantPositions() {
        return ResponseEntity.ok(positionService.getVacantPositions());
    }

    @GetMapping("/by-org-unit/{orgUnitId}")
    public ResponseEntity<List<PositionResponse>> getPositionsByOrgUnit(@PathVariable Long orgUnitId) {
        return ResponseEntity.ok(positionService.getPositionsByOrgUnit(orgUnitId));
    }

    @GetMapping("/vacancy-summary")
    public ResponseEntity<Map<String, Object>> getVacancySummary() {
        return ResponseEntity.ok(positionService.getVacancySummary());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> deletePosition(@PathVariable Long id) {
        try {
            positionService.deletePosition(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting position {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }
}
