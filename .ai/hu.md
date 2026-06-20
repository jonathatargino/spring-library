## 3. Histórias de Usuário

As histórias estão organizadas por serviço. Cada uma especifica o critério de aceitação que será verificado na apresentação.

### 3.1. Histórias do autor-ms

#### HU-01: Listar autores

- **Como** usuário do sistema,
- **quero** ver a lista de todos os autores cadastrados,
- **para** conhecer o acervo de autores disponíveis.
- **Endpoint:** `GET /api/autores`
- **Resposta:** 200 OK com array JSON de autores.
- **Critérios de aceitação:**
  - Retorna lista vazia (`[]`) quando não há autores.
  - Retorna todos os campos: id, nome, nacionalidade, anoNascimento.
  - Código HTTP 200 em qualquer caso.

#### HU-02: Buscar autor por ID

- **Como** front-ms,
- **quero** buscar um autor pelo seu ID,
- **para** exibir o nome do autor junto aos dados do livro.
- **Endpoint:** `GET /api/autores/{id}`
- **Resposta de sucesso:** 200 OK com objeto JSON do autor.
- **Resposta de erro:** 404 Not Found quando o ID não existe.
- **Critérios de aceitação:**
  - `GET /api/autores/1` retorna os dados do autor de ID 1.
  - `GET /api/autores/999` retorna 404 com mensagem de erro no corpo: `{"erro": "Autor não encontrado: 999"}`.
  - Corpo da resposta é JSON válido em ambos os casos.

#### HU-03: Cadastrar autor

- **Como** bibliotecário,
- **quero** cadastrar um novo autor,
- **para** que ele possa ser vinculado a livros.
- **Endpoint:** `POST /api/autores`
- **Corpo:** JSON com nome, nacionalidade, anoNascimento.
- **Resposta de sucesso:** 201 Created com o autor criado (incluindo id gerado).
- **Resposta de erro:** 400 Bad Request com lista de erros de validação.
- **Critérios de aceitação:**
  - Campos obrigatórios: nome (não vazio), nacionalidade (não vazio), anoNascimento (positivo).
  - Corpo vazio ou com campos inválidos retorna 400 com objeto JSON listando os campos com erro.
  - Autor criado com sucesso retorna 201 com o id gerado no corpo.
  - Autor persiste no banco após reinicialização do container (volume Docker configurado).

#### HU-04: Atualizar autor

- **Como** bibliotecário,
- **quero** corrigir os dados de um autor já cadastrado,
- **para** manter o acervo atualizado.
- **Endpoint:** `PUT /api/autores/{id}`
- **Corpo:** JSON com os campos a atualizar.
- **Resposta de sucesso:** 200 OK com o autor atualizado.
- **Resposta de erro:** 404 se o ID não existir; 400 se os dados forem inválidos.
- **Critérios de aceitação:**
  - Todos os campos são substituídos (PUT completo).
  - As mesmas validações do cadastro se aplicam.
  - `PUT /api/autores/999` retorna 404.

#### HU-05: Excluir autor

- **Como** bibliotecário,
- **quero** remover um autor do sistema,
- **para** manter o cadastro limpo.
- **Endpoint:** `DELETE /api/autores/{id}`
- **Resposta de sucesso:** 204 No Content.
- **Resposta de erro:** 404 se o ID não existir.
- **Critérios de aceitação:**
  - Após o DELETE, `GET /api/autores/{id}` retorna 404.
  - DELETE em ID inexistente retorna 404.
  - Resposta de sucesso não possui corpo.

---

### 3.2. Histórias do livro-ms

#### HU-06: Listar livros

- **Como** usuário do sistema,
- **quero** ver a lista de todos os livros cadastrados,
- **para** navegar pelo acervo da biblioteca.
- **Endpoint:** `GET /api/livros`
- **Resposta:** 200 OK com array JSON de livros.
- **Critérios de aceitação:**
  - Retorna todos os campos: id, titulo, genero, anoPublicacao, disponivel, autorId.
  - Retorna lista vazia (`[]`) quando não há livros.
  - Suporta filtro opcional por disponibilidade: `/api/livros?disponivel=true` retorna apenas livros disponíveis.

#### HU-07: Buscar livro por ID

