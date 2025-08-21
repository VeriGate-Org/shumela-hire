import { 
  JobAd, 
  JobAdStatus, 
  PublishingChannel, 
  PublishingRequest, 
  PublishingHistoryEntry,
  JobAdFilters,
  JobAdStats,
  JobApplication,
  generateSlug,
  isJobAdExpired,
  isJobAdActive,
  DEFAULT_PUBLISHING_SETTINGS
} from '../types/jobAd';
import { JobAdDraft } from '../types/jobTemplate';
import { auditLogService } from './auditLogService';

/**
 * Mock data store for published job ads
 * In production, this would connect to actual PostgreSQL database
 */
class MockJobAdStore {
  private jobAds: Map<string, JobAd> = new Map();
  private publishingHistory: Map<string, PublishingHistoryEntry[]> = new Map();
  private applications: Map<string, JobApplication[]> = new Map();

  private generateId(): string {
    return 'job_' + Math.random().toString(36).substring(2, 15);
  }

  private generateHistoryId(): string {
    return 'hist_' + Math.random().toString(36).substring(2, 15);
  }

  async createJobAd(
    draft: JobAdDraft, 
    publishingRequest: PublishingRequest,
    publishedBy: string
  ): Promise<JobAd> {
    const id = this.generateId();
    const now = new Date();
    
    // Generate slug
    const slug = publishingRequest.customSlug || generateSlug(draft.title, id);
    
    const jobAd: JobAd = {
      id,
      draftId: draft.id,
      templateId: draft.templateId,
      requisitionId: draft.requisitionId,
      title: draft.title,
      intro: draft.intro,
      responsibilities: draft.responsibilities,
      requirements: draft.requirements,
      benefits: draft.benefits,
      location: draft.location,
      employmentType: draft.employmentType,
      salaryRangeMin: draft.salaryRangeMin,
      salaryRangeMax: draft.salaryRangeMax,
      contactEmail: draft.contactEmail,
      
      // Publishing fields
      status: JobAdStatus.PUBLISHED,
      channels: publishingRequest.channels,
      publishedAt: now,
      expiresAt: publishingRequest.expiresAt,
      slug,
      
      // SEO and display
      companyName: publishingRequest.companyName,
      department: publishingRequest.department,
      featured: publishingRequest.featured || false,
      
      // Metadata
      createdBy: draft.createdBy,
      createdAt: now,
      updatedAt: now,
      publishedBy,
      
      // Analytics
      viewCount: 0,
      applicationCount: 0,
      
      // Audit trail
      publishingHistory: []
    };

    this.jobAds.set(id, jobAd);
    this.publishingHistory.set(id, []);
    this.applications.set(id, []);

    // Add publishing history entry
    await this.addPublishingHistoryEntry({
      id: this.generateHistoryId(),
      jobAdId: id,
      action: 'PUBLISHED',
      channels: publishingRequest.channels,
      performedBy: publishedBy,
      performedAt: now
    });

    return jobAd;
  }

  async findById(id: string): Promise<JobAd | null> {
    const jobAd = this.jobAds.get(id);
    if (!jobAd) return null;

    // Auto-expire if needed
    if (isJobAdExpired(jobAd) && jobAd.status === JobAdStatus.PUBLISHED) {
      await this.expireJobAd(id);
      return this.jobAds.get(id) || null;
    }

    return jobAd;
  }

  async findBySlug(slug: string): Promise<JobAd | null> {
    const jobAd = Array.from(this.jobAds.values()).find(ad => ad.slug === slug);
    if (!jobAd) return null;

    // Auto-expire if needed
    if (isJobAdExpired(jobAd) && jobAd.status === JobAdStatus.PUBLISHED) {
      await this.expireJobAd(jobAd.id);
      return this.jobAds.get(jobAd.id) || null;
    }

    return jobAd;
  }

