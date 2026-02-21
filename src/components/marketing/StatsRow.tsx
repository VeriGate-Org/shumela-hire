import SectionWrapper from './SectionWrapper';
import StatBlock from './StatBlock';

interface StatsRowProps {
  stats: { value: string; label: string }[];
}

export default function StatsRow({ stats }: StatsRowProps) {
  return (
    <SectionWrapper bg="teal">
      <div className="grid grid-cols-2 md:grid-cols-4 gap-8 text-center">
        {stats.map((stat) => (
          <StatBlock key={stat.label} value={stat.value} label={stat.label} />
        ))}
      </div>
    </SectionWrapper>
  );
}
