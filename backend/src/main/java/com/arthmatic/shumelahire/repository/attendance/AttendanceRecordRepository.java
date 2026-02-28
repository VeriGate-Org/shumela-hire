package com.arthmatic.shumelahire.repository.attendance;

import com.arthmatic.shumelahire.entity.attendance.AttendanceRecord;
import com.arthmatic.shumelahire.entity.attendance.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    List<AttendanceRecord> findByTenantId(String tenantId);

    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.employee.id = :employeeId AND ar.recordDate = :date AND ar.tenantId = :tenantId")
    Optional<AttendanceRecord> findByEmployeeAndDate(
            @Param("employeeId") Long employeeId,
            @Param("date") LocalDate date,
            @Param("tenantId") String tenantId);

    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.employee.id = :employeeId AND ar.recordDate BETWEEN :startDate AND :endDate AND ar.tenantId = :tenantId ORDER BY ar.recordDate ASC")
    List<AttendanceRecord> findByEmployeeAndDateRange(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("tenantId") String tenantId);

    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.recordDate BETWEEN :startDate AND :endDate AND ar.tenantId = :tenantId ORDER BY ar.recordDate ASC")
    List<AttendanceRecord> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("tenantId") String tenantId);

    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.employee.id = :employeeId AND ar.clockInTime IS NOT NULL AND ar.clockOutTime IS NULL AND ar.tenantId = :tenantId")
    Optional<AttendanceRecord> findOpenAttendance(
            @Param("employeeId") Long employeeId,
            @Param("tenantId") String tenantId);

    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.status = :status AND ar.recordDate BETWEEN :startDate AND :endDate AND ar.tenantId = :tenantId")
    List<AttendanceRecord> findByStatusAndDateRange(
            @Param("status") AttendanceStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("tenantId") String tenantId);

    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.clockInTime IS NOT NULL AND ar.clockOutTime IS NULL AND ar.autoClockedOut = false AND ar.tenantId = :tenantId")
    List<AttendanceRecord> findOpenRecords(@Param("tenantId") String tenantId);
}
