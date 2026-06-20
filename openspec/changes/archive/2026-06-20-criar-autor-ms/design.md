## Context

O `autor-ms` é um microsserviço Spring Boot responsável pelo CRUD de autores no sistema de biblioteca. Ele é um serviço completamente independente — não conhece nem chama o `livro-ms` ou o `front-ms`. A comunicação com os demais serviços acontece apenas de fora para dentro: o `front-ms` chama `autor-ms` via HTTP para resolver nomes de autores ao exibir livros.

O serviço será criado do zero como módulo separado no monorepo, na pasta `autor-ms/`, seguindo o mesmo padrão estrutural do `livro-ms`.

## Goals / Non-Goals

**Goals:**
- Expor API REST JSON com CRUD completo de autores na porta 8082
- Persistir dados no banco `db_autores` (PostgreSQL 16) com volume Docker
- Validar campos de entrada (nome, nacionalidade, anoNascimento) com Bean Validation
- Retornar erros no padrão `{"erro": "mensagem"}` com status HTTP adequado
- Ser containerizável via Dockerfile e orquestrável via Docker Compose
- Roteamento pelo Nginx: `/api/autores` → `autor-ms:8082`

**Non-Goals:**
- Validar se `autorId` referenciado em `livro-ms` é válido — essa responsabilidade é do `front-ms`
- Expor interface HTML/Thymeleaf — o serviço é puramente REST
- Gerenciar dados de livros
- Autenticação e autorização

## Decisions

### 1. Estrutura de pacotes padrão do projeto

**Decisão:** Seguir a estrutura `controller/`, `model/`, `repository/`, `service/`, `exception/` sob `com.library.autorms`.

**Rationale:** Consistência com o padrão já definido no `livro-ms` e documentado no CLAUDE.md. Facilita onboarding e manutenção. A camada `service/` isola a lógica de negócio do controller, permitindo que validações e regras (ex: verificar existência antes de deletar) fiquem centralizadas.

**Alternativa considerada:** Arquitetura hexagonal (ports & adapters) — descartada por ser over-engineering para um CRUD simples.

---

### 2. `ddl-auto=update` para criação de schema

**Decisão:** Usar `spring.jpa.hibernate.ddl-auto=update` em vez de scripts SQL manuais ou Flyway.

**Rationale:** O requisito HU-19 exige que o sistema suba com um único comando (`docker compose up --build`) sem configuração manual. O `ddl-auto=update` cria a tabela automaticamente na primeira execução a partir das anotações JPA da entidade `Autor`.

**Alternativa considerada:** Flyway/Liquibase — descartada pela complexidade adicional desnecessária para um ambiente acadêmico/demo.

---

### 3. Tratamento global de erros com `@ControllerAdvice`

**Decisão:** Implementar um `GlobalExceptionHandler` com `@ControllerAdvice` para capturar `EntityNotFoundException` e `MethodArgumentNotValidException`.

**Rationale:** Centraliza o tratamento de erros e garante que todas as respostas de erro sigam o padrão `{"erro": "mensagem"}`. Evita try-catch espalhado pelos controllers e garante consistência de status HTTP (404, 400).

---

### 4. Validação com Bean Validation (Jakarta Validation)

**Decisão:** Usar anotações `@NotBlank`, `@Positive` nos campos da entidade/DTO e ativar com `@Valid` no controller.

**Rationale:** Integração nativa com Spring Boot, sem dependências extras. A anotação `@Positive` garante que `anoNascimento` seja maior que zero. O `@ControllerAdvice` captura `MethodArgumentNotValidException` e formata os erros de validação de forma adequada.

---

### 5. Container Docker multi-stage não obrigatório

**Decisão:** Usar Dockerfile simples com `eclipse-temurin:21-jre` como imagem base, copiando o JAR gerado pelo Maven.

**Rationale:** Simplicidade. O build Maven ocorre no host via `docker compose up --build` com contexto de build adequado. Multi-stage build não traz benefício significativo para este contexto.

## Risks / Trade-offs

| Risco | Mitigação |
|---|---|
| `ddl-auto=update` pode causar inconsistências em alterações de schema destrutivas (renomear colunas) | Aceitável para ambiente de desenvolvimento/demo; em produção real, usar Flyway |
| Sem FK entre `livro-ms.autorId` e `autor-ms`, é possível ter livros com autorId inexistente após exclusão de autor | Por decisão de arquitetura, a integridade referencial é responsabilidade da aplicação (`front-ms`); documentado no CLAUDE.md |
| `ddl-auto=update` não recria o schema se o volume existir com dados incompatíveis | `docker compose down -v` limpa os volumes; documentado no CLAUDE.md |

## Migration Plan

1. Criar pasta `autor-ms/` com estrutura Maven e código-fonte
2. Criar `Dockerfile` em `autor-ms/`
3. Adicionar serviço `autor-ms` e banco `postgres-autores` ao `docker-compose.yml` raiz
4. Adicionar rota `/api/autores` no `nginx.conf` apontando para `autor-ms:8082`
5. Executar `docker compose up --build` para validar

**Rollback:** remover o serviço `autor-ms` e `postgres-autores` do `docker-compose.yml` e reverter o `nginx.conf`.

## Open Questions

- Nenhuma questão em aberto. Todos os requisitos estão definidos nas histórias de usuário HU-01 a HU-05 e nos requisitos técnicos.
