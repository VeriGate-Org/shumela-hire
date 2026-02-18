'use client';

import { useEffect } from 'react';

export default function Error({
  error,
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  useEffect(() => {
    console.error('Application error:', error);
  }, [error]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-[#F8FAFC] px-4">
      <div className="max-w-md w-full text-center">
        <div className="w-16 h-16 mx-auto mb-6 rounded-[2px] bg-[#05527E]/10 flex items-center justify-center">
          <svg className="w-8 h-8 text-[#05527E]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5}
              d="M12 9v3.75m9-.75a9 9 0 11-18 0 9 9 0 0118 0zm-9 3.75h.008v.008H12v-.008z" />
          </svg>
        </div>
        <h1 className="text-2xl font-bold text-[#0F172A] tracking-[-0.03em] mb-2">
          Something went wrong
        </h1>
        <p className="text-[#64748B] leading-relaxed mb-8">
          An unexpected error occurred. Please try again or contact support if the issue persists.
        </p>
        <button
          onClick={reset}
          className="inline-flex items-center px-6 py-3 rounded-full bg-[#05527E] text-white font-medium hover:bg-[#05527E]/90 transition-colors"
        >
          Try again
        </button>
      </div>
    </div>
  );
}
