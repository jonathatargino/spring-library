## Why

Hoje todas as rotas de `autor-ms`, `livro-ms` e `front-ms` são públicas: qualquer pessoa pode cadastrar, editar ou excluir autores e livros, mesmo as histórias de usuário distinguindo papéis ("como bibliotecário" para escrita, "como usuário" para leitura). Não existe enforcement real desses papéis. Esta mudança introduz autenticação via JWT para que ações de escrita exijam o papel `BIBLIOTECARIO` e toda a API exija um token válido, fechando essa lacuna antes de expor o sistema além do ambiente de desenvolvimento.

## What Changes

- `front-ms` ganha uma tela de login (`/login`) que autentica contra uma base de usuários pré-cadastrados (papéis `BIBLIOTECARIO` e `USUARIO`), guarda o JWT emitido na sessão HTTP (cookie de sessão, não o token em cookie próprio) e oferece `/logout`.
- `front-ms` passa a ser o **emissor** do JWT (assina com segredo HS256 compartilhado) após autenticar o usuário; o token contém o papel do usuário (`role`) e expiração curta.
- Os beans `RestClient` do `front-ms` (para `autor-ms` e `livro-ms`) passam a anexar automaticamente o header `Authorization: Bearer <token>` da sessão atual em toda chamada.
- `autor-ms` e `livro-ms` ganham Spring Security configurado como **resource server** simétrico: um filtro valida o JWT (mesma chave HS256) em toda requisição a `/api/**`.
  - Sem token ou token inválido/expirado → HTTP 401 com `{"erro": "..."}`.
  - Token válido mas papel insuficiente para escrita (`POST`/`PUT`/`DELETE` exigem `BIBLIOTECARIO`) → HTTP 403 com `{"erro": "..."}`.
  - Leitura (`GET`) é permitida para qualquer papel autenticado (`BIBLIOTECARIO` ou `USUARIO`).
- Telas de escrita do `front-ms` (`/autores/novo`, `/autores/{id}/editar`, `/autores/{id}/excluir`, `/livros/novo`, `/livros/{id}/editar`, `/livros/{id}/excluir`) passam a exigir sessão autenticada com papel `BIBLIOTECARIO`; sem sessão válida, redireciona para `/login`.
- Telas de leitura (`/autores`, `/livros`, `/livros/{id}`) exigem apenas sessão autenticada (qualquer papel).
- **BREAKING**: endpoints de `autor-ms` e `livro-ms` deixam de ser acessíveis sem token. Qualquer cliente existente (ex.: chamadas diretas via Postman/curl sem header `Authorization`) passa a receber 401.
- **Fora de escopo**: cadastro/gestão de novos usuários via interface. Os usuários (`BIBLIOTECARIO` e `USUARIO`) são pré-cadastrados em memória/configuração no `front-ms` para esta entrega.

## Capabilities

### New Capabilities
- `jwt-validation`: filtro Spring Security em `autor-ms` e `livro-ms` que valida o JWT em toda requisição e autoriza por papel (escrita exige `BIBLIOTECARIO`).
- `login`: tela de login/logout no `front-ms`, autenticação contra usuários pré-cadastrados e emissão do JWT, mantido na sessão HTTP.

### Modified Capabilities
- `autor-crud`: todos os endpoints passam a exigir JWT válido (401 sem token/token inválido); escrita (`POST`/`PUT`/`DELETE`) exige papel `BIBLIOTECARIO` (403 caso contrário).
- `livro-crud`: mesma mudança de `autor-crud` aplicada aos endpoints de `livro-ms`.
- `front-ms-infra`: os beans `RestClient` passam a anexar o header `Authorization: Bearer <token>` lido da sessão em toda chamada a `autor-ms`/`livro-ms`.
- `autor-web-crud`: rotas de cadastro/edição/exclusão de autor exigem sessão autenticada com papel `BIBLIOTECARIO`; sem sessão, redireciona para `/login`.
- `livro-web-crud`: rotas de cadastro/edição/exclusão de livro exigem sessão autenticada com papel `BIBLIOTECARIO`; sem sessão, redireciona para `/login`.

## Impact

- **Código afetado**: `autor-ms` (nova dependência `spring-boot-starter-security` + `jjwt` ou `spring-security-oauth2-resource-server`, filtro JWT, configuração de autorização por método HTTP), `livro-ms` (idem), `front-ms` (dependência `spring-boot-starter-security` para sessão/login form, geração de JWT, interceptor/`ClientHttpRequestInterceptor` no `RestClient` para anexar o token, novos templates `login.html`).
- **Configuração**: novo segredo JWT compartilhado entre os 3 serviços (variável de ambiente/`application.properties`, ex. `jwt.secret`), propagado via `docker-compose.yml`.
- **Usuários pré-cadastrados**: lista fixa de credenciais (`BIBLIOTECARIO`/`USUARIO`) definida em configuração do `front-ms` — sem nova tabela/banco.
- **Dependências externas**: nenhuma chamada de rede nova entre serviços; a validação do JWT é local a cada serviço (chave simétrica compartilhada).
- **Compatibilidade**: clientes diretos das APIs (sem passar pelo `front-ms`) precisam passar a enviar `Authorization: Bearer <token>` — mudança de contrato (ver BREAKING acima).
