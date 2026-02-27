/**
 * English (South Africa) translations
 * Locale: en-ZA
 */
const enZA = {
  locale: {
    code: 'en-ZA',
    name: 'English',
    nativeName: 'English',
    direction: 'ltr' as const,
  },

  // Navigation
  nav: {
    dashboard: 'Dashboard',
    jobs: 'Jobs',
    applications: 'Applications',
    candidates: 'Candidates',
    interviews: 'Interviews',
    offers: 'Offers',
    onboarding: 'Onboarding',
    employees: 'Employees',
    reports: 'Reports',
    analytics: 'Analytics',
    settings: 'Settings',
    profile: 'Profile',
    logout: 'Sign Out',
    recruiterDashboard: 'Recruiter Dashboard',
    performance: 'Performance',
    training: 'Training',
    orgChart: 'Org Chart',
    workflow: 'Workflow',
    support: 'Support',
  },

  // Common actions
  actions: {
    save: 'Save',
    cancel: 'Cancel',
    delete: 'Delete',
    edit: 'Edit',
    create: 'Create',
    update: 'Update',
    submit: 'Submit',
    confirm: 'Confirm',
    close: 'Close',
    back: 'Back',
    next: 'Next',
    search: 'Search',
    filter: 'Filter',
    export: 'Export',
    import: 'Import',
    download: 'Download',
    upload: 'Upload',
    view: 'View',
    add: 'Add',
    remove: 'Remove',
    apply: 'Apply',
    clear: 'Clear',
    refresh: 'Refresh',
    loading: 'Loading…',
    saving: 'Saving…',
    learnMore: 'Learn more',
  },

  // Status labels
  status: {
    active: 'Active',
    inactive: 'Inactive',
    pending: 'Pending',
    approved: 'Approved',
    rejected: 'Rejected',
    draft: 'Draft',
    published: 'Published',
    closed: 'Closed',
    completed: 'Completed',
    inProgress: 'In Progress',
    scheduled: 'Scheduled',
    cancelled: 'Cancelled',
  },

  // Authentication
  auth: {
    signIn: 'Sign In',
    signOut: 'Sign Out',
    signUp: 'Sign Up',
    email: 'Email address',
    password: 'Password',
    forgotPassword: 'Forgot password?',
    rememberMe: 'Remember me',
    welcome: 'Welcome back',
    continueWith: 'Continue with',
    noAccount: 'Don\'t have an account?',
    haveAccount: 'Already have an account?',
  },

  // Settings page
  settings: {
    title: 'Settings',
    notifications: 'Notifications',
    privacy: 'Privacy',
    security: 'Security',
    dataManagement: 'Data Management',
    language: 'Language',
    languageDescription: 'Choose your preferred display language.',
    theme: 'Theme',
    themeLight: 'Light',
    themeDark: 'Dark',
    themeSystem: 'System',
    highContrast: 'High Contrast',
    accessibilitySettings: 'Accessibility Settings',
    profileSettings: 'Profile Settings',
    changePassword: 'Change Password',
    twoFactor: 'Two-Factor Authentication',
    sessionManagement: 'Session Management',
  },

  // Jobs & Recruitment
  jobs: {
    title: 'Job Postings',
    createJob: 'Create Job Posting',
    jobTitle: 'Job Title',
    department: 'Department',
    location: 'Location',
    salary: 'Salary',
    salaryRange: 'Salary Range',
    jobType: 'Job Type',
    fullTime: 'Full-time',
    partTime: 'Part-time',
    contract: 'Contract',
    remote: 'Remote',
    hybrid: 'Hybrid',
    description: 'Job Description',
    requirements: 'Requirements',
    responsibilities: 'Responsibilities',
    benefits: 'Benefits',
    closingDate: 'Closing Date',
    postJob: 'Post Job',
    noJobs: 'No job postings found.',
    searchJobs: 'Search job postings…',
  },

  // Applications
  applications: {
    title: 'Applications',
    applyNow: 'Apply Now',
    applicant: 'Applicant',
    applied: 'Applied',
    appliedOn: 'Applied on',
    status: 'Status',
    resume: 'Resume',
    coverLetter: 'Cover Letter',
    noApplications: 'No applications found.',
    reviewApplication: 'Review Application',
    shortlist: 'Shortlist',
    reject: 'Reject',
    schedule: 'Schedule Interview',
    sendOffer: 'Send Offer',
  },

  // Employees
  employees: {
    title: 'Employees',
    addEmployee: 'Add Employee',
    employeeId: 'Employee ID',
    name: 'Full Name',
    firstName: 'First Name',
    lastName: 'Last Name',
    email: 'Email',
    phone: 'Phone Number',
    department: 'Department',
    position: 'Position',
    startDate: 'Start Date',
    manager: 'Manager',
    employmentType: 'Employment Type',
    noEmployees: 'No employees found.',
    searchEmployees: 'Search employees…',
  },

  // Dashboard
  dashboard: {
    title: 'Dashboard',
    overview: 'Overview',
    recentActivity: 'Recent Activity',
    quickStats: 'Quick Stats',
    openPositions: 'Open Positions',
    activeApplications: 'Active Applications',
    scheduledInterviews: 'Scheduled Interviews',
    pendingOffers: 'Pending Offers',
    newCandidates: 'New Candidates',
    timeToHire: 'Time to Hire',
    welcomeBack: 'Welcome back',
    todaySchedule: 'Today\'s Schedule',
  },

  // Errors & Feedback
  errors: {
    generic: 'Something went wrong. Please try again.',
    notFound: 'Page not found.',
    unauthorized: 'You are not authorised to view this page.',
    forbidden: 'Access denied.',
    serverError: 'Server error. Please contact support.',
    networkError: 'Network error. Please check your connection.',
    required: 'This field is required.',
    invalidEmail: 'Please enter a valid email address.',
    invalidPassword: 'Password must be at least 8 characters.',
    sessionExpired: 'Your session has expired. Please sign in again.',
  },

  // Success messages
  success: {
    saved: 'Changes saved successfully.',
    created: 'Created successfully.',
    updated: 'Updated successfully.',
    deleted: 'Deleted successfully.',
    submitted: 'Submitted successfully.',
    sent: 'Sent successfully.',
    uploaded: 'File uploaded successfully.',
  },

  // Dates & Time
  datetime: {
    today: 'Today',
    yesterday: 'Yesterday',
    thisWeek: 'This Week',
    lastWeek: 'Last Week',
    thisMonth: 'This Month',
    lastMonth: 'Last Month',
    dateFormat: 'DD/MM/YYYY',
    timeFormat: 'HH:mm',
    ago: 'ago',
    just_now: 'Just now',
  },

  // Language switcher
  languageSwitcher: {
    label: 'Language',
    en_ZA: 'English',
    zu_ZA: 'isiZulu',
    af_ZA: 'Afrikaans',
    selectLanguage: 'Select language',
  },
} as const;

export type TranslationKeys = typeof enZA;
export default enZA;
