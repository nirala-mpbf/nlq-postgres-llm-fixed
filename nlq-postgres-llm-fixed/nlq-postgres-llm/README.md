# Natural Language -> PostgreSQL using Spring Boot + LangChain4j + OpenAI + PGVector

This project implements the requested flow:

User question -> Spring Boot REST API -> LangChain4j -> PGVector schema RAG -> OpenAI -> SQL validation -> PostgreSQL -> OpenAI answer -> User

## Stack

- Java 21
- Spring Boot 4.1.1
- PostgreSQL 17 + pgvector
- LangChain4j core/OpenAI 1.22.0 + PGVector 1.22.0-beta32
- OpenAI Chat Model
- OpenAI `text-embedding-3-small`
- Flyway
- JdbcTemplate

LangChain4j supports Java 17+ and provides OpenAI and PGVector integrations. The project uses PGVector in the same PostgreSQL instance so a separate vector database is not required for this MVP.

## Important design decision

The vector store contains **schema/business knowledge**, not employee rows.

PostgreSQL remains the source of truth for employee and department data.

Examples of RAG documents:

- employee table and column descriptions
- department table and column descriptions
- employee -> department relationship
- business terminology such as 10 lakh = 1,000,000 INR
- "employee count" = COUNT(employee.id)

## Project structure

```text
src/main/java/com/example/nlq/
  config/
    AiConfig.java
    PromptTemplates.java
  controller/
    QueryController.java
  dto/
    QueryRequest.java
    QueryResponse.java
  exception/
    GlobalExceptionHandler.java
  security/
    SqlValidator.java
  service/
    SchemaRagService.java
    SqlGenerationService.java
    SqlExecutionService.java
    AnswerGenerationService.java
    QueryService.java

src/main/resources/
  application.yml
  db/migration/
    V1__employee_department_schema.sql
    V2__sample_data.sql

db/
  01_extensions.sql
  02_expected_schema.sql
  03_existing_database_migration.sql

postman/
  NLQ-Postgres.postman_collection.json
```

## 1. Prerequisites

Install:

- JDK 21
- Maven 3.9+
- Docker Desktop (recommended for local PostgreSQL)
- An OpenAI API key

## 2. Start PostgreSQL + PGVector

```bash
docker compose up -d postgres
```

The database will be available at:

```text
jdbc:postgresql://localhost:5432/employee_db
username: employee_app
password: employee_app
```

Flyway will create the `employee` and `department` tables when the application starts.

## 3. Configure OpenAI

Windows PowerShell:

```powershell
$env:OPENAI_API_KEY="YOUR_KEY"
```

Linux/macOS:

```bash
export OPENAI_API_KEY="YOUR_KEY"
```

Or copy `.env.example` values into your environment/configuration.

Optional:

```text
OPENAI_CHAT_MODEL=gpt-4o-mini
OPENAI_EMBEDDING_MODEL=text-embedding-3-small
```

Both are configurable; do not hard-code API keys into source code.

## 4. Run the application

```bash
mvn spring-boot:run
```

On startup:

1. Flyway creates/updates the relational schema.
2. PGVector is enabled.
3. The application creates `nlq_schema_embeddings`.
4. The four schema/business documents are embedded and stored.

## 5. Test the REST API

### Question 1

```bash
curl -X POST http://localhost:8080/api/v1/query \
  -H "Content-Type: application/json" \
  -d '{"question":"Tell me employee between age 30-50"}'
```

### Question 2

```bash
curl -X POST http://localhost:8080/api/v1/query \
  -H "Content-Type: application/json" \
  -d '{"question":"Tell me employee having salary greater than 10 Lakh per annum"}'
```

### Question 3

```bash
curl -X POST http://localhost:8080/api/v1/query \
  -H "Content-Type: application/json" \
  -d '{"question":"Tell me no. of employee present in HR department"}'
```

The response contains the natural-language answer, the validated SQL and the database rows. In production you may remove `sql` from the public DTO and keep it only in audit logs.

## 6. Database changes

### If starting with a new database

Do nothing manually. Flyway migration `V1__employee_department_schema.sql` creates:

```text
employee
  id
  name
  age
  salary
  fk_id_department
  created_date
  modified_date

department
  id
  name
  type
  created_date
  modified_date
```

### If you already have the tables

Review and run `db/03_existing_database_migration.sql` against your database. It:

- enables pgvector
- renames `slary` to `salary` if that typo exists and `salary` does not
- adds missing `id` columns
- adds missing timestamp columns
- adds indexes
- adds the employee -> department foreign key if missing

**Do not blindly run this migration in production. Review it first and take a backup.**

The application assumes:

```sql
employee.fk_id_department = department.id
```

## 7. Security model

The application does not execute arbitrary LLM output directly.

Flow:

```text
OpenAI generated SQL
       |
       v
SqlValidator
       |
       +-- SELECT only
       +-- no comments
       +-- no multiple statements
       +-- no INSERT/UPDATE/DELETE/DROP/etc.
       +-- no system schemas
       +-- only employee/department tables
       +-- automatic LIMIT for row queries
       |
       v
JdbcTemplate -> PostgreSQL
```

For production, also create a dedicated PostgreSQL user with only `SELECT` privileges:

```sql
CREATE USER nlq_readonly WITH PASSWORD 'change-me';
GRANT CONNECT ON DATABASE employee_db TO nlq_readonly;
GRANT USAGE ON SCHEMA public TO nlq_readonly;
GRANT SELECT ON employee, department TO nlq_readonly;
```

Then configure the application with that account.

## 8. Why PGVector is in PostgreSQL

A separate vector database is not necessary for this MVP. LangChain4j supports PGVector directly, so the same PostgreSQL instance can hold both:

- relational application data
- vectorized schema/business knowledge

This is simpler operationally and matches the architecture you described.

## 9. Production improvements

Before production, consider:

- Hide generated SQL from public API responses.
- Use a read-only DB user.
- Add SQL parser/AST validation instead of regex-only validation.
- Add authentication/authorization to the REST API.
- Add query timeout and rate limits.
- Add audit logging of question, generated SQL, user and execution time.
- Version/update RAG schema documents instead of rebuilding them on every startup.
- Add tests for prompt injection and unsafe SQL.
- Add pagination for large result sets.
- Add observability/token-cost metrics.

## 10. Example generated SQL

Question:

```text
Tell me no. of employee present in HR department
```

Expected SQL shape:

```sql
SELECT COUNT(e.id) AS employee_count
FROM employee e
JOIN department d ON e.fk_id_department = d.id
WHERE d.name = 'HR'
```

Question:

```text
Tell me employee having salary greater than 10 Lakh per annum
```

Expected SQL shape:

```sql
SELECT name, age, salary, fk_id_department
FROM employee
WHERE salary > 1000000
```


## Important dependency-version note

The project intentionally uses the LangChain4j versions recommended by the current documentation: the core/OpenAI artifacts use `1.22.0`, while the PGVector integration is `1.22.0-beta32`. Do not change all LangChain4j artifacts to `1.22.0-beta32`; that beta version is not published for the `langchain4j` core artifact.

If IntelliJ previously cached the failed `1.22.0-beta32` lookup, use **Maven -> Reload All Maven Projects** and, if necessary, run `mvn -U clean package` once from a terminal.
