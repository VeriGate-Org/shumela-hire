package com.arthmatic.shumelahire.repository.compensation;

import com.arthmatic.shumelahire.entity.compensation.Benefit;
import com.arthmatic.shumelahire.entity.compensation.BenefitType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BenefitRepository extends JpaRepository<Benefit, Long> {

    List<Benefit> findByEmployeeId(Long employeeId);

    List<Benefit> findByTenantId(String tenantId);

    List<Benefit> findByEmployeeIdAndIsActive(Long employeeId, Boolean isActive);

    List<Benefit> findByEmployeeIdAndBenefitType(Long employeeId, BenefitType benefitType);

    @Query("SELECT b FROM Benefit b WHERE b.tenantId = :tenantId AND b.isActive = true")
    List<Benefit> findActiveBenefitsByTenantId(@Param("tenantId") String tenantId);

    @Query("SELECT b FROM Benefit b WHERE b.employee.id = :employeeId AND b.isActive = true ORDER BY b.benefitType ASC")
    List<Benefit> findActiveByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT COUNT(b) FROM Benefit b WHERE b.tenantId = :tenantId AND b.benefitType = :benefitType AND b.isActive = true")
    Long countActiveByTenantIdAndBenefitType(@Param("tenantId") String tenantId, @Param("benefitType") BenefitType benefitType);
}
