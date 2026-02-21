import type { Metadata } from "next";
import SectionWrapper from "@/components/marketing/SectionWrapper";
import HeroSection from "@/components/marketing/HeroSection";
import CTASection from "@/components/marketing/CTASection";

export const metadata: Metadata = {
  title: "About ShumelaHire",
  description:
    "ShumelaHire, meaning 'hire well' in isiZulu, was built to bring structure and accountability to institutional talent acquisition in South Africa and beyond.",
};

/* ------------------------------------------------------------------ */
/*  Page                                                              */
/* ------------------------------------------------------------------ */

export default function AboutPage() {
  return (
    <>
      {/* --- Hero --- */}
      <SectionWrapper bg="offwhite">
        <HeroSection
          overline="ABOUT US"
          headline="Built to Hire Well"
          subheadline="ShumelaHire — from the isiZulu 'shumela,' meaning to hire or employ — was founded on a simple conviction: institutions that serve the public deserve hiring processes worthy of that responsibility."
          primaryCTA={{ label: "Get in Touch", href: "/contact" }}
        />
      </SectionWrapper>

      {/* --- Name Origin --- */}
      <SectionWrapper bg="white">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
          {/* Left column */}
          <div>
            <p className="text-xs font-bold uppercase tracking-[0.18em] text-[#05527E] mb-4">
              OUR NAME
            </p>
            <h2 className="text-2xl md:text-3xl font-bold tracking-[-0.03em] text-[#0F172A] mb-6">
              Shumela
            </h2>
            <p className="text-base text-[#64748B] leading-relaxed mb-4">
              &lsquo;Shumela&rsquo; is derived from isiZulu, meaning to hire or
              employ. We chose this name because it speaks directly to what we
              do — no abstraction, no metaphor.
            </p>
            <p className="text-base text-[#64748B] leading-relaxed">
              ShumelaHire exists to help institutions hire well.
            </p>
          </div>

          {/* Right column — decorative typographic element */}
          <div className="flex flex-col items-center justify-center text-center">
            <div className="border border-[#E2E8F0] rounded-[2px] px-12 py-16 w-full max-w-md">
              <p className="text-6xl md:text-7xl lg:text-8xl font-extrabold tracking-[-0.04em] text-[#05527E] mb-4 select-none">
                Shumela
              </p>
              <div className="w-16 h-px bg-[#F1C54B] mx-auto mb-4" />
              <p className="text-sm font-bold uppercase tracking-[0.18em] text-[#64748B]">
                /shu·me·la/
              </p>
              <p className="text-base text-[#64748B] leading-relaxed mt-2">
                verb (isiZulu) — to hire, to employ
              </p>
            </div>
          </div>
        </div>
      </SectionWrapper>

      {/* --- Mission --- */}
      <SectionWrapper bg="navy">
        <div className="text-center">
          <p className="text-xs font-bold uppercase tracking-[0.18em] text-[#F1C54B] mb-6">
            OUR MISSION
          </p>
          <blockquote className="text-2xl md:text-3xl font-bold tracking-[-0.03em] text-white max-w-3xl mx-auto leading-snug">
            &ldquo;To provide institutions with talent acquisition
            infrastructure that is structured, transparent, and accountable — so
            that every hiring decision can withstand scrutiny.&rdquo;
          </blockquote>
        </div>
      </SectionWrapper>

      {/* --- Values --- */}
      <SectionWrapper bg="white">
        <div className="text-center mb-14">
          <p className="text-xs font-bold uppercase tracking-[0.18em] text-[#05527E] mb-4">
            OUR VALUES
          </p>
          <h2 className="text-2xl md:text-3xl font-bold tracking-[-0.03em] text-[#0F172A]">
            Principles That Guide Every Decision
          </h2>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          <div className="border border-[#E2E8F0] rounded-[2px] p-8">
            <h3 className="text-lg font-bold text-[#0F172A] mb-3">
              Precision Over Speed
            </h3>
            <p className="text-[#64748B] leading-relaxed">
              We do not optimise for velocity at the expense of rigour. Every
              feature is designed to produce defensible outcomes.
            </p>
          </div>

          <div className="border border-[#E2E8F0] rounded-[2px] p-8">
            <h3 className="text-lg font-bold text-[#0F172A] mb-3">
              Transparency as Default
            </h3>
            <p className="text-[#64748B] leading-relaxed">
              Audit trails, structured evaluations, and clear accountability are
              not premium features. They are the baseline.
            </p>
          </div>

          <div className="border border-[#E2E8F0] rounded-[2px] p-8">
            <h3 className="text-lg font-bold text-[#0F172A] mb-3">
              Institutional Empathy
            </h3>
            <p className="text-[#64748B] leading-relaxed">
              We build for the complexity of real organisations — approval
              chains, compliance requirements, and multi-stakeholder
              decision-making.
            </p>
          </div>

          <div className="border border-[#E2E8F0] rounded-[2px] p-8">
            <h3 className="text-lg font-bold text-[#0F172A] mb-3">
              South African First
            </h3>
            <p className="text-[#64748B] leading-relaxed">
              Built in South Africa, hosted in South Africa, designed for the
              regulatory and cultural context of the continent&rsquo;s most
              sophisticated economy.
            </p>
          </div>
        </div>
      </SectionWrapper>

      {/* --- The Company --- */}
      <SectionWrapper bg="offwhite">
        <div className="max-w-3xl">
          <p className="text-xs font-bold uppercase tracking-[0.18em] text-[#05527E] mb-4">
            THE COMPANY
          </p>
          <h2 className="text-2xl md:text-3xl font-bold tracking-[-0.03em] text-[#0F172A] mb-6">
            Arthmatic DevWorks
          </h2>
          <p className="text-base text-[#64748B] leading-relaxed mb-4">
            ShumelaHire is built and maintained by Arthmatic DevWorks, a South
            African software engineering firm specialising in enterprise
            platforms for regulated industries. We combine deep technical
            capability with domain expertise in human capital management.
          </p>
          <p className="text-base text-[#64748B] leading-relaxed">
            Our engineering team builds with the same rigour we expect from the
            institutions we serve: clean architecture, comprehensive testing,
            and infrastructure that scales.
          </p>
        </div>
      </SectionWrapper>

      {/* --- CTA --- */}
      <CTASection
        headline="Work With Us"
        subtext="Whether you are an institution looking to transform your hiring process or a technologist who shares our values, we would like to hear from you."
        ctaLabel="Get in Touch"
        ctaHref="/contact"
      />
    </>
  );
}
