/**
 * Enterprise Theme System
 * Professional, accessible, and scalable theme configuration
 */

import { UserRole } from '../contexts/AuthContext';
import { enterpriseDesignTokens } from './designTokens';

export type ThemeMode = 'light' | 'dark' | 'system' | 'high-contrast';

export interface EnterpriseThemeColors {
  // Brand Colors
  primary: {
    50: string;
    100: string;
    500: string;
    600: string;
    700: string;
    900: string;
    foreground: string;
  };
  secondary: {
    50: string;
    100: string;
    500: string;
    600: string;
    700: string;
    900: string;
    foreground: string;
  };
  
  // Surface Colors
  background: {
    default: string;
    elevated: string;
    overlay: string;
  };
  surface: {
    default: string;
    elevated: string;
    sunken: string;
    overlay: string;
  };
  
  // Content Colors
  content: {
    primary: string;
    secondary: string;
    tertiary: string;
    inverse: string;
    disabled: string;
  };
  
  // Border Colors
  border: {
    default: string;
    subtle: string;
    strong: string;
    interactive: string;
  };
  
  // Interactive States
  interactive: {
    default: string;
    hover: string;
    active: string;
    disabled: string;
    focus: string;
  };
  
  // Status Colors
  status: {
    success: string;
    warning: string;
    error: string;
    info: string;
  };
}

export interface EnterpriseRoleTheme {
  id: UserRole;
  name: string;
  colors: EnterpriseThemeColors;
  className: string;
  cssVariables: Record<string, string>;
  brandGradients: {
    primary: string;
    secondary: string;
    hero: string;
    card: string;
  };
  shadows: {
    sm: string;
    md: string;
    lg: string;
    colored: string;
  };
}

// Executive Theme - Deep Navy & Gold
const executiveTheme: EnterpriseRoleTheme = {
  id: 'Executive',
  name: 'Executive',
  className: 'theme-executive',
  colors: {
    primary: {
      50: '#f0f4ff',
      100: '#e0ebff',
      500: '#4f46e5',
      600: '#4338ca',
      700: '#3730a3',
      900: '#312e81',
      foreground: '#ffffff',
    },
    secondary: {
      50: '#fffbeb',
      100: '#fef3c7',
      500: '#f59e0b',
      600: '#d97706',
      700: '#b45309',
      900: '#78350f',
      foreground: '#ffffff',
    },
    background: {
      default: '#fafafa',
      elevated: '#ffffff',
      overlay: 'rgba(0, 0, 0, 0.8)',
    },
    surface: {
      default: '#ffffff',
      elevated: '#f8fafc',
      sunken: '#f1f5f9',
      overlay: 'rgba(255, 255, 255, 0.95)',
    },
    content: {
      primary: '#0f172a',
      secondary: '#475569',
      tertiary: '#64748b',
      inverse: '#ffffff',
      disabled: '#94a3b8',
    },
    border: {
      default: '#e2e8f0',
      subtle: '#f1f5f9',
      strong: '#cbd5e1',
      interactive: '#4f46e5',
    },
    interactive: {
      default: '#4f46e5',
      hover: '#4338ca',
      active: '#3730a3',
      disabled: '#94a3b8',
      focus: '#4f46e5',
    },
    status: {
      success: '#22c55e',
      warning: '#f59e0b',
      error: '#ef4444',
      info: '#3b82f6',
    },
  },
  cssVariables: {
    '--primary': '#4f46e5',
    '--primary-foreground': '#ffffff',
    '--secondary': '#f59e0b',
    '--secondary-foreground': '#ffffff',
    '--background': '#fafafa',
    '--foreground': '#0f172a',
    '--card': '#ffffff',
    '--card-foreground': '#0f172a',
    '--border': '#e2e8f0',
    '--ring': '#4f46e5',
  },
  brandGradients: {
    primary: 'linear-gradient(135deg, #4f46e5 0%, #3730a3 100%)',
    secondary: 'linear-gradient(135deg, #f59e0b 0%, #d97706 100%)',
    hero: 'linear-gradient(135deg, #4f46e5 0%, #f59e0b 100%)',
    card: 'linear-gradient(135deg, #ffffff 0%, #f8fafc 100%)',
  },
  shadows: {
    sm: '0 1px 2px 0 rgba(79, 70, 229, 0.05)',
    md: '0 4px 6px -1px rgba(79, 70, 229, 0.1)',
    lg: '0 10px 15px -3px rgba(79, 70, 229, 0.1)',
    colored: '0 4px 14px 0 rgba(79, 70, 229, 0.25)',
  },
};

