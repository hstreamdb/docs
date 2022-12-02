# 向 HStreamDB 中的 Stream 写入 Records

本文档提供了关于如何通过 hstreamdb-java 等客户端向 HStreamDB 中的 Stream 写入数据的相关教程。

同时还可参考其他的相关教程：

- 如何[创建和管理 Stream](./stream.md).
- 如何[通过 Subscription 消费写入 Stream 中的 Records](./consume.md).

为了向 HStreamDB 写数据，我们需要将消息打包成 HStream Record，以及一个创建和发送
消息到服务器的 Producer。

## HStream Record

Stream 中的所有数据都是以 HStream Record 的形式存在，HStreamDB 支持以下两种
HStream Record：

- **HRecord**: 可以看作是一段 JSON 数据，就像一些 NoSQL 数据库中的 document。
- **Raw Record**: 二进制数据。

## 端到端压缩

为了降低传输开销，最大化带宽利用率，HStreamDB 支持对写入的 HStream Record 进行压缩。
用户在创建 `BufferedProducer` 时可以设置压缩算法。当前可选的压缩算法有
`gzip` 和 `zstd`。客户端从 HStreamDB 中消费数据时会自动完成解压缩操作。

## 写入 HStream Record

有两种方法可以把 records 写入 HStreamDB。从简单易用的角度，你可以从
`client.newProducer()` 的`Producer` 入手。这个 `Producer` 没有提供任何配置项，它
只会即刻将收到的每个 record 并行发送到 HServer，这意味着它并不能保证这些 records
的顺序。在生产环境中， `client.newBufferedProducer()` 中的 `BufferedProducer` 将
是更好的选择，`BufferedProducer` 将按顺序缓存打包 records 成一个 batch，并将该
batch 发送到服务器。每一条 record 被写入 stream 时，HServer 将为该 record 生成一
个相应的 record ID，并将其发回给客户端。这个 record ID 在 stream 中是唯一的。

## 使用 Producer

:::: tabs

::: tab Java

```java
// WriteDataSimpleExample.java
```

:::

::: tab Go

```go
// ExampleWriteProducer.go
```

:::

::: tab Python3
@snippet examples/py/snippets/guides.py common append-records
:::

::::

## 使用 BufferedProducer

在几乎所有情况下，我们更推荐使用 `BufferedProducer`。不仅因为它能提供更大的吞吐
量，它还提供了更加灵活的配置去调整，用户可以根据需求去在吞吐量和时延之间做出调整
。你可以配置 `BufferedProducer` 的以下两个设置来控制和设置触发器和缓存区大小。通
过 `BatchSetting`，你可以根据 batch 的最大 record 数、batch 的总字节数和 batch
存在的最大时限来决定何时发送。通过配置 `FlowControlSetting`，你可以为所有的缓存
的 records 设置缓存大小和策略。下面的代码示例展示了如何使用 BatchSetting 来设置
响应的 trigger，以通知 producers 何时应该刷新，以及 `FlowControlSetting` 来限制
`BufferedProducer` 中的 buffer 的最大字节数。

:::: tabs

::: tab Java

```java
// WriteDataBufferedExample.java
```

:::

::: tab Go

```go
// ExampleWriteBatchProducer.go
```

:::

::: tab Python3
@snippet examples/py/snippets/guides.py common buffered-append-records
:::

::::

## 使用分区键（Partition Key）

具有相同分区键的 records 可以在 BufferedProducer 中被保证能有序地写入。HStreamDB
的另一个重要功能，分区，也使用这些分区键来决定 records 将被分配到哪个分区，
以此提高写/读性能。更详细的解释请看[管理 Stream 的分区](./shards.md)。

参考下面的例子，你可以很容易地写入带有分区键的 records。

:::: tabs

::: tab Java

```java
// WriteDataWithKeyExample.java
```

:::

::: tab Go

```go
// ExampleWriteBatchProducerMultiKey.go
```

:::

::::
