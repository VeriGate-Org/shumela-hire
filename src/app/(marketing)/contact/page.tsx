import type { Metadata } from 'next';
import SectionWrapper from '@/components/marketing/SectionWrapper';
import HeroSection from '@/components/marketing/HeroSection';
import ContactForm from '@/components/marketing/ContactForm';

export const metadata: Metadata = {
  title: 'Contact Us',
  description:
    'Get in touch with the ShumelaHire team. We are here to answer your questions about our talent acquisition platform.',
};

export default function ContactPage() {
  return (
    <>
      {/* --- Hero --- */}
      <SectionWrapper bg="offwhite">
        <HeroSection
          overline="CONTACT"
          headline="Get in Touch"
          subheadline="Whether you have questions about the platform, need support, or want to explore a partnership, our team is here to help."
        />
      </SectionWrapper>

      {/* --- Contact Content --- */}
      <SectionWrapper bg="white">
        <div className="grid grid-cols-1 lg:grid-cols-5 gap-12">
          {/* Left — Form */}
          <div className="lg:col-span-3">
            <ContactForm />
          </div>

          {/* Right — Contact Information */}
          <div className="lg:col-span-2">
            <div className="bg-[#F8FAFC] border border-[#E2E8F0] rounded-[2px] p-8">
              <h2 className="text-lg font-bold text-[#0F172A] mb-6">Contact Information</h2>

              {/* Email */}
              <div className="flex items-start gap-3 mb-4">
                <svg
                  className="w-5 h-5 text-[#05527E] mt-0.5 flex-shrink-0"
                  fill="none"
                  viewBox="0 0 24 24"
                  strokeWidth={1.5}
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="M21.75 6.75v10.5a2.25 2.25 0 01-2.25 2.25h-15a2.25 2.25 0 01-2.25-2.25V6.75m19.5 0A2.25 2.25 0 0019.5 4.5h-15a2.25 2.25 0 00-2.25 2.25m19.5 0v.243a2.25 2.25 0 01-1.07 1.916l-7.5 4.615a2.25 2.25 0 01-2.36 0L3.32 8.91a2.25 2.25 0 01-1.07-1.916V6.75"
                  />
                </svg>
                <div>
                  <p className="text-sm font-medium text-[#0F172A]">Email</p>
                  <a
                    href="mailto:hello@shumelahire.co.za"
                    className="text-sm text-[#64748B] hover:text-[#05527E] transition-colors"
                  >
                    hello@shumelahire.co.za
                  </a>
                </div>
              </div>

              {/* Location */}
              <div className="flex items-start gap-3 mb-4">
                <svg
                  className="w-5 h-5 text-[#05527E] mt-0.5 flex-shrink-0"
                  fill="none"
                  viewBox="0 0 24 24"
                  strokeWidth={1.5}
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="M15 10.5a3 3 0 11-6 0 3 3 0 016 0z"
                  />
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="M19.5 10.5c0 7.142-7.5 11.25-7.5 11.25S4.5 17.642 4.5 10.5a7.5 7.5 0 1115 0z"
                  />
                </svg>
                <div>
                  <p className="text-sm font-medium text-[#0F172A]">Location</p>
                  <p className="text-sm text-[#64748B]">Johannesburg, South Africa</p>
                </div>
              </div>

              {/* Hours */}
              <div className="flex items-start gap-3 mb-4">
                <svg
                  className="w-5 h-5 text-[#05527E] mt-0.5 flex-shrink-0"
                  fill="none"
                  viewBox="0 0 24 24"
                  strokeWidth={1.5}
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="M12 6v6h4.5m4.5 0a9 9 0 11-18 0 9 9 0 0118 0z"
                  />
                </svg>
                <div>
                  <p className="text-sm font-medium text-[#0F172A]">Hours</p>
                  <p className="text-sm text-[#64748B]">
                    Monday to Friday, 08:00 &mdash; 17:00 SAST
                  </p>
                </div>
              </div>

              {/* Divider */}
              <div className="h-px bg-[#E2E8F0] my-6" />

              {/* Enterprise Sales */}
              <h3 className="text-sm font-bold text-[#0F172A] mb-2">For Enterprise Sales</h3>
              <p className="text-sm text-[#64748B] mb-3">
                For enterprise pricing, custom integrations, or dedicated support enquiries:
              </p>
              <a
                href="mailto:sales@shumelahire.co.za"
                className="text-sm font-medium text-[#05527E] hover:text-[#05527E]/80 transition-colors"
              >
                sales@shumelahire.co.za
              </a>
            </div>
          </div>
        </div>
      </SectionWrapper>
    </>
  );
}
