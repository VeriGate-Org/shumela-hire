package com.arthmatic.shumelahire.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for Active Directory / LDAP integration.
 * Used in hybrid/on-premises deployments for AD authentication
 * and user provisioning.
 */
@Configuration
@ConfigurationProperties(prefix = "ad")
public class ActiveDirectoryProperties {

    private boolean enabled = false;

    /** AD domain name (e.g., "corp.uthukela.gov.za") */
    private String domain;

    /** LDAP URL (e.g., "ldap://dc01.corp.uthukela.gov.za:389") */
    private String url;

    /** Base DN for user search (e.g., "DC=corp,DC=uthukela,DC=gov,DC=za") */
    private String baseDn;

    /** User search filter (default: searches by sAMAccountName) */
    private String userSearchFilter = "(&(objectClass=user)(sAMAccountName={0}))";

    /** Base DN for group search (e.g., "OU=Groups,DC=corp,DC=uthukela,DC=gov,DC=za") */
    private String groupSearchBase;

    /** Group search filter */
    private String groupSearchFilter = "(&(objectClass=group)(member={0}))";

    /** Default role for provisioned users with no matching AD group */
    private String defaultRole = "EMPLOYEE";

    /** Default tenant ID for AD-provisioned users */
    private String defaultTenantId = "default";

    /**
     * AD group DN → ShumelaHire role mapping.
     * Example: "CN=HR_Managers,OU=Groups,DC=corp" → "HR_MANAGER"
     */
    private Map<String, String> groupRoleMappings = new HashMap<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBaseDn() {
        return baseDn;
    }

    public void setBaseDn(String baseDn) {
        this.baseDn = baseDn;
    }

    public String getUserSearchFilter() {
        return userSearchFilter;
    }

    public void setUserSearchFilter(String userSearchFilter) {
        this.userSearchFilter = userSearchFilter;
    }

    public String getGroupSearchBase() {
        return groupSearchBase;
    }

    public void setGroupSearchBase(String groupSearchBase) {
        this.groupSearchBase = groupSearchBase;
    }

    public String getGroupSearchFilter() {
        return groupSearchFilter;
    }

    public void setGroupSearchFilter(String groupSearchFilter) {
        this.groupSearchFilter = groupSearchFilter;
    }

    public String getDefaultRole() {
        return defaultRole;
    }

    public void setDefaultRole(String defaultRole) {
        this.defaultRole = defaultRole;
    }

    public String getDefaultTenantId() {
        return defaultTenantId;
    }

    public void setDefaultTenantId(String defaultTenantId) {
        this.defaultTenantId = defaultTenantId;
    }

    public Map<String, String> getGroupRoleMappings() {
        return groupRoleMappings;
    }

    public void setGroupRoleMappings(Map<String, String> groupRoleMappings) {
        this.groupRoleMappings = groupRoleMappings;
    }
}
