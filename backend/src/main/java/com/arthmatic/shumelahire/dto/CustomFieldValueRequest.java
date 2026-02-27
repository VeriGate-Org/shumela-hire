package com.arthmatic.shumelahire.dto;

import com.arthmatic.shumelahire.entity.CustomFieldEntityType;
import jakarta.validation.constraints.NotNull;

public class CustomFieldValueRequest {

    @NotNull(message = "Custom field ID is required")
    private Long customFieldId;

    @NotNull(message = "Entity type is required")
    private CustomFieldEntityType entityType;

    @NotNull(message = "Entity ID is required")
    private Long entityId;

    private String fieldValue;

    public CustomFieldValueRequest() {}

    // Getters and Setters
    public Long getCustomFieldId() { return customFieldId; }
    public void setCustomFieldId(Long customFieldId) { this.customFieldId = customFieldId; }

    public CustomFieldEntityType getEntityType() { return entityType; }
    public void setEntityType(CustomFieldEntityType entityType) { this.entityType = entityType; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public String getFieldValue() { return fieldValue; }
    public void setFieldValue(String fieldValue) { this.fieldValue = fieldValue; }
}
