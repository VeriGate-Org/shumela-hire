package com.arthmatic.shumelahire.service.attendance;

import com.arthmatic.shumelahire.dto.attendance.AttendanceRecordResponse;
import com.arthmatic.shumelahire.dto.attendance.ClockInRequest;
import com.arthmatic.shumelahire.dto.attendance.ClockOutRequest;
import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.EmployeeStatus;
import com.arthmatic.shumelahire.entity.attendance.*;
import com.arthmatic.shumelahire.repository.EmployeeRepository;
import com.arthmatic.shumelahire.repository.attendance.AttendanceRecordRepository;
import com.arthmatic.shumelahire.repository.attendance.ShiftScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    private AttendanceRecordRepository attendanceRecordRepository;

    @Mock
    private ShiftScheduleRepository shiftScheduleRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private GeofenceService geofenceService;

    @InjectMocks
    private AttendanceService attendanceService;

    private Employee testEmployee;
    private Shift testShift;
    private ShiftSchedule testSchedule;
    private AttendanceRecord testRecord;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee();
        testEmployee.setId(1L);
        testEmployee.setFirstName("Sipho");
        testEmployee.setLastName("Mkhize");
        testEmployee.setEmail("sipho@company.com");
        testEmployee.setStatus(EmployeeStatus.ACTIVE);
        testEmployee.setHireDate(LocalDate.of(2024, 1, 15));
        testEmployee.setTenantId("test-tenant");

        testShift = new Shift();
        testShift.setId(1L);
        testShift.setName("Morning Shift");
        testShift.setStartTime(LocalTime.of(8, 0));
        testShift.setEndTime(LocalTime.of(17, 0));
        testShift.setBreakDurationMinutes(60);
        testShift.setGracePeriodMinutes(15);

        testSchedule = new ShiftSchedule();
        testSchedule.setId(1L);
        testSchedule.setEmployee(testEmployee);
        testSchedule.setShift(testShift);
        testSchedule.setScheduleDate(LocalDate.now());
        testSchedule.setStatus(ScheduleStatus.SCHEDULED);

        testRecord = new AttendanceRecord();
        testRecord.setId(1L);
        testRecord.setEmployee(testEmployee);
        testRecord.setRecordDate(LocalDate.now());
        testRecord.setClockInTime(LocalDateTime.now().withHour(8).withMinute(5));
        testRecord.setStatus(AttendanceStatus.PRESENT);
        testRecord.setIsLateArrival(false);
        testRecord.setIsEarlyDeparture(false);
        testRecord.setAutoClockedOut(false);
        testRecord.setBreakMinutes(0);
        testRecord.setLateMinutes(0);
        testRecord.setEarlyDepartureMinutes(0);
        testRecord.setCreatedAt(LocalDateTime.now());
        testRecord.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void clockIn_ValidRequest_CreatesAttendanceRecord() {
        ClockInRequest request = new ClockInRequest();
        request.setEmployeeId(1L);
        request.setLatitude(-26.1076);
        request.setLongitude(28.0567);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(attendanceRecordRepository.findOpenAttendance(eq(1L), anyString())).thenReturn(Optional.empty());
        when(shiftScheduleRepository.findByEmployeeAndDate(eq(1L), any(LocalDate.class), anyString()))
                .thenReturn(Optional.empty());
        when(geofenceService.findContainingGeofence(anyDouble(), anyDouble())).thenReturn(null);
        when(attendanceRecordRepository.save(any(AttendanceRecord.class))).thenReturn(testRecord);

        AttendanceRecordResponse result = attendanceService.clockIn(request);

        assertThat(result).isNotNull();
        assertThat(result.getEmployeeId()).isEqualTo(1L);
        verify(attendanceRecordRepository).save(any(AttendanceRecord.class));
    }

    @Test
    void clockIn_AlreadyClockedIn_ThrowsException() {
        ClockInRequest request = new ClockInRequest();
        request.setEmployeeId(1L);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(attendanceRecordRepository.findOpenAttendance(eq(1L), anyString()))
                .thenReturn(Optional.of(testRecord));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> attendanceService.clockIn(request));

        assertThat(ex.getMessage()).contains("already has an open attendance record");
    }

    @Test
    void clockIn_WithGeofence_SetsGeofenceInfo() {
        ClockInRequest request = new ClockInRequest();
        request.setEmployeeId(1L);
        request.setLatitude(-26.1076);
        request.setLongitude(28.0567);

        Geofence matchingGeofence = new Geofence();
        matchingGeofence.setId(1L);
        matchingGeofence.setName("Office");
        matchingGeofence.setGeofenceType(GeofenceType.RADIUS);
        matchingGeofence.setIsActive(true);
        matchingGeofence.setCreatedAt(LocalDateTime.now());
        matchingGeofence.setUpdatedAt(LocalDateTime.now());

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(attendanceRecordRepository.findOpenAttendance(eq(1L), anyString())).thenReturn(Optional.empty());
        when(shiftScheduleRepository.findByEmployeeAndDate(eq(1L), any(LocalDate.class), anyString()))
                .thenReturn(Optional.empty());
        when(geofenceService.findContainingGeofence(-26.1076, 28.0567)).thenReturn(matchingGeofence);
        when(attendanceRecordRepository.save(any(AttendanceRecord.class))).thenAnswer(inv -> {
            AttendanceRecord saved = inv.getArgument(0);
            saved.setId(1L);
            saved.setCreatedAt(LocalDateTime.now());
            saved.setUpdatedAt(LocalDateTime.now());
            return saved;
        });

        AttendanceRecordResponse result = attendanceService.clockIn(request);

        assertThat(result).isNotNull();
        assertThat(result.getClockInWithinGeofence()).isTrue();
    }

    @Test
    void clockOut_WithOpenRecord_CompletesAttendance() {
        ClockOutRequest request = new ClockOutRequest();
        request.setEmployeeId(1L);

        // Set clock-in to 8:00 AM, will clock out ~now
        testRecord.setClockInTime(LocalDateTime.now().minusHours(9));
        testRecord.setShiftSchedule(testSchedule);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(attendanceRecordRepository.findOpenAttendance(eq(1L), anyString()))
                .thenReturn(Optional.of(testRecord));
        when(attendanceRecordRepository.save(any(AttendanceRecord.class))).thenReturn(testRecord);

        AttendanceRecordResponse result = attendanceService.clockOut(request);

        assertThat(result).isNotNull();
        assertThat(testRecord.getClockOutTime()).isNotNull();
        assertThat(testRecord.getTotalHours()).isNotNull();
        verify(attendanceRecordRepository).save(testRecord);
    }

    @Test
    void clockOut_NoOpenRecord_ThrowsException() {
        ClockOutRequest request = new ClockOutRequest();
        request.setEmployeeId(1L);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(attendanceRecordRepository.findOpenAttendance(eq(1L), anyString()))
                .thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> attendanceService.clockOut(request));

        assertThat(ex.getMessage()).contains("No open attendance record found");
    }

    @Test
    void getAttendanceRecord_NonExisting_ThrowsException() {
        when(attendanceRecordRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> attendanceService.getAttendanceRecord(999L));
    }

    @Test
    void clockIn_EmployeeNotFound_ThrowsException() {
        ClockInRequest request = new ClockInRequest();
        request.setEmployeeId(999L);

        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> attendanceService.clockIn(request));
    }
}
