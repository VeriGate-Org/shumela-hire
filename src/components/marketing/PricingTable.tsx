interface PricingTableCategory {
  name: string;
  features: {
    name: string;
    values: (boolean | string)[];
  }[];
}

interface PricingTableProps {
  tiers: string[];
  categories: PricingTableCategory[];
  highlightedTier?: number;
}

function CheckIcon() {
  return (
    <svg
      width="20"
      height="20"
      viewBox="0 0 20 20"
      fill="none"
      className="w-5 h-5 text-[#05527E] mx-auto"
    >
      <path
        d="M5 10.5L8.5 14L15 6"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}

function DashIcon() {
  return (
    <svg
      width="20"
      height="20"
      viewBox="0 0 20 20"
      fill="none"
      className="w-5 h-5 text-[#CBD5E1] mx-auto"
    >
      <line
        x1="6"
        y1="10"
        x2="14"
        y2="10"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
      />
    </svg>
  );
}

export default function PricingTable({
  tiers,
  categories,
  highlightedTier = 1,
}: PricingTableProps) {
  return (
    <div className="overflow-x-auto">
      <table className="w-full text-sm border-collapse">
        <thead>
          <tr className="border-b border-[#E2E8F0]">
            <th className="text-left py-4 pr-4 w-1/4 text-[#64748B] font-medium">
              Feature
            </th>
            {tiers.map((tier, i) => (
              <th key={tier} className="py-4 px-4 text-center">
                <span
                  className={`font-bold text-[#0F172A] ${
                    i === highlightedTier
                      ? 'border-b-2 border-[#F1C54B] pb-1'
                      : ''
                  }`}
                >
                  {tier}
                </span>
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {categories.map((category) => (
            <>
              <tr key={`cat-${category.name}`}>
                <td
                  colSpan={tiers.length + 1}
                  className="bg-[#F8FAFC] py-3 px-4 font-bold text-[#0F172A] text-sm border-b border-[#E2E8F0]"
                >
                  {category.name}
                </td>
              </tr>
              {category.features.map((feature) => (
                <tr
                  key={`feat-${category.name}-${feature.name}`}
                  className="border-b border-[#E2E8F0]"
                >
                  <td className="py-3 pr-4 text-[#1E293B]">{feature.name}</td>
                  {feature.values.map((value, vi) => (
                    <td key={vi} className="py-3 px-4 text-center">
                      {typeof value === 'boolean' ? (
                        value ? (
                          <CheckIcon />
                        ) : (
                          <DashIcon />
                        )
                      ) : (
                        <span className="text-[#1E293B] font-medium">
                          {value}
                        </span>
                      )}
                    </td>
                  ))}
                </tr>
              ))}
            </>
          ))}
        </tbody>
      </table>
    </div>
  );
}
