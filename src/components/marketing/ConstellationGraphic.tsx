interface ConstellationGraphicProps {
  className?: string;
}

export default function ConstellationGraphic({ className = '' }: ConstellationGraphicProps) {
  return (
    <svg
      viewBox="0 0 400 300"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      className={className}
      aria-hidden="true"
    >
      {/* Connecting lines */}
      <line x1="60" y1="80" x2="160" y2="50" stroke="#05527E" strokeOpacity="0.15" strokeWidth="1" />
      <line x1="160" y1="50" x2="280" y2="90" stroke="#05527E" strokeOpacity="0.15" strokeWidth="1" />
      <line x1="280" y1="90" x2="340" y2="160" stroke="#05527E" strokeOpacity="0.15" strokeWidth="1" />
      <line x1="340" y1="160" x2="280" y2="230" stroke="#05527E" strokeOpacity="0.15" strokeWidth="1" />
      <line x1="280" y1="230" x2="160" y2="250" stroke="#05527E" strokeOpacity="0.15" strokeWidth="1" />
      <line x1="160" y1="250" x2="60" y2="200" stroke="#05527E" strokeOpacity="0.15" strokeWidth="1" />
      <line x1="60" y1="200" x2="60" y2="80" stroke="#05527E" strokeOpacity="0.15" strokeWidth="1" />

      {/* Inner connecting lines */}
      <line x1="160" y1="50" x2="200" y2="150" stroke="#05527E" strokeOpacity="0.15" strokeWidth="1" />
      <line x1="280" y1="90" x2="200" y2="150" stroke="#05527E" strokeOpacity="0.15" strokeWidth="1" />
      <line x1="60" y1="200" x2="200" y2="150" stroke="#05527E" strokeOpacity="0.15" strokeWidth="1" />
      <line x1="280" y1="230" x2="200" y2="150" stroke="#05527E" strokeOpacity="0.15" strokeWidth="1" />
      <line x1="340" y1="160" x2="200" y2="150" stroke="#05527E" strokeOpacity="0.15" strokeWidth="1" />
      <line x1="60" y1="80" x2="200" y2="150" stroke="#05527E" strokeOpacity="0.15" strokeWidth="1" />

      {/* Secondary branches */}
      <line x1="60" y1="80" x2="30" y2="40" stroke="#05527E" strokeOpacity="0.08" strokeWidth="1" />
      <line x1="280" y1="90" x2="350" y2="40" stroke="#05527E" strokeOpacity="0.08" strokeWidth="1" />
      <line x1="340" y1="160" x2="380" y2="130" stroke="#05527E" strokeOpacity="0.08" strokeWidth="1" />
      <line x1="160" y1="250" x2="120" y2="280" stroke="#05527E" strokeOpacity="0.08" strokeWidth="1" />
      <line x1="60" y1="200" x2="20" y2="240" stroke="#05527E" strokeOpacity="0.08" strokeWidth="1" />

      {/* Primary nodes */}
      <circle cx="60" cy="80" r="4" fill="#05527E" />
      <circle cx="160" cy="50" r="4" fill="#05527E" />
      <circle cx="280" cy="90" r="4" fill="#05527E" />
      <circle cx="340" cy="160" r="4" fill="#05527E" />
      <circle cx="280" cy="230" r="4" fill="#05527E" />
      <circle cx="160" cy="250" r="4" fill="#05527E" />
      <circle cx="60" cy="200" r="4" fill="#05527E" />

      {/* Central accent node */}
      <circle cx="200" cy="150" r="6" fill="#F1C54B" />

      {/* Accent nodes */}
      <circle cx="350" cy="40" r="3" fill="#F1C54B" />
      <circle cx="120" cy="280" r="3" fill="#F1C54B" />

      {/* Terminal nodes (small) */}
      <circle cx="30" cy="40" r="2" fill="#05527E" opacity="0.5" />
      <circle cx="380" cy="130" r="2" fill="#05527E" opacity="0.5" />
      <circle cx="20" cy="240" r="2" fill="#05527E" opacity="0.5" />
    </svg>
  );
}
