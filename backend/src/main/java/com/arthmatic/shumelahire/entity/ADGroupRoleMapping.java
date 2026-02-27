package com.arthmatic.shumelahire.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ad_group_role_mappings",
       uniqueConstraints = @UniqueConstraint(columnNames = {"ad_group_dn", "tenant_id"}))
public class ADGroupRoleMapping extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "ad_group_name", nullable = false, length = 255)
    private String adGroupName;

    @NotBlank
    @Column(name = "ad_group_dn", nullable = false, length = 500)
    private String adGroupDN;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "shumela_role", nullable = false, length = 30)
    private User.Role shumelaRole;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "priority")
    private Integer priority = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAdGroupName() { return adGroupName; }
    public void setAdGroupName(String adGroupName) { this.adGroupName = adGroupName; }

    public String getAdGroupDN() { return adGroupDN; }
    public void setAdGroupDN(String adGroupDN) { this.adGroupDN = adGroupDN; }

    public User.Role getShumelaRole() { return shumelaRole; }
    public void setShumelaRole(User.Role shumelaRole) { this.shumelaRole = shumelaRole; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
