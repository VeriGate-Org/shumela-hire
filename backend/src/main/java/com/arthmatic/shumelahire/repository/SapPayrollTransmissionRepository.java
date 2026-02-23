package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.SapPayrollTransmission;
import com.arthmatic.shumelahire.entity.TransmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SapPayrollTransmissionRepository extends JpaRepository<SapPayrollTransmission, Long> {

    Optional<SapPayrollTransmission> findByTransmissionId(String transmissionId);

    List<SapPayrollTransmission> findByOfferIdOrderByCreatedAtDesc(Long offerId);

    List<SapPayrollTransmission> findByStatus(TransmissionStatus status);

    @Query("SELECT t FROM SapPayrollTransmission t WHERE t.status IN :statuses ORDER BY t.createdAt DESC")
    List<SapPayrollTransmission> findByStatusIn(@Param("statuses") List<TransmissionStatus> statuses);

    @Query("SELECT t FROM SapPayrollTransmission t WHERE t.status IN ('FAILED', 'RETRY_PENDING') " +
           "AND t.retryCount < t.maxRetries " +
           "AND (t.nextRetryAt IS NULL OR t.nextRetryAt <= :now) " +
           "ORDER BY t.createdAt ASC")
    List<SapPayrollTransmission> findRetryable(@Param("now") LocalDateTime now);

    @Query("SELECT t FROM SapPayrollTransmission t WHERE t.status = 'PENDING' " +
           "ORDER BY t.createdAt ASC")
    List<SapPayrollTransmission> findPending();

    @Query("SELECT t FROM SapPayrollTransmission t WHERE t.status = 'TRANSMITTED' " +
           "AND t.transmittedAt < :cutoff " +
           "ORDER BY t.transmittedAt ASC")
    List<SapPayrollTransmission> findStaleTransmissions(@Param("cutoff") LocalDateTime cutoff);

    Optional<SapPayrollTransmission> findBySapEmployeeNumber(String sapEmployeeNumber);

    long countByStatus(TransmissionStatus status);

    List<SapPayrollTransmission> findByInitiatedByOrderByCreatedAtDesc(Long userId);
}
