# HStream CLI

We can run the following to use HStream CLI:

```sh
docker run -it --rm --name some-hstream-admin --network host hstreamdb/hstream:v0.8.0 hstream --help
```

For the ease of illustration, we execute an interactive bash shell in the
hstream container to use hstream admin,

The following example usage is based on the cluster started in
[quick start](../start/quickstart-with-docker.md), please adjust
correspondingly.

```sh
docker exec -it docker_hserver0_1 bash
> hstream --help
```

```
======= HStream CLI =======

Usage: hstream [--host SERVER-HOST] [--port INT] COMMAND

Available options:
  --host SERVER-HOST       server host admin value (default: "127.0.0.1")
  --port INT               server admin port value (default: 6570)
  -h,--help                Show this help text

Available commands:
  sql                      Start HStream SQL Shell
  nodes                    Manage HStream Server Cluster
  init                     Init HStream Server Cluster
```

## Check Cluster status

```sh
> hstream nodes --help
Usage: hstream nodes COMMAND
  Manage HStream Server Cluster

Available options:
  -h,--help                Show this help text

Available commands:
  list                     List all running nodes in the cluster
  status                   Show the status of nodes specified, if not specified
                           show the status of all nodes
> hstream nodes list
+-----------+
| server_id |
+-----------+
| 100       |
| 101       |
+-----------+

> hstream nodes status
+-----------+---------+-------------------+
| server_id |  state  |      address      |
+-----------+---------+-------------------+
| 100       | Running | 192.168.64.4:6570 |
| 101       | Running | 192.168.64.5:6572 |
+-----------+---------+-------------------+
```

## HStream SQL

HStreamDB also provides an interactive SQL shell for a series of operations, such as
management of streams and views, data insertion and retrieval, etc.

```
> hstream sql --help
Usage: hstream sql [--update-interval INT] [--retry-timeout INT]
  Start HStream SQL Shell

Available options:
  --update-interval INT    interval to update available servers in seconds
                           (default: 30)
  --retry-timeout INT      timeout to retry connecting to a server in seconds
                           (default: 60)
  -h,--help                Show this help text
```

Once you entered shell, you can see the following help info:

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

1. Basic shell commands, starting with `:`
2. SQL statements end with `;`

### Basic CLI Operations

To quit current cli session:

```sh
> :q
```

To print out help info overview:

```sh
> :h
```

To show specific usage of some SQL statements:

```sh
> :help CREATE

  CREATE STREAM <stream_name> [IF EXIST] [AS <select_query>] [ WITH ( {stream_options} ) ];
  CREATE {SOURCE|SINK} CONNECTOR <stream_name> [IF NOT EXIST] WITH ( {connector_options} );
  CREATE VIEW <stream_name> AS <select_query>;
```

Available SQL operations include: `CREATE`, `DROP`, `SELECT`, `SHOW`, `INSERT`,
`TERMINATE`.

### SQL Statements

All the processing and storage operations are done via SQL statements.

#### Stream

There are two ways to create a new data stream.

1. Create an ordinary stream:

```sql
CREATE STREAM stream_name;
```

This will create a stream with no particular function. You can `SELECT` data from
the stream and `INSERT` to via corresponding SQL statement.

2. Create a stream, and this stream will also run a query to select specified
   data from some other stream.

Adding a Select statement after Create with a keyword `AS` can create a stream
will create a stream that processes data from another stream.

For example:

```sql
CREATE STREAM stream_name AS SELECT * from demo EMIT CHANGES;
```

In the example above, by adding an `AS` followed by a `SELECT` statement to the
normal `CREATE` operation, it will create a stream that will also select all
the data from the `demo`.

After Creating the stream, we can insert values into the stream.

```sql
INSERT INTO stream_name (field1, field2) VALUES (1, 2);
```

There is no restriction on the number of fields a query can insert. Also, the
type of value is not restricted. However, you need to make sure that the
number of fields and the number of values are aligned.

Deletion command is `DROP STREAM <Stream_name> ;`, which deletes a stream, and
terminate all the [queries](#queries) that depend on the stream.

For example:

```sql
SELECT * FROM demo EMIT CHANGES;
```

will be terminated if the stream demo is deleted;

```sql
DROP STREAM demo;
```

If you try to delete a stream that does not exist, an error message will be
returned. To turn it off, you can use add `IF EXISTS` after the stream_name:

```sql
DROP STREAM demo IF EXISTS;
```

#### Show all streams

You can also show all streams by using the `SHOW STREAMS` command.

```
> SHOW STEAMS;
+-------------+---------+----------------+-------------+
| Stream Name | Replica | Retention Time | Shard Count |
+-------------+---------+----------------+-------------+
| demo        | 3       | 0sec           | 1           |
+-------------+---------+----------------+-------------+
```

#### Queries

Run a continuous query on the stream to select data from a stream:

After creating a stream, we can select data from the stream in real-time. All the
data inserted after the select query is created will be printed out when the
insert operation happens. Select supports real-time processing on the data
inserted into the stream.

For example, we can choose the field and filter the data selected from the
stream.

```sql
SELECT a FROM demo EMIT CHANGES;
```

This will only select field `a` from stream demo.

How to terminate a query?

A query can be terminated if we know the query id:

```sql
TERMINATE QUERY <id>;
```

We can get all the query information by command `SHOW`:

```sql
SHOW QUERIES;
```

output just for demonstration :

```
+------------------+------------+--------------------------+----------------------------------+
| Query ID         | Status     | Created Time             | SQL Text                         |
+------------------+------------+--------------------------+----------------------------------+
| 1361978122003419 | TERMINATED | 2022-07-28T06:03:42+0000 | select * from demo emit changes; |
+------------------+------------+--------------------------+----------------------------------+
```

Find the query to terminate, make sure is id not already terminated, and pass
the query id to `TERMINATE QUERY`

Or under some circumstances, you can choose to `TERMINATE ALL;`.

### View

View is a projection of specified data from streams. For example,

```sql
CREATE VIEW v_demo AS SELECT SUM(a) FROM demo GROUP BY a EMIT CHANGES;
```

the above command will create a view that keeps track of the sum of `a` (which
have the same values, because of groupby) and have the same value from the point
this query is executed.

The operations on view are very similar to those on streams.

Except we can not use `SELECT ... EMIT CHANGES` performed on streams because a
view is static and there are no changes to emit. Instead, for example, we select
from view with:

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