// HR Theme - Professional Blue
const hrTheme: EnterpriseRoleTheme = {
  id: 'HR',
  name: 'Human Resources',
  className: 'theme-hr',
  colors: {
    primary: {
      50: '#eff6ff',
      100: '#dbeafe',
      500: '#3b82f6',
      600: '#2563eb',
      700: '#1d4ed8',
      900: '#1e3a8a',
      foreground: '#ffffff',
    },
    secondary: {
      50: '#f0f9ff',
      100: '#e0f2fe',
      500: '#0ea5e9',
      600: '#0284c7',
      700: '#0369a1',
      900: '#0c4a6e',
      foreground: '#ffffff',
    },
    background: {
      default: '#ffffff',
      elevated: '#f8fafc',
      overlay: 'rgba(0, 0, 0, 0.8)',
    },
    surface: {
      default: '#f8fafc',
      elevated: '#ffffff',
      sunken: '#f1f5f9',
      overlay: 'rgba(255, 255, 255, 0.95)',
    },
    content: {
      primary: '#0f172a',
      secondary: '#475569',
      tertiary: '#64748b',
      inverse: '#ffffff',
      disabled: '#94a3b8',
    },
    border: {
      default: '#e2e8f0',
      subtle: '#f1f5f9',
      strong: '#cbd5e1',
      interactive: '#3b82f6',
    },
    interactive: {
      default: '#3b82f6',
      hover: '#2563eb',
      active: '#1d4ed8',
      disabled: '#94a3b8',
      focus: '#3b82f6',
    },
    status: {
      success: '#22c55e',
      warning: '#f59e0b',
      error: '#ef4444',
      info: '#3b82f6',
    },
  },
  cssVariables: {
    '--primary': '#3b82f6',
    '--primary-foreground': '#ffffff',
    '--secondary': '#0ea5e9',
    '--secondary-foreground': '#ffffff',
    '--background': '#ffffff',
    '--foreground': '#0f172a',
    '--card': '#f8fafc',
    '--card-foreground': '#0f172a',
    '--border': '#e2e8f0',
    '--ring': '#3b82f6',
  },
  brandGradients: {
    primary: 'linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%)',
    secondary: 'linear-gradient(135deg, #0ea5e9 0%, #0369a1 100%)',
    hero: 'linear-gradient(135deg, #3b82f6 0%, #0ea5e9 100%)',
    card: 'linear-gradient(135deg, #f8fafc 0%, #ffffff 100%)',
  },
  shadows: {
    sm: '0 1px 2px 0 rgba(59, 130, 246, 0.05)',
    md: '0 4px 6px -1px rgba(59, 130, 246, 0.1)',
    lg: '0 10px 15px -3px rgba(59, 130, 246, 0.1)',
    colored: '0 4px 14px 0 rgba(59, 130, 246, 0.25)',
  },
};

