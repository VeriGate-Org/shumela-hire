package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {

    List<LeaveType> findByTenantIdAndActiveTrue(String tenantId);

    List<LeaveType> findByTenantIdOrderBySortOrderAsc(String tenantId);

    Optional<LeaveType> findByTenantIdAndCode(String tenantId, String code);

    boolean existsByTenantIdAndCode(String tenantId, String code);
}
