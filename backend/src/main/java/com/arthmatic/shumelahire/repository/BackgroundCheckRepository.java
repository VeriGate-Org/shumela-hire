package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.BackgroundCheck;
import com.arthmatic.shumelahire.entity.BackgroundCheckStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BackgroundCheckRepository extends JpaRepository<BackgroundCheck, Long> {

    List<BackgroundCheck> findByApplicationIdOrderByCreatedAtDesc(Long applicationId);

    Optional<BackgroundCheck> findByReferenceId(String referenceId);

    Optional<BackgroundCheck> findByExternalScreeningId(String externalScreeningId);

    List<BackgroundCheck> findByStatusOrderByCreatedAtDesc(BackgroundCheckStatus status);

    List<BackgroundCheck> findByStatusInOrderByCreatedAtDesc(List<BackgroundCheckStatus> statuses);

    @Query("SELECT bc FROM BackgroundCheck bc WHERE bc.status IN :statuses AND bc.createdAt < :cutoff ORDER BY bc.createdAt ASC")
    List<BackgroundCheck> findPendingOlderThan(
            @Param("statuses") List<BackgroundCheckStatus> statuses,
            @Param("cutoff") LocalDateTime cutoff);

    @Query("SELECT bc FROM BackgroundCheck bc WHERE bc.application.id = :applicationId AND bc.status = 'COMPLETED' ORDER BY bc.completedAt DESC")
    List<BackgroundCheck> findCompletedByApplicationId(@Param("applicationId") Long applicationId);

    @Query("SELECT COUNT(bc) FROM BackgroundCheck bc WHERE bc.status = :status")
    long countByStatus(@Param("status") BackgroundCheckStatus status);

    @Query("SELECT bc FROM BackgroundCheck bc WHERE bc.initiatedBy = :userId ORDER BY bc.createdAt DESC")
    List<BackgroundCheck> findByInitiatedBy(@Param("userId") Long userId);
}