  async findAll(filters?: JobAdFilters): Promise<JobAd[]> {
    let jobAds = Array.from(this.jobAds.values());

    // Auto-expire jobs if needed
    for (const jobAd of jobAds) {
      if (isJobAdExpired(jobAd) && jobAd.status === JobAdStatus.PUBLISHED) {
        await this.expireJobAd(jobAd.id);
      }
    }

    // Refresh data after expiry
    jobAds = Array.from(this.jobAds.values());

    // Apply filters
    if (filters) {
      if (filters.status) {
        jobAds = jobAds.filter(ad => ad.status === filters.status);
      }
      
      if (filters.channels && filters.channels.length > 0) {
        jobAds = jobAds.filter(ad => 
          filters.channels!.some(channel => ad.channels.includes(channel))
        );
      }
      
      if (filters.location) {
        jobAds = jobAds.filter(ad => 
          ad.location.toLowerCase().includes(filters.location!.toLowerCase())
        );
      }
      
      if (filters.employmentType) {
        jobAds = jobAds.filter(ad => ad.employmentType === filters.employmentType);
      }
      
      if (filters.department) {
        jobAds = jobAds.filter(ad => 
          ad.department?.toLowerCase().includes(filters.department!.toLowerCase())
        );
      }
      
      if (filters.featured !== undefined) {
        jobAds = jobAds.filter(ad => ad.featured === filters.featured);
      }
      
      if (filters.search) {
        const searchLower = filters.search.toLowerCase();
        jobAds = jobAds.filter(ad => 
          ad.title.toLowerCase().includes(searchLower) ||
          ad.intro.toLowerCase().includes(searchLower) ||
          ad.companyName.toLowerCase().includes(searchLower)
        );
      }
      
      if (filters.expiresAfter) {
        jobAds = jobAds.filter(ad => new Date(ad.expiresAt) > filters.expiresAfter!);
      }
      
      if (filters.expiresBefore) {
        jobAds = jobAds.filter(ad => new Date(ad.expiresAt) < filters.expiresBefore!);
      }
    }

    return jobAds.sort((a, b) => b.createdAt.getTime() - a.createdAt.getTime());
  }

  async findPublished(channel?: PublishingChannel): Promise<JobAd[]> {
    const filters: JobAdFilters = { status: JobAdStatus.PUBLISHED };
    if (channel) {
      filters.channels = [channel];
    }
    return this.findAll(filters);
  }

  async update(id: string, updates: Partial<JobAd>): Promise<JobAd | null> {
    const existing = this.jobAds.get(id);
    if (!existing) return null;

    const updated = {
      ...existing,
      ...updates,
      id, // Ensure ID doesn't change
      updatedAt: new Date()
    };

    this.jobAds.set(id, updated);
    return updated;
  }

  async unpublishJobAd(id: string, performedBy: string, reason?: string): Promise<JobAd | null> {
    const jobAd = await this.update(id, { 
      status: JobAdStatus.UNPUBLISHED,
      publishedAt: undefined 
    });
    
    if (jobAd) {
      await this.addPublishingHistoryEntry({
        id: this.generateHistoryId(),
        jobAdId: id,
        action: 'UNPUBLISHED',
        performedBy,
        performedAt: new Date(),
        reason
      });
    }
    
    return jobAd;
  }

  async republishJobAd(
    id: string, 
    channels: PublishingChannel[], 
    expiresAt: Date,
    performedBy: string
  ): Promise<JobAd | null> {
    const jobAd = await this.update(id, { 
      status: JobAdStatus.PUBLISHED,
      channels,
      publishedAt: new Date(),
      expiresAt 
    });
    
    if (jobAd) {
      await this.addPublishingHistoryEntry({
        id: this.generateHistoryId(),
        jobAdId: id,
        action: 'PUBLISHED',
        channels,
        performedBy,
        performedAt: new Date()
      });
    }
    
    return jobAd;
  }

  async expireJobAd(id: string): Promise<JobAd | null> {
    const jobAd = await this.update(id, { status: JobAdStatus.EXPIRED });
    
    if (jobAd) {
      await this.addPublishingHistoryEntry({
        id: this.generateHistoryId(),
        jobAdId: id,
        action: 'EXPIRED',
        performedBy: 'system',
        performedAt: new Date()
      });
    }
    
    return jobAd;
  }

  async incrementViewCount(id: string): Promise<void> {
    const jobAd = this.jobAds.get(id);
    if (jobAd) {
      jobAd.viewCount++;
      this.jobAds.set(id, jobAd);
    }
  }

