type SectionBg = 'white' | 'offwhite' | 'navy' | 'teal';

interface SectionWrapperProps {
  bg?: SectionBg;
  children: React.ReactNode;
  className?: string;
  id?: string;
}

const bgColors: Record<SectionBg, string> = {
  white: 'bg-[#FFFFFF]',
  offwhite: 'bg-[#F8FAFC]',
  navy: 'bg-[#032E49]',
  teal: 'bg-[#05527E]',
};

export default function SectionWrapper({
  bg = 'white',
  children,
  className = '',
  id,
}: SectionWrapperProps) {
  return (
    <section id={id} className={`${bgColors[bg]} py-20 md:py-28 lg:py-32 ${className}`.trim()}>
      <div className="max-w-7xl mx-auto px-6 lg:px-8">
        {children}
      </div>
    </section>
  );
}
