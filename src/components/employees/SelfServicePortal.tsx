'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import {
  UserCircleIcon,
  DocumentTextIcon,
  ClockIcon,
  EnvelopeIcon,
  PhoneIcon,
  MapPinIcon,
  ExclamationTriangleIcon,
} from '@heroicons/react/24/outline';
import { useAuth } from '@/contexts/AuthContext';
import { useToast } from '@/components/Toast';
import type { Employee, EmployeeDocument } from '@/types/employee';
import { EMPLOYEE_STATUS_LABELS } from '@/types/employee';
import { searchEmployees, getDocuments } from '@/services/employeeService';

export default function SelfServicePortal() {
  const { user } = useAuth();
  const { toast } = useToast();
  const [employee, setEmployee] = useState<Employee | null>(null);
  const [documents, setDocuments] = useState<EmployeeDocument[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!user?.email) { setLoading(false); return; }

    // Look up employee by current user's email
    searchEmployees({ search: user.email, size: 1 })
      .then(async (page) => {
        if (page.content.length > 0) {
          const emp = page.content[0];
          setEmployee(emp);
          const docs = await getDocuments(emp.id).catch(() => []);
          setDocuments(docs);
        }
      })
      .catch(() => toast('Could not load your profile', 'error'))
      .finally(() => setLoading(false));
  }, [user?.email, toast]);

  if (loading) {
    return (
      <div className="flex items-center justify-center py-16">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-cta" />
      </div>
    );
  }

  if (!employee) {
    return (
      <div className="enterprise-card p-8 text-center">
        <UserCircleIcon className="w-16 h-16 mx-auto text-muted-foreground/40 mb-3" />
        <h3 className="text-lg font-bold text-foreground mb-1">No employee profile found</h3>
        <p className="text-sm text-muted-foreground">
          Your account is not linked to an employee record. Please contact HR for assistance.
        </p>
      </div>
    );
  }

  const expiringDocs = documents.filter(d => {
    if (!d.expiryDate || d.isExpired) return d.isExpired;
    const daysUntil = Math.ceil((new Date(d.expiryDate).getTime() - Date.now()) / (1000 * 60 * 60 * 24));
    return daysUntil <= 30;
  });

  const initials = `${employee.firstName.charAt(0)}${employee.lastName.charAt(0)}`.toUpperCase();

  return (
    <div className="space-y-6">
      {/* Profile Header */}
      <div className="enterprise-card p-6">
        <div className="flex flex-col sm:flex-row items-start gap-4">
          <div className="w-16 h-16 rounded-full bg-primary/10 text-primary flex items-center justify-center text-xl font-bold shrink-0">
            {employee.profilePhotoUrl ? (
              <img src={employee.profilePhotoUrl} alt="" className="w-16 h-16 rounded-full object-cover" />
            ) : (
              initials
            )}
          </div>
          <div className="flex-1">
            <h2 className="text-xl font-bold text-foreground">
              {employee.displayName || employee.fullName}
            </h2>
            <p className="text-sm text-muted-foreground">
              {employee.jobTitle} {employee.department ? `· ${employee.department}` : ''}
            </p>
            <p className="text-xs text-muted-foreground mt-0.5">{employee.employeeNumber}</p>
            <div className="flex flex-wrap gap-4 mt-3 text-xs text-muted-foreground">
              <span className="inline-flex items-center gap-1"><EnvelopeIcon className="w-3.5 h-3.5" />{employee.email}</span>
              {employee.phone && <span className="inline-flex items-center gap-1"><PhoneIcon className="w-3.5 h-3.5" />{employee.phone}</span>}
              {employee.location && <span className="inline-flex items-center gap-1"><MapPinIcon className="w-3.5 h-3.5" />{employee.location}</span>}
            </div>
          </div>
          <Link href={`/employees/${employee.id}`} className="btn-primary text-sm shrink-0">
            View Full Profile
          </Link>
        </div>
      </div>

      {/* Quick Info Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="enterprise-card p-4">
          <p className="text-xs font-semibold text-muted-foreground uppercase tracking-wide">Status</p>
          <p className="text-lg font-bold text-foreground mt-1">{EMPLOYEE_STATUS_LABELS[employee.status]}</p>
        </div>
        <div className="enterprise-card p-4">
          <p className="text-xs font-semibold text-muted-foreground uppercase tracking-wide">Hire Date</p>
          <p className="text-lg font-bold text-foreground mt-1">
            {employee.hireDate ? new Date(employee.hireDate).toLocaleDateString('en-ZA', { year: 'numeric', month: 'short', day: 'numeric' }) : '—'}
          </p>
        </div>
        <div className="enterprise-card p-4">
          <p className="text-xs font-semibold text-muted-foreground uppercase tracking-wide">Manager</p>
          <p className="text-lg font-bold text-foreground mt-1 truncate">{employee.reportingManagerName || '—'}</p>
        </div>
        <div className="enterprise-card p-4">
          <p className="text-xs font-semibold text-muted-foreground uppercase tracking-wide">Documents</p>
          <p className="text-lg font-bold text-foreground mt-1">{documents.length}</p>
        </div>
      </div>

      {/* Expiring documents alert */}
      {expiringDocs.length > 0 && (
        <div className="rounded-sm border border-yellow-200 bg-yellow-50 p-4">
          <div className="flex items-start gap-3">
            <ExclamationTriangleIcon className="w-5 h-5 text-yellow-600 shrink-0 mt-0.5" />
            <div>
              <h3 className="text-sm font-semibold text-yellow-800">Documents Requiring Attention</h3>
              <p className="text-sm text-yellow-700 mt-0.5">
                {expiringDocs.length} document{expiringDocs.length !== 1 ? 's' : ''} {expiringDocs.some(d => d.isExpired) ? 'expired or ' : ''}expiring soon.
              </p>
              <ul className="mt-2 space-y-1">
                {expiringDocs.map(d => (
                  <li key={d.id} className="text-xs text-yellow-700">
                    {d.title} — {d.isExpired ? 'Expired' : `Expires ${new Date(d.expiryDate!).toLocaleDateString('en-ZA')}`}
                  </li>
                ))}
              </ul>
            </div>
          </div>
        </div>
      )}

      {/* Quick Links */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <Link href={`/employees/${employee.id}#documents`} className="enterprise-card p-4 hover:shadow-md transition-shadow group">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-sm bg-primary/10 flex items-center justify-center">
              <DocumentTextIcon className="w-5 h-5 text-primary" />
            </div>
            <div>
              <p className="text-sm font-semibold text-foreground group-hover:text-primary">My Documents</p>
              <p className="text-xs text-muted-foreground">View and upload documents</p>
            </div>
          </div>
        </Link>

        <Link href={`/employees/${employee.id}#events`} className="enterprise-card p-4 hover:shadow-md transition-shadow group">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-sm bg-primary/10 flex items-center justify-center">
              <ClockIcon className="w-5 h-5 text-primary" />
            </div>
            <div>
              <p className="text-sm font-semibold text-foreground group-hover:text-primary">Employment History</p>
              <p className="text-xs text-muted-foreground">View your employment timeline</p>
            </div>
          </div>
        </Link>

        <Link href="/profile" className="enterprise-card p-4 hover:shadow-md transition-shadow group">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-sm bg-primary/10 flex items-center justify-center">
              <UserCircleIcon className="w-5 h-5 text-primary" />
            </div>
            <div>
              <p className="text-sm font-semibold text-foreground group-hover:text-primary">Account Settings</p>
              <p className="text-xs text-muted-foreground">Manage your account</p>
            </div>
          </div>
        </Link>
      </div>
    </div>
  );
}
