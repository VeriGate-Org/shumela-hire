'use client';

import React from 'react';
import { RequisitionStatus } from '../types/workflow';
import { WORKFLOW_STATES, getWorkflowProgress } from '../services/workflowDefinition';

interface WorkflowStatusBadgeProps {
  status: RequisitionStatus;
  showIcon?: boolean;
  showProgress?: boolean;
  size?: 'sm' | 'md' | 'lg';
}

const WorkflowStatusBadge: React.FC<WorkflowStatusBadgeProps> = ({
  status,
  showIcon = true,
  showProgress = false,
  size = 'md'
}) => {
  const state = WORKFLOW_STATES[status];
  const progress = getWorkflowProgress(status);

  const sizeClasses = {
    sm: 'text-xs px-2 py-1',
    md: 'text-sm px-3 py-1',
    lg: 'text-base px-4 py-2'
  };

  const colorClasses: Record<string, string> = {
    gray: 'bg-gray-100 text-gray-800 border-gray-200',
    blue: 'bg-gold-100 text-gold-800 border-violet-200',
    yellow: 'bg-yellow-100 text-yellow-800 border-yellow-200',
    orange: 'bg-orange-100 text-orange-800 border-orange-200',
    purple: 'bg-purple-100 text-purple-800 border-purple-200',
    green: 'bg-green-100 text-green-800 border-green-200',
    red: 'bg-red-100 text-red-800 border-red-200'
  };

  return (
    <div className="flex flex-col items-center space-y-2">
      <span 
        className={`inline-flex items-center space-x-1 font-medium rounded-full border ${sizeClasses[size]} ${colorClasses[state.color]}`}
        title={state.description}
      >
        {showIcon && <span>{state.icon}</span>}
        <span>{state.name}</span>
      </span>
      
      {showProgress && (
        <div className="flex items-center space-x-2 w-full">
          <div className="flex-1 bg-gray-200 rounded-full h-2">
            <div 
              className={`h-2 rounded-full transition-all duration-300 ${
                progress === 100 && status === RequisitionStatus.APPROVED 
                  ? 'bg-green-500' 
                  : progress === 100 && status === RequisitionStatus.REJECTED
                  ? 'bg-red-500'
                  : 'bg-gold-500'
              }`}
              style={{ width: `${progress}%` }}
            />
          </div>
          <span className="text-xs text-gray-500 min-w-[3rem]">{progress}%</span>
        </div>
      )}
    </div>
  );
};

export default WorkflowStatusBadge;