// IndexedDB wrapper for offline storage
export interface OfflineStorageManager {
  store: (key: string, data: any) => Promise<void>;
  get: (key: string) => Promise<any>;
  remove: (key: string) => Promise<void>;
  clear: () => Promise<void>;
  getAllKeys: () => Promise<string[]>;
}

/** HR-specific offline action types (spec F-5.4.3) */
export type HRActionType =
  | 'leave-request'
  | 'leave-cancel'
  | 'clock-in'
  | 'clock-out'
  | 'approval-approve'
  | 'approval-reject'
  | 'offline-get'
  | 'offline-post'
  | 'offline-put'
  | 'offline-patch'
  | 'offline-delete'
  | string;

export interface OfflineAction {
  id?: number;
  type: HRActionType;
  url: string;
  method: string;
  headers: Record<string, string>;
  body?: string;
  timestamp?: number;
  /** Client-side version of the entity at the time the action was queued */
  localVersion?: number;
  /** Optimistic local entity snapshot (for conflict detection on sync) */
  localSnapshot?: unknown;
}

/** Result returned after attempting to sync a queued action */
export interface SyncResult {
  action: OfflineAction;
  success: boolean;
  /** When true, the server state differs from the local snapshot (conflict) */
  conflict: boolean;
  /** Server's authoritative state when a conflict is detected */
  serverState?: unknown;
  error?: string;
}

export class IndexedDBManager implements OfflineStorageManager {
  private dbName = 'shumelahire-offline';
  private version = 2; // bumped to add conflict-log store
  private db: IDBDatabase | null = null;

  private async openDB(): Promise<IDBDatabase> {
    if (this.db) return this.db;

    return new Promise((resolve, reject) => {
      const request = indexedDB.open(this.dbName, this.version);

      request.onerror = () => reject(request.error);
      request.onsuccess = () => {
        this.db = request.result;
        resolve(this.db);
      };

      request.onupgradeneeded = (event) => {
        const db = (event.target as IDBOpenDBRequest).result;

        // Create object stores
        if (!db.objectStoreNames.contains('offline-actions')) {
          const actionsStore = db.createObjectStore('offline-actions', { keyPath: 'id', autoIncrement: true });
          actionsStore.createIndex('type', 'type', { unique: false });
          actionsStore.createIndex('timestamp', 'timestamp', { unique: false });
        }

        if (!db.objectStoreNames.contains('cached-data')) {
          db.createObjectStore('cached-data', { keyPath: 'key' });
        }

        if (!db.objectStoreNames.contains('user-preferences')) {
          db.createObjectStore('user-preferences', { keyPath: 'key' });
        }

        // v2: store for conflict notifications (server-wins strategy)
        if (!db.objectStoreNames.contains('conflict-log')) {
          const conflictStore = db.createObjectStore('conflict-log', { keyPath: 'id', autoIncrement: true });
          conflictStore.createIndex('resolvedAt', 'resolvedAt', { unique: false });
          conflictStore.createIndex('acknowledged', 'acknowledged', { unique: false });
        }
      };
    });
  }

  async store(key: string, data: any): Promise<void> {
    const db = await this.openDB();
    const transaction = db.transaction(['cached-data'], 'readwrite');
    const store = transaction.objectStore('cached-data');
    
    await new Promise<void>((resolve, reject) => {
      const request = store.put({ 
        key, 
        data, 
        timestamp: Date.now(),
        expires: Date.now() + (24 * 60 * 60 * 1000) // 24 hours
      });
      request.onerror = () => reject(request.error);
      request.onsuccess = () => resolve();
    });
  }

  async get(key: string): Promise<any> {
    const db = await this.openDB();
    const transaction = db.transaction(['cached-data'], 'readonly');
    const store = transaction.objectStore('cached-data');
    
    return new Promise((resolve, reject) => {
      const request = store.get(key);
      request.onerror = () => reject(request.error);
      request.onsuccess = () => {
        const result = request.result;
        if (!result) {
          resolve(null);
          return;
        }

        // Check if data has expired
        if (result.expires && Date.now() > result.expires) {
          // Remove expired data
          this.remove(key);
          resolve(null);
          return;
        }

        resolve(result.data);
      };
    });
  }

  async remove(key: string): Promise<void> {
    const db = await this.openDB();
    const transaction = db.transaction(['cached-data'], 'readwrite');
    const store = transaction.objectStore('cached-data');
    
    await new Promise<void>((resolve, reject) => {
      const request = store.delete(key);
      request.onerror = () => reject(request.error);
      request.onsuccess = () => resolve();
    });
  }

