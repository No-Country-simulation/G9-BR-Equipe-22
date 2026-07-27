import { ref } from 'vue'
import { processarConteudo, buscarConteudoPorId } from '../services/api'

export function useConteudo() {
  const dados = ref(null)
  const carregando = ref(false)
  const erro = ref(null)

  function resetar() {
    dados.value = null
    erro.value = null
  }

  async function processar(titulo, texto) {
    carregando.value = true
    erro.value = null
    dados.value = null
    try {
      dados.value = await processarConteudo(titulo, texto)
    } catch (e) {
      erro.value = e
    } finally {
      carregando.value = false
    }
  }

  async function buscarPorId(id) {
    carregando.value = true
    erro.value = null
    try {
      dados.value = await buscarConteudoPorId(id)
    } catch (e) {
      erro.value = e
    } finally {
      carregando.value = false
    }
  }

  return { dados, carregando, erro, processar, buscarPorId, resetar }
}