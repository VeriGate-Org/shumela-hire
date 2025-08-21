import React from 'react'
import { render, screen, waitFor, fireEvent } from '../../test-utils'
import { VirtualScroll } from '../VirtualScroll'
import { createLargeDataset } from '../../test-utils'

describe('VirtualScroll', () => {
  const mockRenderItem = jest.fn((item: any, index: number) => (
    <div key={item.id} data-testid={`item-${item.id}`}>
      {item.name} - {index}
    </div>
  ))

  beforeEach(() => {
    mockRenderItem.mockClear()
  })

  it('renders virtual scroll container', () => {
    const smallData = createLargeDataset(10)
    
    render(
      <VirtualScroll
        items={smallData}
        renderItem={mockRenderItem}
        itemHeight={50}
        containerHeight={400}
      />
    )

    const container = screen.getByRole('list')
    expect(container).toBeInTheDocument()
  })

  it('renders visible items in viewport', async () => {
    const largeData = createLargeDataset(100)
    
    render(
      <VirtualScroll
        items={largeData}
        renderItem={mockRenderItem}
        itemHeight={50}
        containerHeight={400}
      />
    )

    // Should only render visible items initially (400px / 50px = 8 visible + overscan)
    await waitFor(() => {
      const renderedItems = screen.getAllByTestId(/item-\d+/)
      expect(renderedItems.length).toBeLessThan(largeData.length)
      expect(renderedItems.length).toBeGreaterThan(0)
    })
  })

  it('handles scroll events correctly', async () => {
    const largeData = createLargeDataset(100)
    const mockOnScroll = jest.fn()
    
    render(
      <VirtualScroll
        items={largeData}
        renderItem={mockRenderItem}
        itemHeight={50}
        containerHeight={400}
        onScroll={mockOnScroll}
      />
    )

    const scrollContainer = screen.getByRole('list')
    
    // Simulate scroll event
    fireEvent.scroll(scrollContainer, { target: { scrollTop: 500 } })

    await waitFor(() => {
      expect(mockOnScroll).toHaveBeenCalledWith(500)
    })
  })

  it('handles empty data gracefully', () => {
    render(
      <VirtualScroll
        items={[]}
        renderItem={mockRenderItem}
        itemHeight={50}
        containerHeight={400}
      />
    )

    const container = screen.getByRole('list')
    expect(container).toBeInTheDocument()
    expect(screen.queryByTestId(/item-\d+/)).not.toBeInTheDocument()
  })

  it('calls loadMore when scrolling near bottom', async () => {
    const data = createLargeDataset(50)
    const mockLoadMore = jest.fn()
    
    render(
      <VirtualScroll
        items={data}
        renderItem={mockRenderItem}
        itemHeight={50}
        containerHeight={400}
        loadMore={mockLoadMore}
        hasNextPage={true}
        isLoading={false}
      />
    )

    const scrollContainer = screen.getByRole('list')
    
    // Simulate scrolling near the bottom
    const nearBottomScrollTop = (data.length * 50) - 400 - 100 // Near bottom
    fireEvent.scroll(scrollContainer, { target: { scrollTop: nearBottomScrollTop } })

    await waitFor(() => {
      expect(mockLoadMore).toHaveBeenCalled()
    })
  })

  it('does not call loadMore when isLoading is true', async () => {
    const data = createLargeDataset(50)
    const mockLoadMore = jest.fn()
    
    render(
      <VirtualScroll
        items={data}
        renderItem={mockRenderItem}
        itemHeight={50}
        containerHeight={400}
        loadMore={mockLoadMore}
        hasNextPage={true}
        isLoading={true}
      />
    )

    const scrollContainer = screen.getByRole('list')
    
    // Simulate scrolling near the bottom
    const nearBottomScrollTop = (data.length * 50) - 400 - 100
    fireEvent.scroll(scrollContainer, { target: { scrollTop: nearBottomScrollTop } })

    await waitFor(() => {
      expect(mockLoadMore).not.toHaveBeenCalled()
    }, { timeout: 1000 })
  })

  it('applies custom className', () => {
    const data = createLargeDataset(5)
    
    render(
      <VirtualScroll
        items={data}
        renderItem={mockRenderItem}
        itemHeight={50}
        containerHeight={400}
        className="custom-scroll-class"
      />
    )

    const container = screen.getByRole('list')
    expect(container).toHaveClass('custom-scroll-class')
  })

  it('handles item height changes correctly', async () => {
    const data = createLargeDataset(20)
    const { rerender } = render(
      <VirtualScroll
        items={data}
        renderItem={mockRenderItem}
        itemHeight={50}
        containerHeight={400}
      />
    )

    // Change item height
    rerender(
      <VirtualScroll
        items={data}
        renderItem={mockRenderItem}
        itemHeight={80}
        containerHeight={400}
      />
    )

    await waitFor(() => {
      const container = screen.getByRole('list')
      expect(container).toBeInTheDocument()
    })
  })

  it('calculates total height correctly', () => {
    const data = createLargeDataset(100)
    
    render(
      <VirtualScroll
        items={data}
        renderItem={mockRenderItem}
        itemHeight={50}
        containerHeight={400}
      />
    )

    // The inner container should have height equal to items.length * itemHeight
    const container = screen.getByRole('list')
    const innerContainer = container.firstChild as HTMLElement
    
    expect(innerContainer).toHaveStyle(`height: ${100 * 50}px`)
  })

  it('applies correct overscan buffer', async () => {
    const data = createLargeDataset(100)
    
    render(
      <VirtualScroll
        items={data}
        renderItem={mockRenderItem}
        itemHeight={50}
        containerHeight={400}
        overscan={10}
      />
    )

    await waitFor(() => {
      const renderedItems = screen.getAllByTestId(/item-\d+/)
      // Should render visible items + overscan buffer
      // 400px / 50px = 8 visible items + 20 overscan items (10 each side)
      expect(renderedItems.length).toBeGreaterThan(8)
      expect(renderedItems.length).toBeLessThan(40) // Reasonable upper bound
    })
  })
})
