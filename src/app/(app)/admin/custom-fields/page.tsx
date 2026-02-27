'use client';

import PageWrapper from '@/components/PageWrapper';
import CustomFieldBuilder from '@/components/employees/CustomFieldBuilder';

export default function CustomFieldsPage() {
  return (
    <PageWrapper
      title="Custom Fields"
      subtitle="Configure custom fields for employees, documents, and employment events."
    >
      <CustomFieldBuilder />
    </PageWrapper>
  );
}
