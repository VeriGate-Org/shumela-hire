import { NextRequest, NextResponse } from 'next/server';
import { jobTemplateService } from '../../../../../services/jobTemplateService';

// POST /api/job-templates/[id]/duplicate - Duplicate template
export async function POST(
  request: NextRequest,
  context: { params: Promise<{ id: string }> }
) {
  try {
    const params = await context.params;
    const body = await request.json();
    const { name } = body;

    if (!name) {
      return NextResponse.json(
        { success: false, message: 'New template name is required' },
        { status: 400 }
      );
    }

    const duplicatedTemplate = await jobTemplateService.duplicateTemplate(params.id, name);

    if (!duplicatedTemplate) {
      return NextResponse.json(
        { success: false, message: 'Template not found' },
        { status: 404 }
      );
    }

    return NextResponse.json({
      success: true,
      message: 'Template duplicated successfully',
      data: duplicatedTemplate
    });

  } catch (error) {
    console.error('Error duplicating job template:', error);
    return NextResponse.json(
      { success: false, message: 'Internal server error' },
      { status: 500 }
    );
  }
}