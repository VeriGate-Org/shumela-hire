'use client';

import React, { useState, useEffect } from 'react';

interface HeadcountPlan {
  id: number;
  department: string;
  fiscalYear: number;
  plannedHeadcount: number;
  currentHeadcount: number;
  budget: number | null;
  variance: number;
  forecastVacancies: number;
  newPositionRequests: number;
  orgUnitName: string | null;
}

interface YearSummary {
  fiscalYear: number;
  totalPlannedHeadcount: number;
  totalCurrentHeadcount: number;
  totalVariance: number;
  totalBudget: number;
}

const DEMO_PLANS: HeadcountPlan[] = [
  { id: 1, department: 'Engineering', fiscalYear: 2026, plannedHeadcount: 50, currentHeadcount: 45, budget: 15000000, variance: 5, forecastVacancies: 3, newPositionRequests: 2, orgUnitName: 'Engineering' },
  { id: 2, department: 'Human Resources', fiscalYear: 2026, plannedHeadcount: 12, currentHeadcount: 11, budget: 3500000, variance: 1, forecastVacancies: 1, newPositionRequests: 0, orgUnitName: 'Human Resources' },
  { id: 3, department: 'Finance', fiscalYear: 2026, plannedHeadcount: 20, currentHeadcount: 19, budget: 5000000, variance: 1, forecastVacancies: 2, newPositionRequests: 1, orgUnitName: 'Finance' },
  { id: 4, department: 'Sales', fiscalYear: 2026, plannedHeadcount: 35, currentHeadcount: 32, budget: 9000000, variance: 3, forecastVacancies: 4, newPositionRequests: 3, orgUnitName: 'Sales' },
  { id: 5, department: 'Product', fiscalYear: 2026, plannedHeadcount: 15, currentHeadcount: 13, budget: 4500000, variance: 2, forecastVacancies: 1, newPositionRequests: 2, orgUnitName: 'Product' },
];

const DEMO_SUMMARY: YearSummary = {
  fiscalYear: 2026,
  totalPlannedHeadcount: 132,
  totalCurrentHeadcount: 120,
  totalVariance: 12,
  totalBudget: 37000000,
};

