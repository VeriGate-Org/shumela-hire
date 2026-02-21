interface TestimonialBlockProps {
  quote: string;
  author: string;
  role: string;
  organisation: string;
}

export default function TestimonialBlock({
  quote,
  author,
  role,
  organisation,
}: TestimonialBlockProps) {
  return (
    <div className="py-16 text-center">
      <p className="text-6xl text-[#F1C54B] font-serif leading-none mb-4">&ldquo;</p>

      <blockquote className="text-xl md:text-2xl text-[#0F172A] leading-relaxed italic max-w-3xl mx-auto">
        {quote}
      </blockquote>

      <div className="mt-6 text-sm text-[#64748B]">
        <span className="font-bold text-[#0F172A] not-italic">{author}</span>
        <br />
        <span className="text-[#64748B]">
          {role}, {organisation}
        </span>
      </div>
    </div>
  );
}
