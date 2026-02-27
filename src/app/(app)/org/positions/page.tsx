'use client';

import React, { useState, useEffect } from 'react';

interface Position {
  id: number;
  title: string;
  code: string;
  department: string;
  grade: string;
  fte: number;
  status: string;
  isVacant: boolean;
  jobSharingAllowed: boolean;
  currentEmployeeName: string | null;
  orgUnitName: string | null;
  location: string | null;
  reportingPositionTitle: string | null;
}

const DEMO_POSITIONS: Position[] = [
  {
    id: 1, title: 'Chief Technology Officer', code: 'CTO-001', department: 'Technology Division',
    grade: 'E1', fte: 1, status: 'ACTIVE', isVacant: false, jobSharingAllowed: false,
    currentEmployeeName: 'Alex Johnson', orgUnitName: 'Technology Division', location: 'Head Office',
    reportingPositionTitle: 'Chief Executive Officer',
  },
  {
    id: 2, title: 'VP Engineering', code: 'ENG-VP-001', department: 'Engineering',
    grade: 'E2', fte: 1, status: 'ACTIVE', isVacant: false, jobSharingAllowed: false,
    currentEmployeeName: 'Sarah Williams', orgUnitName: 'Engineering', location: 'Head Office',
    reportingPositionTitle: 'Chief Technology Officer',
  },
  {
    id: 3, title: 'Senior Software Engineer', code: 'ENG-SSE-001', department: 'Engineering',
    grade: 'L4', fte: 1, status: 'ACTIVE', isVacant: true, jobSharingAllowed: false,
    currentEmployeeName: null, orgUnitName: 'Backend Team', location: 'Johannesburg',
    reportingPositionTitle: 'VP Engineering',
  },
  {
    id: 4, title: 'Software Engineer', code: 'ENG-SE-001', department: 'Engineering',
    grade: 'L3', fte: 1, status: 'ACTIVE', isVacant: true, jobSharingAllowed: true,
    currentEmployeeName: null, orgUnitName: 'Frontend Team', location: 'Cape Town',
    reportingPositionTitle: 'VP Engineering',
  },
  {
    id: 5, title: 'HR Business Partner', code: 'HR-BP-001', department: 'Human Resources',
    grade: 'L3', fte: 0.5, status: 'ACTIVE', isVacant: false, jobSharingAllowed: true,
    currentEmployeeName: 'Maria Santos', orgUnitName: 'Human Resources', location: 'Head Office',
    reportingPositionTitle: 'CHRO',
  },
];

