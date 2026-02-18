import React from 'react';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import EmptyState from '../EmptyState';
import { BriefcaseIcon } from '@heroicons/react/24/outline';

// Mock next/link to render a plain anchor
jest.mock('next/link', () => {
  return function MockLink({ children, href, ...props }: { children: React.ReactNode; href: string }) {
    return <a href={href} {...props}>{children}</a>;
  };
});

describe('EmptyState', () => {
  it('renders icon, title, and description', () => {
    render(
      <EmptyState
        icon={BriefcaseIcon}
        title="No jobs found"
        description="There are no open positions at the moment."
      />
    );

    expect(screen.getByText('No jobs found')).toBeInTheDocument();
    expect(screen.getByText('There are no open positions at the moment.')).toBeInTheDocument();

    // The icon should be rendered as an SVG within the status container
    const container = screen.getByRole('status');
    expect(container).toBeInTheDocument();
    const svg = container.querySelector('svg');
    expect(svg).toBeInTheDocument();
  });

  it('renders action button with href when action prop has href', () => {
    render(
      <EmptyState
        icon={BriefcaseIcon}
        title="No applications"
        description="Start by creating a new application."
        action={{ label: 'Create Application', href: '/applications/new' }}
      />
    );

    const link = screen.getByRole('link', { name: /Create Application/i });
    expect(link).toBeInTheDocument();
    expect(link).toHaveAttribute('href', '/applications/new');
  });

  it('renders action button with onClick when action prop has onClick', async () => {
    const handleClick = jest.fn();
    const user = userEvent.setup();

    render(
      <EmptyState
        icon={BriefcaseIcon}
        title="No results"
        description="Try adjusting your filters."
        action={{ label: 'Reset Filters', onClick: handleClick }}
      />
    );

    const button = screen.getByRole('button', { name: /Reset Filters/i });
    expect(button).toBeInTheDocument();

    await user.click(button);
    expect(handleClick).toHaveBeenCalledTimes(1);
  });

  it('does not render an action button when no action prop is provided', () => {
    render(
      <EmptyState
        icon={BriefcaseIcon}
        title="Empty state"
        description="Nothing to show here."
      />
    );

    expect(screen.queryByRole('button')).not.toBeInTheDocument();
    expect(screen.queryByRole('link')).not.toBeInTheDocument();
  });

  it('renders with the correct accessible role', () => {
    render(
      <EmptyState
        icon={BriefcaseIcon}
        title="No data"
        description="No data is available."
      />
    );

    const status = screen.getByRole('status');
    expect(status).toBeInTheDocument();
  });
});
