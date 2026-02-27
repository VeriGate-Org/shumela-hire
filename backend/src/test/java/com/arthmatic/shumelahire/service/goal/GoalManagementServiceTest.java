package com.arthmatic.shumelahire.service.goal;

import com.arthmatic.shumelahire.config.tenant.TenantContext;
import com.arthmatic.shumelahire.dto.goal.*;
import com.arthmatic.shumelahire.entity.goal.*;
import com.arthmatic.shumelahire.entity.performance.PerformanceCycle;
import com.arthmatic.shumelahire.repository.goal.GoalLinkRepository;
import com.arthmatic.shumelahire.repository.goal.GoalRepository;
import com.arthmatic.shumelahire.repository.goal.KeyResultRepository;
import com.arthmatic.shumelahire.repository.performance.PerformanceCycleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalManagementServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private KeyResultRepository keyResultRepository;

    @Mock
    private GoalLinkRepository goalLinkRepository;

    @Mock
    private PerformanceCycleRepository cycleRepository;

    @InjectMocks
    private GoalManagementService goalService;

    private static final String TENANT_ID = "test-tenant";
    private static final String USER_ID = "user-001";

    private Goal testGoal;
    private KeyResult testKeyResult;
    private GoalRequest testGoalRequest;

    @BeforeEach
    void setUp() {
        TenantContext.setCurrentTenant(TENANT_ID);

        testGoal = new Goal();
        testGoal.setId(1L);
        testGoal.setTitle("Increase Market Share");
        testGoal.setType(GoalType.OKR);
        testGoal.setStatus(GoalStatus.DRAFT);
        testGoal.setOwnerType(OwnerType.ORGANIZATION);
        testGoal.setOwnerId("org-001");
        testGoal.setPeriod(GoalPeriod.ANNUAL);
        testGoal.setTenantId(TENANT_ID);
        testGoal.setIsActive(true);

        testKeyResult = new KeyResult();
        testKeyResult.setId(10L);
        testKeyResult.setGoal(testGoal);
        testKeyResult.setMetric("Revenue Growth %");
        testKeyResult.setTargetValue(BigDecimal.valueOf(20));
        testKeyResult.setCurrentValue(BigDecimal.valueOf(10));
        testKeyResult.setTenantId(TENANT_ID);

        testGoalRequest = new GoalRequest();
        testGoalRequest.setTitle("Increase Market Share");
        testGoalRequest.setType(GoalType.OKR);
        testGoalRequest.setOwnerType(OwnerType.ORGANIZATION);
        testGoalRequest.setOwnerId("org-001");
        testGoalRequest.setPeriod(GoalPeriod.ANNUAL);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    // ========== CREATE GOAL ==========

    @Test
    void createGoal_withValidRequest_savesAndReturnsGoal() {
        when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);
        when(goalRepository.findByIdAndTenantId(1L, TENANT_ID)).thenReturn(Optional.of(testGoal));

        GoalResponse response = goalService.createGoal(testGoalRequest, TENANT_ID, USER_ID);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Increase Market Share");
        assertThat(response.getType()).isEqualTo(GoalType.OKR);
        assertThat(response.getStatus()).isEqualTo(GoalStatus.DRAFT);
        verify(goalRepository, times(1)).save(any(Goal.class));
    }

    @Test
    void createGoal_withParentGoal_setsParentAndSaves() {
        Goal parentGoal = new Goal();
        parentGoal.setId(99L);
        parentGoal.setTenantId(TENANT_ID);

        testGoalRequest.setParentGoalId(99L);

        when(goalRepository.findByIdAndTenantId(99L, TENANT_ID)).thenReturn(Optional.of(parentGoal));
        when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);
        when(goalRepository.findByIdAndTenantId(1L, TENANT_ID)).thenReturn(Optional.of(testGoal));

        GoalResponse response = goalService.createGoal(testGoalRequest, TENANT_ID, USER_ID);

        assertThat(response).isNotNull();
        verify(goalRepository, times(2)).findByIdAndTenantId(anyLong(), eq(TENANT_ID));
    }

    @Test
    void createGoal_withInvalidParentId_throwsIllegalArgumentException() {
        testGoalRequest.setParentGoalId(999L);
        when(goalRepository.findByIdAndTenantId(999L, TENANT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.createGoal(testGoalRequest, TENANT_ID, USER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Parent goal not found");
    }

    @Test
    void createGoal_withKeyResults_savesKeyResultsToo() {
        KeyResultRequest krRequest = new KeyResultRequest();
        krRequest.setMetric("Revenue Growth %");
        krRequest.setTargetValue(BigDecimal.valueOf(20));
        testGoalRequest.setKeyResults(List.of(krRequest));

        when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);
        when(goalRepository.findByIdAndTenantId(1L, TENANT_ID)).thenReturn(Optional.of(testGoal));

        goalService.createGoal(testGoalRequest, TENANT_ID, USER_ID);

        verify(keyResultRepository, times(1)).saveAll(anyList());
    }

    // ========== GET GOALS ==========

    @Test
    void getGoals_returnsPagedResults() {
        Page<Goal> page = new PageImpl<>(List.of(testGoal));
        when(goalRepository.findByTenantIdAndIsActiveOrderByCreatedAtDesc(TENANT_ID, true, PageRequest.of(0, 20)))
                .thenReturn(page);

        Page<GoalResponse> result = goalService.getGoals(TENANT_ID, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Increase Market Share");
    }

    @Test
    void getGoal_withValidId_returnsGoal() {
        when(goalRepository.findByIdAndTenantId(1L, TENANT_ID)).thenReturn(Optional.of(testGoal));

        Optional<GoalResponse> result = goalService.getGoal(1L, TENANT_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void getGoal_withUnknownId_returnsEmpty() {
        when(goalRepository.findByIdAndTenantId(999L, TENANT_ID)).thenReturn(Optional.empty());

        Optional<GoalResponse> result = goalService.getGoal(999L, TENANT_ID);

        assertThat(result).isEmpty();
    }

    // ========== ACTIVATE / COMPLETE / CANCEL ==========

    @Test
    void activateGoal_fromDraft_changesStatusToActive() {
        when(goalRepository.findByIdAndTenantId(1L, TENANT_ID)).thenReturn(Optional.of(testGoal));
        when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);

        goalService.activateGoal(1L, TENANT_ID);

        assertThat(testGoal.getStatus()).isEqualTo(GoalStatus.ACTIVE);
        verify(goalRepository).save(testGoal);
    }

    @Test
    void activateGoal_fromActive_throwsIllegalStateException() {
        testGoal.setStatus(GoalStatus.ACTIVE);
        when(goalRepository.findByIdAndTenantId(1L, TENANT_ID)).thenReturn(Optional.of(testGoal));

        assertThatThrownBy(() -> goalService.activateGoal(1L, TENANT_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot be activated");
    }

    @Test
    void completeGoal_fromActive_changesStatusToCompleted() {
        testGoal.setStatus(GoalStatus.ACTIVE);
        when(goalRepository.findByIdAndTenantId(1L, TENANT_ID)).thenReturn(Optional.of(testGoal));
        when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);

        goalService.completeGoal(1L, TENANT_ID);

        assertThat(testGoal.getStatus()).isEqualTo(GoalStatus.COMPLETED);
    }

    @Test
    void cancelGoal_fromDraft_changesStatusToCancelled() {
        when(goalRepository.findByIdAndTenantId(1L, TENANT_ID)).thenReturn(Optional.of(testGoal));
        when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);

        goalService.cancelGoal(1L, TENANT_ID);

        assertThat(testGoal.getStatus()).isEqualTo(GoalStatus.CANCELLED);
    }

    @Test
    void deleteGoal_setsIsActiveFalse() {
        when(goalRepository.findByIdAndTenantId(1L, TENANT_ID)).thenReturn(Optional.of(testGoal));
        when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);

        goalService.deleteGoal(1L, TENANT_ID);

        assertThat(testGoal.getIsActive()).isFalse();
        verify(goalRepository).save(testGoal);
    }

    // ========== KEY RESULTS ==========

    @Test
    void addKeyResult_withValidGoal_createsKeyResult() {
        KeyResultRequest request = new KeyResultRequest();
        request.setMetric("Revenue Growth %");
        request.setTargetValue(BigDecimal.valueOf(20));

        when(goalRepository.findByIdAndTenantId(1L, TENANT_ID)).thenReturn(Optional.of(testGoal));
        when(keyResultRepository.save(any(KeyResult.class))).thenReturn(testKeyResult);

        KeyResultResponse response = goalService.addKeyResult(1L, request, TENANT_ID);

        assertThat(response).isNotNull();
        assertThat(response.getMetric()).isEqualTo("Revenue Growth %");
        assertThat(response.getTargetValue()).isEqualByComparingTo(BigDecimal.valueOf(20));
    }

    @Test
    void updateKeyResultProgress_updatesCurrentValue() {
        ProgressUpdateRequest request = new ProgressUpdateRequest();
        request.setCurrentValue(BigDecimal.valueOf(15));

        when(keyResultRepository.findByIdAndGoalTenantId(10L, TENANT_ID)).thenReturn(Optional.of(testKeyResult));
        when(keyResultRepository.save(any(KeyResult.class))).thenReturn(testKeyResult);

        KeyResultResponse response = goalService.updateKeyResultProgress(10L, request, TENANT_ID);

        assertThat(testKeyResult.getCurrentValue()).isEqualByComparingTo(BigDecimal.valueOf(15));
        assertThat(testKeyResult.getLastUpdated()).isEqualTo(LocalDate.now());
    }

    @Test
    void getKeyResults_progressPctCalculatedCorrectly() {
        // currentValue = 10, targetValue = 20 => 50%
        assertThat(testKeyResult.getProgressPct()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
    }

    // ========== GOAL LINKS ==========

    @Test
    void linkGoalToCycle_withNewLink_createsLink() {
        PerformanceCycle cycle = new PerformanceCycle();
        cycle.setId(5L);
        cycle.setName("2025 Annual Review");
        cycle.setTenantId(TENANT_ID);

        GoalLinkRequest linkRequest = new GoalLinkRequest();
        linkRequest.setWeight(BigDecimal.valueOf(30));

        GoalLink savedLink = new GoalLink();
        savedLink.setId(100L);
        savedLink.setGoal(testGoal);
        savedLink.setReviewCycle(cycle);
        savedLink.setWeight(BigDecimal.valueOf(30));
        savedLink.setTenantId(TENANT_ID);

        when(goalRepository.findByIdAndTenantId(1L, TENANT_ID)).thenReturn(Optional.of(testGoal));
        when(cycleRepository.findByIdAndTenantId(5L, TENANT_ID)).thenReturn(Optional.of(cycle));
        when(goalLinkRepository.existsByGoalIdAndReviewCycleId(1L, 5L)).thenReturn(false);
        when(goalLinkRepository.save(any(GoalLink.class))).thenReturn(savedLink);

        GoalLinkResponse response = goalService.linkGoalToCycle(1L, 5L, linkRequest, TENANT_ID, USER_ID);

        assertThat(response).isNotNull();
        assertThat(response.getWeight()).isEqualByComparingTo(BigDecimal.valueOf(30));
        verify(goalLinkRepository).save(any(GoalLink.class));
    }

    @Test
    void linkGoalToCycle_whenAlreadyLinked_throwsIllegalStateException() {
        PerformanceCycle cycle = new PerformanceCycle();
        cycle.setId(5L);
        cycle.setTenantId(TENANT_ID);

        GoalLinkRequest linkRequest = new GoalLinkRequest();
        linkRequest.setWeight(BigDecimal.valueOf(30));

        when(goalRepository.findByIdAndTenantId(1L, TENANT_ID)).thenReturn(Optional.of(testGoal));
        when(cycleRepository.findByIdAndTenantId(5L, TENANT_ID)).thenReturn(Optional.of(cycle));
        when(goalLinkRepository.existsByGoalIdAndReviewCycleId(1L, 5L)).thenReturn(true);

        assertThatThrownBy(() -> goalService.linkGoalToCycle(1L, 5L, linkRequest, TENANT_ID, USER_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already linked");
    }

    @Test
    void getGoalsByOwner_returnsGoalsForOwner() {
        when(goalRepository.findByTenantIdAndOwnerTypeAndOwnerIdAndIsActiveOrderBySortOrderAscCreatedAtDesc(
                TENANT_ID, OwnerType.ORGANIZATION, "org-001", true))
                .thenReturn(List.of(testGoal));

        List<GoalResponse> results = goalService.getGoalsByOwner(TENANT_ID, OwnerType.ORGANIZATION, "org-001");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getOwnerId()).isEqualTo("org-001");
    }
}
