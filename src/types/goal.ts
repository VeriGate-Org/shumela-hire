// Goal Management Types (STORY-014)

export enum GoalType {
  OKR = 'OKR',
  KPI = 'KPI',
}

export enum GoalStatus {
  DRAFT = 'DRAFT',
  ACTIVE = 'ACTIVE',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
}

export enum OwnerType {
  ORGANIZATION = 'ORGANIZATION',
  DEPARTMENT = 'DEPARTMENT',
  EMPLOYEE = 'EMPLOYEE',
}

export enum GoalPeriod {
  QUARTERLY = 'QUARTERLY',
  SEMI_ANNUAL = 'SEMI_ANNUAL',
  ANNUAL = 'ANNUAL',
  CUSTOM = 'CUSTOM',
}

export enum KeyResultStatus {
  ON_TRACK = 'ON_TRACK',
  AT_RISK = 'AT_RISK',
  OFF_TRACK = 'OFF_TRACK',
  COMPLETED = 'COMPLETED',
}

export interface KeyResult {
  id: string;
  goalId: string;
  metric: string;
  description?: string;
  targetValue: number;
  currentValue: number;
  progressPct: number;
  unitOfMeasure?: string;
  status: KeyResultStatus;
  lastUpdated?: string;
  sortOrder?: number;
  createdAt: string;
  updatedAt?: string;
}

export interface Goal {
  id: string;
  title: string;
  description?: string;
  type: GoalType;
  status: GoalStatus;
  ownerType: OwnerType;
  ownerId: string;
  period: GoalPeriod;
  startDate?: string;
  endDate?: string;
  parentGoalId?: string;
  sortOrder?: number;
  isActive: boolean;
  tenantId: string;
  createdBy?: string;
  createdAt: string;
  updatedAt?: string;
  keyResults: KeyResult[];
}

export interface GoalLink {
  id: string;
  goalId: string;
  reviewCycleId: string;
  reviewCycleName: string;
  weight: number;
  createdBy?: string;
  createdAt: string;
}

// Request types

export interface KeyResultRequest {
  metric: string;
  description?: string;
  targetValue: number;
  currentValue?: number;
  unitOfMeasure?: string;
  status?: KeyResultStatus;
  sortOrder?: number;
}

export interface GoalRequest {
  title: string;
  description?: string;
  type: GoalType;
  ownerType: OwnerType;
  ownerId: string;
  period: GoalPeriod;
  startDate?: string;
  endDate?: string;
  parentGoalId?: string;
  sortOrder?: number;
  keyResults?: KeyResultRequest[];
}

export interface GoalLinkRequest {
  weight: number;
}

export interface ProgressUpdateRequest {
  currentValue: number;
  status?: KeyResultStatus;
}

// Utility functions

export const getGoalStatusColor = (status: GoalStatus): string => {
  switch (status) {
    case GoalStatus.DRAFT:
      return 'bg-gray-100 text-gray-800';
    case GoalStatus.ACTIVE:
      return 'bg-green-100 text-green-800';
    case GoalStatus.COMPLETED:
      return 'bg-blue-100 text-blue-800';
    case GoalStatus.CANCELLED:
      return 'bg-red-100 text-red-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
};

export const getGoalTypeColor = (type: GoalType): string => {
  switch (type) {
    case GoalType.OKR:
      return 'bg-purple-100 text-purple-800';
    case GoalType.KPI:
      return 'bg-indigo-100 text-indigo-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
};

export const getKeyResultStatusColor = (status: KeyResultStatus): string => {
  switch (status) {
    case KeyResultStatus.ON_TRACK:
      return 'bg-green-100 text-green-800';
    case KeyResultStatus.AT_RISK:
      return 'bg-yellow-100 text-yellow-800';
    case KeyResultStatus.OFF_TRACK:
      return 'bg-red-100 text-red-800';
    case KeyResultStatus.COMPLETED:
      return 'bg-blue-100 text-blue-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
};

export const getOwnerTypeLabel = (ownerType: OwnerType): string => {
  switch (ownerType) {
    case OwnerType.ORGANIZATION:
      return 'Organization';
    case OwnerType.DEPARTMENT:
      return 'Department';
    case OwnerType.EMPLOYEE:
      return 'Employee';
    default:
      return ownerType;
  }
};

export const getPeriodLabel = (period: GoalPeriod): string => {
  switch (period) {
    case GoalPeriod.QUARTERLY:
      return 'Quarterly';
    case GoalPeriod.SEMI_ANNUAL:
      return 'Semi-Annual';
    case GoalPeriod.ANNUAL:
      return 'Annual';
    case GoalPeriod.CUSTOM:
      return 'Custom';
    default:
      return period;
  }
};
