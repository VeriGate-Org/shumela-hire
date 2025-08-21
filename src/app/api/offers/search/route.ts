import { NextRequest, NextResponse } from 'next/server';

interface Offer {
  id: number;
  offerNumber: string;
  version: number;
  status: string;
  statusDisplayName: string;
  statusIcon: string;
  statusCssClass: string;
  offerType: string;
  negotiationStatus: string;
  negotiationStatusDisplayName: string;
  negotiationStatusIcon: string;
  negotiationStatusCssClass: string;
  jobTitle: string;
  department: string;
  baseSalary: number;
  currency: string;
  totalCompensation: number;
  startDate: string;
  offerExpiryDate: string;
  offerSentAt?: string;
  acceptedAt?: string;
  declinedAt?: string;
  negotiationRounds: number;
  application: {
    id: number;
    applicant: {
      firstName: string;
      lastName: string;
      email: string;
    };
    jobPosting: {
      title: string;
      department: string;
    };
  };
  createdAt: string;
  createdBy: number;
}

const mockOffers: Offer[] = [
  {
    id: 1,
    offerNumber: 'OFF-2025-001',
    version: 1,
    status: 'DRAFT',
    statusDisplayName: 'Draft',
    statusIcon: '📝',
    statusCssClass: 'bg-gray-100 text-gray-800',
    offerType: 'STANDARD',
    negotiationStatus: 'NOT_STARTED',
    negotiationStatusDisplayName: 'Not Started',
    negotiationStatusIcon: '⏳',
    negotiationStatusCssClass: 'bg-gray-100 text-gray-800',
    jobTitle: 'Senior Software Engineer',
    department: 'Engineering',
    baseSalary: 1800000,
    currency: 'ZAR',
    totalCompensation: 2100000,
    startDate: '2025-03-01',
    offerExpiryDate: '2025-02-15',
    negotiationRounds: 0,
    application: {
      id: 101,
      applicant: {
        firstName: 'Thabo',
        lastName: 'Mthembu',
        email: 'thabo.mthembu@example.com'
      },
      jobPosting: {
        title: 'Senior Software Engineer',
        department: 'Engineering'
      }
    },
    createdAt: '2025-01-15T09:00:00Z',
    createdBy: 1
  },
  {
    id: 2,
    offerNumber: 'OFF-2025-002',
    version: 1,
    status: 'SENT',
    statusDisplayName: 'Sent',
    statusIcon: '📤',
    statusCssClass: 'bg-blue-100 text-blue-800',
    offerType: 'STANDARD',
    negotiationStatus: 'PENDING_REVIEW',
    negotiationStatusDisplayName: 'Pending Review',
    negotiationStatusIcon: '👀',
    negotiationStatusCssClass: 'bg-yellow-100 text-yellow-800',
    jobTitle: 'Product Manager',
    department: 'Product',
    baseSalary: 1650000,
    currency: 'ZAR',
    totalCompensation: 1950000,
    startDate: '2025-02-15',
    offerExpiryDate: '2025-02-08',
    offerSentAt: '2025-01-20T14:30:00Z',
    negotiationRounds: 0,
    application: {
      id: 102,
      applicant: {
        firstName: 'Nomsa',
        lastName: 'Dlamini',
        email: 'nomsa.dlamini@example.com'
      },
      jobPosting: {
        title: 'Product Manager',
        department: 'Product'
      }
    },
    createdAt: '2025-01-18T11:15:00Z',
    createdBy: 1
  },
  {
    id: 3,
    offerNumber: 'OFF-2025-003',
    version: 2,
    status: 'NEGOTIATING',
    statusDisplayName: 'Under Negotiation',
    statusIcon: '🤝',
    statusCssClass: 'bg-orange-100 text-orange-800',
    offerType: 'EXECUTIVE',
    negotiationStatus: 'ACTIVE',
    negotiationStatusDisplayName: 'Active Negotiation',
    negotiationStatusIcon: '🔄',
    negotiationStatusCssClass: 'bg-orange-100 text-orange-800',
    jobTitle: 'Marketing Director',
    department: 'Marketing',
    baseSalary: 2200000,
    currency: 'ZAR',
    totalCompensation: 2800000,
    startDate: '2025-04-01',
    offerExpiryDate: '2025-02-20',
    offerSentAt: '2025-01-10T08:45:00Z',
    negotiationRounds: 2,
    application: {
      id: 103,
      applicant: {
        firstName: 'Michael',
        lastName: 'Van der Merwe',
        email: 'michael.vandermerwe@example.com'
      },
      jobPosting: {
        title: 'Marketing Director',
        department: 'Marketing'
      }
    },
    createdAt: '2025-01-08T16:20:00Z',
    createdBy: 1
  },
  {
    id: 4,
    offerNumber: 'OFF-2025-004',
    version: 1,
    status: 'ACCEPTED',
    statusDisplayName: 'Accepted',
    statusIcon: '✅',
    statusCssClass: 'bg-green-100 text-green-800',
    offerType: 'STANDARD',
    negotiationStatus: 'COMPLETED',
    negotiationStatusDisplayName: 'Completed',
    negotiationStatusIcon: '🎉',
    negotiationStatusCssClass: 'bg-green-100 text-green-800',
    jobTitle: 'Data Scientist',
    department: 'Data Science',
    baseSalary: 1900000,
    currency: 'ZAR',
    totalCompensation: 2300000,
    startDate: '2025-02-01',
    offerExpiryDate: '2025-01-25',
    offerSentAt: '2025-01-12T13:15:00Z',
    acceptedAt: '2025-01-22T10:30:00Z',
    negotiationRounds: 1,
    application: {
      id: 104,
      applicant: {
        firstName: 'Lerato',
        lastName: 'Mokoena',
        email: 'lerato.mokoena@example.com'
      },
      jobPosting: {
        title: 'Data Scientist',
        department: 'Data Science'
      }
    },
    createdAt: '2025-01-05T12:00:00Z',
    createdBy: 1
  },
  {
    id: 5,
    offerNumber: 'OFF-2025-005',
    version: 1,
    status: 'EXPIRED',
    statusDisplayName: 'Expired',
    statusIcon: '⏰',
    statusCssClass: 'bg-red-100 text-red-800',
    offerType: 'STANDARD',
    negotiationStatus: 'EXPIRED',
    negotiationStatusDisplayName: 'Expired',
    negotiationStatusIcon: '❌',
    negotiationStatusCssClass: 'bg-red-100 text-red-800',
    jobTitle: 'UX Designer',
    department: 'Design',
    baseSalary: 1500000,
    currency: 'ZAR',
    totalCompensation: 1750000,
    startDate: '2025-02-01',
    offerExpiryDate: '2025-01-20',
    offerSentAt: '2025-01-08T09:00:00Z',
    negotiationRounds: 0,
    application: {
      id: 105,
      applicant: {
        firstName: 'Sipho',
        lastName: 'Khumalo',
        email: 'sipho.khumalo@example.com'
      },
      jobPosting: {
        title: 'UX Designer',
        department: 'Design'
      }
    },
    createdAt: '2025-01-03T14:45:00Z',
    createdBy: 1
  }
];

