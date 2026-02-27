package com.arthmatic.shumelahire.service.performance;

import com.arthmatic.shumelahire.dto.performance.*;
import com.arthmatic.shumelahire.entity.performance.*;
import com.arthmatic.shumelahire.repository.performance.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PerformanceEnhancementService {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceEnhancementService.class);

    @Autowired
    private KeyResultAreaRepository kraRepository;

    @Autowired
    private PerformanceImprovementPlanRepository pipRepository;

    @Autowired
    private PIPMilestoneRepository milestoneRepository;

    @Autowired
    private CalibrationSessionRepository calibrationSessionRepository;

    @Autowired
    private CalibrationRatingRepository calibrationRatingRepository;

    @Autowired
    private PerformanceContractRepository contractRepository;

    @Autowired
    private PerformanceCycleRepository cycleRepository;

    // ========== KRA OPERATIONS ==========

    public KRAResponse createKRA(KRARequest request, String tenantId, String createdBy) {
        logger.info("Creating KRA '{}' for contract {}", request.getName(), request.getContractId());

        PerformanceContract contract = contractRepository.findByIdAndTenantId(request.getContractId(), tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Performance contract not found"));

        if (kraRepository.existsByNameAndContractIdAndTenantId(request.getName(), contract.getId(), tenantId)) {
            throw new IllegalArgumentException("A KRA with this name already exists for this contract");
        }

        KeyResultArea kra = new KeyResultArea();
        kra.setContract(contract);
        kra.setName(request.getName());
        kra.setDescription(request.getDescription());
        kra.setWeighting(request.getWeighting());
        kra.setSortOrder(request.getSortOrder());
        kra.setTenantId(tenantId);
        kra.setCreatedBy(createdBy);

        KeyResultArea saved = kraRepository.save(kra);
        logger.info("KRA created: {} (id={})", saved.getName(), saved.getId());
        return KRAResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<KRAResponse> getKRAsByContract(Long contractId, String tenantId) {
        return kraRepository.findByContractIdAndTenantIdAndIsActiveOrderBySortOrderAsc(contractId, tenantId, true)
                .stream()
                .map(KRAResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public KRAResponse getKRA(Long id, String tenantId) {
        KeyResultArea kra = kraRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("KRA not found"));
        return KRAResponse.fromEntity(kra);
    }

    public KRAResponse updateKRA(Long id, KRARequest request, String tenantId) {
        KeyResultArea kra = kraRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("KRA not found"));

        kra.setName(request.getName());
        kra.setDescription(request.getDescription());
        kra.setWeighting(request.getWeighting());
        kra.setSortOrder(request.getSortOrder());

        return KRAResponse.fromEntity(kraRepository.save(kra));
    }

    public void deleteKRA(Long id, String tenantId) {
        KeyResultArea kra = kraRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("KRA not found"));
        kra.setIsActive(false);
        kraRepository.save(kra);
        logger.info("KRA soft-deleted: id={}", id);
    }

    // ========== PIP OPERATIONS ==========

    public PIPResponse createPIP(PIPRequest request, String tenantId, String createdBy) {
        logger.info("Creating PIP for employee {} on contract {}", request.getEmployeeId(), request.getContractId());

        PerformanceContract contract = contractRepository.findByIdAndTenantId(request.getContractId(), tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Performance contract not found"));

        validatePIPDates(request.getStartDate(), request.getEndDate());

        PerformanceImprovementPlan pip = new PerformanceImprovementPlan();
        pip.setContract(contract);
        pip.setEmployeeId(request.getEmployeeId());
        pip.setEmployeeName(request.getEmployeeName());
        pip.setManagerId(request.getManagerId());
        pip.setManagerName(request.getManagerName());
        pip.setReason(request.getReason());
        pip.setPerformanceGaps(request.getPerformanceGaps());
        pip.setExpectedImprovements(request.getExpectedImprovements());
        pip.setSupportProvided(request.getSupportProvided());
        pip.setStartDate(request.getStartDate());
        pip.setEndDate(request.getEndDate());
        pip.setTenantId(tenantId);
        pip.setCreatedBy(createdBy);

        PerformanceImprovementPlan saved = pipRepository.save(pip);

        // Create milestones if provided
        if (request.getMilestones() != null && !request.getMilestones().isEmpty()) {
            int order = 1;
            for (PIPMilestoneRequest milestoneReq : request.getMilestones()) {
                PIPMilestone milestone = new PIPMilestone();
                milestone.setPip(saved);
                milestone.setTitle(milestoneReq.getTitle());
                milestone.setDescription(milestoneReq.getDescription());
                milestone.setSuccessCriteria(milestoneReq.getSuccessCriteria());
                milestone.setTargetDate(milestoneReq.getTargetDate());
                milestone.setSortOrder(milestoneReq.getSortOrder() != null ? milestoneReq.getSortOrder() : order);
                milestone.setTenantId(tenantId);
                milestoneRepository.save(milestone);
                order++;
            }
        }

        logger.info("PIP created: id={} for employee {}", saved.getId(), saved.getEmployeeId());
        return PIPResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public Page<PIPResponse> getPIPs(String tenantId, Pageable pageable) {
        return pipRepository.findByTenantIdOrderByCreatedAtDesc(tenantId, pageable)
                .map(PIPResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public PIPResponse getPIP(Long id, String tenantId) {
        PerformanceImprovementPlan pip = pipRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("PIP not found"));
        return PIPResponse.fromEntity(pip);
    }

    public PIPResponse activatePIP(Long id, String tenantId) {
        PerformanceImprovementPlan pip = pipRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("PIP not found"));
        pip.activate();
        return PIPResponse.fromEntity(pipRepository.save(pip));
    }

    public PIPResponse extendPIP(Long id, LocalDate newEndDate, String reason, String tenantId) {
        PerformanceImprovementPlan pip = pipRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("PIP not found"));

        if (newEndDate.isBefore(pip.getEndDate())) {
            throw new IllegalArgumentException("New end date must be after current end date");
        }

        pip.extend(newEndDate, reason);
        return PIPResponse.fromEntity(pipRepository.save(pip));
    }

    public PIPResponse completePIP(Long id, boolean successful, String notes, String userId, String tenantId) {
        PerformanceImprovementPlan pip = pipRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("PIP not found"));

        if (successful) {
            pip.completeSuccessfully(notes, userId);
        } else {
            pip.completeUnsuccessfully(notes, userId);
        }

        return PIPResponse.fromEntity(pipRepository.save(pip));
    }

    public PIPResponse terminatePIP(Long id, String notes, String userId, String tenantId) {
        PerformanceImprovementPlan pip = pipRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("PIP not found"));
        pip.terminate(notes, userId);
        return PIPResponse.fromEntity(pipRepository.save(pip));
    }

    public void completeMilestone(Long milestoneId, String notes, String tenantId) {
        PIPMilestone milestone = milestoneRepository.findByIdAndTenantId(milestoneId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Milestone not found"));
        milestone.complete(notes);
        milestoneRepository.save(milestone);
    }

    // ========== CALIBRATION SESSION OPERATIONS ==========

    public CalibrationSessionResponse createCalibrationSession(CalibrationSessionRequest request, String tenantId, String createdBy) {
        logger.info("Creating calibration session '{}' for cycle {}", request.getName(), request.getCycleId());

        PerformanceCycle cycle = cycleRepository.findByIdAndTenantId(request.getCycleId(), tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Performance cycle not found"));

        CalibrationSession session = new CalibrationSession();
        session.setCycle(cycle);
        session.setName(request.getName());
        session.setDescription(request.getDescription());
        session.setDepartment(request.getDepartment());
        session.setJobLevel(request.getJobLevel());
        session.setFacilitatorId(request.getFacilitatorId());
        session.setFacilitatorName(request.getFacilitatorName());
        session.setScheduledDate(request.getScheduledDate());
        session.setDistributionTarget(request.getDistributionTarget());
        session.setTenantId(tenantId);
        session.setCreatedBy(createdBy);

        CalibrationSession saved = calibrationSessionRepository.save(session);
        logger.info("Calibration session created: id={}", saved.getId());
        return CalibrationSessionResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public Page<CalibrationSessionResponse> getCalibrationSessions(String tenantId, Pageable pageable) {
        return calibrationSessionRepository.findByTenantIdOrderByScheduledDateDesc(tenantId, pageable)
                .map(CalibrationSessionResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public CalibrationSessionResponse getCalibrationSession(Long id, String tenantId) {
        CalibrationSession session = calibrationSessionRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Calibration session not found"));
        return CalibrationSessionResponse.fromEntity(session);
    }

    public CalibrationSessionResponse startCalibrationSession(Long id, String tenantId) {
        CalibrationSession session = calibrationSessionRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Calibration session not found"));
        session.start();
        return CalibrationSessionResponse.fromEntity(calibrationSessionRepository.save(session));
    }

    public CalibrationSessionResponse completeCalibrationSession(Long id, String notes, String tenantId) {
        CalibrationSession session = calibrationSessionRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Calibration session not found"));
        session.complete(notes);

        // Apply calibrated ratings to the reviews
        List<CalibrationRating> ratings = calibrationRatingRepository.findBySessionId(id);
        for (CalibrationRating rating : ratings) {
            if (rating.getCalibratedRating() != null) {
                PerformanceReview review = rating.getReview();
                review.setFinalRating(rating.getCalibratedRating());
                review.setModeratedAt(LocalDateTime.now());
                review.setModeratedBy(session.getFacilitatorId());
            }
        }

        return CalibrationSessionResponse.fromEntity(calibrationSessionRepository.save(session));
    }

    public CalibrationSessionResponse cancelCalibrationSession(Long id, String reason, String tenantId) {
        CalibrationSession session = calibrationSessionRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Calibration session not found"));
        session.cancel(reason);
        return CalibrationSessionResponse.fromEntity(calibrationSessionRepository.save(session));
    }

    public void addCalibrationRating(Long sessionId, CalibrationRatingRequest request, String userId, String tenantId) {
        CalibrationSession session = calibrationSessionRepository.findByIdAndTenantId(sessionId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Calibration session not found"));

        if (session.getStatus() != CalibrationStatus.IN_PROGRESS) {
            throw new IllegalStateException("Calibration session is not in progress");
        }

        CalibrationRating rating = calibrationRatingRepository
                .findBySessionIdAndReviewId(sessionId, request.getReviewId())
                .orElse(new CalibrationRating());

        rating.setSession(session);

        // Fetch the review to get original rating
        PerformanceReview review = rating.getReview();
        if (review == null) {
            // Need to look up the review - this is a new calibration rating
            rating.setTenantId(tenantId);
        }

        rating.setEmployeeId(request.getEmployeeId());
        rating.setEmployeeName(request.getEmployeeName());
        rating.setCalibratedRating(request.getCalibratedRating());
        rating.setAdjustmentReason(request.getAdjustmentReason());
        rating.setCalibratedBy(userId);
        rating.setCalibratedAt(LocalDateTime.now());

        calibrationRatingRepository.save(rating);
    }

    // ========== SELF-ASSESSMENT WORKFLOW ==========

    public void submitSelfAssessment(SelfAssessmentRequest request, String tenantId) {
        logger.info("Submitting self-assessment for review {}", request.getReviewId());

        PerformanceContract contract = findContractByReviewId(request.getReviewId(), tenantId);
        PerformanceReview review = contract.getReviews().stream()
                .filter(r -> r.getId().equals(request.getReviewId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        review.submitSelfAssessment(request.getAssessmentNotes(), request.getSelfRating());

        if (request.getGoalScores() != null) {
            for (SelfAssessmentRequest.GoalScoreRequest goalScore : request.getGoalScores()) {
                ReviewGoalScore score = new ReviewGoalScore();
                score.setReview(review);
                score.setScore(goalScore.getScore());
                score.setComment(goalScore.getComment());
                if (review.getGoalScores() == null) {
                    review.setGoalScores(new ArrayList<>());
                }
                review.getGoalScores().add(score);
            }
        }

        contractRepository.save(contract);
        logger.info("Self-assessment submitted for review {}", request.getReviewId());
    }

    // ========== REVIEW CYCLE AUTOMATION ==========

    public int createReviewsForCycle(Long cycleId, ReviewType reviewType, String tenantId) {
        logger.info("Auto-creating {} reviews for cycle {}", reviewType, cycleId);

        PerformanceCycle cycle = cycleRepository.findByIdAndTenantId(cycleId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Performance cycle not found"));

        List<PerformanceContract> contracts = contractRepository
                .findByEmployeeIdAndTenantIdOrderByCreatedAtDesc("", tenantId);

        // Get all approved contracts for this cycle
        int created = 0;
        Page<PerformanceContract> contractPage = contractRepository.findByCycleIdAndTenantId(
                cycleId, tenantId, Pageable.unpaged());

        for (PerformanceContract contract : contractPage.getContent()) {
            if (contract.getStatus() != ContractStatus.APPROVED) {
                continue;
            }

            // Check if review already exists
            boolean reviewExists = contract.getReviews() != null && contract.getReviews().stream()
                    .anyMatch(r -> r.getType() == reviewType);

            if (!reviewExists) {
                PerformanceReview review = new PerformanceReview(contract, reviewType);
                review.setTenantId(tenantId);
                review.setReviewPeriodStart(cycle.getStartDate().atStartOfDay());

                if (reviewType == ReviewType.MID_YEAR) {
                    review.setReviewPeriodEnd(cycle.getMidYearDeadline().atStartOfDay());
                    review.setDueDate(cycle.getMidYearDeadline().atStartOfDay());
                } else {
                    review.setReviewPeriodEnd(cycle.getFinalReviewDeadline().atStartOfDay());
                    review.setDueDate(cycle.getFinalReviewDeadline().atStartOfDay());
                }

                if (contract.getReviews() == null) {
                    contract.setReviews(new ArrayList<>());
                }
                contract.getReviews().add(review);
                contractRepository.save(contract);
                created++;
            }
        }

        logger.info("Created {} {} reviews for cycle {}", created, reviewType, cycleId);
        return created;
    }

    // ========== MANAGER DASHBOARD ==========

    @Transactional(readOnly = true)
    public ManagerDashboardResponse getManagerDashboard(String managerId, Long cycleId, String tenantId) {
        logger.info("Building manager dashboard for manager {} cycle {}", managerId, cycleId);

        ManagerDashboardResponse dashboard = new ManagerDashboardResponse();
        List<ManagerDashboardResponse.TeamMemberSummary> teamMembers = new ArrayList<>();

        Page<PerformanceContract> contracts = contractRepository.findByManagerIdAndTenantId(managerId, tenantId, Pageable.unpaged());

        List<PerformanceContract> cycleContracts = contracts.getContent().stream()
                .filter(c -> cycleId == null || c.getCycle().getId().equals(cycleId))
                .collect(Collectors.toList());

        int reviewsCompleted = 0;
        int reviewsPending = 0;
        int reviewsOverdue = 0;
        int contractsCompleted = 0;
        int contractsPending = 0;
        double totalRating = 0;
        int ratingCount = 0;
        Map<String, Integer> ratingDistribution = new LinkedHashMap<>();
        ratingDistribution.put("1-Outstanding", 0);
        ratingDistribution.put("2-Exceeds", 0);
        ratingDistribution.put("3-Meets", 0);
        ratingDistribution.put("4-Below", 0);
        ratingDistribution.put("5-Unsatisfactory", 0);

        for (PerformanceContract contract : cycleContracts) {
            ManagerDashboardResponse.TeamMemberSummary member = new ManagerDashboardResponse.TeamMemberSummary();
            member.setEmployeeId(contract.getEmployeeId());
            member.setEmployeeName(contract.getEmployeeName());
            member.setDepartment(contract.getDepartment());
            member.setContractStatus(contract.getStatus().name());

            if (contract.getStatus() == ContractStatus.APPROVED) {
                contractsCompleted++;
            } else {
                contractsPending++;
            }

            // Check reviews
            if (contract.getReviews() != null) {
                for (PerformanceReview review : contract.getReviews()) {
                    if (review.isCompleted()) {
                        reviewsCompleted++;
                    } else if (review.isOverdue()) {
                        reviewsOverdue++;
                    } else {
                        reviewsPending++;
                    }

                    member.setReviewStatus(review.getStatus().name());
                    if (review.getSelfRating() != null) {
                        member.setSelfRating(review.getSelfRating().doubleValue());
                    }
                    if (review.getManagerRating() != null) {
                        member.setManagerRating(review.getManagerRating().doubleValue());
                    }
                    if (review.getFinalRating() != null) {
                        member.setFinalRating(review.getFinalRating().doubleValue());
                        totalRating += review.getFinalRating().doubleValue();
                        ratingCount++;
                        categorizeRating(review.getFinalRating(), ratingDistribution);
                    }
                }
            }

            // Check for active PIPs
            List<PerformanceImprovementPlan> pips = pipRepository.findByEmployeeIdAndTenantId(contract.getEmployeeId(), tenantId);
            boolean hasPIP = pips.stream().anyMatch(p ->
                    p.getStatus() == PIPStatus.ACTIVE || p.getStatus() == PIPStatus.EXTENDED);
            member.setHasPIP(hasPIP);

            teamMembers.add(member);
        }

        dashboard.setTotalDirectReports(cycleContracts.size());
        dashboard.setContractsCompleted(contractsCompleted);
        dashboard.setContractsPending(contractsPending);
        dashboard.setReviewsCompleted(reviewsCompleted);
        dashboard.setReviewsPending(reviewsPending);
        dashboard.setReviewsOverdue(reviewsOverdue);
        dashboard.setActivePIPs((int) pipRepository.findActivePIPsByManager(managerId, tenantId).size());
        dashboard.setAverageTeamRating(ratingCount > 0 ? totalRating / ratingCount : 0.0);
        dashboard.setTeamMembers(teamMembers);
        dashboard.setRatingDistribution(ratingDistribution);

        return dashboard;
    }

    // ========== PERFORMANCE ANALYTICS ==========

    @Transactional(readOnly = true)
    public PerformanceAnalyticsResponse getPerformanceAnalytics(Long cycleId, String tenantId) {
        logger.info("Generating performance analytics for cycle {} tenant {}", cycleId, tenantId);

        PerformanceAnalyticsResponse analytics = new PerformanceAnalyticsResponse();

        // Cycle analytics
        if (cycleId != null) {
            PerformanceCycle cycle = cycleRepository.findByIdAndTenantId(cycleId, tenantId)
                    .orElseThrow(() -> new IllegalArgumentException("Performance cycle not found"));

            PerformanceAnalyticsResponse.CycleAnalytics cycleAnalytics = new PerformanceAnalyticsResponse.CycleAnalytics();
            cycleAnalytics.setCycleId(cycle.getId());
            cycleAnalytics.setCycleName(cycle.getName());

            Page<PerformanceContract> contracts = contractRepository.findByCycleIdAndTenantId(cycleId, tenantId, Pageable.unpaged());
            int totalContracts = (int) contracts.getTotalElements();
            long approvedContracts = contracts.getContent().stream()
                    .filter(c -> c.getStatus() == ContractStatus.APPROVED)
                    .count();

            cycleAnalytics.setTotalContracts(totalContracts);
            cycleAnalytics.setApprovedContracts((int) approvedContracts);
            cycleAnalytics.setPendingContracts(totalContracts - (int) approvedContracts);
            cycleAnalytics.setContractCompletionRate(totalContracts > 0 ? (double) approvedContracts / totalContracts * 100 : 0);

            int totalReviews = 0;
            int completedReviews = 0;
            for (PerformanceContract contract : contracts.getContent()) {
                if (contract.getReviews() != null) {
                    totalReviews += contract.getReviews().size();
                    completedReviews += (int) contract.getReviews().stream()
                            .filter(PerformanceReview::isCompleted)
                            .count();
                }
            }

            cycleAnalytics.setTotalReviews(totalReviews);
            cycleAnalytics.setCompletedReviews(completedReviews);
            cycleAnalytics.setReviewCompletionRate(totalReviews > 0 ? (double) completedReviews / totalReviews * 100 : 0);
            analytics.setCycleAnalytics(cycleAnalytics);

            // Rating distribution
            Map<String, Integer> ratingDist = new LinkedHashMap<>();
            ratingDist.put("Outstanding (4.5-5.0)", 0);
            ratingDist.put("Exceeds (3.5-4.49)", 0);
            ratingDist.put("Meets (2.5-3.49)", 0);
            ratingDist.put("Below (1.5-2.49)", 0);
            ratingDist.put("Unsatisfactory (0-1.49)", 0);

            Map<String, List<BigDecimal>> departmentRatings = new HashMap<>();

            for (PerformanceContract contract : contracts.getContent()) {
                if (contract.getReviews() != null) {
                    for (PerformanceReview review : contract.getReviews()) {
                        BigDecimal rating = review.getFinalRating() != null ? review.getFinalRating() : review.getManagerRating();
                        if (rating != null) {
                            categorizeRatingDetailed(rating, ratingDist);
                            String dept = contract.getDepartment() != null ? contract.getDepartment() : "Unassigned";
                            departmentRatings.computeIfAbsent(dept, k -> new ArrayList<>()).add(rating);
                        }
                    }
                }
            }

            analytics.setRatingDistribution(ratingDist);

            // Department averages
            Map<String, Double> deptAverages = new LinkedHashMap<>();
            departmentRatings.forEach((dept, ratings) -> {
                double avg = ratings.stream()
                        .mapToDouble(BigDecimal::doubleValue)
                        .average()
                        .orElse(0.0);
                deptAverages.put(dept, Math.round(avg * 100.0) / 100.0);
            });
            analytics.setDepartmentAverages(deptAverages);

            // Completion metrics
            PerformanceAnalyticsResponse.CompletionMetrics metrics = new PerformanceAnalyticsResponse.CompletionMetrics();
            metrics.setTotalEmployees(totalContracts);
            int selfSubmitted = 0;
            int managerSubmitted = 0;
            for (PerformanceContract contract : contracts.getContent()) {
                if (contract.getReviews() != null) {
                    for (PerformanceReview review : contract.getReviews()) {
                        if (review.getSelfSubmittedAt() != null) selfSubmitted++;
                        if (review.getManagerSubmittedAt() != null) managerSubmitted++;
                    }
                }
            }
            metrics.setSelfAssessmentsSubmitted(selfSubmitted);
            metrics.setManagerAssessmentsSubmitted(managerSubmitted);
            metrics.setCalibrationsCompleted((int) calibrationSessionRepository.countByTenantIdAndStatus(tenantId, CalibrationStatus.COMPLETED));
            metrics.setSelfAssessmentRate(totalReviews > 0 ? (double) selfSubmitted / totalReviews * 100 : 0);
            metrics.setManagerAssessmentRate(totalReviews > 0 ? (double) managerSubmitted / totalReviews * 100 : 0);
            analytics.setCompletionMetrics(metrics);
        }

        // PIP analytics
        PerformanceAnalyticsResponse.PIPAnalytics pipAnalytics = new PerformanceAnalyticsResponse.PIPAnalytics();
        pipAnalytics.setTotalActive((int) pipRepository.countByTenantIdAndStatus(tenantId, PIPStatus.ACTIVE)
                + (int) pipRepository.countByTenantIdAndStatus(tenantId, PIPStatus.EXTENDED));
        pipAnalytics.setCompletedSuccessfully((int) pipRepository.countByTenantIdAndStatus(tenantId, PIPStatus.COMPLETED_SUCCESSFULLY));
        pipAnalytics.setCompletedUnsuccessfully((int) pipRepository.countByTenantIdAndStatus(tenantId, PIPStatus.COMPLETED_UNSUCCESSFULLY));
        pipAnalytics.setTerminated((int) pipRepository.countByTenantIdAndStatus(tenantId, PIPStatus.TERMINATED));

        long overduePips = pipRepository.findActivePIPs(tenantId).stream().filter(PerformanceImprovementPlan::isOverdue).count();
        pipAnalytics.setOverdue((int) overduePips);

        long totalCompleted = pipAnalytics.getCompletedSuccessfully() + pipAnalytics.getCompletedUnsuccessfully();
        pipAnalytics.setSuccessRate(totalCompleted > 0 ? (double) pipAnalytics.getCompletedSuccessfully() / totalCompleted * 100 : 0);
        analytics.setPipAnalytics(pipAnalytics);

        return analytics;
    }

    // ========== PRIVATE HELPERS ==========

    private void validatePIPDates(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("PIP start date must be before end date");
        }
    }

    private PerformanceContract findContractByReviewId(Long reviewId, String tenantId) {
        // Search through contracts for the review
        Page<PerformanceContract> contracts = contractRepository.findByTenantIdOrderByCreatedAtDesc(tenantId, Pageable.unpaged());
        for (PerformanceContract contract : contracts.getContent()) {
            if (contract.getReviews() != null) {
                for (PerformanceReview review : contract.getReviews()) {
                    if (review.getId().equals(reviewId)) {
                        return contract;
                    }
                }
            }
        }
        throw new IllegalArgumentException("Review not found: " + reviewId);
    }

    private void categorizeRating(BigDecimal rating, Map<String, Integer> distribution) {
        double val = rating.doubleValue();
        if (val >= 4.5) distribution.merge("1-Outstanding", 1, Integer::sum);
        else if (val >= 3.5) distribution.merge("2-Exceeds", 1, Integer::sum);
        else if (val >= 2.5) distribution.merge("3-Meets", 1, Integer::sum);
        else if (val >= 1.5) distribution.merge("4-Below", 1, Integer::sum);
        else distribution.merge("5-Unsatisfactory", 1, Integer::sum);
    }

    private void categorizeRatingDetailed(BigDecimal rating, Map<String, Integer> distribution) {
        double val = rating.doubleValue();
        if (val >= 4.5) distribution.merge("Outstanding (4.5-5.0)", 1, Integer::sum);
        else if (val >= 3.5) distribution.merge("Exceeds (3.5-4.49)", 1, Integer::sum);
        else if (val >= 2.5) distribution.merge("Meets (2.5-3.49)", 1, Integer::sum);
        else if (val >= 1.5) distribution.merge("Below (1.5-2.49)", 1, Integer::sum);
        else distribution.merge("Unsatisfactory (0-1.49)", 1, Integer::sum);
    }
}
