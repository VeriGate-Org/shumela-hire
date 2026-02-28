'use client';

import React, { createContext, useContext, useState, useEffect, useCallback, ReactNode } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { apiFetch } from '@/lib/api-fetch';

interface FeatureGateContextType {
  enabledFeatures: string[];
  isFeatureEnabled: (code: string) => boolean;
  isLoading: boolean;
  refresh: () => Promise<void>;
}

const FeatureGateContext = createContext<FeatureGateContextType | undefined>(undefined);

export function useFeatureGate() {
  const context = useContext(FeatureGateContext);
  if (context === undefined) {
    throw new Error('useFeatureGate must be used within a FeatureGateProvider');
  }
  return context;
}

export function FeatureGateProvider({ children }: { children: ReactNode }) {
  const { user, isAuthenticated } = useAuth();
  const [enabledFeatures, setEnabledFeatures] = useState<string[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const fetchFeatures = useCallback(async () => {
    if (!isAuthenticated) {
      setEnabledFeatures([]);
      return;
    }

    // Platform owners get all features client-side
    if (user?.role === 'PLATFORM_OWNER') {
      setEnabledFeatures(['__ALL__']);
      return;
    }

    setIsLoading(true);
    try {
      const response = await apiFetch('/api/features/enabled');
      if (response.ok) {
        const features: string[] = await response.json();
        setEnabledFeatures(features);
      } else {
        // Fail closed — no features enabled on error
        setEnabledFeatures([]);
      }
    } catch {
      setEnabledFeatures([]);
    } finally {
      setIsLoading(false);
    }
  }, [isAuthenticated, user?.role]);

  useEffect(() => {
    fetchFeatures();
  }, [fetchFeatures]);

  const isFeatureEnabled = useCallback((code: string): boolean => {
    if (enabledFeatures.includes('__ALL__')) return true;
    return enabledFeatures.includes(code);
  }, [enabledFeatures]);

  return (
    <FeatureGateContext.Provider value={{
      enabledFeatures,
      isFeatureEnabled,
      isLoading,
      refresh: fetchFeatures,
    }}>
      {children}
    </FeatureGateContext.Provider>
  );
}
