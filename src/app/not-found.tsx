import Link from 'next/link';

export default function NotFound() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-[#F8FAFC] px-4">
      <div className="max-w-md w-full text-center">
        <div className="text-7xl font-extrabold text-[#05527E]/20 tracking-[-0.04em] mb-4">
          404
        </div>
        <h1 className="text-2xl font-bold text-[#0F172A] tracking-[-0.03em] mb-2">
          Page not found
        </h1>
        <p className="text-[#64748B] leading-relaxed mb-8">
          The page you are looking for does not exist or has been moved.
        </p>
        <Link
          href="/"
          className="inline-flex items-center px-6 py-3 rounded-full bg-[#05527E] text-white font-medium hover:bg-[#05527E]/90 transition-colors"
        >
          Back to home
        </Link>
      </div>
    </div>
  );
}
