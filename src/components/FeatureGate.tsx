'use client';

import { ReactNode } from 'react';
import { useFeatureGate } from '@/contexts/FeatureGateContext';

interface FeatureGateProps {
  feature: string;
  children: ReactNode;
  fallback?: ReactNode;
}

export function FeatureGate({ feature, children, fallback = null }: FeatureGateProps) {
  const { isFeatureEnabled, isLoading } = useFeatureGate();

  if (isLoading) return null;

  if (!isFeatureEnabled(feature)) {
    return <>{fallback}</>;
  }

  return <>{children}</>;
}
