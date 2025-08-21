import type { Meta, StoryObj } from '@storybook/react';
import ThemeDemo from './ThemeDemo';
import { ThemeProvider } from '../contexts/ThemeContext';
import { AuthProvider } from '../contexts/AuthContext';

const meta = {
  title: 'Design System/Theme Demo',
  component: ThemeDemo,
  parameters: {
    layout: 'fullscreen',
    docs: {
      description: {
        component: `
# Theme System Demo

This comprehensive theme system provides:

## 🎨 Role-Based Themes
- **Admin**: Red theme for system administration
- **HR**: Blue theme for human resources
- **Hiring Manager**: Green theme for team hiring
- **Recruiter**: Purple theme for candidate sourcing
- **Applicant**: Orange theme for job seekers
- **Executive**: Indigo theme for strategic oversight

## 🌙 Dark/Light Mode Support
- Toggle between light, dark, and system preferences
- Maintains role colors in both modes
- Smooth transitions and animations
- Persistent user preferences

## 🎯 Usage
\`\`\`tsx
import { ThemeProvider, useTheme } from '@/contexts/ThemeContext';
import ThemeToggle from '@/components/ThemeToggle';

function App() {
  return (
    <ThemeProvider>
      <div className="bg-background text-foreground">
        <ThemeToggle />
        <div className="bg-primary text-primary-foreground p-4">
          Primary colored content
        </div>
      </div>
    </ThemeProvider>
  );
}
\`\`\`

## 🎨 CSS Classes
- \`bg-primary\` - Role-specific primary color
- \`text-primary-foreground\` - Contrasting text
- \`bg-card\` - Card background
- \`border-border\` - Consistent borders
- \`text-muted-foreground\` - Secondary text
        `,
      },
    },
  },
  decorators: [
    (Story) => (
      <AuthProvider>
        <ThemeProvider>
          <Story />
        </ThemeProvider>
      </AuthProvider>
    ),
  ],
  argTypes: {},
} satisfies Meta<typeof ThemeDemo>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {},
};

export const AdminTheme: Story = {
  args: {},
  parameters: {
    docs: {
      description: {
        story: 'Admin theme with red color scheme for system administration tasks.',
      },
    },
  },
  decorators: [
    (Story) => (
      <AuthProvider>
        <ThemeProvider>
          <div className="theme-admin">
            <Story />
          </div>
        </ThemeProvider>
      </AuthProvider>
    ),
  ],
};

export const HRTheme: Story = {
  args: {},
  parameters: {
    docs: {
      description: {
        story: 'HR theme with blue color scheme for human resources management.',
      },
    },
  },
  decorators: [
    (Story) => (
      <AuthProvider>
        <ThemeProvider>
          <div className="theme-hr">
            <Story />
          </div>
        </ThemeProvider>
      </AuthProvider>
    ),
  ],
};

export const HiringManagerTheme: Story = {
  args: {},
  parameters: {
    docs: {
      description: {
        story: 'Hiring Manager theme with green color scheme for team recruitment.',
      },
    },
  },
  decorators: [
    (Story) => (
      <AuthProvider>
        <ThemeProvider>
          <div className="theme-hiring">
            <Story />
          </div>
        </ThemeProvider>
      </AuthProvider>
    ),
  ],
};

export const RecruiterTheme: Story = {
  args: {},
  parameters: {
    docs: {
      description: {
        story: 'Recruiter theme with purple color scheme for candidate sourcing.',
      },
    },
  },
  decorators: [
    (Story) => (
      <AuthProvider>
        <ThemeProvider>
          <div className="theme-recruiter">
            <Story />
          </div>
        </ThemeProvider>
      </AuthProvider>
    ),
  ],
};

export const ApplicantTheme: Story = {
  args: {},
  parameters: {
    docs: {
      description: {
        story: 'Applicant theme with orange color scheme for job seekers.',
      },
    },
  },
  decorators: [
    (Story) => (
      <AuthProvider>
        <ThemeProvider>
          <div className="theme-applicant">
            <Story />
          </div>
        </ThemeProvider>
      </AuthProvider>
    ),
  ],
};

export const ExecutiveTheme: Story = {
  args: {},
  parameters: {
    docs: {
      description: {
        story: 'Executive theme with indigo color scheme for strategic oversight.',
      },
    },
  },
  decorators: [
    (Story) => (
      <AuthProvider>
        <ThemeProvider>
          <div className="theme-executive">
            <Story />
          </div>
        </ThemeProvider>
      </AuthProvider>
    ),
  ],
};

export const DarkMode: Story = {
  args: {},
  parameters: {
    docs: {
      description: {
        story: 'Dark mode version with all role themes supported.',
      },
    },
  },
  decorators: [
    (Story) => (
      <AuthProvider>
        <ThemeProvider>
          <div className="dark">
            <Story />
          </div>
        </ThemeProvider>
      </AuthProvider>
    ),
  ],
};
