import type { Metadata } from 'next';
import HeroSection from '@/components/marketing/HeroSection';
import SectionWrapper from '@/components/marketing/SectionWrapper';

export const metadata: Metadata = {
  title: 'Privacy Policy',
  description:
    'How ShumelaHire collects, uses, and protects personal information in accordance with POPIA.',
};

export default function PrivacyPage() {
  return (
    <>
      <div className="bg-[#F8FAFC]">
        <HeroSection
          overline="LEGAL"
          headline="Privacy Policy"
          subheadline="How ShumelaHire collects, uses, and protects personal information."
        />
      </div>

      <SectionWrapper bg="white">
        <div className="max-w-3xl mx-auto">
          <p className="text-sm text-[#94A3B8] mb-10">
            Last updated: February 2026
          </p>

          <p className="text-[#1E293B] leading-[1.8] mb-6">
            ShumelaHire, operated by Arthmatic DevWorks (Pty) Ltd, is committed to protecting
            the personal information of all users and candidates processed through the platform.
            This Privacy Policy explains how we collect, use, store, and protect personal
            information in compliance with the Protection of Personal Information Act, 2013
            (POPIA).
          </p>

          {/* 1. Information We Collect */}
          <h2 className="text-xl font-bold text-[#0F172A] tracking-[-0.02em] mt-10 mb-4">
            1. Information We Collect
          </h2>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            We collect the following categories of personal information through the platform:
          </p>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            <strong className="font-bold text-[#0F172A]">Candidate data.</strong> Information
            submitted by or about candidates during the recruitment process, including names,
            contact details, CVs, qualifications, interview notes, assessment scores, and
            reference check outcomes. This data is collected and processed on behalf of the
            organisation conducting the recruitment.
          </p>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            <strong className="font-bold text-[#0F172A]">User account data.</strong> Information
            provided during account registration and platform use, including names, email
            addresses, job titles, and organisational affiliations.
          </p>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            <strong className="font-bold text-[#0F172A]">Usage data.</strong> Technical
            information about how users interact with the platform, including browser type, device
            information, pages visited, and feature usage patterns. This data is collected
            automatically and is used to improve platform performance and reliability.
          </p>

          {/* 2. How We Use Your Information */}
          <h2 className="text-xl font-bold text-[#0F172A] tracking-[-0.02em] mt-10 mb-4">
            2. How We Use Your Information
          </h2>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            Personal information collected through ShumelaHire is used for the following purposes:
          </p>
          <ul className="list-disc pl-6 text-[#1E293B] leading-[1.8] mb-4 space-y-2">
            <li>
              <strong className="font-bold text-[#0F172A]">Recruitment workflows.</strong>{' '}
              Processing candidate applications, facilitating evaluations, scheduling interviews,
              and managing hiring decisions on behalf of subscribing organisations.
            </li>
            <li>
              <strong className="font-bold text-[#0F172A]">Platform operation.</strong>{' '}
              Authenticating users, managing permissions, delivering notifications, and providing
              customer support.
            </li>
            <li>
              <strong className="font-bold text-[#0F172A]">Analytics and reporting.</strong>{' '}
              Generating aggregated, anonymised analytics to help organisations measure and
              improve their hiring processes. Individual candidate data is never used for purposes
              beyond the specific recruitment process without explicit consent.
            </li>
          </ul>

          {/* 3. Data Storage and Security */}
          <h2 className="text-xl font-bold text-[#0F172A] tracking-[-0.02em] mt-10 mb-4">
            3. Data Storage and Security
          </h2>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            All data processed through ShumelaHire is stored on infrastructure hosted within the
            Amazon Web Services (AWS) Africa (Cape Town) region. Data does not leave South African
            borders unless explicitly configured by the subscribing organisation.
          </p>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            Security measures include encryption of data in transit and at rest, role-based access
            controls enforced at the application level, regular security assessments, and
            comprehensive audit logging of all data access events.
          </p>

          {/* 4. Data Retention */}
          <h2 className="text-xl font-bold text-[#0F172A] tracking-[-0.02em] mt-10 mb-4">
            4. Data Retention
          </h2>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            ShumelaHire retains personal information only for as long as necessary to fulfil the
            purposes for which it was collected, or as required by applicable law. Subscribing
            organisations can configure retention periods according to their own policies.
          </p>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            When data is no longer required, it is securely deleted or anonymised. Organisations
            may request bulk deletion of candidate data at any time through the platform
            administration interface or by contacting our support team.
          </p>

          {/* 5. Your Rights Under POPIA */}
          <h2 className="text-xl font-bold text-[#0F172A] tracking-[-0.02em] mt-10 mb-4">
            5. Your Rights Under POPIA
          </h2>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            Under the Protection of Personal Information Act, you have the following rights
            regarding your personal information:
          </p>
          <ul className="list-disc pl-6 text-[#1E293B] leading-[1.8] mb-4 space-y-2">
            <li>
              <strong className="font-bold text-[#0F172A]">Access.</strong> You may request
              confirmation of whether we hold personal information about you and request access to
              that information.
            </li>
            <li>
              <strong className="font-bold text-[#0F172A]">Correction.</strong> You may request
              correction or deletion of personal information that is inaccurate, irrelevant,
              excessive, out of date, incomplete, or misleading.
            </li>
            <li>
              <strong className="font-bold text-[#0F172A]">Deletion.</strong> You may request
              deletion of personal information that is no longer necessary for the purpose for
              which it was collected.
            </li>
            <li>
              <strong className="font-bold text-[#0F172A]">Objection.</strong> You may object to
              the processing of your personal information on reasonable grounds.
            </li>
          </ul>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            To exercise any of these rights, please contact our data protection officer using the
            details provided below.
          </p>

          {/* 6. Third-Party Services */}
          <h2 className="text-xl font-bold text-[#0F172A] tracking-[-0.02em] mt-10 mb-4">
            6. Third-Party Services
          </h2>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            ShumelaHire uses Amazon Web Services (AWS) for cloud infrastructure, including
            compute, storage, and database services. AWS operates under its own data processing
            agreements and complies with international data protection standards.
          </p>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            We do not sell, rent, or trade personal information to third parties. Personal
            information is shared with third parties only where required by law or where necessary
            to provide the platform services as contracted with subscribing organisations.
          </p>

          {/* 7. Contact */}
          <h2 className="text-xl font-bold text-[#0F172A] tracking-[-0.02em] mt-10 mb-4">
            7. Contact
          </h2>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            For questions about this Privacy Policy, to exercise your rights under POPIA, or to
            report a data protection concern, please contact our data protection officer:
          </p>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            <strong className="font-bold text-[#0F172A]">Email:</strong>{' '}
            <a
              href="mailto:hello@shumelahire.co.za"
              className="text-[#05527E] underline hover:text-[#05527E]/80 transition-colors"
            >
              hello@shumelahire.co.za
            </a>
          </p>
          <p className="text-[#1E293B] leading-[1.8]">
            We will respond to all requests within the timeframes prescribed by POPIA.
          </p>
        </div>
      </SectionWrapper>
    </>
  );
}
