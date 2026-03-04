'use client';

import React, { useState, ReactNode } from 'react';
import { FeatureGate } from '@/components/FeatureGate';

interface AiAssistPanelProps {
  title: string;
  feature: string;
  children: ReactNode;
  defaultExpanded?: boolean;
}

export default function AiAssistPanel({ title, feature, children, defaultExpanded = false }: AiAssistPanelProps) {
  const [expanded, setExpanded] = useState(defaultExpanded);

  return (
    <FeatureGate feature="AI_ENABLED">
      <FeatureGate feature={feature}>
        <div className="border border-gray-200 rounded-sm bg-white">
          <button
            type="button"
            onClick={() => setExpanded(!expanded)}
            className="w-full flex items-center justify-between px-4 py-3 hover:bg-gray-50 transition-colors"
          >
            <div className="flex items-center gap-2">
              <div className="w-2 h-2 rounded-full bg-teal-500" />
              <span className="text-sm font-semibold text-gray-700 uppercase tracking-wider">{title}</span>
              <span className="text-[10px] font-medium bg-teal-100 text-teal-700 px-1.5 py-0.5 rounded">AI</span>
            </div>
            <svg
              className={`w-4 h-4 text-gray-400 transition-transform ${expanded ? 'rotate-180' : ''}`}
              fill="none" viewBox="0 0 24 24" stroke="currentColor"
            >
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
            </svg>
          </button>
          {expanded && (
            <div className="px-4 pb-4 pt-1 border-t border-gray-100">
              {children}
            </div>
          )}
        </div>
      </FeatureGate>
    </FeatureGate>
  );
}
