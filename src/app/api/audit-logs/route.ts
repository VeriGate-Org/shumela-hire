import { NextRequest, NextResponse } from 'next/server';
import { auditLogService } from '../../../services/auditLogService';

// GET /api/audit-logs - Get audit logs with optional filtering
export async function GET(request: NextRequest) {
  try {
    const { searchParams } = new URL(request.url);
    const entityId = searchParams.get('entityId');
    const entityType = searchParams.get('entityType');
    const userId = searchParams.get('userId');
    const limit = searchParams.get('limit');

    let auditLogs;

    if (entityId && entityType) {
      // Filter by both entity type and ID
      auditLogs = await auditLogService.getRequisitionAuditLogs(entityId);
      // Additional filtering by entity type if needed
      auditLogs = auditLogs.filter(log => log.entityType === entityType);
    } else if (entityId) {
      auditLogs = await auditLogService.getRequisitionAuditLogs(entityId);
    } else if (entityType) {
      auditLogs = await auditLogService.getAllRequisitionAuditLogs();
    } else if (userId) {
      auditLogs = await auditLogService.getUserAuditLogs(userId);
    } else {
      const limitNum = limit ? parseInt(limit) : 50;
      auditLogs = await auditLogService.getRecentAuditLogs(limitNum);
    }

    return NextResponse.json({
      success: true,
      data: auditLogs
    });

  } catch (error) {
    console.error('Error fetching audit logs:', error);
    return NextResponse.json(
      { success: false, message: 'Internal server error' },
      { status: 500 }
    );
  }
}