package com.arthmatic.shumelahire.dto.performance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class KRARequest {

    @NotNull(message = "Contract ID is required")
    private Long contractId;

    @NotBlank(message = "KRA name is required")
    private String name;

    private String description;

    @Positive(message = "Weighting must be positive")
    private BigDecimal weighting;

    private Integer sortOrder;

    // Getters and Setters
    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getWeighting() { return weighting; }
    public void setWeighting(BigDecimal weighting) { this.weighting = weighting; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
