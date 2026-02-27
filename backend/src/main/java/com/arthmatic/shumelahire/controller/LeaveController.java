package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.dto.*;
import com.arthmatic.shumelahire.entity.LeaveRequestStatus;
import com.arthmatic.shumelahire.service.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/leave")
@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER', 'EMPLOYEE')")
public class LeaveController {

    private static final Logger logger = LoggerFactory.getLogger(LeaveController.class);

    @Autowired
    private LeaveTypeService leaveTypeService;

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    @Autowired
    private LeaveEncashmentService leaveEncashmentService;

    @Autowired
    private LeaveDelegationService leaveDelegationService;

    @Autowired
    private PublicHolidayService publicHolidayService;

    // ==================== Leave Types ====================

    @GetMapping("/types")
    public ResponseEntity<List<LeaveTypeResponse>> getActiveLeaveTypes() {
        return ResponseEntity.ok(leaveTypeService.getActiveLeaveTypes());
    }

    @GetMapping("/types/{id}")
    public ResponseEntity<LeaveTypeResponse> getLeaveType(@PathVariable Long id) {
        return ResponseEntity.ok(leaveTypeService.getLeaveType(id));
    }

    // ==================== Leave Requests ====================

    @PostMapping("/requests/employee/{employeeId}")
    public ResponseEntity<LeaveRequestResponse> submitRequest(
            @PathVariable Long employeeId,
            @Valid @RequestBody LeaveRequestCreateRequest request) {
        LeaveRequestResponse response = leaveRequestService.submitRequest(employeeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/requests/{id}")
    public ResponseEntity<LeaveRequestResponse> getRequest(@PathVariable Long id) {
        return ResponseEntity.ok(leaveRequestService.getRequest(id));
    }

    @GetMapping("/requests/employee/{employeeId}")
    public ResponseEntity<Page<LeaveRequestResponse>> getEmployeeRequests(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "submittedAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        return ResponseEntity.ok(leaveRequestService.getEmployeeRequests(employeeId, pageable));
    }

    @PostMapping("/requests/{id}/manager-approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER')")
    public ResponseEntity<LeaveRequestResponse> managerApprove(
            @PathVariable Long id,
            @RequestParam Long managerId,
            @RequestBody(required = false) LeaveApprovalRequest approval) {
        return ResponseEntity.ok(leaveRequestService.managerApprove(id, managerId, approval));
    }

    @PostMapping("/requests/{id}/hr-approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<LeaveRequestResponse> hrApprove(
            @PathVariable Long id,
            @RequestParam Long hrEmployeeId,
            @RequestBody(required = false) LeaveApprovalRequest approval) {
        return ResponseEntity.ok(leaveRequestService.hrApprove(id, hrEmployeeId, approval));
    }

    @PostMapping("/requests/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER')")
    public ResponseEntity<LeaveRequestResponse> reject(
            @PathVariable Long id,
            @RequestParam Long rejectorId,
            @Valid @RequestBody LeaveRejectionRequest rejection) {
        return ResponseEntity.ok(leaveRequestService.reject(id, rejectorId, rejection));
    }

    @PostMapping("/requests/{id}/cancel")
    public ResponseEntity<LeaveRequestResponse> cancel(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(leaveRequestService.cancel(id, reason));
    }

    @GetMapping("/requests/pending/manager/{managerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HIRING_MANAGER')")
    public ResponseEntity<Page<LeaveRequestResponse>> getPendingForManager(
            @PathVariable Long managerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "submittedAt"));
        return ResponseEntity.ok(leaveRequestService.getPendingForManager(managerId, pageable));
    }

    @GetMapping("/requests/pending/hr")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<Page<LeaveRequestResponse>> getPendingHrApproval(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "submittedAt"));
        return ResponseEntity.ok(leaveRequestService.getPendingHrApproval(pageable));
    }

    @GetMapping("/requests/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<Page<LeaveRequestResponse>> searchRequests(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long leaveTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "submittedAt"));
        LeaveRequestStatus requestStatus = status != null ? LeaveRequestStatus.valueOf(status) : null;
        return ResponseEntity.ok(leaveRequestService.searchRequests(
                employeeId, requestStatus, leaveTypeId, startDate, endDate, pageable));
    }

    // ==================== Leave Balances ====================

    @GetMapping("/balances/employee/{employeeId}")
    public ResponseEntity<List<LeaveBalanceResponse>> getEmployeeBalances(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "#{T(java.time.Year).now().getValue()}") int year) {
        return ResponseEntity.ok(leaveBalanceService.getEmployeeBalances(employeeId, year));
    }

    @GetMapping("/balances/employee/{employeeId}/type/{leaveTypeId}")
    public ResponseEntity<LeaveBalanceResponse> getBalance(
            @PathVariable Long employeeId,
            @PathVariable Long leaveTypeId,
            @RequestParam(defaultValue = "#{T(java.time.Year).now().getValue()}") int year) {
        return ResponseEntity.ok(leaveBalanceService.getBalance(employeeId, leaveTypeId, year));
    }

    // ==================== Leave Encashments ====================

    @PostMapping("/encashments/employee/{employeeId}")
    public ResponseEntity<LeaveEncashmentResponse> requestEncashment(
            @PathVariable Long employeeId,
            @Valid @RequestBody LeaveEncashmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                leaveEncashmentService.requestEncashment(employeeId, request));
    }

    @GetMapping("/encashments/employee/{employeeId}")
    public ResponseEntity<Page<LeaveEncashmentResponse>> getEmployeeEncashments(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "requestedAt"));
        return ResponseEntity.ok(leaveEncashmentService.getEmployeeEncashments(employeeId, pageable));
    }

    // ==================== Public Holidays ====================

    @GetMapping("/holidays")
    public ResponseEntity<List<PublicHolidayResponse>> getHolidays(
            @RequestParam int year) {
        return ResponseEntity.ok(publicHolidayService.getHolidaysByYear(year));
    }

    // ==================== Leave Delegations ====================

    @PostMapping("/delegations/delegator/{delegatorId}")
    public ResponseEntity<LeaveDelegationResponse> createDelegation(
            @PathVariable Long delegatorId,
            @Valid @RequestBody LeaveDelegationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                leaveDelegationService.createDelegation(delegatorId, request));
    }

    @GetMapping("/delegations/delegator/{delegatorId}")
    public ResponseEntity<List<LeaveDelegationResponse>> getMyDelegations(
            @PathVariable Long delegatorId) {
        return ResponseEntity.ok(leaveDelegationService.getMyDelegations(delegatorId));
    }

    @GetMapping("/delegations/delegate/{delegateId}")
    public ResponseEntity<List<LeaveDelegationResponse>> getDelegationsToMe(
            @PathVariable Long delegateId) {
        return ResponseEntity.ok(leaveDelegationService.getDelegationsToMe(delegateId));
    }

    @DeleteMapping("/delegations/{id}")
    public ResponseEntity<Void> revokeDelegation(@PathVariable Long id) {
        leaveDelegationService.revokeDelegation(id);
        return ResponseEntity.noContent().build();
    }
}
