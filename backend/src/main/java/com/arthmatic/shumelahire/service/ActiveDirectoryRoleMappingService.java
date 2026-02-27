package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.config.ActiveDirectoryProperties;
import com.arthmatic.shumelahire.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

/**
 * Maps Active Directory group memberships to ShumelaHire roles.
 * Group-to-role mappings are configured via ad.group-role-mappings in application config.
 */
@Service
@ConditionalOnProperty(name = "ad.enabled", havingValue = "true")
public class ActiveDirectoryRoleMappingService {

    private static final Logger logger = LoggerFactory.getLogger(ActiveDirectoryRoleMappingService.class);

    private final ActiveDirectoryProperties adProperties;

    public ActiveDirectoryRoleMappingService(ActiveDirectoryProperties adProperties) {
        this.adProperties = adProperties;
    }

    /**
     * Resolves a ShumelaHire role from AD group memberships.
     * Returns the highest-priority matching role, or the configured default role.
     */
    public User.Role resolveRole(Collection<String> adGroupDns) {
        Map<String, String> mappings = adProperties.getGroupRoleMappings();

        if (mappings.isEmpty() || adGroupDns == null || adGroupDns.isEmpty()) {
            return parseRole(adProperties.getDefaultRole());
        }

        User.Role highestRole = null;

        for (String groupDn : adGroupDns) {
            String normalizedDn = groupDn.toLowerCase();
            for (Map.Entry<String, String> entry : mappings.entrySet()) {
                if (normalizedDn.contains(entry.getKey().toLowerCase())) {
                    User.Role mappedRole = parseRole(entry.getValue());
                    if (mappedRole != null && (highestRole == null || mappedRole.getPriority() > highestRole.getPriority())) {
                        highestRole = mappedRole;
                        logger.debug("Mapped AD group '{}' to role '{}'", groupDn, mappedRole.name());
                    }
                }
            }
        }

        if (highestRole != null) {
            return highestRole;
        }

        logger.debug("No AD group mapping found, using default role: {}", adProperties.getDefaultRole());
        return parseRole(adProperties.getDefaultRole());
    }

    private User.Role parseRole(String roleName) {
        try {
            return User.Role.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid role name '{}', falling back to EMPLOYEE", roleName);
            return User.Role.EMPLOYEE;
        }
    }
}
