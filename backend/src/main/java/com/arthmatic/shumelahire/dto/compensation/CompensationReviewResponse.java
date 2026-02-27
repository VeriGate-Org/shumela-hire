package com.arthmatic.shumelahire.dto.compensation;

import com.arthmatic.shumelahire.entity.compensation.CompensationReview;
import com.arthmatic.shumelahire.entity.compensation.ReviewStatus;
import com.arthmatic.shumelahire.entity.compensation.ReviewType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CompensationReviewResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private Long payGradeId;
    private String payGradeCode;
    private ReviewType reviewType;
    private ReviewStatus status;
    private BigDecimal currentSalary;
    private BigDecimal proposedSalary;
    private BigDecimal approvedSalary;
    private BigDecimal increasePercentage;
    private LocalDate effectiveDate;
    private LocalDate reviewDate;
    private String justification;
    private String approverNotes;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private LocalDateTime implementedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CompensationReviewResponse fromEntity(CompensationReview review) {
        CompensationReviewResponse response = new CompensationReviewResponse();
        response.setId(review.getId());
        if (review.getEmployee() != null) {
            response.setEmployeeId(review.getEmployee().getId());
            response.setEmployeeName(review.getEmployee().getFullName());
        }
        if (review.getPayGrade() != null) {
            response.setPayGradeId(review.getPayGrade().getId());
            response.setPayGradeCode(review.getPayGrade().getCode());
        }
        response.setReviewType(review.getReviewType());
        response.setStatus(review.getStatus());
        response.setCurrentSalary(review.getCurrentSalary());
        response.setProposedSalary(review.getProposedSalary());
        response.setApprovedSalary(review.getApprovedSalary());
        response.setIncreasePercentage(review.getIncreasePercentage());
        response.setEffectiveDate(review.getEffectiveDate());
        response.setReviewDate(review.getReviewDate());
        response.setJustification(review.getJustification());
        response.setApproverNotes(review.getApproverNotes());
        response.setApprovedBy(review.getApprovedBy());
        response.setApprovedAt(review.getApprovedAt());
        response.setImplementedAt(review.getImplementedAt());
        response.setCreatedAt(review.getCreatedAt());
        response.setUpdatedAt(review.getUpdatedAt());
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public Long getPayGradeId() { return payGradeId; }
    public void setPayGradeId(Long payGradeId) { this.payGradeId = payGradeId; }

    public String getPayGradeCode() { return payGradeCode; }
    public void setPayGradeCode(String payGradeCode) { this.payGradeCode = payGradeCode; }

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
