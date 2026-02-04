/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      // ================================
      // 色彩系统 - 来自 DesignSpec.md
      // ================================
      colors: {
        // Primary - 蜜桃粉
        peach: {
          DEFAULT: '#FF9A8B',
          light: '#FFB4A2',
          dark: '#E88A7D',
          50: '#FFF5F3',
        },
        // Secondary
        lavender: '#E0BBE4',
        mint: '#B5EAD7',
        butter: '#FFEAA7',
        sky: '#A8D8EA',
        // Neutral
        cream: '#FDF8F5',
        // Mood Colors
        mood: {
          happy: '#FFD93D',
          calm: '#6BCB77',
          sad: '#4D96FF',
          angry: '#FF6B6B',
          anxious: '#C9B1FF',
        },
        // Weather Colors
        weather: {
          sunny: '#FFE066',
          cloudy: '#B8C5D6',
          overcast: '#8E9AAF',
          rainy: '#7EB6FF',
          snowy: '#E8F4F8',
        },
      },
      // ================================
      // 字体系统
      // ================================
      fontFamily: {
        sans: ['Inter', 'Noto Sans SC', 'PingFang SC', 'sans-serif'],
        mono: ['JetBrains Mono', 'Fira Code', 'monospace'],
      },
      fontSize: {
        'display': ['36px', { lineHeight: '1.2', fontWeight: '700' }],
        'h1': ['28px', { lineHeight: '1.3', fontWeight: '600' }],
        'h2': ['24px', { lineHeight: '1.3', fontWeight: '600' }],
        'h3': ['20px', { lineHeight: '1.4', fontWeight: '600' }],
        'body-lg': ['18px', { lineHeight: '1.6', fontWeight: '400' }],
        'body': ['16px', { lineHeight: '1.6', fontWeight: '400' }],
        'body-sm': ['14px', { lineHeight: '1.5', fontWeight: '400' }],
        'caption': ['12px', { lineHeight: '1.4', fontWeight: '400' }],
      },
      // ================================
      // 圆角系统
      // ================================
      borderRadius: {
        'sm': '6px',
        'md': '12px',
        'lg': '16px',
        'xl': '24px',
      },
      // ================================
      // 阴影系统
      // ================================
      boxShadow: {
        'sm': '0 1px 2px rgba(0,0,0,0.04)',
        'md': '0 4px 12px rgba(0,0,0,0.06)',
        'lg': '0 8px 24px rgba(0,0,0,0.08)',
        'glow': '0 0 20px rgba(255,154,139,0.3)',
      },
      // ================================
      // 动画
      // ================================
      animation: {
        'bounce-in': 'bounceIn 0.3s ease',
        'float': 'float 3s ease-in-out infinite',
        'pulse-glow': 'pulseGlow 2s ease-in-out infinite',
      },
      keyframes: {
        bounceIn: {
          '0%': { transform: 'scale(0.9)', opacity: '0' },
          '50%': { transform: 'scale(1.05)' },
          '100%': { transform: 'scale(1)', opacity: '1' },
        },
        float: {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-10px)' },
        },
        pulseGlow: {
          '0%, 100%': { boxShadow: '0 0 20px rgba(255,154,139,0.3)' },
          '50%': { boxShadow: '0 0 30px rgba(255,154,139,0.5)' },
        },
      },
    },
  },
  plugins: [],
}
