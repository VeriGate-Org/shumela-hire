import React from 'react';
import { render, screen, act } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { LocaleProvider, useLocale } from '../LocaleContext';

// Spy on localStorage
const localStorageMock = (() => {
  let store: Record<string, string> = {};
  return {
    getItem: (key: string) => store[key] ?? null,
    setItem: (key: string, value: string) => { store[key] = value; },
    removeItem: (key: string) => { delete store[key]; },
    clear: () => { store = {}; },
  };
})();
Object.defineProperty(window, 'localStorage', { value: localStorageMock });

function TestConsumer() {
  const { locale, setLocale, t, supportedLocales } = useLocale();
  return (
    <div>
      <span data-testid="locale">{locale}</span>
      <span data-testid="nav-dashboard">{t.nav.dashboard}</span>
      <span data-testid="supported-count">{supportedLocales.length}</span>
      <button onClick={() => setLocale('af-ZA')}>Switch to Afrikaans</button>
      <button onClick={() => setLocale('zu-ZA')}>Switch to isiZulu</button>
      <button onClick={() => setLocale('en-ZA')}>Switch to English</button>
    </div>
  );
}

describe('LocaleContext', () => {
  beforeEach(() => {
    localStorageMock.clear();
  });

  it('defaults to en-ZA', () => {
    render(
      <LocaleProvider>
        <TestConsumer />
      </LocaleProvider>
    );
    expect(screen.getByTestId('locale')).toHaveTextContent('en-ZA');
  });

  it('provides English translations by default', () => {
    render(
      <LocaleProvider>
        <TestConsumer />
      </LocaleProvider>
    );
    expect(screen.getByTestId('nav-dashboard')).toHaveTextContent('Dashboard');
  });

  it('exposes all 3 supported locales', () => {
    render(
      <LocaleProvider>
        <TestConsumer />
      </LocaleProvider>
    );
    expect(screen.getByTestId('supported-count')).toHaveTextContent('3');
  });

  it('switches to Afrikaans and updates translations', async () => {
    const user = userEvent.setup();
    render(
      <LocaleProvider>
        <TestConsumer />
      </LocaleProvider>
    );

    await act(async () => {
      await user.click(screen.getByText('Switch to Afrikaans'));
    });

    expect(screen.getByTestId('locale')).toHaveTextContent('af-ZA');
    expect(screen.getByTestId('nav-dashboard')).toHaveTextContent('Kontroleskerm');
  });

  it('switches to isiZulu and updates translations', async () => {
    const user = userEvent.setup();
    render(
      <LocaleProvider>
        <TestConsumer />
      </LocaleProvider>
    );

    await act(async () => {
      await user.click(screen.getByText('Switch to isiZulu'));
    });

    expect(screen.getByTestId('locale')).toHaveTextContent('zu-ZA');
    expect(screen.getByTestId('nav-dashboard')).toHaveTextContent('Ikhombamzimba');
  });

  it('persists locale choice to localStorage', async () => {
    const user = userEvent.setup();
    render(
      <LocaleProvider>
        <TestConsumer />
      </LocaleProvider>
    );

    await act(async () => {
      await user.click(screen.getByText('Switch to Afrikaans'));
    });

    expect(localStorageMock.getItem('shumelahire-locale')).toBe('af-ZA');
  });

  it('can switch back to English', async () => {
    const user = userEvent.setup();
    render(
      <LocaleProvider>
        <TestConsumer />
      </LocaleProvider>
    );

    await act(async () => {
      await user.click(screen.getByText('Switch to isiZulu'));
    });
    await act(async () => {
      await user.click(screen.getByText('Switch to English'));
    });

    expect(screen.getByTestId('locale')).toHaveTextContent('en-ZA');
    expect(screen.getByTestId('nav-dashboard')).toHaveTextContent('Dashboard');
  });

  it('throws when useLocale is used outside LocaleProvider', () => {
    // Suppress React error boundary noise
    const spy = jest.spyOn(console, 'error').mockImplementation(() => {});
    expect(() => render(<TestConsumer />)).toThrow('useLocale must be used within a LocaleProvider');
    spy.mockRestore();
  });
});
