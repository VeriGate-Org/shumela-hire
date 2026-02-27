package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.dto.org.HeadcountPlanRequest;
import com.arthmatic.shumelahire.dto.org.HeadcountPlanResponse;
import com.arthmatic.shumelahire.entity.HeadcountPlan;
import com.arthmatic.shumelahire.repository.HeadcountPlanRepository;
import com.arthmatic.shumelahire.repository.OrgUnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HeadcountPlanServiceTest {

    @Mock
    private HeadcountPlanRepository headcountPlanRepository;

    @Mock
    private OrgUnitRepository orgUnitRepository;

    @InjectMocks
    private HeadcountPlanService headcountPlanService;

    private HeadcountPlan testPlan;
    private HeadcountPlanRequest testRequest;

    @BeforeEach
    void setUp() {
        testPlan = new HeadcountPlan();
        testPlan.setId(1L);
        testPlan.setDepartment("Engineering");
        testPlan.setFiscalYear(2026);
        testPlan.setPlannedHeadcount(50);
        testPlan.setCurrentHeadcount(45);
        testPlan.setBudget(new BigDecimal("5000000"));
        testPlan.setForecastVacancies(3);
        testPlan.setNewPositionRequests(2);
        testPlan.setCreatedAt(LocalDateTime.now());
        testPlan.setUpdatedAt(LocalDateTime.now());

        testRequest = new HeadcountPlanRequest();
        testRequest.setDepartment("Engineering");
        testRequest.setFiscalYear(2026);
        testRequest.setPlannedHeadcount(50);
        testRequest.setCurrentHeadcount(45);
        testRequest.setBudget(new BigDecimal("5000000"));
    }

    @Test
    void createHeadcountPlan_ValidRequest_ReturnsResponse() {
        when(headcountPlanRepository.save(any(HeadcountPlan.class))).thenReturn(testPlan);

        HeadcountPlanResponse result = headcountPlanService.createHeadcountPlan(testRequest);

        assertThat(result).isNotNull();
        assertThat(result.getDepartment()).isEqualTo("Engineering");
        assertThat(result.getFiscalYear()).isEqualTo(2026);
        assertThat(result.getPlannedHeadcount()).isEqualTo(50);
        assertThat(result.getCurrentHeadcount()).isEqualTo(45);
        assertThat(result.getVariance()).isEqualTo(5);
        verify(headcountPlanRepository, times(1)).save(any(HeadcountPlan.class));
    }

    @Test
    void updateHeadcountPlan_ExistingId_ReturnsUpdatedResponse() {
        HeadcountPlanRequest updateRequest = new HeadcountPlanRequest();
        updateRequest.setDepartment("Engineering");
        updateRequest.setFiscalYear(2026);
        updateRequest.setPlannedHeadcount(55);
        updateRequest.setCurrentHeadcount(48);

        HeadcountPlan updatedPlan = new HeadcountPlan();
        updatedPlan.setId(1L);
        updatedPlan.setDepartment("Engineering");
        updatedPlan.setFiscalYear(2026);
        updatedPlan.setPlannedHeadcount(55);
        updatedPlan.setCurrentHeadcount(48);
        updatedPlan.setCreatedAt(LocalDateTime.now());
        updatedPlan.setUpdatedAt(LocalDateTime.now());

        when(headcountPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        when(headcountPlanRepository.save(any(HeadcountPlan.class))).thenReturn(updatedPlan);

        HeadcountPlanResponse result = headcountPlanService.updateHeadcountPlan(1L, updateRequest);

        assertThat(result.getPlannedHeadcount()).isEqualTo(55);
        assertThat(result.getCurrentHeadcount()).isEqualTo(48);
    }

    @Test
    void getHeadcountPlan_NonExistingId_ThrowsIllegalArgumentException() {
        when(headcountPlanRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> headcountPlanService.getHeadcountPlan(999L));
    }

    @Test
    void getHeadcountPlansByYear_ReturnsPlanList() {
        HeadcountPlan plan2 = new HeadcountPlan();
        plan2.setId(2L);
        plan2.setDepartment("HR");
        plan2.setFiscalYear(2026);
        plan2.setPlannedHeadcount(10);
        plan2.setCurrentHeadcount(9);
        plan2.setCreatedAt(LocalDateTime.now());
        plan2.setUpdatedAt(LocalDateTime.now());

        when(headcountPlanRepository.findByFiscalYearOrdered(2026)).thenReturn(Arrays.asList(testPlan, plan2));

        List<HeadcountPlanResponse> result = headcountPlanService.getHeadcountPlansByYear(2026);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(HeadcountPlanResponse::getDepartment)
                .containsExactlyInAnyOrder("Engineering", "HR");
    }

    @Test
    void getYearSummary_ReturnsAggregatedData() {
        when(headcountPlanRepository.sumPlannedHeadcountByYear(2026)).thenReturn(60L);
        when(headcountPlanRepository.sumCurrentHeadcountByYear(2026)).thenReturn(54L);
        when(headcountPlanRepository.sumBudgetByYear(2026)).thenReturn(new BigDecimal("6000000"));

        Map<String, Object> result = headcountPlanService.getYearSummary(2026);

        assertThat(result).isNotNull();
        assertThat(result.get("fiscalYear")).isEqualTo(2026);
        assertThat(result.get("totalPlannedHeadcount")).isEqualTo(60L);
        assertThat(result.get("totalCurrentHeadcount")).isEqualTo(54L);
        assertThat(result.get("totalVariance")).isEqualTo(6L);
    }

    @Test
    void deleteHeadcountPlan_ExistingId_DeletesPlan() {
        when(headcountPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));

        headcountPlanService.deleteHeadcountPlan(1L);

        verify(headcountPlanRepository, times(1)).delete(testPlan);
    }
}
