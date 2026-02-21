interface FeatureDetailBlockProps {
  title: string;
  description: string;
  features: string[];
  imageSlot: React.ReactNode;
  reversed?: boolean;
}

function GoldCheck() {
  return (
    <svg
      width="18"
      height="18"
      viewBox="0 0 18 18"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      className="shrink-0"
    >
      <path
        d="M3.75 9.75L7.5 13.5L14.25 4.5"
        stroke="#F1C54B"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}

export default function FeatureDetailBlock({
  title,
  description,
  features,
  imageSlot,
  reversed = false,
}: FeatureDetailBlockProps) {
  return (
    <div
      className={`grid lg:grid-cols-2 gap-12 lg:gap-16 items-center ${
        reversed ? 'direction-rtl' : ''
      }`}
      style={reversed ? { direction: 'ltr' } : undefined}
    >
      <div className={reversed ? 'lg:order-2' : ''}>
        <h3 className="text-2xl md:text-3xl font-bold tracking-[-0.03em] text-[#0F172A]">
          {title}
        </h3>

        <p className="text-[#64748B] leading-relaxed mt-4">{description}</p>

        <ul className="mt-4 space-y-3">
          {features.map((feature) => (
            <li key={feature} className="flex items-start gap-3 text-sm text-[#1E293B]">
              <span className="mt-0.5">
                <GoldCheck />
              </span>
              {feature}
            </li>
          ))}
        </ul>
      </div>

      <div className={reversed ? 'lg:order-1' : ''}>
        <div className="rounded-[2px] bg-[#F8FAFC] border border-[#E2E8F0] p-8">
          {imageSlot}
        </div>
      </div>
    </div>
  );
}
