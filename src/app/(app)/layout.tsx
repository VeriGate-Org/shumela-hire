import { AuthProvider } from '@/contexts/AuthContext';
import { ThemeProvider } from '@/contexts/ThemeContext';
import { LayoutProvider } from '@/contexts/LayoutContext';
import { TenantProvider } from '@/contexts/TenantContext';
import { LocaleProvider } from '@/contexts/LocaleContext';
import { ToastProvider } from '@/components/Toast';

export default function AppLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <TenantProvider>
      <ThemeProvider>
        <LocaleProvider>
          <AuthProvider>
            <ToastProvider>
              <LayoutProvider>
                {children}
              </LayoutProvider>
            </ToastProvider>
          </AuthProvider>
        </LocaleProvider>
      </ThemeProvider>
    </TenantProvider>
  );
}