// Admin Theme - Authoritative Red
const adminTheme: EnterpriseRoleTheme = {
  id: 'Admin',
  name: 'Administrator',
  className: 'theme-admin',
  colors: {
    primary: {
      50: '#fef2f2',
      100: '#fee2e2',
      500: '#ef4444',
      600: '#dc2626',
      700: '#b91c1c',
      900: '#7f1d1d',
      foreground: '#ffffff',
    },
    secondary: {
      50: '#fafafa',
      100: '#f5f5f5',
      500: '#737373',
      600: '#525252',
      700: '#404040',
      900: '#171717',
      foreground: '#ffffff',
    },
    background: {
      default: '#fafafa',
      elevated: '#ffffff',
      overlay: 'rgba(0, 0, 0, 0.8)',
    },
    surface: {
      default: '#ffffff',
      elevated: '#fafafa',
      sunken: '#f5f5f5',
      overlay: 'rgba(255, 255, 255, 0.95)',
    },
    content: {
      primary: '#171717',
      secondary: '#404040',
      tertiary: '#525252',
      inverse: '#ffffff',
      disabled: '#a3a3a3',
    },
    border: {
      default: '#e5e5e5',
      subtle: '#f5f5f5',
      strong: '#d4d4d4',
      interactive: '#dc2626',
    },
    interactive: {
      default: '#dc2626',
      hover: '#b91c1c',
      active: '#991b1b',
      disabled: '#a3a3a3',
      focus: '#dc2626',
    },
    status: {
      success: '#22c55e',
      warning: '#f59e0b',
      error: '#ef4444',
      info: '#3b82f6',
    },
  },
  cssVariables: {
    '--primary': '#dc2626',
    '--primary-foreground': '#ffffff',
    '--secondary': '#737373',
    '--secondary-foreground': '#ffffff',
    '--background': '#fafafa',
    '--foreground': '#171717',
    '--card': '#ffffff',
    '--card-foreground': '#171717',
    '--border': '#e5e5e5',
    '--ring': '#dc2626',
  },
  brandGradients: {
    primary: 'linear-gradient(135deg, #dc2626 0%, #991b1b 100%)',
    secondary: 'linear-gradient(135deg, #737373 0%, #404040 100%)',
    hero: 'linear-gradient(135deg, #dc2626 0%, #737373 100%)',
    card: 'linear-gradient(135deg, #ffffff 0%, #fafafa 100%)',
  },
  shadows: {
    sm: '0 1px 2px 0 rgba(220, 38, 38, 0.05)',
    md: '0 4px 6px -1px rgba(220, 38, 38, 0.1)',
    lg: '0 10px 15px -3px rgba(220, 38, 38, 0.1)',
    colored: '0 4px 14px 0 rgba(220, 38, 38, 0.25)',
  },
};

