'use client';

import React from 'react';
import PerformanceDashboard from '@/components/PerformanceDashboard';
import { PerformanceMonitor, usePerformanceReporting } from '@/components/PerformanceMonitor';

export default function PerformanceAnalyticsPage() {
  // Enable performance reporting
  usePerformanceReporting();

  return (
    <div className="min-h-screen bg-gray-50">
      <PerformanceDashboard />
      <PerformanceMonitor />
    </div>
  );
}
