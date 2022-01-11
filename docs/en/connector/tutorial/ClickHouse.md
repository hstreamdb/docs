# How to use HStream connectors with ClickHouse

TODO

### Clickhouse Connector

connector options and default value for clickhouse includes:

```
username = "default"
password = ""
host = "127.0.0.1"
port = 9000
database = "default"
```

An example to create a clickhouse connector which subscribes from stream
`source` and write data to clickhouse looks like:

```sql
CREATE SINK CONNECTOR mysql WITH (type = clickhouse, host = "127.0.0.1", port = 9000, stream = source);
```

The clickhouse table name will be the same as the stream name.