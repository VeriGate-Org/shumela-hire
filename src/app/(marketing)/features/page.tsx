import type { Metadata } from "next";
import SectionWrapper from "@/components/marketing/SectionWrapper";
import HeroSection from "@/components/marketing/HeroSection";
import FeatureCard from "@/components/marketing/FeatureCard";
import CTASection from "@/components/marketing/CTASection";

export const metadata: Metadata = {
  title: "Platform Features",
  description:
    "Explore ShumelaHire's comprehensive talent acquisition platform. From requisition management to analytics, every feature is built for institutional hiring.",
};

/* ------------------------------------------------------------------ */
/*  Icon helpers — simple inline SVGs (w-6 h-6, stroke-based)        */
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

function RoleDefinitionIcon() {
  return (
    <svg {...iconProps} viewBox="0 0 24 24">
      <rect x="4" y="3" width="16" height="18" rx="1" />
      <line x1="8" y1="8" x2="16" y2="8" />
      <line x1="8" y1="12" x2="16" y2="12" />
      <line x1="8" y1="16" x2="12" y2="16" />
    </svg>
  );
}

function ApprovalWorkflowIcon() {
  return (
    <svg {...iconProps} viewBox="0 0 24 24">
      <path d="M9 11l3 3 8-8" />
      <path d="M20 12v6a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h9" />
    </svg>
  );
}

function BudgetControlsIcon() {
  return (
    <svg {...iconProps} viewBox="0 0 24 24">
      <line x1="12" y1="2" x2="12" y2="22" />
      <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6" />
    </svg>
  );
}

function PipelineVisibilityIcon() {
  return (
    <svg {...iconProps} viewBox="0 0 24 24">
      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
      <circle cx="12" cy="12" r="3" />
    </svg>
  );
}

function StructuredScoringIcon() {
  return (
    <svg {...iconProps} viewBox="0 0 24 24">
      <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" />
    </svg>
  );
}

function CommunicationHubIcon() {
  return (
    <svg {...iconProps} viewBox="0 0 24 24">
      <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
    </svg>
  );
}

function DocumentManagementIcon() {
  return (
    <svg {...iconProps} viewBox="0 0 24 24">
      <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
      <polyline points="14 2 14 8 20 8" />
    </svg>
  );
}

function PipelineAnalyticsIcon() {
  return (
    <svg {...iconProps} viewBox="0 0 24 24">
      <line x1="18" y1="20" x2="18" y2="10" />
      <line x1="12" y1="20" x2="12" y2="4" />
      <line x1="6" y1="20" x2="6" y2="14" />
    </svg>
  );
}

function DiversityMetricsIcon() {
  return (
    <svg {...iconProps} viewBox="0 0 24 24">
      <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
      <circle cx="9" cy="7" r="4" />
      <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
      <path d="M16 3.13a4 4 0 0 1 0 7.75" />
    </svg>
  );
}

function ExecutiveDashboardIcon() {
  return (
    <svg {...iconProps} viewBox="0 0 24 24">
      <rect x="3" y="3" width="7" height="7" rx="1" />
      <rect x="14" y="3" width="7" height="7" rx="1" />
      <rect x="3" y="14" width="7" height="7" rx="1" />
      <rect x="14" y="14" width="7" height="7" rx="1" />
    </svg>
  );
}

function PopiaComplianceIcon() {
  return (
    <svg {...iconProps} viewBox="0 0 24 24">
      <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
    </svg>
  );
}

function RoleBasedAccessIcon() {
  return (
    <svg {...iconProps} viewBox="0 0 24 24">
      <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
      <path d="M7 11V7a5 5 0 0 1 10 0v4" />
    </svg>
  );
}

function AuditLoggingIcon() {
  return (
    <svg {...iconProps} viewBox="0 0 24 24">
      <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
      <polyline points="14 2 14 8 20 8" />
      <line x1="16" y1="13" x2="8" y2="13" />
      <line x1="16" y1="17" x2="8" y2="17" />
    </svg>
  );
}

function DataResidencyIcon() {
  return (
    <svg {...iconProps} viewBox="0 0 24 24">
      <ellipse cx="12" cy="5" rx="9" ry="3" />
      <path d="M21 12c0 1.66-4 3-9 3s-9-1.34-9-3" />
      <path d="M3 5v14c0 1.66 4 3 9 3s9-1.34 9-3V5" />
    </svg>
  );
}

/* ------------------------------------------------------------------ */
/*  Category block helper                                             */
/* ------------------------------------------------------------------ */

interface CategoryBlockProps {
  overline: string;
  title: string;
  description: string;
  children: React.ReactNode;
}

function CategoryBlock({
  overline,
  title,
  description,
  children,
}: CategoryBlockProps) {
  return (
    <div className="mb-20 last:mb-0">
      <p className="text-xs font-bold uppercase tracking-[0.18em] text-[#05527E] mb-3">
        {overline}
      </p>
      <h2 className="text-2xl md:text-3xl font-bold tracking-[-0.03em] text-[#0F172A] mb-4">
        {title}
      </h2>
      <p className="text-base text-[#64748B] leading-relaxed max-w-2xl mb-10">
        {description}
      </p>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {children}
      </div>
    </div>
  );
}

