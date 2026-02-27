package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.dto.org.OrgUnitRequest;
import com.arthmatic.shumelahire.dto.org.OrgUnitResponse;
import com.arthmatic.shumelahire.entity.OrgUnit;
import com.arthmatic.shumelahire.repository.EmployeeRepository;
import com.arthmatic.shumelahire.repository.OrgUnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrgUnitServiceTest {

    @Mock
    private OrgUnitRepository orgUnitRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private OrgUnitService orgUnitService;

    private OrgUnit testUnit;
    private OrgUnitRequest testRequest;

    @BeforeEach
    void setUp() {
        testUnit = new OrgUnit();
        testUnit.setId(1L);
        testUnit.setName("Engineering");
        testUnit.setCode("ENG");
        testUnit.setUnitType("DEPARTMENT");
        testUnit.setIsActive(true);
        testUnit.setCreatedAt(LocalDateTime.now());
        testUnit.setUpdatedAt(LocalDateTime.now());

        testRequest = new OrgUnitRequest();
        testRequest.setName("Engineering");
        testRequest.setCode("ENG");
        testRequest.setUnitType("DEPARTMENT");
        testRequest.setIsActive(true);
    }

    @Test
    void createOrgUnit_ValidRequest_ReturnsResponse() {
        when(orgUnitRepository.save(any(OrgUnit.class))).thenReturn(testUnit);

        OrgUnitResponse result = orgUnitService.createOrgUnit(testRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Engineering");
        assertThat(result.getCode()).isEqualTo("ENG");
        assertThat(result.getUnitType()).isEqualTo("DEPARTMENT");
        verify(orgUnitRepository, times(1)).save(any(OrgUnit.class));
    }

    @Test
    void updateOrgUnit_ExistingId_ReturnsUpdatedResponse() {
        OrgUnitRequest updateRequest = new OrgUnitRequest();
        updateRequest.setName("Engineering Dept");
        updateRequest.setCode("ENG");
        updateRequest.setUnitType("DEPARTMENT");
        updateRequest.setIsActive(true);

        OrgUnit updatedUnit = new OrgUnit();
        updatedUnit.setId(1L);
        updatedUnit.setName("Engineering Dept");
        updatedUnit.setCode("ENG");
        updatedUnit.setUnitType("DEPARTMENT");
        updatedUnit.setIsActive(true);
        updatedUnit.setCreatedAt(LocalDateTime.now());
        updatedUnit.setUpdatedAt(LocalDateTime.now());

        when(orgUnitRepository.findById(1L)).thenReturn(Optional.of(testUnit));
        when(orgUnitRepository.save(any(OrgUnit.class))).thenReturn(updatedUnit);

        OrgUnitResponse result = orgUnitService.updateOrgUnit(1L, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Engineering Dept");
        verify(orgUnitRepository, times(1)).save(any(OrgUnit.class));
    }

    @Test
    void getOrgUnit_ExistingId_ReturnsResponse() {
        when(orgUnitRepository.findById(1L)).thenReturn(Optional.of(testUnit));

        OrgUnitResponse result = orgUnitService.getOrgUnit(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Engineering");
    }

    @Test
    void getOrgUnit_NonExistingId_ThrowsIllegalArgumentException() {
        when(orgUnitRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> orgUnitService.getOrgUnit(999L));
    }

    @Test
    void getAllOrgUnits_ReturnsActiveUnits() {
        OrgUnit unit2 = new OrgUnit();
        unit2.setId(2L);
        unit2.setName("HR");
        unit2.setUnitType("DEPARTMENT");
        unit2.setIsActive(true);
        unit2.setCreatedAt(LocalDateTime.now());
        unit2.setUpdatedAt(LocalDateTime.now());

        when(orgUnitRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(testUnit, unit2));

        List<OrgUnitResponse> result = orgUnitService.getAllOrgUnits();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(OrgUnitResponse::getName).containsExactlyInAnyOrder("Engineering", "HR");
    }

    @Test
    void getOrgTree_ReturnsRootUnitsWithChildren() {
        when(orgUnitRepository.findRootUnits()).thenReturn(Collections.singletonList(testUnit));

        List<OrgUnitResponse> result = orgUnitService.getOrgTree();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Engineering");
    }

    @Test
    void deleteOrgUnit_ExistingId_DeactivatesUnit() {
        when(orgUnitRepository.findById(1L)).thenReturn(Optional.of(testUnit));
        when(orgUnitRepository.save(any(OrgUnit.class))).thenReturn(testUnit);

        orgUnitService.deleteOrgUnit(1L);

        verify(orgUnitRepository, times(1)).save(argThat(u -> !u.getIsActive()));
    }

    @Test
    void moveOrgUnit_ValidIds_UpdatesParent() {
        OrgUnit newParent = new OrgUnit();
        newParent.setId(2L);
        newParent.setName("Company");
        newParent.setUnitType("COMPANY");
        newParent.setIsActive(true);
        newParent.setCreatedAt(LocalDateTime.now());
        newParent.setUpdatedAt(LocalDateTime.now());

        when(orgUnitRepository.findById(1L)).thenReturn(Optional.of(testUnit));
        when(orgUnitRepository.findById(2L)).thenReturn(Optional.of(newParent));
        when(orgUnitRepository.save(any(OrgUnit.class))).thenAnswer(inv -> inv.getArgument(0));

        OrgUnitResponse result = orgUnitService.moveOrgUnit(1L, 2L);

        assertThat(result).isNotNull();
        verify(orgUnitRepository, times(2)).findById(any());
        verify(orgUnitRepository, times(1)).save(any(OrgUnit.class));
    }
}
