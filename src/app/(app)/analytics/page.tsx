'use client';

import React, { useState, useEffect, useCallback } from 'react';
import PageWrapper from '@/components/PageWrapper';
import {
  AdvancedAnalyticsDashboard,
  RealTimeMetrics,
  InteractiveFilters,
  FilterConfig,
  FilterValue
} from '@/components/analytics';
import { useTheme } from '@/contexts/ThemeContext';
import { apiFetch } from '@/lib/api-fetch';

// Filter configuration for analytics
const analyticsFilters: FilterConfig[] = [
  {
    id: 'department',
    label: 'Department',
    type: 'select',
    options: [
      { value: 'engineering', label: 'Engineering' },
      { value: 'product', label: 'Product' },
      { value: 'design', label: 'Design' },
      { value: 'marketing', label: 'Marketing' },
      { value: 'sales', label: 'Sales' },
    ],
    placeholder: 'Select department',
  },
  {
    id: 'position_level',
    label: 'Position Level',
    type: 'multiselect',
    options: [
      { value: 'entry', label: 'Entry Level' },
      { value: 'mid', label: 'Mid Level' },
      { value: 'senior', label: 'Senior Level' },
      { value: 'lead', label: 'Lead/Principal' },
      { value: 'manager', label: 'Manager' },
    ],
    placeholder: 'Select position levels',
  },
  {
    id: 'source',
    label: 'Application Source',
    type: 'multiselect',
    options: [
      { value: 'linkedin', label: 'LinkedIn' },
      { value: 'indeed', label: 'Indeed' },
      { value: 'company_site', label: 'Company Website' },
      { value: 'referrals', label: 'Employee Referrals' },
      { value: 'university', label: 'University Partnerships' },
    ],
    placeholder: 'Select sources',
  },
  {
    id: 'date_range',
    label: 'Date Range',
    type: 'daterange',
    placeholder: 'Select date range',
  },
  {
    id: 'experience_years',
    label: 'Years of Experience',
    type: 'range',
    min: 0,
    max: 20,
    placeholder: 'Select experience range',
  },
  {
    id: 'search',
    label: 'Search',
    type: 'search',
    placeholder: 'Search positions, candidates, or keywords...',
  },
];

interface Insight {
  type: 'positive' | 'warning';
  text: string;
}

