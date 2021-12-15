# 数据消费 （Data Consumption）

如何通过 Java SDK 消费 HStreamDB 重的数据

## 前提条件

确保有一个运行中并可用的 HStreamDB

## 概念

客户端可以从一个消费者对象中消费数据。
每一个消费者对象将通过 subscriptionID 来加入一个持有订阅的共享订阅组 （consumer group），
然后客户端可以从订阅的流中获取数据。

每个消费者对象包含一个 `RawRecordReceiver` 和一个 `HRecordReceiver` 。
由此，用户可以根据他们的需求来消费原始数据或 HRecords 。

当客户端收到一条 record 时，应当使用 `responder.ack()` 来返回一个 ack。HStreamDB 当前支
持的是`at-least-once` 消费语义，应此，若不返回 ack，经过 `ackTimeoutSecond` 后，这些
records 会被 server 重新发送。

## 消费 Records

```java

// first, create a subscription for the stream
Subscription subscription =
    Subscription
        .newBuilder()
        .subscription("my_subscription")
        .stream("my_stream")
        .offset(new SubscriptionOffset(SubscriptionOffset.SpecialOffset.LATEST))
        .ackTimeoutSeconds(600)
        .build();
client.createSubscription(subscription);

// second, create a consumer attach to the subscription
Consumer consumer =
    client
        .newConsumer()
        .subscription("my_subscription")
        .rawRecordReceiver(
            ((receivedRawRecord, responder) -> {
                System.out.println(receivedRawRecord.getRecordId());
                responder.ack();
            }))
        .build();

// third, start the consumer
consumer.startAsync().awaitRunning();

```

- 示例中用了 `rawRecordReceiver()` 来消费原始数据，假如想要消费 HReacord，可以用 `hRecordReceiver()`。
