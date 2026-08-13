import { ref } from 'vue'
import { processarLote } from '../services/api'

export function useConteudoLote() {
  const resultado = ref(null)
  const carregando = ref(false)
  const erro = ref(null)

  async function processar(items) {
    carregando.value = true
    erro.value = null
    resultado.value = null
    try {
      resultado.value = await processarLote(items)
    } catch (e) {
      erro.value = e
    } finally {
      carregando.value = false
    }
  }

  return { resultado, carregando, erro, processar }
}