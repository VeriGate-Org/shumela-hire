package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.CustomFieldEntityType;
import com.arthmatic.shumelahire.entity.CustomFieldValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomFieldValueRepository extends JpaRepository<CustomFieldValue, Long> {

    List<CustomFieldValue> findByEntityTypeAndEntityId(CustomFieldEntityType entityType, Long entityId);

    Optional<CustomFieldValue> findByCustomFieldIdAndEntityTypeAndEntityId(
            Long customFieldId, CustomFieldEntityType entityType, Long entityId);

    void deleteByEntityTypeAndEntityId(CustomFieldEntityType entityType, Long entityId);

    void deleteByCustomFieldId(Long customFieldId);

    long countByCustomFieldId(Long customFieldId);
}
