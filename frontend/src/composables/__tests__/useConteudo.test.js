import { describe, it, expect, vi, beforeEach } from 'vitest'
import { flushPromises } from '@vue/test-utils'

vi.mock('../../services/api', () => ({
  processarConteudo: vi.fn(),
  buscarConteudoPorId: vi.fn(),
}))

import { processarConteudo, buscarConteudoPorId } from '../../services/api'
import { useConteudo } from '../useConteudo'

describe('useConteudo', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('processar preenche dados e desliga carregando em caso de sucesso', async () => {
    processarConteudo.mockResolvedValueOnce({ id: 1, categoria: 'Backend' })
    const { dados, carregando, erro, processar } = useConteudo()

    const promise = processar('Titulo', 'Texto')
    expect(carregando.value).toBe(true)
    await promise

    expect(carregando.value).toBe(false)
    expect(erro.value).toBeNull()
    expect(dados.value).toEqual({ id: 1, categoria: 'Backend' })
  })

  it('processar preenche erro e limpa dados em caso de falha', async () => {
    processarConteudo.mockRejectedValueOnce({ status: 400, error: 'Validation Error', detail: 'x' })
    const { dados, erro, carregando, processar } = useConteudo()

    await processar('a', 'b')

    expect(carregando.value).toBe(false)
    expect(dados.value).toBeNull()
    expect(erro.value).toEqual({ status: 400, error: 'Validation Error', detail: 'x' })
  })

  it('buscarPorId preenche dados a partir do id', async () => {
    buscarConteudoPorId.mockResolvedValueOnce({ id: 7 })
    const { dados, buscarPorId } = useConteudo()

    await buscarPorId(7)

    expect(buscarConteudoPorId).toHaveBeenCalledWith(7)
    expect(dados.value).toEqual({ id: 7 })
  })

  it('resetar limpa dados e erro', async () => {
    processarConteudo.mockResolvedValueOnce({ id: 1 })
    const { dados, erro, processar, resetar } = useConteudo()

    await processar('a', 'b')
    expect(dados.value).not.toBeNull()

    resetar()

    expect(dados.value).toBeNull()
    expect(erro.value).toBeNull()
  })
})
