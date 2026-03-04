'use client';

import React, { useState, useEffect } from 'react';
import { useParams, useRouter, useSearchParams } from 'next/navigation';
import { useAuth } from '@/contexts/AuthContext';
import { apiFetch } from '@/lib/api-fetch';
import { useToast } from '@/components/Toast';
import Link from 'next/link';
import {
  ArrowLeftIcon,
  PaperAirplaneIcon,
  CheckCircleIcon,
  ExclamationTriangleIcon,
  DocumentTextIcon,
  UserIcon,
  BuildingOfficeIcon
} from '@heroicons/react/24/outline';

interface ApplicationFormData {
  coverLetter: string;
  reasonForApplication: string;
  availabilityDate: string;
}

export default function InternalApplicationPage() {
  const params = useParams();
  const searchParams = useSearchParams();
  const router = useRouter();
  const { user, isAuthenticated } = useAuth();
  const { toast } = useToast();

  const requisitionId = params.requisitionId as string;
  const jobId = searchParams.get('jobId');
  const jobTitle = searchParams.get('title') || 'Position';

  const [loading, setLoading] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [formData, setFormData] = useState<ApplicationFormData>({
    coverLetter: '',
    reasonForApplication: '',
    availabilityDate: '',
  });

  // Redirect if not authenticated
  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }
  }, [isAuthenticated, router]);

  const handleInputChange = (field: keyof ApplicationFormData, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      setLoading(true);
      setError(null);

      // Combine reasonForApplication and coverLetter for the backend coverLetter field
      const coverLetterParts = [formData.reasonForApplication, formData.coverLetter].filter(Boolean);
      const combinedCoverLetter = coverLetterParts.join('\n\n');

      const response = await apiFetch('/api/applications', {
        method: 'POST',
        body: JSON.stringify({
          jobAdId: jobId ? Number(jobId) : undefined,
          applicationSource: 'INTERNAL',
          coverLetter: combinedCoverLetter || undefined,
          availabilityDate: formData.availabilityDate || undefined,
        }),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.message || errorData?.error || 'Failed to submit application');
      }

      setSubmitted(true);
      toast('Application submitted successfully', 'success');
    } catch (err) {
      console.error('Error submitting application:', err);
      const message = err instanceof Error ? err.message : 'Failed to submit application';
      setError(message);
      toast(message, 'error');
    } finally {
      setLoading(false);
    }
  };

  if (!isAuthenticated) {
    return null; // Will redirect to login
  }

  if (submitted) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="max-w-md mx-auto px-4">
          <div className="bg-white rounded-sm shadow-lg p-8 text-center">
            <CheckCircleIcon className="w-16 h-16 text-green-500 mx-auto mb-4" />
            <h1 className="text-2xl font-bold text-gray-900 mb-2">Application Submitted!</h1>
            <p className="text-gray-600 mb-6">
              Your internal application for <strong>{decodeURIComponent(jobTitle)}</strong> has been submitted successfully.
            </p>
            <div className="bg-gold-50 border border-violet-200 rounded-sm p-4 mb-6">
              <p className="text-sm text-violet-800">
                <strong>What happens next?</strong>
                <br />
                • Your application will be prioritized as an internal candidate
                <br />
                • HR will review your application within 3-5 business days
                <br />
                • You&apos;ll receive updates via email and the internal portal
              </p>
            </div>
            <div className="space-y-3">
              <Link href="/internal/jobs">
                <button className="w-full bg-gold-500 text-violet-950 px-4 py-2 rounded-full hover:bg-gold-600 transition-colors">
                  Browse More Internal Jobs
                </button>
              </Link>
              <Link href="/applicant/applications">
                <button className="w-full border border-gray-300 text-gray-700 px-4 py-2 rounded-full hover:bg-gray-50 transition-colors">
                  View My Applications
                </button>
              </Link>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white shadow border-b border-gray-200">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="py-6">
            <div className="flex items-center justify-between">
              <Link href="/internal/jobs">
                <button className="inline-flex items-center text-gold-600 hover:text-gold-800 transition-colors">
                  <ArrowLeftIcon className="w-4 h-4 mr-2" />
                  Back to Job Board
                </button>
              </Link>
              
              <div className="text-sm text-gray-500">
                Internal Application Portal
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="bg-white rounded-sm shadow-lg">
          <div className="px-8 py-6 border-b border-gray-200">
            <h1 className="text-2xl font-bold text-gray-900">Internal Job Application</h1>
            <p className="text-gray-600 mt-2">
              Applying for: <strong>{decodeURIComponent(jobTitle)}</strong>
            </p>
            <p className="text-sm text-gray-500 mt-1">
              Requisition ID: {requisitionId}
            </p>
          </div>

          {/* Internal Application Benefits */}
          <div className="px-8 py-6 bg-gold-50 border-b border-gray-200">
            <h2 className="text-lg font-semibold text-violet-900 dark:text-gold-400 mb-3">Internal Application Advantages</h2>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="flex items-center">
                <div className="w-8 h-8 bg-gold-500 rounded-full flex items-center justify-center mr-3">
                  <UserIcon className="w-4 h-4 text-violet-950" />
                </div>
                <span className="text-sm text-violet-800">Priority Review</span>
              </div>
              <div className="flex items-center">
                <div className="w-8 h-8 bg-green-600 rounded-full flex items-center justify-center mr-3">
                  <BuildingOfficeIcon className="w-4 h-4 text-white" />
                </div>
                <span className="text-sm text-violet-800">Known Performance</span>
              </div>
              <div className="flex items-center">
                <div className="w-8 h-8 bg-purple-600 rounded-full flex items-center justify-center mr-3">
                  <DocumentTextIcon className="w-4 h-4 text-white" />
                </div>
                <span className="text-sm text-violet-800">Faster Process</span>
              </div>
            </div>
          </div>

          {/* Error Display */}
          {error && (
            <div className="px-8 py-4 bg-red-50 border-b border-gray-200">
              <div className="flex">
                <ExclamationTriangleIcon className="w-5 h-5 text-red-400" />
                <div className="ml-3">
                  <h3 className="text-sm font-medium text-red-800">Application Error</h3>
                  <p className="text-sm text-red-700 mt-1">{error}</p>
                </div>
              </div>
            </div>
          )}

          {/* Application Form */}
          <form onSubmit={handleSubmit} className="px-8 py-6">
            <div className="space-y-6">
              {/* Applicant Info (read-only) */}
              {user && (
                <div>
                  <h3 className="text-lg font-semibold text-gray-900 mb-4">Your Details</h3>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Name</label>
                      <p className="px-3 py-2 bg-gray-50 border border-gray-200 rounded-sm text-gray-700">{user.name}</p>
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Email</label>
                      <p className="px-3 py-2 bg-gray-50 border border-gray-200 rounded-sm text-gray-700">{user.email}</p>
                    </div>
                  </div>
                </div>
              )}

              {/* Application Details */}
              <div>
                <h3 className="text-lg font-semibold text-gray-900 mb-4">Application Details</h3>

                <div className="space-y-4">
                  <div>
                    <label htmlFor="availability-date" className="block text-sm font-medium text-gray-700 mb-2">
                      Available Start Date
                    </label>
                    <input
                      type="date"
                      id="availability-date"
                      value={formData.availabilityDate}
                      onChange={(e) => handleInputChange('availabilityDate', e.target.value)}
                      className="w-full border border-gray-300 rounded-sm px-3 py-2 focus:ring-2 focus:ring-gold-500/60 focus:border-violet-400"
                    />
                  </div>

                  <div>
                    <label htmlFor="reason-for-application" className="block text-sm font-medium text-gray-700 mb-2">
                      Reason for Application *
                    </label>
                    <textarea
                      id="reason-for-application"
                      rows={4}
                      required
                      aria-required="true"
                      value={formData.reasonForApplication}
                      onChange={(e) => handleInputChange('reasonForApplication', e.target.value)}
                      className="w-full border border-gray-300 rounded-sm px-3 py-2 focus:ring-2 focus:ring-gold-500/60 focus:border-violet-400"
                      placeholder="Why are you interested in this position? What motivates you to make this internal move?"
                    />
                  </div>

                  <div>
                    <label htmlFor="cover-letter" className="block text-sm font-medium text-gray-700 mb-2">
                      Cover Letter
                    </label>
                    <textarea
                      id="cover-letter"
                      rows={6}
                      value={formData.coverLetter}
                      onChange={(e) => handleInputChange('coverLetter', e.target.value)}
                      className="w-full border border-gray-300 rounded-sm px-3 py-2 focus:ring-2 focus:ring-gold-500/60 focus:border-violet-400"
                      placeholder="Describe how your current experience and skills make you a great fit for this role..."
                    />
                  </div>
                </div>
              </div>

              {/* Submission */}
              <div className="border-t border-gray-200 pt-6">
                <div className="flex items-center justify-between">
                  <p className="text-sm text-gray-600">
                    By submitting this application, you confirm that all information provided is accurate.
                  </p>

                  <button
                    type="submit"
                    disabled={loading || !formData.reasonForApplication}
                    className="inline-flex items-center px-6 py-3 bg-gold-500 text-violet-950 font-medium rounded-full hover:bg-gold-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                  >
                    {loading ? (
                      <>
                        <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin mr-2"></div>
                        Submitting...
                      </>
                    ) : (
                      <>
                        <PaperAirplaneIcon className="w-5 h-5 mr-2" />
                        Submit Application
                      </>
                    )}
                  </button>
                </div>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}