import { NextRequest, NextResponse } from 'next/server';
import { jobAdService } from '../../../../../services/jobAdService';

// POST /api/ads/[id]/apply - Submit job application
export async function POST(
  request: NextRequest,
  context: { params: Promise<{ id: string }> }
) {
  try {
    const params = await context.params;
    const body = await request.json();
    const {
      applicantName,
      applicantEmail,
      resumeUrl: _resumeUrl,
      coverLetter: _coverLetter,
      source: _source = 'external'
    } = body;

    if (!applicantName || !applicantEmail) {
      return NextResponse.json(
        { success: false, message: 'Applicant name and email are required' },
        { status: 400 }
      );
    }

    // Check if job ad exists and is active
    const jobAd = await jobAdService.getJobAd(params.id);
    if (!jobAd) {
      return NextResponse.json(
        { success: false, message: 'Job ad not found' },
        { status: 404 }
      );
    }

    // Check if job is still accepting applications
    const now = new Date();
    if (new Date(jobAd.expiresAt) <= now || jobAd.status !== 'PUBLISHED') {
      return NextResponse.json(
        { success: false, message: 'This job is no longer accepting applications' },
        { status: 400 }
      );
    }

    // Record application (increment counter)
    await jobAdService.recordApplication(params.id);

    // In a real app, you would:
    // 1. Store the application in a database
    // 2. Send confirmation email to applicant
    // 3. Notify hiring team
    // 4. Parse and store resume
    
    const applicationId = 'app_' + Math.random().toString(36).substring(2, 15);

    return NextResponse.json({
      success: true,
      message: 'Application submitted successfully',
      data: {
        applicationId,
        jobAdId: params.id,
        applicantName,
        applicantEmail,
        submittedAt: new Date().toISOString(),
        status: 'pending'
      }
    });

  } catch (error) {
    console.error('Error submitting application:', error);
    return NextResponse.json(
      { success: false, message: 'Internal server error' },
      { status: 500 }
    );
  }
}