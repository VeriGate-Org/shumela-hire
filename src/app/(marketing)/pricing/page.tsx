import type { Metadata } from 'next';
import HeroSection from '@/components/marketing/HeroSection';
import SectionWrapper from '@/components/marketing/SectionWrapper';
import PricingCard from '@/components/marketing/PricingCard';
import PricingTable from '@/components/marketing/PricingTable';
import FAQAccordion from '@/components/marketing/FAQAccordion';
import CTASection from '@/components/marketing/CTASection';

export const metadata: Metadata = {
  title: 'Pricing',
  description:
    "Transparent pricing for ShumelaHire's talent acquisition platform. Choose the plan that fits your organisation's hiring volume and complexity.",
};

/* ------------------------------------------------------------------ */
/*  Comparison Table Data                                              */
/* ------------------------------------------------------------------ */

const comparisonCategories = [
  {
    name: 'Requisition Management',
    features: [
      { name: 'Active requisitions', values: ['50/year', 'Unlimited', 'Unlimited'] },
      { name: 'Approval workflows', values: [true, true, true] },
      { name: 'Custom templates', values: [false, true, true] },
      { name: 'Budget controls', values: [false, true, true] },
    ],
  },
  {
    name: 'Candidate Management',
    features: [
      { name: 'Pipeline tracking', values: [true, true, true] },
      { name: 'Structured scorecards', values: ['Basic', 'Advanced', 'Advanced'] },
      { name: 'Communication hub', values: [true, true, true] },
      { name: 'Document management', values: [true, true, true] },
    ],
  },
  {
    name: 'Analytics & Reporting',
    features: [
      { name: 'Standard reports', values: [true, true, true] },
      { name: 'Pipeline analytics', values: [false, true, true] },
      { name: 'Diversity metrics', values: [false, true, true] },
      { name: 'Executive dashboards', values: [false, false, true] },
      { name: 'Custom reports', values: [false, false, true] },
    ],
  },
  {
    name: 'Security & Compliance',
    features: [
      { name: 'POPIA compliance', values: [true, true, true] },
      { name: 'Role-based access', values: [true, true, true] },
      { name: 'Audit logging', values: [true, true, true] },
      { name: 'SSO / SAML', values: [false, false, true] },
      { name: 'Data residency', values: [false, false, true] },
    ],
  },
  {
    name: 'Support',
    features: [
      { name: 'Email support', values: [true, true, true] },
      { name: 'Priority support', values: [false, true, true] },
      { name: 'Dedicated account manager', values: [false, false, true] },
      { name: 'On-site training', values: [false, false, true] },
    ],
  },
];

/* ------------------------------------------------------------------ */
/*  FAQ Data                                                           */
/* ------------------------------------------------------------------ */

const faqItems = [
  {
    question: 'How does pricing work?',
    answer:
      "ShumelaHire pricing is based on your organisation's hiring volume and feature requirements. Contact our sales team for a tailored quote based on your specific needs.",
  },
  {
    question: 'Is there a free trial?',
    answer:
      "We offer a guided demonstration of the platform rather than a self-service trial. This ensures you see the features most relevant to your organisation's hiring process.",
  },
  {
    question: 'Can I change plans later?',
    answer:
      'Yes. You can upgrade your plan at any time. Our team will work with you to ensure a smooth transition with no disruption to active requisitions.',
  },
  {
    question: 'What is included in the implementation?',
    answer:
      'Every plan includes onboarding support, data migration assistance, and training for your team. Enterprise plans include dedicated implementation management.',
  },
  {
    question: 'How is data handled under POPIA?',
    answer:
      'ShumelaHire is designed for POPIA compliance from the ground up. All data is processed and stored within South African infrastructure, with configurable retention policies and consent management.',
  },
  {
    question: 'Do you offer government procurement pricing?',
    answer:
      'Yes. We work with government procurement processes and can accommodate specific contracting requirements. Contact our sales team for details.',
  },
];

/* ================================================================== */
/*  Page Component                                                     */
/* ================================================================== */

