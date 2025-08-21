'use client';

import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';

interface LayoutContextType {
  useModernLayout: boolean;
  setUseModernLayout: (use: boolean) => void;
  toggleLayout: () => void;
}

const LayoutContext = createContext<LayoutContextType | undefined>(undefined);

export function LayoutProvider({ children }: { children: ReactNode }) {
  const [useModernLayout, setUseModernLayout] = useState(true); // Default to modern layout

  // Persist layout preference in localStorage
  useEffect(() => {
    const saved = localStorage.getItem('useModernLayout');
    if (saved !== null) {
      setUseModernLayout(JSON.parse(saved));
    }
  }, []);

  useEffect(() => {
    localStorage.setItem('useModernLayout', JSON.stringify(useModernLayout));
  }, [useModernLayout]);

  const toggleLayout = () => {
    setUseModernLayout(prev => !prev);
  };

  return (
    <LayoutContext.Provider value={{
      useModernLayout,
      setUseModernLayout,
      toggleLayout
    }}>
      {children}
    </LayoutContext.Provider>
  );
}

export function useLayout() {
  const context = useContext(LayoutContext);
  if (context === undefined) {
    throw new Error('useLayout must be used within a LayoutProvider');
  }
  return context;
}
