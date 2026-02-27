'use client';

import React, { useState, useEffect } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { engagementService } from '@/services/engagementService';
import type { Recognition } from '@/types/engagement';
import { RecognitionBadge, getBadgeDisplayName } from '@/types/engagement';

export default function RecognitionPage() {
  const { user } = useAuth();
  const [recognitions, setRecognitions] = useState<Recognition[]>([]);
  const [loading, setLoading] = useState(true);
  const [showGive, setShowGive] = useState(false);

  // Give recognition form state
  const [receiverId, setReceiverId] = useState('');
  const [badge, setBadge] = useState<RecognitionBadge>(RecognitionBadge.TEAM_PLAYER);
  const [message, setMessage] = useState('');

  useEffect(() => {
    loadRecognitions();
  }, []);

  const loadRecognitions = async () => {
    setLoading(true);
    try {
      const data = await engagementService.getRecognitionFeed();
      setRecognitions(data.content || []);
    } catch {
      setRecognitions([]);
    } finally {
      setLoading(false);
    }
  };

  const handleGive = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await engagementService.createRecognition({
        giverId: parseInt(user?.id || '0'),
        receiverId: parseInt(receiverId),
        badge,
        message,
      });
      setShowGive(false);
      setReceiverId('');
      setMessage('');
      loadRecognitions();
    } catch {
      // Handle error
    }
  };

  const getBadgeColor = (badge: RecognitionBadge): string => {
    switch (badge) {
      case RecognitionBadge.STAR_PERFORMER: return 'bg-yellow-100 text-yellow-800';
      case RecognitionBadge.LEADER: return 'bg-purple-100 text-purple-800';
      case RecognitionBadge.INNOVATOR: return 'bg-blue-100 text-blue-800';
      case RecognitionBadge.TEAM_PLAYER: return 'bg-green-100 text-green-800';
      case RecognitionBadge.EXTRA_MILE: return 'bg-orange-100 text-orange-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-white shadow">
        <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Recognition Wall</h1>
              <p className="mt-1 text-sm text-gray-600">Give kudos and celebrate your colleagues</p>
            </div>
            <button
              onClick={() => setShowGive(!showGive)}
              className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-full text-white bg-violet-600 hover:bg-violet-700"
            >
              {showGive ? 'Cancel' : 'Give Kudos'}
            </button>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        {/* Give Kudos Form */}
        {showGive && (
          <div className="bg-white shadow rounded-lg p-6 mb-6">
            <h2 className="text-lg font-semibold mb-4">Give Recognition</h2>
            <form onSubmit={handleGive} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700">Recipient Employee ID</label>
                <input type="number" value={receiverId} onChange={(e) => setReceiverId(e.target.value)} required
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-violet-500 focus:ring-violet-500 sm:text-sm"
                  placeholder="Enter employee ID" />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Badge</label>
                <select value={badge} onChange={(e) => setBadge(e.target.value as RecognitionBadge)}
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-violet-500 focus:ring-violet-500 sm:text-sm">
                  {Object.values(RecognitionBadge).map((b) => (
                    <option key={b} value={b}>{getBadgeDisplayName(b)}</option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Message</label>
                <textarea value={message} onChange={(e) => setMessage(e.target.value)} required rows={3}
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-violet-500 focus:ring-violet-500 sm:text-sm"
                  placeholder="Why are you recognizing this person?" />
              </div>
              <button type="submit"
                className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-full text-white bg-violet-600 hover:bg-violet-700">
                Send Recognition
              </button>
            </form>
          </div>
        )}

        {/* Recognition Feed */}
        {loading ? (
          <div className="text-center py-12 text-gray-500">Loading recognitions...</div>
        ) : recognitions.length === 0 ? (
          <div className="text-center py-12 text-gray-500">No recognitions yet. Be the first to give kudos!</div>
        ) : (
          <div className="space-y-4">
            {recognitions.map((recognition) => (
              <div key={recognition.id} className="bg-white shadow rounded-lg p-6">
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <div className="flex items-center space-x-2 mb-2">
                      <span className="font-semibold text-gray-900">{recognition.giverName}</span>
                      <span className="text-gray-500">recognized</span>
                      <span className="font-semibold text-gray-900">{recognition.receiverName}</span>
                    </div>
                    <p className="text-gray-700">{recognition.message}</p>
                    <div className="mt-3 flex items-center space-x-3">
                      <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getBadgeColor(recognition.badge)}`}>
                        {getBadgeDisplayName(recognition.badge)}
                      </span>
                      <span className="text-sm text-gray-500">{recognition.points} points</span>
                      <span className="text-xs text-gray-400">
                        {new Date(recognition.createdAt).toLocaleDateString()}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
