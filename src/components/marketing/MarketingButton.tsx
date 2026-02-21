import Link from 'next/link';

type Variant = 'primary' | 'outline' | 'ghost' | 'dark-bg';
type Size = 'sm' | 'md' | 'lg';

type MarketingButtonBaseProps = {
  variant?: Variant;
  size?: Size;
  className?: string;
  children: React.ReactNode;
};

type MarketingButtonAsLink = MarketingButtonBaseProps & {
  href: string;
} & Omit<React.AnchorHTMLAttributes<HTMLAnchorElement>, 'href' | 'className' | 'children'>;

type MarketingButtonAsButton = MarketingButtonBaseProps & {
  href?: undefined;
} & Omit<React.ButtonHTMLAttributes<HTMLButtonElement>, 'className' | 'children'>;

type MarketingButtonProps = MarketingButtonAsLink | MarketingButtonAsButton;

const variantStyles: Record<Variant, string> = {
  primary: 'bg-[#F1C54B] text-[#032E49] hover:bg-[#F1C54B]/90',
  outline: 'border-2 border-[#05527E] text-[#05527E] hover:bg-[#05527E]/5',
  ghost: 'text-[#05527E] hover:bg-[#05527E]/5',
  'dark-bg': 'bg-[#F1C54B] text-[#032E49] hover:bg-[#F1C54B]/90',
};

const sizeStyles: Record<Size, string> = {
  sm: 'px-4 py-2 text-xs',
  md: 'px-6 py-3 text-sm',
  lg: 'px-8 py-4 text-base',
};

const baseStyles =
  'rounded-[9999px] font-bold uppercase tracking-[0.05em] transition-colors duration-200 inline-flex items-center justify-center';

export default function MarketingButton({
  variant = 'primary',
  size = 'md',
  className = '',
  children,
  ...rest
}: MarketingButtonProps) {
  const classes = `${baseStyles} ${variantStyles[variant]} ${sizeStyles[size]} ${className}`.trim();

  if ('href' in rest && rest.href) {
    const { href, ...anchorProps } = rest as MarketingButtonAsLink;
    return (
      <Link
        href={href}
        className={classes}
        {...anchorProps}
      >
        {children}
      </Link>
    );
  }

  const buttonProps = rest as Omit<MarketingButtonAsButton, 'variant' | 'size' | 'className' | 'children' | 'href'>;
  return (
    <button className={classes} {...buttonProps}>
      {children}
    </button>
  );
}
