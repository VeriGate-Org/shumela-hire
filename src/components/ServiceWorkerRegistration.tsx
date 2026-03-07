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
                  
                  // Show update notification banner
                  const banner = document.createElement('div');
                  banner.setAttribute('role', 'alert');
                  banner.style.cssText = 'position:fixed;top:0;left:0;right:0;z-index:9999;display:flex;align-items:center;justify-content:center;gap:12px;padding:12px 16px;background:#0F172A;color:#F8FAFC;font-size:14px;font-family:Manrope,sans-serif';
                  banner.textContent = 'A new version is available.';
                  const btn = document.createElement('button');
                  btn.textContent = 'Refresh';
                  btn.style.cssText = 'padding:4px 16px;border-radius:9999px;border:2px solid #F1C54B;color:#F1C54B;background:transparent;font-size:13px;font-weight:600;cursor:pointer;text-transform:uppercase;letter-spacing:0.05em';
                  btn.onclick = () => window.location.reload();
                  banner.appendChild(btn);
                  document.body.appendChild(banner);
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
