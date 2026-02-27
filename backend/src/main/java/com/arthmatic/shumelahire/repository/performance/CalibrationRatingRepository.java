package com.arthmatic.shumelahire.repository.performance;

import com.arthmatic.shumelahire.entity.performance.CalibrationRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalibrationRatingRepository extends JpaRepository<CalibrationRating, Long> {

    List<CalibrationRating> findBySessionId(Long sessionId);

    Optional<CalibrationRating> findBySessionIdAndReviewId(Long sessionId, Long reviewId);

    @Query("SELECT cr FROM CalibrationRating cr WHERE cr.session.id = :sessionId AND cr.calibratedRating IS NOT NULL AND cr.originalRating IS NOT NULL AND cr.calibratedRating <> cr.originalRating")
    List<CalibrationRating> findAdjustedRatings(@Param("sessionId") Long sessionId);
}
