import Link from 'next/link';

interface BlogCardProps {
  title: string;
  description: string;
  date: string;
  category: string;
  readTime: string;
  slug: string;
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr);
  return date.toLocaleDateString('en-ZA', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  });
}

export default function BlogCard({
  title,
  description,
  date,
  category,
  readTime,
  slug,
}: BlogCardProps) {
  return (
    <Link href={`/blog/${slug}`} className="group block">
      <article className="bg-white border border-[#E2E8F0] rounded-[2px] overflow-hidden hover:shadow-md transition-shadow duration-200">
        {/* Placeholder image area */}
        <div className="bg-[#F8FAFC] h-48 flex items-center justify-center">
          <svg
            width="40"
            height="40"
            viewBox="0 0 40 40"
            fill="none"
            stroke="#CBD5E1"
            strokeWidth="1.2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <rect x="6" y="6" width="28" height="28" rx="2" />
            <line x1="6" y1="14" x2="34" y2="14" />
            <line x1="12" y1="20" x2="28" y2="20" />
            <line x1="12" y1="25" x2="24" y2="25" />
            <line x1="12" y1="30" x2="20" y2="30" />
          </svg>
        </div>

        {/* Body */}
        <div className="p-6">
          <span className="inline-block text-xs font-bold uppercase tracking-[0.1em] text-[#05527E] bg-[#05527E]/5 px-2.5 py-1 rounded-[2px] mb-3">
            {category}
          </span>

          <h3 className="text-lg font-bold text-[#0F172A] tracking-[-0.02em] mb-2 group-hover:text-[#05527E] transition-colors line-clamp-2">
            {title}
          </h3>

          <p className="text-sm text-[#64748B] leading-relaxed line-clamp-3 mb-4">
            {description}
          </p>

          {/* Footer */}
          <div className="flex items-center gap-3 text-xs text-[#94A3B8]">
            <time dateTime={date}>{formatDate(date)}</time>
            <span aria-hidden="true">&middot;</span>
            <span>{readTime}</span>
          </div>
        </div>
      </article>
    </Link>
  );
}
