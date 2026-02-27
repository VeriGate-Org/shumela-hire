'use client';

import { use } from 'react';
import Link from 'next/link';
import PageWrapper from '@/components/PageWrapper';
import EmploymentTimeline from '@/components/employees/EmploymentTimeline';

export default function EmployeeEventsPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = use(params);
  const employeeId = parseInt(id, 10);

  if (isNaN(employeeId)) {
    return (
      <PageWrapper title="Employment History" subtitle="Invalid employee ID.">
        <div className="enterprise-card p-8 text-center">
          <p className="text-sm text-muted-foreground">Invalid employee ID.</p>
        </div>
      </PageWrapper>
    );
  }

  return (
    <PageWrapper
      title="Employment History"
      subtitle="Timeline of all employment events — hires, promotions, transfers, and more."
      actions={
        <Link href={`/employees/${id}`} className="text-sm font-medium text-primary hover:text-cta">
          &larr; Back to Profile
        </Link>
      }
    >
      <EmploymentTimeline employeeId={employeeId} />
    </PageWrapper>
  );
}
