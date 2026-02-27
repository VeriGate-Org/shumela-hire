package com.arthmatic.shumelahire.service.compensation;

import com.arthmatic.shumelahire.dto.compensation.*;
import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.compensation.*;
import com.arthmatic.shumelahire.repository.EmployeeRepository;
import com.arthmatic.shumelahire.repository.compensation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompensationServiceTest {

    @Mock private PayGradeRepository payGradeRepository;
    @Mock private SalaryBandRepository salaryBandRepository;
    @Mock private CompensationReviewRepository compensationReviewRepository;
    @Mock private TotalRewardsStatementRepository totalRewardsStatementRepository;
    @Mock private BenefitRepository benefitRepository;
    @Mock private EmployeeRepository employeeRepository;

    @InjectMocks
    private CompensationService compensationService;

    private Employee testEmployee;
    private PayGrade testPayGrade;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee();
        testEmployee.setId(1L);
        testEmployee.setFirstName("Jane");
        testEmployee.setLastName("Smith");
        testEmployee.setEmail("jane.smith@example.com");
        testEmployee.setEmployeeNumber("EMP001");
        testEmployee.setHireDate(LocalDate.now());

        testPayGrade = new PayGrade();
        testPayGrade.setId(1L);
        testPayGrade.setCode("PG1");
        testPayGrade.setName("Junior Level");
        testPayGrade.setMinSalary(new BigDecimal("20000"));
        testPayGrade.setMidSalary(new BigDecimal("30000"));
        testPayGrade.setMaxSalary(new BigDecimal("40000"));
        testPayGrade.setCurrency("ZAR");
        testPayGrade.setIsActive(true);
    }

    // ==================== Pay Grade Tests ====================

    @Test
    void createPayGrade_ValidRequest_ReturnsResponse() {
        PayGradeRequest request = new PayGradeRequest();
        request.setCode("PG2");
        request.setName("Senior Level");
        request.setMinSalary(new BigDecimal("50000"));
        request.setMidSalary(new BigDecimal("65000"));
        request.setMaxSalary(new BigDecimal("80000"));
        request.setCurrency("ZAR");

        PayGrade saved = new PayGrade();
        saved.setId(2L);
        saved.setCode("PG2");
        saved.setName("Senior Level");
        saved.setMinSalary(new BigDecimal("50000"));
        saved.setMidSalary(new BigDecimal("65000"));
        saved.setMaxSalary(new BigDecimal("80000"));
        saved.setCurrency("ZAR");
        saved.setIsActive(true);

        when(payGradeRepository.save(any(PayGrade.class))).thenReturn(saved);

        PayGradeResponse response = compensationService.createPayGrade(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getCode()).isEqualTo("PG2");
        assertThat(response.getMinSalary()).isEqualByComparingTo(new BigDecimal("50000"));
        verify(payGradeRepository, times(1)).save(any(PayGrade.class));
    }

    @Test
    void createPayGrade_InvalidSalaryRange_ThrowsException() {
        PayGradeRequest request = new PayGradeRequest();
        request.setCode("PG3");
        request.setName("Invalid Grade");
        request.setMinSalary(new BigDecimal("80000"));
        request.setMidSalary(new BigDecimal("50000"));  // mid < min — invalid
        request.setMaxSalary(new BigDecimal("90000"));

        assertThrows(IllegalArgumentException.class,
                () -> compensationService.createPayGrade(request));
        verify(payGradeRepository, never()).save(any());
    }

    @Test
    void updatePayGrade_NotFound_ThrowsException() {
        when(payGradeRepository.findById(99L)).thenReturn(Optional.empty());

        PayGradeRequest request = new PayGradeRequest();
        request.setCode("PG1");
        request.setName("Updated");
        request.setMinSalary(new BigDecimal("10000"));
        request.setMidSalary(new BigDecimal("20000"));
        request.setMaxSalary(new BigDecimal("30000"));

        assertThrows(IllegalArgumentException.class,
                () -> compensationService.updatePayGrade(99L, request));
    }

    @Test
    void getPayGrade_ExistingId_ReturnsResponse() {
        when(payGradeRepository.findById(1L)).thenReturn(Optional.of(testPayGrade));

        PayGradeResponse response = compensationService.getPayGrade(1L);

        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("PG1");
    }

    @Test
    void deletePayGrade_SetsInactive() {
        when(payGradeRepository.findById(1L)).thenReturn(Optional.of(testPayGrade));
        when(payGradeRepository.save(any(PayGrade.class))).thenReturn(testPayGrade);

        compensationService.deletePayGrade(1L);

        assertThat(testPayGrade.getIsActive()).isFalse();
        verify(payGradeRepository, times(1)).save(testPayGrade);
    }

    @Test
    void getAllPayGrades_ReturnsList() {
        when(payGradeRepository.findAll()).thenReturn(Arrays.asList(testPayGrade));

        List<PayGradeResponse> grades = compensationService.getAllPayGrades();

        assertThat(grades).hasSize(1);
        assertThat(grades.get(0).getCode()).isEqualTo("PG1");
    }

    // ==================== Salary Band Tests ====================

    @Test
    void createSalaryBand_ValidRequest_ReturnsResponse() {
        SalaryBandRequest request = new SalaryBandRequest();
        request.setPayGradeId(1L);
        request.setBandName("Engineering Band A");
        request.setJobFamily("Engineering");
        request.setMinSalary(new BigDecimal("25000"));
        request.setMaxSalary(new BigDecimal("35000"));

        SalaryBand saved = new SalaryBand();
        saved.setId(1L);
        saved.setPayGrade(testPayGrade);
        saved.setBandName("Engineering Band A");
        saved.setJobFamily("Engineering");
        saved.setMinSalary(new BigDecimal("25000"));
        saved.setMaxSalary(new BigDecimal("35000"));
        saved.setIsActive(true);

        when(payGradeRepository.findById(1L)).thenReturn(Optional.of(testPayGrade));
        when(salaryBandRepository.save(any(SalaryBand.class))).thenReturn(saved);

        SalaryBandResponse response = compensationService.createSalaryBand(request);

        assertThat(response).isNotNull();
        assertThat(response.getBandName()).isEqualTo("Engineering Band A");
        assertThat(response.getPayGradeId()).isEqualTo(1L);
    }

    @Test
    void createSalaryBand_InvalidRange_ThrowsException() {
        SalaryBandRequest request = new SalaryBandRequest();
        request.setPayGradeId(1L);
        request.setBandName("Invalid Band");
        request.setMinSalary(new BigDecimal("50000"));
        request.setMaxSalary(new BigDecimal("30000")); // max < min

        assertThrows(IllegalArgumentException.class,
                () -> compensationService.createSalaryBand(request));
        verify(salaryBandRepository, never()).save(any());
    }

    // ==================== Compensation Review Tests ====================

    @Test
    void createReview_ValidRequest_ReturnsResponse() {
        CompensationReviewRequest request = new CompensationReviewRequest();
        request.setEmployeeId(1L);
        request.setReviewType(ReviewType.ANNUAL);
        request.setCurrentSalary(new BigDecimal("30000"));
        request.setProposedSalary(new BigDecimal("33000"));
        request.setEffectiveDate(LocalDate.now().plusMonths(1));

        CompensationReview saved = new CompensationReview();
        saved.setId(1L);
        saved.setEmployee(testEmployee);
        saved.setReviewType(ReviewType.ANNUAL);
        saved.setStatus(ReviewStatus.DRAFT);
        saved.setCurrentSalary(new BigDecimal("30000"));
        saved.setProposedSalary(new BigDecimal("33000"));

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(compensationReviewRepository.save(any(CompensationReview.class))).thenReturn(saved);

        CompensationReviewResponse response = compensationService.createReview(request);

        assertThat(response).isNotNull();
        assertThat(response.getReviewType()).isEqualTo(ReviewType.ANNUAL);
        assertThat(response.getStatus()).isEqualTo(ReviewStatus.DRAFT);
    }

    @Test
    void createReview_EmployeeNotFound_ThrowsException() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        CompensationReviewRequest request = new CompensationReviewRequest();
        request.setEmployeeId(99L);
        request.setReviewType(ReviewType.MERIT);

        assertThrows(IllegalArgumentException.class,
                () -> compensationService.createReview(request));
    }

    @Test
    void submitForApproval_DraftReview_ChangesStatus() {
        CompensationReview review = new CompensationReview();
        review.setId(1L);
        review.setEmployee(testEmployee);
        review.setReviewType(ReviewType.ANNUAL);
        review.setStatus(ReviewStatus.DRAFT);

        CompensationReview updated = new CompensationReview();
        updated.setId(1L);
        updated.setEmployee(testEmployee);
        updated.setReviewType(ReviewType.ANNUAL);
        updated.setStatus(ReviewStatus.PENDING_APPROVAL);

        when(compensationReviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(compensationReviewRepository.save(any(CompensationReview.class))).thenReturn(updated);

        CompensationReviewResponse response = compensationService.submitForApproval(1L);

        assertThat(response.getStatus()).isEqualTo(ReviewStatus.PENDING_APPROVAL);
    }

    @Test
    void submitForApproval_AlreadyApproved_ThrowsException() {
        CompensationReview review = new CompensationReview();
        review.setId(1L);
        review.setStatus(ReviewStatus.APPROVED);

        when(compensationReviewRepository.findById(1L)).thenReturn(Optional.of(review));

        assertThrows(IllegalStateException.class,
                () -> compensationService.submitForApproval(1L));
    }

    @Test
    void approveReview_PendingApproval_SetsApproved() {
        CompensationReview review = new CompensationReview();
        review.setId(1L);
        review.setEmployee(testEmployee);
        review.setStatus(ReviewStatus.PENDING_APPROVAL);
        review.setProposedSalary(new BigDecimal("33000"));

        CompensationReview approved = new CompensationReview();
        approved.setId(1L);
        approved.setEmployee(testEmployee);
        approved.setStatus(ReviewStatus.APPROVED);
        approved.setApprovedBy("manager@example.com");
        approved.setApprovedSalary(new BigDecimal("33000"));

        when(compensationReviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(compensationReviewRepository.save(any(CompensationReview.class))).thenReturn(approved);

        CompensationReviewResponse response = compensationService.approveReview(
                1L, "manager@example.com", new BigDecimal("33000"), "Good performance");

        assertThat(response.getStatus()).isEqualTo(ReviewStatus.APPROVED);
        assertThat(response.getApprovedBy()).isEqualTo("manager@example.com");
    }

    @Test
    void rejectReview_PendingApproval_SetsRejected() {
        CompensationReview review = new CompensationReview();
        review.setId(1L);
        review.setEmployee(testEmployee);
        review.setStatus(ReviewStatus.PENDING_APPROVAL);

        CompensationReview rejected = new CompensationReview();
        rejected.setId(1L);
        rejected.setEmployee(testEmployee);
        rejected.setStatus(ReviewStatus.REJECTED);

        when(compensationReviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(compensationReviewRepository.save(any(CompensationReview.class))).thenReturn(rejected);

        CompensationReviewResponse response = compensationService.rejectReview(
                1L, "manager@example.com", "Budget constraints");

        assertThat(response.getStatus()).isEqualTo(ReviewStatus.REJECTED);
    }

    @Test
    void updateReview_NonDraftStatus_ThrowsException() {
        CompensationReview review = new CompensationReview();
        review.setId(1L);
        review.setStatus(ReviewStatus.PENDING_APPROVAL);

        when(compensationReviewRepository.findById(1L)).thenReturn(Optional.of(review));

        CompensationReviewRequest request = new CompensationReviewRequest();
        request.setEmployeeId(1L);
        request.setReviewType(ReviewType.MERIT);

        assertThrows(IllegalStateException.class,
                () -> compensationService.updateReview(1L, request));
    }

    // ==================== Total Rewards Tests ====================

    @Test
    void createTotalRewardsStatement_ValidRequest_CalculatesTotal() {
        TotalRewardsRequest request = new TotalRewardsRequest();
        request.setEmployeeId(1L);
        request.setStatementDate(LocalDate.now());
        request.setPeriodStart(LocalDate.now().minusYears(1));
        request.setPeriodEnd(LocalDate.now());
        request.setBaseSalary(new BigDecimal("30000"));
        request.setBonus(new BigDecimal("5000"));
        request.setMedicalAidContribution(new BigDecimal("2000"));

        TotalRewardsStatement saved = new TotalRewardsStatement();
        saved.setId(1L);
        saved.setEmployee(testEmployee);
        saved.setStatementDate(LocalDate.now());
        saved.setBaseSalary(new BigDecimal("30000"));
        saved.setBonus(new BigDecimal("5000"));
        saved.setMedicalAidContribution(new BigDecimal("2000"));
        saved.setTotalRemuneration(new BigDecimal("37000"));

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(totalRewardsStatementRepository.save(any(TotalRewardsStatement.class))).thenReturn(saved);

        TotalRewardsResponse response = compensationService.createTotalRewardsStatement(request);

        assertThat(response).isNotNull();
        assertThat(response.getTotalRemuneration()).isEqualByComparingTo(new BigDecimal("37000"));
        verify(totalRewardsStatementRepository, times(1)).save(any());
    }

    @Test
    void getLatestStatement_NoStatement_ThrowsException() {
        when(totalRewardsStatementRepository.findTopByEmployeeIdOrderByStatementDateDesc(99L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> compensationService.getLatestStatementForEmployee(99L));
    }

    // ==================== Benefit Tests ====================

    @Test
    void createBenefit_ValidRequest_ReturnsResponse() {
        BenefitRequest request = new BenefitRequest();
        request.setEmployeeId(1L);
        request.setBenefitType(BenefitType.MEDICAL_AID);
        request.setBenefitName("Discovery Health");
        request.setProvider("Discovery");
        request.setEmployeeContribution(new BigDecimal("1500"));
        request.setEmployerContribution(new BigDecimal("2000"));

        Benefit saved = new Benefit();
        saved.setId(1L);
        saved.setEmployee(testEmployee);
        saved.setBenefitType(BenefitType.MEDICAL_AID);
        saved.setBenefitName("Discovery Health");
        saved.setProvider("Discovery");
        saved.setEmployeeContribution(new BigDecimal("1500"));
        saved.setEmployerContribution(new BigDecimal("2000"));
        saved.setIsActive(true);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(benefitRepository.save(any(Benefit.class))).thenReturn(saved);

        BenefitResponse response = compensationService.createBenefit(request);

        assertThat(response).isNotNull();
        assertThat(response.getBenefitType()).isEqualTo(BenefitType.MEDICAL_AID);
        assertThat(response.getBenefitName()).isEqualTo("Discovery Health");
        assertThat(response.getTotalContribution()).isEqualByComparingTo(new BigDecimal("3500"));
    }

    @Test
    void deleteBenefit_SetsInactive() {
        Benefit benefit = new Benefit();
        benefit.setId(1L);
        benefit.setEmployee(testEmployee);
        benefit.setBenefitType(BenefitType.RETIREMENT_FUND);
        benefit.setIsActive(true);

        when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefit));
        when(benefitRepository.save(any(Benefit.class))).thenReturn(benefit);

        compensationService.deleteBenefit(1L);

        assertThat(benefit.getIsActive()).isFalse();
        verify(benefitRepository, times(1)).save(benefit);
    }

    @Test
    void getBenefitsByEmployee_ReturnsList() {
        Benefit benefit = new Benefit();
        benefit.setId(1L);
        benefit.setEmployee(testEmployee);
        benefit.setBenefitType(BenefitType.MEDICAL_AID);
        benefit.setBenefitName("Test Benefit");
        benefit.setIsActive(true);

        when(benefitRepository.findByEmployeeId(1L)).thenReturn(Arrays.asList(benefit));

        List<BenefitResponse> benefits = compensationService.getBenefitsByEmployee(1L);

        assertThat(benefits).hasSize(1);
        assertThat(benefits.get(0).getBenefitType()).isEqualTo(BenefitType.MEDICAL_AID);
    }

    // ==================== PayGrade Business Logic Tests ====================

    @Test
    void payGrade_IsWithinRange_WorksCorrectly() {
        assertThat(testPayGrade.isWithinRange(new BigDecimal("25000"))).isTrue();
        assertThat(testPayGrade.isWithinRange(new BigDecimal("45000"))).isFalse();
        assertThat(testPayGrade.isWithinRange(new BigDecimal("15000"))).isFalse();
    }

    @Test
    void payGrade_GetCompaRatio_CalculatesCorrectly() {
        BigDecimal salary = new BigDecimal("30000"); // same as mid
        BigDecimal ratio = testPayGrade.getCompaRatio(salary);
        assertThat(ratio).isEqualByComparingTo(new BigDecimal("1.0000"));
    }

    @Test
    void compensationReview_CalculateIncreasePercentage_WorksCorrectly() {
        CompensationReview review = new CompensationReview();
        review.setCurrentSalary(new BigDecimal("30000"));
        review.setProposedSalary(new BigDecimal("33000"));
        review.calculateIncreasePercentage();

        // 10% increase
        assertThat(review.getIncreasePercentage()).isEqualByComparingTo(new BigDecimal("10.00"));
    }

    @Test
    void totalRewardsStatement_CalculateTotalRemuneration_SumsAllComponents() {
        TotalRewardsStatement statement = new TotalRewardsStatement();
        statement.setBaseSalary(new BigDecimal("30000"));
        statement.setBonus(new BigDecimal("5000"));
        statement.setMedicalAidContribution(new BigDecimal("2000"));
        statement.setRetirementFundContribution(new BigDecimal("3000"));
        statement.setTravelAllowance(new BigDecimal("1000"));

        statement.calculateTotalRemuneration();

        assertThat(statement.getTotalRemuneration()).isEqualByComparingTo(new BigDecimal("41000"));
    }
}
