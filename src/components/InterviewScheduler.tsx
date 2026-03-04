'use client';

import React, { useCallback, useEffect, useRef, useState } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { apiFetch } from '@/lib/api-fetch';

interface InterviewSchedulerProps {
  interviewId?: number;
  onSuccess?: (interview: InterviewSaveResponse) => void;
  onCancel?: () => void;
}

interface Application {
  id: number;
  applicantId: number;
  applicantName: string;
  applicantEmail: string;
  jobAdId: number;
  jobTitle: string;
  department: string;
  status: string;
  statusDisplayName: string;
}

interface InterviewData {
  id?: number;
  title: string;
  type: string;
  round: string;
  scheduledAt: string;
  durationMinutes: number;
  location: string;
  meetingLink: string;
  phoneNumber: string;
  meetingRoom: string;
  instructions: string;
  agenda: string;
  interviewerId: number;
  additionalInterviewers: string;
  applicationId: number;
}

interface InterviewSaveResponse {
  id: number;
  title: string;
  scheduledAt: string;
  application: {
    id: number;
  };
}

const INTERVIEW_TYPES = [
  { value: 'PHONE', label: 'Phone Interview' },
  { value: 'VIDEO', label: 'Video Interview' },
  { value: 'IN_PERSON', label: 'In-Person Interview' },
  { value: 'PANEL', label: 'Panel Interview' },
  { value: 'TECHNICAL', label: 'Technical Interview' },
  { value: 'BEHAVIOURAL', label: 'Behavioural Interview' },
  { value: 'COMPETENCY', label: 'Competency Interview' },
  { value: 'GROUP', label: 'Group Interview' },
  { value: 'PRESENTATION', label: 'Presentation Interview' },
  { value: 'CASE_STUDY', label: 'Case Study Interview' },
];

const INTERVIEW_ROUNDS = [
  { value: 'SCREENING', label: 'Phone Screening' },
  { value: 'FIRST_ROUND', label: 'First Interview' },
  { value: 'TECHNICAL', label: 'Technical Assessment' },
  { value: 'SECOND_ROUND', label: 'Second Interview' },
  { value: 'PANEL', label: 'Panel Interview' },
  { value: 'MANAGER', label: 'Manager Interview' },
  { value: 'FINAL', label: 'Final Interview' },
  { value: 'OFFER', label: 'Offer Discussion' },
];

interface Interviewer {
  id: number;
  name: string;
  email: string;
  role: string;
  adObjectId?: string;
  department?: string;
}

function getApplicationLabel(app: Application): string {
  return `${app.applicantName || 'Unknown'} - ${app.jobTitle || 'Unknown'} (${app.department || 'Unknown'})`;
}

