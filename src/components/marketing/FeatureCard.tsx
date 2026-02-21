interface FeatureCardProps {
  icon: React.ReactNode;
  title: string;
  description: string;
}

export default function FeatureCard({ icon, title, description }: FeatureCardProps) {
  return (
    <div className="bg-white border border-[#E2E8F0] rounded-[2px] p-6 shadow-sm hover:shadow-md transition-shadow duration-200">
      <div className="w-12 h-12 rounded-[2px] bg-[#05527E]/10 flex items-center justify-center mb-4 text-[#05527E]">
        {icon}
      </div>

      <h3 className="text-lg font-bold text-[#0F172A] tracking-[-0.02em] mb-2">
        {title}
      </h3>

      <p className="text-sm text-[#64748B] leading-relaxed">{description}</p>
    </div>
  );
}
