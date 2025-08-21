import React, { useState, ReactNode } from 'react';
import ModernSidebar from './ModernSidebar';
import RoleSwitcher from './RoleSwitcher';
import { useAuth } from '../contexts/AuthContext';
import { roleConfigurations } from '../config/roleConfig';
import { BellIcon, MagnifyingGlassIcon, Bars3Icon } from '@heroicons/react/24/outline';
import NotificationCenter from './NotificationCenter';
import UserProfile from './UserProfile';

interface ModernLayoutProps {
  children: ReactNode;
  title?: string;
  subtitle?: string;
  actions?: ReactNode;
}

const ModernLayout: React.FC<ModernLayoutProps> = ({
  children,
  title,
  subtitle,
  actions
}) => {
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const { user } = useAuth();

  const userInitials = user 
    ? user.name.split(' ').map(n => n[0]).join('') 
    : 'JD';
    
  const roleConfig = user ? roleConfigurations[user.role] : null;

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Modern Sidebar */}
      <ModernSidebar 
        isCollapsed={sidebarCollapsed}
        onToggleCollapse={() => setSidebarCollapsed(!sidebarCollapsed)}
      />

      {/* Mobile sidebar overlay */}
      {mobileMenuOpen && (
        <div 
          className="fixed inset-0 z-40 lg:hidden"
          onClick={() => setMobileMenuOpen(false)}
        >
          <div className="fixed inset-0 bg-black/50" />
          <div className="fixed left-0 top-0 h-full w-80 bg-white shadow-xl">
            <ModernSidebar />
          </div>
        </div>
      )}

      {/* Main content area */}
      <div className={`
        transition-all duration-300 ease-in-out
        ${sidebarCollapsed ? 'lg:ml-16' : 'lg:ml-80'}
      `}>
        {/* Top navigation bar */}
        <header className="bg-white border-b border-gray-200 sticky top-0 z-30">
          <div className="px-4 sm:px-6 lg:px-8">
            <div className="flex justify-between h-16">
              {/* Left side */}
              <div className="flex items-center">
                {/* Mobile menu button */}
                <button
                  onClick={() => setMobileMenuOpen(true)}
                  className="lg:hidden p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-blue-500"
                >
                  <Bars3Icon className="h-6 w-6" />
                </button>

                {/* Page title */}
                <div className="ml-4 lg:ml-0">
                  {title && (
                    <h1 className="text-2xl font-bold text-gray-900">{title}</h1>
                  )}
                  {subtitle && (
                    <p className="text-sm text-gray-600 mt-0.5">{subtitle}</p>
                  )}
                </div>
              </div>

              {/* Right side */}
              <div className="flex items-center space-x-4">
                {/* Global search */}
                <div className="hidden sm:block">
                  <div className="relative">
                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                      <MagnifyingGlassIcon className="h-5 w-5 text-gray-400" />
                    </div>
                    <input
                      type="text"
                      placeholder="Search across platform..."
                      className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md leading-5 bg-white placeholder-gray-500 focus:outline-none focus:placeholder-gray-400 focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm"
                    />
                  </div>
                </div>

                {/* Actions */}
                {actions && (
                  <div className="flex items-center space-x-2">
                    {actions}
                  </div>
                )}

                {/* Role Switcher */}
                <RoleSwitcher compact />

                {/* Notifications */}
                <div className="relative">
                  <button className="p-2 text-gray-400 hover:text-gray-500 hover:bg-gray-100 rounded-lg transition-colors relative">
                    <BellIcon className="h-6 w-6" />
                    <span className="absolute top-1 right-1 block h-2 w-2 rounded-full bg-red-400 ring-2 ring-white" />
                  </button>
                </div>

                {/* User profile */}
                <div className="relative">
                  <button className="flex items-center p-2 text-sm rounded-lg hover:bg-gray-100 transition-colors">
                    <div className={`w-8 h-8 rounded-full flex items-center justify-center ${
                      roleConfig ? roleConfig.primaryColor : 'bg-gradient-to-br from-blue-600 to-blue-700'
                    }`}>
                      <span className="text-white font-medium text-sm">{userInitials}</span>
                    </div>
                    <div className="hidden sm:ml-3 sm:block text-left">
                      <p className="text-gray-700 font-medium text-sm">
                        {user?.name || 'John Doe'}
                      </p>
                      <p className="text-xs text-gray-500 flex items-center">
                        <span className="mr-1">{roleConfig?.logo || '👔'}</span>
                        {user?.role || 'HR Manager'}
                      </p>
                    </div>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </header>

        {/* Page content */}
        <main className="flex-1">
          <div className="px-4 sm:px-6 lg:px-8 py-8">
            {children}
          </div>
        </main>

        {/* Footer */}
        <footer className="bg-white border-t border-gray-200">
          <div className="px-4 sm:px-6 lg:px-8 py-6">
            <div className="flex flex-col sm:flex-row justify-between items-center text-sm text-gray-500">
              <div className="flex items-center space-x-6">
                <p>&copy; 2025 E-Recruitment. All rights reserved.</p>
                <div className="flex space-x-4">
                  <a href="#" className="hover:text-gray-700 transition-colors">Privacy Policy</a>
                  <a href="#" className="hover:text-gray-700 transition-colors">Terms of Service</a>
                  <a href="#" className="hover:text-gray-700 transition-colors">Support</a>
                </div>
              </div>
              <div className="mt-4 sm:mt-0">
                <p>Version 2.1.0 - Last updated: August 21, 2025</p>
              </div>
            </div>
          </div>
        </footer>
      </div>
    </div>
  );
};

export default ModernLayout;
