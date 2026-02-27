'use client';

import React, { useEffect, useState } from 'react';
import {
  KeyResult,
  KeyResultStatus,
  getKeyResultStatusColor,
} from '@/types/goal';
import {
  getKeyResults,
  updateKeyResultProgress,
  deleteKeyResult,
} from '@/services/goalService';

interface KeyResultEditorProps {
  goalId: string;
  readOnly?: boolean;
}

export default function KeyResultEditor({ goalId, readOnly = false }: KeyResultEditorProps) {
  const [keyResults, setKeyResults] = useState<KeyResult[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [editValue, setEditValue] = useState<string>('');
  const [editStatus, setEditStatus] = useState<KeyResultStatus | ''>('');

  const loadKeyResults = async () => {
    setLoading(true);
    setError(null);
    try {
      const results = await getKeyResults(goalId);
      setKeyResults(results);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load key results');
    } finally {
      setLoading(false);
    }
  };

  // eslint-disable-next-line react-hooks/exhaustive-deps
  useEffect(() => { loadKeyResults(); }, [goalId]);

  const handleStartEdit = (kr: KeyResult) => {
    setEditingId(kr.id);
    setEditValue(String(kr.currentValue));
    setEditStatus(kr.status);
  };

  const handleSaveProgress = async (krId: string) => {
    try {
      await updateKeyResultProgress(krId, {
        currentValue: parseFloat(editValue) || 0,
        status: editStatus || undefined,
      });
      setEditingId(null);
      loadKeyResults();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to update progress');
    }
  };

  const handleDelete = async (krId: string) => {
    if (!confirm('Delete this key result?')) return;
    try {
      await deleteKeyResult(krId);
      loadKeyResults();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to delete key result');
    }
  };

  if (loading) return <p className="text-sm text-gray-500 py-2">Loading key results...</p>;
  if (error) return <p className="text-sm text-red-600 py-2">{error}</p>;
  if (keyResults.length === 0) return <p className="text-sm text-gray-400 py-2">No key results.</p>;

  return (
    <div className="space-y-3">
      {keyResults.map((kr) => (
        <div key={kr.id} className="border border-gray-200 rounded p-3 bg-gray-50">
          <div className="flex items-start justify-between mb-2">
            <div>
              <p className="text-sm font-medium text-gray-900">{kr.metric}</p>
              {kr.description && (
                <p className="text-xs text-gray-500 mt-0.5">{kr.description}</p>
              )}
            </div>
            <span className={`text-xs px-2 py-0.5 rounded font-medium ${getKeyResultStatusColor(kr.status)}`}>
              {kr.status}
            </span>
          </div>

          {/* Progress bar */}
          <div className="mb-2">
            <div className="flex justify-between text-xs text-gray-500 mb-1">
              <span>{kr.currentValue} / {kr.targetValue} {kr.unitOfMeasure}</span>
              <span className="font-medium">{kr.progressPct}%</span>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-2">
              <div
                className={`h-2 rounded-full ${
                  kr.progressPct >= 100
                    ? 'bg-blue-500'
                    : kr.progressPct >= 70
                    ? 'bg-green-500'
                    : kr.progressPct >= 40
                    ? 'bg-yellow-500'
                    : 'bg-red-500'
                }`}
                style={{ width: `${Math.min(100, kr.progressPct)}%` }}
              />
            </div>
          </div>

          {/* Edit form */}
          {!readOnly && editingId === kr.id ? (
            <div className="flex gap-2 mt-2">
              <input
                type="number"
                value={editValue}
                onChange={(e) => setEditValue(e.target.value)}
                min={0}
                step="any"
                className="w-28 text-sm border-gray-300 rounded shadow-sm"
              />
              <select
                value={editStatus}
                onChange={(e) => setEditStatus(e.target.value as KeyResultStatus)}
                className="text-sm border-gray-300 rounded shadow-sm"
              >
                {Object.values(KeyResultStatus).map((s) => (
                  <option key={s} value={s}>{s}</option>
                ))}
              </select>
              <button
                onClick={() => handleSaveProgress(kr.id)}
                className="text-xs px-2 py-1 bg-blue-600 text-white rounded hover:bg-blue-700"
              >
                Save
              </button>
              <button
                onClick={() => setEditingId(null)}
                className="text-xs px-2 py-1 bg-gray-200 text-gray-700 rounded hover:bg-gray-300"
              >
                Cancel
              </button>
            </div>
          ) : (
            !readOnly && (
              <div className="flex gap-2 mt-2">
                <button
                  onClick={() => handleStartEdit(kr)}
                  className="text-xs px-2 py-1 bg-white border border-gray-300 text-gray-700 rounded hover:bg-gray-50"
                >
                  Update Progress
                </button>
                <button
                  onClick={() => handleDelete(kr.id)}
                  className="text-xs px-2 py-1 text-red-600 hover:text-red-800"
                >
                  Delete
                </button>
              </div>
            )
          )}
        </div>
      ))}
    </div>
  );
}
