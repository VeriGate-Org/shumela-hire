import { apiFetch } from '@/lib/api-fetch';

export interface Department {
  id: number;
  name: string;
  code: string;
  description: string | null;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface DepartmentCreateRequest {
  name: string;
  description?: string;
}

export const departmentService = {
  async getAll(activeOnly?: boolean): Promise<Department[]> {
    const params = activeOnly ? '?active=true' : '';
    const response = await apiFetch(`/api/departments${params}`);
    if (!response.ok) return [];
    const result = await response.json();
    return result.content || result.data || result || [];
  },

  async getActiveNames(): Promise<string[]> {
    const response = await apiFetch('/api/departments/names');
    if (!response.ok) return [];
    const result = await response.json();
    return result.data || result || [];
  },

  async getById(id: number): Promise<Department> {
    const response = await apiFetch(`/api/departments/${id}`);
    if (!response.ok) throw new Error('Department not found');
    const result = await response.json();
    return result.data || result;
  },

  async create(data: DepartmentCreateRequest): Promise<Department> {
    const response = await apiFetch('/api/departments', {
      method: 'POST',
      body: JSON.stringify(data),
    });
    if (!response.ok) {
      const error = await response.text();
      throw new Error(error || 'Failed to create department');
    }
    const result = await response.json();
    return result.data || result;
  },

  async update(id: number, data: DepartmentCreateRequest): Promise<Department> {
    const response = await apiFetch(`/api/departments/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
    if (!response.ok) {
      const error = await response.text();
      throw new Error(error || 'Failed to update department');
    }
    const result = await response.json();
    return result.data || result;
  },

  async deactivate(id: number): Promise<Department> {
    const response = await apiFetch(`/api/departments/${id}/deactivate`, {
      method: 'PATCH',
    });
    if (!response.ok) throw new Error('Failed to deactivate department');
    const result = await response.json();
    return result.data || result;
  },

  async activate(id: number): Promise<Department> {
    const response = await apiFetch(`/api/departments/${id}/activate`, {
      method: 'PATCH',
    });
    if (!response.ok) throw new Error('Failed to activate department');
    const result = await response.json();
    return result.data || result;
  },
};
