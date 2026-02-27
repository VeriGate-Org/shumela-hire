package com.arthmatic.shumelahire.service.ad;

import com.arthmatic.shumelahire.entity.ADSyncLog;
import com.arthmatic.shumelahire.entity.User;
import com.arthmatic.shumelahire.repository.ADSyncLogRepository;
import com.arthmatic.shumelahire.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@ConditionalOnProperty(name = "shumelahire.ad.enabled", havingValue = "true")
public class ActiveDirectorySyncService {

    private static final Logger logger = LoggerFactory.getLogger(ActiveDirectorySyncService.class);

    private final LdapTemplate ldapTemplate;
    private final UserRepository userRepository;
    private final ADSyncLogRepository syncLogRepository;
    private final ActiveDirectoryAuthService authService;

    @Value("${shumelahire.ad.user-search-base:}")
    private String userSearchBase;

    @Value("${shumelahire.ad.sync.enabled:true}")
    private boolean syncEnabled;

    public ActiveDirectorySyncService(LdapTemplate ldapTemplate,
                                       UserRepository userRepository,
                                       ADSyncLogRepository syncLogRepository,
                                       ActiveDirectoryAuthService authService) {
        this.ldapTemplate = ldapTemplate;
        this.userRepository = userRepository;
        this.syncLogRepository = syncLogRepository;
        this.authService = authService;
    }

    /**
     * Scheduled delta sync — runs every hour by default.
     * Only syncs users whose AD attributes have changed.
     */
    @Scheduled(cron = "${shumelahire.ad.sync.cron:0 0 * * * *}")
    public void scheduledDeltaSync() {
        if (!syncEnabled) {
            logger.debug("AD sync is disabled, skipping scheduled delta sync");
            return;
        }
        logger.info("Starting scheduled AD delta sync");
        triggerSync(ADSyncLog.SyncType.DELTA, "SCHEDULER");
    }

    /**
     * Trigger a manual sync (FULL or DELTA).
     */
    @Transactional
    public ADSyncLog triggerSync(ADSyncLog.SyncType syncType, String triggeredBy) {
        // Check if a sync is already in progress
        Optional<ADSyncLog> activeSyncOpt = syncLogRepository.findActiveSyncInProgress();
        if (activeSyncOpt.isPresent()) {
            logger.warn("A sync is already in progress (ID: {}), skipping", activeSyncOpt.get().getId());
            return activeSyncOpt.get();
        }

        ADSyncLog syncLog = new ADSyncLog();
        syncLog.setSyncType(syncType);
        syncLog.setStatus(ADSyncLog.SyncStatus.IN_PROGRESS);
        syncLog.setTriggeredBy(triggeredBy);
        syncLog.setStartedAt(LocalDateTime.now());
        syncLog = syncLogRepository.save(syncLog);

        try {
            performSync(syncLog);
            syncLog.setStatus(ADSyncLog.SyncStatus.COMPLETED);
        } catch (Exception e) {
            logger.error("AD sync failed", e);
            syncLog.setStatus(ADSyncLog.SyncStatus.FAILED);
            syncLog.setErrors(e.getMessage());
        } finally {
            syncLog.setCompletedAt(LocalDateTime.now());
            syncLog.setDurationMs(Duration.between(syncLog.getStartedAt(), syncLog.getCompletedAt()).toMillis());
            syncLogRepository.save(syncLog);
        }

        logger.info("AD sync completed: type={}, created={}, updated={}, disabled={}",
                syncLog.getSyncType(), syncLog.getUsersCreated(),
                syncLog.getUsersUpdated(), syncLog.getUsersDisabled());

        return syncLog;
    }

