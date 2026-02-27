package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.config.ActiveDirectoryProperties;
import com.arthmatic.shumelahire.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class ActiveDirectoryRoleMappingServiceTest {

    private ActiveDirectoryProperties properties;
    private ActiveDirectoryRoleMappingService service;

    @BeforeEach
    void setUp() {
        properties = new ActiveDirectoryProperties();
        properties.setDefaultRole("EMPLOYEE");

        Map<String, String> mappings = new HashMap<>();
        mappings.put("CN=SH-Admins", "ADMIN");
        mappings.put("CN=SH-HR-Managers", "HR_MANAGER");
        mappings.put("CN=SH-Recruiters", "RECRUITER");
        mappings.put("CN=SH-Executives", "EXECUTIVE");
        mappings.put("CN=SH-Interviewers", "INTERVIEWER");
        properties.setGroupRoleMappings(mappings);

        service = new ActiveDirectoryRoleMappingService(properties);
    }

    @Test
    void resolveRole_MatchingAdminGroup_ReturnsAdmin() {
        List<String> groups = List.of(
                "CN=SH-Admins,OU=Groups,DC=corp,DC=uthukela,DC=gov,DC=za",
                "CN=Domain Users,CN=Users,DC=corp"
        );

        User.Role role = service.resolveRole(groups);

        assertThat(role).isEqualTo(User.Role.ADMIN);
    }

    @Test
    void resolveRole_MultipleMatchingGroups_ReturnsHighestPriority() {
        List<String> groups = List.of(
                "CN=SH-Recruiters,OU=Groups,DC=corp",
                "CN=SH-HR-Managers,OU=Groups,DC=corp"
        );

        User.Role role = service.resolveRole(groups);

        assertThat(role).isEqualTo(User.Role.HR_MANAGER);
    }

    @Test
    void resolveRole_NoMatchingGroups_ReturnsDefaultRole() {
        List<String> groups = List.of(
                "CN=Domain Users,CN=Users,DC=corp",
                "CN=Finance-Team,OU=Groups,DC=corp"
        );

        User.Role role = service.resolveRole(groups);

        assertThat(role).isEqualTo(User.Role.EMPLOYEE);
    }

    @Test
    void resolveRole_EmptyGroups_ReturnsDefaultRole() {
        User.Role role = service.resolveRole(Collections.emptyList());

        assertThat(role).isEqualTo(User.Role.EMPLOYEE);
    }

    @Test
    void resolveRole_NullGroups_ReturnsDefaultRole() {
        User.Role role = service.resolveRole(null);

        assertThat(role).isEqualTo(User.Role.EMPLOYEE);
    }

    @Test
    void resolveRole_CaseInsensitiveGroupMatch() {
        List<String> groups = List.of("cn=sh-admins,ou=groups,dc=corp");

        User.Role role = service.resolveRole(groups);

        assertThat(role).isEqualTo(User.Role.ADMIN);
    }

    @Test
    void resolveRole_NoMappingsConfigured_ReturnsDefaultRole() {
        properties.setGroupRoleMappings(new HashMap<>());

        List<String> groups = List.of("CN=SH-Admins,OU=Groups,DC=corp");
        User.Role role = service.resolveRole(groups);

        assertThat(role).isEqualTo(User.Role.EMPLOYEE);
    }

    @Test
    void resolveRole_InvalidDefaultRole_FallsBackToEmployee() {
        properties.setDefaultRole("INVALID_ROLE");
        properties.setGroupRoleMappings(new HashMap<>());

        User.Role role = service.resolveRole(List.of("CN=Domain Users"));

        assertThat(role).isEqualTo(User.Role.EMPLOYEE);
    }
}
