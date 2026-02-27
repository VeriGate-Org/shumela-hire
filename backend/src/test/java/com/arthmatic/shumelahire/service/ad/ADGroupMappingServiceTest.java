package com.arthmatic.shumelahire.service.ad;

import com.arthmatic.shumelahire.dto.ad.ADGroupRoleMappingRequest;
import com.arthmatic.shumelahire.entity.ADGroupRoleMapping;
import com.arthmatic.shumelahire.entity.User;
import com.arthmatic.shumelahire.repository.ADGroupRoleMappingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ADGroupMappingServiceTest {

    @Mock
    private ADGroupRoleMappingRepository repository;

    @InjectMocks
    private ADGroupMappingService service;

    @Test
    void getAllMappings_returnsAllMappings() {
        ADGroupRoleMapping mapping1 = new ADGroupRoleMapping();
        mapping1.setAdGroupName("HR Managers");
        ADGroupRoleMapping mapping2 = new ADGroupRoleMapping();
        mapping2.setAdGroupName("Admins");

        when(repository.findAll()).thenReturn(Arrays.asList(mapping1, mapping2));

        List<ADGroupRoleMapping> result = service.getAllMappings();

        assertThat(result).hasSize(2);
        verify(repository).findAll();
    }

    @Test
    void createMapping_newGroup_createsSuccessfully() {
        ADGroupRoleMappingRequest request = new ADGroupRoleMappingRequest();
        request.setAdGroupName("HR Managers");
        request.setAdGroupDN("CN=HR-Managers,OU=Groups,DC=corp,DC=local");
        request.setShumelaRole(User.Role.HR_MANAGER);
        request.setDescription("Maps HR managers group");

        when(repository.existsByAdGroupDN(request.getAdGroupDN())).thenReturn(false);
        when(repository.save(any(ADGroupRoleMapping.class))).thenAnswer(inv -> inv.getArgument(0));

        ADGroupRoleMapping result = service.createMapping(request);

        assertThat(result.getAdGroupName()).isEqualTo("HR Managers");
        assertThat(result.getShumelaRole()).isEqualTo(User.Role.HR_MANAGER);
        assertThat(result.getIsActive()).isTrue();

        ArgumentCaptor<ADGroupRoleMapping> captor = ArgumentCaptor.forClass(ADGroupRoleMapping.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getAdGroupDN()).isEqualTo("CN=HR-Managers,OU=Groups,DC=corp,DC=local");
    }

    @Test
    void createMapping_duplicateGroup_throwsException() {
        ADGroupRoleMappingRequest request = new ADGroupRoleMappingRequest();
        request.setAdGroupDN("CN=HR-Managers,OU=Groups,DC=corp,DC=local");
        request.setShumelaRole(User.Role.HR_MANAGER);
        request.setAdGroupName("HR Managers");

        when(repository.existsByAdGroupDN(request.getAdGroupDN())).thenReturn(true);

        assertThatThrownBy(() -> service.createMapping(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("mapping already exists");
    }

    @Test
    void deleteMapping_existingId_deletesSuccessfully() {
        ADGroupRoleMapping mapping = new ADGroupRoleMapping();
        mapping.setId(1L);
        mapping.setAdGroupDN("CN=Test,DC=corp,DC=local");

        when(repository.findById(1L)).thenReturn(Optional.of(mapping));

        service.deleteMapping(1L);

        verify(repository).delete(mapping);
    }

    @Test
    void toggleMapping_setsActive() {
        ADGroupRoleMapping mapping = new ADGroupRoleMapping();
        mapping.setId(1L);
        mapping.setIsActive(true);
        mapping.setAdGroupDN("CN=Test,DC=corp,DC=local");

        when(repository.findById(1L)).thenReturn(Optional.of(mapping));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ADGroupRoleMapping result = service.toggleMapping(1L, false);

        assertThat(result.getIsActive()).isFalse();
    }
}
