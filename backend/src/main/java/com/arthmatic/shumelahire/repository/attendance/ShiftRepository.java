package com.arthmatic.shumelahire.repository.attendance;

import com.arthmatic.shumelahire.entity.attendance.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {

    List<Shift> findByTenantId(String tenantId);

    Optional<Shift> findByCodeAndTenantId(String code, String tenantId);

    boolean existsByCodeAndTenantId(String code, String tenantId);

    @Query("SELECT s FROM Shift s WHERE s.tenantId = :tenantId AND s.isActive = true ORDER BY s.name ASC")
    List<Shift> findActiveByTenantId(@Param("tenantId") String tenantId);
}
