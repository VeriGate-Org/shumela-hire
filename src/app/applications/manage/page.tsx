import PageWrapper from '@/components/PageWrapper';
import ApplicationManagementConsole from '@/components/ApplicationManagementConsole';

export default function ApplicationManagementPage() {
  return (
    <PageWrapper
      title="Application Management"
      subtitle="Advanced application management console with search, filtering, and bulk operations"
    >
      <ApplicationManagementConsole />
    </PageWrapper>
  );
}
