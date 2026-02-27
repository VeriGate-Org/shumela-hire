package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.ShiftPattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftPatternRepository extends JpaRepository<ShiftPattern, Long> {

    List<ShiftPattern> findByIsActiveTrue();

    List<ShiftPattern> findByDepartment(String department);

    List<ShiftPattern> findByIsActiveTrueAndDepartment(String department);
}
