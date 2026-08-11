<script setup>
import { RouterLink } from 'vue-router'
import { usePaginacao } from '../composables/usePaginacao'
import CategoriaBadge from '../components/CategoriaBadge.vue'
import ErroAlerta from '../components/ErroAlerta.vue'

const CATEGORIAS = [
  'Backend', 'Frontend', 'DevOps', 'Cloud',
  'Mobile', 'Databases', 'Data Science', 'Data Engineering',
]

const CORES_CAT = {
  Backend: 'var(--color-cat-backend)',
  Frontend: 'var(--color-cat-frontend)',
  DevOps: 'var(--color-cat-devops)',
  Cloud: 'var(--color-cat-cloud)',
  Mobile: 'var(--color-cat-mobile)',
  Databases: 'var(--color-cat-databases)',
  'Data Science': 'var(--color-cat-datascience)',
  'Data Engineering': 'var(--color-cat-dataengineering)',
}

const { pagina, categoria, dados, carregando, erro, irPara, carregar } = usePaginacao(10)

carregar()
</script>

<template>
  <div class="max-w-3xl mx-auto px-4 py-8">
    <div class="flex items-center justify-between mb-6">
      <div>
        <p class="font-mono-tag text-xs font-semibold text-[var(--color-accent)] tracking-widest mb-1">
          TECHTAGGER
        </p>
        <h1 class="text-lg font-semibold">Histórico</h1>
      </div>

      <select
        v-model="categoria"
        class="border border-[var(--color-line)] bg-white px-3 py-1.5 text-sm font-mono-tag focus:outline-none focus:ring-2 focus:ring-[var(--color-accent)] focus:border-transparent transition-shadow"
      >
        <option value="">Todas as categorias</option>
        <option v-for="c in CATEGORIAS" :key="c" :value="c">{{ c }}</option>
      </select>
    </div>

    <Transition name="fade-slide" mode="out-in">
      <div v-if="carregando" key="loading" class="flex items-center gap-2.5 text-sm text-[var(--color-muted)] font-mono-tag py-12 justify-center">
        <span class="w-1.5 h-1.5 bg-[var(--color-accent)] rounded-full animate-bounce [animation-delay:-0.3s]"></span>
        <span class="w-1.5 h-1.5 bg-[var(--color-accent)] rounded-full animate-bounce [animation-delay:-0.15s]"></span>
        <span class="w-1.5 h-1.5 bg-[var(--color-accent)] rounded-full animate-bounce"></span>
        <span class="ml-1">carregando histórico</span>
      </div>

      <ErroAlerta v-else-if="erro" key="erro" :erro="erro" class="mb-4" />

      <div
        v-else-if="dados && dados.content.length === 0"
        key="vazio"
        class="py-14 flex flex-col items-center text-center gap-3"
      >
        <svg width="36" height="36" viewBox="0 0 24 24" fill="none" class="text-[var(--color-line)]">
          <path d="M20.59 13.41 11 3.83A2 2 0 0 0 9.59 3.24H4a1 1 0 0 0-1 1v5.59a2 2 0 0 0 .59 1.41l9.58 9.59a2 2 0 0 0 2.83 0l4.59-4.59a2 2 0 0 0 0-2.83Z" stroke="currentColor" stroke-width="1.5"/>
          <circle cx="7.5" cy="7.5" r="1.25" fill="currentColor"/>
        </svg>
        <p class="text-sm text-[var(--color-muted)] max-w-[220px]">
          Nenhum conteúdo encontrado. Que tal processar o primeiro?
        </p>
        <RouterLink
          to="/"
          class="mt-1 text-xs font-mono-tag px-3 py-1.5 bg-[var(--color-ink)] text-white hover:bg-[var(--color-accent-dark)] transition-colors"
        >
          Processar conteúdo
        </RouterLink>
      </div>

      <div v-else-if="dados" key="lista" class="space-y-3">
        <RouterLink
          v-for="(item, idx) in dados.content"
          :key="item.id"
          :to="`/conteudo/${item.id}`"
          class="rel-card block border-l-2 bg-white px-4 py-3 result-enter"
          :style="{
            borderColor: CORES_CAT[item.categoria] || 'var(--color-line)',
            animationDelay: (idx * 0.05) + 's',
          }"
        >
          <div class="flex items-center justify-between gap-3">
            <p class="font-medium text-sm truncate">{{ item.titulo }}</p>
            <CategoriaBadge :categoria="item.categoria" />
          </div>
          <p class="text-xs text-[var(--color-muted)] mt-1 truncate">{{ item.texto }}</p>
          <div class="flex items-center gap-2 mt-2">
            <span
              v-for="kw in item.keywords.slice(0, 3)"
              :key="kw"
              class="chip-hover text-xs text-[var(--color-muted)] font-mono-tag border border-transparent px-1"
            >
              #{{ kw }}
            </span>
          </div>
        </RouterLink>

        <div class="flex items-center justify-between pt-4">
          <button
            class="text-xs font-mono-tag px-3 py-1.5 border-l-2 border-[var(--color-line)] disabled:opacity-30 hover:border-[var(--color-accent)] transition-colors"
            :disabled="pagina === 0"
            @click="irPara(pagina - 1)"
          >
            ← Anterior
          </button>
          <span class="text-xs text-[var(--color-muted)] font-mono-tag">
            Página {{ dados.number + 1 }} de {{ dados.totalPages }}
          </span>
          <button
            class="text-xs font-mono-tag px-3 py-1.5 border-l-2 border-[var(--color-line)] disabled:opacity-30 hover:border-[var(--color-accent)] transition-colors"
            :disabled="dados.last"
            @click="irPara(pagina + 1)"
          >
            Próxima →
          </button>
        </div>
      </div>
    </Transition>
  </div>
</template>
