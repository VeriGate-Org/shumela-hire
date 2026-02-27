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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveRequestServiceTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private LeaveBalanceService leaveBalanceService;

    @InjectMocks
    private LeaveRequestService leaveRequestService;

    private Employee testEmployee;
    private Employee testManager;
    private LeaveType testLeaveType;
    private LeaveBalance testBalance;
    private LeaveRequestCreateRequest testRequest;

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

        testManager = new Employee();
        testManager.setId(2L);
        testManager.setTenantId("test-tenant");
        testManager.setFirstName("Jane");
        testManager.setLastName("Manager");
        testManager.setEmail("jane@test.com");
        testManager.setEmployeeNumber("EMP002");
        testManager.setHireDate(LocalDate.of(2019, 1, 1));
        testManager.setStatus(EmployeeStatus.ACTIVE);
        testEmployee.setReportingManager(testManager);

        testLeaveType = new LeaveType();
        testLeaveType.setId(1L);
        testLeaveType.setTenantId("test-tenant");
        testLeaveType.setName("Annual Leave");
        testLeaveType.setCode("ANNUAL");
        testLeaveType.setDefaultDaysPerYear(new BigDecimal("21.0"));
        testLeaveType.setActive(true);
        testLeaveType.setRequiresApproval(true);
        testLeaveType.setPaid(true);

        testBalance = new LeaveBalance();
        testBalance.setId(1L);
        testBalance.setTenantId("test-tenant");
        testBalance.setEmployee(testEmployee);
        testBalance.setLeaveType(testLeaveType);
        testBalance.setLeaveYear(LocalDate.now().getYear());
        testBalance.setOpeningBalance(BigDecimal.ZERO);
        testBalance.setAccrued(new BigDecimal("21.0"));
        testBalance.setUsed(BigDecimal.ZERO);
        testBalance.setPending(BigDecimal.ZERO);

        testRequest = new LeaveRequestCreateRequest();
        testRequest.setLeaveTypeId(1L);
        testRequest.setStartDate(LocalDate.now().plusDays(7));
        testRequest.setEndDate(LocalDate.now().plusDays(11));
        testRequest.setNumberOfDays(new BigDecimal("5.0"));
        testRequest.setReason("Family vacation");
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void submitRequest_ValidRequest_CreatesPendingRequest() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(testLeaveType));
        when(leaveRequestRepository.findOverlappingRequests(anyLong(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(leaveBalanceService.getOrCreateBalance(anyLong(), anyLong(), anyInt()))
                .thenReturn(testBalance);

        LeaveRequest savedRequest = new LeaveRequest();
        savedRequest.setId(1L);
        savedRequest.setEmployee(testEmployee);
        savedRequest.setLeaveType(testLeaveType);
        savedRequest.setStartDate(testRequest.getStartDate());
        savedRequest.setEndDate(testRequest.getEndDate());
        savedRequest.setNumberOfDays(testRequest.getNumberOfDays());
        savedRequest.setStatus(LeaveRequestStatus.PENDING);
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(savedRequest);

        LeaveRequestResponse result = leaveRequestService.submitRequest(1L, testRequest);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getNumberOfDays()).isEqualByComparingTo(new BigDecimal("5.0"));
        verify(leaveBalanceService).deductPending(eq(1L), eq(1L), anyInt(), eq(new BigDecimal("5.0")));
    }

    @Test
    void submitRequest_OverlappingDates_ThrowsException() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(testLeaveType));

        LeaveRequest existingRequest = new LeaveRequest();
        existingRequest.setId(99L);
        when(leaveRequestRepository.findOverlappingRequests(anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(existingRequest));

        assertThatThrownBy(() -> leaveRequestService.submitRequest(1L, testRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("overlaps");
    }

    @Test
    void submitRequest_InsufficientBalance_ThrowsException() {
        testBalance.setAccrued(new BigDecimal("2.0")); // Only 2 days available

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(testLeaveType));
        when(leaveRequestRepository.findOverlappingRequests(anyLong(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(leaveBalanceService.getOrCreateBalance(anyLong(), anyLong(), anyInt()))
                .thenReturn(testBalance);

        assertThatThrownBy(() -> leaveRequestService.submitRequest(1L, testRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient");
    }

    @Test
    void submitRequest_InvalidDateRange_ThrowsException() {
        testRequest.setStartDate(LocalDate.now().plusDays(10));
        testRequest.setEndDate(LocalDate.now().plusDays(5)); // end before start

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(testLeaveType));

        assertThatThrownBy(() -> leaveRequestService.submitRequest(1L, testRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("End date");
    }

    @Test
    void managerApprove_PendingRequest_ApprovesSuccessfully() {
        LeaveRequest pendingRequest = new LeaveRequest();
        pendingRequest.setId(1L);
        pendingRequest.setEmployee(testEmployee);
        pendingRequest.setLeaveType(testLeaveType);
        pendingRequest.setStartDate(LocalDate.now().plusDays(7));
        pendingRequest.setEndDate(LocalDate.now().plusDays(11));
        pendingRequest.setNumberOfDays(new BigDecimal("5.0"));
        pendingRequest.setStatus(LeaveRequestStatus.PENDING);

        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(pendingRequest));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(testManager));

        LeaveRequest approvedRequest = new LeaveRequest();
        approvedRequest.setId(1L);
        approvedRequest.setEmployee(testEmployee);
        approvedRequest.setLeaveType(testLeaveType);
        approvedRequest.setStartDate(pendingRequest.getStartDate());
        approvedRequest.setEndDate(pendingRequest.getEndDate());
        approvedRequest.setNumberOfDays(pendingRequest.getNumberOfDays());
        approvedRequest.setStatus(LeaveRequestStatus.MANAGER_APPROVED);
        approvedRequest.setApprovedBy(testManager);
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(approvedRequest);

        LeaveApprovalRequest approval = new LeaveApprovalRequest();
        approval.setNotes("Approved");

        LeaveRequestResponse result = leaveRequestService.managerApprove(1L, 2L, approval);

        assertThat(result.getStatus()).isEqualTo("MANAGER_APPROVED");
    }

    @Test
    void reject_PendingRequest_RejectsAndReversesBalance() {
        LeaveRequest pendingRequest = new LeaveRequest();
        pendingRequest.setId(1L);
        pendingRequest.setEmployee(testEmployee);
        pendingRequest.setLeaveType(testLeaveType);
        pendingRequest.setStartDate(LocalDate.now().plusDays(7));
        pendingRequest.setEndDate(LocalDate.now().plusDays(11));
        pendingRequest.setNumberOfDays(new BigDecimal("5.0"));
        pendingRequest.setStatus(LeaveRequestStatus.PENDING);

        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(pendingRequest));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(testManager));

        LeaveRequest rejectedRequest = new LeaveRequest();
        rejectedRequest.setId(1L);
        rejectedRequest.setEmployee(testEmployee);
        rejectedRequest.setLeaveType(testLeaveType);
        rejectedRequest.setStartDate(pendingRequest.getStartDate());
        rejectedRequest.setEndDate(pendingRequest.getEndDate());
        rejectedRequest.setNumberOfDays(pendingRequest.getNumberOfDays());
        rejectedRequest.setStatus(LeaveRequestStatus.REJECTED);
        rejectedRequest.setRejectedBy(testManager);
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(rejectedRequest);

        LeaveRejectionRequest rejection = new LeaveRejectionRequest();
        rejection.setReason("Team coverage insufficient");

        LeaveRequestResponse result = leaveRequestService.reject(1L, 2L, rejection);

        assertThat(result.getStatus()).isEqualTo("REJECTED");
        verify(leaveBalanceService).reversePending(eq(1L), eq(1L), anyInt(), eq(new BigDecimal("5.0")));
    }

    @Test
    void cancel_PendingRequest_CancelsAndReversesBalance() {
        LeaveRequest pendingRequest = new LeaveRequest();
        pendingRequest.setId(1L);
        pendingRequest.setEmployee(testEmployee);
        pendingRequest.setLeaveType(testLeaveType);
        pendingRequest.setStartDate(LocalDate.now().plusDays(7));
        pendingRequest.setEndDate(LocalDate.now().plusDays(11));
        pendingRequest.setNumberOfDays(new BigDecimal("5.0"));
        pendingRequest.setStatus(LeaveRequestStatus.PENDING);

        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(pendingRequest));

        LeaveRequest cancelledRequest = new LeaveRequest();
        cancelledRequest.setId(1L);
        cancelledRequest.setEmployee(testEmployee);
        cancelledRequest.setLeaveType(testLeaveType);
        cancelledRequest.setStartDate(pendingRequest.getStartDate());
        cancelledRequest.setEndDate(pendingRequest.getEndDate());
        cancelledRequest.setNumberOfDays(pendingRequest.getNumberOfDays());
        cancelledRequest.setStatus(LeaveRequestStatus.CANCELLED);
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(cancelledRequest);

        LeaveRequestResponse result = leaveRequestService.cancel(1L, "Plans changed");

        assertThat(result.getStatus()).isEqualTo("CANCELLED");
        verify(leaveBalanceService).reversePending(eq(1L), eq(1L), anyInt(), eq(new BigDecimal("5.0")));
    }

    @Test
    void managerApprove_NonPendingRequest_ThrowsException() {
        LeaveRequest approvedRequest = new LeaveRequest();
        approvedRequest.setId(1L);
        approvedRequest.setStatus(LeaveRequestStatus.APPROVED);

        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(approvedRequest));

        assertThatThrownBy(() -> leaveRequestService.managerApprove(1L, 2L, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not in PENDING status");
    }
}
