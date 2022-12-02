# Consume Records with Subscriptions

## What is a Subscription?

To consume data from a stream, you must create a subscription to the stream.
When initiated, every subscription will retrieve the data from the beginning.
Consumers which receive and process records connect to a stream through a
subscription. A stream can have multiple subscriptions, but a given subscription
belongs to a single stream. Similarly, a subscription corresponds to one
consumer group with multiple consumers. However, every consumer belongs to only
a single subscription.

Please refer to [this page](./subscription.md) for detailed information about
creating and managing your subscriptions.

## How to consume data with a subscription

To consume data appended to a stream, HStreamDB Clients libraries have provided
asynchronous consumer API, which will initiate requests to join the consumer
group of the subscription specified.

### Two HStream Record types and corresponding receivers

As we [explained](./write.md#hstream-record), there are two types of records in
HStreamDB, HRecord and RawRecord. When initiating a consumer, corresponding
receivers are required. In the case where only HRecord Receiver is set, when the
consumer received a raw record, the consumer will ignore it and consume the next
record. Therefore, in principle, we do not recommend writing both HRecord and
RawRecord in the same stream. However, this is not strictly forbidden in
implementation, and you can provide both receivers to process both types of
records.

## Simple Consumer Example

To get higher throughput for your application, we provide asynchronous fetching
that does not require your application to block for new messages. Messages can
be received in your application using a long-running message receiver and
acknowledged one at a time, as shown in the example below.

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

For better performance, Batched Ack is enabled by default with settings
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

## Multiple consumers and shared consumption progress

In HStream, a subscription is consumed by a consumer group. In this consumer
group, there could be multiple consumers which share the subscription's
progress. To increase the rate of consuming data from a subscription, we could
have a new consumer join the existing subscription. The code is for
demonstration of how consumers can join the consumer group. Usually, the case is
that users would have consumers from different clients.

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

## Flow Control with `maxUnackedRecords`

A common scenario is that your consumers may not process and acknowledge data as
fast as the server sends, or some unexpected problems causing the consumer
client to be unable to acknowledge the data received, which could cause problems
as such:

The server would have to keep resending unacknowledged messages, and maintain
the information about unacknowledged messages, which would consume resources of
the server, and cause the server to face the issue of resource exhaustion.

To mitigate the issue above, use the `maxUnackedRecords` setting of the
subscription to control the maximum number of allowed un-acknowledged records
when the consumers receive messages. Once the number exceeds the
`maxUnackedRecords`, the server will stop sending messages to consumers of the
current subscription.

## Receiving messages in order

Note: the order described below is just for a single consumer. If a subscription
has multiple consumers, the order can still be guaranteed in each, but the order
is no longer preserved if we see the consumer group as an entity.

Consumers will receive messages with the same partition key in the order that
the HStream server receives them. Since HStream delivers hstream records with
at-least-once semantics, in some cases, when HServer does not receive the ack
for some record in the middle, it might deliver the record more than once. In
these cases, we can not guarantee the order either.

## Handling errors

When a consumer is running, and failure happens at the receiver, the default
behaviour is that the consumer will catch the exception, print an error log, and
continue consuming the next record instead of failing.

Consumers could fail in other scenarios, such as network, deleted subscriptions,
etc. However, as a service, you may want the consumer to keep running, so you
can register a listener to handle a failed consumer:

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
