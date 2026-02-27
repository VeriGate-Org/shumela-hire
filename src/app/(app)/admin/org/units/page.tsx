'use client';

import React, { useState, useEffect } from 'react';

interface OrgUnit {
  id: number;
  name: string;
  code: string;
  unitType: string;
  parentId: number | null;
  parentName: string | null;
  managerId: number | null;
  managerName: string | null;
  costCentre: string | null;
  description: string | null;
  isActive: boolean;
}

const UNIT_TYPES = ['COMPANY', 'DIVISION', 'DEPARTMENT', 'TEAM', 'SITE'];

const DEMO_UNITS: OrgUnit[] = [
  { id: 1, name: 'Shumela Corporation', code: 'CORP', unitType: 'COMPANY', parentId: null, parentName: null, managerId: null, managerName: 'CEO', costCentre: 'CC-001', description: 'Root entity', isActive: true },
  { id: 2, name: 'Technology Division', code: 'TECH', unitType: 'DIVISION', parentId: 1, parentName: 'Shumela Corporation', managerId: null, managerName: 'CTO', costCentre: 'CC-100', description: null, isActive: true },
  { id: 3, name: 'Engineering', code: 'ENG', unitType: 'DEPARTMENT', parentId: 2, parentName: 'Technology Division', managerId: null, managerName: 'VP Engineering', costCentre: 'CC-101', description: null, isActive: true },
  { id: 4, name: 'Product', code: 'PROD', unitType: 'DEPARTMENT', parentId: 2, parentName: 'Technology Division', managerId: null, managerName: 'VP Product', costCentre: 'CC-102', description: null, isActive: true },
  { id: 5, name: 'Human Resources', code: 'HR', unitType: 'DEPARTMENT', parentId: 1, parentName: 'Shumela Corporation', managerId: null, managerName: 'CHRO', costCentre: 'CC-200', description: null, isActive: true },
  { id: 6, name: 'Frontend Team', code: 'FE', unitType: 'TEAM', parentId: 3, parentName: 'Engineering', managerId: null, managerName: null, costCentre: null, description: null, isActive: true },
  { id: 7, name: 'Backend Team', code: 'BE', unitType: 'TEAM', parentId: 3, parentName: 'Engineering', managerId: null, managerName: null, costCentre: null, description: null, isActive: true },
];

interface FormState {
  name: string;
  code: string;
  unitType: string;
  parentId: string;
  costCentre: string;
  description: string;
}

const emptyForm: FormState = {
  name: '', code: '', unitType: 'DEPARTMENT', parentId: '', costCentre: '', description: '',
};

