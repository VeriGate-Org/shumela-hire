import { JobBoardPosting, JobBoardType, AvailableBoard } from '@/types/jobBoard';
import { apiFetch } from '@/lib/api-fetch';

export const jobBoardService = {
  async postToBoard(jobPostingId: string, boardType: JobBoardType, boardConfig?: string): Promise<JobBoardPosting> {
    const response = await apiFetch('/api/job-boards/postings', {
      method: 'POST',
      body: JSON.stringify({ jobPostingId, boardType, boardConfig }),
    });
    if (!response.ok) throw new Error('Failed to post to board');
    return response.json();
  },

  async syncPosting(id: number): Promise<JobBoardPosting> {
    const response = await apiFetch(`/api/job-boards/postings/${id}/sync`, { method: 'POST' });
    if (!response.ok) throw new Error('Failed to sync posting');
    return response.json();
  },

  async removePosting(id: number): Promise<JobBoardPosting> {
    const response = await apiFetch(`/api/job-boards/postings/${id}`, { method: 'DELETE' });
    if (!response.ok) throw new Error('Failed to remove posting');
    return response.json();
  },

  async getPostingsByJob(jobId: string): Promise<JobBoardPosting[]> {
    const response = await apiFetch(`/api/job-boards/postings/job/${jobId}`);
    if (!response.ok) return [];
    return response.json();
  },

  async getAvailableBoards(): Promise<AvailableBoard[]> {
    const response = await apiFetch('/api/job-boards/available-boards');
    if (!response.ok) return [];
    return response.json();
  },
};
