'use client';

import React, { useState, useEffect } from 'react';

interface Demographics {
  totalEmployees: number;
  genderBreakdown: Record<string, number>;
  raceBreakdown: Record<string, number>;
  ageGroups: Record<string, number>;
  tenureGroups: Record<string, number>;
  departmentBreakdown: Record<string, number>;
}

interface Turnover {
  terminationsLastYear: number;
  activeEmployees: number;
  turnoverRatePercent: number;
  terminationByDept: Record<string, number>;
}

interface SpanOfControl {
  totalManagers: number;
  averageSpanOfControl: number;
  spanDistribution: Record<string, number>;
  totalEmployees: number;
}

const DEMO_DEMOGRAPHICS: Demographics = {
  totalEmployees: 120,
  genderBreakdown: { Male: 68, Female: 48, 'Non-binary': 4 },
  raceBreakdown: { Black: 55, White: 32, Coloured: 18, Indian: 11, 'Not specified': 4 },
  ageGroups: { 'Under 25': 12, '25-34': 42, '35-44': 38, '45-54': 22, '55+': 6 },
  tenureGroups: { 'Less than 1 year': 18, '1-3 years': 35, '3-5 years': 28, '5-10 years': 27, '10+ years': 12 },
  departmentBreakdown: { Engineering: 45, HR: 11, Finance: 19, Sales: 32, Product: 13 },
};

const DEMO_TURNOVER: Turnover = {
  terminationsLastYear: 14,
  activeEmployees: 120,
  turnoverRatePercent: 10.45,
  terminationByDept: { Engineering: 4, Sales: 5, HR: 2, Finance: 2, Product: 1 },
};

const DEMO_SPAN: SpanOfControl = {
  totalManagers: 18,
  averageSpanOfControl: 5.8,
  spanDistribution: { '1-3': 6, '4-6': 8, '7-10': 3, '11+': 1 },
  totalEmployees: 120,
};

function MiniBar({ label, value, total, color }: { label: string; value: number; total: number; color: string }) {
  const pct = total > 0 ? (value / total) * 100 : 0;
  return (
    <div>
      <div className="flex justify-between text-sm mb-1">
        <span className="text-gray-700">{label}</span>
        <span className="text-gray-500">{value} ({pct.toFixed(1)}%)</span>
      </div>
      <div className="h-3 bg-gray-100 rounded-full overflow-hidden">
        <div className={`h-full rounded-full ${color}`} style={{ width: `${pct}%` }} />
      </div>
    </div>
  );
}

