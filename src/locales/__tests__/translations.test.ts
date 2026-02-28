import enZA from '../en-ZA';
import zuZA from '../zu-ZA';
import afZA from '../af-ZA';
import { getTranslations, SUPPORTED_LOCALES, DEFAULT_LOCALE } from '../index';

describe('Translation dictionaries', () => {
  it('en-ZA has the correct locale code', () => {
    expect(enZA.locale.code).toBe('en-ZA');
  });

  it('zu-ZA has the correct locale code', () => {
    expect(zuZA.locale.code).toBe('zu-ZA');
  });

  it('af-ZA has the correct locale code', () => {
    expect(afZA.locale.code).toBe('af-ZA');
  });

  it('all locales have LTR direction', () => {
    [enZA, zuZA, afZA].forEach((dict) => {
      expect(dict.locale.direction).toBe('ltr');
    });
  });

  it('en-ZA nav.dashboard is Dashboard', () => {
    expect(enZA.nav.dashboard).toBe('Dashboard');
  });

  it('zu-ZA nav.dashboard is translated', () => {
    expect(zuZA.nav.dashboard).toBe('Ikhombamzimba');
  });

  it('af-ZA nav.dashboard is translated', () => {
    expect(afZA.nav.dashboard).toBe('Kontroleskerm');
  });

  it('all locales provide the same top-level keys', () => {
    const enKeys = Object.keys(enZA).sort();
    const zuKeys = Object.keys(zuZA).sort();
    const afKeys = Object.keys(afZA).sort();
    expect(zuKeys).toEqual(enKeys);
    expect(afKeys).toEqual(enKeys);
  });

  it('SUPPORTED_LOCALES contains exactly 3 entries', () => {
    expect(SUPPORTED_LOCALES).toHaveLength(3);
    expect(SUPPORTED_LOCALES).toContain('en-ZA');
    expect(SUPPORTED_LOCALES).toContain('zu-ZA');
    expect(SUPPORTED_LOCALES).toContain('af-ZA');
  });

  it('DEFAULT_LOCALE is en-ZA', () => {
    expect(DEFAULT_LOCALE).toBe('en-ZA');
  });

  it('getTranslations returns English for en-ZA', () => {
    const dict = getTranslations('en-ZA');
    expect(dict.locale.code).toBe('en-ZA');
  });

  it('getTranslations returns isiZulu for zu-ZA', () => {
    const dict = getTranslations('zu-ZA');
    expect(dict.locale.code).toBe('zu-ZA');
  });

  it('getTranslations returns Afrikaans for af-ZA', () => {
    const dict = getTranslations('af-ZA');
    expect(dict.locale.code).toBe('af-ZA');
  });

  it('af-ZA settings.language is "Taal"', () => {
    expect(afZA.settings.language).toBe('Taal');
  });

  it('zu-ZA settings.language is "Ulimi"', () => {
    expect(zuZA.settings.language).toBe('Ulimi');
  });

  it('all locales have languageSwitcher section', () => {
    [enZA, zuZA, afZA].forEach((dict) => {
      expect(dict.languageSwitcher).toBeDefined();
      expect(dict.languageSwitcher.en_ZA).toBeDefined();
      expect(dict.languageSwitcher.zu_ZA).toBeDefined();
      expect(dict.languageSwitcher.af_ZA).toBeDefined();
    });
  });
});