export default function OrgUnitAdminPage() {
  const [units, setUnits] = useState<OrgUnit[]>(DEMO_UNITS);
  const [filterType, setFilterType] = useState('');
  const [filterActive, setFilterActive] = useState('true');
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [form, setForm] = useState<FormState>(emptyForm);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    fetch('/api/org/units')
      .then(res => { if (!res.ok) throw new Error(); return res.json(); })
      .then((data: OrgUnit[]) => { if (data.length > 0) setUnits(data); })
      .catch(() => {/* use demo */});
  }, []);

  const filtered = units.filter(u => {
    if (filterType && u.unitType !== filterType) return false;
    if (filterActive === 'true' && !u.isActive) return false;
    if (filterActive === 'false' && u.isActive) return false;
    return true;
  });

  const typeColors: Record<string, string> = {
    COMPANY: 'bg-indigo-100 text-indigo-800',
    DIVISION: 'bg-blue-100 text-blue-800',
    DEPARTMENT: 'bg-green-100 text-green-800',
    TEAM: 'bg-yellow-100 text-yellow-800',
    SITE: 'bg-purple-100 text-purple-800',
  };

  const openCreate = () => {
    setEditingId(null);
    setForm(emptyForm);
    setError('');
    setShowForm(true);
  };

  const openEdit = (unit: OrgUnit) => {
    setEditingId(unit.id);
    setForm({
      name: unit.name,
      code: unit.code,
      unitType: unit.unitType,
      parentId: unit.parentId?.toString() ?? '',
      costCentre: unit.costCentre ?? '',
      description: unit.description ?? '',
    });
    setError('');
    setShowForm(true);
  };

  const handleSave = async () => {
    if (!form.name.trim()) { setError('Name is required'); return; }
    setSaving(true);
    setError('');

    const payload = {
      name: form.name,
      code: form.code || null,
      unitType: form.unitType,
      parentId: form.parentId ? Number(form.parentId) : null,
      costCentre: form.costCentre || null,
      description: form.description || null,
      isActive: true,
    };

    try {
      const url = editingId ? `/api/org/units/${editingId}` : '/api/org/units';
      const method = editingId ? 'PUT' : 'POST';
      const res = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });

      if (res.ok) {
        const saved: OrgUnit = await res.json();
        if (editingId) {
          setUnits(prev => prev.map(u => u.id === editingId ? saved : u));
        } else {
          setUnits(prev => [...prev, saved]);
        }
        setShowForm(false);
      } else {
        const err = await res.json();
        setError(err.error || 'Failed to save');
      }
    } catch {
      // Demo mode — update local state
      if (editingId) {
        setUnits(prev => prev.map(u =>
          u.id === editingId
            ? {
                ...u,
                name: payload.name,
                code: payload.code ?? '',
                unitType: payload.unitType,
                parentId: payload.parentId,
                parentName: units.find(x => x.id === payload.parentId)?.name ?? null,
                costCentre: payload.costCentre ?? null,
                description: payload.description ?? null,
              }
            : u
        ));
      } else {
        const newId = Math.max(...units.map(u => u.id)) + 1;
        const parentUnit = units.find(u => u.id === payload.parentId);
        const newUnit: OrgUnit = {
          id: newId,
          name: payload.name,
          code: payload.code ?? '',
          unitType: payload.unitType,
          parentId: payload.parentId,
          parentName: parentUnit?.name ?? null,
          managerId: null,
          managerName: null,
          costCentre: payload.costCentre ?? null,
          description: payload.description ?? null,
          isActive: true,
        };
        setUnits(prev => [...prev, newUnit]);
      }
      setShowForm(false);
    } finally {
      setSaving(false);
    }
  };

  const handleDeactivate = async (id: number) => {
    try {
      const res = await fetch(`/api/org/units/${id}`, { method: 'DELETE' });
      if (res.ok || res.status === 204) {
        setUnits(prev => prev.map(u => u.id === id ? { ...u, isActive: false } : u));
      }
    } catch {
      setUnits(prev => prev.map(u => u.id === id ? { ...u, isActive: false } : u));
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white shadow">
        <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Org Unit Management</h1>
              <p className="mt-1 text-sm text-gray-600">Create and manage organizational units</p>
            </div>
            <div className="flex gap-3">
              <a href="/org" className="text-sm text-gray-500 hover:text-gray-700 self-center">← Org Chart</a>
              <button
                onClick={openCreate}
                className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700"
              >
                + New Unit
              </button>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        {/* Filters */}
        <div className="bg-white shadow rounded-lg p-4 mb-6">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <select
              value={filterType}
              onChange={e => setFilterType(e.target.value)}
              className="border border-gray-300 rounded-md px-3 py-2 text-sm focus:ring-indigo-500 focus:border-indigo-500"
            >
              <option value="">All Types</option>
              {UNIT_TYPES.map(t => <option key={t} value={t}>{t}</option>)}
            </select>
            <select
              value={filterActive}
              onChange={e => setFilterActive(e.target.value)}
              className="border border-gray-300 rounded-md px-3 py-2 text-sm focus:ring-indigo-500 focus:border-indigo-500"
            >
              <option value="">All Statuses</option>
              <option value="true">Active Only</option>
              <option value="false">Inactive Only</option>
            </select>
          </div>
        </div>

        {/* Table */}
        <div className="bg-white shadow rounded-lg overflow-hidden">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                {['Name', 'Code', 'Type', 'Parent', 'Manager', 'Cost Centre', 'Status', 'Actions'].map(h => (
                  <th key={h} className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filtered.map(unit => (
                <tr key={unit.id} className={`hover:bg-gray-50 ${!unit.isActive ? 'opacity-50' : ''}`}>
                  <td className="px-6 py-4 text-sm font-medium text-gray-900">{unit.name}</td>
                  <td className="px-6 py-4 text-sm text-gray-500">{unit.code}</td>
                  <td className="px-6 py-4">
                    <span className={`text-xs px-2 py-1 rounded-full font-medium ${typeColors[unit.unitType] || 'bg-gray-100 text-gray-800'}`}>
                      {unit.unitType}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-500">{unit.parentName || '—'}</td>
                  <td className="px-6 py-4 text-sm text-gray-500">{unit.managerName || '—'}</td>
                  <td className="px-6 py-4 text-sm text-gray-500">{unit.costCentre || '—'}</td>
                  <td className="px-6 py-4">
                    <span className={`text-xs px-2 py-1 rounded-full font-medium ${unit.isActive ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                      {unit.isActive ? 'Active' : 'Inactive'}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm">
                    <div className="flex gap-2">
                      <button
                        onClick={() => openEdit(unit)}
                        className="text-indigo-600 hover:text-indigo-800"
                      >
                        Edit
                      </button>
                      {unit.isActive && (
                        <button
                          onClick={() => handleDeactivate(unit.id)}
                          className="text-red-500 hover:text-red-700"
                        >
                          Deactivate
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Create/Edit Modal */}
        {showForm && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg shadow-xl w-full max-w-lg mx-4">
              <div className="px-6 py-4 border-b">
                <h2 className="text-lg font-semibold text-gray-900">
                  {editingId ? 'Edit Org Unit' : 'New Org Unit'}
                </h2>
              </div>
              <div className="px-6 py-4 space-y-4">
                {error && (
                  <div className="bg-red-50 border border-red-200 rounded-md p-3 text-sm text-red-700">
                    {error}
                  </div>
                )}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Name *</label>
                  <input
                    type="text"
                    value={form.name}
                    onChange={e => setForm(f => ({ ...f, name: e.target.value }))}
                    className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:ring-indigo-500 focus:border-indigo-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Code</label>
                  <input
                    type="text"
                    value={form.code}
                    onChange={e => setForm(f => ({ ...f, code: e.target.value }))}
                    className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:ring-indigo-500 focus:border-indigo-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Type *</label>
                  <select
                    value={form.unitType}
                    onChange={e => setForm(f => ({ ...f, unitType: e.target.value }))}
                    className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:ring-indigo-500 focus:border-indigo-500"
                  >
                    {UNIT_TYPES.map(t => <option key={t} value={t}>{t}</option>)}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Parent Unit</label>
                  <select
                    value={form.parentId}
                    onChange={e => setForm(f => ({ ...f, parentId: e.target.value }))}
                    className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:ring-indigo-500 focus:border-indigo-500"
                  >
                    <option value="">— No Parent (Root) —</option>
                    {units.filter(u => u.isActive && u.id !== editingId).map(u => (
                      <option key={u.id} value={u.id}>{u.name} ({u.unitType})</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Cost Centre</label>
                  <input
                    type="text"
                    value={form.costCentre}
                    onChange={e => setForm(f => ({ ...f, costCentre: e.target.value }))}
                    className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:ring-indigo-500 focus:border-indigo-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
                  <textarea
                    value={form.description}
                    onChange={e => setForm(f => ({ ...f, description: e.target.value }))}
                    rows={2}
                    className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:ring-indigo-500 focus:border-indigo-500"
                  />
                </div>
              </div>
              <div className="px-6 py-4 border-t flex justify-end gap-3">
                <button
                  onClick={() => setShowForm(false)}
                  className="px-4 py-2 border border-gray-300 rounded-md text-sm text-gray-700 hover:bg-gray-50"
                >
                  Cancel
                </button>
                <button
                  onClick={handleSave}
                  disabled={saving}
                  className="px-4 py-2 border border-transparent rounded-md text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50"
                >
                  {saving ? 'Saving...' : 'Save'}
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
