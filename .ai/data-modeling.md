## 2. Modelos de Dados

### 2.1. autor-ms (banco postgres-autores)

| Campo             | Tipo         | Restrições                    |
| :---------------- | :----------- | :---------------------------- |
| **id**            | BIGSERIAL    | Chave primária [cite: 51]     |
| **nome**          | VARCHAR(100) | NOT NULL [cite: 51]           |
| **nacionalidade** | VARCHAR(80)  | NOT NULL [cite: 51]           |
| **anoNascimento** | INTEGER      | NOT NULL, positivo [cite: 51] |

### 2.2. livro-ms (banco postgres-livros)

| Campo             | Tipo         | Restrições                                       |
| :---------------- | :----------- | :----------------------------------------------- |
| **id**            | BIGSERIAL    | Chave primária [cite: 53]                        |
| **titulo**        | VARCHAR(150) | NOT NULL [cite: 53]                              |
| **genero**        | VARCHAR(60)  | NOT NULL [cite: 53]                              |
| **anoPublicacao** | INTEGER      | NOT NULL, positivo [cite: 53]                    |
| **disponivel**    | BOOLEAN      | NOT NULL, padrão true [cite: 53]                 |
| **autorId**       | BIGINT       | NOT NULL (referência lógica ao autor) [cite: 53] |

> 💡 **Dica:**
> O campo `autorId` é uma referência lógica, não uma chave estrangeira com constraint no banco[cite: 55]. Em microsserviços, a integridade referencial entre serviços é responsabilidade da aplicação, não do banco[cite: 56].

---
