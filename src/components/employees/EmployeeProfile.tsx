'use client';

import { useState, useEffect } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { EnvelopeIcon, PhoneIcon, MapPinIcon } from '@heroicons/react/24/outline';
import type { Employee, EmployeeStatus } from '@/types/employee';
import { EMPLOYEE_STATUS_LABELS } from '@/types/employee';
import { getEmployee } from '@/services/employeeService';
import EmployeeForm from './EmployeeForm';
import DocumentManager from './DocumentManager';
import EmploymentTimeline from './EmploymentTimeline';

const STATUS_COLORS: Record<EmployeeStatus, string> = {
  ACTIVE: 'bg-green-50 text-green-700 border-green-200',
  PROBATION: 'bg-yellow-50 text-yellow-700 border-yellow-200',
  SUSPENDED: 'bg-orange-50 text-orange-700 border-orange-200',
  TERMINATED: 'bg-red-50 text-red-700 border-red-200',
  RESIGNED: 'bg-gray-50 text-gray-600 border-gray-200',
  RETIRED: 'bg-blue-50 text-blue-700 border-blue-200',
};

type Tab = 'personal' | 'employment' | 'documents' | 'events';

interface EmployeeProfileProps {
  employeeId: number;
}

export default function EmployeeProfile({ employeeId }: EmployeeProfileProps) {
  const { hasPermission } = useAuth();
  const canManage = hasPermission('manage_employees');
  const [employee, setEmployee] = useState<Employee | null>(null);
  const [loading, setLoading] = useState(true);
  const [tab, setTab] = useState<Tab>('personal');
  const [editing, setEditing] = useState(false);

  useEffect(() => {
    setLoading(true);
    getEmployee(employeeId)
      .then(setEmployee)
      .catch(() => setEmployee(null))
      .finally(() => setLoading(false));
  }, [employeeId]);

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
        <p className="text-sm text-muted-foreground">Employee not found.</p>
      </div>
    );
  }

  if (editing) {
    return (
      <div className="enterprise-card p-6">
        <h2 className="text-lg font-bold text-foreground mb-4">Edit Employee</h2>
        <EmployeeForm
          employee={employee}
          onSaved={(updated) => { setEmployee(updated); setEditing(false); }}
          onCancel={() => setEditing(false)}
        />
      </div>
    );
  }

  const initials = `${employee.firstName.charAt(0)}${employee.lastName.charAt(0)}`.toUpperCase();

  const DetailField = ({ label, value }: { label: string; value: string | null | undefined }) => (
    <div>
      <dt className="text-xs font-semibold text-muted-foreground uppercase tracking-wide">{label}</dt>
      <dd className="mt-0.5 text-sm text-foreground">{value || '—'}</dd>
    </div>
  );

  const tabs: { key: Tab; label: string }[] = [
    { key: 'personal', label: 'Personal Info' },
    { key: 'employment', label: 'Employment' },
    { key: 'documents', label: 'Documents' },
    { key: 'events', label: 'History' },
  ];

  return (
    <div className="space-y-6">
      {/* Header card */}
      <div className="enterprise-card p-6">
        <div className="flex flex-col sm:flex-row items-start gap-4">
          <div className="w-16 h-16 rounded-full bg-primary/10 text-primary flex items-center justify-center text-xl font-bold shrink-0">
            {employee.profilePhotoUrl ? (
              <img src={employee.profilePhotoUrl} alt="" className="w-16 h-16 rounded-full object-cover" />
            ) : (
              initials
            )}
          </div>
          <div className="flex-1 min-w-0">
            <div className="flex flex-wrap items-center gap-2">
              <h1 className="text-xl font-bold text-foreground">{employee.displayName || employee.fullName}</h1>
              <span className={`inline-flex items-center rounded-full border px-2 py-0.5 text-xs font-medium ${STATUS_COLORS[employee.status]}`}>
                {EMPLOYEE_STATUS_LABELS[employee.status]}
              </span>
            </div>
            <p className="text-sm text-muted-foreground mt-0.5">
              {employee.jobTitle || 'No title'} {employee.department ? `· ${employee.department}` : ''}
            </p>
            <p className="text-xs text-muted-foreground mt-0.5">
              {employee.employeeNumber}
            </p>
            <div className="flex flex-wrap gap-4 mt-3 text-xs text-muted-foreground">
              <span className="inline-flex items-center gap-1">
                <EnvelopeIcon className="w-3.5 h-3.5" />
                {employee.email}
              </span>
              {employee.phone && (
                <span className="inline-flex items-center gap-1">
                  <PhoneIcon className="w-3.5 h-3.5" />
                  {employee.phone}
                </span>
              )}
              {employee.location && (
                <span className="inline-flex items-center gap-1">
                  <MapPinIcon className="w-3.5 h-3.5" />
                  {employee.location}
                </span>
              )}
            </div>
          </div>
          {canManage && (
            <button onClick={() => setEditing(true)} className="btn-primary text-sm shrink-0">
              Edit
            </button>
          )}
        </div>
      </div>

      {/* Tabs */}
      <div className="flex gap-1 overflow-x-auto border-b border-border">
        {tabs.map(t => (
          <button
            key={t.key}
            onClick={() => setTab(t.key)}
            className={`px-4 py-2.5 text-sm font-medium whitespace-nowrap border-b-2 -mb-px transition-colors ${
              tab === t.key
                ? 'border-primary text-primary'
                : 'border-transparent text-muted-foreground hover:text-foreground'
            }`}
          >
            {t.label}
          </button>
        ))}
      </div>

      {/* Tab content */}
      {tab === 'personal' && (
        <div className="enterprise-card p-6">
          <h2 className="text-sm font-bold text-foreground uppercase tracking-wide mb-4">Personal Information</h2>
          <dl className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-x-6 gap-y-4">
            <DetailField label="Title" value={employee.title} />
            <DetailField label="First Name" value={employee.firstName} />
            <DetailField label="Last Name" value={employee.lastName} />
            <DetailField label="Preferred Name" value={employee.preferredName} />
            <DetailField label="Date of Birth" value={employee.dateOfBirth ? new Date(employee.dateOfBirth).toLocaleDateString('en-ZA') : null} />
            <DetailField label="Gender" value={employee.gender} />
            <DetailField label="Race" value={employee.race} />
            <DetailField label="Nationality" value={employee.nationality} />
            <DetailField label="Citizenship" value={employee.citizenshipStatus} />
            <DetailField label="Marital Status" value={employee.maritalStatus} />
            <DetailField label="Disability Status" value={employee.disabilityStatus} />
          </dl>

          <h2 className="text-sm font-bold text-foreground uppercase tracking-wide mt-8 mb-4">Contact Details</h2>
          <dl className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-x-6 gap-y-4">
            <DetailField label="Work Email" value={employee.email} />
            <DetailField label="Personal Email" value={employee.personalEmail} />
            <DetailField label="Phone" value={employee.phone} />
            <DetailField label="Mobile" value={employee.mobilePhone} />
            <DetailField label="Physical Address" value={employee.physicalAddress} />
            <DetailField label="Postal Address" value={employee.postalAddress} />
            <DetailField label="City" value={employee.city} />
            <DetailField label="Province" value={employee.province} />
            <DetailField label="Postal Code" value={employee.postalCode} />
            <DetailField label="Country" value={employee.country} />
          </dl>

          <h2 className="text-sm font-bold text-foreground uppercase tracking-wide mt-8 mb-4">Emergency Contact</h2>
          <dl className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-x-6 gap-y-4">
            <DetailField label="Name" value={employee.emergencyContactName} />
            <DetailField label="Phone" value={employee.emergencyContactPhone} />
            <DetailField label="Relationship" value={employee.emergencyContactRelationship} />
          </dl>
        </div>
      )}

      {tab === 'employment' && (
        <div className="enterprise-card p-6">
          <h2 className="text-sm font-bold text-foreground uppercase tracking-wide mb-4">Employment Details</h2>
          <dl className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-x-6 gap-y-4">
            <DetailField label="Employee Number" value={employee.employeeNumber} />
            <DetailField label="Department" value={employee.department} />
            <DetailField label="Division" value={employee.division} />
            <DetailField label="Job Title" value={employee.jobTitle} />
            <DetailField label="Job Grade" value={employee.jobGrade} />
            <DetailField label="Employment Type" value={employee.employmentType} />
            <DetailField label="Hire Date" value={employee.hireDate ? new Date(employee.hireDate).toLocaleDateString('en-ZA') : null} />
            <DetailField label="Probation End" value={employee.probationEndDate ? new Date(employee.probationEndDate).toLocaleDateString('en-ZA') : null} />
            <DetailField label="Contract End" value={employee.contractEndDate ? new Date(employee.contractEndDate).toLocaleDateString('en-ZA') : null} />
            <DetailField label="Reporting Manager" value={employee.reportingManagerName} />
            <DetailField label="Cost Centre" value={employee.costCentre} />
            <DetailField label="Location" value={employee.location} />
            <DetailField label="Site" value={employee.site} />
          </dl>

          {employee.terminationDate && (
            <>
              <h2 className="text-sm font-bold text-foreground uppercase tracking-wide mt-8 mb-4">Termination</h2>
              <dl className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-x-6 gap-y-4">
                <DetailField label="Termination Date" value={new Date(employee.terminationDate).toLocaleDateString('en-ZA')} />
                <DetailField label="Reason" value={employee.terminationReason} />
              </dl>
            </>
          )}

          <h2 className="text-sm font-bold text-foreground uppercase tracking-wide mt-8 mb-4">Banking Details</h2>
          <dl className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-x-6 gap-y-4">
            <DetailField label="Bank Name" value={employee.bankName} />
            <DetailField label="Branch Code" value={employee.bankBranchCode} />
          </dl>
        </div>
      )}

      {tab === 'documents' && (
        <DocumentManager employeeId={employeeId} canManage={canManage} />
      )}

      {tab === 'events' && (
        <EmploymentTimeline employeeId={employeeId} />
      )}
    </div>
  );
}