/* ------------------------------------------------------------------ */
/*  Page                                                              */
/* ------------------------------------------------------------------ */

export default function FeaturesPage() {
  return (
    <>
      {/* --- Hero --- */}
      <SectionWrapper bg="offwhite">
        <HeroSection
          overline="THE PLATFORM"
          headline="Every Feature Built for Institutional Hiring"
          subheadline="From requisition creation to offer management, ShumelaHire provides the tools institutions need to hire with precision and accountability."
          primaryCTA={{ label: "Request a Demo", href: "/demo" }}
        />
      </SectionWrapper>

      {/* --- Feature Categories --- */}
      <SectionWrapper bg="white">
        {/* Category 1 */}
        <CategoryBlock
          overline="REQUISITION MANAGEMENT"
          title="Structured From the Start"
          description="Every hire begins with a requisition. ShumelaHire ensures that process is rigorous, auditable, and aligned with organisational policy from day one."
        >
          <FeatureCard
            icon={<RoleDefinitionIcon />}
            title="Role Definition"
            description="Define requirements, competencies, and evaluation criteria before a single candidate is sourced."
          />
          <FeatureCard
            icon={<ApprovalWorkflowIcon />}
            title="Approval Workflows"
            description="Multi-level approval chains ensure every requisition meets organisational standards before going live."
          />
          <FeatureCard
            icon={<BudgetControlsIcon />}
            title="Budget Controls"
            description="Salary bands, grade levels, and cost-centre allocation built into every requisition."
          />
        </CategoryBlock>

        {/* Category 2 */}
        <CategoryBlock
          overline="CANDIDATE MANAGEMENT"
          title="Every Candidate, Tracked and Evaluated"
          description="From application to offer, every candidate interaction is captured, structured, and accessible to the right stakeholders."
        >
          <FeatureCard
            icon={<PipelineVisibilityIcon />}
            title="Pipeline Visibility"
            description="Real-time view of every candidate across every stage. No spreadsheets, no lost applications."
          />
          <FeatureCard
            icon={<StructuredScoringIcon />}
            title="Structured Scoring"
            description="Standardised scorecards with configurable criteria ensure consistent, defensible evaluations."
          />
          <FeatureCard
            icon={<CommunicationHubIcon />}
            title="Communication Hub"
            description="Automated and manual candidate communications with full audit trail."
          />
          <FeatureCard
            icon={<DocumentManagementIcon />}
            title="Document Management"
            description="Centralised storage for CVs, assessments, and compliance documents."
          />
        </CategoryBlock>

        {/* Category 3 */}
        <CategoryBlock
          overline="ANALYTICS & REPORTING"
          title="Data-Driven Hiring Decisions"
          description="Surface the metrics that matter. ShumelaHire turns hiring data into actionable intelligence for every level of your organisation."
        >
          <FeatureCard
            icon={<PipelineAnalyticsIcon />}
            title="Pipeline Analytics"
            description="Time-to-fill, conversion rates, and bottleneck identification across every stage."
          />
          <FeatureCard
            icon={<DiversityMetricsIcon />}
            title="Diversity Metrics"
            description="Track representation across your pipeline with configurable demographic reporting."
          />
          <FeatureCard
            icon={<ExecutiveDashboardIcon />}
            title="Executive Dashboards"
            description="Board-ready reports and KPI tracking for talent acquisition leadership."
          />
        </CategoryBlock>

        {/* Category 4 */}
        <CategoryBlock
          overline="SECURITY & COMPLIANCE"
          title="Enterprise-Grade by Default"
          description="Security and compliance are not add-ons. They are foundational to every layer of the platform."
        >
          <FeatureCard
            icon={<PopiaComplianceIcon />}
            title="POPIA Compliance"
            description="Data handling, consent management, and retention policies aligned with South African privacy law."
          />
          <FeatureCard
            icon={<RoleBasedAccessIcon />}
            title="Role-Based Access"
            description="Granular permissions ensure every user sees precisely what they need and nothing more."
          />
          <FeatureCard
            icon={<AuditLoggingIcon />}
            title="Audit Logging"
            description="Every action logged, timestamped, and attributable. Complete accountability at every level."
          />
          <FeatureCard
            icon={<DataResidencyIcon />}
            title="Data Residency"
            description="South African-hosted infrastructure with configurable data residency controls."
          />
        </CategoryBlock>
      </SectionWrapper>

      {/* --- CTA --- */}
      <CTASection
        headline="See the Platform in Action"
        subtext="Schedule a personalised demonstration and see how ShumelaHire fits your organisation."
        ctaLabel="Request a Demo"
        ctaHref="/demo"
      />
    </>
  );
}
