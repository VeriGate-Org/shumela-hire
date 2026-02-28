package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.TenantFeatureEntitlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantFeatureEntitlementRepository extends JpaRepository<TenantFeatureEntitlement, Long> {

    Optional<TenantFeatureEntitlement> findByTenantIdAndFeatureId(String tenantId, Long featureId);

    List<TenantFeatureEntitlement> findByTenantId(String tenantId);

    List<TenantFeatureEntitlement> findByFeatureId(Long featureId);

    void deleteByTenantIdAndFeatureId(String tenantId, Long featureId);
}
