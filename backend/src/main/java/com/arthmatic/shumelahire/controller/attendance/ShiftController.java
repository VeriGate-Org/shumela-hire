package com.arthmatic.shumelahire.controller.attendance;

import com.arthmatic.shumelahire.dto.attendance.*;
import com.arthmatic.shumelahire.service.attendance.ShiftService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shifts")
@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
public class ShiftController {

    private static final Logger logger = LoggerFactory.getLogger(ShiftController.class);

    @Autowired
    private ShiftService shiftService;

    // ==================== Shift Endpoints ====================

    @PostMapping
    public ResponseEntity<?> createShift(@Valid @RequestBody ShiftRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(shiftService.createShift(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating shift", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateShift(@PathVariable Long id, @Valid @RequestBody ShiftRequest request) {
        try {
            return ResponseEntity.ok(shiftService.updateShift(id, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'EMPLOYEE')")
    public ResponseEntity<?> getShift(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(shiftService.getShift(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<ShiftResponse>> getAllShifts() {
        return ResponseEntity.ok(shiftService.getAllShifts());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<ShiftResponse>> getActiveShifts() {
        return ResponseEntity.ok(shiftService.getActiveShifts());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShift(@PathVariable Long id) {
        try {
            shiftService.deleteShift(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==================== Shift Pattern Endpoints ====================

    @PostMapping("/patterns")
    public ResponseEntity<?> createPattern(@Valid @RequestBody ShiftPatternRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(shiftService.createShiftPattern(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/patterns/{id}")
    public ResponseEntity<?> updatePattern(@PathVariable Long id, @Valid @RequestBody ShiftPatternRequest request) {
        try {
            return ResponseEntity.ok(shiftService.updateShiftPattern(id, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/patterns/{id}")
    public ResponseEntity<?> getPattern(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(shiftService.getShiftPattern(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/patterns")
    public ResponseEntity<List<ShiftPatternResponse>> getAllPatterns() {
        return ResponseEntity.ok(shiftService.getAllShiftPatterns());
    }

    @DeleteMapping("/patterns/{id}")
    public ResponseEntity<?> deletePattern(@PathVariable Long id) {
        try {
            shiftService.deleteShiftPattern(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==================== Schedule Endpoints ====================

    @PostMapping("/schedules")
    public ResponseEntity<?> createSchedule(@Valid @RequestBody ShiftScheduleRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(shiftService.createSchedule(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/schedules/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'EMPLOYEE')")
    public ResponseEntity<?> getSchedule(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(shiftService.getSchedule(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/schedules")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'EMPLOYEE')")
    public ResponseEntity<?> getSchedules(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return ResponseEntity.ok(shiftService.getSchedulesByDateRange(startDate, endDate));
    }

    @GetMapping("/schedules/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'EMPLOYEE')")
    public ResponseEntity<?> getEmployeeSchedules(
            @PathVariable Long employeeId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return ResponseEntity.ok(shiftService.getEmployeeSchedules(employeeId, startDate, endDate));
    }

    @DeleteMapping("/schedules/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id) {
        try {
            shiftService.deleteSchedule(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    static class ErrorResponse {
        private final String message;
        public ErrorResponse(String message) { this.message = message; }
        public String getMessage() { return message; }
    }
}
