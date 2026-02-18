package com.arthmatic.shumelahire.config.tenant;

import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnBean(EntityManager.class)
public class TenantWebConfig implements WebMvcConfigurer {

    private final EntityManager entityManager;

    public TenantWebConfig(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TenantFilterInterceptor(entityManager))
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/public/**");
    }
}
