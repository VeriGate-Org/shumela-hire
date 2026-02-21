'use client';

import { useState, useEffect } from 'react';
import { usePathname } from 'next/navigation';
import Link from 'next/link';
import MarketingButton from './MarketingButton';

const navLinks = [
  { label: 'Platform', href: '/features' },
  { label: 'Solutions', href: '/solutions' },
  { label: 'Pricing', href: '/pricing' },
  { label: 'About', href: '/about' },
  { label: 'Blog', href: '/blog' },
];

interface MarketingNavClientProps {
  children: React.ReactNode;
}

export default function MarketingNavClient({ children }: MarketingNavClientProps) {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);
  const pathname = usePathname();

  useEffect(() => {
    function handleScroll() {
      setScrolled(window.scrollY > 10);
    }

    handleScroll();
    window.addEventListener('scroll', handleScroll, { passive: true });
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  // Close mobile menu on route change
  useEffect(() => {
    setMobileMenuOpen(false);
  }, [pathname]);

  function isActive(href: string) {
    if (href === '/') return pathname === '/';
    return pathname.startsWith(href);
  }

  return (
    <header
      className={`fixed top-0 left-0 right-0 z-50 bg-white/95 backdrop-blur-sm transition-shadow duration-200 ${
        scrolled ? 'shadow-md' : ''
      }`}
    >
      {/* Desktop: render server-side children */}
      <div className="hidden lg:block">{children}</div>

      {/* Mobile header bar */}
      <div className="lg:hidden">
        <div className="max-w-7xl mx-auto px-6 flex items-center justify-between h-16">
          <Link href="/">
            <img src="/shumelahire-logo.svg" alt="ShumelaHire" className="h-8" />
          </Link>

          <button
            type="button"
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="inline-flex items-center justify-center p-2 text-[#1E293B] hover:text-[#05527E] transition-colors"
            aria-label={mobileMenuOpen ? 'Close menu' : 'Open menu'}
            aria-expanded={mobileMenuOpen}
          >
            {mobileMenuOpen ? (
              <svg
                className="h-6 w-6"
                fill="none"
                viewBox="0 0 24 24"
                strokeWidth={2}
                stroke="currentColor"
              >
                <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
              </svg>
            ) : (
              <svg
                className="h-6 w-6"
                fill="none"
                viewBox="0 0 24 24"
                strokeWidth={2}
                stroke="currentColor"
              >
                <path strokeLinecap="round" strokeLinejoin="round" d="M3.75 9h16.5m-16.5 6.75h16.5" />
              </svg>
            )}
          </button>
        </div>

        {/* Mobile slide-down menu */}
        <div
          className={`overflow-hidden transition-all duration-300 ease-in-out ${
            mobileMenuOpen ? 'max-h-[400px] opacity-100' : 'max-h-0 opacity-0'
          }`}
        >
          <nav className="border-t border-[#E2E8F0] bg-white px-6 pb-6 pt-4">
            <ul className="space-y-1">
              {navLinks.map((link) => (
                <li key={link.href}>
                  <Link
                    href={link.href}
                    className={`block px-3 py-3 text-sm font-medium transition-colors rounded-[2px] ${
                      isActive(link.href)
                        ? 'text-[#05527E] bg-[#05527E]/5'
                        : 'text-[#1E293B] hover:text-[#05527E] hover:bg-[#05527E]/5'
                    }`}
                  >
                    {link.label}
                  </Link>
                </li>
              ))}
            </ul>

            <div className="mt-6 space-y-3 border-t border-[#E2E8F0] pt-6">
              <Link
                href="/login"
                className="block text-center text-sm font-medium text-[#05527E] hover:text-[#05527E]/80 transition-colors py-2"
              >
                Sign In
              </Link>
              <MarketingButton href="/demo" size="md" className="w-full">
                Request Demo
              </MarketingButton>
            </div>
          </nav>
        </div>
      </div>
    </header>
  );
}
