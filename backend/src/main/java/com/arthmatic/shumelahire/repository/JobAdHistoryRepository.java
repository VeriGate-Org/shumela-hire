package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.JobAdHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobAdHistoryRepository extends JpaRepository<JobAdHistory, Long> {
    
    // Find history for a specific job ad
    List<JobAdHistory> findByJobAdIdOrderByTimestampDesc(Long jobAdId);
    Page<JobAdHistory> findByJobAdIdOrderByTimestampDesc(Long jobAdId, Pageable pageable);
    
    // Find history by action
    List<JobAdHistory> findByAction(String action);
    
    // Find history by actor
    List<JobAdHistory> findByActorUserId(String actorUserId);
    Page<JobAdHistory> findByActorUserId(String actorUserId, Pageable pageable);
    
    // Find history within date range
    @Query("SELECT h FROM JobAdHistory h WHERE h.timestamp BETWEEN :startDate AND :endDate ORDER BY h.timestamp DESC")
    List<JobAdHistory> findByTimestampBetween(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    // Find recent history
    @Query("SELECT h FROM JobAdHistory h ORDER BY h.timestamp DESC")
    List<JobAdHistory> findRecentHistory(Pageable pageable);
    
    // Count actions by type
    long countByAction(String action);
    
    // Find history for multiple job ads
    @Query("SELECT h FROM JobAdHistory h WHERE h.jobAd.id IN :jobAdIds ORDER BY h.timestamp DESC")
    List<JobAdHistory> findByJobAdIds(@Param("jobAdIds") List<Long> jobAdIds);
    
    // Find history with filters
    @Query("SELECT h FROM JobAdHistory h WHERE " +
           "(:jobAdId IS NULL OR h.jobAd.id = :jobAdId) AND " +
           "(:action IS NULL OR h.action = :action) AND " +
           "(:actorUserId IS NULL OR h.actorUserId = :actorUserId) " +
           "ORDER BY h.timestamp DESC")
    Page<JobAdHistory> findWithFilters(
        @Param("jobAdId") Long jobAdId,
        @Param("action") String action,
        @Param("actorUserId") String actorUserId,
        Pageable pageable
    );
}