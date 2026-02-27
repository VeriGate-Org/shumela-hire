package com.arthmatic.shumelahire.service.attendance;

import com.arthmatic.shumelahire.dto.attendance.*;
import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.attendance.*;
import com.arthmatic.shumelahire.repository.EmployeeRepository;
import com.arthmatic.shumelahire.repository.attendance.ShiftPatternRepository;
import com.arthmatic.shumelahire.repository.attendance.ShiftRepository;
import com.arthmatic.shumelahire.repository.attendance.ShiftScheduleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShiftService {

    private static final Logger logger = LoggerFactory.getLogger(ShiftService.class);

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private ShiftPatternRepository shiftPatternRepository;

    @Autowired
    private ShiftScheduleRepository shiftScheduleRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    // ==================== Shift Operations ====================

    public ShiftResponse createShift(ShiftRequest request) {
        logger.info("Creating shift: {}", request.getName());

        Shift shift = new Shift();
        shift.setName(request.getName());
        shift.setCode(request.getCode());
        shift.setStartTime(LocalTime.parse(request.getStartTime()));
        shift.setEndTime(LocalTime.parse(request.getEndTime()));
        shift.setBreakDurationMinutes(request.getBreakDurationMinutes() != null ? request.getBreakDurationMinutes() : 0);
        shift.setGracePeriodMinutes(request.getGracePeriodMinutes() != null ? request.getGracePeriodMinutes() : 0);
        shift.setIsNightShift(request.getIsNightShift() != null ? request.getIsNightShift() : false);
        shift.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        shift.setColor(request.getColor());

        Shift saved = shiftRepository.save(shift);
        logger.info("Shift created: {} (id={})", saved.getName(), saved.getId());
        return ShiftResponse.fromEntity(saved);
    }

    public ShiftResponse updateShift(Long id, ShiftRequest request) {
        logger.info("Updating shift: {}", id);

        Shift shift = findShiftById(id);
        shift.setName(request.getName());
        shift.setCode(request.getCode());
        shift.setStartTime(LocalTime.parse(request.getStartTime()));
        shift.setEndTime(LocalTime.parse(request.getEndTime()));
        if (request.getBreakDurationMinutes() != null) shift.setBreakDurationMinutes(request.getBreakDurationMinutes());
        if (request.getGracePeriodMinutes() != null) shift.setGracePeriodMinutes(request.getGracePeriodMinutes());
        if (request.getIsNightShift() != null) shift.setIsNightShift(request.getIsNightShift());
        if (request.getIsActive() != null) shift.setIsActive(request.getIsActive());
        shift.setColor(request.getColor());

        Shift saved = shiftRepository.save(shift);
        return ShiftResponse.fromEntity(saved);
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
        return shiftRepository.findAll().stream()
                .filter(s -> Boolean.TRUE.equals(s.getIsActive()))
                .map(ShiftResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public void deleteShift(Long id) {
        Shift shift = findShiftById(id);
        shift.setIsActive(false);
        shiftRepository.save(shift);
        logger.info("Shift deactivated: {}", id);
    }

    // ==================== Shift Pattern Operations ====================

    public ShiftPatternResponse createShiftPattern(ShiftPatternRequest request) {
        logger.info("Creating shift pattern: {}", request.getName());

        ShiftPattern pattern = new ShiftPattern();
        pattern.setName(request.getName());
        pattern.setDescription(request.getDescription());
        pattern.setRotationDays(request.getRotationDays());
        pattern.setPatternDefinition(request.getPatternDefinition());
        pattern.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        ShiftPattern saved = shiftPatternRepository.save(pattern);
        logger.info("Shift pattern created: {} (id={})", saved.getName(), saved.getId());
        return ShiftPatternResponse.fromEntity(saved);
    }

    public ShiftPatternResponse updateShiftPattern(Long id, ShiftPatternRequest request) {
        logger.info("Updating shift pattern: {}", id);

        ShiftPattern pattern = findPatternById(id);
        pattern.setName(request.getName());
        pattern.setDescription(request.getDescription());
        pattern.setRotationDays(request.getRotationDays());
        pattern.setPatternDefinition(request.getPatternDefinition());
        if (request.getIsActive() != null) pattern.setIsActive(request.getIsActive());

        ShiftPattern saved = shiftPatternRepository.save(pattern);
        return ShiftPatternResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public ShiftPatternResponse getShiftPattern(Long id) {
        return ShiftPatternResponse.fromEntity(findPatternById(id));
    }

    @Transactional(readOnly = true)
    public List<ShiftPatternResponse> getAllShiftPatterns() {
        return shiftPatternRepository.findAll().stream()
                .map(ShiftPatternResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public void deleteShiftPattern(Long id) {
        ShiftPattern pattern = findPatternById(id);
        pattern.setIsActive(false);
        shiftPatternRepository.save(pattern);
        logger.info("Shift pattern deactivated: {}", id);
    }

    // ==================== Schedule Operations ====================

    public ShiftScheduleResponse createSchedule(ShiftScheduleRequest request) {
        logger.info("Creating shift schedule for employee {} on {}", request.getEmployeeId(), request.getScheduleDate());

        Employee employee = findEmployeeById(request.getEmployeeId());
        Shift shift = findShiftById(request.getShiftId());

        ShiftSchedule schedule = new ShiftSchedule();
        schedule.setEmployee(employee);
        schedule.setShift(shift);
        schedule.setScheduleDate(LocalDate.parse(request.getScheduleDate()));
        schedule.setStatus(ScheduleStatus.SCHEDULED);

        if (request.getShiftPatternId() != null) {
            schedule.setShiftPattern(findPatternById(request.getShiftPatternId()));
        }

        ShiftSchedule saved = shiftScheduleRepository.save(schedule);
        logger.info("Shift schedule created: id={}", saved.getId());
        return ShiftScheduleResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public ShiftScheduleResponse getSchedule(Long id) {
        return ShiftScheduleResponse.fromEntity(findScheduleById(id));
    }

    @Transactional(readOnly = true)
    public List<ShiftScheduleResponse> getSchedulesByDateRange(String startDate, String endDate) {
        return shiftScheduleRepository.findByDateRange(
                LocalDate.parse(startDate), LocalDate.parse(endDate), null
        ).stream().map(ShiftScheduleResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ShiftScheduleResponse> getEmployeeSchedules(Long employeeId, String startDate, String endDate) {
        return shiftScheduleRepository.findByEmployeeAndDateRange(
                employeeId, LocalDate.parse(startDate), LocalDate.parse(endDate), null
        ).stream().map(ShiftScheduleResponse::fromEntity).collect(Collectors.toList());
    }

    public void deleteSchedule(Long id) {
        ShiftSchedule schedule = findScheduleById(id);
        schedule.setStatus(ScheduleStatus.CANCELLED);
        shiftScheduleRepository.save(schedule);
        logger.info("Shift schedule cancelled: {}", id);
    }

    // ==================== Private Helpers ====================

    private Shift findShiftById(Long id) {
        return shiftRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shift not found: " + id));
    }

    private ShiftPattern findPatternById(Long id) {
        return shiftPatternRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shift pattern not found: " + id));
    }

    ShiftSchedule findScheduleById(Long id) {
        return shiftScheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shift schedule not found: " + id));
    }

    private Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + id));
    }
}
