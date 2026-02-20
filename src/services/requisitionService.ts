import { RequisitionData, RequisitionStatus } from '../types/workflow';
import { apiFetch } from '@/lib/api-fetch';

/**
 * Requisition Service
 * Handles business logic for requisition management
 */
export class RequisitionService {
  async createRequisition(
    requisitionData: {
      jobTitle: string;
      department: string;
      location: string;
      employmentType: string;
      salaryMin?: number;
      salaryMax?: number;
      description: string;
    },
    createdBy: string
  ): Promise<RequisitionData> {
    const response = await apiFetch('/api/requisitions', {
      method: 'POST',
      body: JSON.stringify({ ...requisitionData, createdBy }),
    });
    if (!response.ok) throw new Error('Failed to create requisition');
    const result = await response.json();
    return result.data || result;
  }

  async getRequisition(id: string): Promise<RequisitionData | null> {
    const response = await apiFetch(`/api/requisitions/${id}`);
    if (!response.ok) return null;
    const result = await response.json();
    return result.data || result;
  }

  async getAllRequisitions(): Promise<RequisitionData[]> {
    const response = await apiFetch('/api/requisitions');
    if (!response.ok) return [];
    const result = await response.json();
    return result.data || result || [];
  }

  async getRequisitionsByStatus(status: RequisitionStatus): Promise<RequisitionData[]> {
    const response = await apiFetch(`/api/requisitions?status=${status}`);
    if (!response.ok) return [];
    const result = await response.json();
    return result.data || result || [];
  }

  async getPendingRequisitionsForRole(role: string): Promise<RequisitionData[]> {
    const response = await apiFetch(`/api/requisitions?role=${role}`);
    if (!response.ok) return [];
    const result = await response.json();
    return result.data || result || [];
  }

  async updateRequisition(
    id: string,
    updates: Partial<RequisitionData>
  ): Promise<RequisitionData | null> {
    const response = await apiFetch(`/api/requisitions/${id}`, {
      method: 'PUT',
      body: JSON.stringify(updates),
    });
    if (!response.ok) return null;
    const result = await response.json();
    return result.data || result;
  }

  async deleteRequisition(id: string): Promise<boolean> {
    const response = await apiFetch(`/api/requisitions/${id}`, { method: 'DELETE' });
    return response.ok;
  }
}

// Export singleton instance
export const requisitionService = new RequisitionService();
