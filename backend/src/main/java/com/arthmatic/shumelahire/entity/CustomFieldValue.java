package com.arthmatic.shumelahire.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "custom_field_values",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"custom_field_id", "entity_type", "entity_id"}
        ))
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CustomFieldValue extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "custom_field_id", nullable = false)
    @NotNull(message = "Custom field is required")
    private CustomField customField;

    @Column(name = "custom_field_id", insertable = false, updatable = false)
    private Long customFieldId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, length = 50)
    @NotNull(message = "Entity type is required")
    private CustomFieldEntityType entityType;

    @Column(name = "entity_id", nullable = false)
    @NotNull(message = "Entity ID is required")
    private Long entityId;

    @Column(name = "field_value", columnDefinition = "TEXT")
    private String fieldValue;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public CustomFieldValue() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CustomField getCustomField() { return customField; }
    public void setCustomField(CustomField customField) { this.customField = customField; }

    public Long getCustomFieldId() { return customFieldId; }

    public CustomFieldEntityType getEntityType() { return entityType; }
    public void setEntityType(CustomFieldEntityType entityType) { this.entityType = entityType; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public String getFieldValue() { return fieldValue; }
    public void setFieldValue(String fieldValue) { this.fieldValue = fieldValue; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "CustomFieldValue{" +
                "id=" + id +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                '}';
    }
}
