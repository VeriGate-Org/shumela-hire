import React, { useState, ReactNode } from 'react';
import ModernSidebar from './ModernSidebar';
import { useAuth } from '../contexts/AuthContext';
import { BellIcon, Bars3Icon, QuestionMarkCircleIcon } from '@heroicons/react/24/outline';

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

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Top nav — fixed full width */}
      <header className="fixed top-0 left-0 right-0 z-50 h-14 bg-white border-b border-gray-200">
        <div className="flex h-full items-center justify-between px-4">
          {/* Left: hamburger + logo + breadcrumb */}
          <div className="flex items-center gap-3 min-w-0 flex-1">
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="lg:hidden p-2 rounded hover:bg-gray-100"
            >
              <Bars3Icon className="h-4 w-4 text-gray-500" />
            </button>

            <div className="flex items-center gap-2.5">
              <div className="h-8 w-8 bg-violet-600 rounded-lg grid place-items-center">
                <span className="text-white font-bold text-sm">TG</span>
              </div>
              <span className="font-bold text-sm tracking-tight text-gray-900 hidden sm:block">TalentGate</span>
            </div>

            {title && (
              <div className="hidden md:flex items-center gap-1 pl-4 border-l border-gray-200 text-xs">
                <span className="text-gray-400">/</span>
                <span className="text-gray-900 font-medium">{title}</span>
              </div>
            )}
          </div>

          {/* Right: notifications, help, avatar */}
          <div className="flex items-center gap-1">
            <button className="relative p-2 rounded hover:bg-gray-100">
              <BellIcon className="h-4 w-4 text-gray-500" />
              <span className="absolute top-1.5 right-1.5 block h-1.5 w-1.5 rounded-full bg-orange-500" />
            </button>

            <button className="p-2 rounded hover:bg-gray-100">
              <QuestionMarkCircleIcon className="h-4 w-4 text-gray-500" />
            </button>

            <button className="flex items-center gap-2 px-1.5 py-1 rounded-full hover:bg-gray-100 ml-1">
              <div className="h-7 w-7 rounded-full bg-gradient-to-br from-violet-500 to-violet-700 text-white grid place-items-center font-semibold text-xs ring-2 ring-violet-500/20">
                {userInitials}
              </div>
            </button>
          </div>
        </div>
      </header>

      {/* Sidebar — below top nav */}
      <div className="hidden lg:block">
        <ModernSidebar
          isCollapsed={sidebarCollapsed}
          onToggleCollapse={() => setSidebarCollapsed(!sidebarCollapsed)}
        />
      </div>

      {/* Mobile sidebar overlay */}
      {mobileMenuOpen && (
        <div
          className="fixed inset-0 z-40 lg:hidden"
          onClick={() => setMobileMenuOpen(false)}
        >
          <div className="fixed inset-0 bg-black/30" />
          <div className="fixed left-0 top-14 bottom-0 w-60 bg-white border-r border-gray-200 shadow-lg overflow-y-auto">
            <ModernSidebar />
          </div>
        </div>
      )}

      {/* Main content */}
      <div className={`
        pt-14 transition-all duration-200 ease-in-out
        ${sidebarCollapsed ? 'lg:ml-16' : 'lg:ml-60'}
      `}>
        <main className="p-6 min-h-[calc(100vh-3.5rem)]">
          {children}
        </main>

        <footer className="border-t border-gray-100 bg-white">
          <div className="px-6 py-4 flex flex-col sm:flex-row justify-between items-center text-xs text-gray-400">
            <p>&copy; 2026 TalentGate</p>
            <div className="flex gap-4 mt-2 sm:mt-0">
              <a href="#" className="hover:text-gray-600 transition-colors">Privacy</a>
              <a href="#" className="hover:text-gray-600 transition-colors">Terms</a>
              <a href="#" className="hover:text-gray-600 transition-colors">Support</a>
            </div>
          </div>
        </footer>
      </div>
    </div>
  );
};

export default ModernLayout;
