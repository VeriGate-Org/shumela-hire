'use client';

import dynamic from 'next/dynamic';

// Dynamically import the security page content to avoid SSR issues with SecurityProvider
const SecurityPageContent = dynamic(
  () => import('@/components/SecurityPageContent'),
  {
    ssr: false,
    loading: () => (
      <div className="min-h-screen bg-gray-50 py-8">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center py-12">
            <h1 className="text-2xl font-bold text-gray-900 mb-4">Security & Compliance</h1>
            <p className="text-gray-600">Loading security features...</p>
          </div>
        </div>
      </div>
    ),
  }
);

/**
 * Security & Compliance Page
 * Main page for security management and GDPR compliance
 */
export default function SecurityCompliancePage() {
  return <SecurityPageContent />;
}
