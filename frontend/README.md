# TechTagger — Frontend
 
Interface web do TechTagger, o classificador automático de conteúdo técnico do hackathon **TechMind** (equipe G9-BR-Equipe-22). Consome a API REST do backend Spring Boot para processar (individualmente, em lote ou via CSV), listar e visualizar conteúdos classificados por categoria.
 
## Stack
 
- **Vue 3** (Composition API, `<script setup>`)
- **Vite** — build e dev server
- **Tailwind CSS v4** — estilização
- **Vue Router** (modo hash) — navegação entre telas
- **Axios** — chamadas HTTP à API
## Pré-requisitos
 
- Node.js 18+
- Backend rodando (local: `http://localhost:8080`, ver `README.md` da raiz do projeto)
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
 
Arquivo `.env` **na raiz do `frontend/`** (não em `src/` — o Vite só lê variáveis `VITE_*` desse local):
```
VITE_API_URL=http://64.181.163.68:8080
```
 
Aponta para a URL base do backend. Em desenvolvimento local, normalmente `http://localhost:8080`; o valor acima é o do ambiente publicado. Após qualquer alteração nesse arquivo, reinicie o `npm run dev` — o Vite não recarrega variáveis de ambiente em quente.
 
## Estrutura de pastas
 
```
src/
├── main.js               # bootstrap da aplicação (Vue + Router)
├── App.vue                # layout raiz (navbar + conteúdo + footer)
├── style.css               # tokens de design (cores, fontes) e Tailwind
├── router/
│   └── index.js            # definição das rotas (hash history)
├── services/
│   └── api.js               # cliente Axios + normalização de erros
├── composables/
│   ├── useConteudo.js        # estado/lógica de processar e buscar um conteúdo
│   ├── useConteudoLote.js      # estado/lógica de processar em lote (JSON e CSV)
│   └── usePaginacao.js          # estado/lógica de listagem paginada
├── components/
│   ├── NavBar.vue                # cabeçalho com navegação
│   ├── CategoriaBadge.vue         # badge colorido por categoria
│   └── ErroAlerta.vue              # exibição padronizada de erros da API
└── views/
    ├── ProcessarView.vue            # tela inicial: formulário de processamento individual
    ├── ProcessarLoteView.vue         # processamento em lote: formulário manual ou upload de CSV
    ├── HistoricoView.vue              # listagem paginada com filtro por categoria (dinâmico)
    └── DetalheView.vue                 # detalhe de um conteúdo processado
```
 
## Rotas
 
A navegação usa `createWebHashHistory` (URLs com `#`, ex.: `/#/historico`) — evita a necessidade de configurar rewrite de rotas no servidor de hospedagem.
 
| Rota | Tela | Endpoint(s) consumido(s) |
|---|---|---|
| `/#/` | Processar conteúdo | `POST /conteudo` |
| `/#/lote` | Processar em lote | `POST /conteudo/batch` (formulário) ou `POST /conteudo/batch/csv` (upload de arquivo) |
| `/#/historico` | Histórico paginado | `GET /conteudo?categoria=&page=&size=`, `GET /conteudo/categorias` |
| `/#/conteudo/:id` | Detalhe | `GET /conteudo/{id}` |
 
## Tratamento de erros
 
O `services/api.js` normaliza qualquer erro de resposta da API no formato `{ status, error, detail }`, cobrindo os cenários tratados pelo backend:
 
| Status | Cenário |
|---|---|
| 400 | Validação (título/texto fora do tamanho permitido) |
| 404 | Conteúdo não encontrado |
| 503 | ML API indisponível ou em timeout |
| 0 | Backend fora do ar / erro de rede |
 
O componente `ErroAlerta.vue` exibe a mensagem apropriada para cada caso.
 
**Categorias do filtro no Histórico:** buscadas dinamicamente em `GET /conteudo/categorias`; se a chamada falhar (ou a API retornar lista vazia), a tela usa uma lista fixa de fallback com as 8 categorias conhecidas, para o filtro nunca ficar vazio.
 
## Design
 
Paleta e tipografia definidas em `src/style.css` via `@theme` do Tailwind v4:
- Fonte mono (`JetBrains Mono`) para tags de categoria e elementos técnicos
- Fonte sans (`Inter`) para o corpo do texto
- Cor própria por categoria (8 categorias do modelo: Backend, Frontend, DevOps, Cloud, Mobile, Databases, Data Science, Data Engineering), centralizada em `CategoriaBadge.vue`
- Componentes estilizados como "editor de código" (`editor-card`), reforçando a identidade de ferramenta técnica
## Próximos passos / ideias futuras
 
- Testes automatizados (unitários dos composables, e2e das telas principais)
- Loading skeletons no lugar dos estados de carregamento atuais
- Responsividade mobile mais refinada
- Dark mode
- Preview do CSV (contagem de linhas detectadas) antes de confirmar o envio em `/lote`
 
