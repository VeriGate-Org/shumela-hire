package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.CustomField;
import com.arthmatic.shumelahire.entity.CustomFieldEntityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomFieldRepository extends JpaRepository<CustomField, Long> {

    List<CustomField> findByEntityTypeAndIsActiveTrueOrderByDisplayOrderAsc(CustomFieldEntityType entityType);

    List<CustomField> findByEntityTypeOrderByDisplayOrderAsc(CustomFieldEntityType entityType);

    Optional<CustomField> findByFieldNameAndEntityTypeAndTenantId(
            String fieldName, CustomFieldEntityType entityType, String tenantId);

    boolean existsByFieldNameAndEntityTypeAndTenantId(
            String fieldName, CustomFieldEntityType entityType, String tenantId);

    List<CustomField> findBySection(String section);
}
