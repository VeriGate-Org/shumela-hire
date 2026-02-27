package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.dto.attendance.*;
import com.arthmatic.shumelahire.service.AttendanceReportService;
import com.arthmatic.shumelahire.service.AttendanceService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/attendance")
@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER', 'EMPLOYEE')")
public class AttendanceController {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceController.class);

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private AttendanceReportService reportService;

    // ==================== Clock In/Out ====================

    @PostMapping("/clock-in")
    public ResponseEntity<?> clockIn(@Valid @RequestBody ClockInRequest request) {
        try {
            AttendanceRecordResponse response = attendanceService.clockIn(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Clock-in failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error during clock-in", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @PostMapping("/clock-out")
    public ResponseEntity<?> clockOut(@Valid @RequestBody ClockOutRequest request) {
        try {
            AttendanceRecordResponse response = attendanceService.clockOut(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Clock-out failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error during clock-out", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @PostMapping("/auto-clock-out")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> autoClockOut() {
        try {
            int count = attendanceService.autoClockOutOpenRecords();
            return ResponseEntity.ok(Map.of("autoClockOutCount", count));
        } catch (Exception e) {
            logger.error("Error during auto clock-out", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    // ==================== Attendance Records ====================

    @GetMapping("/{id}")
    public ResponseEntity<?> getRecord(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(attendanceService.getRecord(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting attendance record", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/employee/{employeeId}/today")
    public ResponseEntity<?> getTodayRecord(@PathVariable Long employeeId) {
        try {
            AttendanceRecordResponse response = attendanceService.getTodayRecord(employeeId);
            if (response == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting today's record", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getEmployeeRecords(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        try {
            List<AttendanceRecordResponse> records = attendanceService.getEmployeeRecords(employeeId, start, end);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            logger.error("Error getting employee attendance records", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> getRecordsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<AttendanceRecordResponse> records = attendanceService.getRecordsByDateRange(
                    start, end, PageRequest.of(page, size));
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            logger.error("Error getting attendance records", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    // ==================== Reports ====================

    @GetMapping("/reports/monthly-register")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> getMonthlyRegister(
            @RequestParam Long employeeId,
            @RequestParam int year,
            @RequestParam int month) {
        try {
            return ResponseEntity.ok(reportService.getMonthlyRegister(employeeId, year, month));
        } catch (Exception e) {
            logger.error("Error generating monthly register", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/reports/overtime-summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> getOvertimeSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        try {
            return ResponseEntity.ok(reportService.getOvertimeSummary(start, end));
        } catch (Exception e) {
            logger.error("Error generating overtime summary", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/reports/punctuality")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> getPunctualityReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        try {
            return ResponseEntity.ok(reportService.getPunctualityReport(start, end));
        } catch (Exception e) {
            logger.error("Error generating punctuality report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    @GetMapping("/reports/payroll-export")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<?> getPayrollExport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        try {
            return ResponseEntity.ok(reportService.getPayrollExport(start, end));
        } catch (Exception e) {
            logger.error("Error generating payroll export", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

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
