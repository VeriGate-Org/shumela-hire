/**
 * Enterprise-Grade Design System Tokens
 * Based on modern design principles and accessibility standards
 */

export interface DesignTokens {
  // Color System
  colors: {
    // Neutral palette (grayscale)
    neutral: {
      50: string;
      100: string;
      200: string;
      300: string;
      400: string;
      500: string;
      600: string;
      700: string;
      800: string;
      900: string;
      950: string;
    };
    // Semantic colors
    semantic: {
      success: {
        50: string;
        500: string;
        600: string;
        700: string;
        900: string;
      };
      warning: {
        50: string;
        500: string;
        600: string;
        700: string;
        900: string;
      };
      error: {
        50: string;
        500: string;
        600: string;
        700: string;
        900: string;
      };
      info: {
        50: string;
        500: string;
        600: string;
        700: string;
        900: string;
      };
    };
  };
  
  // Typography Scale
  typography: {
    fontFamily: {
      sans: string[];
      mono: string[];
      display: string[];
    };
    fontSize: {
      xs: [string, string];
      sm: [string, string];
      base: [string, string];
      lg: [string, string];
      xl: [string, string];
      '2xl': [string, string];
      '3xl': [string, string];
      '4xl': [string, string];
      '5xl': [string, string];
      '6xl': [string, string];
    };
    fontWeight: {
      light: number;
      normal: number;
      medium: number;
      semibold: number;
      bold: number;
      extrabold: number;
    };
    lineHeight: {
      none: number;
      tight: number;
      snug: number;
      normal: number;
      relaxed: number;
      loose: number;
    };
  };
  
  // Spacing Scale
  spacing: {
    px: string;
    0: string;
    0.5: string;
    1: string;
    1.5: string;
    2: string;
    2.5: string;
    3: string;
    3.5: string;
    4: string;
    5: string;
    6: string;
    7: string;
    8: string;
    9: string;
    10: string;
    11: string;
    12: string;
    14: string;
    16: string;
    20: string;
    24: string;
    28: string;
    32: string;
    36: string;
    40: string;
    44: string;
    48: string;
    52: string;
    56: string;
    60: string;
    64: string;
    72: string;
    80: string;
    96: string;
  };
  
  // Border Radius
  borderRadius: {
    none: string;
    sm: string;
    base: string;
    md: string;
    lg: string;
    xl: string;
    '2xl': string;
    '3xl': string;
    full: string;
  };
  
  // Shadows
  boxShadow: {
    sm: string;
    base: string;
    md: string;
    lg: string;
    xl: string;
    '2xl': string;
    inner: string;
    none: string;
  };
  
  // Motion
  animation: {
    duration: {
      75: string;
      100: string;
      150: string;
      200: string;
      300: string;
      500: string;
      700: string;
      1000: string;
    };
    easing: {
      linear: string;
      in: string;
      out: string;
      'in-out': string;
    };
  };
  
  // Z-Index Scale
  zIndex: {
    auto: string;
    0: number;
    10: number;
    20: number;
    30: number;
    40: number;
    50: number;
  };
}

export const enterpriseDesignTokens: DesignTokens = {
  colors: {
    neutral: {
      50: '#fafafa',
      100: '#f5f5f5',
      200: '#e5e5e5',
      300: '#d4d4d4',
      400: '#a3a3a3',
      500: '#737373',
      600: '#525252',
      700: '#404040',
      800: '#262626',
      900: '#171717',
      950: '#0a0a0a',
    },
    semantic: {
      success: {
        50: '#f0fdf4',
        500: '#22c55e',
        600: '#16a34a',
        700: '#15803d',
        900: '#14532d',
      },
      warning: {
        50: '#fffbeb',
        500: '#f59e0b',
        600: '#d97706',
        700: '#b45309',
        900: '#78350f',
      },
      error: {
        50: '#fef2f2',
        500: '#ef4444',
        600: '#dc2626',
        700: '#b91c1c',
        900: '#7f1d1d',
      },
      info: {
        50: '#eff6ff',
        500: '#3b82f6',
        600: '#2563eb',
        700: '#1d4ed8',
        900: '#1e3a8a',
      },
    },
  },
  
  typography: {
    fontFamily: {
      sans: ['Inter', 'system-ui', 'sans-serif'],
      mono: ['Fira Code', 'Monaco', 'Consolas', 'monospace'],
      display: ['Cal Sans', 'Inter', 'system-ui', 'sans-serif'],
    },
    fontSize: {
      xs: ['0.75rem', '1rem'],
      sm: ['0.875rem', '1.25rem'],
      base: ['1rem', '1.5rem'],
      lg: ['1.125rem', '1.75rem'],
      xl: ['1.25rem', '1.75rem'],
      '2xl': ['1.5rem', '2rem'],
      '3xl': ['1.875rem', '2.25rem'],
      '4xl': ['2.25rem', '2.5rem'],
      '5xl': ['3rem', '1'],
      '6xl': ['3.75rem', '1'],
    },
    fontWeight: {
      light: 300,
      normal: 400,
      medium: 500,
      semibold: 600,
      bold: 700,
      extrabold: 800,
    },
    lineHeight: {
      none: 1,
      tight: 1.25,
      snug: 1.375,
      normal: 1.5,
      relaxed: 1.625,
      loose: 2,
    },
  },
  
  spacing: {
    px: '1px',
    0: '0px',
    0.5: '0.125rem',
    1: '0.25rem',
    1.5: '0.375rem',
    2: '0.5rem',
    2.5: '0.625rem',
    3: '0.75rem',
    3.5: '0.875rem',
    4: '1rem',
    5: '1.25rem',
    6: '1.5rem',
    7: '1.75rem',
    8: '2rem',
    9: '2.25rem',
    10: '2.5rem',
    11: '2.75rem',
    12: '3rem',
    14: '3.5rem',
    16: '4rem',
    20: '5rem',
    24: '6rem',
    28: '7rem',
    32: '8rem',
    36: '9rem',
    40: '10rem',
    44: '11rem',
    48: '12rem',
    52: '13rem',
    56: '14rem',
    60: '15rem',
    64: '16rem',
    72: '18rem',
    80: '20rem',
    96: '24rem',
  },
  
  borderRadius: {
    none: '0px',
    sm: '0.125rem',
    base: '0.25rem',
    md: '0.375rem',
    lg: '0.5rem',
    xl: '0.75rem',
    '2xl': '1rem',
    '3xl': '1.5rem',
    full: '9999px',
  },
  
  boxShadow: {
    sm: '0 1px 2px 0 rgb(0 0 0 / 0.05)',
    base: '0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1)',
    md: '0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1)',
    lg: '0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1)',
    xl: '0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1)',
    '2xl': '0 25px 50px -12px rgb(0 0 0 / 0.25)',
    inner: 'inset 0 2px 4px 0 rgb(0 0 0 / 0.05)',
    none: '0 0 #0000',
  },
  
  animation: {
    duration: {
      75: '75ms',
      100: '100ms',
      150: '150ms',
      200: '200ms',
      300: '300ms',
      500: '500ms',
      700: '700ms',
      1000: '1000ms',
    },
    easing: {
      linear: 'linear',
      in: 'cubic-bezier(0.4, 0, 1, 1)',
      out: 'cubic-bezier(0, 0, 0.2, 1)',
      'in-out': 'cubic-bezier(0.4, 0, 0.2, 1)',
    },
  },
  
  zIndex: {
    auto: 'auto',
    0: 0,
    10: 10,
    20: 20,
    30: 30,
    40: 40,
    50: 50,
  },
};
