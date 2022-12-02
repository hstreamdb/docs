# 创建和管理 Stream

## 命名资源准则

一个HStream资源的名称可以唯一地识别一个 HStream 资源，如一个 stream、 subscription 或 reader。
资源名称必须符合以下要求:

- 以一个字母开头
- 长度必须不超过255个字符
- 只包含以下字符。字母`[A-Za-z]`，数字`[0-9]`。
  破折号`-`，下划线`_`。

\*用于资源名称作为SQL语句的一部分的情况。例如在 [HStream SQL Shell](../reference/cli.md#hstream-sql) 中或者用 SQL 创建 IO 任务时，
  将会出现资源名称无法被正确解析的情况（如与关键词冲突），此时需要用户用反斜线 `` ` ``，括住资源名称。这个限制或将会在日后的版本中被改进移除。

## Stream 的属性

- Replication factor

  为了容错性和更高的可用性，每个 Stream 都可以在集群中的节点之间进行复制。一个常
  用的生产环境 Replication factor 配置是为 3，也就是说，你的数据总是有三个副本，
  这在出错或你想对 Server 进行维护时将会很有帮助。这种复制是以 Stream 为单位上进
  行的。

- Backlog Retention

  该配置控制 HStreamDB 的 Stream 中的 records 被写入后保留的时间。当超过
  retention 保留的时间后，HStreamDB 将会清理这些 records，不管它是否被消费过。

  + 默认值=7 天
  + 最小值=1 秒
  + 最大值=21 天

## 创建一个 stream

在你写入 records 或者 创建一个订阅之前先创建一个 stream。

:::: tabs

::: tab Java

```java
// CreateStreamExample.java
```

:::

::: tab Go

```go
// ExampleCreateStream.go
```

:::

::: tab Python3
@snippet examples/py/snippets/guides.py common create-stream
:::

::::

## 删除一个 Stream

只有当一个 Stream 没有所属的订阅时才允许被删除，除非传一个强制标删除的 flag 。

## 强制删除一个 Stream

如果你需要删除一个有订阅的 stream 时，请启用强制删除。在强制删除一个 stream 后，
原来 stream 的订阅仍然可以从 backlog 中读取数据。这些订阅的 stream 名字会变成
`__deleted_stream__`。同时，我们并不允许在被删除的 stream 上创建新的订阅，也不允
许向该 stream 写入新的 record。

:::: tabs

::: tab Java

```java
// DeleteStreamExample.java
```

:::

::: tab Go

```go
// ExampleDeleteStream.go
```

:::

::: tab Python3
@snippet examples/py/snippets/guides.py common delete-stream
:::

::::

## 列出所有 stream 信息

可以如下拿到所有 HStream 中的 stream:

:::: tabs

::: tab Java

```java
// ListStreamsExample.java
```

:::

::: tab Go

```go
// ExampleListStreams.go
```

:::

::: tab Python3
@snippet examples/py/snippets/guides.py common list-streams
:::

::::
