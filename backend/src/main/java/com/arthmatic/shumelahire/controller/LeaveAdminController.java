package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.dto.*;
import com.arthmatic.shumelahire.service.*;
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

@RestController
@RequestMapping("/api/leave/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
public class LeaveAdminController {

    private static final Logger logger = LoggerFactory.getLogger(LeaveAdminController.class);

    @Autowired
    private LeaveTypeService leaveTypeService;

    @Autowired
    private LeavePolicyService leavePolicyService;

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    @Autowired
    private LeaveEncashmentService leaveEncashmentService;

    @Autowired
    private PublicHolidayService publicHolidayService;

    // ==================== Leave Type Management ====================

    @GetMapping("/types")
    public ResponseEntity<List<LeaveTypeResponse>> getAllLeaveTypes() {
        return ResponseEntity.ok(leaveTypeService.getAllLeaveTypes());
    }

    @PostMapping("/types")
    public ResponseEntity<LeaveTypeResponse> createLeaveType(@Valid @RequestBody LeaveTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leaveTypeService.createLeaveType(request));
    }

    @PutMapping("/types/{id}")
    public ResponseEntity<LeaveTypeResponse> updateLeaveType(
            @PathVariable Long id,
            @Valid @RequestBody LeaveTypeRequest request) {
        return ResponseEntity.ok(leaveTypeService.updateLeaveType(id, request));
    }

    @DeleteMapping("/types/{id}")
    public ResponseEntity<Void> deactivateLeaveType(@PathVariable Long id) {
        leaveTypeService.deactivateLeaveType(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== Leave Policy Management ====================

    @GetMapping("/policies")
    public ResponseEntity<List<LeavePolicyResponse>> getActivePolicies() {
        return ResponseEntity.ok(leavePolicyService.getActivePolicies());
    }

    @GetMapping("/policies/{id}")
    public ResponseEntity<LeavePolicyResponse> getPolicy(@PathVariable Long id) {
        return ResponseEntity.ok(leavePolicyService.getPolicy(id));
    }

    @GetMapping("/policies/type/{leaveTypeId}")
    public ResponseEntity<List<LeavePolicyResponse>> getPoliciesByType(@PathVariable Long leaveTypeId) {
        return ResponseEntity.ok(leavePolicyService.getPoliciesByLeaveType(leaveTypeId));
    }

    @PostMapping("/policies")
    public ResponseEntity<LeavePolicyResponse> createPolicy(@Valid @RequestBody LeavePolicyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leavePolicyService.createPolicy(request));
    }

    @PutMapping("/policies/{id}")
    public ResponseEntity<LeavePolicyResponse> updatePolicy(
            @PathVariable Long id,
            @Valid @RequestBody LeavePolicyRequest request) {
        return ResponseEntity.ok(leavePolicyService.updatePolicy(id, request));
    }

    @DeleteMapping("/policies/{id}")
    public ResponseEntity<Void> deactivatePolicy(@PathVariable Long id) {
        leavePolicyService.deactivatePolicy(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== Balance Management ====================

    @PostMapping("/balances/adjust")
    public ResponseEntity<LeaveBalanceResponse> adjustBalance(
            @Valid @RequestBody LeaveBalanceAdjustmentRequest request) {
        return ResponseEntity.ok(leaveBalanceService.adjustBalance(request));
    }

    @GetMapping("/balances/year/{year}")
    public ResponseEntity<List<LeaveBalanceResponse>> getAllBalancesByYear(@PathVariable int year) {
        return ResponseEntity.ok(leaveBalanceService.getAllBalancesByYear(year));
    }

    // ==================== Encashment Management ====================

    @GetMapping("/encashments/pending")
    public ResponseEntity<Page<LeaveEncashmentResponse>> getPendingEncashments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "requestedAt"));
        return ResponseEntity.ok(leaveEncashmentService.getPendingEncashments(pageable));
    }

    @PostMapping("/encashments/{id}/approve")
    public ResponseEntity<LeaveEncashmentResponse> approveEncashment(
            @PathVariable Long id,
            @RequestParam Long approverId) {
        return ResponseEntity.ok(leaveEncashmentService.approveEncashment(id, approverId));
    }

    @PostMapping("/encashments/{id}/reject")
    public ResponseEntity<LeaveEncashmentResponse> rejectEncashment(
            @PathVariable Long id,
            @RequestParam String reason) {
        return ResponseEntity.ok(leaveEncashmentService.rejectEncashment(id, reason));
    }

    @PostMapping("/encashments/{id}/paid")
    public ResponseEntity<LeaveEncashmentResponse> markEncashmentAsPaid(
            @PathVariable Long id,
            @RequestParam String payrollReference) {
        return ResponseEntity.ok(leaveEncashmentService.markAsPaid(id, payrollReference));
    }

    // ==================== Public Holiday Management ====================

    @PostMapping("/holidays")
    public ResponseEntity<PublicHolidayResponse> createHoliday(@Valid @RequestBody PublicHolidayRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(publicHolidayService.createHoliday(request));
    }

    @PutMapping("/holidays/{id}")
    public ResponseEntity<PublicHolidayResponse> updateHoliday(
            @PathVariable Long id,
            @Valid @RequestBody PublicHolidayRequest request) {
        return ResponseEntity.ok(publicHolidayService.updateHoliday(id, request));
    }

    @DeleteMapping("/holidays/{id}")
    public ResponseEntity<Void> deleteHoliday(@PathVariable Long id) {
        publicHolidayService.deleteHoliday(id);
        return ResponseEntity.noContent().build();
    }
}
