'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';

export default function SupportPage() {
  const router = useRouter();

  useEffect(() => {
    router.replace('/help?tab=contact');
  }, [router]);

  return null;
}
