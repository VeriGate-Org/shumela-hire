package com.arthmatic.shumelahire.repository.engagement;

import com.arthmatic.shumelahire.entity.engagement.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {

    List<SurveyResponse> findBySurveyId(Long surveyId);

    List<SurveyResponse> findByQuestionId(Long questionId);

    @Query("SELECT COUNT(DISTINCT sr.anonymousToken) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.anonymousToken IS NOT NULL")
    Long countDistinctRespondentsBySurveyId(@Param("surveyId") Long surveyId);

    @Query("SELECT AVG(sr.ratingValue) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.ratingValue IS NOT NULL")
    Double getAverageRatingBySurveyId(@Param("surveyId") Long surveyId);

    @Query("SELECT AVG(sr.ratingValue) FROM SurveyResponse sr WHERE sr.question.id = :questionId AND sr.ratingValue IS NOT NULL")
    Double getAverageRatingByQuestionId(@Param("questionId") Long questionId);

    boolean existsBySurveyIdAndAnonymousToken(Long surveyId, String anonymousToken);
}
