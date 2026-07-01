## ADDED Requirements

### Requirement: Configuração dos beans RestClient
O sistema SHALL configurar dois beans `RestClient` nomeados — um para `autor-ms` e outro para `livro-ms` — lendo as URLs base de `application.properties`.

#### Scenario: RestClient do autor-ms inicializado
- **WHEN** o `front-ms` sobe com `AUTOR_MS_URL=http://autor-ms:8082`
- **THEN** o bean `RestClient` para autor possui base URL `http://autor-ms:8082`

#### Scenario: RestClient do livro-ms inicializado
- **WHEN** o `front-ms` sobe com `LIVRO_MS_URL=http://livro-ms:8081`
- **THEN** o bean `RestClient` para livro possui base URL `http://livro-ms:8081`

#### Scenario: URLs fallback para desenvolvimento local
- **WHEN** as variáveis de ambiente não estão definidas
- **THEN** `autor.ms.url` usa `http://localhost:8082` e `livro.ms.url` usa `http://localhost:8081`

### Requirement: POJOs de domínio Autor e Livro
O sistema SHALL definir as classes `Autor` e `Livro` como POJOs simples (com getters e setters) para representar os dados recebidos das APIs downstream.

#### Scenario: Desserialização de Autor
- **WHEN** `autor-ms` retorna JSON com campos `id`, `nome`, `nacionalidade`, `anoNascimento`
- **THEN** o POJO `Autor` é populado corretamente pelo Jackson

#### Scenario: Desserialização de Livro
- **WHEN** `livro-ms` retorna JSON com campos `id`, `titulo`, `genero`, `anoPublicacao`, `disponivel`, `autorId`
- **THEN** o POJO `Livro` é populado corretamente pelo Jackson

#### Scenario: Thymeleaf binding em formulário de Autor
- **WHEN** o template usa `th:object="${autor}"` com `th:field="*{nome}"`
- **THEN** o binding funciona porque `Autor` possui getter e setter para `nome`

#### Scenario: Thymeleaf binding em formulário de Livro
- **WHEN** o template usa `th:object="${livro}"` com `th:field="*{autorId}"`
- **THEN** o binding funciona porque `Livro` possui getter e setter para `autorId`

### Requirement: Layout base com fragmentos Thymeleaf
O sistema SHALL fornecer um template `fragments/layout.html` com navbar e footer que é reutilizado em todos os templates via `th:replace` ou `th:insert`.

#### Scenario: Navbar presente em todas as páginas
- **WHEN** qualquer template da aplicação é renderizado
- **THEN** a navbar contém links para "Autores" (→ `/autores`) e "Livros" (→ `/livros`)

#### Scenario: Footer presente em todas as páginas
- **WHEN** qualquer template da aplicação é renderizado
- **THEN** o footer com informações do projeto é exibido

#### Scenario: Reutilização de fragmento
- **WHEN** o template `autores/lista.html` é processado
- **THEN** os fragmentos de navbar e footer do `layout.html` são incluídos sem duplicação de código

### Requirement: Tratamento global de erros de integração
O sistema SHALL capturar exceções de `RestClient` (`RestClientResponseException`, `ResourceAccessException`) globalmente e exibir mensagem de erro amigável sem lançar página de exceção do Spring.

#### Scenario: Serviço downstream retorna 4xx ou 5xx
- **WHEN** um controller chama a API e recebe `RestClientResponseException`
- **THEN** o sistema exibe mensagem de erro amigável na view sem stack trace visível ao usuário

#### Scenario: Serviço downstream não acessível (connection refused)
- **WHEN** um controller chama a API e recebe `ResourceAccessException`
- **THEN** o sistema exibe mensagem indicando que o serviço está temporariamente indisponível

#### Scenario: Mensagens flash de sucesso e erro
- **WHEN** uma operação de POST com sucesso ocorre
- **THEN** `redirectAttributes.addFlashAttribute("mensagem", "...")` é usado e a view exibe a mensagem após o redirect

#### Scenario: Mensagem flash visível após redirect
- **WHEN** o browser segue o redirect do PRG e carrega a página de lista
- **THEN** a mensagem flash é exibida uma única vez e desaparece no próximo reload

### Requirement: Padrão PRG obrigatório em formulários
O sistema SHALL aplicar o padrão Post-Redirect-Get em todos os formulários: após POST com sucesso, MUST redirecionar para GET, nunca retornar a view diretamente.

#### Scenario: Criação bem-sucedida redireciona para listagem
- **WHEN** `POST /autores` ou `POST /livros` retorna sucesso
- **THEN** o controller retorna `redirect:/autores` ou `redirect:/livros` (não a view diretamente)

#### Scenario: Edição bem-sucedida redireciona para listagem
- **WHEN** `POST /autores/{id}` ou `POST /livros/{id}` retorna sucesso
- **THEN** o controller retorna `redirect:/autores` ou `redirect:/livros`

#### Scenario: Exclusão bem-sucedida redireciona para listagem
- **WHEN** `POST /autores/{id}/excluir` ou `POST /livros/{id}/excluir` retorna sucesso
- **THEN** o controller retorna `redirect:/autores` ou `redirect:/livros`

#### Scenario: Erro de validação local não redireciona
- **WHEN** o controller detecta campo obrigatório vazio antes de chamar a API
- **THEN** o controller retorna a view do formulário diretamente (sem redirect) com os erros visíveis

---

### Requirement: Propagação do token JWT nas chamadas RestClient
Os beans `RestClient` configurados para `autor-ms` e `livro-ms` SHALL anexar automaticamente o header `Authorization: Bearer <token>` em toda requisição, usando o JWT da sessão HTTP do usuário autenticado atual.

#### Scenario: Chamada autenticada inclui o header Authorization
- **WHEN** um controller do `front-ms` usa o `RestClient` de `autor-ms` ou `livro-ms` dentro de uma requisição com sessão autenticada
- **THEN** a requisição HTTP enviada ao serviço downstream inclui o header `Authorization: Bearer <token>` com o JWT da sessão atual

#### Scenario: API downstream rejeita token expirado
- **WHEN** o `RestClient` envia uma chamada com um JWT expirado e a API downstream retorna HTTP 401
- **THEN** o `front-ms` trata a resposta invalidando a sessão local e redirecionando o usuário para `GET /login`, em vez de propagar uma página de erro genérica
