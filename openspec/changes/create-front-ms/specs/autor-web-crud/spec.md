## ADDED Requirements

### Requirement: Listagem de autores
O sistema SHALL exibir uma tabela com todos os autores cadastrados na rota `GET /autores`, renderizando o template `autores/lista.html` com o layout base.

#### Scenario: Exibir tabela com autores cadastrados
- **WHEN** o usuário acessa `GET /autores`
- **THEN** o sistema chama `GET /api/autores` no `autor-ms` e renderiza uma tabela com colunas: nome, nacionalidade, ano de nascimento, e botões de ação (editar, excluir)

#### Scenario: Lista vazia
- **WHEN** o usuário acessa `GET /autores` e não há autores cadastrados
- **THEN** o sistema exibe mensagem indicando que não há autores, sem erro

#### Scenario: autor-ms indisponível
- **WHEN** o usuário acessa `GET /autores` e `autor-ms` não responde
- **THEN** o sistema exibe mensagem de erro amigável na própria página, sem lançar exception não tratada

#### Scenario: Botão de novo autor presente
- **WHEN** a página de listagem é carregada
- **THEN** existe um botão ou link "+ Novo Autor" que leva a `GET /autores/novo`

### Requirement: Formulário de cadastro de autor
O sistema SHALL exibir o formulário de criação de autor em `GET /autores/novo` usando o template `autores/formulario.html`.

#### Scenario: Exibir formulário vazio para criação
- **WHEN** o usuário acessa `GET /autores/novo`
- **THEN** o sistema exibe um formulário com campos: nome, nacionalidade, anoNascimento, e botão "Salvar"

#### Scenario: Cadastro com dados válidos
- **WHEN** o usuário submete `POST /autores` com nome, nacionalidade e anoNascimento preenchidos e válidos
- **THEN** o sistema chama `POST /api/autores`, e redireciona para `GET /autores` com mensagem flash de sucesso

#### Scenario: Cadastro com campos obrigatórios vazios
- **WHEN** o usuário submete `POST /autores` com algum campo obrigatório vazio
- **THEN** o sistema reexibe o formulário com mensagens de erro nos campos inválidos, sem chamar a API

#### Scenario: API retorna erro 400
- **WHEN** o sistema chama `POST /api/autores` e recebe HTTP 400
- **THEN** o formulário é reexibido com a mensagem de erro retornada pela API

### Requirement: Formulário de edição de autor
O sistema SHALL exibir o formulário de edição de autor em `GET /autores/{id}/editar` com os dados atuais pré-preenchidos.

#### Scenario: Exibir formulário pré-preenchido para edição
- **WHEN** o usuário acessa `GET /autores/{id}/editar`
- **THEN** o sistema chama `GET /api/autores/{id}` e exibe o formulário com os dados atuais preenchidos e botão "Atualizar"

#### Scenario: Atualização com dados válidos
- **WHEN** o usuário submete `POST /autores/{id}` com dados válidos
- **THEN** o sistema chama `PUT /api/autores/{id}`, e redireciona para `GET /autores` com mensagem flash de sucesso

#### Scenario: Autor não encontrado ao editar
- **WHEN** o usuário acessa `GET /autores/{id}/editar` e o autor não existe (API retorna 404)
- **THEN** o sistema redireciona para `GET /autores` com mensagem de erro amigável

#### Scenario: Mesmo template para criar e editar
- **WHEN** o template `autores/formulario.html` é renderizado para criação
- **THEN** o título e botão de submit exibem "Novo Autor" / "Salvar"
- **WHEN** o template é renderizado para edição
- **THEN** o título e botão de submit exibem "Editar Autor" / "Atualizar"

### Requirement: Exclusão de autor
O sistema SHALL excluir um autor via `POST /autores/{id}/excluir` com confirmação JavaScript prévia.

#### Scenario: Confirmação antes de excluir
- **WHEN** o usuário clica em "Excluir" na listagem
- **THEN** um diálogo JavaScript de confirmação é exibido antes do envio do formulário

#### Scenario: Exclusão confirmada com sucesso
- **WHEN** o usuário confirma a exclusão e o sistema envia `POST /autores/{id}/excluir`
- **THEN** o sistema chama `DELETE /api/autores/{id}` e redireciona para `GET /autores` com mensagem flash de sucesso

#### Scenario: Autor não encontrado ao excluir
- **WHEN** o sistema chama `DELETE /api/autores/{id}` e recebe HTTP 404
- **THEN** o sistema redireciona para `GET /autores` com mensagem de erro amigável
