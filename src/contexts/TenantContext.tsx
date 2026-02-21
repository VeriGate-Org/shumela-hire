'use client';

import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { getTenantSubdomain } from '@/lib/tenant-utils';
import { apiFetch } from '@/lib/api-fetch';

interface TenantInfo {
  id: string;
  name: string;
  subdomain: string;
  plan: string;
}

interface TenantContextType {
  tenant: TenantInfo | null;
  tenantId: string;
  isLoading: boolean;
  error: string | null;
}

const TenantContext = createContext<TenantContextType | undefined>(undefined);

export const useTenant = () => {
  const context = useContext(TenantContext);
  if (context === undefined) {
    throw new Error('useTenant must be used within a TenantProvider');
  }
  return context;
};

interface TenantProviderProps {
  children: ReactNode;
}

export const TenantProvider: React.FC<TenantProviderProps> = ({ children }) => {
  const [tenant, setTenant] = useState<TenantInfo | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const subdomain = typeof window !== 'undefined' ? getTenantSubdomain() : 'default';

  useEffect(() => {
    resolveTenant();
  }, [subdomain]);

  async function resolveTenant() {
    // In dev with default subdomain, use a static fallback
    if (subdomain === 'default') {
      setTenant({ id: 'default', name: 'Default Organization', subdomain: 'default', plan: 'STANDARD' });
      setIsLoading(false);
      return;
    }

    try {
      const response = await apiFetch(`/api/public/tenants/resolve/${subdomain}`);
      if (response.ok) {
        const data: TenantInfo = await response.json();
        setTenant(data);
      } else {
        setError('Organization not found');
      }
    } catch {
      setError('Failed to resolve organization');
    } finally {
      setIsLoading(false);
    }
  }

  const tenantId = tenant?.id || 'default';

  return (
    <TenantContext.Provider value={{ tenant, tenantId, isLoading, error }}>
      {children}
    </TenantContext.Provider>
  );
};
