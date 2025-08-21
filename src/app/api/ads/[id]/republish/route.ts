import { NextRequest, NextResponse } from 'next/server';
import { jobAdService } from '../../../../../services/jobAdService';
import { PublishingChannel } from '../../../../../types/jobAd';

// POST /api/ads/[id]/republish - Republish job ad
export async function POST(
  request: NextRequest,
  context: { params: Promise<{ id: string }> }
) {
  try {
    const params = await context.params;
    const body = await request.json();
    const { 
      channels,
      expiresAt,
      performedBy = 'demo_user@company.com'
    } = body;

    if (!channels || !expiresAt) {
      return NextResponse.json(
        { success: false, message: 'channels and expiresAt are required' },
        { status: 400 }
      );
    }

    const republishedJobAd = await jobAdService.republishJobAd(
      params.id,
      channels as PublishingChannel[],
      new Date(expiresAt),
      performedBy
    );

    if (!republishedJobAd) {
      return NextResponse.json(
        { success: false, message: 'Job ad not found' },
        { status: 404 }
      );
    }

    return NextResponse.json({
      success: true,
      message: 'Job ad republished successfully',
      data: republishedJobAd
    });

  } catch (error) {
    console.error('Error republishing job ad:', error);
    return NextResponse.json(
      { success: false, message: 'Internal server error' },
      { status: 500 }
    );
  }
}