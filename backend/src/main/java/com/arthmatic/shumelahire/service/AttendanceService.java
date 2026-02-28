package com.arthmatic.shumelahire.service;

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
import org.springframework.beans.factory.annotation.Autowired;
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

@Service
@Transactional
public class AttendanceService {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceService.class);

    @Autowired
    private AttendanceRecordRepository attendanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private GeofenceRepository geofenceRepository;

    @Autowired
    private ShiftScheduleRepository shiftScheduleRepository;

    @Autowired
    private GeofenceService geofenceService;

    public AttendanceRecordResponse clockIn(ClockInRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + request.getEmployeeId()));

        LocalDate today = LocalDate.now();
        Optional<AttendanceRecord> existing = attendanceRepository.findByEmployeeIdAndRecordDate(
                request.getEmployeeId(), today);

        if (existing.isPresent() && existing.get().getClockIn() != null) {
            throw new IllegalArgumentException("Employee already clocked in today");
        }

        AttendanceRecord record = existing.orElseGet(() -> {
            AttendanceRecord r = new AttendanceRecord();
            r.setEmployee(employee);
            r.setRecordDate(today);
            return r;
        });

        LocalDateTime now = LocalDateTime.now();
        record.setClockIn(now);
        record.setClockInLatitude(request.getLatitude());
        record.setClockInLongitude(request.getLongitude());

        // Geofence validation
        if (request.getGeofenceId() != null && request.getLatitude() != null && request.getLongitude() != null) {
            Geofence geofence = geofenceRepository.findById(request.getGeofenceId()).orElse(null);
            if (geofence != null) {
                record.setGeofence(geofence);
                boolean withinGeofence = geofenceService.isWithinGeofence(geofence,
                        request.getLatitude(), request.getLongitude());
                record.setClockInWithinGeofence(withinGeofence);
                if (!withinGeofence) {
                    logger.warn("Employee {} clocked in outside geofence {}", employee.getId(), geofence.getName());
                }
            }
        }

        // Link to shift schedule and check lateness
        Optional<ShiftSchedule> schedule = shiftScheduleRepository.findByEmployeeIdAndScheduleDate(
                request.getEmployeeId(), today);
        if (schedule.isPresent()) {
            record.setShiftSchedule(schedule.get());
            Shift shift = schedule.get().getShift();
            int lateMinutes = calculateLateMinutes(now.toLocalTime(), shift.getStartTime(), shift.getGracePeriodMins());
            record.setLateMinutes(lateMinutes);
            if (lateMinutes > 0) {
                record.setStatus(AttendanceRecord.AttendanceStatus.LATE);
            } else {
                record.setStatus(AttendanceRecord.AttendanceStatus.PRESENT);
            }
        } else {
            record.setStatus(AttendanceRecord.AttendanceStatus.PRESENT);
        }

        if (request.getNotes() != null) {
            record.setNotes(request.getNotes());
        }

        AttendanceRecord saved = attendanceRepository.save(record);
        logger.info("Employee {} clocked in at {}", employee.getId(), now);
        return AttendanceRecordResponse.fromEntity(saved);
    }

    public AttendanceRecordResponse clockOut(ClockOutRequest request) {
        LocalDate today = LocalDate.now();
        AttendanceRecord record = attendanceRepository.findByEmployeeIdAndRecordDate(
                request.getEmployeeId(), today)
                .orElseThrow(() -> new IllegalArgumentException("No clock-in record found for today"));

        if (record.getClockIn() == null) {
            throw new IllegalArgumentException("Employee has not clocked in today");
        }
        if (record.getClockOut() != null) {
            throw new IllegalArgumentException("Employee already clocked out today");
        }

        LocalDateTime now = LocalDateTime.now();
        record.setClockOut(now);
        record.setClockOutLatitude(request.getLatitude());
        record.setClockOutLongitude(request.getLongitude());

        // Geofence validation for clock-out
        if (record.getGeofence() != null && request.getLatitude() != null && request.getLongitude() != null) {
            boolean withinGeofence = geofenceService.isWithinGeofence(record.getGeofence(),
                    request.getLatitude(), request.getLongitude());
            record.setClockOutWithinGeofence(withinGeofence);
        }

        // Calculate hours
        calculateHours(record);

        // Check early departure
        if (record.getShiftSchedule() != null) {
            Shift shift = record.getShiftSchedule().getShift();
            int earlyMins = calculateEarlyDeparture(now.toLocalTime(), shift.getEndTime());
            record.setEarlyDepartureMins(earlyMins);
        }

        if (request.getNotes() != null) {
            String existing = record.getNotes();
            record.setNotes(existing != null ? existing + "; " + request.getNotes() : request.getNotes());
        }

        AttendanceRecord saved = attendanceRepository.save(record);
        logger.info("Employee {} clocked out at {}", request.getEmployeeId(), now);
        return AttendanceRecordResponse.fromEntity(saved);
    }

    /**
     * Safety net: auto clock-out employees who forgot to clock out.
     */
    public int autoClockOutOpenRecords() {
        List<AttendanceRecord> openRecords = attendanceRepository.findOpenRecords(LocalDate.now());
        int count = 0;
        for (AttendanceRecord record : openRecords) {
            // Set clock-out to end of their shift or 18:00 as default
            LocalTime endTime = LocalTime.of(18, 0);
            if (record.getShiftSchedule() != null) {
                endTime = record.getShiftSchedule().getShift().getEndTime();
            }
            record.setClockOut(record.getRecordDate().atTime(endTime));
            record.setAutoClockedOut(true);
            calculateHours(record);
            attendanceRepository.save(record);
            count++;
        }
        if (count > 0) {
            logger.info("Auto-clocked out {} records", count);
        }
        return count;
    }

    @Transactional(readOnly = true)
    public AttendanceRecordResponse getRecord(Long id) {
        return attendanceRepository.findById(id)
                .map(AttendanceRecordResponse::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Attendance record not found: " + id));
    }

    @Transactional(readOnly = true)
    public AttendanceRecordResponse getTodayRecord(Long employeeId) {
        return attendanceRepository.findByEmployeeIdAndRecordDate(employeeId, LocalDate.now())
                .map(AttendanceRecordResponse::fromEntity)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecordResponse> getEmployeeRecords(Long employeeId, LocalDate start, LocalDate end) {
        return attendanceRepository.findByEmployeeIdAndRecordDateBetween(employeeId, start, end).stream()
                .map(AttendanceRecordResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<AttendanceRecordResponse> getRecordsByDateRange(LocalDate start, LocalDate end, Pageable pageable) {
        return attendanceRepository.findByRecordDateBetween(start, end, pageable)
                .map(AttendanceRecordResponse::fromEntity);
    }

    private void calculateHours(AttendanceRecord record) {
        if (record.getClockIn() == null || record.getClockOut() == null) return;

        long totalMinutes = Duration.between(record.getClockIn(), record.getClockOut()).toMinutes();
        int breakMins = record.getBreakMinutes() != null ? record.getBreakMinutes() : 0;
        long workMinutes = Math.max(0, totalMinutes - breakMins);

        double scheduledHours = 8.0; // default
        if (record.getShiftSchedule() != null) {
            scheduledHours = record.getShiftSchedule().getShift().getScheduledHours();
        }

        double totalHours = workMinutes / 60.0;
        double regular = Math.min(totalHours, scheduledHours);
        double overtime = Math.max(0, totalHours - scheduledHours);

        record.setRegularHours(BigDecimal.valueOf(regular).setScale(2, RoundingMode.HALF_UP));
        record.setOvertimeHours(BigDecimal.valueOf(overtime).setScale(2, RoundingMode.HALF_UP));

        // Update status for half-day
        if (totalHours > 0 && totalHours < scheduledHours / 2) {
            record.setStatus(AttendanceRecord.AttendanceStatus.HALF_DAY);
        }
    }

    static int calculateLateMinutes(LocalTime clockInTime, LocalTime shiftStart, int gracePeriodMins) {
        LocalTime graceEnd = shiftStart.plusMinutes(gracePeriodMins);
        if (clockInTime.isAfter(graceEnd)) {
            return (int) Duration.between(shiftStart, clockInTime).toMinutes();
        }
        return 0;
    }

    static int calculateEarlyDeparture(LocalTime clockOutTime, LocalTime shiftEnd) {
        if (clockOutTime.isBefore(shiftEnd)) {
            return (int) Duration.between(clockOutTime, shiftEnd).toMinutes();
        }
        return 0;
    }
}
