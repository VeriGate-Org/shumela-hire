package com.arthmatic.shumelahire.entity.compensation;

import com.arthmatic.shumelahire.entity.TenantAwareEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pay_grades", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"code", "tenant_id"})
})
public class PayGrade extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @NotBlank
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(name = "min_salary", nullable = false, precision = 15, scale = 2)
    private BigDecimal minSalary;

    @NotNull
    @Column(name = "mid_salary", nullable = false, precision = 15, scale = 2)
    private BigDecimal midSalary;

    @NotNull
    @Column(name = "max_salary", nullable = false, precision = 15, scale = 2)
    private BigDecimal maxSalary;

    @Column(name = "currency", length = 10)
    private String currency = "ZAR";

    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "payGrade", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SalaryBand> salaryBands;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public PayGrade() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getMinSalary() { return minSalary; }
    public void setMinSalary(BigDecimal minSalary) { this.minSalary = minSalary; }

    public BigDecimal getMidSalary() { return midSalary; }
    public void setMidSalary(BigDecimal midSalary) { this.midSalary = midSalary; }

    public BigDecimal getMaxSalary() { return maxSalary; }
    public void setMaxSalary(BigDecimal maxSalary) { this.maxSalary = maxSalary; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public List<SalaryBand> getSalaryBands() { return salaryBands; }
    public void setSalaryBands(List<SalaryBand> salaryBands) { this.salaryBands = salaryBands; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isWithinRange(BigDecimal salary) {
        return salary != null
                && salary.compareTo(minSalary) >= 0
                && salary.compareTo(maxSalary) <= 0;
    }

    public BigDecimal getCompaRatio(BigDecimal salary) {
        if (salary == null || midSalary.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return salary.divide(midSalary, 4, java.math.RoundingMode.HALF_UP);
    }
}
