import { useEffect, useState, useCallback } from 'react';
import { IndexedDBManager, OfflineAction } from '../utils/offlineStorage';

// Offline hook for managing offline functionality
export const useOffline = () => {
  const [isOnline, setIsOnline] = useState(navigator.onLine);
  const [offlineActions, setOfflineActions] = useState<OfflineAction[]>([]);
  const [storage] = useState(new IndexedDBManager());

  const loadOfflineActions = useCallback(async () => {
    try {
      const actions = await storage.getOfflineActions();
      setOfflineActions(actions);
    } catch (error) {
      console.error('Failed to load offline actions:', error);
    }
  }, [storage]);

  const processOfflineActions = useCallback(async () => {
    try {
      const actions = await storage.getOfflineActions();

      for (const action of actions) {
        try {
          const response = await fetch(action.url, {
            method: action.method,
            headers: action.headers,
            body: action.body
          });

          if (response.ok) {
            await storage.removeOfflineAction(action.id!);
            console.log('Successfully synced offline action:', action.type);
          } else {
            console.error('Failed to sync offline action:', action.type, response.status);
          }
        } catch (error) {
          console.error('Error syncing offline action:', action.type, error);
        }
      }

      await loadOfflineActions();
    } catch (error) {
      console.error('Failed to process offline actions:', error);
    }
  }, [storage, loadOfflineActions]);

  useEffect(() => {
    const handleOnline = async () => {
      setIsOnline(true);
      console.log('App is online - syncing offline actions');

      await processOfflineActions();
    };

    const handleOffline = () => {
      setIsOnline(false);
      console.log('App is offline');
    };

    window.addEventListener('online', handleOnline);
    window.addEventListener('offline', handleOffline);

    loadOfflineActions();

    return () => {
      window.removeEventListener('online', handleOnline);
      window.removeEventListener('offline', handleOffline);
    };
  }, [loadOfflineActions, processOfflineActions]);

  const storeOfflineAction = async (action: OfflineAction) => {
    try {
      await storage.storeOfflineAction(action);
      await loadOfflineActions();
      console.log('Stored offline action:', action.type);
    } catch (error) {
      console.error('Failed to store offline action:', error);
    }
  };

  const cacheData = async (key: string, data: any) => {
    try {
      await storage.store(key, data);
      console.log('Data cached:', key);
    } catch (error) {
      console.error('Failed to cache data:', error);
    }
  };

  const getCachedData = async (key: string) => {
    try {
      const data = await storage.get(key);
      return data;
    } catch (error) {
      console.error('Failed to get cached data:', error);
      return null;
    }
  };

  const clearOfflineData = async () => {
    try {
      await storage.clear();
      setOfflineActions([]);
      console.log('Offline data cleared');
    } catch (error) {
      console.error('Failed to clear offline data:', error);
    }
  };

  return {
    isOnline,
    offlineActions,
    storeOfflineAction,
    processOfflineActions,
    cacheData,
    getCachedData,
    clearOfflineData,
    storage
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
        // Try network request
        const response = await fetch(url, options);
        
        // Cache successful GET requests
        if (response.ok && options.method === 'GET' && cacheKey) {
          const data = await response.clone().json();
          await cacheData(cacheKey, data);
        }
        
        return response;
      } else {
        // Offline mode
        if (options.method === 'GET' && cacheKey) {
          // Try to serve from cache
          const cachedData = await getCachedData(cacheKey);
          if (cachedData) {
            return new Response(JSON.stringify(cachedData), {
              status: 200,
              statusText: 'OK (Cached)',
              headers: { 'Content-Type': 'application/json' }
            });
          }
        }

        // Store non-GET requests for later sync
        if (options.method !== 'GET') {
          await storeOfflineAction({
            type: `offline-${options.method?.toLowerCase() || 'request'}`,
            url,
            method: options.method || 'GET',
            headers: options.headers as Record<string, string> || {},
            body: options.body as string
          });

          // Return a success response for user feedback
          return new Response(
            JSON.stringify({ 
              success: true, 
              offline: true, 
              message: 'Action saved for sync when online' 
            }),
            {
              status: 202,
              statusText: 'Accepted (Offline)',
              headers: { 'Content-Type': 'application/json' }
            }
          );
        }

        // No cache available for GET request
        throw new Error('No cached data available offline');
      }
    } catch (error) {
      if (!isOnline) {
        // Store failed request for retry
        if (options.method !== 'GET') {
          await storeOfflineAction({
            type: `offline-${options.method?.toLowerCase() || 'request'}`,
            url,
            method: options.method || 'GET',
            headers: options.headers as Record<string, string> || {},
            body: options.body as string
          });
        }
      }
      
      throw error;
    }
  };

  return { makeRequest, isOnline };
};
