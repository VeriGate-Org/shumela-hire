import { UserRole } from '@/contexts/AuthContext';

// ---------------------------------------------------------------------------
// Types
// ---------------------------------------------------------------------------

export interface GettingStartedStep {
  step: number;
  title: string;
  description: string;
  href?: string;
}

export interface GettingStartedGuide {
  roleCategory: string;
  roles: UserRole[];
  steps: GettingStartedStep[];
}

export interface FeatureGuide {
  name: string;
  description: string;
  href: string;
  requiredPermission: string;
}

export interface FAQItem {
  category: string;
  question: string;
  answer: string;
  relevantRoles?: UserRole[];
}

export interface TroubleshootingItem {
  problem: string;
  symptoms: string;
  resolution: string;
}

export interface ReleaseNote {
  version: string;
  date: string;
  highlights: string[];
}

export interface KeyboardShortcutItem {
  keys: string;
  description: string;
}

// ---------------------------------------------------------------------------
// Getting Started — per-role onboarding
// ---------------------------------------------------------------------------

export const gettingStartedByRole: GettingStartedGuide[] = [
  {
    roleCategory: 'Recruiter / HR Manager',
    roles: ['RECRUITER', 'HR_MANAGER'],
    steps: [
      { step: 1, title: 'Review the Dashboard', description: 'The Dashboard provides an at-a-glance view of open requisitions, pipeline health, and upcoming interviews. Use it as your daily starting point.', href: '/dashboard' },
      { step: 2, title: 'Create a Job Posting', description: 'Navigate to Job Postings and select "New Job". Fill in the role details, requirements, and compensation range. Once published, the posting is visible to candidates.', href: '/job-postings' },
      { step: 3, title: 'Configure Pipeline Stages', description: 'Open Pipeline Management to define the stages a candidate moves through — screening, technical assessment, interview, offer, and so on. Stages can be reordered at any time.', href: '/pipeline' },
      { step: 4, title: 'Review Applications', description: 'Incoming applications appear in the Applications view. Use filters to sort by date, status, or AI screening score. Click into any application to see the full candidate profile.', href: '/applications' },
      { step: 5, title: 'Schedule Interviews', description: 'From an application detail page, schedule interviews directly. Select panellists, choose a time slot, and the system sends calendar invitations automatically.', href: '/interviews' },
      { step: 6, title: 'Generate Offers', description: 'When a candidate is approved, create an offer with the recommended salary band. The offer workflow routes through the required approvals before the candidate receives it.', href: '/offers' },
    ],
  },
  {
    roleCategory: 'Applicant',
    roles: ['APPLICANT'],
    steps: [
      { step: 1, title: 'Browse Open Positions', description: 'Visit Browse Jobs to explore all currently open positions. Use keyword and location filters to find roles that match your skills and interests.', href: '/candidate/jobs' },
      { step: 2, title: 'Submit an Application', description: 'Click "Apply" on any listing to upload your CV and cover letter. Complete the screening questionnaire if one is attached to the role.', href: '/candidate/jobs' },
      { step: 3, title: 'Track Your Status', description: 'My Applications shows the real-time status of every submission — screening, shortlisted, interview scheduled, or offer extended.', href: '/candidate/applications' },
      { step: 4, title: 'Prepare for Interviews', description: 'When an interview is scheduled, you will receive a calendar invitation with details. Check Interview Schedule for date, time, panel, and any preparation materials.', href: '/candidate/interviews' },
      { step: 5, title: 'Review Offers', description: 'Offers appear under My Offers. Review the compensation package, benefits, and start date. You can accept, decline, or request a discussion directly from the offer page.', href: '/candidate/offers' },
      { step: 6, title: 'Explore Internal Opportunities', description: 'If you are already employed within the organisation, Internal Jobs lists roles available for internal transfers or promotions.', href: '/internal/jobs' },
    ],
  },
  {
    roleCategory: 'Admin / Platform Owner',
    roles: ['ADMIN', 'PLATFORM_OWNER'],
    steps: [
      { step: 1, title: 'Configure Permissions', description: 'Open Role Permissions to assign granular access rights to each role. Changes take effect immediately across all active sessions.', href: '/admin/permissions' },
      { step: 2, title: 'Set Up Integrations', description: 'Connect third-party services such as calendar providers, background check APIs, and HRIS systems via the Integrations panel.', href: '/integrations' },
      { step: 3, title: 'Review Audit Logs', description: 'The Audit Logs section provides a full chronological record of every action taken across the platform — useful for compliance and troubleshooting.', href: '/admin/audit-logs' },
      { step: 4, title: 'Manage Workflows', description: 'Define and customise approval workflows for offers, requisitions, and other business processes in Workflow Management.', href: '/workflow' },
      { step: 5, title: 'Monitor Analytics', description: 'Use the Analytics dashboard to track hiring velocity, diversity metrics, and cost-per-hire. Export reports for stakeholder reviews.', href: '/analytics' },
    ],
  },
  {
    roleCategory: 'Hiring Manager',
    roles: ['HIRING_MANAGER'],
    steps: [
      { step: 1, title: 'Review Requisitions', description: 'Your open requisitions are listed on the Dashboard. Click into any requisition to see candidate progress through the pipeline.', href: '/dashboard' },
      { step: 2, title: 'Evaluate Candidates', description: 'Open Applications to review shortlisted candidates. Each profile includes the CV, screening score, and recruiter notes.', href: '/applications' },
      { step: 3, title: 'Provide Interview Feedback', description: 'After conducting interviews, submit structured feedback via the Interviews section. Your ratings feed into the overall candidate score.', href: '/interviews' },
      { step: 4, title: 'Approve Offers', description: 'Once a candidate is selected, the offer enters the approval workflow. Review and approve from the Offers page.', href: '/offers' },
    ],
  },
  {
    roleCategory: 'Interviewer',
    roles: ['INTERVIEWER'],
    steps: [
      { step: 1, title: 'View Your Schedule', description: 'The Interviews page shows all panels you have been assigned to. Calendar invitations are also sent to your email.', href: '/interviews' },
      { step: 2, title: 'Submit Feedback', description: 'After each interview, complete the structured feedback form. Rate the candidate on the defined criteria and add any notes for the hiring panel.', href: '/interviews' },
      { step: 3, title: 'Review Candidate Materials', description: 'Before the interview, open the candidate profile to review their CV, cover letter, and any earlier-stage feedback from recruiters.', href: '/interviews' },
    ],
  },
  {
    roleCategory: 'Executive',
    roles: ['EXECUTIVE'],
    steps: [
      { step: 1, title: 'Access Executive Reports', description: 'The Reports section includes executive summaries covering headcount plans, pipeline conversion rates, and offer-acceptance metrics.', href: '/reports' },
      { step: 2, title: 'Review Analytics', description: 'The Analytics dashboard provides real-time visual breakdowns of hiring activity, diversity metrics, and departmental demand.', href: '/analytics' },
      { step: 3, title: 'Monitor Offers', description: 'Review pending and approved offers across all departments. Filter by seniority, department, or compensation band.', href: '/offers' },
    ],
  },
  {
    roleCategory: 'Employee',
    roles: ['EMPLOYEE'],
    steps: [
      { step: 1, title: 'Browse Internal Jobs', description: 'Internal Jobs lists open positions available for internal transfer or promotion within the organisation.', href: '/internal/jobs' },
      { step: 2, title: 'Complete Training', description: 'The Training section provides onboarding modules and upskilling resources assigned to your role.', href: '/training' },
      { step: 3, title: 'Update Your Profile', description: 'Keep your profile current with your latest skills, certifications, and contact details.', href: '/candidate/profile' },
    ],
  },
];

