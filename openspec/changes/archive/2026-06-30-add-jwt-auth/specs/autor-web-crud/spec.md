## ADDED Requirements

### Requirement: Acesso autenticado às telas de autor
As rotas de leitura (`GET /autores`) SHALL exigir apenas sessão autenticada (qualquer papel). As rotas de escrita (`GET /autores/novo`, `POST /autores`, `GET /autores/{id}/editar`, `POST /autores/{id}`, `POST /autores/{id}/excluir`) SHALL exigir sessão autenticada com papel `BIBLIOTECARIO`. Acesso sem sessão autenticada a qualquer uma dessas rotas SHALL redirecionar para `GET /login`.

#### Scenario: Leitura sem sessão autenticada
- **WHEN** um visitante sem sessão autenticada acessa `GET /autores`
- **THEN** o sistema redireciona para `GET /login`

#### Scenario: Leitura com sessão de usuário comum
- **WHEN** um usuário autenticado com papel `USUARIO` acessa `GET /autores`
- **THEN** o sistema exibe a listagem normalmente

#### Scenario: Tela de cadastro acessada por usuário comum
- **WHEN** um usuário autenticado com papel `USUARIO` acessa `GET /autores/novo`
- **THEN** o sistema bloqueia o acesso (HTTP 403 ou redirecionamento com mensagem de acesso negado), sem exibir o formulário

#### Scenario: Tela de cadastro acessada por bibliotecário
- **WHEN** um usuário autenticado com papel `BIBLIOTECARIO` acessa `GET /autores/novo`
- **THEN** o sistema exibe o formulário normalmente, conforme já especificado em `autor-web-crud`
