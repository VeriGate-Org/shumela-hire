package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.dto.attendance.OvertimeRecordRequest;
import com.arthmatic.shumelahire.dto.attendance.OvertimeRecordResponse;
import com.arthmatic.shumelahire.entity.OvertimeRecord;
import com.arthmatic.shumelahire.service.attendance.OvertimeService;
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
@RequestMapping("/api/overtime")
public class OvertimeController {

    private final OvertimeService overtimeService;

    public OvertimeController(OvertimeService overtimeService) {
        this.overtimeService = overtimeService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER')")
    public ResponseEntity<?> create(@Valid @RequestBody OvertimeRecordRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(overtimeService.create(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER', 'EMPLOYEE')")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(overtimeService.getById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Page<OvertimeRecordResponse>> getByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(overtimeService.getByEmployee(employeeId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "overtimeDate"))));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER')")
    public ResponseEntity<Page<OvertimeRecordResponse>> getPendingApprovals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(overtimeService.getPendingApprovals(PageRequest.of(page, size)));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER')")
    public ResponseEntity<Page<OvertimeRecordResponse>> search(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) OvertimeRecord.OvertimeStatus status,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(overtimeService.search(employeeId, status, startDate, endDate,
                PageRequest.of(page, size)));
    }

    @GetMapping("/bcea-exceedances")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<List<OvertimeRecordResponse>> getBceaExceedances(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(overtimeService.getBceaExceedances(startDate, endDate));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER')")
    public ResponseEntity<?> approve(@PathVariable Long id, @RequestParam Long approvedById) {
        try {
            return ResponseEntity.ok(overtimeService.approve(id, approvedById));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER')")
    public ResponseEntity<?> reject(
            @PathVariable Long id,
            @RequestParam Long rejectedById,
            @RequestParam String reason) {
        try {
            return ResponseEntity.ok(overtimeService.reject(id, rejectedById, reason));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
