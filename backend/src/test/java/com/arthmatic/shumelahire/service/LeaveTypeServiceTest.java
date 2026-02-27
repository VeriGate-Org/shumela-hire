package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.config.tenant.TenantContext;
import com.arthmatic.shumelahire.dto.LeaveTypeRequest;
import com.arthmatic.shumelahire.dto.LeaveTypeResponse;
import com.arthmatic.shumelahire.entity.LeaveType;
import com.arthmatic.shumelahire.repository.LeaveTypeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveTypeServiceTest {

    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    @InjectMocks
    private LeaveTypeService leaveTypeService;

    private LeaveType testLeaveType;
    private LeaveTypeRequest testRequest;

    @BeforeEach
    void setUp() {
        TenantContext.setCurrentTenant("test-tenant");

        testLeaveType = new LeaveType();
        testLeaveType.setId(1L);
        testLeaveType.setTenantId("test-tenant");
        testLeaveType.setName("Annual Leave");
        testLeaveType.setCode("ANNUAL");
        testLeaveType.setDescription("Annual leave entitlement");
        testLeaveType.setDefaultDaysPerYear(new BigDecimal("21.0"));
        testLeaveType.setActive(true);
        testLeaveType.setRequiresApproval(true);
        testLeaveType.setPaid(true);

        testRequest = new LeaveTypeRequest();
        testRequest.setName("Annual Leave");
        testRequest.setCode("ANNUAL");
        testRequest.setDescription("Annual leave entitlement");
        testRequest.setDefaultDaysPerYear(new BigDecimal("21.0"));
        testRequest.setRequiresApproval(true);
        testRequest.setPaid(true);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void createLeaveType_ValidRequest_ReturnsResponse() {
        when(leaveTypeRepository.existsByTenantIdAndCode("test-tenant", "ANNUAL")).thenReturn(false);
        when(leaveTypeRepository.save(any(LeaveType.class))).thenReturn(testLeaveType);

        LeaveTypeResponse result = leaveTypeService.createLeaveType(testRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Annual Leave");
        assertThat(result.getCode()).isEqualTo("ANNUAL");
        assertThat(result.getDefaultDaysPerYear()).isEqualByComparingTo(new BigDecimal("21.0"));
        verify(leaveTypeRepository).save(any(LeaveType.class));
    }

    @Test
    void createLeaveType_DuplicateCode_ThrowsException() {
        when(leaveTypeRepository.existsByTenantIdAndCode("test-tenant", "ANNUAL")).thenReturn(true);

        assertThatThrownBy(() -> leaveTypeService.createLeaveType(testRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(leaveTypeRepository, never()).save(any(LeaveType.class));
    }

    @Test
    void getLeaveType_ExistingId_ReturnsResponse() {
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(testLeaveType));

        LeaveTypeResponse result = leaveTypeService.getLeaveType(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Annual Leave");
    }

    @Test
    void getLeaveType_NonExistingId_ThrowsException() {
        when(leaveTypeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> leaveTypeService.getLeaveType(999L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void getActiveLeaveTypes_ReturnsActiveTypes() {
        LeaveType sickLeave = new LeaveType();
        sickLeave.setId(2L);
        sickLeave.setTenantId("test-tenant");
        sickLeave.setName("Sick Leave");
        sickLeave.setCode("SICK");
        sickLeave.setDefaultDaysPerYear(new BigDecimal("30.0"));
        sickLeave.setActive(true);
        sickLeave.setPaid(true);

        when(leaveTypeRepository.findByTenantIdAndActiveTrue("test-tenant"))
                .thenReturn(Arrays.asList(testLeaveType, sickLeave));

        List<LeaveTypeResponse> result = leaveTypeService.getActiveLeaveTypes();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("ANNUAL");
        assertThat(result.get(1).getCode()).isEqualTo("SICK");
    }

    @Test
    void deactivateLeaveType_ExistingId_DeactivatesType() {
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(testLeaveType));
        when(leaveTypeRepository.save(any(LeaveType.class))).thenReturn(testLeaveType);

        leaveTypeService.deactivateLeaveType(1L);

        assertThat(testLeaveType.isActive()).isFalse();
        verify(leaveTypeRepository).save(testLeaveType);
    }

    @Test
    void updateLeaveType_ValidRequest_ReturnsUpdatedResponse() {
        testRequest.setName("Updated Annual Leave");
        testRequest.setDefaultDaysPerYear(new BigDecimal("25.0"));

        LeaveType updatedType = new LeaveType();
        updatedType.setId(1L);
        updatedType.setTenantId("test-tenant");
        updatedType.setName("Updated Annual Leave");
        updatedType.setCode("ANNUAL");
        updatedType.setDefaultDaysPerYear(new BigDecimal("25.0"));
        updatedType.setActive(true);
        updatedType.setPaid(true);

        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(testLeaveType));
        when(leaveTypeRepository.save(any(LeaveType.class))).thenReturn(updatedType);

        LeaveTypeResponse result = leaveTypeService.updateLeaveType(1L, testRequest);

        assertThat(result.getName()).isEqualTo("Updated Annual Leave");
        assertThat(result.getDefaultDaysPerYear()).isEqualByComparingTo(new BigDecimal("25.0"));
    }
}
