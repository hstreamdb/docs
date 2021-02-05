Streaming SQL quick reference
=============================

## CREATE STREAM

Registers a stream on the bottom layer topic with the same name as the stream. An exception will be thrown if the stream is already created.

```sql
CREATE STREAM <stream_name> [AS <select_query>] WITH (FORMAT = <stream_format>);
```

- `<stream_name>` is a valid identifier.
- `<select_query>` is an optional `SELECT` query. For more information, see `SELECT` section. If `<select_query>` is specified, the created stream will be filled with records from the `SELECT` query continuously. Otherwise, only the stream will be created and kept empty.
- `<stream_format>` specifies the format of records in the stream. Note that we only support `"JSON"` format now.

Examples:

```sql
CREATE STREAM weather WITH (FORMAT = "JSON");

CREATE STREAM abnormal_weather AS SELECT * FROM weather WHERE temperature > 30 AND humidity > 80 WITH (FORMAT = "JSON");
```


## SELECT

Continuously pulls records from the stream(s) specified. It is usually used in an interactive CLI to monitor realtime changes of data. Note that the query writes records to a random-named stream.

```sql
SELECT <select_statement>
  FROM <from_statement>
  WHERE <search_condition>
  GROUP BY <group_by_statement>;

<select_statement>     ::= *
                         | <select_substatement> [, ...]

<select_substatement>  ::= <expression> [AS <field_alias>]

<from_statement>       ::= <stream_name_1>
                           [<join_type> JOIN <stream_name_2>
                           WITHIN (<interval>)
                           ON <stream_name_1>.<field_1> = <stream_name_2>.<field_2>]

<join_type>            ::= INNER

<where_condition>      ::= [NOT] <predicate> [(AND | OR) <predicate>, ...]
<predicate>            ::= <expression> <comp_op> <expression>
<comp_op>              ::= = | <> | > | < | >= | <=

<group_by_statement>   ::= <field_name>
                         | <field_name>, <window_type>

<window_type>          ::= TUMBLING <interval>
                         | HOPPING  <interval> <interval>
                         | SESSION  <interval>
```

- `<expression>` can be a field name, a constant or their association, such as `temperature`, `weather.humidity`, `114514`, `1 + 2` and `SUM(productions)`. Formal specification of `<expression>` is to be added.

- `<interval>` represents a period of time:

  `INTERVAL n (SECOND|MINUTE|DAY|WEEK|MONTH|YEAR|)`

Examples:

- A simple query:
```sql
SELECT * FROM my_stream;
```

- Filtering rows:
```sql
SELECT temperature, humidity FROM weather WHERE temperature > 10 AND humidity < 75;
```

- Joining streams:
```sql
SELECT stream1.temperature, stream2.humidity FROM stream1 INNER JOIN stream2 WITHIN (INTERVAL 5 SECOND) ON stream1.humidity = stream2.humidity;
```

- Grouping records:
```sql
SELECT COUNT(*) FROM weather GROUP BY cityId, TUMBLING (INTERVAL 10 SECOND);
```

## INSERT

Insert a record into specified stream.

```sql
INSERT INTO <stream_name> (<field_name>, ...) VALUES (<field_value>, ...);
```

Example:
```sql
INSERT INTO weather (cityId, temperature, humidity) VALUES (11254469, 12, 65);
```

