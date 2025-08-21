'use client';

import React from 'react';
import { Moon, Sun, Monitor, Palette, Eye } from 'lucide-react';
import { useTheme } from '../contexts/ThemeContext';
import { UserRole } from '../contexts/AuthContext';

interface ThemeToggleProps {
  showRoleSwitch?: boolean;
  compact?: boolean;
  className?: string;
}

const roleIcons: Record<UserRole, string> = {
  'Admin': '👑',
  'HR': '👔',
  'Hiring Manager': '🎯',
  'Recruiter': '🔍',
  'Applicant': '👤',
  'Executive': '🏛️',
};

const roleColors: Record<UserRole, string> = {
  'Admin': 'text-red-600 bg-red-50 hover:bg-red-100 border-red-200',
  'HR': 'text-blue-600 bg-blue-50 hover:bg-blue-100 border-blue-200',
  'Hiring Manager': 'text-green-600 bg-green-50 hover:bg-green-100 border-green-200',
  'Recruiter': 'text-purple-600 bg-purple-50 hover:bg-purple-100 border-purple-200',
  'Applicant': 'text-orange-600 bg-orange-50 hover:bg-orange-100 border-orange-200',
  'Executive': 'text-indigo-600 bg-indigo-50 hover:bg-indigo-100 border-indigo-200',
};

