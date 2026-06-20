## ADDED Requirements

### Requirement: Listar todos os livros
O sistema SHALL retornar todos os livros cadastrados via `GET /api/livros` com status 200 OK e array JSON. O array SHALL estar vazio quando não houver livros.

#### Scenario: Retorna lista de livros existentes
- **WHEN** `GET /api/livros` é chamado com livros cadastrados no banco
- **THEN** o sistema retorna 200 OK com array JSON contendo todos os campos: id, titulo, genero, anoPublicacao, disponivel, autorId

#### Scenario: Retorna lista vazia quando não há livros
- **WHEN** `GET /api/livros` é chamado com banco vazio
- **THEN** o sistema retorna 200 OK com array JSON vazio `[]`

---

### Requirement: Buscar livro por ID
O sistema SHALL retornar os dados de um livro pelo seu identificador via `GET /api/livros/{id}`. Quando o ID não existir, SHALL retornar 404 Not Found com corpo JSON `{"erro": "Livro não encontrado: {id}"}`.

#### Scenario: Livro encontrado
- **WHEN** `GET /api/livros/{id}` é chamado com ID existente
- **THEN** o sistema retorna 200 OK com todos os campos do livro: id, titulo, genero, anoPublicacao, disponivel, autorId

#### Scenario: Livro não encontrado
- **WHEN** `GET /api/livros/999` é chamado e o ID 999 não existe
- **THEN** o sistema retorna 404 Not Found com corpo JSON `{"erro": "Livro não encontrado: 999"}`

---

### Requirement: Cadastrar livro
O sistema SHALL criar um novo livro via `POST /api/livros` e retornar 201 Created com o livro criado (incluindo id gerado). Dados inválidos SHALL retornar 400 Bad Request com JSON detalhando os campos com erro.

#### Scenario: Cadastro com dados válidos
- **WHEN** `POST /api/livros` é chamado com corpo JSON válido `{titulo, genero, anoPublicacao, disponivel, autorId}`
- **THEN** o sistema retorna 201 Created com o livro criado incluindo id gerado e header `Location`

#### Scenario: Cadastro com título vazio
- **WHEN** `POST /api/livros` é chamado com `titulo` vazio ou em branco
- **THEN** o sistema retorna 400 Bad Request com JSON indicando erro no campo `titulo`

#### Scenario: Cadastro com `anoPublicacao` negativo ou zero
- **WHEN** `POST /api/livros` é chamado com `anoPublicacao` <= 0
- **THEN** o sistema retorna 400 Bad Request com JSON indicando erro no campo `anoPublicacao`

#### Scenario: Cadastro sem `autorId`
- **WHEN** `POST /api/livros` é chamado sem o campo `autorId` (null)
- **THEN** o sistema retorna 400 Bad Request com JSON indicando erro no campo `autorId`

#### Scenario: `autorId` não é validado contra `autor-ms`
- **WHEN** `POST /api/livros` é chamado com `autorId` que não existe no `autor-ms`
- **THEN** o sistema retorna 201 Created (a validação de existência do autor é responsabilidade do `front-ms`)

---

### Requirement: Atualizar livro
O sistema SHALL substituir completamente os dados de um livro existente via `PUT /api/livros/{id}` e retornar 200 OK com o livro atualizado. Quando o ID não existir, SHALL retornar 404. Dados inválidos SHALL retornar 400.

#### Scenario: Atualização com dados válidos
- **WHEN** `PUT /api/livros/{id}` é chamado com corpo JSON válido e ID existente
- **THEN** o sistema retorna 200 OK com os dados atualizados

#### Scenario: Alteração de disponibilidade
- **WHEN** `PUT /api/livros/{id}` é chamado com `disponivel: false` em livro atualmente disponível
- **THEN** o sistema retorna 200 OK e o livro passa a ter `disponivel: false`

#### Scenario: Atualização de ID inexistente
- **WHEN** `PUT /api/livros/999` é chamado e o ID 999 não existe
- **THEN** o sistema retorna 404 Not Found com JSON `{"erro": "Livro não encontrado: 999"}`

#### Scenario: Atualização com dados inválidos
- **WHEN** `PUT /api/livros/{id}` é chamado com campos obrigatórios inválidos
- **THEN** o sistema retorna 400 Bad Request com JSON detalhando os erros de validação

---

### Requirement: Excluir livro
O sistema SHALL excluir um livro existente via `DELETE /api/livros/{id}` e retornar 204 No Content sem corpo. Quando o ID não existir, SHALL retornar 404 Not Found.

#### Scenario: Exclusão com sucesso
- **WHEN** `DELETE /api/livros/{id}` é chamado com ID existente
- **THEN** o sistema retorna 204 No Content sem corpo na resposta

#### Scenario: Verificação pós-exclusão
- **WHEN** `DELETE /api/livros/{id}` é executado com sucesso e depois `GET /api/livros/{id}` é chamado
- **THEN** o sistema retorna 404 Not Found para o ID excluído

#### Scenario: Exclusão de ID inexistente
- **WHEN** `DELETE /api/livros/999` é chamado e o ID 999 não existe
- **THEN** o sistema retorna 404 Not Found com JSON `{"erro": "Livro não encontrado: 999"}`

---

### Requirement: Validações de campos do livro
O sistema SHALL validar os campos do livro conforme as restrições do modelo de dados. Campos obrigatórios: `titulo` (não vazio, máx 150 chars), `genero` (não vazio, máx 60 chars), `anoPublicacao` (positivo), `autorId` (não nulo). `disponivel` SHALL ter valor padrão `true` quando não informado.

#### Scenario: Titulo com mais de 150 caracteres
- **WHEN** `POST /api/livros` é chamado com `titulo` com mais de 150 caracteres
- **THEN** o sistema retorna 400 Bad Request com JSON indicando erro no campo `titulo`

#### Scenario: Genero com mais de 60 caracteres
- **WHEN** `POST /api/livros` é chamado com `genero` com mais de 60 caracteres
- **THEN** o sistema retorna 400 Bad Request com JSON indicando erro no campo `genero`

#### Scenario: Disponivel com valor padrão true
- **WHEN** `POST /api/livros` é chamado sem o campo `disponivel`
- **THEN** o livro é criado com `disponivel: true`
