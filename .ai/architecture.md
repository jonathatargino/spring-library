## 1. Visão Geral da Arquitetura

[cite_start]O sistema é composto por quatro containers orquestrados via Docker Compose, conforme o diagrama abaixo:

[Browser]
│
▼
[nginx:80] (API Gateway)
│
├── / ──► front-ms :8080 (Thymeleaf)
├── /api/livros ──► livro-ms :8081 (REST + PostgreSQL)
└── /api/autores ──► autor-ms :8082 (REST + PostgreSQL)

[Docker Compose]
├── front-ms ──► imagem própria
├── livro-ms ──► imagem própria ──► postgres-livros
├── autor-ms ──► imagem própria ──► postgres-autores
└── nginx ──► imagem oficial ──► nginx.conf

### 1.1. Responsabilidades de cada serviço

| Serviço      | Tecnologia              | Responsabilidade                                                                                      |
| :----------- | :---------------------- | :---------------------------------------------------------------------------------------------------- |
| **nginx**    | Nginx 1.25              | API Gateway: roteamento, CORS centralizado, ponto único de entrada[cite: 41].                         |
| **front-ms** | Spring Boot + Thymeleaf | Interface web: consome `livro-ms` e `autor-ms` via `RestClient`, renderiza as páginas HTML[cite: 41]. |
| **livro-ms** | Spring Boot + REST JPA  | CRUD de Livros; armazena `autorId` como Long; não conhece `autor-ms`[cite: 41].                       |
| **autor-ms** | Spring Boot + REST JPA  | CRUD de Autores; serviço independente, sem dependência dos demais[cite: 41].                          |

> 💡 **Regra de ouro dos microsserviços:**
> Cada serviço possui seu próprio banco de dados PostgreSQL[cite: 43]. O `livro-ms` não faz JOIN com a tabela de autores — ele armazena apenas o `autorId`. Quem resolve o nome do autor é o `front-ms`, consultando o `autor-ms` via HTTP antes de renderizar a página[cite: 44].

---
