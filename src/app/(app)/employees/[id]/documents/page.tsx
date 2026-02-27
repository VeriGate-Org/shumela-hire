'use client';

import { use } from 'react';
import Link from 'next/link';
import PageWrapper from '@/components/PageWrapper';
import DocumentManager from '@/components/employees/DocumentManager';
import { useAuth } from '@/contexts/AuthContext';

export default function EmployeeDocumentsPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = use(params);
  const { hasPermission } = useAuth();
  const employeeId = parseInt(id, 10);
  const canManage = hasPermission('manage_employees');

  if (isNaN(employeeId)) {
    return (
      <PageWrapper title="Documents" subtitle="Invalid employee ID.">
        <div className="enterprise-card p-8 text-center">
          <p className="text-sm text-muted-foreground">Invalid employee ID.</p>
        </div>
      </PageWrapper>
    );
  }

  return (
    <PageWrapper
      title="Employee Documents"
      subtitle="Manage employee documents — upload, view, and track document expiry."
      actions={
        <Link href={`/employees/${id}`} className="text-sm font-medium text-primary hover:text-cta">
          &larr; Back to Profile
        </Link>
      }
    >
      <DocumentManager employeeId={employeeId} canManage={canManage} />
    </PageWrapper>
  );
}
