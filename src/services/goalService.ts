import { apiFetch } from '@/lib/api-fetch';
import type {
  Goal,
  GoalRequest,
  GoalLink,
  GoalLinkRequest,
  KeyResult,
  KeyResultRequest,
  ProgressUpdateRequest,
  GoalStatus,
} from '@/types/goal';

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

// ==================== Goals ====================

export async function createGoal(data: GoalRequest): Promise<Goal> {
  const response = await apiFetch('/api/goals', {
    method: 'POST',
    body: JSON.stringify(data),
  });
  if (!response.ok) {
    const err = await response.json().catch(() => ({ message: 'Failed to create goal' }));
    throw new Error(err.message || `HTTP ${response.status}`);
  }
  return response.json();
}

export async function getGoals(page = 0, size = 20): Promise<PageResponse<Goal>> {
  const response = await apiFetch(`/api/goals?page=${page}&size=${size}`);
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
  }
  return response.json();
}

export async function getGoal(id: string): Promise<Goal> {
  const response = await apiFetch(`/api/goals/${id}`);
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
  }
  return response.json();
}

export async function updateGoal(id: string, data: GoalRequest): Promise<Goal> {
  const response = await apiFetch(`/api/goals/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  });
  if (!response.ok) {
    const err = await response.json().catch(() => ({ message: 'Failed to update goal' }));
    throw new Error(err.message || `HTTP ${response.status}`);
  }
  return response.json();
}

export async function deleteGoal(id: string): Promise<void> {
  const response = await apiFetch(`/api/goals/${id}`, { method: 'DELETE' });
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
  }
}

export async function activateGoal(id: string): Promise<Goal> {
  const response = await apiFetch(`/api/goals/${id}/activate`, { method: 'POST' });
  if (!response.ok) {
    const err = await response.json().catch(() => ({ message: 'Failed to activate goal' }));
    throw new Error(err.message || `HTTP ${response.status}`);
  }
  return response.json();
}

export async function completeGoal(id: string): Promise<Goal> {
  const response = await apiFetch(`/api/goals/${id}/complete`, { method: 'POST' });
  if (!response.ok) {
    const err = await response.json().catch(() => ({ message: 'Failed to complete goal' }));
    throw new Error(err.message || `HTTP ${response.status}`);
  }
  return response.json();
}

export async function cancelGoal(id: string): Promise<Goal> {
  const response = await apiFetch(`/api/goals/${id}/cancel`, { method: 'POST' });
  if (!response.ok) {
    const err = await response.json().catch(() => ({ message: 'Failed to cancel goal' }));
    throw new Error(err.message || `HTTP ${response.status}`);
  }
  return response.json();
}

export async function getGoalsByOwner(ownerType: string, ownerId: string): Promise<Goal[]> {
  const response = await apiFetch(`/api/goals/owner/${ownerType}/${ownerId}`);
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
  }
  return response.json();
}

export async function getGoalsByStatus(status: GoalStatus): Promise<Goal[]> {
  const response = await apiFetch(`/api/goals/status/${status}`);
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
  }
  return response.json();
}

export async function getChildGoals(parentGoalId: string): Promise<Goal[]> {
  const response = await apiFetch(`/api/goals/${parentGoalId}/children`);
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
  }
  return response.json();
}

export async function getTopLevelGoals(): Promise<Goal[]> {
  const response = await apiFetch('/api/goals/top-level');
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
  }
  return response.json();
}

// ==================== Key Results ====================

export async function getKeyResults(goalId: string): Promise<KeyResult[]> {
  const response = await apiFetch(`/api/goals/${goalId}/key-results`);
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
  }
  return response.json();
}

export async function addKeyResult(goalId: string, data: KeyResultRequest): Promise<KeyResult> {
  const response = await apiFetch(`/api/goals/${goalId}/key-results`, {
    method: 'POST',
    body: JSON.stringify(data),
  });
  if (!response.ok) {
    const err = await response.json().catch(() => ({ message: 'Failed to add key result' }));
    throw new Error(err.message || `HTTP ${response.status}`);
  }
  return response.json();
}

export async function updateKeyResultProgress(krId: string, data: ProgressUpdateRequest): Promise<KeyResult> {
  const response = await apiFetch(`/api/goals/key-results/${krId}/progress`, {
    method: 'PATCH',
    body: JSON.stringify(data),
  });
  if (!response.ok) {
    const err = await response.json().catch(() => ({ message: 'Failed to update progress' }));
    throw new Error(err.message || `HTTP ${response.status}`);
  }
  return response.json();
}

export async function deleteKeyResult(krId: string): Promise<void> {
  const response = await apiFetch(`/api/goals/key-results/${krId}`, { method: 'DELETE' });
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
  }
}

// ==================== Goal Links ====================

export async function linkGoalToCycle(goalId: string, cycleId: string, data: GoalLinkRequest): Promise<GoalLink> {
  const response = await apiFetch(`/api/goals/${goalId}/link-cycle/${cycleId}`, {
    method: 'POST',
    body: JSON.stringify(data),
  });
  if (!response.ok) {
    const err = await response.json().catch(() => ({ message: 'Failed to link goal to cycle' }));
    throw new Error(err.message || `HTTP ${response.status}`);
  }
  return response.json();
}

export async function unlinkGoalFromCycle(goalId: string, cycleId: string): Promise<void> {
  const response = await apiFetch(`/api/goals/${goalId}/link-cycle/${cycleId}`, { method: 'DELETE' });
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
  }
}

export async function getGoalLinks(goalId: string): Promise<GoalLink[]> {
  const response = await apiFetch(`/api/goals/${goalId}/links`);
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
  }
  return response.json();
}

export async function getGoalsLinkedToCycle(cycleId: string): Promise<GoalLink[]> {
  const response = await apiFetch(`/api/goals/linked-to-cycle/${cycleId}`);
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
  }
  return response.json();
}
