package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.ShiftSwapRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftSwapRequestRepository extends JpaRepository<ShiftSwapRequest, Long> {

    Page<ShiftSwapRequest> findByRequesterId(Long requesterId, Pageable pageable);

    Page<ShiftSwapRequest> findByTargetEmployeeId(Long targetEmployeeId, Pageable pageable);

    @Query("SELECT s FROM ShiftSwapRequest s WHERE s.targetEmployee.id = :employeeId " +
           "AND s.status = 'PENDING_TARGET' ORDER BY s.createdAt DESC")
    List<ShiftSwapRequest> findPendingForTarget(@Param("employeeId") Long employeeId);

    @Query("SELECT s FROM ShiftSwapRequest s WHERE s.status = 'PENDING_MANAGER' " +
           "ORDER BY s.createdAt DESC")
    Page<ShiftSwapRequest> findPendingManagerApproval(Pageable pageable);
}
