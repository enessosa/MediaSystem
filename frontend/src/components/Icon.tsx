const PATHS: Record<string, string> = {
  search: 'M11 4a7 7 0 1 0 0 14 7 7 0 0 0 0-14ZM21 21l-4.35-4.35',
  plus: 'M12 5v14M5 12h14',
  check: 'M20 6 9 17l-5-5',
  star: 'M12 2.5 15 9l7 1-5.2 5 1.2 7-6-3.3L6 22l1.2-7L2 10l7-1Z',
  trash: 'M4 7h16M9 7V4h6v3M6 7l1 13h10l1-13',
  logout: 'M9 4H5a1 1 0 0 0-1 1v14a1 1 0 0 0 1 1h4M16 17l5-5-5-5M21 12H9',
  chevronDown: 'M6 9l6 6 6-6',
  book: 'M4 4.5A2.5 2.5 0 0 1 6.5 2H20v18.5a1 1 0 0 1-1 1H6.5A2.5 2.5 0 0 1 4 19V4.5ZM4 19a2.5 2.5 0 0 1 2.5-2.5H20',
  tv: 'M3 5h18v11H3zM8 20h8M12 16v4',
  clapper: 'M3 8.5 5 5h14l2 3.5M3 8.5V19a1 1 0 0 0 1 1h16a1 1 0 0 0 1-1V8.5M3 8.5h18M7 5l1.5 3.5M12 5l1.5 3.5M17 5l1.5 3.5',
  sparkles: 'M12 3v4M12 17v4M3 12h4M17 12h4M6 6l2 2M16 16l2 2M6 18l2-2M16 8l2-2',
}

interface IconProps {
  name: keyof typeof PATHS
  size?: number
  className?: string
}

function Icon({ name, size = 18, className }: IconProps) {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth={1.8}
      strokeLinecap="round"
      strokeLinejoin="round"
      className={className}
      aria-hidden="true"
    >
      <path d={PATHS[name]} />
    </svg>
  )
}

export default Icon
