## ADDED Requirements

### Requirement: Propagação do token JWT nas chamadas RestClient
Os beans `RestClient` configurados para `autor-ms` e `livro-ms` SHALL anexar automaticamente o header `Authorization: Bearer <token>` em toda requisição, usando o JWT da sessão HTTP do usuário autenticado atual.

#### Scenario: Chamada autenticada inclui o header Authorization
- **WHEN** um controller do `front-ms` usa o `RestClient` de `autor-ms` ou `livro-ms` dentro de uma requisição com sessão autenticada
- **THEN** a requisição HTTP enviada ao serviço downstream inclui o header `Authorization: Bearer <token>` com o JWT da sessão atual

#### Scenario: API downstream rejeita token expirado
- **WHEN** o `RestClient` envia uma chamada com um JWT expirado e a API downstream retorna HTTP 401
- **THEN** o `front-ms` trata a resposta invalidando a sessão local e redirecionando o usuário para `GET /login`, em vez de propagar uma página de erro genérica
