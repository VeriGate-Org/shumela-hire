import React from 'react';
import { render, screen, act } from '@testing-library/react';
import { AuthProvider, useAuth, UserRole } from '../AuthContext';
import { rolePermissions } from '@/config/permissions';

// Mock aws-amplify/auth to prevent actual Cognito calls
jest.mock('aws-amplify/auth', () => ({
  signIn: jest.fn(),
  signOut: jest.fn(),
  fetchAuthSession: jest.fn(),
  getCurrentUser: jest.fn(),
  fetchUserAttributes: jest.fn(),
}));

// Mock aws-amplify
jest.mock('aws-amplify', () => ({
  Amplify: {
    configure: jest.fn(),
  },
}));

// Mock amplify-config so isCognitoConfigured is false in tests
jest.mock('@/lib/amplify-config', () => ({
  isCognitoConfigured: false,
  configureAmplify: jest.fn(),
}));

// Helper component that exposes auth context values for testing
function AuthConsumer({ onAuth }: { onAuth: (auth: ReturnType<typeof useAuth>) => void }) {
  const auth = useAuth();
  React.useEffect(() => {
    onAuth(auth);
  });
  return <div data-testid="auth-consumer">Authenticated: {String(auth.isAuthenticated)}</div>;
}

describe('AuthContext', () => {
  beforeEach(() => {
    // Clear sessionStorage before each test
    sessionStorage.clear();
  });

  it('renders children within AuthProvider', () => {
    render(
      <AuthProvider>
        <div data-testid="child">Hello</div>
      </AuthProvider>
    );

    expect(screen.getByTestId('child')).toBeInTheDocument();
    expect(screen.getByText('Hello')).toBeInTheDocument();
  });

  it('throws an error when useAuth is called outside AuthProvider', () => {
    // Suppress console.error for this test since React will log the error
    const consoleSpy = jest.spyOn(console, 'error').mockImplementation(() => {});

    function BadConsumer() {
      useAuth();
      return null;
    }

    expect(() => render(<BadConsumer />)).toThrow(
      'useAuth must be used within an AuthProvider'
    );

    consoleSpy.mockRestore();
  });

  it('returns default unauthenticated state', async () => {
    let authState: ReturnType<typeof useAuth> | null = null;

    render(
      <AuthProvider>
        <AuthConsumer onAuth={(auth) => { authState = auth; }} />
      </AuthProvider>
    );

    // Wait for loading to finish
    await screen.findByTestId('auth-consumer');

    expect(authState).not.toBeNull();
    expect(authState!.user).toBeNull();
    expect(authState!.isAuthenticated).toBe(false);
    expect(authState!.token).toBeNull();
  });

  it('logs in a user with mock login flow', async () => {
    let authState: ReturnType<typeof useAuth> | null = null;

    render(
      <AuthProvider>
        <AuthConsumer onAuth={(auth) => { authState = auth; }} />
      </AuthProvider>
    );

    await screen.findByTestId('auth-consumer');

    const mockUser = {
      id: '1',
      name: 'Test User',
      email: 'test@example.com',
      role: 'ADMIN' as UserRole,
      permissions: rolePermissions['ADMIN'],
    };

    act(() => {
      authState!.login(mockUser);
    });

    expect(authState!.user).toEqual(mockUser);
    expect(authState!.isAuthenticated).toBe(true);
    expect(authState!.token).toBeTruthy();
    expect(authState!.token).toContain('mock-jwt-token-');

    // Verify sessionStorage was set
    expect(sessionStorage.getItem('jwt_token')).toBeTruthy();
    expect(sessionStorage.getItem('mock_user')).toBeTruthy();
    const storedUser = JSON.parse(sessionStorage.getItem('mock_user')!);
    expect(storedUser.email).toBe('test@example.com');
  });

  it('logs out and clears state', async () => {
    let authState: ReturnType<typeof useAuth> | null = null;

    render(
      <AuthProvider>
        <AuthConsumer onAuth={(auth) => { authState = auth; }} />
      </AuthProvider>
    );

    await screen.findByTestId('auth-consumer');

    // Log in first
    const mockUser = {
      id: '1',
      name: 'Test User',
      email: 'test@example.com',
      role: 'RECRUITER' as UserRole,
      permissions: rolePermissions['RECRUITER'],
    };

    act(() => {
      authState!.login(mockUser);
    });

    expect(authState!.isAuthenticated).toBe(true);

    // Now log out
    await act(async () => {
      await authState!.logout();
    });

    expect(authState!.user).toBeNull();
    expect(authState!.isAuthenticated).toBe(false);
    expect(authState!.token).toBeNull();

    // Verify sessionStorage was cleared
    expect(sessionStorage.getItem('jwt_token')).toBeNull();
    expect(sessionStorage.getItem('mock_user')).toBeNull();
  });

  it('hasPermission returns correct values', async () => {
    let authState: ReturnType<typeof useAuth> | null = null;

    render(
      <AuthProvider>
        <AuthConsumer onAuth={(auth) => { authState = auth; }} />
      </AuthProvider>
    );

    await screen.findByTestId('auth-consumer');

    // Before login, hasPermission should return false
    expect(authState!.hasPermission('view_dashboard')).toBe(false);

    const mockUser = {
      id: '1',
      name: 'Test User',
      email: 'test@example.com',
      role: 'EMPLOYEE' as UserRole,
      permissions: rolePermissions['EMPLOYEE'],
    };

    act(() => {
      authState!.login(mockUser);
    });

    // EMPLOYEE has 'view_dashboard' but not 'manage_jobs'
    expect(authState!.hasPermission('view_dashboard')).toBe(true);
    expect(authState!.hasPermission('manage_jobs')).toBe(false);
  });

  it('switchRole updates user role and permissions', async () => {
    let authState: ReturnType<typeof useAuth> | null = null;

    render(
      <AuthProvider>
        <AuthConsumer onAuth={(auth) => { authState = auth; }} />
      </AuthProvider>
    );

    await screen.findByTestId('auth-consumer');

    // Log in as EMPLOYEE first
    const mockUser = {
      id: '1',
      name: 'Test User',
      email: 'test@example.com',
      role: 'EMPLOYEE' as UserRole,
      permissions: rolePermissions['EMPLOYEE'],
    };

    act(() => {
      authState!.login(mockUser);
    });

    expect(authState!.user!.role).toBe('EMPLOYEE');

    // Switch to ADMIN
    act(() => {
      authState!.switchRole('ADMIN');
    });

    expect(authState!.user!.role).toBe('ADMIN');
    expect(authState!.user!.permissions).toEqual(rolePermissions['ADMIN']);
  });
});
