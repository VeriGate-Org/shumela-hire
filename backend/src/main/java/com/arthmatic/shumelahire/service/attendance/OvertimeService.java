package com.arthmatic.shumelahire.service.attendance;

import com.arthmatic.shumelahire.dto.attendance.OvertimeRecordRequest;
import com.arthmatic.shumelahire.dto.attendance.OvertimeRecordResponse;
import com.arthmatic.shumelahire.entity.AttendanceRecord;
import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.OvertimeRecord;
import com.arthmatic.shumelahire.repository.AttendanceRecordRepository;
import com.arthmatic.shumelahire.repository.EmployeeRepository;
import com.arthmatic.shumelahire.repository.OvertimeRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OvertimeService {

    private static final Logger logger = LoggerFactory.getLogger(OvertimeService.class);

    private final OvertimeRecordRepository overtimeRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRecordRepository attendanceRepository;

    public OvertimeService(OvertimeRecordRepository overtimeRepository,
                           EmployeeRepository employeeRepository,
                           AttendanceRecordRepository attendanceRepository) {
        this.overtimeRepository = overtimeRepository;
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
    }

    public OvertimeRecordResponse create(OvertimeRecordRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        OvertimeRecord record = new OvertimeRecord();
        record.setEmployee(employee);
        record.setOvertimeDate(request.getOvertimeDate());
        record.setOvertimeHours(request.getOvertimeHours());

        OvertimeRecord.OvertimeType type = OvertimeRecord.OvertimeType.valueOf(request.getOvertimeType());
        record.setOvertimeType(type);

        // Apply SA BCEA rate multipliers
        BigDecimal rateMultiplier = resolveRateMultiplier(type);
        record.setRateMultiplier(rateMultiplier);

        if (Boolean.TRUE.equals(request.getIsPreApproved())) {
            record.setIsPreApproved(true);
            record.setStatus(OvertimeRecord.OvertimeStatus.APPROVED);
        }
        record.setReason(request.getReason());

        if (request.getAttendanceRecordId() != null) {
            AttendanceRecord attendance = attendanceRepository.findById(request.getAttendanceRecordId()).orElse(null);
            record.setAttendanceRecord(attendance);
        }

        // BCEA daily limit check
        if (request.getOvertimeHours().compareTo(OvertimeRecord.BCEA_MAX_DAILY_OVERTIME) > 0) {
            logger.warn("Overtime for employee {} on {} exceeds BCEA daily limit of {} hours",
                    employee.getEmployeeNumber(), request.getOvertimeDate(), OvertimeRecord.BCEA_MAX_DAILY_OVERTIME);
        }

        // Calculate weekly total and check BCEA weekly limit
        LocalDate weekStart = request.getOvertimeDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);
        BigDecimal weeklyTotal = overtimeRepository.sumWeeklyOvertimeHours(
                employee.getId(), weekStart, weekEnd);
        BigDecimal newWeeklyTotal = weeklyTotal.add(request.getOvertimeHours());
        record.setWeeklyOvertimeTotal(newWeeklyTotal);
        record.setBceaWeeklyLimitHours(OvertimeRecord.BCEA_MAX_WEEKLY_OVERTIME);

        if (newWeeklyTotal.compareTo(OvertimeRecord.BCEA_MAX_WEEKLY_OVERTIME) > 0) {
            record.setExceedsBceaWeeklyLimit(true);
            logger.warn("Employee {} weekly overtime total ({}) exceeds BCEA limit of {} hours",
                    employee.getEmployeeNumber(), newWeeklyTotal, OvertimeRecord.BCEA_MAX_WEEKLY_OVERTIME);
        }

        // Monthly total
        LocalDate monthStart = request.getOvertimeDate().withDayOfMonth(1);
        LocalDate monthEnd = request.getOvertimeDate().with(TemporalAdjusters.lastDayOfMonth());
        BigDecimal monthlyTotal = overtimeRepository.sumMonthlyOvertimeHours(
                employee.getId(), monthStart, monthEnd);
        record.setMonthlyOvertimeTotal(monthlyTotal.add(request.getOvertimeHours()));

        record = overtimeRepository.save(record);
        logger.info("Overtime recorded: {} hours ({}) for employee {} on {}",
                record.getOvertimeHours(), record.getOvertimeType(), employee.getEmployeeNumber(), record.getOvertimeDate());
        return OvertimeRecordResponse.fromEntity(record);
    }

    @Transactional(readOnly = true)
    public OvertimeRecordResponse getById(Long id) {
        OvertimeRecord record = overtimeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Overtime record not found"));
        return OvertimeRecordResponse.fromEntity(record);
    }

    @Transactional(readOnly = true)
    public Page<OvertimeRecordResponse> getByEmployee(Long employeeId, Pageable pageable) {
        return overtimeRepository.findByEmployeeId(employeeId, pageable)
                .map(OvertimeRecordResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<OvertimeRecordResponse> getPendingApprovals(Pageable pageable) {
        return overtimeRepository.findPendingApprovals(pageable)
                .map(OvertimeRecordResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<OvertimeRecordResponse> search(Long employeeId, OvertimeRecord.OvertimeStatus status,
                                                LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return overtimeRepository.findByFilters(employeeId, status, startDate, endDate, pageable)
                .map(OvertimeRecordResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<OvertimeRecordResponse> getBceaExceedances(LocalDate startDate, LocalDate endDate) {
        return overtimeRepository.findBceaExceedances(startDate, endDate).stream()
                .map(OvertimeRecordResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public OvertimeRecordResponse approve(Long id, Long approvedById) {
        OvertimeRecord record = overtimeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Overtime record not found"));
        Employee approver = employeeRepository.findById(approvedById)
                .orElseThrow(() -> new IllegalArgumentException("Approver not found"));

        record.setStatus(OvertimeRecord.OvertimeStatus.APPROVED);
        record.setApprovedBy(approver);
        record.setApprovedAt(LocalDateTime.now());
        record = overtimeRepository.save(record);
        logger.info("Overtime {} approved by {}", id, approver.getFullName());
        return OvertimeRecordResponse.fromEntity(record);
    }

    public OvertimeRecordResponse reject(Long id, Long rejectedById, String reason) {
        OvertimeRecord record = overtimeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Overtime record not found"));
        Employee rejector = employeeRepository.findById(rejectedById)
                .orElseThrow(() -> new IllegalArgumentException("Rejector not found"));

        record.setStatus(OvertimeRecord.OvertimeStatus.REJECTED);
        record.setApprovedBy(rejector);
        record.setApprovedAt(LocalDateTime.now());
        record.setRejectionReason(reason);
        record = overtimeRepository.save(record);
        logger.info("Overtime {} rejected by {}", id, rejector.getFullName());
        return OvertimeRecordResponse.fromEntity(record);
    }

    /**
     * Resolve BCEA rate multiplier based on overtime type.
     * SA Basic Conditions of Employment Act (BCEA) rates:
     * - Weekday overtime: 1.5x
     * - Saturday: 1.5x
     * - Sunday: 2.0x
     * - Public holiday: 2.0x
     */
    public static BigDecimal resolveRateMultiplier(OvertimeRecord.OvertimeType type) {
        return switch (type) {
            case WEEKDAY -> OvertimeRecord.WEEKDAY_RATE;
            case SATURDAY -> OvertimeRecord.SATURDAY_RATE;
            case SUNDAY -> OvertimeRecord.SUNDAY_RATE;
            case PUBLIC_HOLIDAY -> OvertimeRecord.PUBLIC_HOLIDAY_RATE;
        };
    }
}
