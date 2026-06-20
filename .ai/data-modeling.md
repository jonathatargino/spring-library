## 2. Modelos de Dados

### 2.1. autor-ms (banco postgres-autores)

| Campo             | Tipo         | Restrições                    |
| :---------------- | :----------- | :---------------------------- |
| **id**            | BIGSERIAL    | Chave primária     |
| **nome**          | VARCHAR(100) | NOT NULL           |
| **nacionalidade** | VARCHAR(80)  | NOT NULL           |
| **anoNascimento** | INTEGER      | NOT NULL, positivo |

### 2.2. livro-ms (banco postgres-livros)

| Campo             | Tipo         | Restrições                                       |
| :---------------- | :----------- | :----------------------------------------------- |
| **id**            | BIGSERIAL    | Chave primária                        |
| **titulo**        | VARCHAR(150) | NOT NULL                              |
| **genero**        | VARCHAR(60)  | NOT NULL                              |
| **anoPublicacao** | INTEGER      | NOT NULL, positivo                    |
| **disponivel**    | BOOLEAN      | NOT NULL, padrão true                 |
| **autorId**       | BIGINT       | NOT NULL (referência lógica ao autor) |

> 💡 **Dica:**
> O campo `autorId` é uma referência lógica, não uma chave estrangeira com constraint no banco. Em microsserviços, a integridade referencial entre serviços é responsabilidade da aplicação, não do banco.

---