    private void performSync(ADSyncLog syncLog) {
        // Fetch all users from AD
        List<Map<String, String>> adUsers = fetchAllAdUsers();
        syncLog.setTotalAdUsersProcessed(adUsers.size());

        Set<String> processedGuids = new HashSet<>();
        StringBuilder errors = new StringBuilder();

        for (Map<String, String> adUser : adUsers) {
            try {
                String objectGuid = adUser.get("objectGUID");
                if (objectGuid == null || objectGuid.isEmpty()) {
                    syncLog.setUsersSkipped(syncLog.getUsersSkipped() + 1);
                    continue;
                }
                processedGuids.add(objectGuid);

                String username = adUser.getOrDefault("sAMAccountName", "");
                boolean accountDisabled = "TRUE".equalsIgnoreCase(
                        adUser.getOrDefault("userAccountControl_disabled", "FALSE"));

                Optional<User> existingUser = userRepository.findByAdObjectGuid(objectGuid);

                if (existingUser.isPresent()) {
                    User user = existingUser.get();
                    boolean updated = updateUserFromAdAttributes(user, adUser);
                    if (accountDisabled && user.isEnabled()) {
                        user.setEnabled(false);
                        syncLog.setUsersDisabled(syncLog.getUsersDisabled() + 1);
                        updated = true;
                    }
                    if (updated) {
                        user.setAdSyncedAt(LocalDateTime.now());
                        // Re-resolve role from AD groups
                        if (!username.isEmpty()) {
                            User.Role role = authService.resolveRoleFromAdGroups(username);
                            user.setRole(role);
                        }
                        userRepository.save(user);
                        syncLog.setUsersUpdated(syncLog.getUsersUpdated() + 1);
                    } else {
                        syncLog.setUsersSkipped(syncLog.getUsersSkipped() + 1);
                    }
                } else if (!accountDisabled && !username.isEmpty()) {
                    // Create new user from AD
                    User user = authService.provisionOrUpdateUser(username);
                    syncLog.setUsersCreated(syncLog.getUsersCreated() + 1);
                }
            } catch (Exception e) {
                logger.warn("Error processing AD user: {}", adUser.get("sAMAccountName"), e);
                errors.append("Error for ").append(adUser.get("sAMAccountName"))
                        .append(": ").append(e.getMessage()).append("\n");
                syncLog.setUsersSkipped(syncLog.getUsersSkipped() + 1);
            }
        }

        // Disable users no longer in AD (full sync only)
        if (syncLog.getSyncType() == ADSyncLog.SyncType.FULL) {
            List<User> adSourcedUsers = userRepository.findByAdSourceTrue();
            for (User user : adSourcedUsers) {
                if (user.getAdObjectGuid() != null
                        && !processedGuids.contains(user.getAdObjectGuid())
                        && user.isEnabled()) {
                    user.setEnabled(false);
                    user.setAdSyncedAt(LocalDateTime.now());
                    userRepository.save(user);
                    syncLog.setUsersDisabled(syncLog.getUsersDisabled() + 1);
                }
            }
        }

        if (!errors.isEmpty()) {
            syncLog.setErrors(errors.toString());
            if (syncLog.getStatus() == ADSyncLog.SyncStatus.IN_PROGRESS) {
                syncLog.setStatus(ADSyncLog.SyncStatus.PARTIAL);
            }
        }
    }

    private List<Map<String, String>> fetchAllAdUsers() {
        try {
            return ldapTemplate.search(
                    LdapQueryBuilder.query()
                            .base(userSearchBase)
                            .where("objectClass").is("person")
                            .and("objectClass").is("user"),
                    (AttributesMapper<Map<String, String>>) attrs -> {
                        Map<String, String> map = new HashMap<>();
                        putIfPresent(map, attrs, "objectGUID");
                        putIfPresent(map, attrs, "sAMAccountName");
                        putIfPresent(map, attrs, "mail");
                        putIfPresent(map, attrs, "givenName");
                        putIfPresent(map, attrs, "sn");
                        putIfPresent(map, attrs, "department");
                        putIfPresent(map, attrs, "title");
                        putIfPresent(map, attrs, "telephoneNumber");
                        putIfPresent(map, attrs, "distinguishedName");

                        if (attrs.get("userAccountControl") != null) {
                            int uac = Integer.parseInt(attrs.get("userAccountControl").get().toString());
                            map.put("userAccountControl_disabled",
                                    (uac & 0x0002) != 0 ? "TRUE" : "FALSE");
                        }
                        return map;
                    });
        } catch (Exception e) {
            logger.error("Failed to fetch AD users", e);
            throw new RuntimeException("Failed to fetch users from Active Directory", e);
        }
    }

    private boolean updateUserFromAdAttributes(User user, Map<String, String> adAttrs) {
        boolean updated = false;

        String email = adAttrs.get("mail");
        if (email != null && !email.equals(user.getEmail())) {
            user.setEmail(email);
            updated = true;
        }

        String firstName = adAttrs.get("givenName");
        if (firstName != null && !firstName.equals(user.getFirstName())) {
            user.setFirstName(firstName);
            updated = true;
        }

        String lastName = adAttrs.get("sn");
        if (lastName != null && !lastName.equals(user.getLastName())) {
            user.setLastName(lastName);
            updated = true;
        }

        String department = adAttrs.get("department");
        if (department != null && !department.equals(user.getDepartment())) {
            user.setDepartment(department);
            updated = true;
        }

        String title = adAttrs.get("title");
        if (title != null && !title.equals(user.getJobTitle())) {
            user.setJobTitle(title);
            updated = true;
        }

        String phone = adAttrs.get("telephoneNumber");
        if (phone != null && !phone.equals(user.getPhone())) {
            user.setPhone(phone);
            updated = true;
        }

        String dn = adAttrs.get("distinguishedName");
        if (dn != null && !dn.equals(user.getAdDistinguishedName())) {
            user.setAdDistinguishedName(dn);
            updated = true;
        }

        return updated;
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
