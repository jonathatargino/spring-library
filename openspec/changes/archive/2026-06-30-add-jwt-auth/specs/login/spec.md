## ADDED Requirements

### Requirement: Formulário de login no front-ms
`front-ms` SHALL expor um formulário de login em `GET /login` que autentica usuário e senha contra a base de usuários pré-cadastrados (papéis `BIBLIOTECARIO` e `USUARIO`). Credenciais inválidas SHALL reexibir o formulário com mensagem de erro, sem autenticar.

#### Scenario: Login bem-sucedido como bibliotecário
- **WHEN** o usuário submete `POST /login` com usuário e senha válidos de um cadastro com papel `BIBLIOTECARIO`
- **THEN** o sistema autentica a sessão e redireciona para a página inicial (ou página originalmente solicitada)

#### Scenario: Login bem-sucedido como usuário comum
- **WHEN** o usuário submete `POST /login` com usuário e senha válidos de um cadastro com papel `USUARIO`
- **THEN** o sistema autentica a sessão e redireciona para a página inicial (ou página originalmente solicitada)

#### Scenario: Credenciais inválidas
- **WHEN** o usuário submete `POST /login` com usuário inexistente ou senha incorreta
- **THEN** o sistema reexibe o formulário de login com mensagem de erro, sem criar sessão autenticada

---

### Requirement: Emissão e armazenamento do JWT na sessão
Após autenticação bem-sucedida em `front-ms`, o sistema SHALL emitir um JWT assinado (HS256, segredo compartilhado) contendo o nome do usuário e o papel (`role`), com expiração curta, e mantê-lo associado à sessão HTTP do usuário (não exposto como cookie próprio no navegador).

#### Scenario: JWT emitido com o papel correto
- **WHEN** um usuário com papel `BIBLIOTECARIO` faz login com sucesso
- **THEN** o JWT emitido e mantido na sessão contém a claim `role` com valor `BIBLIOTECARIO`

#### Scenario: JWT disponível para chamadas subsequentes
- **WHEN** um usuário autenticado navega para uma tela que requer chamar `autor-ms` ou `livro-ms`
- **THEN** o `front-ms` recupera o JWT da sessão atual para anexá-lo à chamada `RestClient`

#### Scenario: Token expira após o tempo configurado
- **WHEN** o JWT da sessão atinge sua expiração e uma chamada a `autor-ms`/`livro-ms` é feita
- **THEN** o `front-ms` recebe HTTP 401 da API downstream e invalida a sessão, redirecionando o usuário para `/login`

---

### Requirement: Logout
`front-ms` SHALL expor `POST /logout` que encerra a sessão autenticada (invalidando o JWT associado) e redireciona para `/login`.

#### Scenario: Logout encerra sessão
- **WHEN** um usuário autenticado submete `POST /logout`
- **THEN** o sistema invalida a sessão HTTP e redireciona para `GET /login`

#### Scenario: Acesso a tela protegida após logout
- **WHEN** um usuário faz logout e em seguida acessa uma rota protegida (ex.: `/autores`)
- **THEN** o sistema redireciona para `GET /login` por não haver sessão autenticada