function ApplicationSearchSelect({
  applications,
  value,
  onChange,
  error,
  search,
  onSearchChange,
  open,
  onOpenChange,
}: {
  applications: Application[];
  value: number;
  onChange: (id: number) => void;
  error?: string;
  search: string;
  onSearchChange: (s: string) => void;
  open: boolean;
  onOpenChange: (o: boolean) => void;
}) {
  const containerRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  const selected = applications.find((a) => a.id === value);

  const filtered = applications.filter((app) => {
    if (!search) return true;
    const label = getApplicationLabel(app).toLowerCase();
    return label.includes(search.toLowerCase());
  });

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
        onOpenChange(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [onOpenChange]);

  return (
    <div>
      <label htmlFor="application-search" className="block text-sm font-medium text-foreground mb-1">
        Select Application *
      </label>
      <div ref={containerRef} className="relative">
        <input
          ref={inputRef}
          id="application-search"
          type="text"
          value={open ? search : (selected ? getApplicationLabel(selected) : '')}
          onChange={(e) => {
            onSearchChange(e.target.value);
            if (!open) onOpenChange(true);
          }}
          onFocus={() => {
            onOpenChange(true);
            onSearchChange('');
          }}
          placeholder="Search by name, job title, or department..."
          aria-required="true"
          aria-invalid={!!error}
          aria-describedby={error ? 'application-id-error' : undefined}
          aria-expanded={open}
          role="combobox"
          aria-autocomplete="list"
          autoComplete="off"
          className={`w-full p-3 border rounded-control bg-card focus:ring-2 focus:ring-gold-500/60 focus:border-primary ${error ? 'border-red-500' : 'border-border'}`}
        />
        {selected && !open && (
          <button
            type="button"
            onClick={() => {
              onChange(0);
              onSearchChange('');
              onOpenChange(true);
              inputRef.current?.focus();
            }}
            className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
            aria-label="Clear selection"
          >
            &#x2715;
          </button>
        )}
        {open && (
          <ul
            role="listbox"
            className="absolute z-50 w-full mt-1 max-h-60 overflow-auto bg-card border border-border rounded-control shadow-lg"
          >
            {filtered.length === 0 ? (
              <li className="px-3 py-2 text-sm text-muted-foreground">No applications found</li>
            ) : (
              filtered.map((app) => (
                <li
                  key={app.id}
                  role="option"
                  aria-selected={app.id === value}
                  onClick={() => {
                    onChange(app.id);
                    onSearchChange('');
                    onOpenChange(false);
                  }}
                  className={`px-3 py-2 text-sm cursor-pointer hover:bg-gold-50 ${app.id === value ? 'bg-gold-100 font-medium' : ''}`}
                >
                  <span className="font-medium">{app.applicantName || 'Unknown'}</span>
                  <span className="text-muted-foreground"> - {app.jobTitle || 'Unknown'}</span>
                  <span className="text-muted-foreground text-xs ml-1">({app.department || 'Unknown'})</span>
                  <span className="text-xs text-muted-foreground ml-2">{app.statusDisplayName}</span>
                </li>
              ))
            )}
          </ul>
        )}
      </div>
      {error && <p id="application-id-error" role="alert" className="text-red-500 text-sm mt-1">{error}</p>}
    </div>
  );
}

function InterviewerMultiSelect({
  selectedInterviewers,
  onSelect,
  onRemove,
  error,
  search,
  onSearchChange,
  open,
  onOpenChange,
  adResults,
  searching,
}: {
  selectedInterviewers: Interviewer[];
  onSelect: (interviewer: Interviewer) => void;
  onRemove: (interviewer: Interviewer) => void;
  error?: string;
  search: string;
  onSearchChange: (s: string) => void;
  open: boolean;
  onOpenChange: (o: boolean) => void;
  adResults: Interviewer[];
  searching: boolean;
}) {
  const containerRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  const selectedEmails = new Set(selectedInterviewers.map((i) => i.email));
  const filtered = adResults.filter((i) => !selectedEmails.has(i.email));

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
        onOpenChange(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [onOpenChange]);

  return (
    <div>
      <label htmlFor="interviewer-search" className="block text-sm font-medium text-foreground mb-1">
        Interview Panel *
      </label>
      <div ref={containerRef} className="relative">
        <div
          className={`w-full min-h-[48px] p-2 border rounded-control bg-card focus-within:ring-2 focus-within:ring-gold-500/60 focus-within:border-primary flex flex-wrap gap-1.5 items-center cursor-text ${error ? 'border-red-500' : 'border-border'}`}
          onClick={() => inputRef.current?.focus()}
        >
          {selectedInterviewers.map((i) => (
            <span
              key={i.adObjectId || i.id}
              className="inline-flex items-center gap-1 px-2 py-0.5 bg-gold-100 text-gold-800 rounded text-sm"
            >
              {i.name}
              <button
                type="button"
                onClick={(e) => {
                  e.stopPropagation();
                  onRemove(i);
                }}
                className="text-gold-600 hover:text-gold-900 font-bold"
                aria-label={`Remove ${i.name}`}
              >
                &#x2715;
              </button>
            </span>
          ))}
          <input
            ref={inputRef}
            id="interviewer-search"
            type="text"
            value={search}
            onChange={(e) => {
              onSearchChange(e.target.value);
              if (!open) onOpenChange(true);
            }}
            onFocus={() => onOpenChange(true)}
            placeholder={selectedInterviewers.length === 0 ? 'Search by name or email...' : ''}
            className="flex-1 min-w-[120px] p-1 bg-transparent outline-none text-sm"
            autoComplete="off"
            role="combobox"
            aria-expanded={open}
            aria-autocomplete="list"
          />
        </div>
        {open && (
          <ul
            role="listbox"
            className="absolute z-50 w-full mt-1 max-h-60 overflow-auto bg-card border border-border rounded-control shadow-lg"
          >
            {searching ? (
              <li className="px-3 py-2 text-sm text-muted-foreground">Searching...</li>
            ) : filtered.length === 0 ? (
              <li className="px-3 py-2 text-sm text-muted-foreground">
                {search.length < 2 ? 'Type at least 2 characters to search' : 'No results found'}
              </li>
            ) : (
              filtered.map((i) => (
                <li
                  key={i.adObjectId || i.id}
                  role="option"
                  aria-selected={false}
                  onClick={() => {
                    onSelect(i);
                    onSearchChange('');
                  }}
                  className="px-3 py-2 text-sm cursor-pointer hover:bg-gold-50"
                >
                  <span className="font-medium">{i.name}</span>
                  {i.role && <span className="text-muted-foreground ml-1">- {i.role}</span>}
                  {i.department && <span className="text-muted-foreground text-xs ml-1">({i.department})</span>}
                </li>
              ))
            )}
          </ul>
        )}
      </div>
      {error && <p role="alert" className="text-red-500 text-sm mt-1">{error}</p>}
    </div>
  );
}

