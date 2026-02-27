package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.config.tenant.TenantContext;
import com.arthmatic.shumelahire.dto.LeaveApprovalRequest;
import com.arthmatic.shumelahire.dto.LeaveRejectionRequest;
import com.arthmatic.shumelahire.dto.LeaveRequestCreateRequest;
import com.arthmatic.shumelahire.dto.LeaveRequestResponse;
import com.arthmatic.shumelahire.entity.*;
import com.arthmatic.shumelahire.repository.EmployeeRepository;
import com.arthmatic.shumelahire.repository.LeaveRequestRepository;
import com.arthmatic.shumelahire.repository.LeaveTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class LeaveRequestService {

    private static final Logger logger = LoggerFactory.getLogger(LeaveRequestService.class);

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    public LeaveRequestResponse submitRequest(Long employeeId, LeaveRequestCreateRequest request) {
        logger.info("Submitting leave request for employee: {}", employeeId);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + employeeId));

        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new RuntimeException("Leave type not found: " + request.getLeaveTypeId()));

        // Validate dates
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date must be on or after start date");
        }

        // Check for overlapping requests
        List<LeaveRequest> overlapping = leaveRequestRepository.findOverlappingRequests(
                employeeId, request.getStartDate(), request.getEndDate());
        if (!overlapping.isEmpty()) {
            throw new IllegalArgumentException("Leave request overlaps with an existing request");
        }

        // Validate minimum notice
        if (leaveType.getMinDaysNotice() != null && leaveType.getMinDaysNotice() > 0) {
            long daysNotice = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), request.getStartDate());
            if (daysNotice < leaveType.getMinDaysNotice()) {
                throw new IllegalArgumentException(
                        "Minimum " + leaveType.getMinDaysNotice() + " days notice required for " + leaveType.getName());
            }
        }

        // Validate max consecutive days
        if (leaveType.getMaxConsecutiveDays() != null && request.getNumberOfDays()
                .compareTo(new java.math.BigDecimal(leaveType.getMaxConsecutiveDays())) > 0) {
            throw new IllegalArgumentException(
                    "Maximum " + leaveType.getMaxConsecutiveDays() + " consecutive days allowed for " + leaveType.getName());
        }

        // Check balance
        int year = request.getStartDate().getYear();
        LeaveBalance balance = leaveBalanceService.getOrCreateBalance(employeeId, request.getLeaveTypeId(), year);
        if (balance.getAvailableBalance().compareTo(request.getNumberOfDays()) < 0) {
            throw new IllegalArgumentException("Insufficient leave balance. Available: " + balance.getAvailableBalance());
        }

        // Create leave request
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setStartDate(request.getStartDate());
        leaveRequest.setEndDate(request.getEndDate());
        leaveRequest.setNumberOfDays(request.getNumberOfDays());
        leaveRequest.setHalfDay(request.isHalfDay());
        if (request.getHalfDayPeriod() != null) {
            leaveRequest.setHalfDayPeriod(HalfDayPeriod.valueOf(request.getHalfDayPeriod()));
        }
        leaveRequest.setReason(request.getReason());

        if (request.getDelegateId() != null) {
            Employee delegate = employeeRepository.findById(request.getDelegateId())
                    .orElseThrow(() -> new RuntimeException("Delegate not found: " + request.getDelegateId()));
            leaveRequest.setDelegate(delegate);
        }

        // Auto-approve if not requiring approval
        if (!leaveType.isRequiresApproval()) {
            leaveRequest.setStatus(LeaveRequestStatus.APPROVED);
            leaveBalanceService.confirmUsed(employeeId, request.getLeaveTypeId(), year, request.getNumberOfDays());
        } else {
            leaveRequest.setStatus(LeaveRequestStatus.PENDING);
            leaveBalanceService.deductPending(employeeId, request.getLeaveTypeId(), year, request.getNumberOfDays());
        }

        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);
        logger.info("Leave request created: {} with status: {}", saved.getId(), saved.getStatus());
        return LeaveRequestResponse.fromEntity(saved);
    }

    public LeaveRequestResponse managerApprove(Long requestId, Long managerId, LeaveApprovalRequest approval) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Leave request not found: " + requestId));

        if (leaveRequest.getStatus() != LeaveRequestStatus.PENDING) {
            throw new IllegalStateException("Leave request is not in PENDING status");
        }

        Employee manager = employeeRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found: " + managerId));

        leaveRequest.setApprovedBy(manager);
        leaveRequest.setApprovedAt(LocalDateTime.now());
        leaveRequest.setApprovalNotes(approval != null ? approval.getNotes() : null);
        leaveRequest.setStatus(LeaveRequestStatus.MANAGER_APPROVED);

        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);
        logger.info("Leave request {} manager-approved by {}", requestId, managerId);
        return LeaveRequestResponse.fromEntity(saved);
    }

    public LeaveRequestResponse hrApprove(Long requestId, Long hrEmployeeId, LeaveApprovalRequest approval) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Leave request not found: " + requestId));

        if (leaveRequest.getStatus() != LeaveRequestStatus.MANAGER_APPROVED) {
            throw new IllegalStateException("Leave request is not in MANAGER_APPROVED status");
        }

        Employee hrEmployee = employeeRepository.findById(hrEmployeeId)
                .orElseThrow(() -> new RuntimeException("HR employee not found: " + hrEmployeeId));

        leaveRequest.setHrApprovedBy(hrEmployee);
        leaveRequest.setHrApprovedAt(LocalDateTime.now());
        leaveRequest.setStatus(LeaveRequestStatus.APPROVED);

        // Move from pending to used in balance
        int year = leaveRequest.getStartDate().getYear();
        leaveBalanceService.confirmUsed(
                leaveRequest.getEmployee().getId(),
                leaveRequest.getLeaveType().getId(),
                year,
                leaveRequest.getNumberOfDays());

        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);
        logger.info("Leave request {} HR-approved by {}", requestId, hrEmployeeId);
        return LeaveRequestResponse.fromEntity(saved);
    }

    public LeaveRequestResponse reject(Long requestId, Long rejectorId, LeaveRejectionRequest rejection) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Leave request not found: " + requestId));

        if (leaveRequest.getStatus() != LeaveRequestStatus.PENDING
                && leaveRequest.getStatus() != LeaveRequestStatus.MANAGER_APPROVED) {
            throw new IllegalStateException("Leave request cannot be rejected in current status: " + leaveRequest.getStatus());
        }

        Employee rejector = employeeRepository.findById(rejectorId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + rejectorId));

        leaveRequest.setRejectedBy(rejector);
        leaveRequest.setRejectedAt(LocalDateTime.now());
        leaveRequest.setRejectionReason(rejection.getReason());
        leaveRequest.setStatus(LeaveRequestStatus.REJECTED);

        // Reverse pending balance
        int year = leaveRequest.getStartDate().getYear();
        leaveBalanceService.reversePending(
                leaveRequest.getEmployee().getId(),
                leaveRequest.getLeaveType().getId(),
                year,
                leaveRequest.getNumberOfDays());

        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);
        logger.info("Leave request {} rejected by {}", requestId, rejectorId);
        return LeaveRequestResponse.fromEntity(saved);
    }

    public LeaveRequestResponse cancel(Long requestId, String cancellationReason) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Leave request not found: " + requestId));

        if (leaveRequest.getStatus() == LeaveRequestStatus.REJECTED
                || leaveRequest.getStatus() == LeaveRequestStatus.CANCELLED) {
            throw new IllegalStateException("Leave request is already " + leaveRequest.getStatus());
        }

        int year = leaveRequest.getStartDate().getYear();
        Long employeeId = leaveRequest.getEmployee().getId();
        Long leaveTypeId = leaveRequest.getLeaveType().getId();

        if (leaveRequest.getStatus() == LeaveRequestStatus.APPROVED) {
            // Reverse used balance
            LeaveBalance balance = leaveBalanceService.getOrCreateBalance(employeeId, leaveTypeId, year);
            balance.setUsed(balance.getUsed().subtract(leaveRequest.getNumberOfDays()));
        } else {
            // Reverse pending balance
            leaveBalanceService.reversePending(employeeId, leaveTypeId, year, leaveRequest.getNumberOfDays());
        }

        leaveRequest.setStatus(LeaveRequestStatus.CANCELLED);
        leaveRequest.setCancelledAt(LocalDateTime.now());
        leaveRequest.setCancellationReason(cancellationReason);

        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);
        logger.info("Leave request {} cancelled", requestId);
        return LeaveRequestResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public LeaveRequestResponse getRequest(Long requestId) {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Leave request not found: " + requestId));
        return LeaveRequestResponse.fromEntity(request);
    }

    @Transactional(readOnly = true)
    public Page<LeaveRequestResponse> getEmployeeRequests(Long employeeId, Pageable pageable) {
        return leaveRequestRepository.findByEmployeeId(employeeId, pageable)
                .map(LeaveRequestResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<LeaveRequestResponse> getPendingForManager(Long managerId, Pageable pageable) {
        return leaveRequestRepository.findPendingForManager(LeaveRequestStatus.PENDING, managerId, pageable)
                .map(LeaveRequestResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<LeaveRequestResponse> getPendingHrApproval(Pageable pageable) {
        String tenantId = TenantContext.requireCurrentTenant();
        return leaveRequestRepository.findPendingHrApproval(tenantId, pageable)
                .map(LeaveRequestResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<LeaveRequestResponse> searchRequests(Long employeeId, LeaveRequestStatus status,
                                                      Long leaveTypeId, LocalDate startDate,
                                                      LocalDate endDate, Pageable pageable) {
        String tenantId = TenantContext.requireCurrentTenant();
        return leaveRequestRepository.findByFilters(tenantId, employeeId, status, leaveTypeId, startDate, endDate, pageable)
                .map(LeaveRequestResponse::fromEntity);
    }
}
