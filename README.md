# 🎮 Game Critic — API REST

> API REST para gerenciamento de jogos e avaliações, com autenticação JWT, filtros dinâmicos, testes automatizados e monitoramento em produção.

![Java](https://img.shields.io/badge/Java-21+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![IGDB](https://img.shields.io/badge/IGDB-9147FF?style=for-the-badge&logo=twitch&logoColor=white)
[![Status](https://betteruptime.com/status-badges/v1/monitor/2ouks.svg)](https://game-critic.betteruptime.com)

---

## 📌 Sobre o Projeto

O **Game Critic** é uma API REST para gerenciamento de jogos e avaliações. Permite cadastrar jogos com informações como nome, gênero e plataforma, adicionar reviews com notas e comentários, além de calcular médias de avaliações por jogo.

O projeto foi desenvolvido com foco em boas práticas de backend: separação de responsabilidades em camadas, autenticação stateless com Spring Security + JWT, filtros dinâmicos com Specification, testes unitários e de integração, containerização com Docker e monitoramento de uptime em produção.

---

## 🌐 Links

| Recurso | URL |
|---|---|
| API em produção | https://jogos-review-api.onrender.com |
| Documentação Swagger | https://jogos-review-api.onrender.com/swagger-ui/index.html |
| Status da API | https://game-critic.betteruptime.com |

> ⚠️ A API está hospedada no plano gratuito do Render. O monitoramento de uptime é feito via BetterStack.

---

## 🧱 Tecnologias

| Tecnologia | Descrição |
|---|---|
| **Java 21** | Linguagem principal |
| **Spring Boot 4** | Framework da aplicação |
| **Spring Security** | Autenticação e autorização |
| **Spring Data JPA + Hibernate** | Abstração de acesso ao banco de dados |
| **Auth0 JWT** | Geração e validação de tokens JWT |
| **MySQL** | Banco de dados relacional em produção |
| **H2** | Banco em memória para testes de integração |
| **Maven** | Gerenciamento de dependências e build |
| **Swagger / OpenAPI 2.8.8** | Documentação interativa |
| **Docker** | Containerização da aplicação |
| **Render** | Hospedagem em nuvem |
| **BetterStack** | Monitoramento de uptime em produção |
| **GitHub Actions** | CI/CD automatizado |
| **IGDB API + Twitch OAuth2** | Integração externa para busca e importação de jogos |

---

## 🏗️ Arquitetura

O projeto segue uma **arquitetura em camadas**, com separação clara de responsabilidades.

```
src/main/java/desafio/review_jogos/
│
├── client/           → Clients de integração externa (IGDB)
│   └── dto/          → DTOs de resposta das APIs externas
├── config/           → Configurações (Swagger/OpenAPI, Security, Filtro JWT)
├── controller/       → Recebe e processa as requisições HTTP
├── service/          → Regras de negócio
├── repository/       → Acesso ao banco de dados
├── model/            → Entidades JPA
│   └── enums/        → Enums de Gênero, Plataforma e Role
├── dto/              → Objetos de Transferência de Dados
├── mapper/           → Conversão entre entidades e DTOs
├── exception/        → Exceções customizadas e handler global
├── specification/    → Filtros dinâmicos com Spring Specification
└── validation/       → Grupos de validação (OnCreate, OnUpdate)
```

---

## 🔗 Modelagem

### 📐 Relacionamento

```
Usuario  ──────────────<  Review  >──────────────  Jogo
  (1)                     (N)  (N)                  (1)
```

Um usuário pode ter **muitas reviews**. Cada review pertence a **um único usuário** e a **um único jogo**.

### 🎮 Jogo

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | Long | Identificador único |
| `nome` | String | Nome do jogo (máx. 120 caracteres) |
| `genero` | Genero (enum) | Gênero do jogo |
| `plataforma` | Plataforma (enum) | Plataforma disponível |
| `imageUrl` | String | URL da imagem de capa (máx. 500 chars) |
| `createdAt` | LocalDateTime | Data de criação (gerada automaticamente) |
| `updatedAt` | LocalDateTime | Data da última atualização (gerada automaticamente) |

### ⭐ Review

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | Long | Identificador único |
| `nota` | Integer | Nota de 1 a 10 |
| `comentario` | String | Comentário da avaliação |
| `jogo` | Jogo | Relacionamento com a entidade Jogo |
| `usuario` | Usuario | Relacionamento com o dono da review |
| `createdAt` | LocalDateTime | Data de criação (gerada automaticamente) |
| `updatedAt` | LocalDateTime | Data da última atualização (gerada automaticamente) |

### 👤 Usuario

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | Long | Identificador único |
| `email` | String | E-mail único (máx. 100 caracteres) |
| `nickname` | String | Apelido único (máx. 20 caracteres) |
| `senha` | String | Senha com hash BCrypt |
| `role` | Role (enum) | Papel do usuário |
| `createdAt` | LocalDateTime | Data de criação (gerada automaticamente) |
| `updatedAt` | LocalDateTime | Data da última atualização (gerada automaticamente) |

---

## 🎯 Enums

**Gênero**
```
ACAO, AVENTURA, RPG, ESTRATEGIA, ESPORTES, CORRIDA, LUTA,
FPS, TPS, SURVIVAL, HORROR, PLATAFORMA, METROIDVANIA,
ROGUELIKE, SIMULACAO, PUZZLE, STEALTH, MUSICAL, VISUAL_NOVEL, MOBILE
```

**Plataforma**
```
PS4, PS5, XBOX_ONE, XBOX_SERIES_X, XBOX_SERIES_S, PC, NINTENDO_SWITCH, MOBILE
```

**Role**
```
ROLE_USER, ROLE_ADMIN
```

---

## 🔐 Autenticação e Autorização

Autenticação **stateless** com Spring Security + JWT.

### Fluxo

```
1. POST /auth/registrar  →  cria usuário com senha hasheada (BCrypt)
2. POST /auth/login      →  valida credenciais e retorna token JWT
3. Requisições protegidas →  Authorization: Bearer <token>
```

### Controle de acesso por role

| Ação | ROLE_USER | ROLE_ADMIN |
|---|---|---|
| Listar e buscar jogos | ✅ público | ✅ público |
| Cadastrar, editar, deletar jogo | ❌ | ✅ |
| Criar review | ✅ | ✅ |
| Atualizar própria review | ✅ | ❌ |
| Deletar própria review | ✅ | ✅ |
| Deletar review de outro usuário | ❌ | ✅ |

### Como testar no Swagger

1. Acesse `/swagger-ui/index.html`
2. Use `POST /auth/registrar` para criar um usuário
3. Use `POST /auth/login` para obter o token JWT
4. Clique em **Authorize** 🔒 e cole o token (sem o `Bearer `)

---

## 🌐 Endpoints

### 🔐 Autenticação

| Método | Endpoint | Descrição | Acesso |
|---|---|---|---|
| `POST` | `/auth/registrar` | Cadastra um novo usuário | Público |
| `POST` | `/auth/login` | Login e retorno do token JWT | Público |

### 🎮 Jogos

| Método | Endpoint | Descrição | Acesso |
|---|---|---|---|
| `POST` | `/jogos` | Cadastra um novo jogo | ADMIN |
| `GET` | `/jogos` | Lista jogos (filtros e paginação) | Público |
| `GET` | `/jogos/{id}` | Busca jogo por ID | Público |
| `PUT` | `/jogos/{id}` | Atualiza jogo | ADMIN |
| `DELETE` | `/jogos/{id}` | Remove jogo | ADMIN |

### ⭐ Reviews

| Método | Endpoint | Descrição | Acesso |
|---|---|---|---|
| `POST` | `/jogos/{id}/reviews` | Cria review para um jogo | USER ou ADMIN |
| `GET` | `/jogos/{id}/reviews` | Lista reviews de um jogo | Público |
| `PUT` | `/reviews/{id}` | Atualiza review | Dono |
| `DELETE` | `/reviews/{id}` | Remove review | Dono ou ADMIN |

### 🔎 Admin — Integração IGDB

| Método | Endpoint | Descrição | Acesso |
|---|---|---|---|
| `GET` | `/admin/jogos/buscar?termoBusca=` | Busca jogos na IGDB por termo | ADMIN |
| `POST` | `/admin/jogos/importar` | Importa jogo da IGDB para o sistema | ADMIN |

### 📊 Estatísticas

| Método | Endpoint | Descrição | Acesso |
|---|---|---|---|
| `GET` | `/jogos/{id}/media` | Média de notas de um jogo | Público |

---

## 📋 Exemplos de Requisição

### Registrar usuário — `POST /auth/registrar`

```json
{
  "email": "usuario@teste.com",
  "nickname": "jogador123",
  "senha": "12345678"
}
```

### Login — `POST /auth/login`

```json
{
  "email": "usuario@teste.com",
  "senha": "12345678"
}
```

**Response `200 OK`:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Criar jogo — `POST /jogos` *(requer ADMIN)*

```json
{
  "nome": "God of War",
  "genero": "ACAO",
  "plataforma": "PS5",
  "imageUrl": "https://exemplo.com/imagens/god-of-war.jpg"
}
```

**Response `201 Created`:**
```json
{
  "id": 1,
  "nome": "God of War",
  "genero": "ACAO",
  "plataforma": "PS5",
  "imageUrl": "https://exemplo.com/imagens/god-of-war.jpg",
  "createdAt": "2026-05-08T10:00:00",
  "updatedAt": "2026-05-08T10:00:00"
}
```

### Listar jogos com filtros — `GET /jogos`

```
GET /jogos                                          → todos os jogos
GET /jogos?genero=RPG                               → filtrado por gênero
GET /jogos?plataforma=PS5                           → filtrado por plataforma
GET /jogos?genero=RPG&plataforma=PS5                → filtrado pelos dois
GET /jogos?genero=RPG&page=0&size=5&sort=nome,asc   → com paginação
```

### Criar review — `POST /jogos/1/reviews` *(requer USER ou ADMIN)*

```json
{
  "nota": 9,
  "comentario": "Jogo incrível, narrativa e combate perfeitos!"
}
```

**Response `201 Created`:**
```json
{
  "id": 1,
  "nota": 9,
  "comentario": "Jogo incrível, narrativa e combate perfeitos!",
  "jogoId": 1,
  "createdAt": "2026-05-08T10:05:00",
  "updatedAt": "2026-05-08T10:05:00"
}
```

### Atualizar review — `PUT /reviews/{id}` *(requer dono)*

```json
{
  "nota": 10,
  "comentario": "Mudei de ideia, é perfeito!"
}
```

> Ambos os campos são opcionais — envie apenas o que deseja atualizar.

### Consultar média — `GET /jogos/1/media`

**Response `200 OK`:**
```json
{
  "jogoId": 1,
  "nome": "God of War",
  "mediaNotas": 9.0
}
```

### Erro padronizado

```json
{
  "status": 403,
  "erro": "Forbidden",
  "message": "Você não tem permissão para deletar esta review.",
  "timestamp": "2026-05-03T20:00:00"
}
```

---

## 🔍 Filtros Dinâmicos com Specification

O projeto usa **Spring Data JPA Specification** para filtros opcionais sem duplicar métodos no repository. Um único método cobre todas as combinações de filtros.

```java
public static Specification<Jogo> porGenero(Genero genero) {
    return (root, query, cb) ->
            genero == null ? null : cb.equal(root.get("genero"), genero);
}
```

Quando o filtro é `null`, o Spring Data ignora aquela condição automaticamente.

---

## ⚠️ Tratamento de Exceções

Tratamento global via `@RestControllerAdvice` com respostas padronizadas.

| Exceção | Status | Quando ocorre |
|---|---|---|
| `RecursoNaoEncontradoException` | 404 | Jogo ou review não encontrado |
| `RecursoJaExisteException` | 409 | E-mail, nickname ou nome de jogo duplicado |
| `MethodArgumentNotValidException` | 400 | Dados de entrada inválidos |
| `AccessDeniedException` | 403 | Usuário sem permissão para a operação |
| `Exception` | 500 | Erros inesperados |
| `IgdbIntegrationException` | 502 | Falha na comunicação com a IGDB |
| `MethodArgumentTypeMismatchException` | 400 | Tipo de parâmetro inválido na URL |

---

## 🧪 Testes

### Unitários — JUnit 5 + Mockito

| Classe | Cobertura |
|---|---|
| `JogoServiceTest` | salvar, buscar, atualizar, excluir e média de notas |
| `ReviewServiceTest` | salvar, deletar e atualizar com controle de permissão |
| `TokenServiceTest` | geração e validação de tokens JWT |

### Integração — MockMvc + H2

| Classe | Cobertura |
|---|---|
| `JogoControllerIT` | 9 testes cobrindo CRUD completo e controle de acesso |
| `ReviewControllerIT` | 11 testes cobrindo CRUD, permissões e validações |

```bash
mvn test
```

---

## 💡 Boas Práticas Aplicadas

- ✅ DTOs para não expor entidades JPA nas respostas
- ✅ Enums para consistência nos dados de gênero, plataforma e role
- ✅ Bean Validation com grupos `OnCreate` e `OnUpdate`
- ✅ Arquitetura em camadas com separação clara de responsabilidades
- ✅ Mapper dedicado para conversão entre entidades e DTOs
- ✅ Tratamento global de exceções com `@RestControllerAdvice`
- ✅ Paginação e ordenação com `Pageable` e `@PageableDefault`
- ✅ Filtros dinâmicos com Spring Data Specification
- ✅ Documentação automática com Swagger / OpenAPI
- ✅ Autenticação stateless com Spring Security + JWT
- ✅ Senhas protegidas com hash BCrypt
- ✅ Autorização por roles com controle fino por endpoint
- ✅ Injeção de dependência por construtor em todas as classes
- ✅ Testes unitários e de integração com JUnit 5, Mockito e MockMvc
- ✅ Containerização com Docker e Docker Compose
- ✅ CI/CD com GitHub Actions
- ✅ Monitoramento de uptime em produção com BetterStack
- ✅ Integração com API externa (IGDB) via WebClient com OAuth2 Client Credentials

---

## ⚙️ Como Executar Localmente

### Pré-requisitos

- [Java 21+](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
- [Maven](https://maven.apache.org/)
- [MySQL](https://www.mysql.com/)
- [Docker](https://www.docker.com/) *(opcional)*

### Com Docker

```bash
git clone https://github.com/Felipe-SMZ/jogos-review-api.git
cd jogos-review-api
docker-compose up
```

### Sem Docker

**1. Clone o repositório**
```bash
git clone https://github.com/Felipe-SMZ/jogos-review-api.git
cd jogos-review-api
```

**2. Configure o banco de dados**
```sql
CREATE DATABASE review_jogos;
```

**3. Crie o arquivo `src/main/resources/application.properties`**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/review_jogos?serverTimezone=America/Sao_Paulo
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
springdoc.default-flat-param-object=true
api.security.token.secret=seu_secret_jwt
igdb.client-id=seu_client_id_twitch
igdb.client-secret=seu_client_secret_twitch
```

**4. Execute**
```bash
mvn spring-boot:run
```

**5. Acesse**
```
API:     http://localhost:8080
Swagger: http://localhost:8080/swagger-ui/index.html
```

---

## 👨‍💻 Autor

Desenvolvido por **Felipe Shimizu**

[![Portfolio](https://img.shields.io/badge/Portfólio-000000?style=for-the-badge&logo=vercel&logoColor=white)](https://www.devfelipeshimizu.me/)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/felipesshimizu/)
