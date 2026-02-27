'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import PageWrapper from '@/components/PageWrapper';
import EmployeeDirectory from '@/components/employees/EmployeeDirectory';
import EmployeeForm from '@/components/employees/EmployeeForm';
import type { Employee } from '@/types/employee';

export default function EmployeesPage() {
  const router = useRouter();
  const [view, setView] = useState<'list' | 'create'>('list');

  const handleCreateNew = () => setView('create');

  const handleSaved = (emp: Employee) => {
    router.push(`/employees/${emp.id}`);
  };

  const handleBack = () => setView('list');

  const title = view === 'create' ? 'New Employee' : 'Employees';
  const subtitle = view === 'create'
    ? 'Create a new employee record with personal and employment details.'
    : 'Manage your workforce — search, filter, and view employee profiles.';

  const actions = view === 'list' ? (
    <button onClick={handleCreateNew} className="btn-primary text-sm">
      Add Employee
    </button>
  ) : (
    <button onClick={handleBack} className="text-sm font-medium text-primary hover:text-cta">
      &larr; Back to Directory
    </button>
  );

  return (
    <PageWrapper title={title} subtitle={subtitle} actions={actions}>
      <div className="space-y-6">
        {view === 'list' && <EmployeeDirectory onCreateNew={handleCreateNew} />}
        {view === 'create' && (
          <div className="enterprise-card p-6">
            <EmployeeForm onSaved={handleSaved} onCancel={handleBack} />
          </div>
        )}
      </div>
    </PageWrapper>
  );
}
