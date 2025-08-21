import { RequisitionData, RequisitionStatus, ApprovalHistoryEntry, WorkflowAction, ApprovalRole } from '../types/workflow';

/**
 * Mock PostgreSQL data store for requisitions
 * In production, this would connect to actual PostgreSQL database
 */
class MockRequisitionStore {
  private requisitions: Map<string, RequisitionData> = new Map();
  private approvalHistory: Map<string, ApprovalHistoryEntry[]> = new Map();

  private generateId(): string {
    return 'req_' + Math.random().toString(36).substring(2, 15);
  }

  async create(data: Omit<RequisitionData, 'id' | 'status' | 'createdAt' | 'updatedAt' | 'approvalHistory'>): Promise<RequisitionData> {
    const id = this.generateId();
    const now = new Date();
    
    const requisition: RequisitionData = {
      ...data,
      id,
      status: RequisitionStatus.DRAFT,
      createdAt: now,
      updatedAt: now,
      approvalHistory: []
    };

    this.requisitions.set(id, requisition);
    this.approvalHistory.set(id, []);
    
    return requisition;
  }

  async findById(id: string): Promise<RequisitionData | null> {
    const requisition = this.requisitions.get(id);
    if (!requisition) return null;

    // Attach approval history
    requisition.approvalHistory = this.approvalHistory.get(id) || [];
    return requisition;
  }

  async findAll(): Promise<RequisitionData[]> {
    const results = Array.from(this.requisitions.values());
    
    // Attach approval history to each
    for (const requisition of results) {
      requisition.approvalHistory = this.approvalHistory.get(requisition.id) || [];
    }
    
    return results;
  }

  async findByStatus(status: RequisitionStatus): Promise<RequisitionData[]> {
    const results = Array.from(this.requisitions.values())
      .filter(req => req.status === status);
    
    for (const requisition of results) {
      requisition.approvalHistory = this.approvalHistory.get(requisition.id) || [];
    }
    
    return results;
  }

  async findPendingForRole(role: string): Promise<RequisitionData[]> {
    let targetStatus: RequisitionStatus;
    
    switch (role) {
      case 'HR':
        targetStatus = RequisitionStatus.SUBMITTED;
        break;
      case 'Hiring Manager':
        targetStatus = RequisitionStatus.PENDING_HIRING_MANAGER_APPROVAL;
        break;
      case 'Admin':
        targetStatus = RequisitionStatus.PENDING_EXECUTIVE_APPROVAL;
        break;
      default:
        return [];
    }

    return this.findByStatus(targetStatus);
  }

  async update(id: string, updates: Partial<RequisitionData>): Promise<RequisitionData | null> {
    const existing = this.requisitions.get(id);
    if (!existing) return null;

    const updated = {
      ...existing,
      ...updates,
      updatedAt: new Date()
    };

    this.requisitions.set(id, updated);
    return updated;
  }

  async updateStatus(id: string, status: RequisitionStatus): Promise<RequisitionData | null> {
    return this.update(id, { status });
  }

  async addApprovalHistoryEntry(entry: ApprovalHistoryEntry): Promise<void> {
    const history = this.approvalHistory.get(entry.requisitionId) || [];
    history.push(entry);
    this.approvalHistory.set(entry.requisitionId, history);
  }

  async delete(id: string): Promise<boolean> {
    const deleted = this.requisitions.delete(id);
    if (deleted) {
      this.approvalHistory.delete(id);
    }
    return deleted;
  }

