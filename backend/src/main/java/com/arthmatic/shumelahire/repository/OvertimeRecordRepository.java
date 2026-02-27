package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.OvertimeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface OvertimeRecordRepository extends JpaRepository<OvertimeRecord, Long> {

    List<OvertimeRecord> findByEmployeeIdAndOvertimeDateBetween(Long employeeId, LocalDate start, LocalDate end);

    List<OvertimeRecord> findByStatusAndOvertimeDateBetween(
            OvertimeRecord.OvertimeStatus status, LocalDate start, LocalDate end);

    List<OvertimeRecord> findByOvertimeDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(o.hours), 0) FROM OvertimeRecord o " +
           "WHERE o.employee.id = :employeeId AND o.overtimeDate BETWEEN :start AND :end " +
           "AND o.status IN ('PENDING', 'APPROVED')")
    BigDecimal sumWeeklyHours(@Param("employeeId") Long employeeId,
                              @Param("start") LocalDate start,
                              @Param("end") LocalDate end);
}
