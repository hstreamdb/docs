# CLI

Once you entered CLI, you can see the following help info:

```sh
      __  _________________  _________    __  ___
     / / / / ___/_  __/ __ \/ ____/   |  /  |/  /
    / /_/ /\__ \ / / / /_/ / __/ / /| | / /|_/ /
   / __  /___/ // / / _, _/ /___/ ___ |/ /  / /
  /_/ /_//____//_/ /_/ |_/_____/_/  |_/_/  /_/


Command
  :h                           To show these help info
  :q                           To exit command line interface
  :help [sql_operation]        To show full usage of sql statement

SQL STATEMENTS:
  To create a simplest stream:
    CREATE STREAM stream_name;

  To create a query select all fields from a stream:
    SELECT * FROM stream_name EMIT CHANGES;

  To insert values to a stream:
    INSERT INTO stream_name (field1, field2) VALUES (1, 2);

```


There are two kinds of commands:

1. Basic Cli Operations, start with `:`
2. SQL statements end with `;`

## Basic CLI Operations

To Quit current cli session:

```sh
> :q
```

To print out help info over view:

```sh
> :h
```

To show specific usage of some sql statement:

```sh
> :help CREATE

  CREATE STREAM <stream_name> [IF EXIST] [AS <select_query>] [ WITH ( {stream_options} ) ];
  CREATE {SOURCE|SINK} CONNECTOR <stream_name> [IF NOT EXIST] WITH ( {connector_options} );
  CREATE VIEW <stream_name> AS <select_query>;
```

Available sql operations includes: `CREATE`, `DROP`, `SELECT`, `SHOW`, `INSERT`, `TERMINATE`.

## SQL Statements

All the processing and storage operations are done via SQL statements.

### Stream

#### There are two ways to create a new data stream.

1. Create an ordinary stream:

```sql
CREATE STREAM stream_name;
```

This will create a stream with no special function. You can `SELECT` data from the
stream and `INSERT` to via corresponding SQL statement.

2. Create a stream and this stream will also run a query to select specified data from some other stream.

Adding an Select statement after Create with a keyword `AS` can create a stream
will create a stream which processing data from another stream.

For example:

```sql
CREATE STREAM stream_name AS SELECT * from demo EMIT CHANGES;
```

In the example above, by adding an `AS` followed by a `SELECT` statement to the normal `CREATE` operation,
it will create a stream which will also select all the data from demo.

#### After Creating the stream, we can insert values into the stream.

```sql
INSERT INTO stream_name (field1, field2) VALUES (1, 2);
```

There is no restriction on the number of fields a query can insert.
Also, the type of value are not restricted. However, you do need to make sure
that the number of fields and the number of values are aligned.

#### Select data from a stream

When we have a stream, we can select data from the stream in real-time.
All the data inserted after the select query is created will be print out
when the insert operation happens. Select supports real-time processing on the
data inserted to the stream.

For example, we can choose the field and filter the data selected from the stream.

```sql
SELECT a FROM demo EMIT CHANGES;
```

This will only select field `a` from stream demo.

#### Terminate a query

A query can be terminated if the we know the query id:

```sql
TERMINATE QUERY <id>;
```

We can get all the query information by command `SHOW`:

```sql
SHOW QUERIES;
```

output just for demonstration :

```sh
╭─────────────────┬────────────────┬────────────────┬─────────────────╮
│     queryId     │   queryInfo    │ queryInfoExtra │   queryStatus   │
╞═════════════════╪════════════════╪════════════════╪═════════════════╡
│                 │ createdTime:   │                │                 │
│                 │ 1.626143326e9  │                │ status:         │
│ 810932205589156 │ sqlStatement:  │ PlainQuery:    │ Running         │
│                 │ SELECT  * FROM │ foo            │ timeCheckpoint: │
│                 │ foo       EMIT │                │ 1.626143717e9   │
│                 │ CHANGES;       │                │                 │
╰─────────────────┴────────────────┴────────────────┴─────────────────╯
```

Find the query to terminate, make sure is id not already terminated, and pass
the query id to `TERMINATE QUERY`

Or under some circumstances, you can choose to `TERMINATE ALL ;`.

#### Delete a stream

Deletion command is `DROP STREAM <Stream_name> ;`, which deletes a stream, and terminate all the
queries that depends on the stream.

For example:

```sql
SELECT * FROM demo EMIT CHANGES;
```

will be terminated if the stream demo is deleted;

```sql
DROP STREAM demo;
```

If you try to delete a stream that does not exist, an error message will be returned.
To turn it off, you can use add `IF EXISTS` after the stream_name:

```sql
DROP STREAM demo IF EXISTS;
```

#### Show all streams

You can also show all streams by using the `SHOW STREAMS` command.

### View

View is a projection of specified data from streams. For example,

```sql
CREATE VIEW v_demo AS SELECT SUM(a) FROM demo GROUP BY a EMIT CHANGES;
```

the above command will create a view which keep track of the sum of `a`
(which have the same values,because of group by) and have
the same value from the point this query is executed.

The operations on view are very similar to those on streams.

Except we can not use `SELECT ... EMIT CHANGES` performed on streams,
because a view is static and there are no changes to emit. Instead, for example
we select from view with:

```sql
SELECT * FROM v_demo WHERE a = 1;
```

This will print the sum of `a` when `a` = 1.

If we want to create a view to record the sum of `a`s, we can:

```sql
CREATE STREAM demo2 AS SELECT a, 1 AS b FROM demo EMIT CHANGES;
CREATE VIEW v_demo2 AS SELECT SUM(a) FROM demo2 GROUP BY b EMIT CHANGES;
SELECT * FROM demo2 WHERE b = 1;
```
