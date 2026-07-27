import { createRouter, createWebHistory } from 'vue-router'
import ProcessarView from '../views/ProcessarView.vue'
import HistoricoView from '../views/HistoricoView.vue'
import DetalheView from '../views/DetalheView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'processar', component: ProcessarView },
    { path: '/historico', name: 'historico', component: HistoricoView },
    { path: '/conteudo/:id', name: 'detalhe', component: DetalheView },
  ],
})

export default router