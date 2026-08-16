import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// Config dedicada para os testes (não usa o plugin do Tailwind, que não é
// necessário para rodar os testes e evita acoplar a suíte a esse plugin).
export default defineConfig({
  plugins: [vue()],
  test: {
    environment: 'jsdom',
    globals: true,
    coverage: {
      provider: 'v8',
      reporter: ['text', 'html'],
      include: ['src/**/*.{js,vue}'],
      exclude: ['src/main.js', 'src/**/__tests__/**'],
    },
  },
})