- **Como** front-ms,
- **quero** buscar um livro pelo seu ID,
- **para** exibir seus detalhes na página de detalhe.
- **Endpoint:** `GET /api/livros/{id}`
- **Resposta de sucesso:** 200 OK com objeto JSON do livro.
- **Resposta de erro:** 404 Not Found.
- **Critérios de aceitação:**
  - Retorna todos os campos incluindo autorId.
  - ID inexistente retorna 404 com mensagem JSON.

#### HU-08: Cadastrar livro

- **Como** bibliotecário,
- **quero** cadastrar um novo livro informando seu autor,
- **para** ampliar o acervo da biblioteca.
- **Endpoint:** `POST /api/livros`
- **Corpo:** JSON com titulo, genero, anoPublicacao, disponivel, autorId.
- **Resposta de sucesso:** 201 Created com o livro criado.
- **Resposta de erro:** 400 Bad Request.
- **Critérios de aceitação:**
  - Campos obrigatórios: titulo (não vazio, máx 150 chars), genero (não vazio), anoPublicacao (positivo), autorId (não nulo).
  - `livro-ms` não valida se o `autorId` existe em `autor-ms`; essa validação é feita pelo `front-ms` antes de enviar a requisição.
  - Livro criado com sucesso retorna 201 com o id gerado.

#### HU-09: Atualizar livro

- **Como** bibliotecário,
- **quero** editar os dados de um livro cadastrado,
- **para** corrigir informações ou atualizar disponibilidade.
- **Endpoint:** `PUT /api/livros/{id}`
- **Corpo:** JSON com todos os campos.
- **Resposta:** 200 OK com livro atualizado ou 404/400.
- **Critérios de aceitação:**
  - Todos os campos são substituídos (PUT completo).
  - As mesmas validações do cadastro se aplicam.
  - É possível alterar disponivel de true para false e vice-versa.

#### HU-10: Excluir livro

- **Como** bibliotecário,
- **quero** remover um livro do acervo,
- **para** manter o catálogo atualizado.
- **Endpoint:** `DELETE /api/livros/{id}`
- **Resposta:** 204 No Content ou 404.
- **Critérios de aceitação:**
  - Após o DELETE, `GET /api/livros/{id}` retorna 404.
  - Resposta de sucesso sem corpo.

#### HU-11: Filtrar livros por disponibilidade

- **Como** usuário,
- **quero** filtrar os livros por disponibilidade,
- **para** ver apenas os livros que posso emprestar agora.
- **Endpoint:** `GET /api/livros?disponivel=true`
- **Resposta:** 200 OK com lista filtrada.
- **Critérios de aceitação:**
  - Sem o parâmetro, retorna todos os livros.
  - Com `?disponivel=true`, retorna apenas livros com disponivel true.
  - Com `?disponivel=false`, retorna apenas indisponíveis.

---

### 3.3. Histórias do front-ms

#### HU-12: Tela de listagem de autores

- **Como** usuário,
- **quero** ver a lista de autores em uma página web,
- **para** navegar pelo cadastro de autores.
- **Rota:** `GET /autores`
- **Template:** `autores/lista.html`
- **Critérios de aceitação:**
  - Tabela exibe: nome, nacionalidade, ano de nascimento e botões de ação.
  - Usa o layout base com navbar e footer (fragmentos Thymeleaf).
  - Botão "+ Novo Autor" leva ao formulário de cadastro.
  - Se `autor-ms` estiver indisponível, exibe mensagem de erro amigável em vez de página de exceção.

#### HU-13: Formulário de cadastro e edição de autor

- **Como** bibliotecário,
- **quero** cadastrar e editar autores por uma interface web,
- **para** não precisar usar ferramentas de API diretamente.
- **Rotas:** `GET /autores/novo` e `GET /autores/{id}/editar`
- **Submit:** `POST /autores` e `POST /autores/{id}`
- **Template:** `autores/formulario.html`
- **Critérios de aceitação:**
  - Mesmo template para criar e editar (botão muda de "Salvar" para "Atualizar").
  - Validação no `front-ms` antes de chamar a API: campos obrigatórios não podem ser vazios.
  - Mensagem flash de sucesso após salvar ou atualizar.
  - Erros retornados pela API (400) são exibidos no formulário.
  - Padrão PRG aplicado: após POST com sucesso, redireciona para `/autores`.

#### HU-14: Excluir autor pela interface web

- **Como** bibliotecário,
- **quero** excluir um autor com confirmação,
- **para** evitar exclusões acidentais.
- **Rota:** `POST /autores/{id}/excluir`
- **Critérios de aceitação:**
  - Diálogo de confirmação JavaScript antes do envio.
  - Após exclusão com sucesso, redireciona para `/autores` com mensagem flash.
  - Se a API retornar 404, exibe mensagem de erro na listagem.

