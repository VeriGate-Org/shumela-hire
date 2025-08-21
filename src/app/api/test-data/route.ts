import { NextResponse } from 'next/server';
import { requisitionService } from '../../../services/requisitionService';

// GET /api/test-data - Test demo data initialization
export async function GET() {
  try {
    console.log('Initializing demo data...');
    await requisitionService.initializeDemoData();
    
    console.log('Getting all requisitions...');
    const requisitions = await requisitionService.getAllRequisitions();
    
    console.log(`Found ${requisitions.length} requisitions`);
    
    const requisition1004 = await requisitionService.getRequisition('1004');
    console.log('Requisition 1004:', requisition1004 ? 'Found' : 'Not found');

    return NextResponse.json({
      success: true,
      message: 'Demo data test completed',
      data: {
        totalRequisitions: requisitions.length,
        requisitionIds: requisitions.map(r => r.id),
        requisition1004Exists: !!requisition1004,
        requisition1004: requisition1004
      }
    });

  } catch (error) {
    console.error('Error in test data endpoint:', error);
    return NextResponse.json(
      { success: false, message: 'Test failed', error: error instanceof Error ? error.message : 'Unknown error' },
      { status: 500 }
    );
  }
}