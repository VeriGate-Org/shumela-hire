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

/**
 * Mock data store for job templates
 * In production, this would connect to actual database
 */
class MockJobTemplateStore {
  private templates: Map<string, JobAdTemplate> = new Map();
  private drafts: Map<string, JobAdDraft> = new Map();

  private generateId(): string {
    return 'tpl_' + Math.random().toString(36).substring(2, 15);
  }

  private generateDraftId(): string {
    return 'draft_' + Math.random().toString(36).substring(2, 15);
  }

  async createTemplate(data: Omit<JobAdTemplate, 'id' | 'createdAt' | 'updatedAt' | 'usageCount'>): Promise<JobAdTemplate> {
    const id = this.generateId();
    const now = new Date();
    
    const template: JobAdTemplate = {
      ...data,
      id,
      createdAt: now,
      updatedAt: now,
      usageCount: 0
    };

    this.templates.set(id, template);
    return template;
  }

  async findById(id: string): Promise<JobAdTemplate | null> {
    return this.templates.get(id) || null;
  }

  async findAll(filters?: TemplateFilters): Promise<JobAdTemplate[]> {
    let templates = Array.from(this.templates.values());

    // Apply filters
    if (filters) {
      if (filters.search) {
        const searchLower = filters.search.toLowerCase();
        templates = templates.filter(t => 
          t.name.toLowerCase().includes(searchLower) ||
          t.title.toLowerCase().includes(searchLower) ||
          t.description?.toLowerCase().includes(searchLower)
        );
      }

      if (filters.employmentType) {
        templates = templates.filter(t => 
          t.employmentType.toLowerCase().includes(filters.employmentType!.toLowerCase())
        );
      }

      if (filters.location) {
        templates = templates.filter(t => 
          t.location.toLowerCase().includes(filters.location!.toLowerCase())
        );
      }

      if (filters.createdBy) {
        templates = templates.filter(t => t.createdBy === filters.createdBy);
      }

      if (!filters.showArchived) {
        templates = templates.filter(t => !t.isArchived);
      }
    } else {
      // Default: don't show archived
      templates = templates.filter(t => !t.isArchived);
    }

    return templates.sort((a, b) => b.updatedAt.getTime() - a.updatedAt.getTime());
  }

  async update(id: string, updates: Partial<JobAdTemplate>): Promise<JobAdTemplate | null> {
    const existing = this.templates.get(id);
    if (!existing) return null;

    const updated = {
      ...existing,
      ...updates,
      id, // Ensure ID doesn't change
      updatedAt: new Date()
    };

    this.templates.set(id, updated);
    return updated;
  }

  async delete(id: string): Promise<boolean> {
    return this.templates.delete(id);
  }

  async archive(id: string): Promise<JobAdTemplate | null> {
    return this.update(id, { isArchived: true });
  }

  async unarchive(id: string): Promise<JobAdTemplate | null> {
    return this.update(id, { isArchived: false });
  }

  async duplicate(id: string, newName: string): Promise<JobAdTemplate | null> {
    const original = await this.findById(id);
    if (!original) return null;

    return this.createTemplate({
      ...original,
      name: newName,
      description: `Copy of ${original.name}`,
      isArchived: false,
      createdBy: original.createdBy
    });
  }

  async incrementUsage(id: string): Promise<void> {
    const template = this.templates.get(id);
    if (template) {
      template.usageCount++;
      template.updatedAt = new Date();
      this.templates.set(id, template);
    }
  }

  async getStats(): Promise<TemplateStats> {
    const templates = Array.from(this.templates.values());
    const activeTemplates = templates.filter(t => !t.isArchived);
    const archivedTemplates = templates.filter(t => t.isArchived);
    
    const mostUsedTemplate = templates.reduce((prev, current) => 
      (prev.usageCount > current.usageCount) ? prev : current
    );

    const recentlyCreated = templates
      .sort((a, b) => b.createdAt.getTime() - a.createdAt.getTime())
      .slice(0, 5);

    return {
      totalTemplates: templates.length,
      activeTemplates: activeTemplates.length,
      archivedTemplates: archivedTemplates.length,
      mostUsedTemplate: templates.length > 0 ? mostUsedTemplate : undefined,
      recentlyCreated
    };
  }

  // Draft operations
  async createDraft(data: Omit<JobAdDraft, 'id' | 'createdAt' | 'updatedAt'>): Promise<JobAdDraft> {
    const id = this.generateDraftId();
    const now = new Date();
    
    const draft: JobAdDraft = {
      ...data,
      id,
      createdAt: now,
      updatedAt: now
    };

    this.drafts.set(id, draft);
    return draft;
  }

  async findDraftById(id: string): Promise<JobAdDraft | null> {
    return this.drafts.get(id) || null;
  }