export default function ThemeToggle({ 
  showRoleSwitch = true, 
  compact = false,
  className = '' 
}: ThemeToggleProps) {
  const { mode, toggleTheme, currentRole, applyRoleTheme, isDark } = useTheme();
  const [showRoleMenu, setShowRoleMenu] = React.useState(false);

  const getThemeIcon = () => {
    switch (mode) {
      case 'light':
        return <Sun className="h-4 w-4" />;
      case 'dark':
        return <Moon className="h-4 w-4" />;
      case 'system':
        return <Monitor className="h-4 w-4" />;
      default:
        return <Sun className="h-4 w-4" />;
    }
  };

  const getThemeLabel = () => {
    switch (mode) {
      case 'light':
        return 'Light';
      case 'dark':
        return 'Dark';
      case 'system':
        return 'System';
      default:
        return 'Light';
    }
  };

  if (compact) {
    return (
      <div className={`flex items-center gap-2 ${className}`}>
        <button
          onClick={toggleTheme}
          className="p-2 rounded-lg bg-background/80 backdrop-blur-sm border border-border hover:bg-accent/50 transition-all duration-200 hover:scale-105"
          title={`Current: ${getThemeLabel()} theme`}
        >
          <div className="animate-dark-mode-toggle">
            {getThemeIcon()}
          </div>
        </button>

        {showRoleSwitch && (
          <div className="relative">
            <button
              onClick={() => setShowRoleMenu(!showRoleMenu)}
              className={`p-2 rounded-lg border transition-all duration-200 hover:scale-105 ${currentRole ? roleColors[currentRole] : roleColors.HR}`}
              title={`Current role: ${currentRole || 'HR'}`}
            >
              <span className="text-lg">{currentRole ? roleIcons[currentRole] : roleIcons.HR}</span>
            </button>

            {showRoleMenu && (
              <div className="absolute right-0 top-12 z-50 min-w-[200px] bg-card/95 backdrop-blur-sm border border-border rounded-lg shadow-lg">
                <div className="p-2 space-y-1">
                  <div className="px-3 py-2 text-xs font-medium text-muted-foreground border-b border-border">
                    Switch Role Theme
                  </div>
                  {Object.entries(roleIcons).map(([role, icon]) => (
                    <button
                      key={role}
                      onClick={() => {
                        applyRoleTheme(role as UserRole);
                        setShowRoleMenu(false);
                      }}
                      className={`w-full flex items-center gap-3 px-3 py-2 rounded-md text-left transition-all duration-200 ${
                        currentRole === role 
                          ? roleColors[role as UserRole]
                          : 'hover:bg-accent/50 text-foreground'
                      }`}
                    >
                      <span className="text-lg">{icon}</span>
                      <span className="font-medium">{role}</span>
                      {currentRole === role && (
                        <Eye className="h-4 w-4 ml-auto" />
                      )}
                    </button>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}
      </div>
    );
  }

  return (
    <div className={`flex items-center gap-4 ${className}`}>
      {/* Theme Mode Toggle */}
      <div className="flex items-center gap-2">
        <label className="text-sm font-medium text-foreground">Theme:</label>
        <button
          onClick={toggleTheme}
          className="flex items-center gap-2 px-3 py-2 rounded-lg bg-card/80 backdrop-blur-sm border border-border hover:bg-accent/50 transition-all duration-200 hover:scale-105"
        >
          <div className="animate-dark-mode-toggle">
            {getThemeIcon()}
          </div>
          <span className="text-sm font-medium">{getThemeLabel()}</span>
        </button>
      </div>

      {/* Role Theme Selector */}
      {showRoleSwitch && (
        <div className="flex items-center gap-2">
          <label className="text-sm font-medium text-foreground">Role:</label>
          <div className="relative">
            <button
              onClick={() => setShowRoleMenu(!showRoleMenu)}
              className={`flex items-center gap-2 px-3 py-2 rounded-lg border transition-all duration-200 hover:scale-105 ${currentRole ? roleColors[currentRole] : roleColors.HR}`}
            >
              <span className="text-lg">{currentRole ? roleIcons[currentRole] : roleIcons.HR}</span>
              <span className="text-sm font-medium">{currentRole || 'HR'}</span>
              <Palette className="h-4 w-4" />
            </button>

            {showRoleMenu && (
              <div className="absolute right-0 top-12 z-50 min-w-[220px] bg-card/95 backdrop-blur-sm border border-border rounded-lg shadow-xl">
                <div className="p-3 space-y-2">
                  <div className="flex items-center gap-2 px-2 py-1 text-xs font-medium text-muted-foreground border-b border-border">
                    <Palette className="h-3 w-3" />
                    Role Theme Selector
                  </div>
                  {Object.entries(roleIcons).map(([role, icon]) => (
                    <button
                      key={role}
                      onClick={() => {
                        applyRoleTheme(role as UserRole);
                        setShowRoleMenu(false);
                      }}
                      className={`w-full flex items-center gap-3 px-3 py-2 rounded-md text-left transition-all duration-200 ${
                        currentRole === role 
                          ? roleColors[role as UserRole]
                          : 'hover:bg-accent/50 text-foreground'
                      }`}
                    >
                      <span className="text-lg">{icon}</span>
                      <div className="flex-1">
                        <div className="font-medium text-sm">{role}</div>
                        <div className="text-xs text-muted-foreground">
                          {role === 'Admin' && 'System management & oversight'}
                          {role === 'HR' && 'Human resources coordination'}
                          {role === 'Hiring Manager' && 'Team hiring & interviews'}
                          {role === 'Recruiter' && 'Candidate sourcing & screening'}
                          {role === 'Applicant' && 'Job search & applications'}
                          {role === 'Executive' && 'Strategic oversight & approvals'}
                        </div>
                      </div>
                      {currentRole === role && (
                        <div className="flex items-center gap-1">
                          <Eye className="h-4 w-4" />
                          <span className="text-xs">Active</span>
                        </div>
                      )}
                    </button>
                  ))}
                </div>
                
                {/* Theme Preview */}
                <div className="border-t border-border p-3 bg-muted/30">
                  <div className="text-xs text-muted-foreground mb-2">Current Theme Preview:</div>
                  <div className={`h-8 rounded-md border-2 bg-gradient-to-r ${
                    currentRole === 'Admin' ? 'from-red-100 to-red-200 border-red-300' :
                    currentRole === 'HR' ? 'from-blue-100 to-blue-200 border-blue-300' :
                    currentRole === 'Hiring Manager' ? 'from-green-100 to-green-200 border-green-300' :
                    currentRole === 'Recruiter' ? 'from-purple-100 to-purple-200 border-purple-300' :
                    currentRole === 'Applicant' ? 'from-orange-100 to-orange-200 border-orange-300' :
                    'from-indigo-100 to-indigo-200 border-indigo-300'
                  }`} />
                </div>
              </div>
            )}
          </div>
        </div>
      )}

      {/* Theme Status Indicator */}
      <div className="flex items-center gap-1 text-xs text-muted-foreground">
        <div className={`w-2 h-2 rounded-full ${isDark ? 'bg-blue-400' : 'bg-yellow-400'}`} />
        {isDark ? 'Dark Mode' : 'Light Mode'}
      </div>
    </div>
  );
}

// Utility hook for getting current theme colors
export function useThemeColors() {
  const { roleTheme, isDark } = useTheme();
  
  return {
    primary: roleTheme.colors.primary,
    secondary: roleTheme.colors.secondary,
    background: roleTheme.colors.background,
    brandGradients: roleTheme.brandGradients,
    isDark,
  };
}
