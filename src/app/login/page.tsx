'use client';

import { useRouter } from 'next/navigation';
import { useAuth } from '../../contexts/AuthContext';
import { useEffect } from 'react';
import ThemeToggle from '../../components/ThemeToggle';

export default function LoginPage() {
  const router = useRouter();
  const { user, login } = useAuth();

  // Redirect to dashboard if already logged in
  useEffect(() => {
    if (user) {
      router.push('/dashboard');
    }
  }, [user, router]);

  const handleMockLogin = () => {
    // Mock authentication - simulate successful login
    const mockUser = {
      id: '1',
      name: 'John Doe',
      email: 'john.doe@company.com',
      role: 'ADMIN' as const
    };

    // Set a mock token
    sessionStorage.setItem('jwt_token', 'mock-jwt-token-' + Date.now());
    
    // Log in the user using the auth context
    login(mockUser);
    
    // Redirect to dashboard
    router.push('/dashboard');
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 relative">
      {/* Theme Toggle in top-right corner */}
      <div className="absolute top-6 right-6">
        <ThemeToggle compact />
      </div>
      
      <div className="max-w-md w-full space-y-8">
        <div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            Sign in to your account
          </h2>
          <p className="mt-2 text-center text-sm text-gray-600">
            TalentGate
          </p>
        </div>
        <div>
          <button
            onClick={handleMockLogin}
            className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-violet-600 hover:bg-violet-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-violet-500"
          >
            Sign In (Demo)
          </button>
        </div>
      </div>
    </div>
  );
}