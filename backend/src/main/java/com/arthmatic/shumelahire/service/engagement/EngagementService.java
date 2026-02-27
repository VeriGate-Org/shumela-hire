package com.arthmatic.shumelahire.service.engagement;

import com.arthmatic.shumelahire.dto.engagement.*;
import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.engagement.*;
import com.arthmatic.shumelahire.repository.EmployeeRepository;
import com.arthmatic.shumelahire.repository.engagement.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class EngagementService {

    private static final Logger logger = LoggerFactory.getLogger(EngagementService.class);

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private SurveyQuestionRepository surveyQuestionRepository;

    @Autowired
    private SurveyResponseRepository surveyResponseRepository;

    @Autowired
    private RecognitionRepository recognitionRepository;

    @Autowired
    private WellnessProgramRepository wellnessProgramRepository;

    @Autowired
    private WellnessCheckInRepository wellnessCheckInRepository;

    @Autowired
    private SocialPostRepository socialPostRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    // ==================== Surveys ====================

    public SurveyResponseDTO createSurvey(SurveyRequest request, String createdBy) {
        logger.info("Creating survey: {}", request.getTitle());

        Survey survey = new Survey();
        survey.setTitle(request.getTitle());
        survey.setDescription(request.getDescription());
        survey.setSurveyType(request.getSurveyType());
        survey.setCreatedBy(createdBy);
        survey.setStartDate(request.getStartDate());
        survey.setEndDate(request.getEndDate());
        survey.setIsAnonymous(request.getIsAnonymous() != null ? request.getIsAnonymous() : true);

        Survey saved = surveyRepository.save(survey);

        if (request.getQuestions() != null) {
            for (int i = 0; i < request.getQuestions().size(); i++) {
                SurveyQuestionRequest qr = request.getQuestions().get(i);
                SurveyQuestion question = new SurveyQuestion();
                question.setSurvey(saved);
                question.setQuestionText(qr.getQuestionText());
                question.setQuestionType(qr.getQuestionType() != null ? qr.getQuestionType() : QuestionType.RATING);
                question.setDisplayOrder(qr.getDisplayOrder() != null ? qr.getDisplayOrder() : i);
                question.setIsRequired(qr.getIsRequired() != null ? qr.getIsRequired() : true);
                question.setOptions(qr.getOptions());
                surveyQuestionRepository.save(question);
            }
        }

        logger.info("Survey created: {} (id={})", saved.getTitle(), saved.getId());
        return SurveyResponseDTO.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public SurveyResponseDTO getSurvey(Long id) {
        Survey survey = findSurveyById(id);
        return SurveyResponseDTO.fromEntityWithQuestions(survey);
    }

    @Transactional(readOnly = true)
    public List<SurveyResponseDTO> getAllSurveys() {
        return surveyRepository.findAll().stream()
                .map(SurveyResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SurveyResponseDTO> getSurveysByStatus(SurveyStatus status) {
        return surveyRepository.findByStatus(status).stream()
                .map(SurveyResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public SurveyResponseDTO activateSurvey(Long id) {
        Survey survey = findSurveyById(id);
        survey.activate();
        Survey saved = surveyRepository.save(survey);
        logger.info("Survey activated: {} (id={})", saved.getTitle(), saved.getId());
        return SurveyResponseDTO.fromEntity(saved);
    }

    public SurveyResponseDTO closeSurvey(Long id) {
        Survey survey = findSurveyById(id);
        survey.close();
        Survey saved = surveyRepository.save(survey);
        logger.info("Survey closed: {} (id={})", saved.getTitle(), saved.getId());
        return SurveyResponseDTO.fromEntity(saved);
    }

    public void submitSurveyResponses(SurveyAnswerRequest request) {
        Survey survey = findSurveyById(request.getSurveyId());

        if (!survey.isAcceptingResponses()) {
            throw new IllegalStateException("Survey is not currently accepting responses");
        }

        String anonymousToken = request.getAnonymousToken();
        if (survey.getIsAnonymous() && anonymousToken == null) {
            anonymousToken = UUID.randomUUID().toString();
        }

        if (anonymousToken != null && surveyResponseRepository.existsBySurveyIdAndAnonymousToken(survey.getId(), anonymousToken)) {
            throw new IllegalStateException("Response already submitted for this survey");
        }

        Employee respondent = null;
        if (!survey.getIsAnonymous() && request.getRespondentId() != null) {
            respondent = findEmployeeById(request.getRespondentId());
        }

        if (request.getAnswers() != null) {
            for (SurveyAnswerRequest.AnswerItem answer : request.getAnswers()) {
                SurveyQuestion question = surveyQuestionRepository.findById(answer.getQuestionId())
                        .orElseThrow(() -> new IllegalArgumentException("Question not found: " + answer.getQuestionId()));

                SurveyResponse response = new SurveyResponse();
                response.setSurvey(survey);
                response.setQuestion(question);
                response.setRespondent(respondent);
                response.setAnonymousToken(anonymousToken);
                response.setRatingValue(answer.getRatingValue());
                response.setTextValue(answer.getTextValue());
                response.setSelectedOption(answer.getSelectedOption());
                surveyResponseRepository.save(response);
            }
        }

        logger.info("Survey responses submitted for survey id={}", survey.getId());
    }

    // ==================== Recognition ====================

    public RecognitionResponse createRecognition(RecognitionRequest request) {
        logger.info("Creating recognition from employee {} to {}", request.getGiverId(), request.getReceiverId());

        if (request.getGiverId().equals(request.getReceiverId())) {
            throw new IllegalArgumentException("Cannot give recognition to yourself");
        }

        Employee giver = findEmployeeById(request.getGiverId());
        Employee receiver = findEmployeeById(request.getReceiverId());

        Recognition recognition = new Recognition();
        recognition.setGiver(giver);
        recognition.setReceiver(receiver);
        recognition.setBadge(request.getBadge());
        recognition.setMessage(request.getMessage());
        recognition.setIsPublic(request.getIsPublic() != null ? request.getIsPublic() : true);

        // Calculate points based on badge if not explicitly set
        if (request.getPoints() != null) {
            recognition.setPoints(request.getPoints());
        } else {
            recognition.setPoints(recognition.getPointsForBadge());
        }

        Recognition saved = recognitionRepository.save(recognition);
        logger.info("Recognition created: id={}, badge={}, points={}", saved.getId(), saved.getBadge(), saved.getPoints());
        return RecognitionResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<RecognitionResponse> getRecognitionsForEmployee(Long employeeId) {
        return recognitionRepository.findByReceiverId(employeeId).stream()
                .map(RecognitionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecognitionResponse> getRecognitionsGivenByEmployee(Long employeeId) {
        return recognitionRepository.findByGiverId(employeeId).stream()
                .map(RecognitionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<RecognitionResponse> getPublicRecognitions(String tenantId, Pageable pageable) {
        return recognitionRepository.findPublicByTenantId(tenantId, pageable)
                .map(RecognitionResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public Long getTotalPointsForEmployee(Long employeeId) {
        Long points = recognitionRepository.getTotalPointsByReceiverId(employeeId);
        return points != null ? points : 0L;
    }

    // ==================== Wellness Programs ====================

    public WellnessProgramResponse createWellnessProgram(WellnessProgramRequest request) {
        logger.info("Creating wellness program: {}", request.getName());

        WellnessProgram program = new WellnessProgram();
        program.setName(request.getName());
        program.setDescription(request.getDescription());
        program.setCategory(request.getCategory());
        program.setStartDate(request.getStartDate());
        program.setEndDate(request.getEndDate());
        program.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        program.setMaxParticipants(request.getMaxParticipants());

        WellnessProgram saved = wellnessProgramRepository.save(program);
        logger.info("Wellness program created: {} (id={})", saved.getName(), saved.getId());
        return WellnessProgramResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<WellnessProgramResponse> getAllWellnessPrograms() {
        return wellnessProgramRepository.findAll().stream()
                .map(WellnessProgramResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<WellnessProgramResponse> getActiveWellnessPrograms(String tenantId) {
        return wellnessProgramRepository.findByTenantIdAndIsActive(tenantId, true).stream()
                .map(WellnessProgramResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WellnessProgramResponse getWellnessProgram(Long id) {
        WellnessProgram program = wellnessProgramRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Wellness program not found"));
        return WellnessProgramResponse.fromEntity(program);
    }

    public void deleteWellnessProgram(Long id) {
        WellnessProgram program = wellnessProgramRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Wellness program not found"));
        program.setIsActive(false);
        wellnessProgramRepository.save(program);
        logger.info("Wellness program deactivated: id={}", id);
    }

    // ==================== Wellness Check-Ins ====================

    public WellnessCheckInResponse createWellnessCheckIn(WellnessCheckInRequest request) {
        logger.info("Creating wellness check-in for employee {}", request.getEmployeeId());

        Employee employee = findEmployeeById(request.getEmployeeId());

        WellnessCheckIn checkIn = new WellnessCheckIn();
        checkIn.setEmployee(employee);
        checkIn.setMoodRating(request.getMoodRating());
        checkIn.setEnergyLevel(request.getEnergyLevel());
        checkIn.setStressLevel(request.getStressLevel());
        checkIn.setNotes(request.getNotes());
        checkIn.setCheckInDate(request.getCheckInDate() != null ? request.getCheckInDate() : LocalDate.now());

        if (request.getWellnessProgramId() != null) {
            WellnessProgram program = wellnessProgramRepository.findById(request.getWellnessProgramId())
                    .orElseThrow(() -> new IllegalArgumentException("Wellness program not found"));
            checkIn.setWellnessProgram(program);
        }

        WellnessCheckIn saved = wellnessCheckInRepository.save(checkIn);
        logger.info("Wellness check-in created: id={}, mood={}", saved.getId(), saved.getMoodRating());
        return WellnessCheckInResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<WellnessCheckInResponse> getCheckInsForEmployee(Long employeeId) {
        return wellnessCheckInRepository.findByEmployeeIdOrderByCheckInDateDesc(employeeId).stream()
                .map(WellnessCheckInResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<WellnessCheckInResponse> getCheckInsForEmployeeDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return wellnessCheckInRepository.findByEmployeeIdAndDateRange(employeeId, startDate, endDate).stream()
                .map(WellnessCheckInResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // ==================== Social Posts ====================

    public SocialPostResponse createSocialPost(SocialPostRequest request) {
        logger.info("Creating social post by employee {}", request.getAuthorId());

        Employee author = findEmployeeById(request.getAuthorId());

        SocialPost post = new SocialPost();
        post.setAuthor(author);
        post.setPostType(request.getPostType() != null ? request.getPostType() : SocialPostType.UPDATE);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setIsPinned(request.getIsPinned() != null ? request.getIsPinned() : false);

        SocialPost saved = socialPostRepository.save(post);
        logger.info("Social post created: id={}, type={}", saved.getId(), saved.getPostType());
        return SocialPostResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public Page<SocialPostResponse> getSocialFeed(String tenantId, Pageable pageable) {
        return socialPostRepository.findByTenantId(tenantId, pageable)
                .map(SocialPostResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public SocialPostResponse getSocialPost(Long id) {
        SocialPost post = socialPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Social post not found"));
        return SocialPostResponse.fromEntity(post);
    }

    public SocialPostResponse likeSocialPost(Long id) {
        SocialPost post = socialPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Social post not found"));
        post.incrementLikes();
        SocialPost saved = socialPostRepository.save(post);
        return SocialPostResponse.fromEntity(saved);
    }

    public void deleteSocialPost(Long id) {
        SocialPost post = socialPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Social post not found"));
        socialPostRepository.delete(post);
        logger.info("Social post deleted: id={}", id);
    }

    // ==================== Engagement Analytics ====================

    @Transactional(readOnly = true)
    public EngagementAnalyticsResponse getEngagementAnalytics(String tenantId) {
        EngagementAnalyticsResponse analytics = new EngagementAnalyticsResponse();
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        LocalDateTime thirtyDaysAgoDateTime = thirtyDaysAgo.atStartOfDay();

        // Surveys
        analytics.setActiveSurveys(surveyRepository.countByTenantIdAndStatus(tenantId, SurveyStatus.ACTIVE));

        // Recognition
        analytics.setTotalRecognitions(recognitionRepository.countByTenantIdSince(tenantId, thirtyDaysAgoDateTime));

        // Wellness
        analytics.setWellnessCheckIns(wellnessCheckInRepository.countByTenantIdSince(tenantId, thirtyDaysAgo));
        analytics.setAverageEnergyLevel(wellnessCheckInRepository.getAverageEnergyLevel(tenantId, thirtyDaysAgo));
        analytics.setAverageStressLevel(wellnessCheckInRepository.getAverageStressLevel(tenantId, thirtyDaysAgo));

        // Mood distribution
        List<Object[]> moodData = wellnessCheckInRepository.getMoodDistribution(tenantId, thirtyDaysAgo);
        Map<String, Long> moodDistribution = new LinkedHashMap<>();
        for (Object[] row : moodData) {
            moodDistribution.put(((MoodRating) row[0]).name(), (Long) row[1]);
        }
        analytics.setMoodDistribution(moodDistribution);

        // Social
        analytics.setTotalSocialPosts(socialPostRepository.countByTenantId(tenantId));

        return analytics;
    }

    // ==================== Helpers ====================

    private Survey findSurveyById(Long id) {
        return surveyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found"));
    }

    private Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + id));
    }
}
