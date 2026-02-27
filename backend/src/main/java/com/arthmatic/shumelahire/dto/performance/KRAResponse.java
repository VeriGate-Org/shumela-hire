package com.arthmatic.shumelahire.dto.performance;

import com.arthmatic.shumelahire.entity.performance.KeyResultArea;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class KRAResponse {

    private Long id;
    private Long contractId;
    private String name;
    private String description;
    private BigDecimal weighting;
    private Integer sortOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private int goalCount;

    public static KRAResponse fromEntity(KeyResultArea kra) {
        KRAResponse response = new KRAResponse();
        response.setId(kra.getId());
        response.setContractId(kra.getContract() != null ? kra.getContract().getId() : null);
        response.setName(kra.getName());
        response.setDescription(kra.getDescription());
        response.setWeighting(kra.getWeighting());
        response.setSortOrder(kra.getSortOrder());
        response.setIsActive(kra.getIsActive());
        response.setCreatedAt(kra.getCreatedAt());
        response.setUpdatedAt(kra.getUpdatedAt());
        response.setCreatedBy(kra.getCreatedBy());
        response.setGoalCount(kra.getGoals() != null ? kra.getGoals().size() : 0);
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getWeighting() { return weighting; }
    public void setWeighting(BigDecimal weighting) { this.weighting = weighting; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public int getGoalCount() { return goalCount; }
    public void setGoalCount(int goalCount) { this.goalCount = goalCount; }
}
