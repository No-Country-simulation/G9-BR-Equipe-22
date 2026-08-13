<script setup>
import { reactive, computed } from 'vue'
import { useConteudoLote } from '../composables/useConteudoLote'
import CategoriaBadge from '../components/CategoriaBadge.vue'
import ErroAlerta from '../components/ErroAlerta.vue'

const items = reactive([
  { titulo: '', texto: '' },
  { titulo: '', texto: '' },
])

const { resultado, carregando, erro, processar } = useConteudoLote()

const itensValidos = computed(() =>
  items.filter((i) => i.titulo.trim().length >= 3 && i.texto.trim().length >= 10)
)

const formValido = computed(() => itensValidos.value.length === items.length && items.length > 0)

function adicionarItem() {
  if (items.length >= 100) return
  items.push({ titulo: '', texto: '' })
}

function removerItem(index) {
  if (items.length <= 1) return
  items.splice(index, 1)
}

async function enviar() {
  if (!formValido.value) return
  await processar(items.map((i) => ({ titulo: i.titulo.trim(), texto: i.texto.trim() })))
}
</script>

<template>
  <div class="border-b border-[var(--color-line)] bg-white bg-dot-grid">
    <div class="max-w-5xl mx-auto px-6 pt-14 pb-12">
      <p class="font-mono-tag text-xs font-semibold text-[var(--color-accent)] tracking-widest mb-4">
        PROCESSAMENTO EM LOTE
      </p>
      <h1 class="text-4xl md:text-[2.75rem] font-semibold leading-[1.1] max-w-2xl tracking-tight">
        Processe vários conteúdos de uma vez.
      </h1>
      <p class="text-[var(--color-muted)] mt-4 max-w-lg leading-relaxed">
        Adicione até 100 conteúdos e classifique todos numa única chamada.
      </p>
    </div>
  </div>

  <div class="max-w-5xl mx-auto px-6 py-10 space-y-6">
    <div class="editor-card shadow-card">
      <div class="editor-card__bar">
        <span class="editor-dot bg-red-400"></span>
        <span class="editor-dot bg-yellow-400"></span>
        <span class="editor-dot bg-green-400"></span>
        <span class="ml-2 text-xs font-mono-tag text-[var(--color-muted)]">lote.json</span>
      </div>

      <form @submit.prevent="enviar" class="p-6 space-y-5">
        <div
          v-for="(item, index) in items"
          :key="index"
          class="border border-[var(--color-line)] p-4 space-y-3 relative"
        >
          <div class="flex items-center justify-between">
            <p class="text-xs font-mono-tag text-[var(--color-muted)]">item {{ index + 1 }}</p>
            <button
              v-if="items.length > 1"
              type="button"
              @click="removerItem(index)"
              class="text-xs text-red-600 hover:text-red-800"
            >
              remover
            </button>
          </div>
          <input
            v-model="item.titulo"
            type="text"
            placeholder="Título"
            class="w-full border border-[var(--color-line)] bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-[var(--color-accent)]"
          />
          <textarea
            v-model="item.texto"
            rows="3"
            placeholder="Texto"
            class="w-full border border-[var(--color-line)] bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-[var(--color-accent)] resize-none"
          />
        </div>

        <button
          type="button"
          @click="adicionarItem"
          class="w-full border border-dashed border-[var(--color-line)] py-2.5 text-sm text-[var(--color-muted)] hover:text-[var(--color-ink)] hover:border-[var(--color-ink)] transition-colors"
        >
          + adicionar item
        </button>

        <div class="flex gap-3 pt-1">
          <button
            type="submit"
            :disabled="!formValido || carregando"
            class="px-5 py-2.5 text-sm font-medium bg-[var(--color-ink)] text-white disabled:opacity-40 disabled:cursor-not-allowed hover:bg-[var(--color-accent-dark)] transition-colors"
          >
            {{ carregando ? 'Processando...' : `Processar ${items.length} itens` }}
          </button>
        </div>
      </form>

      <ErroAlerta v-if="erro" :erro="erro" class="mx-6 mb-6" />
    </div>

    <div v-if="resultado" class="editor-card shadow-card">
      <div class="editor-card__bar">
        <span class="ml-0.5 text-xs font-mono-tag text-[var(--color-muted)]"
          >resultados.json · {{ resultado.total }} processados</span
        >
      </div>
      <ul class="divide-y divide-[var(--color-line)]">
        <li v-for="(item, i) in resultado.results" :key="i" class="p-5 space-y-2">
          <p class="text-sm font-medium">{{ item.titulo }}</p>
          <div class="flex items-center gap-3">
            <CategoriaBadge :categoria="item.categoria" />
            <span class="text-sm text-[var(--color-muted)] font-mono-tag"
              >{{ (item.probabilidade * 100).toFixed(1) }}%</span
            >
          </div>
          <div class="flex flex-wrap gap-1.5">
            <span
              v-for="kw in item.keywords"
              :key="kw"
              class="text-xs font-mono-tag bg-[var(--color-paper)] border border-[var(--color-line)] px-2 py-1"
            >
              {{ kw }}
            </span>
          </div>
        </li>
      </ul>
       
    </div>
  </div>
</template>