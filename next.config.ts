import type { NextConfig } from "next";
const withBundleAnalyzer = require('@next/bundle-analyzer')({
  enabled: process.env.ANALYZE === 'true',
});

const nextConfig: NextConfig = {
  // Build output for Docker/standalone deployment
  output: 'standalone',

  // Strict mode for catching issues early
  reactStrictMode: true,

  // Performance optimizations
  experimental: {
    optimizePackageImports: ['lucide-react', '@heroicons/react', 'recharts', 'date-fns'],
  },

  // Compression
  compress: true,

  // Image optimization
  images: {
    remotePatterns: [
      {
        protocol: 'https',
        hostname: 'api.shumelahire.co.za',
      },
    ],
    formats: ['image/webp', 'image/avif'],
    deviceSizes: [640, 750, 828, 1080, 1200, 1920, 2048, 3840],
    imageSizes: [16, 32, 48, 64, 96, 128, 256, 384],
  },

  // Security + caching headers
  async headers() {
    return [
      {
        source: '/(.*)',
        headers: [
          {
            key: 'X-Frame-Options',
            value: 'DENY',
          },
          {
            key: 'X-Content-Type-Options',
            value: 'nosniff',
          },
          {
            key: 'Referrer-Policy',
            value: 'origin-when-cross-origin',
          },
          {
            key: 'X-DNS-Prefetch-Control',
            value: 'on',
          },
          {
            key: 'Strict-Transport-Security',
            value: 'max-age=63072000; includeSubDomains; preload',
          },
          {
            key: 'Permissions-Policy',
            value: 'camera=(), microphone=(), geolocation=()',
          },
          {
            key: 'Content-Security-Policy',
            value: [
              "default-src 'self'",
              "script-src 'self' 'unsafe-inline'",
              "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com",
              "font-src 'self' https://fonts.gstatic.com",
              "img-src 'self' data: blob: https://api.shumelahire.co.za",
              "connect-src 'self' https://api.shumelahire.co.za https://cognito-idp.af-south-1.amazonaws.com https://*.amazoncognito.com",
              "frame-ancestors 'none'",
              "base-uri 'self'",
              "form-action 'self'",
            ].join('; '),
          },
        ],
      },
      {
        source: '/api/:path*',
        headers: [
          {
            key: 'Cache-Control',
            value: 'public, max-age=300, s-maxage=300',
          },
        ],
      },
      {
        source: '/_next/static/:path*',
        headers: [
          {
            key: 'Cache-Control',
            value: 'public, max-age=31536000, immutable',
          },
        ],
      },
    ];
  },

  // Webpack optimizations
  webpack: (config, { isServer }) => {
    config.watchOptions = {
      ...config.watchOptions,
      ignored: ['**/backend/**', '**/node_modules/**'],
    };
    if (!isServer) {
      config.resolve.fallback = {
        ...config.resolve.fallback,
        fs: false,
      };

      // Enhanced per-module code splitting (spec F-5.4.1)
      // Each top-level HR module gets its own async chunk so users on
      // low-bandwidth links only download what they navigate to.
      config.optimization = {
        ...config.optimization,
        splitChunks: {
          chunks: 'all',
          maxInitialRequests: 30,
          maxAsyncRequests: 30,
          cacheGroups: {
            // Vendor: third-party libs rarely change → long-lived cache
            vendor: {
              test: /[\\/]node_modules[\\/]/,
              name: 'vendor',
              chunks: 'all',
              priority: 10,
              reuseExistingChunk: true,
            },
            // Recharts charting lib (large, only needed on analytics pages)
            charts: {
              test: /[\\/]node_modules[\\/](recharts|d3-.*)[\\/]/,
              name: 'chunk-charts',
              chunks: 'async',
              priority: 20,
            },
            // lucide-react icons
            icons: {
              test: /[\\/]node_modules[\\/](lucide-react|@heroicons)[\\/]/,
              name: 'chunk-icons',
              chunks: 'async',
              priority: 20,
            },
            // HR module groupings (pages + components per domain)
            employees: {
              test: /[\\/]src[\\/](app[\\/]\(app\)[\\/]employees|components[\\/]employees)[\\/]/,
              name: 'chunk-employees',
              chunks: 'async',
              priority: 30,
            },
            performance: {
              test: /[\\/]src[\\/](app[\\/]\(app\)[\\/]performance|components[\\/]performance)[\\/]/,
              name: 'chunk-performance',
              chunks: 'async',
              priority: 30,
            },
            analytics: {
              test: /[\\/]src[\\/](app[\\/]\(app\)[\\/]analytics|components[\\/](Analytics|charts))[\\/]/,
              name: 'chunk-analytics',
              chunks: 'async',
              priority: 30,
            },
            reports: {
              test: /[\\/]src[\\/](app[\\/]\(app\)[\\/]reports|components[\\/](Report|reports))[\\/]/,
              name: 'chunk-reports',
              chunks: 'async',
              priority: 30,
            },
          },
        },
      };
    }
    return config;
  },
};

export default withBundleAnalyzer(nextConfig);
