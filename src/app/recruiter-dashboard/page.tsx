'use client';

import React from 'react';
import RecruiterDashboard from '@/components/RecruiterDashboard';
import { useAuth } from '@/contexts/AuthContext';

const RecruiterDashboardPage: React.FC = () => {
  const { user } = useAuth();

  // Check if user has recruiter or manager role
  const hasAccess = user && (user.role === 'RECRUITER' || user.role === 'HIRING_MANAGER' || user.role === 'HR_MANAGER');

  if (!hasAccess) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="bg-white p-8 rounded-sm shadow-lg max-w-md w-full text-center">
          <h1 className="text-2xl font-bold text-gray-900 mb-4">Access Denied</h1>
          <p className="text-gray-600 mb-6">
            You need recruiter, hiring manager, or HR permissions to access this dashboard.
          </p>
          <a 
            href="/dashboard" 
            className="bg-gold-500 text-violet-950 px-4 py-2 rounded hover:bg-gold-600 transition-colors"
          >
            Go to Main Dashboard
          </a>
        </div>
      </div>
    );
  }

  return (
    <div>
      <RecruiterDashboard />
    </div>
  );
};

export default RecruiterDashboardPage;
