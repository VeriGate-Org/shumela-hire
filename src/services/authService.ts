import { apiClient } from './api/apiClient';

// Authentication Types
export interface LoginCredentials {
  email: string;
  password: string;
  rememberMe?: boolean;
}

export interface AuthUser {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: 'admin' | 'recruiter' | 'hiring_manager' | 'interviewer';
  department: string;
  permissions: string[];
  avatar?: string;
  isActive: boolean;
  lastLoginAt?: string;
  createdAt: string;
  updatedAt: string;
}

export interface AuthResponse {
  user: AuthUser;
  token: string;
  refreshToken: string;
  expiresIn: number;
}

export interface RefreshTokenResponse {
  token: string;
  expiresIn: number;
}

export interface PasswordResetRequest {
  email: string;
}

export interface PasswordReset {
  token: string;
  password: string;
  confirmPassword: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface UpdateProfileRequest {
  firstName: string;
  lastName: string;
  email: string;
  department?: string;
  avatar?: File;
}

// Authentication Events
export type AuthEvent = 
  | { type: 'LOGIN_SUCCESS'; user: AuthUser }
  | { type: 'LOGIN_FAILURE'; error: string }
  | { type: 'LOGOUT' }
  | { type: 'TOKEN_REFRESH_SUCCESS'; token: string }
  | { type: 'TOKEN_REFRESH_FAILURE'; error: string }
  | { type: 'SESSION_EXPIRED' }
  | { type: 'PROFILE_UPDATED'; user: AuthUser };

class AuthService {
  private static instance: AuthService;
  private user: AuthUser | null = null;
  private token: string | null = null;
  private refreshToken: string | null = null;
  private tokenExpirationTime: number | null = null;
  private refreshTimer: NodeJS.Timeout | null = null;
  private eventListeners: ((event: AuthEvent) => void)[] = [];

  private constructor() {
    this.loadFromStorage();
    this.setupTokenRefresh();
  }

  static getInstance(): AuthService {
    if (!AuthService.instance) {
      AuthService.instance = new AuthService();
    }
    return AuthService.instance;
  }

  // Event handling
  addEventListener(callback: (event: AuthEvent) => void): () => void {
    this.eventListeners.push(callback);
    return () => {
      const index = this.eventListeners.indexOf(callback);
      if (index > -1) {
        this.eventListeners.splice(index, 1);
      }
    };
  }

  private emitEvent(event: AuthEvent): void {
    this.eventListeners.forEach(callback => {
      try {
        callback(event);
      } catch (error) {
        console.error('Error in auth event listener:', error);
      }
    });
  }

  // Login
  async login(credentials: LoginCredentials): Promise<AuthUser> {
    try {
      const response = await apiClient.post<AuthResponse>('/api/auth/login', credentials);
      
      this.setAuthData(response);
      this.setupTokenRefresh();
      
      this.emitEvent({ type: 'LOGIN_SUCCESS', user: response.user });
      
      return response.user;
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Login failed';
      this.emitEvent({ type: 'LOGIN_FAILURE', error: errorMessage });
      throw error;
    }
  }

  // Logout
  async logout(): Promise<void> {
    try {
      if (this.token) {
        await apiClient.post('/api/auth/logout', { refreshToken: this.refreshToken });
      }
    } catch (error) {
      console.error('Logout request failed:', error);
    } finally {
      this.clearAuthData();
      this.emitEvent({ type: 'LOGOUT' });
    }
  }

  // Get current user
  getCurrentUser(): AuthUser | null {
    return this.user;
  }

  // Get current token
  getToken(): string | null {
    return this.token;
  }

  // Check if user is authenticated
  isAuthenticated(): boolean {
    return this.token !== null && this.user !== null && !this.isTokenExpired();
  }

  // Check if token is expired
  isTokenExpired(): boolean {
    if (!this.tokenExpirationTime) return true;
    return Date.now() >= this.tokenExpirationTime;
  }

