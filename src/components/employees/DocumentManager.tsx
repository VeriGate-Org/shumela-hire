'use client';

import { useState, useEffect, useRef, useCallback } from 'react';
import {
  DocumentIcon,
  ArrowUpTrayIcon,
  TrashIcon,
  ExclamationTriangleIcon,
} from '@heroicons/react/24/outline';
import { useToast } from '@/components/Toast';
import type { EmployeeDocument, EmployeeDocumentType } from '@/types/employee';
import { DOCUMENT_TYPE_LABELS } from '@/types/employee';
import { getDocuments, uploadDocument, deleteDocument } from '@/services/employeeService';

interface DocumentManagerProps {
  employeeId: number;
  canManage: boolean;
}

export default function DocumentManager({ employeeId, canManage }: DocumentManagerProps) {
  const { toast } = useToast();
  const fileRef = useRef<HTMLInputElement>(null);
  const [documents, setDocuments] = useState<EmployeeDocument[]>([]);
  const [loading, setLoading] = useState(true);
  const [uploading, setUploading] = useState(false);

  // Upload form state
  const [showUpload, setShowUpload] = useState(false);
  const [uploadType, setUploadType] = useState<EmployeeDocumentType | ''>('');
  const [uploadTitle, setUploadTitle] = useState('');
  const [uploadDescription, setUploadDescription] = useState('');
  const [uploadExpiry, setUploadExpiry] = useState('');
  const [selectedFile, setSelectedFile] = useState<File | null>(null);

  const loadDocs = useCallback(() => {
    setLoading(true);
    getDocuments(employeeId)
      .then(setDocuments)
      .catch(() => setDocuments([]))
      .finally(() => setLoading(false));
  }, [employeeId]);

  useEffect(() => { loadDocs(); }, [loadDocs]);

  const handleUpload = async () => {
    if (!selectedFile || !uploadType || !uploadTitle.trim()) {
      toast('Please fill in all required fields and select a file', 'error');
      return;
    }
    setUploading(true);
    try {
      await uploadDocument(
        employeeId,
        uploadType,
        uploadTitle,
        selectedFile,
        uploadDescription || undefined,
        uploadExpiry || undefined,
      );
      toast('Document uploaded', 'success');
      setShowUpload(false);
      resetUploadForm();
      loadDocs();
    } catch (err) {
      toast((err as Error).message || 'Upload failed', 'error');
    } finally {
      setUploading(false);
    }
  };

  const handleDelete = async (docId: number, title: string) => {
    if (!confirm(`Delete "${title}"? This cannot be undone.`)) return;
    try {
      await deleteDocument(employeeId, docId);
      toast('Document deleted', 'success');
      loadDocs();
    } catch (err) {
      toast((err as Error).message || 'Delete failed', 'error');
    }
  };

  const resetUploadForm = () => {
    setUploadType('');
    setUploadTitle('');
    setUploadDescription('');
    setUploadExpiry('');
    setSelectedFile(null);
    if (fileRef.current) fileRef.current.value = '';
  };

  const formatFileSize = (bytes: number) => {
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-cta" />
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {/* Upload button */}
      {canManage && !showUpload && (
        <div className="flex justify-end">
          <button onClick={() => setShowUpload(true)} className="btn-primary text-sm inline-flex items-center gap-1.5">
            <ArrowUpTrayIcon className="w-4 h-4" />
            Upload Document
          </button>
        </div>
      )}

      {/* Upload form */}
      {showUpload && (
        <div className="enterprise-card p-4 space-y-3">
          <h3 className="text-sm font-semibold text-foreground">Upload Document</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-muted-foreground uppercase tracking-wide mb-1">Document Type *</label>
              <select
                value={uploadType}
                onChange={(e) => setUploadType(e.target.value as EmployeeDocumentType)}
                className="w-full px-3 py-2 border border-border rounded-sm text-sm bg-card text-foreground focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
              >
                <option value="">Select type</option>
                {(Object.keys(DOCUMENT_TYPE_LABELS) as EmployeeDocumentType[]).map(t => (
                  <option key={t} value={t}>{DOCUMENT_TYPE_LABELS[t]}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-xs font-semibold text-muted-foreground uppercase tracking-wide mb-1">Title *</label>
              <input
                type="text"
                value={uploadTitle}
                onChange={(e) => setUploadTitle(e.target.value)}
                className="w-full px-3 py-2 border border-border rounded-sm text-sm bg-card text-foreground focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
                placeholder="e.g. SA ID Copy"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-muted-foreground uppercase tracking-wide mb-1">Description</label>
              <input
                type="text"
                value={uploadDescription}
                onChange={(e) => setUploadDescription(e.target.value)}
                className="w-full px-3 py-2 border border-border rounded-sm text-sm bg-card text-foreground focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-muted-foreground uppercase tracking-wide mb-1">Expiry Date</label>
              <input
                type="date"
                value={uploadExpiry}
                onChange={(e) => setUploadExpiry(e.target.value)}
                className="w-full px-3 py-2 border border-border rounded-sm text-sm bg-card text-foreground focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
              />
            </div>
            <div className="md:col-span-2">
              <label className="block text-xs font-semibold text-muted-foreground uppercase tracking-wide mb-1">File *</label>
              <input
                ref={fileRef}
                type="file"
                onChange={(e) => setSelectedFile(e.target.files?.[0] || null)}
                className="w-full text-sm text-foreground file:mr-3 file:py-1.5 file:px-3 file:rounded-full file:border-0 file:text-sm file:font-medium file:bg-primary/10 file:text-primary hover:file:bg-primary/20"
              />
            </div>
          </div>
          <div className="flex justify-end gap-2">
            <button onClick={() => { setShowUpload(false); resetUploadForm(); }} className="px-3 py-1.5 text-sm text-muted-foreground hover:text-foreground border border-border rounded-full">
              Cancel
            </button>
            <button onClick={handleUpload} disabled={uploading} className="btn-primary text-sm disabled:opacity-50">
              {uploading ? 'Uploading...' : 'Upload'}
            </button>
          </div>
        </div>
      )}

      {/* Documents list */}
      {documents.length === 0 ? (
        <div className="text-center py-12 text-muted-foreground">
          <DocumentIcon className="w-12 h-12 mx-auto mb-2 opacity-40" />
          <p className="text-sm">No documents uploaded yet.</p>
        </div>
      ) : (
        <div className="space-y-2">
          {documents.map((doc) => (
            <div key={doc.id} className="enterprise-card p-3 flex items-center gap-3">
              <div className="w-9 h-9 rounded-sm bg-primary/10 text-primary flex items-center justify-center shrink-0">
                <DocumentIcon className="w-5 h-5" />
              </div>
              <div className="min-w-0 flex-1">
                <div className="flex items-center gap-2">
                  <p className="text-sm font-medium text-foreground truncate">{doc.title}</p>
                  <span className="inline-flex items-center rounded-full border border-border px-1.5 py-0.5 text-[10px] font-medium text-muted-foreground shrink-0">
                    {DOCUMENT_TYPE_LABELS[doc.documentType]}
                  </span>
                  {doc.isExpired && (
                    <span className="inline-flex items-center gap-0.5 rounded-full border border-red-200 bg-red-50 px-1.5 py-0.5 text-[10px] font-medium text-red-700 shrink-0">
                      <ExclamationTriangleIcon className="w-3 h-3" />
                      Expired
                    </span>
                  )}
                </div>
                <div className="flex gap-3 text-xs text-muted-foreground mt-0.5">
                  <span>{doc.filename}</span>
                  <span>{formatFileSize(doc.fileSize)}</span>
                  {doc.expiryDate && <span>Expires: {new Date(doc.expiryDate).toLocaleDateString('en-ZA')}</span>}
                </div>
              </div>
              <div className="flex items-center gap-1 shrink-0">
                {doc.fileUrl && (
                  <a
                    href={doc.fileUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="px-2.5 py-1 text-xs font-medium text-primary hover:text-primary/80 border border-border rounded-full"
                  >
                    View
                  </a>
                )}
                {canManage && (
                  <button
                    onClick={() => handleDelete(doc.id, doc.title)}
                    className="p-1.5 text-muted-foreground hover:text-red-600 rounded-sm"
                    aria-label="Delete document"
                  >
                    <TrashIcon className="w-4 h-4" />
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
