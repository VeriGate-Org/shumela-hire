'use client';

import React, { useState } from 'react';
import { useAuth, UserRole, ALL_ROLES, ROLE_DISPLAY_NAMES } from '../contexts/AuthContext';
import { roleConfigurations } from '../config/roleConfig';
import ThemeToggle from './ThemeToggle';

interface DashboardShellProps {
  title?: string;
  children?: React.ReactNode;
}

const DashboardShell: React.FC<DashboardShellProps> = ({ title, children }) => {
  const { user, switchRole } = useAuth();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  if (!user) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-gray-900">Please log in</h2>
          <p className="text-gray-600">You need to be authenticated to access the dashboard.</p>
        </div>
      </div>
    );
  }

  const currentConfig = roleConfigurations[user.role];
  const roles = ALL_ROLES;

  return (
    <div className="min-h-screen bg-gray-50 lg:flex">
      {/* Mobile sidebar backdrop */}
      {sidebarOpen && (
        <div 
          className="fixed inset-0 z-40 lg:hidden"
          onClick={() => setSidebarOpen(false)}
        >
          <div className="fixed inset-0 bg-gray-600 bg-opacity-75"></div>
        </div>
      )}

      {/* Sidebar */}
      <div className={`fixed inset-y-0 left-0 z-50 w-64 transform transition-transform duration-300 ease-in-out lg:translate-x-0 lg:static lg:w-64 lg:flex-shrink-0 ${
        sidebarOpen ? 'translate-x-0' : '-translate-x-full'
      }`}>
        <div className={`flex flex-col h-full ${currentConfig.primaryColor} text-white`}>
          {/* Logo and Role */}
          <div className="flex items-center justify-between p-4 border-b border-white border-opacity-20">
            <div className="flex items-center space-x-3">
              <span className="text-2xl">{currentConfig.logo}</span>
              <div>
                <h1 className="text-lg font-bold">TalentGate</h1>
                <p className="text-sm opacity-75">{ROLE_DISPLAY_NAMES[user.role]}</p>
              </div>
            </div>
            <button
              onClick={() => setSidebarOpen(false)}
              className="lg:hidden p-1 rounded-md hover:bg-white hover:bg-opacity-20"
            >
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          {/* Navigation */}
          <nav className="flex-1 px-4 py-6 space-y-2">
            {currentConfig.navigationItems.map((item, index) => (
              <a
                key={index}
                href={item.href}
                className="flex items-center space-x-3 px-3 py-2 rounded-lg text-white hover:bg-white hover:bg-opacity-20 transition-colors duration-200"
              >
                <span className="text-lg">{item.icon}</span>
                <span className="font-medium">{item.label}</span>
              </a>
            ))}
          </nav>

          {/* Role Switcher (for demo purposes) */}
          <div className="p-4 border-t border-white border-opacity-20">
            <div className="mb-2">
              <p className="text-sm font-medium opacity-75">Demo: Switch Role</p>
            </div>
            <select
              value={user.role}
              onChange={(e) => switchRole(e.target.value as UserRole)}
              className="w-full px-3 py-2 text-gray-900 bg-white rounded-md focus:outline-none focus:ring-2 focus:ring-white focus:ring-opacity-50"
            >
              {roles.map((role) => (
                <option key={role} value={role}>
                  {ROLE_DISPLAY_NAMES[role]}
                </option>
              ))}
            </select>
          </div>

          {/* User Info */}
          <div className="p-4 border-t border-white border-opacity-20">
            <div className="flex items-center space-x-3">
              <div className="w-8 h-8 bg-white bg-opacity-20 rounded-full flex items-center justify-center">
                <span className="text-sm font-bold">{user.name.charAt(0)}</span>
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium truncate">{user.name}</p>
                <p className="text-xs opacity-75 truncate">{user.email}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Main content */}
      <div className="flex-1 lg:flex lg:flex-col lg:overflow-hidden">
        {/* Top bar */}
        <div className="sticky top-0 z-10 bg-white shadow-sm border-b border-gray-200">
          <div className="flex items-center justify-between px-4 py-3">
            <div className="flex items-center space-x-4">
              <button
                onClick={() => setSidebarOpen(true)}
                className="lg:hidden p-2 rounded-md text-gray-600 hover:text-gray-900 hover:bg-gray-100"
              >
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
                </svg>
              </button>
              <div>
                <h2 className="text-xl font-semibold text-gray-900">{currentConfig.welcomeMessage}</h2>
              </div>
            </div>
            <div className="flex items-center space-x-4">
              <ThemeToggle compact />
              <button className="p-2 text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded-md">
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 17h5l-5 5v-5zM9 7h5l-5-5v5z" />
                </svg>
              </button>
              <button className="p-2 text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded-md">
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 17h5l-5 5v-5z" />
                </svg>
              </button>
            </div>
          </div>
        </div>

        {/* Main Dashboard Content */}
        <div className="flex-1 flex flex-col min-h-0">
          <div className="flex-1 px-6 py-8 overflow-y-auto">
            <div className="w-full max-w-none">
              {title && (
                <div className="mb-6">
                  <h1 className="text-2xl font-bold text-gray-900">{title}</h1>
                </div>
              )}
              <div className="w-full">
                {children || (
                  <div className="bg-white rounded-lg shadow p-6">
                    <h3 className="text-lg font-medium text-gray-900 mb-4">
                      {ROLE_DISPLAY_NAMES[user.role]} Dashboard Content
                    </h3>
                    <p className="text-gray-600">
                      This is where the main dashboard content for {ROLE_DISPLAY_NAMES[user.role]} would appear.
                      The navigation, colors, and layout are all customized based on the user&apos;s role.
                    </p>
                    <div className="mt-6 grid grid-cols-1 lg:grid-cols-2 gap-6">
                      <div className="border border-gray-200 rounded-lg p-4">
                        <h4 className="font-medium text-gray-900 mb-2">Recent Activity</h4>
                        <p className="text-sm text-gray-600">Role-specific recent activity would be displayed here.</p>
                      </div>
                      <div className="border border-gray-200 rounded-lg p-4">
                        <h4 className="font-medium text-gray-900 mb-2">Quick Actions</h4>
                        <p className="text-sm text-gray-600">Role-specific quick actions would be available here.</p>
                      </div>
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DashboardShell;