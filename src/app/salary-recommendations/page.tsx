'use client';

import React from 'react';
import PageWrapper from '@/components/PageWrapper';
import SalaryRecommendationManager from '@/components/SalaryRecommendationManager';

export default function SalaryRecommendationsPage() {
  return (
    <PageWrapper
      title="Salary Recommendations"
      subtitle="Request, review, and approve salary recommendations for candidates"
    >
      <SalaryRecommendationManager />
    </PageWrapper>
  );
}
