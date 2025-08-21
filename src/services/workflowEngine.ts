import { 
  RequisitionStatus, 
  WorkflowAction, 
  ApprovalRole, 
  RequisitionData, 
  ApprovalHistoryEntry 
} from '../types/workflow';
import {
  isTransitionAllowed,
  getAllowedTransitions,
  getNextApprovalRole
} from './workflowDefinition';
import { requisitionService } from './requisitionService';
import { auditLogService } from './auditLogService';

/**
 * Workflow Engine
 * Orchestrates requisition approval workflow with validation and audit logging
 */
export class WorkflowEngine {
  
  /**
   * Submit requisition for approval
   */
  async submitRequisition(
    requisitionId: string,
    userId: string,
    userRole: ApprovalRole,
    comment?: string
  ): Promise<{ success: boolean; message: string; requisition?: RequisitionData }> {
    try {
      // Get current requisition
      const requisition = await requisitionService.getRequisition(requisitionId);
      if (!requisition) {
        return { success: false, message: 'Requisition not found' };
      }

      // Validate transition
      if (!isTransitionAllowed(
        requisition.status,
        RequisitionStatus.SUBMITTED,
        WorkflowAction.SUBMIT,
        userRole
      )) {
        return { 
          success: false, 
          message: `User with role ${userRole} cannot submit requisition from status ${requisition.status}` 
        };
      }

      // Validate requisition is complete
      if (!this.isRequisitionComplete(requisition)) {
        return { 
          success: false, 
          message: 'Requisition must be complete before submission' 
        };
      }

      // Update status to SUBMITTED (which means pending HR approval)
      const updatedRequisition = await requisitionService.updateRequisition(requisitionId, {
        status: RequisitionStatus.SUBMITTED,
        currentApprovalStep: ApprovalRole.HR
      });

      if (!updatedRequisition) {
        return { success: false, message: 'Failed to update requisition status' };
      }

      // Add to approval history
      const historyEntry: ApprovalHistoryEntry = {
        id: this.generateId(),
        requisitionId,
        action: WorkflowAction.SUBMIT,
        fromStatus: requisition.status,
        toStatus: RequisitionStatus.SUBMITTED,
        approverRole: userRole,
        approverId: userId,
        approverName: 'User', // In production, get from user service
        comment,
        timestamp: new Date()
      };

      // In production, this would be stored in the database
      updatedRequisition.approvalHistory.push(historyEntry);

      // Log audit trail
      await auditLogService.logWorkflowTransition(
        requisitionId,
        requisition.status,
        RequisitionStatus.SUBMITTED,
        userId,
        userRole,
        comment
      );

      return { 
        success: true, 
        message: 'Requisition submitted for approval', 
        requisition: updatedRequisition 
      };

    } catch (error) {
      console.error('Error submitting requisition:', error);
      return { success: false, message: 'Internal server error' };
    }
  }

  /**
   * Approve requisition
   */
  async approveRequisition(
    requisitionId: string,
    userId: string,
    userRole: ApprovalRole,
    comment?: string
  ): Promise<{ success: boolean; message: string; requisition?: RequisitionData }> {
    try {
      const requisition = await requisitionService.getRequisition(requisitionId);
      if (!requisition) {
        return { success: false, message: 'Requisition not found' };
      }

      // Determine next status based on current status and approval sequence
      const nextStatus = this.getNextApprovalStatus(requisition.status);
      if (!nextStatus) {
        return { 
          success: false, 
          message: `Cannot approve requisition in status ${requisition.status}` 
        };
      }

      // Validate user can approve at this stage
      if (!isTransitionAllowed(
        requisition.status,
        nextStatus,
        WorkflowAction.APPROVE,
        userRole
      )) {
        return { 
          success: false, 
          message: `User with role ${userRole} cannot approve requisition at this stage` 
        };
      }

      // Update requisition status
      const nextApprovalRole = getNextApprovalRole(nextStatus);
      const updatedRequisition = await requisitionService.updateRequisition(requisitionId, {
        status: nextStatus,
        currentApprovalStep: nextApprovalRole || undefined
      });

      if (!updatedRequisition) {
        return { success: false, message: 'Failed to update requisition status' };
      }

      // Add to approval history
      const historyEntry: ApprovalHistoryEntry = {
        id: this.generateId(),
        requisitionId,
        action: WorkflowAction.APPROVE,
        fromStatus: requisition.status,
        toStatus: nextStatus,
        approverRole: userRole,
        approverId: userId,
        approverName: 'User', // In production, get from user service
        comment,
        timestamp: new Date()
      };

      updatedRequisition.approvalHistory.push(historyEntry);

      // Log audit trail
      await auditLogService.logWorkflowTransition(
        requisitionId,
        requisition.status,
        nextStatus,
        userId,
        userRole,
        comment
      );

      const statusMessage = nextStatus === RequisitionStatus.APPROVED 
        ? 'Requisition fully approved' 
        : `Requisition approved by ${userRole}, pending next approval`;

      return { 
        success: true, 
        message: statusMessage, 
        requisition: updatedRequisition 
      };

    } catch (error) {
      console.error('Error approving requisition:', error);
      return { success: false, message: 'Internal server error' };
    }
  }

