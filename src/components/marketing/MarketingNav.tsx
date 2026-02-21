import Link from 'next/link';
import MarketingNavClient from './MarketingNavClient';
import MarketingButton from './MarketingButton';

const navLinks = [
  { label: 'Platform', href: '/features' },
  { label: 'Solutions', href: '/solutions' },
  { label: 'Pricing', href: '/pricing' },
  { label: 'About', href: '/about' },
  { label: 'Blog', href: '/blog' },
];

export default function MarketingNav() {
  return (
    <MarketingNavClient>
      <nav className="border-b border-[#E2E8F0]">
        <div className="max-w-7xl mx-auto px-6 lg:px-8 flex items-center justify-between h-16">
          {/* Logo */}
          <Link href="/">
            <img src="/shumelahire-logo.svg" alt="ShumelaHire" className="h-8" />
          </Link>

          {/* Centre nav links */}
          <ul className="flex items-center gap-8">
            {navLinks.map((link) => (
              <li key={link.href}>
                <Link
                  href={link.href}
                  className="text-sm font-medium text-[#1E293B] hover:text-[#05527E] transition-colors"
                >
                  {link.label}
                </Link>
              </li>
            ))}
          </ul>

          {/* Right side actions */}
          <div className="flex items-center gap-4">
            <Link
              href="/login"
              className="text-sm font-medium text-[#05527E] hover:text-[#05527E]/80 transition-colors"
            >
              Sign In
            </Link>
            <MarketingButton href="/demo" size="sm">
              Request Demo
            </MarketingButton>
          </div>
        </div>
      </nav>
    </MarketingNavClient>
  );
}
