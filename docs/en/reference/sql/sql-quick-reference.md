SQL quick reference
===================

## CREATE STREAM

Create a new HStreamDB stream with the stream name given.
An exception will be thrown if the stream is already created.
See [CREATE STREAM](statements/create-stream.md).

```sql
CREATE STREAM stream_name [AS select_query] [WITH (stream_option [, ...])];
```

## CREATE VIEW

Create a new view with the view name given. A view is a physical object like a stream and it is updated with time.
An exception will be thrown if the view is already created. The name of a view can either be the same as a stream.
See [CREATE VIEW](statements/create-view.md).

```sql
CREATE VIEW view_name AS select_query;
```

## SELECT

Get records from a materialized view or a stream. Note that `SELECT` from streams can only used as a part of `CREATE STREAM` or `CREATE VIEW`. When you want to get results in a command-line session, create a materialized view first and then `SELECT` from it.
See [SELECT (Stream)](statements/select-stream.md).

```sql
SELECT <* | expression [ AS field_alias ] [, ...]>
  FROM stream_ref
  [ WHERE expression ]
  [ GROUP BY field_name [, ...] ]
  [ HAVING expression ];
```

## INSERT

Insert data into the specified stream. It can be a data record, a JSON value or binary data.
See [INSERT](statements/insert.md).

```sql
INSERT INTO stream_name (field_name [, ...]) VALUES (field_value [, ...]);
INSERT INTO stream_name VALUES 'json_value';
INSERT INTO stream_name VALUES "binary_value";
```

## DROP

Delete a given stream or view. There can be an optional `IF EXISTS` config to only delete the given category if it exists.

```sql
DROP STREAM    stream_name    [IF EXISTS];
DROP VIEW      view_name      [IF EXISTS];
```

## SHOW

Show the information of all streams, queries, views or connectors.

```sql
SHOW STREAMS;
SHOW QUERIES;
SHOW VIEWS;
SHOW CONNECTORS;
```
