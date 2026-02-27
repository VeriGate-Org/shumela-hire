package com.arthmatic.shumelahire.repository.engagement;

import com.arthmatic.shumelahire.entity.engagement.Recognition;
import com.arthmatic.shumelahire.entity.engagement.RecognitionBadge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecognitionRepository extends JpaRepository<Recognition, Long> {

    List<Recognition> findByReceiverId(Long receiverId);

    List<Recognition> findByGiverId(Long giverId);

    @Query("SELECT r FROM Recognition r WHERE r.tenantId = :tenantId AND r.isPublic = true ORDER BY r.createdAt DESC")
    Page<Recognition> findPublicByTenantId(@Param("tenantId") String tenantId, Pageable pageable);

    @Query("SELECT SUM(r.points) FROM Recognition r WHERE r.receiver.id = :employeeId")
    Long getTotalPointsByReceiverId(@Param("employeeId") Long employeeId);

    @Query("SELECT r.badge, COUNT(r) FROM Recognition r WHERE r.receiver.id = :employeeId GROUP BY r.badge")
    List<Object[]> getBadgeCountsByReceiverId(@Param("employeeId") Long employeeId);

    @Query("SELECT COUNT(r) FROM Recognition r WHERE r.tenantId = :tenantId AND r.createdAt >= :since")
    Long countByTenantIdSince(@Param("tenantId") String tenantId, @Param("since") LocalDateTime since);

    @Query("SELECT r.receiver.id, SUM(r.points) as totalPoints FROM Recognition r WHERE r.tenantId = :tenantId AND r.createdAt >= :since GROUP BY r.receiver.id ORDER BY totalPoints DESC")
    List<Object[]> getTopReceivers(@Param("tenantId") String tenantId, @Param("since") LocalDateTime since, Pageable pageable);
}
