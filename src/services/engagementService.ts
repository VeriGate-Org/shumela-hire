import { apiFetch } from '@/lib/api-fetch';
import type {
  Survey,
  SurveyAnswerRequest,
  Recognition,
  WellnessProgram,
  WellnessCheckIn,
  SocialPost,
  EngagementAnalytics,
  SurveyStatus,
} from '@/types/engagement';

export class EngagementService {
  private baseUrl = '/api/engagement';

  // ==================== Surveys ====================

  async createSurvey(data: {
    title: string;
    description?: string;
    surveyType: string;
    isAnonymous?: boolean;
    questions?: Array<{
      questionText: string;
      questionType?: string;
      displayOrder?: number;
      isRequired?: boolean;
      options?: string;
    }>;
  }): Promise<Survey> {
    const response = await apiFetch(`${this.baseUrl}/surveys`, {
      method: 'POST',
      body: JSON.stringify(data),
    });
    if (!response.ok) throw new Error('Failed to create survey');
    return response.json();
  }

  async getSurveys(status?: SurveyStatus): Promise<Survey[]> {
    const params = status ? `?status=${status}` : '';
    const response = await apiFetch(`${this.baseUrl}/surveys${params}`);
    if (!response.ok) return [];
    return response.json();
  }

  async getSurvey(id: number): Promise<Survey | null> {
    const response = await apiFetch(`${this.baseUrl}/surveys/${id}`);
    if (!response.ok) return null;
    return response.json();
  }

  async activateSurvey(id: number): Promise<Survey> {
    const response = await apiFetch(`${this.baseUrl}/surveys/${id}/activate`, {
      method: 'POST',
    });
    if (!response.ok) throw new Error('Failed to activate survey');
    return response.json();
  }

  async closeSurvey(id: number): Promise<Survey> {
    const response = await apiFetch(`${this.baseUrl}/surveys/${id}/close`, {
      method: 'POST',
    });
    if (!response.ok) throw new Error('Failed to close survey');
    return response.json();
  }

  async submitSurveyResponse(data: SurveyAnswerRequest): Promise<void> {
    const response = await apiFetch(`${this.baseUrl}/surveys/respond`, {
      method: 'POST',
      body: JSON.stringify(data),
    });
    if (!response.ok) throw new Error('Failed to submit survey response');
  }

  // ==================== Recognition ====================

  async createRecognition(data: {
    giverId: number;
    receiverId: number;
    badge: string;
    message: string;
    points?: number;
    isPublic?: boolean;
  }): Promise<Recognition> {
    const response = await apiFetch(`${this.baseUrl}/recognitions`, {
      method: 'POST',
      body: JSON.stringify(data),
    });
    if (!response.ok) throw new Error('Failed to create recognition');
    return response.json();
  }

  async getRecognitionFeed(page = 0, size = 20): Promise<{ content: Recognition[]; totalElements: number }> {
    const response = await apiFetch(`${this.baseUrl}/recognitions/feed?page=${page}&size=${size}`);
    if (!response.ok) return { content: [], totalElements: 0 };
    return response.json();
  }

  async getRecognitionsReceived(employeeId: number): Promise<Recognition[]> {
    const response = await apiFetch(`${this.baseUrl}/recognitions/received/${employeeId}`);
    if (!response.ok) return [];
    return response.json();
  }

  async getRecognitionsGiven(employeeId: number): Promise<Recognition[]> {
    const response = await apiFetch(`${this.baseUrl}/recognitions/given/${employeeId}`);
    if (!response.ok) return [];
    return response.json();
  }

  async getTotalPoints(employeeId: number): Promise<{ employeeId: number; totalPoints: number }> {
    const response = await apiFetch(`${this.baseUrl}/recognitions/points/${employeeId}`);
    if (!response.ok) return { employeeId, totalPoints: 0 };
    return response.json();
  }

  // ==================== Wellness Programs ====================

  async createWellnessProgram(data: {
    name: string;
    description?: string;
    category: string;
    startDate?: string;
    endDate?: string;
    maxParticipants?: number;
  }): Promise<WellnessProgram> {
    const response = await apiFetch(`${this.baseUrl}/wellness/programs`, {
      method: 'POST',
      body: JSON.stringify(data),
    });
    if (!response.ok) throw new Error('Failed to create wellness program');
    return response.json();
  }

  async getWellnessPrograms(activeOnly = false): Promise<WellnessProgram[]> {
    const response = await apiFetch(`${this.baseUrl}/wellness/programs?activeOnly=${activeOnly}`);
    if (!response.ok) return [];
    return response.json();
  }

  async getWellnessProgram(id: number): Promise<WellnessProgram | null> {
    const response = await apiFetch(`${this.baseUrl}/wellness/programs/${id}`);
    if (!response.ok) return null;
    return response.json();
  }

  async deleteWellnessProgram(id: number): Promise<void> {
    const response = await apiFetch(`${this.baseUrl}/wellness/programs/${id}`, {
      method: 'DELETE',
    });
    if (!response.ok) throw new Error('Failed to delete wellness program');
  }

  // ==================== Wellness Check-Ins ====================

  async createWellnessCheckIn(data: {
    employeeId: number;
    moodRating: string;
    energyLevel?: number;
    stressLevel?: number;
    notes?: string;
    wellnessProgramId?: number;
    checkInDate?: string;
  }): Promise<WellnessCheckIn> {
    const response = await apiFetch(`${this.baseUrl}/wellness/check-ins`, {
      method: 'POST',
      body: JSON.stringify(data),
    });
    if (!response.ok) throw new Error('Failed to create wellness check-in');
    return response.json();
  }

  async getCheckInsForEmployee(employeeId: number): Promise<WellnessCheckIn[]> {
    const response = await apiFetch(`${this.baseUrl}/wellness/check-ins/${employeeId}`);
    if (!response.ok) return [];
    return response.json();
  }

  // ==================== Social Posts ====================

  async createSocialPost(data: {
    authorId: number;
    postType?: string;
    title?: string;
    content: string;
    isPinned?: boolean;
  }): Promise<SocialPost> {
    const response = await apiFetch(`${this.baseUrl}/social/posts`, {
      method: 'POST',
      body: JSON.stringify(data),
    });
    if (!response.ok) throw new Error('Failed to create social post');
    return response.json();
  }

  async getSocialFeed(page = 0, size = 20): Promise<{ content: SocialPost[]; totalElements: number }> {
    const response = await apiFetch(`${this.baseUrl}/social/feed?page=${page}&size=${size}`);
    if (!response.ok) return { content: [], totalElements: 0 };
    return response.json();
  }

  async getSocialPost(id: number): Promise<SocialPost | null> {
    const response = await apiFetch(`${this.baseUrl}/social/posts/${id}`);
    if (!response.ok) return null;
    return response.json();
  }

  async likeSocialPost(id: number): Promise<SocialPost> {
    const response = await apiFetch(`${this.baseUrl}/social/posts/${id}/like`, {
      method: 'POST',
    });
    if (!response.ok) throw new Error('Failed to like post');
    return response.json();
  }

  async deleteSocialPost(id: number): Promise<void> {
    const response = await apiFetch(`${this.baseUrl}/social/posts/${id}`, {
      method: 'DELETE',
    });
    if (!response.ok) throw new Error('Failed to delete social post');
  }

  // ==================== Analytics ====================

  async getEngagementAnalytics(): Promise<EngagementAnalytics | null> {
    const response = await apiFetch(`${this.baseUrl}/analytics`);
    if (!response.ok) return null;
    return response.json();
  }
}

export const engagementService = new EngagementService();
