package com.arthmatic.shumelahire.entity.compensation;

import com.arthmatic.shumelahire.entity.TenantAwareEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "salary_bands")
public class SalaryBand extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pay_grade_id", nullable = false)
    @NotNull(message = "Pay grade is required")
    private PayGrade payGrade;

    @NotBlank
    @Column(name = "band_name", nullable = false, length = 100)
    private String bandName;

    @Column(name = "job_family", length = 100)
    private String jobFamily;

    @Column(name = "job_level", length = 50)
    private String jobLevel;

    @NotNull
    @Column(name = "min_salary", nullable = false, precision = 15, scale = 2)
    private BigDecimal minSalary;

    @NotNull
    @Column(name = "max_salary", nullable = false, precision = 15, scale = 2)
    private BigDecimal maxSalary;

    @Column(name = "currency", length = 10)
    private String currency = "ZAR";

    @Column(name = "effective_date")
    private LocalDateTime effectiveDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public SalaryBand() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PayGrade getPayGrade() { return payGrade; }
    public void setPayGrade(PayGrade payGrade) { this.payGrade = payGrade; }

    public String getBandName() { return bandName; }
    public void setBandName(String bandName) { this.bandName = bandName; }

    public String getJobFamily() { return jobFamily; }
    public void setJobFamily(String jobFamily) { this.jobFamily = jobFamily; }

    public String getJobLevel() { return jobLevel; }
    public void setJobLevel(String jobLevel) { this.jobLevel = jobLevel; }

    public BigDecimal getMinSalary() { return minSalary; }
    public void setMinSalary(BigDecimal minSalary) { this.minSalary = minSalary; }

    public BigDecimal getMaxSalary() { return maxSalary; }
    public void setMaxSalary(BigDecimal maxSalary) { this.maxSalary = maxSalary; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDateTime getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDateTime effectiveDate) { this.effectiveDate = effectiveDate; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
