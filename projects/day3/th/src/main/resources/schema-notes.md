# Schema Notes — chat_messages table

## Table definition

Hibernate generates the table automatically on startup (`ddl-auto: update`).
The equivalent SQL is shown below for documentation purposes.

```sql
CREATE TABLE chat_messages (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    sender    VARCHAR(64)  NOT NULL,
    content   VARCHAR(2048),          -- NULL for JOIN/LEAVE events
    timestamp TIMESTAMP    NOT NULL,
    type      VARCHAR(8)   NOT NULL,  -- 'CHAT', 'JOIN', or 'LEAVE'
    PRIMARY KEY (id)
);
```

### Column notes

| Column      | Type          | Nullable | Description                                      |
|-------------|---------------|----------|--------------------------------------------------|
| `id`        | BIGINT        | No       | Auto-incremented surrogate key                   |
| `sender`    | VARCHAR(64)   | No       | Username chosen on the login page                |
| `content`   | VARCHAR(2048) | Yes      | Message text; NULL for system JOIN/LEAVE events  |
| `timestamp` | TIMESTAMP     | No       | UTC time set by `MessageServiceImpl` before save |
| `type`      | VARCHAR(8)    | No       | Enum value: `CHAT`, `JOIN`, or `LEAVE`           |

---

## Why H2 in-memory?

H2 in-memory is used for this project because:

- **Zero configuration** — no external database process or connection string needed.
- **Fast startup** — the schema is created in milliseconds, ideal for demos and CI.
- **Self-contained deployment** — the JAR contains everything; no separate DB container.

**Limitation:** All data is lost when the JVM shuts down. This is acceptable for a
demo/classroom project but not for production use where message history must survive
restarts or scale-out.

---

## Migration path to Azure SQL Database

When persistent storage is required, follow these steps:

### 1. Add the MSSQL JDBC driver to `pom.xml`

```xml
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <scope>runtime</scope>
</dependency>
```

Remove or set the H2 dependency to `test` scope.

### 2. Provision an Azure SQL Database

```bash
az sql server create \
    --name chat-sql-server \
    --resource-group chat-rg \
    --location eastus \
    --admin-user chatadmin \
    --admin-password <STRONG_PASSWORD>

az sql db create \
    --resource-group chat-rg \
    --server chat-sql-server \
    --name chatdb \
    --service-objective S0
```

### 3. Update `application-prod.yml`

```yaml
spring:
  datasource:
    url: jdbc:sqlserver://<server>.database.windows.net:1433;database=chatdb;encrypt=true;trustServerCertificate=false;
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate   # never drop tables in prod
    database-platform: org.hibernate.dialect.SQLServerDialect
```

### 4. Set App Service environment variables

In the Azure Portal → App Service → Configuration → Application settings:

| Name                       | Value                              |
|----------------------------|------------------------------------|
| `SPRING_PROFILES_ACTIVE`   | `prod`                             |
| `DB_USERNAME`              | `chatadmin`                        |
| `DB_PASSWORD`              | *(stored in Azure Key Vault ref)*  |

### 5. Schema migration

For a production schema, replace `ddl-auto: update` with a proper migration tool
such as **Flyway** or **Liquibase**. Add the starter to `pom.xml` and place SQL
scripts under `src/main/resources/db/migration/`.
