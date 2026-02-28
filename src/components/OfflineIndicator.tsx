'use client';

import React from 'react';
import { useOffline } from '../hooks/useOffline';

// Component for displaying offline status, queued actions, and conflict notifications
export const OfflineIndicator: React.FC = () => {
  const { isOnline, offlineActions, conflicts, clearOfflineData, acknowledgeConflict } = useOffline();

  const hasConflicts = conflicts.length > 0;

  if (isOnline && offlineActions.length === 0 && !hasConflicts) return null;

  return (
    <div className="fixed bottom-4 left-4 z-50 max-w-sm space-y-2">
      {/* Connection / sync status */}
      {(!isOnline || offlineActions.length > 0) && (
        <div className={`rounded-sm p-4 shadow-lg border ${
          isOnline ? 'bg-green-50 border-green-200' : 'bg-orange-50 border-orange-200'
        }`}>
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-2">
              <div className={`w-3 h-3 rounded-full ${
                isOnline ? 'bg-green-500' : 'bg-orange-500 animate-pulse'
              }`} />
              <span className={`text-sm font-medium ${
                isOnline ? 'text-green-800' : 'text-orange-800'
              }`}>
                {isOnline ? 'Online' : 'Offline'}
              </span>
            </div>

            {offlineActions.length > 0 && (
              <span className={`text-xs px-2 py-1 rounded-full ${
                isOnline
                  ? 'bg-blue-100 text-blue-800'
                  : 'bg-orange-100 text-orange-800'
              }`}>
                {offlineActions.length} pending
              </span>
            )}
          </div>

          {!isOnline && (
            <p className="text-xs text-orange-600 mt-2">
              You are offline. Actions will sync automatically when your connection is restored.
            </p>
          )}

          {offlineActions.length > 0 && (
            <div className="mt-3 flex items-center justify-between">
              <span className="text-xs text-gray-600">
                {offlineActions.length} action{offlineActions.length !== 1 ? 's' : ''} queued for sync
              </span>
              <button
                onClick={clearOfflineData}
                className="text-xs text-red-600 hover:text-red-800"
              >
                Clear
              </button>
            </div>
          )}
        </div>
      )}

      {/* Conflict notifications (server-wins) */}
      {hasConflicts && (
        <div className="rounded-sm p-4 shadow-lg border bg-yellow-50 border-yellow-300">
          <div className="flex items-center space-x-2 mb-2">
            <svg className="w-4 h-4 text-yellow-600 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20" aria-hidden="true">
              <path fillRule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
            </svg>
            <span className="text-sm font-medium text-yellow-800">
              {conflicts.length} sync conflict{conflicts.length !== 1 ? 's' : ''} resolved
            </span>
          </div>
          <p className="text-xs text-yellow-700 mb-3">
            Some offline changes were overridden by a newer server version. The server state has been kept.
          </p>
          <div className="space-y-1">
            {conflicts.slice(0, 3).map((c) => (
              <div key={c.id} className="flex items-center justify-between text-xs text-yellow-800">
                <span className="truncate mr-2">{c.actionType}</span>
                <button
                  onClick={() => acknowledgeConflict(c.id)}
                  className="flex-shrink-0 underline hover:no-underline"
                >
                  Dismiss
                </button>
              </div>
            ))}
            {conflicts.length > 3 && (
              <p className="text-xs text-yellow-600">+{conflicts.length - 3} more</p>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default OfflineIndicator;
