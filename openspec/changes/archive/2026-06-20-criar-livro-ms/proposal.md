## Why

O `livro-ms` é o microsserviço responsável pelo catálogo de livros da biblioteca. Atualmente apenas o esqueleto (Application + application.properties) existe; o serviço precisa ser completamente implementado para que o `front-ms` possa listar, cadastrar, editar, filtrar e excluir livros.

## What Changes

- Criação do microsserviço `livro-ms` com API REST completa para CRUD de livros.
- Implementação das entidades, repositório, serviço, controller e tratamento de erros.
- Suporte a filtro de livros por disponibilidade via query param `?disponivel=true/false`.
- `pom.xml` com dependências Spring Boot Web, Data JPA, Validation e driver PostgreSQL.
- `Dockerfile` multi-stage para build e execução do jar.
- `application.properties` configurado para conectar ao banco `db_livros` na porta 5433.

## Capabilities

### New Capabilities

- `livro-crud`: CRUD completo de livros via endpoints REST — listar, buscar por ID, criar, atualizar e excluir.
- `livro-filtro-disponibilidade`: Filtro opcional de livros por campo `disponivel` via query param `?disponivel=true/false`.

### Modified Capabilities

<!-- Nenhuma spec existente será alterada -->

## Impact

- **Serviço afetado**: `livro-ms/` — todos os arquivos Java e recursos do serviço são criados do zero.
- **APIs expostas**: `GET/POST/PUT/DELETE /api/livros` e `GET /api/livros/{id}`.
- **Banco de dados**: PostgreSQL `db_livros` na porta 5433 do container `postgres-livros`.
- **Dependências**: Sem alterações em outros microsserviços; `front-ms` já está preparado para consumir esta API.
- **Infraestrutura**: Dockerfile e `docker-compose.yml` já contemplam o serviço; nenhuma alteração de infra necessária.
