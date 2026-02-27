package com.arthmatic.shumelahire.repository.performance;

import com.arthmatic.shumelahire.entity.performance.KeyResultArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KeyResultAreaRepository extends JpaRepository<KeyResultArea, Long> {

    List<KeyResultArea> findByContractIdAndTenantIdOrderBySortOrderAsc(Long contractId, String tenantId);

    List<KeyResultArea> findByContractIdAndTenantIdAndIsActiveOrderBySortOrderAsc(Long contractId, String tenantId, Boolean isActive);

    Optional<KeyResultArea> findByIdAndTenantId(Long id, String tenantId);

    boolean existsByNameAndContractIdAndTenantId(String name, Long contractId, String tenantId);
}
