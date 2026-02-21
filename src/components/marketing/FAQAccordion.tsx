'use client';

interface FAQItem {
  question: string;
  answer: string;
}

interface FAQAccordionProps {
  items: FAQItem[];
}

export default function FAQAccordion({ items }: FAQAccordionProps) {
  return (
    <div>
      {items.map((item, index) => (
        <details
          key={index}
          className={`group ${
            index < items.length - 1 ? 'border-b border-[#E2E8F0]' : ''
          }`}
        >
          <summary className="cursor-pointer py-5 text-[#0F172A] font-medium flex justify-between items-center list-none [&::-webkit-details-marker]:hidden">
            <span>{item.question}</span>
            <svg
              width="20"
              height="20"
              viewBox="0 0 20 20"
              fill="none"
              className="w-5 h-5 text-[#64748B] shrink-0 ml-4 transition-transform duration-200 group-open:rotate-180"
            >
              <path
                d="M5 7.5L10 12.5L15 7.5"
                stroke="currentColor"
                strokeWidth="1.5"
                strokeLinecap="round"
                strokeLinejoin="round"
              />
            </svg>
          </summary>
          <p className="text-[#64748B] leading-relaxed pb-5 text-sm">
            {item.answer}
          </p>
        </details>
      ))}
    </div>
  );
}
