## 1. Modelo de Domínio

- [ ] 1.1 Criar a entidade JPA `Livro` em `livro-ms/src/main/java/com/library/livrooms/model/Livro.java` com campos: `id` (BIGSERIAL PK), `titulo` (VARCHAR 150, NOT NULL), `genero` (VARCHAR 60, NOT NULL), `anoPublicacao` (INTEGER, NOT NULL, positivo), `disponivel` (BOOLEAN, NOT NULL, default true), `autorId` (BIGINT, NOT NULL) — sem FK
- [ ] 1.2 Anotar os campos com `@NotBlank`, `@NotNull`, `@Positive`, `@Size` conforme restrições do modelo de dados, e `@Column` com constraints de banco

## 2. Repositório

- [ ] 2.1 Criar interface `LivroRepository` em `repository/LivroRepository.java` estendendo `JpaRepository<Livro, Long>`
- [ ] 2.2 Adicionar método `findByDisponivel(Boolean disponivel)` no repositório para suportar o filtro de disponibilidade (HU-11)

## 3. Serviço

- [ ] 3.1 Criar `LivroService` em `service/LivroService.java` com método `listarTodos(Optional<Boolean> disponivel)` — sem parâmetro retorna todos; com parâmetro delega para `findByDisponivel`
- [ ] 3.2 Implementar `buscarPorId(Long id)` lançando `EntityNotFoundException("Livro não encontrado: {id}")` quando não encontrado
- [ ] 3.3 Implementar `criar(Livro livro)` retornando a entidade salva
- [ ] 3.4 Implementar `atualizar(Long id, Livro dados)` — carrega pelo id, atualiza todos os campos e salva (PUT completo)
- [ ] 3.5 Implementar `excluir(Long id)` — verifica existência via `buscarPorId` antes de deletar

## 4. Controller

- [ ] 4.1 Criar `LivroController` em `controller/LivroController.java` com `@RestController @RequestMapping("/api/livros")`
- [ ] 4.2 Implementar `GET /api/livros` com `@RequestParam Optional<Boolean> disponivel` delegando para o service (HU-06 + HU-11)
- [ ] 4.3 Implementar `GET /api/livros/{id}` retornando 200 OK ou deixando o `GlobalExceptionHandler` tratar o 404 (HU-07)
- [ ] 4.4 Implementar `POST /api/livros` com `@Valid @RequestBody` retornando 201 Created + header `Location` (HU-08)
- [ ] 4.5 Implementar `PUT /api/livros/{id}` com `@Valid @RequestBody` retornando 200 OK (HU-09)
- [ ] 4.6 Implementar `DELETE /api/livros/{id}` retornando 204 No Content (HU-10)

## 5. Tratamento de Erros

- [ ] 5.1 Criar `GlobalExceptionHandler` em `exception/GlobalExceptionHandler.java` com `@RestControllerAdvice` tratando `EntityNotFoundException` → 404 com `{"erro": "mensagem"}` e `MethodArgumentNotValidException` → 400 com mapa de campos e mensagens

## 6. Configuração e Build

- [ ] 6.1 Verificar `application.properties` já existente: confirmar `spring.datasource.url=jdbc:postgresql://postgres-livros:5432/db_livros`, porta `server.port=8081`, `ddl-auto=update` e credenciais corretas
- [ ] 6.2 Verificar `pom.xml` já existente: confirmar dependências `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation` e `postgresql` (runtime)
- [ ] 6.3 Verificar `Dockerfile` já existente: confirmar build multi-stage com Maven e execução do jar gerado

## 7. Verificação de Integração

- [ ] 7.1 Subir o sistema completo com `docker compose up --build` e verificar que o container `livro-ms` sobe sem erros (HU-19)
- [ ] 7.2 Testar `POST /api/livros` via curl ou cliente HTTP com dados válidos e verificar resposta 201 + id gerado (HU-08)
- [ ] 7.3 Testar `GET /api/livros` e confirmar retorno 200 com o livro criado (HU-06)
- [ ] 7.4 Testar `GET /api/livros?disponivel=true` e confirmar filtro funcionando (HU-11)
- [ ] 7.5 Testar `GET /api/livros/999` e confirmar resposta 404 com `{"erro": "Livro não encontrado: 999"}` (HU-07)
- [ ] 7.6 Testar `PUT /api/livros/{id}` alterando `disponivel` de true para false (HU-09)
- [ ] 7.7 Testar `DELETE /api/livros/{id}` e confirmar 204 + GET retorna 404 (HU-10)
- [ ] 7.8 Testar `POST /api/livros` com dados inválidos (titulo vazio, anoPublicacao negativo, autorId null) e confirmar 400 com mapa de erros (HU-08 critérios de validação)
