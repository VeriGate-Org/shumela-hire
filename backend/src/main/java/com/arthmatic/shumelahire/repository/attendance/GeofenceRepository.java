package com.arthmatic.shumelahire.repository.attendance;

import com.arthmatic.shumelahire.entity.attendance.Geofence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeofenceRepository extends JpaRepository<Geofence, Long> {

    List<Geofence> findByTenantId(String tenantId);

    List<Geofence> findByTenantIdAndIsActive(String tenantId, Boolean isActive);

    @Query("SELECT g FROM Geofence g WHERE g.tenantId = :tenantId AND g.isActive = true")
    List<Geofence> findActiveByTenantId(@Param("tenantId") String tenantId);

    @Query("SELECT g FROM Geofence g WHERE g.tenantId = :tenantId AND g.site = :site AND g.isActive = true")
    List<Geofence> findActiveBySite(@Param("tenantId") String tenantId, @Param("site") String site);
}
