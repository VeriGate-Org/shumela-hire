import { NextRequest, NextResponse } from 'next/server';
import { jobAdService } from '../../../services/jobAdService';
// jobTemplateService is available but not needed in current route handlers
import { JobAdFilters, JobAdStatus, PublishingChannel } from '../../../types/jobAd';

// GET /api/ads - Get all job ads with optional filtering
export async function GET(request: NextRequest) {
  try {
    const { searchParams } = new URL(request.url);
    
    const filters: JobAdFilters = {};
    
    if (searchParams.get('status')) {
      filters.status = searchParams.get('status') as JobAdStatus;
    }
    if (searchParams.get('channels')) {
      const channelsParam = searchParams.get('channels');
      filters.channels = channelsParam?.split(',') as PublishingChannel[];
    }
    if (searchParams.get('location')) {
      filters.location = searchParams.get('location')!;
    }
    if (searchParams.get('employmentType')) {
      filters.employmentType = searchParams.get('employmentType')!;
    }
    if (searchParams.get('department')) {
      filters.department = searchParams.get('department')!;
    }
    if (searchParams.get('featured')) {
      filters.featured = searchParams.get('featured') === 'true';
    }
    if (searchParams.get('search')) {
      filters.search = searchParams.get('search')!;
    }

    // Ensure demo data is initialized
    await jobAdService.initializeDemoData();
    
    const jobAds = await jobAdService.getAllJobAds(filters);

    return NextResponse.json({
      success: true,
      data: jobAds
    });

  } catch (error) {
    console.error('Error fetching job ads:', error);
    return NextResponse.json(
      { success: false, message: 'Internal server error' },
      { status: 500 }
    );
  }
}

// POST /api/ads - Create/publish new job ad from draft
export async function POST(request: NextRequest) {
  try {
    const body = await request.json();
    const {
      draftId,
      channels,
      expiresAt,
      companyName,
      department,
      featured = false,
      customSlug,
      publishedBy = 'demo_user@company.com'
    } = body;

    // Validate required fields
    if (!draftId || !channels || !expiresAt || !companyName) {
      return NextResponse.json(
        { success: false, message: 'draftId, channels, expiresAt, and companyName are required' },
        { status: 400 }
      );
    }

    // Get draft from template service (assuming drafts are stored there)
    // In a real app, you'd have a separate draft service
    const draft = {
      id: draftId,
      templateId: 'template_001',
      requisitionId: undefined,
      title: 'Sample Job Title',
      intro: '<p>Sample job introduction</p>',
      responsibilities: '<p>Sample responsibilities</p>',
      requirements: '<p>Sample requirements</p>',
      benefits: '<p>Sample benefits</p>',
      location: 'Sample Location',
      employmentType: 'Full-time',
      salaryRangeMin: undefined,
      salaryRangeMax: undefined,
      closingDate: undefined,
      contactEmail: 'careers@company.com',
      status: 'draft' as const,
      createdBy: publishedBy,
      createdAt: new Date(),
      updatedAt: new Date()
    };

    const publishingRequest = {
      draftId,
      channels,
      expiresAt: new Date(expiresAt),
      companyName,
      department,
      featured,
      customSlug
    };

    const jobAd = await jobAdService.publishJobAd(draft, publishingRequest, publishedBy);

    return NextResponse.json({
      success: true,
      message: 'Job ad published successfully',
      data: jobAd
    });

  } catch (error) {
    console.error('Error publishing job ad:', error);
    return NextResponse.json(
      { success: false, message: error instanceof Error ? error.message : 'Internal server error' },
      { status: 500 }
    );
  }
}