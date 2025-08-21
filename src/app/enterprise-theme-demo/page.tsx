'use client';

import React from 'react';
import { useTheme } from '../../contexts/ThemeContext';
import EnterpriseThemeToggle from '../../components/EnterpriseThemeToggle';
import { getAllThemes } from '../../config/enterpriseTheme';

export default function EnterpriseThemeDemo() {
  const { roleTheme, currentRole, isDark, isHighContrast } = useTheme();

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <header className="nav-primary sticky top-0 z-40">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            <div className="flex items-center space-x-4">
              <div 
                className="w-8 h-8 rounded-lg flex items-center justify-center text-white font-bold text-sm"
                style={{ backgroundColor: roleTheme.colors.primary[600] }}
              >
                {currentRole?.charAt(0)}
              </div>
              <h1 className="text-xl font-semibold text-foreground">
                Enterprise Theme System
              </h1>
            </div>
            <EnterpriseThemeToggle variant="compact" />
          </div>
        </div>
      </header>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Theme Status Banner */}
        <div className="enterprise-card p-6 rounded-xl mb-8" style={{
          background: `linear-gradient(135deg, ${roleTheme.colors.primary[50]} 0%, ${roleTheme.colors.secondary[50]} 100%)`
        }}>
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-2xl font-bold text-content-primary mb-2">
                {roleTheme.name} Theme
              </h2>
              <p className="text-content-secondary">
                Current mode: <span className="font-medium">
                  {isDark ? 'Dark' : 'Light'}
                  {isHighContrast && ' (High Contrast)'}
                </span>
              </p>
            </div>
            <div className="flex items-center space-x-3">
              {Object.entries(roleTheme.colors.primary).slice(2, 6).map(([shade, color]) => (
                <div
                  key={shade}
                  className="w-12 h-12 rounded-lg border border-border shadow-sm"
                  style={{ backgroundColor: color }}
                  title={`Primary ${shade}`}
                />
              ))}
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Main Content */}
          <div className="lg:col-span-2 space-y-8">
            {/* Color Palette Section */}
            <section>
              <h3 className="text-lg font-semibold text-content-primary mb-4">
                Color Palette
              </h3>
              <div className="grid grid-cols-2 gap-6">
                <ColorPaletteCard 
                  title="Primary Colors"
                  colors={roleTheme.colors.primary}
                  name="primary"
                />
                <ColorPaletteCard 
                  title="Secondary Colors"
                  colors={roleTheme.colors.secondary}
                  name="secondary"
                />
              </div>
            </section>

            {/* Component Showcase */}
            <section>
              <h3 className="text-lg font-semibold text-content-primary mb-4">
                Component Showcase
              </h3>
              <div className="space-y-6">
                {/* Buttons */}
                <div className="enterprise-card p-6 rounded-xl">
                  <h4 className="font-medium text-content-primary mb-4">Buttons</h4>
                  <div className="flex flex-wrap gap-3">
                    <button className="btn-primary px-4 py-2 rounded-md font-medium">
                      Primary Button
                    </button>
                    <button className="btn-secondary px-4 py-2 rounded-md font-medium">
                      Secondary Button
                    </button>
                    <button className="btn-outline px-4 py-2 rounded-md font-medium">
                      Outline Button
                    </button>
                    <button className="px-4 py-2 rounded-md font-medium text-content-tertiary hover:text-content-primary transition-colors">
                      Text Button
                    </button>
                  </div>
                </div>

                {/* Cards and Surfaces */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="enterprise-card p-6 rounded-xl">
                    <h4 className="font-medium text-content-primary mb-2">Default Card</h4>
                    <p className="text-content-secondary text-sm mb-4">
                      This is a standard card component with default styling.
                    </p>
                    <div className="flex justify-between items-center text-xs text-content-tertiary">
                      <span>Status: Active</span>
                      <span>Updated 2h ago</span>
                    </div>
                  </div>

                  <div className="enterprise-card-elevated p-6 rounded-xl">
                    <h4 className="font-medium text-content-primary mb-2">Elevated Card</h4>
                    <p className="text-content-secondary text-sm mb-4">
                      This card has enhanced elevation and shadows.
                    </p>
                    <div className="w-full bg-accent rounded-lg h-2"></div>
                  </div>
                </div>

                {/* Forms */}
                <div className="enterprise-card p-6 rounded-xl">
                  <h4 className="font-medium text-content-primary mb-4">Form Elements</h4>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-medium text-content-primary mb-2">
                        Input Field
                      </label>
                      <input
                        type="text"
                        placeholder="Enter text..."
                        className="w-full px-3 py-2 border border-border rounded-md focus:ring-2 focus:ring-ring focus:border-ring bg-background text-foreground"
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-content-primary mb-2">
                        Select Field
                      </label>
                      <select className="w-full px-3 py-2 border border-border rounded-md focus:ring-2 focus:ring-ring focus:border-ring bg-background text-foreground">
                        <option>Choose option...</option>
                        <option>Option 1</option>
                        <option>Option 2</option>
                      </select>
                    </div>
                  </div>
                </div>

                {/* Status Indicators */}
                <div className="enterprise-card p-6 rounded-xl">
                  <h4 className="font-medium text-content-primary mb-4">Status Indicators</h4>
                  <div className="flex flex-wrap gap-3">
                    <StatusBadge type="success" text="Success" />
                    <StatusBadge type="warning" text="Warning" />
                    <StatusBadge type="error" text="Error" />
                    <StatusBadge type="info" text="Information" />
                  </div>
                </div>
              </div>
            </section>
          </div>

          {/* Sidebar */}
          <div className="space-y-6">
            {/* Theme Controls */}
            <EnterpriseThemeToggle variant="full" showPreview={true} />

            {/* All Themes Preview */}
            <div className="enterprise-card p-6 rounded-xl">
              <h3 className="font-semibold text-content-primary mb-4">All Themes</h3>
              <div className="space-y-3">
                {getAllThemes().map((theme) => (
                  <div key={theme.id} className="flex items-center space-x-3 p-3 rounded-lg hover:bg-accent transition-colors">
                    <div 
                      className="w-6 h-6 rounded-full border-2 border-white shadow-sm"
                      style={{ backgroundColor: theme.colors.primary[600] }}
                    />
                    <div className="flex-1">
                      <div className="font-medium text-sm text-content-primary">{theme.name}</div>
                      <div className="flex space-x-1 mt-1">
                        {[theme.colors.primary[500], theme.colors.secondary[500], theme.colors.status.success].map((color, idx) => (
                          <div
                            key={idx}
                            className="w-4 h-1 rounded-full"
                            style={{ backgroundColor: color }}
                          />
                        ))}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

interface ColorPaletteCardProps {
  title: string;
  colors: Record<string, string>;
  name: string;
}

function ColorPaletteCard({ title, colors, name }: ColorPaletteCardProps) {
  return (
    <div className="enterprise-card p-4 rounded-xl">
      <h4 className="font-medium text-content-primary mb-3">{title}</h4>
      <div className="grid grid-cols-3 gap-2">
        {Object.entries(colors).map(([shade, color]) => (
          <div key={shade} className="group cursor-pointer">
            <div 
              className="w-full h-12 rounded-lg border border-border shadow-sm group-hover:scale-105 transition-transform"
              style={{ backgroundColor: color }}
              title={`${name}-${shade}: ${color}`}
            />
            <div className="text-xs text-content-tertiary mt-1 text-center">{shade}</div>
          </div>
        ))}
      </div>
    </div>
  );
}

interface StatusBadgeProps {
  type: 'success' | 'warning' | 'error' | 'info';
  text: string;
}

function StatusBadge({ type, text }: StatusBadgeProps) {
  const colors = {
    success: 'bg-green-100 text-green-800 border-green-200',
    warning: 'bg-yellow-100 text-yellow-800 border-yellow-200',
    error: 'bg-red-100 text-red-800 border-red-200',
    info: 'bg-blue-100 text-blue-800 border-blue-200',
  };

  return (
    <span className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-medium border ${colors[type]}`}>
      {text}
    </span>
  );
}
