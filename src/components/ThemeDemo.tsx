'use client';

import React from 'react';
import ThemeToggle from './ThemeToggle';
import { useTheme } from '../contexts/ThemeContext';
import { Building2, Users, TrendingUp, Calendar, CheckCircle } from 'lucide-react';

export default function ThemeDemo() {
  const { currentRole, roleTheme, isDark } = useTheme();

  const demoStats = [
    { label: 'Active Jobs', value: '24', icon: Building2, trend: '+12%' },
    { label: 'Candidates', value: '156', icon: Users, trend: '+8%' },
    { label: 'Success Rate', value: '92%', icon: TrendingUp, trend: '+5%' },
    { label: 'Interviews', value: '48', icon: Calendar, trend: '+15%' },
    { label: 'Placements', value: '18', icon: CheckCircle, trend: '+20%' },
  ];

  return (
    <div className="min-h-screen bg-background transition-colors duration-300">
      {/* Header with Theme Controls */}
      <div className="border-b border-border bg-card/50 backdrop-blur-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            <div className="flex items-center gap-4">
              <h1 className="text-xl font-bold text-foreground">
                🎨 Theme System Demo
              </h1>
              <div className="flex items-center gap-2 text-sm text-muted-foreground">
                <div className="w-2 h-2 rounded-full bg-primary animate-pulse" />
                <span>Role: {currentRole}</span>
                <span>•</span>
                <span>Mode: {isDark ? 'Dark' : 'Light'}</span>
              </div>
            </div>
            <ThemeToggle compact showRoleSwitch />
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Hero Section with Role-specific Gradient */}
        <div className={`rounded-2xl p-8 mb-8 bg-gradient-to-r ${roleTheme.brandGradients.hero} border border-border/50`}>
          <div className="flex items-center gap-4 mb-4">
            <div className="w-16 h-16 rounded-full bg-primary/10 flex items-center justify-center text-2xl border border-primary/20">
              {currentRole === 'Admin' && '👑'}
              {currentRole === 'HR' && '👔'}
              {currentRole === 'Hiring Manager' && '🎯'}
              {currentRole === 'Recruiter' && '🔍'}
              {currentRole === 'Applicant' && '👤'}
              {currentRole === 'Executive' && '🏛️'}
            </div>
            <div>
              <h2 className="text-3xl font-bold text-foreground mb-2">
                {currentRole} Dashboard
              </h2>
              <p className="text-muted-foreground text-lg">
                Experience the power of role-based theming and dark/light mode
              </p>
            </div>
          </div>
          
          <div className="flex flex-wrap gap-4">
            <button className="px-6 py-3 bg-primary text-primary-foreground rounded-lg font-medium hover:opacity-90 transition-all duration-200 hover:scale-105 shadow-role-glow">
              Primary Action
            </button>
            <button className="px-6 py-3 bg-secondary text-secondary-foreground rounded-lg font-medium hover:bg-accent transition-all duration-200">
              Secondary Action
            </button>
            <button className="px-6 py-3 border border-border text-foreground rounded-lg font-medium hover:bg-accent transition-all duration-200">
              Outlined Action
            </button>
          </div>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-5 gap-6 mb-8">
          {demoStats.map((stat, index) => (
            <div
              key={stat.label}
              className="bg-card border border-border rounded-xl p-6 hover:shadow-lg transition-all duration-300 hover:scale-105"
            >
              <div className="flex items-center justify-between mb-4">
                <stat.icon className="h-8 w-8 text-primary" />
                <span className="text-xs font-medium text-green-600 bg-green-100 dark:bg-green-900/30 dark:text-green-400 px-2 py-1 rounded-full">
                  {stat.trend}
                </span>
              </div>
              <div className="space-y-1">
                <p className="text-2xl font-bold text-foreground">{stat.value}</p>
                <p className="text-sm text-muted-foreground">{stat.label}</p>
              </div>
            </div>
          ))}
        </div>

        {/* Cards Demo */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8">
          {/* Chart Card */}
          <div className="bg-card border border-border rounded-xl p-6">
            <h3 className="text-lg font-semibold text-foreground mb-4">
              📊 Analytics Overview
            </h3>
            <div className="h-48 bg-muted/30 rounded-lg flex items-center justify-center">
              <div className="text-center">
                <div className={`w-16 h-16 mx-auto mb-4 rounded-full bg-gradient-to-br ${roleTheme.brandGradients.primary} flex items-center justify-center`}>
                  <TrendingUp className="h-8 w-8 text-white" />
                </div>
                <p className="text-muted-foreground">Chart visualization area</p>
                <p className="text-sm text-muted-foreground mt-2">
                  Theme colors: <span className="font-mono text-primary">{roleTheme.colors.primary[500]}</span>
                </p>
              </div>
            </div>
          </div>

          {/* Activity Feed */}
          <div className="bg-card border border-border rounded-xl p-6">
            <h3 className="text-lg font-semibold text-foreground mb-4">
              🔔 Recent Activity
            </h3>
            <div className="space-y-4">
              {[1, 2, 3, 4].map((item) => (
                <div key={item} className="flex items-center gap-3 p-3 bg-muted/20 rounded-lg">
                  <div className="w-2 h-2 rounded-full bg-primary animate-pulse" />
                  <div className="flex-1">
                    <p className="text-sm font-medium text-foreground">
                      Sample activity item {item}
                    </p>
                    <p className="text-xs text-muted-foreground">
                      {item} minutes ago
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Color Palette Demo */}
        <div className="bg-card border border-border rounded-xl p-6">
          <h3 className="text-lg font-semibold text-foreground mb-6">
            🎨 Current Theme Palette
          </h3>
          <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-4">
            <div className="text-center">
              <div className="w-16 h-16 bg-primary rounded-lg mb-2 mx-auto shadow-lg" />
              <p className="text-xs font-medium text-foreground">Primary</p>
            </div>
            <div className="text-center">
              <div className="w-16 h-16 bg-secondary rounded-lg mb-2 mx-auto shadow-lg" />
              <p className="text-xs font-medium text-foreground">Secondary</p>
            </div>
            <div className="text-center">
              <div className="w-16 h-16 bg-accent rounded-lg mb-2 mx-auto shadow-lg" />
              <p className="text-xs font-medium text-foreground">Accent</p>
            </div>
            <div className="text-center">
              <div className="w-16 h-16 bg-muted rounded-lg mb-2 mx-auto shadow-lg" />
              <p className="text-xs font-medium text-foreground">Muted</p>
            </div>
            <div className="text-center">
              <div className="w-16 h-16 bg-card border border-border rounded-lg mb-2 mx-auto shadow-lg" />
              <p className="text-xs font-medium text-foreground">Card</p>
            </div>
            <div className="text-center">
              <div className="w-16 h-16 bg-destructive rounded-lg mb-2 mx-auto shadow-lg" />
              <p className="text-xs font-medium text-foreground">Destructive</p>
            </div>
          </div>
        </div>

        {/* Instructions */}
        <div className="mt-8 bg-muted/30 border border-border rounded-xl p-6">
          <h3 className="text-lg font-semibold text-foreground mb-4">
            🎯 How to Use Themes
          </h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <h4 className="font-medium text-foreground mb-2">Role-Based Themes:</h4>
              <ul className="text-sm text-muted-foreground space-y-1">
                <li>• Each role has its own color scheme</li>
                <li>• Automatic theme switching based on user role</li>
                <li>• Consistent branding across role experiences</li>
                <li>• Use the role selector above to test</li>
              </ul>
            </div>
            <div>
              <h4 className="font-medium text-foreground mb-2">Dark/Light Mode:</h4>
              <ul className="text-sm text-muted-foreground space-y-1">
                <li>• Toggle between light, dark, and system</li>
                <li>• Preserves role colors in both modes</li>
                <li>• Smooth transitions and animations</li>
                <li>• Respects user preferences</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
