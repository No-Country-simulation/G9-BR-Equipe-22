<script setup>
import { reactive, computed } from 'vue'
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

const form = reactive({ titulo: '', texto: '' })
const { dados: resultado, carregando, erro, processar, resetar } = useConteudo()

const erroTitulo = computed(() => {
  const len = form.titulo.trim().length
  if (len === 0) return null
  if (len < 3 || len > 500) return 'Título deve ter entre 3 e 500 caracteres'
  return null
})

const erroTexto = computed(() => {
  const len = form.texto.trim().length
  if (len === 0) return null
  if (len < 10 || len > 10000) return 'Texto deve ter entre 10 e 10000 caracteres'
  return null
})

const formValido = computed(
  () =>
    form.titulo.trim().length >= 3 &&
    form.texto.trim().length >= 10 &&
    !erroTitulo.value &&
    !erroTexto.value
)

const corCategoria = computed(() => CORES_CAT[resultado.value?.categoria] || 'var(--color-accent)')

async function enviar() {
  if (!formValido.value) return
  await processar(form.titulo.trim(), form.texto.trim())
}

function limpar() {
  form.titulo = ''
  form.texto = ''
  resetar()
}
</script>

<template>
  <!-- Hero -->
  <div class="border-b border-[var(--color-line)] bg-white bg-dot-grid">
    <div class="max-w-5xl mx-auto px-6 pt-14 pb-12">
      <p class="font-mono-tag text-xs font-semibold text-[var(--color-accent)] tracking-widest mb-4">
        TECHTAGGER · CLASSIFICADOR AUTOMÁTICO
      </p>
      <h1 class="text-4xl md:text-[2.75rem] font-semibold leading-[1.1] max-w-2xl tracking-tight">
        Transforme texto técnico em conhecimento organizado.
      </h1>
      <p class="text-[var(--color-muted)] mt-4 max-w-lg leading-relaxed">
        Cole documentação, anotações ou artigos abaixo. A IA identifica a
        categoria, extrai palavras-chave e sugere conteúdos relacionados —
        em segundos.
      </p>
    </div>
  </div>

  <div class="max-w-5xl mx-auto px-6 py-10 grid md:grid-cols-2 gap-6 items-start">
    <!-- Formulário -->
    <div class="editor-card shadow-card">
      <div class="editor-card__bar">
        <span class="editor-dot bg-red-400"></span>
        <span class="editor-dot bg-yellow-400"></span>
        <span class="editor-dot bg-green-400"></span>
        <span class="ml-2 text-xs font-mono-tag text-[var(--color-muted)]">novo-conteudo.txt</span>
      </div>

      <form @submit.prevent="enviar" class="p-6 space-y-5">
        <div>
          <label class="block text-sm font-medium mb-1.5">Título</label>
          <input
            v-model="form.titulo"
            type="text"
            placeholder="Ex: Introdução ao Spring Boot"
            class="w-full border border-[var(--color-line)] bg-white px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-[var(--color-accent)] focus:border-transparent transition-shadow"
          />
          <p v-if="erroTitulo" class="text-xs text-red-600 mt-1">{{ erroTitulo }}</p>
        </div>

        <div>
          <label class="block text-sm font-medium mb-1.5">Texto</label>
          <textarea
            v-model="form.texto"
            rows="9"
            placeholder="Cole aqui a descrição, documentação ou anotação técnica..."
            class="w-full border border-[var(--color-line)] bg-white px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-[var(--color-accent)] focus:border-transparent resize-none transition-shadow"
          />
          <div class="flex justify-between mt-1">
            <p v-if="erroTexto" class="text-xs text-red-600">{{ erroTexto }}</p>
            <p class="text-xs text-[var(--color-muted)] ml-auto font-mono-tag">
              {{ form.texto.length }}/10000
            </p>
          </div>
        </div>

        <div class="flex gap-3 pt-1">
          <button
            type="submit"
            :disabled="!formValido || carregando"
            class="px-5 py-2.5 text-sm font-medium bg-[var(--color-ink)] text-white disabled:opacity-40 disabled:cursor-not-allowed hover:bg-[var(--color-accent-dark)] transition-colors"
          >
            {{ carregando ? 'Processando...' : 'Processar conteúdo' }}
          </button>
          <button
            type="button"
            @click="limpar"
            class="px-4 py-2.5 text-sm font-medium text-[var(--color-muted)] hover:text-[var(--color-ink)]"
          >
            Limpar
          </button>
        </div>
      </form>

      <ErroAlerta v-if="erro" :erro="erro" class="mx-6 mb-6" />
    </div>

    <!-- Resultado -->
    <div class="editor-card shadow-card md:sticky md:top-20">
      <div class="editor-card__bar">
        <span class="ml-0.5 text-xs font-mono-tag text-[var(--color-muted)]">resultado.json</span>
      </div>

      <div class="p-6 min-h-[220px]">
        <Transition name="fade-slide" mode="out-in">
          <div
            v-if="carregando"
            key="loading"
            class="flex items-center gap-2.5 text-sm text-[var(--color-muted)] font-mono-tag py-8 justify-center"
          >
            <span class="w-1.5 h-1.5 bg-[var(--color-accent)] rounded-full animate-bounce [animation-delay:-0.3s]"></span>
            <span class="w-1.5 h-1.5 bg-[var(--color-accent)] rounded-full animate-bounce [animation-delay:-0.15s]"></span>
            <span class="w-1.5 h-1.5 bg-[var(--color-accent)] rounded-full animate-bounce"></span>
            <span class="ml-1">aguardando ML API</span>
          </div>

          <div v-else-if="!resultado" key="empty" class="py-10 flex flex-col items-center text-center gap-3">
            <svg width="36" height="36" viewBox="0 0 24 24" fill="none" class="text-[var(--color-line)]">
              <path d="M20.59 13.41 11 3.83A2 2 0 0 0 9.59 3.24H4a1 1 0 0 0-1 1v5.59a2 2 0 0 0 .59 1.41l9.58 9.59a2 2 0 0 0 2.83 0l4.59-4.59a2 2 0 0 0 0-2.83Z" stroke="currentColor" stroke-width="1.5"/>
              <circle cx="7.5" cy="7.5" r="1.25" fill="currentColor"/>
            </svg>
            <p class="text-sm text-[var(--color-muted)] max-w-[220px]">
              Envie um conteúdo ao lado para ver a categoria, palavras-chave e relacionados.
            </p>
          </div>

          <div v-else key="resultado" class="space-y-5 result-enter">
            <div>
              <p class="text-xs font-mono-tag text-[var(--color-muted)] mb-2">"categoria"</p>
              <div class="flex items-center gap-3 mb-2">
                <CategoriaBadge :categoria="resultado.categoria" />
                <span class="text-sm text-[var(--color-muted)] font-mono-tag ml-auto"
                  >{{ (resultado.probabilidade * 100).toFixed(1) }}%</span
                >
              </div>
              <div class="progress-track">
                <div
                  class="progress-fill"
                  :style="{
                    width: (resultado.probabilidade * 100).toFixed(1) + '%',
                    backgroundColor: corCategoria,
                  }"
                ></div>
              </div>
            </div>

            <div v-if="resultado.keywords?.length">
              <p class="text-xs font-mono-tag text-[var(--color-muted)] mb-1.5">"keywords"</p>
              <div class="flex flex-wrap gap-1.5">
                <span
                  v-for="kw in resultado.keywords"
                  :key="kw"
                  class="chip-hover text-xs font-mono-tag bg-[var(--color-paper)] border border-[var(--color-line)] px-2 py-1 cursor-default"
                >
                  {{ kw }}
                </span>
              </div>
            </div>

            <div v-if="resultado.relacionados?.length">
              <p class="text-xs font-mono-tag text-[var(--color-muted)] mb-1.5">"relacionados"</p>
              <ul class="space-y-2">
                <li
                  v-for="(rel, i) in resultado.relacionados"
                  :key="i"
                  class="rel-card text-sm border-l-2 pl-3 py-1 bg-[var(--color-paper)]"
                  :style="{ borderColor: CORES_CAT[rel.category] || 'var(--color-accent)' }"
                >
                  <p class="font-medium">{{ rel.title }}</p>
                  <div class="flex items-center gap-2 mt-1">
                    <p class="text-xs text-[var(--color-muted)] font-mono-tag">
                      {{ rel.category }} · {{ (rel.similarity * 100).toFixed(0) }}% similar
                    </p>
                    <div class="progress-track flex-1 max-w-[80px]">
                      <div
                        class="progress-fill"
                        :style="{
                          width: (rel.similarity * 100).toFixed(0) + '%',
                          backgroundColor: CORES_CAT[rel.category] || 'var(--color-accent)',
                        }"
                      ></div>
                    </div>
                  </div>
                </li>
              </ul>
            </div>
          </div>
        </Transition>
      </div>
    </div>
  </div>
</template>
