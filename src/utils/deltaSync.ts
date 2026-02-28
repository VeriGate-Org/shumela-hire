/**
 * Delta Sync Utility
 *
 * Implements timestamp-based incremental data fetching so that low-bandwidth
 * connections only transfer records that changed since the last successful sync.
 *
 * Usage:
 *   const sync = new DeltaSync(storage);
 *   const employees = await sync.fetchDelta('/api/employees', 'employees');
 */

import { IndexedDBManager } from './offlineStorage';

/** Shape returned by delta-aware API endpoints */
export interface DeltaResponse<T = unknown> {
  data: T[];
  /** ISO-8601 timestamp of the server snapshot used for this response */
  serverTimestamp: string;
  /** Whether more pages are available */
  hasMore?: boolean;
  /** Opaque cursor for next page (server-defined) */
  nextCursor?: string;
}

export interface DeltaSyncOptions {
  /** Extra query params to forward (e.g. field selection) */
  params?: Record<string, string>;
  /** Request fields to include (forwarded as ?fields=...) */
  fields?: string[];
  /** Page size limit */
  pageSize?: number;
  /** Auth headers or any additional headers */
  headers?: Record<string, string>;
}

const SYNC_META_STORE = 'delta-sync-meta';

export class DeltaSync {
  private storage: IndexedDBManager;

  constructor(storage: IndexedDBManager) {
    this.storage = storage;
  }

  /**
   * Fetch only records changed since the last successful sync for `entityKey`.
   * On first run (no stored timestamp) it performs a full fetch.
   *
   * @param baseUrl  API endpoint, e.g. "/api/employees"
   * @param entityKey  Logical name used to persist the last-sync timestamp
   * @param options  Additional fetch options
   * @returns  Array of (possibly partial) records
   */
  async fetchDelta<T = unknown>(
    baseUrl: string,
    entityKey: string,
    options: DeltaSyncOptions = {}
  ): Promise<T[]> {
    const lastSyncedAt = await this.getLastSyncTimestamp(entityKey);
    const url = this.buildUrl(baseUrl, lastSyncedAt, options);

    const headers: Record<string, string> = {
      'Accept-Encoding': 'gzip, br',
      'Content-Type': 'application/json',
      ...options.headers,
    };

    const response = await fetch(url, { headers });

    if (!response.ok) {
      throw new Error(
        `DeltaSync fetch failed for ${entityKey}: ${response.status} ${response.statusText}`
      );
    }

    const body: DeltaResponse<T> = await response.json();

    // Persist the server timestamp so the next call only fetches newer records
    await this.setLastSyncTimestamp(entityKey, body.serverTimestamp);

    return body.data;
  }

  /** Return the stored last-sync timestamp for an entity, or null if never synced. */
  async getLastSyncTimestamp(entityKey: string): Promise<string | null> {
    const meta = await this.storage.get(`${SYNC_META_STORE}:${entityKey}`);
    return meta?.lastSyncedAt ?? null;
  }

  /** Persist a new last-sync timestamp. */
  async setLastSyncTimestamp(entityKey: string, timestamp: string): Promise<void> {
    await this.storage.store(`${SYNC_META_STORE}:${entityKey}`, {
      lastSyncedAt: timestamp,
      updatedAt: new Date().toISOString(),
    });
  }

  /** Clear the sync timestamp for an entity, forcing a full re-fetch next time. */
  async resetSyncTimestamp(entityKey: string): Promise<void> {
    await this.storage.remove(`${SYNC_META_STORE}:${entityKey}`);
  }

  // ---------------------------------------------------------------------------
  // Private helpers
  // ---------------------------------------------------------------------------

  private buildUrl(
    baseUrl: string,
    since: string | null,
    options: DeltaSyncOptions
  ): string {
    const params = new URLSearchParams(options.params ?? {});

    if (since) {
      params.set('since', since);
    }

    if (options.fields && options.fields.length > 0) {
      params.set('fields', options.fields.join(','));
    }

    if (options.pageSize) {
      params.set('limit', String(options.pageSize));
    }

    const qs = params.toString();
    return qs ? `${baseUrl}?${qs}` : baseUrl;
  }
}
