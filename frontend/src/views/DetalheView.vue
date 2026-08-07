<script setup>
import { onMounted } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import { useConteudo } from '../composables/useConteudo'
import CategoriaBadge from '../components/CategoriaBadge.vue'
import ErroAlerta from '../components/ErroAlerta.vue'

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

const route = useRoute()
const { dados: conteudo, carregando, erro, buscarPorId } = useConteudo()

onMounted(() => buscarPorId(route.params.id))
</script>

<template>
  <div class="max-w-2xl mx-auto px-4 py-8">
    <RouterLink
      to="/historico"
      class="inline-flex items-center gap-1.5 text-xs font-mono-tag text-[var(--color-muted)] hover:text-[var(--color-accent)] transition-colors mb-6"
    >
      ← voltar ao histórico
    </RouterLink>

    <Transition name="fade-slide" mode="out-in">
      <div v-if="carregando" key="loading" class="flex items-center gap-2.5 text-sm text-[var(--color-muted)] font-mono-tag py-16 justify-center">
        <span class="w-1.5 h-1.5 bg-[var(--color-accent)] rounded-full animate-bounce [animation-delay:-0.3s]"></span>
        <span class="w-1.5 h-1.5 bg-[var(--color-accent)] rounded-full animate-bounce [animation-delay:-0.15s]"></span>
        <span class="w-1.5 h-1.5 bg-[var(--color-accent)] rounded-full animate-bounce"></span>
        <span class="ml-1">carregando conteúdo</span>
      </div>

      <ErroAlerta v-else-if="erro" key="erro" :erro="erro" />

      <div v-else-if="conteudo" key="conteudo" class="editor-card shadow-card result-enter">
        <div
          class="editor-card__bar"
          :style="{ borderBottom: '2px solid ' + (CORES_CAT[conteudo.categoria] || 'var(--color-accent)') }"
        >
          <span class="editor-dot bg-red-400"></span>
          <span class="editor-dot bg-yellow-400"></span>
          <span class="editor-dot bg-green-400"></span>
          <span class="ml-2 text-xs font-mono-tag text-[var(--color-muted)]">conteudo-{{ conteudo.id }}.json</span>
        </div>

        <div class="p-6 space-y-5">
          <div>
            <h1 class="text-xl font-semibold mb-2">{{ conteudo.titulo }}</h1>
            <div class="flex items-center gap-3">
              <CategoriaBadge :categoria="conteudo.categoria" />
              <span class="text-sm text-[var(--color-muted)] font-mono-tag">
                {{ (conteudo.probabilidade * 100).toFixed(1) }}%
              </span>
            </div>
            <div class="progress-track mt-2">
              <div
                class="progress-fill"
                :style="{
                  width: (conteudo.probabilidade * 100).toFixed(1) + '%',
                  backgroundColor: CORES_CAT[conteudo.categoria] || 'var(--color-accent)',
                }"
              ></div>
            </div>
          </div>

          <div>
            <p class="text-xs font-mono-tag text-[var(--color-muted)] mb-1.5">"texto"</p>
            <p class="text-sm leading-relaxed">{{ conteudo.texto }}</p>
          </div>

          <div v-if="conteudo.keywords?.length">
            <p class="text-xs font-mono-tag text-[var(--color-muted)] mb-1.5">"keywords"</p>
            <div class="flex flex-wrap gap-1.5">
              <span
                v-for="kw in conteudo.keywords"
                :key="kw"
                class="chip-hover text-xs font-mono-tag bg-[var(--color-paper)] border border-[var(--color-line)] px-2 py-1 cursor-default"
              >
                {{ kw }}
              </span>
            </div>
          </div>

          <div v-if="conteudo.criadoEm">
            <p class="text-xs font-mono-tag text-[var(--color-muted)]">
              processado em {{ new Date(conteudo.criadoEm).toLocaleString('pt-BR') }}
            </p>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>
