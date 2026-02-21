'use client';

import { ShareIcon, BookmarkIcon } from '@heroicons/react/24/outline';
import { useToast } from './Toast';

interface JobDetailClientProps {
  jobTitle: string;
  companyName?: string;
}

export default function JobDetailClient({ jobTitle, companyName }: JobDetailClientProps) {
  const { toast } = useToast();
  const handleShare = async () => {
    if (navigator.share) {
      try {
        await navigator.share({
          title: jobTitle,
          text: `Check out this job opportunity: ${jobTitle}${companyName ? ` at ${companyName}` : ''}`,
          url: window.location.href
        });
      } catch (err) {
        // User cancelled sharing
      }
    } else {
      // Fallback to copying URL
      navigator.clipboard.writeText(window.location.href);
      toast('Job URL copied to clipboard', 'success');
    }
  };

  const handleSave = () => {
    // You can implement bookmark functionality here
    // For now, just show a placeholder
    toast('Bookmark functionality would be implemented here', 'info');
  };

  return (
    <div className="flex items-center space-x-3">
      <button
        onClick={handleShare}
        className="inline-flex items-center px-3 py-2 border border-gray-300 rounded-sm hover:bg-gray-50 transition-colors"
      >
        <ShareIcon className="w-4 h-4 mr-2" />
        Share
      </button>
      <button 
        onClick={handleSave}
        className="inline-flex items-center px-3 py-2 border border-gray-300 rounded-sm hover:bg-gray-50 transition-colors"
      >
        <BookmarkIcon className="w-4 h-4 mr-2" />
        Save
      </button>
    </div>
  );
}