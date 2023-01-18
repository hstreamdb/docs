# HStream CLI

We can run the following to use HStream CLI:

```sh
docker run -it --rm --name some-hstream-admin --network host hstreamdb/hstream:latest hstream --help
```

For ease of illustration, we execute an interactive bash shell in the HStream
container to use HStream admin,

The following example usage is based on the cluster started in
[quick start](../start/quickstart-with-docker.md), please adjust
correspondingly.

```sh
docker exec -it docker_hserver_1 bash
> hstream --help
```

```
======= HStream CLI =======

Usage: hstream [--host SERVER-HOST] [--port INT] [--tls-ca STRING]
               [--tls-key STRING] [--tls-cert STRING] [--retry-timeout INT]
               [--service-url ARG] COMMAND

Available options:
  --host SERVER-HOST       Server host value (default: "127.0.0.1")
  --port INT               Server port value (default: 6570)
  --tls-ca STRING          path name of the file that contains list of trusted
                           TLS Certificate Authorities
  --tls-key STRING         path name of the client TLS private key file
  --tls-cert STRING        path name of the client TLS public key certificate
                           file
  --retry-timeout INT      timeout to retry connecting to a server in seconds
                           (default: 60)
  --service-url ARG        The endpoint to connect to
  -h,--help                Show this help text

Available commands:
  sql                      Start HStream SQL Shell
  node                     Manage HStream Server Cluster
  init                     Init HStream Server Cluster
  stream                   Manage Streams in HStreamDB
  subscription             Manage Subscriptions in HStreamDB
```

## Connection

### HStream URL

The HStream CLI Client supports connecting to the server cluster with a url in
the following format:

```
<scheme>://<endpoint>:<port>
```

| Components | Description | Required |
|------------|-------------|----------|
| scheme | The scheme of the connection. Currently, we have `hstream`. To enable security options, `hstreams` is also supported  | Yes |
| endpoint | The endpoint of the server cluster, which can be the hostname or address of the server cluster. | If not given, the value will be set to the `--host` default `127.0.0.1` |
| port | The port of the server cluster. | If not given, the value will be set to the `--port` default `6570` |

### Connection Parameters

HStream commands accept connection parameters as separate command-line flags, in addition (or in replacement) to `--service-url`.

::: tip

In the cases where both `--service-url` and the options below are specified, the client will use the value in `--service-url`.

:::

| Option | Description |
|-|-|
| `--host` | The server host and port number to connect to. This can be the address of any node in the cluster.  Default: `127.0.0.1` |
| `--port` | The server port to connect to. Default: `6570`|

### Security Settings (optional)

If the [security option](../operation/security/overview.md) is enabled, here are
some options that should also be configured for CLI correspondingly.

#### Encryption

If [server encryption](../operation/security/encryption.md) is enabled, the
`--tls-ca` option should be added to CLI connection options:

```sh
hstream --tls-ca "<path to the CA certificate file>"
```

### Authentication

If [server authentication](../operation/security/authentication.md) is enabled,
the `--tls-key` and `--tls-cert` options should be added to CLI connection
options:

```sh
hstream --tls-key "<path to the trusted role key file>" --tls-cert "<path to the signed certificate file>"
```

## Check Cluster Status

```sh
> hstream node --help
Usage: hstream node COMMAND
  Manage HStream Server Cluster

Available options:
  -h,--help                Show this help text

Available commands:
  list                     List all running nodes in the cluster
  status                   Show the status of nodes specified, if not specified
                           show the status of all nodes
  check-running            Check if all nodes in the the cluster are running,
                           and the number of nodes is at least as specified

> hstream node list
+-----------+
| server_id |
+-----------+
| 100       |
| 101       |
+-----------+

> hstream node status
+-----------+---------+-------------------+
| server_id |  state  |      address      |
+-----------+---------+-------------------+
| 100       | Running | 192.168.64.4:6570 |
| 101       | Running | 192.168.64.5:6572 |
+-----------+---------+-------------------+

> hstream node check-running
All nodes in the cluster are running.
```

## Manage Streams

We can also manage streams through the hstream command line tool.

```sh
> hstream stream --help
Usage: hstream stream COMMAND
  Manage Streams in HStreamDB

Available options:
  -h,--help                Show this help text

Available commands:
  list                     Get all streams
  create                   Create a stream
  describe                 Get the details of a stream
  delete                   Delete a stream
```

### Create a stream

```sh
Usage: hstream stream create STREAM_NAME [-r|--replication-factor INT]
                             [-b|--backlog-duration INT] [-s|--shards INT]
  Create a stream

Available options:
  STREAM_NAME              The name of the stream
  -r,--replication-factor INT
                           The replication factor for the stream (default: 1)
  -b,--backlog-duration INT
                           The backlog duration of records in stream in seconds
                           (default: 0)
  -s,--shards INT          The number of shards the stream should have
                           (default: 1)
  -h,--help                Show this help text
```

Example: Create a demo stream with the default settings.

```sh
> hstream stream create demo
+-------------+---------+----------------+-------------+
| Stream Name | Replica | Retention Time | Shard Count |
+-------------+---------+----------------+-------------+
| demo        | 1       | 0 seconds      | 1           |
+-------------+---------+----------------+-------------+
```

### Show and delete streams