  // Refresh token
  async refreshTokens(): Promise<string> {
    if (!this.refreshToken) {
      throw new Error('No refresh token available');
    }

    try {
      const response = await apiClient.post<RefreshTokenResponse>('/api/auth/refresh', {
        refreshToken: this.refreshToken,
      });

      this.token = response.token;
      this.tokenExpirationTime = Date.now() + (response.expiresIn * 1000);
      
      this.saveToStorage();
      this.setupTokenRefresh();
      
      this.emitEvent({ type: 'TOKEN_REFRESH_SUCCESS', token: response.token });
      
      return response.token;
    } catch (error: any) {
      console.error('Token refresh failed:', error);
      this.clearAuthData();
      this.emitEvent({ type: 'TOKEN_REFRESH_FAILURE', error: error.message });
      throw error;
    }
  }

  // Request password reset
  async requestPasswordReset(email: string): Promise<void> {
    await apiClient.post('/api/auth/forgot-password', { email });
  }

  // Reset password
  async resetPassword(data: PasswordReset): Promise<void> {
    if (data.password !== data.confirmPassword) {
      throw new Error('Passwords do not match');
    }
    
    await apiClient.post('/api/auth/reset-password', {
      token: data.token,
      password: data.password,
    });
  }

  // Change password
  async changePassword(data: ChangePasswordRequest): Promise<void> {
    if (data.newPassword !== data.confirmPassword) {
      throw new Error('New passwords do not match');
    }

    await apiClient.post('/api/auth/change-password', {
      currentPassword: data.currentPassword,
      newPassword: data.newPassword,
    });
  }

