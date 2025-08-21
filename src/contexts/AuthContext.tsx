'use client';

import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';

export type UserRole = 'Admin' | 'HR' | 'Hiring Manager' | 'Recruiter' | 'Applicant' | 'Executive';

interface User {
  id: string;
  name: string;
  email: string;
  role: UserRole;
}

interface AuthContextType {
  user: User | null;
  login: (userData: User) => void;
  logout: () => void;
  switchRole: (role: UserRole) => void;
  isAuthenticated: boolean;
  token: string | null;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);

  // Check for existing session on mount
  useEffect(() => {
    const storedToken = sessionStorage.getItem('jwt_token');
    if (storedToken) {
      setToken(storedToken);
      // In a real implementation, you would decode the JWT to get user info
      // For now, we'll use mock data if a token exists
      setUser({
        id: '1',
        name: 'John Doe',
        email: 'john.doe@company.com',
        role: 'Admin'
      });
    }
  }, []);

  const login = (userData: User) => {
    setUser(userData);
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    sessionStorage.removeItem('jwt_token');
  };

  const switchRole = (role: UserRole) => {
    if (user) {
      setUser({ ...user, role });
    }
  };

  const isAuthenticated = !!user && !!token;

  return (
    <AuthContext.Provider value={{ user, login, logout, switchRole, isAuthenticated, token }}>
      {children}
    </AuthContext.Provider>
  );
};