import { NextRequest, NextResponse } from 'next/server';
import { jobTemplateService } from '../../../../../services/jobTemplateService';

// POST /api/job-templates/[id]/archive - Archive template
export async function POST(
  request: NextRequest,
  context: { params: Promise<{ id: string }> }
) {
  try {
    const params = await context.params;
    const archivedTemplate = await jobTemplateService.archiveTemplate(params.id);

    if (!archivedTemplate) {
      return NextResponse.json(
        { success: false, message: 'Template not found' },
        { status: 404 }
      );
    }

    return NextResponse.json({
      success: true,
      message: 'Template archived successfully',
      data: archivedTemplate
    });

  } catch (error) {
    console.error('Error archiving job template:', error);
    return NextResponse.json(
      { success: false, message: 'Internal server error' },
      { status: 500 }
    );
  }
}

// DELETE /api/job-templates/[id]/archive - Unarchive template
export async function DELETE(
  request: NextRequest,
  context: { params: Promise<{ id: string }> }
) {
  try {
    const params = await context.params;
    const unarchivedTemplate = await jobTemplateService.unarchiveTemplate(params.id);

    if (!unarchivedTemplate) {
      return NextResponse.json(
        { success: false, message: 'Template not found' },
        { status: 404 }
      );
    }

    return NextResponse.json({
      success: true,
      message: 'Template unarchived successfully',
      data: unarchivedTemplate
    });

  } catch (error) {
    console.error('Error unarchiving job template:', error);
    return NextResponse.json(
      { success: false, message: 'Internal server error' },
      { status: 500 }
    );
  }
}