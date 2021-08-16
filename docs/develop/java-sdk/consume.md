# Consume data

This page shows how to consume data from HStreamDB using Java SDK.

## Prerequisites

Make sure you have HStreamDB running and accessible.

## Concepts

A client can consume data from a consumer object. A consumer object will join a
subscription with a subscriptionID, and then the client can fetch data from the
subscribed stream.

Each consumer object will contain a `RawRecordReceiver` and a `HRecordReceiver`,
so users can consume raw records or HRecords according to their needs.

With consumer, the client will continuously fetch data from the subscription in
a background thread, also the client will periodically send heartbeats to the
server to maintain the subscription.

## Consume Records

You can consume records like this:

```java

Consumer consumer =
    client
        .newConsumer()
        .subscription("test_subscription")
        .rawRecordReceiver((receivedRawRecord, responder) -> {
            // You can execute some callback function here.
            System.out.Println("Received Raw Record: " + receivedRawRecord.getRecordId())
        })
        .build();
consumer.startAsync().awaitRunning();

// after you consume enough data, you can stop the consumer
consumer.stopAsync().awaitTerminated();

```

- the example use `rawRecordReceiver()` to consume raw records, if you want to
  consume HReacord, just use `hRecordReceiver()` instead.

## Responder

HStreamDB support `checkpoint`. Consumer can use responder to ack server and
commit a checkpoint when it receives data from server.

```java

AtomicInteger consumedCount = new AtomicInteger();
Consumer consumer =
    client
        .newConsumer()
        .subscription("test_subscription")
        .hRecordReceiver((receivedHRecord, responder) -> {
           System.out.println("enter process, count is " + consumedCount.incrementAndGet());
           if (consumedCount.get() == 3) {
             responder.ack();
             System.out.println("finished ack");
           }
        })
        .build();
consumer.startAsync().awaitRunning();

// after you consume enough data, you can stop the consumer
consumer.stopAsync().awaitTerminated();

```
