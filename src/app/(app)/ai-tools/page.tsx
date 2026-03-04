'use client';

import React from 'react';
import PageWrapper from '@/components/PageWrapper';
import { FeatureGate } from '@/components/FeatureGate';
import AiSmartSearch from '@/components/ai/AiSmartSearch';
import AiEmailDrafter from '@/components/ai/AiEmailDrafter';
import AiJobDescriptionWriter from '@/components/ai/AiJobDescriptionWriter';
import AiSalaryBenchmark from '@/components/ai/AiSalaryBenchmark';
import AiAssistPanel from '@/components/ai/AiAssistPanel';

export default function AiToolsPage() {
  return (
    <FeatureGate feature="AI_ENABLED">
      <PageWrapper
        title="AI Tools"
        subtitle="General-purpose AI tools for recruitment workflows"
      >
        <div className="space-y-6">
          {/* Smart Search — full-width, primary position */}
          <AiAssistPanel title="AI Smart Search" feature="AI_SEARCH" defaultExpanded>
            <AiSmartSearch />
          </AiAssistPanel>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Email Drafter — standalone mode */}
            <AiAssistPanel title="AI Email Drafter" feature="AI_EMAIL_DRAFTER">
              <AiEmailDrafter />
            </AiAssistPanel>

            {/* Job Description Writer — standalone mode */}
            <AiAssistPanel title="AI Job Description Writer" feature="AI_JOB_DESCRIPTION">
              <AiJobDescriptionWriter />
            </AiAssistPanel>
          </div>

          {/* Salary Benchmark — standalone mode */}
          <AiAssistPanel title="AI Salary Benchmark" feature="AI_SALARY_BENCHMARK">
            <AiSalaryBenchmark />
          </AiAssistPanel>
        </div>
      </PageWrapper>
    </FeatureGate>
  );
}
