## 4. Requisitos Técnicos

| Requisito                        | Detalhamento                                                                   |
| :------------------------------- | :----------------------------------------------------------------------------- |
| **Docker Compose obrigatório**   | Arquivo `docker-compose.yml` na raiz do projeto.                    |
| **Dockerfile por serviço**       | Cada ms deve ter seu próprio Dockerfile.                            |
| **PostgreSQL por serviço**       | Dois bancos independentes: `db_livros` e `db_autores`.              |
| **Volumes Docker**               | Dados persistidos entre reinicializações.                           |
| **Nginx como gateway**           | Arquivo `nginx.conf` versionado no repositório.                     |
| **REST JSON nos microsserviços** | `livro-ms` e `autor-ms` expõem apenas API REST (`@RestController`). |
| **Thymeleaf apenas no front**    | `front-ms` é o único serviço com interface HTML.                    |
| **RestClient no front**          | Comunicação entre `front-ms` e APIs via `RestClient`.               |
| **Tratamento de erros**          | Falha em um ms não derruba a página inteira.                        |
| **README no repositório**        | Instruções de execução e descrição da arquitetura.                  |
