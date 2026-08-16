import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { nextTick } from 'vue'

vi.mock('../../services/api', () => ({
  listarConteudos: vi.fn(),
}))

import { listarConteudos } from '../../services/api'
import { usePaginacao } from '../usePaginacao'

describe('usePaginacao', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('carrega a página atual chamando listarConteudos com os parâmetros certos', async () => {
    listarConteudos.mockResolvedValue({ results: [], totalPages: 3 })
    const { carregar, pagina, categoria } = usePaginacao(10)

    await carregar()

    expect(listarConteudos).toHaveBeenCalledWith({ categoria: '', page: 0, size: 10 })
  })

  it('irPara ignora página negativa', async () => {
    listarConteudos.mockResolvedValue({ results: [], totalPages: 3 })
    const { pagina, irPara } = usePaginacao()

    irPara(-1)

    expect(pagina.value).toBe(0)
  })

  it('irPara ignora página além do total conhecido', async () => {
    listarConteudos.mockResolvedValue({ results: [], totalPages: 2 })
    const { pagina, dados, carregar, irPara } = usePaginacao()

    await carregar()
    irPara(5)

    expect(pagina.value).toBe(0)
  })

  it('mudar categoria reinicia a paginação para a página 0', async () => {
    listarConteudos.mockResolvedValue({ results: [], totalPages: 3 })
    const { pagina, categoria, irPara } = usePaginacao()

    await irPara(1)
    pagina.value = 1
    categoria.value = 'Backend'
    await nextTick()

    expect(pagina.value).toBe(0)
  })

  it('mudar página ou categoria dispara carregar automaticamente (watch)', async () => {
    listarConteudos.mockResolvedValue({ results: [{ id: 1 }], totalPages: 1 })
    const { categoria, dados } = usePaginacao()

    categoria.value = 'Frontend'
    await nextTick()
    await nextTick()

    expect(listarConteudos).toHaveBeenCalled()
  })
})
