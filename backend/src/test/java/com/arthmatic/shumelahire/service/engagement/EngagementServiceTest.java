package com.arthmatic.shumelahire.service.engagement;

import com.arthmatic.shumelahire.dto.engagement.*;
import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.engagement.*;
import com.arthmatic.shumelahire.repository.EmployeeRepository;
import com.arthmatic.shumelahire.repository.engagement.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EngagementServiceTest {

    @Mock private SurveyRepository surveyRepository;
    @Mock private SurveyQuestionRepository surveyQuestionRepository;
    @Mock private SurveyResponseRepository surveyResponseRepository;
    @Mock private RecognitionRepository recognitionRepository;
    @Mock private WellnessProgramRepository wellnessProgramRepository;
    @Mock private WellnessCheckInRepository wellnessCheckInRepository;
    @Mock private SocialPostRepository socialPostRepository;
    @Mock private EmployeeRepository employeeRepository;

    @InjectMocks
    private EngagementService engagementService;

    private Employee testEmployee;
    private Employee testEmployee2;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee();
        testEmployee.setId(1L);
        testEmployee.setFirstName("Jane");
        testEmployee.setLastName("Smith");
        testEmployee.setEmail("jane.smith@example.com");
        testEmployee.setEmployeeNumber("EMP001");
        testEmployee.setHireDate(LocalDate.now());

        testEmployee2 = new Employee();
        testEmployee2.setId(2L);
        testEmployee2.setFirstName("John");
        testEmployee2.setLastName("Doe");
        testEmployee2.setEmail("john.doe@example.com");
        testEmployee2.setEmployeeNumber("EMP002");
        testEmployee2.setHireDate(LocalDate.now());
    }

    // ==================== Survey Tests ====================

    @Test
    void createSurvey_ValidRequest_ReturnsSurvey() {
        SurveyRequest request = new SurveyRequest();
        request.setTitle("Employee Pulse Survey Q1");
        request.setDescription("Quarterly pulse survey");
        request.setSurveyType(SurveyType.PULSE);
        request.setIsAnonymous(true);

        Survey saved = new Survey();
        saved.setId(1L);
        saved.setTitle("Employee Pulse Survey Q1");
        saved.setDescription("Quarterly pulse survey");
        saved.setSurveyType(SurveyType.PULSE);
        saved.setStatus(SurveyStatus.DRAFT);
        saved.setCreatedBy("admin@example.com");
        saved.setIsAnonymous(true);

        when(surveyRepository.save(any(Survey.class))).thenReturn(saved);

        SurveyResponseDTO response = engagementService.createSurvey(request, "admin@example.com");

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Employee Pulse Survey Q1");
        assertThat(response.getSurveyType()).isEqualTo(SurveyType.PULSE);
        assertThat(response.getStatus()).isEqualTo(SurveyStatus.DRAFT);
        verify(surveyRepository, times(1)).save(any(Survey.class));
    }

    @Test
    void createSurvey_WithQuestions_SavesQuestions() {
        SurveyQuestionRequest qr = new SurveyQuestionRequest();
        qr.setQuestionText("How satisfied are you?");
        qr.setQuestionType(QuestionType.RATING);

        SurveyRequest request = new SurveyRequest();
        request.setTitle("Test Survey");
        request.setSurveyType(SurveyType.ENGAGEMENT);
        request.setQuestions(Arrays.asList(qr));

        Survey saved = new Survey();
        saved.setId(1L);
        saved.setTitle("Test Survey");
        saved.setSurveyType(SurveyType.ENGAGEMENT);
        saved.setStatus(SurveyStatus.DRAFT);
        saved.setCreatedBy("admin");

        SurveyQuestion savedQ = new SurveyQuestion();
        savedQ.setId(1L);
        savedQ.setSurvey(saved);
        savedQ.setQuestionText("How satisfied are you?");

        when(surveyRepository.save(any(Survey.class))).thenReturn(saved);
        when(surveyQuestionRepository.save(any(SurveyQuestion.class))).thenReturn(savedQ);

        engagementService.createSurvey(request, "admin");

        verify(surveyQuestionRepository, times(1)).save(any(SurveyQuestion.class));
    }

    @Test
    void activateSurvey_DraftSurvey_SetsActive() {
        Survey survey = new Survey();
        survey.setId(1L);
        survey.setTitle("Test Survey");
        survey.setStatus(SurveyStatus.DRAFT);
        survey.setCreatedBy("admin");
        survey.setSurveyType(SurveyType.PULSE);

        Survey activated = new Survey();
        activated.setId(1L);
        activated.setTitle("Test Survey");
        activated.setStatus(SurveyStatus.ACTIVE);
        activated.setCreatedBy("admin");
        activated.setSurveyType(SurveyType.PULSE);

        when(surveyRepository.findById(1L)).thenReturn(Optional.of(survey));
        when(surveyRepository.save(any(Survey.class))).thenReturn(activated);

        SurveyResponseDTO response = engagementService.activateSurvey(1L);

        assertThat(response.getStatus()).isEqualTo(SurveyStatus.ACTIVE);
    }

    @Test
    void activateSurvey_AlreadyActive_ThrowsException() {
        Survey survey = new Survey();
        survey.setId(1L);
        survey.setStatus(SurveyStatus.ACTIVE);
        survey.setCreatedBy("admin");
        survey.setSurveyType(SurveyType.PULSE);

        when(surveyRepository.findById(1L)).thenReturn(Optional.of(survey));

        assertThrows(IllegalStateException.class, () -> engagementService.activateSurvey(1L));
    }

    @Test
    void closeSurvey_ActiveSurvey_SetsClosed() {
        Survey survey = new Survey();
        survey.setId(1L);
        survey.setTitle("Test Survey");
        survey.setStatus(SurveyStatus.ACTIVE);
        survey.setCreatedBy("admin");
        survey.setSurveyType(SurveyType.PULSE);

        Survey closed = new Survey();
        closed.setId(1L);
        closed.setTitle("Test Survey");
        closed.setStatus(SurveyStatus.CLOSED);
        closed.setCreatedBy("admin");
        closed.setSurveyType(SurveyType.PULSE);

        when(surveyRepository.findById(1L)).thenReturn(Optional.of(survey));
        when(surveyRepository.save(any(Survey.class))).thenReturn(closed);

        SurveyResponseDTO response = engagementService.closeSurvey(1L);

        assertThat(response.getStatus()).isEqualTo(SurveyStatus.CLOSED);
    }

    @Test
    void submitSurveyResponses_InactiveSurvey_ThrowsException() {
        Survey survey = new Survey();
        survey.setId(1L);
        survey.setStatus(SurveyStatus.CLOSED);
        survey.setCreatedBy("admin");
        survey.setSurveyType(SurveyType.PULSE);

        when(surveyRepository.findById(1L)).thenReturn(Optional.of(survey));

        SurveyAnswerRequest request = new SurveyAnswerRequest();
        request.setSurveyId(1L);

        assertThrows(IllegalStateException.class, () -> engagementService.submitSurveyResponses(request));
    }

    @Test
    void submitSurveyResponses_DuplicateToken_ThrowsException() {
        Survey survey = new Survey();
        survey.setId(1L);
        survey.setStatus(SurveyStatus.ACTIVE);
        survey.setCreatedBy("admin");
        survey.setSurveyType(SurveyType.PULSE);
        survey.setIsAnonymous(true);

        when(surveyRepository.findById(1L)).thenReturn(Optional.of(survey));
        when(surveyResponseRepository.existsBySurveyIdAndAnonymousToken(1L, "existing-token")).thenReturn(true);

        SurveyAnswerRequest request = new SurveyAnswerRequest();
        request.setSurveyId(1L);
        request.setAnonymousToken("existing-token");

        assertThrows(IllegalStateException.class, () -> engagementService.submitSurveyResponses(request));
    }

    @Test
    void getSurvey_NotFound_ThrowsException() {
        when(surveyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> engagementService.getSurvey(99L));
    }

    // ==================== Recognition Tests ====================

    @Test
    void createRecognition_ValidRequest_ReturnsRecognition() {
        RecognitionRequest request = new RecognitionRequest();
        request.setGiverId(1L);
        request.setReceiverId(2L);
        request.setBadge(RecognitionBadge.TEAM_PLAYER);
        request.setMessage("Great teamwork on the project!");

        Recognition saved = new Recognition();
        saved.setId(1L);
        saved.setGiver(testEmployee);
        saved.setReceiver(testEmployee2);
        saved.setBadge(RecognitionBadge.TEAM_PLAYER);
        saved.setMessage("Great teamwork on the project!");
        saved.setPoints(15);
        saved.setIsPublic(true);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(testEmployee2));
        when(recognitionRepository.save(any(Recognition.class))).thenReturn(saved);

        RecognitionResponse response = engagementService.createRecognition(request);

        assertThat(response).isNotNull();
        assertThat(response.getBadge()).isEqualTo(RecognitionBadge.TEAM_PLAYER);
        assertThat(response.getGiverName()).isEqualTo("Jane Smith");
        assertThat(response.getReceiverName()).isEqualTo("John Doe");
        assertThat(response.getPoints()).isEqualTo(15);
        verify(recognitionRepository, times(1)).save(any(Recognition.class));
    }

    @Test
    void createRecognition_SelfRecognition_ThrowsException() {
        RecognitionRequest request = new RecognitionRequest();
        request.setGiverId(1L);
        request.setReceiverId(1L);
        request.setBadge(RecognitionBadge.STAR_PERFORMER);
        request.setMessage("I'm great!");

        assertThrows(IllegalArgumentException.class, () -> engagementService.createRecognition(request));
        verify(recognitionRepository, never()).save(any());
    }

    @Test
    void createRecognition_EmployeeNotFound_ThrowsException() {
        RecognitionRequest request = new RecognitionRequest();
        request.setGiverId(1L);
        request.setReceiverId(99L);
        request.setBadge(RecognitionBadge.INNOVATOR);
        request.setMessage("Great innovation!");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> engagementService.createRecognition(request));
    }

    @Test
    void getTotalPointsForEmployee_ReturnsPoints() {
        when(recognitionRepository.getTotalPointsByReceiverId(1L)).thenReturn(150L);

        Long points = engagementService.getTotalPointsForEmployee(1L);

        assertThat(points).isEqualTo(150L);
    }

    @Test
    void getTotalPointsForEmployee_NullPoints_ReturnsZero() {
        when(recognitionRepository.getTotalPointsByReceiverId(99L)).thenReturn(null);

        Long points = engagementService.getTotalPointsForEmployee(99L);

        assertThat(points).isEqualTo(0L);
    }

    @Test
    void getPublicRecognitions_ReturnsPaginatedResults() {
        Recognition recognition = new Recognition();
        recognition.setId(1L);
        recognition.setGiver(testEmployee);
        recognition.setReceiver(testEmployee2);
        recognition.setBadge(RecognitionBadge.TEAM_PLAYER);
        recognition.setMessage("Well done!");
        recognition.setPoints(15);
        recognition.setIsPublic(true);

        Page<Recognition> page = new PageImpl<>(Arrays.asList(recognition));
        Pageable pageable = PageRequest.of(0, 20);

        when(recognitionRepository.findPublicByTenantId("default", pageable)).thenReturn(page);

        Page<RecognitionResponse> result = engagementService.getPublicRecognitions("default", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getBadge()).isEqualTo(RecognitionBadge.TEAM_PLAYER);
    }

    // ==================== Wellness Program Tests ====================

    @Test
    void createWellnessProgram_ValidRequest_ReturnsProgram() {
        WellnessProgramRequest request = new WellnessProgramRequest();
        request.setName("Mindfulness Monday");
        request.setDescription("Weekly mindfulness sessions");
        request.setCategory(WellnessCategory.MENTAL);

        WellnessProgram saved = new WellnessProgram();
        saved.setId(1L);
        saved.setName("Mindfulness Monday");
        saved.setDescription("Weekly mindfulness sessions");
        saved.setCategory(WellnessCategory.MENTAL);
        saved.setIsActive(true);

        when(wellnessProgramRepository.save(any(WellnessProgram.class))).thenReturn(saved);

        WellnessProgramResponse response = engagementService.createWellnessProgram(request);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Mindfulness Monday");
        assertThat(response.getCategory()).isEqualTo(WellnessCategory.MENTAL);
        verify(wellnessProgramRepository, times(1)).save(any(WellnessProgram.class));
    }

    @Test
    void deleteWellnessProgram_SetsInactive() {
        WellnessProgram program = new WellnessProgram();
        program.setId(1L);
        program.setName("Test Program");
        program.setCategory(WellnessCategory.PHYSICAL);
        program.setIsActive(true);

        when(wellnessProgramRepository.findById(1L)).thenReturn(Optional.of(program));
        when(wellnessProgramRepository.save(any(WellnessProgram.class))).thenReturn(program);

        engagementService.deleteWellnessProgram(1L);

        assertThat(program.getIsActive()).isFalse();
        verify(wellnessProgramRepository, times(1)).save(program);
    }

    @Test
    void deleteWellnessProgram_NotFound_ThrowsException() {
        when(wellnessProgramRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> engagementService.deleteWellnessProgram(99L));
    }

    // ==================== Wellness Check-In Tests ====================

    @Test
    void createWellnessCheckIn_ValidRequest_ReturnsCheckIn() {
        WellnessCheckInRequest request = new WellnessCheckInRequest();
        request.setEmployeeId(1L);
        request.setMoodRating(MoodRating.GOOD);
        request.setEnergyLevel(7);
        request.setStressLevel(4);

        WellnessCheckIn saved = new WellnessCheckIn();
        saved.setId(1L);
        saved.setEmployee(testEmployee);
        saved.setMoodRating(MoodRating.GOOD);
        saved.setEnergyLevel(7);
        saved.setStressLevel(4);
        saved.setCheckInDate(LocalDate.now());

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(wellnessCheckInRepository.save(any(WellnessCheckIn.class))).thenReturn(saved);

        WellnessCheckInResponse response = engagementService.createWellnessCheckIn(request);

        assertThat(response).isNotNull();
        assertThat(response.getMoodRating()).isEqualTo(MoodRating.GOOD);
        assertThat(response.getMoodScore()).isEqualTo(4);
        assertThat(response.getEnergyLevel()).isEqualTo(7);
        assertThat(response.getStressLevel()).isEqualTo(4);
        verify(wellnessCheckInRepository, times(1)).save(any(WellnessCheckIn.class));
    }

    @Test
    void createWellnessCheckIn_WithProgram_LinksProgram() {
        WellnessProgramRequest progReq = new WellnessProgramRequest();
        WellnessProgram program = new WellnessProgram();
        program.setId(10L);
        program.setName("Step Challenge");
        program.setCategory(WellnessCategory.PHYSICAL);

        WellnessCheckInRequest request = new WellnessCheckInRequest();
        request.setEmployeeId(1L);
        request.setMoodRating(MoodRating.GREAT);
        request.setWellnessProgramId(10L);

        WellnessCheckIn saved = new WellnessCheckIn();
        saved.setId(1L);
        saved.setEmployee(testEmployee);
        saved.setMoodRating(MoodRating.GREAT);
        saved.setWellnessProgram(program);
        saved.setCheckInDate(LocalDate.now());

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(wellnessProgramRepository.findById(10L)).thenReturn(Optional.of(program));
        when(wellnessCheckInRepository.save(any(WellnessCheckIn.class))).thenReturn(saved);

        WellnessCheckInResponse response = engagementService.createWellnessCheckIn(request);

        assertThat(response.getWellnessProgramId()).isEqualTo(10L);
        assertThat(response.getWellnessProgramName()).isEqualTo("Step Challenge");
    }

    @Test
    void createWellnessCheckIn_EmployeeNotFound_ThrowsException() {
        WellnessCheckInRequest request = new WellnessCheckInRequest();
        request.setEmployeeId(99L);
        request.setMoodRating(MoodRating.OKAY);

        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> engagementService.createWellnessCheckIn(request));
    }

    // ==================== Social Post Tests ====================

    @Test
    void createSocialPost_ValidRequest_ReturnsPost() {
        SocialPostRequest request = new SocialPostRequest();
        request.setAuthorId(1L);
        request.setPostType(SocialPostType.ANNOUNCEMENT);
        request.setTitle("Company Update");
        request.setContent("We are excited to announce our new office!");

        SocialPost saved = new SocialPost();
        saved.setId(1L);
        saved.setAuthor(testEmployee);
        saved.setPostType(SocialPostType.ANNOUNCEMENT);
        saved.setTitle("Company Update");
        saved.setContent("We are excited to announce our new office!");
        saved.setLikeCount(0);
        saved.setCommentCount(0);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(socialPostRepository.save(any(SocialPost.class))).thenReturn(saved);

        SocialPostResponse response = engagementService.createSocialPost(request);

        assertThat(response).isNotNull();
        assertThat(response.getPostType()).isEqualTo(SocialPostType.ANNOUNCEMENT);
        assertThat(response.getTitle()).isEqualTo("Company Update");
        assertThat(response.getAuthorName()).isEqualTo("Jane Smith");
        assertThat(response.getLikeCount()).isEqualTo(0);
        verify(socialPostRepository, times(1)).save(any(SocialPost.class));
    }

    @Test
    void likeSocialPost_IncrementsLikeCount() {
        SocialPost post = new SocialPost();
        post.setId(1L);
        post.setAuthor(testEmployee);
        post.setPostType(SocialPostType.UPDATE);
        post.setContent("Hello world!");
        post.setLikeCount(5);
        post.setCommentCount(0);

        SocialPost liked = new SocialPost();
        liked.setId(1L);
        liked.setAuthor(testEmployee);
        liked.setPostType(SocialPostType.UPDATE);
        liked.setContent("Hello world!");
        liked.setLikeCount(6);
        liked.setCommentCount(0);

        when(socialPostRepository.findById(1L)).thenReturn(Optional.of(post));
        when(socialPostRepository.save(any(SocialPost.class))).thenReturn(liked);

        SocialPostResponse response = engagementService.likeSocialPost(1L);

        assertThat(response.getLikeCount()).isEqualTo(6);
    }

    @Test
    void deleteSocialPost_NotFound_ThrowsException() {
        when(socialPostRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> engagementService.deleteSocialPost(99L));
    }

    @Test
    void deleteSocialPost_Found_DeletesPost() {
        SocialPost post = new SocialPost();
        post.setId(1L);
        post.setContent("To be deleted");

        when(socialPostRepository.findById(1L)).thenReturn(Optional.of(post));

        engagementService.deleteSocialPost(1L);

        verify(socialPostRepository, times(1)).delete(post);
    }

    // ==================== Entity Business Logic Tests ====================

    @Test
    void survey_IsAcceptingResponses_OnlyWhenActive() {
        Survey survey = new Survey();
        survey.setCreatedBy("admin");
        survey.setSurveyType(SurveyType.PULSE);

        survey.setStatus(SurveyStatus.DRAFT);
        assertThat(survey.isAcceptingResponses()).isFalse();

        survey.setStatus(SurveyStatus.ACTIVE);
        assertThat(survey.isAcceptingResponses()).isTrue();

        survey.setStatus(SurveyStatus.CLOSED);
        assertThat(survey.isAcceptingResponses()).isFalse();
    }

    @Test
    void recognition_GetPointsForBadge_ReturnsCorrectPoints() {
        Recognition recognition = new Recognition();

        recognition.setBadge(RecognitionBadge.STAR_PERFORMER);
        assertThat(recognition.getPointsForBadge()).isEqualTo(25);

        recognition.setBadge(RecognitionBadge.LEADER);
        assertThat(recognition.getPointsForBadge()).isEqualTo(20);

        recognition.setBadge(RecognitionBadge.TEAM_PLAYER);
        assertThat(recognition.getPointsForBadge()).isEqualTo(15);

        recognition.setBadge(RecognitionBadge.HELPER);
        assertThat(recognition.getPointsForBadge()).isEqualTo(10);
    }

    @Test
    void wellnessCheckIn_GetMoodScore_ReturnsCorrectScore() {
        WellnessCheckIn checkIn = new WellnessCheckIn();

        checkIn.setMoodRating(MoodRating.GREAT);
        assertThat(checkIn.getMoodScore()).isEqualTo(5);

        checkIn.setMoodRating(MoodRating.GOOD);
        assertThat(checkIn.getMoodScore()).isEqualTo(4);

        checkIn.setMoodRating(MoodRating.OKAY);
        assertThat(checkIn.getMoodScore()).isEqualTo(3);

        checkIn.setMoodRating(MoodRating.LOW);
        assertThat(checkIn.getMoodScore()).isEqualTo(2);

        checkIn.setMoodRating(MoodRating.STRUGGLING);
        assertThat(checkIn.getMoodScore()).isEqualTo(1);
    }

    @Test
    void socialPost_IncrementLikes_IncrementsCount() {
        SocialPost post = new SocialPost();
        post.setLikeCount(0);

        post.incrementLikes();
        assertThat(post.getLikeCount()).isEqualTo(1);

        post.incrementLikes();
        assertThat(post.getLikeCount()).isEqualTo(2);
    }

    @Test
    void socialPost_IncrementComments_IncrementsCount() {
        SocialPost post = new SocialPost();
        post.setCommentCount(0);

        post.incrementComments();
        assertThat(post.getCommentCount()).isEqualTo(1);
    }
}
