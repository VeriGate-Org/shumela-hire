package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {

    List<Shift> findByActiveTrue();
}
