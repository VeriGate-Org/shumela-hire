package com.arthmatic.shumelahire.repository.goal;

import com.arthmatic.shumelahire.entity.goal.Goal;
import com.arthmatic.shumelahire.entity.goal.GoalStatus;
import com.arthmatic.shumelahire.entity.goal.GoalType;
import com.arthmatic.shumelahire.entity.goal.OwnerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    Page<Goal> findByTenantIdAndIsActiveOrderByCreatedAtDesc(String tenantId, Boolean isActive, Pageable pageable);

    Optional<Goal> findByIdAndTenantId(Long id, String tenantId);

    List<Goal> findByTenantIdAndOwnerTypeAndOwnerIdAndIsActiveOrderBySortOrderAscCreatedAtDesc(
            String tenantId, OwnerType ownerType, String ownerId, Boolean isActive);

    List<Goal> findByTenantIdAndStatusAndIsActiveOrderByCreatedAtDesc(
            String tenantId, GoalStatus status, Boolean isActive);

    List<Goal> findByTenantIdAndTypeAndIsActiveOrderByCreatedAtDesc(
            String tenantId, GoalType type, Boolean isActive);

    List<Goal> findByParentGoalIdAndTenantIdAndIsActiveOrderBySortOrderAsc(
            Long parentGoalId, String tenantId, Boolean isActive);

    List<Goal> findByParentGoalIsNullAndTenantIdAndIsActiveOrderBySortOrderAscCreatedAtDesc(
            String tenantId, Boolean isActive);
}
