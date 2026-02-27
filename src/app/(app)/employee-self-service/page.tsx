'use client';

import PageWrapper from '@/components/PageWrapper';
import SelfServicePortal from '@/components/employees/SelfServicePortal';

export default function EmployeeSelfServicePage() {
  return (
    <PageWrapper
      title="Self Service"
      subtitle="View your profile, documents, and employment information."
    >
      <SelfServicePortal />
    </PageWrapper>
  );
}
