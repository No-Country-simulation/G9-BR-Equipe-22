
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite' // 👈 1. IMPORTAR O TAILWIND V4

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    tailwindcss() // 👈 2. REGISTRAR O PLUGIN DO TAILWIND V4
  ],
  base: './', // 👈 Mantém o caminho relativo para o deploy
  server: {
    proxy: {
      '/api': {
        target: 'https://techtagger.duckdns.org',
        changeOrigin: true,
        secure: true,
      },
    },
  },
})