import React from 'react'
import { render, RenderOptions } from '@testing-library/react'
import { SWRConfig } from 'swr'

// Mock SWR provider for testing
const SWRTestProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  return (
    <SWRConfig
      value={{
        dedupingInterval: 0,
        revalidateOnFocus: false,
        revalidateOnMount: true,
        revalidateOnReconnect: false,
        refreshWhenOffline: false,
        refreshWhenHidden: false,
        refreshInterval: 0,
        errorRetryCount: 0,
        provider: () => new Map(),
      }}
    >
      {children}
    </SWRConfig>
  )
}

// Custom render function with providers
const customRender = (
  ui: React.ReactElement,
  options?: Omit<RenderOptions, 'wrapper'>
) => {
  const Wrapper: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    return (
      <SWRTestProvider>
        {children}
      </SWRTestProvider>
    )
  }

  return render(ui, { wrapper: Wrapper, ...options })
}

// Mock data generators for testing
export const mockJobApplication = {
  id: 1,
  jobTitle: 'Senior Frontend Developer',
  status: 'IN_REVIEW',
  submittedAt: new Date('2025-08-17T10:00:00Z'),
  updatedAt: new Date('2025-08-17T12:00:00Z'),
  applicant: {
    id: 1,
    fullName: 'John Doe',
    email: 'john.doe@example.com',
    phone: '+1234567890',
  },
  rating: 8,
  salaryExpectation: 120000,
}

export const mockApplicant = {
  id: 1,
  fullName: 'John Doe',
  email: 'john.doe@example.com',
  phone: '+1234567890',
  location: 'San Francisco, CA',
  experience: 5,
  skills: ['React', 'TypeScript', 'Node.js'],
  resumeUrl: 'https://example.com/resume.pdf',
  linkedinUrl: 'https://linkedin.com/in/johndoe',
  portfolioUrl: 'https://johndoe.dev',
  source: 'LinkedIn',
  createdAt: new Date('2025-08-17T10:00:00Z'),
  updatedAt: new Date('2025-08-17T10:00:00Z'),
}

export const mockInterview = {
  id: 1,
  applicationId: 1,
  scheduledDate: new Date('2025-08-20T14:00:00Z'),
  duration: 60,
  interviewType: 'TECHNICAL',
  interviewer: {
    name: 'Jane Smith',
    email: 'jane.smith@company.com',
  },
  status: 'SCHEDULED',
  location: 'Conference Room A',
  meetingUrl: 'https://meet.google.com/abc-def-ghi',
  questions: ['Tell us about your experience with React', 'How do you handle state management?'],
  createdAt: new Date('2025-08-17T10:00:00Z'),
  updatedAt: new Date('2025-08-17T10:00:00Z'),
}

export const mockPerformanceMetrics = {
  lcp: 1200,
  fid: 50,
  cls: 0.05,
  fcp: 800,
  ttfb: 200,
  cacheHitRatio: 0.85,
  apiResponseTime: 150,
  memoryUsage: {
    used: 45.2,
    total: 64.0,
  },
  timestamp: new Date('2025-08-17T12:00:00Z'),
}

// Mock API responses
export const mockApiResponses = {
  applications: {
    data: [mockJobApplication],
    total: 1,
    page: 1,
    limit: 10,
  },
  applicants: {
    data: [mockApplicant],
    total: 1,
    page: 1,
    limit: 10,
  },
  interviews: {
    data: [mockInterview],
    total: 1,
    page: 1,
    limit: 10,
  },
  analytics: {
    totalApplications: 150,
    totalInterviews: 75,
    averageTimeToHire: 14,
    conversionRate: 0.25,
    topSources: [
      { source: 'LinkedIn', count: 60 },
      { source: 'Indeed', count: 45 },
      { source: 'Company Website', count: 30 },
    ],
  },
}

// Test helpers for async operations
export const waitForAsyncUpdate = () => new Promise(resolve => setTimeout(resolve, 0))

// Performance testing utilities
export const mockPerformanceObserver = {
  observe: jest.fn(),
  disconnect: jest.fn(),
  takeRecords: jest.fn(() => []),
}

export const createMockPerformanceEntry = (name: string, duration: number) => ({
  name,
  entryType: 'measure',
  startTime: Date.now(),
  duration,
  detail: null,
})

// Virtual scrolling test utilities
export const createLargeDataset = (count: number) => {
  return Array.from({ length: count }, (_, index) => ({
    id: index + 1,
    name: `Item ${index + 1}`,
    description: `Description for item ${index + 1}`,
    category: index % 3 === 0 ? 'Category A' : index % 3 === 1 ? 'Category B' : 'Category C',
    createdAt: new Date(Date.now() - index * 24 * 60 * 60 * 1000),
  }))
}

// Mock intersection observer entries
export const createMockIntersectionObserverEntry = (isIntersecting: boolean) => ({
  isIntersecting,
  intersectionRatio: isIntersecting ? 1 : 0,
  target: document.createElement('div'),
  rootBounds: { top: 0, left: 0, bottom: 600, right: 800, width: 800, height: 600 },
  boundingClientRect: { top: 100, left: 0, bottom: 200, right: 800, width: 800, height: 100 },
  intersectionRect: isIntersecting 
    ? { top: 100, left: 0, bottom: 200, right: 800, width: 800, height: 100 }
    : { top: 0, left: 0, bottom: 0, right: 0, width: 0, height: 0 },
  time: Date.now(),
})

// Export all testing utilities
export * from '@testing-library/react'
export { customRender as render }
