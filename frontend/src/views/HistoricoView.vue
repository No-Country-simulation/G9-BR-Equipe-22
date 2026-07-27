<script setup>
import { RouterLink } from 'vue-router'
import { usePaginacao } from '../composables/usePaginacao'
import CategoriaBadge from '../components/CategoriaBadge.vue'
import ErroAlerta from '../components/ErroAlerta.vue'

const CATEGORIAS = [
  'Backend', 'Frontend', 'DevOps', 'Cloud',
  'Mobile', 'Databases', 'Data Science', 'Data Engineering',
]

const { pagina, categoria, dados, carregando, erro, irPara, carregar } = usePaginacao(10)

carregar()
</script>