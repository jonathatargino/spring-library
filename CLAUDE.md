# Spring Library — Contexto para Agentes IA

Sistema de biblioteca com arquitetura de microsserviços Spring Boot. Toda a documentação de requisitos está em `.ai/`.

## Stack técnica

- **Java 21**, **Spring Boot 3.3.5**, **Maven**
- **PostgreSQL 16** (dois bancos independentes)
- **Docker + Docker Compose** para orquestração
- **Nginx 1.25** como API Gateway

## Estrutura de serviços

| Serviço    | Pacote base                   | Porta | Banco          |
|------------|-------------------------------|-------|----------------|
| `autor-ms` | `com.library.autorms`         | 8082  | db_autores     |
| `livro-ms` | `com.library.livrooms`        | 8081  | db_livros      |
| `front-ms` | `com.library.frontms`         | 8080  | (sem banco)    |

## Decisões de design

- `livro-ms` armazena `autorId` como `Long` — sem FK no banco, sem chamada HTTP para `autor-ms`. Quem resolve o nome é o `front-ms`.
- `front-ms` usa `RestClient` (Spring 6.1+) para chamar as APIs. URLs configuradas via `application.properties`: `livro.ms.url` e `autor.ms.url`.
- No Docker Compose, `front-ms` chama `http://livro-ms:8081` e `http://autor-ms:8082` (nomes de serviço Docker).
- `ddl-auto=update` para criar/atualizar tabelas automaticamente na primeira execução.
- Nginx roteia: `/api/livros` → `livro-ms`, `/api/autores` → `autor-ms`, `/` → `front-ms`.

## Padrões de implementação esperados

### autor-ms e livro-ms (APIs REST)
```
controller/   → @RestController com @RequestMapping
model/        → @Entity JPA
repository/   → JpaRepository
service/      → @Service com lógica de negócio
exception/    → @ControllerAdvice para tratamento global de erros
dto/          → (opcional) classes de request/response
```

### front-ms (MVC Thymeleaf)
```
controller/   → @Controller com @GetMapping/@PostMapping
config/       → @Bean RestClient com base URLs
model/        → POJOs para dados recebidos das APIs
templates/
  autores/
    lista.html
    formulario.html
  livros/
    lista.html
    formulario.html
    detalhe.html
  fragments/
    layout.html   (navbar + footer base)
```

## Tratamento de erros

- APIs retornam `{"erro": "mensagem"}` com status HTTP adequado (404, 400).
- `front-ms` captura exceções de `RestClient` e exibe mensagem amigável (nunca página de erro do Spring).
- Padrão PRG (Post-Redirect-Get) obrigatório em todos os formulários do `front-ms`.

## Modelos de dados

### Autor (autor-ms)
```java
Long id          // BIGSERIAL, PK
String nome      // VARCHAR(100), NOT NULL
String nacionalidade // VARCHAR(80), NOT NULL
Integer anoNascimento // INTEGER, NOT NULL, positivo
```

### Livro (livro-ms)
```java
Long id          // BIGSERIAL, PK
String titulo    // VARCHAR(150), NOT NULL
String genero    // VARCHAR(60), NOT NULL
Integer anoPublicacao // INTEGER, NOT NULL, positivo
Boolean disponivel   // NOT NULL, default true
Long autorId     // BIGINT, NOT NULL (referência lógica, sem FK)
```

## Como rodar

```bash
docker compose up --build   # sobe tudo
docker compose down          # derruba
docker compose down -v       # derruba e apaga volumes
```

## Referências de requisitos

- `.ai/architecture.md` — diagrama e responsabilidades
- `.ai/data-modeling.md` — schema dos modelos
- `.ai/hu.md` — histórias de usuário (HU-01 a HU-21)
- `.ai/technical-requirements.md` — requisitos técnicos
