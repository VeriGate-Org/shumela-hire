package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.WorkflowExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowExecutionRepository extends JpaRepository<WorkflowExecution, Long> {

    List<WorkflowExecution> findByWorkflowDefinitionIdOrderByStartedAtDesc(Long workflowDefinitionId);

    List<WorkflowExecution> findByStatusOrderByStartedAtDesc(String status);
}
