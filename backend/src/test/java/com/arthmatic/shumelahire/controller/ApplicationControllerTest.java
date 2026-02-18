package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.dto.ApplicationCreateRequest;
import com.arthmatic.shumelahire.dto.ApplicationResponse;
import com.arthmatic.shumelahire.dto.ApplicationWithdrawRequest;
import com.arthmatic.shumelahire.entity.ApplicationStatus;
import com.arthmatic.shumelahire.security.JwtAuthenticationEntryPoint;
import com.arthmatic.shumelahire.security.JwtAuthenticationFilter;
import com.arthmatic.shumelahire.security.JwtUtil;
import com.arthmatic.shumelahire.security.RateLimitFilter;
import com.arthmatic.shumelahire.service.ApplicationService;
import com.arthmatic.shumelahire.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApplicationController.class)
@ActiveProfiles("dev")
@DisplayName("ApplicationController Integration Tests")
class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ApplicationService applicationService;

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

    private ApplicationCreateRequest validCreateRequest;
    private ApplicationResponse sampleResponse;

    @BeforeEach
    void setUp() {
        validCreateRequest = new ApplicationCreateRequest();
        validCreateRequest.setApplicantId(1L);
        validCreateRequest.setJobAdId(10L);
        validCreateRequest.setCoverLetter("I am excited to apply for this position.");
        validCreateRequest.setApplicationSource("EXTERNAL");

        sampleResponse = new ApplicationResponse();
        sampleResponse.setId(1L);
        sampleResponse.setApplicantId(1L);
        sampleResponse.setApplicantName("John Doe");
        sampleResponse.setApplicantEmail("john.doe@example.com");
        sampleResponse.setJobAdId(10L);
        sampleResponse.setJobTitle("Senior Java Developer");
        sampleResponse.setDepartment("Engineering");
        sampleResponse.setStatus(ApplicationStatus.SUBMITTED);
        sampleResponse.setCoverLetter("I am excited to apply for this position.");
        sampleResponse.setSubmittedAt(LocalDateTime.now());
        sampleResponse.setCreatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("GET /api/applications")
    class SearchApplications {

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should return paginated applications")
        void searchApplications_Authenticated_ReturnsPaginatedResults() throws Exception {
            Page<ApplicationResponse> page = new PageImpl<>(List.of(sampleResponse));
            when(applicationService.searchApplications(any(), any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/api/applications"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].applicantName").value("John Doe"));
        }

        @Test
        @WithMockUser(roles = {"HR_MANAGER"})
        @DisplayName("should return empty page when no applications exist")
        void searchApplications_NoApplications_ReturnsEmptyPage() throws Exception {
            Page<ApplicationResponse> emptyPage = new PageImpl<>(Collections.emptyList());
            when(applicationService.searchApplications(any(), any(Pageable.class))).thenReturn(emptyPage);

            mockMvc.perform(get("/api/applications"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)));
        }

        @Test
        @WithMockUser(roles = {"RECRUITER"})
        @DisplayName("should accept search parameters")
        void searchApplications_WithSearchParam_ReturnsResults() throws Exception {
            Page<ApplicationResponse> page = new PageImpl<>(List.of(sampleResponse));
            when(applicationService.searchApplications(eq("John"), any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/api/applications")
                            .param("search", "John")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @DisplayName("should return 401 when not authenticated")
        void searchApplications_NotAuthenticated_Returns401() throws Exception {
            mockMvc.perform(get("/api/applications"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(roles = {"APPLICANT"})
        @DisplayName("should return 403 when user lacks required role")
        void searchApplications_InsufficientRole_Returns403() throws Exception {
            mockMvc.perform(get("/api/applications"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /api/applications/{id}")
    class GetApplication {

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should return application by ID")
        void getApplication_ExistingId_ReturnsApplication() throws Exception {
            when(applicationService.getApplication(1L)).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/applications/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.applicantName").value("John Doe"))
                    .andExpect(jsonPath("$.jobTitle").value("Senior Java Developer"))
                    .andExpect(jsonPath("$.status").value("SUBMITTED"));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should return 404 when application not found")
        void getApplication_NonExistingId_Returns404() throws Exception {
            when(applicationService.getApplication(999L))
                    .thenThrow(new IllegalArgumentException("Not found"));

            mockMvc.perform(get("/api/applications/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/applications")
    class SubmitApplication {

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should create application and return 201")
        void submitApplication_ValidRequest_Returns201() throws Exception {
            when(applicationService.submitApplication(any(ApplicationCreateRequest.class)))
                    .thenReturn(sampleResponse);

            mockMvc.perform(post("/api/applications")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validCreateRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.applicantName").value("John Doe"));
        }

        @Test
        @WithMockUser(roles = {"HR_MANAGER"})
        @DisplayName("should return 400 when request body is invalid")
        void submitApplication_InvalidRequest_Returns400() throws Exception {
            ApplicationCreateRequest invalidRequest = new ApplicationCreateRequest();
            // Missing required applicantId and jobAdId

            mockMvc.perform(post("/api/applications")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = {"RECRUITER"})
        @DisplayName("should return 400 when service throws IllegalArgumentException")
        void submitApplication_ServiceThrowsIllegalArg_Returns400() throws Exception {
            when(applicationService.submitApplication(any(ApplicationCreateRequest.class)))
                    .thenThrow(new IllegalArgumentException("Applicant already applied for this job"));

            mockMvc.perform(post("/api/applications")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validCreateRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Applicant already applied for this job"));
        }

        @Test
        @DisplayName("should return 401 when not authenticated")
        void submitApplication_NotAuthenticated_Returns401() throws Exception {
            mockMvc.perform(post("/api/applications")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validCreateRequest)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("PUT /api/applications/{id}/status")
    class UpdateApplicationStatus {

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should update application status and return 200")
        void updateApplicationStatus_ValidRequest_Returns200() throws Exception {
            sampleResponse.setStatus(ApplicationStatus.SCREENING);
            when(applicationService.updateApplicationStatus(eq(1L), eq(ApplicationStatus.SCREENING), any()))
                    .thenReturn(sampleResponse);

            mockMvc.perform(put("/api/applications/1/status")
                            .with(csrf())
                            .param("status", "SCREENING")
                            .param("notes", "Moving to screening phase"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SCREENING"));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should return 400 when status transition is invalid")
        void updateApplicationStatus_InvalidTransition_Returns400() throws Exception {
            when(applicationService.updateApplicationStatus(eq(1L), eq(ApplicationStatus.HIRED), any()))
                    .thenThrow(new IllegalArgumentException("Invalid status transition"));

            mockMvc.perform(put("/api/applications/1/status")
                            .with(csrf())
                            .param("status", "HIRED"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Invalid status transition"));
        }
    }

    @Nested
    @DisplayName("POST /api/applications/{id}/withdraw")
    class WithdrawApplication {

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should withdraw application and return 200")
        void withdrawApplication_ValidRequest_Returns200() throws Exception {
            sampleResponse.setStatus(ApplicationStatus.WITHDRAWN);
            ApplicationWithdrawRequest withdrawRequest = new ApplicationWithdrawRequest("Found another position");
            when(applicationService.withdrawApplication(eq(1L), any(ApplicationWithdrawRequest.class)))
                    .thenReturn(sampleResponse);

            mockMvc.perform(post("/api/applications/1/withdraw")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(withdrawRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("WITHDRAWN"));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should return 400 when withdrawal reason is blank")
        void withdrawApplication_BlankReason_Returns400() throws Exception {
            ApplicationWithdrawRequest invalidRequest = new ApplicationWithdrawRequest();
            // reason is @NotBlank, so blank/null should fail validation

            mockMvc.perform(post("/api/applications/1/withdraw")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should return 400 when application cannot be withdrawn")
        void withdrawApplication_InvalidState_Returns400() throws Exception {
            ApplicationWithdrawRequest withdrawRequest = new ApplicationWithdrawRequest("Changed my mind");
            when(applicationService.withdrawApplication(eq(1L), any(ApplicationWithdrawRequest.class)))
                    .thenThrow(new IllegalArgumentException("Application is already withdrawn"));

            mockMvc.perform(post("/api/applications/1/withdraw")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(withdrawRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Application is already withdrawn"));
        }
    }

    @Nested
    @DisplayName("POST /api/applications/{id}/rate")
    class RateApplication {

        @Test
        @WithMockUser(roles = {"HR_MANAGER"})
        @DisplayName("should rate application and return 200")
        void rateApplication_ValidRequest_Returns200() throws Exception {
            sampleResponse.setRating(4);
            when(applicationService.rateApplication(eq(1L), eq(4), eq("Good candidate")))
                    .thenReturn(sampleResponse);

            mockMvc.perform(post("/api/applications/1/rate")
                            .with(csrf())
                            .param("rating", "4")
                            .param("feedback", "Good candidate"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.rating").value(4));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should return 400 when rating is invalid")
        void rateApplication_InvalidRating_Returns400() throws Exception {
            when(applicationService.rateApplication(eq(1L), eq(10), any()))
                    .thenThrow(new IllegalArgumentException("Rating must be between 1 and 5"));

            mockMvc.perform(post("/api/applications/1/rate")
                            .with(csrf())
                            .param("rating", "10"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Specialized Query Endpoints")
    class SpecializedQueries {

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("GET /api/applications/applicant/{applicantId} should return applicant's applications")
        void getApplicationsByApplicant_ReturnsResults() throws Exception {
            when(applicationService.getApplicationsByApplicant(1L)).thenReturn(List.of(sampleResponse));

            mockMvc.perform(get("/api/applications/applicant/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @WithMockUser(roles = {"RECRUITER"})
        @DisplayName("GET /api/applications/job/{jobAdId} should return job's applications")
        void getApplicationsByJobAd_ReturnsResults() throws Exception {
            when(applicationService.getApplicationsByJobAd(10L)).thenReturn(List.of(sampleResponse));

            mockMvc.perform(get("/api/applications/job/10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @WithMockUser(roles = {"HR_MANAGER"})
        @DisplayName("GET /api/applications/status/{status} should return applications by status")
        void getApplicationsByStatus_ReturnsResults() throws Exception {
            when(applicationService.getApplicationsByStatus(ApplicationStatus.SUBMITTED))
                    .thenReturn(List.of(sampleResponse));

            mockMvc.perform(get("/api/applications/status/SUBMITTED"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("GET /api/applications/can-apply should check if applicant can apply")
        void canApplicantApplyForJob_ReturnsResult() throws Exception {
            when(applicationService.canApplicantApplyForJob(1L, 10L)).thenReturn(true);

            mockMvc.perform(get("/api/applications/can-apply")
                            .param("applicantId", "1")
                            .param("jobAdId", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.canApply").value(true));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("GET /api/applications/requiring-action should return pending applications")
        void getApplicationsRequiringAction_ReturnsResults() throws Exception {
            when(applicationService.getApplicationsRequiringAction()).thenReturn(List.of(sampleResponse));

            mockMvc.perform(get("/api/applications/requiring-action"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("GET /api/applications/recent should return recent applications")
        void getRecentApplications_ReturnsResults() throws Exception {
            when(applicationService.getRecentApplications(7)).thenReturn(List.of(sampleResponse));

            mockMvc.perform(get("/api/applications/recent"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("GET /api/applications/statistics should return statistics")
        void getApplicationStatistics_ReturnsResults() throws Exception {
            when(applicationService.getApplicationStatusStatistics())
                    .thenReturn(List.of(new Object[]{"SUBMITTED", 10L}));

            mockMvc.perform(get("/api/applications/statistics"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }
    }

    @Nested
    @DisplayName("DELETE /api/applications/{id}")
    class DeleteApplication {

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should delete application and return 204")
        void deleteApplication_ValidRequest_Returns204() throws Exception {
            doNothing().when(applicationService).deleteApplication(1L);

            mockMvc.perform(delete("/api/applications/1")
                            .with(csrf()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should return 400 when application cannot be deleted")
        void deleteApplication_InvalidState_Returns400() throws Exception {
            doThrow(new IllegalArgumentException("Cannot delete hired application"))
                    .when(applicationService).deleteApplication(1L);

            mockMvc.perform(delete("/api/applications/1")
                            .with(csrf()))
                    .andExpect(status().isBadRequest());
        }
    }
}
