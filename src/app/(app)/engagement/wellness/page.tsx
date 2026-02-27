'use client';

import React, { useState, useEffect } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { engagementService } from '@/services/engagementService';
import type { WellnessCheckIn, WellnessProgram } from '@/types/engagement';
import { MoodRating, WellnessCategory, getMoodEmoji, getMoodColor } from '@/types/engagement';

export default function WellnessPage() {
  const { user } = useAuth();
  const [checkIns, setCheckIns] = useState<WellnessCheckIn[]>([]);
  const [programs, setPrograms] = useState<WellnessProgram[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'checkin' | 'history' | 'programs'>('checkin');

  // Check-in form state
  const [moodRating, setMoodRating] = useState<MoodRating>(MoodRating.GOOD);
  const [energyLevel, setEnergyLevel] = useState(5);
  const [stressLevel, setStressLevel] = useState(5);
  const [notes, setNotes] = useState('');
  const [submitted, setSubmitted] = useState(false);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const [programsData] = await Promise.all([
        engagementService.getWellnessPrograms(true),
      ]);
      setPrograms(programsData);

      if (user?.id) {
        const history = await engagementService.getCheckInsForEmployee(parseInt(user.id));
        setCheckIns(history);
      }
    } catch {
      // Handle errors
    } finally {
      setLoading(false);
    }
  };

  const handleCheckIn = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await engagementService.createWellnessCheckIn({
        employeeId: parseInt(user?.id || '0'),
        moodRating: moodRating,
        energyLevel,
        stressLevel,
        notes: notes || undefined,
      });
      setSubmitted(true);
      loadData();
    } catch {
      // Handle error
    }
  };

  const moodOptions = Object.values(MoodRating);

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-white shadow">
        <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
          <h1 className="text-3xl font-bold text-gray-900">Wellness Center</h1>
          <p className="mt-1 text-sm text-gray-600">Check in on your wellness and explore programs</p>
        </div>
      </div>

      <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        {/* Tabs */}
        <div className="flex space-x-4 mb-6 border-b border-gray-200">
          {(['checkin', 'history', 'programs'] as const).map((tab) => (
            <button key={tab} onClick={() => setActiveTab(tab)}
              className={`pb-3 px-1 text-sm font-medium border-b-2 ${
                activeTab === tab ? 'border-violet-500 text-violet-600' : 'border-transparent text-gray-500 hover:text-gray-700'
              }`}>
              {tab === 'checkin' ? 'Check In' : tab === 'history' ? 'My History' : 'Programs'}
            </button>
          ))}
        </div>

        {/* Check-In Tab */}
        {activeTab === 'checkin' && (
          <div className="bg-white shadow rounded-lg p-6">
            {submitted ? (
              <div className="text-center py-8">
                <div className="text-4xl mb-4">{getMoodEmoji(moodRating)}</div>
                <h2 className="text-xl font-semibold text-gray-900">Thank you for checking in!</h2>
                <p className="text-gray-500 mt-2">Your wellness matters to us.</p>
                <button onClick={() => setSubmitted(false)}
                  className="mt-4 px-4 py-2 text-sm bg-violet-100 text-violet-800 rounded-full hover:bg-violet-200">
                  Check In Again
                </button>
              </div>
            ) : (
              <form onSubmit={handleCheckIn} className="space-y-6">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-3">How are you feeling today?</label>
                  <div className="flex space-x-3">
                    {moodOptions.map((mood) => (
                      <button key={mood} type="button" onClick={() => setMoodRating(mood)}
                        className={`flex-1 py-3 px-2 rounded-lg text-center transition ${
                          moodRating === mood ? 'ring-2 ring-violet-500 bg-violet-50' : 'bg-gray-50 hover:bg-gray-100'
                        }`}>
                        <div className="text-2xl">{getMoodEmoji(mood)}</div>
                        <div className="text-xs mt-1 text-gray-600">{mood}</div>
                      </button>
                    ))}
                  </div>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Energy Level: {energyLevel}/10</label>
                  <input type="range" min="1" max="10" value={energyLevel}
                    onChange={(e) => setEnergyLevel(parseInt(e.target.value))}
                    className="mt-2 w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Stress Level: {stressLevel}/10</label>
                  <input type="range" min="1" max="10" value={stressLevel}
                    onChange={(e) => setStressLevel(parseInt(e.target.value))}
                    className="mt-2 w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Notes (optional)</label>
                  <textarea value={notes} onChange={(e) => setNotes(e.target.value)} rows={3}
                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-violet-500 focus:ring-violet-500 sm:text-sm"
                    placeholder="Anything you'd like to share..." />
                </div>
                <button type="submit"
                  className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-full text-white bg-violet-600 hover:bg-violet-700">
                  Submit Check-In
                </button>
              </form>
            )}
          </div>
        )}

        {/* History Tab */}
        {activeTab === 'history' && (
          <div className="space-y-4">
            {loading ? (
              <div className="text-center py-12 text-gray-500">Loading history...</div>
            ) : checkIns.length === 0 ? (
              <div className="text-center py-12 text-gray-500">No check-ins yet</div>
            ) : (
              checkIns.map((checkIn) => (
                <div key={checkIn.id} className="bg-white shadow rounded-lg p-4">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center space-x-3">
                      <span className="text-2xl">{getMoodEmoji(checkIn.moodRating)}</span>
                      <div>
                        <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getMoodColor(checkIn.moodRating)}`}>
                          {checkIn.moodRating}
                        </span>
                        <div className="text-xs text-gray-500 mt-1">
                          Energy: {checkIn.energyLevel}/10 | Stress: {checkIn.stressLevel}/10
                        </div>
                      </div>
                    </div>
                    <span className="text-sm text-gray-400">{checkIn.checkInDate}</span>
                  </div>
                  {checkIn.notes && <p className="mt-2 text-sm text-gray-600">{checkIn.notes}</p>}
                </div>
              ))
            )}
          </div>
        )}

        {/* Programs Tab */}
        {activeTab === 'programs' && (
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {loading ? (
              <div className="col-span-full text-center py-12 text-gray-500">Loading programs...</div>
            ) : programs.length === 0 ? (
              <div className="col-span-full text-center py-12 text-gray-500">No active wellness programs</div>
            ) : (
              programs.map((program) => (
                <div key={program.id} className="bg-white shadow rounded-lg p-6">
                  <h3 className="text-lg font-semibold text-gray-900">{program.name}</h3>
                  {program.description && (
                    <p className="mt-1 text-sm text-gray-500">{program.description}</p>
                  )}
                  <div className="mt-3">
                    <span className="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-800">
                      {program.category}
                    </span>
                  </div>
                  {program.startDate && (
                    <p className="mt-2 text-xs text-gray-400">
                      {program.startDate} - {program.endDate || 'Ongoing'}
                    </p>
                  )}
                </div>
              ))
            )}
          </div>
        )}
      </div>
    </div>
  );
}
