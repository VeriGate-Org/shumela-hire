package com.arthmatic.shumelahire.repository.goal;

import com.arthmatic.shumelahire.entity.goal.GoalLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoalLinkRepository extends JpaRepository<GoalLink, Long> {

    List<GoalLink> findByGoalIdAndTenantId(Long goalId, String tenantId);

    List<GoalLink> findByReviewCycleIdAndTenantId(Long reviewCycleId, String tenantId);

    Optional<GoalLink> findByGoalIdAndReviewCycleId(Long goalId, Long reviewCycleId);

    boolean existsByGoalIdAndReviewCycleId(Long goalId, Long reviewCycleId);
}
