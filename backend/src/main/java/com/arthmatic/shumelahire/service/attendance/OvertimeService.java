package com.arthmatic.shumelahire.service.attendance;

import com.arthmatic.shumelahire.dto.attendance.OvertimeRecordRequest;
import com.arthmatic.shumelahire.dto.attendance.OvertimeRecordResponse;
import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.attendance.AttendanceRecord;
import com.arthmatic.shumelahire.entity.attendance.OvertimeRecord;
import com.arthmatic.shumelahire.entity.attendance.OvertimeStatus;
import com.arthmatic.shumelahire.entity.attendance.OvertimeType;
import com.arthmatic.shumelahire.repository.EmployeeRepository;
import com.arthmatic.shumelahire.repository.attendance.AttendanceRecordRepository;
import com.arthmatic.shumelahire.repository.attendance.OvertimeRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OvertimeService {

    private static final Logger logger = LoggerFactory.getLogger(OvertimeService.class);

    // SA BCEA overtime rules
    private static final BigDecimal WEEKDAY_RATE = new BigDecimal("1.5");
    private static final BigDecimal SUNDAY_RATE = new BigDecimal("2.0");
    private static final BigDecimal PUBLIC_HOLIDAY_RATE = new BigDecimal("2.0");
    private static final BigDecimal NIGHT_RATE = new BigDecimal("1.5");
    private static final BigDecimal MAX_WEEKLY_OVERTIME_HOURS = new BigDecimal("10.0");

    @Autowired
    private OvertimeRecordRepository overtimeRecordRepository;

    @Autowired
    private AttendanceRecordRepository attendanceRecordRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public OvertimeRecordResponse createOvertimeRecord(OvertimeRecordRequest request) {
        logger.info("Creating overtime record for employee: {} on {}", request.getEmployeeId(), request.getOvertimeDate());

        Employee employee = findEmployeeById(request.getEmployeeId());
        OvertimeType overtimeType = OvertimeType.valueOf(request.getOvertimeType());
        LocalDate overtimeDate = LocalDate.parse(request.getOvertimeDate());

        // Validate weekly overtime cap per BCEA (max 10 hours/week)
        validateWeeklyOvertimeCap(request.getEmployeeId(), overtimeDate, request.getHours(), employee.getTenantId());

        OvertimeRecord record = new OvertimeRecord();
        record.setEmployee(employee);
        record.setOvertimeDate(overtimeDate);
        record.setOvertimeType(overtimeType);
        record.setHours(request.getHours());
        record.setStatus(OvertimeStatus.PENDING);
        record.setRequestedBy(request.getRequestedBy());
        record.setNotes(request.getNotes());

        // Set rate multiplier based on SA BCEA rules
        BigDecimal rateMultiplier = request.getRateMultiplier();
        if (rateMultiplier == null) {
            rateMultiplier = calculateRateMultiplier(overtimeType, overtimeDate);
        }
        record.setRateMultiplier(rateMultiplier);

        // Link to attendance record if provided
        if (request.getAttendanceRecordId() != null) {
            AttendanceRecord attendance = attendanceRecordRepository.findById(request.getAttendanceRecordId())
                    .orElseThrow(() -> new IllegalArgumentException("Attendance record not found: " + request.getAttendanceRecordId()));
            record.setAttendanceRecord(attendance);
        }

        OvertimeRecord saved = overtimeRecordRepository.save(record);
        logger.info("Overtime record created: id={}, type={}, hours={}, rate={}",
                saved.getId(), overtimeType, request.getHours(), rateMultiplier);
        return OvertimeRecordResponse.fromEntity(saved);
    }

    public OvertimeRecordResponse approveOvertime(Long id, String approver) {
        logger.info("Approving overtime record: {} by {}", id, approver);

        OvertimeRecord record = findById(id);
        if (record.getStatus() != OvertimeStatus.PENDING) {
            throw new IllegalStateException("Only PENDING overtime records can be approved");
        }

        record.setStatus(OvertimeStatus.APPROVED);
        record.setApprovedBy(approver);
        record.setApprovedAt(java.time.LocalDateTime.now());

        OvertimeRecord saved = overtimeRecordRepository.save(record);
        return OvertimeRecordResponse.fromEntity(saved);
    }

    public OvertimeRecordResponse rejectOvertime(Long id, String approver, String reason) {
        logger.info("Rejecting overtime record: {} by {}", id, approver);

        OvertimeRecord record = findById(id);
        if (record.getStatus() != OvertimeStatus.PENDING) {
            throw new IllegalStateException("Only PENDING overtime records can be rejected");
        }

        record.setStatus(OvertimeStatus.REJECTED);
        record.setApprovedBy(approver);
        record.setApprovedAt(java.time.LocalDateTime.now());
        record.setRejectionReason(reason);

        OvertimeRecord saved = overtimeRecordRepository.save(record);
        return OvertimeRecordResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public OvertimeRecordResponse getOvertimeRecord(Long id) {
        return OvertimeRecordResponse.fromEntity(findById(id));
    }

    @Transactional(readOnly = true)
    public List<OvertimeRecordResponse> getEmployeeOvertime(Long employeeId, String startDate, String endDate) {
        Employee employee = findEmployeeById(employeeId);
        return overtimeRecordRepository.findByEmployeeAndDateRange(
                employeeId, LocalDate.parse(startDate), LocalDate.parse(endDate), employee.getTenantId()
        ).stream().map(OvertimeRecordResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OvertimeRecordResponse> getPendingOvertimeRecords() {
        return overtimeRecordRepository.findByStatus(OvertimeStatus.PENDING, null)
                .stream().map(OvertimeRecordResponse::fromEntity).collect(Collectors.toList());
    }

    /**
     * Calculate SA BCEA overtime rate multiplier.
     * - Weekday overtime: 1.5x
     * - Sunday overtime: 2.0x
     * - Public holiday overtime: 2.0x
     * - Night shift overtime: 1.5x
     */
    BigDecimal calculateRateMultiplier(OvertimeType type, LocalDate date) {
        switch (type) {
            case WEEKDAY:
                // Sunday gets 2.0x per BCEA
                if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    return SUNDAY_RATE;
                }
                return WEEKDAY_RATE;
            case WEEKEND:
                if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    return SUNDAY_RATE;
                }
                // Saturday gets 1.5x
                return WEEKDAY_RATE;
            case PUBLIC_HOLIDAY:
                return PUBLIC_HOLIDAY_RATE;
            case NIGHT:
                return NIGHT_RATE;
            default:
                return WEEKDAY_RATE;
        }
    }

    /**
     * Validate that adding these hours won't exceed the BCEA weekly overtime cap (10 hours/week).
     */
    void validateWeeklyOvertimeCap(Long employeeId, LocalDate date, BigDecimal newHours, String tenantId) {
        LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);

        BigDecimal existingHours = overtimeRecordRepository.sumApprovedHoursForWeek(
                employeeId, weekStart, weekEnd, tenantId);

        BigDecimal totalHours = existingHours.add(newHours);
        if (totalHours.compareTo(MAX_WEEKLY_OVERTIME_HOURS) > 0) {
            BigDecimal remaining = MAX_WEEKLY_OVERTIME_HOURS.subtract(existingHours);
            throw new IllegalArgumentException(
                    String.format("BCEA weekly overtime limit exceeded. Maximum: %s hours/week. Already used: %s hours. Remaining: %s hours.",
                            MAX_WEEKLY_OVERTIME_HOURS, existingHours, remaining.max(BigDecimal.ZERO)));
        }
    }

    private OvertimeRecord findById(Long id) {
        return overtimeRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Overtime record not found: " + id));
    }

    private Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + id));
    }
}
