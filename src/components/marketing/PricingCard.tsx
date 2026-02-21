import MarketingButton from './MarketingButton';

interface PricingCardProps {
  name: string;
  description: string;
  price: string;
  period?: string;
  features: string[];
  ctaLabel: string;
  ctaHref: string;
  highlighted?: boolean;
  ctaVariant?: 'primary' | 'outline';
}

export default function PricingCard({
  name,
  description,
  price,
  period,
  features,
  ctaLabel,
  ctaHref,
  highlighted = false,
  ctaVariant = 'outline',
}: PricingCardProps) {
  return (
    <div
      className={`bg-white rounded-[2px] p-8 relative ${
        highlighted
          ? 'border-2 border-[#F1C54B] shadow-lg'
          : 'border border-[#E2E8F0]'
      }`}
    >
      {highlighted && (
        <span className="absolute top-0 right-6 -translate-y-1/2 bg-[#F1C54B] text-[#032E49] text-xs font-bold uppercase tracking-[0.1em] px-3 py-1">
          RECOMMENDED
        </span>
      )}

      <p className="text-xs font-bold uppercase tracking-[0.18em] text-[#64748B]">
        {name}
      </p>

      <p className="text-3xl font-extrabold text-[#0F172A] tracking-[-0.03em] mt-2">
        {price}
        {period && (
          <span className="text-base font-normal text-[#64748B]">{period}</span>
        )}
      </p>

      <p className="text-sm text-[#64748B] mt-3 leading-relaxed">
        {description}
      </p>

      <div className="h-px bg-[#E2E8F0] my-6" />

      <ul className="space-y-3">
        {features.map((feature) => (
          <li key={feature} className="flex items-start gap-3">
            <svg
              width="20"
              height="20"
              viewBox="0 0 20 20"
              fill="none"
              className="w-5 h-5 text-[#05527E] shrink-0 mt-0.5"
            >
              <path
                d="M5 10.5L8.5 14L15 6"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
              />
            </svg>
            <span className="text-sm text-[#1E293B]">{feature}</span>
          </li>
        ))}
      </ul>

      <MarketingButton
        variant={ctaVariant}
        size="md"
        href={ctaHref}
        className="w-full mt-8"
      >
        {ctaLabel}
      </MarketingButton>
    </div>
  );
}
