package com.arthmatic.shumelahire.repository.attendance;

import com.arthmatic.shumelahire.entity.attendance.OvertimeRecord;
import com.arthmatic.shumelahire.entity.attendance.OvertimeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface OvertimeRecordRepository extends JpaRepository<OvertimeRecord, Long> {

    List<OvertimeRecord> findByTenantId(String tenantId);

    @Query("SELECT ot FROM OvertimeRecord ot WHERE ot.employee.id = :employeeId AND ot.overtimeDate BETWEEN :startDate AND :endDate AND ot.tenantId = :tenantId ORDER BY ot.overtimeDate ASC")
    List<OvertimeRecord> findByEmployeeAndDateRange(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("tenantId") String tenantId);

    @Query("SELECT ot FROM OvertimeRecord ot WHERE ot.status = :status AND ot.tenantId = :tenantId ORDER BY ot.overtimeDate ASC")
    List<OvertimeRecord> findByStatus(
            @Param("status") OvertimeStatus status,
            @Param("tenantId") String tenantId);

    @Query("SELECT COALESCE(SUM(ot.hours), 0) FROM OvertimeRecord ot WHERE ot.employee.id = :employeeId AND ot.overtimeDate BETWEEN :startDate AND :endDate AND ot.status IN ('APPROVED', 'PROCESSED') AND ot.tenantId = :tenantId")
    BigDecimal sumApprovedHoursForWeek(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("tenantId") String tenantId);
}
