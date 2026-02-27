// Employee Engagement Types — STORY-013

export enum SurveyType {
  PULSE = 'PULSE',
  ENGAGEMENT = 'ENGAGEMENT',
  SATISFACTION = 'SATISFACTION',
  CUSTOM = 'CUSTOM',
}

export enum SurveyStatus {
  DRAFT = 'DRAFT',
  ACTIVE = 'ACTIVE',
  CLOSED = 'CLOSED',
  ARCHIVED = 'ARCHIVED',
}

export enum QuestionType {
  RATING = 'RATING',
  TEXT = 'TEXT',
  MULTIPLE_CHOICE = 'MULTIPLE_CHOICE',
  YES_NO = 'YES_NO',
  SCALE = 'SCALE',
}

export enum RecognitionBadge {
  TEAM_PLAYER = 'TEAM_PLAYER',
  INNOVATOR = 'INNOVATOR',
  LEADER = 'LEADER',
  HELPER = 'HELPER',
  STAR_PERFORMER = 'STAR_PERFORMER',
  PROBLEM_SOLVER = 'PROBLEM_SOLVER',
  MENTOR = 'MENTOR',
  CULTURE_CHAMPION = 'CULTURE_CHAMPION',
  CUSTOMER_HERO = 'CUSTOMER_HERO',
  EXTRA_MILE = 'EXTRA_MILE',
}

export enum MoodRating {
  GREAT = 'GREAT',
  GOOD = 'GOOD',
  OKAY = 'OKAY',
  LOW = 'LOW',
  STRUGGLING = 'STRUGGLING',
}

export enum WellnessCategory {
  PHYSICAL = 'PHYSICAL',
  MENTAL = 'MENTAL',
  FINANCIAL = 'FINANCIAL',
  SOCIAL = 'SOCIAL',
  NUTRITIONAL = 'NUTRITIONAL',
}

export enum SocialPostType {
  UPDATE = 'UPDATE',
  ANNOUNCEMENT = 'ANNOUNCEMENT',
  ACHIEVEMENT = 'ACHIEVEMENT',
  EVENT = 'EVENT',
  POLL = 'POLL',
}

// Survey interfaces
export interface SurveyQuestion {
  id: number;
  surveyId: number;
  questionText: string;
  questionType: QuestionType;
  displayOrder: number;
  isRequired: boolean;
  options?: string;
}

export interface Survey {
  id: number;
  title: string;
  description?: string;
  surveyType: SurveyType;
  status: SurveyStatus;
  createdBy: string;
  startDate?: string;
  endDate?: string;
  isAnonymous: boolean;
  questions?: SurveyQuestion[];
  createdAt: string;
  updatedAt: string;
}

export interface SurveyAnswerItem {
  questionId: number;
  ratingValue?: number;
  textValue?: string;
  selectedOption?: string;
}

export interface SurveyAnswerRequest {
  surveyId: number;
  anonymousToken?: string;
  respondentId?: number;
  answers: SurveyAnswerItem[];
}

// Recognition interfaces
export interface Recognition {
  id: number;
  giverId: number;
  giverName: string;
  receiverId: number;
  receiverName: string;
  badge: RecognitionBadge;
  message: string;
  points: number;
  isPublic: boolean;
  createdAt: string;
}

// Wellness interfaces
export interface WellnessProgram {
  id: number;
  name: string;
  description?: string;
  category: WellnessCategory;
  startDate?: string;
  endDate?: string;
  isActive: boolean;
  maxParticipants?: number;
  createdAt: string;
  updatedAt: string;
}

export interface WellnessCheckIn {
  id: number;
  employeeId: number;
  employeeName: string;
  moodRating: MoodRating;
  moodScore: number;
  energyLevel?: number;
  stressLevel?: number;
  notes?: string;
  wellnessProgramId?: number;
  wellnessProgramName?: string;
  checkInDate: string;
  createdAt: string;
}

// Social post interfaces
export interface SocialPost {
  id: number;
  authorId: number;
  authorName: string;
  postType: SocialPostType;
  title?: string;
  content: string;
  isPinned: boolean;
  likeCount: number;
  commentCount: number;
  createdAt: string;
  updatedAt: string;
}

// Analytics
export interface EngagementAnalytics {
  activeSurveys: number;
  totalSurveyResponses: number;
  averageSurveyRating: number;
  totalRecognitions: number;
  totalRecognitionPoints: number;
  wellnessCheckIns: number;
  averageEnergyLevel: number;
  averageStressLevel: number;
  moodDistribution: Record<string, number>;
  badgeDistribution: Record<string, number>;
  totalSocialPosts: number;
}

// Utility functions
export function getSurveyStatusColor(status: SurveyStatus): string {
  switch (status) {
    case SurveyStatus.DRAFT: return 'bg-gray-100 text-gray-800';
    case SurveyStatus.ACTIVE: return 'bg-green-100 text-green-800';
    case SurveyStatus.CLOSED: return 'bg-red-100 text-red-800';
    case SurveyStatus.ARCHIVED: return 'bg-yellow-100 text-yellow-800';
    default: return 'bg-gray-100 text-gray-800';
  }
}

export function getBadgeDisplayName(badge: RecognitionBadge): string {
  switch (badge) {
    case RecognitionBadge.TEAM_PLAYER: return 'Team Player';
    case RecognitionBadge.INNOVATOR: return 'Innovator';
    case RecognitionBadge.LEADER: return 'Leader';
    case RecognitionBadge.HELPER: return 'Helper';
    case RecognitionBadge.STAR_PERFORMER: return 'Star Performer';
    case RecognitionBadge.PROBLEM_SOLVER: return 'Problem Solver';
    case RecognitionBadge.MENTOR: return 'Mentor';
    case RecognitionBadge.CULTURE_CHAMPION: return 'Culture Champion';
    case RecognitionBadge.CUSTOMER_HERO: return 'Customer Hero';
    case RecognitionBadge.EXTRA_MILE: return 'Extra Mile';
    default: return badge;
  }
}

export function getMoodEmoji(mood: MoodRating): string {
  switch (mood) {
    case MoodRating.GREAT: return '😊';
    case MoodRating.GOOD: return '🙂';
    case MoodRating.OKAY: return '😐';
    case MoodRating.LOW: return '😔';
    case MoodRating.STRUGGLING: return '😢';
    default: return '😐';
  }
}

export function getMoodColor(mood: MoodRating): string {
  switch (mood) {
    case MoodRating.GREAT: return 'bg-green-100 text-green-800';
    case MoodRating.GOOD: return 'bg-blue-100 text-blue-800';
    case MoodRating.OKAY: return 'bg-yellow-100 text-yellow-800';
    case MoodRating.LOW: return 'bg-orange-100 text-orange-800';
    case MoodRating.STRUGGLING: return 'bg-red-100 text-red-800';
    default: return 'bg-gray-100 text-gray-800';
  }
}
