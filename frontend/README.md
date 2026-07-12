# 🖥️ Frontend — TechTagger

Interface web opcional para cadastro, busca e consulta dos conteúdos processados.

> Status: a definir pelo time (React, Vue, ou outra stack)

## Como rodar (exemplo com React/Vite)

```bash
cd frontend
npm install
npm run dev
```

## Estrutura sugerida

```
frontend/
├── src/
│   ├── components/   # Componentes reutilizáveis
│   ├── pages/         # Telas da aplicação
│   ├── services/      # Chamadas à API do backend
│   └── assets/        # Imagens, ícones, estilos
└── public/
```

## Funcionalidades sugeridas

- Formulário para enviar título + texto
- Exibição da categoria, confiança e palavras-chave retornadas
- Lista de conteúdos já processados, com filtro por categoria
- Busca de conteúdos relacionados

## Variáveis de ambiente

```env
VITE_API_URL=http://localhost:8080
```
