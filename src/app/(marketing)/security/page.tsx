import type { Metadata } from 'next';
import HeroSection from '@/components/marketing/HeroSection';
import SectionWrapper from '@/components/marketing/SectionWrapper';
import FeatureCard from '@/components/marketing/FeatureCard';
import CTASection from '@/components/marketing/CTASection';

export const metadata: Metadata = {
  title: 'Security',
  description:
    "ShumelaHire's security architecture, POPIA compliance, data residency, and enterprise-grade access controls.",
};

/* ------------------------------------------------------------------ */
/*  Inline SVG Icons                                                   */
/* ------------------------------------------------------------------ */

function EncryptionIcon() {
  return (
    <svg
      width="24"
      height="24"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.5"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <rect x="3" y="11" width="18" height="11" rx="2" />
      <path d="M7 11V7a5 5 0 0 1 10 0v4" />
      <circle cx="12" cy="16" r="1" />
    </svg>
  );
}

function AccessControlIcon() {
  return (
    <svg
      width="24"
      height="24"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.5"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
      <circle cx="9" cy="7" r="4" />
      <path d="M19 8l2 2 4-4" />
    </svg>
  );
}

function POPIAIcon() {
  return (
    <svg
      width="24"
      height="24"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.5"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
      <polyline points="9 12 11 14 15 10" />
    </svg>
  );
}

function AuditIcon() {
  return (
    <svg
      width="24"
      height="24"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.5"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
      <polyline points="14 2 14 8 20 8" />
      <line x1="9" y1="13" x2="15" y2="13" />
      <line x1="9" y1="17" x2="13" y2="17" />
    </svg>
  );
}

function DataResidencyIcon() {
  return (
    <svg
      width="24"
      height="24"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.5"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <ellipse cx="12" cy="5" rx="9" ry="3" />
      <path d="M21 12c0 1.66-4 3-9 3s-9-1.34-9-3" />
      <path d="M3 5v14c0 1.66 4 3 9 3s9-1.34 9-3V5" />
    </svg>
  );
}

function InfrastructureIcon() {
  return (
    <svg
      width="24"
      height="24"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.5"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <rect x="2" y="2" width="20" height="8" rx="2" />
      <rect x="2" y="14" width="20" height="8" rx="2" />
      <line x1="6" y1="6" x2="6.01" y2="6" />
      <line x1="6" y1="18" x2="6.01" y2="18" />
    </svg>
  );
}

/* ------------------------------------------------------------------ */
/*  Architecture Sections Data                                         */
/* ------------------------------------------------------------------ */

const architectureSections = [
  {
    title: 'Authentication & Identity',
    description:
      'ShumelaHire uses AWS Cognito for identity management with support for multi-factor authentication, password policies, and session management. Enterprise plans support SAML-based single sign-on integration.',
  },
  {
    title: 'Data Protection',
    description:
      'All personally identifiable information is encrypted at rest using AES-256 encryption. Database backups are encrypted and stored within the same AWS region. Data retention policies are configurable per organisation to meet POPIA requirements.',
  },
  {
    title: 'Network Security',
    description:
      'The platform operates within isolated Virtual Private Clouds with strict security group rules. All traffic is routed through AWS Application Load Balancers with WAF protection. API endpoints are rate-limited and monitored for anomalous activity.',
  },
];

/* ------------------------------------------------------------------ */
/*  Compliance Items Data                                              */
/* ------------------------------------------------------------------ */

const complianceItems = [
  {
    title: 'POPIA',
    description:
      'Full alignment with the Protection of Personal Information Act, including data subject rights, consent management, and breach notification procedures.',
  },
  {
    title: 'King IV',
    description:
      'Governance controls and audit trails aligned with King IV principles for information technology governance.',
  },
  {
    title: 'ISO 27001',
    description:
      'Security practices aligned with ISO 27001 information security management standards. Formal certification in progress.',
  },
];

/* ================================================================== */
/*  Page Component                                                     */
/* ================================================================== */

