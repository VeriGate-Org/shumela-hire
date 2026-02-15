'use client';

import React, { useState } from 'react';
import { vacancyReportService } from '@/services/vacancyReportService';

interface VacancyReportActionsProps {
  jobId: string;
  showDemographics?: boolean;
}

export default function VacancyReportActions({ jobId, showDemographics = false }: VacancyReportActionsProps) {
  const [downloading, setDownloading] = useState<string | null>(null);

  const handleDownload = async (type: 'summary' | 'shortlist' | 'demographics') => {
    setDownloading(type);
    try {
      switch (type) {
        case 'summary':
          await vacancyReportService.downloadVacancySummaryPdf(jobId);
          break;
        case 'shortlist':
          await vacancyReportService.downloadShortlistPackPdf(jobId);
          break;
        case 'demographics':
          await vacancyReportService.downloadDemographicsReportPdf(jobId);
          break;
      }
    } catch (error) {
      console.error(`Failed to download ${type} report:`, error);
      alert(`Failed to download ${type} report. Please try again.`);
    } finally {
      setDownloading(null);
    }
  };

  return (
    <div className="space-y-3">
      <h4 className="text-sm font-semibold text-gray-700 uppercase tracking-wider">
        IDC Reports
      </h4>

      <div className="flex flex-wrap gap-2">
        <button
          onClick={() => handleDownload('summary')}
          disabled={downloading !== null}
          className="inline-flex items-center px-3 py-2 text-sm font-medium text-violet-700 bg-violet-50 border border-violet-200 rounded-md hover:bg-violet-100 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {downloading === 'summary' ? 'Generating...' : 'Vacancy Summary'}
        </button>

        <button
          onClick={() => handleDownload('shortlist')}
          disabled={downloading !== null}
          className="inline-flex items-center px-3 py-2 text-sm font-medium text-violet-700 bg-violet-50 border border-violet-200 rounded-md hover:bg-violet-100 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {downloading === 'shortlist' ? 'Generating...' : 'Shortlist Pack'}
        </button>

        {showDemographics && (
          <button
            onClick={() => handleDownload('demographics')}
            disabled={downloading !== null}
            className="inline-flex items-center px-3 py-2 text-sm font-medium text-violet-700 bg-violet-50 border border-violet-200 rounded-md hover:bg-violet-100 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {downloading === 'demographics' ? 'Generating...' : 'Demographics / EE'}
          </button>
        )}
      </div>

      <p className="text-xs text-gray-500">
        Reports are generated in accordance with POPIA. Demographic data is only included where explicit consent has been provided.
      </p>
    </div>
  );
}
