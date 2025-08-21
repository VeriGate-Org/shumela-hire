import { NextResponse } from 'next/server';

export async function GET() {
  // Mock dashboard counts for offers
  const dashboardCounts = {
    totalOffers: 15,
    pendingOffers: 3,
    activeNegotiations: 2,
    recentAcceptances: 4,
    expiringSoon: 1,
    averageSalary: 1850000, // R1.85M average
    totalValue: 27750000 // R27.75M total value
  };

  return NextResponse.json(dashboardCounts);
}
