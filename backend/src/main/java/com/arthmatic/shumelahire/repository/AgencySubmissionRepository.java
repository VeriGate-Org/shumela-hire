package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.AgencySubmission;
import com.arthmatic.shumelahire.entity.AgencySubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgencySubmissionRepository extends JpaRepository<AgencySubmission, Long> {

    List<AgencySubmission> findByAgencyId(Long agencyId);

    List<AgencySubmission> findByJobPostingId(Long jobPostingId);

    List<AgencySubmission> findByStatus(AgencySubmissionStatus status);

    List<AgencySubmission> findByAgencyIdAndStatus(Long agencyId, AgencySubmissionStatus status);

    long countByAgencyId(Long agencyId);

    long countByAgencyIdAndStatus(Long agencyId, AgencySubmissionStatus status);
}
