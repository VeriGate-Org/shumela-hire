import type { Metadata } from 'next';
import SectionWrapper from '@/components/marketing/SectionWrapper';
import HeroSection from '@/components/marketing/HeroSection';
import DemoForm from '@/components/marketing/DemoForm';

export const metadata: Metadata = {
  title: 'Request a Demo',
  description:
    'Schedule a personalised demonstration of ShumelaHire and see how structured talent acquisition works for your organisation.',
};

const DEMO_STEPS = [
  {
    title: 'Personalised Walkthrough',
    description:
      "A 45-minute demonstration tailored to your organisation's hiring requirements and challenges.",
  },
  {
    title: 'Configuration Discussion',
    description:
      'We will discuss how ShumelaHire can be configured to match your workflows, approval chains, and compliance needs.',
  },
  {
    title: 'Pricing and Implementation',
    description:
      "Transparent pricing based on your organisation's size and requirements, with a clear implementation timeline.",
  },
  {
    title: 'Q&A Session',
    description:
      'Time to address any questions about security, compliance, integrations, or platform capabilities.',
  },
];

export default function DemoPage() {
  return (
    <>
      {/* --- Hero --- */}
      <SectionWrapper bg="offwhite">
        <HeroSection
          overline="DEMO"
          headline="See ShumelaHire in Action"
          subheadline="Schedule a personalised demonstration and discover how structured talent acquisition can transform your organisation's hiring process."
        />
      </SectionWrapper>

      {/* --- Demo Content --- */}
      <SectionWrapper bg="white">
        <div className="grid grid-cols-1 lg:grid-cols-5 gap-12">
          {/* Left — Form */}
          <div className="lg:col-span-3">
            <DemoForm />
          </div>

          {/* Right — What to Expect */}
          <div className="lg:col-span-2">
            <div className="bg-[#F8FAFC] border border-[#E2E8F0] rounded-[2px] p-8">
              <h2 className="text-lg font-bold text-[#0F172A] mb-6">What to Expect</h2>

              {DEMO_STEPS.map((step, index) => (
                <div
                  key={step.title}
                  className={`flex items-start gap-4 ${
                    index < DEMO_STEPS.length - 1 ? 'mb-6' : ''
                  }`}
                >
                  <div className="w-8 h-8 rounded-full bg-[#F1C54B] text-[#032E49] text-sm font-bold flex items-center justify-center flex-shrink-0">
                    {index + 1}
                  </div>
                  <div>
                    <p className="text-sm font-bold text-[#0F172A]">{step.title}</p>
                    <p className="text-sm text-[#64748B] mt-1">{step.description}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </SectionWrapper>
    </>
  );
}
