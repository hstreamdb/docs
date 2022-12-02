# 通过订阅（Subscription）从 HStreamDB 消费数据

## 什么是一个订阅（Subscription）?

要从一个 stream 中消费数据，你必须为该 stream 创建一个订阅。创建成功后，每个订阅
都将从头开始检索数据。接收和处理消息的消费者（consumer）通过一个订阅与一个
stream 相关联。

一个 stream 可以有多个订阅，但一个给定的订阅只属于一个 stream。同样地，一个订阅
对应一个具有多个消费者的 consumer group，但每个消费者只属于一个订阅。

请参考[这个页面](./subscription.md)，了解关于创建和管理订阅的详细信息。

## 如何用一个订阅来消费数据

为了消费写入 stream 中的数据，HStreamDB 客户端库提供了异步 Consumer API，它将发
起请求加入指定订阅的 consumer group。

### 两种 HStream 记录类型和相应的 Receiver

正如我们所介绍的，在 HStreamDB 中有两种 Record 类型，HRecord 和 Raw Record。当启
动一个消费者时，需要相应的 Receiver。在只设置了 HRecord Receiver 的情况下，当消
费者收到一条 raw record 时，消费者将忽略它并消费下一条 record。因此，原则上，我
们不建议在同一个 stream 中同时写入 HRecord 和 raw record。然而，这并没有在实现的
层面上严格禁止，用户仍然可以提供两种 receiver 来同时处理两种类型的 record。

## 简单的数据消费实例

异步的 Consumer API 不需要你的应用程序为新到来的 record 进行阻塞，可以让你的应用
程序获得更高的吞吐量。Records 可以在你的应用程序中使用一个长期运行的 records
receiver 来接收，并逐条 ack，如下面的例子中所示。

:::: tabs

::: tab Java

```java
// ConsumeDataSimpleExample.java
```

:::

::: tab Go

```go
// ExampleConsumer.go
```

:::

::: tab Python3
@snippet examples/py/snippets/guides.py common subscribe-records
:::

::::

For better performance, Batched Ack is enabled by default with setting
`ackBufferSize` = 100 and `ackAgeLimit` = 100, which you can change when
initiating your consumers.

:::: tabs
::: tab Java

```java
Consumer consumer =
    client
        .newConsumer()
        .subscription("you_subscription_id")
        .name("your_consumer_name")
        .hRecordReceiver(your_receiver)
        // When ack() is called, the consumer will not send it to servers immediately,
        // the ack request will be buffered until the ack count reaches ackBufferSize
        // or the consumer is stopping or reached ackAgelimit
        .ackBufferSize(100)
        .ackAgeLimit(100)
        .build();
```

:::
::::

为了获得更好的性能，默认情况下启用了 Batched Ack，和 ackBufferSize = 100 和
ackAgeLimit = 100 的设置，你可以在启动你的消费者时更新它。

:::: tabs
::: tab Java

```java
Consumer consumer =
    client
        .newConsumer()
        .subscription("you_subscription_id")
        .name("your_consumer_name")
        .hRecordReceiver(your_receiver)
        // When ack() is called, the consumer will not send it to servers immediately,
        // the ack request will be buffered until the ack count reaches ackBufferSize
        // or the consumer is stopping or reached ackAgelimit
        .ackBufferSize(100)
        .ackAgeLimit(100)
        .build();
```

:::
::::

## 多个消费者和共享订阅

如先前提到的，在 HStream 中，一个订阅是对应了一个 consumer group 消费的。在这个
consumer group 中，可能会有多个消费者，并且他们共享订阅的进度。当想要提高从订阅
中消费数据的速度时，我们可以让一个新的消费者加入现有的订阅。这段代码是用来演示新
的消费者是如何加入 consumer group 的。更常见的情况是，用户使用来自不同客户端的消
费者去共同消费一个订阅。

:::: tabs

::: tab Java

```java
// ConsumeDataSharedExample.java
```

:::

::: tab Go

```go
// ExampleConsumerGroup.go
```

:::

::::

## 使用 `maxUnackedRecords` 的来实现流控

一个常发生的状况是，消费者处理和确认数据的速度很可能跟不上服务器发送的速度，或者
一些意外的问题导致消费者无法确认收到的数据，这可能会导致以下问题：

服务器将不得不不断重发未确认的消息，并维护未确认的消息的信息，这将消耗服务器的资
源，并导致服务器面临资源耗尽的问题。

为了缓解上述问题，使用订阅的 `maxUnackedRecords` 设置来控制消费者接收消息时允许
的未确认 records 的最大数量。一旦数量超过 `maxUnackedRecords`，服务器将停止向当
前订阅的消费者们发送消息。

## 按顺序接收消息

注意：下面描述的接收顺序只针对单个消费者。如果一个订阅有多个消费者，在每个消费者
中仍然可以保证顺序，但如果我们把 consumer group 看成一个整体，那么顺序性就不再保
证了。

消费者将按照 HStream 服务器收到信息的顺序接收具有相同分区键的 record。由于
HStream 以至少一次的语义发送 hstream record，在某些情况下，当 HServer 可能没有收
到中间某些 record 的 ack 时，它将可能多次发送这条 record。而在这些情况下，我们也
不能保证顺序。

## 处理错误

当消费者正在运行时，如果 receiver 失败了，默认的行为是消费者会将将捕获异常，打印
错误日志，并继续消费下一条记录而不是导致消费者也失败。

在其他情况下可能会导致消费者的失败，例如网络、订阅被删除等。然而，作为一个服务，
你可能希望消费者继续运行，所以你可以设置一个监听器来处理一个消费者失败的情况。

:::: tabs
::: tab Java

```java
// add Listener for handling failed consumer
var threadPool = new ScheduledThreadPoolExecutor(1);
consumer.addListener(
    new Service.Listener() {
      public void failed(Service.State from, Throwable failure) {
        System.out.println("consumer failed, with error: " + failure.getMessage());
      }
    },
    threadPool);
```

:::
::::
