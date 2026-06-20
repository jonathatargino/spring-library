## ADDED Requirements

### Requirement: Filtrar livros por disponibilidade
O sistema SHALL suportar filtro opcional por disponibilidade no endpoint `GET /api/livros` via query param `?disponivel=true` ou `?disponivel=false`. Sem o parâmetro, SHALL retornar todos os livros independente do campo `disponivel`.

#### Scenario: Listar apenas livros disponíveis
- **WHEN** `GET /api/livros?disponivel=true` é chamado
- **THEN** o sistema retorna 200 OK com array JSON contendo apenas livros com `disponivel: true`

#### Scenario: Listar apenas livros indisponíveis
- **WHEN** `GET /api/livros?disponivel=false` é chamado
- **THEN** o sistema retorna 200 OK com array JSON contendo apenas livros com `disponivel: false`

#### Scenario: Sem parâmetro retorna todos os livros
- **WHEN** `GET /api/livros` é chamado sem query param `disponivel`
- **THEN** o sistema retorna 200 OK com todos os livros cadastrados (disponíveis e indisponíveis)

#### Scenario: Filtro com resultado vazio
- **WHEN** `GET /api/livros?disponivel=false` é chamado e não há livros indisponíveis
- **THEN** o sistema retorna 200 OK com array JSON vazio `[]`

---

### Requirement: Passagem de filtro pelo front-ms
O `front-ms` SHALL repassar o parâmetro `?disponivel` recebido na rota `/livros` para a chamada a `GET /api/livros` no `livro-ms`, permitindo que a filtragem ocorra no backend.

#### Scenario: front-ms repassa parâmetro de filtro
- **WHEN** o usuário acessa `/livros?disponivel=true` no `front-ms`
- **THEN** o `front-ms` chama `GET http://livro-ms:8081/api/livros?disponivel=true` e exibe apenas os livros disponíveis