export async function GET(request: NextRequest) {
  const { searchParams } = new URL(request.url);
  const page = parseInt(searchParams.get('page') || '0');
  const size = parseInt(searchParams.get('size') || '10');
  const status = searchParams.get('status');
  const offerType = searchParams.get('offerType');
  const department = searchParams.get('department');
  const minSalary = searchParams.get('minSalary');
  const maxSalary = searchParams.get('maxSalary');

  let filteredOffers = [...mockOffers];

  // Apply filters
  if (status) {
    filteredOffers = filteredOffers.filter(offer => offer.status === status);
  }
  if (offerType) {
    filteredOffers = filteredOffers.filter(offer => offer.offerType === offerType);
  }
  if (department) {
    filteredOffers = filteredOffers.filter(offer => 
      offer.department.toLowerCase().includes(department.toLowerCase())
    );
  }
  if (minSalary) {
    filteredOffers = filteredOffers.filter(offer => offer.baseSalary >= parseInt(minSalary));
  }
  if (maxSalary) {
    filteredOffers = filteredOffers.filter(offer => offer.baseSalary <= parseInt(maxSalary));
  }

  // Pagination
  const startIndex = page * size;
  const endIndex = startIndex + size;
  const paginatedOffers = filteredOffers.slice(startIndex, endIndex);

  return NextResponse.json({
    content: paginatedOffers,
    totalElements: filteredOffers.length,
    totalPages: Math.ceil(filteredOffers.length / size),
    currentPage: page,
    size: size,
    first: page === 0,
    last: page >= Math.ceil(filteredOffers.length / size) - 1
  });
}
