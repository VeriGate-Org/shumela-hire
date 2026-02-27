package com.arthmatic.shumelahire.repository.compensation;

import com.arthmatic.shumelahire.entity.compensation.TotalRewardsStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TotalRewardsStatementRepository extends JpaRepository<TotalRewardsStatement, Long> {

    List<TotalRewardsStatement> findByEmployeeId(Long employeeId);

    List<TotalRewardsStatement> findByTenantId(String tenantId);

    @Query("SELECT trs FROM TotalRewardsStatement trs WHERE trs.employee.id = :employeeId ORDER BY trs.statementDate DESC")
    List<TotalRewardsStatement> findByEmployeeIdOrderByStatementDateDesc(@Param("employeeId") Long employeeId);

    @Query("SELECT trs FROM TotalRewardsStatement trs WHERE trs.employee.id = :employeeId AND trs.periodStart >= :startDate AND trs.periodEnd <= :endDate")
    List<TotalRewardsStatement> findByEmployeeIdAndPeriod(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    Optional<TotalRewardsStatement> findTopByEmployeeIdOrderByStatementDateDesc(Long employeeId);
}
