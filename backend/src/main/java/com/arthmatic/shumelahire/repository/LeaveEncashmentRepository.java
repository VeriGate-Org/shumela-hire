package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.EncashmentStatus;
import com.arthmatic.shumelahire.entity.LeaveEncashment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveEncashmentRepository extends JpaRepository<LeaveEncashment, Long> {

    Page<LeaveEncashment> findByEmployeeId(Long employeeId, Pageable pageable);

    List<LeaveEncashment> findByEmployeeIdAndStatus(Long employeeId, EncashmentStatus status);

    Page<LeaveEncashment> findByTenantIdAndStatus(String tenantId, EncashmentStatus status, Pageable pageable);

    List<LeaveEncashment> findByLeaveBalanceId(Long leaveBalanceId);
}
