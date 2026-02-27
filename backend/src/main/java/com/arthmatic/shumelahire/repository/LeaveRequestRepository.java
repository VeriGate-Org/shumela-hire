package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.LeaveRequest;
import com.arthmatic.shumelahire.entity.LeaveRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    Page<LeaveRequest> findByEmployeeId(Long employeeId, Pageable pageable);

    Page<LeaveRequest> findByEmployeeIdAndStatus(Long employeeId, LeaveRequestStatus status, Pageable pageable);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.id = :employeeId " +
           "AND lr.startDate <= :endDate AND lr.endDate >= :startDate " +
           "AND lr.status NOT IN ('REJECTED', 'CANCELLED')")
    List<LeaveRequest> findOverlappingRequests(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.status = :status " +
           "AND lr.employee.reportingManager.id = :managerId")
    Page<LeaveRequest> findPendingForManager(
            @Param("status") LeaveRequestStatus status,
            @Param("managerId") Long managerId,
            Pageable pageable);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.status = 'MANAGER_APPROVED' " +
           "AND lr.tenantId = :tenantId")
    Page<LeaveRequest> findPendingHrApproval(@Param("tenantId") String tenantId, Pageable pageable);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.id = :employeeId " +
           "AND lr.leaveType.id = :leaveTypeId " +
           "AND lr.status IN ('PENDING', 'MANAGER_APPROVED', 'HR_APPROVED', 'APPROVED') " +
           "AND EXTRACT(YEAR FROM lr.startDate) = :year")
    List<LeaveRequest> findActiveRequestsByEmployeeAndTypeAndYear(
            @Param("employeeId") Long employeeId,
            @Param("leaveTypeId") Long leaveTypeId,
            @Param("year") int year);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.tenantId = :tenantId " +
           "AND lr.startDate <= :endDate AND lr.endDate >= :startDate " +
           "AND lr.status IN ('APPROVED', 'MANAGER_APPROVED', 'HR_APPROVED')")
    List<LeaveRequest> findApprovedRequestsInDateRange(
            @Param("tenantId") String tenantId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT lr FROM LeaveRequest lr WHERE " +
           "(:tenantId IS NULL OR lr.tenantId = :tenantId) AND " +
           "(:employeeId IS NULL OR lr.employee.id = :employeeId) AND " +
           "(:status IS NULL OR lr.status = :status) AND " +
           "(:leaveTypeId IS NULL OR lr.leaveType.id = :leaveTypeId) AND " +
           "(:startDate IS NULL OR lr.startDate >= :startDate) AND " +
           "(:endDate IS NULL OR lr.endDate <= :endDate)")
    Page<LeaveRequest> findByFilters(
            @Param("tenantId") String tenantId,
            @Param("employeeId") Long employeeId,
            @Param("status") LeaveRequestStatus status,
            @Param("leaveTypeId") Long leaveTypeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);
}
