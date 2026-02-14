'use client';

import ThemeToggle from './ThemeToggle';

interface EnterpriseThemeToggleProps {
  variant?: 'compact' | 'full' | 'floating';
  showPreview?: boolean;
  className?: string;
}

export default function EnterpriseThemeToggle({
  variant = 'full',
  className = ''
}: EnterpriseThemeToggleProps) {
  return <ThemeToggle compact={variant === 'compact'} className={className} />;
}