  // Utility methods for demo data
  async seedDemoData(): Promise<void> {
    // Clear existing data first
    this.requisitions.clear();
    this.approvalHistory.clear();

    const now = new Date();
    const demoRequisitions = [
      {
        id: '1001',
        jobTitle: 'Backend Developer',
        department: 'Engineering',
        location: 'Cape Town, Western Cape',
        employmentType: 'Full-time',
        salaryMin: 1400000,
        salaryMax: 1800000,
        description: 'Join our backend team to build scalable microservices and APIs.',
        createdBy: 'john.doe@company.com',
        status: RequisitionStatus.DRAFT,
        createdAt: new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000), // 7 days ago
        updatedAt: new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000),
        approvalHistory: []
      },
      {
        id: '1002',
        jobTitle: 'Frontend Developer',
        department: 'Engineering',
        location: 'Johannesburg, Gauteng',
        employmentType: 'Full-time',
        salaryMin: 1500000,
        salaryMax: 2000000,
        description: 'Build responsive web applications using React and modern frameworks.',
        createdBy: 'jane.smith@company.com',
        status: RequisitionStatus.SUBMITTED,
        createdAt: new Date(now.getTime() - 5 * 24 * 60 * 60 * 1000), // 5 days ago
        updatedAt: new Date(now.getTime() - 4 * 24 * 60 * 60 * 1000), // 4 days ago
        approvalHistory: []
      },
      {
        id: '1003',
        jobTitle: 'DevOps Engineer',
        department: 'Engineering',
        location: 'Remote',
        employmentType: 'Full-time',
        salaryMin: 1700000,
        salaryMax: 2300000,
        description: 'Manage our cloud infrastructure and CI/CD pipelines.',
        createdBy: 'mike.johnson@company.com',
        status: RequisitionStatus.PENDING_HIRING_MANAGER_APPROVAL,
        createdAt: new Date(now.getTime() - 3 * 24 * 60 * 60 * 1000), // 3 days ago
        updatedAt: new Date(now.getTime() - 2 * 24 * 60 * 60 * 1000), // 2 days ago
        approvalHistory: []
      },
      {
        id: '1004',
        jobTitle: 'Senior Data Scientist',
        department: 'Data Science',
        location: 'Durban, KwaZulu-Natal',
        employmentType: 'Full-time',
        salaryMin: 2300000,
        salaryMax: 2900000,
        description: 'Lead data science initiatives and build ML models for our platform.',
        createdBy: 'sarah.wilson@company.com',
        status: RequisitionStatus.APPROVED,
        createdAt: new Date(now.getTime() - 14 * 24 * 60 * 60 * 1000), // 14 days ago
        updatedAt: new Date(now.getTime() - 1 * 24 * 60 * 60 * 1000), // 1 day ago
        approvalHistory: []
      },
      {
        id: '1005',
        jobTitle: 'Marketing Manager',
        department: 'Marketing',
        location: 'Pretoria, Gauteng',
        employmentType: 'Full-time',
        salaryMin: 1300000,
        salaryMax: 1800000,
        description: 'Drive marketing campaigns and brand awareness initiatives.',
        createdBy: 'david.brown@company.com',
        status: RequisitionStatus.REJECTED,
        createdAt: new Date(now.getTime() - 10 * 24 * 60 * 60 * 1000), // 10 days ago
        updatedAt: new Date(now.getTime() - 6 * 24 * 60 * 60 * 1000), // 6 days ago
        approvalHistory: []
      }
    ];

    // Add requisitions to store
    for (const req of demoRequisitions) {
      this.requisitions.set(req.id, req);
      this.approvalHistory.set(req.id, []);
    }

    // Generate approval history for each requisition based on their status
    await this.generateApprovalHistory();
  }

  private async generateApprovalHistory(): Promise<void> {
    const now = new Date();

    // Requisition 1001 (Draft) - No approval history yet
    // No entries needed

    // Requisition 1002 (Submitted - Pending HR Approval)
    this.approvalHistory.set('1002', [
      {
        id: 'hist_1002_1',
        requisitionId: '1002',
        action: WorkflowAction.SUBMIT,
        fromStatus: RequisitionStatus.DRAFT,
        toStatus: RequisitionStatus.SUBMITTED,
        approverRole: ApprovalRole.HIRING_MANAGER,
        approverId: 'jane.smith@company.com',
        approverName: 'Jane Smith',
        comment: 'Submitting for HR review - urgent position needed for Q1 goals.',
        timestamp: new Date(now.getTime() - 4 * 24 * 60 * 60 * 1000) // 4 days ago
      }
    ]);

    // Requisition 1003 (Pending Hiring Manager Approval)
    this.approvalHistory.set('1003', [
      {
        id: 'hist_1003_1',
        requisitionId: '1003',
        action: WorkflowAction.SUBMIT,
        fromStatus: RequisitionStatus.DRAFT,
        toStatus: RequisitionStatus.SUBMITTED,
        approverRole: ApprovalRole.HIRING_MANAGER,
        approverId: 'mike.johnson@company.com',
        approverName: 'Mike Johnson',
        comment: 'Submitting DevOps position for review.',
        timestamp: new Date(now.getTime() - 3 * 24 * 60 * 60 * 1000) // 3 days ago
      },
      {
        id: 'hist_1003_2',
        requisitionId: '1003',
        action: WorkflowAction.APPROVE,
        fromStatus: RequisitionStatus.SUBMITTED,
        toStatus: RequisitionStatus.PENDING_HIRING_MANAGER_APPROVAL,
        approverRole: ApprovalRole.HR,
        approverId: 'hr.team@company.com',
        approverName: 'Sarah Johnson (HR)',
        comment: 'Job requirements are well-defined. Budget approved for this role.',
        timestamp: new Date(now.getTime() - 2 * 24 * 60 * 60 * 1000) // 2 days ago
      }
    ]);

    // Requisition 1004 (Approved - Complete workflow)
    this.approvalHistory.set('1004', [
      {
        id: 'hist_1004_1',
        requisitionId: '1004',
        action: WorkflowAction.SUBMIT,
        fromStatus: RequisitionStatus.DRAFT,
        toStatus: RequisitionStatus.SUBMITTED,
        approverRole: ApprovalRole.HIRING_MANAGER,
        approverId: 'sarah.wilson@company.com',
        approverName: 'Sarah Wilson',
        comment: 'Critical data science role for our ML platform expansion.',
        timestamp: new Date(now.getTime() - 14 * 24 * 60 * 60 * 1000) // 14 days ago
      },
      {
        id: 'hist_1004_2',
        requisitionId: '1004',
        action: WorkflowAction.APPROVE,
        fromStatus: RequisitionStatus.SUBMITTED,
        toStatus: RequisitionStatus.PENDING_HIRING_MANAGER_APPROVAL,
        approverRole: ApprovalRole.HR,
        approverId: 'hr.team@company.com',
        approverName: 'Sarah Johnson (HR)',
        comment: 'Salary range is competitive for senior data science role. Approved.',
        timestamp: new Date(now.getTime() - 12 * 24 * 60 * 60 * 1000) // 12 days ago
      },
      {
        id: 'hist_1004_3',
        requisitionId: '1004',
        action: WorkflowAction.APPROVE,
        fromStatus: RequisitionStatus.PENDING_HIRING_MANAGER_APPROVAL,
        toStatus: RequisitionStatus.PENDING_EXECUTIVE_APPROVAL,
        approverRole: ApprovalRole.HIRING_MANAGER,
        approverId: 'tech.lead@company.com',
        approverName: 'Michael Chen (Tech Lead)',
        comment: 'Strong technical requirements. This hire will accelerate our ML initiatives.',
        timestamp: new Date(now.getTime() - 8 * 24 * 60 * 60 * 1000) // 8 days ago
      },
      {
        id: 'hist_1004_4',
        requisitionId: '1004',
        action: WorkflowAction.APPROVE,
        fromStatus: RequisitionStatus.PENDING_EXECUTIVE_APPROVAL,
        toStatus: RequisitionStatus.APPROVED,
        approverRole: ApprovalRole.EXECUTIVE,
        approverId: 'cto@company.com',
        approverName: 'Jennifer Davis (CTO)',
        comment: 'Strategic hire approved. High priority for recruiting team.',
        timestamp: new Date(now.getTime() - 1 * 24 * 60 * 60 * 1000) // 1 day ago
      }
    ]);

    // Requisition 1005 (Rejected at Hiring Manager step)
    this.approvalHistory.set('1005', [
      {
        id: 'hist_1005_1',
        requisitionId: '1005',
        action: WorkflowAction.SUBMIT,
        fromStatus: RequisitionStatus.DRAFT,
        toStatus: RequisitionStatus.SUBMITTED,
        approverRole: ApprovalRole.HIRING_MANAGER,
        approverId: 'david.brown@company.com',
        approverName: 'David Brown',
        comment: 'Need marketing support for product launch campaign.',
        timestamp: new Date(now.getTime() - 10 * 24 * 60 * 60 * 1000) // 10 days ago
      },
      {
        id: 'hist_1005_2',
        requisitionId: '1005',
        action: WorkflowAction.APPROVE,
        fromStatus: RequisitionStatus.SUBMITTED,
        toStatus: RequisitionStatus.PENDING_HIRING_MANAGER_APPROVAL,
        approverRole: ApprovalRole.HR,
        approverId: 'hr.team@company.com',
        approverName: 'Sarah Johnson (HR)',
        comment: 'Role definition is clear. Moving to hiring manager approval.',
        timestamp: new Date(now.getTime() - 8 * 24 * 60 * 60 * 1000) // 8 days ago
      },
      {
        id: 'hist_1005_3',
        requisitionId: '1005',
        action: WorkflowAction.REJECT,
        fromStatus: RequisitionStatus.PENDING_HIRING_MANAGER_APPROVAL,
        toStatus: RequisitionStatus.REJECTED,
        approverRole: ApprovalRole.HIRING_MANAGER,
        approverId: 'marketing.director@company.com',
        approverName: 'Lisa Garcia (Marketing Director)',
        comment: 'Budget constraints for Q1. Consider consolidating with existing team or resubmit for Q2.',
        timestamp: new Date(now.getTime() - 6 * 24 * 60 * 60 * 1000) // 6 days ago
      }
    ]);
  }
}

