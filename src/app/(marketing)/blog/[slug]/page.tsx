import type { Metadata } from 'next';
import Link from 'next/link';
import { notFound } from 'next/navigation';
import { getPostBySlug, getPostSlugs, renderMarkdown } from '@/lib/blog';
import SectionWrapper from '@/components/marketing/SectionWrapper';

/* ------------------------------------------------------------------ */
/*  Static params                                                      */
/* ------------------------------------------------------------------ */

export function generateStaticParams() {
  return getPostSlugs().map((slug) => ({ slug }));
}

/* ------------------------------------------------------------------ */
/*  Metadata                                                           */
/* ------------------------------------------------------------------ */

export async function generateMetadata({
  params,
}: {
  params: Promise<{ slug: string }>;
}): Promise<Metadata> {
  const { slug } = await params;
  const post = getPostBySlug(slug);

  if (!post) {
    return { title: 'Post Not Found' };
  }

  return {
    title: post.title,
    description: post.description,
  };
}

/* ------------------------------------------------------------------ */
/*  Date formatter                                                     */
/* ------------------------------------------------------------------ */

function formatDate(dateStr: string): string {
  const date = new Date(dateStr);
  return date.toLocaleDateString('en-ZA', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  });
}

/* ------------------------------------------------------------------ */
/*  Page                                                               */
/* ------------------------------------------------------------------ */

export default async function BlogPostPage({
  params,
}: {
  params: Promise<{ slug: string }>;
}) {
  const { slug } = await params;
  const post = getPostBySlug(slug);

  if (!post) {
    notFound();
  }

  const htmlContent = renderMarkdown(post.content);

  return (
    <>
      <SectionWrapper bg="white">
        <div className="max-w-3xl mx-auto">
          {/* Back link */}
          <Link
            href="/blog"
            className="inline-flex items-center gap-1.5 text-sm text-[#05527E] hover:text-[#05527E]/80 transition-colors mb-8"
          >
            <svg
              width="16"
              height="16"
              viewBox="0 0 16 16"
              fill="none"
              stroke="currentColor"
              strokeWidth="1.5"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <line x1="12" y1="8" x2="4" y2="8" />
              <polyline points="8 4 4 8 8 12" />
            </svg>
            Back to Blog
          </Link>

          {/* Category badge */}
          <span className="inline-block text-xs font-bold uppercase tracking-[0.1em] text-[#05527E] bg-[#05527E]/5 px-2.5 py-1 rounded-[2px] mb-4">
            {post.category}
          </span>

          {/* Title */}
          <h1 className="text-3xl md:text-4xl font-extrabold tracking-[-0.04em] text-[#0F172A] mb-4">
            {post.title}
          </h1>

          {/* Meta */}
          <div className="flex flex-wrap items-center gap-3 text-sm text-[#64748B] mb-10 pb-10 border-b border-[#E2E8F0]">
            <time dateTime={post.date}>{formatDate(post.date)}</time>
            <span aria-hidden="true">&middot;</span>
            <span>{post.author}</span>
            <span aria-hidden="true">&middot;</span>
            <span>{post.readTime}</span>
          </div>

          {/* Content */}
          <div
            className="blog-prose"
            dangerouslySetInnerHTML={{ __html: htmlContent }}
          />
        </div>
      </SectionWrapper>

      {/* Inline styles for blog prose */}
      <style>{`
        .blog-prose h2 {
          font-size: 1.25rem;
          font-weight: 700;
          color: #0F172A;
          letter-spacing: -0.02em;
          margin-top: 2.5rem;
          margin-bottom: 1rem;
        }

        .blog-prose h3 {
          font-size: 1.125rem;
          font-weight: 700;
          color: #0F172A;
          margin-top: 2rem;
          margin-bottom: 0.75rem;
        }

        .blog-prose p {
          color: #1E293B;
          line-height: 1.8;
          margin-bottom: 1.25rem;
        }

        .blog-prose ul,
        .blog-prose ol {
          list-style-position: inside;
          padding-left: 1.5rem;
          color: #1E293B;
          line-height: 1.8;
          margin-bottom: 1.25rem;
        }

        .blog-prose ul {
          list-style-type: disc;
        }

        .blog-prose ol {
          list-style-type: decimal;
        }

        .blog-prose li {
          margin-bottom: 0.5rem;
        }

        .blog-prose strong {
          font-weight: 700;
          color: #0F172A;
        }

        .blog-prose em {
          font-style: italic;
        }

        .blog-prose blockquote {
          border-left: 2px solid #F1C54B;
          padding-left: 1.5rem;
          font-style: italic;
          color: #64748B;
          margin-top: 1.5rem;
          margin-bottom: 1.5rem;
        }

        .blog-prose code {
          background-color: #F8FAFC;
          padding: 0.125rem 0.375rem;
          border-radius: 2px;
          font-size: 0.875rem;
          color: #0F172A;
        }
      `}</style>
    </>
  );
}