export default function AnalyticsPage() {
  const [filterValues, setFilterValues] = useState<FilterValue[]>([]);
  const [insights, setInsights] = useState<Insight[]>([]);
  const [insightsLoading, setInsightsLoading] = useState(true);
  const { setCurrentRole } = useTheme();

  // Set theme to executive for analytics
  useEffect(() => {
    setCurrentRole('EXECUTIVE');
  }, [setCurrentRole]);

  const handleFilterChange = (values: FilterValue[]) => {
    setFilterValues(values);
  };

  const handleFilterReset = () => {
    setFilterValues([]);
  };

  // Derive insights from KPI data
  const loadInsights = useCallback(async () => {
    setInsightsLoading(true);
    try {
      const res = await apiFetch('/api/analytics/kpis');
      if (!res.ok) throw new Error('Failed to load KPIs');
      const json = await res.json();
      const kpis = json.kpis ?? {};
      const derived: Insight[] = [];

      // Derive insights from real KPI values
      const interviewConversion = kpis['interview_conversion_rate']?.value;
      if (interviewConversion !== undefined) {
        const rate = Number(interviewConversion);
        if (rate >= 20) {
          derived.push({ type: 'positive', text: `Interview conversion rate is strong at ${rate.toFixed(1)}%` });
        } else if (rate > 0) {
          derived.push({ type: 'warning', text: `Interview conversion rate is ${rate.toFixed(1)}% — consider reviewing screening criteria` });
        }
      }

      const acceptanceRate = kpis['acceptance_rate']?.value;
      if (acceptanceRate !== undefined) {
        const rate = Number(acceptanceRate);
        if (rate >= 80) {
          derived.push({ type: 'positive', text: `Offer acceptance rate is ${rate.toFixed(1)}% — offers are competitive` });
        } else if (rate > 0) {
          derived.push({ type: 'warning', text: `Offer acceptance rate is ${rate.toFixed(1)}% — review compensation packages` });
        }
      }

      const noShowRate = kpis['no_show_rate']?.value;
      if (noShowRate !== undefined) {
        const rate = Number(noShowRate);
        if (rate > 15) {
          derived.push({ type: 'warning', text: `Interview no-show rate is ${rate.toFixed(1)}% — consider sending reminders` });
        } else if (rate > 0) {
          derived.push({ type: 'positive', text: `Low interview no-show rate at ${rate.toFixed(1)}%` });
        }
      }

      const timeToFill = kpis['time_to_fill_days']?.value;
      if (timeToFill !== undefined) {
        const days = Number(timeToFill);
        if (days > 45) {
          derived.push({ type: 'warning', text: `Average time to fill is ${days} days — above industry benchmark of 45 days` });
        } else if (days > 0) {
          derived.push({ type: 'positive', text: `Average time to fill is ${days} days — within target range` });
        }
      }

      const avgScore = kpis['avg_interview_score']?.value;
      if (avgScore !== undefined) {
        const score = Number(avgScore);
        if (score >= 3.5) {
          derived.push({ type: 'positive', text: `Average interview score is ${score.toFixed(1)}/5 — strong candidate quality` });
        } else if (score > 0) {
          derived.push({ type: 'warning', text: `Average interview score is ${score.toFixed(1)}/5 — sourcing may need improvement` });
        }
      }

      setInsights(derived);
    } catch {
      setInsights([]);
    } finally {
      setInsightsLoading(false);
    }
  }, []);

  useEffect(() => {
    loadInsights();
  }, [loadInsights]);

  const actions = (
    <div className="flex items-center gap-2 text-sm text-gray-500">
      <div className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></div>
      Live Data
    </div>
  );

  const positiveInsights = insights.filter(i => i.type === 'positive');
  const warningInsights = insights.filter(i => i.type === 'warning');

  return (
    <PageWrapper
      title="Advanced Analytics"
      subtitle="Comprehensive recruitment metrics, insights, and real-time performance monitoring"
      actions={actions}
    >
      <div className="space-y-6">
        {/* Interactive Filters */}
        <InteractiveFilters
          filters={analyticsFilters}
          values={filterValues}
          onChange={handleFilterChange}
          onReset={handleFilterReset}
        />

        {/* Real-Time Metrics Widget */}
        <RealTimeMetrics updateInterval={3000} />

        {/* Advanced Analytics Dashboard */}
        <AdvancedAnalyticsDashboard filters={filterValues} />

        {/* Key Insights Section — derived from real KPI data */}
        {!insightsLoading && insights.length > 0 && (
          <div className="bg-white rounded-sm border border-gray-200 border-t-2 border-t-gold-500 p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Key Insights & Recommendations</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {positiveInsights.length > 0 && (
                <div className="space-y-3">
                  <h4 className="font-medium text-gray-900">Performance Highlights</h4>
                  <ul className="space-y-2 text-sm text-gray-600">
                    {positiveInsights.map((insight, i) => (
                      <li key={i} className="flex items-start gap-2">
                        <span className="text-green-500 mt-0.5">&#8226;</span>
                        {insight.text}
                      </li>
                    ))}
                  </ul>
                </div>
              )}
              {warningInsights.length > 0 && (
                <div className="space-y-3">
                  <h4 className="font-medium text-gray-900">Areas for Improvement</h4>
                  <ul className="space-y-2 text-sm text-gray-600">
                    {warningInsights.map((insight, i) => (
                      <li key={i} className="flex items-start gap-2">
                        <span className="text-orange-500 mt-0.5">&#8226;</span>
                        {insight.text}
                      </li>
                    ))}
                  </ul>
                </div>
              )}
            </div>
          </div>
        )}

        {!insightsLoading && insights.length === 0 && (
          <div className="bg-white rounded-sm border border-gray-200 border-t-2 border-t-gold-500 p-6 text-center">
            <p className="text-sm text-gray-500">Not enough data to generate insights</p>
          </div>
        )}
      </div>
    </PageWrapper>
  );
}
