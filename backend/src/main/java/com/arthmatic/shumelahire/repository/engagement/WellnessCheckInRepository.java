package com.arthmatic.shumelahire.repository.engagement;

import com.arthmatic.shumelahire.entity.engagement.MoodRating;
import com.arthmatic.shumelahire.entity.engagement.WellnessCheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WellnessCheckInRepository extends JpaRepository<WellnessCheckIn, Long> {

    List<WellnessCheckIn> findByEmployeeIdOrderByCheckInDateDesc(Long employeeId);

    @Query("SELECT wc FROM WellnessCheckIn wc WHERE wc.employee.id = :employeeId AND wc.checkInDate BETWEEN :startDate AND :endDate ORDER BY wc.checkInDate DESC")
    List<WellnessCheckIn> findByEmployeeIdAndDateRange(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT wc.moodRating, COUNT(wc) FROM WellnessCheckIn wc WHERE wc.tenantId = :tenantId AND wc.checkInDate >= :since GROUP BY wc.moodRating")
    List<Object[]> getMoodDistribution(@Param("tenantId") String tenantId, @Param("since") LocalDate since);

    @Query("SELECT AVG(wc.energyLevel) FROM WellnessCheckIn wc WHERE wc.tenantId = :tenantId AND wc.checkInDate >= :since AND wc.energyLevel IS NOT NULL")
    Double getAverageEnergyLevel(@Param("tenantId") String tenantId, @Param("since") LocalDate since);

    @Query("SELECT AVG(wc.stressLevel) FROM WellnessCheckIn wc WHERE wc.tenantId = :tenantId AND wc.checkInDate >= :since AND wc.stressLevel IS NOT NULL")
    Double getAverageStressLevel(@Param("tenantId") String tenantId, @Param("since") LocalDate since);

    @Query("SELECT COUNT(wc) FROM WellnessCheckIn wc WHERE wc.tenantId = :tenantId AND wc.checkInDate >= :since")
    Long countByTenantIdSince(@Param("tenantId") String tenantId, @Param("since") LocalDate since);
}
