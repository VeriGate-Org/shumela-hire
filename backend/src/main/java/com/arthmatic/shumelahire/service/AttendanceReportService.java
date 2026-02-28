package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.entity.AttendanceRecord;
import com.arthmatic.shumelahire.entity.OvertimeRecord;
import com.arthmatic.shumelahire.repository.AttendanceRecordRepository;
import com.arthmatic.shumelahire.repository.OvertimeRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AttendanceReportService {

    @Autowired
    private AttendanceRecordRepository attendanceRepository;

    @Autowired
    private OvertimeRecordRepository overtimeRepository;

    /**
     * Monthly attendance register for an employee.
     */
    public Map<String, Object> getMonthlyRegister(Long employeeId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<AttendanceRecord> records = attendanceRepository.findByEmployeeIdAndRecordDateBetween(
                employeeId, start, end);

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("employeeId", employeeId);
        report.put("year", year);
        report.put("month", month);
        report.put("totalDays", records.size());
        report.put("presentDays", countByStatus(records, AttendanceRecord.AttendanceStatus.PRESENT));
        report.put("lateDays", countByStatus(records, AttendanceRecord.AttendanceStatus.LATE));
        report.put("absentDays", countByStatus(records, AttendanceRecord.AttendanceStatus.ABSENT));
        report.put("halfDays", countByStatus(records, AttendanceRecord.AttendanceStatus.HALF_DAY));
        report.put("leaveDays", countByStatus(records, AttendanceRecord.AttendanceStatus.ON_LEAVE));

        BigDecimal totalRegular = records.stream()
                .map(AttendanceRecord::getRegularHours)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalOvertime = records.stream()
                .map(AttendanceRecord::getOvertimeHours)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalLateMinutes = records.stream()
                .mapToInt(r -> r.getLateMinutes() != null ? r.getLateMinutes() : 0)
                .sum();

        report.put("totalRegularHours", totalRegular);
        report.put("totalOvertimeHours", totalOvertime);
        report.put("totalLateMinutes", totalLateMinutes);

        return report;
    }

    /**
     * Overtime summary for a date range.
     */
    public Map<String, Object> getOvertimeSummary(LocalDate start, LocalDate end) {
        List<OvertimeRecord> records = overtimeRepository.findByOvertimeDateBetween(start, end);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("startDate", start);
        summary.put("endDate", end);
        summary.put("totalRecords", records.size());

        BigDecimal totalHours = records.stream()
                .map(OvertimeRecord::getHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.put("totalHours", totalHours);

        Map<String, BigDecimal> byType = records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getType().name(),
                        Collectors.reducing(BigDecimal.ZERO, OvertimeRecord::getHours, BigDecimal::add)));
        summary.put("hoursByType", byType);

        Map<String, Long> byStatus = records.stream()
                .collect(Collectors.groupingBy(r -> r.getStatus().name(), Collectors.counting()));
        summary.put("countByStatus", byStatus);

        return summary;
    }

    /**
     * Punctuality report for a date range.
     */
    public Map<String, Object> getPunctualityReport(LocalDate start, LocalDate end) {
        List<AttendanceRecord> allRecords = attendanceRepository.findByRecordDateBetween(start, end);
        List<AttendanceRecord> lateRecords = attendanceRepository.findLateRecords(start, end);

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("startDate", start);
        report.put("endDate", end);
        report.put("totalRecords", allRecords.size());
        report.put("lateRecords", lateRecords.size());

        if (!allRecords.isEmpty()) {
            double punctualityRate = ((double) (allRecords.size() - lateRecords.size()) / allRecords.size()) * 100;
            report.put("punctualityRate", BigDecimal.valueOf(punctualityRate).setScale(1, RoundingMode.HALF_UP));
        } else {
            report.put("punctualityRate", BigDecimal.ZERO);
        }

        int averageLateMinutes = lateRecords.isEmpty() ? 0 :
                lateRecords.stream()
                        .mapToInt(r -> r.getLateMinutes() != null ? r.getLateMinutes() : 0)
                        .sum() / lateRecords.size();
        report.put("averageLateMinutes", averageLateMinutes);

        return report;
    }

    /**
     * Payroll export data for a date range.
     */
    public List<Map<String, Object>> getPayrollExport(LocalDate start, LocalDate end) {
        List<AttendanceRecord> records = attendanceRepository.findByRecordDateBetween(start, end);

        // Group by employee
        Map<Long, List<AttendanceRecord>> byEmployee = records.stream()
                .collect(Collectors.groupingBy(r -> r.getEmployee().getId()));

        List<OvertimeRecord> overtimeRecords = overtimeRepository.findByOvertimeDateBetween(start, end);
        Map<Long, List<OvertimeRecord>> overtimeByEmployee = overtimeRecords.stream()
                .filter(o -> o.getStatus() == OvertimeRecord.OvertimeStatus.APPROVED)
                .collect(Collectors.groupingBy(o -> o.getEmployee().getId()));

        List<Map<String, Object>> export = new ArrayList<>();
        for (Map.Entry<Long, List<AttendanceRecord>> entry : byEmployee.entrySet()) {
            Map<String, Object> row = new LinkedHashMap<>();
            List<AttendanceRecord> empRecords = entry.getValue();
            AttendanceRecord first = empRecords.get(0);

            row.put("employeeId", entry.getKey());
            row.put("employeeName", first.getEmployee().getFullName());
            row.put("employeeNumber", first.getEmployee().getEmployeeNumber());
            row.put("department", first.getEmployee().getDepartment());

            BigDecimal regularHours = empRecords.stream()
                    .map(AttendanceRecord::getRegularHours)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            row.put("regularHours", regularHours);

            List<OvertimeRecord> empOvertime = overtimeByEmployee.getOrDefault(entry.getKey(), List.of());
            BigDecimal weekdayOT = sumOvertimeByType(empOvertime, OvertimeRecord.OvertimeType.WEEKDAY);
            BigDecimal weekendOT = sumOvertimeByType(empOvertime, OvertimeRecord.OvertimeType.WEEKEND);
            BigDecimal holidayOT = sumOvertimeByType(empOvertime, OvertimeRecord.OvertimeType.PUBLIC_HOLIDAY);
            BigDecimal nightOT = sumOvertimeByType(empOvertime, OvertimeRecord.OvertimeType.NIGHT);

            row.put("weekdayOvertimeHours", weekdayOT);
            row.put("weekendOvertimeHours", weekendOT);
            row.put("publicHolidayOvertimeHours", holidayOT);
            row.put("nightOvertimeHours", nightOT);
            row.put("daysWorked", empRecords.size());
            row.put("daysLate", empRecords.stream()
                    .filter(r -> r.getStatus() == AttendanceRecord.AttendanceStatus.LATE).count());

            export.add(row);
        }

        return export;
    }

    private long countByStatus(List<AttendanceRecord> records, AttendanceRecord.AttendanceStatus status) {
        return records.stream().filter(r -> r.getStatus() == status).count();
    }

    private BigDecimal sumOvertimeByType(List<OvertimeRecord> records, OvertimeRecord.OvertimeType type) {
        return records.stream()
                .filter(r -> r.getType() == type)
                .map(OvertimeRecord::getHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
