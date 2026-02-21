import Link from 'next/link';

const platformLinks = [
  { label: 'Features', href: '/features' },
  { label: 'Pricing', href: '/pricing' },
  { label: 'Security', href: '/security' },
  { label: 'Blog', href: '/blog' },
];

const solutionLinks = [
  { label: 'Corporates', href: '/solutions#corporates' },
  { label: 'Development Finance', href: '/solutions#development-finance' },
  { label: 'Government', href: '/solutions#government' },
];

const companyLinks = [
  { label: 'About', href: '/about' },
  { label: 'Contact', href: '/contact' },
  { label: 'Privacy', href: '/privacy' },
  { label: 'Terms', href: '/terms' },
];

const columnHeadingClasses = 'uppercase tracking-[0.18em] text-xs font-bold text-white/60 mb-5';
const linkClasses = 'block text-sm text-white/60 hover:text-white transition-colors';

export default function MarketingFooter() {
  return (
    <footer className="bg-[#032E49]">
      {/* Gold accent line */}
      <div className="h-[2px] bg-[#F1C54B]" />

      <div className="max-w-7xl mx-auto px-6 lg:px-8 py-16 md:py-20">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-12 lg:gap-8">
          {/* Column 1: Brand */}
          <div className="lg:col-span-1">
            <Link href="/" className="inline-block mb-5">
              <img
                src="/shumelahire-logo.svg"
                alt="ShumelaHire"
                className="h-8 brightness-0 invert"
              />
            </Link>
            <p className="text-sm text-white/60 leading-relaxed mb-6 max-w-xs">
              Structured talent acquisition for institutions that demand precision.
            </p>
            <p className="text-xs text-white/40">
              A product by Arthmatic DevWorks
            </p>
          </div>

          {/* Column 2: Platform */}
          <div>
            <h3 className={columnHeadingClasses}>Platform</h3>
            <ul className="space-y-3">
              {platformLinks.map((link) => (
                <li key={link.href}>
                  <Link href={link.href} className={linkClasses}>
                    {link.label}
                  </Link>
                </li>
              ))}
            </ul>
          </div>

          {/* Column 3: Solutions */}
          <div>
            <h3 className={columnHeadingClasses}>Solutions</h3>
            <ul className="space-y-3">
              {solutionLinks.map((link) => (
                <li key={link.href}>
                  <Link href={link.href} className={linkClasses}>
                    {link.label}
                  </Link>
                </li>
              ))}
            </ul>
          </div>

          {/* Column 4: Company */}
          <div>
            <h3 className={columnHeadingClasses}>Company</h3>
            <ul className="space-y-3">
              {companyLinks.map((link) => (
                <li key={link.href}>
                  <Link href={link.href} className={linkClasses}>
                    {link.label}
                  </Link>
                </li>
              ))}
            </ul>
          </div>
        </div>
      </div>

      {/* Bottom bar */}
      <div className="border-t border-white/10">
        <div className="max-w-7xl mx-auto px-6 lg:px-8 py-6 flex flex-col md:flex-row items-center justify-between gap-3">
          <p className="text-xs text-white/40">
            &copy; 2026 Arthmatic DevWorks. All rights reserved.
          </p>
          <p className="text-xs text-white/30">
            Built in South Africa
          </p>
        </div>
      </div>
    </footer>
  );
}
