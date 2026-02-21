import type { Metadata } from 'next';
import { getAllPosts } from '@/lib/blog';
import HeroSection from '@/components/marketing/HeroSection';
import SectionWrapper from '@/components/marketing/SectionWrapper';
import BlogCard from '@/components/marketing/BlogCard';

export const metadata: Metadata = {
  title: 'Blog',
  description:
    'Insights on structured hiring, compliance, analytics, and talent acquisition best practices from the ShumelaHire team.',
};

export default function BlogPage() {
  const posts = getAllPosts();

  return (
    <>
      <div className="bg-[#F8FAFC]">
        <HeroSection
          overline="BLOG"
          headline="Insights and Analysis"
          subheadline="Practical perspectives on structured hiring, compliance, and talent acquisition for institutions."
        />
      </div>

      <SectionWrapper bg="white">
        {posts.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {posts.map((post) => (
              <BlogCard
                key={post.slug}
                title={post.title}
                description={post.description}
                date={post.date}
                category={post.category}
                readTime={post.readTime}
                slug={post.slug}
              />
            ))}
          </div>
        ) : (
          <div className="text-center py-16">
            <p className="text-[#64748B] text-lg">No posts yet. Check back soon.</p>
          </div>
        )}
      </SectionWrapper>
    </>
  );
}
