'use client';

import React, { useState } from 'react';

interface JobPostingWorkflowProps {
  jobPosting: {
    id: number;
    title: string;
    department: string;
    status: string;
    statusDisplayName: string;
    statusCssClass: string;
    statusIcon: string;
    canBeSubmittedForApproval: boolean;
    canBeApproved: boolean;
    canBeRejected: boolean;
    canBePublished: boolean;
    canBeUnpublished: boolean;
    canBeClosed: boolean;
    createdAt: string;
    submittedForApprovalAt?: string;
    approvedAt?: string;
    publishedAt?: string;
    unpublishedAt?: string;
    closedAt?: string;
    approvalNotes?: string;
    rejectionReason?: string;
    createdBy: number;
    approvedBy?: number;
    publishedBy?: number;
    daysFromCreation: number;
    daysFromPublication: number;
    applicationsCount: number;
    viewsCount: number;
  };
  onStatusChange?: (jobPostingId: number, newStatus: string) => void;
  currentUserId?: number;
}

export default function JobPostingWorkflow({ jobPosting, onStatusChange, currentUserId = 1 }: JobPostingWorkflowProps) {
  const [showApprovalForm, setShowApprovalForm] = useState(false);
  const [showRejectionForm, setShowRejectionForm] = useState(false);
  const [approvalNotes, setApprovalNotes] = useState('');
  const [rejectionReason, setRejectionReason] = useState('');
  const [loading, setLoading] = useState<string | null>(null);

  const workflowSteps = [
    { key: 'DRAFT', label: 'Draft', icon: '📝', description: 'Job posting is being created' },
    { key: 'PENDING_APPROVAL', label: 'Pending Approval', icon: '⏳', description: 'Waiting for approval' },
    { key: 'APPROVED', label: 'Approved', icon: '✅', description: 'Approved and ready to publish' },
    { key: 'PUBLISHED', label: 'Published', icon: '🌐', description: 'Live and accepting applications' },
    { key: 'CLOSED', label: 'Closed', icon: '🔒', description: 'No longer accepting applications' }
  ];

  const getCurrentStepIndex = () => {
    return workflowSteps.findIndex(step => step.key === jobPosting.status);
  };

  const isStepCompleted = (stepIndex: number) => {
    const currentIndex = getCurrentStepIndex();
    return stepIndex < currentIndex && !isTerminalStatus();
  };

  const isStepCurrent = (stepIndex: number) => {
    return stepIndex === getCurrentStepIndex() && !isTerminalStatus();
  };

  const isTerminalStatus = () => {
    return ['REJECTED', 'CANCELLED', 'CLOSED'].includes(jobPosting.status);
  };

  const getTerminalStatusInfo = () => {
    switch (jobPosting.status) {
      case 'REJECTED':
        return {
          icon: '❌',
          title: 'Job Posting Rejected',
          description: jobPosting.rejectionReason || 'Job posting was rejected during approval process',
          color: 'text-red-600',
          bgColor: 'bg-red-50'
        };
      case 'CANCELLED':
        return {
          icon: '🚫',
          title: 'Job Posting Cancelled',
          description: 'Job posting was cancelled',
          color: 'text-red-600',
          bgColor: 'bg-red-50'
        };
      case 'UNPUBLISHED':
        return {
          icon: '⏸️',
          title: 'Job Posting Unpublished',
          description: 'Job posting was temporarily unpublished',
          color: 'text-orange-600',
          bgColor: 'bg-orange-50'
        };
      default:
        return null;
    }
  };

  const handleWorkflowAction = async (action: string, notes?: string) => {
    try {
      setLoading(action);
      
      let url = `/api/job-postings/${jobPosting.id}/${action}?`;
      
      switch (action) {
        case 'submit-for-approval':
          url += `submittedBy=${currentUserId}`;
          break;
        case 'approve':
          url += `approvedBy=${currentUserId}`;
          if (notes) url += `&approvalNotes=${encodeURIComponent(notes)}`;
          break;
        case 'reject':
          url += `rejectedBy=${currentUserId}&rejectionReason=${encodeURIComponent(notes || '')}`;
          break;
        case 'publish':
          url += `publishedBy=${currentUserId}`;
          break;
        case 'unpublish':
          url += `unpublishedBy=${currentUserId}`;
          break;
        case 'close':
          url += `closedBy=${currentUserId}`;
          break;
      }

      const response = await fetch(url, { method: 'POST' });

      if (response.ok) {
        const updatedJobPosting = await response.json();
        if (onStatusChange) {
          onStatusChange(jobPosting.id, updatedJobPosting.status);
        }
        
        // Reset forms
        setShowApprovalForm(false);
        setShowRejectionForm(false);
        setApprovalNotes('');
        setRejectionReason('');
      } else {
        const errorData = await response.json();
        alert(errorData.message || `Failed to ${action.replace('-', ' ')}`);
      }
    } catch (error) {
      console.error(`Error performing ${action}:`, error);
      alert(`An error occurred while performing ${action.replace('-', ' ')}`);
    } finally {
      setLoading(null);
    }
  };

  const terminalInfo = getTerminalStatusInfo();

  return (
    <div className="bg-white rounded-lg shadow-lg p-6">
      {/* Header */}
      <div className="mb-6">
        <div className="flex justify-between items-start">
          <div>
            <h3 className="text-lg font-medium text-gray-900">{jobPosting.title}</h3>
            <p className="text-gray-600">{jobPosting.department}</p>
          </div>
          <div className="text-right">
            <span className={`inline-flex items-center px-2 py-1 text-xs font-medium rounded-full ${jobPosting.statusCssClass}`}>
              <span className="mr-1">{jobPosting.statusIcon}</span>
              {jobPosting.statusDisplayName}
            </span>
            <p className="text-sm text-gray-500 mt-1">
              Created {jobPosting.daysFromCreation} days ago
            </p>
          </div>
        </div>
      </div>

      {/* Analytics */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
        <div className="bg-blue-50 rounded-lg p-4">
          <div className="flex items-center">
            <span className="text-2xl mr-3">👁️</span>
            <div>
              <p className="text-sm font-medium text-blue-900">Views</p>
              <p className="text-lg font-bold text-blue-600">{jobPosting.viewsCount}</p>
            </div>
          </div>
        </div>
        
        <div className="bg-green-50 rounded-lg p-4">
          <div className="flex items-center">
            <span className="text-2xl mr-3">📄</span>
            <div>
              <p className="text-sm font-medium text-green-900">Applications</p>
              <p className="text-lg font-bold text-green-600">{jobPosting.applicationsCount}</p>
            </div>
          </div>
        </div>
        
        <div className="bg-purple-50 rounded-lg p-4">
          <div className="flex items-center">
            <span className="text-2xl mr-3">📅</span>
            <div>
              <p className="text-sm font-medium text-purple-900">Days Published</p>
              <p className="text-lg font-bold text-purple-600">
                {jobPosting.status === 'PUBLISHED' ? jobPosting.daysFromPublication : 'Not published'}
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Terminal Status Display */}
      {terminalInfo && (
        <div className={`${terminalInfo.bgColor} border rounded-lg p-4 mb-6`}>
          <div className="flex items-center">
            <span className="text-2xl mr-3">{terminalInfo.icon}</span>
            <div>
              <h4 className={`font-medium ${terminalInfo.color}`}>{terminalInfo.title}</h4>
              <p className="text-gray-700 text-sm">{terminalInfo.description}</p>
            </div>
          </div>
        </div>
      )}

      {/* Workflow Progress - Only show for active job postings */}
      {!isTerminalStatus() && jobPosting.status !== 'UNPUBLISHED' && (
        <div className="mb-6">
          <h4 className="font-medium text-gray-900 mb-4">Workflow Progress</h4>
          <div className="space-y-4">
            {workflowSteps.map((step, index) => (
              <div key={step.key} className="flex items-center">
                <div className="flex-shrink-0">
                  <div className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-medium ${
                    isStepCompleted(index) ? 'bg-green-500 text-white' :
                    isStepCurrent(index) ? 'bg-blue-500 text-white' :
                    'bg-gray-200 text-gray-600'
                  }`}>
                    {isStepCompleted(index) ? '✓' : step.icon}
                  </div>
                </div>
                <div className="ml-4 flex-1">
                  <div className="flex items-center justify-between">
                    <p className={`text-sm font-medium ${
                      isStepCompleted(index) || isStepCurrent(index) ? 'text-gray-900' : 'text-gray-500'
                    }`}>
                      {step.label}
                    </p>
                    {isStepCurrent(index) && (
                      <span className="text-xs bg-blue-100 text-blue-800 px-2 py-1 rounded">
                        Current
                      </span>
                    )}
                  </div>
                  <p className="text-xs text-gray-600">{step.description}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Workflow Actions */}
      <div className="space-y-4">
        {jobPosting.canBeSubmittedForApproval && (
          <button
            onClick={() => handleWorkflowAction('submit-for-approval')}
            disabled={loading === 'submit-for-approval'}
            className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 disabled:opacity-50"
          >
            {loading === 'submit-for-approval' ? 'Submitting...' : 'Submit for Approval'}
          </button>
        )}

        {jobPosting.canBeApproved && (
          <div className="space-y-2">
            {!showApprovalForm ? (
              <div className="flex space-x-2">
                <button
                  onClick={() => setShowApprovalForm(true)}
                  className="flex-1 py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-green-600 hover:bg-green-700"
                >
                  Approve Job Posting
                </button>
                <button
                  onClick={() => setShowRejectionForm(true)}
                  className="flex-1 py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
                >
                  Reject Job Posting
                </button>
              </div>
            ) : (
              <div className="border rounded-md p-4">
                <h5 className="font-medium text-gray-900 mb-2">Approve Job Posting</h5>
                <textarea
                  value={approvalNotes}
                  onChange={(e) => setApprovalNotes(e.target.value)}
                  placeholder="Optional approval notes..."
                  className="w-full p-2 border border-gray-300 rounded-md"
                  rows={3}
                />
                <div className="flex justify-end space-x-2 mt-3">
                  <button
                    onClick={() => setShowApprovalForm(false)}
                    className="px-3 py-1 text-gray-600 hover:text-gray-800"
                  >
                    Cancel
                  </button>
                  <button
                    onClick={() => handleWorkflowAction('approve', approvalNotes)}
                    disabled={loading === 'approve'}
                    className="px-4 py-1 bg-green-600 text-white rounded hover:bg-green-700 disabled:opacity-50"
                  >
                    {loading === 'approve' ? 'Approving...' : 'Confirm Approval'}
                  </button>
                </div>
              </div>
            )}
          </div>
        )}

        {jobPosting.canBeRejected && showRejectionForm && (
          <div className="border rounded-md p-4">
            <h5 className="font-medium text-gray-900 mb-2">Reject Job Posting</h5>
            <textarea
              value={rejectionReason}
              onChange={(e) => setRejectionReason(e.target.value)}
              placeholder="Please provide a reason for rejection..."
              className="w-full p-2 border border-gray-300 rounded-md"
              rows={3}
              required
            />
            <div className="flex justify-end space-x-2 mt-3">
              <button
                onClick={() => setShowRejectionForm(false)}
                className="px-3 py-1 text-gray-600 hover:text-gray-800"
              >
                Cancel
              </button>
              <button
                onClick={() => handleWorkflowAction('reject', rejectionReason)}
                disabled={loading === 'reject' || !rejectionReason.trim()}
                className="px-4 py-1 bg-red-600 text-white rounded hover:bg-red-700 disabled:opacity-50"
              >
                {loading === 'reject' ? 'Rejecting...' : 'Confirm Rejection'}
              </button>
            </div>
          </div>
        )}

        {jobPosting.canBePublished && (
          <button
            onClick={() => handleWorkflowAction('publish')}
            disabled={loading === 'publish'}
            className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-green-600 hover:bg-green-700 disabled:opacity-50"
          >
            {loading === 'publish' ? 'Publishing...' : 'Publish Job Posting'}
          </button>
        )}

        {jobPosting.canBeUnpublished && (
          <button
            onClick={() => handleWorkflowAction('unpublish')}
            disabled={loading === 'unpublish'}
            className="w-full flex justify-center py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
          >
            {loading === 'unpublish' ? 'Unpublishing...' : 'Unpublish Job Posting'}
          </button>
        )}

        {jobPosting.canBeClosed && (
          <button
            onClick={() => handleWorkflowAction('close')}
            disabled={loading === 'close'}
            className="w-full flex justify-center py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
          >
            {loading === 'close' ? 'Closing...' : 'Close Job Posting'}
          </button>
        )}
      </div>

      {/* Timeline Information */}
      <div className="mt-6 pt-6 border-t border-gray-200">
        <h4 className="font-medium text-gray-900 mb-2">Timeline</h4>
        <div className="text-sm text-gray-600 space-y-1">
          <p>• Created: {new Date(jobPosting.createdAt).toLocaleDateString()}</p>
          {jobPosting.submittedForApprovalAt && (
            <p>• Submitted for approval: {new Date(jobPosting.submittedForApprovalAt).toLocaleDateString()}</p>
          )}
          {jobPosting.approvedAt && (
            <p>• Approved: {new Date(jobPosting.approvedAt).toLocaleDateString()}</p>
          )}
          {jobPosting.publishedAt && (
            <p>• Published: {new Date(jobPosting.publishedAt).toLocaleDateString()}</p>
          )}
          {jobPosting.unpublishedAt && (
            <p>• Unpublished: {new Date(jobPosting.unpublishedAt).toLocaleDateString()}</p>
          )}
          {jobPosting.closedAt && (
            <p>• Closed: {new Date(jobPosting.closedAt).toLocaleDateString()}</p>
          )}
        </div>
      </div>
    </div>
  );
}