# SQL Overview

SQL is a domain-specific language used in programming and designed for managing
data held in a database management system. A standard for the specification of
SQL is maintained by the American National Standards Institute (ANSI). Also,
there are many variants and extensions to SQL to express more specific programs.

The
[SQL grammar of HStreamDB](https://github.com/hstreamdb/hstream/blob/main/hstream-sql/etc/SQL-v1.cf)
is based on a subset of standard SQL with some extensions to support stream
operations.

## Syntax

SQL inputs are made up of a series of statements. Each statement is made up of a
series of tokens and ends in a semicolon (`;`).

A token can be a keyword argument, an identifier, a literal, an operator, or a
special character. The details of the rules can be found in the
[BNFC grammar file](https://github.com/hstreamdb/hstream/blob/main/hstream-sql/etc/SQL-v1.cf).
Normally, tokens are separated by whitespace.

The following examples are syntactically valid SQL statements:

```sql
SELECT * FROM my_stream;

CREATE STREAM abnormal_weather AS SELECT * FROM weather WHERE temperature > 30 AND humidity > 80 WITH (REPLICATE = 3);

INSERT INTO weather (cityId, temperature, humidity) VALUES (11254469, 12, 65);
```

## Keywords

Some tokens such as `SELECT`, `INSERT` and `WHERE` are reserved _keywords_,
which have specific meanings in SQL syntax. Keywords are case insensitive, which
means that `SELECT` and `select` are equivalent. A keyword can not be used as an
identifier.

For a complete list of keywords, see the [appendix](appendix.md).

## Identifiers

Identifiers are tokens that represent user-defined objects such as streams,
fields, and other ones. For example, `my_stream` can be used as a stream name,
and `temperature` can represent a field in the stream.

By now, identifiers only support C-style naming rules. It means that an
identifier name can only have letters (both uppercase and lowercase letters),
digits, and the underscore. Besides, the first letter of an identifier should be
either a letter or an underscore.

By now, identifiers are case-sensitive, which means that `my_stream` and
`MY_STREAM` are different identifiers.

## Expressions

An expression is a value that can exist almost everywhere in a SQL query. It can
be both a constant whose value is known before execution (such as an integer or
a string literal) and a variable whose value is known during execution (such as
a field of a stream).

### Integer

Integers are in the form of `digits`, where `digits` are one or more
single-digit integers (0 through 9). Negatives such as `-1` are also supported.
**Note that scientific notation is not supported yet**.

### Float

Floats are in the form of `<digits>.<digits>`. Negative floats such as `-11.514`
are supported. Note that

- **scientific notation is not supported yet**.
- **Forms such as `1.` and `.99` are not supported yet**.

### Boolean

A boolean value is either `TRUE` or `FALSE`.

### String

Strings are arbitrary character series surrounded by double quotes (`"`), such
as `"JSON"`.

### Date

Dates represent a date exact to a day in the form of
`DATE <year>-<month>-<day>`, where `<year>`, `<month>` and `<day>` are all
integer constants. Note that the leading `DATE` should not be omitted.

Example: `DATE 2021-01-02`

### Time

Time constants represent time exact to a second or a microsecond in the form of
`TIME <hour>-<minute>-<second>` or
`TIME <hour>-<minute>-<second>.<microsecond>`, where `<hour>`, `<minute>`,
`<second>` and `<microsecond>` are all integer constants. Note that the leading
`TIME` should not be omitted.

Example: `TIME 11:45:14`, `TIME 01:02:03.456`

### Timestamp

Timestamp constants represent values that contain both date and time parts. It
can also contain an optional timezone part for convenience. A timestamp is in
the form of `TIMESTAMP <date_str> <time_str>` or
`TIMESTAMP <date_str> T <time_str> <timezone>`, where `<date_str>` and
`time_str` are described above and `<timezone>` is like `Z`, `+<hour>:<minute>`
or `-<hour>:<minute>`. For more information, please refer to
[ISO 8601](https://en.wikipedia.org/wiki/ISO_8601).

Example: `TIMESTAMP 2022-10-28 12:00:00`,
`TIMESTAMP 1980-01-01 T 01:02:03 +08:00`

### Interval

Intervals represent a time section in the form of
`INTERVAL <time_str> <time_unit>` or. Note that the leading `INTERVAL` should
not be omitted.

Example: `INTERVAL '5' SECOND`(5 seconds)

### Map

Maps represent a set of key-value pairs, where keys and values are valid
expressions. It is in the form of `{<expr_key>: <expr_value>, ...}`.

Example: `{"id": 1, "temp": 30.5}`

### Array

Arrays represent a list of values, where each one of them is a valid expression.
It is in the form of `[<expr_1>, ...]`.

Example: `["aa", "bb", "cc"]`, `[1, 2]`

### Column(Field)

A column(or a field) represents a part of a value in a stream or materialized
view. It is similar to column of a table in traditional relational databases. A
column is in the form of `<identifier>` or
`<identifier_stream>.<identifier_column>`. When a column name is ambiguous(for
example it has the same name as a function application) the back quote `` ` ``
can be used.

Example: `temperature`, `stream_test.humidity`, `` `SUM(a)` ``

### Subquery

A subquery is a SQL clause start with `SELECT`, see
[here](./statements/select-stream.md).

### Function or Operator Application

An expression can also be formed by other expressions by applying functions or
operators on them. The details of function and operator can be found in the
following parts.

Example: `SUM(stream_test.cnt)`, (`raw_stream::jsonb)->>"value"`

## Operators and Functions

Functions are special keywords that mean some computation, such as `SUM` and
`MIN`. And operators are infix functions composed of special characters, such as
`>=` and `<>`.

For a complete list of functions and operators, see the [appendix](appendix.md).

## Special Characters

There are some special characters in the SQL syntax with particular meanings:

- Parentheses (`()`) are used outside an expression for controlling the order of
  evaluation or specifying a function application.
- Brackets (`[]`) are used with maps and arrays for accessing their
  substructures, such as `some_map[temp]` and `some_array[1]`. **Note that it is
  not supported yet**.
- Commas (`,`) are used for delineating a list of objects.
- The semicolons (`;`) represent the end of a SQL statement.
- The asterisk (`*`) represents "all fields", such as
  `SELECT * FROM my_stream;`.
- The period (`.`) is used for accessing a field in a stream, such as
  `my_stream.humidity`.
- The back quote (`` ` ``) represents an "raw column name" in the `SELECT`
  clause to distinguish a column name with functions from actual function
  applications. For example, `SELECT SUM(a) FROM s;` means applying `SUM`
  function on the column `a` from stream `s`. However if the stream `s` actually
  contains a column called `SUM(a)` and you want to take it out, you can use
  back quotes like `` SELECT `SUM(a)` FROM s; ``.

## Comments

A single-line comment begins with `//`:

```
// This is a comment
```

Also, C-style multi-line comments are supported:

```
/* This is another
   comment
*/
```
