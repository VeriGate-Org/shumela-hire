package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.ADSyncLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ADSyncLogRepository extends JpaRepository<ADSyncLog, Long> {

    Page<ADSyncLog> findAllByOrderByStartedAtDesc(Pageable pageable);

    @Query("SELECT l FROM ADSyncLog l WHERE l.status = 'IN_PROGRESS' ORDER BY l.startedAt DESC")
    Optional<ADSyncLog> findActiveSyncInProgress();

    Optional<ADSyncLog> findTopByOrderByStartedAtDesc();
}
