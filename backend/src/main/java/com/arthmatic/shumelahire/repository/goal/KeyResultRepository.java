package com.arthmatic.shumelahire.repository.goal;

import com.arthmatic.shumelahire.entity.goal.KeyResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KeyResultRepository extends JpaRepository<KeyResult, Long> {

    List<KeyResult> findByGoalIdOrderBySortOrderAscCreatedAtAsc(Long goalId);

    Optional<KeyResult> findByIdAndGoalTenantId(Long id, String tenantId);
}
