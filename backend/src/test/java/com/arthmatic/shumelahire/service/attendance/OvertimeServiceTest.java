package com.arthmatic.shumelahire.service.attendance;

import com.arthmatic.shumelahire.dto.attendance.OvertimeRecordRequest;
import com.arthmatic.shumelahire.dto.attendance.OvertimeRecordResponse;
import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.OvertimeRecord;
import com.arthmatic.shumelahire.repository.AttendanceRecordRepository;
import com.arthmatic.shumelahire.repository.EmployeeRepository;
import com.arthmatic.shumelahire.repository.OvertimeRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OvertimeServiceTest {

    @Mock
    private OvertimeRecordRepository overtimeRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private AttendanceRecordRepository attendanceRepository;

    @InjectMocks
    private OvertimeService overtimeService;

    @Test
    void resolveRateMultiplier_weekday_returns1point5() {
        BigDecimal rate = OvertimeService.resolveRateMultiplier(OvertimeRecord.OvertimeType.WEEKDAY);
        assertThat(rate).isEqualByComparingTo(new BigDecimal("1.5"));
    }

    @Test
    void resolveRateMultiplier_saturday_returns1point5() {
        BigDecimal rate = OvertimeService.resolveRateMultiplier(OvertimeRecord.OvertimeType.SATURDAY);
        assertThat(rate).isEqualByComparingTo(new BigDecimal("1.5"));
    }

    @Test
    void resolveRateMultiplier_sunday_returns2() {
        BigDecimal rate = OvertimeService.resolveRateMultiplier(OvertimeRecord.OvertimeType.SUNDAY);
        assertThat(rate).isEqualByComparingTo(new BigDecimal("2.0"));
    }

    @Test
    void resolveRateMultiplier_publicHoliday_returns2() {
        BigDecimal rate = OvertimeService.resolveRateMultiplier(OvertimeRecord.OvertimeType.PUBLIC_HOLIDAY);
        assertThat(rate).isEqualByComparingTo(new BigDecimal("2.0"));
    }

    @Test
    void create_validRequest_createsOvertimeRecord() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setEmployeeNumber("EMP001");
        employee.setFirstName("John");
        employee.setLastName("Doe");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(overtimeRepository.sumWeeklyOvertimeHours(eq(1L), any(), any()))
                .thenReturn(BigDecimal.ZERO);
        when(overtimeRepository.sumMonthlyOvertimeHours(eq(1L), any(), any()))
                .thenReturn(BigDecimal.ZERO);
        when(overtimeRepository.save(any(OvertimeRecord.class))).thenAnswer(inv -> {
            OvertimeRecord r = inv.getArgument(0);
            r.setId(1L);
            return r;
        });

        OvertimeRecordRequest request = new OvertimeRecordRequest();
        request.setEmployeeId(1L);
        request.setOvertimeDate(LocalDate.of(2026, 2, 25)); // Wednesday
        request.setOvertimeHours(new BigDecimal("2.5"));
        request.setOvertimeType("WEEKDAY");
        request.setReason("Project deadline");

        OvertimeRecordResponse response = overtimeService.create(request);

        assertThat(response.getOvertimeHours()).isEqualByComparingTo(new BigDecimal("2.5"));
        assertThat(response.getRateMultiplier()).isEqualByComparingTo(new BigDecimal("1.5"));
        assertThat(response.getStatus()).isEqualTo("PENDING");
        assertThat(response.getExceedsBceaWeeklyLimit()).isFalse();
    }

    @Test
    void create_exceedsBceaWeeklyLimit_flagsExceedance() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setEmployeeNumber("EMP001");
        employee.setFirstName("John");
        employee.setLastName("Doe");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        // Already 9 hours of overtime this week
        when(overtimeRepository.sumWeeklyOvertimeHours(eq(1L), any(), any()))
                .thenReturn(new BigDecimal("9.0"));
        when(overtimeRepository.sumMonthlyOvertimeHours(eq(1L), any(), any()))
                .thenReturn(new BigDecimal("20.0"));
        when(overtimeRepository.save(any(OvertimeRecord.class))).thenAnswer(inv -> {
            OvertimeRecord r = inv.getArgument(0);
            r.setId(1L);
            return r;
        });

        OvertimeRecordRequest request = new OvertimeRecordRequest();
        request.setEmployeeId(1L);
        request.setOvertimeDate(LocalDate.of(2026, 2, 25));
        request.setOvertimeHours(new BigDecimal("2.0"));
        request.setOvertimeType("WEEKDAY");

        OvertimeRecordResponse response = overtimeService.create(request);

        // 9 + 2 = 11 > 10 (BCEA limit)
        assertThat(response.getExceedsBceaWeeklyLimit()).isTrue();
        assertThat(response.getWeeklyOvertimeTotal()).isEqualByComparingTo(new BigDecimal("11.0"));
    }

    @Test
    void approve_setsStatusApproved() {
        Employee approver = new Employee();
        approver.setId(2L);
        approver.setFirstName("Jane");
        approver.setLastName("Manager");

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");

        OvertimeRecord record = new OvertimeRecord();
        record.setId(1L);
        record.setEmployee(employee);
        record.setOvertimeDate(LocalDate.now());
        record.setOvertimeHours(new BigDecimal("2.0"));
        record.setOvertimeType(OvertimeRecord.OvertimeType.WEEKDAY);
        record.setRateMultiplier(new BigDecimal("1.5"));
        record.setStatus(OvertimeRecord.OvertimeStatus.PENDING);

        when(overtimeRepository.findById(1L)).thenReturn(Optional.of(record));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(approver));
        when(overtimeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OvertimeRecordResponse response = overtimeService.approve(1L, 2L);

        assertThat(response.getStatus()).isEqualTo("APPROVED");
    }

    @Test
    void create_employeeNotFound_throwsException() {
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        OvertimeRecordRequest request = new OvertimeRecordRequest();
        request.setEmployeeId(999L);
        request.setOvertimeDate(LocalDate.now());
        request.setOvertimeHours(new BigDecimal("2.0"));
        request.setOvertimeType("WEEKDAY");

        assertThatThrownBy(() -> overtimeService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Employee not found");
    }
}
