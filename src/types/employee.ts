// Employee module TypeScript types — mirrors backend DTOs

export type EmployeeStatus =
  | 'ACTIVE'
  | 'PROBATION'
  | 'SUSPENDED'
  | 'TERMINATED'
  | 'RESIGNED'
  | 'RETIRED';

export const EMPLOYEE_STATUS_LABELS: Record<EmployeeStatus, string> = {
  ACTIVE: 'Active',
  PROBATION: 'Probation',
  SUSPENDED: 'Suspended',
  TERMINATED: 'Terminated',
  RESIGNED: 'Resigned',
  RETIRED: 'Retired',
};

export type EmploymentEventType =
  | 'HIRE'
  | 'PROMOTION'
  | 'TRANSFER'
  | 'DEMOTION'
  | 'SUSPENSION'
  | 'REINSTATEMENT'
  | 'RESIGNATION'
  | 'DISMISSAL'
  | 'RETIREMENT'
  | 'CONTRACT_END';

export const EVENT_TYPE_LABELS: Record<EmploymentEventType, string> = {
  HIRE: 'Hire',
  PROMOTION: 'Promotion',
  TRANSFER: 'Transfer',
  DEMOTION: 'Demotion',
  SUSPENSION: 'Suspension',
  REINSTATEMENT: 'Reinstatement',
  RESIGNATION: 'Resignation',
  DISMISSAL: 'Dismissal',
  RETIREMENT: 'Retirement',
  CONTRACT_END: 'Contract End',
};

export type EmployeeDocumentType =
  | 'ID_DOCUMENT'
  | 'PASSPORT'
  | 'WORK_PERMIT'
  | 'TAX_CERTIFICATE'
  | 'QUALIFICATION'
  | 'CONTRACT'
  | 'OFFER_LETTER'
  | 'DISCIPLINARY'
  | 'MEDICAL'
  | 'TRAINING_CERTIFICATE'
  | 'PERFORMANCE_REVIEW'
  | 'OTHER';

export const DOCUMENT_TYPE_LABELS: Record<EmployeeDocumentType, string> = {
  ID_DOCUMENT: 'ID Document',
  PASSPORT: 'Passport',
  WORK_PERMIT: 'Work Permit',
  TAX_CERTIFICATE: 'Tax Certificate',
  QUALIFICATION: 'Qualification',
  CONTRACT: 'Contract',
  OFFER_LETTER: 'Offer Letter',
  DISCIPLINARY: 'Disciplinary',
  MEDICAL: 'Medical',
  TRAINING_CERTIFICATE: 'Training Certificate',
  PERFORMANCE_REVIEW: 'Performance Review',
  OTHER: 'Other',
};

export type CustomFieldEntityType = 'EMPLOYEE' | 'EMPLOYEE_DOCUMENT' | 'EMPLOYMENT_EVENT';

export type CustomFieldDataType = 'TEXT' | 'NUMBER' | 'DATE' | 'BOOLEAN' | 'SELECT' | 'MULTI_SELECT';

