import type { Metadata } from "next";
import SectionWrapper from "@/components/marketing/SectionWrapper";
import HeroSection from "@/components/marketing/HeroSection";
import FeatureCard from "@/components/marketing/FeatureCard";
import FeatureDetailBlock from "@/components/marketing/FeatureDetailBlock";
import CTASection from "@/components/marketing/CTASection";
import TestimonialBlock from "@/components/marketing/TestimonialBlock";
import StatsRow from "@/components/marketing/StatsRow";

export const metadata: Metadata = {
  title: "Solutions for Corporates",
  description:
    "ShumelaHire helps corporates streamline talent acquisition with standardised workflows, pipeline analytics, and enterprise-grade compliance.",
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

function FragmentedIcon() {
  return (
    <svg {...iconProps} viewBox="0 0 24 24">
      <rect x="3" y="3" width="7" height="7" rx="1" />
      <rect x="14" y="3" width="7" height="7" rx="1" />
      <rect x="3" y="14" width="7" height="7" rx="1" />
      <rect x="14" y="14" width="7" height="7" rx="1" />
    </svg>
  );
}

function ComplianceRiskIcon() {
  return (
    <svg {...iconProps} viewBox="0 0 24 24">
      <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z" />
      <line x1="12" y1="9" x2="12" y2="13" />
      <line x1="12" y1="17" x2="12.01" y2="17" />
    </svg>
  );
}

function MisalignmentIcon() {
  return (
    <svg {...iconProps} viewBox="0 0 24 24">
      <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
      <circle cx="9" cy="7" r="4" />
      <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
      <path d="M16 3.13a4 4 0 0 1 0 7.75" />
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

export default function CorporatesPage() {
  return (
    <>
      {/* --- Hero --- */}
      <SectionWrapper bg="offwhite">
        <HeroSection
          overline="CORPORATES"
          headline="Scale Your Hiring Without Losing Control"
          subheadline="Large corporates need to hire at volume without sacrificing process integrity. ShumelaHire gives talent acquisition teams the structure to move fast and the controls to stay compliant."
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
            What Corporate Hiring Teams Face
          </h2>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <FeatureCard
            icon={<FragmentedIcon />}
            title="Fragmented Processes"
            description="Different business units running different hiring processes. No standardisation, no visibility, no accountability."
          />
          <FeatureCard
            icon={<ComplianceRiskIcon />}
            title="Compliance Risk"
            description="Manual processes create compliance gaps. Audit requests become fire drills. POPIA obligations are met reactively, not proactively."
          />
          <FeatureCard
            icon={<MisalignmentIcon />}
            title="Stakeholder Misalignment"
            description="Hiring managers, recruiters, and executives working from different information. Decisions delayed by miscommunication."
          />
        </div>
      </SectionWrapper>

      {/* --- How ShumelaHire Helps --- */}
      <SectionWrapper bg="offwhite">
        <div className="space-y-20">
          <FeatureDetailBlock
            title="Standardised Workflows"
            description="Define hiring processes once, deploy them across every business unit. Ensure consistency without constraining flexibility."
            features={[
              "Template-based requisitions",
              "Configurable approval chains",
              "Automated stage transitions",
            ]}
            imageSlot={<ImagePlaceholder label="Workflow configuration" />}
            reversed={false}
          />

          <FeatureDetailBlock
            title="Real-Time Pipeline Intelligence"
            description="Every requisition, every candidate, every stage — visible in real time. Identify bottlenecks before they become blockers."
            features={[
              "Live pipeline dashboards",
              "Time-to-fill tracking",
              "Capacity forecasting",
            ]}
            imageSlot={<ImagePlaceholder label="Pipeline dashboard" />}
            reversed={true}
          />

          <FeatureDetailBlock
            title="Enterprise Compliance"
            description="POPIA compliance, role-based access, and audit logging are not configuration options. They are always on."
            features={[
              "Automated compliance controls",
              "Immutable audit trail",
              "Data residency guarantees",
            ]}
            imageSlot={<ImagePlaceholder label="Compliance controls" />}
            reversed={false}
          />
        </div>
      </SectionWrapper>

      {/* --- Testimonial --- */}
      <SectionWrapper bg="white">
        <TestimonialBlock
          quote="We went from managing hiring across twelve business units in spreadsheets to a single platform with complete visibility. The compliance team no longer dreads audit season."
          author="Group Head of HR"
          role="Financial Services"
          organisation="JSE-Listed Corporate"
        />
      </SectionWrapper>

      {/* --- Stats --- */}
      <StatsRow
        stats={[
          { value: "12", label: "Business Units Unified" },
          { value: "60%", label: "Faster Approvals" },
          { value: "100%", label: "Audit Ready" },
          { value: "3x", label: "Recruiter Productivity" },
        ]}
      />

      {/* --- CTA --- */}
      <CTASection
        headline="See How ShumelaHire Works for Corporates"
        ctaLabel="Request a Demo"
        ctaHref="/demo"
      />
    </>
  );
}
