import { apiFetch } from '@/lib/api-fetch';
import type {
  Employee,
  EmployeeCreateForm,
  EmployeeDocument,
  EmployeeFilterParams,
  EmploymentEvent,
  EmploymentEventForm,
  CustomField,
  CustomFieldForm,
  PageResponse,
} from '@/types/employee';

function buildQueryString(params: Record<string, string | number | boolean | undefined>): string {
  const parts: string[] = [];
  for (const [key, val] of Object.entries(params)) {
    if (val !== undefined && val !== '') {
      parts.push(`${encodeURIComponent(key)}=${encodeURIComponent(String(val))}`);
    }
  }
  return parts.length ? `?${parts.join('&')}` : '';
}

// ==================== Employee CRUD ====================

export async function createEmployee(data: EmployeeCreateForm): Promise<Employee> {
  const response = await apiFetch('/api/employees', {
    method: 'POST',
    body: JSON.stringify(data),
  });
  if (!response.ok) {
    const err = await response.json().catch(() => ({ message: 'Failed to create employee' }));
    throw new Error(err.message || `HTTP ${response.status}`);
  }
  return response.json();
}

export async function updateEmployee(id: number, data: EmployeeCreateForm): Promise<Employee> {
  const response = await apiFetch(`/api/employees/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  });
  if (!response.ok) {
    const err = await response.json().catch(() => ({ message: 'Failed to update employee' }));
    throw new Error(err.message || `HTTP ${response.status}`);
  }
  return response.json();
}

export async function getEmployee(id: number): Promise<Employee> {
  const response = await apiFetch(`/api/employees/${id}`);
  if (!response.ok) throw new Error('Employee not found');
  return response.json();
}

export async function searchEmployees(params: EmployeeFilterParams = {}): Promise<PageResponse<Employee>> {
  const qs = buildQueryString({
    search: params.search,
    page: params.page,
    size: params.size,
    sort: params.sort,
    direction: params.direction,
  });
  const response = await apiFetch(`/api/employees${qs}`);
  if (!response.ok) throw new Error('Failed to search employees');
  return response.json();
}

export async function filterEmployees(params: EmployeeFilterParams = {}): Promise<PageResponse<Employee>> {
  const qs = buildQueryString({
    department: params.department,
    status: params.status,
    jobTitle: params.jobTitle,
    location: params.location,
    page: params.page,
    size: params.size,
  });
  const response = await apiFetch(`/api/employees/filter${qs}`);
  if (!response.ok) throw new Error('Failed to filter employees');
  return response.json();
}

export async function getDirectory(page = 0, size = 20): Promise<PageResponse<Employee>> {
  const response = await apiFetch(`/api/employees/directory?page=${page}&size=${size}`);
  if (!response.ok) throw new Error('Failed to load directory');
  return response.json();
}

export async function getDirectReports(managerId: number): Promise<Employee[]> {
  const response = await apiFetch(`/api/employees/${managerId}/direct-reports`);
  if (!response.ok) throw new Error('Failed to load direct reports');
  return response.json();
}

export async function updateEmployeeStatus(
  id: number,
  status: string,
  reason?: string,
): Promise<Employee> {
  const qs = buildQueryString({ status, reason });
  const response = await apiFetch(`/api/employees/${id}/status${qs}`, { method: 'PATCH' });
  if (!response.ok) {
    const err = await response.json().catch(() => ({ message: 'Failed to update status' }));
    throw new Error(err.message || `HTTP ${response.status}`);
  }
  return response.json();
}

// ==================== Lookup Data ====================

export async function getDepartments(): Promise<string[]> {
  const response = await apiFetch('/api/employees/departments');
  if (!response.ok) return [];
  return response.json();
}

export async function getLocations(): Promise<string[]> {
  const response = await apiFetch('/api/employees/locations');
  if (!response.ok) return [];
  return response.json();
}

export async function getJobTitles(): Promise<string[]> {
  const response = await apiFetch('/api/employees/job-titles');
  if (!response.ok) return [];
  return response.json();
}

export async function getDepartmentCounts(): Promise<Record<string, number>> {
  const response = await apiFetch('/api/employees/department-counts');
  if (!response.ok) return {};
  return response.json();
}

// ==================== Documents ====================

export async function getDocuments(employeeId: number): Promise<EmployeeDocument[]> {
  const response = await apiFetch(`/api/employees/${employeeId}/documents`);
  if (!response.ok) throw new Error('Failed to load documents');
  return response.json();
}

export async function uploadDocument(
  employeeId: number,
  type: string,
  title: string,
  file: File,
  description?: string,
  expiryDate?: string,
): Promise<EmployeeDocument> {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('type', type);
  formData.append('title', title);
  if (description) formData.append('description', description);
  if (expiryDate) formData.append('expiryDate', expiryDate);

  const response = await apiFetch(`/api/employees/${employeeId}/documents`, {
    method: 'POST',
    body: formData,
    headers: {}, // Let browser set Content-Type with boundary
  });
  if (!response.ok) {
    const err = await response.json().catch(() => ({ message: 'Upload failed' }));
    throw new Error(err.message || `HTTP ${response.status}`);
  }
  return response.json();
}

export async function deleteDocument(employeeId: number, documentId: number): Promise<void> {
  const response = await apiFetch(`/api/employees/${employeeId}/documents/${documentId}`, {
    method: 'DELETE',
  });
  if (!response.ok) throw new Error('Failed to delete document');
}

export async function getExpiringDocuments(daysAhead = 30): Promise<EmployeeDocument[]> {
  const response = await apiFetch(`/api/employees/documents/expiring?daysAhead=${daysAhead}`);
  if (!response.ok) return [];
  return response.json();
}

// ==================== Employment Events ====================

export async function getEmployeeEvents(employeeId: number): Promise<EmploymentEvent[]> {
  const response = await apiFetch(`/api/employees/${employeeId}/events`);
  if (!response.ok) throw new Error('Failed to load events');
  return response.json();
}

export async function createEmploymentEvent(
  employeeId: number,
  data: EmploymentEventForm,
): Promise<EmploymentEvent> {
  const response = await apiFetch(`/api/employees/${employeeId}/events`, {
    method: 'POST',
    body: JSON.stringify({ ...data, employeeId }),
  });
  if (!response.ok) {
    const err = await response.json().catch(() => ({ message: 'Failed to create event' }));
    throw new Error(err.message || `HTTP ${response.status}`);
  }
  return response.json();
}

// ==================== Custom Fields ====================

export async function getCustomFields(entityType?: string): Promise<CustomField[]> {
  const qs = entityType ? `?entityType=${entityType}` : '';
  const response = await apiFetch(`/api/custom-fields${qs}`);
  if (!response.ok) return [];
  return response.json();
}

export async function createCustomField(data: CustomFieldForm): Promise<CustomField> {
  const response = await apiFetch('/api/custom-fields', {
    method: 'POST',
    body: JSON.stringify(data),
  });
  if (!response.ok) {
    const err = await response.json().catch(() => ({ message: 'Failed to create custom field' }));
    throw new Error(err.message || `HTTP ${response.status}`);
  }
  return response.json();
}

export async function updateCustomField(id: number, data: CustomFieldForm): Promise<CustomField> {
  const response = await apiFetch(`/api/custom-fields/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  });
  if (!response.ok) {
    const err = await response.json().catch(() => ({ message: 'Failed to update custom field' }));
    throw new Error(err.message || `HTTP ${response.status}`);
  }
  return response.json();
}

export async function deleteCustomField(id: number): Promise<void> {
  const response = await apiFetch(`/api/custom-fields/${id}`, { method: 'DELETE' });
  if (!response.ok) throw new Error('Failed to delete custom field');
}

// ==================== Applicant Conversion ====================

export async function convertApplicant(applicantId: number, employeeData: EmployeeCreateForm): Promise<Employee> {
  const response = await apiFetch('/api/employees/convert-applicant', {
    method: 'POST',
    body: JSON.stringify({ applicantId, ...employeeData }),
  });
  if (!response.ok) {
    const err = await response.json().catch(() => ({ message: 'Conversion failed' }));
    throw new Error(err.message || `HTTP ${response.status}`);
  }
  return response.json();
}
