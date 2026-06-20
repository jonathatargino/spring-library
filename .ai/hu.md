## 3. Histórias de Usuário

As histórias estão organizadas por serviço. Cada uma especifica o critério de aceitação que será verificado na apresentação[cite: 58, 59].

### 3.1. Histórias do autor-ms

#### HU-01: Listar autores

- **Como** usuário do sistema[cite: 62],
- **quero** ver a lista de todos os autores cadastrados[cite: 63],
- **para** conhecer o acervo de autores disponíveis[cite: 63].
- **Endpoint:** `GET /api/autores` [cite: 64]
- **Resposta:** 200 OK com array JSON de autores[cite: 65].
- **Critérios de aceitação:**
  - Retorna lista vazia (`[]`) quando não há autores[cite: 67].
  - Retorna todos os campos: id, nome, nacionalidade, anoNascimento[cite: 68].
  - Código HTTP 200 em qualquer caso[cite: 69].

#### HU-02: Buscar autor por ID

- **Como** front-ms[cite: 73],
- **quero** buscar um autor pelo seu ID[cite: 75],
- **para** exibir o nome do autor junto aos dados do livro[cite: 75].
- **Endpoint:** `GET /api/autores/{id}` [cite: 76]
- **Resposta de sucesso:** 200 OK com objeto JSON do autor[cite: 77].
- **Resposta de erro:** 404 Not Found quando o ID não existe[cite: 78].
- **Critérios de aceitação:**
  - `GET /api/autores/1` retorna os dados do autor de ID 1[cite: 80].
  - `GET /api/autores/999` retorna 404 com mensagem de erro no corpo: `{"erro": "Autor não encontrado: 999"}`[cite: 81, 82].
  - Corpo da resposta é JSON válido em ambos os casos[cite: 83].

#### HU-03: Cadastrar autor

- **Como** bibliotecário[cite: 85],
- **quero** cadastrar um novo autor[cite: 86],
- **para** que ele possa ser vinculado a livros[cite: 87].
- **Endpoint:** `POST /api/autores` [cite: 88]
- **Corpo:** JSON com nome, nacionalidade, anoNascimento[cite: 89].
- **Resposta de sucesso:** 201 Created com o autor criado (incluindo id gerado)[cite: 89].
- **Resposta de erro:** 400 Bad Request com lista de erros de validação[cite: 90].
- **Critérios de aceitação:**
  - Campos obrigatórios: nome (não vazio), nacionalidade (não vazio), anoNascimento (positivo)[cite: 93, 94].
  - Corpo vazio ou com campos inválidos retorna 400 com objeto JSON listando os campos com erro[cite: 95].
  - Autor criado com sucesso retorna 201 com o id gerado no corpo[cite: 96].
  - Autor persiste no banco após reinicialização do container (volume Docker configurado)[cite: 97].

#### HU-04: Atualizar autor

- **Como** bibliotecário[cite: 99],
- **quero** corrigir os dados de um autor já cadastrado[cite: 100],
- **para** manter o acervo atualizado[cite: 101].
- **Endpoint:** `PUT /api/autores/{id}` [cite: 102]
- **Corpo:** JSON com os campos a atualizar[cite: 103].
- **Resposta de sucesso:** 200 OK com o autor atualizado[cite: 106].
- **Resposta de erro:** 404 se o ID não existir; 400 se os dados forem inválidos[cite: 106, 107].
- **Critérios de aceitação:**
  - Todos os campos são substituídos (PUT completo)[cite: 108].
  - As mesmas validações do cadastro se aplicam[cite: 109].
  - `PUT /api/autores/999` retorna 404[cite: 110].

#### HU-05: Excluir autor

- **Como** bibliotecário[cite: 112],
- **quero** remover um autor do sistema[cite: 113],
- **para** manter o cadastro limpo[cite: 113].
- **Endpoint:** `DELETE /api/autores/{id}` [cite: 114]
- **Resposta de sucesso:** 204 No Content[cite: 115].
- **Resposta de erro:** 404 se o ID não existir[cite: 115].
- **Critérios de aceitação:**
  - Após o DELETE, `GET /api/autores/{id}` retorna 404[cite: 117].
  - DELETE em ID inexistente retorna 404[cite: 118].
  - Resposta de sucesso não possui corpo[cite: 119].