  /**
   * Reject requisition
   */
  async rejectRequisition(
    requisitionId: string,
    userId: string,
    userRole: ApprovalRole,
    comment: string // Required for rejections
  ): Promise<{ success: boolean; message: string; requisition?: RequisitionData }> {
    try {
      const requisition = await requisitionService.getRequisition(requisitionId);
      if (!requisition) {
        return { success: false, message: 'Requisition not found' };
      }

      // Validate user can reject at this stage
      if (!isTransitionAllowed(
        requisition.status,
        RequisitionStatus.REJECTED,
        WorkflowAction.REJECT,
        userRole
      )) {
        return { 
          success: false, 
          message: `User with role ${userRole} cannot reject requisition at this stage` 
        };
      }

      if (!comment || comment.trim().length === 0) {
        return { success: false, message: 'Comment is required for rejection' };
      }

      // Update requisition status to REJECTED
      const updatedRequisition = await requisitionService.updateRequisition(requisitionId, {
        status: RequisitionStatus.REJECTED,
        currentApprovalStep: undefined
      });

      if (!updatedRequisition) {
        return { success: false, message: 'Failed to update requisition status' };
      }

      // Add to approval history
      const historyEntry: ApprovalHistoryEntry = {
        id: this.generateId(),
        requisitionId,
        action: WorkflowAction.REJECT,
        fromStatus: requisition.status,
        toStatus: RequisitionStatus.REJECTED,
        approverRole: userRole,
        approverId: userId,
        approverName: 'User',
        comment,
        timestamp: new Date()
      };

      updatedRequisition.approvalHistory.push(historyEntry);

      // Log audit trail
      await auditLogService.logWorkflowTransition(
        requisitionId,
        requisition.status,
        RequisitionStatus.REJECTED,
        userId,
        userRole,
        comment
      );

      return { 
        success: true, 
        message: `Requisition rejected by ${userRole}`, 
        requisition: updatedRequisition 
      };

    } catch (error) {
      console.error('Error rejecting requisition:', error);
      return { success: false, message: 'Internal server error' };
    }
  }

  /**
   * Get allowed actions for user on requisition
   */
  async getAllowedActions(
    requisitionId: string,
    userRole: ApprovalRole
  ): Promise<WorkflowAction[]> {
    const requisition = await requisitionService.getRequisition(requisitionId);
    if (!requisition) return [];

    const allowedTransitions = getAllowedTransitions(requisition.status, userRole);
    return allowedTransitions.map(t => t.action);
  }

  /**
   * Get requisitions pending approval for a role
   */
  async getPendingRequisitions(userRole: ApprovalRole): Promise<RequisitionData[]> {
    const roleMap = {
      [ApprovalRole.HR]: 'HR',
      [ApprovalRole.HIRING_MANAGER]: 'Hiring Manager',
      [ApprovalRole.EXECUTIVE]: 'Admin'
    };

    return requisitionService.getPendingRequisitionsForRole(roleMap[userRole]);
  }

  // Private helper methods

  private isRequisitionComplete(requisition: RequisitionData): boolean {
    return !!(
      requisition.jobTitle &&
      requisition.department &&
      requisition.location &&
      requisition.employmentType &&
      requisition.description &&
      requisition.description.trim().length > 0
    );
  }

  private getNextApprovalStatus(currentStatus: RequisitionStatus): RequisitionStatus | null {
    switch (currentStatus) {
      case RequisitionStatus.SUBMITTED:
        return RequisitionStatus.PENDING_HIRING_MANAGER_APPROVAL;
      case RequisitionStatus.PENDING_HIRING_MANAGER_APPROVAL:
        return RequisitionStatus.PENDING_EXECUTIVE_APPROVAL;
      case RequisitionStatus.PENDING_EXECUTIVE_APPROVAL:
        return RequisitionStatus.APPROVED;
      default:
        return null;
    }
  }

  private generateId(): string {
    return 'hist_' + Math.random().toString(36).substring(2, 15);
  }
}

// Export singleton instance
export const workflowEngine = new WorkflowEngine();