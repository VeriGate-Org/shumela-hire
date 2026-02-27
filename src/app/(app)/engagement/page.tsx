'use client';

import React, { useState, useEffect } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { engagementService } from '@/services/engagementService';
import type { EngagementAnalytics } from '@/types/engagement';

export default function EngagementDashboard() {
  const { user } = useAuth();
  const [analytics, setAnalytics] = useState<EngagementAnalytics | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadAnalytics();
  }, []);

  const loadAnalytics = async () => {
    try {
      const data = await engagementService.getEngagementAnalytics();
      setAnalytics(data);
    } catch {
      // Analytics may not be available
    } finally {
      setLoading(false);
    }
  };

  const statCards = [
    { label: 'Active Surveys', value: analytics?.activeSurveys ?? 0, color: 'bg-blue-500' },
    { label: 'Recognitions (30d)', value: analytics?.totalRecognitions ?? 0, color: 'bg-purple-500' },
    { label: 'Wellness Check-Ins', value: analytics?.wellnessCheckIns ?? 0, color: 'bg-green-500' },
    { label: 'Social Posts', value: analytics?.totalSocialPosts ?? 0, color: 'bg-orange-500' },
  ];

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-white shadow">
        <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Employee Engagement</h1>
              <p className="mt-1 text-sm text-gray-600">
                Surveys, recognition, wellness, and social feed
              </p>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        {/* Stats */}
        <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4 mb-8">
          {statCards.map((stat) => (
            <div key={stat.label} className="bg-white overflow-hidden shadow rounded-lg">
              <div className="p-5">
                <div className="flex items-center">
                  <div className={`flex-shrink-0 ${stat.color} rounded-md p-3`}>
                    <span className="text-white text-lg font-bold">{stat.value}</span>
                  </div>
                  <div className="ml-5 w-0 flex-1">
                    <dl>
                      <dt className="text-sm font-medium text-gray-500 truncate">{stat.label}</dt>
                      <dd className="text-lg font-semibold text-gray-900">{stat.value}</dd>
                    </dl>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>

        {/* Mood Distribution */}
        {analytics?.moodDistribution && Object.keys(analytics.moodDistribution).length > 0 && (
          <div className="bg-white shadow rounded-lg p-6 mb-8">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">Mood Distribution (Last 30 Days)</h2>
            <div className="flex space-x-4">
              {Object.entries(analytics.moodDistribution).map(([mood, count]) => (
                <div key={mood} className="flex-1 text-center">
                  <div className="text-2xl font-bold text-gray-900">{count}</div>
                  <div className="text-sm text-gray-500">{mood}</div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Wellness Averages */}
        {(analytics?.averageEnergyLevel || analytics?.averageStressLevel) && (
          <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 mb-8">
            {analytics?.averageEnergyLevel != null && (
              <div className="bg-white shadow rounded-lg p-6">
                <h3 className="text-sm font-medium text-gray-500">Average Energy Level</h3>
                <p className="text-3xl font-bold text-green-600">{analytics.averageEnergyLevel.toFixed(1)}/10</p>
              </div>
            )}
            {analytics?.averageStressLevel != null && (
              <div className="bg-white shadow rounded-lg p-6">
                <h3 className="text-sm font-medium text-gray-500">Average Stress Level</h3>
                <p className="text-3xl font-bold text-red-600">{analytics.averageStressLevel.toFixed(1)}/10</p>
              </div>
            )}
          </div>
        )}

        {/* Quick Navigation */}
        <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
          <a href="/engagement/surveys" className="bg-white shadow rounded-lg p-6 hover:shadow-md transition-shadow">
            <h3 className="text-lg font-semibold text-gray-900">Surveys</h3>
            <p className="mt-1 text-sm text-gray-500">Create and manage pulse surveys</p>
          </a>
          <a href="/engagement/recognition" className="bg-white shadow rounded-lg p-6 hover:shadow-md transition-shadow">
            <h3 className="text-lg font-semibold text-gray-900">Recognition</h3>
            <p className="mt-1 text-sm text-gray-500">Give kudos and view the recognition wall</p>
          </a>
          <a href="/engagement/wellness" className="bg-white shadow rounded-lg p-6 hover:shadow-md transition-shadow">
            <h3 className="text-lg font-semibold text-gray-900">Wellness</h3>
            <p className="mt-1 text-sm text-gray-500">Check-in and browse wellness programs</p>
          </a>
          <a href="/engagement/social" className="bg-white shadow rounded-lg p-6 hover:shadow-md transition-shadow">
            <h3 className="text-lg font-semibold text-gray-900">Social Feed</h3>
            <p className="mt-1 text-sm text-gray-500">Company updates and announcements</p>
          </a>
        </div>
      </div>
    </div>
  );
}
