'use client';

import { useState, useEffect, useCallback } from 'react';
import Link from 'next/link';
import {
  MagnifyingGlassIcon,
  FunnelIcon,
  Squares2X2Icon,
  ListBulletIcon,
  ChevronLeftIcon,
  ChevronRightIcon,
} from '@heroicons/react/24/outline';
import type { Employee, EmployeeStatus, PageResponse } from '@/types/employee';
import { EMPLOYEE_STATUS_LABELS } from '@/types/employee';
import { searchEmployees, filterEmployees, getDepartments, getLocations, getJobTitles } from '@/services/employeeService';

const STATUS_COLORS: Record<EmployeeStatus, string> = {
  ACTIVE: 'bg-green-50 text-green-700 border-green-200',
  PROBATION: 'bg-yellow-50 text-yellow-700 border-yellow-200',
  SUSPENDED: 'bg-orange-50 text-orange-700 border-orange-200',
  TERMINATED: 'bg-red-50 text-red-700 border-red-200',
  RESIGNED: 'bg-gray-50 text-gray-600 border-gray-200',
  RETIRED: 'bg-blue-50 text-blue-700 border-blue-200',
};

interface EmployeeDirectoryProps {
  onCreateNew: () => void;
}

export default function EmployeeDirectory({ onCreateNew }: EmployeeDirectoryProps) {
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [view, setView] = useState<'grid' | 'list'>('grid');
  const [showFilters, setShowFilters] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  // Filter state
  const [filterDepartment, setFilterDepartment] = useState('');
  const [filterStatus, setFilterStatus] = useState('');
  const [filterJobTitle, setFilterJobTitle] = useState('');
  const [filterLocation, setFilterLocation] = useState('');

  // Lookup data
  const [departments, setDepartments] = useState<string[]>([]);
  const [locations, setLocations] = useState<string[]>([]);
  const [jobTitles, setJobTitlesList] = useState<string[]>([]);

  useEffect(() => {
    getDepartments().then(setDepartments);
    getLocations().then(setLocations);
    getJobTitles().then(setJobTitlesList);
  }, []);

  const loadEmployees = useCallback(async () => {
    setLoading(true);
    try {
      let result: PageResponse<Employee>;
      const hasFilters = filterDepartment || filterStatus || filterJobTitle || filterLocation;

      if (hasFilters) {
        result = await filterEmployees({
          department: filterDepartment || undefined,
          status: (filterStatus as EmployeeStatus) || undefined,
          jobTitle: filterJobTitle || undefined,
          location: filterLocation || undefined,
          page,
          size: 12,
        });
      } else {
        result = await searchEmployees({
          search: search || undefined,
          page,
          size: 12,
          sort: 'lastName',
          direction: 'asc',
        });
      }

      setEmployees(result.content);
      setTotalPages(result.totalPages);
      setTotalElements(result.totalElements);
    } catch {
      setEmployees([]);
    } finally {
      setLoading(false);
    }
  }, [search, page, filterDepartment, filterStatus, filterJobTitle, filterLocation]);

  useEffect(() => {
    const timer = setTimeout(loadEmployees, 300);
    return () => clearTimeout(timer);
  }, [loadEmployees]);

  const clearFilters = () => {
    setFilterDepartment('');
    setFilterStatus('');
    setFilterJobTitle('');
    setFilterLocation('');
    setPage(0);
  };

  const getInitials = (firstName: string, lastName: string) => {
    return `${firstName.charAt(0)}${lastName.charAt(0)}`.toUpperCase();
  };

  return (
    <div className="space-y-4">
      {/* Search & Controls Bar */}
      <div className="flex flex-col sm:flex-row gap-3">
        <div className="relative flex-1">
          <MagnifyingGlassIcon className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
          <input
            type="text"
            placeholder="Search by name, email, employee number..."
            value={search}
            onChange={(e) => { setSearch(e.target.value); setPage(0); }}
            className="w-full pl-9 pr-3 py-2 border border-border rounded-sm text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary bg-card text-foreground"
          />
        </div>

        <div className="flex gap-2">
          <button
            onClick={() => setShowFilters(!showFilters)}
            className={`inline-flex items-center gap-1.5 px-3 py-2 text-sm font-medium border rounded-sm ${
              showFilters ? 'border-primary text-primary bg-primary/5' : 'border-border text-muted-foreground hover:text-foreground hover:bg-accent'
            }`}
          >
            <FunnelIcon className="w-4 h-4" />
            Filters
          </button>

          <div className="flex border border-border rounded-sm">
            <button
              onClick={() => setView('grid')}
              className={`px-2.5 py-2 ${view === 'grid' ? 'bg-primary/10 text-primary' : 'text-muted-foreground hover:text-foreground'}`}
              aria-label="Grid view"
            >
              <Squares2X2Icon className="w-4 h-4" />
            </button>
            <button
              onClick={() => setView('list')}
              className={`px-2.5 py-2 ${view === 'list' ? 'bg-primary/10 text-primary' : 'text-muted-foreground hover:text-foreground'}`}
              aria-label="List view"
            >
              <ListBulletIcon className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>

      {/* Filter Panel */}
      {showFilters && (
        <div className="enterprise-card p-4">
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
            <div>
              <label className="block text-xs font-semibold text-muted-foreground uppercase tracking-wide mb-1">Department</label>
              <select
                value={filterDepartment}
                onChange={(e) => { setFilterDepartment(e.target.value); setPage(0); }}
                className="w-full px-3 py-2 border border-border rounded-sm text-sm bg-card text-foreground focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
              >
                <option value="">All Departments</option>
                {departments.map(d => <option key={d} value={d}>{d}</option>)}
              </select>
            </div>
            <div>
              <label className="block text-xs font-semibold text-muted-foreground uppercase tracking-wide mb-1">Status</label>
              <select
                value={filterStatus}
                onChange={(e) => { setFilterStatus(e.target.value); setPage(0); }}
                className="w-full px-3 py-2 border border-border rounded-sm text-sm bg-card text-foreground focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
              >
                <option value="">All Statuses</option>
                {(Object.keys(EMPLOYEE_STATUS_LABELS) as EmployeeStatus[]).map(s => (
                  <option key={s} value={s}>{EMPLOYEE_STATUS_LABELS[s]}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-xs font-semibold text-muted-foreground uppercase tracking-wide mb-1">Job Title</label>
              <select
                value={filterJobTitle}
                onChange={(e) => { setFilterJobTitle(e.target.value); setPage(0); }}
                className="w-full px-3 py-2 border border-border rounded-sm text-sm bg-card text-foreground focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
              >
                <option value="">All Job Titles</option>
                {jobTitles.map(t => <option key={t} value={t}>{t}</option>)}
              </select>
            </div>
            <div>
              <label className="block text-xs font-semibold text-muted-foreground uppercase tracking-wide mb-1">Location</label>
              <select
                value={filterLocation}
                onChange={(e) => { setFilterLocation(e.target.value); setPage(0); }}
                className="w-full px-3 py-2 border border-border rounded-sm text-sm bg-card text-foreground focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
              >
                <option value="">All Locations</option>
                {locations.map(l => <option key={l} value={l}>{l}</option>)}
              </select>
            </div>
          </div>
          {(filterDepartment || filterStatus || filterJobTitle || filterLocation) && (
            <div className="mt-3 flex justify-end">
              <button onClick={clearFilters} className="text-sm text-primary hover:underline">
                Clear all filters
              </button>
            </div>
          )}
        </div>
      )}

      {/* Results count */}
      <div className="flex items-center justify-between text-sm text-muted-foreground">
        <span>{totalElements} employee{totalElements !== 1 ? 's' : ''} found</span>
      </div>

      {/* Loading */}
      {loading && (
        <div className="flex items-center justify-center py-16">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-cta" />
        </div>
      )}

      {/* Empty State */}
      {!loading && employees.length === 0 && (
        <div className="enterprise-card flex flex-col items-center justify-center py-16 px-4">
          <svg className="w-16 h-16 text-muted-foreground/40 mb-4" fill="none" viewBox="0 0 24 24" strokeWidth={1} stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" d="M18 18.72a9.094 9.094 0 003.741-.479 3 3 0 00-4.682-2.72m.94 3.198l.001.031c0 .225-.012.447-.037.666A11.944 11.944 0 0112 21c-2.17 0-4.207-.576-5.963-1.584A6.062 6.062 0 016 18.719m12 0a5.971 5.971 0 00-.941-3.197m0 0A5.995 5.995 0 0012 12.75a5.995 5.995 0 00-5.058 2.772m0 0a3 3 0 00-4.681 2.72 8.986 8.986 0 003.74.477m.94-3.197a5.971 5.971 0 00-.94 3.197M15 6.75a3 3 0 11-6 0 3 3 0 016 0zm6 3a2.25 2.25 0 11-4.5 0 2.25 2.25 0 014.5 0zm-13.5 0a2.25 2.25 0 11-4.5 0 2.25 2.25 0 014.5 0z" />
          </svg>
          <h3 className="text-lg font-bold text-foreground mb-1">No employees found</h3>
          <p className="text-sm text-muted-foreground text-center max-w-sm mb-6">
            {search || filterDepartment || filterStatus ? 'Try adjusting your search or filters.' : 'Add your first employee to get started.'}
          </p>
          {!search && !filterDepartment && !filterStatus && (
            <button onClick={onCreateNew} className="btn-primary text-sm">
              Add Employee
            </button>
          )}
        </div>
      )}

      {/* Grid View */}
      {!loading && employees.length > 0 && view === 'grid' && (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
          {employees.map((emp) => (
            <Link
              key={emp.id}
              href={`/employees/${emp.id}`}
              className="enterprise-card p-4 hover:shadow-md transition-shadow group"
            >
              <div className="flex items-start gap-3">
                <div className="w-10 h-10 rounded-full bg-primary/10 text-primary flex items-center justify-center text-sm font-semibold shrink-0">
                  {emp.profilePhotoUrl ? (
                    <img src={emp.profilePhotoUrl} alt="" className="w-10 h-10 rounded-full object-cover" />
                  ) : (
                    getInitials(emp.firstName, emp.lastName)
                  )}
                </div>
                <div className="min-w-0 flex-1">
                  <h3 className="text-sm font-semibold text-foreground truncate group-hover:text-primary">
                    {emp.displayName || emp.fullName}
                  </h3>
                  <p className="text-xs text-muted-foreground truncate">{emp.jobTitle || 'No title'}</p>
                </div>
              </div>
              <div className="mt-3 space-y-1.5">
                <p className="text-xs text-muted-foreground truncate">{emp.department || '—'}</p>
                <p className="text-xs text-muted-foreground truncate">{emp.location || '—'}</p>
                <p className="text-xs text-muted-foreground truncate">{emp.email}</p>
              </div>
              <div className="mt-3">
                <span className={`inline-flex items-center rounded-full border px-2 py-0.5 text-xs font-medium ${STATUS_COLORS[emp.status]}`}>
                  {EMPLOYEE_STATUS_LABELS[emp.status]}
                </span>
              </div>
            </Link>
          ))}
        </div>
      )}

      {/* List View */}
      {!loading && employees.length > 0 && view === 'list' && (
        <div className="enterprise-card overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-border">
                  <th className="text-left text-xs font-semibold text-muted-foreground uppercase tracking-wide px-4 py-3">Employee</th>
                  <th className="text-left text-xs font-semibold text-muted-foreground uppercase tracking-wide px-4 py-3">Department</th>
                  <th className="text-left text-xs font-semibold text-muted-foreground uppercase tracking-wide px-4 py-3">Job Title</th>
                  <th className="text-left text-xs font-semibold text-muted-foreground uppercase tracking-wide px-4 py-3">Location</th>
                  <th className="text-left text-xs font-semibold text-muted-foreground uppercase tracking-wide px-4 py-3">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-border">
                {employees.map((emp) => (
                  <tr key={emp.id} className="hover:bg-accent/50 transition-colors">
                    <td className="px-4 py-3">
                      <Link href={`/employees/${emp.id}`} className="flex items-center gap-3 group">
                        <div className="w-8 h-8 rounded-full bg-primary/10 text-primary flex items-center justify-center text-xs font-semibold shrink-0">
                          {emp.profilePhotoUrl ? (
                            <img src={emp.profilePhotoUrl} alt="" className="w-8 h-8 rounded-full object-cover" />
                          ) : (
                            getInitials(emp.firstName, emp.lastName)
                          )}
                        </div>
                        <div className="min-w-0">
                          <p className="text-sm font-medium text-foreground truncate group-hover:text-primary">
                            {emp.displayName || emp.fullName}
                          </p>
                          <p className="text-xs text-muted-foreground truncate">{emp.email}</p>
                        </div>
                      </Link>
                    </td>
                    <td className="px-4 py-3 text-sm text-foreground">{emp.department || '—'}</td>
                    <td className="px-4 py-3 text-sm text-foreground">{emp.jobTitle || '—'}</td>
                    <td className="px-4 py-3 text-sm text-foreground">{emp.location || '—'}</td>
                    <td className="px-4 py-3">
                      <span className={`inline-flex items-center rounded-full border px-2 py-0.5 text-xs font-medium ${STATUS_COLORS[emp.status]}`}>
                        {EMPLOYEE_STATUS_LABELS[emp.status]}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Pagination */}
      {!loading && totalPages > 1 && (
        <div className="flex items-center justify-between">
          <p className="text-sm text-muted-foreground">
            Page {page + 1} of {totalPages}
          </p>
          <div className="flex gap-1">
            <button
              onClick={() => setPage(p => Math.max(0, p - 1))}
              disabled={page === 0}
              className="inline-flex items-center px-3 py-1.5 text-sm border border-border rounded-sm disabled:opacity-40 hover:bg-accent"
            >
              <ChevronLeftIcon className="w-4 h-4" />
            </button>
            <button
              onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
              disabled={page >= totalPages - 1}
              className="inline-flex items-center px-3 py-1.5 text-sm border border-border rounded-sm disabled:opacity-40 hover:bg-accent"
            >
              <ChevronRightIcon className="w-4 h-4" />
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
