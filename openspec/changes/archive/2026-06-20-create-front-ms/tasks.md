## 1. Infraestrutura base (front-ms-infra)

- [x] 1.1 Criar `config/RestClientConfig.java` com dois `@Bean RestClient` nomeados (`autorClient` e `livroClient`), lendo `autor.ms.url` e `livro.ms.url` de `@Value`
- [x] 1.2 Criar `model/Autor.java` como POJO com campos: `id` (Long), `nome` (String), `nacionalidade` (String), `anoNascimento` (Integer) — getters e setters obrigatórios para binding Thymeleaf
- [x] 1.3 Criar `model/Livro.java` como POJO com campos: `id` (Long), `titulo` (String), `genero` (String), `anoPublicacao` (Integer), `disponivel` (Boolean), `autorId` (Long) — getters e setters obrigatórios
- [x] 1.4 Criar `templates/fragments/layout.html` com fragmentos Thymeleaf `navbar` (links para `/autores` e `/livros`) e `footer`
- [x] 1.5 Criar `exception/GlobalErrorHandler.java` com `@ControllerAdvice` capturando `RestClientResponseException` e `ResourceAccessException`, adicionando mensagem de erro amigável ao modelo

## 2. CRUD Web de Autores (autor-web-crud)

- [x] 2.1 Criar `controller/AutorController.java` com `@Controller @RequestMapping("/autores")` injetando o bean `autorClient`
- [x] 2.2 Implementar `GET /autores` — chama `GET /api/autores`, captura exceção de integração, retorna view `autores/lista` com atributo `autores`
- [x] 2.3 Criar `templates/autores/lista.html` — tabela com colunas nome, nacionalidade, anoNascimento, e botões Editar / Excluir; botão "+ Novo Autor"; exibe `${mensagem}` e `${erro}` de flash; usa fragmentos de layout
- [x] 2.4 Implementar `GET /autores/novo` — retorna view `autores/formulario` com `Autor` vazio no modelo
- [x] 2.5 Implementar `GET /autores/{id}/editar` — chama `GET /api/autores/{id}`, retorna view `autores/formulario` com dados pré-preenchidos; redireciona com erro se 404
- [x] 2.6 Criar `templates/autores/formulario.html` — mesmo template para criar e editar; adapta título e botão com `th:if`; campos: nome, nacionalidade, anoNascimento; exibe erros de validação; usa fragmentos de layout
- [x] 2.7 Implementar `POST /autores` — valida campos obrigatórios localmente; chama `POST /api/autores`; em sucesso redireciona para `/autores` com flash; em erro reexibe formulário (PRG)
- [x] 2.8 Implementar `POST /autores/{id}` — valida campos; chama `PUT /api/autores/{id}`; redireciona para `/autores` com flash de sucesso ou reexibe formulário com erro (PRG)
- [x] 2.9 Implementar `POST /autores/{id}/excluir` — chama `DELETE /api/autores/{id}`; redireciona para `/autores` com flash de sucesso ou erro (PRG)
- [x] 2.10 Adicionar confirmação JavaScript (`confirm(...)`) no botão Excluir do `lista.html`

## 3. CRUD Web de Livros (livro-web-crud)

- [x] 3.1 Criar `controller/LivroController.java` com `@Controller @RequestMapping("/livros")` injetando `livroClient` e `autorClient`
- [x] 3.2 Implementar `GET /livros` (com parâmetro opcional `?disponivel=`) — chama `GET /api/livros`, para cada livro resolve nome do autor via `GET /api/autores/{autorId}` (exibe "Autor removido" em caso de 404); retorna view `livros/lista`
- [x] 3.3 Criar `templates/livros/lista.html` — tabela com colunas titulo, genero, anoPublicacao, disponivel (badge colorido Sim/Não), nome do autor; botões Detalhe / Editar / Excluir; controles de filtro por disponibilidade; exibe mensagens flash; usa fragmentos de layout
- [x] 3.4 Implementar `GET /livros/{id}` — chama `GET /api/livros/{id}` e `GET /api/autores/{autorId}`; retorna view `livros/detalhe`; exibe erro amigável se livro não encontrado (404)
- [x] 3.5 Criar `templates/livros/detalhe.html` — exibe todos os campos do livro e nome do autor; botões "Editar" e "Voltar para a lista"; usa fragmentos de layout
- [x] 3.6 Implementar `GET /livros/novo` — chama `GET /api/autores` para popular lista; retorna view `livros/formulario` com `Livro` vazio e lista de autores
- [x] 3.7 Implementar `GET /livros/{id}/editar` — chama `GET /api/livros/{id}` e `GET /api/autores`; retorna view `livros/formulario` com dados pré-preenchidos; redireciona com erro se 404
- [x] 3.8 Criar `templates/livros/formulario.html` — campos: titulo, genero, anoPublicacao, disponivel (checkbox), autorId (`<select>` com autores); autor pré-selecionado na edição; adapta título/botão; exibe erros; usa fragmentos de layout
- [x] 3.9 Implementar `POST /livros` — valida campos obrigatórios (titulo, genero, anoPublicacao, autorId); chama `POST /api/livros`; redireciona para `/livros` com flash (PRG)
- [x] 3.10 Implementar `POST /livros/{id}` — valida campos; chama `PUT /api/livros/{id}`; redireciona para `/livros` com flash de sucesso (PRG)
- [x] 3.11 Implementar `POST /livros/{id}/excluir` — chama `DELETE /api/livros/{id}`; redireciona para `/livros` com flash (PRG)
- [x] 3.12 Adicionar confirmação JavaScript com o título do livro no botão Excluir do `lista.html` e `detalhe.html`

## 4. Validação e integração final

- [x] 4.1 Verificar que todos os formulários aplicam validação local antes de chamar a API (campos obrigatórios não vazios, anoPublicacao positivo)
- [x] 4.2 Verificar tratamento de erro no `GlobalErrorHandler`: falha de `RestClient` não deve resultar em página de erro do Spring em nenhuma rota
- [x] 4.3 Testar localmente com `mvn spring-boot:run` com `autor-ms` e `livro-ms` rodando (verificar todas as rotas: listagem, criação, edição, detalhe, exclusão)
- [x] 4.4 Executar `docker compose up --build` e validar que o sistema completo sobe sem erros e está acessível em `http://localhost`
- [x] 4.5 Verificar que `http://localhost/api/autores`, `http://localhost/api/livros` e `http://localhost/` roteiam corretamente via Nginx (HU-21)
