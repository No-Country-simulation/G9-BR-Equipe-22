import { describe, it, expect, vi, beforeEach } from 'vitest'

// Mockamos axios inteiro para não fazer nenhuma chamada de rede real.
vi.mock('axios', () => {
  const post = vi.fn()
  const get = vi.fn()
  return {
    default: {
      create: vi.fn(() => ({ post, get })),
    },
  }
})

import axios from 'axios'
import {
  processarConteudo,
  buscarConteudoPorId,
  listarConteudos,
  processarLote,
  listarCategorias,
} from '../api'

describe('services/api', () => {
  let instancia

  beforeEach(() => {
    vi.clearAllMocks()
    instancia = axios.create()
  })

  it('processarConteudo faz POST /conteudo e retorna os dados', async () => {
    instancia.post.mockResolvedValueOnce({ data: { id: 1, categoria: 'Backend' } })

    const resultado = await processarConteudo('Titulo', 'Texto')

    expect(instancia.post).toHaveBeenCalledWith('/conteudo', { titulo: 'Titulo', texto: 'Texto' })
    expect(resultado).toEqual({ id: 1, categoria: 'Backend' })
  })

  it('processarConteudo normaliza erro de resposta HTTP (com body)', async () => {
    instancia.post.mockRejectedValueOnce({
      response: { status: 400, data: { error: 'Validation Error', detail: ['titulo: vazio'] } },
    })

    await expect(processarConteudo('', '')).rejects.toEqual({
      status: 400,
      error: 'Validation Error',
      detail: ['titulo: vazio'],
    })
  })

  it('processarConteudo normaliza erro de rede (sem response) como "Sem conexão"', async () => {
    instancia.post.mockRejectedValueOnce({})

    await expect(processarConteudo('a', 'b')).rejects.toMatchObject({
      status: 0,
      error: 'Sem conexão',
    })
  })

  it('buscarConteudoPorId faz GET /conteudo/:id', async () => {
    instancia.get.mockResolvedValueOnce({ data: { id: 42 } })

    const resultado = await buscarConteudoPorId(42)

    expect(instancia.get).toHaveBeenCalledWith('/conteudo/42')
    expect(resultado).toEqual({ id: 42 })
  })

  it('listarConteudos só inclui categoria nos params quando informada', async () => {
    instancia.get.mockResolvedValueOnce({ data: { results: [] } })

    await listarConteudos({ page: 1, size: 20 })

    expect(instancia.get).toHaveBeenCalledWith('/conteudo', { params: { page: 1, size: 20 } })
  })

  it('listarConteudos inclui categoria nos params quando informada', async () => {
    instancia.get.mockResolvedValueOnce({ data: { results: [] } })

    await listarConteudos({ categoria: 'Backend', page: 0, size: 10 })

    expect(instancia.get).toHaveBeenCalledWith('/conteudo', {
      params: { page: 0, size: 10, categoria: 'Backend' },
    })
  })

  it('processarLote faz POST /conteudo/batch com os items', async () => {
    instancia.post.mockResolvedValueOnce({ data: { total: 2 } })

    const resultado = await processarLote([{ titulo: 'a', texto: 'b' }])

    expect(instancia.post).toHaveBeenCalledWith('/conteudo/batch', {
      items: [{ titulo: 'a', texto: 'b' }],
    })
    expect(resultado).toEqual({ total: 2 })
  })

  it('listarCategorias faz GET /conteudo/categorias', async () => {
    instancia.get.mockResolvedValueOnce({ data: ['Backend', 'Frontend'] })

    const resultado = await listarCategorias()

    expect(instancia.get).toHaveBeenCalledWith('/conteudo/categorias')
    expect(resultado).toEqual(['Backend', 'Frontend'])
  })
})
