package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.ShiftSwapRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftSwapRequestRepository extends JpaRepository<ShiftSwapRequest, Long> {

    List<ShiftSwapRequest> findByRequesterEmployeeId(Long employeeId);

    List<ShiftSwapRequest> findByTargetEmployeeId(Long employeeId);

    List<ShiftSwapRequest> findByStatus(ShiftSwapRequest.SwapStatus status);
}
