<script setup>
import { reactive, computed, ref } from 'vue'
import { useConteudoLote } from '../composables/useConteudoLote'
import CategoriaBadge from '../components/CategoriaBadge.vue'
import ErroAlerta from '../components/ErroAlerta.vue'

const items = reactive([
  { titulo: '', texto: '' },
  { titulo: '', texto: '' },
])

const { resultado, carregando, erro, processar } = useConteudoLote()

const fileInput = ref(null)
const erroCsv = ref(null)
const nomeArquivo = ref('')

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

// Parser simples de CSV: suporta vírgula como separador e campos entre aspas
// (com vírgulas ou aspas escapadas "" dentro deles)
function parseCsvLine(line) {
  const result = []
  let current = ''
  let dentroAspas = false
  for (let i = 0; i < line.length; i++) {
    const char = line[i]
    if (char === '"') {
      if (dentroAspas && line[i + 1] === '"') {
        current += '"'
        i++
      } else {
        dentroAspas = !dentroAspas
      }
    } else if (char === ',' && !dentroAspas) {
      result.push(current)
      current = ''
    } else {
      current += char
    }
  }
  result.push(current)
  return result.map((v) => v.trim())
}

function parseCsv(texto) {
  const linhas = texto.split(/\r\n|\n|\r/).filter((l) => l.trim().length > 0)
  if (linhas.length === 0) throw new Error('Arquivo CSV vazio.')

  const cabecalho = parseCsvLine(linhas[0]).map((h) => h.toLowerCase())
  const idxTitulo = cabecalho.indexOf('titulo') !== -1 ? cabecalho.indexOf('titulo') : cabecalho.indexOf('título')
  const idxTexto = cabecalho.indexOf('texto')

  if (idxTitulo === -1 || idxTexto === -1) {
    throw new Error('O CSV precisa ter as colunas "titulo" e "texto" no cabeçalho.')
  }

  const linhasDados = linhas.slice(1)
  if (linhasDados.length === 0) throw new Error('O CSV não tem linhas de dados após o cabeçalho.')
  if (linhasDados.length > 100) throw new Error('Máximo de 100 linhas por arquivo CSV.')

  return linhasDados.map((linha) => {
    const campos = parseCsvLine(linha)
    return {
      titulo: (campos[idxTitulo] || '').trim(),
      texto: (campos[idxTexto] || '').trim(),
    }
  })
}

function abrirSeletorArquivo() {
  fileInput.value?.click()
}

function handleArquivoSelecionado(event) {
  const file = event.target.files?.[0]
  if (!file) return

  erroCsv.value = null
  nomeArquivo.value = file.name

  const reader = new FileReader()
  reader.onload = (e) => {
    try {
      const novosItens = parseCsv(e.target.result)
      items.splice(0, items.length, ...novosItens)
    } catch (err) {
      erroCsv.value = err.message || 'Erro ao processar o arquivo CSV.'
    }
  }
  reader.onerror = () => {
    erroCsv.value = 'Não foi possível ler o arquivo.'
  }
  reader.readAsText(file, 'UTF-8')

  event.target.value = ''
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
        Adicione manualmente ou envie um CSV com colunas "titulo" e "texto" — até 100 conteúdos por vez.
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

      <div class="p-6 pb-0 flex items-center gap-3 flex-wrap">
        <input
          ref="fileInput"
          type="file"
          accept=".csv,text/csv"
          class="hidden"
          @change="handleArquivoSelecionado"
        />
        <button
          type="button"
          @click="abrirSeletorArquivo"
          class="chip-hover inline-flex items-center gap-1.5 text-xs font-mono-tag px-3 py-1.5 border border-[var(--color-line)] bg-[var(--color-paper)] hover:border-[var(--color-accent)] transition-colors"
        >
          <svg width="13" height="13" viewBox="0 0 24 24" fill="none">
            <path d="M12 3v12m0 0-4-4m4 4 4-4M5 21h14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          Importar CSV
        </button>
        <span v-if="nomeArquivo" class="text-xs text-[var(--color-muted)] font-mono-tag">
          {{ nomeArquivo }} → {{ items.length }} itens carregados
        </span>
        <span class="text-xs text-[var(--color-muted)] ml-auto">
          colunas esperadas: <code class="font-mono-tag">titulo</code>, <code class="font-mono-tag">texto</code>
        </span>
      </div>

      <div v-if="erroCsv" class="mx-6 mt-4 border-l-2 border-red-600 bg-red-50 px-4 py-2 text-xs text-red-700">
        {{ erroCsv }}
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

    <div v-if="resultado" class="editor-card shadow-card result-enter">
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
              class="chip-hover text-xs font-mono-tag bg-[var(--color-paper)] border border-[var(--color-line)] px-2 py-1 cursor-default"
            >
              {{ kw }}
            </span>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>
