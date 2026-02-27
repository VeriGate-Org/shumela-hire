package com.arthmatic.shumelahire.repository.engagement;

import com.arthmatic.shumelahire.entity.engagement.Survey;
import com.arthmatic.shumelahire.entity.engagement.SurveyStatus;
import com.arthmatic.shumelahire.entity.engagement.SurveyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {

    List<Survey> findByStatus(SurveyStatus status);

    List<Survey> findBySurveyType(SurveyType surveyType);

    @Query("SELECT s FROM Survey s WHERE s.tenantId = :tenantId ORDER BY s.createdAt DESC")
    List<Survey> findByTenantId(@Param("tenantId") String tenantId);

    @Query("SELECT s FROM Survey s WHERE s.tenantId = :tenantId AND s.status = :status ORDER BY s.createdAt DESC")
    List<Survey> findByTenantIdAndStatus(@Param("tenantId") String tenantId, @Param("status") SurveyStatus status);

    @Query("SELECT COUNT(s) FROM Survey s WHERE s.tenantId = :tenantId AND s.status = :status")
    Long countByTenantIdAndStatus(@Param("tenantId") String tenantId, @Param("status") SurveyStatus status);
}
