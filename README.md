# Spring Library — Sistema de Biblioteca com Microsserviços

Sistema de gerenciamento de biblioteca implementado com arquitetura de microsserviços em Spring Boot, orquestrado via Docker Compose.

## Arquitetura

```
[Browser]
    │
    ▼
[nginx:80]  ← ponto único de entrada
    │
    ├── /              → front-ms:8080  (Thymeleaf)
    ├── /api/livros    → livro-ms:8081  (REST + PostgreSQL)
    └── /api/autores   → autor-ms:8082  (REST + PostgreSQL)
```

| Serviço        | Porta  | Tecnologia                  | Banco          |
|----------------|--------|-----------------------------|----------------|
| `nginx`        | 80     | Nginx 1.25                  | —              |
| `front-ms`     | 8080   | Spring Boot + Thymeleaf     | —              |
| `livro-ms`     | 8081   | Spring Boot + JPA + REST    | db_livros      |
| `autor-ms`     | 8082   | Spring Boot + JPA + REST    | db_autores     |
| `postgres-livros` | —   | PostgreSQL 16               | db_livros      |
| `postgres-autores`| —   | PostgreSQL 16               | db_autores     |

## Como executar

### Subir todos os serviços
```bash
docker compose up --build
```

O sistema estará disponível em `http://localhost` após todos os containers iniciarem.

### Derrubar os containers
```bash
docker compose down
```

### Remover containers e volumes (apaga todos os dados)
```bash
docker compose down -v
```

> **Atenção:** `docker compose down -v` apaga os dados persistidos nos volumes do PostgreSQL.

## Autenticação

O sistema exige login. Os usuários abaixo já vêm pré-cadastrados no `front-ms` (configuráveis via `.env`, ver `.env.example`):

| Usuário        | Senha          | Papel          | Pode fazer                         |
|----------------|----------------|----------------|-------------------------------------|
| `bibliotecario`| `biblioteca123`| BIBLIOTECARIO  | Ler e escrever (criar/editar/excluir) |
| `usuario`      | `usuario123`   | USUARIO        | Apenas ler                          |

Acesse `http://localhost/login` para entrar. Ao logar, o `front-ms` emite um JWT e o mantém na sessão, repassando-o automaticamente às chamadas para `autor-ms`/`livro-ms`.

### Usando as APIs diretamente (curl/Postman)

`autor-ms` e `livro-ms` também exigem o JWT no header `Authorization`. Para obter um token sem passar pela tela de login, faça o próprio login via curl e copie o token retornado pelo `front-ms` durante a autenticação, ou — mais simples para testes manuais — gere um token de teste chamando o endpoint de login com as credenciais acima e inspecionando o atributo de sessão no DevTools do navegador (`Application > Cookies`), já que o token fica armazenado na sessão HTTP e não é exposto em um endpoint JSON dedicado nesta versão.

Com o token em mãos:
```bash
curl -H "Authorization: Bearer <token>" http://localhost/api/autores
curl -X POST -H "Authorization: Bearer <token>" -H "Content-Type: application/json" \
  -d '{"nome":"Machado de Assis","nacionalidade":"Brasileira","anoNascimento":1839}' \
  http://localhost/api/autores
```
Sem o header `Authorization`, qualquer chamada a `/api/**` retorna `401 {"erro": "..."}`. Com papel `USUARIO`, chamadas de escrita (`POST`/`PUT`/`DELETE`) retornam `403 {"erro": "Acesso restrito a bibliotecários"}`.

## Endpoints das APIs

### autor-ms (`/api/autores`)
| Método | Rota                 | Descrição           |
|--------|----------------------|---------------------|
| GET    | `/api/autores`       | Listar todos        |
| GET    | `/api/autores/{id}`  | Buscar por ID       |
| POST   | `/api/autores`       | Cadastrar           |
| PUT    | `/api/autores/{id}`  | Atualizar           |
| DELETE | `/api/autores/{id}`  | Excluir             |

### livro-ms (`/api/livros`)
| Método | Rota                          | Descrição                     |
|--------|-------------------------------|-------------------------------|
| GET    | `/api/livros`                 | Listar todos                  |
| GET    | `/api/livros?disponivel=true` | Filtrar por disponibilidade   |
| GET    | `/api/livros/{id}`            | Buscar por ID                 |
| POST   | `/api/livros`                 | Cadastrar                     |
| PUT    | `/api/livros/{id}`            | Atualizar                     |
| DELETE | `/api/livros/{id}`            | Excluir                       |

## Estrutura do projeto

```
spring-library/
├── autor-ms/              # Microserviço de Autores
│   ├── src/main/java/com/library/autorms/
│   ├── src/main/resources/application.properties
│   ├── pom.xml
│   └── Dockerfile
├── livro-ms/              # Microserviço de Livros
│   ├── src/main/java/com/library/livrooms/
│   ├── src/main/resources/application.properties
│   ├── pom.xml
│   └── Dockerfile
├── front-ms/              # Frontend (Thymeleaf)
│   ├── src/main/java/com/library/frontms/
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── templates/     # Templates Thymeleaf
│   ├── pom.xml
│   └── Dockerfile
├── nginx/
│   └── nginx.conf
├── docker-compose.yml
└── README.md
```
