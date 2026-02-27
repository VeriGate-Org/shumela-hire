package com.arthmatic.shumelahire.controller.goal;

import com.arthmatic.shumelahire.config.tenant.TenantContext;
import com.arthmatic.shumelahire.dto.goal.*;
import com.arthmatic.shumelahire.entity.goal.GoalStatus;
import com.arthmatic.shumelahire.entity.goal.OwnerType;
import com.arthmatic.shumelahire.service.goal.GoalManagementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/goals")
@Validated
public class GoalManagementController {

    @Autowired
    private GoalManagementService goalService;

    // ========== GOAL CRUD ==========

    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(
            @Valid @RequestBody GoalRequest request,
            @RequestHeader("X-User-Id") String userId) {

        try {
            String tenantId = TenantContext.requireCurrentTenant();
            GoalResponse goal = goalService.createGoal(request, tenantId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(goal);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<GoalResponse>> getGoals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        String tenantId = TenantContext.requireCurrentTenant();
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(goalService.getGoals(tenantId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getGoal(@PathVariable Long id) {

        String tenantId = TenantContext.requireCurrentTenant();
        Optional<GoalResponse> goal = goalService.getGoal(id, tenantId);
        return goal.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> updateGoal(
            @PathVariable Long id,
            @Valid @RequestBody GoalRequest request) {

        try {
            String tenantId = TenantContext.requireCurrentTenant();
            GoalResponse goal = goalService.updateGoal(id, request, tenantId);
            return ResponseEntity.ok(goal);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {

        try {
            String tenantId = TenantContext.requireCurrentTenant();
            goalService.deleteGoal(id, tenantId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== GOAL STATE TRANSITIONS ==========

    @PostMapping("/{id}/activate")
    public ResponseEntity<GoalResponse> activateGoal(@PathVariable Long id) {

        try {
            String tenantId = TenantContext.requireCurrentTenant();
            GoalResponse goal = goalService.activateGoal(id, tenantId);
            return ResponseEntity.ok(goal);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<GoalResponse> completeGoal(@PathVariable Long id) {

        try {
            String tenantId = TenantContext.requireCurrentTenant();
            GoalResponse goal = goalService.completeGoal(id, tenantId);
            return ResponseEntity.ok(goal);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<GoalResponse> cancelGoal(@PathVariable Long id) {

        try {
            String tenantId = TenantContext.requireCurrentTenant();
            GoalResponse goal = goalService.cancelGoal(id, tenantId);
            return ResponseEntity.ok(goal);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ========== GOAL QUERIES ==========

    @GetMapping("/owner/{ownerType}/{ownerId}")
    public ResponseEntity<List<GoalResponse>> getGoalsByOwner(
            @PathVariable String ownerType,
            @PathVariable String ownerId) {

        try {
            String tenantId = TenantContext.requireCurrentTenant();
            OwnerType type = OwnerType.valueOf(ownerType.toUpperCase());
            List<GoalResponse> goals = goalService.getGoalsByOwner(tenantId, type, ownerId);
            return ResponseEntity.ok(goals);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<GoalResponse>> getGoalsByStatus(@PathVariable String status) {

        try {
            String tenantId = TenantContext.requireCurrentTenant();
            GoalStatus goalStatus = GoalStatus.valueOf(status.toUpperCase());
            List<GoalResponse> goals = goalService.getGoalsByStatus(tenantId, goalStatus);
            return ResponseEntity.ok(goals);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/children")
    public ResponseEntity<List<GoalResponse>> getChildGoals(@PathVariable Long id) {

        try {
            String tenantId = TenantContext.requireCurrentTenant();
            List<GoalResponse> goals = goalService.getChildGoals(id, tenantId);
            return ResponseEntity.ok(goals);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/top-level")
    public ResponseEntity<List<GoalResponse>> getTopLevelGoals() {
        String tenantId = TenantContext.requireCurrentTenant();
        return ResponseEntity.ok(goalService.getTopLevelGoals(tenantId));
    }

    // ========== KEY RESULTS ==========

    @GetMapping("/{goalId}/key-results")
    public ResponseEntity<List<KeyResultResponse>> getKeyResults(@PathVariable Long goalId) {

        try {
            String tenantId = TenantContext.requireCurrentTenant();
            List<KeyResultResponse> krs = goalService.getKeyResults(goalId, tenantId);
            return ResponseEntity.ok(krs);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{goalId}/key-results")
    public ResponseEntity<KeyResultResponse> addKeyResult(
            @PathVariable Long goalId,
            @Valid @RequestBody KeyResultRequest request) {

        try {
            String tenantId = TenantContext.requireCurrentTenant();
            KeyResultResponse kr = goalService.addKeyResult(goalId, request, tenantId);
            return ResponseEntity.status(HttpStatus.CREATED).body(kr);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/key-results/{krId}/progress")
    public ResponseEntity<KeyResultResponse> updateKeyResultProgress(
            @PathVariable Long krId,
            @Valid @RequestBody ProgressUpdateRequest request) {

        try {
            String tenantId = TenantContext.requireCurrentTenant();
            KeyResultResponse kr = goalService.updateKeyResultProgress(krId, request, tenantId);
            return ResponseEntity.ok(kr);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/key-results/{krId}")
    public ResponseEntity<Void> deleteKeyResult(@PathVariable Long krId) {

        try {
            String tenantId = TenantContext.requireCurrentTenant();
            goalService.deleteKeyResult(krId, tenantId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== GOAL LINKS ==========

    @PostMapping("/{goalId}/link-cycle/{cycleId}")
    public ResponseEntity<GoalLinkResponse> linkGoalToCycle(
            @PathVariable Long goalId,
            @PathVariable Long cycleId,
            @Valid @RequestBody GoalLinkRequest request,
            @RequestHeader("X-User-Id") String userId) {

        try {
            String tenantId = TenantContext.requireCurrentTenant();
            GoalLinkResponse link = goalService.linkGoalToCycle(goalId, cycleId, request, tenantId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(link);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{goalId}/link-cycle/{cycleId}")
    public ResponseEntity<Void> unlinkGoalFromCycle(
            @PathVariable Long goalId,
            @PathVariable Long cycleId) {

        try {
            String tenantId = TenantContext.requireCurrentTenant();
            goalService.unlinkGoalFromCycle(goalId, cycleId, tenantId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{goalId}/links")
    public ResponseEntity<List<GoalLinkResponse>> getGoalLinks(@PathVariable Long goalId) {

        try {
            String tenantId = TenantContext.requireCurrentTenant();
            List<GoalLinkResponse> links = goalService.getGoalLinks(goalId, tenantId);
            return ResponseEntity.ok(links);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/linked-to-cycle/{cycleId}")
    public ResponseEntity<List<GoalLinkResponse>> getGoalsLinkedToCycle(@PathVariable Long cycleId) {
        String tenantId = TenantContext.requireCurrentTenant();
        return ResponseEntity.ok(goalService.getGoalsLinkedToCycle(cycleId, tenantId));
    }
}
