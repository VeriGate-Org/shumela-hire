package com.arthmatic.shumelahire.dto;

import com.arthmatic.shumelahire.entity.CustomField;
import com.arthmatic.shumelahire.entity.CustomFieldEntityType;
import com.arthmatic.shumelahire.entity.CustomFieldType;

import java.time.LocalDateTime;

public class CustomFieldResponse {

    private Long id;
    private CustomFieldEntityType entityType;
    private String fieldName;
    private String fieldLabel;
    private CustomFieldType fieldType;
    private Boolean isRequired;
    private Boolean isActive;
    private Integer displayOrder;
    private String options;
    private String defaultValue;
    private String validationRegex;
    private String helpText;
    private String section;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CustomFieldResponse() {}

    public CustomFieldResponse(CustomField field) {
        this.id = field.getId();
        this.entityType = field.getEntityType();
        this.fieldName = field.getFieldName();
        this.fieldLabel = field.getFieldLabel();
        this.fieldType = field.getFieldType();
        this.isRequired = field.getIsRequired();
        this.isActive = field.getIsActive();
        this.displayOrder = field.getDisplayOrder();
        this.options = field.getOptions();
        this.defaultValue = field.getDefaultValue();
        this.validationRegex = field.getValidationRegex();
        this.helpText = field.getHelpText();
        this.section = field.getSection();
        this.createdAt = field.getCreatedAt();
        this.updatedAt = field.getUpdatedAt();
    }

    public static CustomFieldResponse fromEntity(CustomField field) {
        return new CustomFieldResponse(field);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CustomFieldEntityType getEntityType() { return entityType; }
    public void setEntityType(CustomFieldEntityType entityType) { this.entityType = entityType; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getFieldLabel() { return fieldLabel; }
    public void setFieldLabel(String fieldLabel) { this.fieldLabel = fieldLabel; }

    public CustomFieldType getFieldType() { return fieldType; }
    public void setFieldType(CustomFieldType fieldType) { this.fieldType = fieldType; }

    public Boolean getIsRequired() { return isRequired; }
    public void setIsRequired(Boolean isRequired) { this.isRequired = isRequired; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }

    public String getDefaultValue() { return defaultValue; }
    public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }

    public String getValidationRegex() { return validationRegex; }
    public void setValidationRegex(String validationRegex) { this.validationRegex = validationRegex; }

    public String getHelpText() { return helpText; }
    public void setHelpText(String helpText) { this.helpText = helpText; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
