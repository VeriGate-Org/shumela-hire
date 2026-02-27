package com.arthmatic.shumelahire.dto.ad;

import com.arthmatic.shumelahire.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ADGroupRoleMappingRequest {

    @NotBlank(message = "AD group name is required")
    private String adGroupName;

    @NotBlank(message = "AD group DN is required")
    private String adGroupDN;

    @NotNull(message = "ShumelaHire role is required")
    private User.Role shumelaRole;

    private String description;

    private Integer priority = 0;

    public String getAdGroupName() { return adGroupName; }
    public void setAdGroupName(String adGroupName) { this.adGroupName = adGroupName; }

    public String getAdGroupDN() { return adGroupDN; }
    public void setAdGroupDN(String adGroupDN) { this.adGroupDN = adGroupDN; }

    public User.Role getShumelaRole() { return shumelaRole; }
    public void setShumelaRole(User.Role shumelaRole) { this.shumelaRole = shumelaRole; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
}
