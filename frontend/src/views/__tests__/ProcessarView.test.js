import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { ref } from 'vue'

vi.mock('../../composables/useConteudo', () => ({
  useConteudo: vi.fn(),
}))

import { useConteudo } from '../../composables/useConteudo'
import ProcessarView from '../ProcessarView.vue'

function montarComposable(overrides = {}) {
  const mocks = {
    dados: ref(null),
    carregando: ref(false),
    erro: ref(null),
    processar: vi.fn().mockResolvedValue(undefined),
    buscarPorId: vi.fn(),
    resetar: vi.fn(),
    ...overrides,
  }
  useConteudo.mockReturnValue(mocks)
  return mocks
}

describe('ProcessarView', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('mostra o estado vazio quando ainda não há resultado', () => {
    montarComposable()
    const wrapper = mount(ProcessarView)

    expect(wrapper.text()).toContain('Envie um conteúdo ao lado')
  })

  it('mostra o estado de carregamento enquanto processa', () => {
    montarComposable({ carregando: ref(true) })
    const wrapper = mount(ProcessarView)

    expect(wrapper.text()).toContain('aguardando ML API')
  })

  it('mostra mensagens de validação para título e texto muito curtos', async () => {
    montarComposable()
    const wrapper = mount(ProcessarView)

    await wrapper.find('input[type="text"]').setValue('ab')
    await wrapper.find('textarea').setValue('curto')

    expect(wrapper.text()).toContain('Título deve ter entre 3 e 500 caracteres')
    expect(wrapper.text()).toContain('Texto deve ter entre 10 e 10000 caracteres')
    expect(wrapper.find('button[type="submit"]').attributes('disabled')).toBeDefined()
  })

  it('não chama processar ao submeter formulário inválido', async () => {
    const mocks = montarComposable()
    const wrapper = mount(ProcessarView)

    await wrapper.find('input[type="text"]').setValue('ab')
    await wrapper.find('form').trigger('submit.prevent')

    expect(mocks.processar).not.toHaveBeenCalled()
  })

  it('chama processar com título e texto (trimados) ao submeter formulário válido', async () => {
    const mocks = montarComposable()
    const wrapper = mount(ProcessarView)

    await wrapper.find('input[type="text"]').setValue('  Titulo Válido  ')
    await wrapper.find('textarea').setValue('  Um texto com mais de dez caracteres.  ')
    expect(wrapper.find('button[type="submit"]').attributes('disabled')).toBeUndefined()

    await wrapper.find('form').trigger('submit.prevent')

    expect(mocks.processar).toHaveBeenCalledWith('Titulo Válido', 'Um texto com mais de dez caracteres.')
  })

  it('botão Limpar reseta o formulário e chama resetar()', async () => {
    const mocks = montarComposable()
    const wrapper = mount(ProcessarView)

    await wrapper.find('input[type="text"]').setValue('Titulo')
    await wrapper.find('textarea').setValue('Texto com mais de dez caracteres.')

    await wrapper.find('button[type="button"]').trigger('click')

    expect(wrapper.find('input[type="text"]').element.value).toBe('')
    expect(wrapper.find('textarea').element.value).toBe('')
    expect(mocks.resetar).toHaveBeenCalled()
  })

  it('mostra ErroAlerta quando a composable retorna erro', () => {
    montarComposable({ erro: ref({ status: 503, detail: 'Serviço indisponível.' }) })
    const wrapper = mount(ProcessarView)

    expect(wrapper.text()).toContain('Serviço indisponível')
  })

  it('renderiza categoria, keywords e relacionados quando há resultado', async () => {
    montarComposable({
      dados: ref({
        titulo: 'Introdução ao Spring Boot',
        categoria: 'Backend',
        probabilidade: 0.93,
        keywords: ['spring', 'java'],
        relacionados: [{ title: 'Outro conteúdo', category: 'Backend', similarity: 0.8 }],
      }),
    })
    const wrapper = mount(ProcessarView)
    await flushPromises()

    expect(wrapper.text()).toContain('Introdução ao Spring Boot')
    expect(wrapper.text()).toContain('93.0%')
    expect(wrapper.text()).toContain('spring')
    expect(wrapper.text()).toContain('Outro conteúdo')
    expect(wrapper.text()).toContain('80% similar')
  })
})
