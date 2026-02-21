import type { Metadata } from "next";
import SectionWrapper from "@/components/marketing/SectionWrapper";
import HeroSection from "@/components/marketing/HeroSection";
import FeatureCard from "@/components/marketing/FeatureCard";
import FeatureDetailBlock from "@/components/marketing/FeatureDetailBlock";
import CTASection from "@/components/marketing/CTASection";
import StatsRow from "@/components/marketing/StatsRow";

export const metadata: Metadata = {
  title: "Solutions for Development Finance Institutions",
  description:
    "ShumelaHire helps DFIs meet stringent compliance and reporting requirements while building diverse, qualified teams.",
};

/* ------------------------------------------------------------------ */
/*  Icon helpers                                                       */
/* ------------------------------------------------------------------ */

const iconProps = {
  width: 24,
  height: 24,
  fill: "none",
  stroke: "currentColor",
  strokeWidth: 1.5,
  strokeLinecap: "round" as const,
  strokeLinejoin: "round" as const,
};

function RegulatoryIcon() {
  return (
    <svg {...iconProps} viewBox="0 0 24 24">
      <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
    </svg>
  );
}

function DiversityIcon() {
  return (
    <svg {...iconProps} viewBox="0 0 24 24">
      <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
      <circle cx="9" cy="7" r="4" />
      <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
      <path d="M16 3.13a4 4 0 0 1 0 7.75" />
    </svg>
  );
}

function SpecialisedTalentIcon() {
  return (
    <svg {...iconProps} viewBox="0 0 24 24">
      <circle cx="11" cy="11" r="8" />
      <line x1="21" y1="21" x2="16.65" y2="16.65" />
      <line x1="8" y1="11" x2="14" y2="11" />
      <line x1="11" y1="8" x2="11" y2="14" />
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

export default function DevelopmentFinancePage() {
  return (
    <>
      {/* --- Hero --- */}
      <SectionWrapper bg="offwhite">
        <HeroSection
          overline="DEVELOPMENT FINANCE"
          headline="Hire With the Rigour Your Mandate Demands"
          subheadline="Development finance institutions operate under heightened scrutiny. ShumelaHire provides the structured evaluation, diversity tracking, and audit capabilities your stakeholders expect."
          primaryCTA={{ label: "Request a Demo", href: "/demo" }}
        />
      </SectionWrapper>

      {/* --- Challenges --- */}
      <SectionWrapper bg="white">
        <div className="mb-12">
          <p className="text-xs font-bold uppercase tracking-[0.18em] text-[#05527E] mb-3">
            THE CHALLENGE
          </p>
          <h2 className="text-2xl md:text-3xl font-bold tracking-[-0.03em] text-[#0F172A]">
            What DFIs Navigate Every Day
          </h2>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <FeatureCard
            icon={<RegulatoryIcon />}
            title="Regulatory Scrutiny"
            description="DFIs face oversight from multiple regulatory bodies. Hiring decisions must be defensible, documented, and free from bias."
          />
          <FeatureCard
            icon={<DiversityIcon />}
            title="Diversity Mandates"
            description="Transformation targets and diversity requirements are not optional. Tracking and reporting must be systematic, not retrospective."
          />
          <FeatureCard
            icon={<SpecialisedTalentIcon />}
            title="Specialised Talent"
            description="Development finance requires niche expertise. Finding qualified candidates while meeting diversity and governance requirements demands precision."
          />
        </div>
      </SectionWrapper>

      {/* --- Solutions --- */}
      <SectionWrapper bg="offwhite">
        <div className="space-y-20">
          <FeatureDetailBlock
            title="Defensible Evaluation Frameworks"
            description="Structured scorecards, competency-based assessments, and calibrated panel reviews ensure every hiring decision can withstand regulatory scrutiny."
            features={[
              "Weighted competency scoring",
              "Panel calibration tools",
              "Decision rationale documentation",
            ]}
            imageSlot={<ImagePlaceholder label="Evaluation framework" />}
            reversed={false}
          />

          <FeatureDetailBlock
            title="Diversity Tracking and Reporting"
            description="Track transformation targets across your entire pipeline. Generate reports that demonstrate systematic commitment to diversity, not just outcomes."
            features={[
              "Pipeline diversity dashboards",
              "Demographic trend analysis",
              "Board-ready transformation reports",
            ]}
            imageSlot={<ImagePlaceholder label="Diversity metrics" />}
            reversed={true}
          />

          <FeatureDetailBlock
            title="Comprehensive Audit Capability"
            description="Every action, every evaluation, every decision — logged and immutable. When regulators or board committees ask questions, the answers are already documented."
            features={[
              "Immutable audit trail",
              "Regulator-ready export formats",
              "Complete decision lineage",
            ]}
            imageSlot={<ImagePlaceholder label="Audit trail" />}
            reversed={false}
          />
        </div>
      </SectionWrapper>

      {/* --- Stats --- */}
      <StatsRow
        stats={[
          { value: "100%", label: "Audit Compliance" },
          { value: "45%", label: "Faster Time-to-Fill" },
          { value: "2x", label: "Diversity Pipeline Growth" },
          { value: "Zero", label: "Regulatory Findings" },
        ]}
      />

      {/* --- CTA --- */}
      <CTASection
        headline="See How ShumelaHire Supports DFIs"
        subtext="Schedule a demonstration tailored to development finance hiring requirements."
        ctaLabel="Request a Demo"
        ctaHref="/demo"
      />
    </>
  );
}
