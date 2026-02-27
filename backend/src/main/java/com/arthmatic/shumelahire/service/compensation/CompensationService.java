package com.arthmatic.shumelahire.service.compensation;

import com.arthmatic.shumelahire.dto.compensation.*;
import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.compensation.*;
import com.arthmatic.shumelahire.repository.EmployeeRepository;
import com.arthmatic.shumelahire.repository.compensation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompensationService {

    private static final Logger logger = LoggerFactory.getLogger(CompensationService.class);

    @Autowired
    private PayGradeRepository payGradeRepository;

    @Autowired
    private SalaryBandRepository salaryBandRepository;

    @Autowired
    private CompensationReviewRepository compensationReviewRepository;

    @Autowired
    private TotalRewardsStatementRepository totalRewardsStatementRepository;

    @Autowired
    private BenefitRepository benefitRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    // ==================== Pay Grade Operations ====================

    public PayGradeResponse createPayGrade(PayGradeRequest request) {
        logger.info("Creating pay grade: {}", request.getCode());

        validateSalaryRange(request.getMinSalary(), request.getMidSalary(), request.getMaxSalary());

        PayGrade payGrade = new PayGrade();
        payGrade.setCode(request.getCode().toUpperCase().trim());
        payGrade.setName(request.getName());
        payGrade.setDescription(request.getDescription());
        payGrade.setMinSalary(request.getMinSalary());
        payGrade.setMidSalary(request.getMidSalary());
        payGrade.setMaxSalary(request.getMaxSalary());
        payGrade.setCurrency(request.getCurrency() != null ? request.getCurrency() : "ZAR");
        payGrade.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        PayGrade saved = payGradeRepository.save(payGrade);
        logger.info("Pay grade created: {} (id={})", saved.getCode(), saved.getId());
        return PayGradeResponse.fromEntity(saved);
    }

    public PayGradeResponse updatePayGrade(Long id, PayGradeRequest request) {
        logger.info("Updating pay grade: {}", id);

        PayGrade payGrade = findPayGradeById(id);
        validateSalaryRange(request.getMinSalary(), request.getMidSalary(), request.getMaxSalary());

        payGrade.setName(request.getName());
        payGrade.setDescription(request.getDescription());
        payGrade.setMinSalary(request.getMinSalary());
        payGrade.setMidSalary(request.getMidSalary());
        payGrade.setMaxSalary(request.getMaxSalary());
        if (request.getCurrency() != null) payGrade.setCurrency(request.getCurrency());
        if (request.getIsActive() != null) payGrade.setIsActive(request.getIsActive());

        PayGrade saved = payGradeRepository.save(payGrade);
        return PayGradeResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public PayGradeResponse getPayGrade(Long id) {
        return PayGradeResponse.fromEntity(findPayGradeById(id));
    }

    @Transactional(readOnly = true)
    public List<PayGradeResponse> getAllPayGrades() {
        return payGradeRepository.findAll().stream()
                .map(PayGradeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PayGradeResponse> getActivePayGrades() {
        return payGradeRepository.findAll().stream()
                .filter(pg -> Boolean.TRUE.equals(pg.getIsActive()))
                .map(PayGradeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public void deletePayGrade(Long id) {
        PayGrade payGrade = findPayGradeById(id);
        payGrade.setIsActive(false);
        payGradeRepository.save(payGrade);
        logger.info("Pay grade deactivated: {}", id);
    }

    // ==================== Salary Band Operations ====================

    public SalaryBandResponse createSalaryBand(SalaryBandRequest request) {
        logger.info("Creating salary band: {} for pay grade {}", request.getBandName(), request.getPayGradeId());

        if (request.getMinSalary().compareTo(request.getMaxSalary()) > 0) {
            throw new IllegalArgumentException("Minimum salary cannot be greater than maximum salary");
        }

        PayGrade payGrade = findPayGradeById(request.getPayGradeId());

        SalaryBand band = new SalaryBand();
        band.setPayGrade(payGrade);
        band.setBandName(request.getBandName());
        band.setJobFamily(request.getJobFamily());
        band.setJobLevel(request.getJobLevel());
        band.setMinSalary(request.getMinSalary());
        band.setMaxSalary(request.getMaxSalary());
        band.setCurrency(request.getCurrency() != null ? request.getCurrency() : "ZAR");
        band.setEffectiveDate(request.getEffectiveDate());
        band.setExpiryDate(request.getExpiryDate());
        band.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        SalaryBand saved = salaryBandRepository.save(band);
        logger.info("Salary band created: {} (id={})", saved.getBandName(), saved.getId());
        return SalaryBandResponse.fromEntity(saved);
    }

    public SalaryBandResponse updateSalaryBand(Long id, SalaryBandRequest request) {
        logger.info("Updating salary band: {}", id);

        SalaryBand band = findSalaryBandById(id);
        if (request.getMinSalary().compareTo(request.getMaxSalary()) > 0) {
            throw new IllegalArgumentException("Minimum salary cannot be greater than maximum salary");
        }

        band.setBandName(request.getBandName());
        band.setJobFamily(request.getJobFamily());
        band.setJobLevel(request.getJobLevel());
        band.setMinSalary(request.getMinSalary());
        band.setMaxSalary(request.getMaxSalary());
        if (request.getCurrency() != null) band.setCurrency(request.getCurrency());
        band.setEffectiveDate(request.getEffectiveDate());
        band.setExpiryDate(request.getExpiryDate());
        if (request.getIsActive() != null) band.setIsActive(request.getIsActive());

        SalaryBand saved = salaryBandRepository.save(band);
        return SalaryBandResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public SalaryBandResponse getSalaryBand(Long id) {
        return SalaryBandResponse.fromEntity(findSalaryBandById(id));
    }

    @Transactional(readOnly = true)
    public List<SalaryBandResponse> getSalaryBandsByPayGrade(Long payGradeId) {
        return salaryBandRepository.findByPayGradeId(payGradeId).stream()
                .map(SalaryBandResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public void deleteSalaryBand(Long id) {
        SalaryBand band = findSalaryBandById(id);
        band.setIsActive(false);
        salaryBandRepository.save(band);
        logger.info("Salary band deactivated: {}", id);
    }

    // ==================== Compensation Review Operations ====================

    public CompensationReviewResponse createReview(CompensationReviewRequest request) {
        logger.info("Creating compensation review for employee: {}", request.getEmployeeId());

        Employee employee = findEmployeeById(request.getEmployeeId());

        CompensationReview review = new CompensationReview();
        review.setEmployee(employee);
        review.setReviewType(request.getReviewType());
        review.setStatus(ReviewStatus.DRAFT);
        review.setCurrentSalary(request.getCurrentSalary());
        review.setProposedSalary(request.getProposedSalary());
        review.setEffectiveDate(request.getEffectiveDate());
        review.setReviewDate(request.getReviewDate());
        review.setJustification(request.getJustification());

        if (request.getPayGradeId() != null) {
            review.setPayGrade(findPayGradeById(request.getPayGradeId()));
        }

        review.calculateIncreasePercentage();

        CompensationReview saved = compensationReviewRepository.save(review);
        logger.info("Compensation review created: {} (id={})", saved.getReviewType(), saved.getId());
        return CompensationReviewResponse.fromEntity(saved);
    }

    public CompensationReviewResponse updateReview(Long id, CompensationReviewRequest request) {
        logger.info("Updating compensation review: {}", id);

        CompensationReview review = findReviewById(id);
        if (review.getStatus() != ReviewStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT reviews can be updated");
        }

        review.setReviewType(request.getReviewType());
        review.setCurrentSalary(request.getCurrentSalary());
        review.setProposedSalary(request.getProposedSalary());
        review.setEffectiveDate(request.getEffectiveDate());
        review.setReviewDate(request.getReviewDate());
        review.setJustification(request.getJustification());
        if (request.getPayGradeId() != null) {
            review.setPayGrade(findPayGradeById(request.getPayGradeId()));
        }
        review.calculateIncreasePercentage();

        CompensationReview saved = compensationReviewRepository.save(review);
        return CompensationReviewResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public CompensationReviewResponse getReview(Long id) {
        return CompensationReviewResponse.fromEntity(findReviewById(id));
    }

    @Transactional(readOnly = true)
    public List<CompensationReviewResponse> getReviewsByEmployee(Long employeeId) {
        return compensationReviewRepository.findByEmployeeId(employeeId).stream()
                .map(CompensationReviewResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<CompensationReviewResponse> getAllReviews(Pageable pageable) {
        return compensationReviewRepository.findAll(pageable)
                .map(CompensationReviewResponse::fromEntity);
    }

    public CompensationReviewResponse submitForApproval(Long id) {
        logger.info("Submitting review for approval: {}", id);
        CompensationReview review = findReviewById(id);
        if (review.getStatus() != ReviewStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT reviews can be submitted for approval");
        }
        review.setStatus(ReviewStatus.PENDING_APPROVAL);
        return CompensationReviewResponse.fromEntity(compensationReviewRepository.save(review));
    }

    public CompensationReviewResponse approveReview(Long id, String approver, BigDecimal approvedSalary, String notes) {
        logger.info("Approving review: {} by {}", id, approver);
        CompensationReview review = findReviewById(id);
        review.approve(approver, approvedSalary, notes);
        return CompensationReviewResponse.fromEntity(compensationReviewRepository.save(review));
    }

    public CompensationReviewResponse rejectReview(Long id, String approver, String notes) {
        logger.info("Rejecting review: {} by {}", id, approver);
        CompensationReview review = findReviewById(id);
        review.reject(approver, notes);
        return CompensationReviewResponse.fromEntity(compensationReviewRepository.save(review));
    }

    public CompensationReviewResponse implementReview(Long id) {
        logger.info("Implementing review: {}", id);
        CompensationReview review = findReviewById(id);
        review.implement();
        return CompensationReviewResponse.fromEntity(compensationReviewRepository.save(review));
    }

    // ==================== Total Rewards Statement Operations ====================

    public TotalRewardsResponse createTotalRewardsStatement(TotalRewardsRequest request) {
        logger.info("Creating total rewards statement for employee: {}", request.getEmployeeId());

        Employee employee = findEmployeeById(request.getEmployeeId());

        TotalRewardsStatement statement = new TotalRewardsStatement();
        statement.setEmployee(employee);
        statement.setStatementDate(request.getStatementDate());
        statement.setPeriodStart(request.getPeriodStart());
        statement.setPeriodEnd(request.getPeriodEnd());
        statement.setBaseSalary(request.getBaseSalary());
        statement.setBonus(request.getBonus());
        statement.setCommission(request.getCommission());
        statement.setIncentives(request.getIncentives());
        statement.setMedicalAidContribution(request.getMedicalAidContribution());
        statement.setRetirementFundContribution(request.getRetirementFundContribution());
        statement.setLifeInsuranceContribution(request.getLifeInsuranceContribution());
        statement.setOtherBenefits(request.getOtherBenefits());
        statement.setTravelAllowance(request.getTravelAllowance());
        statement.setHousingAllowance(request.getHousingAllowance());
        statement.setOtherAllowances(request.getOtherAllowances());
        statement.setCurrency(request.getCurrency() != null ? request.getCurrency() : "ZAR");
        statement.setNotes(request.getNotes());
        statement.setGeneratedBy(request.getGeneratedBy());
        statement.calculateTotalRemuneration();

        TotalRewardsStatement saved = totalRewardsStatementRepository.save(statement);
        logger.info("Total rewards statement created for employee {} (id={})", request.getEmployeeId(), saved.getId());
        return TotalRewardsResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public TotalRewardsResponse getTotalRewardsStatement(Long id) {
        return TotalRewardsResponse.fromEntity(findStatementById(id));
    }

    @Transactional(readOnly = true)
    public List<TotalRewardsResponse> getStatementsByEmployee(Long employeeId) {
        return totalRewardsStatementRepository.findByEmployeeIdOrderByStatementDateDesc(employeeId).stream()
                .map(TotalRewardsResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TotalRewardsResponse getLatestStatementForEmployee(Long employeeId) {
        return totalRewardsStatementRepository.findTopByEmployeeIdOrderByStatementDateDesc(employeeId)
                .map(TotalRewardsResponse::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("No total rewards statement found for employee: " + employeeId));
    }

    // ==================== Benefit Operations ====================

    public BenefitResponse createBenefit(BenefitRequest request) {
        logger.info("Creating benefit for employee: {}", request.getEmployeeId());

        Employee employee = findEmployeeById(request.getEmployeeId());

        Benefit benefit = new Benefit();
        benefit.setEmployee(employee);
        benefit.setBenefitType(request.getBenefitType());
        benefit.setBenefitName(request.getBenefitName());
        benefit.setProvider(request.getProvider());
        benefit.setPolicyNumber(request.getPolicyNumber());
        benefit.setEmployeeContribution(request.getEmployeeContribution());
        benefit.setEmployerContribution(request.getEmployerContribution());
        benefit.setCurrency(request.getCurrency() != null ? request.getCurrency() : "ZAR");
        benefit.setStartDate(request.getStartDate());
        benefit.setEndDate(request.getEndDate());
        benefit.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        benefit.setNotes(request.getNotes());

        Benefit saved = benefitRepository.save(benefit);
        logger.info("Benefit created for employee {} (id={})", request.getEmployeeId(), saved.getId());
        return BenefitResponse.fromEntity(saved);
    }

    public BenefitResponse updateBenefit(Long id, BenefitRequest request) {
        logger.info("Updating benefit: {}", id);

        Benefit benefit = findBenefitById(id);
        benefit.setBenefitType(request.getBenefitType());
        benefit.setBenefitName(request.getBenefitName());
        benefit.setProvider(request.getProvider());
        benefit.setPolicyNumber(request.getPolicyNumber());
        benefit.setEmployeeContribution(request.getEmployeeContribution());
        benefit.setEmployerContribution(request.getEmployerContribution());
        if (request.getCurrency() != null) benefit.setCurrency(request.getCurrency());
        benefit.setStartDate(request.getStartDate());
        benefit.setEndDate(request.getEndDate());
        if (request.getIsActive() != null) benefit.setIsActive(request.getIsActive());
        benefit.setNotes(request.getNotes());

        Benefit saved = benefitRepository.save(benefit);
        return BenefitResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public BenefitResponse getBenefit(Long id) {
        return BenefitResponse.fromEntity(findBenefitById(id));
    }

    @Transactional(readOnly = true)
    public List<BenefitResponse> getBenefitsByEmployee(Long employeeId) {
        return benefitRepository.findByEmployeeId(employeeId).stream()
                .map(BenefitResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BenefitResponse> getActiveBenefitsByEmployee(Long employeeId) {
        return benefitRepository.findActiveByEmployeeId(employeeId).stream()
                .map(BenefitResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public void deleteBenefit(Long id) {
        Benefit benefit = findBenefitById(id);
        benefit.setIsActive(false);
        benefitRepository.save(benefit);
        logger.info("Benefit deactivated: {}", id);
    }

    // ==================== Private Helpers ====================

    private PayGrade findPayGradeById(Long id) {
        return payGradeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pay grade not found: " + id));
    }

    private SalaryBand findSalaryBandById(Long id) {
        return salaryBandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Salary band not found: " + id));
    }

    private CompensationReview findReviewById(Long id) {
        return compensationReviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Compensation review not found: " + id));
    }

    private TotalRewardsStatement findStatementById(Long id) {
        return totalRewardsStatementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Total rewards statement not found: " + id));
    }

    private Benefit findBenefitById(Long id) {
        return benefitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Benefit not found: " + id));
    }

    private Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + id));
    }

    private void validateSalaryRange(BigDecimal min, BigDecimal mid, BigDecimal max) {
        if (min.compareTo(mid) > 0) {
            throw new IllegalArgumentException("Minimum salary cannot be greater than mid salary");
        }
        if (mid.compareTo(max) > 0) {
            throw new IllegalArgumentException("Mid salary cannot be greater than maximum salary");
        }
    }
}
