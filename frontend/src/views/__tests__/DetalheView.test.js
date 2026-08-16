import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises, RouterLinkStub } from '@vue/test-utils'
import { ref } from 'vue'

vi.mock('../../composables/useConteudo', () => ({
  useConteudo: vi.fn(),
}))
vi.mock('vue-router', async (importOriginal) => {
  const actual = await importOriginal()
  return { ...actual, useRoute: vi.fn() }
})

import { useConteudo } from '../../composables/useConteudo'
import { useRoute } from 'vue-router'
import DetalheView from '../DetalheView.vue'

function montarComposable(overrides = {}) {
  const mocks = {
    dados: ref(null),
    carregando: ref(false),
    erro: ref(null),
    processar: vi.fn(),
    buscarPorId: vi.fn(),
    resetar: vi.fn(),
    ...overrides,
  }
  useConteudo.mockReturnValue(mocks)
  return mocks
}

function montar(overrides, routeParams = { id: '5' }) {
  useRoute.mockReturnValue({ params: routeParams })
  const mocks = montarComposable(overrides)
  const wrapper = mount(DetalheView, {
    global: { stubs: { RouterLink: RouterLinkStub } },
  })
  return { wrapper, mocks }
}

describe('DetalheView', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('chama buscarPorId com o id da rota ao montar', () => {
    const { mocks } = montar(undefined, { id: '42' })
    expect(mocks.buscarPorId).toHaveBeenCalledWith('42')
  })

  it('mostra o estado de carregamento', () => {
    const { wrapper } = montar({ carregando: ref(true) })
    expect(wrapper.text()).toContain('carregando...')
  })

  it('mostra ErroAlerta quando a composable retorna erro', () => {
    const { wrapper } = montar({ erro: ref({ status: 404, detail: 'Não encontrado.' }) })
    expect(wrapper.text()).toContain('Não encontrado')
  })

  it('renderiza título, texto, categoria, keywords e relacionados', async () => {
    const { wrapper } = montar({
      dados: ref({
        id: 5,
        titulo: 'Introdução ao Spring Boot',
        texto: 'Um framework Java...',
        categoria: 'Backend',
        probabilidade: 0.87,
        keywords: ['spring', 'java'],
        relacionados: [{ title: 'Outro', category: 'Backend', similarity: 0.75 }],
      }),
    })
    await flushPromises()

    expect(wrapper.text()).toContain('Introdução ao Spring Boot')
    expect(wrapper.text()).toContain('Um framework Java...')
    expect(wrapper.text()).toContain('87.0% de confiança')
    expect(wrapper.text()).toContain('spring')
    expect(wrapper.text()).toContain('Outro')
    expect(wrapper.text()).toContain('75% similar')
  })

  it('link "voltar ao histórico" aponta para /historico', () => {
    const { wrapper } = montar()
    const link = wrapper.findComponent(RouterLinkStub)
    expect(link.props().to).toBe('/historico')
  })
})
