'use client';

import React, { useState, useEffect } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { engagementService } from '@/services/engagementService';
import type { SocialPost } from '@/types/engagement';
import { SocialPostType } from '@/types/engagement';

export default function SocialFeedPage() {
  const { user } = useAuth();
  const [posts, setPosts] = useState<SocialPost[]>([]);
  const [loading, setLoading] = useState(true);
  const [showCreate, setShowCreate] = useState(false);

  // Create form state
  const [postType, setPostType] = useState<SocialPostType>(SocialPostType.UPDATE);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');

  useEffect(() => {
    loadFeed();
  }, []);

  const loadFeed = async () => {
    setLoading(true);
    try {
      const data = await engagementService.getSocialFeed();
      setPosts(data.content || []);
    } catch {
      setPosts([]);
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await engagementService.createSocialPost({
        authorId: parseInt(user?.id || '0'),
        postType,
        title: title || undefined,
        content,
      });
      setShowCreate(false);
      setTitle('');
      setContent('');
      loadFeed();
    } catch {
      // Handle error
    }
  };

  const handleLike = async (id: number) => {
    try {
      await engagementService.likeSocialPost(id);
      loadFeed();
    } catch {
      // Handle error
    }
  };

  const getPostTypeIcon = (type: SocialPostType): string => {
    switch (type) {
      case SocialPostType.ANNOUNCEMENT: return '📢';
      case SocialPostType.ACHIEVEMENT: return '🏆';
      case SocialPostType.EVENT: return '📅';
      case SocialPostType.POLL: return '📊';
      default: return '💬';
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-white shadow">
        <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Social Feed</h1>
              <p className="mt-1 text-sm text-gray-600">Company updates, announcements, and achievements</p>
            </div>
            <button
              onClick={() => setShowCreate(!showCreate)}
              className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-full text-white bg-violet-600 hover:bg-violet-700"
            >
              {showCreate ? 'Cancel' : 'New Post'}
            </button>
          </div>
        </div>
      </div>

      <div className="max-w-3xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        {/* Create Post Form */}
        {showCreate && (
          <div className="bg-white shadow rounded-lg p-6 mb-6">
            <h2 className="text-lg font-semibold mb-4">Create Post</h2>
            <form onSubmit={handleCreate} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700">Post Type</label>
                <select value={postType} onChange={(e) => setPostType(e.target.value as SocialPostType)}
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-violet-500 focus:ring-violet-500 sm:text-sm">
                  {Object.values(SocialPostType).map((t) => (
                    <option key={t} value={t}>{t}</option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Title (optional)</label>
                <input type="text" value={title} onChange={(e) => setTitle(e.target.value)}
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-violet-500 focus:ring-violet-500 sm:text-sm" />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Content</label>
                <textarea value={content} onChange={(e) => setContent(e.target.value)} required rows={4}
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-violet-500 focus:ring-violet-500 sm:text-sm"
                  placeholder="Share something with your team..." />
              </div>
              <button type="submit"
                className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-full text-white bg-violet-600 hover:bg-violet-700">
                Post
              </button>
            </form>
          </div>
        )}

        {/* Feed */}
        {loading ? (
          <div className="text-center py-12 text-gray-500">Loading feed...</div>
        ) : posts.length === 0 ? (
          <div className="text-center py-12 text-gray-500">No posts yet. Be the first to share!</div>
        ) : (
          <div className="space-y-4">
            {posts.map((post) => (
              <div key={post.id} className={`bg-white shadow rounded-lg p-6 ${post.isPinned ? 'border-l-4 border-yellow-400' : ''}`}>
                <div className="flex items-start justify-between">
                  <div className="flex items-center space-x-3">
                    <span className="text-xl">{getPostTypeIcon(post.postType)}</span>
                    <div>
                      <span className="font-semibold text-gray-900">{post.authorName}</span>
                      {post.isPinned && (
                        <span className="ml-2 text-xs text-yellow-600">Pinned</span>
                      )}
                      <div className="text-xs text-gray-400">
                        {new Date(post.createdAt).toLocaleDateString()} · {post.postType}
                      </div>
                    </div>
                  </div>
                </div>
                {post.title && (
                  <h3 className="mt-3 text-lg font-semibold text-gray-900">{post.title}</h3>
                )}
                <p className="mt-2 text-gray-700 whitespace-pre-wrap">{post.content}</p>
                <div className="mt-4 flex items-center space-x-6">
                  <button onClick={() => handleLike(post.id)}
                    className="flex items-center text-sm text-gray-500 hover:text-violet-600">
                    <span className="mr-1">👍</span> {post.likeCount}
                  </button>
                  <span className="flex items-center text-sm text-gray-500">
                    <span className="mr-1">💬</span> {post.commentCount}
                  </span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
