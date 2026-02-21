import { NextRequest, NextResponse } from 'next/server';

// Interface for the backend API response
interface BackendJobAd {
  id: number;
  requisitionId?: number;
  title: string;
  htmlBody: string;
  channelInternal: boolean;
  channelExternal: boolean;
  status: 'DRAFT' | 'PUBLISHED' | 'UNPUBLISHED' | 'EXPIRED';
  closingDate?: string;
  slug: string;
  createdBy: string;
  createdAt: string;
  updatedAt: string;
  department?: string;
  location?: string;
  employmentType?: string;
  salaryRangeMin?: number;
  salaryRangeMax?: number;
  companyName?: string;
}

interface BackendApiResponse {
  success: boolean;
  data?: {
    content: BackendJobAd[];
    totalElements: number;
    totalPages: number;
    numberOfElements: number;
  };
  message?: string;
  error?: string;
}

// Interface for the JSON feed response
interface JobFeedItem {
  id: number;
  title: string;
  slug: string;
  closingDate?: string;
  department?: string;
  location?: string;
  employmentType?: string;
  companyName?: string;
  url: string;
  createdAt: string;
}

interface JobFeed {
  version: string;
  title: string;
  description: string;
  lastUpdated: string;
  totalJobs: number;
  jobs: JobFeedItem[];
}

// Fetch active jobs from backend
async function fetchActiveJobs(): Promise<BackendJobAd[]> {
  try {
    const baseUrl = process.env.NEXT_PUBLIC_API_URL || '';
    
    // Fetch published external jobs
    const url = new URL('/ads', baseUrl);
    url.searchParams.set('status', 'PUBLISHED');
    url.searchParams.set('channel', 'external');
    url.searchParams.set('size', '100'); // Get up to 100 jobs
    url.searchParams.set('sort', 'createdAt,desc'); // Most recent first
    
    const response = await fetch(url.toString(), {
      next: { revalidate: 300 } // Revalidate every 5 minutes
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const apiResponse: BackendApiResponse = await response.json();
    
    if (!apiResponse.success || !apiResponse.data) {
      return [];
    }

    // Filter out expired jobs
    const now = new Date();
    return apiResponse.data.content.filter(job => {
      // Only include published external jobs that haven't expired
      if (job.status !== 'PUBLISHED' || !job.channelExternal) {
        return false;
      }
      
      // Check if job has expired
      if (job.closingDate) {
        const closingDate = new Date(job.closingDate);
        if (closingDate < now) {
          return false;
        }
      }
      
      return true;
    });
  } catch (error) {
    console.error('Error fetching active jobs:', error);
    return [];
  }
}

// GET /jobs/index.json - JSON feed of active jobs
export async function GET(_request: NextRequest): Promise<NextResponse> {
  try {
    const jobs = await fetchActiveJobs();
    
    // Transform jobs into feed format
    const feedItems: JobFeedItem[] = jobs.map(job => ({
      id: job.id,
      title: job.title,
      slug: job.slug,
      closingDate: job.closingDate,
      department: job.department,
      location: job.location,
      employmentType: job.employmentType,
      companyName: job.companyName,
      url: `/jobs/${job.slug}`,
      createdAt: job.createdAt
    }));

    const feed: JobFeed = {
      version: '1.0',
      title: 'Active Job Openings',
      description: 'Current job opportunities available for external applications',
      lastUpdated: new Date().toISOString(),
      totalJobs: feedItems.length,
      jobs: feedItems
    };

    return NextResponse.json(feed, {
      status: 200,
      headers: {
        'Content-Type': 'application/json',
        'Cache-Control': 'public, max-age=300, s-maxage=300', // Cache for 5 minutes
        'Access-Control-Allow-Origin': '*', // Allow cross-origin requests
        'Access-Control-Allow-Methods': 'GET',
        'Access-Control-Allow-Headers': 'Content-Type'
      }
    });
  } catch (error) {
    console.error('Error generating job feed:', error);
    
    const errorFeed: JobFeed = {
      version: '1.0',
      title: 'Job Feed Error',
      description: 'Error occurred while fetching job data',
      lastUpdated: new Date().toISOString(),
      totalJobs: 0,
      jobs: []
    };

    return NextResponse.json(errorFeed, {
      status: 500,
      headers: {
        'Content-Type': 'application/json',
        'Cache-Control': 'no-cache'
      }
    });
  }
}

// HEAD request for checking feed updates
export async function HEAD(_request: NextRequest): Promise<NextResponse> {
  return new NextResponse(null, {
    status: 200,
    headers: {
      'Content-Type': 'application/json',
      'Cache-Control': 'public, max-age=300, s-maxage=300',
      'Last-Modified': new Date().toUTCString()
    }
  });
}