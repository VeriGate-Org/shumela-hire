package com.example.recruitment.repository;

import com.example.recruitment.entity.ShortlistScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShortlistScoreRepository extends JpaRepository<ShortlistScore, Long> {

    Optional<ShortlistScore> findByApplicationId(Long applicationId);

    @Query("SELECT s FROM ShortlistScore s WHERE s.application.jobPostingId = :jobPostingId ORDER BY s.totalScore DESC")
    List<ShortlistScore> findByJobPostingIdOrderByScore(@Param("jobPostingId") Long jobPostingId);

    @Query("SELECT s FROM ShortlistScore s WHERE s.application.jobPostingId = :jobPostingId AND s.totalScore >= :threshold ORDER BY s.totalScore DESC")
    List<ShortlistScore> findShortlistableByThreshold(@Param("jobPostingId") Long jobPostingId, @Param("threshold") Double threshold);
}
