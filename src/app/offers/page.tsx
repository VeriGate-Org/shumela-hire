'use client';

import React from 'react';
import PageWrapper from '@/components/PageWrapper';
import OfferManagement from '@/components/OfferManagement';

export default function OffersPage() {
  return (
    <PageWrapper 
      title="Offers"
      subtitle="Manage job offers, negotiations, and candidate onboarding processes"
    >
      <OfferManagement />
    </PageWrapper>
  );
}