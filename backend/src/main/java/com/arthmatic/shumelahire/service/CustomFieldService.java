package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.config.tenant.TenantContext;
import com.arthmatic.shumelahire.dto.CustomFieldRequest;
import com.arthmatic.shumelahire.dto.CustomFieldResponse;
import com.arthmatic.shumelahire.dto.CustomFieldValueRequest;
import com.arthmatic.shumelahire.entity.CustomField;
import com.arthmatic.shumelahire.entity.CustomFieldEntityType;
import com.arthmatic.shumelahire.entity.CustomFieldValue;
import com.arthmatic.shumelahire.repository.CustomFieldRepository;
import com.arthmatic.shumelahire.repository.CustomFieldValueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomFieldService {

    private static final Logger logger = LoggerFactory.getLogger(CustomFieldService.class);

    @Autowired
    private CustomFieldRepository customFieldRepository;

    @Autowired
    private CustomFieldValueRepository customFieldValueRepository;

    @Autowired
    private AuditLogService auditLogService;

    // --- Custom Field Definitions ---

    public CustomFieldResponse createField(CustomFieldRequest request) {
        logger.info("Creating custom field: {} for {}", request.getFieldName(), request.getEntityType());

        String tenantId = TenantContext.requireCurrentTenant();
        if (customFieldRepository.existsByFieldNameAndEntityTypeAndTenantId(
                request.getFieldName(), request.getEntityType(), tenantId)) {
            throw new IllegalArgumentException("Custom field already exists: " + request.getFieldName());
        }

        CustomField field = new CustomField();
        mapRequestToEntity(request, field);

        CustomField saved = customFieldRepository.save(field);

        auditLogService.logSystemAction("CUSTOM_FIELD_CREATED", "CUSTOM_FIELD",
                "Custom field created: " + saved.getFieldName() + " for " + saved.getEntityType());

        return CustomFieldResponse.fromEntity(saved);
    }

    public CustomFieldResponse updateField(Long id, CustomFieldRequest request) {
        logger.info("Updating custom field: {}", id);

        CustomField field = customFieldRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Custom field not found: " + id));

        mapRequestToEntity(request, field);

        CustomField updated = customFieldRepository.save(field);

        auditLogService.logSystemAction("CUSTOM_FIELD_UPDATED", "CUSTOM_FIELD",
                "Custom field updated: " + updated.getFieldName());

        return CustomFieldResponse.fromEntity(updated);
    }

    @Transactional(readOnly = true)
    public CustomFieldResponse getField(Long id) {
        CustomField field = customFieldRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Custom field not found: " + id));
        return CustomFieldResponse.fromEntity(field);
    }

    @Transactional(readOnly = true)
    public List<CustomFieldResponse> getFieldsByEntityType(CustomFieldEntityType entityType) {
        return customFieldRepository.findByEntityTypeAndIsActiveTrueOrderByDisplayOrderAsc(entityType).stream()
                .map(CustomFieldResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CustomFieldResponse> getAllFieldsByEntityType(CustomFieldEntityType entityType) {
        return customFieldRepository.findByEntityTypeOrderByDisplayOrderAsc(entityType).stream()
                .map(CustomFieldResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public void deleteField(Long id) {
        CustomField field = customFieldRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Custom field not found: " + id));

        customFieldValueRepository.deleteByCustomFieldId(id);
        customFieldRepository.delete(field);

        auditLogService.logSystemAction("CUSTOM_FIELD_DELETED", "CUSTOM_FIELD",
                "Custom field deleted: " + field.getFieldName());

        logger.info("Custom field deleted: {}", id);
    }

    // --- Custom Field Values ---

    public void setFieldValue(CustomFieldValueRequest request) {
        CustomField field = customFieldRepository.findById(request.getCustomFieldId())
                .orElseThrow(() -> new IllegalArgumentException("Custom field not found: " + request.getCustomFieldId()));

        CustomFieldValue value = customFieldValueRepository
                .findByCustomFieldIdAndEntityTypeAndEntityId(
                        request.getCustomFieldId(), request.getEntityType(), request.getEntityId())
                .orElse(new CustomFieldValue());

        value.setCustomField(field);
        value.setEntityType(request.getEntityType());
        value.setEntityId(request.getEntityId());
        value.setFieldValue(request.getFieldValue());

        customFieldValueRepository.save(value);
    }

    public void setFieldValues(CustomFieldEntityType entityType, Long entityId, Map<Long, String> fieldValues) {
        for (Map.Entry<Long, String> entry : fieldValues.entrySet()) {
            CustomFieldValueRequest request = new CustomFieldValueRequest();
            request.setCustomFieldId(entry.getKey());
            request.setEntityType(entityType);
            request.setEntityId(entityId);
            request.setFieldValue(entry.getValue());
            setFieldValue(request);
        }
    }

    @Transactional(readOnly = true)
    public Map<String, String> getFieldValues(CustomFieldEntityType entityType, Long entityId) {
        List<CustomFieldValue> values = customFieldValueRepository.findByEntityTypeAndEntityId(entityType, entityId);
        return values.stream()
                .collect(Collectors.toMap(
                        v -> v.getCustomField().getFieldName(),
                        v -> v.getFieldValue() != null ? v.getFieldValue() : "",
                        (v1, v2) -> v1
                ));
    }

    public void deleteFieldValues(CustomFieldEntityType entityType, Long entityId) {
        customFieldValueRepository.deleteByEntityTypeAndEntityId(entityType, entityId);
    }

    private void mapRequestToEntity(CustomFieldRequest request, CustomField field) {
        field.setEntityType(request.getEntityType());
        field.setFieldName(request.getFieldName());
        field.setFieldLabel(request.getFieldLabel());
        field.setFieldType(request.getFieldType());
        if (request.getIsRequired() != null) field.setIsRequired(request.getIsRequired());
        if (request.getIsActive() != null) field.setIsActive(request.getIsActive());
        if (request.getDisplayOrder() != null) field.setDisplayOrder(request.getDisplayOrder());
        field.setOptions(request.getOptions());
        field.setDefaultValue(request.getDefaultValue());
        field.setValidationRegex(request.getValidationRegex());
        field.setHelpText(request.getHelpText());
        field.setSection(request.getSection());
    }
}
