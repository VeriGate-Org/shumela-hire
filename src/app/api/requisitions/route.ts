import { NextRequest, NextResponse } from 'next/server';
import { requisitionService } from '../../../services/requisitionService';
import { auditLogService } from '../../../services/auditLogService';
import { RequisitionStatus } from '../../../types/workflow';

// GET /api/requisitions - Get all requisitions or filter by status
export async function GET(request: NextRequest) {
  try {
    // Ensure demo data is initialized
    await requisitionService.initializeDemoData();
    
    const { searchParams } = new URL(request.url);
    const status = searchParams.get('status') as RequisitionStatus | null;
    const role = searchParams.get('role');

    let requisitions;

    if (status) {
      requisitions = await requisitionService.getRequisitionsByStatus(status);
    } else if (role) {
      requisitions = await requisitionService.getPendingRequisitionsForRole(role);
    } else {
      requisitions = await requisitionService.getAllRequisitions();
    }

    return NextResponse.json({
      success: true,
      data: requisitions
    });

  } catch (error) {
    console.error('Error fetching requisitions:', error);
    return NextResponse.json(
      { success: false, message: 'Internal server error' },
      { status: 500 }
    );
  }
}

// POST /api/requisitions - Create new requisition
export async function POST(request: NextRequest) {
  try {
    const body = await request.json();
    const {
      jobTitle,
      department,
      location,
      employmentType,
      salaryMin,
      salaryMax,
      description,
      createdBy = 'demo_user'
    } = body;

    // Validate required fields
    if (!jobTitle || !department || !location || !employmentType) {
      return NextResponse.json(
        { success: false, message: 'Missing required fields' },
        { status: 400 }
      );
    }

    // Create requisition
    const requisition = await requisitionService.createRequisition(
      {
        jobTitle,
        department,
        location,
        employmentType,
        salaryMin: salaryMin ? Number(salaryMin) : undefined,
        salaryMax: salaryMax ? Number(salaryMax) : undefined,
        description: description || ''
      },
      createdBy
    );

    // Log creation
    await auditLogService.logRequisitionCreated(
      requisition.id,
      createdBy,
      'HR', // Default role for demo
      {
        jobTitle: requisition.jobTitle,
        department: requisition.department,
        location: requisition.location,
        employmentType: requisition.employmentType
      }
    );

    return NextResponse.json({
      success: true,
      message: 'Requisition created successfully',
      data: requisition
    }, { status: 201 });

  } catch (error) {
    console.error('Error creating requisition:', error);
    return NextResponse.json(
      { success: false, message: 'Internal server error' },
      { status: 500 }
    );
  }
}