// Singleton instance
export const requisitionStore = new MockRequisitionStore();

/**
 * Requisition Service
 * Handles business logic for requisition management
 */
export class RequisitionService {
  private store = requisitionStore;

  async createRequisition(
    requisitionData: {
      jobTitle: string;
      department: string;
      location: string;
      employmentType: string;
      salaryMin?: number;
      salaryMax?: number;
      description: string;
    },
    createdBy: string
  ): Promise<RequisitionData> {
    return this.store.create({
      ...requisitionData,
      createdBy
    });
  }

  async getRequisition(id: string): Promise<RequisitionData | null> {
    return this.store.findById(id);
  }

  async getAllRequisitions(): Promise<RequisitionData[]> {
    return this.store.findAll();
  }

  async getRequisitionsByStatus(status: RequisitionStatus): Promise<RequisitionData[]> {
    return this.store.findByStatus(status);
  }

  async getPendingRequisitionsForRole(role: string): Promise<RequisitionData[]> {
    return this.store.findPendingForRole(role);
  }

  async updateRequisition(
    id: string,
    updates: Partial<RequisitionData>
  ): Promise<RequisitionData | null> {
    return this.store.update(id, updates);
  }

  async deleteRequisition(id: string): Promise<boolean> {
    return this.store.delete(id);
  }

  // Initialize demo data
  async initializeDemoData(): Promise<void> {
    await this.store.seedDemoData();
    
    // Also initialize audit logs for demo data
    const { approvalTimelineService } = await import('./approvalTimelineService');
    await approvalTimelineService.initializeAuditLogsForDemoData();
  }
}

// Export singleton instance
export const requisitionService = new RequisitionService();