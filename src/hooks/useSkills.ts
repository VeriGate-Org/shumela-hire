'use client';

import { useState, useEffect } from 'react';
import { apiFetchJson } from '@/lib/api-fetch';

export function useSkills() {
  const [skills, setSkills] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    const load = async () => {
      try {
        const names = await apiFetchJson<string[]>('/api/skills/names');
        if (!cancelled) setSkills(names);
      } catch {
        if (!cancelled) setSkills([]);
      } finally {
        if (!cancelled) setLoading(false);
      }
    };
    load();
    return () => { cancelled = true; };
  }, []);

  return { skills, loading };
}
