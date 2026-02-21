interface StatBlockProps {
  value: string;
  label: string;
}

export default function StatBlock({ value, label }: StatBlockProps) {
  return (
    <div>
      <p className="text-4xl md:text-5xl font-extrabold text-[#F1C54B] tracking-[-0.03em]">
        {value}
      </p>
      <p className="text-sm text-white/70 uppercase tracking-[0.05em] font-medium mt-2">
        {label}
      </p>
    </div>
  );
}
