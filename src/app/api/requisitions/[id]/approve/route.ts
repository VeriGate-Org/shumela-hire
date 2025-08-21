import { NextRequest, NextResponse } from 'next/server';
import { workflowEngine } from '../../../../../services/workflowEngine';
import { ApprovalRole } from '../../../../../types/workflow';

// POST /api/requisitions/[id]/approve?role={role} - Approve requisition
export async function POST(
  request: NextRequest,
  context: { params: Promise<{ id: string }> }
) {
  try {
    const params = await context.params;
    const { searchParams } = new URL(request.url);
    const role = searchParams.get('role');
    
    const body = await request.json();
    const {
      userId = 'demo_user',
      comment
    } = body;

    // Validate role parameter
    if (!role || !Object.values(ApprovalRole).includes(role as ApprovalRole)) {
      return NextResponse.json(
        { success: false, message: 'Valid role parameter is required' },
        { status: 400 }
      );
    }

    // Approve requisition
    const result = await workflowEngine.approveRequisition(
      params.id,
      userId,
      role as ApprovalRole,
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
    console.error('Error approving requisition:', error);
    return NextResponse.json(
      { success: false, message: 'Internal server error' },
      { status: 500 }
    );
  }
}