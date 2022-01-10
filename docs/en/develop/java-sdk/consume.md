# Consume data

This page shows how to consume data from HStreamDB using Java SDK.

## Prerequisites

Make sure you have HStreamDB running and accessible.

## Concepts

A client can consume data from a consumer object. A consumer object will join a
consumer group with a subscriptionID, and then the client can fetch data from
the subscribed stream.

Each consumer object will contain a `RawRecordReceiver` and a `HRecordReceiver`,
so users can consume raw records or HRecords according to their needs.

When you received a record from server, you should use `responder.ack()` to send
an ack response to server. HStreamDB now support `at-least-once` consume, if you
don't send acks, records will be retransmitted by the server after
`ackTimeoutSecond`.

## Consume Records

You can consume records like this:

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

- the example use `rawRecordReceiver()` to consume raw records, if you want to
  consume HReacord, just use `hRecordReceiver()` instead.
