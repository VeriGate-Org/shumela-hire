package com.arthmatic.shumelahire.service.goal;

import com.arthmatic.shumelahire.dto.goal.*;
import com.arthmatic.shumelahire.entity.goal.*;
import com.arthmatic.shumelahire.entity.performance.PerformanceCycle;
import com.arthmatic.shumelahire.repository.goal.GoalLinkRepository;
import com.arthmatic.shumelahire.repository.goal.GoalRepository;
import com.arthmatic.shumelahire.repository.goal.KeyResultRepository;
import com.arthmatic.shumelahire.repository.performance.PerformanceCycleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class GoalManagementService {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private KeyResultRepository keyResultRepository;

    @Autowired
    private GoalLinkRepository goalLinkRepository;

    @Autowired
    private PerformanceCycleRepository cycleRepository;

    // ========== GOALS ==========

    public GoalResponse createGoal(GoalRequest request, String tenantId, String createdBy) {
        Goal goal = new Goal();
        applyGoalRequest(goal, request, tenantId);
        goal.setCreatedBy(createdBy);
        goal.setTenantId(tenantId);

        if (request.getParentGoalId() != null) {
            Goal parent = goalRepository.findByIdAndTenantId(request.getParentGoalId(), tenantId)
                    .orElseThrow(() -> new IllegalArgumentException("Parent goal not found"));
            goal.setParentGoal(parent);
        }

        Goal saved = goalRepository.save(goal);

        if (request.getKeyResults() != null && !request.getKeyResults().isEmpty()) {
            createKeyResults(saved, request.getKeyResults(), tenantId);
        }

        return GoalResponse.fromEntity(goalRepository.findByIdAndTenantId(saved.getId(), tenantId).orElseThrow());
    }

    public Page<GoalResponse> getGoals(String tenantId, Pageable pageable) {
        return goalRepository.findByTenantIdAndIsActiveOrderByCreatedAtDesc(tenantId, true, pageable)
                .map(GoalResponse::fromEntity);
    }

    public Optional<GoalResponse> getGoal(Long id, String tenantId) {
        return goalRepository.findByIdAndTenantId(id, tenantId)
                .map(GoalResponse::fromEntity);
    }

    public GoalResponse updateGoal(Long id, GoalRequest request, String tenantId) {
        Goal goal = goalRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        if (!goal.isEditable()) {
            throw new IllegalStateException("Goal cannot be edited in status: " + goal.getStatus());
        }

        applyGoalRequest(goal, request, tenantId);

        if (request.getParentGoalId() != null) {
            if (request.getParentGoalId().equals(id)) {
                throw new IllegalArgumentException("A goal cannot be its own parent");
            }
            Goal parent = goalRepository.findByIdAndTenantId(request.getParentGoalId(), tenantId)
                    .orElseThrow(() -> new IllegalArgumentException("Parent goal not found"));
            goal.setParentGoal(parent);
        } else {
            goal.setParentGoal(null);
        }

        return GoalResponse.fromEntity(goalRepository.save(goal));
    }

    public void deleteGoal(Long id, String tenantId) {
        Goal goal = goalRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));
        goal.setIsActive(false);
        goalRepository.save(goal);
    }

    public GoalResponse activateGoal(Long id, String tenantId) {
        Goal goal = goalRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        if (!goal.canBeActivated()) {
            throw new IllegalStateException("Goal cannot be activated from status: " + goal.getStatus());
        }

        goal.setStatus(GoalStatus.ACTIVE);
        return GoalResponse.fromEntity(goalRepository.save(goal));
    }

    public GoalResponse completeGoal(Long id, String tenantId) {
        Goal goal = goalRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        if (!goal.canBeCompleted()) {
            throw new IllegalStateException("Goal cannot be completed from status: " + goal.getStatus());
        }

        goal.setStatus(GoalStatus.COMPLETED);
        return GoalResponse.fromEntity(goalRepository.save(goal));
    }

    public GoalResponse cancelGoal(Long id, String tenantId) {
        Goal goal = goalRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        if (!goal.canBeCancelled()) {
            throw new IllegalStateException("Goal cannot be cancelled from status: " + goal.getStatus());
        }

        goal.setStatus(GoalStatus.CANCELLED);
        return GoalResponse.fromEntity(goalRepository.save(goal));
    }

    public List<GoalResponse> getGoalsByOwner(String tenantId, OwnerType ownerType, String ownerId) {
        return goalRepository.findByTenantIdAndOwnerTypeAndOwnerIdAndIsActiveOrderBySortOrderAscCreatedAtDesc(
                        tenantId, ownerType, ownerId, true)
                .stream()
                .map(GoalResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<GoalResponse> getGoalsByStatus(String tenantId, GoalStatus status) {
        return goalRepository.findByTenantIdAndStatusAndIsActiveOrderByCreatedAtDesc(tenantId, status, true)
                .stream()
                .map(GoalResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<GoalResponse> getChildGoals(Long parentGoalId, String tenantId) {
        goalRepository.findByIdAndTenantId(parentGoalId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Parent goal not found"));

        return goalRepository.findByParentGoalIdAndTenantIdAndIsActiveOrderBySortOrderAsc(parentGoalId, tenantId, true)
                .stream()
                .map(GoalResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<GoalResponse> getTopLevelGoals(String tenantId) {
        return goalRepository.findByParentGoalIsNullAndTenantIdAndIsActiveOrderBySortOrderAscCreatedAtDesc(tenantId, true)
                .stream()
                .map(GoalResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // ========== KEY RESULTS ==========

    public List<KeyResultResponse> getKeyResults(Long goalId, String tenantId) {
        goalRepository.findByIdAndTenantId(goalId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        return keyResultRepository.findByGoalIdOrderBySortOrderAscCreatedAtAsc(goalId)
                .stream()
                .map(KeyResultResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public KeyResultResponse addKeyResult(Long goalId, KeyResultRequest request, String tenantId) {
        Goal goal = goalRepository.findByIdAndTenantId(goalId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        KeyResult kr = buildKeyResult(goal, request, tenantId);
        return KeyResultResponse.fromEntity(keyResultRepository.save(kr));
    }

    public KeyResultResponse updateKeyResultProgress(Long krId, ProgressUpdateRequest request, String tenantId) {
        KeyResult kr = keyResultRepository.findByIdAndGoalTenantId(krId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Key result not found"));

        kr.setCurrentValue(request.getCurrentValue());
        kr.setLastUpdated(LocalDate.now());

        if (request.getStatus() != null) {
            kr.setStatus(request.getStatus());
        }

        return KeyResultResponse.fromEntity(keyResultRepository.save(kr));
    }

    public void deleteKeyResult(Long krId, String tenantId) {
        KeyResult kr = keyResultRepository.findByIdAndGoalTenantId(krId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Key result not found"));
        keyResultRepository.delete(kr);
    }

    // ========== GOAL LINKS ==========

    public GoalLinkResponse linkGoalToCycle(Long goalId, Long cycleId, GoalLinkRequest request,
                                            String tenantId, String createdBy) {
        Goal goal = goalRepository.findByIdAndTenantId(goalId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        PerformanceCycle cycle = cycleRepository.findByIdAndTenantId(cycleId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Performance cycle not found"));

        if (goalLinkRepository.existsByGoalIdAndReviewCycleId(goalId, cycleId)) {
            throw new IllegalStateException("Goal is already linked to this performance cycle");
        }

        GoalLink link = new GoalLink();
        link.setGoal(goal);
        link.setReviewCycle(cycle);
        link.setWeight(request.getWeight() != null ? request.getWeight() : BigDecimal.ZERO);
        link.setTenantId(tenantId);
        link.setCreatedBy(createdBy);

        return GoalLinkResponse.fromEntity(goalLinkRepository.save(link));
    }

    public void unlinkGoalFromCycle(Long goalId, Long cycleId, String tenantId) {
        GoalLink link = goalLinkRepository.findByGoalIdAndReviewCycleId(goalId, cycleId)
                .orElseThrow(() -> new IllegalArgumentException("Goal link not found"));

        goalLinkRepository.delete(link);
    }

    public List<GoalLinkResponse> getGoalLinks(Long goalId, String tenantId) {
        goalRepository.findByIdAndTenantId(goalId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        return goalLinkRepository.findByGoalIdAndTenantId(goalId, tenantId)
                .stream()
                .map(GoalLinkResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<GoalLinkResponse> getGoalsLinkedToCycle(Long cycleId, String tenantId) {
        return goalLinkRepository.findByReviewCycleIdAndTenantId(cycleId, tenantId)
                .stream()
                .map(GoalLinkResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // ========== PRIVATE HELPERS ==========

    private void applyGoalRequest(Goal goal, GoalRequest request, String tenantId) {
        goal.setTitle(request.getTitle());
        goal.setDescription(request.getDescription());
        goal.setType(request.getType());
        goal.setOwnerType(request.getOwnerType());
        goal.setOwnerId(request.getOwnerId());
        goal.setPeriod(request.getPeriod());
        goal.setStartDate(request.getStartDate());
        goal.setEndDate(request.getEndDate());
        goal.setSortOrder(request.getSortOrder());
    }

    private void createKeyResults(Goal goal, List<KeyResultRequest> requests, String tenantId) {
        List<KeyResult> krs = requests.stream()
                .map(req -> buildKeyResult(goal, req, tenantId))
                .collect(Collectors.toList());
        keyResultRepository.saveAll(krs);
    }

    private KeyResult buildKeyResult(Goal goal, KeyResultRequest request, String tenantId) {
        KeyResult kr = new KeyResult();
        kr.setGoal(goal);
        kr.setMetric(request.getMetric());
        kr.setDescription(request.getDescription());
        kr.setTargetValue(request.getTargetValue());
        kr.setCurrentValue(request.getCurrentValue() != null ? request.getCurrentValue() : BigDecimal.ZERO);
        kr.setUnitOfMeasure(request.getUnitOfMeasure());
        kr.setSortOrder(request.getSortOrder());
        kr.setTenantId(tenantId);
        if (request.getStatus() != null) {
            kr.setStatus(request.getStatus());
        }
        return kr;
    }
}
