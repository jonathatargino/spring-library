## 4. Requisitos Técnicos

| Requisito                        | Detalhamento                                                                   |
| :------------------------------- | :----------------------------------------------------------------------------- |
| **Docker Compose obrigatório**   | Arquivo `docker-compose.yml` na raiz do projeto[cite: 305].                    |
| **Dockerfile por serviço**       | Cada ms deve ter seu próprio Dockerfile[cite: 305].                            |
| **PostgreSQL por serviço**       | Dois bancos independentes: `db_livros` e `db_autores`[cite: 305].              |
| **Volumes Docker**               | Dados persistidos entre reinicializações[cite: 305].                           |
| **Nginx como gateway**           | Arquivo `nginx.conf` versionado no repositório[cite: 305].                     |
| **REST JSON nos microsserviços** | `livro-ms` e `autor-ms` expõem apenas API REST (`@RestController`)[cite: 305]. |
| **Thymeleaf apenas no front**    | `front-ms` é o único serviço com interface HTML[cite: 305].                    |
| **RestClient no front**          | Comunicação entre `front-ms` e APIs via `RestClient`[cite: 305].               |
| **Tratamento de erros**          | Falha em um ms não derruba a página inteira[cite: 305].                        |
| **README no repositório**        | Instruções de execução e descrição da arquitetura[cite: 305].                  |
