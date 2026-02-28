import { AuthProvider } from '@/contexts/AuthContext';
import { ThemeProvider } from '@/contexts/ThemeContext';
import { LayoutProvider } from '@/contexts/LayoutContext';
import { TenantProvider } from '@/contexts/TenantContext';
import { FeatureGateProvider } from '@/contexts/FeatureGateContext';
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
          <FeatureGateProvider>
            <ToastProvider>
              <LayoutProvider>
                {children}
              </LayoutProvider>
            </ToastProvider>
          </FeatureGateProvider>
        </AuthProvider>
      </ThemeProvider>
    </TenantProvider>
  );
}
