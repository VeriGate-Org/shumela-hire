package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.config.tenant.TenantContext;
import com.arthmatic.shumelahire.dto.LeaveBalanceAdjustmentRequest;
import com.arthmatic.shumelahire.dto.LeaveBalanceResponse;
import com.arthmatic.shumelahire.entity.*;
import com.arthmatic.shumelahire.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveBalanceServiceTest {

    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;

    @Mock
    private LeaveAccrualRepository leaveAccrualRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    @Mock
    private LeavePolicyRepository leavePolicyRepository;

    @InjectMocks
    private LeaveBalanceService leaveBalanceService;

    private Employee testEmployee;
    private LeaveType testLeaveType;
    private LeaveBalance testBalance;

    @BeforeEach
    void setUp() {
        TenantContext.setCurrentTenant("test-tenant");

        testEmployee = new Employee();
        testEmployee.setId(1L);
        testEmployee.setTenantId("test-tenant");
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setEmail("john@test.com");
        testEmployee.setEmployeeNumber("EMP001");
        testEmployee.setHireDate(LocalDate.of(2020, 1, 1));
        testEmployee.setStatus(EmployeeStatus.ACTIVE);

        testLeaveType = new LeaveType();
        testLeaveType.setId(1L);
        testLeaveType.setTenantId("test-tenant");
        testLeaveType.setName("Annual Leave");
        testLeaveType.setCode("ANNUAL");
        testLeaveType.setDefaultDaysPerYear(new BigDecimal("21.0"));

        testBalance = new LeaveBalance();
        testBalance.setId(1L);
        testBalance.setTenantId("test-tenant");
        testBalance.setEmployee(testEmployee);
        testBalance.setLeaveType(testLeaveType);
        testBalance.setLeaveYear(2026);
        testBalance.setOpeningBalance(BigDecimal.ZERO);
        testBalance.setAccrued(new BigDecimal("21.0"));
        testBalance.setUsed(new BigDecimal("5.0"));
        testBalance.setPending(new BigDecimal("2.0"));
        testBalance.setCarriedOver(BigDecimal.ZERO);
        testBalance.setAdjustment(BigDecimal.ZERO);
        testBalance.setEncashed(BigDecimal.ZERO);
        testBalance.setForfeited(BigDecimal.ZERO);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void getEmployeeBalances_ReturnsBalances() {
        when(leaveBalanceRepository.findByEmployeeIdAndLeaveYear(1L, 2026))
                .thenReturn(Arrays.asList(testBalance));

        List<LeaveBalanceResponse> result = leaveBalanceService.getEmployeeBalances(1L, 2026);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLeaveTypeName()).isEqualTo("Annual Leave");
        assertThat(result.get(0).getAccrued()).isEqualByComparingTo(new BigDecimal("21.0"));
        assertThat(result.get(0).getUsed()).isEqualByComparingTo(new BigDecimal("5.0"));
    }

    @Test
    void getOrCreateBalance_ExistingBalance_ReturnsExisting() {
        when(leaveBalanceRepository.findByTenantIdAndEmployeeIdAndLeaveTypeIdAndLeaveYear(
                "test-tenant", 1L, 1L, 2026))
                .thenReturn(Optional.of(testBalance));

        LeaveBalance result = leaveBalanceService.getOrCreateBalance(1L, 1L, 2026);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(leaveBalanceRepository, never()).save(any(LeaveBalance.class));
    }

    @Test
    void getOrCreateBalance_NoBalance_CreatesNew() {
        when(leaveBalanceRepository.findByTenantIdAndEmployeeIdAndLeaveTypeIdAndLeaveYear(
                "test-tenant", 1L, 1L, 2026))
                .thenReturn(Optional.empty());
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(testLeaveType));

        LeaveBalance newBalance = new LeaveBalance();
        newBalance.setId(2L);
        newBalance.setEmployee(testEmployee);
        newBalance.setLeaveType(testLeaveType);
        newBalance.setLeaveYear(2026);
        when(leaveBalanceRepository.save(any(LeaveBalance.class))).thenReturn(newBalance);

        LeaveBalance result = leaveBalanceService.getOrCreateBalance(1L, 1L, 2026);

        assertThat(result).isNotNull();
        verify(leaveBalanceRepository).save(any(LeaveBalance.class));
    }

    @Test
    void adjustBalance_AddsAdjustment() {
        when(leaveBalanceRepository.findByTenantIdAndEmployeeIdAndLeaveTypeIdAndLeaveYear(
                "test-tenant", 1L, 1L, 2026))
                .thenReturn(Optional.of(testBalance));
        when(leaveBalanceRepository.save(any(LeaveBalance.class))).thenReturn(testBalance);

        LeaveBalanceAdjustmentRequest request = new LeaveBalanceAdjustmentRequest();
        request.setEmployeeId(1L);
        request.setLeaveTypeId(1L);
        request.setLeaveYear(2026);
        request.setAdjustment(new BigDecimal("3.0"));
        request.setReason("Service years bonus");

        LeaveBalanceResponse result = leaveBalanceService.adjustBalance(request);

        assertThat(testBalance.getAdjustment()).isEqualByComparingTo(new BigDecimal("3.0"));
        assertThat(testBalance.getAdjustmentReason()).isEqualTo("Service years bonus");
        verify(leaveBalanceRepository).save(testBalance);
    }

    @Test
    void deductPending_IncreasesPending() {
        when(leaveBalanceRepository.findByTenantIdAndEmployeeIdAndLeaveTypeIdAndLeaveYear(
                "test-tenant", 1L, 1L, 2026))
                .thenReturn(Optional.of(testBalance));
        when(leaveBalanceRepository.save(any(LeaveBalance.class))).thenReturn(testBalance);

        leaveBalanceService.deductPending(1L, 1L, 2026, new BigDecimal("3.0"));

        assertThat(testBalance.getPending()).isEqualByComparingTo(new BigDecimal("5.0")); // 2.0 + 3.0
        verify(leaveBalanceRepository).save(testBalance);
    }

    @Test
    void confirmUsed_MovesPendingToUsed() {
        when(leaveBalanceRepository.findByTenantIdAndEmployeeIdAndLeaveTypeIdAndLeaveYear(
                "test-tenant", 1L, 1L, 2026))
                .thenReturn(Optional.of(testBalance));
        when(leaveBalanceRepository.save(any(LeaveBalance.class))).thenReturn(testBalance);

        leaveBalanceService.confirmUsed(1L, 1L, 2026, new BigDecimal("2.0"));

        assertThat(testBalance.getPending()).isEqualByComparingTo(BigDecimal.ZERO); // 2.0 - 2.0
        assertThat(testBalance.getUsed()).isEqualByComparingTo(new BigDecimal("7.0")); // 5.0 + 2.0
        verify(leaveBalanceRepository).save(testBalance);
    }

    @Test
    void reversePending_DecreasesPending() {
        when(leaveBalanceRepository.findByTenantIdAndEmployeeIdAndLeaveTypeIdAndLeaveYear(
                "test-tenant", 1L, 1L, 2026))
                .thenReturn(Optional.of(testBalance));
        when(leaveBalanceRepository.save(any(LeaveBalance.class))).thenReturn(testBalance);

        leaveBalanceService.reversePending(1L, 1L, 2026, new BigDecimal("2.0"));

        assertThat(testBalance.getPending()).isEqualByComparingTo(BigDecimal.ZERO); // 2.0 - 2.0
        verify(leaveBalanceRepository).save(testBalance);
    }

    @Test
    void accrueLeave_CreatesAccrualAndUpdatesBalance() {
        when(leaveBalanceRepository.findByTenantIdAndEmployeeIdAndLeaveTypeIdAndLeaveYear(
                "test-tenant", 1L, 1L, 2026))
                .thenReturn(Optional.of(testBalance));
        when(leaveAccrualRepository.existsByLeaveBalanceIdAndAccrualPeriodStartAndAccrualPeriodEnd(
                anyLong(), any(), any())).thenReturn(false);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(testLeaveType));
        when(leaveAccrualRepository.save(any(LeaveAccrual.class))).thenReturn(new LeaveAccrual());
        when(leaveBalanceRepository.save(any(LeaveBalance.class))).thenReturn(testBalance);

        LocalDate periodStart = LocalDate.of(2026, 1, 1);
        LocalDate periodEnd = LocalDate.of(2026, 1, 31);

        leaveBalanceService.accrueLeave(1L, 1L, 2026, new BigDecimal("1.75"),
                periodStart, periodEnd, false);

        verify(leaveAccrualRepository).save(any(LeaveAccrual.class));
        assertThat(testBalance.getAccrued()).isEqualByComparingTo(new BigDecimal("22.75")); // 21.0 + 1.75
    }

    @Test
    void accrueLeave_DuplicatePeriod_SkipsAccrual() {
        when(leaveBalanceRepository.findByTenantIdAndEmployeeIdAndLeaveTypeIdAndLeaveYear(
                "test-tenant", 1L, 1L, 2026))
                .thenReturn(Optional.of(testBalance));
        when(leaveAccrualRepository.existsByLeaveBalanceIdAndAccrualPeriodStartAndAccrualPeriodEnd(
                eq(1L), any(), any())).thenReturn(true);

        LocalDate periodStart = LocalDate.of(2026, 1, 1);
        LocalDate periodEnd = LocalDate.of(2026, 1, 31);

        leaveBalanceService.accrueLeave(1L, 1L, 2026, new BigDecimal("1.75"),
                periodStart, periodEnd, false);

        verify(leaveAccrualRepository, never()).save(any(LeaveAccrual.class));
        verify(leaveBalanceRepository, never()).save(any(LeaveBalance.class));
    }
}
