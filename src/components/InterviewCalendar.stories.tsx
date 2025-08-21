import type { Meta, StoryObj } from '@storybook/react';
import InterviewCalendar from './InterviewCalendar';

const meta: Meta<typeof InterviewCalendar> = {
  title: 'Components/InterviewCalendar',
  component: InterviewCalendar,
  parameters: {
    layout: 'fullscreen',
    docs: {
      description: {
        component: 'A calendar component for scheduling and managing interviews with drag-and-drop functionality.',
      },
    },
  },
  tags: ['autodocs'],
  argTypes: {
    interviews: {
      control: { type: 'object' },
      description: 'Array of interview objects to display on the calendar',
    },
    onInterviewSelect: {
      action: 'interview-selected',
      description: 'Callback when an interview is selected',
    },
    onInterviewUpdate: {
      action: 'interview-updated',
      description: 'Callback when an interview is updated',
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// Mock interview data matching the actual interface
const mockInterviews = [
  {
    id: 1,
    title: 'Technical Interview',
    type: 'technical',
    typeDisplayName: 'Technical Interview',
    round: 'first',
    roundDisplayName: 'First Round',
    status: 'scheduled',
    statusDisplayName: 'Scheduled',
    scheduledAt: '2024-08-20T10:00:00Z',
    durationMinutes: 60,
    location: 'Conference Room A',
    meetingLink: 'https://zoom.us/j/123456789',
    interviewerId: 101,
    canBeRescheduled: true,
    canBeCancelled: true,
    canBeStarted: false,
    canBeCompleted: false,
    requiresFeedback: false,
    isOverdue: false,
    isUpcoming: true,
    application: {
      id: 1,
      applicant: {
        id: 1,
        firstName: 'John',
        lastName: 'Smith',
        email: 'john.smith@email.com',
      },
      jobPosting: {
        id: 1,
        title: 'Senior Frontend Developer',
        department: 'Engineering',
      },
    },
  },
  {
    id: 2,
    title: 'Portfolio Review',
    type: 'portfolio',
    typeDisplayName: 'Portfolio Review',
    round: 'second',
    roundDisplayName: 'Second Round',
    status: 'scheduled',
    statusDisplayName: 'Scheduled',
    scheduledAt: '2024-08-20T14:00:00Z',
    durationMinutes: 90,
    location: 'Virtual',
    meetingLink: 'https://meet.google.com/abc-defg-hij',
    interviewerId: 102,
    canBeRescheduled: true,
    canBeCancelled: true,
    canBeStarted: false,
    canBeCompleted: false,
    requiresFeedback: false,
    isOverdue: false,
    isUpcoming: true,
    application: {
      id: 2,
      applicant: {
        id: 2,
        firstName: 'Emily',
        lastName: 'Davis',
        email: 'emily.davis@email.com',
      },
      jobPosting: {
        id: 2,
        title: 'UX Designer',
        department: 'Design',
      },
    },
  },
  {
    id: 3,
    title: 'System Design Interview',
    type: 'system_design',
    typeDisplayName: 'System Design',
    round: 'final',
    roundDisplayName: 'Final Round',
    status: 'scheduled',
    statusDisplayName: 'Scheduled',
    scheduledAt: '2024-08-21T09:00:00Z',
    durationMinutes: 90,
    location: 'Conference Room B',
    interviewerId: 103,
    canBeRescheduled: true,
    canBeCancelled: true,
    canBeStarted: false,
    canBeCompleted: false,
    requiresFeedback: false,
    isOverdue: false,
    isUpcoming: true,
    application: {
      id: 3,
      applicant: {
        id: 3,
        firstName: 'Alex',
        lastName: 'Rodriguez',
        email: 'alex.rodriguez@email.com',
      },
      jobPosting: {
        id: 3,
        title: 'Backend Developer',
        department: 'Engineering',
      },
    },
  },
  {
    id: 4,
    title: 'Completed Interview',
    type: 'behavioral',
    typeDisplayName: 'Behavioral Interview',
    round: 'first',
    roundDisplayName: 'First Round',
    status: 'completed',
    statusDisplayName: 'Completed',
    scheduledAt: '2024-08-19T15:00:00Z',
    durationMinutes: 45,
    location: 'HR Office',
    interviewerId: 104,
    canBeRescheduled: false,
    canBeCancelled: false,
    canBeStarted: false,
    canBeCompleted: false,
    requiresFeedback: true,
    isOverdue: false,
    isUpcoming: false,
    application: {
      id: 4,
      applicant: {
        id: 4,
        firstName: 'Sarah',
        lastName: 'Johnson',
        email: 'sarah.johnson@email.com',
      },
      jobPosting: {
        id: 4,
        title: 'Product Manager',
        department: 'Product',
      },
    },
  },
];

const lightSchedule = [
  {
    id: 1,
    title: 'Initial Screening',
    type: 'screening',
    typeDisplayName: 'Screening Call',
    round: 'first',
    roundDisplayName: 'First Round',
    status: 'scheduled',
    statusDisplayName: 'Scheduled',
    scheduledAt: '2024-08-21T10:00:00Z',
    durationMinutes: 30,
    location: 'Virtual',
    meetingLink: 'https://zoom.us/j/987654321',
    interviewerId: 105,
    canBeRescheduled: true,
    canBeCancelled: true,
    canBeStarted: false,
    canBeCompleted: false,
    requiresFeedback: false,
    isOverdue: false,
    isUpcoming: true,
    application: {
      id: 5,
      applicant: {
        id: 5,
        firstName: 'Michael',
        lastName: 'Chen',
        email: 'michael.chen@email.com',
      },
      jobPosting: {
        id: 5,
        title: 'Software Engineer',
        department: 'Engineering',
      },
    },
  },
];

export const Default: Story = {
  args: {
    interviews: mockInterviews,
  },
};

export const LightSchedule: Story = {
  args: {
    interviews: lightSchedule,
  },
};

export const EmptyCalendar: Story = {
  args: {
    interviews: [],
  },
};

export const BusyWeek: Story = {
  args: {
    interviews: [
      ...mockInterviews,
      {
        id: 5,
        title: 'Final Interview',
        type: 'final',
        typeDisplayName: 'Final Interview',
        round: 'final',
        roundDisplayName: 'Final Round',
        status: 'scheduled',
        statusDisplayName: 'Scheduled',
        scheduledAt: '2024-08-22T11:00:00Z',
        durationMinutes: 120,
        location: 'Executive Conference Room',
        interviewerId: 106,
        canBeRescheduled: true,
        canBeCancelled: true,
        canBeStarted: false,
        canBeCompleted: false,
        requiresFeedback: false,
        isOverdue: false,
        isUpcoming: true,
        application: {
          id: 6,
          applicant: {
            id: 6,
            firstName: 'Lisa',
            lastName: 'Thompson',
            email: 'lisa.thompson@email.com',
          },
          jobPosting: {
            id: 6,
            title: 'Senior Product Manager',
            department: 'Product',
          },
        },
      },
    ],
  },
};

export const MixedStatuses: Story = {
  args: {
    interviews: [
      {
        id: 1,
        title: 'Completed Interview',
        type: 'technical',
        typeDisplayName: 'Technical Interview',
        round: 'first',
        roundDisplayName: 'First Round',
        status: 'completed',
        statusDisplayName: 'Completed',
        scheduledAt: '2024-08-19T10:00:00Z',
        durationMinutes: 60,
        location: 'Conference Room A',
        interviewerId: 101,
        canBeRescheduled: false,
        canBeCancelled: false,
        canBeStarted: false,
        canBeCompleted: false,
        requiresFeedback: true,
        isOverdue: false,
        isUpcoming: false,
        application: {
          id: 1,
          applicant: {
            id: 1,
            firstName: 'John',
            lastName: 'Smith',
            email: 'john.smith@email.com',
          },
          jobPosting: {
            id: 1,
            title: 'Senior Frontend Developer',
            department: 'Engineering',
          },
        },
      },
      {
        id: 2,
        title: 'Cancelled Interview',
        type: 'behavioral',
        typeDisplayName: 'Behavioral Interview',
        round: 'first',
        roundDisplayName: 'First Round',
        status: 'cancelled',
        statusDisplayName: 'Cancelled',
        scheduledAt: '2024-08-21T14:00:00Z',
        durationMinutes: 45,
        location: 'HR Office',
        interviewerId: 102,
        canBeRescheduled: false,
        canBeCancelled: false,
        canBeStarted: false,
        canBeCompleted: false,
        requiresFeedback: false,
        isOverdue: false,
        isUpcoming: false,
        application: {
          id: 2,
          applicant: {
            id: 2,
            firstName: 'Emily',
            lastName: 'Davis',
            email: 'emily.davis@email.com',
          },
          jobPosting: {
            id: 2,
            title: 'UX Designer',
            department: 'Design',
          },
        },
      },
    ],
  },
};
