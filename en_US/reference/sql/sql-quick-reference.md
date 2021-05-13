# SQL quick reference

## CREATE STREAM

Register a stream on the bottom layer topic with the same name as the stream. An exception will be thrown if the stream is already created. See [CREATE STREAM](statements/create-stream.md).

```sql
CREATE STREAM stream_name [AS select_query] WITH (FORMAT = stream_format);
```

## SELECT

Continuously pulls records from the stream(s) specified. It is usually used in an interactive CLI to monitor realtime changes of data. Note that the query writes records to a random-named stream. See [SELECT (Stream)](statements/select-stream.md).

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

## INSERT

Insert a record into specified stream. See [INSERT](statements/insert.md).

```sql
INSERT INTO stream_name (field_name [, ...]) VALUES (field_value [, ...]);
```
