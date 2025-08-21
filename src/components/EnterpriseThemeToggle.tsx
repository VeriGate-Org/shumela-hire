'use client';

import React, { useState } from 'react';
import { useTheme } from '../contexts/ThemeContext';
import { enterpriseRoleThemes, getAllThemes } from '../config/enterpriseTheme';
import { UserRole } from '../contexts/AuthContext';

interface EnterpriseThemeToggleProps {
  variant?: 'compact' | 'full' | 'floating';
  showPreview?: boolean;
  className?: string;
}

export default function EnterpriseThemeToggle({ 
  variant = 'full', 
  showPreview = true,
  className = ''
}: EnterpriseThemeToggleProps) {
  const { 
    mode, 
    setMode, 
    currentRole, 
    applyRoleTheme, 
    isDark, 
    isHighContrast,
    enableHighContrast 
  } = useTheme();
  
  const [isExpanded, setIsExpanded] = useState(false);

  const handleModeChange = (newMode: 'light' | 'dark' | 'system' | 'high-contrast') => {
    if (newMode === 'high-contrast') {
      enableHighContrast(!isHighContrast);
    } else {
      enableHighContrast(false);
      setMode(newMode);
    }
  };

  const handleRoleChange = (role: UserRole) => {
    applyRoleTheme(role);
    if (variant === 'compact') {
      setIsExpanded(false);
    }
  };

  if (variant === 'compact') {
    return (
      <div className={`relative ${className}`}>
        <button
          onClick={() => setIsExpanded(!isExpanded)}
          className="enterprise-card p-3 hover:shadow-lg transition-all duration-200 rounded-lg"
          aria-label="Open theme settings"
        >
          <div className="flex items-center space-x-2">
            <div 
              className="w-4 h-4 rounded-full border-2 border-white shadow-sm"
              style={{ backgroundColor: enterpriseRoleThemes[currentRole!].colors.primary[600] }}
            />
            <span className="text-sm font-medium text-content-primary">
              {enterpriseRoleThemes[currentRole!].name}
            </span>
            <svg 
              className={`w-4 h-4 transition-transform ${isExpanded ? 'rotate-180' : ''}`}
              fill="none" 
              stroke="currentColor" 
              viewBox="0 0 24 24"
            >
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
            </svg>
          </div>
        </button>

        {isExpanded && (
          <div className="absolute top-full left-0 mt-2 w-80 enterprise-card-elevated p-4 rounded-xl shadow-xl z-50 animate-scale-in">
            <CompactThemeSelector 
              currentRole={currentRole!}
              mode={mode}
              isHighContrast={isHighContrast}
              onRoleChange={handleRoleChange}
              onModeChange={handleModeChange}
            />
          </div>
        )}
      </div>
    );
  }

  if (variant === 'floating') {
    return (
      <div className={`fixed bottom-6 right-6 z-50 ${className}`}>
        <div className="enterprise-card-elevated p-4 rounded-xl shadow-2xl max-w-xs">
          <h3 className="text-lg font-semibold text-content-primary mb-4">Theme Settings</h3>
          <FullThemeControls
            currentRole={currentRole!}
            mode={mode}
            isHighContrast={isHighContrast}
            onRoleChange={handleRoleChange}
            onModeChange={handleModeChange}
            showPreview={showPreview}
          />
        </div>
      </div>
    );
  }

  return (
    <div className={`enterprise-card p-6 rounded-xl ${className}`}>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-semibold text-content-primary">Theme Customization</h2>
        <div className="flex items-center space-x-2">
          <div 
            className="w-3 h-3 rounded-full border border-border"
            style={{ backgroundColor: enterpriseRoleThemes[currentRole!].colors.primary[600] }}
          />
          <span className="text-sm text-content-secondary">
            {isDark ? 'Dark' : 'Light'} • {enterpriseRoleThemes[currentRole!].name}
          </span>
        </div>
      </div>

      <FullThemeControls
        currentRole={currentRole!}
        mode={mode}
        isHighContrast={isHighContrast}
        onRoleChange={handleRoleChange}
        onModeChange={handleModeChange}
        showPreview={showPreview}
      />
    </div>
  );
}

interface CompactThemeSelectorProps {
  currentRole: UserRole;
  mode: string;
  isHighContrast: boolean;
  onRoleChange: (role: UserRole) => void;
  onModeChange: (mode: 'light' | 'dark' | 'system' | 'high-contrast') => void;
}

