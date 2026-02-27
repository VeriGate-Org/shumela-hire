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

    Optional<AttendanceRecord> findByEmployeeIdAndRecordDate(Long employeeId, LocalDate date);

    List<AttendanceRecord> findByEmployeeIdAndRecordDateBetween(Long employeeId, LocalDate start, LocalDate end);

    List<AttendanceRecord> findByRecordDateBetween(LocalDate start, LocalDate end);

    Page<AttendanceRecord> findByRecordDateBetween(LocalDate start, LocalDate end, Pageable pageable);

    List<AttendanceRecord> findByEmployeeIdAndRecordDateBetweenAndStatus(
            Long employeeId, LocalDate start, LocalDate end, AttendanceRecord.AttendanceStatus status);

    @Query("SELECT a FROM AttendanceRecord a WHERE a.clockIn IS NOT NULL AND a.clockOut IS NULL AND a.recordDate < :date")
    List<AttendanceRecord> findOpenRecords(@Param("date") LocalDate date);

    @Query("SELECT a FROM AttendanceRecord a WHERE a.status = 'LATE' AND a.recordDate BETWEEN :start AND :end")
    List<AttendanceRecord> findLateRecords(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COUNT(a) FROM AttendanceRecord a WHERE a.employee.id = :employeeId " +
           "AND a.status = :status AND a.recordDate BETWEEN :start AND :end")
    long countByEmployeeAndStatusAndDateRange(@Param("employeeId") Long employeeId,
                                              @Param("status") AttendanceRecord.AttendanceStatus status,
                                              @Param("start") LocalDate start,
                                              @Param("end") LocalDate end);
}
