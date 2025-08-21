'use client';

import { useEffect } from 'react';

export default function ServiceWorkerRegistration() {
  useEffect(() => {
    if ('serviceWorker' in navigator) {
      window.addEventListener('load', async () => {
        try {
          const registration = await navigator.serviceWorker.register('/sw.js', {
            scope: '/'
          });

          console.log('SW registered: ', registration);

          // Handle service worker updates
          registration.addEventListener('updatefound', () => {
            const newWorker = registration.installing;
            if (!newWorker) return;

            newWorker.addEventListener('statechange', () => {
              if (newWorker.state === 'installed') {
                if (navigator.serviceWorker.controller) {
                  // New content is available; please refresh
                  console.log('New content is available; please refresh.');
                  
                  // Show update notification to user
                  if (window.confirm('New version available! Click OK to refresh.')) {
                    window.location.reload();
                  }
                } else {
                  // Content is cached for offline use
                  console.log('Content is cached for offline use.');
                }
              }
            });
          });

          // Handle background sync registration
          if ('sync' in window.ServiceWorkerRegistration.prototype) {
            console.log('Background Sync is supported');
          }

          // Handle push notifications
          if ('PushManager' in window) {
            console.log('Push messaging is supported');
          }

          // Listen for messages from service worker
          navigator.serviceWorker.addEventListener('message', (event) => {
            console.log('Message from SW:', event.data);
            
            if (event.data.type === 'SKIP_WAITING') {
              window.location.reload();
            }
          });

        } catch (error) {
          console.log('SW registration failed: ', error);
        }
      });

      // Handle service worker controller change
      navigator.serviceWorker.addEventListener('controllerchange', () => {
        console.log('Service worker controller changed');
        window.location.reload();
      });
    }
  }, []);

  return null; // This component doesn't render anything
}
