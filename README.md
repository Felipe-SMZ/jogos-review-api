# 🎮 Review de Jogos — API REST

> API REST para gerenciamento de jogos e avaliações, desenvolvida com Java e Spring Boot.

![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow?style=for-the-badge)

---

## 📌 Sobre o Projeto

O **Review de Jogos** é uma API REST desenvolvida para o gerenciamento de jogos e avaliações. Por meio da API, é possível cadastrar jogos com informações como nome, gênero e plataforma, além de adicionar reviews com notas e comentários para cada jogo.

O projeto foi desenvolvido com foco em boas práticas de desenvolvimento backend, separação de responsabilidades em camadas e documentação interativa via Swagger/OpenAPI.

---

## 🧱 Tecnologias Utilizadas

| Tecnologia | Descrição |
|---|---|
| **Java 17+** | Linguagem principal do projeto |
| **Spring Boot** | Framework para criação da aplicação |
| **Spring Web** | Criação dos endpoints REST |
| **Spring Data JPA** | Abstração de acesso ao banco de dados |
| **Hibernate** | ORM para mapeamento objeto-relacional |
| **MySQL** | Banco de dados relacional |
| **Maven** | Gerenciamento de dependências e build |
| **Swagger / OpenAPI** | Documentação interativa da API |

---

## 🏗️ Arquitetura do Projeto

O projeto segue uma **arquitetura em camadas**, promovendo separação de responsabilidades e facilitando a manutenção e escalabilidade do código.

```
src/main/java/com/seuusuario/reviewdejogos/
│
├── config/         → Configurações da aplicação (Swagger/OpenAPI)
├── controller/     → Recebe e processa as requisições HTTP
├── service/        → Contém as regras de negócio
├── repository/     → Acesso e operações no banco de dados
├── model/          → Entidades JPA mapeadas para o banco
├── dto/            → Objetos de Transferência de Dados (DTOs)
└── mapper/         → Conversão entre entidades e DTOs
```

---

## 🔗 Modelagem das Entidades

### 🎮 Jogo

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | Long | Identificador único |
| `nome` | String | Nome do jogo |
| `genero` | String | Gênero do jogo |
| `plataforma` | String | Plataforma disponível |

### ⭐ Review

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | Long | Identificador único |
| `nota` | Integer | Nota de 1 a 10 |
| `comentario` | String | Comentário da avaliação |
| `jogo` | Jogo | Relacionamento com a entidade Jogo |

### 📐 Relacionamento

```
Jogo  ──────────────<  Review
(1)                    (N)
```

Um jogo pode ter **muitas reviews**, mas cada review pertence a **um único jogo** (relacionamento `@OneToMany` / `@ManyToOne`).

---

## 🚀 Funcionalidades

- [x] Cadastrar um novo jogo
- [x] Listar todos os jogos
- [x] Buscar um jogo por ID
- [x] Deletar um jogo
- [x] Criar uma review para um jogo
- [x] Listar todas as reviews de um jogo
- [x] Deletar uma review
- [x] Calcular a média de notas de um jogo

---

## 🌐 Endpoints da API

### 🎮 Jogos

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/jogos` | Cadastra um novo jogo |
| `GET` | `/jogos` | Lista todos os jogos |
| `GET` | `/jogos/{id}` | Busca um jogo pelo ID |
| `DELETE` | `/jogos/{id}` | Remove um jogo pelo ID |

### ⭐ Reviews

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/jogos/{id}/reviews` | Cria uma review para um jogo |
| `GET` | `/jogos/{id}/reviews` | Lista as reviews de um jogo |
| `DELETE` | `/reviews/{id}` | Remove uma review pelo ID |

### 📊 Estatísticas

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/jogos/{id}/media` | Retorna a média de notas de um jogo |

---

## 📋 Exemplos de Requisição

### Criar um jogo — `POST /jogos`

**Request Body:**
```json
{
  "nome": "God of War",
  "genero": "Ação",
  "plataforma": "PS5"
}
```

**Response `201 Created`:**
```json
{
  "id": 1,
  "nome": "God of War",
  "genero": "Ação",
  "plataforma": "PS5"
}
```

---

### Criar uma review — `POST /jogos/1/reviews`

**Request Body:**
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

---

### Consultar média de notas — `GET /jogos/1/media`

**Response `200 OK`:**
```json
{
  "jogoId": 1,
  "nome": "God of War",
  "mediaNotas": 9.0
}
```

---

## 📘 Documentação da API — Swagger

O projeto conta com documentação interativa gerada automaticamente pelo **Swagger / OpenAPI**, permitindo visualizar e testar todos os endpoints diretamente pelo navegador.

Após iniciar a aplicação, acesse:

```
http://localhost:8080/swagger-ui/index.html
```

No Swagger é possível:

- Visualizar todos os endpoints disponíveis
- Testar requisições diretamente pelo navegador
- Inspecionar parâmetros de entrada e formatos de resposta
- Consultar os modelos de dados (schemas)

---

## ⚙️ Como Executar o Projeto

### Pré-requisitos

- [Java 17+](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
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

Crie um banco de dados MySQL:

```sql
CREATE DATABASE review_jogos;
```

Crie o arquivo `src/main/resources/application.properties` com suas credenciais, baseado no arquivo de exemplo:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/review_jogos
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

**3. Execute o projeto**

Via Maven:

```bash
mvn spring-boot:run
```

Ou importe o projeto na sua IDE e execute a classe principal `ReviewDeJogosApplication.java`.

**4. Acesse a API**

```
http://localhost:8080
```

---

## 💡 Boas Práticas Aplicadas

- ✅ **DTOs** para não expor as entidades JPA diretamente nas respostas
- ✅ **Bean Validation** para validação dos dados de entrada
- ✅ **Arquitetura em camadas** com separação clara de responsabilidades
- ✅ **Mapper** dedicado para conversão entre entidades e DTOs
- ✅ **Tratamento de exceções** com respostas de erro padronizadas
- ✅ **Documentação automática** com Swagger / OpenAPI

---

## 📈 Melhorias Futuras

-  ✅ Paginação e ordenação dos resultados
- [ ] Filtro de jogos por gênero ou plataforma
- [ ] Autenticação e autorização com Spring Security + JWT
- [ ] Tratamento global de exceções com `@ControllerAdvice`
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