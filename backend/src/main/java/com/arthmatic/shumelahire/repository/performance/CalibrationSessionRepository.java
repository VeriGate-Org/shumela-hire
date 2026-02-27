package com.arthmatic.shumelahire.repository.performance;

import com.arthmatic.shumelahire.entity.performance.CalibrationSession;
import com.arthmatic.shumelahire.entity.performance.CalibrationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalibrationSessionRepository extends JpaRepository<CalibrationSession, Long> {

    Page<CalibrationSession> findByTenantIdOrderByScheduledDateDesc(String tenantId, Pageable pageable);

    Optional<CalibrationSession> findByIdAndTenantId(Long id, String tenantId);

    List<CalibrationSession> findByCycleIdAndTenantId(Long cycleId, String tenantId);

    List<CalibrationSession> findByTenantIdAndStatus(String tenantId, CalibrationStatus status);

    List<CalibrationSession> findByCycleIdAndDepartmentAndTenantId(Long cycleId, String department, String tenantId);

    long countByTenantIdAndStatus(String tenantId, CalibrationStatus status);
}
