package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.dto.JobPostingCreateRequest;
import com.arthmatic.shumelahire.dto.JobPostingResponse;
import com.arthmatic.shumelahire.entity.EmploymentType;
import com.arthmatic.shumelahire.entity.ExperienceLevel;
import com.arthmatic.shumelahire.entity.JobPostingStatus;
import com.arthmatic.shumelahire.security.JwtAuthenticationEntryPoint;
import com.arthmatic.shumelahire.security.JwtAuthenticationFilter;
import com.arthmatic.shumelahire.security.JwtUtil;
import com.arthmatic.shumelahire.security.RateLimitFilter;
import com.arthmatic.shumelahire.service.CustomUserDetailsService;
import com.arthmatic.shumelahire.service.JobPostingService;
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

import java.math.BigDecimal;
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

@WebMvcTest(JobPostingController.class)
@ActiveProfiles("dev")
@DisplayName("JobPostingController Integration Tests")
class JobPostingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JobPostingService jobPostingService;

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

    private JobPostingCreateRequest validRequest;
    private JobPostingResponse sampleResponse;

    @BeforeEach
    void setUp() {
        validRequest = new JobPostingCreateRequest();
        validRequest.setTitle("Senior Java Developer");
        validRequest.setDepartment("Engineering");
        validRequest.setLocation("Cape Town");
        validRequest.setEmploymentType(EmploymentType.FULL_TIME);
        validRequest.setExperienceLevel(ExperienceLevel.SENIOR);
        validRequest.setDescription("We are looking for a Senior Java Developer.");
        validRequest.setPositionsAvailable(2);
        validRequest.setSalaryMin(new BigDecimal("500000"));
        validRequest.setSalaryMax(new BigDecimal("800000"));

        sampleResponse = new JobPostingResponse();
        sampleResponse.setId(1L);
        sampleResponse.setTitle("Senior Java Developer");
        sampleResponse.setDepartment("Engineering");
        sampleResponse.setLocation("Cape Town");
        sampleResponse.setEmploymentType(EmploymentType.FULL_TIME);
        sampleResponse.setExperienceLevel(ExperienceLevel.SENIOR);
        sampleResponse.setDescription("We are looking for a Senior Java Developer.");
        sampleResponse.setStatus(JobPostingStatus.DRAFT);
        sampleResponse.setCreatedBy(1L);
        sampleResponse.setCreatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("GET /api/job-postings")
    class SearchJobPostings {

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should return paginated job postings")
        void searchJobPostings_Authenticated_ReturnsPaginatedResults() throws Exception {
            Page<JobPostingResponse> page = new PageImpl<>(List.of(sampleResponse));
            when(jobPostingService.searchJobPostings(any(), any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/api/job-postings"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].title").value("Senior Java Developer"));
        }

        @Test
        @WithMockUser(roles = {"HR_MANAGER"})
        @DisplayName("should return empty page when no postings exist")
        void searchJobPostings_NoPostings_ReturnsEmptyPage() throws Exception {
            Page<JobPostingResponse> emptyPage = new PageImpl<>(Collections.emptyList());
            when(jobPostingService.searchJobPostings(any(), any(Pageable.class))).thenReturn(emptyPage);

            mockMvc.perform(get("/api/job-postings"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", hasSize(0)));
        }

        @Test
        @DisplayName("should return 401 when not authenticated")
        void searchJobPostings_NotAuthenticated_Returns401() throws Exception {
            mockMvc.perform(get("/api/job-postings"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(roles = {"APPLICANT"})
        @DisplayName("should return 403 when user lacks required role")
        void searchJobPostings_InsufficientRole_Returns403() throws Exception {
            mockMvc.perform(get("/api/job-postings"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /api/job-postings/{id}")
    class GetJobPosting {

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should return job posting by ID")
        void getJobPosting_ExistingId_ReturnsJobPosting() throws Exception {
            when(jobPostingService.getJobPosting(1L)).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/job-postings/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("Senior Java Developer"))
                    .andExpect(jsonPath("$.department").value("Engineering"));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should return 404 when job posting not found")
        void getJobPosting_NonExistingId_Returns404() throws Exception {
            when(jobPostingService.getJobPosting(999L)).thenThrow(new IllegalArgumentException("Not found"));

            mockMvc.perform(get("/api/job-postings/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/job-postings/slug/{slug}")
    class GetJobPostingBySlug {

        @Test
        @WithMockUser(roles = {"HR_MANAGER"})
        @DisplayName("should return job posting by slug")
        void getJobPostingBySlug_ExistingSlug_ReturnsJobPosting() throws Exception {
            sampleResponse.setSlug("senior-java-developer");
            when(jobPostingService.getJobPostingBySlug("senior-java-developer")).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/job-postings/slug/senior-java-developer"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.slug").value("senior-java-developer"));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should return 404 when slug not found")
        void getJobPostingBySlug_NonExistingSlug_Returns404() throws Exception {
            when(jobPostingService.getJobPostingBySlug("nonexistent"))
                    .thenThrow(new IllegalArgumentException("Not found"));

            mockMvc.perform(get("/api/job-postings/slug/nonexistent"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/job-postings")
    class CreateJobPosting {

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should create job posting and return 201")
        void createJobPosting_ValidRequest_Returns201() throws Exception {
            when(jobPostingService.createJobPosting(any(JobPostingCreateRequest.class), eq(1L)))
                    .thenReturn(sampleResponse);

            mockMvc.perform(post("/api/job-postings")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest))
                            .param("createdBy", "1"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("Senior Java Developer"));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should return 400 when request body is invalid")
        void createJobPosting_InvalidRequest_Returns400() throws Exception {
            JobPostingCreateRequest invalidRequest = new JobPostingCreateRequest();
            // Missing required fields: title, department, description, employmentType, experienceLevel

            mockMvc.perform(post("/api/job-postings")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest))
                            .param("createdBy", "1"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should return 400 when service throws IllegalArgumentException")
        void createJobPosting_ServiceThrowsIllegalArg_Returns400() throws Exception {
            when(jobPostingService.createJobPosting(any(JobPostingCreateRequest.class), eq(1L)))
                    .thenThrow(new IllegalArgumentException("Duplicate title"));

            mockMvc.perform(post("/api/job-postings")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest))
                            .param("createdBy", "1"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Duplicate title"));
        }

        @Test
        @DisplayName("should return 401 when not authenticated")
        void createJobPosting_NotAuthenticated_Returns401() throws Exception {
            mockMvc.perform(post("/api/job-postings")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest))
                            .param("createdBy", "1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(roles = {"APPLICANT"})
        @DisplayName("should return 403 when user lacks ADMIN or HR_MANAGER role")
        void createJobPosting_InsufficientRole_Returns403() throws Exception {
            mockMvc.perform(post("/api/job-postings")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest))
                            .param("createdBy", "1"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("PUT /api/job-postings/{id}")
    class UpdateJobPosting {

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should update job posting and return 200")
        void updateJobPosting_ValidRequest_Returns200() throws Exception {
            sampleResponse.setTitle("Updated Title");
            when(jobPostingService.updateJobPosting(eq(1L), any(JobPostingCreateRequest.class), eq(1L)))
                    .thenReturn(sampleResponse);

            mockMvc.perform(put("/api/job-postings/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest))
                            .param("updatedBy", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Updated Title"));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should return 400 when update fails due to invalid state")
        void updateJobPosting_InvalidState_Returns400() throws Exception {
            when(jobPostingService.updateJobPosting(eq(1L), any(JobPostingCreateRequest.class), eq(1L)))
                    .thenThrow(new IllegalStateException("Cannot update published posting"));

            mockMvc.perform(put("/api/job-postings/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest))
                            .param("updatedBy", "1"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Cannot update published posting"));
        }
    }

    @Nested
    @DisplayName("Status Transition Endpoints")
    class StatusTransitions {

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("POST /api/job-postings/{id}/submit-for-approval should submit posting for approval")
        void submitForApproval_ValidRequest_Returns200() throws Exception {
            sampleResponse.setStatus(JobPostingStatus.PENDING_APPROVAL);
            when(jobPostingService.submitForApproval(eq(1L), eq(1L))).thenReturn(sampleResponse);

            mockMvc.perform(post("/api/job-postings/1/submit-for-approval")
                            .with(csrf())
                            .param("submittedBy", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("PENDING_APPROVAL"));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("POST /api/job-postings/{id}/submit-for-approval should return 400 on invalid state")
        void submitForApproval_InvalidState_Returns400() throws Exception {
            when(jobPostingService.submitForApproval(eq(1L), eq(1L)))
                    .thenThrow(new IllegalStateException("Cannot submit published posting for approval"));

            mockMvc.perform(post("/api/job-postings/1/submit-for-approval")
                            .with(csrf())
                            .param("submittedBy", "1"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("POST /api/job-postings/{id}/approve should approve posting")
        void approveJobPosting_ValidRequest_Returns200() throws Exception {
            sampleResponse.setStatus(JobPostingStatus.APPROVED);
            when(jobPostingService.approveJobPosting(eq(1L), eq(2L), any()))
                    .thenReturn(sampleResponse);

            mockMvc.perform(post("/api/job-postings/1/approve")
                            .with(csrf())
                            .param("approvedBy", "2")
                            .param("approvalNotes", "Looks good"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("APPROVED"));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("POST /api/job-postings/{id}/reject should reject posting")
        void rejectJobPosting_ValidRequest_Returns200() throws Exception {
            sampleResponse.setStatus(JobPostingStatus.REJECTED);
            when(jobPostingService.rejectJobPosting(eq(1L), eq(2L), eq("Budget not approved")))
                    .thenReturn(sampleResponse);

            mockMvc.perform(post("/api/job-postings/1/reject")
                            .with(csrf())
                            .param("rejectedBy", "2")
                            .param("rejectionReason", "Budget not approved"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("REJECTED"));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("POST /api/job-postings/{id}/publish should publish posting")
        void publishJobPosting_ValidRequest_Returns200() throws Exception {
            sampleResponse.setStatus(JobPostingStatus.PUBLISHED);
            when(jobPostingService.publishJobPosting(eq(1L), eq(1L))).thenReturn(sampleResponse);

            mockMvc.perform(post("/api/job-postings/1/publish")
                            .with(csrf())
                            .param("publishedBy", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("PUBLISHED"));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("POST /api/job-postings/{id}/unpublish should unpublish posting")
        void unpublishJobPosting_ValidRequest_Returns200() throws Exception {
            sampleResponse.setStatus(JobPostingStatus.UNPUBLISHED);
            when(jobPostingService.unpublishJobPosting(eq(1L), eq(1L))).thenReturn(sampleResponse);

            mockMvc.perform(post("/api/job-postings/1/unpublish")
                            .with(csrf())
                            .param("unpublishedBy", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("UNPUBLISHED"));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("POST /api/job-postings/{id}/close should close posting")
        void closeJobPosting_ValidRequest_Returns200() throws Exception {
            sampleResponse.setStatus(JobPostingStatus.CLOSED);
            when(jobPostingService.closeJobPosting(eq(1L), eq(1L))).thenReturn(sampleResponse);

            mockMvc.perform(post("/api/job-postings/1/close")
                            .with(csrf())
                            .param("closedBy", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("CLOSED"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/job-postings/{id}")
    class DeleteJobPosting {

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should delete job posting and return 204")
        void deleteJobPosting_ValidRequest_Returns204() throws Exception {
            doNothing().when(jobPostingService).deleteJobPosting(eq(1L), eq(1L));

            mockMvc.perform(delete("/api/job-postings/1")
                            .with(csrf())
                            .param("deletedBy", "1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should return 400 when deletion is not allowed")
        void deleteJobPosting_InvalidState_Returns400() throws Exception {
            doThrow(new IllegalStateException("Cannot delete published posting"))
                    .when(jobPostingService).deleteJobPosting(eq(1L), eq(1L));

            mockMvc.perform(delete("/api/job-postings/1")
                            .with(csrf())
                            .param("deletedBy", "1"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Specialized Query Endpoints")
    class SpecializedQueries {

        @Test
        @WithMockUser(roles = {"HR_MANAGER"})
        @DisplayName("GET /api/job-postings/status/{status} should return postings by status")
        void getJobPostingsByStatus_ValidStatus_ReturnsResults() throws Exception {
            when(jobPostingService.getJobPostingsByStatus(JobPostingStatus.PUBLISHED))
                    .thenReturn(List.of(sampleResponse));

            mockMvc.perform(get("/api/job-postings/status/PUBLISHED"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("GET /api/job-postings/requiring-approval should return pending postings")
        void getJobsRequiringApproval_ReturnsResults() throws Exception {
            when(jobPostingService.getJobsRequiringApproval()).thenReturn(List.of(sampleResponse));

            mockMvc.perform(get("/api/job-postings/requiring-approval"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("GET /api/job-postings/featured should return featured postings")
        void getFeaturedJobs_ReturnsResults() throws Exception {
            when(jobPostingService.getFeaturedJobs()).thenReturn(List.of(sampleResponse));

            mockMvc.perform(get("/api/job-postings/featured"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("GET /api/job-postings/urgent should return urgent postings")
        void getUrgentJobs_ReturnsResults() throws Exception {
            when(jobPostingService.getUrgentJobs()).thenReturn(List.of(sampleResponse));

            mockMvc.perform(get("/api/job-postings/urgent"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("GET /api/job-postings/published should return published postings")
        void getPublishedJobs_ReturnsPaginatedResults() throws Exception {
            Page<JobPostingResponse> page = new PageImpl<>(List.of(sampleResponse));
            when(jobPostingService.getPublishedJobs(any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/api/job-postings/published"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("GET /api/job-postings/creator/{createdBy} should return creator's postings")
        void getJobPostingsByCreator_ReturnsResults() throws Exception {
            Page<JobPostingResponse> page = new PageImpl<>(List.of(sampleResponse));
            when(jobPostingService.getJobPostingsByCreator(eq(1L), any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/api/job-postings/creator/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("GET /api/job-postings/statistics should return statistics")
        void getJobPostingStatistics_ReturnsResults() throws Exception {
            when(jobPostingService.getJobPostingStatistics())
                    .thenReturn(List.of(new Object[]{"PUBLISHED", 5L}));

            mockMvc.perform(get("/api/job-postings/statistics"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }
    }
}