  async incrementApplicationCount(id: string): Promise<void> {
    const jobAd = this.jobAds.get(id);
    if (jobAd) {
      jobAd.applicationCount++;
      this.jobAds.set(id, jobAd);
    }
  }

  async addPublishingHistoryEntry(entry: PublishingHistoryEntry): Promise<void> {
    const history = this.publishingHistory.get(entry.jobAdId) || [];
    history.push(entry);
    this.publishingHistory.set(entry.jobAdId, history);
    
    // Also update the job ad's history
    const jobAd = this.jobAds.get(entry.jobAdId);
    if (jobAd) {
      jobAd.publishingHistory = history;
      this.jobAds.set(entry.jobAdId, jobAd);
    }
  }

  async getPublishingHistory(jobAdId: string): Promise<PublishingHistoryEntry[]> {
    return this.publishingHistory.get(jobAdId) || [];
  }

  async getStats(): Promise<JobAdStats> {
    const allAds = Array.from(this.jobAds.values());
    const publishedAds = allAds.filter(ad => ad.status === JobAdStatus.PUBLISHED);
    const expiredAds = allAds.filter(ad => ad.status === JobAdStatus.EXPIRED);
    const internalAds = allAds.filter(ad => ad.channels.includes(PublishingChannel.INTERNAL));
    const externalAds = allAds.filter(ad => ad.channels.includes(PublishingChannel.EXTERNAL));
    const featuredAds = allAds.filter(ad => ad.featured);
    
    const totalViews = allAds.reduce((sum, ad) => sum + ad.viewCount, 0);
    const totalApplications = allAds.reduce((sum, ad) => sum + ad.applicationCount, 0);
    
    return {
      totalAds: allAds.length,
      publishedAds: publishedAds.length,
      expiredAds: expiredAds.length,
      totalViews,
      totalApplications,
      internalAds: internalAds.length,
      externalAds: externalAds.length,
      featuredAds: featuredAds.length,
      averageViewsPerAd: allAds.length > 0 ? totalViews / allAds.length : 0,
      conversionRate: totalViews > 0 ? totalApplications / totalViews : 0
    };
  }

  async delete(id: string): Promise<boolean> {
    const deleted = this.jobAds.delete(id);
    if (deleted) {
      this.publishingHistory.delete(id);
      this.applications.delete(id);
    }
    return deleted;
  }

