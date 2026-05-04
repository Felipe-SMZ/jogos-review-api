# 🎮 Review de Jogos — API REST

> API REST para gerenciamento de jogos e avaliações, desenvolvida com Java e Spring Boot.

![Java](https://img.shields.io/badge/Java-21+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow?style=for-the-badge)

---

## 📌 Sobre o Projeto

O **Review de Jogos** é uma API REST desenvolvida para o gerenciamento de jogos e avaliações. Por meio da API, é
possível cadastrar jogos com informações como nome, gênero e plataforma, além de adicionar reviews com notas e
comentários para cada jogo.

O projeto foi desenvolvido com foco em boas práticas de desenvolvimento backend, separação de responsabilidades em
camadas e documentação interativa via Swagger/OpenAPI.

---

## 🧱 Tecnologias Utilizadas

| Tecnologia                  | Descrição                             |
|-----------------------------|---------------------------------------|
| **Java 21+**                | Linguagem principal do projeto        |
| **Spring Boot 4**           | Framework para criação da aplicação   |
| **Spring Web**              | Criação dos endpoints REST            |
| **Spring Data JPA**         | Abstração de acesso ao banco de dados |
| **Hibernate**               | ORM para mapeamento objeto-relacional |
| **MySQL**                   | Banco de dados relacional             |
| **Maven**                   | Gerenciamento de dependências e build |
| **Swagger / OpenAPI 2.8.8** | Documentação interativa da API        |

---

## 🏗️ Arquitetura do Projeto

O projeto segue uma **arquitetura em camadas**, promovendo separação de responsabilidades e facilitando a manutenção e
escalabilidade do código.

```
src/main/java/desafio/review_jogos/
│
├── config/           → Configurações da aplicação (Swagger/OpenAPI)
├── controller/       → Recebe e processa as requisições HTTP
├── service/          → Contém as regras de negócio
├── repository/       → Acesso e operações no banco de dados
├── model/            → Entidades JPA mapeadas para o banco
│   └── enums/        → Enums de Gênero e Plataforma
├── dto/              → Objetos de Transferência de Dados (DTOs)
├── mapper/           → Conversão entre entidades e DTOs
├── exception/        → Exceções customizadas e handler global
├── specification/    → Filtros dinâmicos com Spring Specification
└── validation/       → Grupos de validação (OnCreate, OnUpdate)
```

---

## 🔗 Modelagem das Entidades

### 🎮 Jogo

| Campo        | Tipo              | Descrição             |
|--------------|-------------------|-----------------------|
| `id`         | Long              | Identificador único   |
| `nome`       | String            | Nome do jogo          |
| `genero`     | Genero (enum)     | Gênero do jogo        |
| `plataforma` | Plataforma (enum) | Plataforma disponível |

### ⭐ Review

| Campo        | Tipo    | Descrição                          |
|--------------|---------|------------------------------------|
| `id`         | Long    | Identificador único                |
| `nota`       | Integer | Nota de 1 a 10                     |
| `comentario` | String  | Comentário da avaliação            |
| `jogo`       | Jogo    | Relacionamento com a entidade Jogo |

### 📐 Relacionamento

```
Jogo  ──────────────<  Review
(1)                    (N)
```

Um jogo pode ter **muitas reviews**, mas cada review pertence a **um único jogo** (`@OneToMany` / `@ManyToOne`).

---

## 🎯 Enums

### Gênero

```
ACAO, AVENTURA, RPG, ESTRATEGIA, ESPORTES, CORRIDA, LUTA,
FPS, TPS, SURVIVAL, HORROR, PLATAFORMA, METROIDVANIA,
ROGUELIKE, SIMULACAO, PUZZLE, STEALTH, MUSICAL, VISUAL_NOVEL, MOBILE
```

### Plataforma

```
PS4, PS5, XBOX_ONE, XBOX_SERIES_X, XBOX_SERIES_S,
PC, NINTENDO_SWITCH, MOBILE
```

---

## 🚀 Funcionalidades

- [x] Cadastrar um novo jogo
- [x] Listar todos os jogos (com paginação e ordenação)
- [x] Filtrar jogos por gênero e/ou plataforma
- [x] Buscar um jogo por ID
- [x] Atualizar um jogo
- [x] Deletar um jogo
- [x] Criar uma review para um jogo
- [x] Listar todas as reviews de um jogo (com paginação)
- [x] Deletar uma review
- [x] Calcular a média de notas de um jogo
- [x] Tratamento global de exceções com respostas padronizadas

---

## 🌐 Endpoints da API

### 🎮 Jogos

| Método   | Endpoint      | Descrição                                          |
|----------|---------------|----------------------------------------------------|
| `POST`   | `/jogos`      | Cadastra um novo jogo                              |
| `GET`    | `/jogos`      | Lista todos os jogos (suporta filtros e paginação) |
| `GET`    | `/jogos/{id}` | Busca um jogo pelo ID                              |
| `PUT`    | `/jogos/{id}` | Atualiza um jogo pelo ID                           |
| `DELETE` | `/jogos/{id}` | Remove um jogo pelo ID                             |

### ⭐ Reviews

| Método   | Endpoint              | Descrição                                   |
|----------|-----------------------|---------------------------------------------|
| `POST`   | `/jogos/{id}/reviews` | Cria uma review para um jogo                |
| `GET`    | `/jogos/{id}/reviews` | Lista as reviews de um jogo (com paginação) |
| `DELETE` | `/reviews/{id}`       | Remove uma review pelo ID                   |

### 📊 Estatísticas

| Método | Endpoint            | Descrição                           |
|--------|---------------------|-------------------------------------|
| `GET`  | `/jogos/{id}/media` | Retorna a média de notas de um jogo |

---

## 📋 Exemplos de Requisição

### Criar um jogo — `POST /jogos`

```json
{
  "nome": "God of War",
  "genero": "ACAO",
  "plataforma": "PS5"
}
```

**Response `201 Created`:**

```json
{
  "id": 1,
  "nome": "God of War",
  "genero": "ACAO",
  "plataforma": "PS5"
}
```

### Listar jogos com filtros — `GET /jogos`

```
GET /jogos                                        → todos os jogos
GET /jogos?genero=RPG                             → filtrado por gênero
GET /jogos?plataforma=PS5                         → filtrado por plataforma
GET /jogos?genero=RPG&plataforma=PS5              → filtrado pelos dois
GET /jogos?genero=RPG&page=0&size=5&sort=nome,asc → com paginação
```

### Criar uma review — `POST /jogos/1/reviews`

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
  "jogoId": 1
}
```

### Consultar média de notas — `GET /jogos/1/media`

**Response `200 OK`:**

```json
{
  "jogoId": 1,
  "nome": "God of War",
  "mediaNotas": 9.0
}
```

### Resposta de erro padronizada

```json
{
  "status": 404,
  "erro": "Not Found",
  "message": "Jogo com id 99 não encontrado.",
  "timestamp": "2026-05-02T10:30:00"
}
```

---

## 🔍 Filtros Dinâmicos com Specification

O projeto utiliza **Spring Data JPA Specification** para permitir filtros opcionais sem duplicar métodos no repository.

**Por que usar Specification?**
Sem ela, cada combinação de filtros exigiria um método diferente no repository (`findByGenero`, `findByPlataforma`,
`findByGeneroAndPlataforma`...). Com Specification, um único método cobre todos os casos.

**Como funciona:**

```java
// JogoSpecification.java
public static Specification<Jogo> porGenero(Genero genero) {
    return (root, query, cb) ->
            genero == null ? null : cb.equal(root.get("genero"), genero);
}
```

Quando o filtro é `null`, retorna `null` e o Spring Data ignora aquela condição automaticamente.

**Como combinar no Service:**

```java
Specification<Jogo> spec = Specification
        .where(JogoSpecification.porGenero(genero))
        .and(JogoSpecification.porPlataforma(plataforma));

