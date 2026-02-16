import React, { useState, useMemo } from 'react';
import { 
  CalendarIcon, 
  ChartBarIcon, 
  UsersIcon, 
  ClockIcon,
  ArrowTrendingUpIcon,
  DocumentChartBarIcon
} from '@heroicons/react/24/outline';
import {
  ApplicationVolumeChart,
  PipelineFunnelChart,
  SourceEffectivenessChart,
  TimeToHireChart,
  PerformanceGaugeChart,
  HiringManagerPerformanceChart,
  MonthlyTrendsChart,
} from '../charts';

// Mock data - In real app, this would come from APIs
const mockApplicationVolumeData = Array.from({ length: 30 }, (_, i) => ({
  date: new Date(Date.now() - (29 - i) * 24 * 60 * 60 * 1000).toISOString(),
  applications: Math.floor(Math.random() * 50) + 20,
  interviews: Math.floor(Math.random() * 25) + 5,
  offers: Math.floor(Math.random() * 10) + 2,
  hires: Math.floor(Math.random() * 5) + 1,
}));

const mockPipelineData = [
  { stage: 'Applications', count: 1250, color: '#3b82f6' },
  { stage: 'Phone Screen', count: 450, color: '#6366f1' },
  { stage: 'Technical', count: 200, color: '#8b5cf6' },
  { stage: 'Final', count: 85, color: '#a855f7' },
  { stage: 'Offer', count: 45, color: '#d946ef' },
  { stage: 'Hired', count: 35, color: '#10b981' },
];

const mockSourceData = [
  { source: 'LinkedIn', applications: 450, hires: 25, conversionRate: 5.6 },
  { source: 'Indeed', applications: 320, hires: 15, conversionRate: 4.7 },
  { source: 'Company Site', applications: 280, hires: 22, conversionRate: 7.9 },
  { source: 'Referrals', applications: 150, hires: 35, conversionRate: 23.3 },
  { source: 'University', applications: 200, hires: 18, conversionRate: 9.0 },
];

const mockTimeToHireData = [
  { position: 'Software Engineer', timeToHire: 25, target: 30 },
  { position: 'Product Manager', timeToHire: 35, target: 28 },
  { position: 'Data Scientist', timeToHire: 42, target: 35 },
  { position: 'UX Designer', timeToHire: 28, target: 25 },
  { position: 'DevOps Engineer', timeToHire: 38, target: 32 },
];

const mockPerformanceData = [
  { metric: 'Time to Fill', current: 32, target: 30, percentage: 93.8 },
  { metric: 'Quality of Hire', current: 4.2, target: 4.0, percentage: 105.0 },
  { metric: 'Offer Acceptance', current: 78, target: 80, percentage: 97.5 },
  { metric: 'Cost per Hire', current: 3200, target: 3500, percentage: 91.4 },
];

const mockHiringManagerData = [
  { manager: 'Sarah Chen', positions: 8, timeToFill: 28, satisfaction: 4.5 },
  { manager: 'Mike Rodriguez', positions: 12, timeToFill: 35, satisfaction: 4.2 },
  { manager: 'Emily Watson', positions: 6, timeToFill: 22, satisfaction: 4.8 },
  { manager: 'David Kim', positions: 15, timeToFill: 31, satisfaction: 4.1 },
  { manager: 'Lisa Park', positions: 9, timeToFill: 26, satisfaction: 4.6 },
];

const mockMonthlyData = Array.from({ length: 12 }, (_, i) => ({
  month: new Date(2024, i, 1).toLocaleDateString('en-US', { month: 'short' }),
  applications: Math.floor(Math.random() * 500) + 300,
  interviews: Math.floor(Math.random() * 200) + 100,
  offers: Math.floor(Math.random() * 50) + 25,
  hires: Math.floor(Math.random() * 30) + 15,
  rejections: Math.floor(Math.random() * 400) + 200,
}));

// Time range selector
const timeRanges = [
  { key: 'week', label: 'Last 7 Days' },
  { key: 'month', label: 'Last 30 Days' },
  { key: 'quarter', label: 'Last 3 Months' },
  { key: 'year', label: 'Last 12 Months' },
] as const;

interface AdvancedAnalyticsDashboardProps {
  className?: string;
}

