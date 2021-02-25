SQL Overview
============

SQL is a domain-specific language used in programming and designed for managing data held in a database management system. A standard for the specification of SQL is maintained bt the American National Standards Institute (ANSI). And there are also many variants and extensions of SQL to express more specific programs.

The [SQL grammar of HStreamDB](https://github.com/hstreamdb/hstream/blob/master/hstream-sql/etc/SQL.cf) is based on a subset of SQL-92 with some extensions to support stream operations.

## Syntax

SQL inputs are made up of a series of statements. Each statements is made up of a series of tokens and ends in a semicolon (`;`).

A token can be a keyword argument, an identifier, a literal, an operator or a special character. The details of the rules can be found in the [BNFC grammar file](https://github.com/hstreamdb/hstream/blob/master/hstream-sql/etc/SQL.cf) or [generated alex file](https://github.com/hstreamdb/hstream/blob/master/hstream-sql/etc/Lex.x). Normally, tokens are seperated by whitespace.

The following examples are valid SQL statements in syntax:

```sql
SELECT * FROM my_stream;

CREATE STREAM abnormal_weather AS SELECT * FROM weather WHERE temperature > 30 AND humidity > 80 WITH (FORMAT = "JSON");

INSERT INTO weather (cityId, temperature, humidity) VALUES (11254469, 12, 65);
```

## Keywords

Some tokens such as `SELECT`, `INSERT` and `WHERE` are reserved *keywords*, which have specific meaning in SQL syntax. Keywords are case insensitive, which means that `SELECT` and `select` are equivalent. A keyword can not be used as an identifier.

For a complete list of keywords, see the [appendix](appendix.md).

## Identifiers

Identifiers are tokens that represent user-defined objects such as streams, fields and other ones. For example, `my_stream` can be used as a stream name and `temperature` can represent a field in the stream.

By now, identifiers only support C-style naming rules. It means that an identifier name can only have letters (both uppercase and lowercase letters), digits and underscore and the first letter of an identifier should be either a letter or an underscore.

By now, identifiers are case sensitive, which meanws that `my_stream` and `MY_STREAM` are different identifiers.

## Literals (Constants)

Literals objects with known values before being executed. By now, there are six types of constants: integers, floats, strings, dates, time and intervals.

### Integers

Integers are in the form of `digits`, where `digits` is one or more single-digit integers (0 through 9). **Note that scientific notation is not supported yet. And due to the limitations of the grammar, please write `0-n` when you want to represent a negative `-n`**.

### Floats

Floats are in the form of `digits . digits`. Note that scientific notation is not supported yet. Note that

- **Due to the limitations of the grammar, please write `0-n` when you want to represent a negative `-n`**.
- **Forms such as `1.` and `.99` are not supported yet**.

### Strings

Strings are arbitrary character series surrounded by double quotes (`"`), such as `"JSON"`.

### Dates

Dates represent a date exact to a day in the form of `DATE <year>-<month>-<day>`, where `<year>`, `<month>` and `<day>` are all integer constants. Note that the leading `DATE` should not be omitted.

Example: `DATE 2021-01-02`

### Time

Time constants represent time exact to a second in the form of `TIME <hour>-<minute>-<second>`, where `<hour>`, `<minute>` and `<second>` are all integer constants. Note that the leading `TIME` should not be omitted.

Example: `TIME 11:45:14`

### Intervals

Intervals represent a time section in the form of `INTERVAL <num> <time_unit>`, where `<num>` is an integer constant and `<time_unit>` is one of `YEAR`, `MONTH`, `WEEK`, `DAY`, `MINUTE` and `SECOND`. Note that the leading `INTERVAL` should not be omitted.

Example: `INTERVAL 5 SECOND`

## Operators and Functions

Functions are special keywords which mean some computation, such as `SUM` and `MIN`. And operators are infix functions composed of special characters, such as `>=` and `<>`.

For a complete list of functions and operators, see the [appendix](appendix.md).

## Special characters

There are some special characters in the SQL syntax with particular meanings:

- Parentheses (`()`) are used outside an expression for controlling the order of evaluation or specifying a function application.
- Brackets (`[]`) are used with maps and arrays for accessing their substructures, such as `some_map[temp]` and `some_array[1]`. **Note that it is not supported yet**.
- Commas (`,`) are used for delineating a list of objects.
- The semicolons (`;`) represent the end of a SQL statement.
- The asterisk (`*`) represents "all fields", such as `SELECT * FROM  my_stream;`.
- The period (`.`) is used for accessing a field in a stream, such as `my_stream.humidity`.

## Comments

A single-line comment begins with `//`:

```
// This is a comment
```

Also C-style multi-line comments are supported:

```
/* This is another
   comment
*/
```
