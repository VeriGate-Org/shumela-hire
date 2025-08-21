package com.example.recruitment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditLogService.class);
    
    /**
     * Log applicant-related actions
     */
    public void logApplicantAction(Long applicantId, String action, String entityType, String details) {
        // For now, just log to application logs
        // In production, this would save to audit_logs table
        logger.info("AUDIT: Applicant {} - Action: {} - Entity: {} - Details: {} - Timestamp: {}", 
                   applicantId, action, entityType, details, LocalDateTime.now());
    }
    
    /**
     * Log general user actions
     */
    public void logUserAction(Long userId, String action, String entityType, String details) {
        logger.info("AUDIT: User {} - Action: {} - Entity: {} - Details: {} - Timestamp: {}", 
                   userId, action, entityType, details, LocalDateTime.now());
    }
    
    /**
     * Log system actions
     */
    public void logSystemAction(String action, String entityType, String details) {
        logger.info("AUDIT: SYSTEM - Action: {} - Entity: {} - Details: {} - Timestamp: {}", 
                   action, entityType, details, LocalDateTime.now());
    }
    
    /**
     * Log authentication actions
     */
    public void logAuthAction(String email, String action, String result, String ipAddress) {
        logger.info("AUDIT: AUTH - Email: {} - Action: {} - Result: {} - IP: {} - Timestamp: {}", 
                   email, action, result, ipAddress, LocalDateTime.now());
    }
    
    /**
     * Log document actions
     */
    public void logDocumentAction(Long documentId, Long applicantId, String action, String filename) {
        logger.info("AUDIT: Document {} - Applicant {} - Action: {} - File: {} - Timestamp: {}", 
                   documentId, applicantId, action, filename, LocalDateTime.now());
    }
    
    /**
     * Log job ad actions
     */
    public void logJobAdAction(Long jobAdId, String action, String userId, String details) {
        logger.info("AUDIT: JobAd {} - User {} - Action: {} - Details: {} - Timestamp: {}", 
                   jobAdId, userId, action, details, LocalDateTime.now());
    }
}