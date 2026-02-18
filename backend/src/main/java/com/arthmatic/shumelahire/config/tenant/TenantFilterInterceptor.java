package com.arthmatic.shumelahire.config.tenant;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

public class TenantFilterInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TenantFilterInterceptor.class);

    private final EntityManager entityManager;

    public TenantFilterInterceptor(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId != null) {
            Session session = entityManager.unwrap(Session.class);
            session.enableFilter("tenantFilter").setParameter("tenantId", tenantId);
            logger.debug("Enabled tenant filter for tenant: {}", tenantId);
        }
        return true;
    }
}
