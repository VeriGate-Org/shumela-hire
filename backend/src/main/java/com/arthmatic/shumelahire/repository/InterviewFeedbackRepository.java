package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.InterviewFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewFeedbackRepository extends JpaRepository<InterviewFeedback, Long> {

    List<InterviewFeedback> findByInterviewIdOrderBySubmittedAtDesc(Long interviewId);

    Optional<InterviewFeedback> findByInterviewIdAndSubmittedBy(Long interviewId, Long submittedBy);

    boolean existsByInterviewIdAndSubmittedBy(Long interviewId, Long submittedBy);

    long countByInterviewId(Long interviewId);

    @Query("SELECT f FROM InterviewFeedback f WHERE f.interview.application.id = :applicationId ORDER BY f.submittedAt DESC")
    List<InterviewFeedback> findByApplicationId(@Param("applicationId") Long applicationId);
}
