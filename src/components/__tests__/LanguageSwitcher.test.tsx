import React from 'react';
import { render, screen, act } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { LocaleProvider } from '@/contexts/LocaleContext';
import LanguageSwitcher from '../LanguageSwitcher';

function renderWithLocale(ui: React.ReactElement) {
  return render(<LocaleProvider>{ui}</LocaleProvider>);
}

describe('LanguageSwitcher', () => {
  it('renders with current locale label', () => {
    renderWithLocale(<LanguageSwitcher />);
    expect(screen.getByText('English')).toBeInTheDocument();
  });

  it('opens the dropdown on click', async () => {
    const user = userEvent.setup();
    renderWithLocale(<LanguageSwitcher />);

    const button = screen.getByRole('button');
    await act(async () => {
      await user.click(button);
    });

    expect(screen.getByRole('listbox')).toBeInTheDocument();
    expect(screen.getAllByRole('option').length).toBe(3);
  });

  it('displays all three supported locales', async () => {
    const user = userEvent.setup();
    renderWithLocale(<LanguageSwitcher />);

    await act(async () => {
      await user.click(screen.getByRole('button'));
    });

    expect(screen.getByRole('option', { name: /English/i })).toBeInTheDocument();
    expect(screen.getByRole('option', { name: /isiZulu/i })).toBeInTheDocument();
    expect(screen.getByRole('option', { name: /Afrikaans/i })).toBeInTheDocument();
  });

  it('closes the dropdown after selecting a locale', async () => {
    const user = userEvent.setup();
    renderWithLocale(<LanguageSwitcher />);

    await act(async () => {
      await user.click(screen.getByRole('button'));
    });

    await act(async () => {
      await user.click(screen.getByRole('option', { name: /Afrikaans/i }));
    });

    expect(screen.queryByRole('listbox')).not.toBeInTheDocument();
  });

  it('reflects the selected locale after switching', async () => {
    const user = userEvent.setup();
    renderWithLocale(<LanguageSwitcher />);

    await act(async () => {
      await user.click(screen.getByRole('button'));
    });

    await act(async () => {
      await user.click(screen.getByRole('option', { name: /Afrikaans/i }));
    });

    // Button should now show Afrikaans
    expect(screen.getByText('Afrikaans')).toBeInTheDocument();
  });

  it('closes on Escape key', async () => {
    const user = userEvent.setup();
    renderWithLocale(<LanguageSwitcher />);

    await act(async () => {
      await user.click(screen.getByRole('button'));
    });

    expect(screen.getByRole('listbox')).toBeInTheDocument();

    await act(async () => {
      await user.keyboard('{Escape}');
    });

    expect(screen.queryByRole('listbox')).not.toBeInTheDocument();
  });

  it('renders compact mode without text label', () => {
    renderWithLocale(<LanguageSwitcher compact />);
    // In compact mode, the locale text isn't rendered
    expect(screen.queryByText('English')).not.toBeInTheDocument();
  });
});
