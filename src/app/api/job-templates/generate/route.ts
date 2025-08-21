import { NextRequest, NextResponse } from 'next/server';
import { jobTemplateService } from '../../../../services/jobTemplateService';
import { requisitionService } from '../../../../services/requisitionService';

// POST /api/job-templates/generate - Generate job ad from template
export async function POST(request: NextRequest) {
  try {
    const body = await request.json();
    const { templateId, requisitionId, customData } = body;

    if (!templateId) {
      return NextResponse.json(
        { success: false, message: 'Template ID is required' },
        { status: 400 }
      );
    }

    let requisitionData;
    if (requisitionId) {
      requisitionData = await requisitionService.getRequisition(requisitionId);
      if (!requisitionData) {
        return NextResponse.json(
          { success: false, message: 'Requisition not found' },
          { status: 404 }
        );
      }
    }

    const draft = await jobTemplateService.generateJobAdDraft(
      {
        templateId,
        requisitionId,
        customData: customData || {}
      },
      requisitionData || undefined
    );

    return NextResponse.json({
      success: true,
      message: 'Job ad draft generated successfully',
      data: draft
    });

  } catch (error) {
    console.error('Error generating job ad:', error);
    return NextResponse.json(
      { success: false, message: error instanceof Error ? error.message : 'Internal server error' },
      { status: 500 }
    );
  }
}