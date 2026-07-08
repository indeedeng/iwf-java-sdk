-- Initializes the schema/table and the initial dataset used by the data-attribute DB-sync
-- integration test (io.iworkflow.integ.DbSyncTest). Postgres runs any file in
-- /docker-entrypoint-initdb.d/ once on first container start, against POSTGRES_DB (iwf_integ).

CREATE TABLE IF NOT EXISTS iwf_integ_data (
    pk       TEXT PRIMARY KEY,
    data_col TEXT
);

-- The value is stored as JSON text (the SDK mirrors the data attribute's ObjectEncoder JSON), so a
-- String data attribute of value seeded-value is stored as the JSON string "seeded-value".
INSERT INTO iwf_integ_data (pk, data_col)
VALUES ('db-sync-test-key', '"seeded-value"')
ON CONFLICT (pk) DO UPDATE SET data_col = EXCLUDED.data_col;