---

### 3.2. Histórias do livro-ms

#### HU-06: Listar livros

- **Como** usuário do sistema[cite: 122],
- **quero** ver a lista de todos os livros cadastrados[cite: 123],
- **para** navegar pelo acervo da biblioteca[cite: 124].
- **Endpoint:** `GET /api/livros` [cite: 125]
- **Resposta:** 200 OK com array JSON de livros[cite: 126].
- **Critérios de aceitação:**
  - Retorna todos os campos: id, titulo, genero, anoPublicacao, disponivel, autorId[cite: 128].
  - Retorna lista vazia (`[]`) quando não há livros[cite: 129].
  - Suporta filtro opcional por disponibilidade: `/api/livros?disponivel=true` retorna apenas livros disponíveis[cite: 130, 131, 132].

#### HU-07: Buscar livro por ID

- **Como** front-ms[cite: 138],
- **quero** buscar um livro pelo seu ID[cite: 139],
- **para** exibir seus detalhes na página de detalhe[cite: 141].
- **Endpoint:** `GET /api/livros/{id}` [cite: 142]
- **Resposta de sucesso:** 200 OK com objeto JSON do livro[cite: 143].
- **Resposta de erro:** 404 Not Found[cite: 144].
- **Critérios de aceitação:**
  - Retorna todos os campos incluindo autorId[cite: 146].
  - ID inexistente retorna 404 com mensagem JSON[cite: 147].

#### HU-08: Cadastrar livro

- **Como** bibliotecário[cite: 150],
- **quero** cadastrar um novo livro informando seu autor[cite: 151],
- **para** ampliar o acervo da biblioteca[cite: 152].
- **Endpoint:** `POST /api/livros` [cite: 153]
- **Corpo:** JSON com titulo, genero, anoPublicacao, disponivel, autorId[cite: 154].
- **Resposta de sucesso:** 201 Created com o livro criado[cite: 154].
- **Resposta de erro:** 400 Bad Request[cite: 155].
- **Critérios de aceitação:**
  - Campos obrigatórios: titulo (não vazio, máx 150 chars), genero (não vazio), anoPublicacao (positivo), autorId (não nulo)[cite: 157].
  - `livro-ms` não valida se o `autorId` existe em `autor-ms`; essa validação é feita pelo `front-ms` antes de enviar a requisição[cite: 158].
  - Livro criado com sucesso retorna 201 com o id gerado[cite: 159].

#### HU-09: Atualizar livro

- **Como** bibliotecário[cite: 161],
- **quero** editar os dados de um livro cadastrado[cite: 162],
- **para** corrigir informações ou atualizar disponibilidade[cite: 163].
- **Endpoint:** `PUT /api/livros/{id}` [cite: 164]
- **Corpo:** JSON com todos os campos[cite: 165].
- **Resposta:** 200 OK com livro atualizado ou 404/400[cite: 166].
- **Critérios de aceitação:**
  - Todos os campos são substituídos (PUT completo)[cite: 168].
  - As mesmas validações do cadastro se aplicam[cite: 169].
  - É possível alterar disponivel de true para false e vice-versa[cite: 172].

#### HU-10: Excluir livro

- **Como** bibliotecário[cite: 174],
- **quero** remover um livro do acervo[cite: 175],
- **para** manter o catálogo atualizado[cite: 175].
- **Endpoint:** `DELETE /api/livros/{id}` [cite: 176]
- **Resposta:** 204 No Content ou 404[cite: 176].
- **Critérios de aceitação:**
  - Após o DELETE, `GET /api/livros/{id}` retorna 404[cite: 178].
  - Resposta de sucesso sem corpo[cite: 179].

#### HU-11: Filtrar livros por disponibilidade