// Hiring Manager Theme - Growth Green
const hiringTheme: EnterpriseRoleTheme = {
  id: 'Hiring Manager',
  name: 'Hiring Manager',
  className: 'theme-hiring',
  colors: {
    primary: {
      50: '#f0fdf4',
      100: '#dcfce7',
      500: '#22c55e',
      600: '#16a34a',
      700: '#15803d',
      900: '#14532d',
      foreground: '#ffffff',
    },
    secondary: {
      50: '#f7fee7',
      100: '#ecfccb',
      500: '#84cc16',
      600: '#65a30d',
      700: '#4d7c0f',
      900: '#365314',
      foreground: '#ffffff',
    },
    background: {
      default: '#fefffe',
      elevated: '#ffffff',
      overlay: 'rgba(0, 0, 0, 0.8)',
    },
    surface: {
      default: '#f9fafb',
      elevated: '#ffffff',
      sunken: '#f3f4f6',
      overlay: 'rgba(255, 255, 255, 0.95)',
    },
    content: {
      primary: '#111827',
      secondary: '#374151',
      tertiary: '#6b7280',
      inverse: '#ffffff',
      disabled: '#9ca3af',
    },
    border: {
      default: '#e5e7eb',
      subtle: '#f3f4f6',
      strong: '#d1d5db',
      interactive: '#16a34a',
    },
    interactive: {
      default: '#16a34a',
      hover: '#15803d',
      active: '#14532d',
      disabled: '#9ca3af',
      focus: '#16a34a',
    },
    status: {
      success: '#22c55e',
      warning: '#f59e0b',
      error: '#ef4444',
      info: '#3b82f6',
    },
  },
  cssVariables: {
    '--primary': '#16a34a',
    '--primary-foreground': '#ffffff',
    '--secondary': '#84cc16',
    '--secondary-foreground': '#ffffff',
    '--background': '#fefffe',
    '--foreground': '#111827',
    '--card': '#f9fafb',
    '--card-foreground': '#111827',
    '--border': '#e5e7eb',
    '--ring': '#16a34a',
  },
  brandGradients: {
    primary: 'linear-gradient(135deg, #16a34a 0%, #14532d 100%)',
    secondary: 'linear-gradient(135deg, #84cc16 0%, #4d7c0f 100%)',
    hero: 'linear-gradient(135deg, #22c55e 0%, #84cc16 100%)',
    card: 'linear-gradient(135deg, #f9fafb 0%, #ffffff 100%)',
  },
  shadows: {
    sm: '0 1px 2px 0 rgba(22, 163, 74, 0.05)',
    md: '0 4px 6px -1px rgba(22, 163, 74, 0.1)',
    lg: '0 10px 15px -3px rgba(22, 163, 74, 0.1)',
    colored: '0 4px 14px 0 rgba(22, 163, 74, 0.25)',
  },
};

// Recruiter Theme - Dynamic Purple
const recruiterTheme: EnterpriseRoleTheme = {
  id: 'Recruiter',
  name: 'Recruiter',
  className: 'theme-recruiter',
  colors: {
    primary: {
      50: '#faf5ff',
      100: '#f3e8ff',
      500: '#a855f7',
      600: '#9333ea',
      700: '#7c3aed',
      900: '#581c87',
      foreground: '#ffffff',
    },
    secondary: {
      50: '#fdf4ff',
      100: '#fae8ff',
      500: '#d946ef',
      600: '#c026d3',
      700: '#a21caf',
      900: '#701a75',
      foreground: '#ffffff',
    },
    background: {
      default: '#fdfdfe',
      elevated: '#ffffff',
      overlay: 'rgba(0, 0, 0, 0.8)',
    },
    surface: {
      default: '#fafbfc',
      elevated: '#ffffff',
      sunken: '#f4f5f7',
      overlay: 'rgba(255, 255, 255, 0.95)',
    },
    content: {
      primary: '#0f0f23',
      secondary: '#2d2d40',
      tertiary: '#52525b',
      inverse: '#ffffff',
      disabled: '#a1a1aa',
    },
    border: {
      default: '#e4e4e7',
      subtle: '#f4f4f5',
      strong: '#d4d4d8',
      interactive: '#9333ea',
    },
    interactive: {
      default: '#9333ea',
      hover: '#7c3aed',
      active: '#6d28d9',
      disabled: '#a1a1aa',
      focus: '#9333ea',
    },
    status: {
      success: '#22c55e',
      warning: '#f59e0b',
      error: '#ef4444',
      info: '#3b82f6',
    },
  },
  cssVariables: {
    '--primary': '#9333ea',
    '--primary-foreground': '#ffffff',
    '--secondary': '#d946ef',
    '--secondary-foreground': '#ffffff',
    '--background': '#fdfdfe',
    '--foreground': '#0f0f23',
    '--card': '#fafbfc',
    '--card-foreground': '#0f0f23',
    '--border': '#e4e4e7',
    '--ring': '#9333ea',
  },
  brandGradients: {
    primary: 'linear-gradient(135deg, #9333ea 0%, #7c3aed 100%)',
    secondary: 'linear-gradient(135deg, #d946ef 0%, #a21caf 100%)',
    hero: 'linear-gradient(135deg, #a855f7 0%, #d946ef 100%)',
    card: 'linear-gradient(135deg, #fafbfc 0%, #ffffff 100%)',
  },
  shadows: {
    sm: '0 1px 2px 0 rgba(147, 51, 234, 0.05)',
    md: '0 4px 6px -1px rgba(147, 51, 234, 0.1)',
    lg: '0 10px 15px -3px rgba(147, 51, 234, 0.1)',
    colored: '0 4px 14px 0 rgba(147, 51, 234, 0.25)',
  },
};

