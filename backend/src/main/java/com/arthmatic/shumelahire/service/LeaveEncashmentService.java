package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.config.tenant.TenantContext;
import com.arthmatic.shumelahire.dto.LeaveEncashmentRequest;
import com.arthmatic.shumelahire.dto.LeaveEncashmentResponse;
import com.arthmatic.shumelahire.entity.*;
import com.arthmatic.shumelahire.repository.EmployeeRepository;
import com.arthmatic.shumelahire.repository.LeaveEncashmentRepository;
import com.arthmatic.shumelahire.repository.LeaveTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;

@Service
@Transactional
public class LeaveEncashmentService {

    private static final Logger logger = LoggerFactory.getLogger(LeaveEncashmentService.class);

    @Autowired
    private LeaveEncashmentRepository leaveEncashmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    public LeaveEncashmentResponse requestEncashment(Long employeeId, LeaveEncashmentRequest request) {
        logger.info("Encashment request from employee: {} for leave type: {}", employeeId, request.getLeaveTypeId());

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + employeeId));

        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new RuntimeException("Leave type not found: " + request.getLeaveTypeId()));

        int currentYear = Year.now().getValue();
        LeaveBalance balance = leaveBalanceService.getOrCreateBalance(employeeId, request.getLeaveTypeId(), currentYear);

        // Validate sufficient balance
        if (balance.getAvailableBalance().compareTo(request.getDaysToEncash()) < 0) {
            throw new IllegalArgumentException("Insufficient balance for encashment. Available: " + balance.getAvailableBalance());
        }

        BigDecimal totalAmount = request.getDailyRate().multiply(request.getDaysToEncash());

        LeaveEncashment encashment = new LeaveEncashment();
        encashment.setEmployee(employee);
        encashment.setLeaveType(leaveType);
        encashment.setLeaveBalance(balance);
        encashment.setDaysEncashed(request.getDaysToEncash());
        encashment.setDailyRate(request.getDailyRate());
        encashment.setTotalAmount(totalAmount);
        encashment.setStatus(EncashmentStatus.PENDING);

        LeaveEncashment saved = leaveEncashmentRepository.save(encashment);
        logger.info("Encashment request created: {} for {} days", saved.getId(), request.getDaysToEncash());
        return LeaveEncashmentResponse.fromEntity(saved);
    }

    public LeaveEncashmentResponse approveEncashment(Long encashmentId, Long approverId) {
        LeaveEncashment encashment = leaveEncashmentRepository.findById(encashmentId)
                .orElseThrow(() -> new RuntimeException("Encashment not found: " + encashmentId));

        if (encashment.getStatus() != EncashmentStatus.PENDING) {
            throw new IllegalStateException("Encashment is not in PENDING status");
        }

        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + approverId));

        encashment.setApprovedBy(approver);
        encashment.setApprovedAt(LocalDateTime.now());
        encashment.setStatus(EncashmentStatus.APPROVED);

        // Deduct from balance
        LeaveBalance balance = encashment.getLeaveBalance();
        balance.setEncashed(balance.getEncashed().add(encashment.getDaysEncashed()));

        LeaveEncashment saved = leaveEncashmentRepository.save(encashment);
        logger.info("Encashment {} approved by {}", encashmentId, approverId);
        return LeaveEncashmentResponse.fromEntity(saved);
    }

    public LeaveEncashmentResponse rejectEncashment(Long encashmentId, String reason) {
        LeaveEncashment encashment = leaveEncashmentRepository.findById(encashmentId)
                .orElseThrow(() -> new RuntimeException("Encashment not found: " + encashmentId));

        if (encashment.getStatus() != EncashmentStatus.PENDING) {
            throw new IllegalStateException("Encashment is not in PENDING status");
        }

        encashment.setStatus(EncashmentStatus.REJECTED);
        encashment.setRejectionReason(reason);

        LeaveEncashment saved = leaveEncashmentRepository.save(encashment);
        logger.info("Encashment {} rejected", encashmentId);
        return LeaveEncashmentResponse.fromEntity(saved);
    }

    public LeaveEncashmentResponse markAsPaid(Long encashmentId, String payrollReference) {
        LeaveEncashment encashment = leaveEncashmentRepository.findById(encashmentId)
                .orElseThrow(() -> new RuntimeException("Encashment not found: " + encashmentId));

        if (encashment.getStatus() != EncashmentStatus.APPROVED) {
            throw new IllegalStateException("Encashment must be approved before marking as paid");
        }

        encashment.setStatus(EncashmentStatus.PAID);
        encashment.setPaidAt(LocalDateTime.now());
        encashment.setPayrollReference(payrollReference);

        LeaveEncashment saved = leaveEncashmentRepository.save(encashment);
        logger.info("Encashment {} marked as paid with reference: {}", encashmentId, payrollReference);
        return LeaveEncashmentResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public Page<LeaveEncashmentResponse> getEmployeeEncashments(Long employeeId, Pageable pageable) {
        return leaveEncashmentRepository.findByEmployeeId(employeeId, pageable)
                .map(LeaveEncashmentResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<LeaveEncashmentResponse> getPendingEncashments(Pageable pageable) {
        String tenantId = TenantContext.requireCurrentTenant();
        return leaveEncashmentRepository.findByTenantIdAndStatus(tenantId, EncashmentStatus.PENDING, pageable)
                .map(LeaveEncashmentResponse::fromEntity);
    }
}
