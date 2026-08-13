<script setup>
import { RouterLink, useRoute } from 'vue-router'

const route = useRoute()
const links = [
  { to: '/', label: 'Processar' },
  { to: '/lote', label: 'Lote' },
  { to: '/historico', label: 'Histórico' },
]
</script>

<template>
  <header class="sticky top-0 z-10 border-b border-[var(--color-line)] bg-[var(--color-surface)]/90 backdrop-blur">
    <div class="max-w-5xl mx-auto px-6 py-3.5 flex items-center justify-between">
      <RouterLink to="/" class="flex items-center gap-2.5 group">
        <span
          class="flex items-center justify-center w-7 h-7 bg-[var(--color-ink)] text-white font-mono-tag font-bold text-sm group-hover:bg-[var(--color-accent)] transition-colors"
        >
          #
        </span>
        <span class="font-mono-tag font-bold text-base tracking-tight">TechTagger</span>
      </RouterLink>
      <nav class="flex gap-1">
        <RouterLink
          v-for="link in links"
          :key="link.to"
          :to="link.to"
          class="px-3.5 py-1.5 text-sm font-medium transition-colors"
          :class="
            route.path === link.to
              ? 'bg-[var(--color-ink)] text-white'
              : 'text-[var(--color-muted)] hover:text-[var(--color-ink)] hover:bg-[var(--color-paper)]'
          "
        >
          {{ link.label }}
        </RouterLink>
      </nav>
    </div>
  </header>
</template>