// Applicant Theme - Approachable Orange
const applicantTheme: EnterpriseRoleTheme = {
  id: 'Applicant',
  name: 'Applicant',
  className: 'theme-applicant',
  colors: {
    primary: {
      50: '#fff7ed',
      100: '#ffedd5',
      500: '#f97316',
      600: '#ea580c',
      700: '#c2410c',
      900: '#9a3412',
      foreground: '#ffffff',
    },
    secondary: {
      50: '#fffbeb',
      100: '#fef3c7',
      500: '#f59e0b',
      600: '#d97706',
      700: '#b45309',
      900: '#78350f',
      foreground: '#ffffff',
    },
    background: {
      default: '#fffefb',
      elevated: '#ffffff',
      overlay: 'rgba(0, 0, 0, 0.8)',
    },
    surface: {
      default: '#fefcf8',
      elevated: '#ffffff',
      sunken: '#f9f7f4',
      overlay: 'rgba(255, 255, 255, 0.95)',
    },
    content: {
      primary: '#1c1917',
      secondary: '#44403c',
      tertiary: '#78716c',
      inverse: '#ffffff',
      disabled: '#a8a29e',
    },
    border: {
      default: '#e7e5e4',
      subtle: '#f5f5f4',
      strong: '#d6d3d1',
      interactive: '#ea580c',
    },
    interactive: {
      default: '#ea580c',
      hover: '#c2410c',
      active: '#9a3412',
      disabled: '#a8a29e',
      focus: '#ea580c',
    },
    status: {
      success: '#22c55e',
      warning: '#f59e0b',
      error: '#ef4444',
      info: '#3b82f6',
    },
  },
  cssVariables: {
    '--primary': '#ea580c',
    '--primary-foreground': '#ffffff',
    '--secondary': '#f59e0b',
    '--secondary-foreground': '#ffffff',
    '--background': '#fffefb',
    '--foreground': '#1c1917',
    '--card': '#fefcf8',
    '--card-foreground': '#1c1917',
    '--border': '#e7e5e4',
    '--ring': '#ea580c',
  },
  brandGradients: {
    primary: 'linear-gradient(135deg, #ea580c 0%, #c2410c 100%)',
    secondary: 'linear-gradient(135deg, #f59e0b 0%, #b45309 100%)',
    hero: 'linear-gradient(135deg, #f97316 0%, #f59e0b 100%)',
    card: 'linear-gradient(135deg, #fefcf8 0%, #ffffff 100%)',
  },
  shadows: {
    sm: '0 1px 2px 0 rgba(234, 88, 12, 0.05)',
    md: '0 4px 6px -1px rgba(234, 88, 12, 0.1)',
    lg: '0 10px 15px -3px rgba(234, 88, 12, 0.1)',
    colored: '0 4px 14px 0 rgba(234, 88, 12, 0.25)',
  },
};

export const enterpriseRoleThemes: Record<UserRole, EnterpriseRoleTheme> = {
  Executive: executiveTheme,
  HR: hrTheme,
  Admin: adminTheme,
  'Hiring Manager': hiringTheme,
  Recruiter: recruiterTheme,
  Applicant: applicantTheme,
};

export const getThemeForRole = (role: UserRole): EnterpriseRoleTheme => {
  return enterpriseRoleThemes[role];
};

export const getAllThemes = (): EnterpriseRoleTheme[] => {
  return Object.values(enterpriseRoleThemes);
};
