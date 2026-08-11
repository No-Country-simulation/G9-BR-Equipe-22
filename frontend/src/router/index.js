import { createRouter, createWebHashHistory } from 'vue-router' // Mudar para createWebHashHistory
import ProcessarView from '../views/ProcessarView.vue'
import HistoricoView from '../views/HistoricoView.vue'
import DetalheView from '../views/DetalheView.vue'

const routes = [
  { path: '/', component: ProcessarView },
  { path: '/historico', component: HistoricoView },
  { path: '/conteudo/:id', component: DetalheView }
]

const router = createRouter({
  history: createWebHashHistory(), // Ativar o Hash aqui
  routes
})

export default router