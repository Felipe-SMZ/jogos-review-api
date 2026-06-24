# 🎮 Game Critic — API REST

> API REST para catálogo de jogos e avaliações, com autenticação JWT, filtros dinâmicos, integração com IGDB, testes automatizados e monitoramento em produção.

![Java](https://img.shields.io/badge/Java-21+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![IGDB](https://img.shields.io/badge/IGDB-9147FF?style=for-the-badge&logo=twitch&logoColor=white)
[![Status](https://betteruptime.com/status-badges/v1/monitor/2ouks.svg)](https://game-critic.betteruptime.com)

---

## 📌 Sobre o projeto

O **Game Critic** é uma API REST para gerenciamento de jogos e avaliações. A aplicação permite cadastrar jogos, consultar catálogo com filtros dinâmicos, registrar reviews com autenticação JWT e importar jogos da **IGDB** para enriquecer o banco com título, capa, descrição, nota e plataformas. 

O projeto foi desenvolvido com foco em boas práticas de backend: arquitetura em camadas, autenticação stateless com Spring Security + JWT, Specifications para filtros opcionais, testes unitários e de integração, documentação com Swagger, containerização com Docker e monitoramento em produção. 

---

## 🚀 Principais recursos

- Cadastro, listagem, atualização e remoção de jogos.
- Cadastro e gerenciamento de reviews com controle de permissão por usuário.
- Busca paginada com filtros dinâmicos por gênero e plataforma.
- Importação de jogos da **IGDB** para o sistema.
- Suporte a **múltiplas plataformas** por jogo.
- Campos enriquecidos como `summary`, `rating` e `imageUrl`.
- Autenticação e autorização com JWT.
- Documentação interativa com Swagger/OpenAPI.
- Testes automatizados com JUnit 5, Mockito, MockMvc e H2.

---

## 🌐 Links

| Recurso | URL |
|---|---|
| API em produção | [https://jogos-review-api.onrender.com](https://jogos-review-api.onrender.com) |
| Swagger | [https://jogos-review-api.onrender.com/swagger-ui/index.html](https://jogos-review-api.onrender.com/swagger-ui/index.html) |
| Status | [https://game-critic.betteruptime.com](https://game-critic.betteruptime.com) |

> ⚠️ A API está hospedada no plano gratuito do Render, então a primeira requisição pode levar alguns segundos até o serviço despertar.

---

## 🧱 Tecnologias

| Tecnologia | Descrição |
|---|---|
| **Java 21** | Linguagem principal |
| **Spring Boot** | Framework da aplicação |
| **Spring Security** | Autenticação e autorização |
| **Spring Data JPA + Hibernate** | Persistência e acesso a dados |
| **JWT / Auth0 Java JWT** | Geração e validação de token |
| **MySQL** | Banco relacional em produção |
| **H2** | Banco em memória para testes |
| **Maven** | Build e dependências |
| **Swagger / OpenAPI** | Documentação interativa |
| **Docker / Docker Compose** | Containerização |
| **Render** | Deploy da aplicação |
| **BetterStack** | Monitoramento de uptime |
| **GitHub Actions** | Pipeline CI/CD |
| **IGDB API + Twitch OAuth2** | Integração externa para busca e importação de jogos |

---

## 🏗️ Arquitetura

A aplicação segue uma **arquitetura em camadas**, com separação clara entre entrada HTTP, regras de negócio, persistência e integração externa.

```text
src/main/java/desafio/review_jogos/
│
├── client/           → Integração externa com IGDB
│   └── dto/          → DTOs da API externa
├── config/           → Segurança, OpenAPI e filtros
├── controller/       → Endpoints REST
├── service/          → Regras de negócio
├── repository/       → Acesso ao banco de dados
├── model/            → Entidades JPA
│   └── enums/        → Enums de domínio
├── dto/              → DTOs internos
├── mapper/           → Conversão entre entidade e DTO
├── exception/        → Exceções customizadas e handler global
├── specification/    → Filtros dinâmicos
└── validation/       → Grupos de validação
```

---

## 🔗 Modelagem

### Relacionamento

```text
Usuario  ──────────────<  Review  >──────────────  Jogo
  (1)                     (N)  (N)                  (1)
```

Um usuário pode criar várias reviews, e cada review pertence a um único usuário e a um único jogo.

### Jogo

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | Long | Identificador único |
| `nome` | String | Nome do jogo |
| `genero` | `Genero` | Gênero principal do jogo |
| `plataformas` | `Set<Plataforma>` | Plataformas associadas |
| `imageUrl` | String | URL da capa |
| `summary` | String | Descrição do jogo |
| `rating` | BigDecimal | Nota externa importada |
| `createdAt` | LocalDateTime | Data de criação |
| `updatedAt` | LocalDateTime | Data de atualização |

### Review

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | Long | Identificador único |
| `nota` | Integer | Nota de 1 a 10 |
| `comentario` | String | Comentário da avaliação |
| `jogo` | Jogo | Jogo avaliado |
| `usuario` | Usuario | Autor da review |
| `createdAt` | LocalDateTime | Data de criação |
| `updatedAt` | LocalDateTime | Data de atualização |

### Usuario

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | Long | Identificador único |
| `email` | String | E-mail único |
| `nickname` | String | Nome de exibição |
| `senha` | String | Senha com hash BCrypt |
| `role` | Role | Papel do usuário |
| `createdAt` | LocalDateTime | Data de criação |
| `updatedAt` | LocalDateTime | Data de atualização |

---

## 🎯 Enums

### Gênero
```text
ACAO, AVENTURA, RPG, ESTRATEGIA, ESPORTES, CORRIDA, LUTA,
FPS, TPS, SURVIVAL, HORROR, PLATAFORMA, METROIDVANIA,
ROGUELIKE, SIMULACAO, PUZZLE, STEALTH, MUSICAL, VISUAL_NOVEL,
MOBILE, OUTROS
```

### Plataforma
```text
PS4, PS5, XBOX_ONE, XBOX_SERIES_X, XBOX_SERIES_S, PC,
NINTENDO_SWITCH, MOBILE, OUTROS
```

### Role
```text
ROLE_USER, ROLE_ADMIN
```

---

## 🔐 Autenticação e autorização

A API usa autenticação **stateless** com JWT.

### Fluxo

```text
POST /auth/registrar  → cria usuário
POST /auth/login      → valida credenciais e retorna token
Bearer Token          → acesso aos endpoints protegidos
```

### Controle de acesso

| Ação | Público | USER | ADMIN |
|---|---|---|---|
| Listar e buscar jogos | ✅ | ✅ | ✅ |
| Criar, editar e deletar jogo | ❌ | ❌ | ✅ |
| Criar review | ❌ | ✅ | ✅ |
| Atualizar própria review | ❌ | ✅ | ❌ |
| Deletar própria review | ❌ | ✅ | ✅ |
| Buscar/importar da IGDB | ❌ | ❌ | ✅ |

### Teste no Swagger

1. Use `POST /auth/registrar` para criar um usuário.
2. Faça login em `POST /auth/login`.
3. Copie o token.
4. Clique em **Authorize** no Swagger e cole o token sem o prefixo `Bearer`.

---

## 🌐 Endpoints

### Autenticação

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/auth/registrar` | Registra um usuário |
| `POST` | `/auth/login` | Retorna token JWT |

### Jogos

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/jogos` | Cadastra um jogo |
| `GET` | `/jogos` | Lista jogos com filtros e paginação |
| `GET` | `/jogos/{id}` | Busca jogo por ID |
| `PUT` | `/jogos/{id}` | Atualiza jogo |
| `DELETE` | `/jogos/{id}` | Remove jogo |

### Reviews

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/jogos/{id}/reviews` | Cria review para um jogo |
| `GET` | `/jogos/{id}/reviews` | Lista reviews do jogo |
| `PUT` | `/reviews/{id}` | Atualiza review |
| `DELETE` | `/reviews/{id}` | Remove review |

### Admin — IGDB

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/admin/jogos/buscar?termoBusca=` | Busca jogos na IGDB |
| `POST` | `/admin/jogos/importar` | Importa jogo para o sistema |

### Estatísticas

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/jogos/{id}/media` | Retorna média de notas do jogo |

---

## 📋 Exemplos

### Criar jogo

```json
{
  "nome": "God of War Ragnarok",
  "genero": "ACAO",
  "plataformas": ["PS5", "PC"],
  "imageUrl": "https://exemplo.com/capa.jpg",
  "summary": "Kratos e Atreus enfrentam novas ameaças no Ragnarok.",
  "rating": 9.50
}
```

### Resposta

```json
{
  "id": 1,
  "nome": "God of War Ragnarok",
  "genero": "ACAO",
  "plataformas": ["PS5", "PC"],
  "imageUrl": "https://exemplo.com/capa.jpg",
  "summary": "Kratos e Atreus enfrentam novas ameaças no Ragnarok.",
  "rating": 9.50,
  "createdAt": "2026-06-23T20:00:00",
  "updatedAt": "2026-06-23T20:00:00"
}
```

### Filtros

```text
GET /jogos
GET /jogos?genero=RPG
GET /jogos?plataforma=PS5
GET /jogos?genero=RPG&plataforma=PC
GET /jogos?page=0&size=5&sort=nome,asc
```

### Importação da IGDB

```json
{
  "idIgdb": 1020,
  "genero": "RPG"
}
```

### Erro padronizado

```json
{
  "status": 403,
  "erro": "Forbidden",
  "message": "Você não tem permissão para deletar esta review.",
  "timestamp": "2026-06-23T20:00:00"
}
```

---

## 🔍 Filtros dinâmicos

A listagem de jogos usa **Spring Data JPA Specification** para combinar filtros opcionais sem multiplicar métodos no repository.

```java
public static Specification<Jogo> porGenero(Genero genero) {
    return (root, query, cb) ->
            genero == null ? null : cb.equal(root.get("genero"), genero);
}
```

O mesmo conceito é usado para plataforma, permitindo combinar parâmetros na URL de forma flexível.

---

## ⚠️ Tratamento de exceções

A API usa `@RestControllerAdvice` para padronizar respostas de erro.

| Exceção | Status |
|---|---|
| `RecursoNaoEncontradoException` | 404 |
| `RecursoJaExisteException` | 409 |
| `MethodArgumentNotValidException` | 400 |
| `MethodArgumentTypeMismatchException` | 400 |
| `AccessDeniedException` | 403 |
| `IgdbIntegrationException` | 502 |
| `Exception` | 500 |

---

## 🧪 Testes

A aplicação possui testes unitários e de integração cobrindo regras críticas de negócio, autenticação, permissões e integração com a IGDB.

### Unitários
- `JogoServiceTest`
- `ReviewServiceTest`
- `TokenServiceTest`
- `IgdbImportacaoServiceTest`

### Integração
- `JogoControllerIT`
- `ReviewControllerIT`
- `AutenticacaoControllerIT`
- `AdminJogoControllerIT`

Execução:

```bash
mvn test
```

---

## 💡 Boas práticas aplicadas

- DTOs para entrada e saída
- Mapper dedicado
- Validação com Bean Validation
- Arquitetura em camadas
- JWT stateless
- BCrypt para senhas
- Controle de acesso por perfil
- Paginação e ordenação
- Specifications para filtros opcionais
- Testes unitários e de integração
- Integração com API externa
- Documentação com Swagger
- Deploy monitorado em produção

---

## ⚙️ Como executar localmente

### Pré-requisitos

- Java 21+
- Maven
- MySQL
- Docker (opcional)

### Com Docker

```bash
git clone https://github.com/Felipe-SMZ/jogos-review-api.git
cd jogos-review-api
docker-compose up
```

### Sem Docker

#### 1. Clone o repositório

```bash
git clone https://github.com/Felipe-SMZ/jogos-review-api.git
cd jogos-review-api
```

#### 2. Crie o banco

```sql
CREATE DATABASE review_jogos;
```

#### 3. Configure `application.properties`

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

#### 4. Execute

```bash
mvn spring-boot:run
```

#### 5. Acesse

```text
API:     http://localhost:8080
Swagger: http://localhost:8080/swagger-ui/index.html
```

---

## 👨‍💻 Autor

Desenvolvido por **Felipe Shimizu**

[![Portfolio](https://img.shields.io/badge/Portfólio-000000?style=for-the-badge&logo=vercel&logoColor=white)](https://www.devfelipeshimizu.me/)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/felipesshimizu/)