// ---------------------------------------------------------------------------
// Feature Guides
// ---------------------------------------------------------------------------

export const featureGuides: FeatureGuide[] = [
  { name: 'Dashboard', description: 'Central overview of recruitment activity, open requisitions, and key metrics.', href: '/dashboard', requiredPermission: 'view_dashboard' },
  { name: 'Job Postings', description: 'Create, edit, and manage job listings. Control visibility and application deadlines.', href: '/job-postings', requiredPermission: 'manage_jobs' },
  { name: 'Applications', description: 'Review, filter, and manage candidate applications with AI-powered screening.', href: '/applications', requiredPermission: 'view_applications' },
  { name: 'Applicants', description: 'View and manage candidate profiles across all job postings.', href: '/applicants', requiredPermission: 'view_applicants' },
  { name: 'Pipeline', description: 'Visual pipeline board to track candidates through hiring stages.', href: '/pipeline', requiredPermission: 'manage_pipeline' },
  { name: 'Interviews', description: 'Schedule interviews, assign panellists, and collect structured feedback.', href: '/interviews', requiredPermission: 'view_interviews' },
  { name: 'Offers', description: 'Generate, approve, and track compensation offers through the approval workflow.', href: '/offers', requiredPermission: 'manage_offers' },
  { name: 'Salary Recommendations', description: 'AI-driven salary band recommendations based on market data and internal equity.', href: '/salary-recommendations', requiredPermission: 'view_salary_data' },
  { name: 'Workflow Management', description: 'Define and customise multi-step approval workflows for recruitment processes.', href: '/workflow', requiredPermission: 'manage_workflow' },
  { name: 'Analytics', description: 'Interactive dashboards for hiring velocity, diversity, and cost-per-hire metrics.', href: '/analytics', requiredPermission: 'view_analytics' },
  { name: 'Recruiter Analytics', description: 'Individual recruiter performance metrics and productivity tracking.', href: '/recruiter-dashboard', requiredPermission: 'view_recruiter_analytics' },
  { name: 'Reports', description: 'Generate and export structured reports for compliance and stakeholder reviews.', href: '/reports', requiredPermission: 'view_reports' },
  { name: 'Role Permissions', description: 'Configure granular role-based access controls across the platform.', href: '/admin/permissions', requiredPermission: 'manage_permissions' },
  { name: 'Audit Logs', description: 'Full chronological record of user actions for compliance and auditing.', href: '/admin/audit-logs', requiredPermission: 'view_audit_logs' },
  { name: 'Integrations', description: 'Connect third-party services including calendars, HRIS, and background checks.', href: '/integrations', requiredPermission: 'manage_integrations' },
  { name: 'Internal Jobs', description: 'Browse and apply for internal transfer and promotion opportunities.', href: '/internal/jobs', requiredPermission: 'view_internal_jobs' },
  { name: 'Browse Jobs', description: 'Search and filter open positions by keyword, location, and department.', href: '/candidate/jobs', requiredPermission: 'browse_jobs' },
  { name: 'My Applications', description: 'Track the status of your submitted applications in real time.', href: '/candidate/applications', requiredPermission: 'manage_own_applications' },
  { name: 'Training', description: 'Access onboarding modules and professional development resources.', href: '/training', requiredPermission: 'view_training' },
];

