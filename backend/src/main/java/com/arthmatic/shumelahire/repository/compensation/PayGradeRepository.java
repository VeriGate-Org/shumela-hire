package com.arthmatic.shumelahire.repository.compensation;

import com.arthmatic.shumelahire.entity.compensation.PayGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayGradeRepository extends JpaRepository<PayGrade, Long> {

    Optional<PayGrade> findByCodeAndTenantId(String code, String tenantId);

    List<PayGrade> findByTenantIdAndIsActive(String tenantId, Boolean isActive);

    List<PayGrade> findByTenantId(String tenantId);

    boolean existsByCodeAndTenantId(String code, String tenantId);

    @Query("SELECT pg FROM PayGrade pg WHERE pg.tenantId = :tenantId AND pg.isActive = true ORDER BY pg.code ASC")
    List<PayGrade> findActiveByTenantId(@Param("tenantId") String tenantId);
}
