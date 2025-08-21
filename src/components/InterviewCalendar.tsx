'use client';

import React, { useState, useEffect } from 'react';

interface Interview {
  id: number;
  title: string;
  type: string;
  typeDisplayName: string;
  round: string;
  roundDisplayName: string;
  status: string;
  statusDisplayName: string;
  scheduledAt: string;
  durationMinutes: number;
  location?: string;
  meetingLink?: string;
  phoneNumber?: string;
  meetingRoom?: string;
  interviewerId: number;
  canBeRescheduled: boolean;
  canBeCancelled: boolean;
  canBeStarted: boolean;
  canBeCompleted: boolean;
  requiresFeedback: boolean;
  isOverdue: boolean;
  isUpcoming: boolean;
  application: {
    id: number;
    applicant: {
      id: number;
      firstName: string;
      lastName: string;
      email: string;
    };
    jobPosting: {
      id: number;
      title: string;
      department: string;
    };
  };
}

interface InterviewCalendarProps {
  interviews: Interview[];
  onInterviewSelect?: (interview: Interview) => void;
  onInterviewUpdate?: (interviewId: number, updatedInterview: Interview) => void;
}

const WEEKDAYS = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
const MONTHS = [
  'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December'
];

export default function InterviewCalendar({ interviews, onInterviewSelect, onInterviewUpdate }: InterviewCalendarProps) {
  const [currentDate, setCurrentDate] = useState(new Date());
  const [view, setView] = useState<'month' | 'week' | 'day'>('month');
  const [selectedInterview, setSelectedInterview] = useState<Interview | null>(null);
  const [showActionModal, setShowActionModal] = useState(false);

  const getCalendarDays = () => {
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth();
    
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const startDate = new Date(firstDay);
    startDate.setDate(startDate.getDate() - firstDay.getDay());
    
    const days = [];
    const current = new Date(startDate);
    
    for (let i = 0; i < 42; i++) {
      days.push(new Date(current));
      current.setDate(current.getDate() + 1);
    }
    
    return days;
  };

  const getWeekDays = () => {
    const startOfWeek = new Date(currentDate);
    startOfWeek.setDate(currentDate.getDate() - currentDate.getDay());
    
    const days = [];
    for (let i = 0; i < 7; i++) {
      const day = new Date(startOfWeek);
      day.setDate(startOfWeek.getDate() + i);
      days.push(day);
    }
    
    return days;
  };

  const getInterviewsForDate = (date: Date) => {
    return interviews.filter(interview => {
      const interviewDate = new Date(interview.scheduledAt);
      return (
        interviewDate.getDate() === date.getDate() &&
        interviewDate.getMonth() === date.getMonth() &&
        interviewDate.getFullYear() === date.getFullYear()
      );
    });
  };

  const getInterviewsForWeek = () => {
    const weekDays = getWeekDays();
    const weekInterviews = [];
    
    for (const day of weekDays) {
      const dayInterviews = getInterviewsForDate(day);
      weekInterviews.push({ date: day, interviews: dayInterviews });
    }
    
    return weekInterviews;
  };

  const getDayTimeSlots = () => {
    const slots = [];
    for (let hour = 8; hour < 18; hour++) {
      slots.push(hour);
    }
    return slots;
  };

  const getInterviewsForHour = (hour: number) => {
    return interviews.filter(interview => {
      const interviewDate = new Date(interview.scheduledAt);
      return (
        interviewDate.getDate() === currentDate.getDate() &&
        interviewDate.getMonth() === currentDate.getMonth() &&
        interviewDate.getFullYear() === currentDate.getFullYear() &&
        interviewDate.getHours() === hour
      );
    });
  };

  const navigateDate = (direction: 'prev' | 'next') => {
    const newDate = new Date(currentDate);
    
    if (view === 'month') {
      newDate.setMonth(currentDate.getMonth() + (direction === 'next' ? 1 : -1));
    } else if (view === 'week') {
      newDate.setDate(currentDate.getDate() + (direction === 'next' ? 7 : -7));
    } else {
      newDate.setDate(currentDate.getDate() + (direction === 'next' ? 1 : -1));
    }
    
    setCurrentDate(newDate);
  };

  const handleInterviewClick = (interview: Interview) => {
    setSelectedInterview(interview);
    setShowActionModal(true);
    if (onInterviewSelect) {
      onInterviewSelect(interview);
    }
  };

  const handleInterviewAction = async (action: string, interviewId: number, params?: any) => {
    try {
      let url = `/api/interviews/${interviewId}/${action}`;
      let method = 'POST';
      
      if (params) {
        const searchParams = new URLSearchParams(params);
        url += `?${searchParams.toString()}`;
      }
      
      const response = await fetch(url, { method });
      
      if (response.ok) {
        const updatedInterview = await response.json();
        if (onInterviewUpdate) {
          onInterviewUpdate(interviewId, updatedInterview);
        }
        setShowActionModal(false);
        setSelectedInterview(null);
      } else {
        const errorData = await response.json();
        alert(errorData.message || `Failed to ${action.replace('-', ' ')}`);
      }
    } catch (error) {
      console.error(`Error performing ${action}:`, error);
      alert(`An error occurred while performing ${action.replace('-', ' ')}`);
    }
  };

  const handleReschedule = () => {
    const newTime = prompt('Enter new date and time (YYYY-MM-DD HH:MM):');
    const reason = prompt('Reason for rescheduling:');
    
    if (newTime && reason && selectedInterview) {
      try {
        const newScheduledAt = new Date(newTime.replace(' ', 'T')).toISOString();
        handleInterviewAction('reschedule', selectedInterview.id, {
          newScheduledAt,
          reason,
          rescheduledBy: 1
        });
      } catch (error) {
        alert('Invalid date format. Please use YYYY-MM-DD HH:MM');
      }
    }
  };

  const handleCancel = () => {
    const reason = prompt('Reason for cancellation:');
    
    if (reason && selectedInterview) {
      handleInterviewAction('cancel', selectedInterview.id, {
        reason,
        cancelledBy: 1
      });
    }
  };

  const getInterviewStatusColor = (interview: Interview) => {
    switch (interview.status) {
      case 'SCHEDULED':
        return 'bg-blue-100 text-blue-800 border-blue-200';
      case 'RESCHEDULED':
        return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      case 'IN_PROGRESS':
        return 'bg-green-100 text-green-800 border-green-200';
      case 'COMPLETED':
        return 'bg-purple-100 text-purple-800 border-purple-200';
      case 'CANCELLED':
        return 'bg-red-100 text-red-800 border-red-200';
      case 'NO_SHOW':
        return 'bg-gray-100 text-gray-800 border-gray-200';
      default:
        return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  };

  const formatTime = (dateString: string) => {
    return new Date(dateString).toLocaleTimeString('en-US', {
      hour: 'numeric',
      minute: '2-digit',
      hour12: true
    });
  };

  const isToday = (date: Date) => {
    const today = new Date();
    return (
      date.getDate() === today.getDate() &&
      date.getMonth() === today.getMonth() &&
      date.getFullYear() === today.getFullYear()
    );
  };

  const isCurrentMonth = (date: Date) => {
    return date.getMonth() === currentDate.getMonth();
  };

  return (
    <div className="bg-white rounded-lg shadow">
      {/* Calendar Header */}
      <div className="px-6 py-4 border-b border-gray-200">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-4">
            <h2 className="text-xl font-semibold text-gray-900">
              {view === 'month' && `${MONTHS[currentDate.getMonth()]} ${currentDate.getFullYear()}`}
              {view === 'week' && `Week of ${currentDate.toLocaleDateString()}`}
              {view === 'day' && currentDate.toLocaleDateString()}
            </h2>
            <div className="flex items-center space-x-2">
              <button
                onClick={() => navigateDate('prev')}
                className="p-2 text-gray-400 hover:text-gray-600"
              >
                ←
              </button>
              <button
                onClick={() => setCurrentDate(new Date())}
                className="px-3 py-1 text-sm text-blue-600 hover:text-blue-800"
              >
                Today
              </button>
              <button
                onClick={() => navigateDate('next')}
                className="p-2 text-gray-400 hover:text-gray-600"
              >
                →
              </button>
            </div>
          </div>
          
          <div className="flex items-center space-x-2">
            <div className="flex rounded-md shadow-sm">
              <button
                onClick={() => setView('month')}
                className={`px-3 py-2 text-sm font-medium rounded-l-md border ${
                  view === 'month'
                    ? 'bg-blue-50 text-blue-700 border-blue-200'
                    : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-50'
                }`}
              >
                Month
              </button>
              <button
                onClick={() => setView('week')}
                className={`px-3 py-2 text-sm font-medium border-t border-b ${
                  view === 'week'
                    ? 'bg-blue-50 text-blue-700 border-blue-200'
                    : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-50'
                }`}
              >
                Week
              </button>
              <button
                onClick={() => setView('day')}
                className={`px-3 py-2 text-sm font-medium rounded-r-md border ${
                  view === 'day'
                    ? 'bg-blue-50 text-blue-700 border-blue-200'
                    : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-50'
                }`}
              >
                Day
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Calendar Content */}
      <div className="p-6">
        {/* Month View */}
        {view === 'month' && (
          <div>
            {/* Weekday Headers */}
            <div className="grid grid-cols-7 gap-1 mb-2">
              {WEEKDAYS.map(day => (
                <div key={day} className="p-2 text-center text-sm font-medium text-gray-500">
                  {day}
                </div>
              ))}
            </div>
            
            {/* Calendar Grid */}
            <div className="grid grid-cols-7 gap-1">
              {getCalendarDays().map((date, index) => {
                const dayInterviews = getInterviewsForDate(date);
                
                return (
                  <div
                    key={index}
                    className={`min-h-[100px] p-2 border rounded-md ${
                      isCurrentMonth(date) ? 'bg-white' : 'bg-gray-50'
                    } ${isToday(date) ? 'bg-blue-50 border-blue-200' : 'border-gray-200'}`}
                  >
                    <div className={`text-sm font-medium mb-1 ${
                      isCurrentMonth(date) ? 'text-gray-900' : 'text-gray-400'
                    } ${isToday(date) ? 'text-blue-600' : ''}`}>
                      {date.getDate()}
                    </div>
                    
                    <div className="space-y-1">
                      {dayInterviews.slice(0, 3).map(interview => (
                        <div
                          key={interview.id}
                          onClick={() => handleInterviewClick(interview)}
                          className={`text-xs p-1 rounded border cursor-pointer hover:shadow-sm ${getInterviewStatusColor(interview)}`}
                        >
                          <div className="font-medium truncate">
                            {formatTime(interview.scheduledAt)}
                          </div>
                          <div className="truncate">
                            {interview.application.applicant.firstName} {interview.application.applicant.lastName}
                          </div>
                        </div>
                      ))}
                      {dayInterviews.length > 3 && (
                        <div className="text-xs text-gray-500 text-center">
                          +{dayInterviews.length - 3} more
                        </div>
                      )}
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        )}

        {/* Week View */}
        {view === 'week' && (
          <div>
            <div className="grid grid-cols-8 gap-1">
              {/* Time column header */}
              <div className="p-2"></div>
              
              {/* Day headers */}
              {getWeekDays().map(date => (
                <div key={date.toString()} className={`p-2 text-center border-b ${
                  isToday(date) ? 'bg-blue-50 text-blue-600 font-medium' : 'text-gray-700'
                }`}>
                  <div className="text-sm">{WEEKDAYS[date.getDay()]}</div>
                  <div className="text-lg">{date.getDate()}</div>
                </div>
              ))}
              
              {/* Time slots */}
              {getDayTimeSlots().map(hour => (
                <React.Fragment key={hour}>
                  <div className="p-2 text-sm text-gray-500 border-r">
                    {hour}:00
                  </div>
                  {getWeekDays().map(date => {
                    const hourInterviews = interviews.filter(interview => {
                      const interviewDate = new Date(interview.scheduledAt);
                      return (
                        interviewDate.getDate() === date.getDate() &&
                        interviewDate.getMonth() === date.getMonth() &&
                        interviewDate.getFullYear() === date.getFullYear() &&
                        interviewDate.getHours() === hour
                      );
                    });
                    
                    return (
                      <div key={`${date.toString()}-${hour}`} className="p-1 border-b border-r min-h-[60px]">
                        {hourInterviews.map(interview => (
                          <div
                            key={interview.id}
                            onClick={() => handleInterviewClick(interview)}
                            className={`text-xs p-1 rounded border cursor-pointer hover:shadow-sm mb-1 ${getInterviewStatusColor(interview)}`}
                          >
                            <div className="font-medium">
                              {interview.application.applicant.firstName} {interview.application.applicant.lastName}
                            </div>
                            <div className="truncate">
                              {interview.roundDisplayName}
                            </div>
                          </div>
                        ))}
                      </div>
                    );
                  })}
                </React.Fragment>
              ))}
            </div>
          </div>
        )}

        {/* Day View */}
        {view === 'day' && (
          <div>
            <div className="space-y-1">
              {getDayTimeSlots().map(hour => {
                const hourInterviews = getInterviewsForHour(hour);
                
                return (
                  <div key={hour} className="flex border-b border-gray-200">
                    <div className="w-20 p-2 text-sm text-gray-500 border-r">
                      {hour}:00
                    </div>
                    <div className="flex-1 p-2 min-h-[80px]">
                      {hourInterviews.map(interview => (
                        <div
                          key={interview.id}
                          onClick={() => handleInterviewClick(interview)}
                          className={`p-3 rounded-md border cursor-pointer hover:shadow-md mb-2 ${getInterviewStatusColor(interview)}`}
                        >
                          <div className="flex justify-between items-start">
                            <div>
                              <div className="font-medium text-sm">
                                {formatTime(interview.scheduledAt)} - {interview.title}
                              </div>
                              <div className="text-sm">
                                {interview.application.applicant.firstName} {interview.application.applicant.lastName}
                              </div>
                              <div className="text-xs text-gray-600">
                                {interview.application.jobPosting.title}
                              </div>
                            </div>
                            <div className="text-xs">
                              {interview.durationMinutes}min
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        )}
      </div>

      {/* Interview Action Modal */}
      {showActionModal && selectedInterview && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-xl max-w-md w-full m-4">
            <div className="px-6 py-4 border-b border-gray-200">
              <h3 className="text-lg font-medium text-gray-900">
                {selectedInterview.title}
              </h3>
              <p className="text-sm text-gray-600">
                {selectedInterview.application.applicant.firstName} {selectedInterview.application.applicant.lastName}
              </p>
            </div>
            
            <div className="px-6 py-4">
              <div className="space-y-2 text-sm">
                <p><strong>Type:</strong> {selectedInterview.typeDisplayName}</p>
                <p><strong>Round:</strong> {selectedInterview.roundDisplayName}</p>
                <p><strong>Status:</strong> {selectedInterview.statusDisplayName}</p>
                <p><strong>Date:</strong> {new Date(selectedInterview.scheduledAt).toLocaleString()}</p>
                <p><strong>Duration:</strong> {selectedInterview.durationMinutes} minutes</p>
                {selectedInterview.location && <p><strong>Location:</strong> {selectedInterview.location}</p>}
                {selectedInterview.meetingLink && (
                  <p><strong>Meeting:</strong> <a href={selectedInterview.meetingLink} target="_blank" rel="noopener noreferrer" className="text-blue-600 hover:underline">Join Meeting</a></p>
                )}
              </div>
            </div>
            
            <div className="px-6 py-4 border-t border-gray-200 flex justify-end space-x-3">
              <button
                onClick={() => setShowActionModal(false)}
                className="px-3 py-2 text-sm text-gray-700 hover:text-gray-900"
              >
                Close
              </button>
              
              {selectedInterview.canBeStarted && (
                <button
                  onClick={() => handleInterviewAction('start', selectedInterview.id, { startedBy: 1 })}
                  className="px-3 py-2 bg-green-600 text-white text-sm rounded hover:bg-green-700"
                >
                  Start
                </button>
              )}
              
              {selectedInterview.canBeCompleted && (
                <button
                  onClick={() => handleInterviewAction('complete', selectedInterview.id, { completedBy: 1 })}
                  className="px-3 py-2 bg-blue-600 text-white text-sm rounded hover:bg-blue-700"
                >
                  Complete
                </button>
              )}
              
              {selectedInterview.canBeRescheduled && (
                <button
                  onClick={handleReschedule}
                  className="px-3 py-2 bg-yellow-600 text-white text-sm rounded hover:bg-yellow-700"
                >
                  Reschedule
                </button>
              )}
              
              {selectedInterview.canBeCancelled && (
                <button
                  onClick={handleCancel}
                  className="px-3 py-2 bg-red-600 text-white text-sm rounded hover:bg-red-700"
                >
                  Cancel
                </button>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}