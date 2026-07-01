## ADDED Requirements

### Requirement: Autenticação e autorização nos endpoints de livro
Todos os endpoints de `/api/livros/**` SHALL exigir um JWT válido (ver capacidade `jwt-validation`). Endpoints de leitura (`GET /api/livros`, `GET /api/livros/{id}`) SHALL aceitar qualquer papel autenticado. Endpoints de escrita (`POST /api/livros`, `PUT /api/livros/{id}`, `DELETE /api/livros/{id}`) SHALL exigir papel `BIBLIOTECARIO`.

#### Scenario: Listagem sem token
- **WHEN** `GET /api/livros` é chamado sem header `Authorization`
- **THEN** o sistema retorna HTTP 401 com corpo JSON `{"erro": "..."}`, sem retornar a lista de livros

#### Scenario: Cadastro como usuário comum
- **WHEN** `POST /api/livros` é chamado com token válido de papel `USUARIO`
- **THEN** o sistema retorna HTTP 403 com corpo JSON `{"erro": "..."}`, sem criar o livro

#### Scenario: Cadastro como bibliotecário
- **WHEN** `POST /api/livros` é chamado com token válido de papel `BIBLIOTECARIO` e corpo válido
- **THEN** o sistema retorna HTTP 201 com o livro criado, conforme o comportamento já especificado em `livro-crud`

#### Scenario: Atualização de disponibilidade como usuário comum
- **WHEN** `PUT /api/livros/{id}` é chamado com token válido de papel `USUARIO`
- **THEN** o sistema retorna HTTP 403 com corpo JSON `{"erro": "..."}`, sem alterar o livro
