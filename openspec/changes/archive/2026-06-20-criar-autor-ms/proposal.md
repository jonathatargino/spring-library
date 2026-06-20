## Why

O sistema de biblioteca requer um microsserviço dedicado ao gerenciamento de autores (`autor-ms`) que exponha uma API REST independente, com banco de dados PostgreSQL próprio, para que o `front-ms` possa resolver nomes de autores e realizar operações de CRUD sem acoplar essa lógica ao `livro-ms`.

## What Changes

- Criação do módulo Maven `autor-ms` na pasta `autor-ms/` do monorepo com pacote base `com.library.autorms`
- API REST CRUD completa para a entidade `Autor` (GET, POST, PUT, DELETE)
- Banco de dados PostgreSQL dedicado (`db_autores`) com volume Docker para persistência
- Dockerfile próprio para o serviço
- Integração ao `docker-compose.yml` existente com nome de serviço `autor-ms` na porta 8082
- Tratamento global de erros retornando `{"erro": "mensagem"}` com status HTTP adequado
- Validação de campos de entrada com Bean Validation (JSR-380)

## Capabilities

### New Capabilities

- `autor-crud`: CRUD completo de autores via API REST — listar todos (HU-01), buscar por ID (HU-02), cadastrar (HU-03), atualizar (HU-04) e excluir (HU-05)
- `autor-infraestrutura`: Configuração de container Docker, banco PostgreSQL dedicado e roteamento Nginx para `/api/autores` (HU-19, HU-20, HU-21)

### Modified Capabilities

## Impact

- **Novo serviço**: pasta `autor-ms/` com estrutura Maven completa (pom.xml, `src/main/java/com/library/autorms/`)
- **Banco de dados**: container `postgres-autores` adicionado ao Docker Compose com volume `postgres_autores_data`
- **nginx.conf**: rota `/api/autores` já prevista na arquitetura, precisa apontar para `autor-ms:8082`
- **docker-compose.yml**: adição do serviço `autor-ms` e seu banco
- **front-ms**: depende do `autor-ms` estar operacional para resolver nomes de autores nos livros (sem alteração de código nesta change)