  async clear(): Promise<void> {
    const db = await this.openDB();
    const transaction = db.transaction(['cached-data'], 'readwrite');
    const store = transaction.objectStore('cached-data');
    
    await new Promise<void>((resolve, reject) => {
      const request = store.clear();
      request.onerror = () => reject(request.error);
      request.onsuccess = () => resolve();
    });
  }

  async getAllKeys(): Promise<string[]> {
    const db = await this.openDB();
    const transaction = db.transaction(['cached-data'], 'readonly');
    const store = transaction.objectStore('cached-data');
    
    return new Promise((resolve, reject) => {
      const request = store.getAllKeys();
      request.onerror = () => reject(request.error);
      request.onsuccess = () => resolve(request.result as string[]);
    });
  }

  // Offline action methods
  async storeOfflineAction(action: OfflineAction): Promise<void> {
    const db = await this.openDB();
    const transaction = db.transaction(['offline-actions'], 'readwrite');
    const store = transaction.objectStore('offline-actions');
    
    await new Promise<void>((resolve, reject) => {
      const request = store.add({
        ...action,
        timestamp: Date.now()
      });
      request.onerror = () => reject(request.error);
      request.onsuccess = () => resolve();
    });
  }

  async getOfflineActions(type?: string): Promise<OfflineAction[]> {
    const db = await this.openDB();
    const transaction = db.transaction(['offline-actions'], 'readonly');
    const store = transaction.objectStore('offline-actions');
    
    return new Promise((resolve, reject) => {
      let request: IDBRequest;
      
      if (type) {
        const index = store.index('type');
        request = index.getAll(type);
      } else {
        request = store.getAll();
      }
      
      request.onerror = () => reject(request.error);
      request.onsuccess = () => resolve(request.result);
    });
  }

  async removeOfflineAction(id: number): Promise<void> {
    const db = await this.openDB();
    const transaction = db.transaction(['offline-actions'], 'readwrite');
    const store = transaction.objectStore('offline-actions');

    await new Promise<void>((resolve, reject) => {
      const request = store.delete(id);
      request.onerror = () => reject(request.error);
      request.onsuccess = () => resolve();
    });
  }

  // ---------------------------------------------------------------------------
  // Conflict log (server-wins strategy – spec F-5.4.3)
  // ---------------------------------------------------------------------------

  /**
   * Record a conflict that was resolved by the server-wins strategy.
   * The UI reads unacknowledged conflicts to notify the user.
   */
  async logConflict(entry: {
    actionType: string;
    localSnapshot: unknown;
    serverState: unknown;
    url: string;
  }): Promise<void> {
    const db = await this.openDB();
    const transaction = db.transaction(['conflict-log'], 'readwrite');
    const store = transaction.objectStore('conflict-log');

    await new Promise<void>((resolve, reject) => {
      const request = store.add({
        ...entry,
        resolvedAt: Date.now(),
        acknowledged: false,
      });
      request.onerror = () => reject(request.error);
      request.onsuccess = () => resolve();
    });
  }

  /** Return all unacknowledged conflicts. */
  async getUnacknowledgedConflicts(): Promise<Array<{
    id: number;
    actionType: string;
    localSnapshot: unknown;
    serverState: unknown;
    url: string;
    resolvedAt: number;
    acknowledged: boolean;
  }>> {
    const db = await this.openDB();
    const transaction = db.transaction(['conflict-log'], 'readonly');
    const store = transaction.objectStore('conflict-log');
    const index = store.index('acknowledged');

    return new Promise((resolve, reject) => {
      const request = index.getAll(IDBKeyRange.only(false));
      request.onerror = () => reject(request.error);
      request.onsuccess = () => resolve(request.result);
    });
  }

  /** Mark a conflict entry as acknowledged by the user. */
  async acknowledgeConflict(id: number): Promise<void> {
    const db = await this.openDB();
    const transaction = db.transaction(['conflict-log'], 'readwrite');
    const store = transaction.objectStore('conflict-log');

    await new Promise<void>((resolve, reject) => {
      // Read then update (IDB has no partial update API)
      const getReq = store.get(id);
      getReq.onerror = () => reject(getReq.error);
      getReq.onsuccess = () => {
        const record = getReq.result;
        if (!record) { resolve(); return; }
        const putReq = store.put({ ...record, acknowledged: true });
        putReq.onerror = () => reject(putReq.error);
        putReq.onsuccess = () => resolve();
      };
    });
  }
}
