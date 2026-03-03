'use client';

import { useState, useEffect } from 'react';
import { departmentService } from '@/services/departmentService';

export function useDepartments() {
  const [departments, setDepartments] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    const load = async () => {
      try {
        const names = await departmentService.getActiveNames();
        if (!cancelled) setDepartments(names);
      } catch {
        if (!cancelled) setDepartments([]);
      } finally {
        if (!cancelled) setLoading(false);
      }
    };
    load();
    return () => { cancelled = true; };
  }, []);

  return { departments, loading };
}
