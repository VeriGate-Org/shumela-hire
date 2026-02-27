package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.Position;
import com.arthmatic.shumelahire.entity.PositionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {

    Page<Position> findByDepartment(String department, Pageable pageable);

    Page<Position> findByStatus(PositionStatus status, Pageable pageable);

    Page<Position> findByIsVacant(Boolean isVacant, Pageable pageable);

    Optional<Position> findByCodeAndTenantId(String code, String tenantId);

    boolean existsByCodeAndTenantId(String code, String tenantId);

    List<Position> findByOrgUnitId(Long orgUnitId);

    List<Position> findByCurrentEmployeeId(Long employeeId);

    @Query("SELECT p FROM Position p WHERE p.status = 'ACTIVE' AND p.isVacant = true")
    List<Position> findVacantPositions();

    @Query("SELECT COUNT(p) FROM Position p WHERE p.isVacant = true AND p.status = 'ACTIVE'")
    long countVacantPositions();

    @Query("SELECT COUNT(p) FROM Position p WHERE p.status = 'ACTIVE'")
    long countActivePositions();

    @Query("SELECT p.department, COUNT(p) FROM Position p WHERE p.status = 'ACTIVE' GROUP BY p.department")
    List<Object[]> countByDepartment();

    @Query("SELECT p FROM Position p WHERE " +
           "(:department IS NULL OR p.department = :department) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:isVacant IS NULL OR p.isVacant = :isVacant)")
    Page<Position> findByFilters(
            @Param("department") String department,
            @Param("status") PositionStatus status,
            @Param("isVacant") Boolean isVacant,
            Pageable pageable);
}
