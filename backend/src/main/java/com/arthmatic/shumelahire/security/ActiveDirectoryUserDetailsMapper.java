package com.arthmatic.shumelahire.security;

import com.arthmatic.shumelahire.entity.User;
import com.arthmatic.shumelahire.service.ActiveDirectoryUserProvisioningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Maps AD authentication context to a local ShumelaHire User entity.
 * Auto-provisions users on first login and syncs on subsequent logins.
 */
public class ActiveDirectoryUserDetailsMapper implements UserDetailsContextMapper {

    private static final Logger logger = LoggerFactory.getLogger(ActiveDirectoryUserDetailsMapper.class);

    private final ActiveDirectoryUserProvisioningService provisioningService;

    public ActiveDirectoryUserDetailsMapper(ActiveDirectoryUserProvisioningService provisioningService) {
        this.provisioningService = provisioningService;
    }

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
                                          Collection<? extends GrantedAuthority> authorities) {
        logger.debug("Mapping AD user: {} with {} authorities", username, authorities.size());

        // Extract AD group DNs from the authorities
        Collection<String> adGroupDns = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Provision or sync user in local database
        User user = provisioningService.provisionOrSyncUser(ctx, username, adGroupDns);

        logger.info("AD user mapped: {} -> role={}", username, user.getRole().name());
        return user;
    }

    @Override
    public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
        // Not needed — we don't write back to AD
        throw new UnsupportedOperationException("Writing to Active Directory is not supported");
    }
}
