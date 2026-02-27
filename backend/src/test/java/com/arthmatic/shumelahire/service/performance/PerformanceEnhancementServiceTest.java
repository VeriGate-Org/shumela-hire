package com.arthmatic.shumelahire.service.performance;

import com.arthmatic.shumelahire.dto.performance.*;
import com.arthmatic.shumelahire.entity.performance.*;
import com.arthmatic.shumelahire.repository.performance.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PerformanceEnhancementServiceTest {

    @Mock
    private KeyResultAreaRepository kraRepository;

    @Mock
    private PerformanceImprovementPlanRepository pipRepository;

    @Mock
    private PIPMilestoneRepository milestoneRepository;

    @Mock
    private CalibrationSessionRepository calibrationSessionRepository;

    @Mock
    private CalibrationRatingRepository calibrationRatingRepository;

    @Mock
    private PerformanceContractRepository contractRepository;

    @Mock
    private PerformanceCycleRepository cycleRepository;

    @InjectMocks
    private PerformanceEnhancementService service;

    private String tenantId = "test-tenant";
    private String userId = "test-user";
    private PerformanceCycle testCycle;
    private PerformanceContract testContract;

    @BeforeEach
    void setUp() {
        LocalDate now = LocalDate.now();

        testCycle = new PerformanceCycle();
        testCycle.setId(1L);
        testCycle.setName("2026 Performance Cycle");
        testCycle.setStartDate(now.minusMonths(6));
        testCycle.setEndDate(now.plusMonths(6));
        testCycle.setMidYearDeadline(now.plusMonths(1));
        testCycle.setFinalReviewDeadline(now.plusMonths(5));
        testCycle.setStatus(CycleStatus.ACTIVE);
        testCycle.setTenantId(tenantId);

        testContract = new PerformanceContract();
        testContract.setId(1L);
        testContract.setCycle(testCycle);
        testContract.setEmployeeId("EMP001");
        testContract.setEmployeeName("John Doe");
        testContract.setManagerId("MGR001");
        testContract.setManagerName("Jane Smith");
        testContract.setDepartment("Engineering");
        testContract.setStatus(ContractStatus.APPROVED);
        testContract.setTenantId(tenantId);
        testContract.setGoals(new ArrayList<>());
        testContract.setReviews(new ArrayList<>());
    }

    // ========== KRA TESTS ==========

    @Test
    void createKRA_ShouldCreateValidKRA() {
        // Given
        KRARequest request = new KRARequest();
        request.setContractId(1L);
        request.setName("Customer Satisfaction");
        request.setDescription("Maintain high customer satisfaction scores");
        request.setWeighting(new BigDecimal("30.00"));

        when(contractRepository.findByIdAndTenantId(1L, tenantId)).thenReturn(Optional.of(testContract));
        when(kraRepository.existsByNameAndContractIdAndTenantId("Customer Satisfaction", 1L, tenantId)).thenReturn(false);

        KeyResultArea savedKra = new KeyResultArea();
        savedKra.setId(1L);
        savedKra.setContract(testContract);
        savedKra.setName("Customer Satisfaction");
        savedKra.setDescription("Maintain high customer satisfaction scores");
        savedKra.setWeighting(new BigDecimal("30.00"));
        savedKra.setIsActive(true);
        savedKra.setGoals(new ArrayList<>());
        when(kraRepository.save(any(KeyResultArea.class))).thenReturn(savedKra);

        // When
        KRAResponse result = service.createKRA(request, tenantId, userId);

        // Then
        assertNotNull(result);
        assertEquals("Customer Satisfaction", result.getName());
        assertEquals(new BigDecimal("30.00"), result.getWeighting());
        verify(kraRepository).save(any(KeyResultArea.class));
    }

    @Test
    void createKRA_ShouldThrowException_WhenDuplicateName() {
        // Given
        KRARequest request = new KRARequest();
        request.setContractId(1L);
        request.setName("Existing KRA");

        when(contractRepository.findByIdAndTenantId(1L, tenantId)).thenReturn(Optional.of(testContract));
        when(kraRepository.existsByNameAndContractIdAndTenantId("Existing KRA", 1L, tenantId)).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> service.createKRA(request, tenantId, userId));
        verify(kraRepository, never()).save(any());
    }

    @Test
    void createKRA_ShouldThrowException_WhenContractNotFound() {
        // Given
        KRARequest request = new KRARequest();
        request.setContractId(999L);
        request.setName("Test KRA");

        when(contractRepository.findByIdAndTenantId(999L, tenantId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> service.createKRA(request, tenantId, userId));
    }

    @Test
    void getKRAsByContract_ShouldReturnActiveKRAs() {
        // Given
        KeyResultArea kra1 = new KeyResultArea();
        kra1.setId(1L);
        kra1.setName("KRA 1");
        kra1.setContract(testContract);
        kra1.setIsActive(true);
        kra1.setGoals(new ArrayList<>());

        KeyResultArea kra2 = new KeyResultArea();
        kra2.setId(2L);
        kra2.setName("KRA 2");
        kra2.setContract(testContract);
        kra2.setIsActive(true);
        kra2.setGoals(new ArrayList<>());

        when(kraRepository.findByContractIdAndTenantIdAndIsActiveOrderBySortOrderAsc(1L, tenantId, true))
                .thenReturn(List.of(kra1, kra2));

        // When
        List<KRAResponse> results = service.getKRAsByContract(1L, tenantId);

        // Then
        assertEquals(2, results.size());
        assertEquals("KRA 1", results.get(0).getName());
        assertEquals("KRA 2", results.get(1).getName());
    }

    @Test
    void deleteKRA_ShouldSoftDelete() {
        // Given
        KeyResultArea kra = new KeyResultArea();
        kra.setId(1L);
        kra.setName("KRA to delete");
        kra.setIsActive(true);

        when(kraRepository.findByIdAndTenantId(1L, tenantId)).thenReturn(Optional.of(kra));
        when(kraRepository.save(any(KeyResultArea.class))).thenReturn(kra);

        // When
        service.deleteKRA(1L, tenantId);

        // Then
        assertFalse(kra.getIsActive());
        verify(kraRepository).save(kra);
    }

    // ========== PIP TESTS ==========

    @Test
    void createPIP_ShouldCreateValidPIP() {
        // Given
        PIPRequest request = new PIPRequest();
        request.setContractId(1L);
        request.setEmployeeId("EMP001");
        request.setEmployeeName("John Doe");
        request.setManagerId("MGR001");
        request.setManagerName("Jane Smith");
        request.setReason("Below expectations in Q3");
        request.setPerformanceGaps("Code quality and deadline adherence");
        request.setExpectedImprovements("Improve code review scores by 20%");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusMonths(3));

        when(contractRepository.findByIdAndTenantId(1L, tenantId)).thenReturn(Optional.of(testContract));

        PerformanceImprovementPlan savedPip = new PerformanceImprovementPlan();
        savedPip.setId(1L);
        savedPip.setContract(testContract);
        savedPip.setEmployeeId("EMP001");
        savedPip.setEmployeeName("John Doe");
        savedPip.setManagerId("MGR001");
        savedPip.setManagerName("Jane Smith");
        savedPip.setReason("Below expectations in Q3");
        savedPip.setStartDate(LocalDate.now());
        savedPip.setEndDate(LocalDate.now().plusMonths(3));
        savedPip.setStatus(PIPStatus.DRAFT);
        savedPip.setCreatedBy(userId);
        savedPip.setMilestones(new ArrayList<>());

        when(pipRepository.save(any(PerformanceImprovementPlan.class))).thenReturn(savedPip);

        // When
        PIPResponse result = service.createPIP(request, tenantId, userId);

        // Then
        assertNotNull(result);
        assertEquals("EMP001", result.getEmployeeId());
        assertEquals("DRAFT", result.getStatus());
        verify(pipRepository).save(any(PerformanceImprovementPlan.class));
    }

    @Test
    void createPIP_ShouldThrowException_WhenDatesInvalid() {
        // Given
        PIPRequest request = new PIPRequest();
        request.setContractId(1L);
        request.setEmployeeId("EMP001");
        request.setManagerId("MGR001");
        request.setReason("Test");
        request.setStartDate(LocalDate.now().plusMonths(3));
        request.setEndDate(LocalDate.now()); // End before start

        when(contractRepository.findByIdAndTenantId(1L, tenantId)).thenReturn(Optional.of(testContract));

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> service.createPIP(request, tenantId, userId));
        verify(pipRepository, never()).save(any());
    }

    @Test
    void createPIP_WithMilestones_ShouldCreateMilestones() {
        // Given
        PIPMilestoneRequest milestoneReq = new PIPMilestoneRequest();
        milestoneReq.setTitle("First checkpoint");
        milestoneReq.setTargetDate(LocalDate.now().plusMonths(1));

        PIPRequest request = new PIPRequest();
        request.setContractId(1L);
        request.setEmployeeId("EMP001");
        request.setEmployeeName("John Doe");
        request.setManagerId("MGR001");
        request.setManagerName("Jane Smith");
        request.setReason("Improvement needed");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusMonths(3));
        request.setMilestones(List.of(milestoneReq));

        when(contractRepository.findByIdAndTenantId(1L, tenantId)).thenReturn(Optional.of(testContract));

        PerformanceImprovementPlan savedPip = new PerformanceImprovementPlan();
        savedPip.setId(1L);
        savedPip.setContract(testContract);
        savedPip.setEmployeeId("EMP001");
        savedPip.setEmployeeName("John Doe");
        savedPip.setManagerId("MGR001");
        savedPip.setManagerName("Jane Smith");
        savedPip.setReason("Improvement needed");
        savedPip.setStartDate(LocalDate.now());
        savedPip.setEndDate(LocalDate.now().plusMonths(3));
        savedPip.setStatus(PIPStatus.DRAFT);
        savedPip.setCreatedBy(userId);
        savedPip.setMilestones(new ArrayList<>());

        when(pipRepository.save(any(PerformanceImprovementPlan.class))).thenReturn(savedPip);
        when(milestoneRepository.save(any(PIPMilestone.class))).thenReturn(new PIPMilestone());

        // When
        PIPResponse result = service.createPIP(request, tenantId, userId);

        // Then
        assertNotNull(result);
        verify(milestoneRepository, times(1)).save(any(PIPMilestone.class));
    }

    @Test
    void activatePIP_ShouldActivateDraftPIP() {
        // Given
        PerformanceImprovementPlan pip = new PerformanceImprovementPlan();
        pip.setId(1L);
        pip.setContract(testContract);
        pip.setEmployeeId("EMP001");
        pip.setEmployeeName("John Doe");
        pip.setManagerId("MGR001");
        pip.setManagerName("Jane Smith");
        pip.setReason("Test");
        pip.setStartDate(LocalDate.now());
        pip.setEndDate(LocalDate.now().plusMonths(3));
        pip.setStatus(PIPStatus.DRAFT);
        pip.setCreatedBy(userId);
        pip.setMilestones(new ArrayList<>());

        when(pipRepository.findByIdAndTenantId(1L, tenantId)).thenReturn(Optional.of(pip));
        when(pipRepository.save(any(PerformanceImprovementPlan.class))).thenReturn(pip);

        // When
        PIPResponse result = service.activatePIP(1L, tenantId);

        // Then
        assertEquals("ACTIVE", result.getStatus());
    }

    @Test
    void extendPIP_ShouldExtendActivePIP() {
        // Given
        PerformanceImprovementPlan pip = new PerformanceImprovementPlan();
        pip.setId(1L);
        pip.setContract(testContract);
        pip.setEmployeeId("EMP001");
        pip.setEmployeeName("John Doe");
        pip.setManagerId("MGR001");
        pip.setManagerName("Jane Smith");
        pip.setReason("Test");
        pip.setStartDate(LocalDate.now().minusMonths(2));
        pip.setEndDate(LocalDate.now().plusMonths(1));
        pip.setStatus(PIPStatus.ACTIVE);
        pip.setCreatedBy(userId);
        pip.setMilestones(new ArrayList<>());

        when(pipRepository.findByIdAndTenantId(1L, tenantId)).thenReturn(Optional.of(pip));
        when(pipRepository.save(any(PerformanceImprovementPlan.class))).thenReturn(pip);

        LocalDate newEnd = LocalDate.now().plusMonths(3);

        // When
        PIPResponse result = service.extendPIP(1L, newEnd, "Needs more time", tenantId);

        // Then
        assertEquals("EXTENDED", result.getStatus());
        assertNotNull(result.getOriginalEndDate());
    }

    @Test
    void extendPIP_ShouldThrow_WhenNewDateBeforeCurrent() {
        // Given
        PerformanceImprovementPlan pip = new PerformanceImprovementPlan();
        pip.setId(1L);
        pip.setStatus(PIPStatus.ACTIVE);
        pip.setEndDate(LocalDate.now().plusMonths(2));

        when(pipRepository.findByIdAndTenantId(1L, tenantId)).thenReturn(Optional.of(pip));

        LocalDate earlierDate = LocalDate.now();

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> service.extendPIP(1L, earlierDate, "Reason", tenantId));
    }

    @Test
    void completePIP_Successfully_ShouldUpdateStatus() {
        // Given
        PerformanceImprovementPlan pip = new PerformanceImprovementPlan();
        pip.setId(1L);
        pip.setContract(testContract);
        pip.setEmployeeId("EMP001");
        pip.setEmployeeName("John Doe");
        pip.setManagerId("MGR001");
        pip.setManagerName("Jane Smith");
        pip.setReason("Test");
        pip.setStartDate(LocalDate.now().minusMonths(3));
        pip.setEndDate(LocalDate.now());
        pip.setStatus(PIPStatus.ACTIVE);
        pip.setCreatedBy(userId);
        pip.setMilestones(new ArrayList<>());

        when(pipRepository.findByIdAndTenantId(1L, tenantId)).thenReturn(Optional.of(pip));
        when(pipRepository.save(any(PerformanceImprovementPlan.class))).thenReturn(pip);

        // When
        PIPResponse result = service.completePIP(1L, true, "Employee improved significantly", userId, tenantId);

        // Then
        assertEquals("COMPLETED_SUCCESSFULLY", result.getStatus());
    }

    @Test
    void completePIP_Unsuccessfully_ShouldUpdateStatus() {
        // Given
        PerformanceImprovementPlan pip = new PerformanceImprovementPlan();
        pip.setId(1L);
        pip.setContract(testContract);
        pip.setEmployeeId("EMP001");
        pip.setEmployeeName("John Doe");
        pip.setManagerId("MGR001");
        pip.setManagerName("Jane Smith");
        pip.setReason("Test");
        pip.setStartDate(LocalDate.now().minusMonths(3));
        pip.setEndDate(LocalDate.now());
        pip.setStatus(PIPStatus.ACTIVE);
        pip.setCreatedBy(userId);
        pip.setMilestones(new ArrayList<>());

        when(pipRepository.findByIdAndTenantId(1L, tenantId)).thenReturn(Optional.of(pip));
        when(pipRepository.save(any(PerformanceImprovementPlan.class))).thenReturn(pip);

        // When
        PIPResponse result = service.completePIP(1L, false, "Insufficient progress", userId, tenantId);

        // Then
        assertEquals("COMPLETED_UNSUCCESSFULLY", result.getStatus());
    }

    @Test
    void terminatePIP_ShouldTerminateActivePIP() {
        // Given
        PerformanceImprovementPlan pip = new PerformanceImprovementPlan();
        pip.setId(1L);
        pip.setContract(testContract);
        pip.setEmployeeId("EMP001");
        pip.setEmployeeName("John Doe");
        pip.setManagerId("MGR001");
        pip.setManagerName("Jane Smith");
        pip.setReason("Test");
        pip.setStartDate(LocalDate.now().minusMonths(1));
        pip.setEndDate(LocalDate.now().plusMonths(2));
        pip.setStatus(PIPStatus.ACTIVE);
        pip.setCreatedBy(userId);
        pip.setMilestones(new ArrayList<>());

        when(pipRepository.findByIdAndTenantId(1L, tenantId)).thenReturn(Optional.of(pip));
        when(pipRepository.save(any(PerformanceImprovementPlan.class))).thenReturn(pip);

        // When
        PIPResponse result = service.terminatePIP(1L, "Role change", userId, tenantId);

        // Then
        assertEquals("TERMINATED", result.getStatus());
    }

    // ========== CALIBRATION SESSION TESTS ==========

    @Test
    void createCalibrationSession_ShouldCreateValidSession() {
        // Given
        CalibrationSessionRequest request = new CalibrationSessionRequest();
        request.setCycleId(1L);
        request.setName("Engineering Q4 Calibration");
        request.setDepartment("Engineering");
        request.setFacilitatorId("HR001");
        request.setFacilitatorName("HR Manager");
        request.setScheduledDate(LocalDateTime.now().plusDays(7));

        when(cycleRepository.findByIdAndTenantId(1L, tenantId)).thenReturn(Optional.of(testCycle));

        CalibrationSession savedSession = new CalibrationSession();
        savedSession.setId(1L);
        savedSession.setCycle(testCycle);
        savedSession.setName("Engineering Q4 Calibration");
        savedSession.setDepartment("Engineering");
        savedSession.setFacilitatorId("HR001");
        savedSession.setFacilitatorName("HR Manager");
        savedSession.setStatus(CalibrationStatus.SCHEDULED);
        savedSession.setRatings(new ArrayList<>());

        when(calibrationSessionRepository.save(any(CalibrationSession.class))).thenReturn(savedSession);

        // When
        CalibrationSessionResponse result = service.createCalibrationSession(request, tenantId, userId);

        // Then
        assertNotNull(result);
        assertEquals("Engineering Q4 Calibration", result.getName());
        assertEquals("SCHEDULED", result.getStatus());
        verify(calibrationSessionRepository).save(any(CalibrationSession.class));
    }

    @Test
    void startCalibrationSession_ShouldStartScheduledSession() {
        // Given
        CalibrationSession session = new CalibrationSession();
        session.setId(1L);
        session.setCycle(testCycle);
        session.setName("Test Session");
        session.setStatus(CalibrationStatus.SCHEDULED);
        session.setRatings(new ArrayList<>());

        when(calibrationSessionRepository.findByIdAndTenantId(1L, tenantId)).thenReturn(Optional.of(session));
        when(calibrationSessionRepository.save(any(CalibrationSession.class))).thenReturn(session);

        // When
        CalibrationSessionResponse result = service.startCalibrationSession(1L, tenantId);

        // Then
        assertEquals("IN_PROGRESS", result.getStatus());
    }

    @Test
    void completeCalibrationSession_ShouldApplyRatings() {
        // Given
        CalibrationSession session = new CalibrationSession();
        session.setId(1L);
        session.setCycle(testCycle);
        session.setName("Test Session");
        session.setStatus(CalibrationStatus.IN_PROGRESS);
        session.setFacilitatorId("HR001");
        session.setRatings(new ArrayList<>());

        PerformanceReview review = new PerformanceReview();
        review.setId(1L);

        CalibrationRating rating = new CalibrationRating();
        rating.setId(1L);
        rating.setSession(session);
        rating.setReview(review);
        rating.setOriginalRating(new BigDecimal("3.50"));
        rating.setCalibratedRating(new BigDecimal("3.75"));

        when(calibrationSessionRepository.findByIdAndTenantId(1L, tenantId)).thenReturn(Optional.of(session));
        when(calibrationRatingRepository.findBySessionId(1L)).thenReturn(List.of(rating));
        when(calibrationSessionRepository.save(any(CalibrationSession.class))).thenReturn(session);

        // When
        CalibrationSessionResponse result = service.completeCalibrationSession(1L, "Calibration complete", tenantId);

        // Then
        assertEquals("COMPLETED", result.getStatus());
        // Verify the review got the calibrated rating
        assertEquals(new BigDecimal("3.75"), review.getFinalRating());
        assertNotNull(review.getModeratedAt());
    }

    @Test
    void cancelCalibrationSession_ShouldCancelScheduledSession() {
        // Given
        CalibrationSession session = new CalibrationSession();
        session.setId(1L);
        session.setCycle(testCycle);
        session.setName("Test Session");
        session.setStatus(CalibrationStatus.SCHEDULED);
        session.setRatings(new ArrayList<>());

        when(calibrationSessionRepository.findByIdAndTenantId(1L, tenantId)).thenReturn(Optional.of(session));
        when(calibrationSessionRepository.save(any(CalibrationSession.class))).thenReturn(session);

        // When
        CalibrationSessionResponse result = service.cancelCalibrationSession(1L, "Rescheduled", tenantId);

        // Then
        assertEquals("CANCELLED", result.getStatus());
    }

    // ========== REVIEW CYCLE AUTOMATION TESTS ==========

    @Test
    void createReviewsForCycle_ShouldCreateMidYearReviews() {
        // Given
        when(cycleRepository.findByIdAndTenantId(1L, tenantId)).thenReturn(Optional.of(testCycle));

        Page<PerformanceContract> contractPage = new PageImpl<>(List.of(testContract));
        when(contractRepository.findByCycleIdAndTenantId(eq(1L), eq(tenantId), any(Pageable.class)))
                .thenReturn(contractPage);
        when(contractRepository.save(any(PerformanceContract.class))).thenReturn(testContract);

        // When
        int created = service.createReviewsForCycle(1L, ReviewType.MID_YEAR, tenantId);

        // Then
        assertEquals(1, created);
        verify(contractRepository).save(testContract);
    }

    @Test
    void createReviewsForCycle_ShouldNotDuplicate_WhenReviewExists() {
        // Given
        PerformanceReview existingReview = new PerformanceReview(testContract, ReviewType.MID_YEAR);
        testContract.getReviews().add(existingReview);

        when(cycleRepository.findByIdAndTenantId(1L, tenantId)).thenReturn(Optional.of(testCycle));

        Page<PerformanceContract> contractPage = new PageImpl<>(List.of(testContract));
        when(contractRepository.findByCycleIdAndTenantId(eq(1L), eq(tenantId), any(Pageable.class)))
                .thenReturn(contractPage);

        // When
        int created = service.createReviewsForCycle(1L, ReviewType.MID_YEAR, tenantId);

        // Then
        assertEquals(0, created);
        verify(contractRepository, never()).save(any());
    }

    // ========== MANAGER DASHBOARD TESTS ==========

    @Test
    void getManagerDashboard_ShouldReturnDashboardData() {
        // Given
        Page<PerformanceContract> contracts = new PageImpl<>(List.of(testContract));
        when(contractRepository.findByManagerIdAndTenantId(eq("MGR001"), eq(tenantId), any(Pageable.class)))
                .thenReturn(contracts);
        when(pipRepository.findByEmployeeIdAndTenantId("EMP001", tenantId)).thenReturn(Collections.emptyList());
        when(pipRepository.findActivePIPsByManager("MGR001", tenantId)).thenReturn(Collections.emptyList());

        // When
        ManagerDashboardResponse result = service.getManagerDashboard("MGR001", 1L, tenantId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalDirectReports());
        assertEquals(1, result.getContractsCompleted()); // Contract is APPROVED
        assertEquals(0, result.getContractsPending());
        assertEquals(1, result.getTeamMembers().size());
        assertEquals("John Doe", result.getTeamMembers().get(0).getEmployeeName());
    }

    // ========== PERFORMANCE ANALYTICS TESTS ==========

    @Test
    void getPerformanceAnalytics_ShouldReturnAnalytics() {
        // Given
        PerformanceReview completedReview = new PerformanceReview(testContract, ReviewType.FINAL);
        completedReview.setStatus(ReviewStatus.COMPLETED);
        completedReview.setFinalRating(new BigDecimal("3.80"));
        completedReview.setSelfSubmittedAt(LocalDateTime.now());
        completedReview.setManagerSubmittedAt(LocalDateTime.now());
        testContract.setReviews(new ArrayList<>(List.of(completedReview)));

        when(cycleRepository.findByIdAndTenantId(1L, tenantId)).thenReturn(Optional.of(testCycle));

        Page<PerformanceContract> contractPage = new PageImpl<>(List.of(testContract));
        when(contractRepository.findByCycleIdAndTenantId(eq(1L), eq(tenantId), any(Pageable.class)))
                .thenReturn(contractPage);
        when(calibrationSessionRepository.countByTenantIdAndStatus(tenantId, CalibrationStatus.COMPLETED)).thenReturn(2L);
        when(pipRepository.countByTenantIdAndStatus(eq(tenantId), any(PIPStatus.class))).thenReturn(0L);
        when(pipRepository.findActivePIPs(tenantId)).thenReturn(Collections.emptyList());

        // When
        PerformanceAnalyticsResponse result = service.getPerformanceAnalytics(1L, tenantId);

        // Then
        assertNotNull(result);
        assertNotNull(result.getCycleAnalytics());
        assertEquals(1, result.getCycleAnalytics().getTotalContracts());
        assertEquals(1, result.getCycleAnalytics().getApprovedContracts());
        assertEquals(100.0, result.getCycleAnalytics().getContractCompletionRate());
        assertEquals(1, result.getCycleAnalytics().getTotalReviews());
        assertEquals(1, result.getCycleAnalytics().getCompletedReviews());

        assertNotNull(result.getCompletionMetrics());
        assertEquals(1, result.getCompletionMetrics().getSelfAssessmentsSubmitted());
        assertEquals(1, result.getCompletionMetrics().getManagerAssessmentsSubmitted());

        assertNotNull(result.getRatingDistribution());
        assertNotNull(result.getPipAnalytics());
    }

    @Test
    void getPerformanceAnalytics_WithNoCycle_ShouldReturnPIPAnalyticsOnly() {
        // Given
        when(pipRepository.countByTenantIdAndStatus(eq(tenantId), any(PIPStatus.class))).thenReturn(0L);
        when(pipRepository.findActivePIPs(tenantId)).thenReturn(Collections.emptyList());

        // When
        PerformanceAnalyticsResponse result = service.getPerformanceAnalytics(null, tenantId);

        // Then
        assertNotNull(result);
        assertNull(result.getCycleAnalytics());
        assertNotNull(result.getPipAnalytics());
    }
}
