/**
 * Field-Level API Selection Utility
 *
 * Allows API consumers to request only the fields they need, reducing payload
 * size on low-bandwidth connections (spec F-5.4.2).
 *
 * The server is expected to honour a `?fields=` query parameter.
 *
 * Usage:
 *   const url = buildFieldSelectUrl('/api/employees', ['id', 'name', 'email']);
 *   // → '/api/employees?fields=id%2Cname%2Cemail'
 *
 *   // Or compose with other params:
 *   const params = buildFieldSelectParams(['id', 'name']);
 *   fetch(`/api/employees?${params}`);
 */

/** Pre-defined field sets for common entities (reduce network payload) */
export const EMPLOYEE_LIST_FIELDS = ['id', 'firstName', 'lastName', 'email', 'jobTitle', 'department', 'status'] as const;
export const EMPLOYEE_DETAIL_FIELDS = [...EMPLOYEE_LIST_FIELDS, 'phone', 'startDate', 'manager', 'location', 'salary'] as const;

export const APPLICATION_LIST_FIELDS = ['id', 'applicantName', 'jobTitle', 'status', 'appliedAt', 'stage'] as const;
export const APPLICATION_DETAIL_FIELDS = [...APPLICATION_LIST_FIELDS, 'email', 'phone', 'resumeUrl', 'notes', 'score'] as const;

export const LEAVE_LIST_FIELDS = ['id', 'employeeId', 'leaveType', 'startDate', 'endDate', 'status', 'requestedAt'] as const;
export const ATTENDANCE_LIST_FIELDS = ['id', 'employeeId', 'clockIn', 'clockOut', 'date', 'status'] as const;

/**
 * Build a URLSearchParams object with the `fields` param set.
 * Can be merged with other params using spread or URLSearchParams.set().
 */
export function buildFieldSelectParams(
  fields: readonly string[] | string[]
): URLSearchParams {
  const params = new URLSearchParams();
  if (fields.length > 0) {
    params.set('fields', fields.join(','));
  }
  return params;
}

/**
 * Append field selection to an existing URL string.
 * Merges gracefully with any already-present query string.
 */
export function buildFieldSelectUrl(baseUrl: string, fields: readonly string[] | string[]): string {
  if (fields.length === 0) return baseUrl;

  const [path, existingQs] = baseUrl.split('?');
  const params = existingQs ? new URLSearchParams(existingQs) : new URLSearchParams();
  params.set('fields', fields.join(','));
  return `${path}?${params.toString()}`;
}

/**
 * Merge multiple param sources (field selection, pagination, delta-sync, etc.)
 * into a single query string to avoid duplicated logic at call sites.
 */
export function mergeQueryParams(
  ...sources: Array<Record<string, string> | URLSearchParams | null | undefined>
): URLSearchParams {
  const merged = new URLSearchParams();
  for (const source of sources) {
    if (!source) continue;
    const entries = source instanceof URLSearchParams ? source.entries() : Object.entries(source);
    for (const [key, value] of entries) {
      merged.set(key, value);
    }
  }
  return merged;
}

/** Filter a plain object to only include the specified keys (client-side projection). */
export function projectFields<T extends Record<string, unknown>>(
  obj: T,
  fields: (keyof T)[]
): Partial<T> {
  return fields.reduce<Partial<T>>((acc, key) => {
    if (key in obj) acc[key] = obj[key];
    return acc;
  }, {});
}
