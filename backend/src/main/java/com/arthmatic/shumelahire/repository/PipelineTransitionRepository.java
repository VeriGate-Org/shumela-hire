package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.PipelineTransition;
import com.arthmatic.shumelahire.entity.PipelineStage;
import com.arthmatic.shumelahire.entity.TransitionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PipelineTransitionRepository extends JpaRepository<PipelineTransition, Long> {

    // Basic queries
    List<PipelineTransition> findByApplicationId(Long applicationId);
    
    List<PipelineTransition> findByApplicationIdOrderByCreatedAtDesc(Long applicationId);
    
    List<PipelineTransition> findByToStage(PipelineStage toStage);
    
    List<PipelineTransition> findByFromStage(PipelineStage fromStage);
    
    List<PipelineTransition> findByTransitionType(TransitionType transitionType);

    // Latest transition for application
    @Query("SELECT pt FROM PipelineTransition pt WHERE pt.application.id = :applicationId " +
           "ORDER BY pt.createdAt DESC")
    Optional<PipelineTransition> findLatestTransitionByApplicationId(@Param("applicationId") Long applicationId);

    // Transition timeline
    @Query("SELECT pt FROM PipelineTransition pt WHERE pt.application.id = :applicationId " +
           "ORDER BY pt.effectiveAt ASC")
    List<PipelineTransition> findTransitionTimelineByApplicationId(@Param("applicationId") Long applicationId);

    // Stage duration analytics
    @Query("SELECT pt.toStage, AVG(pt.durationInPreviousStageHours) FROM PipelineTransition pt " +
           "WHERE pt.durationInPreviousStageHours IS NOT NULL " +
           "AND pt.createdAt >= :startDate AND pt.createdAt < :endDate " +
           "GROUP BY pt.toStage")
    List<Object[]> getAverageStageDurations(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    // Conversion rates between stages
    @Query("SELECT pt.fromStage, pt.toStage, COUNT(pt) FROM PipelineTransition pt " +
           "WHERE pt.fromStage IS NOT NULL " +
           "AND pt.createdAt >= :startDate AND pt.createdAt < :endDate " +
           "GROUP BY pt.fromStage, pt.toStage")
    List<Object[]> getStageConversionRates(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    // Pipeline funnel data
    @Query("SELECT pt.toStage, COUNT(DISTINCT pt.application.id) FROM PipelineTransition pt " +
           "WHERE pt.createdAt >= :startDate AND pt.createdAt < :endDate " +
           "GROUP BY pt.toStage " +
           "ORDER BY pt.toStage")
    List<Object[]> getPipelineFunnelData(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    // Transition velocity (transitions per day)
    @Query("SELECT DATE(pt.createdAt), COUNT(pt) FROM PipelineTransition pt " +
           "WHERE pt.createdAt >= :startDate AND pt.createdAt < :endDate " +
           "GROUP BY DATE(pt.createdAt) " +
           "ORDER BY DATE(pt.createdAt)")
    List<Object[]> getTransitionVelocity(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    // Automated vs manual transitions
    @Query("SELECT pt.automated, COUNT(pt) FROM PipelineTransition pt " +
           "WHERE pt.createdAt >= :startDate AND pt.createdAt < :endDate " +
           "GROUP BY pt.automated")
    List<Object[]> getAutomationStatistics(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    // Rejection analysis
    @Query("SELECT pt.toStage, pt.reason, COUNT(pt) FROM PipelineTransition pt " +
           "WHERE pt.transitionType = 'REJECTION' " +
           "AND pt.createdAt >= :startDate AND pt.createdAt < :endDate " +
           "GROUP BY pt.toStage, pt.reason")
    List<Object[]> getRejectionAnalysis(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    // Withdrawal analysis
    @Query("SELECT pt.fromStage, pt.reason, COUNT(pt) FROM PipelineTransition pt " +
           "WHERE pt.transitionType = 'WITHDRAWAL' " +
           "AND pt.createdAt >= :startDate AND pt.createdAt < :endDate " +
           "GROUP BY pt.fromStage, pt.reason")
    List<Object[]> getWithdrawalAnalysis(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    // User activity analysis
    @Query("SELECT pt.createdBy, COUNT(pt) FROM PipelineTransition pt " +
           "WHERE pt.createdAt >= :startDate AND pt.createdAt < :endDate " +
           "GROUP BY pt.createdBy " +
           "ORDER BY COUNT(pt) DESC")
    List<Object[]> getUserActivityStatistics(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    // Time-based queries
    @Query("SELECT pt FROM PipelineTransition pt " +
           "WHERE pt.createdAt >= :startDate AND pt.createdAt < :endDate " +
           "ORDER BY pt.createdAt DESC")
    Page<PipelineTransition> findTransitionsByDateRange(@Param("startDate") LocalDateTime startDate,
                                                       @Param("endDate") LocalDateTime endDate,
                                                       Pageable pageable);

    // Interview-triggered transitions
    List<PipelineTransition> findByTriggeredByInterviewId(Long interviewId);

    // Assessment-triggered transitions
    List<PipelineTransition> findByTriggeredByAssessmentId(Long assessmentId);

    // Applications stuck in stages
    @Query("SELECT DISTINCT pt.application FROM PipelineTransition pt " +
           "WHERE pt.toStage = :stage " +
           "AND pt.createdAt < :cutoffDate " +
           "AND NOT EXISTS (SELECT pt2 FROM PipelineTransition pt2 " +
           "                WHERE pt2.application = pt.application " +
           "                AND pt2.createdAt > pt.createdAt)")
    List<com.arthmatic.shumelahire.entity.Application> findApplicationsStuckInStage(
            @Param("stage") PipelineStage stage,
            @Param("cutoffDate") LocalDateTime cutoffDate);

    // Bottleneck analysis
    @Query("SELECT pt.toStage, AVG(pt.durationInPreviousStageHours), COUNT(pt) FROM PipelineTransition pt " +
           "WHERE pt.durationInPreviousStageHours IS NOT NULL " +
           "AND pt.durationInPreviousStageHours > :threshold " +
           "AND pt.createdAt >= :startDate AND pt.createdAt < :endDate " +
           "GROUP BY pt.toStage " +
           "ORDER BY AVG(pt.durationInPreviousStageHours) DESC")
    List<Object[]> identifyBottlenecks(@Param("threshold") Long thresholdHours,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    // Regression analysis
    @Query("SELECT pt FROM PipelineTransition pt " +
           "WHERE pt.transitionType = 'REGRESSION' " +
           "AND pt.createdAt >= :startDate AND pt.createdAt < :endDate " +
           "ORDER BY pt.createdAt DESC")
    List<PipelineTransition> findRegressions(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    // Success rate by stage
    @Query("SELECT pt.fromStage, " +
           "COUNT(CASE WHEN pt.toStage IN ('OFFER_ACCEPTED', 'HIRED') THEN 1 END) as successful, " +
           "COUNT(pt) as total " +
           "FROM PipelineTransition pt " +
           "WHERE pt.fromStage IS NOT NULL " +
           "AND pt.createdAt >= :startDate AND pt.createdAt < :endDate " +
           "GROUP BY pt.fromStage")
    List<Object[]> getSuccessRatesByStage(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    // Recent activity
    @Query("SELECT pt FROM PipelineTransition pt " +
           "WHERE pt.createdAt >= :since " +
           "ORDER BY pt.createdAt DESC")
    List<PipelineTransition> findRecentActivity(@Param("since") LocalDateTime since, Pageable pageable);

    // Job posting pipeline analytics
    @Query("SELECT pt.application.jobPosting.id, pt.toStage, COUNT(pt) " +
           "FROM PipelineTransition pt " +
           "WHERE pt.application.jobPosting.id = :jobPostingId " +
           "AND pt.createdAt >= :startDate AND pt.createdAt < :endDate " +
           "GROUP BY pt.application.jobPosting.id, pt.toStage")
    List<Object[]> getJobPostingPipelineStats(@Param("jobPostingId") Long jobPostingId,
                                            @Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    // Department pipeline analytics
    @Query("SELECT pt.application.jobPosting.department, pt.toStage, COUNT(pt) " +
           "FROM PipelineTransition pt " +
           "WHERE pt.createdAt >= :startDate AND pt.createdAt < :endDate " +
           "GROUP BY pt.application.jobPosting.department, pt.toStage")
    List<Object[]> getDepartmentPipelineStats(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    // Count transitions by type
    @Query("SELECT pt.transitionType, COUNT(pt) FROM PipelineTransition pt " +
           "WHERE pt.createdAt >= :startDate AND pt.createdAt < :endDate " +
           "GROUP BY pt.transitionType")
    List<Object[]> getTransitionTypeStatistics(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);
}