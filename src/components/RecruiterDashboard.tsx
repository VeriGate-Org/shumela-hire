'use client';

import React, { useState, useEffect } from 'react';

interface RecruiterMetrics {
  totalApplications: number;
  activeJobPostings: number;
  newApplicants: number;
  applicationsByStatus: { [key: string]: number };
  conversionRates: {
    screeningRate: number;
    interviewRate: number;
    hireRate: number;
  };
  dailyTrends: { [key: string]: number };
}

interface ApplicationPerVacancy {
  vacancy: string;
  applications: number;
  jobId: number;
}

interface PipelineFunnelData {
  funnel: { [key: string]: number };
  department: string;
  period: string;
}

interface TimeToFillData {
  averageDays: number;
  positions: Array<{
    jobTitle: string;
    department: string;
    daysToFill: number;
    hiredDate: string;
  }>;
  department: string;
}

interface RecentActivity {
  id: number;
  applicantName: string;
  jobTitle: string;
  status: string;
  action: string;
  timestamp: string;
  department: string;
}

interface DepartmentStats {
  departments: {
    [key: string]: {
      totalApplications: number;
      uniqueApplicants: number;
      hired: number;
      averageTimeToFill: number;
    };
  };
  period: string;
}

const RecruiterDashboard: React.FC = () => {
  const [metrics, setMetrics] = useState<RecruiterMetrics | null>(null);
  const [applicationsPerVacancy, setApplicationsPerVacancy] = useState<ApplicationPerVacancy[]>([]);
  const [pipelineFunnel, setPipelineFunnel] = useState<PipelineFunnelData | null>(null);
  const [timeToFill, setTimeToFill] = useState<TimeToFillData | null>(null);
  const [recentActivity, setRecentActivity] = useState<RecentActivity[]>([]);
  const [departmentStats, setDepartmentStats] = useState<DepartmentStats | null>(null);
  
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  // Filters
  const [dateRange, setDateRange] = useState('30');
  const [selectedDepartment, setSelectedDepartment] = useState<string>('');
  
  const departments = ['Engineering', 'Marketing', 'Sales', 'HR', 'Finance', 'Operations'];

  useEffect(() => {
    fetchDashboardData();
  }, [dateRange, selectedDepartment]);

  const fetchDashboardData = async () => {
    setLoading(true);
    setError(null);
    
    try {
      // Fetch all dashboard data in parallel
      const [
        metricsResponse,
        vacancyResponse,
        funnelResponse,
        timeToFillResponse,
        activityResponse,
        statsResponse
      ] = await Promise.all([
        fetch(`/api/recruiter/metrics?days=${dateRange}`),
        fetch(`/api/recruiter/applications/per-vacancy?days=${dateRange}`),
        fetch(`/api/recruiter/pipeline/funnel?department=${selectedDepartment}&days=${dateRange}`),
        fetch(`/api/recruiter/time-to-fill?department=${selectedDepartment}&days=${dateRange}`),
        fetch('/api/recruiter/activity?limit=10'),
        fetch(`/api/recruiter/departments/stats?days=${dateRange}`)
      ]);

      const [
        metricsData,
        vacancyData,
        funnelData,
        timeToFillData,
        activityData,
        statsData
      ] = await Promise.all([
        metricsResponse.json(),
        vacancyResponse.json(),
        funnelResponse.json(),
        timeToFillResponse.json(),
        activityResponse.json(),
        statsResponse.json()
      ]);

      setMetrics(metricsData);
      setApplicationsPerVacancy(vacancyData);
      setPipelineFunnel(funnelData);
      setTimeToFill(timeToFillData);
      setRecentActivity(activityData);
      setDepartmentStats(statsData);
      
    } catch (err) {
      setError('Failed to fetch dashboard data');
      console.error('Dashboard fetch error:', err);
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadgeColor = (status: string) => {
    switch (status.toLowerCase()) {
      case 'hired': return 'bg-green-100 text-green-800';
      case 'interview_scheduled': return 'bg-blue-100 text-blue-800';
      case 'screening': return 'bg-yellow-100 text-yellow-800';
      case 'rejected': return 'bg-red-100 text-red-800';
      case 'withdrawn': return 'bg-gray-100 text-gray-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="bg-white p-8 rounded-lg shadow-lg max-w-md w-full">
          <h2 className="text-xl font-semibold text-red-600 mb-4">Error</h2>
          <p className="text-gray-600 mb-4">{error}</p>
          <button 
            onClick={fetchDashboardData}
            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
          >
            Try Again
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6 p-6 bg-gray-50 min-h-screen">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Recruiter Dashboard</h1>
          <p className="text-gray-600">Analytics and insights for recruitment performance</p>
        </div>
        
        {/* Filters */}
        <div className="flex gap-4">
          <select 
            value={dateRange} 
            onChange={(e) => setDateRange(e.target.value)}
            className="border border-gray-300 rounded px-3 py-2"
          >
            <option value="7">Last 7 days</option>
            <option value="30">Last 30 days</option>
            <option value="90">Last 90 days</option>
            <option value="180">Last 6 months</option>
          </select>

          <select 
            value={selectedDepartment} 
            onChange={(e) => setSelectedDepartment(e.target.value)}
            className="border border-gray-300 rounded px-3 py-2"
          >
            <option value="">All Departments</option>
            {departments.map((dept) => (
              <option key={dept} value={dept}>{dept}</option>
            ))}
          </select>
        </div>
      </div>

      {/* KPI Cards */}
      {metrics && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <div className="bg-white p-6 rounded-lg shadow">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Total Applications</p>
                <p className="text-2xl font-bold text-gray-900">{metrics.totalApplications}</p>
                <p className="text-xs text-gray-500">{metrics.newApplicants} new applicants</p>
              </div>
              <div className="p-3 bg-blue-100 rounded-full">
                <svg className="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
              </div>
            </div>
          </div>

          <div className="bg-white p-6 rounded-lg shadow">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Active Job Postings</p>
                <p className="text-2xl font-bold text-gray-900">{metrics.activeJobPostings}</p>
                <p className="text-xs text-gray-500">Open positions</p>
              </div>
              <div className="p-3 bg-green-100 rounded-full">
                <svg className="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m8 0H8m8 0v1.5a2.5 2.5 0 002.5 2.5v0a2.5 2.5 0 002.5-2.5V6z" />
                </svg>
              </div>
            </div>
          </div>

          <div className="bg-white p-6 rounded-lg shadow">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Interview Rate</p>
                <p className="text-2xl font-bold text-gray-900">{metrics.conversionRates.interviewRate.toFixed(1)}%</p>
                <p className="text-xs text-gray-500">Screening to interview</p>
              </div>
              <div className="p-3 bg-yellow-100 rounded-full">
                <svg className="w-6 h-6 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                </svg>
              </div>
            </div>
          </div>

          <div className="bg-white p-6 rounded-lg shadow">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Hire Rate</p>
                <p className="text-2xl font-bold text-gray-900">{metrics.conversionRates.hireRate.toFixed(1)}%</p>
                <p className="text-xs text-gray-500">Application to hire</p>
              </div>
              <div className="p-3 bg-purple-100 rounded-full">
                <svg className="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
            </div>
          </div>
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Pipeline Funnel */}
        {pipelineFunnel && (
          <div className="bg-white p-6 rounded-lg shadow">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Recruitment Pipeline</h3>
            <p className="text-sm text-gray-600 mb-6">{pipelineFunnel.department} - {pipelineFunnel.period}</p>
            <div className="space-y-4">
              {Object.entries(pipelineFunnel.funnel).map(([stage, count]) => {
                const percentage = Object.values(pipelineFunnel.funnel)[0] > 0 
                  ? (count / Object.values(pipelineFunnel.funnel)[0]) * 100 
                  : 0;
                return (
                  <div key={stage} className="flex items-center justify-between">
                    <div className="flex-1">
                      <div className="flex justify-between mb-1">
                        <span className="text-sm font-medium text-gray-700">{stage}</span>
                        <span className="text-sm text-gray-600">{count} ({percentage.toFixed(1)}%)</span>
                      </div>
                      <div className="w-full bg-gray-200 rounded-full h-2">
                        <div 
                          className="bg-blue-600 h-2 rounded-full transition-all duration-300" 
                          style={{ width: `${percentage}%` }}
                        ></div>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        )}

        {/* Applications per Vacancy */}
        <div className="bg-white p-6 rounded-lg shadow">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Applications per Vacancy</h3>
          <p className="text-sm text-gray-600 mb-6">Job postings with highest application volume</p>
          <div className="space-y-3">
            {applicationsPerVacancy.slice(0, 6).map((vacancy, index) => {
              const maxApplications = Math.max(...applicationsPerVacancy.map(v => v.applications));
              const percentage = maxApplications > 0 ? (vacancy.applications / maxApplications) * 100 : 0;
              return (
                <div key={vacancy.jobId} className="flex items-center justify-between">
                  <div className="flex-1 mr-4">
                    <div className="flex justify-between mb-1">
                      <span className="text-sm font-medium text-gray-700 truncate">{vacancy.vacancy}</span>
                      <span className="text-sm text-gray-600 ml-2">{vacancy.applications}</span>
                    </div>
                    <div className="w-full bg-gray-200 rounded-full h-2">
                      <div 
                        className="bg-green-600 h-2 rounded-full transition-all duration-300" 
                        style={{ width: `${percentage}%` }}
                      ></div>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Time to Fill */}
        {timeToFill && (
          <div className="bg-white p-6 rounded-lg shadow">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Time to Fill</h3>
            <p className="text-sm text-gray-600 mb-6">
              Average: {timeToFill.averageDays.toFixed(1)} days ({timeToFill.department})
            </p>
            <div className="space-y-4">
              {timeToFill.positions.slice(0, 5).map((position, index) => (
                <div key={index} className="flex justify-between items-center border-b border-gray-200 pb-3">
                  <div className="flex-1">
                    <p className="font-medium text-gray-900">{position.jobTitle}</p>
                    <p className="text-sm text-gray-600">{position.department}</p>
                  </div>
                  <div className="text-right">
                    <p className="font-semibold text-gray-900">{position.daysToFill} days</p>
                    <p className="text-xs text-gray-500">{new Date(position.hiredDate).toLocaleDateString()}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Recent Activity */}
        <div className="bg-white p-6 rounded-lg shadow">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Recent Activity</h3>
          <p className="text-sm text-gray-600 mb-6">Latest recruitment activities</p>
          <div className="space-y-4">
            {recentActivity.map((activity) => (
              <div key={activity.id} className="flex justify-between items-center border-b border-gray-200 pb-3">
                <div className="flex-1">
                  <p className="font-medium text-gray-900">{activity.applicantName}</p>
                  <p className="text-sm text-gray-600">{activity.jobTitle}</p>
                </div>
                <div className="text-right">
                  <span className={`inline-block px-2 py-1 rounded-full text-xs font-medium ${getStatusBadgeColor(activity.status)}`}>
                    {activity.status.replace('_', ' ')}
                  </span>
                  <p className="text-xs text-gray-500 mt-1">
                    {new Date(activity.timestamp).toLocaleDateString()}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Department Statistics */}
      {departmentStats && (
        <div className="bg-white p-6 rounded-lg shadow">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Department Performance</h3>
          <p className="text-sm text-gray-600 mb-6">Statistics by department for {departmentStats.period}</p>
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-gray-200">
                  <th className="text-left py-3 px-4 font-semibold text-gray-900">Department</th>
                  <th className="text-right py-3 px-4 font-semibold text-gray-900">Applications</th>
                  <th className="text-right py-3 px-4 font-semibold text-gray-900">Applicants</th>
                  <th className="text-right py-3 px-4 font-semibold text-gray-900">Hired</th>
                  <th className="text-right py-3 px-4 font-semibold text-gray-900">Avg. Time to Fill</th>
                </tr>
              </thead>
              <tbody>
                {Object.entries(departmentStats.departments).map(([dept, stats]) => (
                  <tr key={dept} className="border-b border-gray-100 hover:bg-gray-50">
                    <td className="py-3 px-4 text-gray-900">{dept}</td>
                    <td className="text-right py-3 px-4 text-gray-900">{stats.totalApplications}</td>
                    <td className="text-right py-3 px-4 text-gray-900">{stats.uniqueApplicants}</td>
                    <td className="text-right py-3 px-4 text-gray-900">{stats.hired}</td>
                    <td className="text-right py-3 px-4 text-gray-900">{stats.averageTimeToFill.toFixed(1)} days</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};

export default RecruiterDashboard;
