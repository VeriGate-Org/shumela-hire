import { NextRequest, NextResponse } from 'next/server';
import { jobAdService } from '../../../../services/jobAdService';

// GET /api/ads/[id] - Get specific job ad
export async function GET(
  request: NextRequest,
  context: { params: Promise<{ id: string }> }
) {
  try {
    const params = await context.params;
    const jobAd = await jobAdService.getJobAd(params.id);

    if (!jobAd) {
      return NextResponse.json(
        { success: false, message: 'Job ad not found' },
        { status: 404 }
      );
    }

    // Record view
    await jobAdService.recordView(params.id);

    return NextResponse.json({
      success: true,
      data: jobAd
    });

  } catch (error) {
    console.error('Error fetching job ad:', error);
    return NextResponse.json(
      { success: false, message: 'Internal server error' },
      { status: 500 }
    );
  }
}

// PUT /api/ads/[id] - Update job ad
export async function PUT(
  request: NextRequest,
  context: { params: Promise<{ id: string }> }
) {
  try {
    const params = await context.params;
    const body = await request.json();
    
    const {
      title,
      intro,
      responsibilities,
      requirements,
      benefits,
      location,
      employmentType,
      salaryRangeMin,
      salaryRangeMax,
      contactEmail,
      companyName,
      department,
      featured,
      expiresAt
    } = body;

    // Build updates object
    const updates: Record<string, unknown> = {};
    if (title !== undefined) updates.title = title;
    if (intro !== undefined) updates.intro = intro;
    if (responsibilities !== undefined) updates.responsibilities = responsibilities;
    if (requirements !== undefined) updates.requirements = requirements;
    if (benefits !== undefined) updates.benefits = benefits;
    if (location !== undefined) updates.location = location;
    if (employmentType !== undefined) updates.employmentType = employmentType;
    if (salaryRangeMin !== undefined) updates.salaryRangeMin = salaryRangeMin ? Number(salaryRangeMin) : undefined;
    if (salaryRangeMax !== undefined) updates.salaryRangeMax = salaryRangeMax ? Number(salaryRangeMax) : undefined;
    if (contactEmail !== undefined) updates.contactEmail = contactEmail;
    if (companyName !== undefined) updates.companyName = companyName;
    if (department !== undefined) updates.department = department;
    if (featured !== undefined) updates.featured = featured;
    if (expiresAt !== undefined) updates.expiresAt = new Date(expiresAt);

    const updatedJobAd = await jobAdService.updateJobAd(params.id, updates);

    if (!updatedJobAd) {
      return NextResponse.json(
        { success: false, message: 'Job ad not found' },
        { status: 404 }
      );
    }

    return NextResponse.json({
      success: true,
      message: 'Job ad updated successfully',
      data: updatedJobAd
    });

  } catch (error) {
    console.error('Error updating job ad:', error);
    return NextResponse.json(
      { success: false, message: 'Internal server error' },
      { status: 500 }
    );
  }
}

// DELETE /api/ads/[id] - Delete job ad
export async function DELETE(
  request: NextRequest,
  context: { params: Promise<{ id: string }> }
) {
  try {
    const params = await context.params;
    const deleted = await jobAdService.deleteJobAd(params.id);

    if (!deleted) {
      return NextResponse.json(
        { success: false, message: 'Job ad not found' },
        { status: 404 }
      );
    }

    return NextResponse.json({
      success: true,
      message: 'Job ad deleted successfully'
    });

  } catch (error) {
    console.error('Error deleting job ad:', error);
    return NextResponse.json(
      { success: false, message: 'Internal server error' },
      { status: 500 }
    );
  }
}