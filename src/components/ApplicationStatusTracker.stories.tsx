import type { Meta, StoryObj } from '@storybook/react';
import ApplicationStatusTracker from './ApplicationStatusTracker';

const meta: Meta<typeof ApplicationStatusTracker> = {
  title: 'Components/ApplicationStatusTracker',
  component: ApplicationStatusTracker,
  parameters: {
    layout: 'centered',
    docs: {
      description: {
        component: 'A visual tracker showing the progress of a job application through different stages.',
      },
    },
  },
  tags: ['autodocs'],
  argTypes: {
    application: {
      control: { type: 'object' },
      description: 'Application object with status and details',
    },
    onWithdraw: {
      action: 'withdraw',
      description: 'Callback function when application is withdrawn',
    },
    showWithdrawOption: {
      control: { type: 'boolean' },
      description: 'Whether to show the withdraw option',
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// Mock application data
const mockApplication = {
  id: 1,
  jobTitle: 'Senior Frontend Developer',
  department: 'Engineering',
  status: 'under_review',
  statusDisplayName: 'Under Review',
  statusCssClass: 'bg-yellow-100 text-yellow-800',
  submittedAt: '2024-08-15T10:00:00Z',
  rating: 4.5,
  canBeWithdrawn: true,
  daysFromSubmission: 5,
};

export const Default: Story = {
  args: {
    application: mockApplication,
    showWithdrawOption: true,
  },
};

export const HighRatedApplication: Story = {
  args: {
    application: {
      ...mockApplication,
      rating: 5.0,
      status: 'interview_scheduled',
      statusDisplayName: 'Interview Scheduled',
      statusCssClass: 'bg-violet-100 text-violet-800',
    },
    showWithdrawOption: true,
  },
};

export const WithdrawnApplication: Story = {
  args: {
    application: {
      ...mockApplication,
      status: 'withdrawn',
      statusDisplayName: 'Withdrawn',
      statusCssClass: 'bg-gray-100 text-gray-800',
      withdrawnAt: '2024-08-18T14:30:00Z',
      withdrawalReason: 'Found another opportunity',
      canBeWithdrawn: false,
    },
    showWithdrawOption: false,
  },
};

export const RejectedApplication: Story = {
  args: {
    application: {
      ...mockApplication,
      status: 'rejected',
      statusDisplayName: 'Not Selected',
      statusCssClass: 'bg-red-100 text-red-800',
      canBeWithdrawn: false,
      rating: 2.0,
    },
    showWithdrawOption: false,
  },
};

export const NoWithdrawOption: Story = {
  args: {
    application: mockApplication,
    showWithdrawOption: false,
  },
};
