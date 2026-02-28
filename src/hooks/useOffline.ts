'use client';

import { useEffect, useState, useCallback, useRef } from 'react';
import { IndexedDBManager, OfflineAction, SyncResult } from '../utils/offlineStorage';

// Guard against SSR – IndexedDB is browser-only
const isBrowser = typeof window !== 'undefined' && typeof indexedDB !== 'undefined';

// Offline hook for managing offline functionality
export const useOffline = () => {
  const [isOnline, setIsOnline] = useState(() => (isBrowser ? navigator.onLine : true));
  const [offlineActions, setOfflineActions] = useState<OfflineAction[]>([]);
  const [conflicts, setConflicts] = useState<Awaited<ReturnType<IndexedDBManager['getUnacknowledgedConflicts']>>>([]);
  const storageRef = useRef<IndexedDBManager | null>(null);

  // Lazily initialise the storage singleton (browser only)
  const getStorage = useCallback((): IndexedDBManager => {
    if (!isBrowser) throw new Error('IndexedDB is not available in this environment');
    if (!storageRef.current) storageRef.current = new IndexedDBManager();
    return storageRef.current;
  }, []);

  const loadOfflineActions = useCallback(async () => {
    if (!isBrowser) return;
    try {
      const storage = getStorage();
      const actions = await storage.getOfflineActions();
      setOfflineActions(actions);
    } catch (error) {
      console.error('Failed to load offline actions:', error);
    }
  }, [getStorage]);

  const loadConflicts = useCallback(async () => {
    if (!isBrowser) return;
    try {
      const storage = getStorage();
      const unacked = await storage.getUnacknowledgedConflicts();
      setConflicts(unacked);
    } catch (error) {
      console.error('Failed to load conflicts:', error);
    }
  }, [getStorage]);

  /**
   * Sync all queued offline actions using a server-wins conflict resolution
   * strategy (spec F-5.4.3). On a 409 Conflict response the server state is
   * stored in the conflict log and the user is notified via `conflicts` state.
   */
  const processOfflineActions = useCallback(async (): Promise<SyncResult[]> => {
    if (!isBrowser) return [];
    const storage = getStorage();
    const results: SyncResult[] = [];

    try {
      const actions = await storage.getOfflineActions();

      for (const action of actions) {
        try {
          const response = await fetch(action.url, {
            method: action.method,
            headers: action.headers,
            body: action.body,
          });

          if (response.ok) {
            await storage.removeOfflineAction(action.id!);
            results.push({ action, success: true, conflict: false });
          } else if (response.status === 409) {
            // Server-wins: log conflict, discard local action
            let serverState: unknown = null;
            try { serverState = await response.clone().json(); } catch { /* ignore */ }

            await storage.logConflict({
              actionType: action.type,
              localSnapshot: action.localSnapshot ?? null,
              serverState,
              url: action.url,
            });
            await storage.removeOfflineAction(action.id!);
            results.push({ action, success: false, conflict: true, serverState });
          } else {
            console.error('Failed to sync offline action:', action.type, response.status);
            results.push({ action, success: false, conflict: false, error: `HTTP ${response.status}` });
          }
        } catch (error) {
          console.error('Error syncing offline action:', action.type, error);
          results.push({ action, success: false, conflict: false, error: String(error) });
        }
      }

      await loadOfflineActions();
      await loadConflicts();
    } catch (error) {
      console.error('Failed to process offline actions:', error);
    }

    return results;
  }, [getStorage, loadOfflineActions, loadConflicts]);

  useEffect(() => {
    if (!isBrowser) return;

    const handleOnline = async () => {
      setIsOnline(true);
      await processOfflineActions();
    };

    const handleOffline = () => {
      setIsOnline(false);
    };

    window.addEventListener('online', handleOnline);
    window.addEventListener('offline', handleOffline);

    loadOfflineActions();
    loadConflicts();

    return () => {
      window.removeEventListener('online', handleOnline);
      window.removeEventListener('offline', handleOffline);
    };
  }, [loadOfflineActions, loadConflicts, processOfflineActions]);

  const storeOfflineAction = async (action: OfflineAction) => {
    if (!isBrowser) return;
    try {
      const storage = getStorage();
      await storage.storeOfflineAction(action);
      await loadOfflineActions();
    } catch (error) {
      console.error('Failed to store offline action:', error);
    }
  };

  const cacheData = async (key: string, data: unknown) => {
    if (!isBrowser) return;
    try {
      const storage = getStorage();
      await storage.store(key, data);
    } catch (error) {
      console.error('Failed to cache data:', error);
    }
  };

  const getCachedData = async (key: string) => {
    if (!isBrowser) return null;
    try {
      const storage = getStorage();
      return await storage.get(key);
    } catch (error) {
      console.error('Failed to get cached data:', error);
      return null;
    }
  };

  const clearOfflineData = async () => {
    if (!isBrowser) return;
    try {
      const storage = getStorage();
      await storage.clear();
      setOfflineActions([]);
    } catch (error) {
      console.error('Failed to clear offline data:', error);
    }
  };

  const acknowledgeConflict = async (id: number) => {
    if (!isBrowser) return;
    try {
      const storage = getStorage();
      await storage.acknowledgeConflict(id);
      await loadConflicts();
    } catch (error) {
      console.error('Failed to acknowledge conflict:', error);
    }
  };

  // ---------------------------------------------------------------------------
  // HR-specific offline action helpers (spec F-5.4.3)
  // ---------------------------------------------------------------------------

  /** Queue an offline leave request for later sync. */
  const queueLeaveRequest = (payload: {
    employeeId: string;
    leaveType: string;
    startDate: string;
    endDate: string;
    reason?: string;
    localSnapshot?: unknown;
  }) =>
    storeOfflineAction({
      type: 'leave-request',
      url: '/api/leave/requests',
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
      localSnapshot: payload.localSnapshot,
    });

  /** Queue an offline clock-in event. */
  const queueClockIn = (payload: {
    employeeId: string;
    timestamp: string;
    locationLat?: number;
    locationLng?: number;
  }) =>
    storeOfflineAction({
      type: 'clock-in',
      url: '/api/attendance/clock-in',
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });

  /** Queue an offline clock-out event. */
  const queueClockOut = (payload: {
    employeeId: string;
    timestamp: string;
    locationLat?: number;
    locationLng?: number;
  }) =>
    storeOfflineAction({
      type: 'clock-out',
      url: '/api/attendance/clock-out',
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });

  /** Queue an offline approval action (approve or reject). */
  const queueApprovalAction = (
    action: 'approve' | 'reject',
    payload: { requestId: string; comments?: string; localSnapshot?: unknown }
  ) =>
    storeOfflineAction({
      type: action === 'approve' ? 'approval-approve' : 'approval-reject',
      url: `/api/approvals/${payload.requestId}/${action}`,
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ comments: payload.comments }),
      localSnapshot: payload.localSnapshot,
    });

  return {
    isOnline,
    offlineActions,
    conflicts,
    storeOfflineAction,
    processOfflineActions,
    cacheData,
    getCachedData,
    clearOfflineData,
    acknowledgeConflict,
    queueLeaveRequest,
    queueClockIn,
    queueClockOut,
    queueApprovalAction,
    storage: isBrowser ? getStorage() : null,
  };
};