export default function PositionsPage() {
  const [positions, setPositions] = useState<Position[]>(DEMO_POSITIONS);
  const [filterDept, setFilterDept] = useState('');
  const [filterStatus, setFilterStatus] = useState('');
  const [filterVacant, setFilterVacant] = useState('');
  const [loading, setLoading] = useState(false);
  const [summary, setSummary] = useState({ totalPositions: 5, vacantPositions: 2, filledPositions: 3, vacancyRate: 40 });

  useEffect(() => {
    setLoading(true);
    const params = new URLSearchParams();
    if (filterDept) params.set('department', filterDept);
    if (filterStatus) params.set('status', filterStatus);
    if (filterVacant) params.set('isVacant', filterVacant);

    Promise.all([
      fetch(`/api/org/positions?${params}`),
      fetch('/api/org/positions/vacancy-summary'),
    ])
      .then(async ([posRes, sumRes]) => {
        if (posRes.ok) {
          const data = await posRes.json();
          if (data.content && data.content.length > 0) setPositions(data.content);
        }
        if (sumRes.ok) {
          const sumData = await sumRes.json();
          setSummary(sumData);
        }
      })
      .catch(() => {/* use demo */})
      .finally(() => setLoading(false));
  }, [filterDept, filterStatus, filterVacant]);

  const filtered = positions.filter(p => {
    if (filterDept && !p.department.toLowerCase().includes(filterDept.toLowerCase())) return false;
    if (filterStatus && p.status !== filterStatus) return false;
    if (filterVacant === 'true' && !p.isVacant) return false;
    if (filterVacant === 'false' && p.isVacant) return false;
    return true;
  });

  const statusColors: Record<string, string> = {
    ACTIVE: 'bg-green-100 text-green-800',
    INACTIVE: 'bg-red-100 text-red-800',
    FROZEN: 'bg-gray-100 text-gray-800',
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white shadow">
        <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Position Management</h1>
              <p className="mt-1 text-sm text-gray-600">
                Manage all positions independently of employees
              </p>
            </div>
            <div className="flex gap-3">
              <a href="/org" className="text-sm text-gray-500 hover:text-gray-700 self-center">
                ← Org Chart
              </a>
              <button className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700">
                + New Position
              </button>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        {/* Summary Cards */}
        <div className="grid grid-cols-1 sm:grid-cols-4 gap-4 mb-6">
          {[
            { label: 'Total Positions', value: summary.totalPositions, color: 'text-gray-900' },
            { label: 'Filled', value: summary.filledPositions, color: 'text-green-700' },
            { label: 'Vacant', value: summary.vacantPositions, color: 'text-red-700' },
            { label: 'Vacancy Rate', value: `${summary.vacancyRate?.toFixed(1) ?? 0}%`, color: 'text-orange-700' },
          ].map(card => (
            <div key={card.label} className="bg-white shadow rounded-lg p-4">
              <p className="text-xs font-medium text-gray-500 uppercase">{card.label}</p>
              <p className={`text-2xl font-bold ${card.color}`}>{card.value}</p>
            </div>
          ))}
        </div>

        {/* Filters */}
        <div className="bg-white shadow rounded-lg p-4 mb-6">
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <input
              type="text"
              placeholder="Filter by department..."
              value={filterDept}
              onChange={e => setFilterDept(e.target.value)}
              className="border border-gray-300 rounded-md px-3 py-2 text-sm focus:ring-indigo-500 focus:border-indigo-500"
            />
            <select
              value={filterStatus}
              onChange={e => setFilterStatus(e.target.value)}
              className="border border-gray-300 rounded-md px-3 py-2 text-sm focus:ring-indigo-500 focus:border-indigo-500"
            >
              <option value="">All Statuses</option>
              <option value="ACTIVE">Active</option>
              <option value="INACTIVE">Inactive</option>
              <option value="FROZEN">Frozen</option>
            </select>
            <select
              value={filterVacant}
              onChange={e => setFilterVacant(e.target.value)}
              className="border border-gray-300 rounded-md px-3 py-2 text-sm focus:ring-indigo-500 focus:border-indigo-500"
            >
              <option value="">All Positions</option>
              <option value="true">Vacant Only</option>
              <option value="false">Filled Only</option>
            </select>
          </div>
        </div>

        {/* Positions Table */}
        <div className="bg-white shadow rounded-lg overflow-hidden">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                {['Title', 'Code', 'Department', 'Grade', 'FTE', 'Status', 'Vacancy', 'Current Employee', 'Org Unit'].map(h => (
                  <th key={h} className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {loading ? (
                <tr>
                  <td colSpan={9} className="px-6 py-8 text-center text-gray-400">Loading...</td>
                </tr>
              ) : filtered.length === 0 ? (
                <tr>
                  <td colSpan={9} className="px-6 py-8 text-center text-gray-400">No positions found</td>
                </tr>
              ) : (
                filtered.map(pos => (
                  <tr key={pos.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm font-medium text-gray-900">{pos.title}</div>
                      {pos.reportingPositionTitle && (
                        <div className="text-xs text-gray-500">Reports to: {pos.reportingPositionTitle}</div>
                      )}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-600">{pos.code}</td>
                    <td className="px-6 py-4 text-sm text-gray-600">{pos.department}</td>
                    <td className="px-6 py-4 text-sm text-gray-600">{pos.grade}</td>
                    <td className="px-6 py-4 text-sm text-gray-600">{pos.fte}</td>
                    <td className="px-6 py-4">
                      <span className={`text-xs px-2 py-1 rounded-full font-medium ${statusColors[pos.status] || 'bg-gray-100 text-gray-800'}`}>
                        {pos.status}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <span className={`text-xs px-2 py-1 rounded-full font-medium ${pos.isVacant ? 'bg-red-100 text-red-800' : 'bg-green-100 text-green-800'}`}>
                        {pos.isVacant ? 'Vacant' : 'Filled'}
                      </span>
                      {pos.jobSharingAllowed && (
                        <span className="ml-1 text-xs px-1 py-0.5 rounded bg-blue-50 text-blue-700">Shared</span>
                      )}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-600">
                      {pos.currentEmployeeName || <span className="text-gray-400 italic">—</span>}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-600">{pos.orgUnitName}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
