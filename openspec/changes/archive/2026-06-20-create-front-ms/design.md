## Context

O `front-ms` é o frontend Thymeleaf da biblioteca. Já existe com scaffold básico (pom.xml com dependências corretas, Dockerfile multi-stage, application.properties com as URLs dos serviços). O que falta é toda a camada de implementação: beans de configuração, POJOs de domínio, controllers MVC, tratamento de erros e templates HTML.

Os dois backends (`autor-ms` em :8082, `livro-ms` em :8081) já estão funcionais com REST JSON completo. No Docker Compose as URLs são injetadas via variáveis de ambiente (`LIVRO_MS_URL`, `AUTOR_MS_URL`) — as propriedades `livro.ms.url` e `autor.ms.url` já mapeiam essas variáveis.

## Goals / Non-Goals

**Goals:**
- Implementar CRUD web completo de autores (HU-12, HU-13, HU-14)
- Implementar CRUD web completo de livros com resolução de nome do autor (HU-15, HU-16, HU-17, HU-18)
- Garantir que falhas nas APIs downstream exibam mensagem amigável (sem página de erro Spring)
- Aplicar padrão PRG em todos os formulários
- Layout base reutilizável com fragmentos Thymeleaf (navbar + footer)

**Non-Goals:**
- Autenticação/autorização de usuários
- Paginação ou busca textual
- Cache de chamadas HTTP
- Testes automatizados (não exigidos nas HUs)
- Alteração em `autor-ms`, `livro-ms`, `nginx.conf` ou `docker-compose.yml`

## Decisions

### D1 — RestClient configurado por base URL

**Decisão**: Criar um `@Configuration` com dois `@Bean RestClient` nomeados — um para `autor-ms` e outro para `livro-ms` — usando `RestClient.builder().baseUrl(...)`.

**Alternativa considerada**: Um único `RestClient` sem base URL, montando a URL completa em cada chamada. Descartado por duplicação e violação de responsabilidade.

**Motivação**: O Spring 6.1+ recomenda `RestClient` como substituto moderno do `RestTemplate`. A injeção por bean nomeado torna os controllers testáveis e o isolamento de base URL limpo.

### D2 — Tratamento de erros com `@ControllerAdvice`

**Decisão**: Capturar `RestClientResponseException` e `ResourceAccessException` globalmente num `GlobalErrorHandler` que retorna uma view `error/generico.html` ou adiciona atributos flash ao `RedirectAttributes`.

**Alternativa considerada**: Try-catch dentro de cada método de controller. Descartado por duplicação; o handler global cobre todos os controllers sem repetição.

**Regra**: A página de lista nunca quebra — se `autor-ms` ou `livro-ms` estiver offline, o controller captura a exceção e exibe mensagem amigável na mesma view.

### D3 — Resolução de autor no front-ms (sem chamada paralela)

**Decisão**: Na listagem de livros, para cada livro chamar `GET /api/autores/{autorId}` sequencialmente. Se retornar 404 ou erro, exibir `"Autor removido"` nessa linha sem quebrar o restante.

**Alternativa considerada**: Buscar todos os autores de uma vez e fazer o join em memória. Viável, mas cria dependência forte na ordenação dos dados. A abordagem sequencial é mais simples e coerente com o escopo do projeto.

### D4 — Padrão PRG via `RedirectAttributes`

**Decisão**: Todo POST de formulário com sucesso chama `redirectAttributes.addFlashAttribute("mensagem", "...")` e redireciona para a rota GET correspondente. Os templates leem `${mensagem}` e `${erro}` do modelo Thymeleaf.

### D5 — POJOs de domínio simples (sem record)

**Decisão**: Usar classes Java com getters/setters para `Autor` e `Livro`, não records Java 17+.

**Motivação**: Thymeleaf binding em formulários exige setters no objeto do modelo. Records são imutáveis e incompatíveis com `th:object`.

### D6 — Validação no front antes de chamar a API

**Decisão**: Validar campos obrigatórios no controller antes de enviar para a API. Se inválido, retornar o formulário com mensagens de erro sem chamar a API.

**Motivação**: HU-13 e HU-16 exigem validação local. Evita round-trip desnecessário e garante UX consistente mesmo se a API estiver lenta.

## Risks / Trade-offs

- **[Risco] Chamadas sequenciais de autor por livro**: lista de 100 livros gera 100 chamadas HTTP → latência alta. **Mitigação**: aceitável para o escopo acadêmico; em produção usaríamos cache ou batch.
- **[Risco] `autor-ms` offline ao carregar formulário de livro**: o `<select>` de autores fica vazio. **Mitigação**: exibir mensagem de aviso no formulário e desabilitar o submit.
- **[Trade-off] Sem paginação**: lista de todos os livros/autores numa única chamada pode ser lenta com dados reais grandes. Aceitável para o projeto.
- **[Risco] Mensagem flash perdida após duplo redirect**: se o browser recarregar a página de lista, a flash attribute sumiu. **Mitigação**: comportamento esperado do padrão PRG; documentar no README.

## Migration Plan

1. Criar os arquivos Java sob `front-ms/src/main/java/com/library/frontms/`
2. Criar os templates Thymeleaf sob `front-ms/src/main/resources/templates/`
3. Verificar localmente com `mvn spring-boot:run` (com `autor-ms` e `livro-ms` rodando)
4. Validar integração completa com `docker compose up --build`

Rollback: como é adição pura (nenhum arquivo existente é modificado), reverter é simplesmente apagar os arquivos adicionados.

## Open Questions

- Nenhuma questão em aberto — todos os requisitos são claros pelas HUs.
