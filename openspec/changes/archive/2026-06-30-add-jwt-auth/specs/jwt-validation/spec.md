## ADDED Requirements

### Requirement: Validação de token JWT em toda requisição às APIs protegidas
`autor-ms` e `livro-ms` SHALL validar a presença e a validade de um JWT (assinatura, expiração) no header `Authorization: Bearer <token>` em toda requisição a `/api/**`. Requisições sem o header, com token malformado, com assinatura inválida ou expirado SHALL retornar HTTP 401 com corpo JSON `{"erro": "..."}`.

#### Scenario: Requisição sem header Authorization
- **WHEN** uma requisição a `/api/autores` ou `/api/livros` é feita sem o header `Authorization`
- **THEN** o sistema retorna HTTP 401 com corpo JSON `{"erro": "Token de autenticação não informado"}`

#### Scenario: Token com assinatura inválida
- **WHEN** uma requisição é feita com `Authorization: Bearer <token>` cujo token não foi assinado com o segredo compartilhado configurado
- **THEN** o sistema retorna HTTP 401 com corpo JSON `{"erro": "Token inválido"}`

#### Scenario: Token expirado
- **WHEN** uma requisição é feita com um token cuja claim `exp` já passou
- **THEN** o sistema retorna HTTP 401 com corpo JSON `{"erro": "Token expirado"}`

#### Scenario: Token válido permite seguir para a regra de autorização
- **WHEN** uma requisição é feita com um token assinado corretamente, não expirado e com claim de papel (`role`)
- **THEN** o sistema processa a requisição e aplica a regra de autorização por papel descrita no requisito de escrita

---

### Requirement: Autorização de escrita restrita ao papel BIBLIOTECARIO
`autor-ms` e `livro-ms` SHALL exigir que o JWT contenha o papel `BIBLIOTECARIO` para autorizar requisições `POST`, `PUT` e `DELETE` em `/api/**`. Requisições `GET` SHALL ser autorizadas para qualquer papel autenticado (`BIBLIOTECARIO` ou `USUARIO`). Requisições autenticadas com papel insuficiente para a operação SHALL retornar HTTP 403 com corpo JSON `{"erro": "..."}`.

#### Scenario: Bibliotecário pode criar, atualizar e excluir
- **WHEN** um token com `role: BIBLIOTECARIO` é usado em `POST /api/autores`, `PUT /api/livros/{id}` ou `DELETE /api/autores/{id}`
- **THEN** o sistema processa a requisição normalmente, retornando o status de sucesso esperado pelo endpoint

#### Scenario: Usuário comum não pode criar, atualizar ou excluir
- **WHEN** um token com `role: USUARIO` é usado em `POST /api/autores`, `PUT /api/livros/{id}` ou `DELETE /api/autores/{id}`
- **THEN** o sistema retorna HTTP 403 com corpo JSON `{"erro": "Acesso restrito a bibliotecários"}`

#### Scenario: Leitura permitida para qualquer papel autenticado
- **WHEN** um token com `role: USUARIO` ou `role: BIBLIOTECARIO` é usado em `GET /api/autores` ou `GET /api/livros/{id}`
- **THEN** o sistema retorna a resposta normal do endpoint (200 ou 404, conforme o caso), sem bloquear por papel
