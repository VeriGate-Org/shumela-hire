const placeholderLogos = [
  'Acme Corp',
  'Global Bank',
  'SA Gov',
  'DevFin',
  'TechCo',
  'MedGroup',
];

export default function TrustBar() {
  return (
    <section className="py-16">
      <div className="max-w-7xl mx-auto px-6 lg:px-8 text-center">
        <div className="h-[2px] w-16 bg-[#F1C54B] mx-auto mb-6" />

        <p className="text-xs font-bold uppercase tracking-[0.18em] text-[#64748B] mb-10">
          Trusted by leading institutions
        </p>

        <div className="flex flex-wrap items-center justify-center gap-8 md:gap-12">
          {placeholderLogos.map((name) => (
            <div
              key={name}
              className="flex items-center justify-center w-28 h-10 bg-[#64748B]/10 rounded-[2px] opacity-40"
            >
              <span className="text-xs font-medium text-[#64748B] select-none">
                {name}
              </span>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
