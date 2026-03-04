package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    List<Skill> findAllByOrderByNameAsc();

    List<Skill> findByIsActiveTrueOrderByNameAsc();

    Optional<Skill> findByName(String name);

    Optional<Skill> findByCode(String code);

    boolean existsByName(String name);

    boolean existsByCode(String code);

    @Query("SELECT s.name FROM Skill s WHERE s.isActive = true ORDER BY s.name ASC")
    List<String> findActiveNames();

    List<Skill> findByCategoryAndIsActiveTrueOrderByNameAsc(String category);
}