export default function WorkforcePage() {
  const [demographics, setDemographics] = useState<Demographics>(DEMO_DEMOGRAPHICS);
  const [turnover, setTurnover] = useState<Turnover>(DEMO_TURNOVER);
  const [span, setSpan] = useState<SpanOfControl>(DEMO_SPAN);
  const [activeTab, setActiveTab] = useState<'demographics' | 'turnover' | 'span'>('demographics');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    Promise.all([
      fetch('/api/org/workforce/demographics'),
      fetch('/api/org/workforce/turnover'),
      fetch('/api/org/workforce/span-of-control'),
    ])
      .then(async ([demoRes, turnRes, spanRes]) => {
        if (demoRes.ok) {
          const data = await demoRes.json();
          if (data.totalEmployees != null) setDemographics(data);
        }
        if (turnRes.ok) {
          const data = await turnRes.json();
          if (data.turnoverRatePercent != null) setTurnover(data);
        }
        if (spanRes.ok) {
          const data = await spanRes.json();
          if (data.totalManagers != null) setSpan(data);
        }
      })
      .catch(() => {/* use demo */})
      .finally(() => setLoading(false));
  }, []);

  const tabs = [
    { id: 'demographics' as const, label: 'Demographics' },
    { id: 'turnover' as const, label: 'Turnover' },
    { id: 'span' as const, label: 'Span of Control' },
  ];

  const genderColors = ['bg-blue-500', 'bg-pink-500', 'bg-purple-500', 'bg-gray-400'];
  const raceColors = ['bg-indigo-500', 'bg-green-500', 'bg-yellow-500', 'bg-orange-500', 'bg-gray-400'];
  const ageColors = ['bg-cyan-400', 'bg-blue-400', 'bg-blue-600', 'bg-indigo-600', 'bg-purple-700', 'bg-gray-400'];
  const tenureColors = ['bg-green-300', 'bg-green-400', 'bg-green-500', 'bg-green-600', 'bg-green-800'];

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white shadow">
        <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Workforce Analytics</h1>
              <p className="mt-1 text-sm text-gray-600">
                Demographics, turnover, and span of control insights
              </p>
            </div>
            <a href="/org" className="text-sm text-gray-500 hover:text-gray-700">← Org Chart</a>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        {/* KPI Cards */}
        <div className="grid grid-cols-1 sm:grid-cols-4 gap-4 mb-6">
          {[
            { label: 'Total Employees', value: demographics.totalEmployees },
            { label: 'Turnover Rate', value: `${turnover.turnoverRatePercent}%`, color: 'text-orange-600' },
            { label: 'Avg Span of Control', value: span.averageSpanOfControl, color: 'text-blue-600' },
            { label: 'Total Managers', value: span.totalManagers },
          ].map(card => (
            <div key={card.label} className="bg-white shadow rounded-lg p-4">
              <p className="text-xs font-medium text-gray-500 uppercase">{card.label}</p>
              <p className={`text-2xl font-bold ${card.color ?? 'text-gray-900'}`}>{card.value}</p>
            </div>
          ))}
        </div>

        {/* Tabs */}
        <div className="bg-white shadow rounded-lg overflow-hidden">
          <div className="border-b border-gray-200">
            <nav className="flex">
              {tabs.map(tab => (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id)}
                  className={`px-6 py-4 text-sm font-medium border-b-2 transition-colors ${
                    activeTab === tab.id
                      ? 'border-indigo-600 text-indigo-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700'
                  }`}
                >
                  {tab.label}
                </button>
              ))}
            </nav>
          </div>

          <div className="p-6">
            {loading && (
              <div className="text-center text-gray-400 py-8">Loading analytics...</div>
            )}

            {!loading && activeTab === 'demographics' && (
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                {/* Gender */}
                <div>
                  <h3 className="text-sm font-semibold text-gray-700 mb-4 uppercase tracking-wide">Gender</h3>
                  <div className="space-y-3">
                    {Object.entries(demographics.genderBreakdown).map(([label, value], i) => (
                      <MiniBar key={label} label={label} value={value} total={demographics.totalEmployees} color={genderColors[i] || 'bg-gray-400'} />
                    ))}
                  </div>
                </div>

                {/* Race */}
                <div>
                  <h3 className="text-sm font-semibold text-gray-700 mb-4 uppercase tracking-wide">Race / Ethnicity</h3>
                  <div className="space-y-3">
                    {Object.entries(demographics.raceBreakdown).map(([label, value], i) => (
                      <MiniBar key={label} label={label} value={value} total={demographics.totalEmployees} color={raceColors[i] || 'bg-gray-400'} />
                    ))}
                  </div>
                </div>

                {/* Age Groups */}
                <div>
                  <h3 className="text-sm font-semibold text-gray-700 mb-4 uppercase tracking-wide">Age Groups</h3>
                  <div className="space-y-3">
                    {Object.entries(demographics.ageGroups).map(([label, value], i) => (
                      <MiniBar key={label} label={label} value={value} total={demographics.totalEmployees} color={ageColors[i] || 'bg-gray-400'} />
                    ))}
                  </div>
                </div>

                {/* Tenure */}
                <div>
                  <h3 className="text-sm font-semibold text-gray-700 mb-4 uppercase tracking-wide">Tenure</h3>
                  <div className="space-y-3">
                    {Object.entries(demographics.tenureGroups).map(([label, value], i) => (
                      <MiniBar key={label} label={label} value={value} total={demographics.totalEmployees} color={tenureColors[i] || 'bg-gray-400'} />
                    ))}
                  </div>
                </div>
              </div>
            )}

            {!loading && activeTab === 'turnover' && (
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                <div>
                  <h3 className="text-sm font-semibold text-gray-700 mb-4 uppercase tracking-wide">Turnover Overview</h3>
                  <div className="space-y-4">
                    <div className="bg-orange-50 rounded-lg p-4">
                      <p className="text-xs text-orange-600 font-medium uppercase">Annual Turnover Rate</p>
                      <p className="text-4xl font-bold text-orange-700">{turnover.turnoverRatePercent}%</p>
                      <p className="text-sm text-orange-500 mt-1">Industry avg: ~15%</p>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                      <div className="bg-gray-50 rounded-lg p-3">
                        <p className="text-xs text-gray-500">Active Employees</p>
                        <p className="text-xl font-bold text-gray-900">{turnover.activeEmployees}</p>
                      </div>
                      <div className="bg-red-50 rounded-lg p-3">
                        <p className="text-xs text-red-500">Left (Last 12 mo)</p>
                        <p className="text-xl font-bold text-red-700">{turnover.terminationsLastYear}</p>
                      </div>
                    </div>
                  </div>
                </div>

                <div>
                  <h3 className="text-sm font-semibold text-gray-700 mb-4 uppercase tracking-wide">Terminations by Department</h3>
                  <div className="space-y-3">
                    {Object.entries(turnover.terminationByDept).map(([dept, count]) => (
                      <MiniBar key={dept} label={dept} value={count} total={turnover.terminationsLastYear} color="bg-red-400" />
                    ))}
                  </div>
                </div>
              </div>
            )}

            {!loading && activeTab === 'span' && (
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                <div>
                  <h3 className="text-sm font-semibold text-gray-700 mb-4 uppercase tracking-wide">Span of Control</h3>
                  <div className="space-y-4">
                    <div className="bg-blue-50 rounded-lg p-4">
                      <p className="text-xs text-blue-600 font-medium uppercase">Average Span</p>
                      <p className="text-4xl font-bold text-blue-700">{span.averageSpanOfControl}</p>
                      <p className="text-sm text-blue-500 mt-1">Direct reports per manager</p>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                      <div className="bg-gray-50 rounded-lg p-3">
                        <p className="text-xs text-gray-500">Total Managers</p>
                        <p className="text-xl font-bold text-gray-900">{span.totalManagers}</p>
                      </div>
                      <div className="bg-gray-50 rounded-lg p-3">
                        <p className="text-xs text-gray-500">Total Employees</p>
                        <p className="text-xl font-bold text-gray-900">{span.totalEmployees}</p>
                      </div>
                    </div>
                  </div>
                </div>

                <div>
                  <h3 className="text-sm font-semibold text-gray-700 mb-4 uppercase tracking-wide">Distribution of Direct Reports</h3>
                  <div className="space-y-3">
                    {Object.entries(span.spanDistribution).map(([range, count]) => (
                      <MiniBar key={range} label={`${range} reports`} value={count} total={span.totalManagers} color="bg-blue-500" />
                    ))}
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
