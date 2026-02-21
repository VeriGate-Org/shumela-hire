import MarketingButton from './MarketingButton';

interface HeroSectionProps {
  overline: string;
  headline: string;
  subheadline: string;
  primaryCTA?: { label: string; href: string };
  secondaryCTA?: { label: string; href: string };
  children?: React.ReactNode;
  align?: 'left' | 'center';
}

export default function HeroSection({
  overline,
  headline,
  subheadline,
  primaryCTA,
  secondaryCTA,
  children,
  align = 'center',
}: HeroSectionProps) {
  const isCenter = align === 'center';

  return (
    <section className="pt-32 pb-20">
      <div className="max-w-7xl mx-auto px-6 lg:px-8">
        <div
          className={`${isCenter ? 'text-center' : 'text-left'} ${
            isCenter ? 'flex flex-col items-center' : ''
          }`}
        >
          <p className="text-xs font-bold uppercase tracking-[0.18em] text-[#05527E] mb-4">
            {overline}
          </p>

          <h1 className="text-4xl md:text-5xl lg:text-6xl font-extrabold tracking-[-0.04em] text-[#0F172A] mb-6">
            {headline}
          </h1>

          <p
            className={`text-lg md:text-xl text-[#64748B] leading-relaxed max-w-2xl mb-10 ${
              isCenter ? 'mx-auto' : ''
            }`}
          >
            {subheadline}
          </p>

          {(primaryCTA || secondaryCTA) && (
            <div
              className={`flex flex-wrap gap-4 ${
                isCenter ? 'justify-center' : 'justify-start'
              }`}
            >
              {primaryCTA && (
                <MarketingButton variant="primary" size="lg" href={primaryCTA.href}>
                  {primaryCTA.label}
                </MarketingButton>
              )}

              {secondaryCTA && (
                <MarketingButton variant="outline" size="lg" href={secondaryCTA.href}>
                  {secondaryCTA.label}
                </MarketingButton>
              )}
            </div>
          )}
        </div>

        {children && <div className="mt-16">{children}</div>}
      </div>
    </section>
  );
}
