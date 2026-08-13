import { createRouter, createWebHashHistory } from 'vue-router'
import ProcessarView from '../views/ProcessarView.vue'
import HistoricoView from '../views/HistoricoView.vue'
import DetalheView from '../views/DetalheView.vue'
import ProcessarLoteView from '../views/ProcessarLoteView.vue'

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    { path: '/', name: 'processar', component: ProcessarView },
    { path: '/historico', name: 'historico', component: HistoricoView },
    { path: '/conteudo/:id', name: 'detalhe', component: DetalheView },
    { path: '/lote', name: 'lote', component: ProcessarLoteView },
  ],
})

export default router