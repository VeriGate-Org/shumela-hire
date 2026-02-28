package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.dto.attendance.*;
import com.arthmatic.shumelahire.entity.*;
import com.arthmatic.shumelahire.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ShiftService {

    private static final Logger logger = LoggerFactory.getLogger(ShiftService.class);

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private ShiftPatternRepository patternRepository;

    @Autowired
    private ShiftScheduleRepository scheduleRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // ==================== Shift CRUD ====================

    public ShiftResponse createShift(ShiftRequest request) {
        Shift shift = new Shift();
        mapRequestToShift(request, shift);
        shift.setActive(true);
        Shift saved = shiftRepository.save(shift);
        logger.info("Created shift: {}", saved.getName());
        return ShiftResponse.fromEntity(saved);
    }

    public ShiftResponse updateShift(Long id, ShiftRequest request) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shift not found: " + id));
        mapRequestToShift(request, shift);
        return ShiftResponse.fromEntity(shiftRepository.save(shift));
    }

    @Transactional(readOnly = true)
    public ShiftResponse getShift(Long id) {
        return shiftRepository.findById(id)
                .map(ShiftResponse::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Shift not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<ShiftResponse> getAllActiveShifts() {
        return shiftRepository.findByActiveTrue().stream()
                .map(ShiftResponse::fromEntity)
                .toList();
    }

    public void deactivateShift(Long id) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shift not found: " + id));
        shift.setActive(false);
        shiftRepository.save(shift);
    }

    // ==================== Shift Scheduling ====================

    public List<ShiftScheduleResponse> assignSchedule(ShiftScheduleRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + request.getEmployeeId()));
        Shift shift = shiftRepository.findById(request.getShiftId())
                .orElseThrow(() -> new IllegalArgumentException("Shift not found: " + request.getShiftId()));

        List<ShiftSchedule> schedules = new ArrayList<>();
        for (LocalDate date = request.getStartDate(); !date.isAfter(request.getEndDate()); date = date.plusDays(1)) {
            if (scheduleRepository.findByEmployeeIdAndScheduleDate(employee.getId(), date).isEmpty()) {
                ShiftSchedule schedule = new ShiftSchedule();
                schedule.setEmployee(employee);
                schedule.setShift(shift);
                schedule.setScheduleDate(date);
                schedule.setStatus(ShiftSchedule.ScheduleStatus.SCHEDULED);
                schedule.setNotes(request.getNotes());
                schedules.add(schedule);
            }
        }

        List<ShiftSchedule> saved = scheduleRepository.saveAll(schedules);
        logger.info("Assigned {} schedule entries for employee {}", saved.size(), employee.getId());
        return saved.stream().map(ShiftScheduleResponse::fromEntity).toList();
    }

    public List<ShiftScheduleResponse> bulkAssignSchedule(ShiftScheduleRequest request) {
        if (request.getEmployeeIds() == null || request.getEmployeeIds().isEmpty()) {
            throw new IllegalArgumentException("Employee IDs required for bulk assignment");
        }

        List<ShiftScheduleResponse> results = new ArrayList<>();
        for (Long empId : request.getEmployeeIds()) {
            ShiftScheduleRequest single = new ShiftScheduleRequest();
            single.setEmployeeId(empId);
            single.setShiftId(request.getShiftId());
            single.setStartDate(request.getStartDate());
            single.setEndDate(request.getEndDate());
            single.setNotes(request.getNotes());
            results.addAll(assignSchedule(single));
        }
        return results;
    }

    @Transactional(readOnly = true)
    public List<ShiftScheduleResponse> getEmployeeSchedule(Long employeeId, LocalDate start, LocalDate end) {
        return scheduleRepository.findByEmployeeIdAndScheduleDateBetween(employeeId, start, end).stream()
                .map(ShiftScheduleResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ShiftScheduleResponse> getSchedulesByDateRange(LocalDate start, LocalDate end) {
        return scheduleRepository.findByScheduleDateBetween(start, end).stream()
                .map(ShiftScheduleResponse::fromEntity)
                .toList();
    }

    /**
     * Auto-assign shifts from a rotation pattern.
     * Pattern JSON format: [{"shiftId": 1, "days": 5}, {"shiftId": null, "days": 2}]
     * where null shiftId means day off.
     */
    public int autoAssignFromPattern(Long patternId, List<Long> employeeIds, LocalDate startDate, LocalDate endDate) {
        ShiftPattern pattern = patternRepository.findById(patternId)
                .orElseThrow(() -> new IllegalArgumentException("Shift pattern not found: " + patternId));

        try {
            List<PatternEntry> entries = objectMapper.readValue(pattern.getPatternJson(),
                    new TypeReference<List<PatternEntry>>() {});

            int totalAssigned = 0;
            for (Long empId : employeeIds) {
                Employee employee = employeeRepository.findById(empId)
                        .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + empId));

                int dayIndex = 0;
                for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                    int patternDay = dayIndex % pattern.getRotationDays();
                    PatternEntry entry = getPatternEntryForDay(entries, patternDay);

                    if (entry != null && entry.shiftId != null) {
                        if (scheduleRepository.findByEmployeeIdAndScheduleDate(empId, date).isEmpty()) {
                            Shift shift = shiftRepository.findById(entry.shiftId).orElse(null);
                            if (shift != null) {
                                ShiftSchedule schedule = new ShiftSchedule();
                                schedule.setEmployee(employee);
                                schedule.setShift(shift);
                                schedule.setScheduleDate(date);
                                schedule.setStatus(ShiftSchedule.ScheduleStatus.SCHEDULED);
                                scheduleRepository.save(schedule);
                                totalAssigned++;
                            }
                        }
                    }
                    dayIndex++;
                }
            }

            logger.info("Auto-assigned {} schedules from pattern {}", totalAssigned, pattern.getName());
            return totalAssigned;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid pattern JSON: " + e.getMessage());
        }
    }

    // ==================== Shift Patterns ====================

    @Transactional(readOnly = true)
    public List<ShiftPattern> getAllActivePatterns() {
        return patternRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public ShiftPattern getPattern(Long id) {
        return patternRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shift pattern not found: " + id));
    }

    public ShiftPattern createPattern(ShiftPattern pattern) {
        return patternRepository.save(pattern);
    }

    public ShiftPattern updatePattern(Long id, ShiftPattern updated) {
        ShiftPattern existing = getPattern(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setRotationDays(updated.getRotationDays());
        existing.setPatternJson(updated.getPatternJson());
        return patternRepository.save(existing);
    }

    // ==================== Helpers ====================

    private void mapRequestToShift(ShiftRequest request, Shift shift) {
        shift.setName(request.getName());
        shift.setStartTime(request.getStartTime());
        shift.setEndTime(request.getEndTime());
        shift.setBreakDurationMins(request.getBreakDurationMins() != null ? request.getBreakDurationMins() : 0);
        shift.setGracePeriodMins(request.getGracePeriodMins() != null ? request.getGracePeriodMins() : 0);
        shift.setNightShift(request.getNightShift() != null ? request.getNightShift() : false);
        shift.setColor(request.getColor());
    }

    private PatternEntry getPatternEntryForDay(List<PatternEntry> entries, int dayIndex) {
        int currentDay = 0;
        for (PatternEntry entry : entries) {
            currentDay += entry.days;
            if (dayIndex < currentDay) return entry;
        }
        return null;
    }

    static class PatternEntry {
        public Long shiftId;
        public int days;
    }
}
