<script setup>
defineProps({
  erro: { type: Object, required: true },
})

function tituloPorStatus(status) {
  if (status === 400) return 'Dados inválidos'
  if (status === 404) return 'Não encontrado'
  if (status === 503) return 'Serviço indisponível'
  if (status === 0) return 'Sem conexão'
  return 'Erro inesperado'
}
</script>

<template>
  <div class="border-l-2 border-red-600 bg-red-50 px-4 py-3 text-sm">
    <p class="font-mono-tag font-semibold text-red-800">
      {{ tituloPorStatus(erro.status) }}
    </p>
    <p class="text-red-700 mt-1" v-if="!Array.isArray(erro.detail)">{{ erro.detail }}</p>
    <ul class="text-red-700 mt-1 list-disc list-inside" v-else>
      <li v-for="(msg, i) in erro.detail" :key="i">{{ msg }}</li>
    </ul>
  </div>
</template>