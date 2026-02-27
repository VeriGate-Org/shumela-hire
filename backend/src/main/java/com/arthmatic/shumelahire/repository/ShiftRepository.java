package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {

    List<Shift> findByIsActiveTrue();

    Optional<Shift> findByCode(String code);

    List<Shift> findByDepartment(String department);

    List<Shift> findByIsActiveTrueAndDepartment(String department);
}
