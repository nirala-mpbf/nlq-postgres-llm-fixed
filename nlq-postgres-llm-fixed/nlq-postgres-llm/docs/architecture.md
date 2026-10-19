# Architecture

```text
                         +----------------+
                         |      User      |
                         +-------+--------+
                                 |
                         Natural language
                                 |
                                 v
                    +------------+-------------+
                    |       Spring Boot        |
                    |        REST API          |
                    +------------+-------------+
                                 |
                                 v
                    +------------+-------------+
                    |       Query Service      |
                    +------------+-------------+
                                 |
                    +------------+-------------+
                    |       LangChain4j        |
                    +------+----------------+---+
                           |                |
                    schema RAG             |
                           |                |
                           v                v
                    +------+-----+   +-----+------+
                    |  PGVector  |   |   OpenAI   |
                    | schema/RAG |   |  SQL LLM   |
                    +------------+   +-----+------+
                                          |
                                          | generated SQL
                                          v
                                   +------+-------+
                                   | SQL Validator|
                                   +------+-------+
                                          |
                                          | SELECT
                                          v
                                   +------+-------+
                                   |  PostgreSQL  |
                                   |              |
                                   | employee     |
                                   | department   |
                                   +------+-------+
                                          |
                                          | rows
                                          v
                                   +------+-------+
                                   |   OpenAI /   |
                                   | Answer Layer |
                                   +------+-------+
                                          |
                                          v
                                        User
```
