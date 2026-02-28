package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.EmploymentEvent;
import com.arthmatic.shumelahire.entity.EmploymentEventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmploymentEventRepository extends JpaRepository<EmploymentEvent, Long> {

    List<EmploymentEvent> findByEmployeeIdOrderByEventDateDesc(Long employeeId);

    Page<EmploymentEvent> findByEmployeeId(Long employeeId, Pageable pageable);

    List<EmploymentEvent> findByEmployeeIdAndEventType(Long employeeId, EmploymentEventType eventType);

    // Find events within a date range
    @Query("SELECT e FROM EmploymentEvent e WHERE e.employeeId = :employeeId " +
           "AND e.eventDate BETWEEN :startDate AND :endDate ORDER BY e.eventDate DESC")
    List<EmploymentEvent> findByEmployeeAndDateRange(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Find recent events across all employees
    @Query("SELECT e FROM EmploymentEvent e ORDER BY e.createdAt DESC")
    Page<EmploymentEvent> findRecentEvents(Pageable pageable);

    // Find events by type across all employees
    Page<EmploymentEvent> findByEventType(EmploymentEventType eventType, Pageable pageable);

    // Count events by type for an employee
    long countByEmployeeIdAndEventType(Long employeeId, EmploymentEventType eventType);
}
