import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { ref } from 'vue'

vi.mock('../../composables/useConteudoLote', () => ({
  useConteudoLote: vi.fn(),
}))

import { useConteudoLote } from '../../composables/useConteudoLote'
import ProcessarLoteView from '../ProcessarLoteView.vue'

function montarComposable(overrides = {}) {
  const mocks = {
    resultado: ref(null),
    carregando: ref(false),
    erro: ref(null),
    processar: vi.fn().mockResolvedValue(undefined),
    ...overrides,
  }
  useConteudoLote.mockReturnValue(mocks)
  return mocks
}

async function dispararUploadCsv(wrapper, conteudo, nomeArquivo = 'itens.csv') {
  const file = new File([conteudo], nomeArquivo, { type: 'text/csv' })
  const input = wrapper.find('input[type="file"]')
  Object.defineProperty(input.element, 'files', { value: [file], configurable: true })
  await input.trigger('change')
}

// FileReader.onload roda de forma assíncrona (via um timer real, mesmo no
// jsdom), então esperar um número fixo de ticks é frágil -- sob o
// instrumentador de cobertura (v8), por exemplo, esse timing muda o
// suficiente para o teste falhar de forma intermitente. Em vez disso,
// fazemos polling da condição esperada até ela aparecer no DOM.
async function aguardarCondicao(condicao, { timeout = 1000, intervalo = 5 } = {}) {
  const inicio = Date.now()
  while (Date.now() - inicio < timeout) {
    await flushPromises()
    if (condicao()) return
    await new Promise((resolve) => setTimeout(resolve, intervalo))
  }
  throw new Error('Timeout esperando a condição do FileReader.')
}

describe('ProcessarLoteView', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('começa com 2 itens vazios e o botão desabilitado', () => {
    montarComposable()
    const wrapper = mount(ProcessarLoteView)

    expect(wrapper.findAll('textarea')).toHaveLength(2)
    expect(wrapper.find('button[type="submit"]').attributes('disabled')).toBeDefined()
  })

  it('adicionarItem acrescenta um novo item ao formulário', async () => {
    montarComposable()
    const wrapper = mount(ProcessarLoteView)

    await wrapper.find('button.w-full').trigger('click')

    expect(wrapper.findAll('textarea')).toHaveLength(3)
  })

  it('não adiciona item além do limite de 100', async () => {
    montarComposable()
    const wrapper = mount(ProcessarLoteView)
    const botaoAdicionar = wrapper.find('button.w-full')

    for (let i = 0; i < 100; i++) {
      await botaoAdicionar.trigger('click')
    }

    expect(wrapper.findAll('textarea')).toHaveLength(100)
  })

  it('removerItem remove um item, mas nunca deixa a lista vazia', async () => {
    montarComposable()
    const wrapper = mount(ProcessarLoteView)

    await wrapper.find('button.w-full').trigger('click')
    expect(wrapper.findAll('textarea')).toHaveLength(3)

    const botoesRemover = wrapper.findAll('button').filter((b) => b.text() === 'remover')
    await botoesRemover[0].trigger('click')
    expect(wrapper.findAll('textarea')).toHaveLength(2)

    // com 1 item restante, o botão "remover" nem aparece mais (v-if items.length > 1)
    await wrapper.findAll('button').filter((b) => b.text() === 'remover')[0].trigger('click')
    expect(wrapper.findAll('textarea')).toHaveLength(1)
    expect(wrapper.findAll('button').filter((b) => b.text() === 'remover')).toHaveLength(0)
  })

  it('habilita o botão de envio só quando todos os itens são válidos, e envia trimado', async () => {
    const mocks = montarComposable()
    const wrapper = mount(ProcessarLoteView)

    const inputs = wrapper.findAll('input[type="text"]')
    const textareas = wrapper.findAll('textarea')
    await inputs[0].setValue('  Titulo 1  ')
    await textareas[0].setValue('  Texto com mais de dez caracteres.  ')
    await inputs[1].setValue('Titulo 2')
    await textareas[1].setValue('Outro texto valido aqui.')

    expect(wrapper.find('button[type="submit"]').attributes('disabled')).toBeUndefined()

    await wrapper.find('form').trigger('submit.prevent')

    expect(mocks.processar).toHaveBeenCalledWith([
      { titulo: 'Titulo 1', texto: 'Texto com mais de dez caracteres.' },
      { titulo: 'Titulo 2', texto: 'Outro texto valido aqui.' },
    ])
  })

  it('importa itens válidos de um CSV bem formado', async () => {
    montarComposable()
    const wrapper = mount(ProcessarLoteView)

    const csv = 'titulo,texto\nIntrodução ao Spring Boot,Framework Java para aplicações web.\nVue 3,Framework JS reativo para interfaces.'
    await dispararUploadCsv(wrapper, csv)
    await aguardarCondicao(() => wrapper.findAll('input[type="text"]')[0]?.element.value === 'Introdução ao Spring Boot')

    const inputs = wrapper.findAll('input[type="text"]')
    expect(inputs).toHaveLength(2)
    expect(inputs[0].element.value).toBe('Introdução ao Spring Boot')
    expect(wrapper.text()).toContain('itens.csv')
    expect(wrapper.text()).toContain('2 itens carregados')
  })

  it('mostra erro quando o CSV não tem as colunas titulo/texto', async () => {
    montarComposable()
    const wrapper = mount(ProcessarLoteView)

    const csv = 'nome,descricao\nA,B'
    await dispararUploadCsv(wrapper, csv)
    await aguardarCondicao(() => wrapper.text().includes('O CSV precisa ter as colunas'))

    expect(wrapper.text()).toContain('O CSV precisa ter as colunas')
  })

  it('mostra erro quando o CSV está vazio', async () => {
    montarComposable()
    const wrapper = mount(ProcessarLoteView)

    await dispararUploadCsv(wrapper, '')
    await aguardarCondicao(() => wrapper.text().includes('Arquivo CSV vazio'))

    expect(wrapper.text()).toContain('Arquivo CSV vazio')
  })

  it('renderiza a lista de resultados quando o lote retorna', () => {
    montarComposable({
      resultado: ref({
        total: 1,
        results: [{ titulo: 'Titulo', categoria: 'Frontend', probabilidade: 0.8, keywords: ['vue'] }],
      }),
    })
    const wrapper = mount(ProcessarLoteView)

    expect(wrapper.text()).toContain('1 processados')
    expect(wrapper.text()).toContain('80.0%')
    expect(wrapper.text()).toContain('vue')
  })

  it('mostra ErroAlerta quando a composable retorna erro', () => {
    montarComposable({ erro: ref({ status: 503, detail: 'Serviço indisponível.' }) })
    const wrapper = mount(ProcessarLoteView)

    expect(wrapper.text()).toContain('Serviço indisponível')
  })
})
