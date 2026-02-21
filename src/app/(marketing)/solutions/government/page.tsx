import type { Metadata } from "next";
import SectionWrapper from "@/components/marketing/SectionWrapper";
import HeroSection from "@/components/marketing/HeroSection";
import FeatureCard from "@/components/marketing/FeatureCard";
import FeatureDetailBlock from "@/components/marketing/FeatureDetailBlock";
import CTASection from "@/components/marketing/CTASection";
import StatsRow from "@/components/marketing/StatsRow";

export const metadata: Metadata = {
  title: "Solutions for Government Agencies",
  description:
    "ShumelaHire helps government agencies navigate public sector hiring requirements with transparent, auditable, and compliant talent acquisition processes.",
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

function AccountabilityIcon() {
  return (
    <svg {...iconProps} viewBox="0 0 24 24">
      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
      <circle cx="12" cy="12" r="3" />
    </svg>
  );
}

function ProcurementIcon() {
  return (
    <svg {...iconProps} viewBox="0 0 24 24">
      <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
      <polyline points="14 2 14 8 20 8" />
      <line x1="16" y1="13" x2="8" y2="13" />
      <line x1="16" y1="17" x2="8" y2="17" />
      <polyline points="10 9 9 9 8 9" />
    </svg>
  );
}

function PerceptionIcon() {
  return (
    <svg {...iconProps} viewBox="0 0 24 24">
      <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
      <path d="M9 12l2 2 4-4" />
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

export default function GovernmentPage() {
  return (
    <>
      {/* --- Hero --- */}
      <SectionWrapper bg="offwhite">
        <HeroSection
          overline="GOVERNMENT"
          headline="Transparent Hiring for Public Institutions"
          subheadline="Government hiring processes must withstand public scrutiny. ShumelaHire provides the transparency, structure, and accountability that public service demands."
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
            What Public Sector Hiring Demands
          </h2>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <FeatureCard
            icon={<AccountabilityIcon />}
            title="Public Accountability"
            description="Every hiring decision can be subject to parliamentary questions, PAIA requests, and public scrutiny. Documentation must be complete and accessible."
          />
          <FeatureCard
            icon={<ProcurementIcon />}
            title="Procurement Compliance"
            description="Government procurement regulations add layers of complexity to platform adoption. Vendor requirements, BEE scoring, and contract terms must be addressed."
          />
          <FeatureCard
            icon={<PerceptionIcon />}
            title="Cadre Deployment Perception"
            description="Public confidence in merit-based hiring requires demonstrably fair, structured, and transparent evaluation processes."
          />
        </div>
      </SectionWrapper>

      {/* --- Solutions --- */}
      <SectionWrapper bg="offwhite">
        <div className="space-y-20">
          <FeatureDetailBlock
            title="Complete Audit Trail"
            description="Every action documented. Every decision traceable. Every evaluation structured and defensible."
            features={[
              "Immutable audit logs",
              "Decision rationale capture",
              "PAIA-ready documentation",
            ]}
            imageSlot={<ImagePlaceholder label="Audit trail view" />}
            reversed={false}
          />

          <FeatureDetailBlock
            title="Structured Evaluation"
            description="Standardised scorecards and blind review options ensure hiring decisions are based on merit and competency, not subjectivity."
            features={[
              "Competency-based scoring",
              "Panel calibration tools",
              "Blind review options",
            ]}
            imageSlot={<ImagePlaceholder label="Evaluation scorecard" />}
            reversed={true}
          />

          <FeatureDetailBlock
            title="Procurement Ready"
            description="ShumelaHire is designed to work within government procurement frameworks, with the documentation and compliance certifications you need."
            features={[
              "BEE Level 1 contributor",
              "SITA-aligned architecture",
              "South African data residency",
            ]}
            imageSlot={<ImagePlaceholder label="Compliance certifications" />}
            reversed={false}
          />
        </div>
      </SectionWrapper>

      {/* --- Stats --- */}
      <StatsRow
        stats={[
          { value: "100%", label: "PAIA Compliant" },
          { value: "50%", label: "Reduction in Disputes" },
          { value: "Level 1", label: "BEE Contributor" },
          { value: "Zero", label: "Data Offshore" },
        ]}
      />

      {/* --- CTA --- */}
      <CTASection
        headline="See How ShumelaHire Serves Government"
        subtext="Request a demonstration aligned to public sector procurement and hiring requirements."
        ctaLabel="Request a Demo"
        ctaHref="/demo"
      />
    </>
  );
}
