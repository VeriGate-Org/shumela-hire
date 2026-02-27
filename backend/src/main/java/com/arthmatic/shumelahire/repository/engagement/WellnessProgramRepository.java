package com.arthmatic.shumelahire.repository.engagement;

import com.arthmatic.shumelahire.entity.engagement.WellnessCategory;
import com.arthmatic.shumelahire.entity.engagement.WellnessProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WellnessProgramRepository extends JpaRepository<WellnessProgram, Long> {

    List<WellnessProgram> findByTenantIdAndIsActive(String tenantId, Boolean isActive);

    List<WellnessProgram> findByCategory(WellnessCategory category);

    @Query("SELECT wp FROM WellnessProgram wp WHERE wp.tenantId = :tenantId ORDER BY wp.createdAt DESC")
    List<WellnessProgram> findByTenantId(@Param("tenantId") String tenantId);
}
