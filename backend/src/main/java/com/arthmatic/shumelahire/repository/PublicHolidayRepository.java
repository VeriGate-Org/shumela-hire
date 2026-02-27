package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.PublicHoliday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PublicHolidayRepository extends JpaRepository<PublicHoliday, Long> {

    List<PublicHoliday> findByTenantIdAndActiveTrue(String tenantId);

    @Query("SELECT ph FROM PublicHoliday ph WHERE ph.tenantId = :tenantId " +
           "AND ph.active = true " +
           "AND ph.holidayDate BETWEEN :startDate AND :endDate " +
           "AND (:country IS NULL OR ph.country = :country)")
    List<PublicHoliday> findHolidaysInRange(
            @Param("tenantId") String tenantId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("country") String country);

    boolean existsByTenantIdAndHolidayDateAndCountry(String tenantId, LocalDate holidayDate, String country);

    @Query("SELECT ph FROM PublicHoliday ph WHERE ph.tenantId = :tenantId " +
           "AND ph.active = true " +
           "AND EXTRACT(YEAR FROM ph.holidayDate) = :year " +
           "ORDER BY ph.holidayDate ASC")
    List<PublicHoliday> findByTenantAndYear(
            @Param("tenantId") String tenantId,
            @Param("year") int year);
}
