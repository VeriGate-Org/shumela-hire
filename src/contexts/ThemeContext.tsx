'use client';

import React, { createContext, useContext, useEffect, useState } from 'react';
import { UserRole } from './AuthContext';
import { enterpriseRoleThemes, EnterpriseRoleTheme, ThemeMode } from '../config/enterpriseTheme';

interface EnterpriseThemeContextType {
  mode: ThemeMode;
  setMode: (mode: ThemeMode) => void;
  isDark: boolean;
  isHighContrast: boolean;
  currentRole?: UserRole;
  setCurrentRole: (role: UserRole) => void;
  roleTheme: EnterpriseRoleTheme;
  toggleTheme: () => void;
  applyRoleTheme: (role: UserRole) => void;
  enableHighContrast: (enabled: boolean) => void;
}

const ThemeContext = createContext<EnterpriseThemeContextType | undefined>(undefined);

export function ThemeProvider({ children }: { children: React.ReactNode }) {
  const [mode, setMode] = useState<ThemeMode>('system');
  const [isDark, setIsDark] = useState(false);
  const [isHighContrast, setIsHighContrast] = useState(false);
  const [currentRole, setCurrentRole] = useState<UserRole>('HR');

  // Initialize theme from localStorage (only once on mount)
  useEffect(() => {
    const savedMode = localStorage.getItem('theme-mode') as ThemeMode;
    const savedRole = localStorage.getItem('current-role') as UserRole;
    const savedHighContrast = localStorage.getItem('high-contrast') === 'true';
    
    if (savedMode) {
      setMode(savedMode);
    }
    
    if (savedRole) {
      setCurrentRole(savedRole);
    }
    
    if (savedHighContrast) {
      setIsHighContrast(true);
    }
  }, []); // Empty dependency array - only run on mount

  // Handle dark mode state changes when mode changes
  useEffect(() => {
    const updateDarkMode = () => {
      if (mode === 'system') {
        setIsDark(window.matchMedia('(prefers-color-scheme: dark)').matches);
      } else {
        setIsDark(mode === 'dark');
      }
    };

    updateDarkMode();

    // Listen for system theme changes
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
    const handleChange = () => {
      if (mode === 'system') {
        setIsDark(mediaQuery.matches);
      }
    };

    mediaQuery.addEventListener('change', handleChange);
    return () => mediaQuery.removeEventListener('change', handleChange);
  }, [mode]); // Only depend on mode changes

  // Apply theme classes to document
  useEffect(() => {
    const root = document.documentElement;
    
    // Remove existing theme classes
    root.classList.remove('light', 'dark', 'high-contrast');
    Object.values(enterpriseRoleThemes).forEach(theme => {
      root.classList.remove(theme.className);
    });

    // Apply current theme classes
    root.classList.add(isDark ? 'dark' : 'light');
    
    if (isHighContrast) {
      root.classList.add('high-contrast');
    }
    
    root.classList.add(enterpriseRoleThemes[currentRole].className);

    // Apply CSS custom properties from the enterprise theme
    const theme = enterpriseRoleThemes[currentRole];
    Object.entries(theme.cssVariables).forEach(([property, value]) => {
      root.style.setProperty(property, value);
    });

    // Save to localStorage
    localStorage.setItem('theme-mode', mode);
    localStorage.setItem('current-role', currentRole);
    localStorage.setItem('high-contrast', isHighContrast.toString());
  }, [mode, isDark, currentRole, isHighContrast]);

  const toggleTheme = () => {
    const modes: ThemeMode[] = ['light', 'dark', 'system'];
    const currentIndex = modes.indexOf(mode);
    const nextMode = modes[(currentIndex + 1) % modes.length];
    setMode(nextMode);
  };

  const applyRoleTheme = (role: UserRole) => {
    setCurrentRole(role);
    
    // Add smooth transition animation
    document.body.classList.add('animate-theme-transition');
    setTimeout(() => {
      document.body.classList.remove('animate-theme-transition');
    }, 300);
  };

  const enableHighContrast = (enabled: boolean) => {
    setIsHighContrast(enabled);
  };

  const value: EnterpriseThemeContextType = {
    mode,
    setMode,
    isDark,
    isHighContrast,
    currentRole,
    setCurrentRole,
    roleTheme: enterpriseRoleThemes[currentRole],
    toggleTheme,
    applyRoleTheme,
    enableHighContrast,
  };

  return (
    <ThemeContext.Provider value={value}>
      <div className="enterprise-theme-provider transition-colors duration-300">
        {children}
      </div>
    </ThemeContext.Provider>
  );
}

export function useTheme() {
  const context = useContext(ThemeContext);
  if (context === undefined) {
    throw new Error('useTheme must be used within a ThemeProvider');
  }
  return context;
}

// Legacy export for backward compatibility
export type { ThemeMode };
export { enterpriseRoleThemes as roleThemes };
