## Context

O `livro-ms` já possui estrutura inicial (Application class, pom.xml e application.properties), mas sem nenhuma lógica de negócio. Ele segue o mesmo padrão arquitetural do `autor-ms` (já implementado): Spring Boot 3.3.5, JPA com `ddl-auto=update`, PostgreSQL 16 em container Docker dedicado e API REST exposta via Nginx.

A diferença central em relação ao `autor-ms` é que `livro-ms` possui um campo `disponivel` (Boolean) e um campo `autorId` (Long, referência lógica sem FK). O `autorId` não é validado contra o `autor-ms`; quem faz essa validação é o `front-ms` antes de enviar a requisição.

## Goals / Non-Goals

**Goals:**
- Implementar CRUD completo de livros (`GET`, `POST`, `PUT`, `DELETE`) em `/api/livros`.
- Suportar filtro por disponibilidade via `?disponivel=true/false`.
- Tratar erros de validação (400) e not found (404) com JSON `{"erro": "mensagem"}`.
- Garantir persistência via volume Docker declarado no `docker-compose.yml` existente.

**Non-Goals:**
- Validar se `autorId` existe no `autor-ms` (responsabilidade do `front-ms`).
- Implementar paginação ou ordenação.
- Adicionar testes automatizados nesta entrega.
- Modificar infraestrutura (Dockerfile, docker-compose.yml, nginx.conf) já existente.

## Decisions

### 1. Mesma estrutura de pacotes do `autor-ms`

**Decisão:** Replicar exatamente a estrutura `controller/`, `model/`, `repository/`, `service/`, `exception/` do `autor-ms`.

**Rationale:** Consistência entre microsserviços facilita manutenção. O `autor-ms` já foi validado como padrão do projeto. DTOs opcionais ficam para uma iteração futura caso a API precise de projeções.

**Alternativa considerada:** Usar DTOs de request/response separados da entidade JPA. Descartado nesta entrega por não haver necessidade de ocultar campos ou fazer transformações.

---

### 2. `Livro` como entidade JPA sem FK explícita para `autorId`

**Decisão:** `autorId` é mapeado como `@Column(nullable = false)` do tipo `Long`, sem `@ManyToOne` ou `@JoinColumn`.

**Rationale:** Alinhado com a regra de ouro dos microsserviços do projeto — cada serviço possui seu próprio banco. FK cross-database não é possível no PostgreSQL; FK lógica é responsabilidade da aplicação.

**Alternativa considerada:** `@Transient` + chamada HTTP ao `autor-ms` no service. Descartado porque viola a independência do serviço e cria acoplamento.

---

### 3. Filtro de disponibilidade via `@RequestParam Optional<Boolean>`

**Decisão:** O endpoint `GET /api/livros` aceita `?disponivel=true/false`. Quando ausente, retorna todos os livros. A lógica é resolvida no `LivroService` com dois métodos de repositório.

**Rationale:** Mais explícito e testável que usar `Specification` JPA. Para dois casos (todos / filtrado), um `if/else` no service é suficiente.

**Alternativa considerada:** Spring Data `JpaSpecificationExecutor`. Descartado por ser excessivo para um filtro simples.

---

### 4. Tratamento de erros via `@RestControllerAdvice`

**Decisão:** `GlobalExceptionHandler` trata `EntityNotFoundException` (404) e `MethodArgumentNotValidException` (400), idêntico ao `autor-ms`.

**Rationale:** Padroniza o formato de resposta de erro em `{"erro": "mensagem"}` em todo o projeto.

---

### 5. `ddl-auto=update` para criação da tabela

**Decisão:** Manter `spring.jpa.hibernate.ddl-auto=update` conforme já configurado no `application.properties`.

**Rationale:** Cria a tabela `livros` automaticamente na primeira execução sem necessidade de scripts SQL adicionais. Aceitável para o escopo do projeto.

## Risks / Trade-offs

- **[Risco] Integridade referencial de `autorId`** → O `livro-ms` armazena IDs de autores que podem ser excluídos do `autor-ms` sem notificação. Mitigação: o `front-ms` exibe "Autor removido" quando `autor-ms` retorna 404 para um `autorId` referenciado.

- **[Trade-off] Sem paginação** → A listagem retorna todos os livros de uma vez. Mitigação: aceitável dado o escopo do projeto (biblioteca pequena). Paginação pode ser adicionada via `Pageable` no repositório em versão futura.

- **[Risco] `ddl-auto=update` em produção** → Pode causar perda de dados se o modelo mudar incorretamente. Mitigação: neste projeto, aceitável por ser ambiente de desenvolvimento/avaliação.