- **Como** usuário[cite: 181],
- **quero** filtrar os livros por disponibilidade[cite: 182],
- **para** ver apenas os livros que posso emprestar agora[cite: 182].
- **Endpoint:** `GET /api/livros?disponivel=true` [cite: 183]
- **Resposta:** 200 OK com lista filtrada[cite: 183].
- **Critérios de aceitação:**
  - Sem o parâmetro, retorna todos os livros[cite: 185].
  - Com `?disponivel=true`, retorna apenas livros com disponivel true[cite: 186].
  - Com `?disponivel=false`, retorna apenas indisponíveis[cite: 187].

---

### 3.3. Histórias do front-ms

#### HU-12: Tela de listagem de autores

- **Como** usuário[cite: 191],
- **quero** ver a lista de autores em uma página web[cite: 192],
- **para** navegar pelo cadastro de autores[cite: 193].
- **Rota:** `GET /autores` [cite: 194]
- **Template:** `autores/lista.html` [cite: 195]
- **Critérios de aceitação:**
  - Tabela exibe: nome, nacionalidade, ano de nascimento e botões de ação[cite: 197].
  - Usa o layout base com navbar e footer (fragmentos Thymeleaf)[cite: 198].
  - Botão "+ Novo Autor" leva ao formulário de cadastro[cite: 199].
  - Se `autor-ms` estiver indisponível, exibe mensagem de erro amigável em vez de página de exceção[cite: 202].

#### HU-13: Formulário de cadastro e edição de autor

- **Como** bibliotecário[cite: 205],
- **quero** cadastrar e editar autores por uma interface web[cite: 206],
- **para** não precisar usar ferramentas de API diretamente[cite: 207].
- **Rotas:** `GET /autores/novo` e `GET /autores/{id}/editar` [cite: 208]
- **Submit:** `POST /autores` e `POST /autores/{id}` [cite: 209]
- **Template:** `autores/formulario.html` [cite: 209]
- **Critérios de aceitação:**
  - Mesmo template para criar e editar (botão muda de "Salvar" para "Atualizar")[cite: 211].
  - Validação no `front-ms` antes de chamar a API: campos obrigatórios não podem ser vazios[cite: 212].
  - Mensagem flash de sucesso após salvar ou atualizar[cite: 213].
  - Erros retornados pela API (400) são exibidos no formulário[cite: 214].
  - Padrão PRG aplicado: após POST com sucesso, redireciona para `/autores`[cite: 215].

#### HU-14: Excluir autor pela interface web

- **Como** bibliotecário[cite: 217],
- **quero** excluir um autor com confirmação[cite: 218],
- **para** evitar exclusões acidentais[cite: 219].
- **Rota:** `POST /autores/{id}/excluir` [cite: 220]
- **Critérios de aceitação:**
  - Diálogo de confirmação JavaScript antes do envio[cite: 222].
  - Após exclusão com sucesso, redireciona para `/autores` com mensagem flash[cite: 223].
  - Se a API retornar 404, exibe mensagem de erro na listagem[cite: 224].

#### HU-15: Tela de listagem de livros com nome do autor

- **Como** usuário[cite: 227],
- **quero** ver a lista de livros com o nome do autor resolvido[cite: 228],
- **para** não ver apenas um número de ID na coluna autor[cite: 228].
- **Rota:** `GET /livros` [cite: 232]
- **Template:** `livros/lista.html` [cite: 233]
- **Critérios de aceitação:**
  - `front-ms` chama `GET /api/livros` e, para cada livro, resolve o nome do autor via `GET /api/autores/{autorId}`[cite: 235].
  - Tabela exibe: título, gênero, ano, disponível (Sim/Não com badge colorido) e nome do autor[cite: 236].
  - Se o autor de um livro não for encontrado em `autor-ms`, exibe "Autor removido" na coluna, sem quebrar a página[cite: 237].
  - Filtro de disponibilidade via `?disponivel=true/false` passado adiante para `livro-ms`[cite: 238].

#### HU-16: Formulário de cadastro e edição de livro

