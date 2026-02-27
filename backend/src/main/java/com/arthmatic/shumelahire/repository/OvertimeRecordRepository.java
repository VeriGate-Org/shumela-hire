package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.OvertimeRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface OvertimeRecordRepository extends JpaRepository<OvertimeRecord, Long> {

    Page<OvertimeRecord> findByEmployeeId(Long employeeId, Pageable pageable);

    List<OvertimeRecord> findByEmployeeIdAndOvertimeDateBetween(
            Long employeeId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT o FROM OvertimeRecord o WHERE o.status = 'PENDING' " +
           "ORDER BY o.overtimeDate DESC")
    Page<OvertimeRecord> findPendingApprovals(Pageable pageable);

    @Query("SELECT COALESCE(SUM(o.overtimeHours), 0) FROM OvertimeRecord o " +
           "WHERE o.employee.id = :employeeId " +
           "AND o.overtimeDate BETWEEN :weekStart AND :weekEnd " +
           "AND o.status IN ('PENDING', 'APPROVED')")
    BigDecimal sumWeeklyOvertimeHours(
            @Param("employeeId") Long employeeId,
            @Param("weekStart") LocalDate weekStart,
            @Param("weekEnd") LocalDate weekEnd);

    @Query("SELECT COALESCE(SUM(o.overtimeHours), 0) FROM OvertimeRecord o " +
           "WHERE o.employee.id = :employeeId " +
           "AND o.overtimeDate BETWEEN :monthStart AND :monthEnd " +
           "AND o.status IN ('PENDING', 'APPROVED')")
    BigDecimal sumMonthlyOvertimeHours(
            @Param("employeeId") Long employeeId,
            @Param("monthStart") LocalDate monthStart,
            @Param("monthEnd") LocalDate monthEnd);

    @Query("SELECT o FROM OvertimeRecord o WHERE o.exceedsBceaWeeklyLimit = true " +
           "AND o.overtimeDate BETWEEN :startDate AND :endDate " +
           "ORDER BY o.overtimeDate DESC")
    List<OvertimeRecord> findBceaExceedances(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT o FROM OvertimeRecord o WHERE " +
           "(:employeeId IS NULL OR o.employee.id = :employeeId) AND " +
           "(:status IS NULL OR o.status = :status) AND " +
           "o.overtimeDate BETWEEN :startDate AND :endDate " +
           "ORDER BY o.overtimeDate DESC")
    Page<OvertimeRecord> findByFilters(
            @Param("employeeId") Long employeeId,
            @Param("status") OvertimeRecord.OvertimeStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);
}
