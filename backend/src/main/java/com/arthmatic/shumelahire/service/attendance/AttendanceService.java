package com.arthmatic.shumelahire.service.attendance;

import com.arthmatic.shumelahire.dto.attendance.AttendanceRecordResponse;
import com.arthmatic.shumelahire.dto.attendance.ClockInRequest;
import com.arthmatic.shumelahire.dto.attendance.ClockOutRequest;
import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.attendance.*;
import com.arthmatic.shumelahire.repository.EmployeeRepository;
import com.arthmatic.shumelahire.repository.attendance.AttendanceRecordRepository;
import com.arthmatic.shumelahire.repository.attendance.ShiftScheduleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttendanceService {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceService.class);
    private static final BigDecimal STANDARD_WORK_HOURS = new BigDecimal("8.00");

    @Autowired
    private AttendanceRecordRepository attendanceRecordRepository;

    @Autowired
    private ShiftScheduleRepository shiftScheduleRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private GeofenceService geofenceService;

    public AttendanceRecordResponse clockIn(ClockInRequest request) {
        logger.info("Clock-in for employee: {}", request.getEmployeeId());

        Employee employee = findEmployeeById(request.getEmployeeId());
        LocalDate today = LocalDate.now();

        // Check for existing open attendance
        Optional<AttendanceRecord> existingOpen = attendanceRecordRepository.findOpenAttendance(
                request.getEmployeeId(), employee.getTenantId());
        if (existingOpen.isPresent()) {
            throw new IllegalStateException("Employee already has an open attendance record. Please clock out first.");
        }

        // Find today's schedule if exists
        Optional<ShiftSchedule> schedule = shiftScheduleRepository.findByEmployeeAndDate(
                request.getEmployeeId(), today, employee.getTenantId());

        AttendanceRecord record = new AttendanceRecord();
        record.setEmployee(employee);
        record.setRecordDate(today);
        record.setClockInTime(LocalDateTime.now());
        record.setClockInLatitude(request.getLatitude());
        record.setClockInLongitude(request.getLongitude());
        record.setStatus(AttendanceStatus.PRESENT);
        record.setNotes(request.getNotes());

        // Geofence validation
        if (request.getLatitude() != null && request.getLongitude() != null) {
            Geofence geofence = geofenceService.findContainingGeofence(request.getLatitude(), request.getLongitude());
            if (geofence != null) {
                record.setClockInGeofence(geofence);
                record.setClockInWithinGeofence(true);
            } else {
                record.setClockInWithinGeofence(false);
            }
        }

        // Link to schedule and check for late arrival
        if (schedule.isPresent()) {
            ShiftSchedule ss = schedule.get();
            record.setShiftSchedule(ss);

            Shift shift = ss.getShift();
            LocalTime shiftStart = shift.getStartTime();
            LocalTime clockInTime = record.getClockInTime().toLocalTime();
            int gracePeriod = shift.getGracePeriodMinutes() != null ? shift.getGracePeriodMinutes() : 0;

            LocalTime lateCutoff = shiftStart.plusMinutes(gracePeriod);
            if (clockInTime.isAfter(lateCutoff)) {
                record.setIsLateArrival(true);
                long lateMinutes = Duration.between(shiftStart, clockInTime).toMinutes();
                record.setLateMinutes((int) lateMinutes);
                record.setStatus(AttendanceStatus.LATE);
            }
        }

        AttendanceRecord saved = attendanceRecordRepository.save(record);
        logger.info("Clock-in recorded for employee {} (record id={})", request.getEmployeeId(), saved.getId());
        return AttendanceRecordResponse.fromEntity(saved);
    }

    public AttendanceRecordResponse clockOut(ClockOutRequest request) {
        logger.info("Clock-out for employee: {}", request.getEmployeeId());

        Employee employee = findEmployeeById(request.getEmployeeId());

        AttendanceRecord record = attendanceRecordRepository.findOpenAttendance(
                request.getEmployeeId(), employee.getTenantId())
                .orElseThrow(() -> new IllegalStateException("No open attendance record found. Please clock in first."));

        record.setClockOutTime(LocalDateTime.now());
        record.setClockOutLatitude(request.getLatitude());
        record.setClockOutLongitude(request.getLongitude());

        if (request.getNotes() != null) {
            String existingNotes = record.getNotes() != null ? record.getNotes() + "\n" : "";
            record.setNotes(existingNotes + request.getNotes());
        }

        // Geofence validation for clock-out
        if (request.getLatitude() != null && request.getLongitude() != null) {
            Geofence geofence = geofenceService.findContainingGeofence(request.getLatitude(), request.getLongitude());
            if (geofence != null) {
                record.setClockOutGeofence(geofence);
                record.setClockOutWithinGeofence(true);
            } else {
                record.setClockOutWithinGeofence(false);
            }
        }

        // Calculate hours
        calculateWorkHours(record);

        // Check for early departure
        if (record.getShiftSchedule() != null) {
            Shift shift = record.getShiftSchedule().getShift();
            LocalTime shiftEnd = shift.getEndTime();
            LocalTime clockOutTime = record.getClockOutTime().toLocalTime();

            if (clockOutTime.isBefore(shiftEnd)) {
                record.setIsEarlyDeparture(true);
                long earlyMinutes = Duration.between(clockOutTime, shiftEnd).toMinutes();
                record.setEarlyDepartureMinutes((int) earlyMinutes);
            }
        }

        AttendanceRecord saved = attendanceRecordRepository.save(record);
        logger.info("Clock-out recorded for employee {} (record id={})", request.getEmployeeId(), saved.getId());
        return AttendanceRecordResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public AttendanceRecordResponse getAttendanceRecord(Long id) {
        return AttendanceRecordResponse.fromEntity(findRecordById(id));
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecordResponse> getEmployeeAttendance(Long employeeId, String startDate, String endDate) {
        Employee employee = findEmployeeById(employeeId);
        return attendanceRecordRepository.findByEmployeeAndDateRange(
                employeeId, LocalDate.parse(startDate), LocalDate.parse(endDate), employee.getTenantId()
        ).stream().map(AttendanceRecordResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecordResponse> getAttendanceByDateRange(String startDate, String endDate) {
        return attendanceRecordRepository.findByDateRange(
                LocalDate.parse(startDate), LocalDate.parse(endDate), null
        ).stream().map(AttendanceRecordResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<AttendanceRecordResponse> getOpenAttendance(Long employeeId) {
        Employee employee = findEmployeeById(employeeId);
        return attendanceRecordRepository.findOpenAttendance(employeeId, employee.getTenantId())
                .map(AttendanceRecordResponse::fromEntity);
    }

    /**
     * Auto-clock-out open records (typically run as scheduled task).
     */
    public int autoClockOutOpenRecords(String tenantId) {
        List<AttendanceRecord> openRecords = attendanceRecordRepository.findOpenRecords(tenantId);
        int count = 0;
        for (AttendanceRecord record : openRecords) {
            // Only auto-clock-out records from previous days
            if (record.getRecordDate().isBefore(LocalDate.now())) {
                record.setClockOutTime(record.getRecordDate().atTime(23, 59, 59));
                record.setAutoClockedOut(true);
                calculateWorkHours(record);
                attendanceRecordRepository.save(record);
                count++;
            }
        }
        if (count > 0) {
            logger.info("Auto-clocked-out {} open records for tenant {}", count, tenantId);
        }
        return count;
    }

    private void calculateWorkHours(AttendanceRecord record) {
        if (record.getClockInTime() == null || record.getClockOutTime() == null) {
            return;
        }

        Duration duration = Duration.between(record.getClockInTime(), record.getClockOutTime());
        long totalMinutes = duration.toMinutes();

        // Subtract break
        int breakMinutes = 0;
        if (record.getShiftSchedule() != null) {
            breakMinutes = record.getShiftSchedule().getShift().getBreakDurationMinutes();
        }
        record.setBreakMinutes(breakMinutes);
        totalMinutes -= breakMinutes;
        if (totalMinutes < 0) totalMinutes = 0;

        BigDecimal totalHours = BigDecimal.valueOf(totalMinutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        record.setTotalHours(totalHours);

        // Split into regular and overtime (standard 8hr day per BCEA)
        if (totalHours.compareTo(STANDARD_WORK_HOURS) > 0) {
            record.setRegularHours(STANDARD_WORK_HOURS);
            record.setOvertimeHours(totalHours.subtract(STANDARD_WORK_HOURS));
        } else {
            record.setRegularHours(totalHours);
            record.setOvertimeHours(BigDecimal.ZERO);
        }
    }

    private AttendanceRecord findRecordById(Long id) {
        return attendanceRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attendance record not found: " + id));
    }

    private Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + id));
    }
}
