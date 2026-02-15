import { NextRequest, NextResponse } from 'next/server';
import { workflowEngine } from '../../../../../services/workflowEngine';
import { ApprovalRole } from '../../../../../types/workflow';

// POST /api/requisitions/[id]/submit - Submit requisition for approval
export async function POST(
  request: NextRequest,
  context: { params: Promise<{ id: string }> }
) {
  try {
    const params = await context.params;
    const body = await request.json();
    const {
      userId = 'demo_user',
      userRole = 'HR_MANAGER',
      comment
    } = body;

    // Validate user role
    if (!Object.values(ApprovalRole).includes(userRole)) {
      return NextResponse.json(
        { success: false, message: 'Invalid user role' },
        { status: 400 }
      );
    }

    // Submit requisition
    const result = await workflowEngine.submitRequisition(
      params.id,
      userId,
      userRole as ApprovalRole,
      comment
    );

    if (!result.success) {
      return NextResponse.json(
        { success: false, message: result.message },
        { status: 400 }
      );
    }

    return NextResponse.json({
      success: true,
      message: result.message,
      data: result.requisition
    });

  } catch (error) {
    console.error('Error submitting requisition:', error);
    return NextResponse.json(
      { success: false, message: 'Internal server error' },
      { status: 500 }
    );
  }
}