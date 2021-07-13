SQL quick reference
===================

## CREATE STREAM

Create a new HStreamDB stream with stream name given.
An exception will be thrown if the stream is already created.
See [CREATE STREAM](statements/create-stream.md).

```sql
CREATE STREAM stream_name [AS select_query] [WITH (stream_option [, ...])];
```

## CREATE VIEW

Create a new view with view name given. A view is a physical object like a stream and it is updated with time.
An exception will be thrown if the view is already created. The name of a view can either be the same as a stream.
See [CREATE VIEW](statements/create-view.md)

```sql
CREATE VIEW view_name AS select_query;
```

## CREATE CONNECTOR

Create a new connector for fetching data from or writing data to an external system. A connector can be either a source or a sink one. **Note that source connector is not supported yet**.
When creating a connector, its type and binded stream must be specified in the `WITH` clause. There can be other options such as database name, user name and password.
There can be an optional `IF NOT EXIST` config to only create the given connector if it does not exist.
See [CREATE CONNECTOR](statements/create-connector.md)

```sql
CREATE CONNECTOR <SOURCE|SINK> CONNECTOR connector_name [IF NOT EXIST] WITH (connector_option [, ...]);
```

## SELECT (from streams)

Continuously get records from the stream(s) specified as streaming data flows in.
It is usually used in an interactive CLI to monitor realtime changes of data.
Note that the query writes these records to a random-named stream.
See [SELECT (Stream)](statements/select-stream.md).

```sql
SELECT <* | expression [ AS field_alias ] [, ...]>
  FROM stream_name_1
       [ join_type JOIN stream_name_2
         WITHIN (some_interval)
         ON stream_name_1.field_1 = stream_name_2.field_2 ]
  [ WHERE search_condition ]
  [ GROUP BY field_name [, window_type] ]
  EMIT CHANGES;
```

## SELECT (from views)

Get a record from the specified view. The fields to get have to be already in the view.
It produces one or zero static record and costs little time.
See [Select (View)](statements/select-view.md).

```sql
SELECT <* | expression [ AS field_alias ] [, ...]>
  FROM view_name
  WHERE field_name = value_expression;
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
DROP STREAM stream_name [IF EXISTS];
DROP VIEW view_name [IF EXISTS];
```

## SHOW

Show the information of all streams, queries, views or connectors.

```sql
SHOW STREAMS;
SHOW QUERIES;
SHOW VIEWS;
SHOW CONNECTORS;
```