// Offline API hook for making network requests with offline support
export const useOfflineAPI = () => {
  const { isOnline, storeOfflineAction, getCachedData, cacheData } = useOffline();

  const makeRequest = async (
    url: string,
    options: RequestInit = {},
    cacheKey?: string
  ): Promise<Response> => {
    try {
      if (isOnline) {
        const response = await fetch(url, options);

        // Cache successful GET requests
        if (response.ok && (!options.method || options.method === 'GET') && cacheKey) {
          const data = await response.clone().json();
          await cacheData(cacheKey, data);
        }

        return response;
      } else {
        // Offline mode
        if ((!options.method || options.method === 'GET') && cacheKey) {
          const cachedData = await getCachedData(cacheKey);
          if (cachedData) {
            return new Response(JSON.stringify(cachedData), {
              status: 200,
              statusText: 'OK (Cached)',
              headers: { 'Content-Type': 'application/json' },
            });
          }
        }

        // Store non-GET requests for later sync
        if (options.method && options.method !== 'GET') {
          await storeOfflineAction({
            type: `offline-${options.method.toLowerCase()}` as OfflineAction['type'],
            url,
            method: options.method,
            headers: (options.headers as Record<string, string>) || {},
            body: options.body as string,
          });

          return new Response(
            JSON.stringify({
              success: true,
              offline: true,
              message: 'Action saved for sync when online',
            }),
            {
              status: 202,
              statusText: 'Accepted (Offline)',
              headers: { 'Content-Type': 'application/json' },
            }
          );
        }

        throw new Error('No cached data available offline');
      }
    } catch (error) {
      if (!isOnline && options.method && options.method !== 'GET') {
        await storeOfflineAction({
          type: `offline-${options.method.toLowerCase()}` as OfflineAction['type'],
          url,
          method: options.method,
          headers: (options.headers as Record<string, string>) || {},
          body: options.body as string,
        });
      }

      throw error;
    }
  };

  return { makeRequest, isOnline };
};
