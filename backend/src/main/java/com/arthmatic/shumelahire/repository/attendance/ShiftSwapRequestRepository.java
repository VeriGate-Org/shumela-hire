package com.arthmatic.shumelahire.repository.attendance;

import com.arthmatic.shumelahire.entity.attendance.ShiftSwapRequest;
import com.arthmatic.shumelahire.entity.attendance.SwapRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftSwapRequestRepository extends JpaRepository<ShiftSwapRequest, Long> {

    List<ShiftSwapRequest> findByTenantId(String tenantId);

    @Query("SELECT sr FROM ShiftSwapRequest sr WHERE sr.requesterEmployee.id = :employeeId AND sr.tenantId = :tenantId ORDER BY sr.createdAt DESC")
    List<ShiftSwapRequest> findByRequester(
            @Param("employeeId") Long employeeId,
            @Param("tenantId") String tenantId);

    @Query("SELECT sr FROM ShiftSwapRequest sr WHERE sr.targetEmployee.id = :employeeId AND sr.tenantId = :tenantId ORDER BY sr.createdAt DESC")
    List<ShiftSwapRequest> findByTarget(
            @Param("employeeId") Long employeeId,
            @Param("tenantId") String tenantId);

    @Query("SELECT sr FROM ShiftSwapRequest sr WHERE sr.status = :status AND sr.tenantId = :tenantId ORDER BY sr.createdAt ASC")
    List<ShiftSwapRequest> findByStatus(
            @Param("status") SwapRequestStatus status,
            @Param("tenantId") String tenantId);

    @Query("SELECT sr FROM ShiftSwapRequest sr WHERE (sr.requesterEmployee.id = :employeeId OR sr.targetEmployee.id = :employeeId) AND sr.tenantId = :tenantId ORDER BY sr.createdAt DESC")
    List<ShiftSwapRequest> findByEmployee(
            @Param("employeeId") Long employeeId,
            @Param("tenantId") String tenantId);
}
