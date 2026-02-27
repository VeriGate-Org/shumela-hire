// Performance Management Types
export interface PerformanceCycle {
  id: string;
  name: string;
  description?: string;
  startDate: string;
  endDate: string;
  midYearDeadline: string;
  finalReviewDeadline: string;
  status: CycleStatus;
  tenantId: string;
  createdBy: string;
  createdAt: string;
  updatedAt?: string;
  isDefault: boolean;
}

export interface PerformanceContract {
  id: string;
  cycle: PerformanceCycle;
  employeeId: string;
  employeeName: string;
  employeeNumber?: string;
  managerId: string;
  managerName: string;
  department?: string;
  jobTitle?: string;
  jobLevel?: string;
  status: ContractStatus;
  submittedAt?: string;
  approvedAt?: string;
  approvedBy?: string;
  approvalComments?: string;
  rejectionReason?: string;
  version: number;
  goals: PerformanceGoal[];
  reviews: PerformanceReview[];
}

export interface PerformanceGoal {
  id: string;
  contractId: string;
  kraId?: string;
  title: string;
  description?: string;
  type: GoalType;
  weighting: number;
  targetValue?: string;
  measurementCriteria?: string;
  smartCriteria?: string;
  isActive: boolean;
  sortOrder?: number;
  kpis: GoalKPI[];
}

export interface GoalKPI {
  id: string;
  goalId: string;
  name: string;
  description?: string;
  targetValue?: string;
  measurementUnit?: string;
  weighting: number;
  type: KPIType;
  sortOrder?: number;
}

export interface PerformanceReview {
  id: string;
  contractId: string;
  type: ReviewType;
  status: ReviewStatus;
  selfAssessmentNotes?: string;
  selfRating?: number;
  selfSubmittedAt?: string;
  managerAssessmentNotes?: string;
  managerRating?: number;
  managerSubmittedAt?: string;
  finalRating?: number;
  completedAt?: string;
  dueDate?: string;
  goalScores: ReviewGoalScore[];
  evidenceFiles: ReviewEvidence[];
}

export interface ReviewGoalScore {
  id: string;
  reviewId: string;
  goalId: string;
  selfScore?: number;
  selfComments?: string;
  managerScore?: number;
  managerComments?: string;
  finalScore?: number;
}

export interface ReviewEvidence {
  id: string;
  reviewId: string;
  fileName: string;
  filePath: string;
  fileSize: number;
  contentType: string;
  description?: string;
  evidenceType: EvidenceType;
  uploadedBy: string;
  uploadedAt: string;
}

export interface PerformanceTemplate {
  id: string;
  name: string;
  description?: string;
  department?: string;
  jobLevel?: string;
  jobFamily?: string;
  goalTemplate?: string;
  kpiTemplate?: string;
  isActive: boolean;
  isDefault: boolean;
}

// Enums
export enum CycleStatus {
  PLANNING = 'PLANNING',
  ACTIVE = 'ACTIVE',
  MID_YEAR = 'MID_YEAR',
  FINAL_REVIEW = 'FINAL_REVIEW',
  CLOSED = 'CLOSED'
}

export enum ContractStatus {
  DRAFT = 'DRAFT',
  SUBMITTED = 'SUBMITTED',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  ACTIVE = 'ACTIVE'
}

export enum GoalType {
  STRATEGIC = 'STRATEGIC',
  OPERATIONAL = 'OPERATIONAL',
  DEVELOPMENT = 'DEVELOPMENT',
  BEHAVIORAL = 'BEHAVIORAL'
}

export enum KPIType {
  QUANTITATIVE = 'QUANTITATIVE',
  QUALITATIVE = 'QUALITATIVE',
  BEHAVIORAL = 'BEHAVIORAL'
}

export enum ReviewType {
  MID_YEAR = 'MID_YEAR',
  FINAL = 'FINAL'
}

export enum ReviewStatus {
  PENDING = 'PENDING',
  EMPLOYEE_SUBMITTED = 'EMPLOYEE_SUBMITTED',
  MANAGER_SUBMITTED = 'MANAGER_SUBMITTED',
  COMPLETED = 'COMPLETED'
}

export enum EvidenceType {
  DOCUMENT = 'DOCUMENT',
  PRESENTATION = 'PRESENTATION',
  REPORT = 'REPORT',
  CERTIFICATE = 'CERTIFICATE',
  FEEDBACK = 'FEEDBACK',
  OTHER = 'OTHER'
}

// Request/Response Types
export interface CreateCycleRequest {
  name: string;
  description?: string;
  startDate: string;
  endDate: string;
  midYearDeadline: string;
  finalReviewDeadline: string;
}

export interface CreateContractRequest {
  cycleId: string;
  employeeId: string;
  employeeName: string;
  employeeNumber?: string;
  managerId: string;
  managerName: string;
  department?: string;
  jobTitle?: string;
  jobLevel?: string;
  templateId?: string;
  goals: CreateGoalRequest[];
}

