package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.ShiftSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftScheduleRepository extends JpaRepository<ShiftSchedule, Long> {

    List<ShiftSchedule> findByEmployeeIdAndScheduleDateBetween(
            Long employeeId, LocalDate startDate, LocalDate endDate);

    Optional<ShiftSchedule> findByEmployeeIdAndScheduleDate(Long employeeId, LocalDate date);

    @Query("SELECT s FROM ShiftSchedule s WHERE s.scheduleDate BETWEEN :startDate AND :endDate " +
           "AND s.status = 'SCHEDULED' AND s.isPublished = true " +
           "ORDER BY s.scheduleDate, s.employee.lastName")
    List<ShiftSchedule> findPublishedSchedules(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT s FROM ShiftSchedule s WHERE s.shift.department = :department " +
           "AND s.scheduleDate BETWEEN :startDate AND :endDate " +
           "ORDER BY s.scheduleDate, s.employee.lastName")
    Page<ShiftSchedule> findByDepartmentAndDateRange(
            @Param("department") String department,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    @Query("SELECT s FROM ShiftSchedule s WHERE s.shift.id = :shiftId " +
           "AND s.scheduleDate = :date AND s.status = 'SCHEDULED'")
    List<ShiftSchedule> findByShiftAndDate(
            @Param("shiftId") Long shiftId,
            @Param("date") LocalDate date);

    @Query("SELECT COUNT(s) FROM ShiftSchedule s WHERE s.employee.id = :employeeId " +
           "AND s.scheduleDate BETWEEN :startDate AND :endDate " +
           "AND s.status = 'SCHEDULED'")
    long countScheduledShifts(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
