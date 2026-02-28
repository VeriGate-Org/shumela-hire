package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.dto.attendance.*;
import com.arthmatic.shumelahire.entity.ShiftPattern;
import com.arthmatic.shumelahire.service.ShiftService;
import com.arthmatic.shumelahire.service.ShiftSwapService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shifts")
@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER', 'EMPLOYEE')")
public class ShiftController {

    private static final Logger logger = LoggerFactory.getLogger(ShiftController.class);

    @Autowired
    private ShiftService shiftService;

    @Autowired
    private ShiftSwapService swapService;

    // ==================== Shift CRUD ====================

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> createShift(@Valid @RequestBody ShiftRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(shiftService.createShift(request));
        } catch (Exception e) {
            logger.error("Error creating shift", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal server error"));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> updateShift(@PathVariable Long id, @Valid @RequestBody ShiftRequest request) {
        try {
            return ResponseEntity.ok(shiftService.updateShift(id, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getShift(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(shiftService.getShift(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllActiveShifts() {
        return ResponseEntity.ok(shiftService.getAllActiveShifts());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> deactivateShift(@PathVariable Long id) {
        try {
            shiftService.deactivateShift(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== Schedules ====================

    @PostMapping("/schedules")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> assignSchedule(@Valid @RequestBody ShiftScheduleRequest request) {
        try {
            List<ShiftScheduleResponse> response = shiftService.assignSchedule(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error assigning schedule", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal server error"));
        }
    }

    @PostMapping("/schedules/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> bulkAssignSchedule(@Valid @RequestBody ShiftScheduleRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(shiftService.bulkAssignSchedule(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error bulk-assigning schedule", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal server error"));
        }
    }

    @GetMapping("/schedules/employee/{employeeId}")
    public ResponseEntity<?> getEmployeeSchedule(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(shiftService.getEmployeeSchedule(employeeId, start, end));
    }

    @GetMapping("/schedules")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> getSchedulesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(shiftService.getSchedulesByDateRange(start, end));
    }

    // ==================== Patterns ====================

    @GetMapping("/patterns")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> getAllPatterns() {
        return ResponseEntity.ok(shiftService.getAllActivePatterns());
    }

    @GetMapping("/patterns/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> getPattern(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(shiftService.getPattern(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/patterns")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> createPattern(@Valid @RequestBody ShiftPattern pattern) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shiftService.createPattern(pattern));
    }

    @PostMapping("/patterns/{patternId}/auto-assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> autoAssignFromPattern(
            @PathVariable Long patternId,
            @RequestParam List<Long> employeeIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            int count = shiftService.autoAssignFromPattern(patternId, employeeIds, startDate, endDate);
            return ResponseEntity.ok(Map.of("assignedCount", count));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ==================== Shift Swaps ====================

    @PostMapping("/swaps")
    public ResponseEntity<?> requestSwap(
            @RequestParam Long requesterEmployeeId,
            @RequestParam Long targetEmployeeId,
            @RequestParam Long requesterScheduleId,
            @RequestParam Long targetScheduleId,
            @RequestParam(required = false) String reason) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    swapService.requestSwap(requesterEmployeeId, targetEmployeeId,
                            requesterScheduleId, targetScheduleId, reason));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/swaps/{id}/accept")
    public ResponseEntity<?> acceptSwap(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(swapService.targetAcceptSwap(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/swaps/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> approveSwap(@PathVariable Long id, @RequestParam Long approverId) {
        try {
            return ResponseEntity.ok(swapService.approveSwap(id, approverId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/swaps/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> rejectSwap(@PathVariable Long id,
                                        @RequestParam Long approverId,
                                        @RequestParam String reason) {
        try {
            return ResponseEntity.ok(swapService.rejectSwap(id, approverId, reason));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/swaps/{id}/cancel")
    public ResponseEntity<?> cancelSwap(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(swapService.cancelSwap(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/swaps/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> getPendingSwaps() {
        return ResponseEntity.ok(swapService.getPendingSwaps());
    }

    @GetMapping("/swaps/employee/{employeeId}")
    public ResponseEntity<?> getEmployeeSwaps(@PathVariable Long employeeId) {
        return ResponseEntity.ok(Map.of(
                "requested", swapService.getSwapsByRequester(employeeId),
                "received", swapService.getSwapsByTarget(employeeId)));
    }
}
