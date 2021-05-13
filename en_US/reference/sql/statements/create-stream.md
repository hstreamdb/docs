# CREATE STREAM

Register a stream on the bottom layer topic with the same name as the stream. An exception will be thrown if the stream is already created.

## Synopsis

```sql
CREATE STREAM stream_name [ AS select_query ] WITH (FORMAT = stream_format);
```

## Notes

- `stream_name` is a valid identifier.
- `select_query` is an optional `SELECT` (Stream) query. For more information, see `SELECT` section. When `<select_query>` is specified, the created stream will be filled with records from the `SELECT` query continuously. Otherwise, the stream will only be created and kept empty.
- `stream_format` specifies the format of records in the stream. Note that we only support `"JSON"` format now.

## Examples

```sql
CREATE STREAM weather WITH (FORMAT = "JSON");

CREATE STREAM abnormal_weather AS SELECT * FROM weather WHERE temperature > 30 AND humidity > 80 EMIT CHANGES WITH (FORMAT = "JSON");
```