  // Initialize with demo data
  async seedDemoData(): Promise<void> {
    this.jobAds.clear();
    this.publishingHistory.clear();
    this.applications.clear();

    const now = new Date();
    
    // Create some demo job ads
    const demoAds: Omit<JobAd, 'id' | 'publishingHistory'>[] = [
      {
        draftId: 'draft_001',
        templateId: 'tpl_001',
        requisitionId: '1004',
        title: 'Senior Software Engineer',
        intro: '<p>Join our innovative engineering team and help build the next generation of software solutions.</p>',
        responsibilities: '<h3>Key Responsibilities:</h3><ul><li>Design and develop scalable software solutions</li><li>Collaborate with cross-functional teams</li><li>Mentor junior developers</li></ul>',
        requirements: '<h3>Requirements:</h3><ul><li>5+ years of software development experience</li><li>Proficiency in modern programming languages</li><li>Strong problem-solving skills</li></ul>',
        benefits: '<h3>Benefits:</h3><ul><li>Competitive salary and equity</li><li>Comprehensive health coverage</li><li>Flexible work arrangements</li></ul>',
        location: 'San Francisco, CA',
        employmentType: 'Full-time',
        salaryRangeMin: 120000,
        salaryRangeMax: 180000,
        contactEmail: 'careers@company.com',
        status: JobAdStatus.PUBLISHED,
        channels: [PublishingChannel.INTERNAL, PublishingChannel.EXTERNAL],
        publishedAt: new Date(now.getTime() - 5 * 24 * 60 * 60 * 1000),
        expiresAt: new Date(now.getTime() + 25 * 24 * 60 * 60 * 1000),
        slug: 'senior-software-engineer-sf',
        companyName: 'TechCorp Inc.',
        department: 'Engineering',
        featured: true,
        createdBy: 'hr@company.com',
        createdAt: new Date(now.getTime() - 5 * 24 * 60 * 60 * 1000),
        updatedAt: new Date(now.getTime() - 5 * 24 * 60 * 60 * 1000),
        publishedBy: 'hr@company.com',
        viewCount: 145,
        applicationCount: 12
      },
      {
        draftId: 'draft_002',
        title: 'Product Manager',
        intro: '<p>Lead product strategy and drive innovation in our fast-growing company.</p>',
        responsibilities: '<h3>What You\'ll Do:</h3><ul><li>Define product roadmap and strategy</li><li>Work with engineering and design teams</li><li>Analyze market trends and user feedback</li></ul>',
        requirements: '<h3>What We\'re Looking For:</h3><ul><li>3+ years of product management experience</li><li>Strong analytical and communication skills</li><li>Experience with agile methodologies</li></ul>',
        benefits: '<h3>Why Join Us:</h3><ul><li>Competitive compensation package</li><li>Stock options</li><li>Professional development budget</li></ul>',
        location: 'Remote',
        employmentType: 'Full-time',
        salaryRangeMin: 100000,
        salaryRangeMax: 140000,
        contactEmail: 'careers@company.com',
        status: JobAdStatus.PUBLISHED,
        channels: [PublishingChannel.EXTERNAL],
        publishedAt: new Date(now.getTime() - 3 * 24 * 60 * 60 * 1000),
        expiresAt: new Date(now.getTime() + 27 * 24 * 60 * 60 * 1000),
        slug: 'product-manager-remote',
        companyName: 'TechCorp Inc.',
        department: 'Product',
        featured: false,
        createdBy: 'hr@company.com',
        createdAt: new Date(now.getTime() - 3 * 24 * 60 * 60 * 1000),
        updatedAt: new Date(now.getTime() - 3 * 24 * 60 * 60 * 1000),
        publishedBy: 'hr@company.com',
        viewCount: 89,
        applicationCount: 7
      },
      {
        draftId: 'draft_003',
        title: 'Marketing Specialist',
        intro: '<p>Drive marketing campaigns and help grow our brand presence.</p>',
        responsibilities: '<h3>Responsibilities:</h3><ul><li>Create and execute marketing campaigns</li><li>Manage social media presence</li><li>Analyze campaign performance</li></ul>',
        requirements: '<h3>Requirements:</h3><ul><li>2+ years of marketing experience</li><li>Experience with digital marketing tools</li><li>Creative thinking and analytical skills</li></ul>',
        benefits: '<h3>Benefits:</h3><ul><li>Health and dental insurance</li><li>Flexible PTO</li><li>Professional development opportunities</li></ul>',
        location: 'New York, NY',
        employmentType: 'Full-time',
        salaryRangeMin: 60000,
        salaryRangeMax: 80000,
        contactEmail: 'careers@company.com',
        status: JobAdStatus.EXPIRED,
        channels: [PublishingChannel.INTERNAL, PublishingChannel.EXTERNAL],
        publishedAt: new Date(now.getTime() - 35 * 24 * 60 * 60 * 1000),
        expiresAt: new Date(now.getTime() - 5 * 24 * 60 * 60 * 1000),
        slug: 'marketing-specialist-ny',
        companyName: 'TechCorp Inc.',
        department: 'Marketing',
        featured: false,
        createdBy: 'hr@company.com',
        createdAt: new Date(now.getTime() - 35 * 24 * 60 * 60 * 1000),
        updatedAt: new Date(now.getTime() - 5 * 24 * 60 * 60 * 1000),
        publishedBy: 'hr@company.com',
        viewCount: 203,
        applicationCount: 25
      }
    ];

    // Create job ads with IDs
    for (const adData of demoAds) {
      const id = this.generateId();
      const jobAd: JobAd = {
        ...adData,
        id,
        publishingHistory: []
      };
      
      this.jobAds.set(id, jobAd);
      this.publishingHistory.set(id, []);
      this.applications.set(id, []);
    }
  }
}

// Singleton instance
const jobAdStore = new MockJobAdStore();

/**
 * Job Ad Service
 * Handles business logic for job ad publishing and management
 */
export class JobAdService {
  private store = jobAdStore;

