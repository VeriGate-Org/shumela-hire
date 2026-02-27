package com.arthmatic.shumelahire.repository.performance;

import com.arthmatic.shumelahire.entity.performance.PIPMilestone;
import com.arthmatic.shumelahire.entity.performance.PIPMilestoneStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PIPMilestoneRepository extends JpaRepository<PIPMilestone, Long> {

    List<PIPMilestone> findByPipIdOrderBySortOrderAsc(Long pipId);

    Optional<PIPMilestone> findByIdAndTenantId(Long id, String tenantId);

    List<PIPMilestone> findByPipIdAndStatus(Long pipId, PIPMilestoneStatus status);

    long countByPipIdAndStatus(Long pipId, PIPMilestoneStatus status);
}
