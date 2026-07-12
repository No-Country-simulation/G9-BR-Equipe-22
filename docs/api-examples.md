# Exemplos de Uso da API

Mínimo de 3 exemplos exigidos pelo edital do hackathon.

## 1. Processar conteúdo sobre Backend

```bash
curl -X POST http://localhost:8080/conteudo \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Introdução ao Spring Boot",
    "texto": "Neste conteúdo são apresentados os conceitos básicos para criação de APIs REST utilizando Java e Spring Boot."
  }'
```

**Resposta:**
```json
{
  "categoria": "Backend",
  "probabilidade": 0.91,
  "keywords": ["Spring Boot", "Java", "REST API"]
}
```

## 2. Processar conteúdo sobre Frontend

```bash
curl -X POST http://localhost:8080/conteudo \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "React Hooks na prática",
    "texto": "Aprenda a usar useState e useEffect para gerenciar estado e efeitos colaterais em componentes React."
  }'
```

**Resposta:**
```json
{
  "categoria": "Frontend",
  "probabilidade": 0.88,
  "keywords": ["React", "Hooks", "useState", "useEffect"]
}
```

## 3. Processar conteúdo sobre DevOps

```bash
curl -X POST http://localhost:8080/conteudo \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Orquestração de containers com Kubernetes",
    "texto": "Entenda os conceitos de pods, deployments e services no Kubernetes para orquestrar containers Docker em produção."
  }'
```

**Resposta:**
```json
{
  "categoria": "DevOps",
  "probabilidade": 0.85,
  "keywords": ["Kubernetes", "Docker", "containers", "orquestração"]
}
```

## 4. Buscar conteúdo já processado

```bash
curl http://localhost:8080/conteudo/1
```

## 5. Filtrar por categoria

```bash
curl "http://localhost:8080/conteudo?categoria=Backend"
```

## Tratamento de erro (entrada inválida)

```bash
curl -X POST http://localhost:8080/conteudo \
  -H "Content-Type: application/json" \
  -d '{"titulo": "ab", "texto": "curto"}'
```

**Resposta (400):**
```json
{
  "error": "Validation Error",
  "detail": "titulo deve ter no mínimo 3 caracteres"
}
```
