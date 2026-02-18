package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.entity.*;
import com.arthmatic.shumelahire.security.JwtAuthenticationEntryPoint;
import com.arthmatic.shumelahire.security.JwtAuthenticationFilter;
import com.arthmatic.shumelahire.security.JwtUtil;
import com.arthmatic.shumelahire.security.RateLimitFilter;
import com.arthmatic.shumelahire.service.CustomUserDetailsService;
import com.arthmatic.shumelahire.service.InterviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InterviewController.class)
@ActiveProfiles("dev")
@DisplayName("InterviewController Integration Tests")
class InterviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InterviewService interviewService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private RateLimitFilter rateLimitFilter;

    private Interview sampleInterview;
    private LocalDateTime scheduledTime;

    @BeforeEach
    void setUp() {
        scheduledTime = LocalDateTime.now().plusDays(3);

        sampleInterview = new Interview();
        sampleInterview.setId(1L);
        sampleInterview.setTitle("Phone Screening - Senior Java Developer");
        sampleInterview.setType(InterviewType.PHONE);
        sampleInterview.setRound(InterviewRound.SCREENING);
        sampleInterview.setStatus(InterviewStatus.SCHEDULED);
        sampleInterview.setScheduledAt(scheduledTime);
        sampleInterview.setDurationMinutes(60);
        sampleInterview.setInterviewerId(5L);
        sampleInterview.setInterviewerName("Jane Smith");
        sampleInterview.setLocation("Conference Room A");
        sampleInterview.setCreatedBy(1L);
        sampleInterview.setCreatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("GET /api/interviews")
    class SearchInterviews {

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should return paginated interviews")
        void searchInterviews_Authenticated_ReturnsPaginatedResults() throws Exception {
            Page<Interview> page = new PageImpl<>(List.of(sampleInterview));
            when(interviewService.searchInterviews(
                    any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/api/interviews"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].title").value("Phone Screening - Senior Java Developer"));
        }

        @Test
        @WithMockUser(roles = {"HR_MANAGER"})
        @DisplayName("should return empty page when no interviews exist")
        void searchInterviews_NoInterviews_ReturnsEmptyPage() throws Exception {
            Page<Interview> emptyPage = new PageImpl<>(Collections.emptyList());
            when(interviewService.searchInterviews(
                    any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                    .thenReturn(emptyPage);

            mockMvc.perform(get("/api/interviews"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)));
        }

        @Test
        @DisplayName("should return 401 when not authenticated")
        void searchInterviews_NotAuthenticated_Returns401() throws Exception {
            mockMvc.perform(get("/api/interviews"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(roles = {"APPLICANT"})
        @DisplayName("should return 403 when user lacks required role")
        void searchInterviews_InsufficientRole_Returns403() throws Exception {
            mockMvc.perform(get("/api/interviews"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = {"INTERVIEWER"})
        @DisplayName("should allow access for INTERVIEWER role")
        void searchInterviews_InterviewerRole_Returns200() throws Exception {
            Page<Interview> page = new PageImpl<>(List.of(sampleInterview));
            when(interviewService.searchInterviews(
                    any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/api/interviews"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /api/interviews/{id}")
    class GetInterview {

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should return interview by ID")
        void getInterview_ExistingId_ReturnsInterview() throws Exception {
            when(interviewService.getInterviewById(1L)).thenReturn(sampleInterview);

            mockMvc.perform(get("/api/interviews/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("Phone Screening - Senior Java Developer"))
                    .andExpect(jsonPath("$.type").value("PHONE"))
                    .andExpect(jsonPath("$.status").value("SCHEDULED"));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should return 404 when interview not found")
        void getInterview_NonExistingId_Returns404() throws Exception {
            when(interviewService.getInterviewById(999L))
                    .thenThrow(new IllegalArgumentException("Not found"));

            mockMvc.perform(get("/api/interviews/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/interviews")
    class CreateInterview {

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should create interview and return 201")
        void createInterview_ValidRequest_Returns201() throws Exception {
            when(interviewService.createInterview(any(Interview.class), eq(1L)))
                    .thenReturn(sampleInterview);

            // Build a JSON body that represents the Interview entity for creation.
            // We need to provide the minimal required fields.
            String requestBody = objectMapper.writeValueAsString(sampleInterview);

            mockMvc.perform(post("/api/interviews")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                            .param("createdBy", "1"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("Phone Screening - Senior Java Developer"));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should return 400 when creation fails due to invalid argument")
        void createInterview_InvalidArgument_Returns400() throws Exception {
            when(interviewService.createInterview(any(Interview.class), eq(1L)))
                    .thenThrow(new IllegalArgumentException("Application not found"));

            mockMvc.perform(post("/api/interviews")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleInterview))
                            .param("createdBy", "1"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 401 when not authenticated")
        void createInterview_NotAuthenticated_Returns401() throws Exception {
            mockMvc.perform(post("/api/interviews")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleInterview))
                            .param("createdBy", "1"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("PUT /api/interviews/{id}")
    class UpdateInterview {

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should update interview and return 200")
        void updateInterview_ValidRequest_Returns200() throws Exception {
            sampleInterview.setLocation("Updated Conference Room B");
            when(interviewService.updateInterview(eq(1L), any(Interview.class), eq(1L)))
                    .thenReturn(sampleInterview);

            mockMvc.perform(put("/api/interviews/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleInterview))
                            .param("updatedBy", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.location").value("Updated Conference Room B"));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should return 404 when interview not found for update")
        void updateInterview_NonExistingId_Returns404() throws Exception {
            when(interviewService.updateInterview(eq(999L), any(Interview.class), eq(1L)))
                    .thenThrow(new IllegalArgumentException("Not found"));

            mockMvc.perform(put("/api/interviews/999")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleInterview))
                            .param("updatedBy", "1"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should return 400 when update fails due to invalid state")
        void updateInterview_InvalidState_Returns400() throws Exception {
            when(interviewService.updateInterview(eq(1L), any(Interview.class), eq(1L)))
                    .thenThrow(new IllegalStateException("Cannot update completed interview"));

            mockMvc.perform(put("/api/interviews/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleInterview))
                            .param("updatedBy", "1"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /api/interviews/{id}")
    class DeleteInterview {

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should delete interview and return 204")
        void deleteInterview_ValidRequest_Returns204() throws Exception {
            doNothing().when(interviewService).deleteInterview(eq(1L), eq(1L));

            mockMvc.perform(delete("/api/interviews/1")
                            .with(csrf())
                            .param("deletedBy", "1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should return 404 when interview not found for deletion")
        void deleteInterview_NonExistingId_Returns404() throws Exception {
            doThrow(new IllegalArgumentException("Not found"))
                    .when(interviewService).deleteInterview(eq(999L), eq(1L));

            mockMvc.perform(delete("/api/interviews/999")
                            .with(csrf())
                            .param("deletedBy", "1"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should return 400 when deletion fails due to invalid state")
        void deleteInterview_InvalidState_Returns400() throws Exception {
            doThrow(new IllegalStateException("Cannot delete in-progress interview"))
                    .when(interviewService).deleteInterview(eq(1L), eq(1L));

            mockMvc.perform(delete("/api/interviews/1")
                            .with(csrf())
                            .param("deletedBy", "1"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Interview Lifecycle Endpoints")
    class InterviewLifecycle {

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("POST /api/interviews/{id}/reschedule should reschedule interview")
        void rescheduleInterview_ValidRequest_Returns200() throws Exception {
            LocalDateTime newTime = scheduledTime.plusDays(2);
            sampleInterview.setScheduledAt(newTime);
            sampleInterview.setStatus(InterviewStatus.RESCHEDULED);
            when(interviewService.rescheduleInterview(eq(1L), any(LocalDateTime.class), eq("Conflict"), eq(1L)))
                    .thenReturn(sampleInterview);

            mockMvc.perform(post("/api/interviews/1/reschedule")
                            .with(csrf())
                            .param("newScheduledAt", newTime.toString())
                            .param("reason", "Conflict")
                            .param("rescheduledBy", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("RESCHEDULED"));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("POST /api/interviews/{id}/reschedule should return 400 on invalid state")
        void rescheduleInterview_InvalidState_Returns400() throws Exception {
            LocalDateTime newTime = scheduledTime.plusDays(2);
            when(interviewService.rescheduleInterview(eq(1L), any(LocalDateTime.class), eq("Conflict"), eq(1L)))
                    .thenThrow(new IllegalStateException("Cannot reschedule completed interview"));

            mockMvc.perform(post("/api/interviews/1/reschedule")
                            .with(csrf())
                            .param("newScheduledAt", newTime.toString())
                            .param("reason", "Conflict")
                            .param("rescheduledBy", "1"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("POST /api/interviews/{id}/cancel should cancel interview")
        void cancelInterview_ValidRequest_Returns200() throws Exception {
            sampleInterview.setStatus(InterviewStatus.CANCELLED);
            when(interviewService.cancelInterview(eq(1L), eq("No longer needed"), eq(1L)))
                    .thenReturn(sampleInterview);

            mockMvc.perform(post("/api/interviews/1/cancel")
                            .with(csrf())
                            .param("reason", "No longer needed")
                            .param("cancelledBy", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("CANCELLED"));
        }

        @Test
        @WithMockUser(roles = {"HR_MANAGER"})
        @DisplayName("POST /api/interviews/{id}/start should start interview")
        void startInterview_ValidRequest_Returns200() throws Exception {
            sampleInterview.setStatus(InterviewStatus.IN_PROGRESS);
            when(interviewService.startInterview(eq(1L), eq(1L))).thenReturn(sampleInterview);

            mockMvc.perform(post("/api/interviews/1/start")
                            .with(csrf())
                            .param("startedBy", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
        }

        @Test
        @WithMockUser(roles = {"HR_MANAGER"})
        @DisplayName("POST /api/interviews/{id}/complete should complete interview")
        void completeInterview_ValidRequest_Returns200() throws Exception {
            sampleInterview.setStatus(InterviewStatus.COMPLETED);
            when(interviewService.completeInterview(eq(1L), eq(1L))).thenReturn(sampleInterview);

            mockMvc.perform(post("/api/interviews/1/complete")
                            .with(csrf())
                            .param("completedBy", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("COMPLETED"));
        }

        @Test
        @WithMockUser(roles = {"INTERVIEWER"})
        @DisplayName("POST /api/interviews/{id}/feedback should submit feedback")
        void submitFeedback_ValidRequest_Returns200() throws Exception {
            sampleInterview.setFeedback("Great candidate");
            sampleInterview.setRating(4);
            sampleInterview.setRecommendation(InterviewRecommendation.HIRE);
            when(interviewService.submitFeedback(
                    eq(1L), eq("Great candidate"), eq(4), eq(4), eq(5), eq(3),
                    eq("Excellent"), eq(InterviewRecommendation.HIRE), any(), any(), any(), any(), eq(5L)))
                    .thenReturn(sampleInterview);

            mockMvc.perform(post("/api/interviews/1/feedback")
                            .with(csrf())
                            .param("feedback", "Great candidate")
                            .param("rating", "4")
                            .param("communicationSkills", "4")
                            .param("technicalSkills", "5")
                            .param("culturalFit", "3")
                            .param("overallImpression", "Excellent")
                            .param("recommendation", "HIRE")
                            .param("submittedBy", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.feedback").value("Great candidate"))
                    .andExpect(jsonPath("$.rating").value(4));
        }
    }

    @Nested
    @DisplayName("Query and Utility Endpoints")
    class QueryEndpoints {

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("GET /api/interviews/application/{applicationId} should return interviews by application")
        void getInterviewsByApplication_ReturnsResults() throws Exception {
            when(interviewService.getInterviewsByApplication(1L)).thenReturn(List.of(sampleInterview));

            mockMvc.perform(get("/api/interviews/application/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("GET /api/interviews/upcoming should return upcoming interviews")
        void getUpcomingInterviews_ReturnsResults() throws Exception {
            when(interviewService.getUpcomingInterviews(7)).thenReturn(List.of(sampleInterview));

            mockMvc.perform(get("/api/interviews/upcoming"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("GET /api/interviews/overdue should return overdue interviews")
        void getOverdueInterviews_ReturnsResults() throws Exception {
            when(interviewService.getOverdueInterviews()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/interviews/overdue"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("GET /api/interviews/pending-feedback should return interviews needing feedback")
        void getInterviewsRequiringFeedback_ReturnsResults() throws Exception {
            when(interviewService.getInterviewsRequiringFeedback()).thenReturn(List.of(sampleInterview));

            mockMvc.perform(get("/api/interviews/pending-feedback"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("GET /api/interviews/availability/interviewer/{id} should check availability")
        void checkInterviewerAvailability_ReturnsResult() throws Exception {
            LocalDateTime startTime = LocalDateTime.now().plusDays(1);
            when(interviewService.isInterviewerAvailable(eq(5L), any(LocalDateTime.class), eq(60)))
                    .thenReturn(true);

            mockMvc.perform(get("/api/interviews/availability/interviewer/5")
                            .param("startTime", startTime.toString())
                            .param("durationMinutes", "60"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.available").value(true));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("GET /api/interviews/types should return interview types enum values")
        void getInterviewTypes_ReturnsEnumValues() throws Exception {
            mockMvc.perform(get("/api/interviews/types"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(InterviewType.values().length)));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("GET /api/interviews/rounds should return interview rounds enum values")
        void getInterviewRounds_ReturnsEnumValues() throws Exception {
            mockMvc.perform(get("/api/interviews/rounds"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(InterviewRound.values().length)));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("GET /api/interviews/statuses should return interview statuses enum values")
        void getInterviewStatuses_ReturnsEnumValues() throws Exception {
            mockMvc.perform(get("/api/interviews/statuses"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(InterviewStatus.values().length)));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("GET /api/interviews/recommendations should return recommendation enum values")
        void getInterviewRecommendations_ReturnsEnumValues() throws Exception {
            mockMvc.perform(get("/api/interviews/recommendations"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(InterviewRecommendation.values().length)));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("POST /api/interviews/{id}/reminder-sent should mark reminder as sent")
        void markReminderSent_ValidRequest_Returns200() throws Exception {
            doNothing().when(interviewService).markReminderSent(1L);

            mockMvc.perform(post("/api/interviews/1/reminder-sent")
                            .with(csrf()))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("POST /api/interviews/{id}/reminder-sent should return 404 when not found")
        void markReminderSent_NotFound_Returns404() throws Exception {
            doThrow(new IllegalArgumentException("Not found"))
                    .when(interviewService).markReminderSent(999L);

            mockMvc.perform(post("/api/interviews/999/reminder-sent")
                            .with(csrf()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("POST /api/interviews/{id}/request-feedback should request feedback")
        void requestFeedback_ValidRequest_Returns200() throws Exception {
            doNothing().when(interviewService).requestFeedback(1L);

            mockMvc.perform(post("/api/interviews/1/request-feedback")
                            .with(csrf()))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("GET /api/interviews/analytics should return analytics data")
        void getInterviewAnalytics_ReturnsResults() throws Exception {
            Map<String, Object> analytics = Map.of(
                    "totalInterviews", 50,
                    "completedInterviews", 30,
                    "cancelledInterviews", 5
            );
            when(interviewService.getInterviewAnalytics(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(analytics);

            mockMvc.perform(get("/api/interviews/analytics"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalInterviews").value(50))
                    .andExpect(jsonPath("$.completedInterviews").value(30));
        }
    }
}
