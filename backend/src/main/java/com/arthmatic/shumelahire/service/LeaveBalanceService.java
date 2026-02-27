package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.config.tenant.TenantContext;
import com.arthmatic.shumelahire.dto.LeaveBalanceAdjustmentRequest;
import com.arthmatic.shumelahire.dto.LeaveBalanceResponse;
import com.arthmatic.shumelahire.entity.*;
import com.arthmatic.shumelahire.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LeaveBalanceService {

    private static final Logger logger = LoggerFactory.getLogger(LeaveBalanceService.class);

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private LeaveAccrualRepository leaveAccrualRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private LeavePolicyRepository leavePolicyRepository;

    @Transactional(readOnly = true)
    public List<LeaveBalanceResponse> getEmployeeBalances(Long employeeId, int year) {
        return leaveBalanceRepository.findByEmployeeIdAndLeaveYear(employeeId, year).stream()
                .map(LeaveBalanceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LeaveBalanceResponse getBalance(Long employeeId, Long leaveTypeId, int year) {
        String tenantId = TenantContext.requireCurrentTenant();
        LeaveBalance balance = leaveBalanceRepository
                .findByTenantIdAndEmployeeIdAndLeaveTypeIdAndLeaveYear(tenantId, employeeId, leaveTypeId, year)
                .orElseThrow(() -> new RuntimeException("Leave balance not found"));
        return LeaveBalanceResponse.fromEntity(balance);
    }

    public LeaveBalance getOrCreateBalance(Long employeeId, Long leaveTypeId, int year) {
        String tenantId = TenantContext.requireCurrentTenant();
        return leaveBalanceRepository
                .findByTenantIdAndEmployeeIdAndLeaveTypeIdAndLeaveYear(tenantId, employeeId, leaveTypeId, year)
                .orElseGet(() -> {
                    Employee employee = employeeRepository.findById(employeeId)
                            .orElseThrow(() -> new RuntimeException("Employee not found: " + employeeId));
                    LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                            .orElseThrow(() -> new RuntimeException("Leave type not found: " + leaveTypeId));

                    LeaveBalance balance = new LeaveBalance();
                    balance.setEmployee(employee);
                    balance.setLeaveType(leaveType);
                    balance.setLeaveYear(year);
                    return leaveBalanceRepository.save(balance);
                });
    }

    public LeaveBalanceResponse adjustBalance(LeaveBalanceAdjustmentRequest request) {
        String tenantId = TenantContext.requireCurrentTenant();
        logger.info("Adjusting leave balance for employee: {}, type: {}, year: {}, amount: {}",
                request.getEmployeeId(), request.getLeaveTypeId(), request.getLeaveYear(), request.getAdjustment());

        LeaveBalance balance = getOrCreateBalance(
                request.getEmployeeId(), request.getLeaveTypeId(), request.getLeaveYear());

        balance.setAdjustment(balance.getAdjustment().add(request.getAdjustment()));
        balance.setAdjustmentReason(request.getReason());

        LeaveBalance saved = leaveBalanceRepository.save(balance);
        return LeaveBalanceResponse.fromEntity(saved);
    }

    public void accrueLeave(Long employeeId, Long leaveTypeId, int year, BigDecimal days,
                            LocalDate periodStart, LocalDate periodEnd, boolean proRated) {
        String tenantId = TenantContext.requireCurrentTenant();

        // Prevent duplicate accrual
        LeaveBalance balance = getOrCreateBalance(employeeId, leaveTypeId, year);
        if (leaveAccrualRepository.existsByLeaveBalanceIdAndAccrualPeriodStartAndAccrualPeriodEnd(
                balance.getId(), periodStart, periodEnd)) {
            logger.warn("Accrual already exists for balance: {}, period: {} - {}", balance.getId(), periodStart, periodEnd);
            return;
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + employeeId));
        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new RuntimeException("Leave type not found: " + leaveTypeId));

        // Create accrual record
        LeaveAccrual accrual = new LeaveAccrual();
        accrual.setEmployee(employee);
        accrual.setLeaveType(leaveType);
        accrual.setLeaveBalance(balance);
        accrual.setAccrualDate(LocalDate.now());
        accrual.setDaysAccrued(days);
        accrual.setAccrualPeriodStart(periodStart);
        accrual.setAccrualPeriodEnd(periodEnd);
        accrual.setProRated(proRated);
        leaveAccrualRepository.save(accrual);

        // Update balance
        balance.setAccrued(balance.getAccrued().add(days));
        balance.setLastAccrualDate(LocalDate.now());
        leaveBalanceRepository.save(balance);

        logger.info("Accrued {} days for employee: {}, leave type: {}", days, employeeId, leaveTypeId);
    }

    public void deductPending(Long employeeId, Long leaveTypeId, int year, BigDecimal days) {
        LeaveBalance balance = getOrCreateBalance(employeeId, leaveTypeId, year);
        balance.setPending(balance.getPending().add(days));
        leaveBalanceRepository.save(balance);
    }

    public void confirmUsed(Long employeeId, Long leaveTypeId, int year, BigDecimal days) {
        LeaveBalance balance = getOrCreateBalance(employeeId, leaveTypeId, year);
        balance.setPending(balance.getPending().subtract(days));
        balance.setUsed(balance.getUsed().add(days));
        leaveBalanceRepository.save(balance);
    }

    public void reversePending(Long employeeId, Long leaveTypeId, int year, BigDecimal days) {
        LeaveBalance balance = getOrCreateBalance(employeeId, leaveTypeId, year);
        balance.setPending(balance.getPending().subtract(days));
        leaveBalanceRepository.save(balance);
    }

    @Transactional(readOnly = true)
    public List<LeaveBalanceResponse> getAllBalancesByYear(int year) {
        String tenantId = TenantContext.requireCurrentTenant();
        return leaveBalanceRepository.findAllByTenantAndYear(tenantId, year).stream()
                .map(LeaveBalanceResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
