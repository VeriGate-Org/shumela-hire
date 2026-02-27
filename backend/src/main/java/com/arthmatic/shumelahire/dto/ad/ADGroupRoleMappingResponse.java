package com.arthmatic.shumelahire.dto.ad;

import com.arthmatic.shumelahire.entity.ADGroupRoleMapping;

import java.time.LocalDateTime;

public class ADGroupRoleMappingResponse {

    private Long id;
    private String adGroupName;
    private String adGroupDN;
    private String shumelaRole;
    private Boolean isActive;
    private String description;
    private Integer priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ADGroupRoleMappingResponse from(ADGroupRoleMapping mapping) {
        ADGroupRoleMappingResponse response = new ADGroupRoleMappingResponse();
        response.setId(mapping.getId());
        response.setAdGroupName(mapping.getAdGroupName());
        response.setAdGroupDN(mapping.getAdGroupDN());
        response.setShumelaRole(mapping.getShumelaRole().name());
        response.setIsActive(mapping.getIsActive());
        response.setDescription(mapping.getDescription());
        response.setPriority(mapping.getPriority());
        response.setCreatedAt(mapping.getCreatedAt());
        response.setUpdatedAt(mapping.getUpdatedAt());
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAdGroupName() { return adGroupName; }
    public void setAdGroupName(String adGroupName) { this.adGroupName = adGroupName; }

    public String getAdGroupDN() { return adGroupDN; }
    public void setAdGroupDN(String adGroupDN) { this.adGroupDN = adGroupDN; }

    public String getShumelaRole() { return shumelaRole; }
    public void setShumelaRole(String shumelaRole) { this.shumelaRole = shumelaRole; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
