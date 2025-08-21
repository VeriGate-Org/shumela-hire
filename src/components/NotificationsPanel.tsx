'use client';

import React, { useState, useEffect } from 'react';
import { useAuth } from '@/contexts/AuthContext';

interface Notification {
  id: number;
  title: string;
  message: string;
  type: string;
  channel: string;
  createdAt: string;
  isRead: boolean;
  readAt?: string;
  applicationId?: number;
  jobTitle?: string;
}

interface NotificationsPanelProps {
  applicantId?: number;
  limit?: number;
  showHeader?: boolean;
}

export default function NotificationsPanel({ 
  applicantId, 
  limit = 5, 
  showHeader = true 
}: NotificationsPanelProps) {
  const { token } = useAuth();
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(true);
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    loadNotifications();
  }, [applicantId]);

  const loadNotifications = async () => {
    try {
      // For demo purposes, create mock notifications
      // In real implementation, this would call /api/notifications/applicant/${applicantId}
      const mockNotifications: Notification[] = [
        {
          id: 1,
          title: 'Application Submitted Successfully',
          message: 'Your application for Software Developer has been submitted and is under review. We will contact you soon with updates.',
          type: 'APPLICATION_SUBMITTED',
          channel: 'EMAIL',
          createdAt: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString(),
          isRead: false,
          applicationId: 101,
          jobTitle: 'Software Developer',
        },
        {
          id: 2,
          title: 'Application Status Updated',
          message: 'Your application for Software Developer has moved to the screening stage. Our team is reviewing your qualifications.',
          type: 'PIPELINE_STAGE_CHANGED',
          channel: 'IN_APP',
          createdAt: new Date(Date.now() - 6 * 60 * 60 * 1000).toISOString(),
          isRead: true,
          readAt: new Date(Date.now() - 4 * 60 * 60 * 1000).toISOString(),
          applicationId: 101,
          jobTitle: 'Software Developer',
        },
        {
          id: 3,
          title: 'Interview Scheduled',
          message: 'Congratulations! We would like to schedule an interview for the Data Analyst position. Please check your email for details.',
          type: 'INTERVIEW_SCHEDULED',
          channel: 'EMAIL',
          createdAt: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString(),
          isRead: false,
          applicationId: 102,
          jobTitle: 'Data Analyst',
        },
        {
          id: 4,
          title: 'Document Uploaded',
          message: 'Your CV has been successfully uploaded and attached to your application for Project Manager.',
          type: 'DOCUMENT_SHARED',
          channel: 'IN_APP',
          createdAt: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000).toISOString(),
          isRead: true,
          readAt: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(),
          applicationId: 103,
          jobTitle: 'Project Manager',
        },
        {
          id: 5,
          title: 'Welcome to Our Platform',
          message: 'Welcome to our recruitment platform! Your profile has been created successfully. You can now browse and apply for positions.',
          type: 'SYSTEM_UPDATE',
          channel: 'IN_APP',
          createdAt: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString(),
          isRead: true,
          readAt: new Date(Date.now() - 6 * 24 * 60 * 60 * 1000).toISOString(),
        },
      ];

      // Apply limit
      const limitedNotifications = mockNotifications.slice(0, limit);
      setNotifications(limitedNotifications);
      setUnreadCount(limitedNotifications.filter(n => !n.isRead).length);
    } catch (error) {
      console.error('Error loading notifications:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleMarkAsRead = async (notificationId: number) => {
    try {
      // In real implementation, this would call the API
      // await fetch(`/api/notifications/${notificationId}/read`, { method: 'POST', ... });
      
      // For demo, just update local state
      setNotifications(prev => 
        prev.map(notification => 
          notification.id === notificationId 
            ? { ...notification, isRead: true, readAt: new Date().toISOString() }
            : notification
        )
      );
      setUnreadCount(prev => Math.max(0, prev - 1));
    } catch (error) {
      console.error('Error marking notification as read:', error);
    }
  };

  const getNotificationIcon = (type: string) => {
    switch (type.toUpperCase()) {
      case 'APPLICATION_SUBMITTED':
        return '📝';
      case 'PIPELINE_STAGE_CHANGED':
        return '🔄';
      case 'APPLICATION_WITHDRAWN':
        return '↩️';
      case 'APPLICATION_APPROVED':
        return '✅';
      case 'OFFER_EXTENDED':
        return '💰';
      case 'INTERVIEW_SCHEDULED':
        return '📅';
      case 'INTERVIEW_RESCHEDULED':
        return '🔄';
      case 'INTERVIEW_CANCELLED':
        return '❌';
      case 'DOCUMENT_SHARED':
        return '📎';
      case 'SYSTEM_UPDATE':
        return '🆙';
      case 'MESSAGE_RECEIVED':
        return '💬';
      default:
        return '🔔';
    }
  };

  const getTimeAgo = (dateString: string) => {
    const date = new Date(dateString);
    const now = new Date();
    const diff = now.getTime() - date.getTime();
    const minutes = Math.floor(diff / (1000 * 60));
    
    if (minutes < 1) return 'Just now';
    if (minutes < 60) return `${minutes}m ago`;
    
    const hours = Math.floor(minutes / 60);
    if (hours < 24) return `${hours}h ago`;
    
    const days = Math.floor(hours / 24);
    if (days < 7) return `${days}d ago`;
    
    const weeks = Math.floor(days / 7);
    if (weeks < 4) return `${weeks}w ago`;
    
    return date.toLocaleDateString();
  };

  const getNotificationTypeColor = (type: string) => {
    switch (type.toUpperCase()) {
      case 'APPLICATION_SUBMITTED':
      case 'APPLICATION_APPROVED':
        return 'text-blue-600';
      case 'INTERVIEW_SCHEDULED':
        return 'text-purple-600';
      case 'OFFER_EXTENDED':
        return 'text-green-600';
      case 'APPLICATION_WITHDRAWN':
      case 'INTERVIEW_CANCELLED':
        return 'text-red-600';
      case 'PIPELINE_STAGE_CHANGED':
        return 'text-orange-600';
      default:
        return 'text-gray-600';
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center py-8">
        <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (notifications.length === 0) {
    return (
      <div className="text-center py-8 text-gray-500">
        <div className="text-4xl mb-2">🔔</div>
        <p>No notifications yet.</p>
        <p className="text-sm">We&apos;ll notify you about important updates to your applications.</p>
      </div>
    );
  }

  return (
    <div className="w-full">
      {showHeader && (
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-lg font-medium text-gray-900">
            Recent Notifications
          </h3>
          {unreadCount > 0 && (
            <div className="flex items-center gap-2">
              <div className="w-2 h-2 bg-blue-600 rounded-full animate-pulse"></div>
              <span className="text-sm font-medium text-blue-600">
                {unreadCount} unread
              </span>
            </div>
          )}
        </div>
      )}

      <div className="space-y-3">
        {notifications.map((notification) => (
          <div 
            key={notification.id} 
            className={`p-4 border rounded-lg cursor-pointer transition-all duration-200 ${
              notification.isRead 
                ? 'bg-white border-gray-200 hover:bg-gray-50' 
                : 'bg-blue-50 border-blue-200 hover:bg-blue-100 shadow-sm'
            }`}
            onClick={() => !notification.isRead && handleMarkAsRead(notification.id)}
          >
            <div className="flex items-start gap-3">
              <div className={`text-lg flex-shrink-0 ${getNotificationTypeColor(notification.type)}`}>
                {getNotificationIcon(notification.type)}
              </div>
              
              <div className="flex-1 min-w-0">
                <div className="flex items-start justify-between gap-2 mb-1">
                  <h4 className={`text-sm font-medium ${
                    notification.isRead ? 'text-gray-700' : 'text-gray-900'
                  }`}>
                    {notification.title}
                  </h4>
                  
                  <div className="flex items-center gap-2 flex-shrink-0">
                    <span className="text-xs text-gray-500">
                      {getTimeAgo(notification.createdAt)}
                    </span>
                    {!notification.isRead && (
                      <div className="w-2 h-2 bg-blue-600 rounded-full"></div>
                    )}
                  </div>
                </div>

                <p className={`text-sm ${
                  notification.isRead ? 'text-gray-500' : 'text-gray-700'
                } mb-2`}>
                  {notification.message.length > 120 
                    ? notification.message.substring(0, 120) + '...'
                    : notification.message
                  }
                </p>

                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-3 text-xs text-gray-400">
                    <span className="capitalize">{notification.channel.toLowerCase()}</span>
                    {notification.jobTitle && (
                      <>
                        <span>•</span>
                        <span>{notification.jobTitle}</span>
                      </>
                    )}
                  </div>
                  
                  {notification.readAt && (
                    <span className="text-xs text-gray-400">
                      Read {getTimeAgo(notification.readAt)}
                    </span>
                  )}
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>

      {notifications.length >= limit && (
        <div className="mt-4 text-center">
          <button className="text-sm text-blue-600 hover:text-blue-700 font-medium">
            View all notifications →
          </button>
        </div>
      )}
    </div>
  );
}