  // Update profile
  async updateProfile(data: UpdateProfileRequest): Promise<AuthUser> {
    const formData = new FormData();
    formData.append('firstName', data.firstName);
    formData.append('lastName', data.lastName);
    formData.append('email', data.email);
    
    if (data.department) {
      formData.append('department', data.department);
    }
    
    if (data.avatar) {
      formData.append('avatar', data.avatar);
    }

    const updatedUser = await apiClient.put<AuthUser>('/api/auth/profile', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    this.user = updatedUser;
    this.saveToStorage();
    
    this.emitEvent({ type: 'PROFILE_UPDATED', user: updatedUser });
    
    return updatedUser;
  }

  // Check permissions
  hasPermission(permission: string): boolean {
    return this.user?.permissions.includes(permission) || false;
  }

  hasAnyPermission(permissions: string[]): boolean {
    return permissions.some(permission => this.hasPermission(permission));
  }

  hasAllPermissions(permissions: string[]): boolean {
    return permissions.every(permission => this.hasPermission(permission));
  }

  // Role-based checks
  isAdmin(): boolean {
    return this.user?.role === 'admin';
  }

  isRecruiter(): boolean {
    return this.user?.role === 'recruiter' || this.isAdmin();
  }

  isHiringManager(): boolean {
    return this.user?.role === 'hiring_manager' || this.isAdmin();
  }

  isInterviewer(): boolean {
    return this.user?.role === 'interviewer' || this.isRecruiter() || this.isHiringManager();
  }

  // Get user's department
  getUserDepartment(): string | undefined {
    return this.user?.department;
  }

  // Private methods
  private setAuthData(response: AuthResponse): void {
    this.user = response.user;
    this.token = response.token;
    this.refreshToken = response.refreshToken;
    this.tokenExpirationTime = Date.now() + (response.expiresIn * 1000);
    
    this.saveToStorage();
  }

  private clearAuthData(): void {
    this.user = null;
    this.token = null;
    this.refreshToken = null;
    this.tokenExpirationTime = null;
    
    if (this.refreshTimer) {
      clearTimeout(this.refreshTimer);
      this.refreshTimer = null;
    }
    
    this.clearStorage();
  }

  private saveToStorage(): void {
    try {
      const authData = {
        user: this.user,
        token: this.token,
        refreshToken: this.refreshToken,
        tokenExpirationTime: this.tokenExpirationTime,
      };
      
      localStorage.setItem('auth_data', JSON.stringify(authData));
      
      // Also save token separately for easy access
      if (this.token) {
        localStorage.setItem('auth_token', this.token);
      }
    } catch (error) {
      console.error('Failed to save auth data to storage:', error);
    }
  }

  private loadFromStorage(): void {
    try {
      const authDataStr = localStorage.getItem('auth_data');
      
      if (authDataStr) {
        const authData = JSON.parse(authDataStr);
        
        this.user = authData.user;
        this.token = authData.token;
        this.refreshToken = authData.refreshToken;
        this.tokenExpirationTime = authData.tokenExpirationTime;
        
        // Check if token is expired
        if (this.isTokenExpired()) {
          this.clearAuthData();
        }
      }
    } catch (error) {
      console.error('Failed to load auth data from storage:', error);
      this.clearStorage();
    }
  }

  private clearStorage(): void {
    localStorage.removeItem('auth_data');
    localStorage.removeItem('auth_token');
  }

  private setupTokenRefresh(): void {
    if (this.refreshTimer) {
      clearTimeout(this.refreshTimer);
    }

    if (!this.tokenExpirationTime || !this.refreshToken) {
      return;
    }

    // Refresh token 5 minutes before expiration
    const refreshTime = this.tokenExpirationTime - Date.now() - (5 * 60 * 1000);

    if (refreshTime > 0) {
      this.refreshTimer = setTimeout(() => {
        this.refreshTokens().catch(() => {
          // Token refresh failed, user needs to login again
          this.emitEvent({ type: 'SESSION_EXPIRED' });
        });
      }, refreshTime);
    } else {
      // Token is about to expire or already expired
      if (this.refreshToken) {
        this.refreshTokens().catch(() => {
          this.emitEvent({ type: 'SESSION_EXPIRED' });
        });
      } else {
        this.emitEvent({ type: 'SESSION_EXPIRED' });
      }
    }
  }
}

// Permission constants
export const PERMISSIONS = {
  // Job management
  CREATE_JOBS: 'jobs.create',
  EDIT_JOBS: 'jobs.edit',
  DELETE_JOBS: 'jobs.delete',
  PUBLISH_JOBS: 'jobs.publish',
  
  // Application management
  VIEW_APPLICATIONS: 'applications.view',
  EDIT_APPLICATIONS: 'applications.edit',
  DELETE_APPLICATIONS: 'applications.delete',
  EXPORT_APPLICATIONS: 'applications.export',
  
  // Interview management
  SCHEDULE_INTERVIEWS: 'interviews.schedule',
  CONDUCT_INTERVIEWS: 'interviews.conduct',
  VIEW_ALL_INTERVIEWS: 'interviews.view_all',
  
  // Offer management
  CREATE_OFFERS: 'offers.create',
  APPROVE_OFFERS: 'offers.approve',
  VIEW_OFFER_DETAILS: 'offers.view_details',
  
  // Analytics
  VIEW_ANALYTICS: 'analytics.view',
  EXPORT_ANALYTICS: 'analytics.export',
  VIEW_ADVANCED_ANALYTICS: 'analytics.advanced',
  
  // Workflow
  CREATE_WORKFLOWS: 'workflows.create',
  EDIT_WORKFLOWS: 'workflows.edit',
  EXECUTE_WORKFLOWS: 'workflows.execute',
  VIEW_WORKFLOW_LOGS: 'workflows.view_logs',
  
  // User management
  MANAGE_USERS: 'users.manage',
  VIEW_USER_ACTIVITY: 'users.view_activity',
  
  // System
  ADMIN_ACCESS: 'system.admin',
  VIEW_AUDIT_LOGS: 'system.audit_logs',
  MANAGE_SETTINGS: 'system.settings',
} as const;

// Export singleton instance
export const authService = AuthService.getInstance();

// Initialize auth service
export const initializeAuth = () => {
  // Set up automatic logout on session expiration
  authService.addEventListener((event) => {
    if (event.type === 'SESSION_EXPIRED') {
      console.warn('Session expired. Redirecting to login...');
      // Clear any app state and redirect to login
      window.location.href = '/login';
    }
  });

  // Set up API client interceptors
  apiClient.addRequestInterceptor((config) => {
    const token = authService.getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  });

  apiClient.addResponseInterceptor(
    (response) => response,
    async (error) => {
      const originalRequest = error.config;

      if (error.response?.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;

        try {
          await authService.refreshTokens();
          const newToken = authService.getToken();
          originalRequest.headers.Authorization = `Bearer ${newToken}`;
          return apiClient.request(originalRequest);
        } catch (refreshError) {
          authService.logout();
          return Promise.reject(refreshError);
        }
      }

      return Promise.reject(error);
    }
  );
};

export default authService;
