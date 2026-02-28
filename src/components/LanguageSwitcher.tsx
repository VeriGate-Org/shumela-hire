'use client';

import React, { useState, useRef, useEffect } from 'react';
import { useLocale } from '@/contexts/LocaleContext';
import { SupportedLocale } from '@/locales';

const LOCALE_LABELS: Record<SupportedLocale, { label: string; flag: string }> = {
  'en-ZA': { label: 'English', flag: '🇿🇦' },
  'zu-ZA': { label: 'isiZulu', flag: '🇿🇦' },
  'af-ZA': { label: 'Afrikaans', flag: '🇿🇦' },
};

interface LanguageSwitcherProps {
  /** Render as a compact icon-only button (for sidebars / top-bars) */
  compact?: boolean;
  className?: string;
}

export default function LanguageSwitcher({ compact = false, className = '' }: LanguageSwitcherProps) {
  const { locale, setLocale, supportedLocales, t } = useLocale();
  const [open, setOpen] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);

  // Close on outside click
  useEffect(() => {
    function handleClick(e: MouseEvent) {
      if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
        setOpen(false);
      }
    }
    if (open) document.addEventListener('mousedown', handleClick);
    return () => document.removeEventListener('mousedown', handleClick);
  }, [open]);

  // Close on Escape
  useEffect(() => {
    function handleKey(e: KeyboardEvent) {
      if (e.key === 'Escape') setOpen(false);
    }
    if (open) document.addEventListener('keydown', handleKey);
    return () => document.removeEventListener('keydown', handleKey);
  }, [open]);

  const current = LOCALE_LABELS[locale];

  return (
    <div ref={containerRef} className={`relative ${className}`}>
      <button
        type="button"
        aria-haspopup="listbox"
        aria-expanded={open}
        aria-label={t.languageSwitcher.selectLanguage}
        onClick={() => setOpen((v) => !v)}
        className={
          compact
            ? 'flex items-center gap-1.5 rounded-md px-2 py-1.5 text-sm text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-white/10 transition-colors'
            : 'flex items-center gap-2 rounded-lg border border-gray-200 dark:border-white/10 bg-white dark:bg-white/5 px-3 py-2 text-sm font-medium text-gray-700 dark:text-gray-200 shadow-sm hover:bg-gray-50 dark:hover:bg-white/10 transition-colors'
        }
      >
        <span aria-hidden="true">{current.flag}</span>
        {!compact && <span>{current.label}</span>}
        <svg
          className={`h-3.5 w-3.5 text-gray-400 transition-transform ${open ? 'rotate-180' : ''}`}
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          strokeWidth={2}
          aria-hidden="true"
        >
          <path strokeLinecap="round" strokeLinejoin="round" d="M19 9l-7 7-7-7" />
        </svg>
      </button>

      {open && (
        <ul
          role="listbox"
          aria-label={t.languageSwitcher.selectLanguage}
          className="absolute right-0 z-50 mt-1.5 w-40 origin-top-right rounded-xl border border-gray-200 dark:border-white/10 bg-white dark:bg-gray-900 shadow-lg ring-1 ring-black/5 focus:outline-none overflow-hidden"
        >
          {supportedLocales.map((loc) => {
            const { label, flag } = LOCALE_LABELS[loc];
            const isSelected = loc === locale;
            return (
              <li
                key={loc}
                role="option"
                aria-selected={isSelected}
                onClick={() => {
                  setLocale(loc);
                  setOpen(false);
                }}
                onKeyDown={(e) => {
                  if (e.key === 'Enter' || e.key === ' ') {
                    setLocale(loc);
                    setOpen(false);
                  }
                }}
                tabIndex={0}
                className={`flex cursor-pointer items-center gap-2.5 px-3 py-2.5 text-sm transition-colors
                  ${isSelected
                    ? 'bg-blue-50 dark:bg-blue-900/30 text-blue-700 dark:text-blue-300 font-medium'
                    : 'text-gray-700 dark:text-gray-200 hover:bg-gray-50 dark:hover:bg-white/5'
                  }`}
              >
                <span aria-hidden="true">{flag}</span>
                <span>{label}</span>
                {isSelected && (
                  <svg className="ml-auto h-3.5 w-3.5" fill="currentColor" viewBox="0 0 20 20" aria-hidden="true">
                    <path
                      fillRule="evenodd"
                      d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                      clipRule="evenodd"
                    />
                  </svg>
                )}
              </li>
            );
          })}
        </ul>
      )}
    </div>
  );
}
