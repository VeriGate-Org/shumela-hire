package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.LeaveAccrual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveAccrualRepository extends JpaRepository<LeaveAccrual, Long> {

    List<LeaveAccrual> findByLeaveBalanceId(Long leaveBalanceId);

    List<LeaveAccrual> findByEmployeeIdAndLeaveTypeId(Long employeeId, Long leaveTypeId);

    @Query("SELECT la FROM LeaveAccrual la WHERE la.employee.id = :employeeId " +
           "AND la.leaveType.id = :leaveTypeId " +
           "AND la.accrualDate BETWEEN :startDate AND :endDate " +
           "ORDER BY la.accrualDate ASC")
    List<LeaveAccrual> findByEmployeeAndTypeAndDateRange(
            @Param("employeeId") Long employeeId,
            @Param("leaveTypeId") Long leaveTypeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    boolean existsByLeaveBalanceIdAndAccrualPeriodStartAndAccrualPeriodEnd(
            Long leaveBalanceId, LocalDate accrualPeriodStart, LocalDate accrualPeriodEnd);
}
