package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.OrgUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrgUnitRepository extends JpaRepository<OrgUnit, Long> {

    List<OrgUnit> findByParentIsNull();

    List<OrgUnit> findByParentId(Long parentId);

    List<OrgUnit> findByIsActiveTrue();

    @Query("SELECT o FROM OrgUnit o WHERE o.parent IS NULL AND o.isActive = true")
    List<OrgUnit> findRootUnits();

    @Query("SELECT o FROM OrgUnit o WHERE o.unitType = :unitType AND o.isActive = true")
    List<OrgUnit> findByUnitType(@Param("unitType") String unitType);

    Optional<OrgUnit> findByCodeAndTenantId(String code, String tenantId);

    boolean existsByCodeAndTenantId(String code, String tenantId);

    @Query("SELECT o FROM OrgUnit o WHERE o.manager.id = :managerId")
    List<OrgUnit> findByManagerId(@Param("managerId") Long managerId);

    @Query("SELECT COUNT(o) FROM OrgUnit o WHERE o.isActive = true")
    long countActiveUnits();
}