```sh
> hstream stream list
+-------------+---------+----------------+-------------+
| Stream Name | Replica | Retention Time | Shard Count |
+-------------+---------+----------------+-------------+
| demo2       | 1       | 0 seconds      | 1           |
+-------------+---------+----------------+-------------+

> hstream stream delete demo
Done.

> hstream stream list
+-------------+---------+----------------+-------------+
| Stream Name | Replica | Retention Time | Shard Count |
+-------------+---------+----------------+-------------+
```

## Manage Subscription

We can also manage streams through the hstream command line tool.

```sh
> hstream stream --help
Usage: hstream subscription COMMAND
  Manage Subscriptions in HStreamDB

Available options:
  -h,--help                Show this help text

Available commands:
  list                     Get all subscriptions
  create                   Create a subscription
  describe                 Get the details of a subscription
  delete                   Delete a subscription
```

### Create a subscription

```sh
Usage: hstream subscription create SUB_ID --stream STREAM_NAME
                                   [--ack-timeout INT]
                                   [--max-unacked-records INT]
                                   [--offset [earliest|latest]]
  Create a subscription

Available options:
  SUB_ID                   The ID of the subscription
  --stream STREAM_NAME     The stream associated with the subscription
  --ack-timeout INT        Timeout for acknowledgements in seconds
  --max-unacked-records INT
                           Maximum number of unacked records allowed per
                           subscription
  --offset [earliest|latest]
                           The offset of the subscription to start from
  -h,--help                Show this help text
```

Example: Create a subscription to the stream `demo` with the default settings.

```sh
> hstream subscription create --stream demo sub_demo
+-----------------+-------------+-------------+---------------------+
| Subscription ID | Stream Name | Ack Timeout | Max Unacked Records |
+-----------------+-------------+-------------+---------------------+
| sub_demo        | demo        | 60 seconds  | 10000               |
+-----------------+-------------+-------------+---------------------+
```

### Show and delete streams

```sh
> hstream subscription list
+-----------------+-------------+-------------+---------------------+
| Subscription ID | Stream Name | Ack Timeout | Max Unacked Records |
+-----------------+-------------+-------------+---------------------+
| sub_demo        | demo        | 60 seconds  | 10000               |
+-----------------+-------------+-------------+---------------------+

> hstream subscription delete sub_demo
Done.

> hstream subscription list
+-----------------+-------------+-------------+---------------------+
| Subscription ID | Stream Name | Ack Timeout | Max Unacked Records |
+-----------------+-------------+-------------+---------------------+
```

## HStream SQL

HStreamDB also provides an interactive SQL shell for a series of operations,
such as the management of streams and views, data insertion and retrieval, etc.

```sh
> hstream sql --help
Usage: hstream sql [--update-interval INT] [--retry-timeout INT]
  Start HStream SQL Shell

Available options:
  --update-interval INT    interval to update available servers in seconds
                           (default: 30)
  --retry-timeout INT      timeout to retry connecting to a server in seconds
                           (default: 60)
  -e,--execute STRING      execute the statement and quit
  --history-file STRING    history file path to write interactively executed
                           statements
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

To quit the current CLI session:

```sh
> :q
```

To print out help info overview:

```sh
> :h
```

To show the specific usage of some SQL statements:

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

This will create a stream with no particular function. You can `SELECT` data
from the stream and `INSERT` to via the corresponding SQL statement.

2. Create a stream, and this stream will also run a query to select specified
   data from some other stream.

Adding a Select statement after Create with a keyword `AS` can create a stream
will create a stream that processes data from another stream.

For example:

```sql
CREATE STREAM stream_name AS SELECT * from demo;
```

In the example above, by adding an `AS` followed by a `SELECT` statement to the
normal `CREATE` operation, it will create a stream that will also select all the
data from the `demo`.

After Creating the stream, we can insert values into the stream.

```sql
INSERT INTO stream_name (field1, field2) VALUES (1, 2);
```

There is no restriction on the number of fields a query can insert. Also, the
type of value is not restricted. However, you need to make sure that the number
of fields and the number of values are aligned.

The deletion command is `DROP STREAM <Stream_name> ;`, which deletes a stream,
and terminates all the [queries](#queries) that depend on the stream.

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

After creating a stream, we can select data from the stream in real-time. All
the data inserted after the select query is created will be printed out when the
insert operation happens. Select supports real-time processing of the data
inserted into the stream.

For example, we can choose the field and filter the data selected from the
stream.

```sql
SELECT a FROM demo EMIT CHANGES;
```

This will only select field `a` from the stream demo.

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

The view is a projection of specified data from streams. For example,

```sql
CREATE VIEW v_demo AS SELECT SUM(a) FROM demo GROUP BY a;
```

the above command will create a view that keeps track of the sum of `a` (which
have the same values, because of groupby) and have the same value from the point
this query is executed.

The operations on view are very similar to those on streams.

Except we can not use `SELECT ... EMIT CHANGES` performed on streams because a
view is static and there are no changes to emit. Instead, for example, we select
from the view with:

```sql
SELECT * FROM v_demo WHERE a = 1;
```

This will print the sum of `a` when `a` = 1.

If we want to create a view to record the sum of `a`s, we can:

```sql
CREATE STREAM demo2 AS SELECT a, 1 AS b FROM demo;
CREATE VIEW v_demo2 AS SELECT SUM(a) FROM demo2 GROUP BY b;
SELECT * FROM demo2 WHERE b = 1;
```
