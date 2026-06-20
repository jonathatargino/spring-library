## Why

O `front-ms` é o único ponto de interface web do sistema de biblioteca; sem ele, os usuários não conseguem interagir com os microsserviços `autor-ms` e `livro-ms` via browser. A aplicação já possui scaffold (pom.xml, Dockerfile, application.properties), mas nenhuma implementação — controllers, models, config e templates precisam ser criados do zero.

## What Changes

- Criação do bean `RestClient` configurado para `autor-ms` e `livro-ms` via `application.properties`
- POJOs `Autor` e `Livro` para deserialização das respostas JSON
- `AutorController` com rotas Thymeleaf para listagem, cadastro, edição e exclusão de autores (HU-12, HU-13, HU-14)
- `LivroController` com rotas Thymeleaf para listagem, cadastro, edição, detalhe e exclusão de livros (HU-15, HU-16, HU-17, HU-18)
- Template base `fragments/layout.html` com navbar e footer reutilizáveis
- Templates `autores/lista.html`, `autores/formulario.html`
- Templates `livros/lista.html`, `livros/formulario.html`, `livros/detalhe.html`
- Tratamento de erros: falha de RestClient exibe mensagem amigável, nunca página de exceção do Spring
- Padrão PRG em todos os formulários (Post-Redirect-Get)
- Validação local no front antes de chamar as APIs
- Resolução do nome do autor a partir de `autorId` na listagem e detalhe de livros
- Diálogos de confirmação JavaScript antes de exclusões

## Capabilities

### New Capabilities

- `autor-web-crud`: Interface web completa de CRUD de autores — listagem, formulário de criação/edição e exclusão com confirmação, usando Thymeleaf e RestClient para `autor-ms`
- `livro-web-crud`: Interface web completa de CRUD de livros — listagem com resolução de nome do autor, formulário de criação/edição com `<select>` de autores, página de detalhe e exclusão com confirmação
- `front-ms-infra`: Configuração base do front-ms — RestClient beans, POJOs de domínio, layout fragmentado Thymeleaf e tratamento global de erros de integração

### Modified Capabilities

## Impact

- **front-ms/src**: criação de ~15 arquivos Java e ~7 templates Thymeleaf, nenhum arquivo existente é alterado
- **Dependências externas**: `autor-ms` (port 8082) e `livro-ms` (port 8081) devem estar acessíveis; front-ms só funciona corretamente quando os dois estão online
- **docker-compose.yml**: já configurado com `LIVRO_MS_URL` e `AUTOR_MS_URL`; nenhuma alteração necessária
- **nginx.conf**: roteia `/` para `front-ms:8080`; sem alteração necessária
