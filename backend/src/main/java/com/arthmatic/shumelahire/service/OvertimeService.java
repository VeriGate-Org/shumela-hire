package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.dto.attendance.OvertimeRecordResponse;
import com.arthmatic.shumelahire.entity.*;
import com.arthmatic.shumelahire.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@Transactional
public class OvertimeService {

    private static final Logger logger = LoggerFactory.getLogger(OvertimeService.class);

    @Autowired
    private OvertimeRecordRepository overtimeRepository;

    @Autowired
    private AttendanceRecordRepository attendanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Auto-detect overtime from attendance records.
     * Creates overtime records based on SA labour law rules.
     */
    public List<OvertimeRecordResponse> detectOvertime(LocalDate date) {
        List<AttendanceRecord> records = attendanceRepository.findByRecordDateBetween(date, date);

        return records.stream()
                .filter(r -> r.getOvertimeHours() != null && r.getOvertimeHours().compareTo(BigDecimal.ZERO) > 0)
                .map(record -> createOvertimeFromAttendance(record, date))
                .toList();
    }

    public OvertimeRecordResponse createOvertimeRecord(Long employeeId, LocalDate date,
                                                        BigDecimal hours, OvertimeRecord.OvertimeType type,
                                                        String reason) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        // Enforce SA weekly overtime limit (10 hours)
        validateWeeklyOvertimeLimit(employeeId, date, hours);

        OvertimeRecord record = new OvertimeRecord();
        record.setEmployee(employee);
        record.setOvertimeDate(date);
        record.setHours(hours);
        record.setType(type);
        record.setRateMultiplier(OvertimeRecord.getDefaultMultiplier(type));
        record.setStatus(OvertimeRecord.OvertimeStatus.PENDING);
        record.setReason(reason);

        OvertimeRecord saved = overtimeRepository.save(record);
        logger.info("Created overtime record for employee {}: {} hours ({})", employeeId, hours, type);
        return OvertimeRecordResponse.fromEntity(saved);
    }

    public OvertimeRecordResponse approveOvertime(Long overtimeId, Long approverId) {
        OvertimeRecord record = overtimeRepository.findById(overtimeId)
                .orElseThrow(() -> new IllegalArgumentException("Overtime record not found"));

        if (record.getStatus() != OvertimeRecord.OvertimeStatus.PENDING) {
            throw new IllegalArgumentException("Overtime record is not pending approval");
        }

        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new IllegalArgumentException("Approver not found"));

        record.setStatus(OvertimeRecord.OvertimeStatus.APPROVED);
        record.setApprovedBy(approver);
        record.setApprovedAt(LocalDateTime.now());

        OvertimeRecord saved = overtimeRepository.save(record);
        logger.info("Overtime {} approved by {}", overtimeId, approverId);
        return OvertimeRecordResponse.fromEntity(saved);
    }

    public OvertimeRecordResponse rejectOvertime(Long overtimeId, Long approverId, String rejectionReason) {
        OvertimeRecord record = overtimeRepository.findById(overtimeId)
                .orElseThrow(() -> new IllegalArgumentException("Overtime record not found"));

        if (record.getStatus() != OvertimeRecord.OvertimeStatus.PENDING) {
            throw new IllegalArgumentException("Overtime record is not pending");
        }

        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new IllegalArgumentException("Approver not found"));

        record.setStatus(OvertimeRecord.OvertimeStatus.REJECTED);
        record.setApprovedBy(approver);
        record.setApprovedAt(LocalDateTime.now());
        record.setRejectionReason(rejectionReason);

        return OvertimeRecordResponse.fromEntity(overtimeRepository.save(record));
    }

    @Transactional(readOnly = true)
    public List<OvertimeRecordResponse> getEmployeeOvertime(Long employeeId, LocalDate start, LocalDate end) {
        return overtimeRepository.findByEmployeeIdAndOvertimeDateBetween(employeeId, start, end).stream()
                .map(OvertimeRecordResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OvertimeRecordResponse> getPendingApprovals(LocalDate start, LocalDate end) {
        return overtimeRepository.findByStatusAndOvertimeDateBetween(
                OvertimeRecord.OvertimeStatus.PENDING, start, end).stream()
                .map(OvertimeRecordResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OvertimeRecordResponse> getOvertimeByDateRange(LocalDate start, LocalDate end) {
        return overtimeRepository.findByOvertimeDateBetween(start, end).stream()
                .map(OvertimeRecordResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public BigDecimal getWeeklyOvertimeHours(Long employeeId, LocalDate date) {
        LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);
        return overtimeRepository.sumWeeklyHours(employeeId, weekStart, weekEnd);
    }

    // ==================== SA Overtime Rules ====================

    /**
     * Determine the overtime type based on the date (SA labour law).
     */
    public static OvertimeRecord.OvertimeType determineOvertimeType(LocalDate date, boolean isPublicHoliday) {
        if (isPublicHoliday) {
            return OvertimeRecord.OvertimeType.PUBLIC_HOLIDAY;
        }
        DayOfWeek dow = date.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
            return OvertimeRecord.OvertimeType.WEEKEND;
        }
        return OvertimeRecord.OvertimeType.WEEKDAY;
    }

    /**
     * Get the SA-compliant rate multiplier for the overtime type.
     * Weekday: 1.5x, Weekend/Sunday/Public Holiday: 2.0x
     */
    public static BigDecimal getSAMultiplier(OvertimeRecord.OvertimeType type) {
        return OvertimeRecord.getDefaultMultiplier(type);
    }

    // ==================== Private ====================

    private OvertimeRecordResponse createOvertimeFromAttendance(AttendanceRecord attendance, LocalDate date) {
        OvertimeRecord.OvertimeType type = determineOvertimeType(date, false);

        OvertimeRecord record = new OvertimeRecord();
        record.setEmployee(attendance.getEmployee());
        record.setAttendanceRecord(attendance);
        record.setOvertimeDate(date);
        record.setHours(attendance.getOvertimeHours());
        record.setType(type);
        record.setRateMultiplier(OvertimeRecord.getDefaultMultiplier(type));
        record.setStatus(OvertimeRecord.OvertimeStatus.PENDING);

        OvertimeRecord saved = overtimeRepository.save(record);
        return OvertimeRecordResponse.fromEntity(saved);
    }

    private void validateWeeklyOvertimeLimit(Long employeeId, LocalDate date, BigDecimal newHours) {
        BigDecimal currentWeekly = getWeeklyOvertimeHours(employeeId, date);
        BigDecimal projected = currentWeekly.add(newHours);

        if (projected.compareTo(OvertimeRecord.MAX_WEEKLY_OVERTIME_HOURS) > 0) {
            throw new IllegalArgumentException(String.format(
                    "Weekly overtime limit exceeded. Current: %.1f hrs, Requested: %.1f hrs, Max: %s hrs (SA BCEA)",
                    currentWeekly.doubleValue(), newHours.doubleValue(),
                    OvertimeRecord.MAX_WEEKLY_OVERTIME_HOURS.toPlainString()));
        }
    }
}
