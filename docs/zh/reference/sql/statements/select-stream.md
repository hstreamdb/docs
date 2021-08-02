# SELECT (Stream)

Continuously pulls records from the stream(s) specified. It is usually used in an interactive CLI to monitor realtime changes of data. Note that the query writes records to a random-named stream.

## Synopsis

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

## Notes

- `expression` can be a field name, a constant, or their association, such as `temperature`, `weather.humidity`, `114514`, `1 + 2` and `SUM(productions)`.
- `some_interval` represents a period of time. See [Intervals](../sql-overview.md#intervals).
- `join_type` specifies the type of joining operation. Only `INNER` is supported yet.
- `window_type` specifies the type of time window:
  ```
  window_type ::= TUMBLING some_interval
                | HOPPING  some_interval some_interval
                | SESSION  some_interval
  ```
- `search_condition` is actually a boolean expression:
  ```
  search_condition ::= [NOT] predicate [ <AND | OR> predicate [, ...] ]
  predicate        ::= expression comp_op expression
  comp_op          ::= = | <> | > | < | >= | <=
  ```

## Examples

- A simple query:

```sql
SELECT * FROM my_stream EMIT CHANGES;
```

- Filtering rows:

```sql
SELECT temperature, humidity FROM weather WHERE temperature > 10 AND humidity < 75 EMIT CHANGES;
```

- Joining streams:

```sql
SELECT stream1.temperature, stream2.humidity FROM stream1 INNER JOIN stream2 WITHIN (INTERVAL 5 SECOND) ON stream1.humidity = stream2.humidity EMIT CHANGES;
```

- Grouping records:

```sql
SELECT COUNT(*) FROM weather GROUP BY cityId, TUMBLING (INTERVAL 10 SECOND) EMIT CHANGES;
```
