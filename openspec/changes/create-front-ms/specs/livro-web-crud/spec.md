## ADDED Requirements

### Requirement: Listagem de livros com nome do autor resolvido
O sistema SHALL exibir a lista de livros em `GET /livros` com o nome do autor resolvido para cada livro, usando o template `livros/lista.html`.

#### Scenario: Exibir tabela com livros e nome do autor
- **WHEN** o usuário acessa `GET /livros`
- **THEN** o sistema chama `GET /api/livros` e, para cada livro, resolve o nome do autor via `GET /api/autores/{autorId}`, exibindo tabela com: título, gênero, anoPublicacao, disponivel (badge colorido Sim/Não), nome do autor

#### Scenario: Autor removido não quebra a listagem
- **WHEN** um livro possui `autorId` que não existe em `autor-ms` (404)
- **THEN** a coluna autor exibe "Autor removido" para esse livro, sem interromper a renderização dos demais

#### Scenario: Filtro por disponibilidade
- **WHEN** o usuário acessa `GET /livros?disponivel=true`
- **THEN** o sistema repassa `?disponivel=true` para `GET /api/livros` e exibe apenas os livros disponíveis

#### Scenario: Filtro `disponivel=false`
- **WHEN** o usuário acessa `GET /livros?disponivel=false`
- **THEN** o sistema repassa o parâmetro e exibe apenas os livros indisponíveis

#### Scenario: livro-ms indisponível
- **WHEN** o usuário acessa `GET /livros` e `livro-ms` não responde
- **THEN** o sistema exibe mensagem de erro amigável sem lançar exception não tratada

### Requirement: Formulário de cadastro de livro
O sistema SHALL exibir o formulário de criação de livro em `GET /livros/novo` com um `<select>` de autores populado.

#### Scenario: Exibir formulário com lista de autores
- **WHEN** o usuário acessa `GET /livros/novo`
- **THEN** o sistema chama `GET /api/autores` para popular o `<select>` e exibe formulário com campos: titulo, genero, anoPublicacao, disponivel (checkbox), autor (select)

#### Scenario: Cadastro de livro com dados válidos
- **WHEN** o usuário submete `POST /livros` com todos os campos válidos
- **THEN** o sistema chama `POST /api/livros` e redireciona para `GET /livros` com mensagem flash de sucesso (padrão PRG)

#### Scenario: Campos obrigatórios não preenchidos
- **WHEN** o usuário submete `POST /livros` com titulo, genero, anoPublicacao ou autorId faltando
- **THEN** o formulário é reexibido com mensagens de erro nos campos inválidos, sem chamar a API

#### Scenario: API retorna erro 400 no cadastro
- **WHEN** o sistema chama `POST /api/livros` e recebe HTTP 400
- **THEN** o formulário é reexibido com a mensagem de erro retornada pela API

#### Scenario: autor-ms indisponível ao carregar formulário de livro
- **WHEN** o usuário acessa `GET /livros/novo` e `autor-ms` não responde
- **THEN** o sistema exibe o formulário com aviso de que a lista de autores não está disponível e o submit permanece desabilitado ou com aviso

### Requirement: Formulário de edição de livro
O sistema SHALL exibir o formulário de edição de livro em `GET /livros/{id}/editar` com os dados atuais pré-preenchidos, incluindo o autor selecionado no `<select>`.

#### Scenario: Formulário pré-preenchido para edição
- **WHEN** o usuário acessa `GET /livros/{id}/editar`
- **THEN** o sistema busca o livro e a lista de autores, exibe o formulário com todos os campos preenchidos e o autor atual selecionado no `<select>`

#### Scenario: Atualização de livro com dados válidos
- **WHEN** o usuário submete `POST /livros/{id}` com dados válidos
- **THEN** o sistema chama `PUT /api/livros/{id}` e redireciona para `GET /livros` com mensagem flash de sucesso

#### Scenario: Livro não encontrado ao editar
- **WHEN** o usuário acessa `GET /livros/{id}/editar` e o livro não existe (404)
- **THEN** o sistema redireciona para `GET /livros` com mensagem de erro amigável

#### Scenario: Alteração de disponibilidade
- **WHEN** o usuário muda o checkbox de disponível e submete o formulário
- **THEN** o sistema envia o novo valor de `disponivel` no payload e o livro é atualizado corretamente

### Requirement: Página de detalhe do livro
O sistema SHALL exibir todos os dados de um livro em `GET /livros/{id}` com nome do autor resolvido, usando o template `livros/detalhe.html`.

#### Scenario: Exibir detalhe completo do livro
- **WHEN** o usuário acessa `GET /livros/{id}`
- **THEN** o sistema chama `GET /api/livros/{id}` e `GET /api/autores/{autorId}`, exibindo: titulo, genero, anoPublicacao, disponivel e nome do autor

#### Scenario: Botões de ação presentes
- **WHEN** a página de detalhe é carregada
- **THEN** existem botões "Editar" (→ `/livros/{id}/editar`) e "Voltar para a lista" (→ `/livros`)

#### Scenario: Livro não encontrado no detalhe
- **WHEN** o usuário acessa `GET /livros/{id}` e o livro não existe (404)
- **THEN** o sistema exibe página de erro amigável informando que o livro não foi encontrado

### Requirement: Exclusão de livro
O sistema SHALL excluir um livro via `POST /livros/{id}/excluir` com confirmação JavaScript que inclui o título do livro.

#### Scenario: Confirmação com título do livro
- **WHEN** o usuário clica em "Excluir" na listagem ou no detalhe do livro
- **THEN** um diálogo JavaScript de confirmação exibe o título do livro antes do envio

#### Scenario: Exclusão confirmada com sucesso
- **WHEN** o usuário confirma e o sistema envia `POST /livros/{id}/excluir`
- **THEN** o sistema chama `DELETE /api/livros/{id}` e redireciona para `GET /livros` com mensagem flash de sucesso

#### Scenario: Livro não encontrado ao excluir
- **WHEN** o sistema chama `DELETE /api/livros/{id}` e recebe HTTP 404
- **THEN** o sistema redireciona para `GET /livros` com mensagem de erro amigável
