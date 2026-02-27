package com.arthmatic.shumelahire.entity.compensation;

import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.TenantAwareEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "compensation_reviews")
public class CompensationReview extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    @NotNull(message = "Employee is required")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pay_grade_id")
    private PayGrade payGrade;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_type", nullable = false, length = 50)
    @NotNull
    private ReviewType reviewType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ReviewStatus status = ReviewStatus.DRAFT;

    @Column(name = "current_salary", precision = 15, scale = 2)
    private BigDecimal currentSalary;

    @Column(name = "proposed_salary", precision = 15, scale = 2)
    private BigDecimal proposedSalary;

    @Column(name = "approved_salary", precision = 15, scale = 2)
    private BigDecimal approvedSalary;

    @Column(name = "increase_percentage", precision = 6, scale = 2)
    private BigDecimal increasePercentage;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "review_date")
    private LocalDate reviewDate;

    @Column(name = "justification", columnDefinition = "TEXT")
    private String justification;

    @Column(name = "approver_notes", columnDefinition = "TEXT")
    private String approverNotes;

    @Column(name = "approved_by", length = 255)
    private String approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "implemented_at")
    private LocalDateTime implementedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public CompensationReview() {}

    public void calculateIncreasePercentage() {
        if (currentSalary != null && currentSalary.compareTo(BigDecimal.ZERO) != 0 && proposedSalary != null) {
            BigDecimal diff = proposedSalary.subtract(currentSalary);
            this.increasePercentage = diff.divide(currentSalary, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, java.math.RoundingMode.HALF_UP);
        }
    }

    public void approve(String approver, BigDecimal approvedSalaryAmount, String notes) {
        if (status != ReviewStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Review is not pending approval");
        }
        this.status = ReviewStatus.APPROVED;
        this.approvedBy = approver;
        this.approvedSalary = approvedSalaryAmount;
        this.approverNotes = notes;
        this.approvedAt = LocalDateTime.now();
    }

    public void reject(String approver, String notes) {
        if (status != ReviewStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Review is not pending approval");
        }
        this.status = ReviewStatus.REJECTED;
        this.approvedBy = approver;
        this.approverNotes = notes;
        this.approvedAt = LocalDateTime.now();
    }

    public void implement() {
        if (status != ReviewStatus.APPROVED) {
            throw new IllegalStateException("Review must be approved before implementation");
        }
        this.status = ReviewStatus.IMPLEMENTED;
        this.implementedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public PayGrade getPayGrade() { return payGrade; }
    public void setPayGrade(PayGrade payGrade) { this.payGrade = payGrade; }

    public ReviewType getReviewType() { return reviewType; }
    public void setReviewType(ReviewType reviewType) { this.reviewType = reviewType; }

    public ReviewStatus getStatus() { return status; }
    public void setStatus(ReviewStatus status) { this.status = status; }

    public BigDecimal getCurrentSalary() { return currentSalary; }
    public void setCurrentSalary(BigDecimal currentSalary) { this.currentSalary = currentSalary; }

    public BigDecimal getProposedSalary() { return proposedSalary; }
    public void setProposedSalary(BigDecimal proposedSalary) { this.proposedSalary = proposedSalary; }

    public BigDecimal getApprovedSalary() { return approvedSalary; }
    public void setApprovedSalary(BigDecimal approvedSalary) { this.approvedSalary = approvedSalary; }

    public BigDecimal getIncreasePercentage() { return increasePercentage; }
    public void setIncreasePercentage(BigDecimal increasePercentage) { this.increasePercentage = increasePercentage; }

    public LocalDate getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }

    public LocalDate getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDate reviewDate) { this.reviewDate = reviewDate; }

    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }

    public String getApproverNotes() { return approverNotes; }
    public void setApproverNotes(String approverNotes) { this.approverNotes = approverNotes; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public LocalDateTime getImplementedAt() { return implementedAt; }
    public void setImplementedAt(LocalDateTime implementedAt) { this.implementedAt = implementedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
