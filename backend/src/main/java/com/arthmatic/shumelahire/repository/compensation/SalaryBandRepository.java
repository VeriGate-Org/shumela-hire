package com.arthmatic.shumelahire.repository.compensation;

import com.arthmatic.shumelahire.entity.compensation.SalaryBand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaryBandRepository extends JpaRepository<SalaryBand, Long> {

    List<SalaryBand> findByPayGradeId(Long payGradeId);

    List<SalaryBand> findByTenantId(String tenantId);

    List<SalaryBand> findByTenantIdAndIsActive(String tenantId, Boolean isActive);

    @Query("SELECT sb FROM SalaryBand sb WHERE sb.tenantId = :tenantId AND sb.isActive = true AND sb.jobFamily = :jobFamily")
    List<SalaryBand> findActiveByTenantIdAndJobFamily(@Param("tenantId") String tenantId, @Param("jobFamily") String jobFamily);

    @Query("SELECT sb FROM SalaryBand sb WHERE sb.payGrade.id = :payGradeId AND sb.isActive = true")
    List<SalaryBand> findActiveByPayGradeId(@Param("payGradeId") Long payGradeId);
}