export default function HeadcountPage() {
  const [plans, setPlans] = useState<HeadcountPlan[]>(DEMO_PLANS);
  const [summary, setSummary] = useState<YearSummary>(DEMO_SUMMARY);
  const [selectedYear, setSelectedYear] = useState(2026);
  const [fiscalYears, setFiscalYears] = useState([2024, 2025, 2026]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    Promise.all([
      fetch(`/api/org/headcount?fiscalYear=${selectedYear}`),
      fetch(`/api/org/headcount/summary?year=${selectedYear}`),
      fetch('/api/org/headcount/fiscal-years'),
    ])
      .then(async ([plansRes, sumRes, yearsRes]) => {
        if (plansRes.ok) {
          const data = await plansRes.json();
          if (Array.isArray(data) && data.length > 0) setPlans(data);
        }
        if (sumRes.ok) {
          const data = await sumRes.json();
          setSummary(data);
        }
        if (yearsRes.ok) {
          const data = await yearsRes.json();
          if (Array.isArray(data) && data.length > 0) setFiscalYears(data);
        }
      })
      .catch(() => {/* use demo */})
      .finally(() => setLoading(false));
  }, [selectedYear]);

  const formatCurrency = (val: number | null) => {
    if (val == null) return '—';
    return new Intl.NumberFormat('en-ZA', { style: 'currency', currency: 'ZAR', notation: 'compact' }).format(val);
  };

  const varianceColor = (v: number) => v > 0 ? 'text-red-600' : v < 0 ? 'text-blue-600' : 'text-green-600';

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white shadow">
        <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Headcount Planning</h1>
              <p className="mt-1 text-sm text-gray-600">
                Budget and headcount planning by department
              </p>
            </div>
            <div className="flex items-center gap-3">
              <select
                value={selectedYear}
                onChange={e => setSelectedYear(Number(e.target.value))}
                className="border border-gray-300 rounded-md px-3 py-2 text-sm focus:ring-indigo-500 focus:border-indigo-500"
              >
                {fiscalYears.map(y => (
                  <option key={y} value={y}>FY {y}</option>
                ))}
              </select>
              <a href="/org" className="text-sm text-gray-500 hover:text-gray-700">← Org Chart</a>
              <button className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700">
                + Add Plan
              </button>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        {/* Year Summary */}
        <div className="grid grid-cols-1 sm:grid-cols-4 gap-4 mb-6">
          {[
            { label: 'Planned HC', value: summary.totalPlannedHeadcount },
            { label: 'Current HC', value: summary.totalCurrentHeadcount },
            { label: 'Variance', value: summary.totalVariance, suffix: ' open' },
            { label: 'Total Budget', value: formatCurrency(summary.totalBudget) },
          ].map(card => (
            <div key={card.label} className="bg-white shadow rounded-lg p-4">
              <p className="text-xs font-medium text-gray-500 uppercase">{card.label}</p>
              <p className="text-2xl font-bold text-gray-900">
                {card.value}{card.suffix ?? ''}
              </p>
            </div>
          ))}
        </div>

        {/* Plans Table */}
        <div className="bg-white shadow rounded-lg overflow-hidden">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                {['Department', 'Planned HC', 'Current HC', 'Variance', 'Budget', 'Forecast Vacancies', 'New Requests', 'Org Unit'].map(h => (
                  <th key={h} className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {loading ? (
                <tr>
                  <td colSpan={8} className="px-6 py-8 text-center text-gray-400">Loading...</td>
                </tr>
              ) : plans.length === 0 ? (
                <tr>
                  <td colSpan={8} className="px-6 py-8 text-center text-gray-400">No plans for this fiscal year</td>
                </tr>
              ) : (
                plans.map(plan => (
                  <tr key={plan.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 text-sm font-medium text-gray-900">{plan.department}</td>
                    <td className="px-6 py-4 text-sm text-gray-600 text-center">{plan.plannedHeadcount}</td>
                    <td className="px-6 py-4 text-sm text-gray-600 text-center">{plan.currentHeadcount}</td>
                    <td className="px-6 py-4 text-sm text-center">
                      <span className={`font-medium ${varianceColor(plan.variance)}`}>
                        {plan.variance > 0 ? '+' : ''}{plan.variance}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-600">{formatCurrency(plan.budget)}</td>
                    <td className="px-6 py-4 text-sm text-center text-orange-700">{plan.forecastVacancies}</td>
                    <td className="px-6 py-4 text-sm text-center text-blue-700">{plan.newPositionRequests}</td>
                    <td className="px-6 py-4 text-sm text-gray-500">{plan.orgUnitName || '—'}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Bar Chart Visualization */}
        <div className="mt-6 bg-white shadow rounded-lg p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Planned vs Current by Department</h3>
          <div className="space-y-4">
            {plans.map(plan => {
              const maxHC = Math.max(...plans.map(p => p.plannedHeadcount));
              const plannedPct = (plan.plannedHeadcount / maxHC) * 100;
              const currentPct = (plan.currentHeadcount / maxHC) * 100;
              return (
                <div key={plan.id}>
                  <div className="flex justify-between text-sm mb-1">
                    <span className="font-medium text-gray-700">{plan.department}</span>
                    <span className="text-gray-500">{plan.currentHeadcount} / {plan.plannedHeadcount}</span>
                  </div>
                  <div className="relative h-5 bg-gray-100 rounded-full overflow-hidden">
                    <div
                      className="absolute h-full bg-indigo-200 rounded-full"
                      style={{ width: `${plannedPct}%` }}
                    />
                    <div
                      className="absolute h-full bg-indigo-600 rounded-full"
                      style={{ width: `${currentPct}%` }}
                    />
                  </div>
                </div>
              );
            })}
          </div>
          <div className="mt-3 flex gap-6 text-xs text-gray-500">
            <span className="flex items-center gap-1"><span className="w-3 h-3 rounded-full bg-indigo-600 inline-block" /> Current</span>
            <span className="flex items-center gap-1"><span className="w-3 h-3 rounded-full bg-indigo-200 inline-block" /> Planned</span>
          </div>
        </div>
      </div>
    </div>
  );
}
