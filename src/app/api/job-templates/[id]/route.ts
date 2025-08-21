import { NextRequest, NextResponse } from 'next/server';
import { jobTemplateService } from '../../../../services/jobTemplateService';

// GET /api/job-templates/[id] - Get specific template
export async function GET(
  request: NextRequest,
  context: { params: Promise<{ id: string }> }
) {
  try {
    const params = await context.params;
    const template = await jobTemplateService.getTemplate(params.id);

    if (!template) {
      return NextResponse.json(
        { success: false, message: 'Template not found' },
        { status: 404 }
      );
    }

    return NextResponse.json({
      success: true,
      data: template
    });

  } catch (error) {
    console.error('Error fetching job template:', error);
    return NextResponse.json(
      { success: false, message: 'Internal server error' },
      { status: 500 }
    );
  }
}

// PUT /api/job-templates/[id] - Update template
export async function PUT(
  request: NextRequest,
  context: { params: Promise<{ id: string }> }
) {
  try {
    const params = await context.params;
    const body = await request.json();
    
    const {
      name,
      description,
      title,
      intro,
      responsibilities,
      requirements,
      benefits,
      location,
      employmentType,
      salaryRangeMin,
      salaryRangeMax,
      closingDate,
      contactEmail,
      isArchived
    } = body;

    // Build updates object
    const updates: Record<string, unknown> = {};
    if (name !== undefined) updates.name = name;
    if (description !== undefined) updates.description = description;
    if (title !== undefined) updates.title = title;
    if (intro !== undefined) updates.intro = intro;
    if (responsibilities !== undefined) updates.responsibilities = responsibilities;
    if (requirements !== undefined) updates.requirements = requirements;
    if (benefits !== undefined) updates.benefits = benefits;
    if (location !== undefined) updates.location = location;
    if (employmentType !== undefined) updates.employmentType = employmentType;
    if (salaryRangeMin !== undefined) updates.salaryRangeMin = salaryRangeMin ? Number(salaryRangeMin) : undefined;
    if (salaryRangeMax !== undefined) updates.salaryRangeMax = salaryRangeMax ? Number(salaryRangeMax) : undefined;
    if (closingDate !== undefined) updates.closingDate = closingDate ? new Date(closingDate) : undefined;
    if (contactEmail !== undefined) updates.contactEmail = contactEmail;
    if (isArchived !== undefined) updates.isArchived = isArchived;

    const updatedTemplate = await jobTemplateService.updateTemplate(params.id, updates);

    if (!updatedTemplate) {
      return NextResponse.json(
        { success: false, message: 'Template not found' },
        { status: 404 }
      );
    }

    return NextResponse.json({
      success: true,
      message: 'Template updated successfully',
      data: updatedTemplate
    });

  } catch (error) {
    console.error('Error updating job template:', error);
    return NextResponse.json(
      { success: false, message: 'Internal server error' },
      { status: 500 }
    );
  }
}

// DELETE /api/job-templates/[id] - Delete template
export async function DELETE(
  request: NextRequest,
  context: { params: Promise<{ id: string }> }
) {
  try {
    const params = await context.params;
    const deleted = await jobTemplateService.deleteTemplate(params.id);

    if (!deleted) {
      return NextResponse.json(
        { success: false, message: 'Template not found' },
        { status: 404 }
      );
    }

    return NextResponse.json({
      success: true,
      message: 'Template deleted successfully'
    });

  } catch (error) {
    console.error('Error deleting job template:', error);
    return NextResponse.json(
      { success: false, message: 'Internal server error' },
      { status: 500 }
    );
  }
}