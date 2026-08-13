<script setup>
import { RouterLink } from 'vue-router'
import { usePaginacao } from '../composables/usePaginacao'
import CategoriaBadge from '../components/CategoriaBadge.vue'
import ErroAlerta from '../components/ErroAlerta.vue'

const CATEGORIAS = [
  'Backend', 'Frontend', 'DevOps', 'Cloud',
  'Mobile', 'Databases', 'Data Science', 'Data Engineering',
]

const { pagina, categoria, dados, carregando, erro, irPara, carregar } = usePaginacao(10)

carregar()
</script>

<template>
  <div class="border-b border-[var(--color-line)] bg-white">
    <div class="max-w-5xl mx-auto px-6 pt-12 pb-8">
      <p class="font-mono-tag text-xs font-semibold text-[var(--color-accent)] tracking-widest mb-3">
        HISTÓRICO
      </p>
      <div class="flex items-end justify-between flex-wrap gap-4">
        <h1 class="text-3xl font-semibold tracking-tight">Conteúdos processados</h1>
        <select
          v-model="categoria"
          class="border border-[var(--color-line)] bg-white px-3 py-2 text-sm font-mono-tag focus:outline-none focus:ring-2 focus:ring-[var(--color-accent)] focus:border-transparent transition-shadow cursor-pointer"
        >
          <option value="">Todas as categorias</option>
          <option v-for="c in CATEGORIAS" :key="c" :value="c">{{ c }}</option>
        </select>
      </div>
    </div>
  </div>

  <div class="max-w-5xl mx-auto px-6 py-8">
    <ErroAlerta v-if="erro" :erro="erro" class="mb-4" />

    <div v-if="carregando" class="text-sm text-[var(--color-muted)] font-mono-tag py-16 text-center">
      carregando...
    </div>

    <div
      v-else-if="dados && dados.content.length === 0"
      class="border border-dashed border-[var(--color-line)] p-10 text-center text-sm text-[var(--color-muted)]"
    >
      Nenhum conteúdo processado ainda{{ categoria ? ' nessa categoria' : '' }}.
    </div>

    <template v-else-if="dados">
      <ul class="editor-card shadow-card divide-y divide-[var(--color-line)]">
        <li v-for="item in dados.content" :key="item.id">
          <RouterLink
            :to="`/conteudo/${item.id}`"
            class="flex items-center justify-between gap-4 px-5 py-4 hover:bg-[var(--color-paper)] transition-colors"
          >
            <div class="min-w-0">
              <p class="text-sm font-medium truncate">{{ item.titulo }}</p>
              <p class="text-xs text-[var(--color-muted)] font-mono-tag mt-0.5">
                #{{ item.id }} · {{ item.criadoEm?.slice(0, 10) }}
              </p>
            </div>
            <CategoriaBadge :categoria="item.categoria" />
          </RouterLink>
        </li>
      </ul>

      <div class="flex items-center justify-between mt-5 text-sm">
        <p class="text-[var(--color-muted)] font-mono-tag">
          página {{ dados.number + 1 }} de {{ dados.totalPages || 1 }} · {{ dados.totalElements }} itens
        </p>
        <div class="flex gap-2">
          <button
            @click="irPara(pagina - 1)"
            :disabled="pagina === 0"
            class="px-3.5 py-1.5 border border-[var(--color-line)] bg-white disabled:opacity-40 disabled:cursor-not-allowed hover:bg-[var(--color-paper)] transition-colors"
          >
            ← Anterior
          </button>
          <button
            @click="irPara(pagina + 1)"
            :disabled="pagina + 1 >= dados.totalPages"
            class="px-3.5 py-1.5 border border-[var(--color-line)] bg-white disabled:opacity-40 disabled:cursor-not-allowed hover:bg-[var(--color-paper)] transition-colors"
          >
            Próxima →
          </button>
        </div>
      </div>
    </template>
  </div>
</template>