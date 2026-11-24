# Simple Tweet API

API REST em Java e Spring Boot que simula as funcionalidades básicas de uma rede social no estilo Twitter/X: cadastro de usuários, autenticação com JWT, tweets, curtidas, comentários com respostas e feed paginado.

Projeto de estudo com foco em organização em camadas, segurança com OAuth2 Resource Server e JWT, testes unitários e convenções de código consistentes.

## Sumário

- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Como executar](#como-executar)
- [Autenticação](#autenticação)
- [Endpoints](#endpoints)
- [Validação e erros](#validação-e-erros)
- [Modelagem de dados](#modelagem-de-dados)
- [Testes](#testes)
- [Convenções de código](#convenções-de-código)
- [Fluxo de branches](#fluxo-de-branches)

## Tecnologias

- Java 17
- Spring Boot 3.5 (Web, Data JPA, Security, OAuth2 Resource Server, Validation)
- Hibernate
- MySQL
- Lombok
- Maven (com Maven Wrapper)
- Docker Compose (banco de dados local)
- JUnit 5 e Mockito

## Arquitetura

Organização por camada técnica, dentro de `com.riquelmemr.simpletweet`:

| Pacote | Responsabilidade |
|---|---|
| `controller` | Endpoints REST. Recebe a requisição, valida o corpo e delega para a facade. |
| `facade` | Orquestra services e mappers para cada caso de uso. Interface pública em `facade/`, implementação em `facade/impl/`. |
| `service` | Regras de negócio e transações. Interface em `service/<assunto>/`, implementação em `service/<assunto>/impl/`. |
| `repository` | Acesso a dados com Spring Data JPA. |
| `mapper` | Conversão manual entre entidades e DTOs. |
| `model` | Entidades JPA. |
| `dto` | Records de entrada (`request`) e saída (`response`). |
| `config` | Configuração do Spring: segurança, JWT, propriedades, `Clock` e carga inicial de dados. |
| `security` | Geração de token e handlers de 401/403. |
| `exceptions` | Exceções da aplicação e o `GlobalExceptionHandler`. |
| `utils` | Funções estáticas utilitárias. |

Fluxo de uma requisição: `Controller -> Facade -> Service -> Repository`.

## Como executar

### Pré-requisitos

- JDK 17 ou superior
- Docker (para o MySQL) ou um MySQL acessível
- OpenSSL (para gerar as chaves do JWT)

### 1. Subir o banco de dados

O arquivo `docker/docker-compose.yml` sobe um MySQL com o banco `tweetdatabase` na porta `3306`. Ele lê as credenciais de variáveis de ambiente:

```bash
export DATABASE_USERNAME=tweet
export DATABASE_PASSWORD=tweet
export DATABASE_ROOT_PASSWORD=root

docker compose -f docker/docker-compose.yml up -d
```

### 2. Gerar as chaves do JWT

Os tokens são assinados com um par de chaves RSA. Os arquivos não são versionados (estão no `.gitignore`), então cada ambiente precisa gerar os seus:

```bash
mkdir -p src/main/resources/jwt
openssl genrsa -out src/main/resources/jwt/private.pem 2048
openssl rsa -in src/main/resources/jwt/private.pem -pubout -out src/main/resources/jwt/public.pem
```

### 3. Configurar as variáveis de ambiente da aplicação

| Variável | Exemplo | Descrição |
|---|---|---|
| `DATABASE_URL` | `jdbc:mysql://localhost:3306/tweetdatabase` | URL JDBC do MySQL |
| `DATABASE_USERNAME` | `tweet` | Usuário do banco |
| `DATABASE_PASSWORD` | `tweet` | Senha do banco |

As demais configurações ficam em `src/main/resources/application.yml`:

| Propriedade | Padrão | Descrição |
|---|---|---|
| `jwt.public-key` | `classpath:jwt/public.pem` | Chave pública usada para validar o token |
| `jwt.private-key` | `classpath:jwt/private.pem` | Chave privada usada para assinar o token |
| `jwt.expiration` | `3000` | Validade do token, em segundos |

### 4. Executar

```bash
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`. O schema é criado pelo Hibernate (`ddl-auto: update`) e o `data.sql` cadastra as roles `ADMIN` e `BASIC`.

Na primeira execução, o `DataInitializer` cria um usuário administrador:

| Usuário | Senha |
|---|---|
| `admin` | `nimda` |

Essa credencial serve apenas para desenvolvimento local.

## Autenticação

A API é stateless e usa o Spring Security como OAuth2 Resource Server. O fluxo é:

1. Obter o token em `POST /auth/token` com usuário e senha.
2. Enviar o token em todas as outras requisições no header `Authorization: Bearer <token>`.

O token carrega o id do usuário no `subject` e as roles na claim `authorities`, mapeadas para `ROLE_ADMIN` e `ROLE_BASIC`.

Todo usuário cadastrado pela API recebe a role `BASIC`. Edição e exclusão de tweets e edição de usuário são permitidas apenas ao dono do recurso ou a um `ADMIN`.

Exemplo:

```bash
curl -X POST http://localhost:8080/auth/token \
  -H 'Content-Type: application/json' \
  -d '{"username": "admin", "password": "nimda"}'
```

```json
{
  "accessToken": "eyJraWQiOi...",
  "expiresIn": 3000
}
```

## Endpoints

Todos os endpoints exigem token, exceto os marcados como públicos.

### Autenticação e usuários

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `POST` | `/auth/token` | Público | Gera o token de acesso |
| `POST` | `/users/create` | Público | Cria uma conta |
| `GET` | `/users` | `ADMIN` | Lista todos os usuários |
| `PUT` | `/users/update/{id}` | Dono ou `ADMIN` | Atualiza nome, username, e-mail e bio. Campos omitidos não são alterados. |

### Tweets

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `POST` | `/tweets/create` | Autenticado | Cria um tweet |
| `PUT` | `/tweets/update/{id}` | Dono ou `ADMIN` | Atualiza o conteúdo do tweet |
| `DELETE` | `/tweets/delete/{id}` | Dono ou `ADMIN` | Remove o tweet |
| `GET` | `/tweets/{userId}` | Autenticado | Lista os tweets de um usuário |
| `GET` | `/tweets/feed?page=0&pageSize=10` | Autenticado | Feed paginado, do mais recente para o mais antigo |

### Curtidas

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `POST` | `/tweets/{tweetId}/like` | Autenticado | Curte o tweet, ou remove a curtida se já existir |
| `GET` | `/tweets/{tweetId}/likes?page=0&pageSize=50` | Autenticado | Lista quem curtiu o tweet |
| `POST` | `/tweets/comments/{commentId}/likes` | Autenticado | Curte o comentário, ou remove a curtida se já existir |

### Comentários

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `POST` | `/tweets/{tweetId}/comment` | Autenticado | Comenta em um tweet |
| `POST` | `/tweets/comments/{commentId}/reply` | Autenticado | Responde a um comentário |
| `GET` | `/tweets/{tweetId}/comments?page=0&pageSize=50` | Autenticado | Lista os comentários do tweet com as respostas aninhadas |

## Validação e erros

Os corpos das requisições são validados com Bean Validation:

| Request | Regras |
|---|---|
| Cadastro de usuário | `username` e `password` obrigatórios, `email` obrigatório e válido |
| Atualização de usuário | `email`, se informado, precisa ser válido |
| Login | `username` e `password` obrigatórios |
| Tweet e comentário | `content` obrigatório |

Todo erro, incluindo os do framework (JSON malformado, método não suportado, parâmetro com tipo inválido), é devolvido no mesmo formato:

```json
{
  "timestamp": "2026-11-24T07:04:50.275895",
  "status": 400,
  "error": "Validation failed",
  "message": "content: must not be blank"
}
```

| Status | Quando |
|---|---|
| `400` | Corpo inválido, credenciais inválidas ou usuário já existente |
| `401` | Token ausente, inválido ou expirado |
| `403` | Usuário sem permissão sobre o recurso |
| `404` | Recurso não encontrado |
| `500` | Erro inesperado. O detalhe fica no log do servidor, nunca na resposta. |

## Modelagem de dados

| Entidade | Tabela | Descrição |
|---|---|---|
| `User` | `tb_user` | Conta do usuário. Username único. |
| `Role` | `tb_role` | Papel de acesso (`ADMIN`, `BASIC`), ligado ao usuário por `tb_user_roles`. |
| `Tweet` | `tb_tweet` | Publicação de um usuário. |
| `Like` | `tb_like` | Curtida de um usuário em um tweet. |
| `Comment` | `tb_comment` | Comentário em um tweet ou resposta a outro comentário (`parent_comment_id`). |
| `CommentLike` | `tb_comment_like` | Curtida de um usuário em um comentário. |

`User`, `Tweet`, `Like`, `Comment` e `CommentLike` estendem `Item`, que define o id (`pk`) e as datas de criação e de modificação.

## Testes

Os testes unitários cobrem as services com JUnit 5 e Mockito, seguindo o padrão Arrange, Act e Assert. Objetos de teste são montados com builders em `src/test/java/.../builder`.

```bash
./mvnw test
```

O teste `SimpletweetApplicationTests` sobe o contexto completo do Spring e precisa do banco disponível e das variáveis de ambiente configuradas. Para rodar apenas os unitários:

```bash
./mvnw test -Dtest='!SimpletweetApplicationTests' -Dsurefire.failIfNoSpecifiedTests=false
```

## Convenções de código

- Injeção de dependência sempre por construtor (`@RequiredArgsConstructor` com campos `private final`), nunca `@Autowired`.
- Services e facades são interface mais implementação. A implementação fica em `impl/` e é package-private.
- Implementação de service tem `@Transactional(readOnly = true)` na classe e `@Transactional` nos métodos que gravam.
- Configuração lida com `@ConfigurationProperties`, sem `@Value` espalhado pelas classes.
- O horário atual vem do bean `Clock`, nunca de `now()` direto.
- DTOs são `record`.
- Sem `var`, sempre com o tipo explícito.
- Sem comentários no código de produção.
- Imports em um único bloco, em ordem alfabética, sem `*`, com os estáticos em bloco separado.
- Indentação de 4 espaços e linha em branco após a abertura da classe.
- Logs com `@Slf4j`, em inglês, com os identificadores entre colchetes. Falha esperada é `warn` e falha inesperada é `error`.
- Mensagens de exceção em inglês.

## Fluxo de branches

O projeto segue git flow:

| Branch | Papel |
|---|---|
| `main` | Código lançado |
| `develop` | Integração do dia a dia |
| `feat/`, `test/`, `refactor/`, `docs/` | Uma tarefa por branch, criada a partir da `develop` e integrada por Pull Request |

Commits seguem o formato `Tipo: descrição curta`, com os tipos `Feat`, `Fix`, `Docs`, `Refactor`, `Chore` e `Test`.
