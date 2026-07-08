# Data Attribute → Postgres sync (overview)

Automatically mirror a workflow **data attribute** to a cell (row + column) of a user-owned Postgres
table. The iWF data attribute stays the source of truth; the database cell is a mirror.

- **Load on start** — when the workflow starts, the data attribute is loaded from its mapped column.
- **Sync on mutation** — whenever the data attribute is mutated in a state's `execute` method, the new
  value is written back to the same column.

```java
DataSource ds = /* your configured, pooled DataSource */;

DataAttributeDef.create(
    String.class,
    "status",
    DbAttributeSync.of(ds, "public.orders", "id",
        (workflowId, persistence) -> new CellLocation(workflowId, "status_col")));
```

## How it works (3 lines)

1. `Client.startWorkflow` starts at a load system state that `SELECT`s each mapped column and sets the
   data attribute, then transitions to your real starting state (input passes through).
2. When a state's `execute` mutates a mapped data attribute, the worker reroutes the decision through a
   sync system state that `UPDATE`s the column, then continues to your original decision.
3. iWF persists the data attribute first, then the DB write happens — so a DB failure retries only the
   sync step, never your `execute`. Neither system state is registered; both are worker-internal.

## Key limitations (v1)

- Fixed-key data attributes only (not `createByPrefix`).
- Sync fires on `execute` mutations only (not `waitUntil`).
- Load fires for workflows started through the registered `Client`.
- The mapped column stores the data attribute's JSON — use a `text`/`jsonb` column.

See the detailed guide: [data-attribute-db-sync.md](./data-attribute-db-sync.md).
