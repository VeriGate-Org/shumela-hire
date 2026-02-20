import {
  JobAdTemplate,
  JobAdDraft,
  GenerateAdRequest,
  TemplateFilters,
  TemplateStats,
  RequisitionData,
  DEFAULT_TEMPLATE_CONTENT,
  TEMPLATE_PLACEHOLDERS
} from '../types/jobTemplate';
import { apiFetch } from '@/lib/api-fetch';

/**
 * Job Template Service
 * Handles business logic for job ad template management
 */
export class JobTemplateService {
  // Template CRUD operations
  async createTemplate(
    templateData: Omit<JobAdTemplate, 'id' | 'createdAt' | 'updatedAt' | 'usageCount'>,
    createdBy: string
  ): Promise<JobAdTemplate> {
    const response = await apiFetch('/api/job-templates', {
      method: 'POST',
      body: JSON.stringify({ ...templateData, createdBy }),
    });
    if (!response.ok) throw new Error('Failed to create template');
    const result = await response.json();
    return result.data || result;
  }

  async getTemplate(id: string): Promise<JobAdTemplate | null> {
    const response = await apiFetch(`/api/job-templates/${id}`);
    if (!response.ok) return null;
    const result = await response.json();
    return result.data || result;
  }

  async getAllTemplates(filters?: TemplateFilters): Promise<JobAdTemplate[]> {
    const params = new URLSearchParams();
    if (filters?.search) params.set('search', filters.search);
    if (filters?.employmentType) params.set('employmentType', filters.employmentType);
    if (filters?.location) params.set('location', filters.location);
    if (filters?.createdBy) params.set('createdBy', filters.createdBy);
    if (filters?.showArchived) params.set('showArchived', 'true');

    const query = params.toString();
    const response = await apiFetch(`/api/job-templates${query ? `?${query}` : ''}`);
    if (!response.ok) return [];
    const result = await response.json();
    return result.data || result || [];
  }

  async updateTemplate(
    id: string,
    updates: Partial<JobAdTemplate>
  ): Promise<JobAdTemplate | null> {
    const response = await apiFetch(`/api/job-templates/${id}`, {
      method: 'PUT',
      body: JSON.stringify(updates),
    });
    if (!response.ok) return null;
    const result = await response.json();
    return result.data || result;
  }

  async deleteTemplate(id: string): Promise<boolean> {
    const response = await apiFetch(`/api/job-templates/${id}`, { method: 'DELETE' });
    return response.ok;
  }

  async archiveTemplate(id: string): Promise<JobAdTemplate | null> {
    return this.updateTemplate(id, { isArchived: true });
  }

  async unarchiveTemplate(id: string): Promise<JobAdTemplate | null> {
    return this.updateTemplate(id, { isArchived: false });
  }

  async duplicateTemplate(id: string, newName: string): Promise<JobAdTemplate | null> {
    const response = await apiFetch(`/api/job-templates/${id}/duplicate`, {
      method: 'POST',
      body: JSON.stringify({ name: newName }),
    });
    if (!response.ok) return null;
    const result = await response.json();
    return result.data || result;
  }

  async getTemplateStats(): Promise<TemplateStats> {
    const response = await apiFetch('/api/job-templates/stats');
    if (!response.ok) {
      return {
        totalTemplates: 0,
        activeTemplates: 0,
        archivedTemplates: 0,
        recentlyCreated: [],
      };
    }
    const result = await response.json();
    return result.data || result;
  }

  // Placeholder replacement logic
  replacePlaceholders(content: string, data: Record<string, string>): string {
    let result = content;

    for (const [key, value] of Object.entries(data)) {
      const placeholder = key.startsWith('{{') ? key : `{{${key}}}`;
      const regex = new RegExp(placeholder.replace(/[{}]/g, '\\$&'), 'g');
      result = result.replace(regex, value || '');
    }

    return result;
  }

  // Generate job ad draft from template
  async generateJobAdDraft(request: GenerateAdRequest, requisitionData?: RequisitionData): Promise<JobAdDraft> {
    const response = await apiFetch('/api/job-templates/generate-draft', {
      method: 'POST',
      body: JSON.stringify({ request, requisitionData }),
    });
    if (!response.ok) throw new Error('Failed to generate draft');
    const result = await response.json();
    return result.data || result;
  }

  // Get available placeholders
  getAvailablePlaceholders() {
    return TEMPLATE_PLACEHOLDERS;
  }

  // Create template with default content
  async createDefaultTemplate(
    name: string,
    createdBy: string,
    customizations?: Partial<JobAdTemplate>
  ): Promise<JobAdTemplate> {
    const templateData: Omit<JobAdTemplate, 'id' | 'createdAt' | 'updatedAt' | 'usageCount'> = {
      name,
      description: 'New job ad template',
      ...DEFAULT_TEMPLATE_CONTENT,
      contactEmail: createdBy,
      isArchived: false,
      createdBy,
      ...customizations
    };

    return this.createTemplate(templateData, createdBy);
  }
}

// Export singleton instance
export const jobTemplateService = new JobTemplateService();
