## ADDED Requirements

### Requirement: Acesso autenticado às telas de livro
As rotas de leitura (`GET /livros`, `GET /livros/{id}`) SHALL exigir apenas sessão autenticada (qualquer papel). As rotas de escrita (`GET /livros/novo`, `POST /livros`, `GET /livros/{id}/editar`, `POST /livros/{id}`, `POST /livros/{id}/excluir`) SHALL exigir sessão autenticada com papel `BIBLIOTECARIO`. Acesso sem sessão autenticada a qualquer uma dessas rotas SHALL redirecionar para `GET /login`.

#### Scenario: Leitura sem sessão autenticada
- **WHEN** um visitante sem sessão autenticada acessa `GET /livros` ou `GET /livros/{id}`
- **THEN** o sistema redireciona para `GET /login`

#### Scenario: Leitura com sessão de usuário comum
- **WHEN** um usuário autenticado com papel `USUARIO` acessa `GET /livros` ou `GET /livros/{id}`
- **THEN** o sistema exibe a listagem ou o detalhe normalmente

#### Scenario: Tela de cadastro acessada por usuário comum
- **WHEN** um usuário autenticado com papel `USUARIO` acessa `GET /livros/novo`
- **THEN** o sistema bloqueia o acesso (HTTP 403 ou redirecionamento com mensagem de acesso negado), sem exibir o formulário

#### Scenario: Exclusão acessada por bibliotecário
- **WHEN** um usuário autenticado com papel `BIBLIOTECARIO` submete `POST /livros/{id}/excluir`
- **THEN** o sistema processa a exclusão normalmente, conforme já especificado em `livro-web-crud`
