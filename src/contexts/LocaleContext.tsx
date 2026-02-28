'use client';

import React, { createContext, useContext, useState, useEffect, useCallback, ReactNode } from 'react';
import { SupportedLocale, DEFAULT_LOCALE, SUPPORTED_LOCALES, getTranslations, TranslationKeys } from '@/locales';

const LOCALE_STORAGE_KEY = 'shumelahire-locale';

interface LocaleContextType {
  locale: SupportedLocale;
  setLocale: (locale: SupportedLocale) => void;
  t: TranslationKeys;
  supportedLocales: SupportedLocale[];
  isRTL: boolean;
}

const LocaleContext = createContext<LocaleContextType | undefined>(undefined);

function resolveInitialLocale(): SupportedLocale {
  if (typeof window === 'undefined') return DEFAULT_LOCALE;
  const stored = localStorage.getItem(LOCALE_STORAGE_KEY) as SupportedLocale | null;
  if (stored && SUPPORTED_LOCALES.includes(stored)) return stored;

  // Check browser language preference
  const browserLang = navigator.language as SupportedLocale;
  if (SUPPORTED_LOCALES.includes(browserLang)) return browserLang;

  // Check language prefix (e.g. "zu" → "zu-ZA")
  const langPrefix = navigator.language.split('-')[0];
  const match = SUPPORTED_LOCALES.find((l) => l.startsWith(langPrefix));
  if (match) return match;

  return DEFAULT_LOCALE;
}

export function LocaleProvider({ children }: { children: ReactNode }) {
  const [locale, setLocaleState] = useState<SupportedLocale>(DEFAULT_LOCALE);

  useEffect(() => {
    const resolved = resolveInitialLocale();
    setLocaleState(resolved);
    document.documentElement.lang = resolved;
  }, []);

  const setLocale = useCallback((next: SupportedLocale) => {
    if (!SUPPORTED_LOCALES.includes(next)) return;
    setLocaleState(next);
    localStorage.setItem(LOCALE_STORAGE_KEY, next);
    document.documentElement.lang = next;
  }, []);

  const t = getTranslations(locale);
  const isRTL = t.locale.direction === 'rtl';

  return (
    <LocaleContext.Provider value={{ locale, setLocale, t, supportedLocales: SUPPORTED_LOCALES, isRTL }}>
      {children}
    </LocaleContext.Provider>
  );
}

export function useLocale(): LocaleContextType {
  const context = useContext(LocaleContext);
  if (context === undefined) {
    throw new Error('useLocale must be used within a LocaleProvider');
  }
  return context;
}
