'use client';

import React, { useState, useEffect } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { engagementService } from '@/services/engagementService';
import type { Survey } from '@/types/engagement';
import { SurveyStatus, SurveyType, getSurveyStatusColor } from '@/types/engagement';

export default function SurveysPage() {
  const { user } = useAuth();
  const [surveys, setSurveys] = useState<Survey[]>([]);
  const [loading, setLoading] = useState(true);
  const [showCreate, setShowCreate] = useState(false);
  const [filter, setFilter] = useState<SurveyStatus | ''>('');

  // Create form state
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [surveyType, setSurveyType] = useState<SurveyType>(SurveyType.PULSE);
  const [isAnonymous, setIsAnonymous] = useState(true);

  useEffect(() => {
    loadSurveys();
  }, [filter]);

  const loadSurveys = async () => {
    setLoading(true);
    try {
      const data = filter
        ? await engagementService.getSurveys(filter as SurveyStatus)
        : await engagementService.getSurveys();
      setSurveys(data);
    } catch {
      setSurveys([]);
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await engagementService.createSurvey({ title, description, surveyType, isAnonymous });
      setShowCreate(false);
      setTitle('');
      setDescription('');
      loadSurveys();
    } catch {
      // Handle error
    }
  };

  const handleActivate = async (id: number) => {
    try {
      await engagementService.activateSurvey(id);
      loadSurveys();
    } catch {
      // Handle error
    }
  };

  const handleClose = async (id: number) => {
    try {
      await engagementService.closeSurvey(id);
      loadSurveys();
    } catch {
      // Handle error
    }
  };

  const isHR = user?.role === 'ADMIN' || user?.role === 'HR_MANAGER';

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-white shadow">
        <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Pulse Surveys</h1>
              <p className="mt-1 text-sm text-gray-600">Create and manage employee surveys</p>
            </div>
            {isHR && (
              <button
                onClick={() => setShowCreate(!showCreate)}
                className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-full text-white bg-violet-600 hover:bg-violet-700"
              >
                {showCreate ? 'Cancel' : 'Create Survey'}
              </button>
            )}
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        {/* Create Form */}
        {showCreate && (
          <div className="bg-white shadow rounded-lg p-6 mb-6">
            <h2 className="text-lg font-semibold mb-4">New Survey</h2>
            <form onSubmit={handleCreate} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700">Title</label>
                <input type="text" value={title} onChange={(e) => setTitle(e.target.value)} required
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-violet-500 focus:ring-violet-500 sm:text-sm" />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Description</label>
                <textarea value={description} onChange={(e) => setDescription(e.target.value)} rows={3}
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-violet-500 focus:ring-violet-500 sm:text-sm" />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700">Type</label>
                  <select value={surveyType} onChange={(e) => setSurveyType(e.target.value as SurveyType)}
                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-violet-500 focus:ring-violet-500 sm:text-sm">
                    {Object.values(SurveyType).map((t) => (
                      <option key={t} value={t}>{t}</option>
                    ))}
                  </select>
                </div>
                <div className="flex items-center pt-6">
                  <input type="checkbox" checked={isAnonymous} onChange={(e) => setIsAnonymous(e.target.checked)}
                    className="h-4 w-4 text-violet-600 border-gray-300 rounded" />
                  <label className="ml-2 text-sm text-gray-700">Anonymous responses</label>
                </div>
              </div>
              <button type="submit"
                className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-full text-white bg-violet-600 hover:bg-violet-700">
                Create Survey
              </button>
            </form>
          </div>
        )}

        {/* Filters */}
        <div className="flex space-x-2 mb-6">
          <button onClick={() => setFilter('')}
            className={`px-3 py-1 rounded-full text-sm ${!filter ? 'bg-violet-100 text-violet-800' : 'bg-gray-100 text-gray-600'}`}>
            All
          </button>
          {Object.values(SurveyStatus).map((status) => (
            <button key={status} onClick={() => setFilter(status)}
              className={`px-3 py-1 rounded-full text-sm ${filter === status ? 'bg-violet-100 text-violet-800' : 'bg-gray-100 text-gray-600'}`}>
              {status}
            </button>
          ))}
        </div>

        {/* Survey List */}
        {loading ? (
          <div className="text-center py-12 text-gray-500">Loading surveys...</div>
        ) : surveys.length === 0 ? (
          <div className="text-center py-12 text-gray-500">No surveys found</div>
        ) : (
          <div className="space-y-4">
            {surveys.map((survey) => (
              <div key={survey.id} className="bg-white shadow rounded-lg p-6">
                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="text-lg font-semibold text-gray-900">{survey.title}</h3>
                    {survey.description && (
                      <p className="mt-1 text-sm text-gray-500">{survey.description}</p>
                    )}
                    <div className="mt-2 flex items-center space-x-3">
                      <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getSurveyStatusColor(survey.status)}`}>
                        {survey.status}
                      </span>
                      <span className="text-xs text-gray-500">{survey.surveyType}</span>
                      {survey.isAnonymous && (
                        <span className="text-xs text-gray-500">Anonymous</span>
                      )}
                    </div>
                  </div>
                  {isHR && (
                    <div className="flex space-x-2">
                      {survey.status === SurveyStatus.DRAFT && (
                        <button onClick={() => handleActivate(survey.id)}
                          className="px-3 py-1 text-sm bg-green-100 text-green-800 rounded-full hover:bg-green-200">
                          Activate
                        </button>
                      )}
                      {survey.status === SurveyStatus.ACTIVE && (
                        <button onClick={() => handleClose(survey.id)}
                          className="px-3 py-1 text-sm bg-red-100 text-red-800 rounded-full hover:bg-red-200">
                          Close
                        </button>
                      )}
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
