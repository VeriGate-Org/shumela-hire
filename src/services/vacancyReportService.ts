const API_BASE = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export interface VacancySummaryData {
  jobId: string;
  reportGeneratedAt: string;
  totalApplications: number;
  applicationsByStatus: Record<string, number>;
  applicationsBySource: Record<string, number>;
  shortlistedCount: number;
  demographics: {
    totalApplicants: number;
    totalWithConsent: number;
    genderBreakdown: Record<string, number>;
    raceBreakdown: Record<string, number>;
    disabilityBreakdown: Record<string, number>;
    citizenshipBreakdown: Record<string, number>;
  };
}

function getAuthHeaders(): Record<string, string> {
  const token = sessionStorage.getItem('jwt_token');
  return {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json',
  };
}

export const vacancyReportService = {
  async getVacancySummary(jobId: string): Promise<VacancySummaryData> {
    const response = await fetch(`${API_BASE}/api/vacancy-reports/${jobId}/summary`, {
      headers: getAuthHeaders(),
    });
    if (!response.ok) throw new Error('Failed to fetch vacancy summary');
    return response.json();
  },

  async downloadVacancySummaryPdf(jobId: string): Promise<void> {
    const response = await fetch(`${API_BASE}/api/vacancy-reports/${jobId}/summary/pdf`, {
      headers: getAuthHeaders(),
    });
    if (!response.ok) throw new Error('Failed to download vacancy summary PDF');
    const blob = await response.blob();
    downloadBlob(blob, `vacancy-summary-${jobId}.pdf`);
  },

  async downloadShortlistPackPdf(jobId: string): Promise<void> {
    const response = await fetch(`${API_BASE}/api/vacancy-reports/${jobId}/shortlist-pack/pdf`, {
      headers: getAuthHeaders(),
    });
    if (!response.ok) throw new Error('Failed to download shortlist pack PDF');
    const blob = await response.blob();
    downloadBlob(blob, `shortlist-pack-${jobId}.pdf`);
  },

  async downloadDemographicsReportPdf(jobId: string): Promise<void> {
    const response = await fetch(`${API_BASE}/api/vacancy-reports/${jobId}/demographics/pdf`, {
      headers: getAuthHeaders(),
    });
    if (!response.ok) throw new Error('Failed to download demographics report PDF');
    const blob = await response.blob();
    downloadBlob(blob, `demographics-report-${jobId}.pdf`);
  },
};

function downloadBlob(blob: Blob, filename: string) {
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  window.URL.revokeObjectURL(url);
  a.remove();
}
