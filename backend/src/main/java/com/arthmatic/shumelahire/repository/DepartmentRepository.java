package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findAllByOrderByNameAsc();

    List<Department> findByIsActiveTrueOrderByNameAsc();

    Optional<Department> findByName(String name);

    Optional<Department> findByCode(String code);

    boolean existsByName(String name);

    boolean existsByCode(String code);

    @Query("SELECT d.name FROM Department d WHERE d.isActive = true ORDER BY d.name ASC")
    List<String> findActiveNames();
}
