package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.HeadcountPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HeadcountPlanRepository extends JpaRepository<HeadcountPlan, Long> {

    List<HeadcountPlan> findByFiscalYear(Integer fiscalYear);

    List<HeadcountPlan> findByDepartment(String department);

    Optional<HeadcountPlan> findByDepartmentAndFiscalYearAndTenantId(
            String department, Integer fiscalYear, String tenantId);

    @Query("SELECT h FROM HeadcountPlan h WHERE h.fiscalYear = :year ORDER BY h.department")
    List<HeadcountPlan> findByFiscalYearOrdered(@Param("year") Integer year);

    @Query("SELECT SUM(h.plannedHeadcount) FROM HeadcountPlan h WHERE h.fiscalYear = :year")
    Long sumPlannedHeadcountByYear(@Param("year") Integer year);

    @Query("SELECT SUM(h.currentHeadcount) FROM HeadcountPlan h WHERE h.fiscalYear = :year")
    Long sumCurrentHeadcountByYear(@Param("year") Integer year);

    @Query("SELECT SUM(h.budget) FROM HeadcountPlan h WHERE h.fiscalYear = :year")
    java.math.BigDecimal sumBudgetByYear(@Param("year") Integer year);

    @Query("SELECT DISTINCT h.fiscalYear FROM HeadcountPlan h ORDER BY h.fiscalYear DESC")
    List<Integer> findDistinctFiscalYears();
}
