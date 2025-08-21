package com.example.recruitment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "interviews")
public class Interview {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    @NotNull(message = "Application is required")
    private Application application;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private InterviewType type = InterviewType.PHONE;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "round", nullable = false)
    private InterviewRound round = InterviewRound.SCREENING;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InterviewStatus status = InterviewStatus.SCHEDULED;
    
    @Column(name = "scheduled_at", nullable = false)
    @NotNull(message = "Interview date/time is required")
    private LocalDateTime scheduledAt;
    
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes = 60;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "meeting_link")
    private String meetingLink;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "meeting_room")
    private String meetingRoom;
    
    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;
    
    @Column(name = "agenda", columnDefinition = "TEXT")
    private String agenda;
    
    @Column(name = "interviewer_id", nullable = false)
    @NotNull(message = "Interviewer is required")
    private Long interviewerId; // Primary interviewer
    
    @Column(name = "additional_interviewers")
    private String additionalInterviewers; // JSON array of interviewer IDs
    
    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;
    
    @Column(name = "rating")
    private Integer rating; // 1-5 stars
    
    @Column(name = "technical_assessment", columnDefinition = "TEXT")
    private String technicalAssessment;
    
    @Column(name = "communication_skills")
    private Integer communicationSkills; // 1-5
    
    @Column(name = "technical_skills")
    private Integer technicalSkills; // 1-5
    
    @Column(name = "cultural_fit")
    private Integer culturalFit; // 1-5
    
    @Column(name = "overall_impression", columnDefinition = "TEXT")
    private String overallImpression;
    
    @Column(name = "recommendation")
    @Enumerated(EnumType.STRING)
    private InterviewRecommendation recommendation;
    
    @Column(name = "next_steps", columnDefinition = "TEXT")
    private String nextSteps;
    
    @Column(name = "candidate_questions", columnDefinition = "TEXT")
    private String candidateQuestions;
    
    @Column(name = "interviewer_notes", columnDefinition = "TEXT")
    private String interviewerNotes;
    
    @Column(name = "rescheduled_from")
    private LocalDateTime rescheduledFrom;
    
    @Column(name = "reschedule_reason")
    private String rescheduleReason;
    
    @Column(name = "reschedule_count")
    private Integer rescheduleCount = 0;
    
    @Column(name = "reminder_sent_at")
    private LocalDateTime reminderSentAt;
    
    @Column(name = "feedback_requested_at")
    private LocalDateTime feedbackRequestedAt;
    
    @Column(name = "feedback_submitted_at")
    private LocalDateTime feedbackSubmittedAt;
    
    @Column(name = "created_by", nullable = false)
    private Long createdBy;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    
    @Column(name = "cancellation_reason")
    private String cancellationReason;
    
    // Constructors
    public Interview() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Interview(Application application, LocalDateTime scheduledAt, Long interviewerId, InterviewType type) {
        this();
        this.application = application;
        this.scheduledAt = scheduledAt;
        this.interviewerId = interviewerId;
        this.type = type;
        this.title = generateDefaultTitle();
    }
    
    // Lifecycle callbacks
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Business methods
    public boolean canBeRescheduled() {
        return status == InterviewStatus.SCHEDULED && 
               scheduledAt.isAfter(LocalDateTime.now().plusHours(2)); // At least 2 hours notice
    }
    
    public boolean canBeCancelled() {
        return status == InterviewStatus.SCHEDULED || status == InterviewStatus.RESCHEDULED;
    }
    
    public boolean canBeStarted() {
        return status == InterviewStatus.SCHEDULED && 
               LocalDateTime.now().isAfter(scheduledAt.minusMinutes(15)) && // 15 minutes before
               LocalDateTime.now().isBefore(getEndTime().plusMinutes(30)); // 30 minutes after end
    }
    
    public boolean canBeCompleted() {
        return status == InterviewStatus.IN_PROGRESS;
    }
    
    public boolean requiresFeedback() {
        return status == InterviewStatus.COMPLETED && feedback == null;
    }
    
    public boolean isOverdue() {
        return status == InterviewStatus.SCHEDULED && 
               LocalDateTime.now().isAfter(getEndTime().plusMinutes(15));
    }
    
    public boolean isUpcoming() {
        return status == InterviewStatus.SCHEDULED && 
               scheduledAt.isAfter(LocalDateTime.now()) &&
               scheduledAt.isBefore(LocalDateTime.now().plusDays(7));
    }
    
    public LocalDateTime getEndTime() {
        return scheduledAt.plusMinutes(durationMinutes);
    }
    
    public long getMinutesUntilInterview() {
        return ChronoUnit.MINUTES.between(LocalDateTime.now(), scheduledAt);
    }
    
    public long getMinutesSinceInterview() {
        return ChronoUnit.MINUTES.between(getEndTime(), LocalDateTime.now());
    }
    
    public String getStatusDisplayName() {
        return status.getDisplayName();
    }
    
    public String getTypeDisplayName() {
        return type.getDisplayName();
    }
    
    public String getRoundDisplayName() {
        return round.getDisplayName();
    }
    
    public Double getAverageSkillRating() {
        if (communicationSkills == null || technicalSkills == null || culturalFit == null) {
            return null;
        }
        return (communicationSkills + technicalSkills + culturalFit) / 3.0;
    }
    
    public boolean hasConflictWith(LocalDateTime otherStart, LocalDateTime otherEnd) {
        LocalDateTime thisEnd = getEndTime();
        return !(thisEnd.isBefore(otherStart) || scheduledAt.isAfter(otherEnd));
    }
    
    private String generateDefaultTitle() {
        if (application != null && application.getJobPosting() != null) {
            return round.getDisplayName() + " Interview - " + application.getJobPosting().getTitle();
        }
        return round.getDisplayName() + " Interview";
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Application getApplication() {
        return application;
    }
    
    public void setApplication(Application application) {
        this.application = application;
        if (application != null && title == null) {
            this.title = generateDefaultTitle();
        }
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public InterviewType getType() {
        return type;
    }
    
    public void setType(InterviewType type) {
        this.type = type;
    }
    
    public InterviewRound getRound() {
        return round;
    }
    
    public void setRound(InterviewRound round) {
        this.round = round;
    }
    
    public InterviewStatus getStatus() {
        return status;
    }
    
    public void setStatus(InterviewStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }
    
    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }
    
    public Integer getDurationMinutes() {
        return durationMinutes;
    }
    
    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getMeetingLink() {
        return meetingLink;
    }
    
    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getMeetingRoom() {
        return meetingRoom;
    }
    
    public void setMeetingRoom(String meetingRoom) {
        this.meetingRoom = meetingRoom;
    }
    
    public String getInstructions() {
        return instructions;
    }
    
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    
    public String getAgenda() {
        return agenda;
    }
    
    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }
    
    public Long getInterviewerId() {
        return interviewerId;
    }
    
    public void setInterviewerId(Long interviewerId) {
        this.interviewerId = interviewerId;
    }
    
    public String getAdditionalInterviewers() {
        return additionalInterviewers;
    }
    
    public void setAdditionalInterviewers(String additionalInterviewers) {
        this.additionalInterviewers = additionalInterviewers;
    }
    
    public String getFeedback() {
        return feedback;
    }
    
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
    
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    
    public String getTechnicalAssessment() {
        return technicalAssessment;
    }
    
    public void setTechnicalAssessment(String technicalAssessment) {
        this.technicalAssessment = technicalAssessment;
    }
    
    public Integer getCommunicationSkills() {
        return communicationSkills;
    }
    
    public void setCommunicationSkills(Integer communicationSkills) {
        this.communicationSkills = communicationSkills;
    }
    
    public Integer getTechnicalSkills() {
        return technicalSkills;
    }
    
    public void setTechnicalSkills(Integer technicalSkills) {
        this.technicalSkills = technicalSkills;
    }
    
    public Integer getCulturalFit() {
        return culturalFit;
    }
    
    public void setCulturalFit(Integer culturalFit) {
        this.culturalFit = culturalFit;
    }
    
    public String getOverallImpression() {
        return overallImpression;
    }
    
    public void setOverallImpression(String overallImpression) {
        this.overallImpression = overallImpression;
    }
    
    public InterviewRecommendation getRecommendation() {
        return recommendation;
    }
    
    public void setRecommendation(InterviewRecommendation recommendation) {
        this.recommendation = recommendation;
    }
    
    public String getNextSteps() {
        return nextSteps;
    }
    
    public void setNextSteps(String nextSteps) {
        this.nextSteps = nextSteps;
    }
    
    public String getCandidateQuestions() {
        return candidateQuestions;
    }
    
    public void setCandidateQuestions(String candidateQuestions) {
        this.candidateQuestions = candidateQuestions;
    }
    
    public String getInterviewerNotes() {
        return interviewerNotes;
    }
    
    public void setInterviewerNotes(String interviewerNotes) {
        this.interviewerNotes = interviewerNotes;
    }
    
    public LocalDateTime getRescheduledFrom() {
        return rescheduledFrom;
    }
    
    public void setRescheduledFrom(LocalDateTime rescheduledFrom) {
        this.rescheduledFrom = rescheduledFrom;
    }
    
    public String getRescheduleReason() {
        return rescheduleReason;
    }
    
    public void setRescheduleReason(String rescheduleReason) {
        this.rescheduleReason = rescheduleReason;
    }
    
    public Integer getRescheduleCount() {
        return rescheduleCount;
    }
    
    public void setRescheduleCount(Integer rescheduleCount) {
        this.rescheduleCount = rescheduleCount;
    }
    
    public LocalDateTime getReminderSentAt() {
        return reminderSentAt;
    }
    
    public void setReminderSentAt(LocalDateTime reminderSentAt) {
        this.reminderSentAt = reminderSentAt;
    }
    
    public LocalDateTime getFeedbackRequestedAt() {
        return feedbackRequestedAt;
    }
    
    public void setFeedbackRequestedAt(LocalDateTime feedbackRequestedAt) {
        this.feedbackRequestedAt = feedbackRequestedAt;
    }
    
    public LocalDateTime getFeedbackSubmittedAt() {
        return feedbackSubmittedAt;
    }
    
    public void setFeedbackSubmittedAt(LocalDateTime feedbackSubmittedAt) {
        this.feedbackSubmittedAt = feedbackSubmittedAt;
    }
    
    public Long getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }
    
    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
    
    public String getCancellationReason() {
        return cancellationReason;
    }
    
    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
}