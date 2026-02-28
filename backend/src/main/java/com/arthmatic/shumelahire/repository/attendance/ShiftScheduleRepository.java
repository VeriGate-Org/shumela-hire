package com.arthmatic.shumelahire.repository.attendance;

import com.arthmatic.shumelahire.entity.attendance.ScheduleStatus;
import com.arthmatic.shumelahire.entity.attendance.ShiftSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftScheduleRepository extends JpaRepository<ShiftSchedule, Long> {

    List<ShiftSchedule> findByTenantId(String tenantId);

    @Query("SELECT ss FROM ShiftSchedule ss WHERE ss.employee.id = :employeeId AND ss.scheduleDate = :date AND ss.tenantId = :tenantId")
    Optional<ShiftSchedule> findByEmployeeAndDate(
            @Param("employeeId") Long employeeId,
            @Param("date") LocalDate date,
            @Param("tenantId") String tenantId);

    @Query("SELECT ss FROM ShiftSchedule ss WHERE ss.employee.id = :employeeId AND ss.scheduleDate BETWEEN :startDate AND :endDate AND ss.tenantId = :tenantId ORDER BY ss.scheduleDate ASC")
    List<ShiftSchedule> findByEmployeeAndDateRange(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("tenantId") String tenantId);

    @Query("SELECT ss FROM ShiftSchedule ss WHERE ss.scheduleDate BETWEEN :startDate AND :endDate AND ss.tenantId = :tenantId ORDER BY ss.scheduleDate ASC, ss.employee.lastName ASC")
    List<ShiftSchedule> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("tenantId") String tenantId);

    @Query("SELECT ss FROM ShiftSchedule ss WHERE ss.employee.id = :employeeId AND ss.status = :status AND ss.tenantId = :tenantId")
    List<ShiftSchedule> findByEmployeeAndStatus(
            @Param("employeeId") Long employeeId,
            @Param("status") ScheduleStatus status,
            @Param("tenantId") String tenantId);
}
