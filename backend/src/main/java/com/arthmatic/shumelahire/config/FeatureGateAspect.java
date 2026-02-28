package com.arthmatic.shumelahire.config;

import com.arthmatic.shumelahire.annotation.FeatureGate;
import com.arthmatic.shumelahire.service.FeatureGateService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class FeatureGateAspect {

    private final FeatureGateService featureGateService;

    public FeatureGateAspect(FeatureGateService featureGateService) {
        this.featureGateService = featureGateService;
    }

    @Pointcut("@within(com.arthmatic.shumelahire.annotation.FeatureGate)")
    public void classAnnotated() {}

    @Pointcut("@annotation(com.arthmatic.shumelahire.annotation.FeatureGate)")
    public void methodAnnotated() {}

    @Before("classAnnotated() || methodAnnotated()")
    public void checkFeatureGate(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        // Method-level annotation takes precedence over class-level
        FeatureGate gate = method.getAnnotation(FeatureGate.class);
        if (gate == null) {
            gate = joinPoint.getTarget().getClass().getAnnotation(FeatureGate.class);
        }

        if (gate != null) {
            featureGateService.requireFeature(gate.value());
        }
    }
}