export interface Employee {
  id: number;
  employeeNumber: string;
  title: string | null;
  firstName: string;
  lastName: string;
  preferredName: string | null;
  fullName: string;
  displayName: string;
  email: string;
  personalEmail: string | null;
  phone: string | null;
  mobilePhone: string | null;
  dateOfBirth: string | null;
  gender: string | null;
  race: string | null;
  disabilityStatus: string | null;
  citizenshipStatus: string | null;
  nationality: string | null;
  maritalStatus: string | null;
  bankName: string | null;
  bankBranchCode: string | null;
  physicalAddress: string | null;
  postalAddress: string | null;
  city: string | null;
  province: string | null;
  postalCode: string | null;
  country: string | null;
  status: EmployeeStatus;
  department: string | null;
  division: string | null;
  jobTitle: string | null;
  jobGrade: string | null;
  employmentType: string | null;
  hireDate: string | null;
  probationEndDate: string | null;
  terminationDate: string | null;
  terminationReason: string | null;
  contractEndDate: string | null;
  reportingManagerId: number | null;
  reportingManagerName: string | null;
  costCentre: string | null;
  location: string | null;
  site: string | null;
  applicantId: number | null;
  profilePhotoUrl: string | null;
  emergencyContactName: string | null;
  emergencyContactPhone: string | null;
  emergencyContactRelationship: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface EmployeeDocument {
  id: number;
  employeeId: number;
  documentType: EmployeeDocumentType;
  title: string;
  description: string | null;
  filename: string;
  fileUrl: string;
  fileSize: number;
  contentType: string;
  version: number;
  expiryDate: string | null;
  isActive: boolean;
  isExpired: boolean;
  uploadedBy: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface EmploymentEvent {
  id: number;
  employeeId: number;
  employeeName: string;
  eventType: EmploymentEventType;
  eventDate: string;
  effectiveDate: string | null;
  description: string | null;
  notes: string | null;
  previousDepartment: string | null;
  newDepartment: string | null;
  previousJobTitle: string | null;
  newJobTitle: string | null;
  previousJobGrade: string | null;
  newJobGrade: string | null;
  previousReportingManagerId: number | null;
  newReportingManagerId: number | null;
  previousLocation: string | null;
  newLocation: string | null;
  recordedBy: string | null;
  createdAt: string;
}

export interface CustomField {
  id: number;
  fieldName: string;
  fieldLabel: string;
  entityType: CustomFieldEntityType;
  dataType: CustomFieldDataType;
  isRequired: boolean;
  isActive: boolean;
  displayOrder: number;
  options: string | null;
  defaultValue: string | null;
  validationRegex: string | null;
  helpText: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CustomFieldValue {
  id: number;
  customFieldId: number;
  entityId: number;
  value: string;
}

// Form types for create/update operations
export interface EmployeeCreateForm {
  firstName: string;
  lastName: string;
  title: string;
  preferredName: string;
  email: string;
  personalEmail: string;
  phone: string;
  mobilePhone: string;
  dateOfBirth: string;
  gender: string;
  race: string;
  disabilityStatus: string;
  citizenshipStatus: string;
  nationality: string;
  maritalStatus: string;
  idNumber: string;
  taxNumber: string;
  bankAccountNumber: string;
  bankName: string;
  bankBranchCode: string;
  physicalAddress: string;
  postalAddress: string;
  city: string;
  province: string;
  postalCode: string;
  country: string;
  department: string;
  division: string;
  jobTitle: string;
  jobGrade: string;
  employmentType: string;
  hireDate: string;
  probationEndDate: string;
  contractEndDate: string;
  reportingManagerId: string;
  costCentre: string;
  location: string;
  site: string;
  emergencyContactName: string;
  emergencyContactPhone: string;
  emergencyContactRelationship: string;
  demographicsConsent: boolean;
}

export interface EmploymentEventForm {
  eventType: EmploymentEventType | '';
  eventDate: string;
  effectiveDate: string;
  description: string;
  notes: string;
  previousDepartment: string;
  newDepartment: string;
  previousJobTitle: string;
  newJobTitle: string;
  previousJobGrade: string;
  newJobGrade: string;
  previousReportingManagerId: string;
  newReportingManagerId: string;
  previousLocation: string;
  newLocation: string;
}

export interface CustomFieldForm {
  fieldName: string;
  fieldLabel: string;
  entityType: CustomFieldEntityType | '';
  dataType: CustomFieldDataType | '';
  isRequired: boolean;
  displayOrder: number;
  options: string;
  defaultValue: string;
  validationRegex: string;
  helpText: string;
}

export interface EmployeeFilterParams {
  search?: string;
  department?: string;
  status?: EmployeeStatus;
  jobTitle?: string;
  location?: string;
  page?: number;
  size?: number;
  sort?: string;
  direction?: 'asc' | 'desc';
}

// Spring Data Page response shape
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}
