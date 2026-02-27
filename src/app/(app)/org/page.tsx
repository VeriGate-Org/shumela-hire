'use client';

import React, { useState, useEffect, useCallback } from 'react';

interface OrgUnit {
  id: number;
  name: string;
  code: string;
  unitType: string;
  parentId: number | null;
  managerId: number | null;
  managerName: string | null;
  costCentre: string | null;
  isActive: boolean;
  children: OrgUnit[];
}

interface OrgNodeProps {
  node: OrgUnit;
  level: number;
  onSelect: (node: OrgUnit) => void;
  selected: OrgUnit | null;
}

function OrgNode({ node, level, onSelect, selected }: OrgNodeProps) {
  const [expanded, setExpanded] = useState(level < 2);
  const hasChildren = node.children && node.children.length > 0;
  const isSelected = selected?.id === node.id;

  const typeColors: Record<string, string> = {
    COMPANY: 'bg-indigo-100 text-indigo-800',
    DIVISION: 'bg-blue-100 text-blue-800',
    DEPARTMENT: 'bg-green-100 text-green-800',
    TEAM: 'bg-yellow-100 text-yellow-800',
    SITE: 'bg-purple-100 text-purple-800',
  };

  const color = typeColors[node.unitType] || 'bg-gray-100 text-gray-800';

  return (
    <div className={`pl-${level > 0 ? 4 : 0}`}>
      <div
        className={`flex items-center p-3 rounded-lg cursor-pointer border transition-all mb-1 ${
          isSelected ? 'border-indigo-500 bg-indigo-50' : 'border-transparent hover:bg-gray-50'
        }`}
        onClick={() => onSelect(node)}
      >
        {hasChildren ? (
          <button
            onClick={(e) => { e.stopPropagation(); setExpanded(!expanded); }}
            className="mr-2 text-gray-400 hover:text-gray-600 w-5 h-5 flex items-center justify-center"
          >
            {expanded ? '▼' : '▶'}
          </button>
        ) : (
          <span className="mr-2 w-5 h-5" />
        )}
        <div className="flex-1">
          <div className="flex items-center gap-2">
            <span className="font-medium text-gray-900">{node.name}</span>
            {node.code && (
              <span className="text-xs text-gray-500">({node.code})</span>
            )}
            <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${color}`}>
              {node.unitType}
            </span>
          </div>
          {node.managerName && (
            <p className="text-xs text-gray-500 mt-0.5">Manager: {node.managerName}</p>
          )}
          {node.costCentre && (
            <p className="text-xs text-gray-400">CC: {node.costCentre}</p>
          )}
        </div>
        <div className="text-xs text-gray-400">
          {node.children.length} sub-unit{node.children.length !== 1 ? 's' : ''}
        </div>
      </div>
      {expanded && hasChildren && (
        <div className="ml-6 border-l-2 border-gray-100 pl-4">
          {node.children.map(child => (
            <OrgNode
              key={child.id}
              node={child}
              level={level + 1}
              onSelect={onSelect}
              selected={selected}
            />
          ))}
        </div>
      )}
    </div>
  );
}

const DEMO_TREE: OrgUnit[] = [
  {
    id: 1,
    name: 'Shumela Corporation',
    code: 'CORP',
    unitType: 'COMPANY',
    parentId: null,
    managerId: null,
    managerName: 'CEO',
    costCentre: 'CC-001',
    isActive: true,
    children: [
      {
        id: 2,
        name: 'Technology Division',
        code: 'TECH',
        unitType: 'DIVISION',
        parentId: 1,
        managerId: null,
        managerName: 'CTO',
        costCentre: 'CC-100',
        isActive: true,
        children: [
          {
            id: 3,
            name: 'Engineering',
            code: 'ENG',
            unitType: 'DEPARTMENT',
            parentId: 2,
            managerId: null,
            managerName: 'VP Engineering',
            costCentre: 'CC-101',
            isActive: true,
            children: [
              {
                id: 6,
                name: 'Frontend Team',
                code: 'FE',
                unitType: 'TEAM',
                parentId: 3,
                managerId: null,
                managerName: 'Lead Developer',
                costCentre: null,
                isActive: true,
                children: [],
              },
              {
                id: 7,
                name: 'Backend Team',
                code: 'BE',
                unitType: 'TEAM',
                parentId: 3,
                managerId: null,
                managerName: 'Lead Developer',
                costCentre: null,
                isActive: true,
                children: [],
              },
            ],
          },
          {
            id: 4,
            name: 'Product',
            code: 'PROD',
            unitType: 'DEPARTMENT',
            parentId: 2,
            managerId: null,
            managerName: 'VP Product',
            costCentre: 'CC-102',
            isActive: true,
            children: [],
          },
        ],
      },
      {
        id: 5,
        name: 'Human Resources',
        code: 'HR',
        unitType: 'DEPARTMENT',
        parentId: 1,
        managerId: null,
        managerName: 'CHRO',
        costCentre: 'CC-200',
        isActive: true,
        children: [],
      },
    ],
  },
];

export default function OrgChartPage() {
  const [tree, setTree] = useState<OrgUnit[]>(DEMO_TREE);
  const [selected, setSelected] = useState<OrgUnit | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    // Attempt to load from API
    setLoading(true);
    fetch('/api/org/units/tree')
      .then(res => {
        if (!res.ok) throw new Error('API unavailable');
        return res.json();
      })
      .then(data => {
        if (Array.isArray(data) && data.length > 0) setTree(data);
      })
      .catch(() => {/* use demo data */})
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white shadow">
        <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Org Chart</h1>
              <p className="mt-1 text-sm text-gray-600">
                Interactive organizational structure — click to explore
              </p>
            </div>
            <div className="flex gap-3">
              <a
                href="/org/positions"
                className="inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
              >
                Positions
              </a>
              <a
                href="/org/headcount"
                className="inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
              >
                Headcount
              </a>
              <a
                href="/org/workforce"
                className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700"
              >
                Workforce Analytics
              </a>
            </div>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Org Tree */}
          <div className="lg:col-span-2 bg-white shadow rounded-lg p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">Organization Structure</h2>
            {loading ? (
              <div className="flex items-center justify-center h-48 text-gray-400">Loading...</div>
            ) : (
              <div className="overflow-auto max-h-[70vh]">
                {tree.map(root => (
                  <OrgNode
                    key={root.id}
                    node={root}
                    level={0}
                    onSelect={setSelected}
                    selected={selected}
                  />
                ))}
              </div>
            )}
          </div>

          {/* Detail Panel */}
          <div className="space-y-4">
            {selected ? (
              <div className="bg-white shadow rounded-lg p-6">
                <h3 className="text-lg font-semibold text-gray-900 mb-4">{selected.name}</h3>
                <dl className="space-y-3">
                  <div>
                    <dt className="text-xs font-medium text-gray-500 uppercase">Type</dt>
                    <dd className="text-sm text-gray-900">{selected.unitType}</dd>
                  </div>
                  {selected.code && (
                    <div>
                      <dt className="text-xs font-medium text-gray-500 uppercase">Code</dt>
                      <dd className="text-sm text-gray-900">{selected.code}</dd>
                    </div>
                  )}
                  {selected.managerName && (
                    <div>
                      <dt className="text-xs font-medium text-gray-500 uppercase">Manager</dt>
                      <dd className="text-sm text-gray-900">{selected.managerName}</dd>
                    </div>
                  )}
                  {selected.costCentre && (
                    <div>
                      <dt className="text-xs font-medium text-gray-500 uppercase">Cost Centre</dt>
                      <dd className="text-sm text-gray-900">{selected.costCentre}</dd>
                    </div>
                  )}
                  <div>
                    <dt className="text-xs font-medium text-gray-500 uppercase">Sub-units</dt>
                    <dd className="text-sm text-gray-900">{selected.children.length}</dd>
                  </div>
                  <div>
                    <dt className="text-xs font-medium text-gray-500 uppercase">Status</dt>
                    <dd>
                      <span className={`text-xs px-2 py-1 rounded-full font-medium ${selected.isActive ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                        {selected.isActive ? 'Active' : 'Inactive'}
                      </span>
                    </dd>
                  </div>
                </dl>
                <div className="mt-4 pt-4 border-t">
                  <a
                    href={`/admin/org/units`}
                    className="text-sm text-indigo-600 hover:text-indigo-800"
                  >
                    Manage units →
                  </a>
                </div>
              </div>
            ) : (
              <div className="bg-white shadow rounded-lg p-6 text-center text-gray-400">
                <p className="text-sm">Select a unit to view details</p>
              </div>
            )}

            {/* Quick Links */}
            <div className="bg-white shadow rounded-lg p-6">
              <h3 className="text-sm font-semibold text-gray-700 mb-3">Quick Actions</h3>
              <div className="space-y-2">
                <a
                  href="/org/positions?isVacant=true"
                  className="block text-sm text-indigo-600 hover:text-indigo-800"
                >
                  View vacant positions →
                </a>
                <a
                  href="/org/headcount"
                  className="block text-sm text-indigo-600 hover:text-indigo-800"
                >
                  View headcount plans →
                </a>
                <a
                  href="/org/workforce"
                  className="block text-sm text-indigo-600 hover:text-indigo-800"
                >
                  Workforce demographics →
                </a>
                <a
                  href="/admin/org/units"
                  className="block text-sm text-indigo-600 hover:text-indigo-800"
                >
                  Admin: Manage org units →
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
