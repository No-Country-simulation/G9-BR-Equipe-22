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
  <div class="max-w-3xl mx-auto px-4 py-8">
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-lg font-mono-tag font-semibold">Histórico</h1>

      <select
        v-model="categoria"
        class="border-l-2 border-gray-300 bg-white px-3 py-1.5 text-sm font-mono-tag"
      >
        <option value="">Todas as categorias</option>
        <option v-for="c in CATEGORIAS" :key="c" :value="c">{{ c }}</option>
      </select>
    </div>

    <div v-if="carregando" class="text-sm text-gray-500 py-8 text-center">
      Carregando...
    </div>

    <ErroAlerta v-else-if="erro" :erro="erro" class="mb-4" />

    <div v-else-if="dados && dados.content.length === 0" class="text-sm text-gray-500 py-8 text-center">
      Nenhum conteúdo encontrado.
    </div>

    <div v-else-if="dados" class="space-y-3">
      <RouterLink
        v-for="item in dados.content"
        :key="item.id"
        :to="`/conteudo/${item.id}`"
        class="block border-l-2 border-gray-200 bg-white px-4 py-3 hover:border-gray-400 transition-colors"
      >
        <div class="flex items-center justify-between gap-3">
          <p class="font-medium text-sm truncate">{{ item.titulo }}</p>
          <CategoriaBadge :categoria="item.categoria" />
        </div>
        <p class="text-xs text-gray-500 mt-1 truncate">{{ item.texto }}</p>
        <div class="flex items-center gap-2 mt-2">
          <span
            v-for="kw in item.keywords.slice(0, 3)"
            :key="kw"
            class="text-xs text-gray-400 font-mono-tag"
          >
            #{{ kw }}
          </span>
        </div>
      </RouterLink>

      <div class="flex items-center justify-between pt-4">
        <button
          class="text-xs font-mono-tag px-3 py-1.5 border-l-2 border-gray-300 disabled:opacity-30"
          :disabled="pagina === 0"
          @click="irPara(pagina - 1)"
        >
          ← Anterior
        </button>
        <span class="text-xs text-gray-500">
          Página {{ dados.number + 1 }} de {{ dados.totalPages }}
        </span>
        <button
          class="text-xs font-mono-tag px-3 py-1.5 border-l-2 border-gray-300 disabled:opacity-30"
          :disabled="dados.last"
          @click="irPara(pagina + 1)"
        >
          Próxima →
        </button>
      </div>
    </div>
  </div>
</template>