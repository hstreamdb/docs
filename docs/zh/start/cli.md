# 命令行界面

每次进入命令行界面时，都会出现下列信息：

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

命令行界面可以处理以下两种指令

1. 以 `:` 开头的基本命令
2. 以 `;` 结尾的 SQL 语句

## 基本命令

退出当前命令行界面：

```sh
> :q
```

显示所有帮助信息：

```sh
> :h
```

显示某一种 SQL 语句的帮助信息：

```sh
> :help CREATE

  CREATE STREAM <stream_name> [IF EXIST] [AS <select_query>] [ WITH ( {stream_options} ) ];
  CREATE {SOURCE|SINK} CONNECTOR <stream_name> [IF NOT EXIST] WITH ( {connector_options} );
  CREATE VIEW <stream_name> AS <select_query>;
```

当前仅支持显示以下 SQL 语句的帮助信息: `CREATE`, `DROP`, `SELECT`, `SHOW`, `INSERT`, `TERMINATE`.

## SQL 语句

所有的 HStreamDB 的处理和存储操作都可以通过 SQL 语句来完成

### Stream 相关的语句

#### 当前有两种方法来创造一个新的 Stream

1. 创建一个普通的 Stream：

```sql
CREATE STREAM stream_name;
```

这条命令会创建一个普通的 stream 。用户可以用相应的 SQL 语句从这个 stream 中 `INSERT` 插入和 `SELECT` 提取数据。

2. 创建一个带有 `SELECT` 任务的 stream

在原本的创建语句后面加一个 `AS` 和一个 `SELECT` 语句，可以创建一个 stream，
这个 Stream 会根据提供的 `SELECT` 语句提取和处理指定 stream 的数据。

例如：

```sql
CREATE STREAM stream_name AS SELECT * from demo EMIT CHANGES;
```

在上述的例子中，stream_name 会提取所有写入 demo 的数据。

#### 创建完一个 stream 之后，我们可以往这个 stream 中 写入（INSERT）数据。

```sql
INSERT INTO stream_name (field1, field2) VALUES (1, 2);
```

当前并没有限制可以插入的字段数量，也没有值的类型。但是，你确实需要确保字段的数量和后面值的数量是一致的。

#### 从一个 stream 中 SELECT 数据

当我们有一个 stream 时，我们可以实时地从这个 stream 中查询数据，
这个查询任务会把所有在这个查询任务运行开始后写入的数据进行过滤和处理，
并把符合条件的结果打印出来。

比如，我们可以选择从 stream 中提取一个字段的值。

```sql
SELECT a FROM demo EMIT CHANGES;
```

这句 SQL 仅会 SELECT demo 中字段 `a` 的值。

#### 终止一个查询任务（TERMINATE a query）

当我们有一个查询任务的 ID 的时候，我们就可以终止这个任务：

```sql
TERMINATE QUERY <id>;
```

当然，我们也可以 `SHOW` 出所有的查询任务信息：

```sql
SHOW QUERIES;
```

仅作展示用的输出结果：

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

找到你想要终止的任务，确认任务的状态还未被终止，把查询任务的 ID 传给 `TERMINATE QUERY`。

或者，你也可以选择终止所有 `TERMINATE ALL`。

#### 删除一个 stream

`DROP STREAM <Stream_name> ;` 是删除操作，它不仅会删除所指定的 stream，还会终止所有依赖于这个 stream 的查询任务。

例如：

```sql
SELECT * FROM demo EMIT CHANGES;
```

如果 demo 被删除，上述的查询任务就会被终止：

```sql
DROP STREAM demo;
```

当试图删除一个不存在的 stream 时，cli 会打印出错误信息；在 stream_name 后面加上 `IF EXISTS`, 这个错误将不会出现。

```sql
DROP STREAM demo IF EXISTS;
```

#### SHOW 所有的 streams

通过使用 `SHOW STREAMS`命令可以显示所有当前存在 stream 。

### 物化视图 （View）

视图是 streams 中一些被指定的数据的投射。举例来说，

```sql
CREATE VIEW v_demo AS SELECT SUM(a) FROM demo GROUP BY a EMIT CHANGES;
```

上述命令将创建一个视图，它记录了当前具有相同值 的`a` 的总和 (由于 group by 的缘故，只有 `a` 的值相同时才会被分归到一起)。

对视图的操作与对流的操作非常相似。但是，我们不能使用在 stream 上执行的`SELECT ... EMIT CHANGES`。
因为视图是静态的，没有任何变化需要 EMIT。相对的，比如说
我们从视图中 SELECT：

```sql
SELECT * FROM v_demo WHERE a = 2;
```

上述命令会 SELECT 当 a = 2 时，我们记录的所有视图，即为所有当 a = 2 时，
所有 a 的总和，可以理解为：“a = 2的次数” * “2”。

假设我们想创建一个视图记录所有 a 的总和，我们可以：

```sql
CREATE STREAM demo2 AS SELECT a, 1 AS b FROM demo EMIT CHANGES;
CREATE VIEW v_demo2 AS SELECT SUM(a) FROM demo2 GROUP BY b EMIT CHANGES;
SELECT * FROM demo2 WHERE b = 1;
```
