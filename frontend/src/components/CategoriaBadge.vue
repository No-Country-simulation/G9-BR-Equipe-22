<script setup>
const props = defineProps({
  categoria: { type: String, required: true },
})

const CORES = {
  Backend: 'var(--color-cat-backend)',
  Frontend: 'var(--color-cat-frontend)',
  DevOps: 'var(--color-cat-devops)',
  Cloud: 'var(--color-cat-cloud)',
  Mobile: 'var(--color-cat-mobile)',
  Databases: 'var(--color-cat-databases)',
  'Data Science': 'var(--color-cat-datascience)',
  'Data Engineering': 'var(--color-cat-dataengineering)',
}

const cor = CORES[props.categoria] || 'var(--color-muted)'
</script>

<template>
  <span
    class="inline-flex items-center gap-1.5 px-2 py-1 text-xs font-mono-tag font-medium border-l-2 bg-white"
    :style="{ borderColor: cor, color: cor }"
  >
    #{{ categoria.toLowerCase().replace(' ', '-') }}
  </span>
</template>