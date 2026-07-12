# 🤝 Como Contribuir — TechTagger

Este guia existe para o time trabalhar junto de forma organizada, sem pisar no trabalho um do outro e mantendo o histórico do projeto limpo e fácil de entender — inclusive para quem for avaliar.

## 📑 Índice

- [Antes de começar](#antes-de-começar)
- [Estrutura de branches](#-estrutura-de-branches)
- [Fluxo de trabalho](#-fluxo-de-trabalho)
- [Padrão de commits](#-padrão-de-commits)
- [Pull Requests](#-pull-requests)
- [Onde mexer em cada parte](#-onde-mexer-em-cada-parte)
- [Padrões de código](#-padrões-de-código)
- [Reportando problemas](#-reportando-problemas)
- [Checklist antes de dar push](#-checklist-antes-de-dar-push)

---

## Antes de começar

1. Clone o repositório e siga o [README.md](./README.md) para subir o projeto localmente
2. Confirme que consegue rodar sua parte (backend, ml-api, frontend ou notebook) sem erro antes de começar a mexer
3. Combine com o time quem está trabalhando em quê, para evitar duas pessoas na mesma tarefa

---

## 🌳 Estrutura de branches

- **`main`** — código estável e funcionando. Protegida: só recebe mudanças via Pull Request, nunca commit direto.
- **`feature/<descricao-curta>`** — uma branch por tarefa ou funcionalidade nova.
- **`fix/<descricao-curta>`** — correção de bug específico.
- **`docs/<descricao-curta>`** — mudanças só de documentação.

### Convenção de nomes

```
feature/backend-endpoint-conteudo
feature/ml-api-deteccao-tecnologias
feature/notebook-eda-inicial
feature/frontend-tela-busca
fix/backend-validacao-titulo-vazio
fix/ml-api-campo-url-nan
docs/readme-instrucoes-instalacao
chore/docker-compose-healthcheck
```

---

## 🔄 Fluxo de trabalho

```bash
# 1. Sempre parta da main atualizada
git checkout main
git pull origin main

# 2. Crie sua branch a partir dela
git checkout -b feature/minha-tarefa

# 3. Trabalhe e commite em pequenos passos (veja padrão abaixo)
git add .
git commit -m "feat(backend): adiciona endpoint POST /conteudo"

# 4. Mantenha sua branch atualizada com a main (evita conflitos grandes depois)
git checkout main
git pull origin main
git checkout feature/minha-tarefa
git merge main

# 5. Suba sua branch
git push -u origin feature/minha-tarefa

# 6. Abra um Pull Request no GitHub apontando para a main
```

---

## 📝 Padrão de commits

Usamos [Conventional Commits](https://www.conventionalcommits.org/) — deixa o histórico do projeto claro e é um padrão bem reconhecido, o que soma pontos em avaliações técnicas.

```
<tipo>(<escopo opcional>): <descrição curta, no imperativo>
```

### Tipos

| Tipo | Quando usar |
|---|---|
| `feat` | Nova funcionalidade |
| `fix` | Correção de bug |
| `docs` | Mudança só de documentação |
| `refactor` | Reorganização de código sem mudar comportamento |
| `test` | Adição ou ajuste de testes |
| `chore` | Manutenção (dependências, configs, CI) |
| `style` | Formatação, sem mudança de lógica |

### Escopos sugeridos

`backend`, `ml-api`, `frontend`, `data-science`, `infra`, `docs`

### Exemplos

```bash
feat(ml-api): adiciona deteccao de tecnologias no texto
fix(backend): corrige mapeamento de campos snake_case para camelCase
docs(readme): atualiza instrucoes de instalacao do frontend
refactor(ml-api): extrai logica de limpeza de texto para utils
test(backend): adiciona teste do endpoint POST /conteudo
chore(infra): adiciona healthcheck no mysql do docker-compose
```

**Boas práticas:**
- Um commit = uma mudança lógica (evite commits gigantes misturando várias coisas)
- Escreva no imperativo ("adiciona", não "adicionado" ou "adicionando")
- Se precisar explicar o porquê, use o corpo do commit (linha em branco + parágrafo explicativo)

---

## 🔀 Pull Requests

Todo PR usa automaticamente o [template padrão](./.github/PULL_REQUEST_TEMPLATE.md). Preencha:

- **Título** claro, seguindo o mesmo padrão dos commits (ex: `feat(backend): adiciona endpoint de busca por categoria`)
- **Descrição** do que mudou e por quê
- **Como testar** — passos objetivos para quem for revisar
- **Checklist** marcado

### Boas práticas de revisão

- Peça revisão de pelo menos 1 pessoa do time antes do merge, se o tempo permitir
- Teste localmente a branch antes de aprovar
- Prefira **Squash and merge** no GitHub para manter o histórico da `main` limpo
- Delete a branch depois do merge (o próprio GitHub oferece essa opção)

---

## 📁 Onde mexer em cada parte

| Vou trabalhar em... | Pasta | README específico |
|---|---|---|
| Notebook, dataset, treino do modelo | `data-science/` | [`data-science/README.md`](./data-science/README.md) |
| API Python que serve o modelo | `ml-api/` | [`ml-api/README.md`](./ml-api/README.md) |
| API Java, banco de dados | `backend/` | [`backend/README.md`](./backend/README.md) |
| Interface web | `frontend/` | [`frontend/README.md`](./frontend/README.md) |
| Docker, deploy, OCI | `infra/` | [`infra/README.md`](./infra/README.md) |

Cada pasta tem instruções específicas de setup, execução e testes.

---

## 🎨 Padrões de código

### Python (`data-science/`, `ml-api/`)
- Siga PEP 8 quando possível
- Nomes de função e variável em `snake_case`
- Docstrings curtas em funções não óbvias

### Java (`backend/`)
- Convenção padrão Java: classes em `PascalCase`, métodos/variáveis em `camelCase`
- Um `Controller` só chama `Service`, nunca `Repository` diretamente
- DTOs separados de entidades (nunca expor entidade JPA direto na API)

### Geral
- Sem código comentado "morto" (se não usa, apaga)
- Sem `print()`/`console.log()` esquecido em código de produção
- Nomes de variável descritivos (evite `x`, `temp`, `data2`)

---

## 🐛 Reportando problemas

Encontrou um bug? Abra uma [Issue](../../issues/new/choose) usando o template de bug report. Inclua:
- O que você esperava que acontecesse
- O que aconteceu de fato
- Passos para reproduzir
- Logs/prints do erro, se tiver

Tem uma ideia de melhoria? Use o template de feature request.

---

## ✅ Checklist antes de dar push

- [ ] O projeto builda/roda sem erro localmente
- [ ] Não subi arquivo `.env` ou qualquer credencial real
- [ ] Não subi `node_modules/`, `venv/`, `target/`, `__pycache__/` (o `.gitignore` já cobre, mas confira)
- [ ] Mensagem de commit segue o padrão descrito acima
- [ ] Testei o fluxo que mexi, não só assumi que funcionou

---

Dúvidas? Chama o time no grupo antes de travar sozinho — hackathon é contra o tempo, não contra o orgulho. 🚀
