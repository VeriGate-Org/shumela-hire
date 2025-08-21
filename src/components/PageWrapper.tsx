'use client';

import React, { ReactNode } from 'react';
import ModernLayout from '@/components/ModernLayout';
import DashboardShell from '@/components/DashboardShell';
import { useLayout } from '@/contexts/LayoutContext';
import { useAuth } from '@/contexts/AuthContext';

interface PageWrapperProps {
  children: ReactNode;
  title?: string;
  subtitle?: string;
  actions?: ReactNode;
  showLayoutToggle?: boolean;
}

export default function PageWrapper({ 
  children, 
  title, 
  subtitle, 
  actions,
  showLayoutToggle = false
}: PageWrapperProps) {
  const { useModernLayout, toggleLayout } = useLayout();
  const { user } = useAuth();

  // Generate dynamic title and subtitle if not provided
  const defaultTitle = title || 'Dashboard';
  const defaultSubtitle = subtitle || `Welcome back${user?.name ? `, ${user.name}` : ''}! Here's your overview.`;

  // Create layout toggle component
  const layoutToggle = showLayoutToggle ? (
    <div className="flex items-center space-x-2">
      <span className="text-sm text-gray-600">Classic</span>
      <button
        onClick={toggleLayout}
        className={`
          relative inline-flex h-6 w-11 flex-shrink-0 cursor-pointer rounded-full border-2 border-transparent 
          transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2
          ${useModernLayout ? 'bg-blue-600' : 'bg-gray-200'}
        `}
      >
        <span
          className={`
            pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 
            transition duration-200 ease-in-out
            ${useModernLayout ? 'translate-x-5' : 'translate-x-0'}
          `}
        />
      </button>
      <span className="text-sm text-gray-600">Modern</span>
    </div>
  ) : null;

  // Combine actions with layout toggle
  const combinedActions = (
    <div className="flex items-center space-x-3">
      {layoutToggle}
      {actions}
    </div>
  );

  if (useModernLayout) {
    return (
      <ModernLayout 
        title={defaultTitle}
        subtitle={defaultSubtitle}
        actions={showLayoutToggle || actions ? combinedActions : undefined}
      >
        {children}
      </ModernLayout>
    );
  }

  return (
    <DashboardShell
      title={defaultTitle}
    >
      {(showLayoutToggle || actions) && (
        <div className="mb-4 flex justify-end">
          {combinedActions}
        </div>
      )}
      {children}
    </DashboardShell>
  );
}
