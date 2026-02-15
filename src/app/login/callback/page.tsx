'use client';

import { useEffect, Suspense } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { useAuth, UserRole } from '../../../contexts/AuthContext';

function LoginCallbackContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { login } = useAuth();

  useEffect(() => {
    const handleCallback = async () => {
      const code = searchParams.get('code');
      const error = searchParams.get('error');

      if (error) {
        console.error('Authentication error:', error);
        router.push('/login?error=auth_failed');
        return;
      }

      if (!code) {
        router.push('/login?error=no_code');
        return;
      }

      try {
        // In a real implementation, you would exchange the code for tokens
        // For now, we'll mock the JWT parsing and user creation
        const mockJwtPayload = {
          sub: '123e4567-e89b-12d3-a456-426614174000',
          name: 'John Doe',
          email: 'john.doe@company.com',
          preferred_username: 'john.doe',
          // Mock role claim - in production this would come from Keycloak
          // Keycloak typically stores roles in: realm_access.roles, resource_access, or custom claims
          realm_access: {
            roles: ['ADMIN', 'user'] // Role mapping would be configured in Keycloak
          },
          resource_access: {
            'talentgate': {
              roles: ['ADMIN']
            }
          }
        };

        // Extract role from JWT claims (following Keycloak standard patterns)
        const extractUserRole = (payload: {
          realm_access?: { roles?: string[] };
          resource_access?: { [key: string]: { roles?: string[] } };
        }): string => {
          // Check resource-specific roles first
          const clientRoles = payload.resource_access?.['talentgate']?.roles || [];
          const realmRoles = payload.realm_access?.roles || [];

          // Define role hierarchy for TalentGate
          const roleHierarchy = ['ADMIN', 'EXECUTIVE', 'HR_MANAGER', 'HIRING_MANAGER', 'RECRUITER', 'INTERVIEWER', 'EMPLOYEE', 'APPLICANT'];

          // Find highest priority role
          for (const role of roleHierarchy) {
            if (clientRoles.includes(role) || realmRoles.includes(role)) {
              return role;
            }
          }

          return 'APPLICANT'; // Default role
        };

        const userRole = extractUserRole(mockJwtPayload);
        
        // Create user object
        const userData = {
          id: mockJwtPayload.sub,
          name: mockJwtPayload.name,
          email: mockJwtPayload.email,
          role: userRole as UserRole, // Type assertion for mock data
        };

        // Store JWT in memory (mock implementation)
        sessionStorage.setItem('jwt_token', 'mock_jwt_token_' + Date.now());
        
        // Login user
        login(userData);
        
        // Redirect to dashboard
        router.push('/dashboard');
      } catch (error) {
        console.error('Token exchange failed:', error);
        router.push('/login?error=token_exchange_failed');
      }
    };

    handleCallback();
  }, [searchParams, login, router]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="text-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-indigo-600 mx-auto"></div>
        <p className="mt-4 text-gray-600">Completing sign in...</p>
      </div>
    </div>
  );
}

export default function LoginCallbackPage() {
  return (
    <Suspense fallback={
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-indigo-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading...</p>
        </div>
      </div>
    }>
      <LoginCallbackContent />
    </Suspense>
  );
}