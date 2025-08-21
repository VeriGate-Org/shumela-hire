import React, { ReactNode } from 'react';
import DashboardNavigation from './DashboardNavigation';
import NotificationCenter from './NotificationCenter';
import UserProfile from './UserProfile';
import GlobalSearch from './GlobalSearch';
import { MobileHeader } from './MobileNavigation';
import PWAInstallPrompt, { PWAStatus } from './PWAInstallPrompt';
import OfflineIndicator from './OfflineIndicator';
import ServiceWorkerRegistration from './ServiceWorkerRegistration';

interface DashboardLayoutProps {
  children: ReactNode;
  title?: string;
  subtitle?: string;
  actions?: ReactNode;
}

const DashboardLayout: React.FC<DashboardLayoutProps> = ({
  children,
  title,
  subtitle,
  actions
}) => {
  return (
    <div className="flex h-screen bg-gray-50">
      {/* Service Worker Registration */}
      <ServiceWorkerRegistration />
      
      {/* Mobile Header */}
      <MobileHeader />
      
      {/* Desktop Navigation */}
      <DashboardNavigation />
      
      {/* PWA Components */}
      <PWAInstallPrompt />
      <PWAStatus />
      <OfflineIndicator />
      
      <div className="flex-1 flex flex-col overflow-hidden md:ml-80 pt-16 md:pt-0">
        {/* Top header - hidden on mobile */}
        <header className="hidden md:block bg-white border-b border-gray-200 px-6 py-4">
          <div className="flex items-center justify-between">
            <div>
              {title && (
                <h1 className="text-2xl font-bold text-gray-900">{title}</h1>
              )}
              {subtitle && (
                <p className="text-gray-600 mt-1">{subtitle}</p>
              )}
            </div>
            <div className="flex items-center space-x-4">
              <GlobalSearch />
              <NotificationCenter />
              <UserProfile />
              {actions && actions}
            </div>
          </div>
        </header>

        {/* Main content */}
        <main className="flex-1 overflow-y-auto">
          {children}
        </main>
      </div>
    </div>
  );
};

export default DashboardLayout;
