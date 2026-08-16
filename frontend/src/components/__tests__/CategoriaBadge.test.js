import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import CategoriaBadge from '../CategoriaBadge.vue'

describe('CategoriaBadge', () => {
  it('renderiza a categoria formatada em minúsculas com hífen', () => {
    const wrapper = mount(CategoriaBadge, { props: { categoria: 'Data Science' } })
    expect(wrapper.text()).toBe('#data-science')
  })

  it('renderiza categoria conhecida sem hífen (uma palavra só)', () => {
    const wrapper = mount(CategoriaBadge, { props: { categoria: 'Backend' } })
    expect(wrapper.text()).toBe('#backend')
  })

  it('não quebra com uma categoria desconhecida (usa cor padrão)', () => {
    const wrapper = mount(CategoriaBadge, { props: { categoria: 'Categoria Nova' } })
    expect(wrapper.text()).toBe('#categoria-nova')
  })
})
