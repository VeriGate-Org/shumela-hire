import SectionWrapper from './SectionWrapper';
import MarketingButton from './MarketingButton';

interface CTASectionProps {
  headline: string;
  subtext?: string;
  ctaLabel: string;
  ctaHref: string;
}

export default function CTASection({
  headline,
  subtext,
  ctaLabel,
  ctaHref,
}: CTASectionProps) {
  return (
    <SectionWrapper bg="navy">
      <div className="text-center">
        <h2 className="text-2xl md:text-3xl font-bold tracking-[-0.03em] text-white mb-6">
          {headline}
        </h2>

        {subtext && (
          <p className="text-white/70 leading-relaxed max-w-2xl mx-auto mb-10">
            {subtext}
          </p>
        )}

        <MarketingButton variant="dark-bg" size="lg" href={ctaHref}>
          {ctaLabel}
        </MarketingButton>
      </div>
    </SectionWrapper>
  );
}
