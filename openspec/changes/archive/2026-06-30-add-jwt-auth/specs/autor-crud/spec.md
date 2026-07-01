## ADDED Requirements

### Requirement: Autenticação e autorização nos endpoints de autor
Todos os endpoints de `/api/autores/**` SHALL exigir um JWT válido (ver capacidade `jwt-validation`). Endpoints de leitura (`GET /api/autores`, `GET /api/autores/{id}`) SHALL aceitar qualquer papel autenticado. Endpoints de escrita (`POST /api/autores`, `PUT /api/autores/{id}`, `DELETE /api/autores/{id}`) SHALL exigir papel `BIBLIOTECARIO`.

#### Scenario: Listagem sem token
- **WHEN** `GET /api/autores` é chamado sem header `Authorization`
- **THEN** o sistema retorna HTTP 401 com corpo JSON `{"erro": "..."}`, sem retornar a lista de autores

#### Scenario: Cadastro como usuário comum
- **WHEN** `POST /api/autores` é chamado com token válido de papel `USUARIO`
- **THEN** o sistema retorna HTTP 403 com corpo JSON `{"erro": "..."}`, sem criar o autor

#### Scenario: Cadastro como bibliotecário
- **WHEN** `POST /api/autores` é chamado com token válido de papel `BIBLIOTECARIO` e corpo válido
- **THEN** o sistema retorna HTTP 201 com o autor criado, conforme o comportamento já especificado em `autor-crud`

#### Scenario: Exclusão como bibliotecário
- **WHEN** `DELETE /api/autores/{id}` é chamado com token válido de papel `BIBLIOTECARIO`
- **THEN** o sistema retorna HTTP 204, conforme o comportamento já especificado em `autor-crud`
