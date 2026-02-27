'use client';

import React, { useState } from 'react';
import GoalList from '@/components/goals/GoalList';
import GoalForm from '@/components/goals/GoalForm';
import KeyResultEditor from '@/components/goals/KeyResultEditor';
import {
  Goal,
  getGoalStatusColor,
  getGoalTypeColor,
  getOwnerTypeLabel,
  getPeriodLabel,
} from '@/types/goal';

export default function GoalsPage() {
  const [selectedGoal, setSelectedGoal] = useState<Goal | null>(null);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  const handleGoalSelect = (goal: Goal) => {
    setSelectedGoal(goal);
    setShowCreateForm(false);
  };

  const handleCreateSuccess = () => {
    setShowCreateForm(false);
    setRefreshTrigger((t) => t + 1);
  };

  const handleCreateGoal = () => {
    setSelectedGoal(null);
    setShowCreateForm(true);
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white shadow">
        <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Goal Management</h1>
              <p className="mt-1 text-sm text-gray-600">
                Manage OKRs and KPIs across the organisation
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Goal List */}
          <div className="lg:col-span-1">
            <GoalList
              onGoalSelect={handleGoalSelect}
              onCreateGoal={handleCreateGoal}
              refreshTrigger={refreshTrigger}
            />
          </div>

          {/* Detail / Form Panel */}
          <div className="lg:col-span-2">
            {showCreateForm && (
              <div className="bg-white shadow rounded-sm">
                <div className="px-4 py-5 sm:p-6 border-b border-gray-200">
                  <h2 className="text-lg font-medium text-gray-900">Create New Goal</h2>
                </div>
                <div className="px-4 py-5 sm:p-6">
                  <GoalForm
                    onSuccess={handleCreateSuccess}
                    onCancel={() => setShowCreateForm(false)}
                  />
                </div>
              </div>
            )}

            {selectedGoal && !showCreateForm && (
              <div className="space-y-6">
                {/* Goal Detail Card */}
                <div className="bg-white shadow rounded-sm">
                  <div className="px-4 py-5 sm:p-6 border-b border-gray-200">
                    <div className="flex items-start justify-between">
                      <div>
                        <div className="flex items-center gap-2 mb-1">
                          <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${getGoalTypeColor(selectedGoal.type)}`}>
                            {selectedGoal.type}
                          </span>
                          <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${getGoalStatusColor(selectedGoal.status)}`}>
                            {selectedGoal.status}
                          </span>
                        </div>
                        <h2 className="text-xl font-semibold text-gray-900">{selectedGoal.title}</h2>
                      </div>
                      <button
                        onClick={() => setSelectedGoal(null)}
                        className="text-gray-400 hover:text-gray-600 text-sm"
                      >
                        ✕
                      </button>
                    </div>
                  </div>
                  <div className="px-4 py-5 sm:p-6">
                    {selectedGoal.description && (
                      <p className="text-sm text-gray-700 mb-4">{selectedGoal.description}</p>
                    )}
                    <dl className="grid grid-cols-2 gap-x-4 gap-y-3 text-sm">
                      <div>
                        <dt className="text-gray-500">Owner</dt>
                        <dd className="font-medium">{getOwnerTypeLabel(selectedGoal.ownerType)}: {selectedGoal.ownerId}</dd>
                      </div>
                      <div>
                        <dt className="text-gray-500">Period</dt>
                        <dd className="font-medium">{getPeriodLabel(selectedGoal.period)}</dd>
                      </div>
                      {selectedGoal.startDate && (
                        <div>
                          <dt className="text-gray-500">Start Date</dt>
                          <dd className="font-medium">{new Date(selectedGoal.startDate).toLocaleDateString()}</dd>
                        </div>
                      )}
                      {selectedGoal.endDate && (
                        <div>
                          <dt className="text-gray-500">End Date</dt>
                          <dd className="font-medium">{new Date(selectedGoal.endDate).toLocaleDateString()}</dd>
                        </div>
                      )}
                      <div>
                        <dt className="text-gray-500">Created</dt>
                        <dd className="font-medium">{new Date(selectedGoal.createdAt).toLocaleDateString()}</dd>
                      </div>
                      {selectedGoal.createdBy && (
                        <div>
                          <dt className="text-gray-500">Created By</dt>
                          <dd className="font-medium">{selectedGoal.createdBy}</dd>
                        </div>
                      )}
                    </dl>
                  </div>
                </div>

                {/* Key Results */}
                <div className="bg-white shadow rounded-sm">
                  <div className="px-4 py-5 sm:p-6 border-b border-gray-200">
                    <h3 className="text-lg font-medium text-gray-900">Key Results</h3>
                    <p className="text-sm text-gray-500 mt-1">
                      Track measurable outcomes for this goal
                    </p>
                  </div>
                  <div className="px-4 py-5 sm:p-6">
                    <KeyResultEditor goalId={selectedGoal.id} />
                  </div>
                </div>
              </div>
            )}

            {!selectedGoal && !showCreateForm && (
              <div className="bg-white shadow rounded-sm flex items-center justify-center" style={{ minHeight: 200 }}>
                <p className="text-gray-400 text-sm">Select a goal to view details, or create a new one.</p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
