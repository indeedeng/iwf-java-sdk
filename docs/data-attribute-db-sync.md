# Data Attribute → Postgres sync (user guide)

This feature lets you automatically mirror an iWF **data attribute** to a single cell (a row + column)
of an arbitrary, **user-owned** Postgres table:

- **Load on start**: when a workflow starts, the data attribute's initial value is loaded from the
  mapped database column.
- **Sync on mutation**: whenever the data attribute is mutated inside a state's `execute` method, the
  new value is written back to the same column.

It is entirely an **SDK-side** feature — iWF's server is not involved in your database, and you own the
database schema, credentials, and connection pooling.

## Mental model

The iWF data attribute is the **source of truth**; the Postgres cell is a **mirror**.

- On start, the mirror seeds the data attribute (load).
- During execution, the data attribute drives the mirror (sync).
- iWF persists the data attribute first, then the database is updated. If the database write fails, only
  the (internal) sync step is retried — your `execute` code is **not** re-run.

## Prerequisites

1. A Postgres table you own, with:
   - a **primary key column** (text/uuid/numeric — the PK value is bound as a string parameter), and
   - a **data column** of type `text` or `jsonb` (the SDK stores the data attribute's JSON there).
2. A configured `javax.sql.DataSource`. You own it — the SDK never stores credentials and never creates
   connections other than via `dataSource.getConnection()`. Use any driver/pool (HikariCP,
   `PGSimpleDataSource`, etc.).

Example table:

```sql
CREATE TABLE orders (
    id         TEXT PRIMARY KEY,
    status_col TEXT
);
INSERT INTO orders (id, status_col) VALUES ('order-123', '"NEW"');
```

## Setup

### 1. Build a DataSource

```java
import org.postgresql.ds.PGSimpleDataSource;
import javax.sql.DataSource;

PGSimpleDataSource ds = new PGSimpleDataSource();
ds.setUrl("jdbc:postgresql://localhost:5432/mydb");
ds.setUser("app");
ds.setPassword(System.getenv("DB_PASSWORD")); // keep credentials out of code
// (in production prefer a pooled DataSource, e.g. HikariCP)
```

### 2. Declare the data attribute with a DB-sync mapping

Use the `DataAttributeDef.create(type, key, DbAttributeSync)` overload in your workflow's
`getPersistenceSchema()`:

```java
import io.iworkflow.core.persistence.*;

public class OrderWorkflow implements ObjectWorkflow {
    public static final String STATUS = "status";

    @Override
    public List<PersistenceFieldDef> getPersistenceSchema() {
        return Arrays.asList(
            DataAttributeDef.create(
                String.class,
                STATUS,
                DbAttributeSync.of(
                    /* dataSource   */ MyDataSources.ORDERS,
                    /* table        */ "orders",          // optionally schema-qualified: "public.orders"
                    /* pk column    */ "id",
                    /* locator      */ (workflowId, persistence) ->
                        new CellLocation(workflowId, "status_col"))));
    }

    @Override
    public List<StateDef> getWorkflowStates() {
        return Arrays.asList(StateDef.startingState(new DecideState()));
    }
}
```

**The locator** `(String workflowId, Persistence persistence) -> CellLocation` returns which cell to
load-from / sync-to:

- `CellLocation.getPkValue()` — the primary-key **value** identifying the row (bound as a parameter).
- `CellLocation.getColumnName()` — the **data column** name.

The table name and the primary-key column name are fixed in `DbAttributeSync.of(...)`. The locator can
compute the PK value from the `workflowId` and/or from the current `persistence` (other data attributes
already set at that point).

### 3. Use the data attribute normally

```java
public class DecideState implements WorkflowState<String> {
    @Override public Class<String> getInputType() { return String.class; }

    @Override
    public StateDecision execute(Context ctx, String in, CommandResults r,
                                 Persistence persistence, Communication comm) {
        String status = persistence.getDataAttribute(OrderWorkflow.STATUS, String.class); // loaded from DB
        // ... business logic ...
        persistence.setDataAttribute(OrderWorkflow.STATUS, "PROCESSED"); // mirrored back to DB
        return StateDecision.gracefulCompleteWorkflow(status);
    }
}
```

Start the workflow as usual with the registered `Client` — the load happens automatically:

```java
client.startWorkflow(OrderWorkflow.class, "order-123", 3600, "start");
```

## Behavior / semantics

| Aspect | Behavior |
|---|---|
| When load fires | Once, at workflow start (via the registered `Client`). |
| When sync fires | After a state's `execute` mutates a mapped data attribute. |
| Ordering | iWF persists the data attribute first; then the DB `UPDATE` runs. |
| Retry / idempotency | The DB write is a single-row idempotent `UPDATE`; safe to retry. On failure only the internal sync step retries — your `execute` is not re-run. |
| Stored value | The data attribute's `ObjectEncoder` JSON (same bytes iWF stores). Use a `text`/`jsonb` column. |
| Multiple synced attributes | Each maps to its own cell; all load at start and each syncs when mutated. |

## Column & type guidance

The mapped column stores the data attribute's JSON. For a `String` attribute of value `NEW`, the column
holds the JSON string `"NEW"`. For complex objects it holds the serialized JSON object. This round-trips
any type uniformly, so use `text` or `jsonb`. Mapping to a native scalar column (e.g. an `integer`
column) is **not** supported in v1.

## Limitations (v1)

- **Fixed-key data attributes only** — `DataAttributeDef.createByPrefix(...)` is not supported for DB
  sync (a `WorkflowDefinitionException` is thrown at registration if you try).
- **`execute` mutations only** — a data attribute mutated in `waitUntil` (and not in `execute`) is not
  synced. Mutate DB-synced data attributes in `execute`.
- **Started via the registered `Client`** — starts issued directly against the server API / another SDK
  with an explicit start stateId bypass the load step.

## Troubleshooting

| Symptom | Likely cause |
|---|---|
| Data attribute is `null` after start | Row/PK/column mismatch — the locator's PK value or column name doesn't match an existing row/column, or the column is null. |
| Column not updated after a state | The attribute was mutated in `waitUntil` (not `execute`), or the mutated key isn't the DB-mapped one. |
| `unsafe SQL identifier` error | The table / PK / data-column name isn't a plain identifier (`^[A-Za-z_][A-Za-z0-9_]*$`, optionally schema-qualified). Rename or quote-safe your identifiers. |
| Connection / pool errors | Check the `DataSource` config, network access from the **worker** process to Postgres, and pool sizing. |
| `WorkflowDefinitionException: DB sync is not supported for prefix data attributes` | You used `createByPrefix` with a `DbAttributeSync`; use a fixed key. |

## Security notes

- **Credentials** are never held by the SDK — you supply a configured `DataSource`.
- **Values** (primary-key value and the data value) are always bound via `PreparedStatement` parameters.
- **Identifiers** (table, PK column, data column) cannot be bound; they are validated against a strict
  allowlist and double-quoted, so a malformed identifier is rejected rather than injected.
