package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.dto.attendance.*;
import com.arthmatic.shumelahire.service.attendance.ShiftService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
public class ShiftController {

    private final ShiftService shiftService;

    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    // ---- Shift CRUD ----

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> createShift(@Valid @RequestBody ShiftRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(shiftService.createShift(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> updateShift(@PathVariable Long id, @Valid @RequestBody ShiftRequest request) {
        try {
            return ResponseEntity.ok(shiftService.updateShift(id, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER', 'EMPLOYEE')")
    public ResponseEntity<?> getShift(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(shiftService.getShift(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<ShiftResponse>> getAllShifts() {
        return ResponseEntity.ok(shiftService.getAllShifts());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<ShiftResponse>> getActiveShifts() {
        return ResponseEntity.ok(shiftService.getActiveShifts());
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> toggleActive(@PathVariable Long id, @RequestParam boolean active) {
        try {
            return ResponseEntity.ok(shiftService.toggleShiftActive(id, active));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> deleteShift(@PathVariable Long id) {
        try {
            shiftService.deleteShift(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // ---- Shift Pattern CRUD ----

    @PostMapping("/patterns")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> createPattern(@Valid @RequestBody ShiftPatternRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(shiftService.createPattern(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/patterns/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> updatePattern(@PathVariable Long id, @Valid @RequestBody ShiftPatternRequest request) {
        try {
            return ResponseEntity.ok(shiftService.updatePattern(id, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/patterns/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER')")
    public ResponseEntity<?> getPattern(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(shiftService.getPattern(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/patterns")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER')")
    public ResponseEntity<List<ShiftPatternResponse>> getAllPatterns() {
        return ResponseEntity.ok(shiftService.getAllPatterns());
    }

    @DeleteMapping("/patterns/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> deletePattern(@PathVariable Long id) {
        try {
            shiftService.deletePattern(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // ---- Scheduling ----

    @PostMapping("/schedules")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER')")
    public ResponseEntity<?> assignSchedule(@Valid @RequestBody ShiftScheduleRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(shiftService.assignSchedule(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/schedules/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<ShiftScheduleResponse>> getEmployeeSchedule(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(shiftService.getEmployeeSchedule(employeeId, startDate, endDate));
    }

    @GetMapping("/schedules/department")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER')")
    public ResponseEntity<Page<ShiftScheduleResponse>> getDepartmentSchedule(
            @RequestParam String department,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(shiftService.getDepartmentSchedule(department, startDate, endDate,
                PageRequest.of(page, size)));
    }

    @DeleteMapping("/schedules/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER')")
    public ResponseEntity<?> cancelSchedule(@PathVariable Long id) {
        try {
            shiftService.cancelSchedule(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
