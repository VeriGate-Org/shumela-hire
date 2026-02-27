package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.LeavePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeavePolicyRepository extends JpaRepository<LeavePolicy, Long> {

    List<LeavePolicy> findByTenantIdAndActiveTrue(String tenantId);

    List<LeavePolicy> findByTenantIdAndLeaveTypeId(String tenantId, Long leaveTypeId);

    @Query("SELECT lp FROM LeavePolicy lp WHERE lp.tenantId = :tenantId " +
           "AND lp.leaveType.id = :leaveTypeId " +
           "AND lp.active = true " +
           "AND lp.effectiveFrom <= :effectiveDate " +
           "AND (lp.effectiveTo IS NULL OR lp.effectiveTo >= :effectiveDate) " +
           "AND (:department IS NULL OR lp.department IS NULL OR lp.department = :department) " +
           "AND (:employmentType IS NULL OR lp.employmentType IS NULL OR lp.employmentType = :employmentType) " +
           "AND (:jobGrade IS NULL OR lp.jobGrade IS NULL OR lp.jobGrade = :jobGrade)")
    List<LeavePolicy> findApplicablePolicies(
            @Param("tenantId") String tenantId,
            @Param("leaveTypeId") Long leaveTypeId,
            @Param("effectiveDate") LocalDate effectiveDate,
            @Param("department") String department,
            @Param("employmentType") String employmentType,
            @Param("jobGrade") String jobGrade);
}
