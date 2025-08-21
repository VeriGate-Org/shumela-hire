import { UserRole } from '../contexts/AuthContext';

export interface RoleConfig {
  primaryColor: string;
  secondaryColor: string;
  logo: string;
  welcomeMessage: string;
  description: string;
  navigationItems: Array<{
    label: string;
    href: string;
    icon: string;
  }>;
}

export const roleConfigurations: Record<UserRole, RoleConfig> = {
  Admin: {
    primaryColor: 'bg-red-600',
    secondaryColor: 'bg-red-50',
    logo: '👑',
    welcomeMessage: 'System Administration Dashboard',
    description: 'Manage users, system settings, and oversee all recruitment activities.',
    navigationItems: [
      { label: 'Dashboard', href: '/dashboard', icon: '🏠' },
      { label: 'Applications', href: '/applications', icon: '�' },
      { label: 'Interviews', href: '/interviews', icon: '�' },
      { label: 'Analytics', href: '/analytics', icon: '📊' },
      { label: 'Integrations', href: '/integrations', icon: '�' },
      { label: 'Training', href: '/training', icon: '🎓' },
      { label: 'Role Permissions', href: '/admin/permissions', icon: '�' },
      { label: 'Audit Logs', href: '/admin/audit-logs', icon: '📋' },
      { label: 'Workflow Management', href: '/workflow', icon: '🔄' },
      { label: 'Internal Jobs', href: '/internal/jobs', icon: '�' },
    ],
  },
  HR: {
    primaryColor: 'bg-blue-600',
    secondaryColor: 'bg-blue-50',
    logo: '👔',
    welcomeMessage: 'Human Resources Dashboard',
    description: 'Manage employee lifecycle, policies, and recruitment coordination.',
    navigationItems: [
      { label: 'Recruiter Analytics', href: '/recruiter-dashboard', icon: '📊' },
      { label: 'Application Management', href: '/applications/manage', icon: '⚙️' },
      { label: 'Workflow Management', href: '/workflow', icon: '🔄' },
      { label: 'Internal Jobs', href: '/internal/jobs', icon: '💼' },
      { label: 'Job Postings', href: '/job-postings', icon: '📄' },
      { label: 'Applications', href: '/applications', icon: '📋' },
      { label: 'Applicants', href: '/applicants', icon: '👥' },
      { label: 'Interviews', href: '/interviews', icon: '📅' },
      { label: 'Pipeline', href: '/pipeline', icon: '🔄' },
      { label: 'Offers', href: '/offers', icon: '💰' },
      { label: 'Analytics', href: '/analytics', icon: '📊' },
      { label: 'Employee Records', href: '/hr/employees', icon: '👤' },
      { label: 'Recruitment Overview', href: '/hr/recruitment', icon: '🎯' },
      { label: 'Onboarding', href: '/hr/onboarding', icon: '🚀' },
      { label: 'Policies & Compliance', href: '/hr/policies', icon: '📜' },
      { label: 'Performance Reviews', href: '/hr/performance', icon: '⭐' },
    ],
  },
  'Hiring Manager': {
    primaryColor: 'bg-green-600',
    secondaryColor: 'bg-green-50',
    logo: '🎯',
    welcomeMessage: 'Hiring Manager Dashboard',
    description: 'Oversee hiring for your team and manage interview processes.',
    navigationItems: [
      { label: 'Recruiter Analytics', href: '/recruiter-dashboard', icon: '📊' },
      { label: 'Application Management', href: '/applications/manage', icon: '⚙️' },
      { label: 'Workflow Management', href: '/workflow', icon: '🔄' },
      { label: 'Internal Jobs', href: '/internal/jobs', icon: '💼' },
      { label: 'Job Postings', href: '/job-postings', icon: '📄' },
      { label: 'My Job Postings', href: '/hiring/jobs', icon: '📝' },
      { label: 'Candidate Pipeline', href: '/pipeline', icon: '🔄' },
      { label: 'Interview Schedule', href: '/interviews', icon: '📅' },
      { label: 'Offers', href: '/offers', icon: '💰' },
      { label: 'Analytics', href: '/analytics', icon: '📊' },
      { label: 'Team Requests', href: '/hiring/requests', icon: '📋' },
      { label: 'Hiring Analytics', href: '/hiring/analytics', icon: '📈' },
    ],
  },
  Recruiter: {
    primaryColor: 'bg-purple-600',
    secondaryColor: 'bg-purple-50',
    logo: '🔍',
    welcomeMessage: 'Recruiter Dashboard',
    description: 'Source, screen, and manage candidates throughout the hiring process.',
    navigationItems: [
      { label: 'Dashboard Analytics', href: '/recruiter-dashboard', icon: '📊' },
      { label: 'Application Management', href: '/applications/manage', icon: '⚙️' },
      { label: 'Workflow Management', href: '/workflow', icon: '🔄' },
      { label: 'Internal Jobs', href: '/internal/jobs', icon: '💼' },
      { label: 'Applications', href: '/applications', icon: '📋' },
      { label: 'Applicants', href: '/applicants', icon: '👥' },
      { label: 'Active Jobs', href: '/recruiter/jobs', icon: '💼' },
      { label: 'Candidate Database', href: '/recruiter/candidates', icon: '👥' },
      { label: 'Sourcing Tools', href: '/recruiter/sourcing', icon: '🔍' },
      { label: 'Interview Coordination', href: '/interviews', icon: '📅' },
      { label: 'Pipeline Management', href: '/pipeline', icon: '🔄' },
      { label: 'Offers', href: '/offers', icon: '💰' },
      { label: 'Analytics', href: '/analytics', icon: '📊' },
    ],
  },
  Applicant: {
    primaryColor: 'bg-orange-600',
    secondaryColor: 'bg-orange-50',
    logo: '👤',
    welcomeMessage: 'Applicant Portal',
    description: 'Track your applications and manage your job search journey.',
    navigationItems: [
      { label: 'Browse Jobs', href: '/candidate/jobs', icon: '🔍' },
      { label: 'My Applications', href: '/candidate/applications', icon: '📄' },
      { label: 'My Profile', href: '/candidate/profile', icon: '👤' },
      { label: 'Interview Schedule', href: '/candidate/interviews', icon: '📅' },
      { label: 'My Offers', href: '/candidate/offers', icon: '💰' },
      { label: 'Internal Jobs', href: '/internal/jobs', icon: '💼' },
      { label: 'Messages', href: '/applicant/messages', icon: '💬' },
    ],
  },
  Executive: {
    primaryColor: 'bg-indigo-600',
    secondaryColor: 'bg-indigo-50',
    logo: '🏛️',
    welcomeMessage: 'Executive Dashboard',
    description: 'Strategic oversight of organizational hiring and high-level approvals.',
    navigationItems: [
      { label: 'Workflow Management', href: '/workflow', icon: '🔄' },
      { label: 'Internal Jobs', href: '/internal/jobs', icon: '💼' },
      { label: 'Strategic Planning', href: '/executive/planning', icon: '🎯' },
      { label: 'Budget & Approvals', href: '/executive/budget', icon: '💰' },
      { label: 'Executive Reports', href: '/executive/reports', icon: '📊' },
      { label: 'Analytics Dashboard', href: '/analytics', icon: '📈' },
      { label: 'High-Value Offers', href: '/offers', icon: '💰' },
      { label: 'Organizational Overview', href: '/executive/overview', icon: '🏢' },
      { label: 'Leadership Team', href: '/executive/leadership', icon: '👥' },
    ],
  },
};