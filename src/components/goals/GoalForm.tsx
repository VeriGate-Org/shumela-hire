'use client';

import React, { useState } from 'react';
import {
  GoalType,
  GoalPeriod,
  OwnerType,
  GoalRequest,
  KeyResultRequest,
} from '@/types/goal';
import { createGoal } from '@/services/goalService';

interface GoalFormProps {
  onSuccess?: () => void;
  onCancel?: () => void;
  parentGoalId?: string;
}

const emptyKeyResult = (): KeyResultRequest => ({
  metric: '',
  targetValue: 0,
  currentValue: 0,
  unitOfMeasure: '',
});

export default function GoalForm({ onSuccess, onCancel, parentGoalId }: GoalFormProps) {
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [keyResults, setKeyResults] = useState<KeyResultRequest[]>([]);

  const [form, setForm] = useState<GoalRequest>({
    title: '',
    description: '',
    type: GoalType.OKR,
    ownerType: OwnerType.ORGANIZATION,
    ownerId: '',
    period: GoalPeriod.ANNUAL,
    startDate: '',
    endDate: '',
    parentGoalId: parentGoalId ? parentGoalId : undefined,
    keyResults: [],
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value || undefined }));
  };

  const handleAddKeyResult = () => {
    setKeyResults((prev) => [...prev, emptyKeyResult()]);
  };

  const handleRemoveKeyResult = (index: number) => {
    setKeyResults((prev) => prev.filter((_, i) => i !== index));
  };

  const handleKeyResultChange = (
    index: number,
    field: keyof KeyResultRequest,
    value: string | number
  ) => {
    setKeyResults((prev) => {
      const updated = [...prev];
      updated[index] = { ...updated[index], [field]: value };
      return updated;
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSubmitting(true);

    try {
      const payload: GoalRequest = {
        ...form,
        keyResults: keyResults.filter((kr) => kr.metric.trim() !== ''),
      };
      await createGoal(payload);
      onSuccess?.();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to create goal');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {error && (
        <div className="bg-red-50 border border-red-200 rounded p-3 text-sm text-red-700">
          {error}
        </div>
      )}

      {/* Basic Info */}
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <div className="sm:col-span-2">
          <label className="block text-sm font-medium text-gray-700">Title *</label>
          <input
            name="title"
            type="text"
            required
            value={form.title}
            onChange={handleChange}
            className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
          />
        </div>

        <div className="sm:col-span-2">
          <label className="block text-sm font-medium text-gray-700">Description</label>
          <textarea
            name="description"
            rows={3}
            value={form.description || ''}
            onChange={handleChange}
            className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">Type *</label>
          <select
            name="type"
            value={form.type}
            onChange={handleChange}
            className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
          >
            {Object.values(GoalType).map((t) => (
              <option key={t} value={t}>{t}</option>
            ))}
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">Period *</label>
          <select
            name="period"
            value={form.period}
            onChange={handleChange}
            className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
          >
            {Object.values(GoalPeriod).map((p) => (
              <option key={p} value={p}>{p}</option>
            ))}
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">Owner Type *</label>
          <select
            name="ownerType"
            value={form.ownerType}
            onChange={handleChange}
            className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
          >
            {Object.values(OwnerType).map((o) => (
              <option key={o} value={o}>{o}</option>
            ))}
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">Owner ID *</label>
          <input
            name="ownerId"
            type="text"
            required
            value={form.ownerId}
            onChange={handleChange}
            placeholder="e.g. org-001, dept-hr, emp-123"
            className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">Start Date</label>
          <input
            name="startDate"
            type="date"
            value={form.startDate || ''}
            onChange={handleChange}
            className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">End Date</label>
          <input
            name="endDate"
            type="date"
            value={form.endDate || ''}
            onChange={handleChange}
            className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
          />
        </div>
      </div>

      {/* Key Results */}
      <div>
        <div className="flex items-center justify-between mb-3">
          <h3 className="text-sm font-medium text-gray-700">Key Results</h3>
          <button
            type="button"
            onClick={handleAddKeyResult}
            className="text-xs px-3 py-1 border border-blue-300 text-blue-600 rounded hover:bg-blue-50"
          >
            + Add Key Result
          </button>
        </div>

        {keyResults.length === 0 && (
          <p className="text-sm text-gray-400 text-center py-3 border-2 border-dashed border-gray-200 rounded">
            No key results added yet. Click &quot;Add Key Result&quot; to add one.
          </p>
        )}

        {keyResults.map((kr, index) => (
          <div key={index} className="border border-gray-200 rounded p-3 mb-3 bg-gray-50">
            <div className="flex justify-between items-center mb-2">
              <span className="text-xs font-medium text-gray-600">Key Result {index + 1}</span>
              <button
                type="button"
                onClick={() => handleRemoveKeyResult(index)}
                className="text-xs text-red-500 hover:text-red-700"
              >
                Remove
              </button>
            </div>
            <div className="grid grid-cols-1 gap-2 sm:grid-cols-3">
              <div className="sm:col-span-3">
                <input
                  type="text"
                  placeholder="Metric name *"
                  required
                  value={kr.metric}
                  onChange={(e) => handleKeyResultChange(index, 'metric', e.target.value)}
                  className="block w-full border-gray-300 rounded-md shadow-sm text-sm"
                />
              </div>
              <div>
                <input
                  type="number"
                  placeholder="Target value *"
                  required
                  min={0.01}
                  step="any"
                  value={kr.targetValue || ''}
                  onChange={(e) => handleKeyResultChange(index, 'targetValue', parseFloat(e.target.value) || 0)}
                  className="block w-full border-gray-300 rounded-md shadow-sm text-sm"
                />
              </div>
              <div>
                <input
                  type="number"
                  placeholder="Current value"
                  min={0}
                  step="any"
                  value={kr.currentValue || ''}
                  onChange={(e) => handleKeyResultChange(index, 'currentValue', parseFloat(e.target.value) || 0)}
                  className="block w-full border-gray-300 rounded-md shadow-sm text-sm"
                />
              </div>
              <div>
                <input
                  type="text"
                  placeholder="Unit (e.g. %, $, units)"
                  value={kr.unitOfMeasure || ''}
                  onChange={(e) => handleKeyResultChange(index, 'unitOfMeasure', e.target.value)}
                  className="block w-full border-gray-300 rounded-md shadow-sm text-sm"
                />
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Actions */}
      <div className="flex justify-end gap-3 pt-4 border-t border-gray-200">
        {onCancel && (
          <button
            type="button"
            onClick={onCancel}
            className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
          >
            Cancel
          </button>
        )}
        <button
          type="submit"
          disabled={submitting}
          className="px-4 py-2 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-md hover:bg-blue-700 disabled:opacity-50"
        >
          {submitting ? 'Creating...' : 'Create Goal'}
        </button>
      </div>
    </form>
  );
}
