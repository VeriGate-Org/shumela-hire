package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.AttendanceRecord;
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
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    Page<AttendanceRecord> findByEmployeeId(Long employeeId, Pageable pageable);

    List<AttendanceRecord> findByEmployeeIdAndAttendanceDate(Long employeeId, LocalDate date);

    Optional<AttendanceRecord> findByEmployeeIdAndAttendanceDateAndStatus(
            Long employeeId, LocalDate date, AttendanceRecord.AttendanceStatus status);

    @Query("SELECT a FROM AttendanceRecord a WHERE a.employee.id = :employeeId " +
           "AND a.attendanceDate BETWEEN :startDate AND :endDate " +
           "ORDER BY a.attendanceDate DESC")
    List<AttendanceRecord> findByEmployeeAndDateRange(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT a FROM AttendanceRecord a WHERE a.attendanceDate = :date " +
           "AND (:status IS NULL OR a.status = :status) " +
           "ORDER BY a.employee.lastName")
    Page<AttendanceRecord> findByDateAndOptionalStatus(
            @Param("date") LocalDate date,
            @Param("status") AttendanceRecord.AttendanceStatus status,
            Pageable pageable);

    @Query("SELECT a FROM AttendanceRecord a WHERE a.attendanceDate BETWEEN :startDate AND :endDate " +
           "AND a.lateArrival = true ORDER BY a.attendanceDate DESC")
    List<AttendanceRecord> findLateArrivals(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT a FROM AttendanceRecord a WHERE a.employee.id = :employeeId " +
           "AND a.attendanceDate BETWEEN :startDate AND :endDate " +
           "AND a.lateArrival = true")
    List<AttendanceRecord> findLateArrivalsByEmployee(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(a) FROM AttendanceRecord a WHERE a.employee.id = :employeeId " +
           "AND a.attendanceDate BETWEEN :startDate AND :endDate " +
           "AND a.status IN ('CLOCKED_OUT', 'APPROVED')")
    long countDaysWorked(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT AVG(a.totalHoursWorked) FROM AttendanceRecord a " +
           "WHERE a.employee.id = :employeeId " +
           "AND a.attendanceDate BETWEEN :startDate AND :endDate " +
           "AND a.totalHoursWorked IS NOT NULL")
    Double findAverageHoursWorked(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT a FROM AttendanceRecord a WHERE a.status = 'CLOCKED_IN' " +
           "AND a.attendanceDate < :date")
    List<AttendanceRecord> findOpenAttendanceRecords(@Param("date") LocalDate date);

    @Query("SELECT a FROM AttendanceRecord a WHERE " +
           "(:employeeId IS NULL OR a.employee.id = :employeeId) AND " +
           "(:department IS NULL OR a.employee.department = :department) AND " +
           "a.attendanceDate BETWEEN :startDate AND :endDate " +
           "ORDER BY a.attendanceDate DESC, a.employee.lastName")
    Page<AttendanceRecord> findByFilters(
            @Param("employeeId") Long employeeId,
            @Param("department") String department,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);
}
