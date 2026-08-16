import { describe, it, expect } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createRouter, createMemoryHistory } from 'vue-router'
import { defineComponent, h } from 'vue'
import NavBar from '../NavBar.vue'

const TelaFake = defineComponent({ render: () => h('div') })

function criarRouterDeTeste() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/', name: 'processar', component: TelaFake },
      { path: '/lote', name: 'lote', component: TelaFake },
      { path: '/historico', name: 'historico', component: TelaFake },
    ],
  })
}

describe('NavBar', () => {
  it('renderiza os três links de navegação', async () => {
    const router = criarRouterDeTeste()
    router.push('/')
    await router.isReady()
    const wrapper = mount(NavBar, { global: { plugins: [router] } })

    const textos = wrapper.findAll('a').map((a) => a.text())
    expect(textos).toEqual(expect.arrayContaining(['Processar', 'Lote', 'Histórico']))
  })

  it('marca como ativo o link correspondente à rota atual', async () => {
    const router = criarRouterDeTeste()
    router.push('/lote')
    await router.isReady()
    const wrapper = mount(NavBar, { global: { plugins: [router] } })
    await flushPromises()

    const linkLote = wrapper.findAll('a').find((a) => a.text() === 'Lote')
    const linkProcessar = wrapper.findAll('a').find((a) => a.text() === 'Processar')
    expect(linkLote.classes()).toContain('bg-[var(--color-ink)]')
    expect(linkProcessar.classes()).not.toContain('bg-[var(--color-ink)]')
  })
})
