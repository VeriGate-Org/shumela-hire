import { AuthProvider } from '@/contexts/AuthContext';
import { ThemeProvider } from '@/contexts/ThemeContext';
import { LayoutProvider } from '@/contexts/LayoutContext';
import { TenantProvider } from '@/contexts/TenantContext';
import { ToastProvider } from '@/components/Toast';

export default function AppLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <TenantProvider>
      <ThemeProvider>
        <AuthProvider>
          <ToastProvider>
            <LayoutProvider>
              {children}
            </LayoutProvider>
          </ToastProvider>
        </AuthProvider>
      </ThemeProvider>
    </TenantProvider>
  );
}
