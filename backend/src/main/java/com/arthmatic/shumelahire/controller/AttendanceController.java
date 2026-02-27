package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.dto.attendance.AttendanceRecordResponse;
import com.arthmatic.shumelahire.dto.attendance.ClockInRequest;
import com.arthmatic.shumelahire.dto.attendance.ClockOutRequest;
import com.arthmatic.shumelahire.entity.AttendanceRecord;
import com.arthmatic.shumelahire.service.attendance.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/clock-in")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'EMPLOYEE', 'HIRING_MANAGER', 'RECRUITER', 'INTERVIEWER', 'EXECUTIVE')")
    public ResponseEntity<?> clockIn(@Valid @RequestBody ClockInRequest request) {
        try {
            AttendanceRecordResponse response = attendanceService.clockIn(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/clock-out")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'EMPLOYEE', 'HIRING_MANAGER', 'RECRUITER', 'INTERVIEWER', 'EXECUTIVE')")
    public ResponseEntity<?> clockOut(@Valid @RequestBody ClockOutRequest request) {
        try {
            AttendanceRecordResponse response = attendanceService.clockOut(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'EMPLOYEE', 'HIRING_MANAGER')")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(attendanceService.getById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'EMPLOYEE', 'HIRING_MANAGER')")
    public ResponseEntity<Page<AttendanceRecordResponse>> getByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(attendanceService.getByEmployee(employeeId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "attendanceDate"))));
    }

    @GetMapping("/employee/{employeeId}/range")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'EMPLOYEE', 'HIRING_MANAGER')")
    public ResponseEntity<List<AttendanceRecordResponse>> getByEmployeeAndDateRange(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(attendanceService.getByEmployeeAndDateRange(employeeId, startDate, endDate));
    }

    @GetMapping("/daily")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER')")
    public ResponseEntity<Page<AttendanceRecordResponse>> getByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) AttendanceRecord.AttendanceStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(attendanceService.getByDateAndStatus(date, status,
                PageRequest.of(page, size)));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER')")
    public ResponseEntity<Page<AttendanceRecordResponse>> search(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String department,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(attendanceService.search(employeeId, department, startDate, endDate,
                PageRequest.of(page, size)));
    }

    @GetMapping("/late-arrivals")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER')")
    public ResponseEntity<List<AttendanceRecordResponse>> getLateArrivals(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(attendanceService.getLateArrivals(startDate, endDate));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER')")
    public ResponseEntity<?> approve(
            @PathVariable Long id,
            @RequestParam Long approvedById) {
        try {
            return ResponseEntity.ok(attendanceService.approve(id, approvedById));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
