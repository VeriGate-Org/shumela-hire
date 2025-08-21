package com.example.recruitment.repository;

import com.example.recruitment.entity.ScreeningAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScreeningAnswerRepository extends JpaRepository<ScreeningAnswer, Long> {
    
    List<ScreeningAnswer> findByApplicationId(Long applicationId);
    
    Optional<ScreeningAnswer> findByApplicationIdAndScreeningQuestionId(Long applicationId, Long screeningQuestionId);
    
    @Query("SELECT sa FROM ScreeningAnswer sa " +
           "JOIN sa.screeningQuestion sq " +
           "WHERE sa.applicationId = :applicationId " +
           "ORDER BY sq.displayOrder ASC")
    List<ScreeningAnswer> findByApplicationIdOrderedByQuestionDisplay(@Param("applicationId") Long applicationId);
    
    @Query("SELECT COUNT(sa) FROM ScreeningAnswer sa " +
           "JOIN sa.screeningQuestion sq " +
           "WHERE sa.applicationId = :applicationId " +
           "AND sq.isRequired = true " +
           "AND (sa.answerValue IS NULL OR sa.answerValue = '' OR sa.answerValue = ' ')")
    Long countMissingRequiredAnswersByApplicationId(@Param("applicationId") Long applicationId);
    
    @Query("SELECT COUNT(sa) FROM ScreeningAnswer sa " +
           "WHERE sa.applicationId = :applicationId " +
           "AND sa.isValid = false")
    Long countInvalidAnswersByApplicationId(@Param("applicationId") Long applicationId);
    
    @Query("SELECT COUNT(sa) FROM ScreeningAnswer sa " +
           "JOIN sa.screeningQuestion sq " +
           "WHERE sa.applicationId = :applicationId " +
           "AND sq.isRequired = true")
    Long countRequiredAnswersByApplicationId(@Param("applicationId") Long applicationId);
    
    @Query("SELECT COUNT(sa) FROM ScreeningAnswer sa " +
           "WHERE sa.applicationId = :applicationId")
    Long countTotalAnswersByApplicationId(@Param("applicationId") Long applicationId);
    
    @Query("SELECT sa FROM ScreeningAnswer sa " +
           "JOIN sa.screeningQuestion sq " +
           "WHERE sq.jobPostingId = :jobPostingId " +
           "AND sa.isValid = true")
    List<ScreeningAnswer> findValidAnswersByJobPostingId(@Param("jobPostingId") Long jobPostingId);
    
    void deleteByApplicationId(Long applicationId);
    
    boolean existsByApplicationIdAndScreeningQuestionId(Long applicationId, Long screeningQuestionId);
}