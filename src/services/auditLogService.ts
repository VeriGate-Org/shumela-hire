import { AuditLogEntry } from '../types/workflow';

/**
 * Mock PostgreSQL data store for audit logs
 * In production, this would connect to actual PostgreSQL database
 */
class MockAuditLogStore {
  private auditLogs: Map<string, AuditLogEntry> = new Map();

  private generateId(): string {
    return 'audit_' + Math.random().toString(36).substring(2, 15);
  }

  async create(entry: Omit<AuditLogEntry, 'id' | 'timestamp'>, customTimestamp?: Date): Promise<AuditLogEntry> {
    const id = this.generateId();
    const auditEntry: AuditLogEntry = {
      ...entry,
      id,
      timestamp: customTimestamp || new Date()
    };

    this.auditLogs.set(id, auditEntry);
    return auditEntry;
  }

  async findByEntityId(entityId: string): Promise<AuditLogEntry[]> {
    return Array.from(this.auditLogs.values())
      .filter(entry => entry.entityId === entityId)
      .sort((a, b) => b.timestamp.getTime() - a.timestamp.getTime());
  }

  async findByEntityType(entityType: string): Promise<AuditLogEntry[]> {
    return Array.from(this.auditLogs.values())
      .filter(entry => entry.entityType === entityType)
      .sort((a, b) => b.timestamp.getTime() - a.timestamp.getTime());
  }

  async findByUserId(userId: string): Promise<AuditLogEntry[]> {
    return Array.from(this.auditLogs.values())
      .filter(entry => entry.userId === userId)
      .sort((a, b) => b.timestamp.getTime() - a.timestamp.getTime());
  }

  async findAll(limit?: number): Promise<AuditLogEntry[]> {
    const entries = Array.from(this.auditLogs.values())
      .sort((a, b) => b.timestamp.getTime() - a.timestamp.getTime());
    
    return limit ? entries.slice(0, limit) : entries;
  }

  async findRecent(limit: number = 50): Promise<AuditLogEntry[]> {
    return this.findAll(limit);
  }
}

// Singleton instance
const auditLogStore = new MockAuditLogStore();

/**
 * Audit Log Service
 * Tracks all system actions for compliance and debugging
 */
export class AuditLogService {
  private store = auditLogStore;

  /**
   * Log a workflow transition
   */
  async logWorkflowTransition(
    requisitionId: string,
    fromStatus: string,
    toStatus: string,
    userId: string,
    userRole: string,
    comment?: string,
    customTimestamp?: Date
  ): Promise<AuditLogEntry> {
    const action = toStatus === 'REJECTED' ? 'REJECT' : 'APPROVE';
    return this.store.create({
      entityType: 'Requisition',
      entityId: requisitionId,
      action: `workflow_${action.toLowerCase()}`,
      userId,
      userRole,
      details: {
        action,
        fromStatus,
        toStatus,
        comment: comment || undefined
      }
    }, customTimestamp);
  }

  /**
   * Log requisition creation
   */
  async logRequisitionCreated(
    requisitionId: string,
    userId: string,
    userRole: string,
    requisitionData: Record<string, unknown> | Date
  ): Promise<AuditLogEntry> {
    // Handle case where timestamp is passed as the data parameter (for demo data)
    const timestamp = requisitionData instanceof Date ? requisitionData : undefined;
    const data = requisitionData instanceof Date ? {} : requisitionData;
    
    return this.store.create({
      entityType: 'Requisition',
      entityId: requisitionId,
      action: 'created',
      userId,
      userRole,
      details: {
        jobTitle: data.jobTitle,
        department: data.department,
        location: data.location,
        employmentType: data.employmentType
      }
    }, timestamp);
  }

  /**
   * Log requisition update
   */
  async logRequisitionUpdated(
    requisitionId: string,
    userId: string,
    userRole: string,
    changes: Record<string, unknown>
  ): Promise<AuditLogEntry> {
    return this.store.create({
      entityType: 'Requisition',
      entityId: requisitionId,
      action: 'updated',
      userId,
      userRole,
      details: {
        changes
      }
    });
  }

  /**
   * Log user authentication
   */
  async logUserAuthentication(
    userId: string,
    userRole: string,
    action: 'login' | 'logout',
    ipAddress?: string
  ): Promise<AuditLogEntry> {
    return this.store.create({
      entityType: 'User',
      entityId: userId,
      action: `auth_${action}`,
      userId,
      userRole,
      details: {
        action,
        ipAddress,
        timestamp: new Date().toISOString()
      }
    });
  }

  /**
   * Log role switch (for demo purposes)
   */
  async logRoleSwitch(
    userId: string,
    fromRole: string,
    toRole: string
  ): Promise<AuditLogEntry> {
    return this.store.create({
      entityType: 'User',
      entityId: userId,
      action: 'role_switch',
      userId,
      userRole: toRole,
      details: {
        fromRole,
        toRole,
        reason: 'demo_role_switch'
      }
    });
  }

  /**
   * Get audit logs for a specific requisition
   */
  async getRequisitionAuditLogs(requisitionId: string): Promise<AuditLogEntry[]> {
    return this.store.findByEntityId(requisitionId);
  }

  /**
   * Get all audit logs for requisitions
   */
  async getAllRequisitionAuditLogs(): Promise<AuditLogEntry[]> {
    return this.store.findByEntityType('Requisition');
  }

  /**
   * Get audit logs for a specific user
   */
  async getUserAuditLogs(userId: string): Promise<AuditLogEntry[]> {
    return this.store.findByUserId(userId);
  }

  /**
   * Get recent audit logs
   */
  async getRecentAuditLogs(limit: number = 50): Promise<AuditLogEntry[]> {
    return this.store.findRecent(limit);
  }

  /**
   * Get all audit logs
   */
  async getAllAuditLogs(): Promise<AuditLogEntry[]> {
    return this.store.findAll();
  }
}

// Export singleton instance
export const auditLogService = new AuditLogService();