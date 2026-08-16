import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import ErroAlerta from '../ErroAlerta.vue'

describe('ErroAlerta', () => {
  it.each([
    [400, 'Dados inválidos'],
    [404, 'Não encontrado'],
    [503, 'Serviço indisponível'],
    [0, 'Sem conexão'],
    [500, 'Erro inesperado'],
    [999, 'Erro inesperado'],
  ])('status %i mostra o título "%s"', (status, tituloEsperado) => {
    const wrapper = mount(ErroAlerta, { props: { erro: { status, detail: 'algo deu errado' } } })
    expect(wrapper.text()).toContain(tituloEsperado)
  })

  it('renderiza detail como texto simples quando não é uma lista', () => {
    const wrapper = mount(ErroAlerta, { props: { erro: { status: 500, detail: 'mensagem única' } } })
    expect(wrapper.text()).toContain('mensagem única')
    expect(wrapper.findAll('li')).toHaveLength(0)
  })

  it('renderiza detail como lista quando é um array (erros de validação)', () => {
    const wrapper = mount(ErroAlerta, {
      props: { erro: { status: 400, detail: ['titulo: vazio', 'texto: muito curto'] } },
    })
    const itens = wrapper.findAll('li')
    expect(itens).toHaveLength(2)
    expect(itens[0].text()).toBe('titulo: vazio')
    expect(itens[1].text()).toBe('texto: muito curto')
  })
})
