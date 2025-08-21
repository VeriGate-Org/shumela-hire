import React from 'react'
import { render, screen } from '../../test-utils'
import DashboardShell from '../DashboardShell'

describe('DashboardShell', () => {
  it('renders dashboard navigation', () => {
    render(<DashboardShell />)
    
    // Check for main navigation elements
    expect(screen.getByText(/dashboard/i)).toBeInTheDocument()
  })

  it('displays the main content area', () => {
    render(<DashboardShell />)
    
    const mainContent = screen.getByRole('main')
    expect(mainContent).toBeInTheDocument()
  })

  it('has proper navigation structure', () => {
    render(<DashboardShell />)
    
    const nav = screen.getByRole('navigation')
    expect(nav).toBeInTheDocument()
  })
})
