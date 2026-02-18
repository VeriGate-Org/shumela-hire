package com.arthmatic.shumelahire.controller;

import com.arthmatic.shumelahire.entity.User;
import com.arthmatic.shumelahire.repository.UserRepository;
import com.arthmatic.shumelahire.security.JwtAuthenticationEntryPoint;
import com.arthmatic.shumelahire.security.JwtAuthenticationFilter;
import com.arthmatic.shumelahire.security.JwtUtil;
import com.arthmatic.shumelahire.security.RateLimitFilter;
import com.arthmatic.shumelahire.service.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserInfoController.class)
@ActiveProfiles("dev")
@DisplayName("UserInfoController Integration Tests")
class UserInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

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

    @Nested
    @DisplayName("GET /api/auth/me")
    class GetCurrentUser {

        @Test
        @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
        @DisplayName("should return user info when authenticated with ADMIN role")
        void getCurrentUser_Authenticated_ReturnsUserInfo() throws Exception {
            mockMvc.perform(get("/api/auth/me"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.roles").isArray())
                    .andExpect(jsonPath("$.roles", hasItem("ROLE_ADMIN")));
        }

        @Test
        @WithMockUser(username = "hr@test.com", roles = {"HR_MANAGER"})
        @DisplayName("should return user info when authenticated with HR_MANAGER role")
        void getCurrentUser_AuthenticatedHrManager_ReturnsUserInfo() throws Exception {
            mockMvc.perform(get("/api/auth/me"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.roles").isArray())
                    .andExpect(jsonPath("$.roles", hasItem("ROLE_HR_MANAGER")));
        }

        @Test
        @WithMockUser(username = "recruiter@test.com", roles = {"RECRUITER"})
        @DisplayName("should return roles in the response body")
        void getCurrentUser_Authenticated_ContainsRoles() throws Exception {
            mockMvc.perform(get("/api/auth/me"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.roles").exists())
                    .andExpect(jsonPath("$.roles").isArray())
                    .andExpect(jsonPath("$.roles").isNotEmpty());
        }

        @Test
        @DisplayName("should permit access without authentication since /api/auth/** is permitAll")
        void getCurrentUser_NotAuthenticated_PermitAllEndpoint() throws Exception {
            // /api/auth/** is configured as permitAll in SecurityConfig.
            // When no authentication is present, the Authentication parameter will be null,
            // and the controller returns 401 in the response body (not from Spring Security).
            mockMvc.perform(get("/api/auth/me"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.error").value("Not authenticated"));
        }

        @Test
        @WithMockUser(username = "user@test.com", roles = {"APPLICANT"})
        @DisplayName("should return principal name for non-JWT non-User principal")
        void getCurrentUser_MockUser_ReturnsPrincipalInfo() throws Exception {
            // @WithMockUser creates a UsernamePasswordAuthenticationToken
            // whose principal is a simple User (Spring Security's User), not our custom User entity.
            // The controller will fall into the "else" branch setting the principal name.
            mockMvc.perform(get("/api/auth/me"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.roles").isArray());
        }
    }
}
