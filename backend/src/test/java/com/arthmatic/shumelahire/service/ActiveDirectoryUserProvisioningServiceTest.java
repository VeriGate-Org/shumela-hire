package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.config.ActiveDirectoryProperties;
import com.arthmatic.shumelahire.entity.User;
import com.arthmatic.shumelahire.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActiveDirectoryUserProvisioningServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActiveDirectoryRoleMappingService roleMappingService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private DirContextOperations adContext;

    private ActiveDirectoryProperties adProperties;
    private ActiveDirectoryUserProvisioningService service;

    @BeforeEach
    void setUp() {
        adProperties = new ActiveDirectoryProperties();
        adProperties.setDomain("corp.uthukela.gov.za");
        adProperties.setDefaultTenantId("default");
        adProperties.setDefaultRole("EMPLOYEE");

        service = new ActiveDirectoryUserProvisioningService(
                userRepository, roleMappingService, adProperties, passwordEncoder, auditLogService);
    }

    @Test
    void provisionOrSyncUser_NewUser_CreatesUserInDatabase() {
        when(adContext.getStringAttribute("objectGUID")).thenReturn("ad-guid-12345");
        when(adContext.getStringAttribute("mail")).thenReturn("jdoe@corp.uthukela.gov.za");
        when(adContext.getStringAttribute("givenName")).thenReturn("John");
        when(adContext.getStringAttribute("sn")).thenReturn("Doe");
        when(adContext.getStringAttribute("department")).thenReturn("IT");
        when(adContext.getStringAttribute("title")).thenReturn("Systems Administrator");
        when(adContext.getStringAttribute("telephoneNumber")).thenReturn("+27123456789");
        when(adContext.getStringAttribute("physicalDeliveryOfficeName")).thenReturn("Ladysmith");

        when(userRepository.findBySsoProviderAndSsoUserIdAndTenantId("AZURE_AD", "ad-guid-12345", "default"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmailAndTenantId("jdoe@corp.uthukela.gov.za", "default"))
                .thenReturn(Optional.empty());
        when(roleMappingService.resolveRole(anyCollection())).thenReturn(User.Role.EMPLOYEE);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("jdoe");
        savedUser.setRole(User.Role.EMPLOYEE);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        List<String> groups = List.of("CN=Domain Users,DC=corp");
        User result = service.provisionOrSyncUser(adContext, "jdoe", groups);

        assertThat(result).isNotNull();
        verify(userRepository, times(1)).save(any(User.class));
        verify(auditLogService, times(1)).logApplicantAction(
                eq(1L), eq("AD_USER_PROVISIONED"), eq("USER"), anyString());
    }

    @Test
    void provisionOrSyncUser_ExistingUserBySsoId_SyncsAttributes() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("jdoe");
        existingUser.setRole(User.Role.EMPLOYEE);
        existingUser.setSsoProvider("AZURE_AD");
        existingUser.setSsoUserId("ad-guid-12345");
        existingUser.setTenantId("default");

        when(adContext.getStringAttribute("objectGUID")).thenReturn("ad-guid-12345");
        when(adContext.getStringAttribute("givenName")).thenReturn("John");
        when(adContext.getStringAttribute("sn")).thenReturn("Doe");
        when(adContext.getStringAttribute("department")).thenReturn("Engineering");
        when(adContext.getStringAttribute("title")).thenReturn("Senior Developer");
        when(adContext.getStringAttribute("telephoneNumber")).thenReturn("+27999999999");

        when(userRepository.findBySsoProviderAndSsoUserIdAndTenantId("AZURE_AD", "ad-guid-12345", "default"))
                .thenReturn(Optional.of(existingUser));
        when(roleMappingService.resolveRole(anyCollection())).thenReturn(User.Role.EMPLOYEE);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        List<String> groups = List.of("CN=Domain Users,DC=corp");
        User result = service.provisionOrSyncUser(adContext, "jdoe", groups);

        assertThat(result.getDepartment()).isEqualTo("Engineering");
        assertThat(result.getJobTitle()).isEqualTo("Senior Developer");
        assertThat(result.getLastLogin()).isNotNull();
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void provisionOrSyncUser_ExistingUser_RoleUpdatedFromAd() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("jdoe");
        existingUser.setRole(User.Role.EMPLOYEE);
        existingUser.setSsoProvider("AZURE_AD");
        existingUser.setSsoUserId("ad-guid-12345");
        existingUser.setTenantId("default");
        existingUser.setFailedLoginAttempts(3);

        when(adContext.getStringAttribute("objectGUID")).thenReturn("ad-guid-12345");

        when(userRepository.findBySsoProviderAndSsoUserIdAndTenantId("AZURE_AD", "ad-guid-12345", "default"))
                .thenReturn(Optional.of(existingUser));
        when(roleMappingService.resolveRole(anyCollection())).thenReturn(User.Role.HR_MANAGER);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        List<String> groups = List.of("CN=SH-HR-Managers,DC=corp");
        User result = service.provisionOrSyncUser(adContext, "jdoe", groups);

        assertThat(result.getRole()).isEqualTo(User.Role.HR_MANAGER);
        assertThat(result.getFailedLoginAttempts()).isEqualTo(0);
    }

    @Test
    void provisionOrSyncUser_ExistingUserByEmail_LinksToSso() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("jdoe");
        existingUser.setRole(User.Role.EMPLOYEE);
        existingUser.setTenantId("default");

        when(adContext.getStringAttribute("objectGUID")).thenReturn("ad-guid-new");
        when(adContext.getStringAttribute("mail")).thenReturn("jdoe@corp.uthukela.gov.za");

        when(userRepository.findBySsoProviderAndSsoUserIdAndTenantId("AZURE_AD", "ad-guid-new", "default"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmailAndTenantId("jdoe@corp.uthukela.gov.za", "default"))
                .thenReturn(Optional.of(existingUser));
        when(roleMappingService.resolveRole(anyCollection())).thenReturn(User.Role.EMPLOYEE);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        List<String> groups = List.of("CN=Domain Users,DC=corp");
        User result = service.provisionOrSyncUser(adContext, "jdoe", groups);

        assertThat(result.getSsoProvider()).isEqualTo("AZURE_AD");
        assertThat(result.getSsoUserId()).isEqualTo("ad-guid-new");
    }
}
