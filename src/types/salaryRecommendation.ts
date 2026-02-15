export enum SalaryRecommendationStatus {
  DRAFT = 'DRAFT',
  PENDING_REVIEW = 'PENDING_REVIEW',
  RECOMMENDED = 'RECOMMENDED',
  PENDING_APPROVAL = 'PENDING_APPROVAL',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  RETURNED = 'RETURNED',
  IMPLEMENTED = 'IMPLEMENTED',
}

export interface SalaryRecommendation {
  id: number;
  recommendationNumber: string;
  status: SalaryRecommendationStatus;
  positionTitle: string;
  department?: string;
  jobGrade?: string;
  positionLevel?: string;
  requestedBy: string;
  candidateName?: string;
  candidateCurrentSalary?: number;
  candidateExpectedSalary?: number;
  marketDataReference?: string;
  proposedMinSalary?: number;
  proposedMaxSalary?: number;
  proposedTargetSalary?: number;
  recommendedSalary?: number;
  recommendedBy?: string;
  recommendedAt?: string;
  recommendationJustification?: string;
  bonusRecommendation?: string;
  equityRecommendation?: string;
  benefitsNotes?: string;
  requiresApproval: boolean;
  approvalLevelRequired?: number;
  approvedBy?: string;
  approvedAt?: string;
  approvalNotes?: string;
  rejectedBy?: string;
  rejectionReason?: string;
  currency: string;
  applicationId?: number;
  offerId?: number;
  createdAt: string;
  updatedAt: string;
}

export interface SalaryRecommendationCreateRequest {
  positionTitle: string;
  department?: string;
  jobGrade?: string;
  positionLevel?: string;
  candidateName?: string;
  candidateCurrentSalary?: number;
  candidateExpectedSalary?: number;
  marketDataReference?: string;
  proposedMinSalary?: number;
  proposedMaxSalary?: number;
  proposedTargetSalary?: number;
  applicationId?: number;
}

export interface SalaryRecommendationProvideRequest {
  recommendedSalary: number;
  recommendationJustification?: string;
  bonusRecommendation?: string;
  equityRecommendation?: string;
  benefitsNotes?: string;
}

export function getStatusColor(status: SalaryRecommendationStatus): string {
  switch (status) {
    case SalaryRecommendationStatus.DRAFT: return 'bg-gray-100 text-gray-800';
    case SalaryRecommendationStatus.PENDING_REVIEW: return 'bg-yellow-100 text-yellow-800';
    case SalaryRecommendationStatus.RECOMMENDED: return 'bg-blue-100 text-blue-800';
    case SalaryRecommendationStatus.PENDING_APPROVAL: return 'bg-orange-100 text-orange-800';
    case SalaryRecommendationStatus.APPROVED: return 'bg-green-100 text-green-800';
    case SalaryRecommendationStatus.REJECTED: return 'bg-red-100 text-red-800';
    case SalaryRecommendationStatus.RETURNED: return 'bg-purple-100 text-purple-800';
    case SalaryRecommendationStatus.IMPLEMENTED: return 'bg-emerald-100 text-emerald-800';
    default: return 'bg-gray-100 text-gray-800';
  }
}

export function getStatusDisplayName(status: SalaryRecommendationStatus): string {
  switch (status) {
    case SalaryRecommendationStatus.DRAFT: return 'Draft';
    case SalaryRecommendationStatus.PENDING_REVIEW: return 'Pending Review';
    case SalaryRecommendationStatus.RECOMMENDED: return 'Recommended';
    case SalaryRecommendationStatus.PENDING_APPROVAL: return 'Pending Approval';
    case SalaryRecommendationStatus.APPROVED: return 'Approved';
    case SalaryRecommendationStatus.REJECTED: return 'Rejected';
    case SalaryRecommendationStatus.RETURNED: return 'Returned';
    case SalaryRecommendationStatus.IMPLEMENTED: return 'Implemented';
    default: return status;
  }
}
