export default function Loading() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-[#F8FAFC]">
      <div className="flex flex-col items-center gap-4">
        <div className="w-8 h-8 border-2 border-[#05527E]/20 border-t-[#05527E] rounded-full animate-spin" />
        <p className="text-sm text-[#64748B] uppercase tracking-[0.05em] font-medium">
          Loading
        </p>
      </div>
    </div>
  );
}
