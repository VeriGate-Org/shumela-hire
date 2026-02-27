package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    List<LeaveBalance> findByEmployeeIdAndLeaveYear(Long employeeId, Integer leaveYear);

    Optional<LeaveBalance> findByTenantIdAndEmployeeIdAndLeaveTypeIdAndLeaveYear(
            String tenantId, Long employeeId, Long leaveTypeId, Integer leaveYear);

    List<LeaveBalance> findByTenantIdAndEmployeeId(String tenantId, Long employeeId);

    @Query("SELECT lb FROM LeaveBalance lb WHERE lb.tenantId = :tenantId " +
           "AND lb.leaveYear = :year " +
           "ORDER BY lb.employee.lastName, lb.employee.firstName")
    List<LeaveBalance> findAllByTenantAndYear(
            @Param("tenantId") String tenantId,
            @Param("year") Integer year);

    @Query("SELECT lb FROM LeaveBalance lb WHERE lb.tenantId = :tenantId " +
           "AND lb.leaveYear = :year " +
           "AND lb.leaveType.id = :leaveTypeId")
    List<LeaveBalance> findByTenantAndYearAndLeaveType(
            @Param("tenantId") String tenantId,
            @Param("year") Integer year,
            @Param("leaveTypeId") Long leaveTypeId);

    boolean existsByTenantIdAndEmployeeIdAndLeaveTypeIdAndLeaveYear(
            String tenantId, Long employeeId, Long leaveTypeId, Integer leaveYear);
}