export const AdvancedAnalyticsDashboard: React.FC<AdvancedAnalyticsDashboardProps> = ({
  className = '',
}) => {
  const [selectedTimeRange, setSelectedTimeRange] = useState<typeof timeRanges[number]['key']>('month');
  const [selectedView, setSelectedView] = useState<'overview' | 'performance' | 'sources' | 'managers'>('overview');

  // Filter data based on selected time range
  const filteredVolumeData = useMemo(() => {
    const days = selectedTimeRange === 'week' ? 7 : selectedTimeRange === 'month' ? 30 : 90;
    return mockApplicationVolumeData.slice(-days);
  }, [selectedTimeRange]);

  // Summary statistics
  const summaryStats = useMemo(() => {
    const totalApplications = filteredVolumeData.reduce((sum, day) => sum + day.applications, 0);
    const totalHires = filteredVolumeData.reduce((sum, day) => sum + day.hires, 0);
    const conversionRate = totalApplications > 0 ? (totalHires / totalApplications) * 100 : 0;
    const avgTimeToHire = mockTimeToHireData.reduce((sum, pos) => sum + pos.timeToHire, 0) / mockTimeToHireData.length;

    return {
      totalApplications,
      totalHires,
      conversionRate,
      avgTimeToHire,
      activePositions: mockTimeToHireData.length,
      pipelineValue: mockPipelineData.reduce((sum, stage) => sum + stage.count, 0),
    };
  }, [filteredVolumeData]);

  return (
    <div className={`space-y-6 ${className}`}>
      {/* Header with controls */}
      <div className="bg-white rounded-sm border border-gray-200 border-t-2 border-t-gold-500 p-6">
        <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Advanced Analytics Dashboard</h1>
            <p className="text-gray-500">Comprehensive recruitment metrics and insights</p>
          </div>
          
          {/* Time range selector */}
          <div className="flex flex-wrap gap-2">
            {timeRanges.map((range) => (
              <button
                key={range.key}
                onClick={() => setSelectedTimeRange(range.key)}
                className={`px-4 py-2 rounded-sm text-sm font-medium transition-colors ${
                  selectedTimeRange === range.key
                    ? 'bg-gold-100 text-violet-700'
                    : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                }`}
              >
                {range.label}
              </button>
            ))}
          </div>
        </div>

        {/* View selector */}
        <div className="flex flex-wrap gap-2 mt-4">
          {[
            { key: 'overview', label: 'Overview', icon: ChartBarIcon },
            { key: 'performance', label: 'Performance', icon: ArrowTrendingUpIcon },
            { key: 'sources', label: 'Sources', icon: DocumentChartBarIcon },
            { key: 'managers', label: 'Managers', icon: UsersIcon },
          ].map(({ key, label, icon: Icon }) => (
            <button
              key={key}
              onClick={() => setSelectedView(key as any)}
              className={`flex items-center gap-2 px-4 py-2 rounded-sm text-sm font-medium transition-colors ${
                selectedView === key
                  ? 'bg-gold-500 text-violet-950'
                  : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
              }`}
            >
              <Icon className="w-4 h-4" />
              {label}
            </button>
          ))}
        </div>
      </div>

      {/* Summary cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6 gap-4">
        {[
          {
            title: 'Total Applications',
            value: summaryStats.totalApplications.toLocaleString(),
            icon: DocumentChartBarIcon,
            color: 'text-gold-600 bg-gold-100',
            change: '+12.5%',
            changeType: 'positive' as const,
          },
          {
            title: 'Total Hires',
            value: summaryStats.totalHires.toString(),
            icon: UsersIcon,
            color: 'text-green-600 bg-green-100',
            change: '+8.3%',
            changeType: 'positive' as const,
          },
          {
            title: 'Conversion Rate',
            value: `${summaryStats.conversionRate.toFixed(1)}%`,
            icon: ArrowTrendingUpIcon,
            color: 'text-purple-600 bg-purple-100',
            change: '-2.1%',
            changeType: 'negative' as const,
          },
          {
            title: 'Avg Time to Hire',
            value: `${summaryStats.avgTimeToHire.toFixed(0)} days`,
            icon: ClockIcon,
            color: 'text-orange-600 bg-orange-100',
            change: '-5.2%',
            changeType: 'positive' as const,
          },
          {
            title: 'Active Positions',
            value: summaryStats.activePositions.toString(),
            icon: ChartBarIcon,
            color: 'text-indigo-600 bg-indigo-100',
            change: '+3',
            changeType: 'positive' as const,
          },
          {
            title: 'Pipeline Value',
            value: summaryStats.pipelineValue.toLocaleString(),
            icon: CalendarIcon,
            color: 'text-teal-600 bg-teal-100',
            change: '+18.7%',
            changeType: 'positive' as const,
          },
        ].map((stat) => (
          <div key={stat.title} className="bg-white rounded-sm border border-gray-200 border-t-2 border-t-gold-500 p-6">
            <div className="flex items-center justify-between">
              <div className={`p-2 rounded-sm ${stat.color}`}>
                <stat.icon className="w-5 h-5" />
              </div>
              <span
                className={`text-sm font-medium ${
                  stat.changeType === 'positive' ? 'text-green-600' : 'text-red-600'
                }`}
              >
                {stat.change}
              </span>
            </div>
            <div className="mt-4">
              <h3 className="text-2xl font-bold text-gray-900">{stat.value}</h3>
              <p className="text-sm text-gray-500 mt-1">{stat.title}</p>
            </div>
          </div>
        ))}
      </div>

      {/* Main content based on selected view */}
      {selectedView === 'overview' && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <ApplicationVolumeChart
            data={filteredVolumeData}
            timeframe={selectedTimeRange === 'week' ? 'week' : selectedTimeRange === 'month' ? 'month' : 'quarter'}
            className="lg:col-span-2"
          />
          <PipelineFunnelChart data={mockPipelineData} />
          <MonthlyTrendsChart data={mockMonthlyData} />
        </div>
      )}

      {selectedView === 'performance' && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <PerformanceGaugeChart data={mockPerformanceData} />
          <TimeToHireChart data={mockTimeToHireData} />
        </div>
      )}

      {selectedView === 'sources' && (
        <div className="grid grid-cols-1 gap-6">
          <SourceEffectivenessChart data={mockSourceData} />
        </div>
      )}

      {selectedView === 'managers' && (
        <div className="grid grid-cols-1 gap-6">
          <HiringManagerPerformanceChart data={mockHiringManagerData} />
        </div>
      )}
    </div>
  );
};

export default AdvancedAnalyticsDashboard;
