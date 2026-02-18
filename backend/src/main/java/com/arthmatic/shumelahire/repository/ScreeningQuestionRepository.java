package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.ScreeningQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScreeningQuestionRepository extends JpaRepository<ScreeningQuestion, Long> {
    
    List<ScreeningQuestion> findByJobPostingIdAndIsActiveTrue(Long jobPostingId);
    
    List<ScreeningQuestion> findByJobPostingIdAndIsActiveTrueOrderByDisplayOrder(Long jobPostingId);
    
    List<ScreeningQuestion> findByJobPostingId(Long jobPostingId);
    
    @Query("SELECT sq FROM ScreeningQuestion sq WHERE sq.jobPostingId = :jobPostingId " +
           "AND sq.isActive = true ORDER BY sq.displayOrder ASC")
    List<ScreeningQuestion> findActiveQuestionsByJobPostingIdOrderedByDisplay(@Param("jobPostingId") Long jobPostingId);
    
    @Query("SELECT COUNT(sq) FROM ScreeningQuestion sq WHERE sq.jobPostingId = :jobPostingId " +
           "AND sq.isActive = true AND sq.isRequired = true")
    Long countRequiredQuestionsByJobPostingId(@Param("jobPostingId") Long jobPostingId);
    
    @Query("SELECT COUNT(sq) FROM ScreeningQuestion sq WHERE sq.jobPostingId = :jobPostingId " +
           "AND sq.isActive = true")
    Long countActiveQuestionsByJobPostingId(@Param("jobPostingId") Long jobPostingId);
    
    boolean existsByJobPostingIdAndQuestionTextAndIsActiveTrue(Long jobPostingId, String questionText);
    
    @Query("SELECT MAX(sq.displayOrder) FROM ScreeningQuestion sq WHERE sq.jobPostingId = :jobPostingId")
    Integer findMaxDisplayOrderByJobPostingId(@Param("jobPostingId") Long jobPostingId);
}