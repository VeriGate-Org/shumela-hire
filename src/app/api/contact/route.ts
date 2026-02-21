import { NextResponse } from 'next/server';

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export async function POST(request: Request) {
  try {
    const body = await request.json();

    const { name, email, organisation, subject, message } = body;

    // Validate required fields
    if (!name || !String(name).trim()) {
      return NextResponse.json(
        { success: false, message: 'Name is required.' },
        { status: 400 },
      );
    }

    if (!email || !String(email).trim()) {
      return NextResponse.json(
        { success: false, message: 'Email is required.' },
        { status: 400 },
      );
    }

    if (!EMAIL_REGEX.test(String(email))) {
      return NextResponse.json(
        { success: false, message: 'Please provide a valid email address.' },
        { status: 400 },
      );
    }

    if (!organisation || !String(organisation).trim()) {
      return NextResponse.json(
        { success: false, message: 'Organisation is required.' },
        { status: 400 },
      );
    }

    if (!subject || !String(subject).trim()) {
      return NextResponse.json(
        { success: false, message: 'Subject is required.' },
        { status: 400 },
      );
    }

    if (!message || !String(message).trim()) {
      return NextResponse.json(
        { success: false, message: 'Message is required.' },
        { status: 400 },
      );
    }

    // Placeholder: log to console for now; integrate with email/CRM later
    console.log('[Contact Enquiry]', {
      name: String(name).trim(),
      email: String(email).trim(),
      organisation: String(organisation).trim(),
      phone: body.phone ? String(body.phone).trim() : '',
      subject: String(subject).trim(),
      message: String(message).trim(),
      timestamp: new Date().toISOString(),
    });

    return NextResponse.json(
      { success: true, message: 'Enquiry received' },
      { status: 200 },
    );
  } catch {
    return NextResponse.json(
      { success: false, message: 'An unexpected error occurred. Please try again.' },
      { status: 500 },
    );
  }
}
