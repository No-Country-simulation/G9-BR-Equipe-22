<script setup>
import { onMounted } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import { useConteudo } from '../composables/useConteudo'
import CategoriaBadge from '../components/CategoriaBadge.vue'
import ErroAlerta from '../components/ErroAlerta.vue'

const route = useRoute()
const { dados: conteudo, carregando, erro, buscarPorId } = useConteudo()

onMounted(() => buscarPorId(route.params.id))
</script>

<template>
  <div class="max-w-2xl mx-auto px-6 py-10">
    <RouterLink
      to="/historico"
      class="text-sm text-[var(--color-muted)] hover:text-[var(--color-ink)] font-mono-tag inline-flex items-center gap-1.5"
    >
      ← voltar ao histórico
    </RouterLink>

    <div v-if="carregando" class="mt-8 text-sm text-[var(--color-muted)] font-mono-tag">carregando...</div>

    <ErroAlerta v-else-if="erro" :erro="erro" class="mt-6" />

    <div v-else-if="conteudo" class="mt-6 editor-card shadow-card">
      <div class="editor-card__bar">
        <span class="editor-dot bg-red-400"></span>
        <span class="editor-dot bg-yellow-400"></span>
        <span class="editor-dot bg-green-400"></span>
        <span class="ml-2 text-xs font-mono-tag text-[var(--color-muted)]">conteudo-{{ conteudo.id }}.json</span>
      </div>

      <div class="p-6 space-y-5">
        <p class="text-sm font-medium">{{ conteudo.titulo }}</p>

        <div>
          <p class="text-xs font-mono-tag text-[var(--color-muted)] mb-1.5">"texto"</p>
          <p class="text-sm text-[var(--color-ink)] leading-relaxed whitespace-pre-wrap">{{ conteudo.texto }}</p>
        </div>

        <div class="flex items-center gap-3">
          <CategoriaBadge :categoria="conteudo.categoria" />
          <span class="text-sm text-[var(--color-muted)] font-mono-tag"
            >{{ (conteudo.probabilidade * 100).toFixed(1) }}% de confiança</span
          >
        </div>

        <div v-if="conteudo.keywords?.length">
          <p class="text-xs font-mono-tag text-[var(--color-muted)] mb-1.5">"keywords"</p>
          <div class="flex flex-wrap gap-1.5">
            <span
              v-for="kw in conteudo.keywords"
              :key="kw"
              class="text-xs font-mono-tag bg-[var(--color-paper)] border border-[var(--color-line)] px-2 py-1"
            >
              {{ kw }}
            </span>
          </div>
        </div>

        <div v-if="conteudo.relacionados?.length">
          <p class="text-xs font-mono-tag text-[var(--color-muted)] mb-1.5">"relacionados"</p>
          <ul class="space-y-2">
            <li
              v-for="(rel, i) in conteudo.relacionados"
              :key="i"
              class="text-sm border-l-2 border-[var(--color-accent)] pl-3"
            >
              <p class="font-medium">{{ rel.title }}</p>
              <p class="text-xs text-[var(--color-muted)] font-mono-tag">
                {{ rel.category }} · {{ (rel.similarity * 100).toFixed(0) }}% similar
              </p>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>