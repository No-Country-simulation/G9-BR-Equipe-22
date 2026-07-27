<script setup>
import { onMounted } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import { useConteudo } from '../composables/useConteudo'
import CategoriaBadge from '../components/CategoriaBadge.vue'
import ErroAlerta from '../components/ErroAlerta.vue'

const route = useRoute()
const { dados: conteudo, carregando, erro, buscarPorId } = useConteudo()

onMounted(() => buscarPorId(route.params.id))
</script>