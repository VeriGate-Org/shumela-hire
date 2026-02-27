'use client';

import { useState, useEffect } from 'react';
import type { EmploymentEvent, EmploymentEventType } from '@/types/employee';
import { EVENT_TYPE_LABELS } from '@/types/employee';
import { getEmployeeEvents } from '@/services/employeeService';

const EVENT_ICONS: Record<EmploymentEventType, { color: string; bg: string }> = {
  HIRE: { color: 'text-green-600', bg: 'bg-green-100' },
  PROMOTION: { color: 'text-blue-600', bg: 'bg-blue-100' },
  TRANSFER: { color: 'text-indigo-600', bg: 'bg-indigo-100' },
  DEMOTION: { color: 'text-orange-600', bg: 'bg-orange-100' },
  SUSPENSION: { color: 'text-red-600', bg: 'bg-red-100' },
  REINSTATEMENT: { color: 'text-teal-600', bg: 'bg-teal-100' },
  RESIGNATION: { color: 'text-gray-600', bg: 'bg-gray-100' },
  DISMISSAL: { color: 'text-red-700', bg: 'bg-red-100' },
  RETIREMENT: { color: 'text-purple-600', bg: 'bg-purple-100' },
  CONTRACT_END: { color: 'text-gray-500', bg: 'bg-gray-100' },
};

interface EmploymentTimelineProps {
  employeeId: number;
}

export default function EmploymentTimeline({ employeeId }: EmploymentTimelineProps) {
  const [events, setEvents] = useState<EmploymentEvent[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    getEmployeeEvents(employeeId)
      .then(setEvents)
      .catch(() => setEvents([]))
      .finally(() => setLoading(false));
  }, [employeeId]);

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-cta" />
      </div>
    );
  }

  if (events.length === 0) {
    return (
      <div className="text-center py-12 text-muted-foreground">
        <p className="text-sm">No employment events recorded yet.</p>
      </div>
    );
  }

  return (
    <div className="relative">
      {/* Vertical line */}
      <div className="absolute left-4 top-0 bottom-0 w-px bg-border" />

      <div className="space-y-6">
        {events.map((event) => {
          const style = EVENT_ICONS[event.eventType] || { color: 'text-gray-500', bg: 'bg-gray-100' };
          return (
            <div key={event.id} className="relative pl-10">
              {/* Dot */}
              <div className={`absolute left-2.5 top-1 w-3 h-3 rounded-full border-2 border-card ${style.bg}`} />

              <div className="enterprise-card p-4">
                <div className="flex items-start justify-between gap-4">
                  <div>
                    <span className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ${style.bg} ${style.color}`}>
                      {EVENT_TYPE_LABELS[event.eventType]}
                    </span>
                    {event.description && (
                      <p className="mt-1.5 text-sm text-foreground">{event.description}</p>
                    )}

                    {/* Change details */}
                    {(event.previousDepartment || event.newDepartment) && (
                      <p className="mt-1 text-xs text-muted-foreground">
                        Department: {event.previousDepartment || '—'} → {event.newDepartment || '—'}
                      </p>
                    )}
                    {(event.previousJobTitle || event.newJobTitle) && (
                      <p className="text-xs text-muted-foreground">
                        Title: {event.previousJobTitle || '—'} → {event.newJobTitle || '—'}
                      </p>
                    )}
                    {(event.previousLocation || event.newLocation) && (
                      <p className="text-xs text-muted-foreground">
                        Location: {event.previousLocation || '—'} → {event.newLocation || '—'}
                      </p>
                    )}

                    {event.notes && (
                      <p className="mt-1.5 text-xs text-muted-foreground italic">{event.notes}</p>
                    )}
                  </div>
                  <div className="text-right shrink-0">
                    <p className="text-sm font-medium text-foreground">
                      {new Date(event.eventDate).toLocaleDateString('en-ZA', { year: 'numeric', month: 'short', day: 'numeric' })}
                    </p>
                    {event.effectiveDate && event.effectiveDate !== event.eventDate && (
                      <p className="text-xs text-muted-foreground">
                        Effective: {new Date(event.effectiveDate).toLocaleDateString('en-ZA', { year: 'numeric', month: 'short', day: 'numeric' })}
                      </p>
                    )}
                    {event.recordedBy && (
                      <p className="text-xs text-muted-foreground mt-0.5">by {event.recordedBy}</p>
                    )}
                  </div>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
