import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { ref } from 'vue'
import { RouterLinkStub } from '@vue/test-utils'

vi.mock('../../composables/usePaginacao', () => ({
  usePaginacao: vi.fn(),
}))
vi.mock('../../services/api', () => ({
  listarCategorias: vi.fn(),
}))

import { usePaginacao } from '../../composables/usePaginacao'
import { listarCategorias } from '../../services/api'
import HistoricoView from '../HistoricoView.vue'

function montarComposable(overrides = {}) {
  const mocks = {
    pagina: ref(0),
    categoria: ref(''),
    dados: ref(null),
    carregando: ref(false),
    erro: ref(null),
    irPara: vi.fn(),
    carregar: vi.fn(),
    ...overrides,
  }
  usePaginacao.mockReturnValue(mocks)
  return mocks
}

function montar(overrides) {
  const mocks = montarComposable(overrides)
  const wrapper = mount(HistoricoView, {
    global: { stubs: { RouterLink: RouterLinkStub } },
  })
  return { wrapper, mocks }
}

describe('HistoricoView', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    listarCategorias.mockResolvedValue(['Backend', 'Frontend'])
  })

  it('chama carregar() ao montar', () => {
    const { mocks } = montar()
    expect(mocks.carregar).toHaveBeenCalled()
  })

  it('busca categorias disponíveis no onMounted e preenche o select', async () => {
    const { wrapper } = montar()
    await flushPromises()

    const opcoes = wrapper.find('select').findAll('option')
    expect(opcoes.map((o) => o.text())).toEqual(['Todas as categorias', 'Backend', 'Frontend'])
  })

  it('não quebra quando listarCategorias falha', async () => {
    listarCategorias.mockRejectedValueOnce(new Error('falhou'))
    const { wrapper } = montar()
    await flushPromises()

    const opcoes = wrapper.find('select').findAll('option')
    expect(opcoes).toHaveLength(1)
  })

  it('mostra o estado de carregamento', () => {
    const { wrapper } = montar({ carregando: ref(true) })
    expect(wrapper.text()).toContain('carregando...')
  })

  it('mostra mensagem de vazio quando não há conteúdos', () => {
    const { wrapper } = montar({ dados: ref({ content: [], number: 0, totalPages: 0, totalElements: 0 }) })
    expect(wrapper.text()).toContain('Nenhum conteúdo processado ainda.')
  })

  it('lista os conteúdos e monta o link para o detalhe', () => {
    const { wrapper } = montar({
      dados: ref({
        content: [{ id: 1, titulo: 'Item 1', categoria: 'Backend', criadoEm: '2026-08-15T10:00:00' }],
        number: 0,
        totalPages: 2,
        totalElements: 11,
      }),
    })

    expect(wrapper.text()).toContain('Item 1')
    expect(wrapper.text()).toContain('página 1 de 2 · 11 itens')
    const link = wrapper.findComponent(RouterLinkStub)
    expect(link.props().to).toBe('/conteudo/1')
  })

  it('desabilita "Anterior" na primeira página e "Próxima" na última', () => {
    const { wrapper } = montar({
      pagina: ref(0),
      dados: ref({ content: [{ id: 1, titulo: 'X', categoria: 'Backend' }], number: 0, totalPages: 1, totalElements: 1 }),
    })

    const botoes = wrapper.findAll('button')
    const anterior = botoes.find((b) => b.text().includes('Anterior'))
    const proxima = botoes.find((b) => b.text().includes('Próxima'))
    expect(anterior.attributes('disabled')).toBeDefined()
    expect(proxima.attributes('disabled')).toBeDefined()
  })

  it('irPara é chamado com a página seguinte ao clicar em Próxima', async () => {
    const { wrapper, mocks } = montar({
      pagina: ref(0),
      dados: ref({ content: [{ id: 1, titulo: 'X', categoria: 'Backend' }], number: 0, totalPages: 3, totalElements: 30 }),
    })

    const botoes = wrapper.findAll('button')
    const proxima = botoes.find((b) => b.text().includes('Próxima'))
    await proxima.trigger('click')

    expect(mocks.irPara).toHaveBeenCalledWith(1)
  })

  it('mostra ErroAlerta quando a composable retorna erro', () => {
    const { wrapper } = montar({ erro: ref({ status: 0, detail: 'Sem conexão.' }) })
    expect(wrapper.text()).toContain('Sem conexão')
  })
})
