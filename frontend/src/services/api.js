import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  headers: { 'Content-Type': 'application/json' },
})

function normalizarErro(err) {
  if (err.response) {
    const { status, data } = err.response
    return {
      status,
      error: data?.error || 'Erro',
      detail: data?.detail || 'Ocorreu um erro inesperado.',
    }
  }
  return {
    status: 0,
    error: 'Sem conexão',
    detail: 'Não foi possível falar com o servidor. Verifique se o backend está rodando.',
  }
}

export async function processarConteudo(titulo, texto) {
  try {
    const { data } = await api.post('/conteudo', { titulo, texto })
    return data
  } catch (err) {
    throw normalizarErro(err)
  }
}

export async function buscarConteudoPorId(id) {
  try {
    const { data } = await api.get(`/conteudo/${id}`)
    return data
  } catch (err) {
    throw normalizarErro(err)
  }
}

export async function listarConteudos({ categoria = '', page = 0, size = 10 } = {}) {
  try {
    const params = { page, size }
    if (categoria) params.categoria = categoria
    const { data } = await api.get('/conteudo', { params })
    return data
  } catch (err) {
    throw normalizarErro(err)
  }
}

export default api