'use client';

import React, { useState } from 'react';
import PageWrapper from '../../components/PageWrapper';
import RoleDashboard from '../../components/dashboard/RoleDashboard';
import { useAuth } from '../../contexts/AuthContext';

export default function DashboardPage() {
  const [selectedTimeframe, setSelectedTimeframe] = useState('30days');
  const { user } = useAuth();

  // Default to Hiring Manager if no user role is available
  const userRole = user?.role || 'Hiring Manager';

  const actions = (
    <div className="flex items-center space-x-3">
      <button className="inline-flex items-center px-4 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
        Export Data
      </button>
      <button className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
        Create Position
      </button>
    </div>
  );

  const dashboardContent = (
    <RoleDashboard 
      role={userRole}
      selectedTimeframe={selectedTimeframe}
      onTimeframeChange={setSelectedTimeframe}
    />
  );

  return (
    <PageWrapper 
      title={`${userRole} Dashboard`}
      subtitle={`Welcome back! Here's your ${userRole.toLowerCase()} overview for the selected timeframe.`}
      actions={actions}
      showLayoutToggle={true}
    >
      {dashboardContent}
    </PageWrapper>
  );
}