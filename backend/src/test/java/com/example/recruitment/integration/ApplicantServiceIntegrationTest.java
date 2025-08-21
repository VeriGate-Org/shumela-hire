package com.example.recruitment.integration;

import com.example.recruitment.entity.Applicant;
import com.example.recruitment.repository.ApplicantRepository;
import com.example.recruitment.service.ApplicantService;
import com.example.recruitment.dto.ApplicantCreateRequest;
import com.example.recruitment.dto.ApplicantResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ApplicantServiceIntegrationTest {

    @Autowired
    private ApplicantService applicantService;

    @Autowired
    private ApplicantRepository applicantRepository;

    private ApplicantCreateRequest testRequest;

    @BeforeEach
    void setUp() {
        // Clean up any existing test data
        applicantRepository.deleteAll();

        // Set up test request
        testRequest = new ApplicantCreateRequest();
        testRequest.setName("Integration");
        testRequest.setSurname("Test");
        testRequest.setEmail("integration.test@example.com");
        testRequest.setPhone("+1234567890");
        testRequest.setIdPassportNumber("INT123456");
        testRequest.setAddress("123 Integration St, Test City, TC");
        testRequest.setEducation("{\"degree\": \"Computer Science\", \"university\": \"Test University\"}");
        testRequest.setExperience("{\"years\": 3, \"companies\": [\"Test Corp\"]}");
        testRequest.setSkills("[\"Java\", \"Spring Boot\", \"Testing\"]");
    }

    @Test
    void createApplicant_FullIntegration_Success() {
        // When
        ApplicantResponse response = applicantService.createApplicant(testRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getName()).isEqualTo("Integration");
        assertThat(response.getSurname()).isEqualTo("Test");
        assertThat(response.getEmail()).isEqualTo("integration.test@example.com");

        // Verify it was actually saved in the database
        Applicant savedApplicant = applicantRepository.findByEmail("integration.test@example.com").orElse(null);
        assertThat(savedApplicant).isNotNull();
        assertThat(savedApplicant.getName()).isEqualTo("Integration");
        assertThat(savedApplicant.getSurname()).isEqualTo("Test");
    }

    @Test
    void updateApplicant_FullIntegration_Success() {
        // Given
        ApplicantResponse createdApplicant = applicantService.createApplicant(testRequest);

        ApplicantCreateRequest updateRequest = new ApplicantCreateRequest();
        updateRequest.setName("Updated");
        updateRequest.setSurname("Name");
        updateRequest.setEmail("updated.name@example.com");
        updateRequest.setPhone("+0987654321");
        updateRequest.setIdPassportNumber("UPD123456");
        updateRequest.setAddress("456 Updated Ave, New City, NC");
        updateRequest.setEducation("{\"degree\": \"Software Engineering\", \"university\": \"Updated University\"}");
        updateRequest.setExperience("{\"years\": 5, \"companies\": [\"Updated Corp\"]}");
        updateRequest.setSkills("[\"Python\", \"Django\", \"PostgreSQL\"]");

        // When
        ApplicantResponse updatedResponse = applicantService.updateApplicant(createdApplicant.getId(), updateRequest);

        // Then
        assertThat(updatedResponse).isNotNull();
        assertThat(updatedResponse.getId()).isEqualTo(createdApplicant.getId());
        assertThat(updatedResponse.getName()).isEqualTo("Updated");
        assertThat(updatedResponse.getSurname()).isEqualTo("Name");
        assertThat(updatedResponse.getEmail()).isEqualTo("updated.name@example.com");

        // Verify it was actually updated in the database
        Applicant updatedApplicant = applicantRepository.findById(createdApplicant.getId()).orElse(null);
        assertThat(updatedApplicant).isNotNull();
        assertThat(updatedApplicant.getName()).isEqualTo("Updated");
        assertThat(updatedApplicant.getSurname()).isEqualTo("Name");
        assertThat(updatedApplicant.getEmail()).isEqualTo("updated.name@example.com");
    }

    @Test
    void getApplicant_FullIntegration_Success() {
        // Given
        ApplicantResponse createdApplicant = applicantService.createApplicant(testRequest);

        // When
        ApplicantResponse retrievedApplicant = applicantService.getApplicant(createdApplicant.getId());

        // Then
        assertThat(retrievedApplicant).isNotNull();
        assertThat(retrievedApplicant.getId()).isEqualTo(createdApplicant.getId());
        assertThat(retrievedApplicant.getName()).isEqualTo("Integration");
        assertThat(retrievedApplicant.getSurname()).isEqualTo("Test");
        assertThat(retrievedApplicant.getEmail()).isEqualTo("integration.test@example.com");
    }

    @Test
    void applicantLifecycle_FullIntegration_AllOperationsWork() {
        // Create
        ApplicantResponse created = applicantService.createApplicant(testRequest);
        assertThat(created.getId()).isNotNull();
        assertThat(applicantRepository.count()).isEqualTo(1);

        // Read
        ApplicantResponse retrieved = applicantService.getApplicant(created.getId());
        assertThat(retrieved.getName()).isEqualTo("Integration");

        // Update
        ApplicantCreateRequest updateRequest = new ApplicantCreateRequest();
        updateRequest.setName("Lifecycle");
        updateRequest.setSurname("Test");
        updateRequest.setEmail("lifecycle.test@example.com");
        updateRequest.setPhone("+1111111111");

        ApplicantResponse updated = applicantService.updateApplicant(created.getId(), updateRequest);
        assertThat(updated.getName()).isEqualTo("Lifecycle");
        assertThat(updated.getEmail()).isEqualTo("lifecycle.test@example.com");

        // Verify final state
        Applicant finalApplicant = applicantRepository.findById(created.getId()).orElse(null);
        assertThat(finalApplicant).isNotNull();
        assertThat(finalApplicant.getName()).isEqualTo("Lifecycle");
        assertThat(finalApplicant.getEmail()).isEqualTo("lifecycle.test@example.com");
    }

    @Test
    void emailUniqueness_FullIntegration_EnforcedCorrectly() {
        // Given
        applicantService.createApplicant(testRequest);

        // When & Then
        ApplicantCreateRequest duplicateRequest = new ApplicantCreateRequest();
        duplicateRequest.setName("Duplicate");
        duplicateRequest.setSurname("Test");
        duplicateRequest.setEmail("integration.test@example.com"); // Same email

        try {
            applicantService.createApplicant(duplicateRequest);
            // Should not reach this point
            assertThat(false).isTrue();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("Email already exists");
        }

        // Verify only one applicant exists
        assertThat(applicantRepository.count()).isEqualTo(1);
    }
}
