# 🐳 Infra — TechTagger

Infraestrutura local (Docker) e configuração de deploy na OCI.

## Subir o banco de dados localmente

```bash
cd infra
docker compose up -d
```

Isso sobe:
- **MySQL** na porta `3306` (banco `techtagger`)
- **Adminer** (visualizador web do banco) na porta `8081`

## Comandos úteis

```bash
docker compose ps          # ver status
docker compose logs -f     # ver logs
docker compose stop        # parar (mantém dados)
docker compose down -v     # apaga tudo, inclusive dados
```

## OCI

### Serviços utilizados

| Serviço | Uso |
|---|---|
| Object Storage | Armazenar os arquivos do modelo (`.pkl`, `.npy`, `.csv`, `.json`) |
| OCI Compute | Hospedar a ML API e/ou o Backend |

### Passo a passo resumido

1. Criar um *bucket* no Object Storage (ex: `techtagger-models`)
2. Fazer upload dos arquivos de `ml-api/models/` para o bucket
3. Configurar `USE_OCI_STORAGE=True` no `.env` da ML API, com namespace e nome do bucket
4. Subir uma instância de Compute (Always Free Tier serve para o MVP)
5. Instalar Docker na instância e rodar os containers da ML API e/ou Backend

Scripts e configs específicas de deploy ficam em [`infra/oci/`](./oci).