// ---------------------------------------------------------------------------
// FAQ
// ---------------------------------------------------------------------------

export const faqItems: FAQItem[] = [
  // General
  { category: 'General', question: 'How do I reset my password?', answer: 'Click your profile avatar in the top-right corner, then select Settings > Security. If you are locked out, use the "Forgot password" link on the login screen to receive a reset email.' },
  { category: 'General', question: 'How do I change my notification preferences?', answer: 'Navigate to Settings > Notifications. Toggle individual email and push notification channels on or off. Changes save automatically.' },
  { category: 'General', question: 'Is there a mobile app?', answer: 'ShumelaHire is a responsive web application. It works on any modern mobile browser without requiring a separate app download.' },
  { category: 'General', question: 'Does ShumelaHire support dark mode?', answer: 'Not at this time. The interface uses a light theme designed for extended desktop use. Dark mode is on the product roadmap.' },
  { category: 'General', question: 'Which browsers are supported?', answer: 'ShumelaHire supports the latest versions of Chrome, Firefox, Safari, and Edge. Internet Explorer is not supported.' },

  // Recruitment
  { category: 'Recruitment', question: 'How does AI CV screening work?', answer: 'When a candidate submits an application, the system automatically scores the CV against the job requirements. The score appears on the application card. Recruiters can still override the AI assessment at any time.', relevantRoles: ['RECRUITER', 'HR_MANAGER', 'ADMIN'] },
  { category: 'Recruitment', question: 'Can I create custom pipeline stages?', answer: 'Yes. Navigate to Pipeline Management and use the "Add Stage" button. Stages can be renamed, reordered, or removed. Changes apply to all future applications for that pipeline.', relevantRoles: ['RECRUITER', 'HR_MANAGER', 'ADMIN'] },
  { category: 'Recruitment', question: 'How do I schedule a panel interview?', answer: 'Open the application detail page and click "Schedule Interview". Add multiple panellists, select a time slot, and the system sends calendar invitations to all participants.', relevantRoles: ['RECRUITER', 'HR_MANAGER', 'HIRING_MANAGER'] },
  { category: 'Recruitment', question: 'How do I reject a candidate?', answer: 'From the application detail page, change the status to "Rejected". You can optionally include a reason. The candidate receives a notification if email notifications are enabled for the posting.', relevantRoles: ['RECRUITER', 'HR_MANAGER', 'HIRING_MANAGER'] },
  { category: 'Recruitment', question: 'Where are salary recommendations sourced from?', answer: 'Salary recommendations use a combination of internal compensation data and configurable market benchmarks. Administrators can update the benchmark dataset in the Integrations panel.', relevantRoles: ['RECRUITER', 'HR_MANAGER', 'ADMIN'] },
  { category: 'Recruitment', question: 'Can I reopen a closed job posting?', answer: 'Yes. Navigate to the closed posting and select "Reopen". This restores the listing and re-activates the pipeline. Previously received applications are preserved.', relevantRoles: ['RECRUITER', 'HR_MANAGER'] },

  // Applicant
  { category: 'Applicant', question: 'How do I know my application was received?', answer: 'After submitting, you will see a confirmation message on screen. Your application also appears immediately under My Applications with a "Submitted" status.', relevantRoles: ['APPLICANT'] },
  { category: 'Applicant', question: 'Can I edit my application after submission?', answer: 'You cannot edit a submitted application directly. If you need to update information, contact the recruiter listed on the job posting to request a revision.', relevantRoles: ['APPLICANT'] },
  { category: 'Applicant', question: 'How long does the review process take?', answer: 'Review timelines vary by organisation and role. Your application status updates in real time under My Applications. You will receive an email notification when the status changes.', relevantRoles: ['APPLICANT'] },
  { category: 'Applicant', question: 'Can I reapply for a position I was rejected from?', answer: 'Reapplication policies are set by the organisation. If the position is still open and accepts new applications, you may submit a new application.', relevantRoles: ['APPLICANT'] },

  // Administration
  { category: 'Administration', question: 'How do I add new users to the platform?', answer: 'Navigate to Role Permissions under Administration. New users are created through your identity provider or by an administrator via the user management interface.', relevantRoles: ['ADMIN', 'PLATFORM_OWNER'] },
  { category: 'Administration', question: 'How do I configure integrations?', answer: 'Open the Integrations page under System. Each integration provides setup instructions and requires an API key or OAuth connection. Test the connection before enabling it in production.', relevantRoles: ['ADMIN', 'PLATFORM_OWNER'] },
  { category: 'Administration', question: 'How do I view system activity?', answer: 'Audit Logs provide a chronological record of all actions. Filter by user, action type, or date range. Logs can be exported for compliance reporting.', relevantRoles: ['ADMIN', 'PLATFORM_OWNER'] },
  { category: 'Administration', question: 'Does ShumelaHire support multi-tenancy?', answer: 'Yes. Platform Owners can manage multiple tenants, each with isolated data, configurations, and user pools. Tenant management is available under Platform > Tenants.', relevantRoles: ['PLATFORM_OWNER'] },
];

