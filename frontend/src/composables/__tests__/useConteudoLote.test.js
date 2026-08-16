import { describe, it, expect, vi, beforeEach } from 'vitest'

vi.mock('../../services/api', () => ({
  processarLote: vi.fn(),
}))

import { processarLote } from '../../services/api'
import { useConteudoLote } from '../useConteudoLote'

describe('useConteudoLote', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('processar envia os items e preenche resultado', async () => {
    processarLote.mockResolvedValueOnce({ total: 2, results: [] })
    const { resultado, carregando, erro, processar } = useConteudoLote()

    const items = [{ titulo: 'a', texto: 'b' }]
    const promise = processar(items)
    expect(carregando.value).toBe(true)
    await promise

    expect(processarLote).toHaveBeenCalledWith(items)
    expect(carregando.value).toBe(false)
    expect(erro.value).toBeNull()
    expect(resultado.value).toEqual({ total: 2, results: [] })
  })

  it('processar preenche erro e mantém resultado nulo em caso de falha', async () => {
    processarLote.mockRejectedValueOnce({ status: 503, error: 'Service Unavailable' })
    const { resultado, erro, processar } = useConteudoLote()

    await processar([])

    expect(resultado.value).toBeNull()
    expect(erro.value).toEqual({ status: 503, error: 'Service Unavailable' })
  })
})
