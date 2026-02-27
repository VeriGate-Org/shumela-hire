package com.arthmatic.shumelahire.repository.compensation;

import com.arthmatic.shumelahire.entity.compensation.CompensationReview;
import com.arthmatic.shumelahire.entity.compensation.ReviewStatus;
import com.arthmatic.shumelahire.entity.compensation.ReviewType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CompensationReviewRepository extends JpaRepository<CompensationReview, Long> {

    List<CompensationReview> findByEmployeeId(Long employeeId);

    List<CompensationReview> findByStatus(ReviewStatus status);

    List<CompensationReview> findByTenantId(String tenantId);

    Page<CompensationReview> findByTenantId(String tenantId, Pageable pageable);

    @Query("SELECT cr FROM CompensationReview cr WHERE cr.tenantId = :tenantId AND cr.status = :status")
    List<CompensationReview> findByTenantIdAndStatus(@Param("tenantId") String tenantId, @Param("status") ReviewStatus status);

    @Query("SELECT cr FROM CompensationReview cr WHERE cr.employee.id = :employeeId AND cr.reviewType = :reviewType ORDER BY cr.createdAt DESC")
    List<CompensationReview> findByEmployeeIdAndReviewType(@Param("employeeId") Long employeeId, @Param("reviewType") ReviewType reviewType);

    @Query("SELECT cr FROM CompensationReview cr WHERE cr.tenantId = :tenantId AND cr.effectiveDate BETWEEN :startDate AND :endDate")
    List<CompensationReview> findByTenantIdAndEffectiveDateBetween(
            @Param("tenantId") String tenantId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(cr) FROM CompensationReview cr WHERE cr.tenantId = :tenantId AND cr.status = :status")
    Long countByTenantIdAndStatus(@Param("tenantId") String tenantId, @Param("status") ReviewStatus status);
}