  async publishJobAd(
    draft: JobAdDraft,
    publishingRequest: PublishingRequest,
    publishedBy: string
  ): Promise<JobAd> {
    // Validate expiry date
    const now = new Date();
    const maxExpiry = new Date(now.getTime() + DEFAULT_PUBLISHING_SETTINGS.maxExpiryDays * 24 * 60 * 60 * 1000);
    
    if (publishingRequest.expiresAt <= now) {
      throw new Error('Expiry date must be in the future');
    }
    
    if (publishingRequest.expiresAt > maxExpiry) {
      throw new Error(`Expiry date cannot be more than ${DEFAULT_PUBLISHING_SETTINGS.maxExpiryDays} days in the future`);
    }

    // Create and publish the job ad
    const jobAd = await this.store.createJobAd(draft, publishingRequest, publishedBy);

    // Log to audit trail
    await auditLogService.logWorkflowTransition(
      jobAd.id,
      'DRAFT',
      'PUBLISHED',
      publishedBy,
      'HR',
      `Published to channels: ${publishingRequest.channels.join(', ')}`
    );

    return jobAd;
  }

  async getJobAd(id: string): Promise<JobAd | null> {
    return this.store.findById(id);
  }

  async getJobAdBySlug(slug: string): Promise<JobAd | null> {
    return this.store.findBySlug(slug);
  }

  async getAllJobAds(filters?: JobAdFilters): Promise<JobAd[]> {
    return this.store.findAll(filters);
  }

  async getPublishedJobAds(channel?: PublishingChannel): Promise<JobAd[]> {
    return this.store.findPublished(channel);
  }

  async updateJobAd(id: string, updates: Partial<JobAd>): Promise<JobAd | null> {
    return this.store.update(id, updates);
  }

  async unpublishJobAd(id: string, performedBy: string, reason?: string): Promise<JobAd | null> {
    const result = await this.store.unpublishJobAd(id, performedBy, reason);
    
    if (result) {
      await auditLogService.logWorkflowTransition(
        id,
        'PUBLISHED',
        'UNPUBLISHED',
        performedBy,
        'HR',
        reason || 'Job ad unpublished'
      );
    }
    
    return result;
  }

  async republishJobAd(
    id: string,
    channels: PublishingChannel[],
    expiresAt: Date,
    performedBy: string
  ): Promise<JobAd | null> {
    const result = await this.store.republishJobAd(id, channels, expiresAt, performedBy);
    
    if (result) {
      await auditLogService.logWorkflowTransition(
        id,
        'UNPUBLISHED',
        'PUBLISHED',
        performedBy,
        'HR',
        `Republished to channels: ${channels.join(', ')}`
      );
    }
    
    return result;
  }

  async deleteJobAd(id: string): Promise<boolean> {
    return this.store.delete(id);
  }

  async recordView(id: string): Promise<void> {
    await this.store.incrementViewCount(id);
  }

  async recordApplication(id: string): Promise<void> {
    await this.store.incrementApplicationCount(id);
  }

  async getPublishingHistory(jobAdId: string): Promise<PublishingHistoryEntry[]> {
    return this.store.getPublishingHistory(jobAdId);
  }

  async getStats(): Promise<JobAdStats> {
    return this.store.getStats();
  }

  async checkAndExpireJobAds(): Promise<void> {
    const publishedAds = await this.getPublishedJobAds();
    const now = new Date();
    
    for (const ad of publishedAds) {
      if (new Date(ad.expiresAt) <= now) {
        await this.store.expireJobAd(ad.id);
      }
    }
  }

  generateSlug(title: string, id?: string): string {
    return generateSlug(title, id);
  }

  validateSlug(slug: string): boolean {
    return /^[a-z0-9-]+$/.test(slug) && slug.length >= 3 && slug.length <= 100;
  }

  async isSlugAvailable(slug: string, excludeId?: string): Promise<boolean> {
    const allAds = await this.getAllJobAds();
    return !allAds.some(ad => ad.slug === slug && ad.id !== excludeId);
  }

  // Initialize demo data
  async initializeDemoData(): Promise<void> {
    await this.store.seedDemoData();
  }
}

// Export singleton instance
export const jobAdService = new JobAdService();