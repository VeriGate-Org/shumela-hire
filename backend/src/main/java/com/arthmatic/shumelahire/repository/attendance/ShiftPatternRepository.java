package com.arthmatic.shumelahire.repository.attendance;

import com.arthmatic.shumelahire.entity.attendance.ShiftPattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftPatternRepository extends JpaRepository<ShiftPattern, Long> {

    List<ShiftPattern> findByTenantId(String tenantId);

    @Query("SELECT sp FROM ShiftPattern sp WHERE sp.tenantId = :tenantId AND sp.isActive = true ORDER BY sp.name ASC")
    List<ShiftPattern> findActiveByTenantId(@Param("tenantId") String tenantId);
}