- **Como** bibliotecário[cite: 240],
- **quero** cadastrar e editar livros escolhendo o autor em um `<select>`[cite: 241],
- **para** vincular corretamente o livro ao seu autor[cite: 242].
- **Rotas:** `GET /livros/novo` e `GET /livros/{id}/editar` [cite: 243]
- **Submit:** `POST /livros` e `POST /livros/{id}` [cite: 244]
- **Template:** `livros/formulario.html` [cite: 245]
- **Critérios de aceitação:**
  - O `front-ms` carrega a lista de autores chamando `GET /api/autores` para popular o `<select>`[cite: 247].
  - Na edição, o autor atual do livro aparece pré-selecionado[cite: 248].
  - Validação local: título, gênero, ano e autor são obrigatórios[cite: 249].
  - Erros retornados pela API são exibidos no formulário[cite: 250].
  - Padrão PRG aplicado[cite: 251].

#### HU-17: Página de detalhe do livro

- **Como** usuário[cite: 253],
- **quero** ver todos os detalhes de um livro em uma página dedicada[cite: 254],
- **para** obter informações completas antes de solicitá-lo[cite: 254].
- **Rota:** `GET /livros/{id}` [cite: 255]
- **Template:** `livros/detalhe.html` [cite: 256]
- **Critérios de aceitação:**
  - Exibe todos os campos do livro[cite: 258].
  - O nome do autor é resolvido via chamada a `GET /api/autores/{autorId}`[cite: 262].
  - Botões "Editar" e "Voltar para a lista" presentes[cite: 263].
  - Se o livro não existir, exibe página de erro amigável[cite: 264].

#### HU-18: Excluir livro pela interface web

- **Como** bibliotecário[cite: 266],
- **quero** excluir um livro com confirmação[cite: 267],
- **para** removê-lo do catálogo com segurança[cite: 267].
- **Rota:** `POST /livros/{id}/excluir` [cite: 268]
- **Critérios de aceitação:**
  - Diálogo de confirmação JavaScript com o título do livro[cite: 270].
  - Redireciona para `/livros` com mensagem flash após sucesso[cite: 271].

---

### 3.4. Histórias de Infraestrutura

#### HU-19: Subir todo o sistema com um único comando

- **Como** avaliador[cite: 274],
- **quero** subir toda a aplicação com um único comando[cite: 275],
- **para** verificar o funcionamento sem configuração manual[cite: 275].
- **Comando esperado:** `docker compose up --build` [cite: 277]
- **Critérios de aceitação:**
  - Todos os containers sobem sem erro após o comando[cite: 279].
  - O sistema está acessível em `http://localhost` após a inicialização[cite: 280].
  - Os bancos são criados e populados automaticamente na primeira execução[cite: 281].
  - `docker compose down` derruba tudo sem erros[cite: 282].

#### HU-20: Persistência de dados entre reinicializações

- **Como** usuário[cite: 285],
- **quero** que os dados cadastrados não sejam perdidos ao reiniciar os containers[cite: 286],
- **para** não precisar recadastrar tudo a cada execução[cite: 286].
- **Critérios de aceitação:**
  - Os volumes Docker dos dois bancos PostgreSQL estão declarados no `docker-compose.yml`[cite: 288, 292].
  - Dados cadastrados sobrevivem a `docker compose restart`[cite: 293].
  - `docker compose down -v` remove os volumes (comportamento documentado no README)[cite: 294].

#### HU-21: Roteamento pelo Nginx

- **Como** desenvolvedor[cite: 296],
- **quero** que o Nginx roteie as requisições corretamente[cite: 297],
- **para** que o browser acesse tudo por uma única porta[cite: 297].
- **Critérios de aceitação:**
  - `http://localhost/` serve o `front-ms`[cite: 299].
  - `http://localhost/api/livros` roteia para `livro-ms`[cite: 300].
  - `http://localhost/api/autores` roteia para `autor-ms`[cite: 301].
  - Requisições para rotas inexistentes retornam 404 do Nginx[cite: 302].
  - O `front-ms` chama os microsserviços pelos nomes de serviço Docker (`livro-ms:8081`, `autor-ms:8082`), não por localhost[cite: 303].

---