function CompactThemeSelector({
  currentRole,
  mode,
  isHighContrast,
  onRoleChange,
  onModeChange
}: CompactThemeSelectorProps) {
  return (
    <div className="space-y-4">
      <div>
        <label className="block text-sm font-medium text-content-primary mb-2">
          Theme Mode
        </label>
        <div className="grid grid-cols-2 gap-2">
          {[
            { key: 'light', label: 'Light', icon: '☀️' },
            { key: 'dark', label: 'Dark', icon: '🌙' },
            { key: 'system', label: 'System', icon: '💻' },
            { key: 'high-contrast', label: 'High Contrast', icon: '🔍' }
          ].map(({ key, label, icon }) => (
            <button
              key={key}
              onClick={() => onModeChange(key as any)}
              className={`p-2 rounded-lg text-xs font-medium transition-all ${
                (key === 'high-contrast' ? isHighContrast : mode === key)
                  ? 'bg-primary text-primary-foreground'
                  : 'bg-surface-elevated text-content-secondary hover:bg-accent'
              }`}
            >
              <span className="mr-1">{icon}</span>
              {label}
            </button>
          ))}
        </div>
      </div>

      <div>
        <label className="block text-sm font-medium text-content-primary mb-2">
          Role Theme
        </label>
        <div className="grid grid-cols-2 gap-1">
          {getAllThemes().map((theme) => (
            <button
              key={theme.id}
              onClick={() => onRoleChange(theme.id)}
              className={`p-2 rounded-lg text-xs transition-all flex items-center space-x-2 ${
                currentRole === theme.id
                  ? 'bg-primary text-primary-foreground'
                  : 'hover:bg-accent text-content-secondary'
              }`}
            >
              <div 
                className="w-3 h-3 rounded-full border border-white/20"
                style={{ backgroundColor: theme.colors.primary[600] }}
              />
              <span className="truncate">{theme.name}</span>
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}

interface FullThemeControlsProps extends CompactThemeSelectorProps {
  showPreview: boolean;
}

function FullThemeControls({
  currentRole,
  mode,
  isHighContrast,
  onRoleChange,
  onModeChange,
  showPreview
}: FullThemeControlsProps) {
  return (
    <div className="space-y-6">
      {/* Theme Mode Selection */}
      <div>
        <label className="block text-sm font-medium text-content-primary mb-3">
          Appearance Mode
        </label>
        <div className="grid grid-cols-4 gap-3">
          {[
            { key: 'light', label: 'Light', icon: '☀️', desc: 'Light theme' },
            { key: 'dark', label: 'Dark', icon: '🌙', desc: 'Dark theme' },
            { key: 'system', label: 'System', icon: '💻', desc: 'Follow system preference' },
            { key: 'high-contrast', label: 'High Contrast', icon: '🔍', desc: 'High contrast for accessibility' }
          ].map(({ key, label, icon, desc }) => (
            <button
              key={key}
              onClick={() => onModeChange(key as any)}
              className={`p-3 rounded-lg border transition-all text-center ${
                (key === 'high-contrast' ? isHighContrast : mode === key)
                  ? 'border-primary bg-primary/10 text-primary'
                  : 'border-border hover:border-border-interactive hover:bg-accent'
              }`}
              title={desc}
            >
              <div className="text-lg mb-1">{icon}</div>
              <div className="text-xs font-medium">{label}</div>
            </button>
          ))}
        </div>
      </div>

      {/* Role Theme Selection */}
      <div>
        <label className="block text-sm font-medium text-content-primary mb-3">
          Role Theme
        </label>
        <div className="grid grid-cols-2 gap-3">
          {getAllThemes().map((theme) => (
            <button
              key={theme.id}
              onClick={() => onRoleChange(theme.id)}
              className={`p-4 rounded-lg border transition-all ${
                currentRole === theme.id
                  ? 'border-primary bg-primary/10'
                  : 'border-border hover:border-border-interactive hover:bg-accent'
              }`}
            >
              <div className="flex items-center space-x-3 mb-2">
                <div 
                  className="w-4 h-4 rounded-full border border-white shadow-sm"
                  style={{ backgroundColor: theme.colors.primary[600] }}
                />
                <span className="font-medium text-content-primary">{theme.name}</span>
              </div>
              <div className="flex space-x-1">
                <div 
                  className="w-6 h-2 rounded-full"
                  style={{ backgroundColor: theme.colors.primary[500] }}
                />
                <div 
                  className="w-6 h-2 rounded-full"
                  style={{ backgroundColor: theme.colors.secondary[500] }}
                />
                <div 
                  className="w-6 h-2 rounded-full"
                  style={{ backgroundColor: theme.colors.status.success }}
                />
              </div>
            </button>
          ))}
        </div>
      </div>

      {/* Theme Preview */}
      {showPreview && (
        <div>
          <label className="block text-sm font-medium text-content-primary mb-3">
            Preview
          </label>
          <div className="enterprise-card p-4 rounded-lg space-y-3">
            <div className="flex items-center justify-between">
              <h4 className="font-medium text-content-primary">Sample Card</h4>
              <div className="text-xs text-content-tertiary">Today</div>
            </div>
            <p className="text-sm text-content-secondary">
              This is how your interface will look with the selected theme.
            </p>
            <div className="flex space-x-2">
              <button className="btn-primary px-3 py-1 text-xs rounded-md">
                Primary Button
              </button>
              <button className="btn-secondary px-3 py-1 text-xs rounded-md">
                Secondary
              </button>
              <button className="btn-outline px-3 py-1 text-xs rounded-md">
                Outline
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