export default function SecurityPage() {
  return (
    <>
      {/* ---------------------------------------------------------- */}
      {/* 1. Hero                                                      */}
      {/* ---------------------------------------------------------- */}
      <div className="bg-[#F8FAFC]">
        <HeroSection
          overline="SECURITY"
          headline="Enterprise-Grade Security, Built In"
          subheadline="Security and compliance are not add-on features. They are foundational to every layer of the ShumelaHire platform."
          primaryCTA={{ label: 'Contact Us', href: '/contact' }}
        />
      </div>

      {/* ---------------------------------------------------------- */}
      {/* 2. Security Overview                                         */}
      {/* ---------------------------------------------------------- */}
      <SectionWrapper bg="white">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          <FeatureCard
            icon={<EncryptionIcon />}
            title="Data Encryption"
            description="All data encrypted at rest (AES-256) and in transit (TLS 1.3). Encryption keys managed through AWS KMS with automatic rotation."
          />
          <FeatureCard
            icon={<AccessControlIcon />}
            title="Access Control"
            description="Role-based access controls with granular permissions. Every action is authenticated, authorised, and logged."
          />
          <FeatureCard
            icon={<POPIAIcon />}
            title="POPIA Compliance"
            description="Purpose-built for South African privacy law. Consent management, data minimisation, and configurable retention policies."
          />
          <FeatureCard
            icon={<AuditIcon />}
            title="Audit Logging"
            description="Comprehensive, immutable audit trail for every action. Who did what, when, and from where — always available."
          />
          <FeatureCard
            icon={<DataResidencyIcon />}
            title="Data Residency"
            description="All data processed and stored within South African AWS infrastructure (af-south-1). No cross-border data transfers without explicit consent."
          />
          <FeatureCard
            icon={<InfrastructureIcon />}
            title="Infrastructure Security"
            description="Hosted on AWS with VPC isolation, security groups, and automated vulnerability scanning. SOC 2 aligned practices."
          />
        </div>
      </SectionWrapper>

      {/* ---------------------------------------------------------- */}
      {/* 3. Architecture Detail                                       */}
      {/* ---------------------------------------------------------- */}
      <SectionWrapper bg="offwhite">
        <div className="max-w-3xl">
          <p className="text-xs font-bold uppercase tracking-[0.18em] text-[#05527E] mb-4">
            ARCHITECTURE
          </p>
          <h2 className="text-2xl md:text-3xl font-bold tracking-[-0.03em] text-[#0F172A] mb-12">
            Security Architecture
          </h2>

          {architectureSections.map((section) => (
            <div
              key={section.title}
              className="border-l-2 border-[#05527E] pl-6 mb-8"
            >
              <h3 className="text-lg font-bold text-[#0F172A]">
                {section.title}
              </h3>
              <p className="text-[#64748B] leading-relaxed mt-2">
                {section.description}
              </p>
            </div>
          ))}
        </div>
      </SectionWrapper>

      {/* ---------------------------------------------------------- */}
      {/* 4. Compliance                                                */}
      {/* ---------------------------------------------------------- */}
      <SectionWrapper bg="navy">
        <div className="text-center">
          <p className="text-xs font-bold uppercase tracking-[0.18em] text-[#F1C54B] mb-4">
            COMPLIANCE
          </p>
          <h2 className="text-2xl md:text-3xl font-bold tracking-[-0.03em] text-white">
            Regulatory Alignment
          </h2>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mt-8">
          {complianceItems.map((item) => (
            <div key={item.title} className="text-center">
              <h3 className="text-lg font-bold text-white/90 mb-3">
                {item.title}
              </h3>
              <p className="text-white/60 leading-relaxed text-sm">
                {item.description}
              </p>
            </div>
          ))}
        </div>
      </SectionWrapper>

      {/* ---------------------------------------------------------- */}
      {/* 5. CTA                                                       */}
      {/* ---------------------------------------------------------- */}
      <CTASection
        headline="Security Questions"
        subtext="Our engineering team is available to discuss ShumelaHire's security architecture in detail."
        ctaLabel="Contact Us"
        ctaHref="/contact"
      />
    </>
  );
}