return jogoRepository.

findAll(spec, pageable).

map(JogoMapper::toResponse);
```

**Para adicionar um novo filtro**, basta criar um novo método estático na `JogoSpecification` e encadeá-lo com `.and()`
no Service.

**Requisito:** o repository deve estender `JpaSpecificationExecutor<Jogo>`.

---

## ⚠️ Tratamento de Exceções

O projeto usa `@RestControllerAdvice` para capturar exceções e retornar respostas padronizadas.

| Exceção                           | Status | Quando ocorre                 |
|-----------------------------------|--------|-------------------------------|
| `RecursoNaoEncontradoException`   | 404    | Jogo ou review não encontrado |
| `RecursoJaExisteException`        | 409    | Jogo com nome duplicado       |
| `MethodArgumentNotValidException` | 400    | Dados de entrada inválidos    |
| `Exception`                       | 500    | Erros inesperados             |

---

## 📘 Documentação da API — Swagger

Após iniciar a aplicação, acesse:

```
http://localhost:8080/swagger-ui/index.html
```

---

## ⚙️ Como Executar o Projeto

### Pré-requisitos

- [Java 21+](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
- [Maven](https://maven.apache.org/)
- [MySQL](https://www.mysql.com/)
- IDE de sua preferência (IntelliJ IDEA ou Eclipse)

### Passo a passo

**1. Clone o repositório**

```bash
git clone https://github.com/Felipe-SMZ/jogos-review-api.git
cd review-de-jogos
```

**2. Configure o banco de dados**

```sql
CREATE
DATABASE review_jogos;
```

Crie o arquivo `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/review_jogos?serverTimezone=America/Sao_Paulo
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
springdoc.default-flat-param-object=true
```

**3. Execute o projeto**

```bash
mvn spring-boot:run
```

**4. Acesse a API**

```
http://localhost:8080
```

---

## 💡 Boas Práticas Aplicadas

- ✅ **DTOs** para não expor as entidades JPA diretamente nas respostas
- ✅ **Enums** para garantir consistência nos dados de gênero e plataforma
- ✅ **Bean Validation** com grupos `OnCreate` e `OnUpdate`
- ✅ **Arquitetura em camadas** com separação clara de responsabilidades
- ✅ **Mapper** dedicado para conversão entre entidades e DTOs
- ✅ **Tratamento global de exceções** com `@RestControllerAdvice`
- ✅ **Paginação e ordenação** com `Pageable` e `@PageableDefault`
- ✅ **Filtros dinâmicos** com Spring Data Specification
- ✅ **Documentação automática** com Swagger / OpenAPI

---

## 📈 Melhorias Futuras

- [x] Paginação e ordenação dos resultados
- [x] Filtro de jogos por gênero ou plataforma
- [x] Tratamento global de exceções com `@ControllerAdvice`
- [ ] Autenticação e autorização com Spring Security + JWT
- [ ] Testes unitários e de integração com JUnit e Mockito
- [ ] Deploy em nuvem (Render, Railway ou AWS)
- [ ] Containerização com Docker

---

## 👨‍💻 Autor

Desenvolvido por **Felipe Shimizu**

- 🌐 Portfólio: [https://www.devfelipeshimizu.me/](https://www.devfelipeshimizu.me/)
- 💼 LinkedIn: [https://www.linkedin.com/in/felipesshimizu/](https://www.linkedin.com/in/felipesshimizu/)

Desenvolvido para fins de estudo e aprimoramento em desenvolvimento backend com **Java** e **Spring Boot**.

---

> ⭐ Se este projeto foi útil para você, considere deixar uma estrela no repositório!