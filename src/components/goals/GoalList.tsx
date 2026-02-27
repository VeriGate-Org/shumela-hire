'use client';

import React, { useEffect, useState } from 'react';
import {
  Goal,
  GoalStatus,
  GoalType,
  getGoalStatusColor,
  getGoalTypeColor,
  getPeriodLabel,
  getOwnerTypeLabel,
} from '@/types/goal';
import { getGoals, activateGoal, completeGoal, cancelGoal, deleteGoal } from '@/services/goalService';

interface GoalListProps {
  onGoalSelect?: (goal: Goal) => void;
  onCreateGoal?: () => void;
  refreshTrigger?: number;
}

export default function GoalList({ onGoalSelect, onCreateGoal, refreshTrigger }: GoalListProps) {
  const [goals, setGoals] = useState<Goal[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [statusFilter, setStatusFilter] = useState<GoalStatus | ''>('');
  const [typeFilter, setTypeFilter] = useState<GoalType | ''>('');

  const loadGoals = async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await getGoals(page, 20);
      setGoals(result.content);
      setTotalPages(result.totalPages);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load goals');
    } finally {
      setLoading(false);
    }
  };

  // eslint-disable-next-line react-hooks/exhaustive-deps
  useEffect(() => { loadGoals(); }, [page, refreshTrigger]);

  const handleActivate = async (id: string, e: React.MouseEvent) => {
    e.stopPropagation();
    try {
      await activateGoal(id);
      loadGoals();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to activate goal');
    }
  };

  const handleComplete = async (id: string, e: React.MouseEvent) => {
    e.stopPropagation();
    try {
      await completeGoal(id);
      loadGoals();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to complete goal');
    }
  };

  const handleCancel = async (id: string, e: React.MouseEvent) => {
    e.stopPropagation();
    if (!confirm('Are you sure you want to cancel this goal?')) return;
    try {
      await cancelGoal(id);
      loadGoals();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to cancel goal');
    }
  };

  const handleDelete = async (id: string, e: React.MouseEvent) => {
    e.stopPropagation();
    if (!confirm('Are you sure you want to delete this goal?')) return;
    try {
      await deleteGoal(id);
      loadGoals();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to delete goal');
    }
  };

  const filteredGoals = goals.filter((g) => {
    if (statusFilter && g.status !== statusFilter) return false;
    if (typeFilter && g.type !== typeFilter) return false;
    return true;
  });

  return (
    <div className="bg-white shadow rounded-sm">
      {/* Header */}
      <div className="px-4 py-5 sm:p-6 border-b border-gray-200">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-medium text-gray-900">Goals</h2>
          {onCreateGoal && (
            <button
              onClick={onCreateGoal}
              className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-full text-white bg-blue-600 hover:bg-blue-700"
            >
              + New Goal
            </button>
          )}
        </div>

        {/* Filters */}
        <div className="mt-4 flex gap-3">
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value as GoalStatus | '')}
            className="text-sm border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="">All Statuses</option>
            {Object.values(GoalStatus).map((s) => (
              <option key={s} value={s}>{s}</option>
            ))}
          </select>
          <select
            value={typeFilter}
            onChange={(e) => setTypeFilter(e.target.value as GoalType | '')}
            className="text-sm border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="">All Types</option>
            {Object.values(GoalType).map((t) => (
              <option key={t} value={t}>{t}</option>
            ))}
          </select>
        </div>
      </div>

      {/* Content */}
      <div className="divide-y divide-gray-200">
        {loading && (
          <div className="px-4 py-8 text-center text-gray-500">Loading goals...</div>
        )}
        {error && (
          <div className="px-4 py-4 text-center text-red-600">{error}</div>
        )}
        {!loading && filteredGoals.length === 0 && (
          <div className="px-4 py-8 text-center text-gray-500">No goals found.</div>
        )}
        {!loading && filteredGoals.map((goal) => (
          <div
            key={goal.id}
            onClick={() => onGoalSelect?.(goal)}
            className="px-4 py-4 hover:bg-gray-50 cursor-pointer"
          >
            <div className="flex items-start justify-between">
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2 mb-1">
                  <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${getGoalTypeColor(goal.type)}`}>
                    {goal.type}
                  </span>
                  <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${getGoalStatusColor(goal.status)}`}>
                    {goal.status}
                  </span>
                  {goal.parentGoalId && (
                    <span className="text-xs text-gray-400">↳ Child goal</span>
                  )}
                </div>
                <p className="text-sm font-medium text-gray-900 truncate">{goal.title}</p>
                <p className="text-xs text-gray-500 mt-1">
                  {getOwnerTypeLabel(goal.ownerType)} · {getPeriodLabel(goal.period)}
                  {goal.keyResults?.length > 0 && (
                    <span> · {goal.keyResults.length} key result{goal.keyResults.length !== 1 ? 's' : ''}</span>
                  )}
                </p>
              </div>
              <div className="ml-4 flex items-center gap-1 flex-shrink-0">
                {goal.status === GoalStatus.DRAFT && (
                  <button
                    onClick={(e) => handleActivate(goal.id, e)}
                    className="text-xs px-2 py-1 bg-green-100 text-green-700 rounded hover:bg-green-200"
                  >
                    Activate
                  </button>
                )}
                {goal.status === GoalStatus.ACTIVE && (
                  <button
                    onClick={(e) => handleComplete(goal.id, e)}
                    className="text-xs px-2 py-1 bg-blue-100 text-blue-700 rounded hover:bg-blue-200"
                  >
                    Complete
                  </button>
                )}
                {(goal.status === GoalStatus.DRAFT || goal.status === GoalStatus.ACTIVE) && (
                  <button
                    onClick={(e) => handleCancel(goal.id, e)}
                    className="text-xs px-2 py-1 bg-yellow-100 text-yellow-700 rounded hover:bg-yellow-200"
                  >
                    Cancel
                  </button>
                )}
                <button
                  onClick={(e) => handleDelete(goal.id, e)}
                  className="text-xs px-2 py-1 bg-red-100 text-red-700 rounded hover:bg-red-200"
                >
                  Delete
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="px-4 py-3 border-t border-gray-200 flex items-center justify-between">
          <button
            onClick={() => setPage((p) => Math.max(0, p - 1))}
            disabled={page === 0}
            className="text-sm px-3 py-1 border rounded disabled:opacity-50"
          >
            Previous
          </button>
          <span className="text-sm text-gray-600">
            Page {page + 1} of {totalPages}
          </span>
          <button
            onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
            disabled={page >= totalPages - 1}
            className="text-sm px-3 py-1 border rounded disabled:opacity-50"
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
}
