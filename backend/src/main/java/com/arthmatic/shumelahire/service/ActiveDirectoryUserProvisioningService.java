package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.config.ActiveDirectoryProperties;
import com.arthmatic.shumelahire.entity.User;
import com.arthmatic.shumelahire.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Auto-provisions and syncs users from Active Directory into the local database.
 * On first AD login, creates a local User record with SSO tracking.
 * On subsequent logins, syncs profile attributes from AD.
 */
@Service
@Transactional
@ConditionalOnProperty(name = "ad.enabled", havingValue = "true")
public class ActiveDirectoryUserProvisioningService {

    private static final Logger logger = LoggerFactory.getLogger(ActiveDirectoryUserProvisioningService.class);
    private static final String SSO_PROVIDER = "AZURE_AD";

    private final UserRepository userRepository;
    private final ActiveDirectoryRoleMappingService roleMappingService;
    private final ActiveDirectoryProperties adProperties;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public ActiveDirectoryUserProvisioningService(
            UserRepository userRepository,
            ActiveDirectoryRoleMappingService roleMappingService,
            ActiveDirectoryProperties adProperties,
            PasswordEncoder passwordEncoder,
            AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.roleMappingService = roleMappingService;
        this.adProperties = adProperties;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    /**
     * Provision or sync a user from AD context after successful authentication.
     * Returns the local User entity (created or updated).
     */
    public User provisionOrSyncUser(DirContextOperations adContext, String username,
                                     java.util.Collection<String> adGroupDns) {
        String ssoUserId = extractAttribute(adContext, "objectGUID", username);
        String tenantId = adProperties.getDefaultTenantId();

        // Try to find existing user by SSO ID first, then by username
        Optional<User> existingUser = userRepository
                .findBySsoProviderAndSsoUserIdAndTenantId(SSO_PROVIDER, ssoUserId, tenantId);

        if (existingUser.isEmpty()) {
            existingUser = userRepository.findByEmailAndTenantId(
                    extractAttribute(adContext, "mail", username + "@" + adProperties.getDomain()),
                    tenantId);
        }

        if (existingUser.isPresent()) {
            return syncExistingUser(existingUser.get(), adContext, adGroupDns);
        } else {
            return createNewUser(adContext, username, ssoUserId, adGroupDns);
        }
    }

    private User createNewUser(DirContextOperations adContext, String username,
                                String ssoUserId, java.util.Collection<String> adGroupDns) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(extractAttribute(adContext, "mail", username + "@" + adProperties.getDomain()));
        user.setFirstName(extractAttribute(adContext, "givenName", ""));
        user.setLastName(extractAttribute(adContext, "sn", ""));
        user.setDepartment(extractAttribute(adContext, "department", null));
        user.setJobTitle(extractAttribute(adContext, "title", null));
        user.setPhone(extractAttribute(adContext, "telephoneNumber", null));
        user.setLocation(extractAttribute(adContext, "physicalDeliveryOfficeName", null));

        // Set a random non-usable password (AD handles auth)
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

        user.setSsoProvider(SSO_PROVIDER);
        user.setSsoUserId(ssoUserId);
        user.setTenantId(adProperties.getDefaultTenantId());
        user.setEnabled(true);
        user.setEmailVerified(true); // AD users are pre-verified
        user.setRole(roleMappingService.resolveRole(adGroupDns));
        user.setLastLogin(LocalDateTime.now());

        User saved = userRepository.save(user);

        auditLogService.logApplicantAction(
                saved.getId(), "AD_USER_PROVISIONED", "USER",
                "Auto-provisioned from Active Directory: " + username);

        logger.info("Provisioned new AD user: {} with role {}", username, saved.getRole().name());
        return saved;
    }

    private User syncExistingUser(User user, DirContextOperations adContext,
                                   java.util.Collection<String> adGroupDns) {
        // Sync profile attributes from AD
        String firstName = extractAttribute(adContext, "givenName", null);
        if (firstName != null) user.setFirstName(firstName);

        String lastName = extractAttribute(adContext, "sn", null);
        if (lastName != null) user.setLastName(lastName);

        String department = extractAttribute(adContext, "department", null);
        if (department != null) user.setDepartment(department);

        String title = extractAttribute(adContext, "title", null);
        if (title != null) user.setJobTitle(title);

        String phone = extractAttribute(adContext, "telephoneNumber", null);
        if (phone != null) user.setPhone(phone);

        // Ensure SSO fields are set
        if (user.getSsoProvider() == null) {
            user.setSsoProvider(SSO_PROVIDER);
        }
        if (user.getSsoUserId() == null) {
            user.setSsoUserId(extractAttribute(adContext, "objectGUID",
                    user.getUsername()));
        }

        // Update role from current AD group memberships
        User.Role resolvedRole = roleMappingService.resolveRole(adGroupDns);
        if (user.getRole() != resolvedRole) {
            logger.info("Updated role for AD user {} from {} to {}",
                    user.getUsername(), user.getRole(), resolvedRole);
            user.setRole(resolvedRole);
        }

        user.setLastLogin(LocalDateTime.now());
        user.resetFailedLoginAttempts();

        return userRepository.save(user);
    }

    private String extractAttribute(DirContextOperations ctx, String attributeName, String defaultValue) {
        try {
            String value = ctx.getStringAttribute(attributeName);
            return value != null ? value : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