  async findDraftsByTemplate(templateId: string): Promise<JobAdDraft[]> {
    return Array.from(this.drafts.values())
      .filter(d => d.templateId === templateId)
      .sort((a, b) => b.createdAt.getTime() - a.createdAt.getTime());
  }

  // Initialize with demo data
  async seedDemoData(): Promise<void> {
    this.templates.clear();
    this.drafts.clear();

    const now = new Date();
    const demoTemplates: Omit<JobAdTemplate, 'id' | 'createdAt' | 'updatedAt' | 'usageCount'>[] = [
      {
        name: 'Software Engineer Template',
        description: 'Standard template for software engineering positions',
        title: '{{jobTitle}} - {{department}}',
        intro: `<p>Join our innovative engineering team at <strong>{{companyName}}</strong> as a <strong>{{jobTitle}}</strong>. We're building cutting-edge solutions that impact millions of users worldwide.</p>`,
        responsibilities: `<h3>What You'll Do:</h3>
<ul>
<li>Design and develop scalable software solutions</li>
<li>Collaborate with product managers and designers</li>
<li>Write clean, maintainable, and well-tested code</li>
<li>Participate in code reviews and architectural discussions</li>
<li>Mentor junior developers and contribute to team growth</li>
</ul>`,
        requirements: `<h3>What We're Looking For:</h3>
<ul>
<li>Bachelor's degree in Computer Science or equivalent experience</li>
<li>3+ years of experience in software development</li>
<li>Proficiency in modern programming languages (Python, JavaScript, Go, etc.)</li>
<li>Experience with cloud platforms (AWS, GCP, Azure)</li>
<li>Strong problem-solving and communication skills</li>
</ul>`,
        benefits: `<h3>What We Offer:</h3>
<ul>
<li>Competitive salary: {{salaryRange}}</li>
<li>Equity package with high growth potential</li>
<li>Comprehensive health, dental, and vision coverage</li>
<li>R50,000 annual learning and development budget</li>
<li>Flexible work arrangements and unlimited PTO</li>
<li>Top-tier equipment and modern office spaces</li>
</ul>`,
        location: '{{location}}',
        employmentType: '{{employmentType}}',
        contactEmail: '{{contactEmail}}',
        isArchived: false,
        createdBy: 'hr.team@company.com'
      },
      {
        name: 'Marketing Manager Template',
        description: 'Template for marketing leadership roles',
        title: '{{jobTitle}} - Drive Growth at {{companyName}}',
        intro: `<p>Are you passionate about building brands and driving growth? Join <strong>{{companyName}}</strong> as a <strong>{{jobTitle}}</strong> and lead our marketing initiatives to new heights.</p>`,
        responsibilities: `<h3>Your Mission:</h3>
<ul>
<li>Develop and execute comprehensive marketing strategies</li>
<li>Lead cross-functional campaigns from concept to launch</li>
<li>Analyze market trends and competitive landscape</li>
<li>Manage marketing budget and optimize ROI</li>
<li>Build and mentor a high-performing marketing team</li>
</ul>`,
        requirements: `<h3>What You Bring:</h3>
<ul>
<li>MBA or Bachelor's in Marketing, Business, or related field</li>
<li>5+ years of marketing experience with leadership responsibilities</li>
<li>Proven track record of driving growth and engagement</li>
<li>Experience with digital marketing tools and analytics</li>
<li>Excellent communication and project management skills</li>
</ul>`,
        benefits: `<h3>Why You'll Love It Here:</h3>
<ul>
<li>Competitive compensation: {{salaryRange}}</li>
<li>Performance-based bonuses and equity</li>
<li>Premium healthcare and wellness programs</li>
<li>Professional development and conference attendance</li>
<li>Creative freedom and entrepreneurial environment</li>
<li>Work-life balance with flexible schedules</li>
</ul>`,
        location: '{{location}}',
        employmentType: '{{employmentType}}',
        contactEmail: '{{contactEmail}}',
        isArchived: false,
        createdBy: 'hr.team@company.com'
      },
      {
        name: 'Data Scientist Template',
        description: 'Template for data science and analytics roles',
        title: 'Data Scientist - {{department}} | {{companyName}}',
        intro: `<p>Transform data into insights that drive business decisions. Join our data science team at <strong>{{companyName}}</strong> and help us unlock the power of our data.</p>`,
        responsibilities: `<h3>Key Responsibilities:</h3>
<ul>
<li>Build predictive models and machine learning algorithms</li>
<li>Analyze large datasets to identify trends and patterns</li>
<li>Create data visualizations and dashboards</li>
<li>Collaborate with engineering teams to deploy models</li>
<li>Present findings to stakeholders and leadership</li>
</ul>`,
        requirements: `<h3>Required Qualifications:</h3>
<ul>
<li>PhD or Master's in Data Science, Statistics, or related field</li>
<li>3+ years of hands-on data science experience</li>
<li>Proficiency in Python/R and SQL</li>
<li>Experience with ML frameworks (TensorFlow, PyTorch, scikit-learn)</li>
<li>Strong statistical analysis and experimental design skills</li>
</ul>`,
        benefits: `<h3>Perks & Benefits:</h3>
<ul>
<li>Highly competitive salary: {{salaryRange}}</li>
<li>Significant equity upside</li>
<li>World-class health and dental coverage</li>
<li>Access to cutting-edge tools and technologies</li>
<li>Conference speaking and research publication opportunities</li>
<li>Collaborative and intellectually stimulating environment</li>
</ul>`,
        location: '{{location}}',
        employmentType: '{{employmentType}}',
        contactEmail: '{{contactEmail}}',
        isArchived: false,
        createdBy: 'hr.team@company.com'
      }
    ];

    // Create demo templates
    for (const templateData of demoTemplates) {
      const template = await this.createTemplate(templateData);
      // Simulate some usage
      template.usageCount = Math.floor(Math.random() * 10) + 1;
      this.templates.set(template.id, template);
    }
  }
}

