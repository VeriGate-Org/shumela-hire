// API Services Index
// This file provides a centralized export for all API services

// Core API Client
export { apiClient } from './api/apiClient';
export type { ApiError, PaginatedResponse, PaginationParams } from './api/apiClient';

// Application API
export { applicationApi } from './api/applicationApi';
export type {
  Application,
  ApplicationFilters,
  ApplicationStatistics,
  ApplicationNote,
} from './api/applicationApi';

// Job API
export { jobApi, analyticsApi, workflowApi } from './api/jobApi';
export type {
  JobPosting,
  JobSkill,
  JobTemplate,
  JobStatistics,
  JobFilters,
  RecruitmentMetrics,
  PipelineMetrics,
  PerformanceMetrics,
  RecruitmentAnalytics,
  WorkflowExecution,
} from './api/jobApi';

// Authentication Service
export { authService, initializeAuth, PERMISSIONS } from './authService';
export type {
  LoginCredentials,
  AuthUser,
  AuthResponse,
  RefreshTokenResponse,
  PasswordResetRequest,
  PasswordReset,
  ChangePasswordRequest,
  UpdateProfileRequest,
  AuthEvent,
} from './authService';

// WebSocket Service
export { 
  webSocketService, 
  notificationService, 
  initializeWebSocket 
} from './webSocketService';
export type {
  WebSocketEvent,
  ApplicationStatusUpdate,
  InterviewScheduled,
  WorkflowStatusUpdate,
  NewApplicationReceived,
  OfferStatusUpdate,
  SystemNotification,
  ConnectionStatus,
  WebSocketOptions,
} from './webSocketService';

// Import services for internal use
import { apiClient } from './api/apiClient';
import { authService, initializeAuth } from './authService';
import { webSocketService, initializeWebSocket } from './webSocketService';

// Initialize all services
export const initializeServices = () => {
  // Initialize authentication
  initializeAuth();
  
  // Initialize WebSocket with authentication token
  const token = authService.getToken();
  if (token) {
    initializeWebSocket(token);
  }
  
  // Set up auth state listener to manage WebSocket connection
  authService.addEventListener((event: any) => {
    if (event.type === 'LOGIN_SUCCESS') {
      initializeWebSocket(authService.getToken() || undefined);
    } else if (event.type === 'LOGOUT') {
      webSocketService.disconnect();
    }
  });
};

// Service health check
export const checkServiceHealth = async () => {
  const results = {
    api: false,
    auth: false,
    websocket: false,
  };

  try {
    // Check API health
    await apiClient.get('/api/health');
    results.api = true;
  } catch (error) {
    console.error('API health check failed:', error);
  }

  try {
    // Check auth status
    results.auth = authService.isAuthenticated();
  } catch (error) {
    console.error('Auth health check failed:', error);
  }

  try {
    // Check WebSocket status
    results.websocket = webSocketService.isConnected();
  } catch (error) {
    console.error('WebSocket health check failed:', error);
  }

  return results;
};

// Service status information
export const getServiceStatus = () => {
  return {
    api: {
      baseUrl: (apiClient as any).baseURL,
      timeout: (apiClient as any).timeout,
    },
    auth: {
      isAuthenticated: authService.isAuthenticated(),
      user: authService.getCurrentUser(),
      tokenExpired: authService.isTokenExpired(),
    },
    websocket: {
      status: webSocketService.getConnectionStatus(),
      reconnectAttempts: webSocketService.getReconnectAttempts(),
    },
  };
};