#### HU-15: Tela de listagem de livros com nome do autor

- **Como** usuário,
- **quero** ver a lista de livros com o nome do autor resolvido,
- **para** não ver apenas um número de ID na coluna autor.
- **Rota:** `GET /livros`
- **Template:** `livros/lista.html`
- **Critérios de aceitação:**
  - `front-ms` chama `GET /api/livros` e, para cada livro, resolve o nome do autor via `GET /api/autores/{autorId}`.
  - Tabela exibe: título, gênero, ano, disponível (Sim/Não com badge colorido) e nome do autor.
  - Se o autor de um livro não for encontrado em `autor-ms`, exibe "Autor removido" na coluna, sem quebrar a página.
  - Filtro de disponibilidade via `?disponivel=true/false` passado adiante para `livro-ms`.

#### HU-16: Formulário de cadastro e edição de livro

- **Como** bibliotecário,
- **quero** cadastrar e editar livros escolhendo o autor em um `<select>`,
- **para** vincular corretamente o livro ao seu autor.
- **Rotas:** `GET /livros/novo` e `GET /livros/{id}/editar`
- **Submit:** `POST /livros` e `POST /livros/{id}`
- **Template:** `livros/formulario.html`
- **Critérios de aceitação:**
  - O `front-ms` carrega a lista de autores chamando `GET /api/autores` para popular o `<select>`.
  - Na edição, o autor atual do livro aparece pré-selecionado.
  - Validação local: título, gênero, ano e autor são obrigatórios.
  - Erros retornados pela API são exibidos no formulário.
  - Padrão PRG aplicado.

#### HU-17: Página de detalhe do livro

- **Como** usuário,
- **quero** ver todos os detalhes de um livro em uma página dedicada,
- **para** obter informações completas antes de solicitá-lo.
- **Rota:** `GET /livros/{id}`
- **Template:** `livros/detalhe.html`
- **Critérios de aceitação:**
  - Exibe todos os campos do livro.
  - O nome do autor é resolvido via chamada a `GET /api/autores/{autorId}`.
  - Botões "Editar" e "Voltar para a lista" presentes.
  - Se o livro não existir, exibe página de erro amigável.

#### HU-18: Excluir livro pela interface web

- **Como** bibliotecário,
- **quero** excluir um livro com confirmação,
- **para** removê-lo do catálogo com segurança.
- **Rota:** `POST /livros/{id}/excluir`
- **Critérios de aceitação:**
  - Diálogo de confirmação JavaScript com o título do livro.
  - Redireciona para `/livros` com mensagem flash após sucesso.

---

### 3.4. Histórias de Infraestrutura

#### HU-19: Subir todo o sistema com um único comando

- **Como** avaliador,
- **quero** subir toda a aplicação com um único comando,
- **para** verificar o funcionamento sem configuração manual.
- **Comando esperado:** `docker compose up --build`
- **Critérios de aceitação:**
  - Todos os containers sobem sem erro após o comando.
  - O sistema está acessível em `http://localhost` após a inicialização.
  - Os bancos são criados e populados automaticamente na primeira execução.
  - `docker compose down` derruba tudo sem erros.

#### HU-20: Persistência de dados entre reinicializações

- **Como** usuário,
- **quero** que os dados cadastrados não sejam perdidos ao reiniciar os containers,
- **para** não precisar recadastrar tudo a cada execução.
- **Critérios de aceitação:**
  - Os volumes Docker dos dois bancos PostgreSQL estão declarados no `docker-compose.yml`.
  - Dados cadastrados sobrevivem a `docker compose restart`.
  - `docker compose down -v` remove os volumes (comportamento documentado no README).

#### HU-21: Roteamento pelo Nginx

- **Como** desenvolvedor,
- **quero** que o Nginx roteie as requisições corretamente,
- **para** que o browser acesse tudo por uma única porta.
- **Critérios de aceitação:**
  - `http://localhost/` serve o `front-ms`.
  - `http://localhost/api/livros` roteia para `livro-ms`.
  - `http://localhost/api/autores` roteia para `autor-ms`.
  - Requisições para rotas inexistentes retornam 404 do Nginx.
  - O `front-ms` chama os microsserviços pelos nomes de serviço Docker (`livro-ms:8081`, `autor-ms:8082`), não por localhost.

---