// Singleton instance
const templateStore = new MockJobTemplateStore();

/**
 * Job Template Service
 * Handles business logic for job ad template management
 */
export class JobTemplateService {
  private store = templateStore;

  // Template CRUD operations
  async createTemplate(
    templateData: Omit<JobAdTemplate, 'id' | 'createdAt' | 'updatedAt' | 'usageCount'>,
    createdBy: string
  ): Promise<JobAdTemplate> {
    return this.store.createTemplate({
      ...templateData,
      createdBy
    });
  }

  async getTemplate(id: string): Promise<JobAdTemplate | null> {
    return this.store.findById(id);
  }

  async getAllTemplates(filters?: TemplateFilters): Promise<JobAdTemplate[]> {
    return this.store.findAll(filters);
  }

  async updateTemplate(
    id: string,
    updates: Partial<JobAdTemplate>
  ): Promise<JobAdTemplate | null> {
    return this.store.update(id, updates);
  }

  async deleteTemplate(id: string): Promise<boolean> {
    return this.store.delete(id);
  }

  async archiveTemplate(id: string): Promise<JobAdTemplate | null> {
    return this.store.archive(id);
  }

  async unarchiveTemplate(id: string): Promise<JobAdTemplate | null> {
    return this.store.unarchive(id);
  }

  async duplicateTemplate(id: string, newName: string): Promise<JobAdTemplate | null> {
    return this.store.duplicate(id, newName);
  }

  async getTemplateStats(): Promise<TemplateStats> {
    return this.store.getStats();
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
    const template = await this.getTemplate(request.templateId);
    if (!template) {
      throw new Error('Template not found');
    }

    // Increment usage count
    await this.store.incrementUsage(request.templateId);

    // Prepare replacement data
    const replacementData: Record<string, string> = {
      companyName: 'Your Company', // This would come from app config
      ...request.customData
    };

    if (requisitionData) {
      replacementData.jobTitle = requisitionData.jobTitle;
      replacementData.department = requisitionData.department;
      replacementData.location = requisitionData.location;
      replacementData.employmentType = requisitionData.employmentType;
      replacementData.contactEmail = requisitionData.createdBy;
      
      if (requisitionData.salaryMin && requisitionData.salaryMax) {
        replacementData.salaryRange = `R${requisitionData.salaryMin.toLocaleString()} - R${requisitionData.salaryMax.toLocaleString()}`;
      } else if (requisitionData.salaryMin) {
        replacementData.salaryRange = `R${requisitionData.salaryMin.toLocaleString()}+`;
      }
    }

    // Replace placeholders in all content fields
    const draft: Omit<JobAdDraft, 'id' | 'createdAt' | 'updatedAt'> = {
      templateId: template.id,
      requisitionId: request.requisitionId,
      title: this.replacePlaceholders(template.title, replacementData),
      intro: this.replacePlaceholders(template.intro, replacementData),
      responsibilities: this.replacePlaceholders(template.responsibilities, replacementData),
      requirements: this.replacePlaceholders(template.requirements, replacementData),
      benefits: this.replacePlaceholders(template.benefits, replacementData),
      location: this.replacePlaceholders(template.location, replacementData),
      employmentType: this.replacePlaceholders(template.employmentType, replacementData),
      salaryRangeMin: template.salaryRangeMin,
      salaryRangeMax: template.salaryRangeMax,
      closingDate: template.closingDate,
      contactEmail: this.replacePlaceholders(template.contactEmail, replacementData),
      status: 'draft',
      createdBy: requisitionData?.createdBy || 'unknown'
    };

    return this.store.createDraft(draft);
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

  // Initialize demo data
  async initializeDemoData(): Promise<void> {
    await this.store.seedDemoData();
  }
}

// Export singleton instance
export const jobTemplateService = new JobTemplateService();