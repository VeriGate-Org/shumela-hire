import { DeltaSync } from '../deltaSync';
import { IndexedDBManager } from '../offlineStorage';

// --------------------------------------------------------------------------
// Minimal IndexedDBManager mock
// --------------------------------------------------------------------------
const mockStore: Record<string, unknown> = {};

function resetMockStorage() {
  Object.keys(mockStore).forEach((k) => delete mockStore[k]);
}

const mockStorage = {
  get: jest.fn(async (key: string) => mockStore[key] ?? null),
  store: jest.fn(async (key: string, value: unknown) => { mockStore[key] = value; }),
  remove: jest.fn(async (key: string) => { delete mockStore[key]; }),
  clear: jest.fn(async () => resetMockStorage()),
  getAllKeys: jest.fn(async () => Object.keys(mockStore)),
  storeOfflineAction: jest.fn(),
  getOfflineActions: jest.fn(async () => []),
  removeOfflineAction: jest.fn(),
  logConflict: jest.fn(),
  getUnacknowledgedConflicts: jest.fn(async () => []),
  acknowledgeConflict: jest.fn(),
} as unknown as IndexedDBManager;

// --------------------------------------------------------------------------
// Helpers: build a mock fetch response without using the global Response ctor
// --------------------------------------------------------------------------
function makeFetchResponse(
  data: unknown,
  serverTimestamp = '2025-06-01T00:00:00Z',
  status = 200
) {
  const body = JSON.stringify({ data, serverTimestamp });
  return {
    ok: status >= 200 && status < 300,
    status,
    statusText: status === 200 ? 'OK' : 'Error',
    json: jest.fn(async () => JSON.parse(body)),
    text: jest.fn(async () => body),
    headers: new Map([['Content-Type', 'application/json']]),
  };
}

describe('DeltaSync', () => {
  let deltaSync: DeltaSync;
  let fetchMock: jest.Mock;

  beforeEach(() => {
    jest.clearAllMocks();
    resetMockStorage();

    (mockStorage.get as jest.Mock).mockImplementation(async () => null);
    (mockStorage.store as jest.Mock).mockImplementation(async (key: string, value: unknown) => {
      mockStore[key] = value;
    });
    (mockStorage.remove as jest.Mock).mockImplementation(async (key: string) => {
      delete mockStore[key];
    });

    fetchMock = jest.fn();
    global.fetch = fetchMock;

    deltaSync = new DeltaSync(mockStorage);
  });

  describe('fetchDelta', () => {
    it('calls the API without a since param on first fetch', async () => {
      fetchMock.mockResolvedValueOnce(makeFetchResponse([{ id: 1, name: 'Alice' }]));

      await deltaSync.fetchDelta('/api/employees', 'employees');

      const calledUrl = fetchMock.mock.calls[0][0] as string;
      expect(calledUrl).not.toContain('since=');
      expect(calledUrl).toBe('/api/employees');
    });

    it('includes since= param after first successful fetch', async () => {
      (mockStorage.get as jest.Mock).mockResolvedValueOnce({ lastSyncedAt: '2025-05-01T00:00:00Z' });
      fetchMock.mockResolvedValueOnce(makeFetchResponse([]));

      await deltaSync.fetchDelta('/api/employees', 'employees');

      const calledUrl = fetchMock.mock.calls[0][0] as string;
      expect(calledUrl).toContain('since=2025-05-01');
    });

    it('stores the serverTimestamp after a successful fetch', async () => {
      fetchMock.mockResolvedValueOnce(makeFetchResponse([], '2025-07-15T12:00:00Z'));

      await deltaSync.fetchDelta('/api/employees', 'employees');

      expect(mockStorage.store).toHaveBeenCalledWith(
        'delta-sync-meta:employees',
        expect.objectContaining({ lastSyncedAt: '2025-07-15T12:00:00Z' })
      );
    });

    it('returns the data array from the response', async () => {
      const items = [{ id: 1 }, { id: 2 }];
      fetchMock.mockResolvedValueOnce(makeFetchResponse(items));

      const result = await deltaSync.fetchDelta('/api/employees', 'employees');
      expect(result).toEqual(items);
    });

    it('throws when the API returns a non-ok status', async () => {
      fetchMock.mockResolvedValueOnce(makeFetchResponse(null, '', 404));

      await expect(deltaSync.fetchDelta('/api/employees', 'employees')).rejects.toThrow(
        'DeltaSync fetch failed'
      );
    });

    it('appends field selection when fields option is provided', async () => {
      fetchMock.mockResolvedValueOnce(makeFetchResponse([]));

      await deltaSync.fetchDelta('/api/employees', 'employees', {
        fields: ['id', 'name'],
      });

      const calledUrl = fetchMock.mock.calls[0][0] as string;
      expect(calledUrl).toContain('fields=id%2Cname');
    });

    it('appends pageSize as limit param when provided', async () => {
      fetchMock.mockResolvedValueOnce(makeFetchResponse([]));

      await deltaSync.fetchDelta('/api/employees', 'employees', { pageSize: 50 });

      const calledUrl = fetchMock.mock.calls[0][0] as string;
      expect(calledUrl).toContain('limit=50');
    });
  });

  describe('resetSyncTimestamp', () => {
    it('removes the stored timestamp', async () => {
      await deltaSync.resetSyncTimestamp('employees');
      expect(mockStorage.remove).toHaveBeenCalledWith('delta-sync-meta:employees');
    });
  });

  describe('setLastSyncTimestamp / getLastSyncTimestamp', () => {
    it('returns null when no timestamp has been stored', async () => {
      const ts = await deltaSync.getLastSyncTimestamp('employees');
      expect(ts).toBeNull();
    });

    it('returns the stored timestamp after setLastSyncTimestamp', async () => {
      // Use the real in-memory mockStore for this round-trip test
      (mockStorage.store as jest.Mock).mockImplementation(async (key: string, value: unknown) => {
        mockStore[key] = value;
      });
      (mockStorage.get as jest.Mock).mockImplementation(async (key: string) => {
        const entry = mockStore[key] as { lastSyncedAt?: string } | undefined;
        return entry ?? null;
      });

      await deltaSync.setLastSyncTimestamp('leave', '2025-09-01T00:00:00Z');
      const ts = await deltaSync.getLastSyncTimestamp('leave');
      expect(ts).toBe('2025-09-01T00:00:00Z');
    });
  });
});
