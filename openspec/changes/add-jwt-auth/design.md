## Context

O sistema é composto por 3 serviços Spring Boot (`autor-ms`, `livro-ms`, `front-ms`) atrás de um Nginx que expõe tanto `/api/livros` e `/api/autores` (diretamente, sem passar pelo `front-ms`) quanto a UI Thymeleaf em `/`. Hoje nenhum dos três tem qualquer autenticação. As HUs distinguem "bibliotecário" (escrita) de "usuário" (leitura), mas isso nunca foi imposto em código.

Como o Nginx expõe as APIs diretamente, autenticar só no `front-ms` não seria suficiente — quem chamar `autor-ms`/`livro-ms` direto pelo Nginx continuaria sem controle. Por isso a validação precisa existir nos dois microsserviços de API, e o `front-ms` precisa repassar o token nas chamadas que faz em nome do usuário logado.

## Goals / Non-Goals

**Goals:**
- Toda requisição a `/api/autores/**` e `/api/livros/**` exige um JWT válido.
- Escrita (`POST`/`PUT`/`DELETE`) nessas APIs exige papel `BIBLIOTECARIO`; leitura (`GET`) aceita qualquer papel autenticado.
- `front-ms` oferece login/logout, mantém o usuário autenticado em sessão HTTP e anexa o JWT em toda chamada `RestClient`.
- Telas de escrita do `front-ms` exigem sessão com papel `BIBLIOTECARIO`; telas de leitura exigem apenas sessão autenticada.
- Mudança é viável de implementar e revisar dentro do escopo do projeto (sem infraestrutura nova como Keycloak/Auth0).

**Non-Goals:**
- Cadastro de novos usuários via UI ou API (usuários são fixos/pré-configurados).
- Refresh token / renovação silenciosa de sessão expirada — ao expirar, o usuário simplesmente refaz login.
- Single Sign-On entre serviços além do necessário para este sistema (não há OAuth2/OpenID Connect com provedor externo).
- Auditoria/log de acessos.

## Decisions

### 1. `front-ms` é o emissor do JWT; `autor-ms`/`livro-ms` são apenas validadores
`front-ms` autentica o login (usuário/senha) e assina o token. `autor-ms` e `livro-ms` não autenticam ninguém — só verificam assinatura/expiração/claims de um token que já chegou pronto.

**Alternativas consideradas:**
- Cada microsserviço ter seu próprio login: rejeitado, duplicaria a tela de login e a lista de usuários em 3 lugares.
- Serviço de autenticação dedicado (`auth-ms`): mais correto a longo prazo, mas adiciona um quarto serviço, um quinto container e complexidade desproporcional ao escopo desta mudança. Pode ser uma evolução futura.

### 2. Chave simétrica (HS256) compartilhada via variável de ambiente
Os 3 serviços compartilham o mesmo segredo (`jwt.secret`), propagado via `docker-compose.yml` (mesma variável de ambiente nos 3 containers). `autor-ms`/`livro-ms` usam esse segredo só para verificar assinatura.

**Alternativas consideradas:**
- Par de chaves assimétrico (RS256), com `front-ms` guardando a chave privada e os outros dois só a pública: mais seguro (microsserviços nunca veem a chave que assina), mas exige gerar e distribuir um par de chaves — overhead desnecessário dado que os 3 serviços já compartilham rede interna confiável no Docker Compose e o mesmo `docker-compose.yml`/repositório controla a configuração de todos.

### 3. Usuários pré-cadastrados em memória no `front-ms`
Sem HU pedindo cadastro de usuário, e para não introduzir um banco no `front-ms` (hoje "sem banco", por decisão do `CLAUDE.md`), os usuários ficam fixos em `application.properties`/config Java (`InMemoryUserDetailsManager` do Spring Security, ou lista simples mapeada manualmente), cada um com `username`, senha (hash BCrypt) e papel (`BIBLIOTECARIO` ou `USUARIO`).

**Alternativas consideradas:**
- Tabela `usuario` em banco próprio do `front-ms`: mais realista, mas adiciona um banco PostgreSQL novo e contraria a decisão de arquitetura existente ("front-ms sem banco"); fora do escopo desta mudança.
- Delegar usuários a `autor-ms` ou `livro-ms`: misturaria autenticação com os domínios de autor/livro, sem necessidade.

