import { ref, watch } from 'vue'
import { listarConteudos } from '../services/api'

export function usePaginacao(tamanho = 10) {
  const pagina = ref(0)
  const categoria = ref('')
  const dados = ref(null)
  const carregando = ref(false)
  const erro = ref(null)

  async function carregar() {
    carregando.value = true
    erro.value = null
    try {
      dados.value = await listarConteudos({
        categoria: categoria.value,
        page: pagina.value,
        size: tamanho,
      })
    } catch (e) {
      erro.value = e
    } finally {
      carregando.value = false
    }
  }

  function irPara(p) {
    if (p < 0 || (dados.value && p >= dados.value.totalPages)) return
    pagina.value = p
  }

  watch(categoria, () => {
    pagina.value = 0
  })

  watch([pagina, categoria], carregar)

  return { pagina, categoria, dados, carregando, erro, irPara, carregar }
}