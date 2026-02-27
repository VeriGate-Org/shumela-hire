package com.arthmatic.shumelahire.service.attendance;

import com.arthmatic.shumelahire.dto.attendance.OvertimeRecordRequest;
import com.arthmatic.shumelahire.dto.attendance.OvertimeRecordResponse;
import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.EmployeeStatus;
import com.arthmatic.shumelahire.entity.attendance.OvertimeRecord;
import com.arthmatic.shumelahire.entity.attendance.OvertimeStatus;
import com.arthmatic.shumelahire.entity.attendance.OvertimeType;
import com.arthmatic.shumelahire.repository.EmployeeRepository;
import com.arthmatic.shumelahire.repository.attendance.AttendanceRecordRepository;
import com.arthmatic.shumelahire.repository.attendance.OvertimeRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OvertimeServiceTest {

    @Mock
    private OvertimeRecordRepository overtimeRecordRepository;

    @Mock
    private AttendanceRecordRepository attendanceRecordRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private OvertimeService overtimeService;

    private Employee testEmployee;
    private OvertimeRecord testRecord;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee();
        testEmployee.setId(1L);
        testEmployee.setFirstName("Thandi");
        testEmployee.setLastName("Nkosi");
        testEmployee.setEmail("thandi@company.com");
        testEmployee.setStatus(EmployeeStatus.ACTIVE);
        testEmployee.setHireDate(LocalDate.of(2024, 1, 15));
        testEmployee.setTenantId("test-tenant");

        testRecord = new OvertimeRecord();
        testRecord.setId(1L);
        testRecord.setEmployee(testEmployee);
        testRecord.setOvertimeDate(LocalDate.of(2026, 2, 23)); // Monday
        testRecord.setOvertimeType(OvertimeType.WEEKDAY);
        testRecord.setHours(new BigDecimal("2.0"));
        testRecord.setRateMultiplier(new BigDecimal("1.5"));
        testRecord.setStatus(OvertimeStatus.PENDING);
        testRecord.setCreatedAt(LocalDateTime.now());
        testRecord.setUpdatedAt(LocalDateTime.now());
    }

    // ==================== SA BCEA Rate Multiplier Tests ====================

    @Test
    void calculateRateMultiplier_WeekdayOvertime_Returns1_5() {
        // Monday - BCEA 1.5x
        LocalDate monday = LocalDate.of(2026, 2, 23);
        BigDecimal rate = overtimeService.calculateRateMultiplier(OvertimeType.WEEKDAY, monday);

        assertThat(rate).isEqualByComparingTo(new BigDecimal("1.5"));
    }

    @Test
    void calculateRateMultiplier_SundayOvertime_Returns2_0() {
        // Sunday - BCEA 2.0x
        LocalDate sunday = LocalDate.of(2026, 3, 1);
        assertThat(sunday.getDayOfWeek()).isEqualTo(DayOfWeek.SUNDAY);

        BigDecimal rate = overtimeService.calculateRateMultiplier(OvertimeType.WEEKDAY, sunday);

        assertThat(rate).isEqualByComparingTo(new BigDecimal("2.0"));
    }

    @Test
    void calculateRateMultiplier_WeekendSaturday_Returns1_5() {
        // Saturday - 1.5x
        LocalDate saturday = LocalDate.of(2026, 2, 28);
        assertThat(saturday.getDayOfWeek()).isEqualTo(DayOfWeek.SATURDAY);

        BigDecimal rate = overtimeService.calculateRateMultiplier(OvertimeType.WEEKEND, saturday);

        assertThat(rate).isEqualByComparingTo(new BigDecimal("1.5"));
    }

    @Test
    void calculateRateMultiplier_WeekendSunday_Returns2_0() {
        LocalDate sunday = LocalDate.of(2026, 3, 1);
        BigDecimal rate = overtimeService.calculateRateMultiplier(OvertimeType.WEEKEND, sunday);

        assertThat(rate).isEqualByComparingTo(new BigDecimal("2.0"));
    }

    @Test
    void calculateRateMultiplier_PublicHoliday_Returns2_0() {
        // Public holiday always 2.0x
        LocalDate freedomDay = LocalDate.of(2026, 4, 27);
        BigDecimal rate = overtimeService.calculateRateMultiplier(OvertimeType.PUBLIC_HOLIDAY, freedomDay);

        assertThat(rate).isEqualByComparingTo(new BigDecimal("2.0"));
    }

    @Test
    void calculateRateMultiplier_NightShift_Returns1_5() {
        BigDecimal rate = overtimeService.calculateRateMultiplier(OvertimeType.NIGHT, LocalDate.now());

        assertThat(rate).isEqualByComparingTo(new BigDecimal("1.5"));
    }

    // ==================== Weekly Overtime Cap Tests (BCEA max 10hr/week) ====================

    @Test
    void validateWeeklyOvertimeCap_WithinLimit_NoException() {
        when(overtimeRecordRepository.sumApprovedHoursForWeek(
                anyLong(), any(LocalDate.class), any(LocalDate.class), anyString()))
                .thenReturn(new BigDecimal("5.0"));

        // Adding 4 hours with 5 existing = 9 hours (within 10 limit)
        overtimeService.validateWeeklyOvertimeCap(
                1L, LocalDate.of(2026, 2, 23), new BigDecimal("4.0"), "test-tenant");

        // No exception thrown
    }

    @Test
    void validateWeeklyOvertimeCap_AtExactLimit_NoException() {
        when(overtimeRecordRepository.sumApprovedHoursForWeek(
                anyLong(), any(LocalDate.class), any(LocalDate.class), anyString()))
                .thenReturn(new BigDecimal("8.0"));

        // Adding 2 hours with 8 existing = exactly 10 hours
        overtimeService.validateWeeklyOvertimeCap(
                1L, LocalDate.of(2026, 2, 23), new BigDecimal("2.0"), "test-tenant");
    }

    @Test
    void validateWeeklyOvertimeCap_ExceedsLimit_ThrowsException() {
        when(overtimeRecordRepository.sumApprovedHoursForWeek(
                anyLong(), any(LocalDate.class), any(LocalDate.class), anyString()))
                .thenReturn(new BigDecimal("8.0"));

        // Adding 3 hours with 8 existing = 11 hours (exceeds 10 limit)
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                overtimeService.validateWeeklyOvertimeCap(
                        1L, LocalDate.of(2026, 2, 23), new BigDecimal("3.0"), "test-tenant"));

        assertThat(ex.getMessage()).contains("BCEA weekly overtime limit exceeded");
        assertThat(ex.getMessage()).contains("10.0");
    }

    // ==================== CRUD Tests ====================

    @Test
    void createOvertimeRecord_ValidRequest_ReturnsResponse() {
        OvertimeRecordRequest request = new OvertimeRecordRequest();
        request.setEmployeeId(1L);
        request.setOvertimeDate("2026-02-23");
        request.setOvertimeType("WEEKDAY");
        request.setHours(new BigDecimal("2.0"));

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(overtimeRecordRepository.sumApprovedHoursForWeek(
                anyLong(), any(LocalDate.class), any(LocalDate.class), anyString()))
                .thenReturn(BigDecimal.ZERO);
        when(overtimeRecordRepository.save(any(OvertimeRecord.class))).thenReturn(testRecord);

        OvertimeRecordResponse result = overtimeService.createOvertimeRecord(request);

        assertThat(result).isNotNull();
        assertThat(result.getOvertimeType()).isEqualTo("WEEKDAY");
        assertThat(result.getHours()).isEqualByComparingTo(new BigDecimal("2.0"));
        verify(overtimeRecordRepository).save(any(OvertimeRecord.class));
    }

    @Test
    void approveOvertime_PendingRecord_SetsApproved() {
        when(overtimeRecordRepository.findById(1L)).thenReturn(Optional.of(testRecord));
        when(overtimeRecordRepository.save(any(OvertimeRecord.class))).thenReturn(testRecord);

        overtimeService.approveOvertime(1L, "manager@company.com");

        assertThat(testRecord.getStatus()).isEqualTo(OvertimeStatus.APPROVED);
        assertThat(testRecord.getApprovedBy()).isEqualTo("manager@company.com");
        assertThat(testRecord.getApprovedAt()).isNotNull();
    }

    @Test
    void approveOvertime_AlreadyApproved_ThrowsException() {
        testRecord.setStatus(OvertimeStatus.APPROVED);
        when(overtimeRecordRepository.findById(1L)).thenReturn(Optional.of(testRecord));

        assertThrows(IllegalStateException.class, () ->
                overtimeService.approveOvertime(1L, "manager@company.com"));
    }

    @Test
    void rejectOvertime_PendingRecord_SetsRejected() {
        when(overtimeRecordRepository.findById(1L)).thenReturn(Optional.of(testRecord));
        when(overtimeRecordRepository.save(any(OvertimeRecord.class))).thenReturn(testRecord);

        overtimeService.rejectOvertime(1L, "manager@company.com", "Budget exceeded");

        assertThat(testRecord.getStatus()).isEqualTo(OvertimeStatus.REJECTED);
        assertThat(testRecord.getRejectionReason()).isEqualTo("Budget exceeded");
    }

    @Test
    void getOvertimeRecord_NonExisting_ThrowsException() {
        when(overtimeRecordRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                overtimeService.getOvertimeRecord(999L));
    }
}
