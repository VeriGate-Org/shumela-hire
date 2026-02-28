import {
  buildFieldSelectParams,
  buildFieldSelectUrl,
  mergeQueryParams,
  projectFields,
  EMPLOYEE_LIST_FIELDS,
} from '../fieldSelection';

describe('buildFieldSelectParams', () => {
  it('returns URLSearchParams with fields joined by comma', () => {
    const params = buildFieldSelectParams(['id', 'name', 'email']);
    expect(params.get('fields')).toBe('id,name,email');
  });

  it('returns empty URLSearchParams when no fields supplied', () => {
    const params = buildFieldSelectParams([]);
    expect(params.get('fields')).toBeNull();
  });

  it('works with the predefined EMPLOYEE_LIST_FIELDS constant', () => {
    const params = buildFieldSelectParams(EMPLOYEE_LIST_FIELDS);
    expect(params.get('fields')).toContain('id');
    expect(params.get('fields')).toContain('email');
  });
});

describe('buildFieldSelectUrl', () => {
  it('appends ?fields= to a plain URL', () => {
    const url = buildFieldSelectUrl('/api/employees', ['id', 'name']);
    expect(url).toBe('/api/employees?fields=id%2Cname');
  });

  it('merges fields with existing query params', () => {
    const url = buildFieldSelectUrl('/api/employees?page=1', ['id']);
    expect(url).toContain('page=1');
    expect(url).toContain('fields=id');
  });

  it('returns the base URL unchanged when fields array is empty', () => {
    const url = buildFieldSelectUrl('/api/employees', []);
    expect(url).toBe('/api/employees');
  });
});

describe('mergeQueryParams', () => {
  it('merges multiple param sources', () => {
    const merged = mergeQueryParams(
      { page: '1' },
      new URLSearchParams('limit=20'),
      { since: '2025-01-01' }
    );
    expect(merged.get('page')).toBe('1');
    expect(merged.get('limit')).toBe('20');
    expect(merged.get('since')).toBe('2025-01-01');
  });

  it('later sources override earlier ones for duplicate keys', () => {
    const merged = mergeQueryParams({ limit: '10' }, { limit: '50' });
    expect(merged.get('limit')).toBe('50');
  });

  it('ignores null / undefined sources', () => {
    const merged = mergeQueryParams(null, undefined, { a: '1' });
    expect(merged.get('a')).toBe('1');
  });
});

describe('projectFields', () => {
  it('returns only specified keys', () => {
    const obj = { id: '1', name: 'Alice', salary: 9000, internal: true };
    const projected = projectFields(obj, ['id', 'name']);
    expect(projected).toEqual({ id: '1', name: 'Alice' });
    expect('salary' in projected).toBe(false);
  });

  it('handles missing keys gracefully', () => {
    const obj = { id: '1' } as Record<string, unknown>;
    const projected = projectFields(obj, ['id', 'nonExistent' as keyof typeof obj]);
    expect(projected).toEqual({ id: '1' });
  });
});
