import React from 'react'
import { render } from '../../test-utils'
import { PerformanceMonitor } from '../PerformanceMonitor'

describe('PerformanceMonitor', () => {
  it('renders PerformanceMonitor component', () => {
    const { container } = render(<PerformanceMonitor />)
    expect(container).toBeDefined()
  })

  it('can be imported successfully', () => {
    expect(PerformanceMonitor).toBeDefined()
    expect(typeof PerformanceMonitor).toBe('function')
  })
})
