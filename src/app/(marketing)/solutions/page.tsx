import type { Metadata } from "next";
import SectionWrapper from "@/components/marketing/SectionWrapper";
import HeroSection from "@/components/marketing/HeroSection";
import SolutionCard from "@/components/marketing/SolutionCard";
import FeatureDetailBlock from "@/components/marketing/FeatureDetailBlock";
import CTASection from "@/components/marketing/CTASection";

export const metadata: Metadata = {
  title: "Solutions",
  description:
    "ShumelaHire serves corporates, development finance institutions, and government agencies with purpose-built talent acquisition solutions.",
};

/* ------------------------------------------------------------------ */
/*  Icon helpers                                                       */
/* ------------------------------------------------------------------ */

const iconProps = {
  width: 28,
  height: 28,
  fill: "none",
  stroke: "currentColor",
  strokeWidth: 1.5,
  strokeLinecap: "round" as const,
  strokeLinejoin: "round" as const,
};

function BuildingIcon() {
  return (
    <svg {...iconProps} viewBox="0 0 24 24">
      <rect x="4" y="2" width="16" height="20" rx="1" />
      <line x1="9" y1="6" x2="9" y2="6.01" />
      <line x1="15" y1="6" x2="15" y2="6.01" />
      <line x1="9" y1="10" x2="9" y2="10.01" />
      <line x1="15" y1="10" x2="15" y2="10.01" />
      <line x1="9" y1="14" x2="9" y2="14.01" />
      <line x1="15" y1="14" x2="15" y2="14.01" />
      <path d="M9 22v-4h6v4" />
    </svg>
  );
}

function InstitutionIcon() {
  return (
    <svg {...iconProps} viewBox="0 0 24 24">
      <circle cx="12" cy="12" r="10" />
      <line x1="2" y1="12" x2="22" y2="12" />
      <path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z" />
    </svg>
  );
}

function GovernmentIcon() {
  return (
    <svg {...iconProps} viewBox="0 0 24 24">
      <path d="M3 21h18" />
      <path d="M5 21V7l7-4 7 4v14" />
      <path d="M9 21v-6h6v6" />
      <line x1="9" y1="10" x2="9" y2="10.01" />
      <line x1="15" y1="10" x2="15" y2="10.01" />
    </svg>
  );
}

/* ------------------------------------------------------------------ */
/*  Image placeholder helper                                           */
/* ------------------------------------------------------------------ */

function ImagePlaceholder({ label }: { label: string }) {
  return (
    <div className="bg-white border border-[#E2E8F0] rounded-[2px] aspect-video flex items-center justify-center text-[#CBD5E1] text-sm">
      {label}
    </div>
  );
}

/* ------------------------------------------------------------------ */
/*  Page                                                               */
/* ------------------------------------------------------------------ */

export default function SolutionsPage() {
  return (
    <>
      {/* --- Hero --- */}
      <SectionWrapper bg="offwhite">
        <HeroSection
          overline="SOLUTIONS"
          headline="Purpose-Built for Institutional Hiring"
          subheadline="Different institutions face different hiring challenges. ShumelaHire adapts to the compliance requirements, approval structures, and evaluation criteria that define your sector."
          primaryCTA={{ label: "Request a Demo", href: "/demo" }}
        />
      </SectionWrapper>

      {/* --- Solution Cards --- */}
      <SectionWrapper bg="white">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          <SolutionCard
            title="Corporates"
            description="Streamline high-volume hiring across business units with standardised processes, delegated approvals, and real-time pipeline visibility for talent acquisition leadership."
            href="/solutions/corporates"
            icon={<BuildingIcon />}
          />
          <SolutionCard
            title="Development Finance Institutions"
            description="Meet the unique compliance and reporting requirements of DFIs with structured evaluation frameworks, diversity tracking, and comprehensive audit trails."
            href="/solutions/development-finance"
            icon={<InstitutionIcon />}
          />
          <SolutionCard
            title="Government Agencies"
            description="Navigate public sector procurement and hiring requirements with built-in compliance controls, transparent evaluation processes, and complete accountability."
            href="/solutions/government"
            icon={<GovernmentIcon />}
          />
        </div>
      </SectionWrapper>

      {/* --- Cross-cutting Value Props --- */}
      <SectionWrapper bg="offwhite">
        <div className="mb-16">
          <p className="text-xs font-bold uppercase tracking-[0.18em] text-[#05527E] mb-3">
            ACROSS ALL SECTORS
          </p>
          <h2 className="text-2xl md:text-3xl font-bold tracking-[-0.03em] text-[#0F172A]">
            Common Capabilities, Sector-Specific Configuration
          </h2>
        </div>

        <div className="space-y-20">
          <FeatureDetailBlock
            title="Compliance at Every Level"
            description="Whether your organisation operates under POPIA, King IV, or public sector regulations, ShumelaHire provides the controls and audit trails you need."
            features={[
              "Configurable compliance frameworks",
              "Automated policy enforcement",
              "Regulatory reporting",
            ]}
            imageSlot={<ImagePlaceholder label="Compliance dashboard" />}
            reversed={false}
          />

          <FeatureDetailBlock
            title="Stakeholder Alignment"
            description="Hiring decisions in institutions involve multiple stakeholders. ShumelaHire gives each one — from recruiters to executives — the right view at the right time."
            features={[
              "Role-specific dashboards",
              "Collaborative evaluation tools",
              "Executive oversight controls",
            ]}
            imageSlot={<ImagePlaceholder label="Stakeholder views" />}
            reversed={true}
          />

          <FeatureDetailBlock
            title="Measurable Outcomes"
            description="Every metric that matters to institutional hiring — time-to-fill, quality-of-hire, diversity ratios, cost-per-hire — tracked and reported automatically."
            features={[
              "Automated KPI tracking",
              "Board-ready reporting",
              "Trend analysis and forecasting",
            ]}
            imageSlot={<ImagePlaceholder label="Analytics overview" />}
            reversed={false}
          />
        </div>
      </SectionWrapper>

      {/* --- CTA --- */}
      <CTASection
        headline="Find Your Solution"
        subtext="Tell us about your organisation and we will show you how ShumelaHire fits."
        ctaLabel="Request a Demo"
        ctaHref="/demo"
      />
    </>
  );
}
