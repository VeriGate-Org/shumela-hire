package com.example.recruitment.service;

import com.example.recruitment.entity.*;
import com.example.recruitment.repository.AgencyProfileRepository;
import com.example.recruitment.repository.AgencySubmissionRepository;
import com.example.recruitment.repository.JobPostingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AgencyPortalService {

    private static final Logger logger = LoggerFactory.getLogger(AgencyPortalService.class);

    @Autowired
    private AgencyProfileRepository agencyProfileRepository;

    @Autowired
    private AgencySubmissionRepository agencySubmissionRepository;

    @Autowired
    private JobPostingRepository jobPostingRepository;

    public AgencyProfile registerAgency(AgencyProfile agency) {
        agency.setStatus(AgencyStatus.PENDING_APPROVAL);
        agency.setCreatedAt(LocalDateTime.now());
        AgencyProfile saved = agencyProfileRepository.save(agency);
        logger.info("Agency registered: {}", saved.getAgencyName());
        return saved;
    }

    public List<AgencyProfile> getAllAgencies() {
        return agencyProfileRepository.findAll();
    }

    public AgencyProfile getAgency(Long id) {
        return agencyProfileRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Agency not found: " + id));
    }

    @Transactional
    public AgencyProfile approveAgency(Long agencyId) {
        AgencyProfile agency = getAgency(agencyId);
        if (!agency.getStatus().canTransitionTo(AgencyStatus.ACTIVE)) {
            throw new IllegalStateException("Cannot approve agency in status: " + agency.getStatus());
        }
        agency.setStatus(AgencyStatus.ACTIVE);
        return agencyProfileRepository.save(agency);
    }

    @Transactional
    public AgencyProfile suspendAgency(Long agencyId) {
        AgencyProfile agency = getAgency(agencyId);
        if (!agency.getStatus().canTransitionTo(AgencyStatus.SUSPENDED)) {
            throw new IllegalStateException("Cannot suspend agency in status: " + agency.getStatus());
        }
        agency.setStatus(AgencyStatus.SUSPENDED);
        return agencyProfileRepository.save(agency);
    }

    @Transactional
    public AgencySubmission submitCandidate(Long agencyId, AgencySubmission submission) {
        AgencyProfile agency = getAgency(agencyId);
        if (!agency.getStatus().isActive()) {
            throw new IllegalStateException("Only active agencies can submit candidates");
        }

        JobPosting jobPosting = jobPostingRepository.findById(submission.getJobPosting().getId())
            .orElseThrow(() -> new RuntimeException("Job posting not found"));

        submission.setAgency(agency);
        submission.setJobPosting(jobPosting);
        submission.setStatus(AgencySubmissionStatus.SUBMITTED);
        submission.setSubmittedAt(LocalDateTime.now());

        AgencySubmission saved = agencySubmissionRepository.save(submission);
        logger.info("Agency {} submitted candidate {} for job posting {}",
            agency.getAgencyName(), submission.getCandidateName(), jobPosting.getId());
        return saved;
    }

    @Transactional
    public AgencySubmission reviewSubmission(Long submissionId, boolean accept, Long reviewedBy) {
        AgencySubmission submission = agencySubmissionRepository.findById(submissionId)
            .orElseThrow(() -> new RuntimeException("Submission not found: " + submissionId));

        submission.setStatus(accept ? AgencySubmissionStatus.ACCEPTED : AgencySubmissionStatus.REJECTED);
        submission.setReviewedAt(LocalDateTime.now());
        submission.setReviewedBy(reviewedBy);

        AgencySubmission saved = agencySubmissionRepository.save(submission);
        logger.info("Submission {} {}", submissionId, accept ? "accepted" : "rejected");
        return saved;
    }

    public Map<String, Object> getAgencyDashboard(Long agencyId) {
        AgencyProfile agency = getAgency(agencyId);
        long totalSubmissions = agencySubmissionRepository.countByAgencyId(agencyId);
        long acceptedSubmissions = agencySubmissionRepository.countByAgencyIdAndStatus(
            agencyId, AgencySubmissionStatus.ACCEPTED);

        Map<String, Object> dashboard = new LinkedHashMap<>();
        dashboard.put("agencyName", agency.getAgencyName());
        dashboard.put("status", agency.getStatus());
        dashboard.put("totalSubmissions", totalSubmissions);
        dashboard.put("acceptedSubmissions", acceptedSubmissions);
        dashboard.put("placementRate", totalSubmissions > 0
            ? (double) acceptedSubmissions / totalSubmissions * 100 : 0);
        return dashboard;
    }
}
