package com.arthmatic.shumelahire.config;

import com.arthmatic.shumelahire.exception.FeatureNotEnabledException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(FeatureNotEnabledException.class)
    public ResponseEntity<Map<String, Object>> handleFeatureNotEnabled(FeatureNotEnabledException ex) {
        logger.warn("Feature not enabled: {}", ex.getFeatureCode());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "error", "Feature not available",
                "message", "The feature '" + ex.getFeatureCode() + "' is not enabled for your plan",
                "featureCode", ex.getFeatureCode(),
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}