// ---------------------------------------------------------------------------
// Troubleshooting
// ---------------------------------------------------------------------------

export const troubleshootingItems: TroubleshootingItem[] = [
  { problem: 'Unable to log in', symptoms: 'Login page returns "Invalid credentials" or the page does not respond after clicking Sign In.', resolution: 'Verify your email and password are correct. If using SSO, confirm your identity provider session is active. Clear browser cookies and try again. If the issue persists, use the "Forgot password" link or contact your administrator.' },
  { problem: 'Blank screen after login', symptoms: 'The page loads but shows a white screen with no content or a spinner that never completes.', resolution: 'Hard-refresh the page (Ctrl+Shift+R or Cmd+Shift+R). Disable browser extensions that may block scripts. Check your network connection. If the problem continues, clear the browser cache entirely.' },
  { problem: 'Missing navigation items', symptoms: 'Expected sidebar links such as Job Postings or Analytics are not visible.', resolution: 'Navigation items are controlled by your role permissions. Contact your administrator to verify your role has the necessary permissions assigned. If permissions were recently changed, sign out and sign back in.' },
  { problem: 'Calendar invitations not received', symptoms: 'An interview is scheduled but the panellist or candidate did not receive a calendar invitation.', resolution: 'Check the spam or junk folder. Verify the email address on the user profile is correct. Confirm the calendar integration is connected and active under Integrations.' },
  { problem: 'CV upload fails', symptoms: 'The file upload spinner runs indefinitely or displays an error message.', resolution: 'Ensure the file is a PDF, DOC, or DOCX under 10 MB. If the file meets those criteria, try a different browser. Contact support if the issue persists across browsers.' },
  { problem: 'Unexpected analytics data', symptoms: 'Dashboard numbers or charts show values that seem incorrect or out of date.', resolution: 'Analytics data refreshes periodically. Click the refresh icon on the dashboard if available. If data is significantly wrong, verify the date range filter. Report persistent discrepancies to your administrator.' },
  { problem: 'Bulk action fails', symptoms: 'A bulk operation such as rejecting multiple applications or exporting data stops partway through.', resolution: 'Reduce the batch size and retry. Bulk operations time out after processing a large number of records. If the problem persists, perform the action in smaller batches or contact support.' },
];