export default function PricingPage() {
  return (
    <>
      {/* ---------------------------------------------------------- */}
      {/* 1. Hero                                                      */}
      {/* ---------------------------------------------------------- */}
      <div className="bg-[#F8FAFC]">
        <HeroSection
          overline="PRICING"
          headline="Transparent Pricing for Every Organisation"
          subheadline="Every plan includes core platform features. Choose the tier that matches your hiring volume and compliance requirements."
          primaryCTA={{ label: 'Contact Sales', href: '/contact' }}
        />
      </div>

      {/* ---------------------------------------------------------- */}
      {/* 2. Pricing Cards                                             */}
      {/* ---------------------------------------------------------- */}
      <SectionWrapper bg="white">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 items-start">
          <PricingCard
            name="Standard"
            price="Contact Sales"
            description="For organisations with up to 50 active requisitions per year."
            features={[
              'Up to 50 requisitions/year',
              'Core workflow automation',
              'Standard reporting',
              'Email support',
              '5 user accounts',
              'POPIA compliance tools',
            ]}
            ctaLabel="Contact Sales"
            ctaHref="/contact"
            ctaVariant="outline"
          />

          <PricingCard
            name="Professional"
            price="Contact Sales"
            description="For growing organisations with complex hiring needs and compliance requirements."
            features={[
              'Unlimited requisitions',
              'Advanced workflow automation',
              'Pipeline analytics',
              'Priority support',
              '25 user accounts',
              'Custom scorecards',
              'API access',
              'Diversity reporting',
            ]}
            ctaLabel="Contact Sales"
            ctaHref="/contact"
            highlighted
            ctaVariant="primary"
          />

          <PricingCard
            name="Enterprise"
            price="Contact Sales"
            description="For large institutions requiring dedicated infrastructure, custom integrations, and premium support."
            features={[
              'Everything in Professional',
              'Unlimited users',
              'Dedicated account manager',
              'Custom integrations',
              'SSO / SAML',
              'Data residency controls',
              'SLA guarantees',
              'On-site training',
            ]}
            ctaLabel="Contact Sales"
            ctaHref="/contact"
            ctaVariant="outline"
          />
        </div>
      </SectionWrapper>

      {/* ---------------------------------------------------------- */}
      {/* 3. Comparison Table                                          */}
      {/* ---------------------------------------------------------- */}
      <SectionWrapper bg="offwhite">
        <div className="text-center mb-12">
          <p className="text-xs font-bold uppercase tracking-[0.18em] text-[#05527E] mb-4">
            COMPARE PLANS
          </p>
          <h2 className="text-2xl md:text-3xl font-bold tracking-[-0.03em] text-[#0F172A]">
            Feature Comparison
          </h2>
        </div>

        <PricingTable
          tiers={['Standard', 'Professional', 'Enterprise']}
          categories={comparisonCategories}
        />
      </SectionWrapper>

      {/* ---------------------------------------------------------- */}
      {/* 4. FAQ                                                       */}
      {/* ---------------------------------------------------------- */}
      <SectionWrapper bg="white">
        <div className="max-w-3xl mx-auto">
          <div className="text-center mb-12">
            <p className="text-xs font-bold uppercase tracking-[0.18em] text-[#05527E] mb-4">
              FREQUENTLY ASKED QUESTIONS
            </p>
            <h2 className="text-2xl md:text-3xl font-bold tracking-[-0.03em] text-[#0F172A]">
              Common Questions
            </h2>
          </div>

          <FAQAccordion items={faqItems} />
        </div>
      </SectionWrapper>

      {/* ---------------------------------------------------------- */}
      {/* 5. CTA                                                       */}
      {/* ---------------------------------------------------------- */}
      <CTASection
        headline="Find the Right Plan for Your Organisation"
        subtext="Our team will help you identify the tier that matches your hiring volume and compliance requirements."
        ctaLabel="Contact Sales"
        ctaHref="/contact"
      />
    </>
  );
}
