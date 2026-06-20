## ADDED Requirements

### Requirement: Listar todos os autores
O sistema SHALL expor o endpoint `GET /api/autores` que retorna a lista completa de autores cadastrados em formato JSON com status HTTP 200.

#### Scenario: Lista com autores cadastrados
- **WHEN** existe ao menos um autor no banco e a requisição `GET /api/autores` é feita
- **THEN** o sistema retorna HTTP 200 com array JSON contendo todos os autores, cada um com os campos `id`, `nome`, `nacionalidade` e `anoNascimento`

#### Scenario: Lista vazia
- **WHEN** não há autores cadastrados e a requisição `GET /api/autores` é feita
- **THEN** o sistema retorna HTTP 200 com array JSON vazio (`[]`)

---

### Requirement: Buscar autor por ID
O sistema SHALL expor o endpoint `GET /api/autores/{id}` que retorna um único autor pelo seu ID com HTTP 200, ou HTTP 404 com corpo JSON `{"erro": "Autor não encontrado: {id}"}` quando o ID não existir.

#### Scenario: Autor encontrado
- **WHEN** existe um autor com o ID informado e a requisição `GET /api/autores/{id}` é feita
- **THEN** o sistema retorna HTTP 200 com objeto JSON contendo `id`, `nome`, `nacionalidade` e `anoNascimento`

#### Scenario: Autor não encontrado
- **WHEN** não existe autor com o ID informado e a requisição `GET /api/autores/999` é feita
- **THEN** o sistema retorna HTTP 404 com corpo JSON `{"erro": "Autor não encontrado: 999"}`

---

### Requirement: Cadastrar autor
O sistema SHALL expor o endpoint `POST /api/autores` que recebe um JSON com `nome`, `nacionalidade` e `anoNascimento`, persiste o autor no banco e retorna HTTP 201 com o objeto criado incluindo o `id` gerado. Campos inválidos ou ausentes devem resultar em HTTP 400 com lista de erros de validação.

#### Scenario: Cadastro com dados válidos
- **WHEN** a requisição `POST /api/autores` é feita com JSON `{"nome": "José Saramago", "nacionalidade": "Português", "anoNascimento": 1922}`
- **THEN** o sistema retorna HTTP 201 com o objeto autor incluindo o `id` gerado pelo banco

#### Scenario: Cadastro com nome vazio
- **WHEN** a requisição `POST /api/autores` é feita com JSON `{"nome": "", "nacionalidade": "Português", "anoNascimento": 1922}`
- **THEN** o sistema retorna HTTP 400 com JSON descrevendo o erro de validação no campo `nome`

#### Scenario: Cadastro com anoNascimento negativo
- **WHEN** a requisição `POST /api/autores` é feita com JSON `{"nome": "Autor", "nacionalidade": "Brasileiro", "anoNascimento": -100}`
- **THEN** o sistema retorna HTTP 400 com JSON descrevendo o erro de validação no campo `anoNascimento`

#### Scenario: Cadastro com corpo vazio
- **WHEN** a requisição `POST /api/autores` é feita com corpo vazio ou sem campos obrigatórios
- **THEN** o sistema retorna HTTP 400 com JSON listando todos os campos com erro de validação

#### Scenario: Persistência entre reinicializações
- **WHEN** um autor é cadastrado com sucesso e o container `autor-ms` é reiniciado
- **THEN** o autor continua acessível via `GET /api/autores/{id}` após a reinicialização

---

### Requirement: Atualizar autor
O sistema SHALL expor o endpoint `PUT /api/autores/{id}` que substitui completamente os dados de um autor existente e retorna HTTP 200 com o autor atualizado. Retorna HTTP 404 se o ID não existir e HTTP 400 se os dados forem inválidos.

#### Scenario: Atualização com dados válidos
- **WHEN** existe um autor com o ID informado e a requisição `PUT /api/autores/{id}` é feita com JSON válido contendo `nome`, `nacionalidade` e `anoNascimento`
- **THEN** o sistema retorna HTTP 200 com o objeto autor com os dados atualizados

#### Scenario: Atualização de autor inexistente
- **WHEN** não existe autor com o ID informado e a requisição `PUT /api/autores/999` é feita
- **THEN** o sistema retorna HTTP 404 com corpo JSON `{"erro": "Autor não encontrado: 999"}`

#### Scenario: Atualização com dados inválidos
- **WHEN** existe um autor com o ID informado e a requisição `PUT /api/autores/{id}` é feita com campos inválidos (ex: `nome` vazio)
- **THEN** o sistema retorna HTTP 400 com JSON descrevendo os erros de validação

#### Scenario: PUT é substituição completa
- **WHEN** a requisição `PUT /api/autores/{id}` é feita com todos os campos
- **THEN** todos os campos do autor são substituídos pelos valores enviados na requisição

---

### Requirement: Excluir autor
O sistema SHALL expor o endpoint `DELETE /api/autores/{id}` que remove o autor do banco e retorna HTTP 204 sem corpo. Retorna HTTP 404 se o ID não existir.

#### Scenario: Exclusão de autor existente
- **WHEN** existe um autor com o ID informado e a requisição `DELETE /api/autores/{id}` é feita
- **THEN** o sistema retorna HTTP 204 sem corpo na resposta

#### Scenario: Autor removido não é mais acessível
- **WHEN** um autor foi excluído com sucesso e a requisição `GET /api/autores/{id}` é feita com o mesmo ID
- **THEN** o sistema retorna HTTP 404

#### Scenario: Exclusão de autor inexistente
- **WHEN** não existe autor com o ID informado e a requisição `DELETE /api/autores/999` é feita
- **THEN** o sistema retorna HTTP 404 com corpo JSON `{"erro": "Autor não encontrado: 999"}`

---

### Requirement: Validação de campos obrigatórios
O sistema SHALL aplicar as seguintes regras de validação em `POST` e `PUT`:
- `nome`: obrigatório, não pode ser vazio ou nulo, máximo 100 caracteres
- `nacionalidade`: obrigatório, não pode ser vazio ou nulo, máximo 80 caracteres
- `anoNascimento`: obrigatório, deve ser um número inteiro positivo (maior que zero)

#### Scenario: Todos os campos obrigatórios ausentes
- **WHEN** a requisição é feita sem nenhum campo
- **THEN** o sistema retorna HTTP 400 com objeto JSON contendo os erros de validação de cada campo obrigatório

#### Scenario: Campo nome com mais de 100 caracteres
- **WHEN** a requisição é feita com `nome` contendo mais de 100 caracteres
- **THEN** o sistema retorna HTTP 400 com erro de validação no campo `nome`
