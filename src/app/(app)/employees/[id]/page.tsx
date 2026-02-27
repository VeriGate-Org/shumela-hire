'use client';

import { use } from 'react';
import Link from 'next/link';
import PageWrapper from '@/components/PageWrapper';
import EmployeeProfile from '@/components/employees/EmployeeProfile';

export default function EmployeeDetailPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = use(params);
  const employeeId = parseInt(id, 10);

  if (isNaN(employeeId)) {
    return (
      <PageWrapper title="Employee" subtitle="Invalid employee ID.">
        <div className="enterprise-card p-8 text-center">
          <p className="text-sm text-muted-foreground">Invalid employee ID.</p>
          <Link href="/employees" className="text-sm text-primary hover:underline mt-2 inline-block">
            Back to Directory
          </Link>
        </div>
      </PageWrapper>
    );
  }

  return (
    <PageWrapper
      title="Employee Profile"
      subtitle="View and manage employee details, documents, and employment history."
      actions={
        <Link href="/employees" className="text-sm font-medium text-primary hover:text-cta">
          &larr; Back to Directory
        </Link>
      }
    >
      <EmployeeProfile employeeId={employeeId} />
    </PageWrapper>
  );
}
