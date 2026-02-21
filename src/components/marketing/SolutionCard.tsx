import Link from 'next/link';

interface SolutionCardProps {
  title: string;
  description: string;
  href: string;
  icon: React.ReactNode;
}

export default function SolutionCard({ title, description, href, icon }: SolutionCardProps) {
  return (
    <Link
      href={href}
      className="bg-white border border-[#E2E8F0] rounded-[2px] p-8 hover:shadow-md transition-shadow duration-200 group block"
    >
      <div className="w-14 h-14 rounded-[2px] bg-[#05527E]/10 flex items-center justify-center mb-5 text-[#05527E]">
        {icon}
      </div>

      <h3 className="text-xl font-bold text-[#0F172A] tracking-[-0.02em] mb-3 group-hover:text-[#05527E] transition-colors">
        {title}
      </h3>

      <p className="text-[#64748B] leading-relaxed text-sm mb-4">
        {description}
      </p>

      <span className="text-sm font-medium text-[#05527E]">
        Learn more →
      </span>
    </Link>
  );
}
