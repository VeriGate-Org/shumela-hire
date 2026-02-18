package com.arthmatic.shumelahire.config.tenant;

public final class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {}

    public static void setCurrentTenant(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static String getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    public static String requireCurrentTenant() {
        String tenantId = CURRENT_TENANT.get();
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalStateException("No tenant context set for current request");
        }
        return tenantId;
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
