package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.LeaveDelegation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveDelegationRepository extends JpaRepository<LeaveDelegation, Long> {

    List<LeaveDelegation> findByDelegatorIdAndActiveTrue(Long delegatorId);

    List<LeaveDelegation> findByDelegateIdAndActiveTrue(Long delegateId);

    @Query("SELECT ld FROM LeaveDelegation ld WHERE ld.delegate.id = :delegateId " +
           "AND ld.active = true " +
           "AND ld.canApproveLeave = true " +
           "AND ld.startDate <= :date AND ld.endDate >= :date")
    List<LeaveDelegation> findActiveDelegationsForApproval(
            @Param("delegateId") Long delegateId,
            @Param("date") LocalDate date);

    @Query("SELECT ld FROM LeaveDelegation ld WHERE ld.delegator.id = :delegatorId " +
           "AND ld.delegate.id = :delegateId " +
           "AND ld.active = true " +
           "AND ld.startDate <= :endDate AND ld.endDate >= :startDate")
    Optional<LeaveDelegation> findOverlappingDelegation(
            @Param("delegatorId") Long delegatorId,
            @Param("delegateId") Long delegateId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
