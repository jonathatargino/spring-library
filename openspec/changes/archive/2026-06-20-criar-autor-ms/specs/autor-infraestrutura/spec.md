## ADDED Requirements

### Requirement: Container Docker do autor-ms
O `autor-ms` SHALL possuir um `Dockerfile` próprio na pasta `autor-ms/` que permita construir a imagem do serviço. O serviço SHALL ser adicionado ao `docker-compose.yml` na raiz do monorepo com nome de serviço `autor-ms`, expondo a porta 8082, e com dependência do container `postgres-autores`.

#### Scenario: Build e start via Docker Compose
- **WHEN** o comando `docker compose up --build` é executado na raiz do projeto
- **THEN** o container `autor-ms` sobe sem erros e o endpoint `GET /api/autores` responde na porta 8082

#### Scenario: Dockerfile presente
- **WHEN** o repositório é inspecionado
- **THEN** existe um arquivo `autor-ms/Dockerfile` válido para build da imagem Spring Boot

---

### Requirement: Banco de dados PostgreSQL dedicado
O `autor-ms` SHALL usar um container PostgreSQL 16 independente (`postgres-autores`) declarado no `docker-compose.yml`, com banco de nome `db_autores`. O schema da tabela `autores` SHALL ser criado automaticamente pelo Hibernate (`ddl-auto=update`) na primeira execução.

#### Scenario: Tabela criada automaticamente
- **WHEN** o `autor-ms` sobe pela primeira vez com banco vazio
- **THEN** a tabela `autores` é criada automaticamente no banco `db_autores` sem intervenção manual

#### Scenario: Isolamento do banco
- **WHEN** o container `postgres-autores` está rodando
- **THEN** ele é um container PostgreSQL independente do `postgres-livros`, sem dados compartilhados entre eles

---

### Requirement: Persistência de dados via volume Docker
O `docker-compose.yml` SHALL declarar um volume nomeado para o banco `postgres-autores`, garantindo que os dados de autores sobrevivam a reinicializações de container.

#### Scenario: Dados persistem após restart
- **WHEN** um autor é cadastrado e o comando `docker compose restart` é executado
- **THEN** o autor continua acessível via `GET /api/autores/{id}` após a reinicialização

#### Scenario: Volumes removidos com down -v
- **WHEN** o comando `docker compose down -v` é executado
- **THEN** o volume `postgres_autores_data` é removido e os dados são perdidos (comportamento esperado e documentado)

---

### Requirement: Roteamento Nginx para /api/autores
O `nginx.conf` SHALL conter uma diretiva `location /api/autores` que roteia as requisições para `autor-ms:8082`, mantendo o path na requisição proxiada.

#### Scenario: Requisição roteada pelo Nginx
- **WHEN** a requisição `GET http://localhost/api/autores` é feita ao Nginx na porta 80
- **THEN** o Nginx roteie a requisição para `http://autor-ms:8082/api/autores` e retorne a resposta do serviço

#### Scenario: Rota inexistente retorna 404 do Nginx
- **WHEN** uma rota não mapeada é acessada (ex: `GET http://localhost/nao-existe`)
- **THEN** o Nginx retorna HTTP 404

---

### Requirement: Sistema acessível com um único comando
O sistema completo (incluindo `autor-ms`) SHALL estar acessível após executar `docker compose up --build` a partir da raiz do monorepo, sem necessidade de configuração manual adicional.

#### Scenario: Inicialização completa sem configuração manual
- **WHEN** o comando `docker compose up --build` é executado em ambiente sem nenhum container rodando
- **THEN** todos os containers sobem sem erros em ordem correta (bancos antes dos serviços) e `http://localhost/api/autores` responde com HTTP 200

#### Scenario: Teardown limpo
- **WHEN** o comando `docker compose down` é executado
- **THEN** todos os containers param sem erros e os volumes são preservados
