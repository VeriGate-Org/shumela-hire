import { NextRequest, NextResponse } from 'next/server';
import { jobAdService } from '../../../../../services/jobAdService';

// POST /api/ads/[id]/unpublish - Unpublish job ad
export async function POST(
  request: NextRequest,
  context: { params: Promise<{ id: string }> }
) {
  try {
    const params = await context.params;
    const body = await request.json();
    const { 
      performedBy = 'demo_user@company.com',
      reason 
    } = body;

    const unpublishedJobAd = await jobAdService.unpublishJobAd(params.id, performedBy, reason);

    if (!unpublishedJobAd) {
      return NextResponse.json(
        { success: false, message: 'Job ad not found' },
        { status: 404 }
      );
    }

    return NextResponse.json({
      success: true,
      message: 'Job ad unpublished successfully',
      data: unpublishedJobAd
    });

  } catch (error) {
    console.error('Error unpublishing job ad:', error);
    return NextResponse.json(
      { success: false, message: 'Internal server error' },
      { status: 500 }
    );
  }
}