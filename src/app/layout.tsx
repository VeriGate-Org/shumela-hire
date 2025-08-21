import type { Metadata, Viewport } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import { AuthProvider } from '@/contexts/AuthContext';
import { ThemeProvider } from '@/contexts/ThemeContext';
import { LayoutProvider } from '@/contexts/LayoutContext';

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "E-Recruitment Dashboard",
  description: "Comprehensive recruitment and hiring management platform",
  manifest: "/manifest.json",
  keywords: ["recruitment", "hiring", "HR", "job", "dashboard", "management"],
  authors: [{ name: "E-Recruitment Team" }],
  creator: "E-Recruitment Dashboard",
  publisher: "E-Recruitment Inc.",
  metadataBase: new URL('https://e-recruitment.app'),
  openGraph: {
    title: "E-Recruitment Dashboard",
    description: "Comprehensive recruitment and hiring management platform",
    url: "https://e-recruitment.app",
    siteName: "E-Recruitment",
    images: [
      {
        url: "/og-image.jpg",
        width: 1200,
        height: 630,
        alt: "E-Recruitment Dashboard"
      }
    ],
    locale: "en_US",
    type: "website"
  },
  twitter: {
    card: "summary_large_image",
    title: "E-Recruitment Dashboard",
    description: "Comprehensive recruitment and hiring management platform",
    images: ["/twitter-image.jpg"]
  },
  appleWebApp: {
    title: "E-Recruitment",
    statusBarStyle: "default",
    capable: true
  },
  formatDetection: {
    telephone: false
  }
};

export const viewport: Viewport = {
  width: 'device-width',
  initialScale: 1,
  maximumScale: 1,
  userScalable: false,
  themeColor: [
    { media: '(prefers-color-scheme: light)', color: '#3b82f6' },
    { media: '(prefers-color-scheme: dark)', color: '#1e40af' }
  ],
  colorScheme: 'light'
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <head>
        <link rel="icon" href="/favicon.ico" />
        <link rel="apple-touch-icon" href="/icons/icon-192x192.png" />
        <meta name="mobile-web-app-capable" content="yes" />
        <meta name="apple-mobile-web-app-capable" content="yes" />
        <meta name="apple-mobile-web-app-status-bar-style" content="default" />
        <meta name="msapplication-TileColor" content="#3b82f6" />
        <meta name="msapplication-config" content="/browserconfig.xml" />
      </head>
      <body
        className={`${geistSans.variable} ${geistMono.variable} antialiased`}
      >
        <ThemeProvider>
          <AuthProvider>
            <LayoutProvider>
              {children}
            </LayoutProvider>
          </AuthProvider>
        </ThemeProvider>
      </body>
    </html>
  );
}
