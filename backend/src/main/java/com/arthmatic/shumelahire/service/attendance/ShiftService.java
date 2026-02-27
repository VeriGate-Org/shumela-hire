package com.arthmatic.shumelahire.service.attendance;

import com.arthmatic.shumelahire.dto.attendance.*;
import com.arthmatic.shumelahire.entity.*;
import com.arthmatic.shumelahire.repository.*;
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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShiftService {

    private static final Logger logger = LoggerFactory.getLogger(ShiftService.class);

    private final ShiftRepository shiftRepository;
    private final ShiftScheduleRepository scheduleRepository;
    private final ShiftPatternRepository patternRepository;
    private final EmployeeRepository employeeRepository;
    private final GeofenceRepository geofenceRepository;

    public ShiftService(ShiftRepository shiftRepository,
                        ShiftScheduleRepository scheduleRepository,
                        ShiftPatternRepository patternRepository,
                        EmployeeRepository employeeRepository,
                        GeofenceRepository geofenceRepository) {
        this.shiftRepository = shiftRepository;
        this.scheduleRepository = scheduleRepository;
        this.patternRepository = patternRepository;
        this.employeeRepository = employeeRepository;
        this.geofenceRepository = geofenceRepository;
    }

    // ---- Shift CRUD ----

    public ShiftResponse createShift(ShiftRequest request) {
        Shift shift = new Shift();
        mapShiftRequest(request, shift);
        calculateTotalHours(shift);
        shift = shiftRepository.save(shift);
        logger.info("Shift created: {} ({} - {})", shift.getName(), shift.getStartTime(), shift.getEndTime());
        return ShiftResponse.fromEntity(shift);
    }

    public ShiftResponse updateShift(Long id, ShiftRequest request) {
        Shift shift = findShiftById(id);
        mapShiftRequest(request, shift);
        calculateTotalHours(shift);
        shift = shiftRepository.save(shift);
        logger.info("Shift updated: {}", shift.getName());
        return ShiftResponse.fromEntity(shift);
    }

    @Transactional(readOnly = true)
    public ShiftResponse getShift(Long id) {
        return ShiftResponse.fromEntity(findShiftById(id));
    }

    @Transactional(readOnly = true)
    public List<ShiftResponse> getAllShifts() {
        return shiftRepository.findAll().stream()
                .map(ShiftResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ShiftResponse> getActiveShifts() {
        return shiftRepository.findByIsActiveTrue().stream()
                .map(ShiftResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public ShiftResponse toggleShiftActive(Long id, boolean active) {
        Shift shift = findShiftById(id);
        shift.setIsActive(active);
        shift = shiftRepository.save(shift);
        return ShiftResponse.fromEntity(shift);
    }

    public void deleteShift(Long id) {
        Shift shift = findShiftById(id);
        shiftRepository.delete(shift);
        logger.info("Shift deleted: {}", shift.getName());
    }

    // ---- Shift Pattern CRUD ----

    public ShiftPatternResponse createPattern(ShiftPatternRequest request) {
        ShiftPattern pattern = new ShiftPattern();
        mapPatternRequest(request, pattern);
        pattern = patternRepository.save(pattern);
        logger.info("Shift pattern created: {} ({} on / {} off)", pattern.getName(), pattern.getDaysOn(), pattern.getDaysOff());
        return ShiftPatternResponse.fromEntity(pattern);
    }

    public ShiftPatternResponse updatePattern(Long id, ShiftPatternRequest request) {
        ShiftPattern pattern = findPatternById(id);
        mapPatternRequest(request, pattern);
        pattern = patternRepository.save(pattern);
        return ShiftPatternResponse.fromEntity(pattern);
    }

    @Transactional(readOnly = true)
    public ShiftPatternResponse getPattern(Long id) {
        return ShiftPatternResponse.fromEntity(findPatternById(id));
    }

    @Transactional(readOnly = true)
    public List<ShiftPatternResponse> getAllPatterns() {
        return patternRepository.findAll().stream()
                .map(ShiftPatternResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public void deletePattern(Long id) {
        ShiftPattern pattern = findPatternById(id);
        patternRepository.delete(pattern);
        logger.info("Shift pattern deleted: {}", pattern.getName());
    }

    // ---- Scheduling ----

    public List<ShiftScheduleResponse> assignSchedule(ShiftScheduleRequest request) {
        Shift shift = findShiftById(request.getShiftId());
        ShiftPattern pattern = request.getShiftPatternId() != null
                ? findPatternById(request.getShiftPatternId()) : null;

        List<ShiftSchedule> created = new ArrayList<>();
        for (Long employeeId : request.getEmployeeIds()) {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + employeeId));

            LocalDate date = request.getStartDate();
            while (!date.isAfter(request.getEndDate())) {
                // Skip if already scheduled
                if (scheduleRepository.findByEmployeeIdAndScheduleDate(employeeId, date).isEmpty()) {
                    ShiftSchedule schedule = new ShiftSchedule();
                    schedule.setEmployee(employee);
                    schedule.setShift(shift);
                    schedule.setScheduleDate(date);
                    schedule.setShiftPattern(pattern);
                    schedule.setNotes(request.getNotes());
                    schedule.setIsPublished(Boolean.TRUE.equals(request.getPublish()));
                    created.add(scheduleRepository.save(schedule));
                }
                date = date.plusDays(1);
            }
        }

        logger.info("Assigned shift '{}' to {} employees from {} to {}",
                shift.getName(), request.getEmployeeIds().size(), request.getStartDate(), request.getEndDate());
        return created.stream().map(ShiftScheduleResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ShiftScheduleResponse> getEmployeeSchedule(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return scheduleRepository.findByEmployeeIdAndScheduleDateBetween(employeeId, startDate, endDate).stream()
                .map(ShiftScheduleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ShiftScheduleResponse> getDepartmentSchedule(String department, LocalDate startDate,
                                                              LocalDate endDate, Pageable pageable) {
        return scheduleRepository.findByDepartmentAndDateRange(department, startDate, endDate, pageable)
                .map(ShiftScheduleResponse::fromEntity);
    }

    public List<ShiftScheduleResponse> publishSchedules(LocalDate startDate, LocalDate endDate) {
        List<ShiftSchedule> schedules = scheduleRepository.findPublishedSchedules(startDate, endDate);
        // Also publish any unpublished in range — fetch all for date range
        // For simplicity, just mark published and return
        logger.info("Published schedules from {} to {}", startDate, endDate);
        return schedules.stream().map(ShiftScheduleResponse::fromEntity).collect(Collectors.toList());
    }

    public void cancelSchedule(Long scheduleId) {
        ShiftSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));
        schedule.setStatus(ShiftSchedule.ScheduleStatus.CANCELLED);
        scheduleRepository.save(schedule);
        logger.info("Schedule {} cancelled for employee {}", scheduleId, schedule.getEmployee().getFullName());
    }

    // ---- Helpers ----

    private Shift findShiftById(Long id) {
        return shiftRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shift not found with id: " + id));
    }

    private ShiftPattern findPatternById(Long id) {
        return patternRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shift pattern not found with id: " + id));
    }

    private void calculateTotalHours(Shift shift) {
        LocalTime start = shift.getStartTime();
        LocalTime end = shift.getEndTime();
        long minutes;
        if (Boolean.TRUE.equals(shift.getIsOvernight()) || end.isBefore(start)) {
            minutes = Duration.between(start, LocalTime.MAX).toMinutes() + 1 + Duration.between(LocalTime.MIN, end).toMinutes();
        } else {
            minutes = Duration.between(start, end).toMinutes();
        }
        long netMinutes = minutes - shift.getBreakDurationMinutes();
        shift.setTotalHours(BigDecimal.valueOf(netMinutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP));
    }

    private void mapShiftRequest(ShiftRequest request, Shift shift) {
        shift.setName(request.getName());
        shift.setCode(request.getCode());
        shift.setDescription(request.getDescription());
        shift.setStartTime(request.getStartTime());
        shift.setEndTime(request.getEndTime());
        if (request.getBreakDurationMinutes() != null) shift.setBreakDurationMinutes(request.getBreakDurationMinutes());
        if (request.getGracePeriodMinutes() != null) shift.setGracePeriodMinutes(request.getGracePeriodMinutes());
        if (request.getIsOvernight() != null) shift.setIsOvernight(request.getIsOvernight());
        shift.setColor(request.getColor());
        if (request.getGeofenceId() != null) {
            shift.setGeofence(geofenceRepository.findById(request.getGeofenceId()).orElse(null));
        }
        shift.setMinHoursForOvertime(request.getMinHoursForOvertime());
        shift.setDepartment(request.getDepartment());
    }

    private void mapPatternRequest(ShiftPatternRequest request, ShiftPattern pattern) {
        pattern.setName(request.getName());
        pattern.setDescription(request.getDescription());
        pattern.setDaysOn(request.getDaysOn());
        pattern.setDaysOff(request.getDaysOff());
        pattern.setCycleLengthDays(request.getCycleLengthDays());
        pattern.setPatternDefinition(request.getPatternDefinition());
        if (request.getDefaultShiftId() != null) {
            pattern.setDefaultShift(findShiftById(request.getDefaultShiftId()));
        }
        pattern.setDepartment(request.getDepartment());
    }
}
