package com.arthmatic.shumelahire.service.attendance;

import com.arthmatic.shumelahire.dto.attendance.AttendanceRecordResponse;
import com.arthmatic.shumelahire.dto.attendance.ClockInRequest;
import com.arthmatic.shumelahire.dto.attendance.ClockOutRequest;
import com.arthmatic.shumelahire.entity.*;
import com.arthmatic.shumelahire.repository.AttendanceRecordRepository;
import com.arthmatic.shumelahire.repository.EmployeeRepository;
import com.arthmatic.shumelahire.repository.GeofenceRepository;
import com.arthmatic.shumelahire.repository.ShiftScheduleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private final AttendanceRecordRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final GeofenceRepository geofenceRepository;
    private final ShiftScheduleRepository shiftScheduleRepository;
    private final GeofenceService geofenceService;

    public AttendanceService(AttendanceRecordRepository attendanceRepository,
                             EmployeeRepository employeeRepository,
                             GeofenceRepository geofenceRepository,
                             ShiftScheduleRepository shiftScheduleRepository,
                             GeofenceService geofenceService) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
        this.geofenceRepository = geofenceRepository;
        this.shiftScheduleRepository = shiftScheduleRepository;
        this.geofenceService = geofenceService;
    }

    public AttendanceRecordResponse clockIn(ClockInRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        LocalDate today = LocalDate.now();

        // Check for existing open attendance record
        Optional<AttendanceRecord> existing = attendanceRepository
                .findByEmployeeIdAndAttendanceDateAndStatus(
                        employee.getId(), today, AttendanceRecord.AttendanceStatus.CLOCKED_IN);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Employee already clocked in for today");
        }

        AttendanceRecord record = new AttendanceRecord();
        record.setEmployee(employee);
        record.setAttendanceDate(today);
        record.setClockInTime(LocalDateTime.now());
        record.setStatus(AttendanceRecord.AttendanceStatus.CLOCKED_IN);

        if (request.getClockMethod() != null) {
            record.setClockInMethod(AttendanceRecord.ClockMethod.valueOf(request.getClockMethod()));
        }
        record.setClockInLatitude(request.getLatitude());
        record.setClockInLongitude(request.getLongitude());
        record.setClockInIpAddress(request.getIpAddress());
        record.setDeviceInfo(request.getDeviceInfo());

        // Geofence validation
        if (request.getGeofenceId() != null) {
            Geofence geofence = geofenceRepository.findById(request.getGeofenceId()).orElse(null);
            if (geofence != null) {
                record.setGeofence(geofence);
                boolean withinGeofence = geofenceService.isWithinGeofence(
                        geofence, request.getLatitude(), request.getLongitude());
                record.setClockInWithinGeofence(withinGeofence);

                if (!withinGeofence && geofence.getEnforceOnClockIn()
                        && !geofence.getAllowOverrideWithReason()) {
                    throw new IllegalArgumentException(
                            "Clock-in location is outside the designated geofence area");
                }
                if (!withinGeofence && geofence.getEnforceOnClockIn()
                        && geofence.getAllowOverrideWithReason()
                        && (request.getOverrideReason() == null || request.getOverrideReason().isBlank())) {
                    throw new IllegalArgumentException(
                            "Clock-in outside geofence requires a reason");
                }
                if (!withinGeofence && request.getOverrideReason() != null) {
                    record.setNotes("Geofence override: " + request.getOverrideReason());
                }
            }
        }

        // Check shift schedule for late arrival
        Optional<ShiftSchedule> schedule = shiftScheduleRepository
                .findByEmployeeIdAndScheduleDate(employee.getId(), today);
        if (schedule.isPresent()) {
            Shift shift = schedule.get().getShift();
            record.setShift(shift);
            record.setScheduledStartTime(shift.getStartTime());
            record.setScheduledEndTime(shift.getEndTime());
            record.setBreakDurationMinutes(shift.getBreakDurationMinutes());

            LocalTime clockInLocalTime = record.getClockInTime().toLocalTime();
            LocalTime graceTime = shift.getStartTime().plusMinutes(shift.getGracePeriodMinutes());
            if (clockInLocalTime.isAfter(graceTime)) {
                record.setLateArrival(true);
                long lateMinutes = Duration.between(shift.getStartTime(), clockInLocalTime).toMinutes();
                record.setLateMinutes((int) lateMinutes);
            }
        }

        record = attendanceRepository.save(record);
        logger.info("Clock-in recorded for employee {} at {}", employee.getEmployeeNumber(), record.getClockInTime());
        return AttendanceRecordResponse.fromEntity(record);
    }

    public AttendanceRecordResponse clockOut(ClockOutRequest request) {
        AttendanceRecord record = attendanceRepository.findById(request.getAttendanceRecordId())
                .orElseThrow(() -> new IllegalArgumentException("Attendance record not found"));

        if (record.getStatus() != AttendanceRecord.AttendanceStatus.CLOCKED_IN) {
            throw new IllegalArgumentException("Attendance record is not in CLOCKED_IN status");
        }

        record.setClockOutTime(LocalDateTime.now());
        record.setStatus(AttendanceRecord.AttendanceStatus.CLOCKED_OUT);

        if (request.getClockMethod() != null) {
            record.setClockOutMethod(AttendanceRecord.ClockMethod.valueOf(request.getClockMethod()));
        }
        record.setClockOutLatitude(request.getLatitude());
        record.setClockOutLongitude(request.getLongitude());
        record.setClockOutIpAddress(request.getIpAddress());
        if (request.getNotes() != null) {
            record.setNotes(record.getNotes() != null
                    ? record.getNotes() + "; " + request.getNotes()
                    : request.getNotes());
        }

        // Geofence validation for clock-out
        if (record.getGeofence() != null && record.getGeofence().getEnforceOnClockOut()) {
            boolean withinGeofence = geofenceService.isWithinGeofence(
                    record.getGeofence(), request.getLatitude(), request.getLongitude());
            record.setClockOutWithinGeofence(withinGeofence);
        }

        // Calculate hours worked
        calculateHoursWorked(record);

        // Check early departure
        if (record.getScheduledEndTime() != null) {
            LocalTime clockOutLocalTime = record.getClockOutTime().toLocalTime();
            if (clockOutLocalTime.isBefore(record.getScheduledEndTime())) {
                record.setEarlyDeparture(true);
                long earlyMinutes = Duration.between(clockOutLocalTime, record.getScheduledEndTime()).toMinutes();
                record.setEarlyDepartureMinutes((int) earlyMinutes);
            }
        }

        record = attendanceRepository.save(record);
        logger.info("Clock-out recorded for employee {} at {}. Total hours: {}",
                record.getEmployee().getEmployeeNumber(), record.getClockOutTime(), record.getTotalHoursWorked());
        return AttendanceRecordResponse.fromEntity(record);
    }

    @Transactional(readOnly = true)
    public AttendanceRecordResponse getById(Long id) {
        AttendanceRecord record = attendanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attendance record not found"));
        return AttendanceRecordResponse.fromEntity(record);
    }

    @Transactional(readOnly = true)
    public Page<AttendanceRecordResponse> getByEmployee(Long employeeId, Pageable pageable) {
        return attendanceRepository.findByEmployeeId(employeeId, pageable)
                .map(AttendanceRecordResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecordResponse> getByEmployeeAndDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByEmployeeAndDateRange(employeeId, startDate, endDate).stream()
                .map(AttendanceRecordResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<AttendanceRecordResponse> getByDateAndStatus(LocalDate date,
                                                              AttendanceRecord.AttendanceStatus status,
                                                              Pageable pageable) {
        return attendanceRepository.findByDateAndOptionalStatus(date, status, pageable)
                .map(AttendanceRecordResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<AttendanceRecordResponse> search(Long employeeId, String department,
                                                  LocalDate startDate, LocalDate endDate,
                                                  Pageable pageable) {
        return attendanceRepository.findByFilters(employeeId, department, startDate, endDate, pageable)
                .map(AttendanceRecordResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecordResponse> getLateArrivals(LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findLateArrivals(startDate, endDate).stream()
                .map(AttendanceRecordResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public AttendanceRecordResponse approve(Long id, Long approvedById) {
        AttendanceRecord record = attendanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attendance record not found"));
        Employee approver = employeeRepository.findById(approvedById)
                .orElseThrow(() -> new IllegalArgumentException("Approver not found"));

        record.setStatus(AttendanceRecord.AttendanceStatus.APPROVED);
        record.setApprovedBy(approver);
        record.setApprovedAt(LocalDateTime.now());
        record = attendanceRepository.save(record);
        logger.info("Attendance record {} approved by {}", id, approver.getFullName());
        return AttendanceRecordResponse.fromEntity(record);
    }

    private void calculateHoursWorked(AttendanceRecord record) {
        if (record.getClockInTime() != null && record.getClockOutTime() != null) {
            Duration duration = Duration.between(record.getClockInTime(), record.getClockOutTime());
            long totalMinutes = duration.toMinutes();

            // Subtract break
            int breakMinutes = record.getBreakDurationMinutes() != null ? record.getBreakDurationMinutes() : 0;
            long netMinutes = Math.max(0, totalMinutes - breakMinutes);

            BigDecimal totalHours = BigDecimal.valueOf(netMinutes)
                    .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
            record.setTotalHoursWorked(totalHours);

            // Determine regular vs overtime hours
            BigDecimal regularThreshold = BigDecimal.valueOf(8); // default 8-hour day
            if (record.getShift() != null && record.getShift().getMinHoursForOvertime() != null) {
                regularThreshold = record.getShift().getMinHoursForOvertime();
            }

            if (totalHours.compareTo(regularThreshold) > 0) {
                record.setRegularHours(regularThreshold);
                record.setOvertimeHours(totalHours.subtract(regularThreshold));
            } else {
                record.setRegularHours(totalHours);
                record.setOvertimeHours(BigDecimal.ZERO);
            }
        }
    }
}
