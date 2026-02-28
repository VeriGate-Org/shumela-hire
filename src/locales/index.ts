import enZA, { TranslationKeys } from './en-ZA';
import zuZA from './zu-ZA';
import afZA from './af-ZA';

export type SupportedLocale = 'en-ZA' | 'zu-ZA' | 'af-ZA';

export const SUPPORTED_LOCALES: SupportedLocale[] = ['en-ZA', 'zu-ZA', 'af-ZA'];

export const DEFAULT_LOCALE: SupportedLocale = 'en-ZA';

export const translations: Record<SupportedLocale, TranslationKeys> = {
  'en-ZA': enZA,
  'zu-ZA': zuZA as unknown as TranslationKeys,
  'af-ZA': afZA as unknown as TranslationKeys,
};

export function getTranslations(locale: SupportedLocale): TranslationKeys {
  return translations[locale] ?? translations[DEFAULT_LOCALE];
}

export { enZA, zuZA, afZA };
export type { TranslationKeys };