export interface CreateGoalRequest {
  title: string;
  description?: string;
  type: GoalType;
  weighting: number;
  targetValue?: string;
  measurementCriteria?: string;
  kpis: CreateKPIRequest[];
}

export interface CreateKPIRequest {
  name: string;
  description?: string;
  targetValue?: string;
  measurementUnit?: string;
  weighting: number;
  type: KPIType;
}

export interface CreateTemplateRequest {
  name: string;
  description?: string;
  department?: string;
  jobLevel?: string;
  jobFamily?: string;
  goalTemplate?: string;
  kpiTemplate?: string;
}

// Utility functions
export const getCycleStatusColor = (status: CycleStatus): string => {
  switch (status) {
    case CycleStatus.PLANNING:
      return 'bg-gray-100 text-gray-800';
    case CycleStatus.ACTIVE:
      return 'bg-green-100 text-green-800';
    case CycleStatus.MID_YEAR:
      return 'bg-blue-100 text-blue-800';
    case CycleStatus.FINAL_REVIEW:
      return 'bg-purple-100 text-purple-800';
    case CycleStatus.CLOSED:
      return 'bg-red-100 text-red-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
};

export const getContractStatusColor = (status: ContractStatus): string => {
  switch (status) {
    case ContractStatus.DRAFT:
      return 'bg-gray-100 text-gray-800';
    case ContractStatus.SUBMITTED:
      return 'bg-yellow-100 text-yellow-800';
    case ContractStatus.APPROVED:
      return 'bg-green-100 text-green-800';
    case ContractStatus.REJECTED:
      return 'bg-red-100 text-red-800';
    case ContractStatus.ACTIVE:
      return 'bg-blue-100 text-blue-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
};

export const getGoalTypeColor = (type: GoalType): string => {
  switch (type) {
    case GoalType.STRATEGIC:
      return 'bg-purple-100 text-purple-800';
    case GoalType.OPERATIONAL:
      return 'bg-blue-100 text-blue-800';
    case GoalType.DEVELOPMENT:
      return 'bg-green-100 text-green-800';
    case GoalType.BEHAVIORAL:
      return 'bg-orange-100 text-orange-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
};

export const formatDate = (dateString: string): string => {
  return new Date(dateString).toLocaleDateString();
};

export const formatDateTime = (dateString: string): string => {
  return new Date(dateString).toLocaleString();
};

export const isDatePast = (dateString: string): boolean => {
  return new Date(dateString) < new Date();
};

export const getDaysUntil = (dateString: string): number => {
  const targetDate = new Date(dateString);
  const today = new Date();
  const diffTime = targetDate.getTime() - today.getTime();
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
};

// ========== PERFORMANCE ENHANCEMENT TYPES (STORY-010) ==========

// Key Result Area (KRA Framework)
export interface KeyResultArea {
  id: string;
  contractId: string;
  name: string;
  description?: string;
  weighting: number;
  sortOrder?: number;
  isActive: boolean;
  createdAt: string;
  updatedAt?: string;
  createdBy?: string;
  goalCount: number;
}

export interface CreateKRARequest {
  contractId: string;
  name: string;
  description?: string;
  weighting: number;
  sortOrder?: number;
}

// Performance Improvement Plan (PIP)
export enum PIPStatus {
  DRAFT = 'DRAFT',
  ACTIVE = 'ACTIVE',
  EXTENDED = 'EXTENDED',
  COMPLETED_SUCCESSFULLY = 'COMPLETED_SUCCESSFULLY',
  COMPLETED_UNSUCCESSFULLY = 'COMPLETED_UNSUCCESSFULLY',
  TERMINATED = 'TERMINATED'
}

export enum PIPMilestoneStatus {
  PENDING = 'PENDING',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  MISSED = 'MISSED'
}

export interface PerformanceImprovementPlan {
  id: string;
  contractId: string;
  employeeId: string;
  employeeName: string;
  managerId: string;
  managerName: string;
  reason: string;
  performanceGaps?: string;
  expectedImprovements?: string;
  supportProvided?: string;
  startDate: string;
  endDate: string;
  originalEndDate?: string;
  extensionReason?: string;
  status: PIPStatus;
  outcomeNotes?: string;
  completedAt?: string;
  completedBy?: string;
  createdAt: string;
  createdBy: string;
  overdue: boolean;
  milestones: PIPMilestone[];
}

export interface PIPMilestone {
  id: string;
  title: string;
  description?: string;
  successCriteria?: string;
  targetDate: string;
  completedDate?: string;
  status: PIPMilestoneStatus;
  managerNotes?: string;
  employeeNotes?: string;
  overdue: boolean;
}

export interface CreatePIPRequest {
  contractId: string;
  employeeId: string;
  employeeName?: string;
  managerId: string;
  managerName?: string;
  reason: string;
  performanceGaps?: string;
  expectedImprovements?: string;
  supportProvided?: string;
  startDate: string;
  endDate: string;
  milestones?: CreatePIPMilestoneRequest[];
}

export interface CreatePIPMilestoneRequest {
  title: string;
  description?: string;
  successCriteria?: string;
  targetDate: string;
  sortOrder?: number;
}

// Calibration Sessions
export enum CalibrationStatus {
  SCHEDULED = 'SCHEDULED',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED'
}

export interface CalibrationSession {
  id: string;
  cycleId: string;
  name: string;
  description?: string;
  department?: string;
  jobLevel?: string;
  facilitatorId: string;
  facilitatorName: string;
  scheduledDate?: string;
  startedAt?: string;
  completedAt?: string;
  status: CalibrationStatus;
  notes?: string;
  distributionTarget?: string;
  createdAt: string;
  createdBy: string;
  ratingCount: number;
}

export interface CreateCalibrationSessionRequest {
  cycleId: string;
  name: string;
  description?: string;
  department?: string;
  jobLevel?: string;
  facilitatorId: string;
  facilitatorName?: string;
  scheduledDate?: string;
  distributionTarget?: string;
}

export interface CalibrationRatingRequest {
  reviewId: string;
  employeeId?: string;
  employeeName?: string;
  calibratedRating: number;
  adjustmentReason?: string;
}

// Self-Assessment
export interface SelfAssessmentRequest {
  reviewId: string;
  assessmentNotes?: string;
  selfRating: number;
  goalScores?: GoalScoreRequest[];
}

export interface GoalScoreRequest {
  goalId: string;
  score: number;
  comment?: string;
}

// Manager Dashboard
export interface ManagerDashboard {
  totalDirectReports: number;
  contractsCompleted: number;
  contractsPending: number;
  reviewsCompleted: number;
  reviewsPending: number;
  reviewsOverdue: number;
  activePIPs: number;
  averageTeamRating: number;
  teamMembers: TeamMemberSummary[];
  ratingDistribution: Record<string, number>;
}

export interface TeamMemberSummary {
  employeeId: string;
  employeeName: string;
  department?: string;
  contractStatus: string;
  reviewStatus?: string;
  selfRating?: number;
  managerRating?: number;
  finalRating?: number;
  hasPIP: boolean;
}

// Performance Analytics
export interface PerformanceAnalytics {
  cycleAnalytics?: CycleAnalytics;
  ratingDistribution?: Record<string, number>;
  departmentAverages?: Record<string, number>;
  completionMetrics?: CompletionMetrics;
  pipAnalytics?: PIPAnalytics;
}

export interface CycleAnalytics {
  cycleId: string;
  cycleName: string;
  totalContracts: number;
  approvedContracts: number;
  pendingContracts: number;
  totalReviews: number;
  completedReviews: number;
  contractCompletionRate: number;
  reviewCompletionRate: number;
}

export interface CompletionMetrics {
  totalEmployees: number;
  selfAssessmentsSubmitted: number;
  managerAssessmentsSubmitted: number;
  calibrationsCompleted: number;
  selfAssessmentRate: number;
  managerAssessmentRate: number;
}

export interface PIPAnalytics {
  totalActive: number;
  completedSuccessfully: number;
  completedUnsuccessfully: number;
  terminated: number;
  overdue: number;
  successRate: number;
}

// Utility functions for new types
export const getPIPStatusColor = (status: PIPStatus): string => {
  switch (status) {
    case PIPStatus.DRAFT:
      return 'bg-gray-100 text-gray-800';
    case PIPStatus.ACTIVE:
      return 'bg-yellow-100 text-yellow-800';
    case PIPStatus.EXTENDED:
      return 'bg-orange-100 text-orange-800';
    case PIPStatus.COMPLETED_SUCCESSFULLY:
      return 'bg-green-100 text-green-800';
    case PIPStatus.COMPLETED_UNSUCCESSFULLY:
      return 'bg-red-100 text-red-800';
    case PIPStatus.TERMINATED:
      return 'bg-red-100 text-red-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
};

export const getCalibrationStatusColor = (status: CalibrationStatus): string => {
  switch (status) {
    case CalibrationStatus.SCHEDULED:
      return 'bg-blue-100 text-blue-800';
    case CalibrationStatus.IN_PROGRESS:
      return 'bg-yellow-100 text-yellow-800';
    case CalibrationStatus.COMPLETED:
      return 'bg-green-100 text-green-800';
    case CalibrationStatus.CANCELLED:
      return 'bg-gray-100 text-gray-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
};

export const getMilestoneStatusColor = (status: PIPMilestoneStatus): string => {
  switch (status) {
    case PIPMilestoneStatus.PENDING:
      return 'bg-gray-100 text-gray-800';
    case PIPMilestoneStatus.IN_PROGRESS:
      return 'bg-blue-100 text-blue-800';
    case PIPMilestoneStatus.COMPLETED:
      return 'bg-green-100 text-green-800';
    case PIPMilestoneStatus.MISSED:
      return 'bg-red-100 text-red-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
};