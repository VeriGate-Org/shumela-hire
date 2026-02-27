package com.arthmatic.shumelahire.service.ad;

import com.arthmatic.shumelahire.entity.ADGroupRoleMapping;
import com.arthmatic.shumelahire.entity.User;
import com.arthmatic.shumelahire.repository.ADGroupRoleMappingRepository;
import com.arthmatic.shumelahire.repository.UserRepository;
import com.arthmatic.shumelahire.config.tenant.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import java.time.LocalDateTime;
import java.util.*;

@Service
@ConditionalOnProperty(name = "shumelahire.ad.enabled", havingValue = "true")
public class ActiveDirectoryAuthService {

    private static final Logger logger = LoggerFactory.getLogger(ActiveDirectoryAuthService.class);

    private final LdapTemplate ldapTemplate;
    private final UserRepository userRepository;
    private final ADGroupRoleMappingRepository groupMappingRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${shumelahire.ad.user-search-base:}")
    private String userSearchBase;

    @Value("${shumelahire.ad.user-search-filter:(sAMAccountName={0})}")
    private String userSearchFilter;

    @Value("${shumelahire.ad.group-search-base:}")
    private String groupSearchBase;

    @Value("${shumelahire.ad.domain:}")
    private String domain;

    public ActiveDirectoryAuthService(LdapTemplate ldapTemplate,
                                       UserRepository userRepository,
                                       ADGroupRoleMappingRepository groupMappingRepository,
                                       PasswordEncoder passwordEncoder) {
        this.ldapTemplate = ldapTemplate;
        this.userRepository = userRepository;
        this.groupMappingRepository = groupMappingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticate user against Active Directory via LDAP bind.
     * Returns the User entity (creating or updating if necessary).
     */
    @Transactional
    public User authenticate(String username, String password) {
        logger.info("Attempting AD authentication for user: {}", username);

        String bindDn = buildBindDn(username);

        try {
            ldapTemplate.getContextSource().getContext(bindDn, password);
        } catch (Exception e) {
            logger.warn("AD authentication failed for user: {}", username);
            throw new RuntimeException("Active Directory authentication failed", e);
        }

        logger.info("AD authentication successful for user: {}", username);
        return provisionOrUpdateUser(username);
    }

    /**
     * Provision a new user from AD or update an existing AD-sourced user.
     */
    @Transactional
    public User provisionOrUpdateUser(String username) {
        Map<String, String> adAttributes = fetchUserAttributes(username);
        if (adAttributes.isEmpty()) {
            throw new RuntimeException("User not found in Active Directory: " + username);
        }

        String objectGuid = adAttributes.getOrDefault("objectGUID", "");
        String email = adAttributes.getOrDefault("mail", username + "@" + domain);
        String firstName = adAttributes.getOrDefault("givenName", "");
        String lastName = adAttributes.getOrDefault("sn", "");
        String department = adAttributes.getOrDefault("department", "");
        String title = adAttributes.getOrDefault("title", "");
        String phone = adAttributes.getOrDefault("telephoneNumber", "");
        String dn = adAttributes.getOrDefault("distinguishedName", "");
        boolean accountEnabled = !"TRUE".equalsIgnoreCase(
                adAttributes.getOrDefault("userAccountControl_disabled", "FALSE"));

        Optional<User> existingUser = userRepository.findByAdObjectGuid(objectGuid);
        User user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setDepartment(department);
            user.setJobTitle(title);
            user.setPhone(phone);
            user.setAdDistinguishedName(dn);
            user.setAdSyncedAt(LocalDateTime.now());
            user.setEnabled(accountEnabled);
        } else {
            user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setDepartment(department);
            user.setJobTitle(title);
            user.setPhone(phone);
            user.setAdObjectGuid(objectGuid);
            user.setAdDistinguishedName(dn);
            user.setAdSource(true);
            user.setAdSyncedAt(LocalDateTime.now());
            user.setSsoProvider("ACTIVE_DIRECTORY");
            user.setEnabled(accountEnabled);
            user.setEmailVerified(true);
            user.setTenantId(TenantContext.requireCurrentTenant());
        }

        // Resolve role from AD group membership
        User.Role resolvedRole = resolveRoleFromAdGroups(username);
        user.setRole(resolvedRole);

        user.setLastLogin(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Resolve the highest-priority ShumelaHire role from AD group memberships.
     */
    public User.Role resolveRoleFromAdGroups(String username) {
        List<String> memberOfDNs = fetchUserGroupMemberships(username);
        List<ADGroupRoleMapping> activeMappings = groupMappingRepository.findByIsActiveTrue();

        User.Role highestRole = User.Role.EMPLOYEE; // default role
        int highestPriority = highestRole.getPriority();

        for (ADGroupRoleMapping mapping : activeMappings) {
            if (memberOfDNs.contains(mapping.getAdGroupDN())) {
                if (mapping.getShumelaRole().getPriority() > highestPriority) {
                    highestRole = mapping.getShumelaRole();
                    highestPriority = mapping.getShumelaRole().getPriority();
                }
            }
        }

        return highestRole;
    }

    /**
     * Fetch user attributes from AD via LDAP search.
     */
    public Map<String, String> fetchUserAttributes(String username) {
        try {
            List<Map<String, String>> results = ldapTemplate.search(
                    LdapQueryBuilder.query()
                            .base(userSearchBase)
                            .where("sAMAccountName").is(username),
                    (AttributesMapper<Map<String, String>>) attrs -> {
                        Map<String, String> map = new HashMap<>();
                        putIfPresent(map, attrs, "objectGUID");
                        putIfPresent(map, attrs, "mail");
                        putIfPresent(map, attrs, "givenName");
                        putIfPresent(map, attrs, "sn");
                        putIfPresent(map, attrs, "department");
                        putIfPresent(map, attrs, "title");
                        putIfPresent(map, attrs, "telephoneNumber");
                        putIfPresent(map, attrs, "distinguishedName");
                        putIfPresent(map, attrs, "sAMAccountName");

                        // Check if account is disabled (bit 2 of userAccountControl)
                        if (attrs.get("userAccountControl") != null) {
                            int uac = Integer.parseInt(attrs.get("userAccountControl").get().toString());
                            map.put("userAccountControl_disabled",
                                    (uac & 0x0002) != 0 ? "TRUE" : "FALSE");
                        }
                        return map;
                    });
            return results.isEmpty() ? Collections.emptyMap() : results.get(0);
        } catch (Exception e) {
            logger.error("Failed to fetch AD attributes for user: {}", username, e);
            return Collections.emptyMap();
        }
    }

    /**
     * Fetch the list of group DNs a user is a member of.
     */
    public List<String> fetchUserGroupMemberships(String username) {
        try {
            List<List<String>> results = ldapTemplate.search(
                    LdapQueryBuilder.query()
                            .base(userSearchBase)
                            .where("sAMAccountName").is(username),
                    (AttributesMapper<List<String>>) attrs -> {
                        List<String> groups = new ArrayList<>();
                        if (attrs.get("memberOf") != null) {
                            NamingEnumeration<?> members = attrs.get("memberOf").getAll();
                            while (members.hasMore()) {
                                groups.add(members.next().toString());
                            }
                        }
                        return groups;
                    });
            return results.isEmpty() ? Collections.emptyList() : results.get(0);
        } catch (Exception e) {
            logger.error("Failed to fetch group memberships for user: {}", username, e);
            return Collections.emptyList();
        }
    }

    /**
     * List AD groups available in the configured search base.
     */
    public List<Map<String, String>> listAdGroups() {
        try {
            return ldapTemplate.search(
                    LdapQueryBuilder.query()
                            .base(groupSearchBase)
                            .where("objectClass").is("group"),
                    (AttributesMapper<Map<String, String>>) attrs -> {
                        Map<String, String> group = new HashMap<>();
                        putIfPresent(group, attrs, "cn");
                        putIfPresent(group, attrs, "distinguishedName");
                        putIfPresent(group, attrs, "description");
                        return group;
                    });
        } catch (Exception e) {
            logger.error("Failed to list AD groups", e);
            return Collections.emptyList();
        }
    }

    private String buildBindDn(String username) {
        if (domain != null && !domain.isEmpty()) {
            return username + "@" + domain;
        }
        return username;
    }

    private void putIfPresent(Map<String, String> map, Attributes attrs, String key) {
        try {
            if (attrs.get(key) != null) {
                map.put(key, attrs.get(key).get().toString());
            }
        } catch (NamingException e) {
            // Attribute not available, skip
        }
    }
}
