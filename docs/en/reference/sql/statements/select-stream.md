SELECT (Stream)
===============

Get records from a materialized view or a stream. Note that `SELECT` from streams can only used as a part of `CREATE STREAM` or `CREATE VIEW`. When you want to get results in a command-line session, create a materialized view first and then `SELECT` from it.

## Synopsis

```sql
SELECT <* | identifier.* | expression [ AS field_alias ] [, ...]>
  FROM table_ref [, ...]
  [ WHERE expression ]
  [ GROUP BY field_name [, ...] ]
  [ HAVING expression ];
```

## Notes

### About `expression`
`expression` can be any expression described [here](../sql-overview.md#Expressions), such as `temperature`, `weather.humidity`, `114514`, `1 + 2`, `SUM(productions)`, `` `COUNT(*)` `` and even subquery `SELECT * FROM stream_test WHERE a > 1`. In `WHERE` and `HAVING` clauses, `expression` should have a value of boolean type.

### About `table_ref`

`table_ref` is a source stream or materialized view:
```
  table_ref ::= <identifier>
              | ( <select_query> )
              | <table_ref> <join_type> <table_ref> <join_condition>
              | <time_window> ( <table_ref> <time_window_parameters> )
              | <table_ref> AS <identifier>
```

It seems quite complex! Do not worry. In a word, a `table_ref` is something you can retrieve data from. A `table_ref` can be an identifier, a subquery, a join of two `table_ref`s, a `table_ref` with a time window or a `table_ref` with an alias. We will describe them in detail.

#### JOIN

Fortunately, the `JOIN` in our SQL query is the same as the SQL standard, which is used by most of your familiar databases such as MySQL and PostgreSQL. It can be one of:

- `CROSS JOIN`, which produces the Cartesian product of two stream and/or materialized view(s). It is equivalent to `INNER JOIN ON TRUE`. Note that when you use comma(`,`) between two stream and/or materialized view(s), it implicitly uses `CROSS JOIN`.
- `[INNER] JOIN`, which produces all data in the qualified Cartesian product by the join condition. Note a join condition must be specified.
- `LEFT [OUTER] JOIN`, which produces all data in the qualified Cartesian product by the join condition plus one copy of each row in the left-hand `table_ref` for which there was no right-hand row that passed the join condition(extended with nulls on the right). Note a join condition must be specified.
- `RIGHT [OUTER] JOIN`, which produces all data in the qualified Cartesian product by the join condition plus one copy of each row in the right-hand `table_ref` for which there was no left-hand row that passed the join condition(extended with nulls on the left). Note a join condition must be specified.
- `FULL [OUTER] JOIN`, which produces all data in the qualified Cartesian product by the join condition, plus one row for each unmatched left-hand row (extended with nulls on the right), plus one row for each unmatched right-hand row (extended with nulls on the left). Note a join condition must be specified.

A join condition can be any of
- `ON <expression>`. The condition passes when the value of the expression is `TRUE`.
- `USING(column[, ...])`. The specified column(s) is matched.
- `NATURAL`. The common columns of two `table_ref`s are matched. It is equivalent to `USING(common_columns)`.

#### Time Windows

A `table_ref` can also have a time window. It is defined as
```
  time_window ::= TUMBLING some_interval
                | HOPPING  some_interval some_interval
                | SLIDING  some_interval
```

Note that
- `some_interval` represents a period of time. See [Intervals](../sql-overview.md#intervals).

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
SELECT stream1.temperature, stream2.humidity FROM stream1 JOIN stream2 USING(humidity);
```

- Grouping records:
```sql
SELECT COUNT(*) FROM TUMBLING(weather, INTERVAL 00:00:10) GROUP BY cityId;
```

- Subqueries:
```sql
SELECT res.r1 AS mm, res.r2 AS nn FROM (SELECT s01.a AS r1, SUM(s02.c) AS r2 FROM s01 JOIN s02 ON TRUE GROUP BY s02.a) AS res;
```
