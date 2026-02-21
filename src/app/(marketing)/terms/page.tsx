import type { Metadata } from 'next';
import HeroSection from '@/components/marketing/HeroSection';
import SectionWrapper from '@/components/marketing/SectionWrapper';

export const metadata: Metadata = {
  title: 'Terms of Service',
  description:
    'Terms and conditions governing the use of the ShumelaHire platform.',
};

export default function TermsPage() {
  return (
    <>
      <div className="bg-[#F8FAFC]">
        <HeroSection
          overline="LEGAL"
          headline="Terms of Service"
          subheadline="Terms and conditions governing the use of the ShumelaHire platform."
        />
      </div>

      <SectionWrapper bg="white">
        <div className="max-w-3xl mx-auto">
          <p className="text-sm text-[#94A3B8] mb-10">
            Last updated: February 2026
          </p>

          <p className="text-[#1E293B] leading-[1.8] mb-6">
            These Terms of Service govern your access to and use of the ShumelaHire platform,
            operated by Arthmatic DevWorks (Pty) Ltd. By accessing or using the platform, you
            agree to be bound by these terms. If you do not agree with any part of these terms,
            you must not use the platform.
          </p>

          {/* 1. Acceptance of Terms */}
          <h2 className="text-xl font-bold text-[#0F172A] tracking-[-0.02em] mt-10 mb-4">
            1. Acceptance of Terms
          </h2>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            By accessing ShumelaHire, creating an account, or using any feature of the platform,
            you confirm that you have read, understood, and agree to these Terms of Service and
            our Privacy Policy. If you are using the platform on behalf of an organisation, you
            represent that you have the authority to bind that organisation to these terms.
          </p>

          {/* 2. Platform Access */}
          <h2 className="text-xl font-bold text-[#0F172A] tracking-[-0.02em] mt-10 mb-4">
            2. Platform Access
          </h2>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            Access to ShumelaHire is restricted to authorised users within subscribing
            organisations. Each user is responsible for maintaining the confidentiality and
            security of their account credentials.
          </p>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            Users are assigned roles with specific permissions that determine their access to
            platform features and data. Users must not attempt to access features, data, or
            administrative functions beyond those permitted by their assigned role.
          </p>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            Subscribing organisations are responsible for managing user accounts, including
            provisioning, role assignment, and deactivation of accounts when users no longer
            require access.
          </p>

          {/* 3. Acceptable Use */}
          <h2 className="text-xl font-bold text-[#0F172A] tracking-[-0.02em] mt-10 mb-4">
            3. Acceptable Use
          </h2>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            You may use ShumelaHire solely for its intended purpose: managing talent acquisition
            workflows within your organisation. You must not:
          </p>
          <ul className="list-disc pl-6 text-[#1E293B] leading-[1.8] mb-4 space-y-2">
            <li>Use the platform for any unlawful purpose or in violation of any applicable law or regulation.</li>
            <li>Attempt to gain unauthorised access to any part of the platform, other user accounts, or connected systems.</li>
            <li>Introduce malicious code, viruses, or any other harmful material to the platform.</li>
            <li>Scrape, harvest, or extract data from the platform through automated means without prior written consent.</li>
            <li>Use candidate data collected through the platform for purposes beyond the specific recruitment process for which it was collected.</li>
            <li>Interfere with or disrupt the platform or its supporting infrastructure.</li>
          </ul>

          {/* 4. Data and Privacy */}
          <h2 className="text-xl font-bold text-[#0F172A] tracking-[-0.02em] mt-10 mb-4">
            4. Data and Privacy
          </h2>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            The collection, processing, and storage of personal information through ShumelaHire
            is governed by our{' '}
            <a
              href="/privacy"
              className="text-[#05527E] underline hover:text-[#05527E]/80 transition-colors"
            >
              Privacy Policy
            </a>
            . By using the platform, you agree to the data practices described therein.
          </p>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            Subscribing organisations act as responsible parties under the Protection of Personal
            Information Act, 2013 (POPIA) for candidate data processed through the platform.
            Arthmatic DevWorks acts as an operator processing data on behalf of the subscribing
            organisation. Both parties are responsible for ensuring compliance with POPIA within
            their respective roles.
          </p>

          {/* 5. Intellectual Property */}
          <h2 className="text-xl font-bold text-[#0F172A] tracking-[-0.02em] mt-10 mb-4">
            5. Intellectual Property
          </h2>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            The ShumelaHire platform, including all software, design, content, trademarks, and
            documentation, is the intellectual property of Arthmatic DevWorks (Pty) Ltd. No right,
            title, or interest in the platform is transferred to users or subscribing
            organisations, except the limited right to use the platform in accordance with these
            terms.
          </p>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            Content uploaded to the platform by users, including job descriptions, candidate
            evaluations, and organisational data, remains the property of the subscribing
            organisation. By uploading content, you grant Arthmatic DevWorks a limited licence to
            store, process, and display that content solely for the purpose of providing the
            platform services.
          </p>

          {/* 6. Service Availability */}
          <h2 className="text-xl font-bold text-[#0F172A] tracking-[-0.02em] mt-10 mb-4">
            6. Service Availability
          </h2>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            Arthmatic DevWorks will use reasonable efforts to maintain the availability of the
            ShumelaHire platform. However, we do not guarantee uninterrupted access. The platform
            may be temporarily unavailable due to scheduled maintenance, system updates, or
            circumstances beyond our reasonable control.
          </p>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            Where practicable, we will provide advance notice of scheduled maintenance windows.
            Service level commitments, where applicable, are defined in the service agreement
            between Arthmatic DevWorks and the subscribing organisation.
          </p>

          {/* 7. Limitation of Liability */}
          <h2 className="text-xl font-bold text-[#0F172A] tracking-[-0.02em] mt-10 mb-4">
            7. Limitation of Liability
          </h2>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            To the maximum extent permitted by South African law, Arthmatic DevWorks shall not be
            liable for any indirect, incidental, special, consequential, or punitive damages
            arising from or related to your use of the platform, including but not limited to loss
            of data, loss of revenue, or business interruption.
          </p>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            Our total aggregate liability for any claims arising from or related to these terms
            or the use of the platform shall not exceed the total fees paid by the subscribing
            organisation to Arthmatic DevWorks in the twelve months preceding the claim.
          </p>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            Nothing in these terms excludes or limits liability that cannot be excluded or limited
            under applicable law.
          </p>

          {/* 8. Termination */}
          <h2 className="text-xl font-bold text-[#0F172A] tracking-[-0.02em] mt-10 mb-4">
            8. Termination
          </h2>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            Arthmatic DevWorks may suspend or terminate your access to the platform immediately
            if you breach these terms, if the subscribing organisation&apos;s service agreement is
            terminated, or if continued access poses a security risk to the platform or other
            users.
          </p>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            Upon termination, your right to access the platform ceases immediately. Subscribing
            organisations may request export of their data within 30 days of termination, after
            which data will be securely deleted in accordance with our retention policies.
          </p>

          {/* 9. Governing Law */}
          <h2 className="text-xl font-bold text-[#0F172A] tracking-[-0.02em] mt-10 mb-4">
            9. Governing Law
          </h2>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            These terms are governed by and construed in accordance with the laws of the Republic
            of South Africa. Any disputes arising from these terms or the use of the platform
            shall be subject to the exclusive jurisdiction of the courts of the Gauteng Division
            of the High Court of South Africa.
          </p>

          {/* 10. Contact */}
          <h2 className="text-xl font-bold text-[#0F172A] tracking-[-0.02em] mt-10 mb-4">
            10. Contact
          </h2>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            For questions about these Terms of Service, please contact us:
          </p>
          <p className="text-[#1E293B] leading-[1.8] mb-4">
            <strong className="font-bold text-[#0F172A]">Email:</strong>{' '}
            <a
              href="mailto:legal@shumelahire.co.za"
              className="text-[#05527E] underline hover:text-[#05527E]/80 transition-colors"
            >
              legal@shumelahire.co.za
            </a>
          </p>
          <p className="text-[#1E293B] leading-[1.8]">
            We reserve the right to update these terms from time to time. Continued use of the
            platform following any changes constitutes acceptance of the revised terms.
          </p>
        </div>
      </SectionWrapper>
    </>
  );
}