export default function InterviewScheduler({ interviewId, onSuccess, onCancel }: InterviewSchedulerProps) {
  const { user } = useAuth();
  const [selectedInterviewers, setSelectedInterviewers] = useState<Interviewer[]>([]);
  const [adResults, setAdResults] = useState<Interviewer[]>([]);
  const [adSearching, setAdSearching] = useState(false);
  const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const [formData, setFormData] = useState<InterviewData>({
    title: '',
    type: 'VIDEO',
    round: 'SCREENING',
    scheduledAt: '',
    durationMinutes: 60,
    location: '',
    meetingLink: '',
    phoneNumber: '',
    meetingRoom: '',
    instructions: '',
    agenda: '',
    interviewerId: 0,
    additionalInterviewers: '[]',
    applicationId: 0,
  });

  const [applications, setApplications] = useState<Application[]>([]);
  const [availability, setAvailability] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [checkingAvailability, setCheckingAvailability] = useState(false);
  const [appSearch, setAppSearch] = useState('');
  const [appDropdownOpen, setAppDropdownOpen] = useState(false);
  const [interviewerSearch, setInterviewerSearch] = useState('');
  const [interviewerDropdownOpen, setInterviewerDropdownOpen] = useState(false);

  const getActorId = useCallback((): number | null => {
    const actorId = Number(user?.id);
    if (!Number.isFinite(actorId) || actorId <= 0) {
      setErrors((prev) => ({ ...prev, general: 'Unable to identify current user. Please sign in again.' }));
      return null;
    }
    return actorId;
  }, [user]);

  const loadApplications = useCallback(async () => {
    try {
      const response = await apiFetch('/api/applications?status=SUBMITTED,SCREENING,INTERVIEW_SCHEDULED,INTERVIEW_COMPLETED&size=200');
      if (response.ok) {
        const data = await response.json();
        setApplications(data.content || data);
      }
    } catch (error) {
      console.error('Error loading applications:', error);
    }
  }, []);

  const loadInterview = useCallback(async () => {
    if (!interviewId) return;

    try {
      setLoading(true);
      const response = await apiFetch(`/api/interviews/${interviewId}`);
      if (response.ok) {
        const data = await response.json();
        setFormData({
          ...data,
          scheduledAt: new Date(data.scheduledAt).toISOString().slice(0, 16),
          applicationId: data.application?.id ?? data.applicationId ?? 0,
          additionalInterviewers: data.additionalInterviewers || '[]',
        });

        // Load interviewers from internal user table for display
        const interviewerIds: number[] = [];
        if (data.interviewerId) interviewerIds.push(data.interviewerId);
        try {
          const additional: number[] = JSON.parse(data.additionalInterviewers || '[]');
          interviewerIds.push(...additional);
        } catch { /* ignore */ }

        if (interviewerIds.length > 0) {
          const intResponse = await apiFetch('/api/auth/interviewers');
          if (intResponse.ok) {
            const allInterviewers = await intResponse.json() as Interviewer[];
            const selected = allInterviewers.filter((i: Interviewer) => interviewerIds.includes(i.id));
            setSelectedInterviewers(selected);
          }
        }
      }
    } catch (error) {
      console.error('Error loading interview:', error);
    } finally {
      setLoading(false);
    }
  }, [interviewId]);

  const searchAdUsers = useCallback(async (query: string) => {
    if (query.trim().length < 2) {
      setAdResults([]);
      return;
    }
    try {
      setAdSearching(true);
      const response = await apiFetch(`/api/users/search-ad?q=${encodeURIComponent(query.trim())}`);
      if (response.ok) {
        const data = await response.json();
        setAdResults(
          (data as Array<{ adObjectId: string; displayName: string; email: string; jobTitle: string; department: string }>).map(
            (u) => ({
              id: 0,
              name: u.displayName || '',
              email: u.email || '',
              role: u.jobTitle || '',
              adObjectId: u.adObjectId,
              department: u.department || '',
            }),
          ),
        );
      }
    } catch (error) {
      console.error('Error searching AD users:', error);
    } finally {
      setAdSearching(false);
    }
  }, []);

  useEffect(() => {
    void loadApplications();
    if (interviewId) {
      void loadInterview();
    }
  }, [interviewId, loadApplications, loadInterview]);

  useEffect(() => {
    if (formData.applicationId > 0 && formData.round) {
      const application = applications.find((app) => app.id === formData.applicationId);
      if (application) {
        const roundLabel = INTERVIEW_ROUNDS.find((round) => round.value === formData.round)?.label || '';
        setFormData((prev) => ({
          ...prev,
          title: `${roundLabel} - ${application.jobTitle || 'Unknown'}`,
        }));
      }
    }
  }, [formData.applicationId, formData.round, applications]);

  const handleInputChange = <K extends keyof InterviewData>(field: K, value: InterviewData[K]) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    if (errors[field]) {
      setErrors((prev) => ({ ...prev, [field]: '' }));
    }
  };

  const handleInterviewerSelect = (interviewer: Interviewer) => {
    setSelectedInterviewers((prev) => [...prev, interviewer]);
    if (errors.interviewerId) {
      setErrors((prev) => ({ ...prev, interviewerId: '' }));
    }
  };

  const handleInterviewerRemove = (interviewer: Interviewer) => {
    setSelectedInterviewers((prev) =>
      prev.filter((i) => (i.adObjectId || i.email) !== (interviewer.adObjectId || interviewer.email)),
    );
  };

  const handleInterviewerSearchChange = (query: string) => {
    setInterviewerSearch(query);
    if (debounceRef.current) clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => {
      void searchAdUsers(query);
    }, 300);
  };

  const checkAvailability = async () => {
    const primaryInterviewer = selectedInterviewers[0];
    if (!formData.scheduledAt || !primaryInterviewer || primaryInterviewer.id <= 0) return;

    try {
      setCheckingAvailability(true);
      const startTime = new Date(formData.scheduledAt).toISOString();

      const availabilityResponse = await apiFetch(
        `/api/interviews/availability/interviewer/${primaryInterviewer.id}?startTime=${startTime}&durationMinutes=${formData.durationMinutes}`,
      );

      if (availabilityResponse.ok) {
        const data = await availabilityResponse.json();
        if (!data.available) {
          setErrors((prev) => ({ ...prev, scheduledAt: 'Interviewer is not available at this time' }));
        } else {
          setErrors((prev) => ({ ...prev, scheduledAt: '' }));
        }
      }

      const suggestionsResponse = await apiFetch(
        `/api/interviews/suggestions/interviewer/${primaryInterviewer.id}?preferredDate=${startTime}&durationMinutes=${formData.durationMinutes}&numberOfSuggestions=5`,
      );

      if (suggestionsResponse.ok) {
        const suggestions = await suggestionsResponse.json() as string[];
        setAvailability(suggestions);
      }
    } catch (error) {
      console.error('Error checking availability:', error);
    } finally {
      setCheckingAvailability(false);
    }
  };

  const validateForm = () => {
    const newErrors: Record<string, string> = {};

    if (!formData.title.trim()) newErrors.title = 'Interview title is required';
    if (!formData.scheduledAt) newErrors.scheduledAt = 'Interview date and time is required';
    if (formData.applicationId === 0) newErrors.applicationId = 'Please select an application';
    if (selectedInterviewers.length === 0) newErrors.interviewerId = 'Please select at least one interviewer';
    if (formData.durationMinutes < 15) newErrors.durationMinutes = 'Duration must be at least 15 minutes';
    if (formData.durationMinutes > 480) newErrors.durationMinutes = 'Duration cannot exceed 8 hours';

    if (formData.scheduledAt) {
      const scheduledDate = new Date(formData.scheduledAt);
      const hour = scheduledDate.getHours();
      const dayOfWeek = scheduledDate.getDay();

      if (dayOfWeek === 0 || dayOfWeek === 6) {
        newErrors.scheduledAt = 'Interviews must be scheduled on weekdays';
      } else if (hour < 8 || hour >= 18) {
        newErrors.scheduledAt = 'Interviews must be scheduled between 8 AM and 6 PM';
      }
    }

    if (formData.scheduledAt) {
      const scheduledDate = new Date(formData.scheduledAt);
      const now = new Date();
      const hoursAhead = (scheduledDate.getTime() - now.getTime()) / (1000 * 60 * 60);

      if (hoursAhead < 2) {
        newErrors.scheduledAt = 'Interview must be scheduled at least 2 hours in advance';
      }
    }

    if (formData.type === 'PHONE' && !formData.phoneNumber.trim()) {
      newErrors.phoneNumber = 'Phone number is required for phone interviews';
    }

    if (formData.type === 'VIDEO' && !formData.meetingLink.trim()) {
      newErrors.meetingLink = 'Meeting link is required for video interviews';
    }

    if (['IN_PERSON', 'PANEL', 'GROUP'].includes(formData.type) && !formData.location.trim()) {
      newErrors.location = 'Location is required for in-person interviews';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    if (!validateForm()) return;

    const actorId = getActorId();
    if (!actorId) return;

    try {
      setLoading(true);

      // Provision any AD users that don't have an internal ID yet
      const provisionedIds: number[] = [];
      for (const interviewer of selectedInterviewers) {
        if (interviewer.id > 0) {
          provisionedIds.push(interviewer.id);
        } else {
          const res = await apiFetch('/api/users/provision-ad', {
            method: 'POST',
            body: JSON.stringify({
              adObjectId: interviewer.adObjectId,
              displayName: interviewer.name,
              email: interviewer.email,
              jobTitle: interviewer.role,
              department: interviewer.department,
            }),
          });
          if (res.ok) {
            const provisioned = await res.json() as { id: number };
            provisionedIds.push(provisioned.id);
          } else {
            setErrors({ general: `Failed to provision user: ${interviewer.name}` });
            setLoading(false);
            return;
          }
        }
      }

      const [primaryId, ...restIds] = provisionedIds;

      const submitData: InterviewData = {
        ...formData,
        interviewerId: primaryId ?? 0,
        additionalInterviewers: JSON.stringify(restIds),
        scheduledAt: new Date(formData.scheduledAt).toISOString(),
      };

      const url = interviewId
        ? `/api/interviews/${interviewId}?updatedBy=${actorId}`
        : `/api/interviews?createdBy=${actorId}`;

      const method = interviewId ? 'PUT' : 'POST';

      const response = await apiFetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(submitData),
      });

      if (response.ok) {
        const result = await response.json() as InterviewSaveResponse;
        if (onSuccess) {
          onSuccess(result);
        }
      } else {
        const errorData = await response.json();
        setErrors({ general: errorData.message || 'Failed to save interview' });
      }
    } catch (error) {
      console.error('Error saving interview:', error);
      setErrors({ general: 'An error occurred while saving' });
    } finally {
      setLoading(false);
    }
  };

  const handleSuggestedTimeSelect = (suggestedTime: string) => {
    const localTime = new Date(suggestedTime).toISOString().slice(0, 16);
    handleInputChange('scheduledAt', localTime);
  };

  if (loading && interviewId) {
    return (
      <div className="flex items-center justify-center p-8">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-cta mx-auto mb-4" />
          <p className="text-muted-foreground">Loading interview...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto bg-card rounded-card border border-border shadow-md">
      <div className="px-6 py-4 border-b border-border">
        <h2 className="text-2xl font-bold text-foreground">
          {interviewId ? 'Edit Interview' : 'Schedule New Interview'}
        </h2>
        <p className="text-muted-foreground mt-1">
          {interviewId ? 'Update interview details and scheduling' : 'Schedule an interview with a candidate'}
        </p>
      </div>

      <form onSubmit={handleSubmit} className="p-6">
        {errors.general && (
          <div className="mb-6 p-4 bg-red-100 border border-red-300 text-red-700 rounded-control">
            {errors.general}
          </div>
        )}

        <div className="space-y-6">
          {!interviewId && (
            <ApplicationSearchSelect
              applications={applications}
              value={formData.applicationId}
              onChange={(id) => handleInputChange('applicationId', id)}
              error={errors.applicationId}
              search={appSearch}
              onSearchChange={setAppSearch}
              open={appDropdownOpen}
              onOpenChange={setAppDropdownOpen}
            />
          )}

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label htmlFor="interview-title" className="block text-sm font-medium text-foreground mb-1">
                Interview Title *
              </label>
              <input
                type="text"
                id="interview-title"
                value={formData.title}
                onChange={(event) => handleInputChange('title', event.target.value)}
                aria-required="true"
                aria-invalid={!!errors.title}
                aria-describedby={errors.title ? 'interview-title-error' : undefined}
                className={`w-full p-3 border rounded-control bg-card focus:ring-2 focus:ring-gold-500/60 focus:border-primary ${errors.title ? 'border-red-500' : 'border-border'}`}
                placeholder="e.g. Technical Interview - Senior Developer"
              />
              {errors.title && <p id="interview-title-error" role="alert" className="text-red-500 text-sm mt-1">{errors.title}</p>}
            </div>

            <div>
              <label htmlFor="interview-type" className="block text-sm font-medium text-foreground mb-1">
                Interview Type *
              </label>
              <select
                id="interview-type"
                value={formData.type}
                onChange={(event) => handleInputChange('type', event.target.value)}
                aria-required="true"
                className="w-full p-3 border border-border rounded-control bg-card focus:ring-2 focus:ring-gold-500/60 focus:border-primary"
              >
                {INTERVIEW_TYPES.map((type) => (
                  <option key={type.value} value={type.value}>{type.label}</option>
                ))}
              </select>
            </div>

            <div>
              <label htmlFor="interview-round" className="block text-sm font-medium text-foreground mb-1">
                Interview Round *
              </label>
              <select
                id="interview-round"
                value={formData.round}
                onChange={(event) => handleInputChange('round', event.target.value)}
                aria-required="true"
                className="w-full p-3 border border-border rounded-control bg-card focus:ring-2 focus:ring-gold-500/60 focus:border-primary"
              >
                {INTERVIEW_ROUNDS.map((round) => (
                  <option key={round.value} value={round.value}>{round.label}</option>
                ))}
              </select>
            </div>

            <div>
              <InterviewerMultiSelect
                selectedInterviewers={selectedInterviewers}
                onSelect={handleInterviewerSelect}
                onRemove={handleInterviewerRemove}
                error={errors.interviewerId}
                search={interviewerSearch}
                onSearchChange={handleInterviewerSearchChange}
                open={interviewerDropdownOpen}
                onOpenChange={setInterviewerDropdownOpen}
                adResults={adResults}
                searching={adSearching}
              />
            </div>
          </div>

          <fieldset className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <legend className="sr-only">Interview Scheduling</legend>
            <div>
              <label htmlFor="scheduled-at" className="block text-sm font-medium text-foreground mb-1">
                Date & Time *
              </label>
              <input
                type="datetime-local"
                id="scheduled-at"
                value={formData.scheduledAt}
                onChange={(event) => handleInputChange('scheduledAt', event.target.value)}
                onBlur={checkAvailability}
                aria-required="true"
                aria-invalid={!!errors.scheduledAt}
                aria-describedby={errors.scheduledAt ? 'scheduled-at-error' : undefined}
                className={`w-full p-3 border rounded-control bg-card focus:ring-2 focus:ring-gold-500/60 focus:border-primary ${errors.scheduledAt ? 'border-red-500' : 'border-border'}`}
              />
              {errors.scheduledAt && <p id="scheduled-at-error" role="alert" className="text-red-500 text-sm mt-1">{errors.scheduledAt}</p>}
              {checkingAvailability && (
                <p className="text-link text-sm mt-1">Checking availability...</p>
              )}
            </div>

            <div>
              <label htmlFor="duration-minutes" className="block text-sm font-medium text-foreground mb-1">
                Duration (minutes) *
              </label>
              <input
                type="number"
                id="duration-minutes"
                min="15"
                max="480"
                step="15"
                value={formData.durationMinutes}
                onChange={(event) => handleInputChange('durationMinutes', Number(event.target.value))}
                aria-required="true"
                aria-invalid={!!errors.durationMinutes}
                aria-describedby={errors.durationMinutes ? 'duration-minutes-error' : undefined}
                className={`w-full p-3 border rounded-control bg-card focus:ring-2 focus:ring-gold-500/60 focus:border-primary ${errors.durationMinutes ? 'border-red-500' : 'border-border'}`}
              />
              {errors.durationMinutes && <p id="duration-minutes-error" role="alert" className="text-red-500 text-sm mt-1">{errors.durationMinutes}</p>}
            </div>
          </fieldset>

          {availability.length > 0 && (
            <div>
              <label className="block text-sm font-medium text-foreground mb-1">
                Suggested Available Times
              </label>
              <div className="flex flex-wrap gap-2">
                {availability.map((time) => (
                  <button
                    key={time}
                    type="button"
                    onClick={() => handleSuggestedTimeSelect(time)}
                    className="px-3 py-1 bg-gold-100 text-gold-800 rounded-control hover:bg-gold-200 text-sm"
                  >
                    {new Date(time).toLocaleString()}
                  </button>
                ))}
              </div>
            </div>
          )}

          <div className="space-y-4">
            {(formData.type === 'IN_PERSON' || formData.type === 'PANEL' || formData.type === 'GROUP') && (
              <div>
                <label htmlFor="interview-location" className="block text-sm font-medium text-foreground mb-1">
                  Location *
                </label>
                <input
                  type="text"
                  id="interview-location"
                  value={formData.location}
                  onChange={(event) => handleInputChange('location', event.target.value)}
                  aria-required="true"
                  aria-invalid={!!errors.location}
                  aria-describedby={errors.location ? 'interview-location-error' : undefined}
                  className={`w-full p-3 border rounded-control bg-card focus:ring-2 focus:ring-gold-500/60 focus:border-primary ${errors.location ? 'border-red-500' : 'border-border'}`}
                  placeholder="e.g. Conference Room A, 2nd Floor"
                />
                {errors.location && <p id="interview-location-error" role="alert" className="text-red-500 text-sm mt-1">{errors.location}</p>}
              </div>
            )}

            {formData.type === 'VIDEO' && (
              <div>
                <label htmlFor="meeting-link" className="block text-sm font-medium text-foreground mb-1">
                  Video Meeting Link *
                </label>
                <input
                  type="url"
                  id="meeting-link"
                  value={formData.meetingLink}
                  onChange={(event) => handleInputChange('meetingLink', event.target.value)}
                  aria-required="true"
                  aria-invalid={!!errors.meetingLink}
                  aria-describedby={errors.meetingLink ? 'meeting-link-error' : undefined}
                  className={`w-full p-3 border rounded-control bg-card focus:ring-2 focus:ring-gold-500/60 focus:border-primary ${errors.meetingLink ? 'border-red-500' : 'border-border'}`}
                  placeholder="e.g. https://zoom.us/j/123456789"
                />
                {errors.meetingLink && <p id="meeting-link-error" role="alert" className="text-red-500 text-sm mt-1">{errors.meetingLink}</p>}
              </div>
            )}

            {formData.type === 'PHONE' && (
              <div>
                <label htmlFor="phone-number" className="block text-sm font-medium text-foreground mb-1">
                  Phone Number *
                </label>
                <input
                  type="tel"
                  id="phone-number"
                  value={formData.phoneNumber}
                  onChange={(event) => handleInputChange('phoneNumber', event.target.value)}
                  aria-required="true"
                  aria-invalid={!!errors.phoneNumber}
                  aria-describedby={errors.phoneNumber ? 'phone-number-error' : undefined}
                  className={`w-full p-3 border rounded-control bg-card focus:ring-2 focus:ring-gold-500/60 focus:border-primary ${errors.phoneNumber ? 'border-red-500' : 'border-border'}`}
                  placeholder="e.g. +27 11 123 4567"
                />
                {errors.phoneNumber && <p id="phone-number-error" role="alert" className="text-red-500 text-sm mt-1">{errors.phoneNumber}</p>}
              </div>
            )}

            {(formData.type === 'PANEL' || formData.type === 'GROUP') && (
              <div>
                <label className="block text-sm font-medium text-foreground mb-1">
                  Meeting Room
                </label>
                <input
                  type="text"
                  value={formData.meetingRoom}
                  onChange={(event) => handleInputChange('meetingRoom', event.target.value)}
                  className="w-full p-3 border border-border rounded-control bg-card focus:ring-2 focus:ring-gold-500/60 focus:border-primary"
                  placeholder="e.g. Boardroom 1"
                />
              </div>
            )}
          </div>

          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-foreground mb-1">
                Interview Agenda
              </label>
              <textarea
                value={formData.agenda}
                onChange={(event) => handleInputChange('agenda', event.target.value)}
                rows={3}
                className="w-full p-3 border border-border rounded-control bg-card focus:ring-2 focus:ring-gold-500/60 focus:border-primary"
                placeholder="Outline what will be covered in this interview"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-foreground mb-1">
                Instructions for Candidate
              </label>
              <textarea
                value={formData.instructions}
                onChange={(event) => handleInputChange('instructions', event.target.value)}
                rows={3}
                className="w-full p-3 border border-border rounded-control bg-card focus:ring-2 focus:ring-gold-500/60 focus:border-primary"
                placeholder="Any special instructions or preparation requirements"
              />
            </div>
          </div>
        </div>

        <div className="flex justify-end space-x-4 pt-6 mt-6 border-t border-border">
          {onCancel && (
            <button
              type="button"
              onClick={onCancel}
              className="px-6 py-2 border border-border text-foreground rounded-control hover:bg-accent"
            >
              Cancel
            </button>
          )}
          <button
            type="submit"
            disabled={loading}
            className="px-6 py-2 bg-cta text-cta-foreground rounded-full border border-cta-border hover:bg-cta-hover disabled:opacity-50 disabled:cursor-not-allowed font-semibold"
          >
            {loading ? (
              <span className="flex items-center">
                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-cta-foreground mr-2" />
                Saving...
              </span>
            ) : (
              interviewId ? 'Update Interview' : 'Schedule Interview'
            )}
          </button>
        </div>
      </form>
    </div>
  );
}
