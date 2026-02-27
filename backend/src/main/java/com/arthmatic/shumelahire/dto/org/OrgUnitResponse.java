package com.arthmatic.shumelahire.dto.org;

import com.arthmatic.shumelahire.entity.OrgUnit;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrgUnitResponse {

    private Long id;
    private String name;
    private String code;
    private String unitType;
    private Long parentId;
    private String parentName;
    private Long managerId;
    private String managerName;
    private String costCentre;
    private String description;
    private Boolean isActive;
    private List<OrgUnitResponse> children = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static OrgUnitResponse fromEntity(OrgUnit unit) {
        OrgUnitResponse response = new OrgUnitResponse();
        response.setId(unit.getId());
        response.setName(unit.getName());
        response.setCode(unit.getCode());
        response.setUnitType(unit.getUnitType());
        response.setCostCentre(unit.getCostCentre());
        response.setDescription(unit.getDescription());
        response.setIsActive(unit.getIsActive());
        response.setCreatedAt(unit.getCreatedAt());
        response.setUpdatedAt(unit.getUpdatedAt());

        if (unit.getParent() != null) {
            response.setParentId(unit.getParent().getId());
            response.setParentName(unit.getParent().getName());
        }

        if (unit.getManager() != null) {
            response.setManagerId(unit.getManager().getId());
            response.setManagerName(unit.getManager().getFullName());
        }

        return response;
    }

    public static OrgUnitResponse fromEntityWithChildren(OrgUnit unit) {
        OrgUnitResponse response = fromEntity(unit);
        if (unit.getChildren() != null) {
            response.setChildren(unit.getChildren().stream()
                    .map(OrgUnitResponse::fromEntityWithChildren)
                    .collect(Collectors.toList()));
        }
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getUnitType() { return unitType; }
    public void setUnitType(String unitType) { this.unitType = unitType; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }

    public Long getManagerId() { return managerId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public String getCostCentre() { return costCentre; }
    public void setCostCentre(String costCentre) { this.costCentre = costCentre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public List<OrgUnitResponse> getChildren() { return children; }
    public void setChildren(List<OrgUnitResponse> children) { this.children = children; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
