CREATE CONNECTOR
================

Create a new connector for fetching data from or writing data to an external system. A connector can be either a source or a sink one.


## Synopsis

Create source connector:

```sql
CREATE SOURCE CONNECTOR connector_name FROM source_name WITH (connector_option [, ...]);
```

Create sink connector:

```sql
CREATE SINK CONNECTOR connector_name TO sink_name WITH (connector_option [, ...]);
```

## Notes

- `connector_name` is a valid identifier.
- `source_name` is a valid identifier(`mysql`, `postgresql` etc.).
- There is are some connector options in the `WITH` clause separated by commas.

check [Connectors](https://hstream.io/docs/en/latest/io/connectors.html) to find the connectors and their configuration options .

## Examples

```sql
create source connector source01 from mysql with ("host" = "mysql-s1", "port" = 3306, "user" = "root", "password" = "password", "database" = "d1", "table" = "person", "stream" = "stream01");
```

```sql
create sink connector sink01 to postgresql with ("host" = "pg-s1", "port" = 5432, "user" = "postgres", "password" = "postgres", "database" = "d1", "table" = "person", "stream" = "stream01");
```
