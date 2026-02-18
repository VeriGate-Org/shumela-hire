package com.arthmatic.shumelahire.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "recruitment_metrics")
public class RecruitmentMetrics extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "metric_date", nullable = false)
    private LocalDate metricDate;

    @Column(name = "metric_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MetricType metricType;

    @Column(name = "metric_category", nullable = false)
    private String metricCategory; // APPLICATIONS, INTERVIEWS, OFFERS, HIRES, PIPELINE

    @Column(name = "metric_name", nullable = false)
    private String metricName;

    @Column(name = "metric_value", precision = 15, scale = 4, nullable = false)
    private BigDecimal metricValue;

    @Column(name = "department")
    private String department;

    @Column(name = "job_posting_id")
    private Long jobPostingId;

    @Column(name = "recruiter_id")
    private Long recruiterId;

    @Column(name = "hiring_manager_id")
    private Long hiringManagerId;

    @Column(name = "period_start_date")
    private LocalDate periodStartDate;

    @Column(name = "period_end_date")
    private LocalDate periodEndDate;

    @Column(name = "target_value", precision = 15, scale = 4)
    private BigDecimal targetValue;

    @Column(name = "previous_period_value", precision = 15, scale = 4)
    private BigDecimal previousPeriodValue;

    @Column(name = "variance_percentage", precision = 10, scale = 4)
    private BigDecimal variancePercentage;

    @Column(name = "trend_direction")
    @Enumerated(EnumType.STRING)
    private TrendDirection trendDirection;

    @Column(name = "benchmark_value", precision = 15, scale = 4)
    private BigDecimal benchmarkValue;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "data_source")
    private String dataSource;

    @Column(name = "calculation_method")
    private String calculationMethod;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private Long createdBy;

    // Constructors
    public RecruitmentMetrics() {
        this.createdAt = LocalDateTime.now();
    }

    public RecruitmentMetrics(LocalDate metricDate, MetricType metricType, String metricCategory, 
                            String metricName, BigDecimal metricValue) {
        this();
        this.metricDate = metricDate;
        this.metricType = metricType;
        this.metricCategory = metricCategory;
        this.metricName = metricName;
        this.metricValue = metricValue;
    }

    // Lifecycle callbacks
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Business methods
    public boolean isAboveTarget() {
        return targetValue != null && metricValue.compareTo(targetValue) > 0;
    }

    public boolean isBelowTarget() {
        return targetValue != null && metricValue.compareTo(targetValue) < 0;
    }

    public BigDecimal getTargetVariance() {
        if (targetValue == null || targetValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return metricValue.subtract(targetValue)
                         .divide(targetValue, 4, RoundingMode.HALF_UP)
                         .multiply(BigDecimal.valueOf(100));
    }

    public BigDecimal getPeriodOverPeriodChange() {
        if (previousPeriodValue == null || previousPeriodValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return metricValue.subtract(previousPeriodValue)
                         .divide(previousPeriodValue, 4, RoundingMode.HALF_UP)
                         .multiply(BigDecimal.valueOf(100));
    }

    public String getPerformanceStatus() {
        if (targetValue == null) return "NO_TARGET";
        
        BigDecimal variance = getTargetVariance();
        if (variance.compareTo(BigDecimal.valueOf(10)) > 0) return "EXCEEDING";
        if (variance.compareTo(BigDecimal.valueOf(-10)) < 0) return "BELOW_TARGET";
        return "ON_TARGET";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getMetricDate() {
        return metricDate;
    }

    public void setMetricDate(LocalDate metricDate) {
        this.metricDate = metricDate;
    }

    public MetricType getMetricType() {
        return metricType;
    }

    public void setMetricType(MetricType metricType) {
        this.metricType = metricType;
    }

    public String getMetricCategory() {
        return metricCategory;
    }

    public void setMetricCategory(String metricCategory) {
        this.metricCategory = metricCategory;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public BigDecimal getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(BigDecimal metricValue) {
        this.metricValue = metricValue;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Long getJobPostingId() {
        return jobPostingId;
    }

    public void setJobPostingId(Long jobPostingId) {
        this.jobPostingId = jobPostingId;
    }

    public Long getRecruiterId() {
        return recruiterId;
    }

    public void setRecruiterId(Long recruiterId) {
        this.recruiterId = recruiterId;
    }

    public Long getHiringManagerId() {
        return hiringManagerId;
    }

    public void setHiringManagerId(Long hiringManagerId) {
        this.hiringManagerId = hiringManagerId;
    }

    public LocalDate getPeriodStartDate() {
        return periodStartDate;
    }

    public void setPeriodStartDate(LocalDate periodStartDate) {
        this.periodStartDate = periodStartDate;
    }

    public LocalDate getPeriodEndDate() {
        return periodEndDate;
    }

    public void setPeriodEndDate(LocalDate periodEndDate) {
        this.periodEndDate = periodEndDate;
    }

    public BigDecimal getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(BigDecimal targetValue) {
        this.targetValue = targetValue;
    }

    public BigDecimal getPreviousPeriodValue() {
        return previousPeriodValue;
    }

    public void setPreviousPeriodValue(BigDecimal previousPeriodValue) {
        this.previousPeriodValue = previousPeriodValue;
    }

    public BigDecimal getVariancePercentage() {
        return variancePercentage;
    }

    public void setVariancePercentage(BigDecimal variancePercentage) {
        this.variancePercentage = variancePercentage;
    }

    public TrendDirection getTrendDirection() {
        return trendDirection;
    }

    public void setTrendDirection(TrendDirection trendDirection) {
        this.trendDirection = trendDirection;
    }

    public BigDecimal getBenchmarkValue() {
        return benchmarkValue;
    }

    public void setBenchmarkValue(BigDecimal benchmarkValue) {
        this.benchmarkValue = benchmarkValue;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getCalculationMethod() {
        return calculationMethod;
    }

    public void setCalculationMethod(String calculationMethod) {
        this.calculationMethod = calculationMethod;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
}