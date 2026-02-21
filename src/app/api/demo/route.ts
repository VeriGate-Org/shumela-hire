import { NextResponse } from 'next/server';

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

const REQUIRED_FIELDS = [
  { key: 'firstName', label: 'First name' },
  { key: 'lastName', label: 'Last name' },
  { key: 'workEmail', label: 'Work email' },
  { key: 'organisation', label: 'Organisation' },
  { key: 'jobTitle', label: 'Job title' },
  { key: 'organisationType', label: 'Organisation type' },
  { key: 'employeeCount', label: 'Employee count' },
] as const;

export async function POST(request: Request) {
  try {
    const body = await request.json();

    // Validate required fields
    for (const { key, label } of REQUIRED_FIELDS) {
      if (!body[key] || !String(body[key]).trim()) {
        return NextResponse.json(
          { success: false, message: `${label} is required.` },
          { status: 400 },
        );
      }
    }

    // Validate email format
    if (!EMAIL_REGEX.test(String(body.workEmail))) {
      return NextResponse.json(
        { success: false, message: 'Please provide a valid work email address.' },
        { status: 400 },
      );
    }

    // Placeholder: log to console for now; integrate with email/CRM later
    console.log('[Demo Request]', {
      firstName: String(body.firstName).trim(),
      lastName: String(body.lastName).trim(),
      workEmail: String(body.workEmail).trim(),
      organisation: String(body.organisation).trim(),
      jobTitle: String(body.jobTitle).trim(),
      organisationType: String(body.organisationType).trim(),
      employeeCount: String(body.employeeCount).trim(),
      currentProcess: body.currentProcess ? String(body.currentProcess).trim() : '',
      message: body.message ? String(body.message).trim() : '',
      timestamp: new Date().toISOString(),
    });

    return NextResponse.json(
      { success: true, message: 'Demo request received' },
      { status: 200 },
    );
  } catch {
    return NextResponse.json(
      { success: false, message: 'An unexpected error occurred. Please try again.' },
      { status: 500 },
    );
  }
}