### 4. JWT guardado na sessão HTTP do `front-ms`, não em cookie próprio
Após login, o token gerado fica em `HttpSession` (atributo, ex. `SecurityContext` do Spring Security via `HttpSessionSecurityContextRepository`, que já é o padrão default). O navegador só vê o cookie de sessão (`JSESSIONID`), nunca o JWT diretamente. O `RestClient` lê o token da sessão (via um `ClientHttpRequestInterceptor` que recebe o token corrente, injetado a partir do contexto de segurança da requisição) e o envia como `Authorization: Bearer <token>` para `autor-ms`/`livro-ms`.

**Alternativas consideradas:**
- Guardar o JWT em cookie próprio no browser: exporia o token ao JS/cliente sem necessidade, já que o `front-ms` é quem faz as chamadas server-side; sessão é suficiente e mais simples de integrar com o filtro de autorização padrão do Spring Security MVC.

### 5. Expiração curta + sem refresh
Token expira em ~30 minutos. Ao expirar, a próxima chamada do `front-ms` a `autor-ms`/`livro-ms` recebe 401; o `front-ms` invalida a sessão e redireciona para `/login`.

**Alternativas consideradas:**
- Refresh token: adiciona um segundo tipo de token e um endpoint de renovação — não justificado pelo escopo (ambiente de demonstração/avaliação, não produção com usuários reais navegando por horas).

### 6. Autorização por papel via `@PreAuthorize`/`HttpSecurity` baseada no verbo HTTP
Em `autor-ms` e `livro-ms`, a config de segurança autoriza por padrão de rota + método: `GET /api/**` exige autenticado (qualquer papel); `POST|PUT|DELETE /api/**` exige `hasRole("BIBLIOTECARIO")`. Evita anotar manualmente cada método de controller.

## Risks / Trade-offs

- **[Risco] Segredo JWT idêntico nos 3 serviços** → se um serviço for comprometido, o atacante pode forjar tokens válidos para os outros dois. Mitigação: segredo via variável de ambiente (nunca commitado), documentado como "trocar em produção real"; aceitável para o escopo do projeto (ambiente de avaliação/demo).
- **[Risco] Usuários fixos em config** → não há como revogar/alterar credenciais sem redeploy do `front-ms`. Mitigação: documentar como limitação conhecida (Non-Goal); evolução futura natural é uma tabela de usuários.
- **[Risco] BREAKING change nas APIs** → integrações existentes que chamam `autor-ms`/`livro-ms` direto (Postman, scripts de avaliação) passam a falhar com 401 sem o header `Authorization`. Mitigação: documentar claramente no README como obter um token (ex. endpoint de login retornando o JWT também em JSON para uso via curl/Postman, não só via sessão web).
- **[Trade-off] Sem serviço de auth dedicado** → simplicidade agora, mas se o sistema crescer (mais serviços, mais tipos de usuário), a lógica de emissão de token no `front-ms` precisará ser extraída. Aceito conscientemente dado o escopo atual.

## Migration Plan

1. Implementar e testar localmente o login + JWT no `front-ms` e a validação no `autor-ms`/`livro-ms` em uma branch, com `docker compose up --build`.
2. Adicionar a variável `JWT_SECRET` ao `docker-compose.yml` (mesmo valor nos 3 serviços) e a um `.env`/`.env.example` documentado.
3. Subir os 3 serviços já protegidos de uma vez (não há como fazer rollout gradual dado que `front-ms` depende do mesmo segredo que `autor-ms`/`livro-ms` validam) — é um corte único.
4. Rollback: reverter o commit/imagem; como não há mudança de schema de banco, `docker compose down && docker compose up --build` na versão anterior é suficiente.

## Open Questions

- O endpoint de login do `front-ms` deve também responder com o JWT em JSON puro (para clientes de API/Postman), além do fluxo de sessão web? Recomendação: sim, simplifica testes manuais das APIs protegidas — a confirmar com o usuário se necessário durante a implementação.
