# TechTagger — Frontend

Interface web do TechTagger, o classificador automático de conteúdo técnico do hackathon **TechMind** (equipe G9-BR-Equipe-22). Consome a API REST do backend Spring Boot para processar, listar e visualizar conteúdos classificados por categoria.

## Stack

- **Vue 3** (Composition API, `<script setup>`)
- **Vite** — build e dev server
- **Tailwind CSS v4** — estilização
- **Vue Router** — navegação entre telas
- **Axios** — chamadas HTTP à API

## Pré-requisitos

- Node.js 18+
- Backend rodando em `http://localhost:8080` (ver `README.md` da raiz do projeto)

## Como rodar

```bash
# instalar dependências
npm install

# subir o servidor de desenvolvimento
npm run dev
```

A aplicação sobe em `http://localhost:5173`.

Para gerar a build de produção:
```bash
npm run build
npm run preview   # serve a build localmente, para conferência
```

## Variáveis de ambiente

Arquivo `.env` na raiz do frontend:

```
VITE_API_URL=http://localhost:8080
```

Aponta para a URL base do backend. Ajuste conforme o ambiente (dev local, OCI, etc).

## Estrutura de pastas

```
src/
├── main.js              # bootstrap da aplicação (Vue + Router)
├── App.vue               # layout raiz (navbar + conteúdo + footer)
├── style.css              # tokens de design (cores, fontes) e Tailwind
├── router/
│   └── index.js           # definição das rotas
├── services/
│   └── api.js              # cliente Axios + normalização de erros
├── composables/
│   ├── useConteudo.js       # estado/lógica de processar e buscar um conteúdo
│   └── usePaginacao.js       # estado/lógica de listagem paginada
├── components/
│   ├── NavBar.vue            # cabeçalho com navegação
│   ├── CategoriaBadge.vue     # badge colorido por categoria
│   └── ErroAlerta.vue          # exibição padronizada de erros da API
└── views/
    ├── ProcessarView.vue        # tela inicial: formulário de processamento
    ├── HistoricoView.vue         # listagem paginada com filtro por categoria
    └── DetalheView.vue            # detalhe de um conteúdo processado
```

## Rotas

| Rota | Tela | Endpoint consumido |
|---|---|---|
| `/` | Processar conteúdo | `POST /conteudo` |
| `/historico` | Histórico paginado | `GET /conteudo?categoria=&page=&size=` |
| `/conteudo/:id` | Detalhe | `GET /conteudo/{id}` |

## Tratamento de erros

O `services/api.js` normaliza qualquer erro de resposta da API no formato `{ status, error, detail }`, cobrindo os cenários tratados pelo backend:

| Status | Cenário |
|---|---|
| 400 | Validação (título/texto fora do tamanho permitido) |
| 404 | Conteúdo não encontrado |
| 503 | ML API indisponível ou em timeout |
| 0 | Backend fora do ar / erro de rede |

O componente `ErroAlerta.vue` exibe a mensagem apropriada para cada caso.

## Design

Paleta e tipografia definidas em `src/style.css` via `@theme` do Tailwind v4:
- Fonte mono (`JetBrains Mono`) para tags de categoria e elementos técnicos
- Fonte sans (`Inter`) para o corpo do texto
- Cor própria por categoria (8 categorias do modelo: Backend, Frontend, DevOps, Cloud, Mobile, Databases, Data Science, Data Engineering)
- Componentes estilizados como "editor de código" (`editor-card`), reforçando a identidade de ferramenta técnica

## Próximos passos / ideias futuras

- Testes automatizados (unitários dos composables, e2e das telas principais)
- Loading skeletons no lugar dos estados de carregamento atuais
- Responsividade mobile mais refinada
- Dark mode