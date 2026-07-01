## 1. Configuração compartilhada

- [x] 1.1 Definir o segredo JWT (`jwt.secret`) como variável de ambiente (`JWT_SECRET`) e documentar em `.env.example`
- [x] 1.2 Adicionar `JWT_SECRET` (mesmo valor) às definições dos três serviços em `docker-compose.yml`
- [x] 1.3 Definir constantes/claims comuns (nome da claim de papel, valores `BIBLIOTECARIO`/`USUARIO`, tempo de expiração ~30min) documentadas em cada serviço

## 2. autor-ms — validação de JWT

- [x] 2.1 Adicionar dependências `spring-boot-starter-security` e `spring-security-oauth2-resource-server` (ou `jjwt`) ao `pom.xml`
- [x] 2.2 Configurar `SecurityFilterChain` em `autor-ms`: exigir autenticação em `/api/**`, `GET` liberado para qualquer papel autenticado, `POST`/`PUT`/`DELETE` exigindo `hasRole("BIBLIOTECARIO")`
- [x] 2.3 Configurar o decoder/validador JWT com a chave simétrica (`JWT_SECRET`) lida de `application.properties`
- [x] 2.4 Implementar `AuthenticationEntryPoint` e `AccessDeniedHandler` customizados retornando `{"erro": "..."}` em JSON (401 e 403, respectivamente), em vez do padrão HTML do Spring Security
- [x] 2.5 Testar manualmente: requisição sem token (401), com papel insuficiente em escrita (403), com papel correto (sucesso)

## 3. livro-ms — validação de JWT

- [x] 3.1 Adicionar as mesmas dependências de segurança ao `pom.xml` de `livro-ms`
- [x] 3.2 Configurar `SecurityFilterChain` em `livro-ms` com as mesmas regras de `autor-ms` (leitura aberta a autenticados, escrita restrita a `BIBLIOTECARIO`)
- [x] 3.3 Configurar o decoder/validador JWT com a mesma chave simétrica (`JWT_SECRET`)
- [x] 3.4 Reaproveitar/replicar o `AuthenticationEntryPoint`/`AccessDeniedHandler` customizados para o padrão `{"erro": "..."}`
- [x] 3.5 Testar manualmente: requisição sem token (401), com papel insuficiente em escrita (403), com papel correto (sucesso)

## 4. front-ms — login e sessão

- [x] 4.1 Adicionar dependência `spring-boot-starter-security` ao `pom.xml` de `front-ms`
- [x] 4.2 Definir os usuários pré-cadastrados (username, senha com hash BCrypt, papel) em configuração Java (`UserDetailsService`/`InMemoryUserDetailsManager`) — ao menos um `BIBLIOTECARIO` e um `USUARIO`
- [x] 4.3 Configurar `SecurityFilterChain` do `front-ms`: `formLogin` apontando para `GET /login`, `logout` em `POST /logout`, liberar acesso público apenas a `/login` e recursos estáticos
- [x] 4.4 Criar template `templates/login.html` (formulário usuário/senha, exibição de erro de credenciais inválidas) usando o layout base
- [x] 4.5 Implementar componente que gera o JWT (claims `sub`, `role`, `exp`) no momento do login bem-sucedido e o associa à sessão HTTP (ex.: bean de sessão/escopo de requisição lido a partir do `Authentication` corrente)
- [x] 4.6 Verificar que o logout invalida a sessão e redireciona para `/login`

## 5. front-ms — propagação do JWT e tratamento de expiração

- [x] 5.1 Implementar um `ClientHttpRequestInterceptor` que lê o JWT da sessão/contexto de segurança atual e anexa `Authorization: Bearer <token>` em toda requisição dos beans `RestClient` de `autor-ms` e `livro-ms`
- [x] 5.2 Registrar o interceptor nos dois beans `RestClient` existentes (`config/`)
- [x] 5.3 Tratar resposta 401 vinda de `autor-ms`/`livro-ms` no `front-ms`: invalidar a sessão local e redirecionar para `/login` em vez de exibir página de erro genérica
- [x] 5.4 Tratar resposta 403 vinda de `autor-ms`/`livro-ms` no `front-ms`: exibir mensagem amigável de acesso negado (cobre o caso de um `USUARIO` tentar forçar uma rota de escrita)

## 6. front-ms — proteção das telas por papel

- [x] 6.1 Restringir, na configuração de segurança, as rotas de escrita de autor (`/autores/novo`, `POST /autores`, `/autores/{id}/editar`, `POST /autores/{id}`, `POST /autores/{id}/excluir`) ao papel `BIBLIOTECARIO`
- [x] 6.2 Restringir as rotas de escrita de livro (`/livros/novo`, `POST /livros`, `/livros/{id}/editar`, `POST /livros/{id}`, `POST /livros/{id}/excluir`) ao papel `BIBLIOTECARIO`
- [x] 6.3 Garantir que rotas de leitura (`/autores`, `/livros`, `/livros/{id}`) exigem apenas sessão autenticada, sem restrição de papel
- [x] 6.4 Ajustar a navbar (`fragments/layout.html`) para exibir/ocultar links de "Novo Autor"/"Novo Livro" conforme o papel do usuário logado, e exibir usuário atual + botão de logout
- [x] 6.5 Testar manualmente com os dois papéis: `USUARIO` não vê/n consegue acessar telas de escrita; `BIBLIOTECARIO` acessa tudo

## 7. Documentação e verificação end-to-end

- [x] 7.1 Atualizar `README`/`.env.example` explicando como configurar `JWT_SECRET` e quais são os usuários pré-cadastrados de teste
- [x] 7.2 Documentar como obter um token via login para uso direto nas APIs (curl/Postman) durante a avaliação
- [x] 7.3 Rodar `docker compose up --build` e validar o fluxo completo: login como `USUARIO` (só leitura), logout, login como `BIBLIOTECARIO` (CRUD completo), chamadas diretas a `/api/autores`/`/api/livros` via Nginx sem token (401) e com token (200/201/204 conforme o caso)
