package com.arthmatic.shumelahire.entity;

import com.arthmatic.shumelahire.config.tenant.TenantContext;
import jakarta.persistence.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@MappedSuperclass
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = String.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public abstract class TenantAwareEntity {

    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;

    @PrePersist
    protected void prePersistTenant() {
        if (this.tenantId == null) {
            this.tenantId = TenantContext.requireCurrentTenant();
        }
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
