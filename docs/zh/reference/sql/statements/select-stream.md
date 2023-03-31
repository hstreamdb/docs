# SELECT (Stream)

Get records from a materialized view or a stream. Note that `SELECT` from
streams can only be used as a part of `CREATE STREAM` or `CREATE VIEW`. Unless
when there are cases you would want to run an interactive query from the command
shell, you could add `EMIT CHANGES` at the end of the following examples.

## Synopsis

```sql
SELECT <* | identifier.* | expression [ AS field_alias ] [, ...]>
  FROM stream_ref
  [ WHERE expression ]
  [ GROUP BY field_name [, ...] ]
  [ HAVING expression ];
```

## Notes

### About `expression`

`expression` can be any expression described
[here](../sql-overview.md#Expressions), such as `temperature`,
`weather.humidity`, `114514`, `1 + 2`, `SUM(productions)`, `` `COUNT(*)` `` and
even subquery `SELECT * FROM stream_test WHERE a > 1`. In `WHERE` and `HAVING`
clauses, `expression` should have a value of boolean type.

### About `stream_ref`

`stream_ref` specifies a source stream or materialized view:

```
  stream_ref ::= <identifier>
              | <stream_ref> AS <identifier>
              | <stream_ref> <join_type> <stream_ref> <join_condition> WITHIN Interval
              | <time_window_function> ( <identifier> )
```

It seems quite complex! Do not worry. In a word, a `stream_ref` is something you
can retrieve data from. A `stream_ref` can be an identifier, a join
of two `stream_ref`s, a `stream_ref` with a time window or a `stream_ref` with an
alias. We will describe them in detail.

#### JOIN

Fortunately, the `JOIN` in our SQL query is the same as the SQL standard, which
is used by most of your familiar databases such as MySQL and PostgreSQL. It can
be one of:

- `CROSS JOIN`, which produces the Cartesian product of two streams and/or
  materialized view(s). It is equivalent to `INNER JOIN ON TRUE`.
- `[INNER] JOIN`, which produces all data in the qualified Cartesian product by
  the join condition. Note a join condition must be specified.
- `LEFT [OUTER] JOIN`, which produces all data in the qualified Cartesian
  product by the join condition plus one copy of each row in the left-hand
  `stream_ref` for which there was no right-hand row that passed the join
  condition(extended with nulls on the right). Note a join condition must be
  specified.
- `RIGHT [OUTER] JOIN`, which produces all data in the qualified Cartesian
  product by the join condition plus one copy of each row in the right-hand
  `stream_ref` for which there was no left-hand row that passed the join
  condition(extended with nulls on the left). Note a join condition must be
  specified.
- `FULL [OUTER] JOIN`, which produces all data in the qualified Cartesian
  product by the join condition, plus one row for each unmatched left-hand row
  (extended with nulls on the right), plus one row for each unmatched right-hand
  row (extended with nulls on the left). Note a join condition must be
  specified.

A join condition can be any of

- `ON <expression>`. The condition passes when the value of the expression is
  `TRUE`.
- `USING(column[, ...])`. The specified column(s) is matched.
- `NATURAL`. The common columns of two `stream_ref`s are matched. It is
  equivalent to `USING(common_columns)`.

#### Time Windows

A `stream_ref` can also have a time window. Currently, we support the following 3
time-window functions:

```
Tumble( <identifier>, <some_interval>)
HOP( <identifier>, <some_interval>, <some_interval>)
SLIDE( <identifier>, <some_interval>)
```

Note that

- `some_interval` represents a period of time. See
  [Intervals](../sql-overview.md#intervals).

## Examples

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
SELECT stream1.temperature, stream2.humidity FROM stream1 JOIN stream2 USING(humidity) WITHIN (INTERVAL '1' HOUR);
```

- Grouping records:

```sql
SELECT COUNT(*) FROM TUMBLE(weather, INTERVAL '10' SECOND) GROUP BY cityId;
```
