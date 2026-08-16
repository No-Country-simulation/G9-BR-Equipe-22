import { describe, it, expect } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createRouter, createMemoryHistory } from 'vue-router'
import { defineComponent, h } from 'vue'
import App from '../App.vue'

const TelaFake = defineComponent({
  name: 'TelaFake',
  render: () => h('p', 'conteudo-da-rota'),
})

function criarRouterDeTeste() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [{ path: '/', name: 'inicio', component: TelaFake }],
  })
}

describe('App', () => {
  it('renderiza a NavBar e a rota ativa via RouterView', async () => {
    const router = criarRouterDeTeste()
    router.push('/')
    await router.isReady()

    // NavBar tem sua própria suíte de testes (NavBar.test.js); aqui só
    // importa confirmar que App.vue a inclui e delega o restante ao
    // RouterView, então a stub evita links para rotas que este router
    // de teste (propositalmente mínimo) não registra.
    const wrapper = mount(App, {
      global: { plugins: [router], stubs: { NavBar: { template: '<nav>navbar-stub</nav>' } } },
    })
    await flushPromises()

    expect(wrapper.text()).toContain('navbar-stub')
    expect(wrapper.text()).toContain('conteudo-da-rota')
  })
})
