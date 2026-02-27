package com.arthmatic.shumelahire.repository.performance;

import com.arthmatic.shumelahire.entity.performance.PerformanceImprovementPlan;
import com.arthmatic.shumelahire.entity.performance.PIPStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerformanceImprovementPlanRepository extends JpaRepository<PerformanceImprovementPlan, Long> {

    Page<PerformanceImprovementPlan> findByTenantIdOrderByCreatedAtDesc(String tenantId, Pageable pageable);

    Optional<PerformanceImprovementPlan> findByIdAndTenantId(Long id, String tenantId);

    List<PerformanceImprovementPlan> findByEmployeeIdAndTenantId(String employeeId, String tenantId);

    List<PerformanceImprovementPlan> findByManagerIdAndTenantId(String managerId, String tenantId);

    List<PerformanceImprovementPlan> findByTenantIdAndStatus(String tenantId, PIPStatus status);

    @Query("SELECT p FROM PerformanceImprovementPlan p WHERE p.tenantId = :tenantId AND p.status IN ('ACTIVE', 'EXTENDED')")
    List<PerformanceImprovementPlan> findActivePIPs(@Param("tenantId") String tenantId);

    @Query("SELECT p FROM PerformanceImprovementPlan p WHERE p.managerId = :managerId AND p.tenantId = :tenantId AND p.status IN ('ACTIVE', 'EXTENDED')")
    List<PerformanceImprovementPlan> findActivePIPsByManager(@Param("managerId") String managerId, @Param("tenantId") String tenantId);

    long countByTenantIdAndStatus(String tenantId, PIPStatus status);
}
