## 1. Estrutura Maven do Projeto

- [ ] 1.1 Criar a pasta `autor-ms/` na raiz do monorepo
- [ ] 1.2 Criar `autor-ms/pom.xml` com groupId `com.library`, artifactId `autorms`, Java 21, Spring Boot 3.3.5, dependências: `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`, `postgresql`
- [ ] 1.3 Criar a estrutura de diretórios: `autor-ms/src/main/java/com/library/autorms/` com subpastas `controller/`, `model/`, `repository/`, `service/`, `exception/`
- [ ] 1.4 Criar `autor-ms/src/main/resources/application.properties` com porta 8082, datasource apontando para `postgres-autores:5432/db_autores` (variáveis de ambiente), `ddl-auto=update`, `show-sql=false`
- [ ] 1.5 Criar a classe principal `AutorMsApplication.java` com `@SpringBootApplication` no pacote `com.library.autorms`

## 2. Camada de Modelo e Repositório

- [ ] 2.1 Criar a entidade `Autor.java` em `model/` com `@Entity`, `@Table(name = "autores")`, campos `id` (`@Id`, `@GeneratedValue(IDENTITY)`), `nome` (`@Column(length=100)`, `@NotBlank`), `nacionalidade` (`@Column(length=80)`, `@NotBlank`), `anoNascimento` (`@Positive`, `@NotNull`)
- [ ] 2.2 Criar a interface `AutorRepository.java` em `repository/` estendendo `JpaRepository<Autor, Long>`

## 3. Camada de Serviço

- [ ] 3.1 Criar `AutorService.java` em `service/` com `@Service`, métodos: `listarTodos()`, `buscarPorId(Long id)`, `criar(Autor autor)`, `atualizar(Long id, Autor dados)`, `excluir(Long id)`
- [ ] 3.2 Implementar `buscarPorId` lançando `EntityNotFoundException` com mensagem `"Autor não encontrado: {id}"` quando o ID não existir
- [ ] 3.3 Implementar `atualizar` copiando todos os campos do objeto recebido para a entidade existente antes de salvar (PUT completo)
- [ ] 3.4 Implementar `excluir` verificando a existência antes de deletar e lançando `EntityNotFoundException` se não existir

## 4. Camada de Controller

- [ ] 4.1 Criar `AutorController.java` em `controller/` com `@RestController` e `@RequestMapping("/api/autores")`
- [ ] 4.2 Implementar `GET /api/autores` retornando `ResponseEntity<List<Autor>>` com HTTP 200 (HU-01)
- [ ] 4.3 Implementar `GET /api/autores/{id}` retornando `ResponseEntity<Autor>` com HTTP 200, delegando para `AutorService.buscarPorId` (HU-02)
- [ ] 4.4 Implementar `POST /api/autores` com `@Valid @RequestBody Autor autor` retornando `ResponseEntity<Autor>` com HTTP 201 via `ResponseEntity.created(uri).body(autor)` (HU-03)
- [ ] 4.5 Implementar `PUT /api/autores/{id}` com `@Valid @RequestBody Autor dados` retornando `ResponseEntity<Autor>` com HTTP 200 (HU-04)
- [ ] 4.6 Implementar `DELETE /api/autores/{id}` retornando `ResponseEntity<Void>` com HTTP 204 (HU-05)

## 5. Tratamento Global de Erros

- [ ] 5.1 Criar `GlobalExceptionHandler.java` em `exception/` com `@RestControllerAdvice`
- [ ] 5.2 Implementar handler para `EntityNotFoundException` retornando HTTP 404 com corpo `{"erro": "<mensagem da exceção>"}`
- [ ] 5.3 Implementar handler para `MethodArgumentNotValidException` retornando HTTP 400 com mapa dos campos inválidos e suas mensagens de erro
- [ ] 5.4 Verificar que o padrão de resposta de erro segue `{"erro": "mensagem"}` para todos os casos de erro

## 6. Dockerfile

- [ ] 6.1 Criar `autor-ms/Dockerfile` usando `eclipse-temurin:21-jre` como imagem base
- [ ] 6.2 Configurar o Dockerfile para copiar o JAR gerado por Maven (`target/*.jar`) e definir o `ENTRYPOINT` com `java -jar`
- [ ] 6.3 Expor a porta 8082 no Dockerfile (`EXPOSE 8082`)

## 7. Docker Compose e Nginx

- [ ] 7.1 Adicionar o serviço `postgres-autores` ao `docker-compose.yml` com imagem `postgres:16`, variáveis de ambiente `POSTGRES_DB=db_autores`, `POSTGRES_USER`, `POSTGRES_PASSWORD`, e volume `postgres_autores_data:/var/lib/postgresql/data`
- [ ] 7.2 Adicionar o serviço `autor-ms` ao `docker-compose.yml` com `build: ./autor-ms`, porta `8082:8082`, `depends_on: postgres-autores`, e variáveis de ambiente para conexão com o banco
- [ ] 7.3 Declarar o volume nomeado `postgres_autores_data` na seção `volumes:` do `docker-compose.yml` (HU-20)
- [ ] 7.4 Adicionar (ou verificar) a diretiva `location /api/autores` no `nginx.conf` com `proxy_pass http://autor-ms:8082` (HU-21)

## 8. Validação End-to-End

- [ ] 8.1 Executar `docker compose up --build` e verificar que o container `autor-ms` sobe sem erros e que `GET http://localhost/api/autores` retorna HTTP 200 com `[]`
- [ ] 8.2 Testar `POST http://localhost/api/autores` com dados válidos e verificar resposta HTTP 201 com `id` gerado
- [ ] 8.3 Testar `GET http://localhost/api/autores/{id}` com o ID retornado no passo anterior e verificar HTTP 200
- [ ] 8.4 Testar `PUT http://localhost/api/autores/{id}` com dados atualizados e verificar HTTP 200 com dados alterados
- [ ] 8.5 Testar `DELETE http://localhost/api/autores/{id}` e verificar HTTP 204 e, em seguida, que `GET /api/autores/{id}` retorna HTTP 404
- [ ] 8.6 Testar `POST` com campos inválidos (nome vazio, anoNascimento negativo) e verificar HTTP 400 com corpo de erro
- [ ] 8.7 Executar `docker compose restart autor-ms` após cadastrar um autor e verificar que o dado persiste (HU-20)
- [ ] 8.8 Executar `docker compose down` e verificar teardown sem erros (HU-19)
