import { ApprovalStep } from '../components/ApprovalTimeline';
import { ApprovalHistoryEntry, RequisitionStatus, WorkflowAction, ApprovalRole } from '../types/workflow';
import { requisitionService } from './requisitionService';
import { auditLogService } from './auditLogService';

/**
 * Service to convert requisition approval history into ApprovalTimeline data
 */
export class ApprovalTimelineService {
  
  /**
   * Convert approval history entries into ApprovalTimeline steps
   */
  async getApprovalTimelineForRequisition(requisitionId: string): Promise<ApprovalStep[]> {
    const requisition = await requisitionService.getRequisition(requisitionId);
    if (!requisition) {
      return [];
    }

    const steps: ApprovalStep[] = [];
    const approvalHistory = requisition.approvalHistory || [];

    // Create a map of completed steps
    const completedSteps = new Map<string, ApprovalHistoryEntry>();
    for (const entry of approvalHistory) {
      if (entry.action === WorkflowAction.APPROVE) {
        completedSteps.set(entry.approverRole, entry);
      } else if (entry.action === WorkflowAction.REJECT) {
        completedSteps.set(entry.approverRole, entry);
      }
    }

    // Generate timeline based on workflow definition
    const workflowSteps = this.getWorkflowSteps();

    for (const workflowStep of workflowSteps) {
      const historyEntry = completedSteps.get(workflowStep.role);
      
      if (historyEntry) {
        // Step has been completed
        const status = historyEntry.action === WorkflowAction.APPROVE ? 'approved' : 'rejected';
        steps.push({
          role: workflowStep.role,
          approverName: historyEntry.approverName,
          status: status as 'approved' | 'rejected',
          timestamp: historyEntry.timestamp.toISOString(),
          comment: historyEntry.comment
        });
      } else if (workflowStep.role === this.getCurrentPendingRole(requisition.status)) {
        // This is the current pending step
        steps.push({
          role: workflowStep.role,
          approverName: workflowStep.defaultApprover,
          status: 'pending',
          timestamp: undefined,
          comment: undefined
        });
      } else if (this.isStepInFuture(workflowStep.role, requisition.status)) {
        // Future step - show as pending with reduced opacity
        steps.push({
          role: workflowStep.role,
          approverName: workflowStep.defaultApprover,
          status: 'pending',
          timestamp: undefined,
          comment: undefined
        });
      }
    }

    return steps;
  }

  /**
   * Get demo timeline data for display purposes
   */
  getDemoApprovalTimeline(): ApprovalStep[] {
    return [
      {
        role: 'HR',
        approverName: 'Sarah Johnson',
        status: 'approved',
        timestamp: '2024-01-15T10:30:00Z',
        comment: 'Job requirements are well-defined and align with company standards.'
      },
      {
        role: 'Hiring Manager',
        approverName: 'Michael Chen',
        status: 'approved',
        timestamp: '2024-01-16T14:20:00Z',
        comment: 'Team needs this role urgently. Budget approved.'
      },
      {
        role: 'Executive',
        approverName: 'Jennifer Davis',
        status: 'pending',
        timestamp: undefined,
        comment: undefined
      }
    ];
  }

  /**
   * Create audit log entries for demo data initialization
   */
  async initializeAuditLogsForDemoData(): Promise<void> {
    const requisitions = await requisitionService.getAllRequisitions();
    
    for (const requisition of requisitions) {
      // Create audit entries based on approval history
      if (requisition.approvalHistory) {
        for (const historyEntry of requisition.approvalHistory) {
          await auditLogService.logWorkflowTransition(
            requisition.id,
            historyEntry.fromStatus,
            historyEntry.toStatus,
            historyEntry.approverId,
            historyEntry.approverRole,
            historyEntry.comment || '',
            historyEntry.timestamp
          );
        }
      }

      // Log requisition creation
      await auditLogService.logRequisitionCreated(
        requisition.id,
        requisition.createdBy,
        'Creator', // Default role for creation
        requisition.createdAt
      );
    }
  }

  /**
   * Get workflow steps for complete workflow view
   */
  private getWorkflowSteps(): Array<{role: string, defaultApprover: string}> {
    return [
      { role: ApprovalRole.HR, defaultApprover: 'HR Team' },
      { role: ApprovalRole.HIRING_MANAGER, defaultApprover: 'Hiring Manager' },
      { role: ApprovalRole.EXECUTIVE, defaultApprover: 'Executive Team' }
    ];
  }

  /**
   * Get the current pending role based on requisition status
   */
  private getCurrentPendingRole(status: RequisitionStatus): string | null {
    switch (status) {
      case RequisitionStatus.SUBMITTED:
        return ApprovalRole.HR;
      case RequisitionStatus.PENDING_HIRING_MANAGER_APPROVAL:
        return ApprovalRole.HIRING_MANAGER;
      case RequisitionStatus.PENDING_EXECUTIVE_APPROVAL:
        return ApprovalRole.EXECUTIVE;
      default:
        return null;
    }
  }

  /**
   * Check if a step is in the future (not yet reached)
   */
  private isStepInFuture(role: string, status: RequisitionStatus): boolean {
    const stepOrder = [ApprovalRole.HR, ApprovalRole.HIRING_MANAGER, ApprovalRole.EXECUTIVE];
    const currentStepIndex = this.getCurrentStepIndex(status);
    const roleIndex = stepOrder.indexOf(role as ApprovalRole);
    
    return roleIndex > currentStepIndex;
  }

  /**
   * Get current step index in workflow
   */
  private getCurrentStepIndex(status: RequisitionStatus): number {
    switch (status) {
      case RequisitionStatus.DRAFT:
        return -1;
      case RequisitionStatus.SUBMITTED:
        return 0; // HR step
      case RequisitionStatus.PENDING_HIRING_MANAGER_APPROVAL:
        return 1; // Hiring Manager step
      case RequisitionStatus.PENDING_EXECUTIVE_APPROVAL:
        return 2; // Executive step
      case RequisitionStatus.APPROVED:
      case RequisitionStatus.REJECTED:
        return 3; // Complete
      default:
        return -1;
    }
  }
}

// Export singleton instance
export const approvalTimelineService = new ApprovalTimelineService();