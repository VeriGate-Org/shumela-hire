'use client';

import ReportVisualization from '../../components/ReportVisualization';
import DashboardLayout from '../../components/DashboardLayout';

export default function VisualizationPage() {
  return (
    <DashboardLayout 
      title="Data Visualization" 
      subtitle="Interactive charts and analytics for recruitment insights"
    >
      <ReportVisualization />
    </DashboardLayout>
  );
}
