package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.dto.attendance.OvertimeRecordResponse;
import com.arthmatic.shumelahire.entity.OvertimeRecord;
import com.arthmatic.shumelahire.service.OvertimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/overtime")
@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER', 'EMPLOYEE')")
public class OvertimeController {

    private static final Logger logger = LoggerFactory.getLogger(OvertimeController.class);

    @Autowired
    private OvertimeService overtimeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> createOvertimeRecord(
            @RequestParam Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam BigDecimal hours,
            @RequestParam OvertimeRecord.OvertimeType type,
            @RequestParam(required = false) String reason) {
        try {
            OvertimeRecordResponse response = overtimeService.createOvertimeRecord(
                    employeeId, date, hours, type, reason);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Overtime creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating overtime record", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal server error"));
        }
    }

    @PostMapping("/detect")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> detectOvertime(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<OvertimeRecordResponse> detected = overtimeService.detectOvertime(date);
            return ResponseEntity.ok(Map.of("detected", detected.size(), "records", detected));
        } catch (Exception e) {
            logger.error("Error detecting overtime", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal server error"));
        }
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> approveOvertime(@PathVariable Long id, @RequestParam Long approverId) {
        try {
            return ResponseEntity.ok(overtimeService.approveOvertime(id, approverId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> rejectOvertime(@PathVariable Long id,
                                            @RequestParam Long approverId,
                                            @RequestParam String reason) {
        try {
            return ResponseEntity.ok(overtimeService.rejectOvertime(id, approverId, reason));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getEmployeeOvertime(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(overtimeService.getEmployeeOvertime(employeeId, start, end));
    }

    @GetMapping("/employee/{employeeId}/weekly-hours")
    public ResponseEntity<?> getWeeklyOvertimeHours(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        BigDecimal hours = overtimeService.getWeeklyOvertimeHours(employeeId, date);
        return ResponseEntity.ok(Map.of(
                "weeklyOvertimeHours", hours,
                "maxWeeklyHours", OvertimeRecord.MAX_WEEKLY_OVERTIME_HOURS,
                "remaining", OvertimeRecord.MAX_WEEKLY_OVERTIME_HOURS.subtract(hours)));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> getPendingApprovals(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(overtimeService.getPendingApprovals(start, end));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> getOvertimeByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(overtimeService.getOvertimeByDateRange(start, end));
    }
}
