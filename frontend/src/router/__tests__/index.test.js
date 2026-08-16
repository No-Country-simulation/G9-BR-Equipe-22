import { describe, it, expect } from 'vitest'
import router from '../index'
import ProcessarView from '../../views/ProcessarView.vue'
import HistoricoView from '../../views/HistoricoView.vue'
import DetalheView from '../../views/DetalheView.vue'
import ProcessarLoteView from '../../views/ProcessarLoteView.vue'

describe('router', () => {
  it('registra as 4 rotas esperadas com os componentes certos', () => {
    const rotas = router.getRoutes()
    const porPath = Object.fromEntries(rotas.map((r) => [r.path, r]))

    expect(porPath['/'].components.default).toBe(ProcessarView)
    expect(porPath['/historico'].components.default).toBe(HistoricoView)
    expect(porPath['/conteudo/:id'].components.default).toBe(DetalheView)
    expect(porPath['/lote'].components.default).toBe(ProcessarLoteView)
  })

  it('usa nomes de rota estáveis para navegação programática', () => {
    const nomes = router.getRoutes().map((r) => r.name)
    expect(nomes).toEqual(expect.arrayContaining(['processar', 'historico', 'detalhe', 'lote']))
  })

  it('resolve /conteudo/123 para a rota "detalhe" com o param id', () => {
    const resolvido = router.resolve('/conteudo/123')
    expect(resolvido.name).toBe('detalhe')
    expect(resolvido.params.id).toBe('123')
  })
})
