package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.entity.Applicant;
import com.arthmatic.shumelahire.entity.Application;
import com.arthmatic.shumelahire.entity.ApplicationStatus;
import com.arthmatic.shumelahire.entity.Notification;
import com.arthmatic.shumelahire.entity.NotificationChannel;
import com.arthmatic.shumelahire.entity.NotificationType;
import com.arthmatic.shumelahire.entity.NotificationPriority;
import com.arthmatic.shumelahire.repository.ApplicantRepository;
import com.arthmatic.shumelahire.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import com.arthmatic.shumelahire.entity.NotificationPriority;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private ApplicantRepository applicantRepository;
    
    @Autowired
    private AuditLogService auditLogService;
    
    @Value("${notification.email.enabled:true}")
    private boolean emailEnabled;
    
    @Value("${notification.sms.enabled:false}")
    private boolean smsEnabled;

    /**
     * Send notification for application creation
     */
    @Async
    public void notifyApplicationSubmitted(Application application) {
        logger.info("Sending application submitted notification for application {}", application.getId());
        
        String subject = "Application Submitted Successfully";
        String message = String.format(
            "Dear %s,\n\n" +
            "Your application for the position '%s' has been submitted successfully.\n\n" +
            "Application Details:\n" +
            "- Position: %s\n" +
            "- Department: %s\n" +
            "- Application ID: %s\n" +
            "- Submitted: %s\n\n" +
            "We will review your application and contact you regarding the next steps.\n\n" +
            "Thank you for your interest in joining our organization.\n\n" +
            "Best regards,\n" +
            "HR Team",
            application.getApplicant().getFullName(),
            application.getJobTitle(),
            application.getJobTitle(),
            application.getDepartment(),
            application.getId(),
            application.getSubmittedAt()
        );
        
        sendNotification(application.getApplicant().getId(), subject, message, 
                        NotificationChannel.EMAIL, "APPLICATION_SUBMITTED");
        
        if (smsEnabled) {
            String smsMessage = String.format(
                "Hi %s, your application for %s has been submitted successfully. Application ID: %s",
                application.getApplicant().getName(),
                application.getJobTitle(),
                application.getId()
            );
            sendNotification(application.getApplicant().getId(), "Application Submitted", smsMessage, 
                            NotificationChannel.SMS, "APPLICATION_SUBMITTED");
        }
    }

    /**
     * Send notification for status change
     */
    @Async
    public void notifyStatusChange(Application application, ApplicationStatus previousStatus) {
        logger.info("Sending status change notification for application {} from {} to {}", 
                   application.getId(), previousStatus, application.getStatus());
        
        String subject = getStatusChangeSubject(application.getStatus());
        String message = getStatusChangeMessage(application, previousStatus);
        
        sendNotification(application.getApplicant().getId(), subject, message, 
                        NotificationChannel.EMAIL, "STATUS_CHANGE");
        
        if (smsEnabled && isImportantStatusChange(application.getStatus())) {
            String smsMessage = String.format(
                "Hi %s, your application for %s has been updated to: %s",
                application.getApplicant().getName(),
                application.getJobTitle(),
                getStatusDisplayName(application.getStatus())
            );
            sendNotification(application.getApplicant().getId(), "Application Update", smsMessage, 
                            NotificationChannel.SMS, "STATUS_CHANGE");
        }
    }

    /**
     * Send notification for application withdrawal
     */
    @Async
    public void notifyApplicationWithdrawn(Application application) {
        logger.info("Sending withdrawal notification for application {}", application.getId());
        
        String subject = "Application Withdrawn";
        String message = String.format(
            "Dear %s,\n\n" +
            "Your application for the position '%s' has been withdrawn as requested.\n\n" +
            "Application Details:\n" +
            "- Position: %s\n" +
            "- Application ID: %s\n" +
            "- Withdrawal Date: %s\n" +
            "- Reason: %s\n\n" +
            "You are welcome to apply for other positions or reapply for this position in the future.\n\n" +
            "Thank you for your interest in our organization.\n\n" +
            "Best regards,\n" +
            "HR Team",
            application.getApplicant().getFullName(),
            application.getJobTitle(),
            application.getJobTitle(),
            application.getId(),
            application.getWithdrawnAt(),
            application.getWithdrawalReason()
        );
        
        sendNotification(application.getApplicant().getId(), subject, message, 
                        NotificationChannel.EMAIL, "APPLICATION_WITHDRAWN");
    }

    /**
     * Send notification for shortlisting
     */
    @Async
    public void notifyApplicationShortlisted(Application application) {
        logger.info("Sending shortlist notification for application {}", application.getId());
        
        String subject = "Congratulations! You've Been Shortlisted";
        String message = String.format(
            "Dear %s,\n\n" +
            "Congratulations! We are pleased to inform you that your application for the position '%s' has been shortlisted.\n\n" +
            "Application Details:\n" +
            "- Position: %s\n" +
            "- Department: %s\n" +
            "- Application ID: %s\n\n" +
            "We will be in touch soon regarding the next steps in our selection process.\n\n" +
            "Thank you for your continued interest in our organization.\n\n" +
            "Best regards,\n" +
            "HR Team",
            application.getApplicant().getFullName(),
            application.getJobTitle(),
            application.getJobTitle(),
            application.getDepartment(),
            application.getId()
        );
        
        sendNotification(application.getApplicant().getId(), subject, message, 
                        NotificationChannel.EMAIL, "APPLICATION_SHORTLISTED");
        
        if (smsEnabled) {
            String smsMessage = String.format(
                "Congratulations %s! Your application for %s has been shortlisted. We'll be in touch soon.",
                application.getApplicant().getName(),
                application.getJobTitle()
            );
            sendNotification(application.getApplicant().getId(), "Application Shortlisted", smsMessage, 
                            NotificationChannel.SMS, "APPLICATION_SHORTLISTED");
        }
    }

    /**
     * Send a notification
     */
    private void sendNotification(Long applicantId, String subject, String message, 
                                 NotificationChannel channel, String eventType) {
        try {
            Applicant applicant = applicantRepository.findById(applicantId)
                    .orElseThrow(() -> new IllegalArgumentException("Applicant not found: " + applicantId));
            
            // Map event type to NotificationType
            NotificationType notificationType = mapEventTypeToNotificationType(eventType);
            
            // Create notification record using existing structure
            Notification notification = new Notification();
            notification.setRecipientId(applicantId);
            notification.setType(notificationType);
            notification.setChannel(channel);
            notification.setPriority(getNotificationPriority(notificationType));
            notification.setTitle(subject);
            notification.setMessage(message);
            
            // Set channel-specific fields
            switch (channel) {
                case EMAIL:
                    notification.setEmailTo(applicant.getEmail());
                    notification.setEmailSubject(subject);
                    break;
                case SMS:
                    notification.setPhoneNumber(applicant.getPhone());
                    break;
                case IN_APP:
                case PUSH:
                case WEBHOOK:
                case SLACK:
                case BROWSER:
                    // These channels don't need additional setup for basic functionality
                    break;
            }
            
            // Attempt to send
            boolean sent = false;
            String errorMessage = null;
            
            try {
                switch (channel) {
                    case EMAIL:
                        sent = sendEmail(applicant.getEmail(), subject, message);
                        break;
                    case SMS:
                        sent = sendSMS(applicant.getPhone(), message);
                        break;
                    case IN_APP:
                        sent = true; // In-app notifications are always "sent" (stored)
                        break;
                    case PUSH:
                    case WEBHOOK:
                    case SLACK:
                    case BROWSER:
                        sent = true; // Placeholder for future implementation
                        break;
                }
            } catch (Exception e) {
                logger.error("Error sending {} notification to applicant {}: {}", 
                           channel, applicantId, e.getMessage());
                errorMessage = e.getMessage();
            }
            
            // Update notification status
            notification.setIsDelivered(sent);
            notification.setDeliveredAt(sent ? LocalDateTime.now() : null);
            notification.setDeliveryError(errorMessage);
            
            notificationRepository.save(notification);
            
            // Log to audit
            auditLogService.logApplicantAction(applicantId, 
                sent ? "NOTIFICATION_SENT" : "NOTIFICATION_FAILED", 
                "NOTIFICATION", 
                channel + ": " + eventType + (errorMessage != null ? " - " + errorMessage : ""));
            
            logger.info("Notification {} for applicant {} via {} - Status: {}", 
                       eventType, applicantId, channel, sent ? "SENT" : "FAILED");
                       
        } catch (Exception e) {
            logger.error("Failed to send notification to applicant {}: {}", applicantId, e.getMessage(), e);
        }
    }

    /**
     * Mock email sending - replace with actual email service integration
     */
    private boolean sendEmail(String email, String subject, String message) {
        if (!emailEnabled) {
            logger.info("Email sending is disabled - would send to {}: {}", email, subject);
            return true; // Simulate success when disabled
        }
        
        // TODO: Integrate with actual email service (SendGrid, AWS SES, etc.)
        logger.info("Sending email to {}: {}", email, subject);
        
        // Simulate email sending
        try {
            Thread.sleep(100); // Simulate network delay
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Mock SMS sending - replace with actual SMS service integration
     */
    private boolean sendSMS(String phoneNumber, String message) {
        if (!smsEnabled) {
            logger.info("SMS sending is disabled - would send to {}: {}", phoneNumber, message);
            return true; // Simulate success when disabled
        }
        
        // TODO: Integrate with actual SMS service (Twilio, AWS SNS, etc.)
        logger.info("Sending SMS to {}: {}", phoneNumber, message);
        
        // Simulate SMS sending
        try {
            Thread.sleep(200); // Simulate network delay
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Get notifications for applicant
     */
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsForApplicant(Long applicantId, int limit) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(applicantId)
                .stream()
                .limit(limit)
                .toList();
    }

    /**
     * Mark notification as read
     */
    public void markNotificationAsRead(Long notificationId) {
        notificationRepository.findById(notificationId)
                .ifPresent(notification -> {
                    notification.setReadAt(LocalDateTime.now());
                    notificationRepository.save(notification);
                });
    }

    // Helper methods
    
    private NotificationType mapEventTypeToNotificationType(String eventType) {
        return switch (eventType) {
            case "APPLICATION_SUBMITTED" -> NotificationType.APPLICATION_SUBMITTED;
            case "STATUS_CHANGE" -> NotificationType.PIPELINE_STAGE_CHANGED;
            case "APPLICATION_WITHDRAWN" -> NotificationType.APPLICATION_WITHDRAWN;
            case "APPLICATION_SHORTLISTED" -> NotificationType.APPLICATION_APPROVED;
            default -> NotificationType.APPLICATION_VIEWED; // Default fallback
        };
    }
    
    private NotificationPriority getNotificationPriority(NotificationType type) {
        return switch (type) {
            case OFFER_EXTENDED, INTERVIEW_SCHEDULED, APPLICATION_REJECTED -> NotificationPriority.HIGH;
            case APPLICATION_APPROVED, APPLICATION_SUBMITTED -> NotificationPriority.MEDIUM;
            default -> NotificationPriority.LOW;
        };
    }

    private String getStatusChangeSubject(ApplicationStatus status) {
        return switch (status) {
            case SCREENING -> "Application Under Review";
            case INTERVIEW_SCHEDULED -> "Interview Scheduled";
            case INTERVIEW_COMPLETED -> "Interview Completed";
            case REFERENCE_CHECK -> "Reference Check in Progress";
            case OFFER_PENDING, OFFERED -> "Job Offer Extended";
            case OFFER_ACCEPTED -> "Offer Accepted - Welcome Aboard!";
            case OFFER_DECLINED -> "Offer Declined";
            case HIRED -> "Welcome to the Team!";
            case REJECTED -> "Application Status Update";
            default -> "Application Status Update";
        };
    }

    private String getStatusChangeMessage(Application application, ApplicationStatus previousStatus) {
        String applicantName = application.getApplicant().getFullName();
        String jobTitle = application.getJobTitle();
        String currentStatus = getStatusDisplayName(application.getStatus());
        
        return switch (application.getStatus()) {
            case SCREENING -> String.format(
                "Dear %s,\n\nYour application for '%s' is now under review by our hiring team.\n\n" +
                "We will contact you soon with the next steps.\n\nBest regards,\nHR Team",
                applicantName, jobTitle);
            case INTERVIEW_SCHEDULED -> String.format(
                "Dear %s,\n\nGreat news! We would like to invite you for an interview for the position '%s'.\n\n" +
                "We will send you the interview details separately.\n\nBest regards,\nHR Team",
                applicantName, jobTitle);
            case INTERVIEW_COMPLETED -> String.format(
                "Dear %s,\n\nThank you for taking the time to interview for the position '%s'.\n\n" +
                "We are currently reviewing all candidates and will be in touch soon.\n\nBest regards,\nHR Team",
                applicantName, jobTitle);
            case OFFER_PENDING, OFFERED -> String.format(
                "Dear %s,\n\nCongratulations! We are pleased to extend an offer for the position '%s'.\n\n" +
                "Please review the offer details and let us know your decision.\n\nBest regards,\nHR Team",
                applicantName, jobTitle);
            case HIRED -> String.format(
                "Dear %s,\n\nWelcome to the team! We are excited to have you join us in the role of '%s'.\n\n" +
                "We will send you onboarding information separately.\n\nBest regards,\nHR Team",
                applicantName, jobTitle);
            case REJECTED -> String.format(
                "Dear %s,\n\nThank you for your interest in the position '%s'.\n\n" +
                "After careful consideration, we have decided to move forward with other candidates.\n\n" +
                "We encourage you to apply for other opportunities with us.\n\nBest regards,\nHR Team",
                applicantName, jobTitle);
            default -> String.format(
                "Dear %s,\n\nYour application for '%s' has been updated.\n\n" +
                "Current Status: %s\n\nBest regards,\nHR Team",
                applicantName, jobTitle, currentStatus);
        };
    }

    private boolean isImportantStatusChange(ApplicationStatus status) {
        return status == ApplicationStatus.INTERVIEW_SCHEDULED ||
               status == ApplicationStatus.OFFERED ||
               status == ApplicationStatus.HIRED ||
               status == ApplicationStatus.REJECTED;
    }

    private String getStatusDisplayName(ApplicationStatus status) {
        return switch (status) {
            case SUBMITTED -> "Application Submitted";
            case SCREENING -> "Under Review";
            case INTERVIEW_SCHEDULED -> "Interview Scheduled";
            case INTERVIEW_COMPLETED -> "Interview Completed";
            case REFERENCE_CHECK -> "Reference Check";
            case OFFER_PENDING -> "Offer Pending";
            case OFFERED -> "Offer Extended";
            case OFFER_ACCEPTED -> "Offer Accepted";
            case OFFER_DECLINED -> "Offer Declined";
            case HIRED -> "Hired";
            case REJECTED -> "Not Selected";
            case WITHDRAWN -> "Withdrawn";
        };
    }
}
