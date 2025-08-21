import { renderHook, waitFor } from '@testing-library/react'
import { SWRConfig } from 'swr'
import { useApiData } from '../useApiData'
import { mockApiResponses } from '../../test-utils'

// Mock fetch
global.fetch = jest.fn()
const mockFetch = fetch as jest.MockedFunction<typeof fetch>

// Wrapper component for SWR
const wrapper = ({ children }: { children: React.ReactNode }) => (
  <SWRConfig
    value={{
      dedupingInterval: 0,
      revalidateOnFocus: false,
      revalidateOnMount: true,
      provider: () => new Map(),
    }}
  >
    {children}
  </SWRConfig>
)

describe('useApiData', () => {
  beforeEach(() => {
    mockFetch.mockClear()
  })

  afterEach(() => {
    jest.clearAllMocks()
  })

  it('fetches data successfully', async () => {
    const mockResponse = {
      data: mockApiResponses.applications.data,
      success: true,
      message: 'Data fetched successfully'
    }

    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: async () => mockResponse,
    } as Response)

    const { result } = renderHook(
      () => useApiData('/applications'),
      { wrapper }
    )

    expect(result.current.isLoading).toBe(true)
    expect(result.current.data).toBeUndefined()
    expect(result.current.error).toBeUndefined()

    await waitFor(() => {
      expect(result.current.isLoading).toBe(false)
    })

    expect(result.current.data).toEqual(mockResponse.data)
    expect(result.current.error).toBeUndefined()
    expect(result.current.isError).toBe(false)
    expect(mockFetch).toHaveBeenCalledWith('/api/applications')
  })

  it('handles fetch errors correctly', async () => {
    const errorMessage = 'Network error'
    mockFetch.mockRejectedValueOnce(new Error(errorMessage))

    const { result } = renderHook(
      () => useApiData('/applications'),
      { wrapper }
    )

    await waitFor(() => {
      expect(result.current.isLoading).toBe(false)
    })

    expect(result.current.data).toBeUndefined()
    expect(result.current.error).toBeDefined()
    expect(result.current.isError).toBe(true)
  })

  it('handles HTTP error responses', async () => {
    mockFetch.mockResolvedValueOnce({
      ok: false,
      status: 404,
      statusText: 'Not Found',
      json: async () => ({ message: 'Resource not found' }),
    } as Response)

    const { result } = renderHook(
      () => useApiData('/applications/999'),
      { wrapper }
    )

    await waitFor(() => {
      expect(result.current.isLoading).toBe(false)
    })

    expect(result.current.data).toBeUndefined()
    expect(result.current.error).toBeDefined()
    expect(result.current.isError).toBe(true)
  })

  it('uses custom SWR configuration', async () => {
    const mockResponse = {
      data: mockApiResponses.applications.data,
      success: true,
    }

    mockFetch.mockResolvedValue({
      ok: true,
      json: async () => mockResponse,
    } as Response)

    const customConfig = {
      refreshInterval: 5000,
      revalidateOnFocus: true,
    }

    const { result } = renderHook(
      () => useApiData('/applications', customConfig),
      { wrapper }
    )

    await waitFor(() => {
      expect(result.current.isLoading).toBe(false)
    })

    expect(result.current.data).toEqual(mockResponse.data)
  })

  it('handles conditional fetching with null endpoint', async () => {
    const { result, rerender } = renderHook(
      ({ endpoint }) => useApiData(endpoint),
      { wrapper, initialProps: { endpoint: null as string | null } }
    )

    expect(result.current.data).toBeUndefined()
    expect(result.current.isLoading).toBe(false)
    expect(result.current.isError).toBe(false)
    expect(mockFetch).not.toHaveBeenCalled()

    // Enable fetching
    const mockResponse = {
      data: mockApiResponses.applications.data,
      success: true,
    }

    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: async () => mockResponse,
    } as Response)

    rerender({ endpoint: '/applications' })

    await waitFor(() => {
      expect(result.current.isLoading).toBe(false)
    })

    expect(result.current.data).toEqual(mockResponse.data)
    expect(mockFetch).toHaveBeenCalledWith('/api/applications')
  })

  it('provides mutate and refresh functions', async () => {
    const mockResponse = {
      data: mockApiResponses.applications.data,
      success: true,
    }

    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: async () => mockResponse,
    } as Response)

    const { result } = renderHook(
      () => useApiData('/applications'),
      { wrapper }
    )

    await waitFor(() => {
      expect(result.current.isLoading).toBe(false)
    })

    expect(typeof result.current.mutate).toBe('function')
    expect(typeof result.current.refresh).toBe('function')

    // Test refresh function
    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({
        ...mockResponse,
        data: [...mockResponse.data, { id: 999, jobTitle: 'New Job' }]
      }),
    } as Response)

    await result.current.refresh()

    // Should have called fetch again
    expect(mockFetch).toHaveBeenCalledTimes(2)
  })

  it('extracts data from ApiResponse wrapper', async () => {
    const innerData = [{ id: 1, name: 'Test Item' }]
    const mockResponse = {
      data: innerData,
      success: true,
      message: 'Success',
    }

    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: async () => mockResponse,
    } as Response)

    const { result } = renderHook(
      () => useApiData('/test'),
      { wrapper }
    )

    await waitFor(() => {
      expect(result.current.isLoading).toBe(false)
    })

    // Should extract the data property from the ApiResponse
    expect(result.current.data).toEqual(innerData)
    expect(result.current.data).not.toEqual(mockResponse)
  })

  it('handles loading states correctly', async () => {
    const mockResponse = {
      data: mockApiResponses.applications.data,
      success: true,
    }

    let resolvePromise: (value: any) => void
    const delayedPromise = new Promise(resolve => {
      resolvePromise = resolve
    })

    mockFetch.mockReturnValueOnce(
      delayedPromise.then(() => ({
        ok: true,
        json: async () => mockResponse,
      })) as Promise<Response>
    )

    const { result } = renderHook(
      () => useApiData('/applications'),
      { wrapper }
    )

    expect(result.current.isLoading).toBe(true)
    expect(result.current.data).toBeUndefined()
    expect(result.current.isError).toBe(false)

    // Resolve the promise
    resolvePromise!({
      ok: true,
      json: async () => mockResponse,
    })

    await waitFor(() => {
      expect(result.current.isLoading).toBe(false)
    })

    expect(result.current.data).toEqual(mockResponse.data)
    expect(result.current.isError).toBe(false)
  })
})