// ---------------------------------------------------------------------------
// Release Notes
// ---------------------------------------------------------------------------

export const releaseNotes: ReleaseNote[] = [
  {
    version: 'v2.1.0',
    date: 'February 2026',
    highlights: [
      'Help Center with role-aware onboarding guides, FAQ, and troubleshooting',
      'Platform Owner role with multi-tenant feature entitlement management',
      'Keyboard shortcuts overlay accessible via the "?" key',
      'Improved Flyway migration versioning for smoother upgrades',
    ],
  },
  {
    version: 'v2.0.0',
    date: 'January 2026',
    highlights: [
      'Complete UI redesign following The Architect brand system',
      'AI-powered CV screening and salary recommendations',
      'Configurable multi-step approval workflows',
      'Role-based navigation with granular permission controls',
      'Audit log with full-text search and CSV export',
    ],
  },
  {
    version: 'v1.5.0',
    date: 'December 2025',
    highlights: [
      'Internal job board for employee transfers and promotions',
      'Recruiter analytics dashboard with individual performance metrics',
      'Calendar integration for automated interview scheduling',
      'Notification preferences with email and push channels',
    ],
  },
];

// ---------------------------------------------------------------------------
// Additional Keyboard Shortcuts (beyond useKeyboardShortcuts)
// ---------------------------------------------------------------------------

export const additionalShortcuts: KeyboardShortcutItem[] = [
  { keys: 'Esc', description: 'Close modals and overlays' },
];
